<template>
  <div class="video-upload">
    <div class="upload-container">
      <!-- 上传标签 -->
      <div class="upload-label">上传视频：</div>

      <!-- 文件选择区域 -->
      <div class="file-select-area">
        <el-button @click="triggerFileSelect" :disabled="disabled" class="select-btn">
          <el-icon>
            <VideoCamera />
          </el-icon>
          {{ selectedFile && selectedFile.name ? selectedFile.name : selectedFile ? "重新选择视频" : "请选择视频" }}
        </el-button>

        <!-- 隐藏的文件输入 -->
        <input ref="fileInput" type="file" :accept="accept" @change="handleFileSelect" style="display: none" />
      </div>

      <!-- 开始上传按钮 -->
      <div class="upload-action">
        <el-button type="primary" @click="startUpload" :disabled="!selectedFile || uploading || disabled"
          :loading="uploading">
          {{ uploading ? "上传中..." : "开始上传" }}
        </el-button>
      </div>
    </div>

    <!-- 选中的文件信息 -->
    <!-- <div v-if="selectedFile" class="file-info">
      <div class="file-details">
        <el-icon class="file-icon"><VideoPlay /></el-icon>
        <div class="file-meta">
          <div class="file-name">{{ selectedFile.name }}</div>
          <div class="file-size">{{ formatFileSize(selectedFile.size) }}</div>
          <div class="file-duration" v-if="videoDuration">时长: {{ formatDuration(videoDuration) }}</div>
        </div>
        <el-button type="danger" :icon="Delete" size="small" circle @click="removeSelectedFile" :disabled="uploading" />
      </div>

      <div class="video-preview" v-if="videoPreviewUrl">
        <video
          ref="videoPreview"
          :src="videoPreviewUrl"
          controls
          preload="metadata"
          @loadedmetadata="handleVideoLoaded"
          class="preview-video"
        >
          您的浏览器不支持视频预览
        </video>
      </div>
    </div> -->

    <!-- 上传进度 -->
    <div v-if="uploading" class="upload-progress">
      <el-progress :percentage="uploadProgress" :show-text="false" />
      <div class="progress-info">
        <span class="progress-text">正在上传到 {{ uploadType.toUpperCase() }}... {{ uploadProgress }}%</span>
        <span class="upload-speed" v-if="uploadSpeed">{{ uploadSpeed }}</span>
      </div>
    </div>

    <!-- 上传成功后的文件信息 -->
    <div v-if="uploadedFile" class="uploaded-file">
      <div class="success-info">
        <el-icon class="success-icon">
          <CircleCheck />
        </el-icon>
        <span class="success-text">上传成功！</span>
      </div>
      <div class="uploaded-details">
        <div class="uploaded-name">{{ uploadedFile.originalName }}</div>
        <div class="uploaded-url">
          <el-link :href="uploadedFile.url" target="_blank" type="primary"> 查看视频 </el-link>
          <el-button size="small" @click="copyUrl(uploadedFile.url)"> 复制链接 </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from "vue";
import { ElMessage, ElButton, ElIcon, ElProgress, ElLink } from "element-plus";
import { VideoCamera, VideoPlay, Delete, CircleCheck } from "@element-plus/icons-vue";
import { useOssConfig } from "@/hooks/useOssConfig";

