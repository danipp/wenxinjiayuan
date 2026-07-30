<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="16"
    @close="$emit('update:show', false)"
  >
    <view class="intro-popup-panel">
      <text class="intro-title">活动介绍</text>
      <scroll-view scroll-y class="intro-scroll-body">
        <text class="intro-desc-text">{{ description }}</text>
      </scroll-view>
      <button class="intro-confirm-btn" @click="$emit('update:show', false)">
        我知道了
      </button>
    </view>
  </u-popup>
</template>

<script>
export default {
  props: {
    show: { type: Boolean, default: false },
    description: { type: String, default: "" },
  },
};
</script>

<style lang="scss" scoped>
.intro-popup-panel {
  background-color: #ffffff;
  padding: 48rpx 40rpx calc(48rpx + env(safe-area-inset-bottom)) 40rpx;
  display: flex;
  flex-direction: column;

  .intro-title {
    font-size: 34rpx;
    font-weight: bold;
    color: #1a202c;
    text-align: center;
    margin-bottom: 32rpx;
  }

  .intro-scroll-body {
    max-height: 35vh;
    margin-bottom: 48rpx;

    .intro-desc-text {
      font-size: 28rpx;
      color: #4a5568;
      line-height: 1.6;
    }
  }

  .intro-confirm-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background-color: #07c160;
    color: #ffffff;
    font-size: 30rpx;
    font-weight: bold;
    border-radius: 44rpx;
    box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);

    &::after {
      border: none;
    }
  }
}
</style>
