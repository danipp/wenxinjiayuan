<template>
  <view class="photo-section-card">
    <view class="section-header">
      <text class="section-title">活动照片</text>
      <view class="btn-green-border" @click="sendPhoto">
        <u-icon name="camera" color="#07c160" size="14"></u-icon>
        <text class="btn-text">发照片</text>
      </view>
    </view>

    <view class="photo-grid">
      <view
        v-for="(photo, index) in photoList"
        :key="photo.id"
        class="photo-item"
        @click="$emit('view-photo', index)"
      >
        <view class="img-box">
          <image class="grid-img" :src="photo.image" mode="aspectFill"></image>
          <view v-if="index === 0" class="hot-badge">🔥 最受欢迎</view>
        </view>
        <view class="img-footer">
          <text class="photo-author">{{ photo.author }}</text>
          <view class="likes-time-row">
            <text class="time">{{ photo.time }}</text>
            <view class="like-box">
              <u-icon name="heart" color="#94a3b8" size="12"></u-icon>
              <text class="like-num">{{ photo.likes }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import ossUpload from "@/utils/upload"; // 引入您的oss上传逻辑

export default {
  mixins: [ossUpload],
  props: {
    photoList: { type: Array, default: () => [] },
    idx: { type: String | Number, default: () => "" },
  },
  data() {
    return {};
  },
  methods: {
    async sendPhoto() {
      const results = await this.chooseAndUploadImage(
        { count: 1 },
        () => {},
        true,
        "activityPhoto"
      );
      // 活动id idx 调接口之后 this.$emit("refresh")通知更新详情接口
      console.log(results, "123");
    },
  },
};
</script>

<style lang="scss" scoped>
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28rpx;

  .section-title {
    font-size: 34rpx;
    font-weight: bold;
    color: #1a202c;
  }

  .btn-green-border {
    display: flex;
    align-items: center;
    background-color: #ffffff;
    border: 2rpx solid #07c160;
    padding: 8rpx 20rpx;
    border-radius: 24rpx;
    cursor: pointer;

    .btn-text {
      font-size: 22rpx;
      color: #07c160;
      font-weight: bold;
      margin-left: 8rpx;
    }
  }
}

.photo-section-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

  .photo-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24rpx;

    .photo-item {
      background-color: #ffffff;
      border-radius: 16rpx;
      overflow: hidden;
      border: 2rpx solid #edf2f7;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.02);
      display: flex;
      flex-direction: column;

      .img-box {
        position: relative;
        width: 100%;
        height: 240rpx;

        .grid-img {
          width: 100%;
          height: 100%;
        }

        .hot-badge {
          position: absolute;
          left: 16rpx;
          bottom: 16rpx;
          background-color: rgba(255, 110, 34, 0.85);
          color: #ffffff;
          font-size: 20rpx;
          font-weight: bold;
          padding: 4rpx 12rpx;
          border-radius: 8rpx;
        }
      }

      .img-footer {
        padding: 16rpx 20rpx;
        display: flex;
        flex-direction: column;
        gap: 8rpx;

        .photo-author {
          font-size: 26rpx;
          font-weight: bold;
          color: #1a202c;
          text-align: left;
        }

        .likes-time-row {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .time {
            font-size: 22rpx;
            color: #a0aec0;
            text-align: left;
          }

          .like-box {
            display: flex;
            align-items: center;
            gap: 8rpx;

            .like-num {
              font-size: 22rpx;
              color: #718096;
            }
          }
        }
      }
    }
  }
}
</style>
