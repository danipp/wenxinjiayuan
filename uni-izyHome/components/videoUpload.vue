<template>
    <view class="video-upload-wrapper">
        <!-- 已上传视频展示 -->
        <view class="uploaded-list">
            <view v-if="currentFileList.length > 0" class="uploaded-list">
                <view class="uploaded-item" v-for="(item, index) in currentFileList" :key="index"
                    @touchstart="handleTouchStart($event, index)" @touchmove="handleTouchMove($event)"
                    @touchend="handleTouchEnd($event, index)">
                    <!-- 视频封面 -->
                    <image v-if="item.thumb" :src="item.thumb" mode="aspectFill" class="uploaded-img"></image>
                    <view v-else class="fileName flex">点击预览</view>
                    <!-- 视频时长 -->
                    <view class="video-duration">{{ formatDuration(item.duration) }}</view>
                    <!-- 播放图标 -->
                    <!-- <u-icon name="play-circle" class="video-play-icon" size="36"></u-icon> -->
                </view>
            </view>
            <!-- 上传区域 -->
            <view class="upload-area" v-if="canAddMore" @click.stop="handleUpload">
                <slot>
                    <view>
                        <view class="default-upload">
                            <u-icon name="plus" size="40" color="#c0c4cc"></u-icon>
                            <text class="upload-text" v-if="showText">{{ uploadText }}</text>
                            <view class="loading_page" v-if="uploading">
                                <u-loading-icon mode="circle"></u-loading-icon>
                            </view>
                        </view>
                        <view class="tips">
                            <view class="upload-view" v-if="minCount">最少上传{{ minCount }}张图片</view>
                            <view class="upload-view">最多上传{{ maxCount }}个视频</view>
                            <!-- <view class="upload-view" v-if="maxDuration">最长可上传{{ maxDuration }}秒视频</view> -->
                            <!-- <view class="upload-view">视频大小不能超过{{ maxSize }}MB</view> -->
                        </view>
                    </view>
                </slot>
            </view>
        </view>

        <!-- 删除确认弹窗 -->
        <u-modal :show="deleteModalVisible" title="提示" content="确定要删除这个视频吗？" confirm-text="删除" cancel-text="取消"
            @confirm="confirmDelete" @cancel="deleteModalVisible = false" :showCancelButton="true"></u-modal>

        <!-- 视频预览弹窗 -->
        <u-modal :show="videoPreviewVisible" :closeable="true" @cancel="videoPreviewVisible = false" mode="center"
            :round="16" :mask="true" :maskCloseAble="true" :showConfirmButton="false" :showCancelButton="true"
            cancel-text="关闭" v-if="videoPreviewVisible">
            <view class="video-preview-container">
                <video :src="currentPreviewVideo.url" :controls="true" :autoplay="false" :show-fullscreen-btn="true"
                    :show-center-play-btn="true" :enable-play-gesture="true" class="preview-video"
                    :muted="true"></video>
                <view class="video-title" v-if="currentPreviewVideo.name">
                    {{ currentPreviewVideo.name }}
                </view>
            </view>
        </u-modal>
    </view>
</template>

<script>
import ossUpload from '@/utils/upload'

