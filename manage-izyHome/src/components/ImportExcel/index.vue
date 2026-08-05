<template>
  <el-dialog v-model="dialogVisible" :title="`批量添加${parameter.title}`" :destroy-on-close="true" width="580px" draggable
    :close-on-click-modal="false">

    <!-- 绑定 params 和 rules 用于表单校验 -->
    <el-form class="drawer-multiColumn-form" label-width="100px" ref="formRef" :model="parameter.params"
      :rules="parameter.rules">
      <el-form-item label="模板下载 :">
        <el-button type="primary" :icon="Download" @click="downloadTemp"> 点击下载 </el-button>
      </el-form-item>

      <el-form-item label="文件上传 :">
        <!-- 
          修改重点：
          1. auto-upload: 由是否有插槽决定。有插槽则为 false (等待手动触发)，无插槽则为 true (自动触发)。
          2. http-request: 无论哪种模式，最终都汇聚到 uploadExcel 方法执行统一上传。
        -->
        <el-upload ref="uploadRef" action="#" class="upload" :drag="true" :limit="excelLimit" :multiple="true"
          :show-file-list="true" :http-request="uploadExcel" :before-upload="beforeExcelUpload"
          :on-exceed="handleExceed" :on-success="excelUploadSuccess" :on-error="excelUploadError"
          :accept="parameter.fileType!.join(',')" :auto-upload="!hasExtraSlot" v-model:file-list="fileList">
          <slot name="empty">
            <el-icon class="el-icon--upload">
              <upload-filled />
            </el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </slot>
          <template #tip>
            <slot name="tip">
              <div class="el-upload__tip">请上传 .xls , .xlsx 标准格式文件，文件最大为 {{ parameter.fileSize }}M</div>
            </slot>
          </template>
        </el-upload>
      </el-form-item>

      <!-- 额外插槽 -->
      <slot name="extra"></slot>

      <el-form-item label="数据覆盖 :">
        <el-switch v-model="isCover" />
      </el-form-item>
    </el-form>

    <!-- 底部按钮：有 extra 插槽时显示 -->
    <template #footer v-if="hasExtraSlot">
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <!-- 点击确定，执行 submitUpload -->
        <el-button type="primary" @click="submitUpload" :loading="uploadLoading">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="ImportExcel">
import { ref, useSlots, computed } from "vue";
import { useDownload } from "@/hooks/useDownload";
import { Download, UploadFilled } from "@element-plus/icons-vue";
import { ElNotification, UploadRequestOptions, UploadRawFile, FormInstance, FormRules } from "element-plus";
import { UploadUserFile } from "element-plus";
// 扩展接口
export interface ExcelParameterProps {
  title: string;
  fileSize?: number;
  fileType?: File.ExcelMimeType[];
  tempApi?: (params: any) => Promise<any>;
  importApi?: (params: any) => Promise<any>;
  getTableList?: () => void;
  params?: any; // 额外参数
  rules?: FormRules; // 校验规则
}
const fileList = ref<UploadUserFile[]>([]);
const formRef = ref<FormInstance>();
const uploadRef = ref();
const isCover = ref(false);
const excelLimit = ref(1);
const dialogVisible = ref(false);
const uploadLoading = ref(false);

const slots = useSlots();
// 判断是否有额外插槽
const hasExtraSlot = computed(() => !!slots.extra);

const parameter = ref<ExcelParameterProps>({
  title: "",
  fileSize: 5,
  fileType: ["application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"],
  params: {},
  rules: {}
});

// 接收父组件参数
const acceptParams = (params: ExcelParameterProps) => {
  parameter.value = { ...parameter.value, ...params };
  dialogVisible.value = true;
  isCover.value = false;
  // 清理校验和文件
  setTimeout(() => {
    if (uploadRef.value) uploadRef.value.clearFiles();
    if (formRef.value) formRef.value.clearValidate();
  }, 0);
};

// 模板下载
const downloadTemp = () => {
  if (!parameter.value.tempApi) return;
  useDownload(parameter.value.tempApi, `${parameter.value.title}模板`);
};

// --------------------------------------------------------
// 1. 核心上传逻辑 (统一接口调用)
// 无论是自动上传还是手动点击确定，最终都会进入这里
// --------------------------------------------------------
const uploadExcel = async (param: UploadRequestOptions) => {
  let excelFormData = new FormData();
  excelFormData.append("file", param.file);
  excelFormData.append("isCover", isCover.value as unknown as Blob);

  // 将额外参数追加到 FormData
  if (parameter.value.params) {
    for (const key in parameter.value.params) {
      if (parameter.value.params[key] !== undefined && parameter.value.params[key] !== null) {
        excelFormData.append(key, parameter.value.params[key]);
      }
    }
  }

  try {
    uploadLoading.value = true;
    await parameter.value.importApi!(excelFormData);
    excelUploadSuccess();
    parameter.value.getTableList && parameter.value.getTableList();
    dialogVisible.value = false; // 上传成功，关闭弹窗
  } catch (error) {
    excelUploadError();
    // 注意：element-plus 在 http-request 抛错时会自动处理 onError，但我们需要手动清空文件以便用户重试
    // 如果是自动模式，通常希望清空；手动模式下也清空让用户重新选，或者根据需求保留
    uploadRef.value.clearFiles();
  } finally {
    uploadLoading.value = false;
  }
};

// --------------------------------------------------------
// 2. 手动触发逻辑 (点击“确定”按钮)
// --------------------------------------------------------
const submitUpload = async () => {
  if (!formRef.value) return;
  // (1) 先校验表单
  await formRef.value.validate((valid) => {
    if (valid) {
      // (2) 校验是否选择了文件
      // uploadFiles 包含了等待上传的文件
      if (fileList.value.length === 0) {
        ElNotification({ title: "温馨提示", message: "请先选择要上传的文件！", type: "warning" });
        return;
      }

      // (3) 核心：手动触发上传。
      // 这会调用 el-upload 内部逻辑，进而触发 before-upload，如果通过则触发 http-request (即 uploadExcel)
      uploadRef.value.submit();
    }
  });
};

/**
 * @description 文件校验
 * 注意：在 auto-upload=false 时，before-upload 只在调用 submit() 时触发
 */
const beforeExcelUpload = (file: UploadRawFile) => {
  const isExcel = parameter.value.fileType!.includes(file.type as File.ExcelMimeType);
  const fileSize = file.size / 1024 / 1024 < parameter.value.fileSize!;
  if (!isExcel)
    ElNotification({ title: "温馨提示", message: "上传文件只能是 xls / xlsx 格式！", type: "warning" });
  if (!fileSize)
    setTimeout(() => {
      ElNotification({ title: "温馨提示", message: `上传文件大小不能超过 ${parameter.value.fileSize}MB！`, type: "warning" });
    }, 0);
  return isExcel && fileSize;
};

// 文件数超出提示
const handleExceed = (files: File[]) => {
  uploadRef.value!.clearFiles();
  const file = files[0] as UploadRawFile;
  uploadRef.value!.handleStart(file);
};

const excelUploadError = () => {
  ElNotification({ title: "温馨提示", message: `批量添加${parameter.value.title}失败，请您重新上传！`, type: "error" });
};

const excelUploadSuccess = () => {
  ElNotification({ title: "温馨提示", message: `批量添加${parameter.value.title}成功！`, type: "success" });
};

defineExpose({
  acceptParams
});
</script>

<style lang="scss" scoped>
@import "./index.scss";

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
}

.upload {
  width: 100%;
}
</style>