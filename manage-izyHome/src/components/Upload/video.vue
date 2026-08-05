<template>
  <div class="upload-box">
    <el-upload
      ref="uploadRef"
      :id="uuid"
      action="#"
      :class="['upload', self_disabled ? 'disabled' : '', drag ? 'no-border' : '']"
      :multiple="multiple"
      :disabled="self_disabled"
      :show-file-list="false"
      :http-request="handleHttpUpload"
      :before-upload="beforeUpload"
      :on-error="uploadError"
      :drag="drag"
      :accept="fileType.join(',')"
      :limit="computedLimit"
      :on-exceed="handleExceed"
    >
      <!-- 核心布局：视频列表与上传区域横向容器 -->
      <div class="video-grid-container">
        <!-- 已上传视频列表 -->
        <template v-if="showFile">
          <div v-for="(url, index) in videoUrls" :key="index" class="video-item">
            <video :src="url" class="upload-video" controls />
            <div class="video-handle" @click.stop>
              <!-- 注意：这里你原代码是 isEdit 和 isDel 都在单视频里，多视频只有 Del，我保持了原逻辑 -->
              <div v-if="!self_disabled && isEdit && !multiple" class="handle-icon" @click="editVideo">
                <el-icon>
                  <Edit />
                </el-icon>
                <span>编辑</span>
              </div>
              <div
                class="handle-icon"
                @click="
                  videoViewVisible = true;
                  currentViewIndex = index;
                "
              >
                <el-icon>
                  <ZoomIn />
                </el-icon>
                <span>查看</span>
              </div>
              <div v-if="!self_disabled && isDel" class="handle-icon" @click="deleteVideo(index)">
                <el-icon>
                  <Delete />
                </el-icon>
                <span>删除</span>
              </div>
            </div>
          </div>
        </template>

        <!-- 上传区域（无论单文件还是多文件，都使用统一的卡片样式） -->
        <div v-if="(multiple && videoUrls.length < limitCount) || (!multiple && videoUrls.length === 0)">
          <slot name="empty">
            <div class="upload-area-card">
              <div class="upload-empty">
                <span class="plus-icon">+</span>
                <span>点击或拖拽文件至此处上传</span>
              </div>
            </div>
          </slot>
        </div>
      </div>
    </el-upload>

    <div class="el-upload__tip">
      <slot name="tip"></slot>
    </div>

    <!-- 视频查看器 -->
    <el-dialog v-model="videoViewVisible" title="视频预览" width="80%">
      <video :src="videoUrls[currentViewIndex]" class="view-video" controls style="width: 100%; max-height: 50vh" />
    </el-dialog>
  </div>
</template>
  
  <script setup name="UploadVideo">
import { ref, computed, inject, watch } from "vue";
import { generateUUID } from "@/utils";
import { ElNotification, ElDialog, formContextKey, formItemContextKey } from "element-plus";
import { VideoCamera, Edit, ZoomIn, Delete } from "@element-plus/icons-vue";
import { useOssConfig } from "@/hooks/useOssConfig";
const ossConfig = useOssConfig();

// 接受父组件参数
const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  ossPath: { type: String, default: "" },
  drag: { type: Boolean, default: true },
  disabled: { type: Boolean, default: false },
  fileSize: { type: Number, default: 50 },
  fileType: { type: Array, default: () => ["video/mp4", "video/avi", "video/mov", "video/flv", "video/wmv"] },
  height: { type: String, default: "150px" }, // 默认高度调整为图片卡片常见尺寸
  width: { type: String, default: "150px" }, // 默认宽度调整为图片卡片常见尺寸
  borderRadius: { type: String, default: "8px" },
  isEdit: { type: Boolean, default: false },
  isDel: { type: Boolean, default: true },
  multiple: { type: Boolean, default: false },
  limitCount: { type: Number, default: 9 },
  showFile: { type: Boolean, default: true }
});

// 组件内部视频列表
const videoUrls = ref([...props.modelValue]);
const uuid = ref("video-id-" + generateUUID());
const videoViewVisible = ref(false);
const currentViewIndex = ref(0);
const uploadRef = ref(null);