export default {
    mixins: [ossUpload],
    name: 'OssVideoUpload',
    props: {
        // 支持v-model双向绑定视频数组
        value: {
            type: Array,
            default: () => []
        },
        // 最大上传数量
        maxCount: {
            type: Number,
            default: 3
        },
        minCount: {
            type: Number,
            default: 0
        },
        // 视频大小限制(MB)
        maxSize: {
            type: Number,
            default: 50 // 视频通常允许更大容量
        },
        // 最长视频时长(秒)，0为不限制
        maxDuration: {
            type: Number,
            default: 0
        },
        // 选择视频配置
        chooseOptions: {
            type: Object,
            default: () => ({
                sourceType: ['album', 'camera'],
                maxDuration: 60 // 默认最长60秒
            })
        },
        // 是否禁用
        disabled: {
            type: Boolean,
            default: false
        },
        // 是否显示提示文字
        showText: {
            type: Boolean,
            default: true
        },
        // 上传提示文字
        uploadText: {
            type: String,
            default: '点击上传视频'
        },
        // 是否并发上传
        concurrent: {
            type: Boolean,
            default: false // 视频建议串行上传，避免带宽占用过高
        },
        ossPath: {
            type: String,
            default: 'userVideo'
        }
    },
    data() {
        return {
            uploading: false,
            deleteModalVisible: false,
            currentDeleteIndex: -1,
            currentFileList: [],
            touchStatus: {
                isTouching: false,
                startX: 0,
                startY: 0,
                startTime: 0,
                isLongPress: false,
                currentIndex: -1
            },
            longPressTimer: null,
            // 视频预览相关
            videoPreviewVisible: false,
            currentPreviewVideo: {}
        }
    },
    watch: {
        value: {
            immediate: true,
            handler(newVal) {
                this.currentFileList = [...newVal]
            }
        }
    },
    computed: {
        canAddMore() {
            return this.currentFileList.length < this.maxCount && !this.disabled
        }
    },
    methods: {
        // 处理上传
        async handleUpload() {
            if (!this.canAddMore) return

            this.uploading = true
            try {
                const remaining = this.maxCount - this.currentFileList.length
                const options = {
                    ...this.chooseOptions,
                    count: remaining,
                    ...(this.maxDuration && { maxDuration: this.maxDuration })
                }

                const results = await this.chooseAndUploadVideo(
                    options,
                    this.handleProgress,
                    this.concurrent
                )

                this.currentFileList = [...this.currentFileList, ...results]
                this.$emit('input', this.currentFileList)
                this.$emit('success', this.currentFileList)
            } catch (err) {
                this.$emit('error', err)
                // this.$u.toast(err.message)
            } finally {
                this.uploading = false
            }
        },

        // 选择并上传视频
        chooseAndUploadVideo(options = {}, progressCallback, concurrent = true) {
            return new Promise((resolve, reject) => {
                const chooseOptions = {
                    sourceType: ['album', 'camera'],
                    maxDuration: 60,
                    ...options
                }

                uni.chooseVideo({
                    ...chooseOptions,
                    success: (res) => {
                        // 视频信息处理
                        const file = {
                            path: res.tempFilePath,
                            name: `video-${Date.now()}.mp4`,
                            size: res.size,
                            duration: res.duration,
                            thumb: res.thumbTempFilePath,
                            index: 0
                        }

                        // 校验
                        try {
                            this.checkFileSize([file])
                            this.checkVideoDuration([file])
                        } catch (err) {
                            // reject(err)
                            return
                        }

                        // 单文件上传（视频通常单次上传一个）
                        if (concurrent) {
                            Promise.all([this.uploadFileToOss(file, progressCallback, this.ossPath)])
                                .then(results => resolve(results))
                                .catch(reject)
                        } else {
                            this.uploadFileToOss(file, progressCallback, this.ossPath)
                                .then(result => {
                                    // 补充视频特有信息
                                    result.duration = file.duration
                                    result.thumb = file.thumb
                                    resolve([result])
                                })
                                .catch(reject)
                        }
                    },
                    fail: reject
                })
            })
        },

        // 检查视频大小
        checkFileSize(files) {
            const maxSizeBytes = this.maxSize * 1024 * 1024
            return files.every(file => {
                if (file.size > maxSizeBytes) {
                    this.$u.toast(`视频大小不能超过${this.maxSize}MB`)
                    throw new Error(`视频大小不能超过${this.maxSize}MB`)
                }
                return true
            })
        },

        // 检查视频时长
        checkVideoDuration(files) {
            if (!this.maxDuration) return true

            return files.every(file => {
                if (file.duration > this.maxDuration) {
                    this.$u.toast(`视频时长不能超过${this.maxDuration}秒`)
                    throw new Error(`视频时长不能超过${this.maxDuration}秒`)
                }
                return true
            })
        },

        // 格式化时长（秒 -> mm:ss）
        formatDuration(seconds) {
            if (!seconds) return '00:00'
            const minute = Math.floor(seconds / 60)
            const second = Math.floor(seconds % 60)
            return `${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}`
        },

        // 进度回调
        handleProgress(progress, index) {
            this.$emit('progress', {
                progress,
                index: this.currentFileList.length + index
            })
        },

        // 触摸开始（长按删除逻辑）
        handleTouchStart(e, index) {
            this.touchStatus = {
                isTouching: true,
                startX: e.touches[0].clientX,
                startY: e.touches[0].clientY,
                startTime: Date.now(),
                isLongPress: false,
                currentIndex: index
            }

            if (this.longPressTimer) clearTimeout(this.longPressTimer)
            this.longPressTimer = setTimeout(() => {
                if (this.touchStatus.isTouching && !this.disabled) {
                    this.touchStatus.isLongPress = true
                    this.showDeleteConfirm(this.touchStatus.currentIndex)
                }
            }, 500)
        },

        // 触摸移动
        handleTouchMove(e) {
            if (!this.touchStatus.isTouching) return

            const moveX = Math.abs(e.touches[0].clientX - this.touchStatus.startX)
            const moveY = Math.abs(e.touches[0].clientY - this.touchStatus.startY)

            if (moveX > 10 || moveY > 10) {
                this.touchStatus.isTouching = false
                clearTimeout(this.longPressTimer)
            }
        },

        // 触摸结束（预览逻辑）
        handleTouchEnd(e, index) {
            clearTimeout(this.longPressTimer)

            if (!this.touchStatus.isLongPress && this.touchStatus.isTouching) {
                this.openVideoPreview(index)
            }

            this.touchStatus = {
                isTouching: false,
                startX: 0,
                startY: 0,
                startTime: 0,
                isLongPress: false,
                currentIndex: -1
            }
        },

        // 打开视频预览弹窗
        openVideoPreview(index) {
            const video = this.currentFileList[index]
            if (!video || !video.url) {
                this.$u.toast('视频地址无效')
                return
            }

            // 设置当前预览的视频
            this.currentPreviewVideo = video
            // 显示弹窗
            this.videoPreviewVisible = true
        },

        // 显示删除确认
        showDeleteConfirm(index) {
            this.currentDeleteIndex = index
            this.deleteModalVisible = true
        },

        // 确认删除
        confirmDelete() {
            if (this.currentDeleteIndex === -1) return

            const newList = [...this.currentFileList]
            newList.splice(this.currentDeleteIndex, 1)
            this.currentFileList = newList
            this.$emit('input', newList)
            this.$emit('remove', newList, this.currentDeleteIndex)

            this.deleteModalVisible = false
            this.currentDeleteIndex = -1
        },

        // 清空视频
        clearAll() {
            this.currentFileList = []
            this.$emit('input', [])
        }
    }
}
</script>

