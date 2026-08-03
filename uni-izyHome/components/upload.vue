<template>
  <view class="image-upload-wrapper">
    <!-- 已上传图片展示 -->
    <view class="uploaded-list">
      <view class="uploaded-list">
        <view
          class="uploaded-item"
          v-for="(item, index) in currentFileList"
          :key="index"
        >
          <!-- @touchstart="handleTouchStart($event, index)" @touchmove="handleTouchMove($event)"
                        @touchend="handleTouchEnd($event, index)" -->
          <image
            :src="item.url"
            mode="aspectFill"
            class="uploaded-img"
            @click="previewImage(index)"
          ></image>
          <view
            class="dele-icon"
            @click="showDeleteConfirm(index)"
            v-if="!disabled"
          >
            <u-icon name="close" color="#fff" size="16"></u-icon>
          </view>
        </view>
        <!-- 上传区域 -->
        <view class="upload-area" v-if="canAddMore" @click.stop="handleUpload">
          <slot>
            <view>
              <view class="upload-custom-btn">
                <u-icon name="camera-fill" size="28" color="#a0cfff"></u-icon>
                <text class="upload-text">拍照片</text>
                <view class="loading_page" v-if="uploading">
                  <u-loading-icon mode="circle"></u-loading-icon>
                </view>
              </view>
              <view
                class="tips"
                selectable="false"
                space="false"
                decode="false"
              >
                <view class="upload-view">
                  <view v-if="minCount">最少上传{{ minCount }}张图片</view>
                  最多上传{{ maxCount }}张图片
                </view>
                <!-- <view class="upload-view">图片大小不能超过{{ maxSize }}MB</view> -->
              </view>
            </view>
          </slot>
        </view>
      </view>
    </view>
    <!-- 删除确认弹窗 -->
    <u-modal
      :show="deleteModalVisible"
      title="提示"
      content="确定要删除这张图片吗？"
      confirm-text="删除"
      cancel-text="取消"
      @confirm="confirmDelete"
      @cancel="deleteModalVisible = false"
      :showCancelButton="true"
    ></u-modal>
  </view>
</template>

<script>
import ossUpload from "@/utils/upload"; // 引入您的oss上传逻辑

