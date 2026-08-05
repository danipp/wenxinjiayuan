<template>
  <div class="upload-container" :class="{ 'is-media-mode': type === 'media' }">
    <el-upload ref="uploadRef" :id="uuid" action="#" :class="['upload-component', self_disabled ? 'disabled' : '']"
      :multiple="multiple" :disabled="self_disabled" :show-file-list="false" :http-request="handleHttpUpload"
      :before-upload="beforeUpload" :on-error="uploadError" :drag="drag" :accept="acceptTypes" :limit="computedLimit"
      :on-exceed="handleExceed">

      <!-- ===========================
           布局模式 A: Media 混合模式 (上下结构)
           =========================== -->
      <template v-if="type === 'media'">
        <!-- 1. 上方：宽大的上传拖拽区 -->
        <div class="media-upload-dragger"
          v-if="(multiple && fileList.length < limitCount) || (!multiple && fileList.length === 0)"
          v-loading="isUploading" element-loading-text="文件上传中..." element-loading-background="rgba(255,255,255,0.9)">
          <div class="dragger-content">
            <!-- 蓝色盒子图标 -->
            <div class="icon-box">
              <el-icon>
                <Box />
              </el-icon>
            </div>
            <!-- 主文案 -->
            <div class="main-text">点击或者拖动文件上传</div>
            <!-- 格式提示 -->
            <div class="sub-text">
              支持格式：{{ formatExtensions(finalFileTypes) || '.jpg/.png/.mp4' }}
            </div>
          </div>
        </div>

        <!-- 2. 下方：已上传文件列表 (横排 Grid) -->
        <!-- 注意：这里使用了 @click.stop 防止点击文件时触发上传选择 -->
        <div class="media-file-list" v-if="showFile && fileList.length > 0" @click.stop>
          <div v-for="(item, index) in fileList" :key="index" class="file-card-item">
            <!-- 渲染内容 (复用下方逻辑) -->
            <component :is="renderFileContent(item)" :item="item" />

            <!-- 遮罩操作层 -->
            <div class="file-handle media-handle">
              <div v-if="!self_disabled && isEdit && !multiple" class="handle-icon" @click="editFile">
                <el-icon>
                  <Edit />
                </el-icon>
              </div>
              <div class="handle-icon" @click="handleView(index)">
                <el-icon>
                  <ZoomIn />
                </el-icon>
              </div>
              <div v-if="!self_disabled && isDel" class="handle-icon" @click="deleteFile(index)">
                <el-icon>
                  <Delete />
                </el-icon>
              </div>
            </div>
          </div>
        </div>
      </template>


      <!-- ===========================
           布局模式 B: 普通/紧凑模式 (Grid 混合结构)
           =========================== -->
      <template v-else>
        <div class="compact-grid-container">
          <!-- 已上传列表 -->
          <template v-if="showFile">
            <div v-for="(item, index) in fileList" :key="index" class="file-card-item">
              <component :is="renderFileContent(item)" :item="item" />

              <div class="file-handle" @click.stop>
                <div v-if="!self_disabled && isEdit && !multiple" class="handle-icon" @click="editFile">
                  <el-icon>
                    <Edit />
                  </el-icon>
                  <span>编辑</span>
                </div>
                <div class="handle-icon" @click="handleView(index)">
                  <el-icon>
                    <ZoomIn />
                  </el-icon>
                  <span>预览</span>
                </div>
                <div v-if="!self_disabled && isDel" class="handle-icon" @click="deleteFile(index)">
                  <el-icon>
                    <Delete />
                  </el-icon>
                  <span>删除</span>
                </div>
              </div>
            </div>
          </template>

          <!-- 上传按钮 (小方块) -->
          <div v-if="(multiple && fileList.length < limitCount) || (!multiple && fileList.length === 0)"
            class="upload-card-btn" v-loading="isUploading">
            <div class="btn-content">
              <el-icon v-if="type === 'video'" class="type-icon">
                <VideoCamera />
              </el-icon>
              <el-icon v-else class="type-icon">
                <Plus />
              </el-icon>
            </div>
          </div>
        </div>
      </template>

    </el-upload>

    <!-- 底部提示语 -->
    <div class="el-upload__tip" v-if="showTip && type !== 'media'">
      <slot name="tip">请上传大小不超过 {{ fileSize }}M 的文件</slot>
    </div>

    <!-- 预览组件 (保持不变) -->
    <el-image-viewer v-if="showImageViewer" :url-list="previewImageList" :initial-index="initialIndex"
      @close="showImageViewer = false" hide-on-click-modal />
    <el-dialog v-model="showVideoViewer" width="80%" top="5vh" append-to-body class="video-viewer-dialog"
      destroy-on-close title="视频预览">

      <div class="video-container"><video :src="currentVideoUrl" controls autoplay class="video-player"></video></div>
    </el-dialog>
  </div>
</template>

