<template>
    <!-- 触发按钮：动态生成视频数量提示 -->
    <div>
        <div class="preview-trigger" @click="visible = true" :style="{ width: triggerWidth, height: triggerWidth }"
            v-for="(item, index) in videoList" :key="index">
            <div class="trigger-content" @click="handlePreviewVideo(index)">
                <el-icon>
                    <VideoCameraFilled />
                </el-icon>
                <p class="count">视频预览</p>
            </div>
        </div>
    </div>


    <!-- 预览弹窗 -->
    <el-dialog v-model="visible" title="视频预览" :width="dialogWidth" :close-on-click-modal="false"
        :before-close="handleClose" custom-class="video-preview-dialog" top="30px">
        <!-- 视频切换控制 -->
        <div class="video-nav">
            <el-button type="text" icon="ArrowLeft" @click="switchVideo(-1)" :disabled="currentIndex === 0"
                class="nav-btn" />
            <div class="video-count">
                {{ currentIndex + 1 }} / {{ videoList.length }}
            </div>
            <el-button type="text" icon="ArrowRight" @click="switchVideo(1)"
                :disabled="currentIndex === videoList.length - 1" class="nav-btn" />
        </div>

        <!-- 视频播放容器 -->
        <div class="video-container">
            <!-- 加载状态 -->
            <div class="loading-mask" v-if="isLoading">
                <el-loading-spinner size="60" />
            </div>

            <!-- 视频播放器 -->
            <video :src="currentVideo" class="video-player" controls preload="metadata" @loadeddata="isLoading = false"
                @error="handleVideoError">
                您的浏览器不支持HTML5视频播放
            </video>
        </div>

        <!-- 底部操作按钮 -->
        <template #footer>
            <el-button @click="handleClose">关闭</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { ElMessage } from "element-plus";
// 接收视频链接数组
const props = defineProps({
    videoList: {
        type: Array,
        default: () => [],
        validator: (val) => val.every(item => typeof item === 'string')
    }
});

// 组件状态
const visible = ref(false);
const currentIndex = ref(0);
const isLoading = ref(true);
const dialogWidth = ref("80%");
const triggerWidth = ref("60px"); // 触发按钮尺寸

// 当前视频链接
const currentVideo = ref('');

// 初始化视频
const initVideo = () => {
    if (props.videoList.length) {
        currentVideo.value = props.videoList[currentIndex.value];
        isLoading.value = true;
    }
};

// 切换视频
const switchVideo = (direction) => {
    const newIndex = currentIndex.value + direction;
    if (newIndex >= 0 && newIndex < props.videoList.length) {
        currentIndex.value = newIndex;
        initVideo();
    }
};

// 视频加载错误
const handleVideoError = () => {
    isLoading.value = false;
    ElMessage.error(`视频加载失败: ${currentVideo.value}`);
};

// 关闭弹窗
const handleClose = () => {
    visible.value = false;
};

// 下载视频
const handleDownload = () => {
    if (!currentVideo.value) return;

    const link = document.createElement('a');
    link.href = currentVideo.value;
    const fileName = currentVideo.value.split('/').pop().split('?')[0] || `video-${currentIndex.value + 1}.mp4`;
    link.download = fileName;
    link.click();
};

// 自适应处理
const handleResize = () => {
    dialogWidth.value = window.innerWidth < 768 ? '95%' : '80%';
    triggerWidth.value = window.innerWidth < 768 ? '40px' : '60px';
};
const handlePreviewVideo = (index)=>{
    currentIndex.value = index;
    initVideo();
    visible.value = true;
}
// 生命周期
onMounted(() => {
    handleResize();
    window.addEventListener('resize', handleResize);
    nextTick(initVideo);
});

onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
});
</script>

<style lang="scss" scoped>
/* 触发按钮样式 */
.preview-trigger {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border: 2px dashed var(--el-color-primary-light-5);
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
    margin-right: 20px;
    &:hover {
        border-color: var(--el-color-primary);
        transform: scale(1.05);
    }

    .trigger-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: var(--el-color-primary);

        .icon {
            font-size: 20px;
            margin-bottom: 4px;
        }

        .count {
            font-size: 12px;
            margin: 0;
        }
    }
}

/* 弹窗整体样式 */
.video-preview-dialog {
    :deep(.el-dialog__header) {
        border-bottom: 1px solid var(--el-border-color);
        padding: 20px 24px;

        .el-dialog__title {
            font-size: 18px;
            font-weight: 600;
        }
    }

    :deep(.el-dialog__body) {
        padding: 20px;
        text-align: center;
    }

    :deep(.el-dialog__footer) {
        border-top: 1px solid var(--el-border-color-lighter);
        padding: 16px 24px;
        text-align: right;
    }
}

/* 视频导航栏 */
.video-nav {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
    margin-bottom: 20px;

    .nav-btn {
        font-size: 24px;
        color: var(--el-color-primary);
        transition: color 0.3s;

        &:hover {
            color: var(--el-color-primary-dark-2);
        }

        &:disabled {
            color: var(--el-text-color-placeholder);
            cursor: not-allowed;
        }
    }

    .video-count {
        font-size: 16px;
        color: var(--el-text-color-primary);
    }
}

/* 视频容器 */
.video-container {
    position: relative;
    width: 100%;
    max-width: 800px;
    margin: 0 auto;
    aspect-ratio: 16 / 9;
    background-color: #000;
    border-radius: 8px;
    overflow: hidden;

    .loading-mask {
        position: absolute;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: rgba(0, 0, 0, 0.6);
        z-index: 10;
    }

    .video-player {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }
}

/* 响应式适配 */
@media (max-width: 768px) {
    .video-nav {
        gap: 10px;
    }

    .nav-btn {
        font-size: 20px !important;
    }

    .video-count {
        font-size: 14px;
    }
}
</style>