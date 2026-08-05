<template>
    <div class="image-preview">
        <!-- 点击触发预览的图片列表 -->
        <div class="image-list" v-if="images.length > 0">
            <el-image
                v-for="(img, index) in images"
                :key="index"
                :src="getImageUrl(img)"
                :preview-src-list="getPreviewList"
                :initial-index="index"
                :fit="fit"
                :preview-teleported="true"
                :z-index="9999"
                :style="getItemStyle"
                :class="{ 'single-image': images.length === 1 }"
                @click="handleImageClick(index)"
            >
                <!-- 加载失败占位图 -->
                <template #error>
                    <div class="image-error">
                        <el-icon>
                            <PictureFilled />
                        </el-icon>
                    </div>
                </template>
            </el-image>
        </div>
        <Teleport to="body">
            <!-- 手动控制的预览弹窗（备用方案，如需自定义关闭逻辑） -->
            <el-image-viewer v-if="viewerVisible" :url-list="getPreviewList" :initial-index="currentIndex"
                @close="handleClose" />
        </Teleport>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
// import { PictureFilled } from '@element-plus/icons-vue';

// 父组件传入的参数
const props = defineProps({
    // 图片数组（支持字符串或对象格式，如 ['url1', { url: 'url2' }]）
    images: {
        type: Array,
        default: () => []
    },
    // 图片适应方式（同 el-image 的 fit 属性）
    fit: {
        type: String,
        default: 'cover'
    },
    // 图片的尺寸（px）
    size: {
        type: Number,
        default: 50
    },
    // 是否使用内置预览（el-image 自带的预览），false 则使用手动控制的 viewer
    useBuiltInPreview: {
        type: Boolean,
        default: true
    }
});
// 预览相关状态
const viewerVisible = ref(false);
const currentIndex = ref(0);

// 提取图片 URL（兼容字符串和对象格式）
const getImageUrl = (img) => {
    if (typeof img === 'string') return img;
    if (typeof img === 'object' && img.url) return img.url;
    return '';
};
// 获取预览图片列表（纯 URL 数组）
const getPreviewList = computed(() => {
    return props.images.map(img => getImageUrl(img)).filter(url => url);
});

const getItemStyle = computed(() => ({
    width: `${props.size}px`,
    height: `${props.size}px`
}));

// 点击图片触发预览
const handleImageClick = (index) => {
    if (!props.useBuiltInPreview) {
        currentIndex.value = index;
        viewerVisible.value = true;
    }
    // 若使用内置预览，el-image 会自动处理，无需额外逻辑
};

// 关闭预览
const handleClose = () => {
    viewerVisible.value = false;
};

// 暴露关闭方法给父组件
defineExpose({
    close: handleClose
});
</script>

<style lang="scss" scoped>
.image-preview {
    .image-list {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;

        // 图片容器样式
        .el-image {
            border-radius: 4px;
            cursor: zoom-in;
            transition: transform 0.2s;
        }

        // 尺寸通过内联样式控制（见 getItemStyle）
    }

    // 加载失败样式
    .image-error {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f5f5;
        color: #909399;

        .el-icon {
            font-size: 20px;
        }
    }
}

::v-deep(.el-image-viewer) {
    z-index: 9999 !important;
    /* 高于表格的 z-index（通常 el-table 是 1000 左右） */
}
</style>