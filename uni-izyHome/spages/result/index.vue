<template>
  <view class="pay-success-container">
    <!-- 背景流光氛围（增强空间感） -->
    <view class="ambient-glow glow-top"></view>
    <view class="ambient-glow glow-bottom"></view>

    <!-- 页面主体内容 -->
    <view class="content-wrapper">
      <!-- 1. 核心动画图标区域 -->
      <view class="hero-section">
        <view class="icon-outer-ring">
          <view class="ripple-effect"></view>
          <view class="icon-inner">
            <!-- 纯CSS绘制动态对勾 -->
            <view class="checkmark-draw"></view>
          </view>
        </view>
        <text class="status-text">支付成功</text>
        <text class="sub-text">资金已安全支付，交易已完成</text>
      </view>

      <!-- 2. 精致金额展示卡片 -->
      <!-- <view class="amount-card">
        <text class="card-label">本次实付金额</text>
        <view class="amount-display">
          <text class="currency">￥</text>
          <text class="integer">128</text>
          <text class="decimal">.00</text>
        </view>
      </view> -->

      <!-- 3. 安全提示/信任标识 -->
      <view class="security-badge">
        <view class="shield-icon"></view>
        <text class="security-text">官方支付加密通道 · 资金安全保障</text>
      </view>

      <!-- 4. 操作按钮 -->
      <view class="action-group">
        <button
          class="btn btn-primary"
          hover-class="btn-hover"
          @click="onComplete"
        >
          完 成
        </button>
        <button
          class="btn btn-secondary"
          hover-class="btn-hover"
          @click="goHome"
        >
          返回首页
        </button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      // 可通过路由参数传入金额，如：this.amount = options.amount
      amount: "128.00",
    };
  },
  methods: {
    // 点击完成
    onComplete() {
      // 根据你的业务跳转，比如返回上级页面
      uni.navigateBack({ delta: 1 });
    },
    // 返回首页
    goHome() {
      uni.switchTab({ url: "/pages/index/index" });
    },
  },
};
</script>

<style lang="scss" scoped>
.pay-success-container {
  min-height: 100vh;
  background-color: #f8fafc;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 48rpx;
  box-sizing: border-box;
}

/* ------------------- 背景光影 ------------------- */
.ambient-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(120rpx);
  pointer-events: none;
  z-index: 0;
}
.glow-top {
  width: 500rpx;
  height: 500rpx;
  background: rgba(16, 185, 129, 0.12); /* 翡翠绿 */
  top: -120rpx;
  right: -100rpx;
}
.glow-bottom {
  width: 400rpx;
  height: 400rpx;
  background: rgba(99, 102, 241, 0.08); /* 靛蓝微光 */
  bottom: 0rpx;
  left: -80rpx;
}

/* ------------------- 主体包裹器 ------------------- */
.content-wrapper {
  width: 100%;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ------------------- 1. 图标区 ------------------- */
.hero-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
  animation: fadeInDown 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;

  .icon-outer-ring {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 40rpx;
    display: flex;
    justify-content: center;
    align-items: center;

    .ripple-effect {
      position: absolute;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background: rgba(16, 185, 129, 0.15);
      animation: ripple 2.5s infinite ease-out;
    }

    .icon-inner {
      width: 130rpx;
      height: 130rpx;
      background: linear-gradient(135deg, #10b981 0%, #047857 100%);
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      box-shadow: 0 16rpx 32rpx rgba(16, 185, 129, 0.3);
      animation: popIn 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;

      .checkmark-draw {
        width: 32rpx;
        height: 56rpx;
        border-bottom: 7rpx solid #ffffff;
        border-right: 7rpx solid #ffffff;
        transform: rotate(45deg) translate(-4rpx, -4rpx);
        animation: checkDraw 0.4s 0.4s ease forwards;
        opacity: 0;
      }
    }
  }

  .status-text {
    font-size: 44rpx;
    font-weight: 700;
    color: #0f172a;
    letter-spacing: 2rpx;
    margin-bottom: 16rpx;
  }

  .sub-text {
    font-size: 26rpx;
    color: #94a3b8;
  }
}

/* ------------------- 2. 金额卡片 ------------------- */
.amount-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(24rpx);
  border: 1rpx solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 20rpx 50rpx rgba(0, 0, 0, 0.03);
  border-radius: 36rpx;
  padding: 48rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
  animation: fadeInUp 0.8s 0.2s cubic-bezier(0.16, 1, 0.3, 1) both;

  .card-label {
    font-size: 24rpx;
    color: #64748b;
    margin-bottom: 16rpx;
    letter-spacing: 1rpx;
  }

  .amount-display {
    color: #0f172a;
    display: flex;
    align-items: baseline;

    .currency {
      font-size: 36rpx;
      font-weight: 600;
      margin-right: 4rpx;
    }
    .integer {
      font-size: 80rpx;
      font-weight: 800;
      font-family: "DIN Alternate", -apple-system, BlinkMacSystemFont,
        sans-serif;
      letter-spacing: -2rpx;
    }
    .decimal {
      font-size: 38rpx;
      font-weight: 600;
    }
  }
}

/* ------------------- 3. 安全标识 ------------------- */
.security-badge {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 80rpx;
  animation: fadeInUp 0.8s 0.3s cubic-bezier(0.16, 1, 0.3, 1) both;

  .shield-icon {
    width: 24rpx;
    height: 28rpx;
    background: #10b981;
    clip-path: polygon(50% 0%, 100% 20%, 100% 70%, 50% 100%, 0% 70%, 0% 20%);
    opacity: 0.8;
  }

  .security-text {
    font-size: 22rpx;
    color: #94a3b8;
    letter-spacing: 0.5rpx;
  }
}

/* ------------------- 4. 按钮组 ------------------- */
.action-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  animation: fadeInUp 0.8s 0.4s cubic-bezier(0.16, 1, 0.3, 1) both;

  .btn {
    width: 100%;
    height: 100rpx;
    line-height: 100rpx;
    border-radius: 50rpx;
    font-size: 32rpx;
    font-weight: 600;
    border: none;
    transition: all 0.2s ease;

    &::after {
      border: none;
    }

    &.btn-primary {
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      color: #ffffff;
      box-shadow: 0 16rpx 36rpx rgba(16, 185, 129, 0.28);
    }

    &.btn-secondary {
      background: #ffffff;
      color: #334155;
      box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
    }
  }

  .btn-hover {
    transform: translateY(2rpx) scale(0.98);
    opacity: 0.9;
  }
}

/* ------------------- 动画定义 ------------------- */

/* 顶部下沉渐隐 */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 底部上升渐显 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 图标弹出 */
@keyframes popIn {
  0% {
    transform: scale(0);
  }
  70% {
    transform: scale(1.15);
  }
  100% {
    transform: scale(1);
  }
}

/* 波纹扩散 */
@keyframes ripple {
  0% {
    transform: scale(0.8);
    opacity: 0.8;
  }
  100% {
    transform: scale(1.7);
    opacity: 0;
  }
}

/* 动态画勾 */
@keyframes checkDraw {
  0% {
    opacity: 0;
    height: 0;
    width: 0;
  }
  50% {
    opacity: 1;
    height: 56rpx;
    width: 0;
  }
  100% {
    opacity: 1;
    height: 56rpx;
    width: 32rpx;
  }
}
</style>