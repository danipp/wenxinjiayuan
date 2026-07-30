<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="36rpx"
    @close="handleClose"
    @touchmove.stop.prevent
  >
    <view class="frame-detail-popup">
      <view class="popup-handle"></view>

      <scroll-view
        scroll-y
        class="popup-scroll"
        :class="{ 'no-footer': !showFooter }"
      >
        <view class="frame-cover-box">
          <image
            class="frame-cover"
            :src="frameInfo.image"
            mode="aspectFill"
          ></image>
          <view class="price-badge">
            <text class="price-symbol">¥</text>
            <text class="price-num">{{ priceText }}</text>
          </view>
        </view>

        <view class="detail-content">
          <view class="title-row">
            <text class="frame-title">{{ frameInfo.name || "打卡相框" }}</text>
            <text class="stock-tag">库存{{ frameInfo.stock || 0 }}件</text>
          </view>
          <text class="frame-desc">{{
            frameInfo.desc || "用于社区打卡的 NFC 智能相框"
          }}</text>

          <view class="info-card">
            <view class="info-row">
              <text class="label">相框编号</text>
              <text class="value">{{ frameInfo.frameNo || "--" }}</text>
            </view>
            <view class="info-row">
              <text class="label">适用场景</text>
              <text class="value">{{ frameInfo.scene || "社区打卡" }}</text>
            </view>
            <view class="info-row">
              <text class="label">规格尺寸</text>
              <text class="value">{{ frameInfo.size || "--" }}</text>
            </view>
            <view class="info-row">
              <text class="label">配送方式</text>
              <text class="value">{{ frameInfo.delivery || "社区配送" }}</text>
            </view>
          </view>

          <view class="feature-list">
            <view
              v-for="item in frameInfo.features || []"
              :key="item"
              class="feature-item"
            >
              <u-icon
                name="checkmark-circle-fill"
                color="#07c160"
                size="28rpx"
              ></u-icon>
              <text>{{ item }}</text>
            </view>
          </view>
        </view>
      </scroll-view>

      <view v-if="showFooter" class="footer-bar">
        <button class="buy-btn" @click="handlePay">{{ buttonText }}</button>
      </view>
    </view>
  </u-popup>
</template>

<script>
export default {
  props: {
    show: {
      type: Boolean,
      default: false,
    },
    frameInfo: {
      type: Object,
      default: () => ({}),
    },
    showFooter: {
      type: Boolean,
      default: true,
    },
    buttonText: {
      type: String,
      default: "立即购买",
    },
  },
  computed: {
    priceText() {
      const price = Number(this.frameInfo.price || 0);
      return price.toFixed(2);
    },
  },
  methods: {
    handleClose() {
      this.$emit("close");
    },
    handlePay() {
      this.$emit("pay", this.frameInfo);
    },
  },
};
</script>

<style lang="scss" scoped>
.frame-detail-popup {
  background-color: #ffffff;
  padding-bottom: calc(152rpx + env(safe-area-inset-bottom));
  max-height: 82vh;
  overflow: hidden;
  border-radius: 72rpx;

  .popup-handle {
    width: 84rpx;
    height: 8rpx;
    border-radius: 8rpx;
    background-color: #e2e8f0;
    margin: 20rpx auto 24rpx;
  }

  .popup-scroll {
    height: calc(82vh - 204rpx);

    &.no-footer {
      height: calc(82vh - 52rpx);
    }
  }

  .frame-cover-box {
    margin: 0 32rpx;
    height: 380rpx;
    border-radius: 32rpx;
    overflow: hidden;
    position: relative;
    background-color: #edf2f7;

    .frame-cover {
      width: 100%;
      height: 100%;
    }

    .price-badge {
      position: absolute;
      right: 24rpx;
      bottom: 24rpx;
      background-color: rgba(7, 193, 96, 0.94);
      color: #ffffff;
      border-radius: 36rpx;
      padding: 10rpx 24rpx;
      display: flex;
      align-items: baseline;

      .price-symbol {
        font-size: 24rpx;
        font-weight: bold;
      }

      .price-num {
        font-size: 40rpx;
        font-weight: 800;
        margin-left: 4rpx;
      }
    }
  }

  .detail-content {
    padding: 32rpx;

    .title-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16rpx;

      .frame-title {
        flex: 1;
        font-size: 38rpx;
        font-weight: 800;
        color: #1a202c;
      }

      .stock-tag {
        font-size: 22rpx;
        color: #07c160;
        background-color: #f0faf5;
        border-radius: 20rpx;
        padding: 6rpx 16rpx;
        margin-left: 16rpx;
      }
    }

    .frame-desc {
      display: block;
      font-size: 26rpx;
      color: #718096;
      line-height: 1.6;
      margin-bottom: 28rpx;
    }

    .info-card {
      background-color: #f8fafc;
      border-radius: 24rpx;
      padding: 20rpx 24rpx;
      margin-bottom: 28rpx;

      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        line-height: 56rpx;

        .label {
          font-size: 26rpx;
          color: #94a3b8;
        }

        .value {
          font-size: 26rpx;
          color: #2d3748;
          font-weight: bold;
          max-width: 420rpx;
          text-align: right;
        }
      }
    }

    .feature-list {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;

      .feature-item {
        display: flex;
        align-items: center;
        background-color: #f0faf5;
        border-radius: 28rpx;
        padding: 10rpx 18rpx;
        font-size: 24rpx;
        color: #4a5568;

        text {
          margin-left: 8rpx;
        }
      }
    }
  }

  .footer-bar {
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
    padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
    box-sizing: border-box;

    .buy-btn {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      border-radius: 48rpx;
      background-color: #07c160;
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
      box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);

      &::after {
        border: none;
      }
    }
  }
}
</style>