<script setup name="UploadFile">
import { ref, computed, inject, watch, h } from "vue";
import { generateUUID } from "@/utils";
import { ElNotification, formContextKey, ElImageViewer, ElIcon } from "element-plus";
import { VideoCamera, Edit, ZoomIn, Delete, Document, Close, Box, VideoPlay, Plus } from "@element-plus/icons-vue";
import { useOssConfig } from "@/hooks/useOssConfig";

const ossConfig = useOssConfig();

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  ossPath: { type: String, default: "" },
  drag: { type: Boolean, default: true },
  disabled: { type: Boolean, default: false },
  fileSize: { type: Number, default: 5 },
  // type='media' 时启用截图的布局样式
  type: { type: String, default: "image", validator: (val) => ['image', 'video', 'media', 'file'].includes(val) },
  fileType: { type: Array, default: () => [] },
  height: { type: String, default: "148px" }, // 卡片高度
  width: { type: String, default: "148px" },  // 卡片宽度
  borderRadius: { type: String, default: "8px" },
  isEdit: { type: Boolean, default: false },
  isDel: { type: Boolean, default: true },
  multiple: { type: Boolean, default: false },
  limitCount: { type: Number, default: 9 },
  showFile: { type: Boolean, default: true },
  showTip: { type: Boolean, default: true }
});

const emit = defineEmits(["update:modelValue", "success", "error"]);
const fileList = ref([]);
const uuid = ref("upload-" + generateUUID());
const uploadRef = ref(null);
const isUploading = ref(false); // Loading 状态

// 预览状态
const showImageViewer = ref(false);
const showVideoViewer = ref(false);
const initialIndex = ref(0);
const currentVideoUrl = ref("");
const previewImageList = ref([]);

const formContext = inject(formContextKey, void 0);
const self_disabled = computed(() => props.disabled || formContext?.disabled || isUploading.value);

// 类型配置
const defaultMimeTypes = {
  image: ["image/jpeg", "image/png", "image/gif"],
  video: ["video/mp4", "video/webm", "video/avi"],
  media: ["image/jpeg", "image/png", "image/gif", "video/mp4", "video/webm"], // 默认混合类型
  file: []
};
const finalFileTypes = computed(() => props.fileType.length > 0 ? props.fileType : defaultMimeTypes[props.type] || []);
const acceptTypes = computed(() => {
  if (props.type === 'media' && props.fileType.length === 0) return "image/*,video/*";
  return finalFileTypes.value.join(",");
});
const computedLimit = computed(() => props.multiple ? props.limitCount - fileList.value.length : (fileList.value.length === 0 ? 1 : 0));

watch(() => props.modelValue, (val) => {
  fileList.value = val.map(item => (typeof item === 'string' ? { url: item, name: item.split('/').pop() } : item));
}, { deep: true, immediate: true });

// 辅助函数
const getFileCategory = (url) => {
  if (!url) return 'file';
  const ext = url.split('.').pop().toLowerCase();
  if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext)) return 'image';
  if (['mp4', 'webm', 'avi', 'mov'].includes(ext)) return 'video';
  return 'file';
};

const formatExtensions = (types) => {
  const exts = types.map(t => t.includes('/') ? '.' + t.split('/')[1] : t);
  return [...new Set(exts)].slice(0, 5).join('/');
}

// 渲染函数 (JSX 风格，用于简化模板中的 v-if/else 重复)
const renderFileContent = (item) => {
  const category = getFileCategory(item.url);
  if (category === 'image') return h('img', { src: item.url, class: 'preview-img' });
  if (category === 'video') return h('div', { class: 'preview-video-box' }, [
    h('video', { src: item.url, class: 'preview-video' }),
    h(ElIcon, { class: 'play-icon' }, () => h(VideoPlay))
  ]);
  return h('div', { class: 'preview-file' }, [
    h(ElIcon, { class: 'icon' }, () => h(Document)),
    h('span', { class: 'name', title: item.name }, item.name)
  ]);
};

// 交互逻辑
const handleView = (index) => {
  const item = fileList.value[index];
  const cat = getFileCategory(item.url);
  if (cat === 'image') {
    const imgs = fileList.value.filter(f => getFileCategory(f.url) === 'image');
    previewImageList.value = imgs.map(f => f.url);
    initialIndex.value = previewImageList.value.indexOf(item.url);
    showImageViewer.value = true;
  } else if (cat === 'video') {
    currentVideoUrl.value = item.url;
    showVideoViewer.value = true;
  } else {
    window.open(item.url, '_blank');
  }
};

const handleHttpUpload = async (options) => {
  isUploading.value = true;
  try {
    const res = await ossConfig.getOssConfig(options.file, props.ossPath);
    if (res?.url) {
      const newItem = { url: res.url, name: options.file.name || res.name };
      fileList.value = props.multiple ? [...fileList.value, newItem] : [newItem];
      emit("update:modelValue", fileList.value);
      emit("success", newItem);
    }
  } catch (e) {
    emit("error", e);
    ElNotification.error("上传失败");
  } finally {
    isUploading.value = false;
  }
};

