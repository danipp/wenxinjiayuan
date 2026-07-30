<template>
  <view class="footer-action-bar" :class="{ 'footer-action-live': !isExpired }">
    <button class="main-share-btn" open-type="share">分享</button>
    <button
      v-if="!isExpired"
      class="signup-btn"
      :class="{ 'signup-btn-disabled': !canSignup, 'signup-btn-done': signedUp }"
      :disabled="!canSignup"
      @click="$emit('signup')"
    >
      <text class="signup-text">{{ signupText }}</text>
      <view v-if="!signedUp" class="countdown-row">
        <text class="countdown-prefix">{{ countdownPrefix }}</text>
        <u-count-down
          :time="countdownTime"
          format="DD天 HH:mm:ss"
          autoStart
          @finish="$emit('countdown-finish')"
        ></u-count-down>
      </view>
    </button>
  </view>
</template>

<script>
export default {
  props: {
    isExpired: { type: Boolean, default: false },
    canSignup: { type: Boolean, default: false },
    signedUp: { type: Boolean, default: false },
    signupText: { type: String, default: "立即报名" },
    countdownPrefix: { type: String, default: "距开始" },
    countdownTime: { type: Number, default: 0 },
  },
};
</script>

<style lang="scss" scoped>
.footer-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #ffffff;
  box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
  padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
  box-sizing: border-box;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 20rpx;

  .main-share-btn {
    flex: 1;
    width: 100%;
    height: 96rpx;
    line-height: 96rpx;
    background-color: #07c160;
    color: #ffffff;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 48rpx;
    box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
    padding: 0;
    margin: 0;

    &::after {
      border: none;
    }
  }

  .signup-btn {
    flex: 7;
    height: 96rpx;
    background-color: #07c160;
    color: #ffffff;
    border-radius: 48rpx;
    box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
    padding: 10rpx 0 8rpx;
    margin: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    line-height: 1.2;

    .signup-text {
      font-size: 30rpx;
      font-weight: bold;
    }

    .countdown-row {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20rpx;
      color: rgba(255, 255, 255, 0.86);
      margin-top: 6rpx;
    }

    .countdown-prefix {
      font-size: 20rpx;
      color: rgba(255, 255, 255, 0.86);
      margin-right: 4rpx;
    }

    &::after {
      border: none;
    }
  }

  .signup-btn-disabled {
    background-color: #a0c4a8;
    box-shadow: none;
  }

  .signup-btn-done {
    background-color: #a0c4a8;
    box-shadow: none;
  }

  ::v-deep .u-count-down {
    display: inline-flex;
    align-items: center;
    font-size: 20rpx;
    color: rgba(255, 255, 255, 0.86);
  }
}

.footer-action-live {
  .main-share-btn {
    flex: 3;
    background-color: #ffffff;
    color: #07c160;
    border: 2rpx solid #07c160;
    box-shadow: none;
  }
}
</style>