<style lang="scss" scoped>
.video-upload-wrapper {
    width: 100%;
    padding: 16rpx;
    gap: 10px;
    box-sizing: border-box;
}

.uploaded-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
    margin-bottom: 16rpx;
    position: relative;


}

.uploaded-item {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    border-radius: 8rpx;
    overflow: hidden;
    border: 1px solid #eee;
    background-color: #f5f5f5;

    .uploaded-img {
        width: 100%;
        height: 100%;
    }

    .fileName {
        font-size: 24rpx;
        justify-content: center;
        color: #999;
        height: 100%;
    }

    .video-duration {
        position: absolute;
        bottom: 0;
        right: 0;
        padding: 2rpx 6rpx;
        font-size: 20rpx;
        color: white;
        background-color: rgba(0, 0, 0, 0.5);
        border-top-left-radius: 4rpx;
    }

    .video-play-icon {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        color: rgba(255, 255, 255, 0.8);
    }
}

.upload-area {
    display: inline-block;
    cursor: pointer;
    position: relative;
}

.default-upload {
    width: 160rpx;
    height: 160rpx;
    border: 1px dashed #c0c4cc;
    border-radius: 8rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #f9f9f9;
    position: relative;

    .loading_page {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: rgba(255, 255, 255, 0.8);
        z-index: 999;
    }

    .upload-text {
        font-size: 24rpx;
        color: #909399;
        margin-top: 8rpx;
    }
}

.tips {
    .upload-view {
        font-size: 24rpx;
        color: #e45656;
        margin-top: 8rpx;
    }
}

// 视频预览弹窗样式
.video-preview-container {
    width: 90%;
    padding: 20rpx 0;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.preview-video {
    width: 100%;
    height: 500rpx;
    background-color: #000;
    border-radius: 12rpx;
}

.video-title {
    font-size: 28rpx;
    color: #333;
    margin-top: 20rpx;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>