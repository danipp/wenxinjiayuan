<template>
  <view class="activity-base-card">
    <view class="title-row">
      <text class="bracket">「</text>
      <text class="title-text">{{ activity.title }}</text>
      <text class="bracket">」</text>
    </view>
    <view class="info-row" style="margin-bottom: 16rpx">
      <text class="label">地址：</text>
      <text class="value text-ellipsis" style="max-width: 360rpx">{{
        activity.location
      }}</text>
      <view v-if="isExpired" class="link-label flex" @click="$emit('open-intro')"
        >活动详情 <u-icon name="arrow-right" color="#999" size="14"></u-icon
      ></view>
    </view>
    <view class="info-row">
      <text class="label">时间：</text>
      <text class="value">{{ activityTimeText }}</text>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    activity: { type: Object, default: () => ({}) },
    isExpired: { type: Boolean, default: false },
    activityTimeText: { type: String, default: "" },
  },
};
</script>

<style lang="scss" scoped>
.activity-base-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 36rpx 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
  margin-bottom: 28rpx;

  .title-row {
    display: flex;
    align-items: center;
    margin-bottom: 28rpx;

    .bracket {
      font-size: 40rpx;
      font-weight: bold;
      color: #2b5c9c;
    }

    .title-text {
      font-size: 36rpx;
      font-weight: 800;
      color: #1a202c;
      background-color: #d1fae5;
      padding: 4rpx 12rpx;
      border-radius: 8rpx;
    }
  }

  .info-row {
    display: flex;
    align-items: center;

    .label {
      font-size: 27rpx;
      color: #718096;
      white-space: nowrap;
    }

    .value {
      font-size: 27rpx;
      color: #2d3748;
    }

    .link-label {
      font-size: 24rpx;
      color: #94a3b8;
      margin-left: auto;
      cursor: pointer;
    }
  }
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