// 定义组件属性
const props = defineProps({
  // 上传方式: 'oss' 或 'r2'
  uploadType: {
    type: String,
    default: "oss",
    validator: value => ["oss", "r2"].includes(value)
  },
  ossPath: {
    type: String,
    default: ""
  },
  // OSS配置
  ossConfig: {
    type: Object,
    default: () => ({
      region: "oss-cn-hangzhou",
      accessKeyId: "",
      accessKeySecret: "",
      bucket: "",
      endpoint: ""
    })
  },
  // R2配置
  r2Config: {
    type: Object,
    default: () => ({
      accountId: "",
      accessKeyId: "",
      secretAccessKey: "",
      bucket: "",
      endpoint: ""
    })
  },
  // 文件大小限制（字节）
  maxSize: {
    type: Number,
    default: 100 * 1024 * 1024 // 默认100MB
  },
  // 视频时长限制（秒）
  maxDuration: {
    type: Number,
    default: 300 // 默认5分钟
  },
  // 自定义接受的文件类型
  accept: {
    type: String,
    default: ".mp4,.avi,.mov,.wmv,.flv,.webm,.mkv,video/*"
  },
  // 上传路径前缀
  pathPrefix: {
    type: String,
    default: "videos/"
  },
  // 上传前的钩子函数
  beforeUpload: {
    type: Function,
    default: null
  },
  // 上传成功回调
  onSuccess: {
    type: Function,
    default: null
  },
  // 上传失败回调
  onError: {
    type: Function,
    default: null
  },
  // 上传进度回调
  onProgress: {
    type: Function,
    default: null
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 视频列表 (用于双向绑定)
  videoList: { type: Array, default: () => [] }
});

// 定义事件
const emit = defineEmits([
  "file-selected",
  "upload-start",
  "upload-success",
  "upload-error",
  "upload-progress",
  "update:videoList"
]);

// 响应式数据
const fileInput = ref();
const videoPreview = ref();
const selectedFile = ref(null);
const videoPreviewUrl = ref("");
const videoDuration = ref(0);
const uploading = ref(false);
const uploadProgress = ref(0);
const uploadSpeed = ref("");
const uploadedFile = ref(null);
const startTime = ref(0);
const lastLoaded = ref(0);

// 计算属性
const acceptedTypes = computed(() => {
  return [".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".mkv"];
});

// 触发文件选择
const triggerFileSelect = () => {
  if (props.disabled) return;
  fileInput.value?.click();
};

// 处理文件选择
const handleFileSelect = async event => {
  const target = event.target;
  const file = target.files?.[0];

  if (!file) return;

  if (await validateFile(file)) {
    selectedFile.value = file;
    await createVideoPreview(file);
    emit("file-selected", file);
  }

  // 清空input值，允许重复选择同一文件
  target.value = "";
};

// 验证文件
const validateFile = async file => {
  // 检查文件类型
  const fileExtension = "." + file.name.split(".").pop()?.toLowerCase();
  if (!acceptedTypes.value.includes(fileExtension) && !file.type.startsWith("video/")) {
    ElMessage.error("只支持视频格式文件");
    return false;
  }

  // 检查文件大小
  if (file.size > props.maxSize) {
    ElMessage.error(`视频文件大小不能超过 ${formatFileSize(props.maxSize)}`);
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

// 创建视频预览
const createVideoPreview = async file => {
  try {
    // 创建预览URL
    if (videoPreviewUrl.value) {
      URL.revokeObjectURL(videoPreviewUrl.value);
    }
    videoPreviewUrl.value = URL.createObjectURL(file);

    // 等待下一个tick确保video元素已渲染
    await nextTick();
  } catch (error) {
    console.error("创建视频预览失败:", error);
  }
};

// 处理视频加载完成
const handleVideoLoaded = () => {
  if (videoPreview.value) {
    videoDuration.value = videoPreview.value.duration;

    // 检查视频时长
    if (videoDuration.value > props.maxDuration) {
      ElMessage.error(`视频时长不能超过 ${formatDuration(props.maxDuration)}`);
      removeSelectedFile();
      return;
    }
  }
};

// 开始上传
const startUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.error("请先选择视频文件");
    return;
  }

  uploading.value = true;
  uploadProgress.value = 0;
  uploadSpeed.value = "";
  startTime.value = Date.now();
  lastLoaded.value = 0;

  try {
    emit("upload-start", selectedFile.value);

    let result;
    if (props.uploadType === "oss") {
      result = await uploadToOSS(selectedFile.value);
    } else if (props.uploadType === "r2") {
      result = await uploadToR2(selectedFile.value);
    } else {
      throw new Error("不支持的上传方式");
    }

    const uploadedFileInfo = {
      id: generateId(),
      originalName: selectedFile.value.name,
      fileName: result.fileName,
      size: selectedFile.value.size,
      type: selectedFile.value.type,
      url: result.url,
      duration: videoDuration.value,
      uploadType: props.uploadType.toUpperCase(),
      uploadTime: new Date().toISOString()
    };

    uploadedFile.value = uploadedFileInfo;

    emit("upload-success", result, selectedFile.value);
    props.onSuccess?.(result, selectedFile.value);
    uploadSuccess(result, selectedFile.value);
  } catch (error) {
    console.error("上传错误:", error);
    emit("upload-error", error, selectedFile.value);
    props.onError?.(error, selectedFile.value);
    ElMessage.error(`视频上传失败: ${error.message}`);
  } finally {
    uploading.value = false;
    setTimeout(() => {
      uploadProgress.value = 0;
      uploadSpeed.value = "";
    }, 1000);
  }
};

// 上传到阿里云OSS
const uploadToOSS = async file => {
  const ossCifig = useOssConfig();
  const result = await ossCifig.getOssConfig(file,'materia');
  return {
    fileName: file.name,
    url: result.url,
    objectName: result.name || file.name
  };
};
// 计算上传速度
const calculateUploadSpeed = (bytesLoaded, timeElapsed) => {
  if (timeElapsed === 0) return "";

  const bytesPerSecond = bytesLoaded / timeElapsed;
  const speedMB = bytesPerSecond / (1024 * 1024);

  if (speedMB >= 1) {
    return `${speedMB.toFixed(2)} MB/s`;
  } else {
    const speedKB = bytesPerSecond / 1024;
    return `${speedKB.toFixed(2)} KB/s`;
  }
};

// 移除选中的文件
const removeSelectedFile = () => {
  console.log("删除啦");

  selectedFile.value = null;
  videoDuration.value = 0;
  if (videoPreviewUrl.value) {
    URL.revokeObjectURL(videoPreviewUrl.value);
    videoPreviewUrl.value = "";
  }
  uploadedFile.value = null;
};

// 复制文件URL
const copyUrl = async url => {
  try {
    await navigator.clipboard.writeText(url);
    ElMessage.success("链接已复制到剪贴板");
  } catch (error) {
    const textArea = document.createElement("textarea");
    textArea.value = url;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand("copy");
    document.body.removeChild(textArea);
    ElMessage.success("链接已复制到剪贴板");
  }
};

// 生成文件名
const generateFileName = originalName => {
  const timestamp = Date.now();
  const random = Math.random().toString(36).substring(2, 8);
  const extension = originalName.split(".").pop();
  return `${timestamp}_${random}.${extension}`;
};

// 生成ID
const generateId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substring(2);
};

// 格式化文件大小
const formatFileSize = bytes => {
  if (bytes === 0) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

// 格式化时长
const formatDuration = seconds => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = Math.floor(seconds % 60);
  return `${minutes}:${remainingSeconds.toString().padStart(2, "0")}`;
};

// 上传成功后，自动同步 videoList
const uploadSuccess = (result, file) => {
  const newList = [...props.videoList, { url: result.url, name: file.name }];
  emit("update:videoList", newList);
  emit("upload-success", result, file);
  ElMessage.success(`视频上传成功`);
};

// 删除视频时同步 videoList
const handleRemove = index => {
  const newList = props.videoList.slice();
  newList.splice(index, 1);
  emit("update:videoList", newList);
};

// 暴露方法给父组件
defineExpose({
  triggerFileSelect,
  startUpload,
  removeSelectedFile,
  selectedFile,
  uploadedFile
});
</script>

<style lang="scss" scoped>
.video-upload {
  width: 100%;
  max-width: 800px;

  .upload-container {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background-color: #fafafa;

    .upload-label {
      font-size: 14px;
      color: #606266;
      white-space: nowrap;
    }

    .file-select-area {
      flex: 1;

      .select-btn {
        width: 100%;
        justify-content: flex-start;

        .el-icon {
          margin-right: 8px;
        }
      }
    }

    .upload-action {
      .el-button {
        min-width: 100px;
      }
    }
  }

  .file-info {
    margin-top: 16px;
    padding: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    background-color: #fff;

    .file-details {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      .file-icon {
        font-size: 24px;
        color: #409eff;
      }

      .file-meta {
        flex: 1;

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
          margin-bottom: 2px;
        }

        .file-duration {
          font-size: 12px;
          color: #67c23a;
        }
      }
    }

    .video-preview {
      .preview-video {
        width: 100%;
        max-width: 400px;
        height: auto;
        border-radius: 6px;
        background-color: #000;
      }
    }
  }

  .upload-progress {
    margin-top: 16px;
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 6px;

    .progress-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 8px;

      .progress-text {
        font-size: 14px;
        color: #606266;
      }

      .upload-speed {
        font-size: 12px;
        color: #67c23a;
        font-weight: 500;
      }
    }
  }

  .uploaded-file {
    margin-top: 16px;
    padding: 16px;
    background-color: #f0f9ff;
    border: 1px solid #b3d8ff;
    border-radius: 6px;

    .success-info {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;

      .success-icon {
        color: #67c23a;
        font-size: 18px;
      }

      .success-text {
        color: #67c23a;
        font-weight: 500;
      }
    }

    .uploaded-details {
      .uploaded-name {
        font-size: 14px;
        color: #303133;
        margin-bottom: 8px;
        word-break: break-all;
      }

      .uploaded-url {
        display: flex;
        align-items: center;
        gap: 12px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .video-upload {
    .upload-container {
      flex-direction: column;
      align-items: stretch;
      gap: 12px;

      .upload-label {
        text-align: center;
      }
    }

    .file-info {
      .file-details {
        flex-direction: column;
        align-items: flex-start;
        text-align: center;

        .file-meta {
          width: 100%;
        }
      }

      .video-preview {
        .preview-video {
          max-width: 100%;
        }
      }
    }

    .uploaded-file {
      .uploaded-details {
        .uploaded-url {
          flex-direction: column;
          align-items: stretch;
          gap: 8px;
        }
      }
    }
  }
}
</style>