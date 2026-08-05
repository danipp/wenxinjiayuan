<template>
  <div class="document-upload">
    <!-- 自定义插槽布局 -->
    <div v-if="$slots.default" @click="triggerUpload" class="custom-upload-area">
      <slot></slot>
    </div>

    <!-- 默认上传布局 -->
    <div v-else class="default-upload-area" @click="triggerUpload">
      <el-icon class="upload-icon" :size="48">
        <Upload />
      </el-icon>
      <div class="upload-text">
        <p class="primary-text">点击上传文档</p>
        <p class="secondary-text">支持 DOC、DOCX 格式，文件大小不超过 {{ formatFileSize(maxSize) }}</p>
      </div>
    </div>

    <!-- 隐藏的文件输入 -->
    <input ref="fileInput" type="file" :accept="accept" @change="handleFileChange" style="display: none" :multiple="multiple" />

    <!-- 上传进度 -->
    <div v-if="uploading && showFile" class="upload-progress">
      <el-progress :percentage="uploadProgress" :show-text="false" />
      <p class="progress-text">正在上传... {{ uploadProgress }}%</p>
    </div>

    <!-- 已上传文件列表 -->
    <div v-if="fileList.length > 0 && showFile" class="file-list">
      <div v-for="(file, index) in fileList" :key="index" class="file-item">
        <el-icon class="file-icon">
          <Document />
        </el-icon>
        <div class="file-info">
          <span class="file-name">{{ file.name }}</span>
          <span class="file-size">{{ formatFileSize(file.size) }}</span>
        </div>
        <el-button type="danger" :icon="Delete" size="small" circle @click="removeFile(index)" class="remove-btn" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { ElMessage, ElIcon, ElProgress, ElButton } from "element-plus";
import { Upload, Document, Delete } from "@element-plus/icons-vue";
import { useOssConfig } from "@/hooks/useOssConfig";
const ossCifig = useOssConfig();
const props = defineProps({
  // maxSize: 10 * 1024 * 1024, // 默认10MB
  // multiple: false,
  // accept: ".doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  // disabled: false
  maxSize: {
    type: Number,
    default: 10 * 1024 * 1024
  },
  multiple: {
    type: Boolean,
    default: false
  },
  accept: {
    type: String,
    default: ".doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  },
  disabled: {
    type: Boolean,
    default: false
  },
  ossPath: {
    type: String,
    default: "materia"
  },
  showFile: {
    type: Boolean,
    default: true
  }
});

// 定义事件
const emit = defineEmits(["update:fileList", "success", "error", "progress", "change"]);

// 响应式数据
const fileInput = ref();
const fileList = ref([]);
const uploading = ref(false);
const uploadProgress = ref(0);

// 计算属性
const acceptedTypes = computed(() => {
  return props.accept.split(",");
});

// 触发文件选择
const triggerUpload = () => {
  if (props.disabled) return;
  fileInput.value?.click();
};

// 处理文件选择
const handleFileChange = async event => {
  const target = event.target;
  const files = Array.from(target.files || []);

  if (files.length === 0) return;

  const successfulUploads = [];

  for (const file of files) {
    if (await validateFile(file)) {
      // if (!props.multiple) {
      //   fileList.value = [file];
      // } else {
      //   fileList.value.push(file);
      // }
      try {
        const response = await uploadFile(file);
        successfulUploads.push({ response, file });
      } catch (error) {
        // 单个文件失败时已在 uploadFile 内部处理，这里继续上传其他文件
      }
    }
  }

  // 清空input值，允许重复选择同一文件
  target.value = "";

  // 触发变化事件
  emit("change", fileList.value);
  emit("update:fileList", fileList.value);

  successfulUploads.forEach(({ response, file }) => {
    emit("success", response, file);
  });

  if (successfulUploads.length > 0) {
    ElMessage.success("上传成功");
  }
};

// 验证文件
const validateFile = async file => {
  // 检查文件类型
  const fileExtension = "." + file.name.split(".").pop()?.toLowerCase();
  console.log(fileExtension, "fileExtension");

  if (!acceptedTypes.value.includes(fileExtension)) {
    ElMessage.error("不支持该类型格式的文件");
    return false;
  }

  // 检查文件大小
  if (file.size > props.maxSize) {
    ElMessage.error(`文件大小不能超过 ${formatFileSize(props.maxSize)}`);
    return false;
  }

  // 执行自定义验证
  if (props.beforeUpload) {
    try {
      const result = await props.beforeUpload(file);
      if (!result) {
        return false;
      }
    } catch (error) {
      ElMessage.error("文件验证失败");
      return false;
    }
  }

  return true;
};

// 上传文件
const uploadFile = async file => {
  uploading.value = true;
  uploadProgress.value = 0;

  try {
    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += Math.random() * 10;
        emit("progress", uploadProgress.value, file);
        props.onProgress?.(uploadProgress.value, file);
      }
    }, 200);

    let response = await ossCifig.getOssConfig(file, props.ossPath);
    response.name = file.name;
    response.size = file.size;
    if (props.multiple) {
      fileList.value.push(response);
    } else {
      fileList.value = [response];
    }
    clearInterval(progressInterval);
    uploadProgress.value = 100;
    emit("update:fileList", fileList.value);
    return response;
  } catch (error) {
    emit("error", error, file);
    props.onError?.(error, file);
    if (props.showFile) {
      ElMessage.error("文档上传失败");
    }
    throw error;
  } finally {
    uploading.value = false;
    setTimeout(() => {
      uploadProgress.value = 0;
    }, 1000);
  }
};

