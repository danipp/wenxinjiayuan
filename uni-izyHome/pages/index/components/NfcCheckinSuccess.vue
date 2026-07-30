<template>
  <view v-if="show" class="nfc-mask">
    <view class="success-card">
      <view class="ring-wrap">
        <!-- 扩散红晕 -->
        <view class="pulse-ring ring-one"></view>
        <view class="pulse-ring ring-two"></view>

        <!-- 核心心跳区域 -->
        <view class="success-circle">
          <view class="heart-shape"></view>
          <!-- 变长、改色且支持无缝流动的心电图线条 -->
          <view class="ecg-line"></view>
        </view>
      </view>

      <text class="success-title">打卡成功</text>
      <text class="success-desc">已识别打卡相框，正在同步打卡信息</text>

      <view class="frame-info">
        <view class="info-row">
          <text class="label">相框编号</text>
          <text class="value">{{ checkinParams.frameId || "--" }}</text>
        </view>
        <view class="info-row">
          <text class="label">打卡位置</text>
          <text class="value">{{
            checkinParams.locationName || "社区打卡点"
          }}</text>
        </view>
      </view>

      <view class="status-row">
        <u-loading-icon
          v-if="submitStatus === 'submitting'"
          size="18"
        ></u-loading-icon>
        <u-icon
          v-else-if="submitStatus === 'success'"
          name="checkmark-circle-fill"
          color="#07c160"
          size="18"
        ></u-icon>
        <u-icon
          v-else
          name="close-circle-fill"
          color="#ff4d4f"
          size="18"
        ></u-icon>
        <text class="status-text">{{ statusText }}</text>
      </view>

      <button
        v-if="submitStatus !== 'submitting'"
        class="close-btn"
        @click="closePopup"
      >
        我知道了
      </button>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    show: {
      type: Boolean,
      default: false,
    },
    checkinParams: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      submitStatus: "submitting",
    };
  },
  computed: {
    statusText() {
      const textMap = {
        submitting: "提交中...",
        success: "同步成功",
        fail: "同步失败，请稍后重试",
      };
      return textMap[this.submitStatus];
    },
  },
  watch: {
    show: {
      immediate: true,
      handler(val) {
        if (val) this.submitCheckin();
      },
    },
  },
  methods: {
    submitCheckin() {
      this.submitStatus = "submitting";

      // 模拟提交
      setTimeout(() => {
        console.log("提交NFC打卡参数：", this.checkinParams);
        this.submitStatus = "success";
        this.$emit("submit-success", this.checkinParams);
      }, 1200);
    },
    closePopup() {
      this.$emit("close");
    },
  },
};
</script>

