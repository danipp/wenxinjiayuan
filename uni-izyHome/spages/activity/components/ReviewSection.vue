<template>
  <view class="review-section-card">
    <view class="section-header">
      <text class="section-title"
        >活动评价<text class="count">({{ reviewTotal }}条)</text></text
      >
      <view class="btn-green-border" @click="$emit('write')">
        <u-icon name="edit-pen" color="#07c160" size="14"></u-icon>
        <text class="btn-text">写评价</text>
      </view>
    </view>

    <view class="review-list">
      <view
        v-for="(item, idx) in reviews.slice(0, 2)"
        :key="idx"
        class="review-item-row"
      >
        <u-avatar
          class="avatar"
          :src="item.avatar"
          size="72rpx"
          mode="aspectFill"
        ></u-avatar>
        <view class="review-right">
          <view class="name-time-row">
            <text class="name">{{ item.name }}</text>
            <text class="time">{{ item.time }}</text>
          </view>
          <view class="rating-bubble">
            <text class="emoji">{{ item.emoji }}</text>
            <text class="rating-text">{{ item.statusText }}</text>
            <view class="stars">⭐️⭐️⭐️⭐️⭐️</view>
          </view>
          <text class="review-content">{{ item.content }}</text>
        </view>
      </view>
    </view>

    <view
      v-if="reviews.length > 2"
      class="more-reviews-bar"
      @click="$emit('view-all')"
    >
      <text class="more-text">查看更多评价</text>
      <u-icon name="arrow-right" color="#07c160" size="12"></u-icon>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    reviews: { type: Array, default: () => [] },
    reviewTotal: { type: Number, default: 0 },
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

    .count {
      font-size: 26rpx;
      color: #718096;
      margin-left: 8rpx;
    }
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

.review-section-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
  margin-bottom: 28rpx;

  .review-list {
    display: flex;
    flex-direction: column;
    gap: 32rpx;
  }

  .review-item-row {
    display: flex;
    gap: 24rpx;

    .avatar {
      width: 72rpx;
      height: 72rpx;
      border-radius: 50%;
      background-color: #f1f3f5;
    }

    .review-right {
      flex: 1;
      display: flex;
      flex-direction: column;

      .name-time-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12rpx;

        .name {
          font-size: 28rpx;
          font-weight: bold;
          color: #2d3748;
        }
        .time {
          font-size: 22rpx;
          color: #a0aec0;
        }
      }

      .rating-bubble {
        display: flex;
        align-items: center;
        background-color: #f0faf5;
        padding: 12rpx 24rpx;
        border-radius: 24rpx;
        width: fit-content;
        margin-bottom: 16rpx;

        .emoji {
          font-size: 26rpx;
        }
        .rating-text {
          font-size: 24rpx;
          color: #07c160;
          font-weight: bold;
          margin: 0 12rpx 0 8rpx;
        }
        .stars {
          font-size: 20rpx;
        }
      }

      .review-content {
        font-size: 27rpx;
        color: #4a5568;
        line-height: 1.5;
      }
    }
  }

  .more-reviews-bar {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    margin-top: 28rpx;
    padding-top: 24rpx;
    border-top: 2rpx dashed #f1f5f9;
    cursor: pointer;

    .more-text {
      font-size: 26rpx;
      color: #07c160;
      font-weight: bold;
    }
  }
}
</style>