// 移除文件
const removeFile = index => {
  fileList.value.splice(index, 1);
  emit("change", fileList.value);
  emit("update:fileList", fileList.value);
};

// 格式化文件大小
const formatFileSize = bytes => {
  if (bytes === 0) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

// 暴露方法给父组件
defineExpose({
  triggerUpload,
  removeFile,
  fileList
});
</script>

<style lang="scss" scoped>
.document-upload {
  width: 100%;

  .custom-upload-area {
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      opacity: 0.8;
    }
  }

  .default-upload-area {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    border: 2px dashed #d9d9d9;
    border-radius: 8px;
    background-color: #fafafa;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: #409eff;
      background-color: #f0f9ff;

      .upload-icon {
        color: #409eff;
      }

      .primary-text {
        color: #409eff;
      }
    }

    .upload-icon {
      color: #c0c4cc;
      margin-bottom: 16px;
      transition: color 0.3s ease;
    }

    .upload-text {
      text-align: center;

      .primary-text {
        font-size: 16px;
        color: #606266;
        margin: 0 0 8px 0;
        font-weight: 500;
        transition: color 0.3s ease;
      }

      .secondary-text {
        font-size: 14px;
        color: #909399;
        margin: 0;
        line-height: 1.4;
      }
    }
  }

  .upload-progress {
    margin-top: 16px;
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 6px;

    .progress-text {
      margin: 8px 0 0 0;
      font-size: 14px;
      color: #606266;
      text-align: center;
    }
  }

  .file-list {
    margin-top: 16px;

    .file-item {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      background-color: #f8f9fa;
      border-radius: 6px;
      margin-bottom: 8px;
      transition: all 0.3s ease;

      &:hover {
        background-color: #ecf5ff;

        .remove-btn {
          opacity: 1;
        }
      }

      .file-icon {
        color: #409eff;
        font-size: 20px;
        margin-right: 12px;
      }

      .file-info {
        flex: 1;
        display: flex;
        flex-direction: column;

        .file-name {
          font-size: 14px;
          color: #303133;
          font-weight: 500;
          margin-bottom: 4px;
          word-break: break-all;
        }

        .file-size {
          font-size: 12px;
          color: #909399;
        }
      }

      .remove-btn {
        opacity: 0;
        transition: opacity 0.3s ease;
        margin-left: 12px;
      }
    }
  }

  // 禁用状态
  &.disabled {
    .default-upload-area,
    .custom-upload-area {
      cursor: not-allowed;
      opacity: 0.6;

      &:hover {
        border-color: #d9d9d9;
        background-color: #fafafa;

        .upload-icon {
          color: #c0c4cc;
        }

        .primary-text {
          color: #606266;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .document-upload {
    .default-upload-area {
      padding: 30px 15px;

      .upload-text {
        .primary-text {
          font-size: 14px;
        }

        .secondary-text {
          font-size: 12px;
        }
      }
    }

    .file-list {
      .file-item {
        padding: 10px 12px;

        .file-info {
          .file-name {
            font-size: 13px;
          }

          .file-size {
            font-size: 11px;
          }
        }
      }
    }
  }
}
</style>