// 动态计算 limit，基于当前已上传的视频数量
const computedLimit = computed(() => {
  const currentCount = videoUrls.value.length;
  if (props.multiple) {
    // 多选模式：限制总数减去当前已上传数量
    return props.limitCount - currentCount;
  } else {
    // 单选模式：如果已有视频则限制为0，否则为1
    return currentCount === 0 ? 1 : 0;
  }
});

// 获取表单上下文
const formContext = inject(formContextKey, void 0);
const formItemContext = inject(formItemContextKey, void 0);

// 判断是否禁用
const self_disabled = computed(() => {
  return props.disabled || formContext?.disabled;
});

// 事件发射
const emit = defineEmits(["update:modelValue", "success", "error"]);

// 监听外部视频列表变化
watch(
  () => props.modelValue,
  newVal => {
    videoUrls.value = [...newVal];
    // 当外部更新视频列表时，清除 el-upload 内部的文件列表
    if (uploadRef.value) {
      uploadRef.value.clearFiles();
    }
  },
  { deep: true }
);

/** 处理视频上传 */
const handleHttpUpload = async options => {
  try {
    const result = await ossConfig.getOssConfig(options.file, props.ossPath);
    if (result && result.url) {
      videoUrls.value = props.multiple ? [...videoUrls.value, result.url] : [result.url];
      emit("update:modelValue", videoUrls.value);
      // 上传成功，手动触发成功逻辑
      ElNotification({ title: "上传成功", message: "视频上传成功！", type: "success" });
      emit("success");
    } else {
      // 如果没有返回 url，视为上传失败
      throw new Error("上传失败：未获取到视频地址");
    }
  } catch (error) {
    // 抛出错误，让 el-upload 触发 on-error 回调（统一处理错误提示）
    throw error;
  }
};

/** 删除视频 */
const deleteVideo = index => {
  videoUrls.value = videoUrls.value.filter((_, i) => i !== index);
  emit("update:modelValue", videoUrls.value);
  // 清除 el-upload 内部的文件列表，使其重新计算限制
  if (uploadRef.value) {
    uploadRef.value.clearFiles();
  }
};

/** 编辑视频（替换） */
const editVideo = () => {
  // 对于单视频，通常是替换操作，这里我假设你是想通过重新点击上传来替换
  // 如果是多视频，你可能需要更复杂的编辑逻辑，例如弹出编辑框
  const dom = document.querySelector(`#${uuid.value} .el-upload__input`);
  dom && dom.dispatchEvent(new MouseEvent("click"));
};

/** 文件上传前验证 */
const beforeUpload = rawFile => {
  const fileSize = rawFile.size / 1024 / 1024 < props.fileSize;
  const fileType = props.fileType.includes(rawFile.type);

  if (!fileType) {
    ElNotification({ title: "格式错误", message: `上传视频格式不支持，支持格式：${props.fileType.join(", ")}`, type: "warning" });
    return false;
  }
  if (!fileSize) {
    ElNotification({ title: "大小超限", message: `上传视频大小不能超过 ${props.fileSize}M！`, type: "warning" });
    return false;
  }
  return true;
};

/** 处理文件超出限制 */
const handleExceed = (files, fileList) => {
  const currentCount = videoUrls.value.length;
  const remaining = props.multiple ? props.limitCount - currentCount : currentCount === 0 ? 1 : 0;
  if (remaining <= 0) {
    ElNotification({ title: "数量超限", message: `最多只能上传 ${props.limitCount} 个视频文件！`, type: "warning" });
  }
};

/** 上传错误 */
const uploadError = error => {
  console.error("视频上传失败:", error);
  ElNotification({ title: "上传失败", message: "视频上传失败，请重新尝试！", type: "error" });
  emit("error", error);
};
</script>
  
  <style scoped lang="scss">
.is-error {
  .upload {
    :deep(.el-upload),
    :deep(.el-upload-dragger) {
      border: 1px dashed var(--el-color-danger) !important;
      &:hover {
        border-color: var(--el-color-primary) !important;
      }
    }
  }
}

