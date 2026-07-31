<template>
  <view class="edit-profile-page">
    <view class="card-box">
      <!-- 1. 头像行 (微信原生 chooseAvatar + 透明覆盖) -->
      <view class="form-row relative-row">
        <text class="label">头像</text>
        <view class="right-box">
          <u-avatar
            class="avatar"
            :src="avatarUrl"
            mode="aspectFill"
          ></u-avatar>
          <u-icon name="arrow-right" color="#cbd5e1" size="28rpx"></u-icon>
        </view>

        <!-- 透明遮罩层：点击整行，实则触发原生头像选择 -->
        <!-- #ifdef MP-WEIXIN -->
        <button
          class="overlay-trigger-btn"
          open-type="chooseAvatar"
          @chooseavatar="handleChooseAvatar"
        ></button>
        <!-- #endif -->
        <!-- #ifndef MP-WEIXIN -->
        <div class="overlay-trigger-btn" @click="handleCustomAvatarClick"></div>
        <!-- #endif -->
      </view>
      <u-line color="#f1f5f9"></u-line>

      <!-- 2. 昵称行 (微信键盘昵称联想推荐 + 透明覆盖) -->
      <view class="form-row relative-row">
        <text class="label">昵称</text>
        <view class="right-box">
          <input
            type="nickname"
            :value="nickname"
            placeholder="请输入昵称"
            @input="handleNicknameInput"
            @blur="handleNicknameBlur"
            style="text-align: right"
          />
          <u-icon name="arrow-right" color="#cbd5e1" size="28rpx"></u-icon>
        </view>
      </view>
      <u-line color="#f1f5f9"></u-line>

      <!-- 3. 电话行 (微信手机一键授权 + 透明覆盖) -->
      <view class="form-row relative-row">
        <text class="label">电话</text>
        <view class="right-box">
          <text class="value" :class="{ highlight: !phoneNumber }">
            {{ phoneNumber || "去授权手机号" }}
          </text>
          <u-icon name="arrow-right" color="#cbd5e1" size="28rpx"></u-icon>
        </view>

        <!-- 透明遮罩层：点击整行，一键拉起微信官方手机号授权解密弹窗 -->
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
      </view>
      <u-line color="#f1f5f9"></u-line>
    </view>
    <view class="fixed-btn">
      <u-button type="success" @click="updateProfile" shape="circle"
        >保存信息</u-button
      >
    </view>
  </view>
</template>

<script>
// 仅引入核心混入逻辑，完全不引入外部组件
import profileMixin from "@/utils/profileMixin.js";
import phoneAuthMixin from "@/utils/phoneAuthMixin.js";
import { updateProfile } from "@/api/login";
export default {
  // 融合头像昵称、手机号混入
  mixins: [profileMixin, phoneAuthMixin],
  data() {
    return {};
  },
  onShow() {
    // 读取缓存的用户头像和昵称
    const cachedProfile = uni.getStorageSync("user_profile_data");
    if (cachedProfile) {
      this.avatarUrl = cachedProfile.avatarUrl;
      this.nickname = cachedProfile.nickname;
    }
    // 读取手机号
    this.initPhoneNumber();
  },
  methods: {
    // A. 头像微信原生态直改
    handleChooseAvatar(e) {
      this.onChooseAvatar(e);
      this.updateProfile();
    },
    // B. 非微信平台头像选择回调
    handleCustomAvatarClick() {
      this.chooseCustomAvatar();
      this.updateProfile();
    },
    updateProfile() {
      uni.showLoading({
        mask: true,
        title: "更新中...",
      });
      updateProfile({
        avatarUrl: this.avatarUrl,
        nickName: this.nickname,
      }).then((res) => {
        uni.showToast({
          title: "保存成功",
          icon: "success",
        });
        this.syncProfileToStorage();
        setTimeout(() => {
          this.$back();
        }, 800);
      });
    },
    // C. 微信联想键盘昵称同步
    handleNicknameInput(e) {
      this.onNicknameInput(e);
    },
    handleNicknameBlur(e) {
      this.onNicknameBlur(e);
    },
    // D. 微信原生手机号一键登录授权
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
    // 同步到本地缓存
    syncProfileToStorage() {
      uni.setStorageSync("user_profile_data", {
        avatarUrl: this.avatarUrl,
        nickname: this.nickname,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.edit-profile-page {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx;
  box-sizing: border-box;

  .card-box {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 8rpx 32rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.015);
  }

  /* 基础列表行 */
  .form-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 32rpx 0;
  }

  /* 核心布局：使其支持绝对定位覆盖 */
  .relative-row {
    position: relative;
  }

  /* 微信原生按钮透明全覆盖层（透明度设为 0） */
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
    cursor: pointer;

    &::after {
      border: none;
    }
  }

  /* 微信原生昵称输入框透明全覆盖层（透明度设为 0） */
  .overlay-trigger-input {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    opacity: 0;
    z-index: 10;
    text-align: right;
    padding-right: 64rpx;
    box-sizing: border-box;
  }

  .label {
    font-size: 30rpx;
    font-weight: bold;
    color: #333333;
  }

  .right-box {
    display: flex;
    align-items: center;
    gap: 20rpx;

    .avatar {
      width: 88rpx;
      height: 88rpx;
      border-radius: 50%;
      background-color: #f1f3f5;
    }

    .value {
      font-size: 28rpx;
      color: #333333;
      font-weight: bold;

      &.gray {
        color: #94a3b8;
        font-weight: normal;
      }

      &.highlight {
        color: #07c160;
      }
    }

    .select-text {
      user-select: text !important;
    }
  }
}
.fixed-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  padding: 40rpx 20rpx;
  background-color: #fff;
  border-radius: 10rpx 10rpx 0 0;
}
</style>