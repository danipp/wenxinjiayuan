<template>
  <view class="invite-page">
    <view class="invite-card">
      <view class="form-row relative-row">
        <text class="label">手机号码</text>
        <view class="right-box">
          <text class="value" :class="{ highlight: !phoneNumber }">
            {{ phoneNumber || "一键授权手机号" }}
          </text>
          <u-icon name="arrow-right" color="#cbd5e1" size="14"></u-icon>
        </view>

        <!-- #ifdef MP-WEIXIN -->
        <button
          v-if="!phoneNumber"
          class="overlay-trigger-btn"
          open-type="getPhoneNumber"
          @getphonenumber="handleGetPhoneNumber"
        ></button>
        <view
          v-else
          class="overlay-trigger-btn"
          @click="showPhoneAuthedToast"
        ></view>
        <!-- #endif -->
        <!-- #ifndef MP-WEIXIN -->
        <view class="overlay-trigger-btn" @click="mockNonWechatAuth"></view>
        <!-- #endif -->
      </view>
      <u-line color="#f1f5f9"></u-line>

      <view class="form-row">
        <text class="label">社区名称</text>
        <view class="right-box">
          <text class="value community-value">{{ communityName || "--" }}</text>
        </view>
      </view>

      <button class="confirm-btn" @click="handleConfirm">立即认证</button>
    </view>
  </view>
</template>

<script>
import phoneAuthMixin from "@/utils/phoneAuthMixin.js";

export default {
  mixins: [phoneAuthMixin],
  data() {
    return {
      communityName: "",
    };
  },
  onLoad(options) {
    this.communityName =
      options && options.name ? decodeURIComponent(options.name) : "";
    this.initPhoneNumber();
  },
  methods: {
    handleGetPhoneNumber(e) {
      if (this.phoneNumber || uni.getStorageSync("user_phone_number")) {
        this.showPhoneAuthedToast();
        return;
      }
      this.onGetPhoneNumber(e);
    },
    showPhoneAuthedToast() {
      uni.showToast({
        title: "手机号已授权",
        icon: "none",
      });
    },
    mockNonWechatAuth() {
      uni.showModal({
        title: "模拟授权",
        content: "是否一键绑定手机号 152******85？",
        success: (res) => {
          if (res.confirm) {
            this.phoneNumber = "152******85";
            uni.setStorageSync("user_phone_number", "152******85");
          }
        },
      });
    },
    handleConfirm() {
      if (!this.phoneNumber) {
        uni.showToast({
          title: "请先授权手机号",
          icon: "none",
        });
        return;
      }
      if (!this.communityName) {
        uni.showToast({
          title: "缺少社区信息",
          icon: "none",
        });
        return;
      }

      uni.showToast({
        title: "提交成功",
        icon: "success",
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.invite-page {
  min-height: 100vh;
  padding: 48rpx 32rpx;
  box-sizing: border-box;
  background: #f7f8fa;
}

.invite-card {
  padding: 8rpx 32rpx 48rpx;
  border-radius: 24rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 36rpx rgba(0, 0, 0, 0.05);
}

.form-row {
  min-height: 108rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.relative-row {
  position: relative;
}

.label {
  font-size: 30rpx;
  color: #333333;
  font-weight: 600;
  flex-shrink: 0;
}

.right-box {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12rpx;
}

.value {
  font-size: 28rpx;
  color: #333333;
  font-weight: 600;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  &.highlight {
    color: #07c160;
  }
}

.community-value {
  color: #4a5568;
}

.overlay-trigger-btn {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  z-index: 10;
  padding: 0;
  margin: 0;
  background: none;
  border: none;

  &::after {
    border: none;
  }
}

.confirm-btn {
  width: 100%;
  height: 88rpx;
  margin-top: 48rpx;
  border-radius: 44rpx;
  background: #07c160;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 600;
  line-height: 88rpx;
  box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);

  &::after {
    border: none;
  }
}
</style>