const deleteFile = (idx) => {
  fileList.value.splice(idx, 1);
  emit("update:modelValue", fileList.value);
  if (uploadRef.value) uploadRef.value.clearFiles();
};
const editFile = () => document.querySelector(`#${uuid.value} .el-upload__input`)?.click();
const beforeUpload = (file) => {
  if (file.size / 1024 / 1024 > props.fileSize) {
    ElNotification.warning(`文件不能超过 ${props.fileSize}M`);
    return false;
  }
  return true; // 简单校验，依赖 accept
};
const handleExceed = () => ElNotification.warning(`最多上传 ${props.limitCount} 个文件`);
</script>

<style scoped lang="scss">
// 变量绑定
$w: v-bind(width);
$h: v-bind(height);
$r: v-bind(borderRadius);

.upload-container {
  width: 100%;

  :deep(.el-upload) {
    width: 100%;
    display: block; // 关键：Media模式下改为块级，方便上下布局
    border: none;

    .el-upload-dragger {
      padding: 0;
      border: none;
      background: transparent;
      width: 100%;
      height: auto;

      &.is-dragover {
        border: none;
      }

      // 移除默认拖拽边框，由内部元素控制
    }
  }
}

/* ================= Media 模式样式 (仿截图) ================= */
.media-upload-dragger {
  width: 100%;
  height: 180px; // 截图中的高度感
  border: 1px dashed var(--el-border-color); // 默认边框
  border-radius: 8px;
  background-color: #fcfcfc;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s;
  cursor: pointer;
  margin-bottom: 16px; // 撑开与下方列表的距离

  &:hover,
  :deep(.is-dragover) & {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
  }

  .dragger-content {
    text-align: center;

    .icon-box {
      font-size: 48px;
      color: #557df8; // 截图中的蓝色图标颜色
      margin-bottom: 12px;
      display: inline-block;
      // 如果需要图标背景框
      // background: #eef2fe; padding: 10px; border-radius: 12px;
    }

    .main-text {
      font-size: 14px;
      color: #333;
      font-weight: 500;
      margin-bottom: 8px;
    }

    .sub-text {
      font-size: 12px;
      color: #999;
    }
  }
}

.media-file-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  width: 100%;
  // 横排展示，保持原有的卡片大小
}

/* ================= 紧凑/普通 模式样式 ================= */
.compact-grid-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.upload-card-btn {
  width: $w;
  height: $h;
  border: 1px dashed var(--el-border-color);
  border-radius: $r;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: 0.3s;

  &:hover {
    border-color: var(--el-color-primary);
    color: var(--el-color-primary);
  }

  .btn-content {
    text-align: center;
    color: #8c939d;

    .type-icon {
      font-size: 24px;
      display: block;
      margin: 0 auto 8px;
    }

    .btn-text {
      font-size: 12px;
    }
  }
}

/* ================= 通用文件卡片样式 (Grid Item) ================= */
.file-card-item {
  position: relative;
  width: $w;
  height: $h;
  border-radius: $r;
  overflow: hidden;
  border: 1px solid var(--el-border-color-lighter);
  background: #f8f9fa;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;

  // 内容样式
  :deep(.preview-img),
  :deep(.preview-video) {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  :deep(.preview-video-box) {
    width: 100%;
    height: 100%;
    position: relative;

    .play-icon {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: #fff;
      font-size: 32px;
      opacity: 0.8;
    }
  }

  :deep(.preview-file) {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 10px;

    .icon {
      font-size: 40px;
      color: #909399;
      margin-bottom: 5px;
    }

    .name {
      font-size: 12px;
      color: #606266;
      text-align: center;
      width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  // 遮罩层
  .file-handle {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    justify-content: center;
    gap: 15px;
    opacity: 0;
    transition: 0.3s;
    z-index: 10;
    flex-wrap: wrap;

    .handle-icon {
      color: #fff;
      cursor: pointer;
      font-size: 14px;
      white-space: nowrap;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;

      &:hover {
        color: var(--el-color-primary);
      }
    }

    &.media-handle {
      .handle-icon {
        font-size: 25px;
      }
    }
  }

  &:hover .file-handle {
    opacity: 1;
  }
}

/* ================= 视频预览弹窗 ================= */
:deep(.video-viewer-dialog) {
  background: transparent;
  box-shadow: none;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 0;
    background: transparent;
  }
}

.video-viewer-header {
  position: absolute;
  top: -40px;
  width: 100%;
  display: flex;
  justify-content: space-between;
  color: #fff;
  font-size: 16px;
  z-index: 2000;

  .close-btn {
    font-size: 24px;
    cursor: pointer;
  }
}

.video-container {
  width: 100%;
  height: 80vh;
  background: rgba(0, 0, 0, 0.9);
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;

  .video-player {
    max-width: 100%;
    max-height: 100%;
  }
}
</style>