<style lang="scss" scoped>
.nfc-mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(15, 23, 42, 0.45);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48rpx;
  box-sizing: border-box;

  .success-card {
    width: 100%;
    background-color: #ffffff; /* 保持干净的白底 */
    border-radius: 44rpx;
    padding: 60rpx 44rpx 44rpx;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    align-items: center;
    animation: card-pop 0.4s cubic-bezier(0.16, 1, 0.3, 1) both;

    .ring-wrap {
      width: 212rpx;
      height: 212rpx;
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 36rpx;

      .pulse-ring {
        position: absolute;
        width: 148rpx;
        height: 148rpx;
        border-radius: 50%;
        background-color: rgba(255, 42, 85, 0.1);
        animation: pulse-out 1.5s cubic-bezier(0.25, 0.8, 0.25, 1) infinite;
      }

      .ring-two {
        animation-delay: 0.75s;
      }

      .success-circle {
        width: 188rpx;
        height: 164rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 2;
        position: relative;
        animation: heartbeat-loop 1.5s cubic-bezier(0.25, 0.8, 0.25, 1) infinite;

        .heart-shape {
          width: 112rpx;
          height: 112rpx;
          background: linear-gradient(135deg, #ff6b81 0%, #e63946 100%);
          transform: rotate(-45deg);
          position: relative;
          box-shadow: 0 16rpx 48rpx rgba(230, 57, 70, 0.25);

          &::before,
          &::after {
            content: "";
            position: absolute;
            width: 112rpx;
            height: 112rpx;
            background: inherit;
            border-radius: 50%;
          }

          &::before {
            top: -56rpx;
            left: 0;
          }

          &::after {
            left: 56rpx;
            top: 0;
          }
        }

        .ecg-line {
          position: absolute;
          /* 居中定位 */
          left: 50%;
          transform: translateX(-50%);
          top: 42rpx;
          /* 将宽度增加到 280rpx，使其明显长于爱心，横跨延伸至外部白底上 */
          width: 280rpx;
          height: 40rpx;
          z-index: 3;
          /* 边缘平滑羽化遮罩 */
          -webkit-mask-image: linear-gradient(
            to right,
            transparent,
            #000 15%,
            #000 85%,
            transparent
          );
          mask-image: linear-gradient(
            to right,
            transparent,
            #000 15%,
            #000 85%,
            transparent
          );

          &::before {
            content: "";
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-repeat: repeat-x;
            /* 设定单次波折的重复周期宽度 */
            background-size: 140rpx 40rpx;
            /* 
              将 SVG 路径颜色 stroke 改为 %23ff2a55 (即 #ff2a55 珊瑚红)
              该颜色在白底和红色爱心上均具有极佳的视觉辨识度与色彩协调性
            */
            background-image: url("data:image/svg+xml,%3Csvg width='78' height='20' viewBox='0 0 78 20' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M0 10H16L21 10L26 3L32 18L38 10H78' stroke='white' stroke-width='2.6' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
            /* 添加微弱的外发光阴影，提升精致度 */
            filter: drop-shadow(0 0 2rpx rgba(126, 125, 125, 0.4));
            /* 无缝向左滑动，位移量 -140rpx 与 background-size 宽度完美对应 */
            animation: ecg-flow-seamless 1.5s linear infinite;
          }
        }
      }
    }

    .success-title {
      font-size: 44rpx;
      font-weight: 800;
      color: #1a202c;
    }

    .success-desc {
      font-size: 26rpx;
      color: #718096;
      margin-top: 16rpx;
    }

    .frame-info {
      width: 100%;
      background-color: #f0faf5;
      border-radius: 28rpx;
      padding: 24rpx 28rpx;
      box-sizing: border-box;
      margin-top: 40rpx;

      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        line-height: 56rpx;

        .label {
          font-size: 26rpx;
          color: #718096;
        }

        .value {
          font-size: 26rpx;
          color: #1a202c;
          font-weight: bold;
          max-width: 360rpx;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

    .status-row {
      display: flex;
      align-items: center;
      gap: 12rpx;
      margin-top: 36rpx;

      .status-text {
        font-size: 26rpx;
        color: #4a5568;
      }
    }

    .close-btn {
      width: 100%;
      height: 88rpx;
      line-height: 88rpx;
      border-radius: 44rpx;
      background-color: #07c160;
      color: #ffffff;
      font-size: 30rpx;
      font-weight: bold;
      margin-top: 40rpx;
      box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);

      &::after {
        border: none;
      }
    }
  }
}

/* 卡片轻柔入场 */
@keyframes card-pop {
  0% {
    opacity: 0;
    transform: scale(0.92) translateY(24rpx);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 扩散波纹无缝过渡 */
@keyframes pulse-out {
  0% {
    transform: scale(0.9);
    opacity: 0.85;
  }
  80% {
    transform: scale(1.6);
    opacity: 0;
  }
  100% {
    transform: scale(1.6);
    opacity: 0;
  }
}

/* 仿真实心跳起伏 */
@keyframes heartbeat-loop {
  0% {
    transform: scale(1);
  }
  12% {
    transform: scale(1.08);
  }
  24% {
    transform: scale(0.98);
  }
  36% {
    transform: scale(1.12);
  }
  55% {
    transform: scale(1);
  }
  100% {
    transform: scale(1);
  }
}

/* 
  精密向左滚动的无缝波形动画：
  平滑滑过单次图案宽度(140rpx)，保证在任意时间节点上，图案首尾相连均不出现跳变和断层。
*/
@keyframes ecg-flow-seamless {
  from {
    background-position-x: 0;
  }
  to {
    background-position-x: -140rpx;
  }
}
</style>