:deep(.disabled) {
  .el-upload,
  .el-upload-dragger {
    cursor: not-allowed !important;
    background: var(--el-disabled-bg-color);
    border: 1px dashed var(--el-border-color-darker) !important;
    &:hover {
      border: 1px dashed var(--el-border-color-darker) !important;
    }
  }
}

.upload-box {
  // 核心容器：横向排列视频与上传区域
  width: 100%;
  .video-grid-container {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    gap: 16px; /* 调整间距 */
  }

  :deep(.upload) {
    width: 100%; /* 确保上传组件本身占据全部宽度以便 flex 布局 */
    .el-upload {
      position: relative;
      width: 100%; /* el-upload 内部也应占据全部宽度 */
      overflow: hidden;
      border: none !important; /* 移除 el-upload 默认边框 */
      transition: var(--el-transition-duration-fast);
      display: flex; /* 让 el-upload 内部的 dragger 或内容能 flex 排列 */
      flex-wrap: wrap;
      gap: 16px; // 继承自 .video-grid-container 的 gap
    }

    .el-upload-dragger {
      // 样式移到 .upload-area-card，这里只处理拖拽时的背景
      //   width: v-bind(width);
      //   height: v-bind(height);
      width: 100%;
      display: flex;
      align-items: center;
      //   justify-content: center;
      padding: 0;
      overflow: hidden;
      background-color: transparent;
      border: none; // 移除 dragger 默认边框，由 .upload-area-card 接管
      border-radius: v-bind(borderRadius);
      &:hover {
        border-color: transparent; // 移除 dragger 默认边框
      }
    }

    .el-upload-dragger.is-dragover {
      // 拖拽时的样式
      background-color: var(--el-color-primary-light-9);
      border: 2px dashed var(--el-color-primary) !important;
    }
  }

  .el-upload__tip {
    margin-top: 8px;
    line-height: 18px;
    text-align: center;
    color: var(--el-text-color-secondary);
    font-size: 12px;
  }

  // 视频项样式
  .video-item {
    position: relative;
    width: 150px; /* 固定宽度 */
    height: 150px; /* 固定高度 */
    border-radius: v-bind(borderRadius);
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    flex-shrink: 0; /* 防止被挤压 */

    .upload-video {
      width: 100%;
      height: 100%;
      object-fit: cover;
      background-color: #000;
    }

    .video-handle {
      position: absolute;
      bottom: 0; /* 操作栏移到底部 */
      left: 0;
      right: 0;
      top: 0;
      display: flex;
      justify-content: space-around; /* 均匀分布操作图标 */
      padding: 5px 0; /* 调整内边距 */
      background: rgba(0, 0, 0, 0.6); /* 调整背景透明度 */
      opacity: 0;
      transition: opacity 0.3s;
      box-sizing: border-box; /* 确保 padding 不会撑大宽度 */

      .handle-icon {
        display: flex;
        align-items: center;
        color: #fff;
        padding: 0 5px; /* 调整内边距 */
        cursor: pointer;
        font-size: 14px;
        .el-icon {
          margin-right: 4px;
        }
        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    &:hover .video-handle {
      opacity: 1;
    }
  }

  // 上传区域卡片样式 (统一为图片上传的卡片风格)
  .upload-area-card {
    width: v-bind(width);
    height: v-bind(height);
    border: 1px dashed var(--el-border-color-darker); /* 虚线边框 */
    border-radius: v-bind(borderRadius);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: border-color 0.3s ease;
    flex-shrink: 0; /* 防止被挤压 */

    &:hover {
      border-color: var(--el-color-primary);
    }

    .upload-empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
      font-size: 14px;
      color: var(--el-text-color-secondary);

      .plus-icon {
        font-size: 40px; /* 加号图标大小 */
        line-height: 1;
        color: var(--el-text-color-placeholder);
        margin-bottom: 8px;
        display: block;
      }

      .el-icon {
        // 移除原来的 VideoCamera 图标，改为统一的 plus-icon
        display: none;
      }
    }
  }
}
</style>