export default {
  mixins: [ossUpload],
  name: "OssImageUpload",
  props: {
    // 支持v-model双向绑定图片数组
    value: {
      type: Array,
      default: () => [],
    },
    // 最少上传数量
    minCount: {
      type: Number,
      default: 0,
    },
    // 最大上传数量
    maxCount: {
      type: Number,
      default: 5,
    },
    // 图片大小限制(MB)
    maxSize: {
      type: Number,
      default: 15, // 默认5MB
    },
    // 选择图片配置
    chooseOptions: {
      type: Object,
      default: () => ({
        sizeType: ["original", "compressed"],
        sourceType: ["album", "camera"],
      }),
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false,
    },
    // 是否显示提示文字
    showText: {
      type: Boolean,
      default: true,
    },
    // 上传提示文字
    uploadText: {
      type: String,
      default: "点击上传",
    },
    // 是否并发上传
    concurrent: {
      type: Boolean,
      default: true,
    },
    ossPath: {
      type: String,
      default: "userImage",
    },
  },
  data() {
    return {
      uploading: false,
      deleteModalVisible: false, // 删除确认弹窗显示状态
      currentDeleteIndex: -1, // 当前要删除的图片索引
      // 初始化响应式数据
      currentFileList: [],
      touchStatus: {
        isTouching: false, // 是否处于触摸状态
        startX: 0, // 触摸起始X坐标
        startY: 0, // 触摸起始Y坐标
        startTime: 0, // 触摸开始时间
        isLongPress: false, // 是否触发了长按
        currentIndex: -1, // 当前触摸的图片索引
      },
      longPressTimer: null,
    };
  },
  watch: {
    // 监听value变化，同步到currentFileList
    value: {
      immediate: true,
      handler(newVal) {
        this.currentFileList = [...newVal];
      },
    },
  },
  computed: {
    // 是否可继续添加
    canAddMore() {
      return this.currentFileList.length < this.maxCount && !this.disabled;
    },
  },
  methods: {
    // 处理上传
    async handleUpload() {
      if (!this.canAddMore) return;

      this.uploading = true;
      try {
        const remaining = this.maxCount - this.currentFileList.length;
        const options = {
          ...this.chooseOptions,
          count: remaining,
        };

        // 选择并上传图片
        const results = await this.chooseAndUploadImage(
          options,
          this.handleProgress,
          this.concurrent,
          this.ossPath
        );

        // 合并结果到文件列表（双向绑定）
        this.currentFileList = [...this.currentFileList, ...results];
        this.$emit("input", this.currentFileList);
        this.$emit("success", this.currentFileList);
      } catch (err) {
        // console.error('上传失败:', err)
        // this.$u.toast(err.message || '上传失败，请重试')
        this.$emit("error", err);
      } finally {
        this.uploading = false;
      }
    },

    // 检查文件大小
    checkFileSize(files) {
      const maxSizeBytes = this.maxSize * 1024 * 1024; // 转换为字节
      return files.every((file) => {
        if (file.size > maxSizeBytes) {
          this.$fuck.showToast("图片大小不能超过" + this.maxSize + "MB");
          throw new Error(`图片大小不能超过${this.maxSize}MB`);
        }
        return true;
      });
    },

    // 重写选择图片逻辑（添加大小校验）
    chooseAndUploadImage(options = {}, progressCallback, concurrent = true) {
      return new Promise((resolve, reject) => {
        const chooseOptions = {
          count: 9,
          sizeType: ["original", "compressed"],
          sourceType: ["album", "camera"],
          ...options,
        };

        uni.chooseImage({
          ...chooseOptions,
          success: (res) => {
            const tempFilePaths = res.tempFilePaths;
            const tempFiles = res.tempFiles || [];

            if (tempFilePaths.length === 0) {
              reject(new Error("未选择图片"));
              return;
            }

            // 图片大小校验
            try {
              this.checkFileSize(tempFiles);
            } catch (err) {
              reject(err);
              return;
            }

            // 包装文件信息
            const files = tempFilePaths.map((path, index) => ({
              path,
              name:
                tempFiles[index]?.name || `image-${Date.now()}-${index}.png`,
              size: tempFiles[index]?.size || 0, // 传递文件大小
              index,
            }));

            // 上传逻辑（复用原方法的上传逻辑）
            if (concurrent) {
              Promise.all(
                files.map((file) =>
                  this.uploadFileToOss(file, progressCallback, this.ossPath)
                )
              )
                .then((results) => {
                  resolve(results.sort((a, b) => a.index - b.index));
                })
                .catch(reject);
            } else {
              const results = [];
              const uploadNext = (index) => {
                if (index >= files.length) {
                  resolve(results);
                  return;
                }
                this.uploadFileToOss(
                  files[index],
                  progressCallback,
                  this.ossPath
                )
                  .then((result) => {
                    results.push(result);
                    uploadNext(index + 1);
                  })
                  .catch(reject);
              };
              uploadNext(0);
            }
          },
          fail: reject,
        });
      });
    },

    // 进度回调
    handleProgress(progress, index) {
      this.$emit("progress", {
        progress,
        index: this.currentFileList.length + index,
      });
    },

    // 触摸开始
    handleTouchStart(e, index) {
      // 记录触摸起始信息
      this.touchStatus = {
        isTouching: true,
        startX: e.touches[0].clientX,
        startY: e.touches[0].clientY,
        startTime: Date.now(),
        isLongPress: false,
        currentIndex: index,
      };

      // 清除之前的定时器
      if (this.longPressTimer) {
        clearTimeout(this.longPressTimer);
      }
      // 设置长按定时器(500ms触发)
      this.longPressTimer = setTimeout(() => {
        if (this.touchStatus.isTouching && !this.disabled) {
          this.touchStatus.isLongPress = true;
          this.showDeleteConfirm(this.touchStatus.currentIndex);
        }
      }, 500);
    },

    // 触摸移动
    handleTouchMove(e) {
      if (!this.touchStatus.isTouching) return;

      // 计算移动距离
      const moveX = Math.abs(e.touches[0].clientX - this.touchStatus.startX);
      const moveY = Math.abs(e.touches[0].clientY - this.touchStatus.startY);

      // 如果移动距离超过10px，判断为滑动，取消长按
      if (moveX > 10 || moveY > 10) {
        this.touchStatus.isTouching = false;
        clearTimeout(this.longPressTimer);
      }
    },

    // 触摸结束
    handleTouchEnd(e, index) {
      clearTimeout(this.longPressTimer);

      // 如果没有触发长按，且是有效触摸，执行点击预览
      if (!this.touchStatus.isLongPress && this.touchStatus.isTouching) {
        this.previewImage(index);
      }

      // 重置触摸状态
      this.touchStatus = {
        isTouching: false,
        startX: 0,
        startY: 0,
        startTime: 0,
        isLongPress: false,
        currentIndex: -1,
      };
    },

    // 显示删除确认弹窗
    showDeleteConfirm(index) {
      this.currentDeleteIndex = index;
      this.deleteModalVisible = true;
    },

    // 确认删除
    confirmDelete() {
      if (this.currentDeleteIndex === -1) return;

      const newList = [...this.currentFileList];
      newList.splice(this.currentDeleteIndex, 1);
      this.currentFileList = newList;
      this.$emit("input", newList);
      this.$emit("remove", newList, this.currentDeleteIndex);

      // 重置状态
      this.deleteModalVisible = false;
      this.currentDeleteIndex = -1;
    },

    // 预览图片
    previewImage(index) {
      const urls = this.currentFileList.map((item) => item.url);
      uni.previewImage({
        current: urls[index],
        urls,
      });
    },

    // 清空图片
    clearAll() {
      this.currentFileList = [];
      this.$emit("input", []);
    },
  },
};
</script>

<style lang="scss" scoped>
.image-upload-wrapper {
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
}

.uploaded-item {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  border-radius: 8rpx;
  overflow: hidden;
  border: 1px solid #eee;

  .dele-icon {
    position: absolute;
    top: 0;
    right: 0;
    z-index: 1;
    background: #19be6b;
    color: #fff;
    padding: 10rpx;
    border-radius: 50rpx 0 0 50rpx;
  }

  .uploaded-img {
    width: 100%;
    height: 100%;
  }
}

.upload-area {
  display: inline-block;
  cursor: pointer;
}

.upload-custom-btn {
  width: 160rpx;
  height: 160rpx;
  background-color: #f4f6f8;
  border: 2rpx dashed #dcdfe6;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
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
    margin-top: 10rpx;
  }

  &.video-btn {
    background-color: #fff5f5;
    border-color: #fbc4c4;
  }
}

.tips {
  .upload-view {
    font-size: 24rpx;
    color: #e45656;
    margin-top: 8rpx;
  }
}
</style>