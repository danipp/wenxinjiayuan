<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="16"
    :closeOnClickOverlay="true"
    @close="handlePopupClose"
    @touchmove.stop.prevent
  >
    <div class="phone-auth-container">
      <!-- 1. 顶部标题栏 -->
      <div class="popup-header">
        <span class="title">{{ mode === 'volunteer' ? '志愿者身份认证' : mode === 'resident' ? '安全授权验证' : '身份认证' }}</span>
        <div class="close-btn" @click="handlePopupClose">
          <u-icon name="close" color="#999" size="18"></u-icon>
        </div>
      </div>

      <!-- 模式说明 -->
      <div v-if="mode === 'all'" class="mode-desc">
        请选择以下任意一种方式进行身份认证
      </div>

      <!-- TAB 切换（all 模式始终显示，volunteer 模式始终显示，resident 模式不显示） -->
      <div v-if="mode !== 'resident'" class="auth-tabs">
        <view
          class="auth-tab"
          :class="{ active: activeTab === 'phone' }"
          @click="activeTab = 'phone'"
        >
          <u-icon name="phone" color="#07c160" size="18"></u-icon>
          <text>手机号授权</text>
        </view>
        <view
          class="auth-tab"
          :class="{ active: activeTab === 'code' }"
          @click="activeTab = 'code'"
        >
          <u-icon name="file-text" color="#3b82f6" size="18"></u-icon>
          <text>志愿者编号</text>
        </view>
      </div>

      <!-- 子面板：手机号授权（resident 模式直接显示，其他模式按 TAB） -->
      <view v-if="mode === 'resident' || activeTab === 'phone'" class="auth-panel">
        <view class="phone-row">
          <span class="item-label">手机号</span>
          <view class="item-value-box">
            <text v-if="phoneNumber" class="phone-display">{{
              phoneNumber
            }}</text>
            <!-- #ifdef MP-WEIXIN -->
            <button
              v-else
              class="auth-action-btn"
              open-type="getPhoneNumber"
              @getphonenumber="onGetPhoneNumber"
            >
              一键授权
            </button>
            <!-- #endif -->
          </view>
        </view>
        <!-- 志愿者模式也直接登录 -->
        <view
          v-if="phoneNumber && mode === 'volunteer'"
          class="confirm-box"
        >
          <button class="confirm-btn phone-btn" @click="emitPhoneSuccess">
            确认登录
          </button>
        </view>
      </view>

      <!-- 子面板：志愿者编号认证（非 resident 模式） -->
      <view v-if="mode !== 'resident' && activeTab === 'code'" class="auth-panel">
        <view class="volunteer-form">
          <view class="form-item">
            <text class="form-label">志愿者编号</text>
            <input
              class="form-input"
              v-model="volunteerCode"
              placeholder="请输入您的志愿者编号"
              maxlength="20"
            />
          </view>
          <view class="volunteer-tips">
            如果您已是注册志愿者并获得了编号，输入后系统将自动完成身份验证。
          </view>
        </view>
        <view class="confirm-box">
          <button
            class="confirm-btn code-btn"
            :disabled="!volunteerCode || verifying"
            @click="handleVolunteerVerify"
          >
            {{ verifying ? '验证中...' : '验证并登录' }}
          </button>
        </view>
      </view>

      <!-- 温馨提示 -->
      <div class="security-tips">
        <u-icon name="lock-fill" color="#07c160" size="14"></u-icon>
        <text class="tips-text">温馨家园将严格保护您的个人隐私安全</text>
      </div>
    </div>
  </u-popup>
</template>

<script>
import phoneAuthMixin from "@/utils/phoneAuthMixin.js";
import request from "@/utils/request.js";

export default {
  name: "PhoneAuthPopup",
  mixins: [phoneAuthMixin],
  props: {
    show: {
      type: Boolean,
      default: false,
    },
    mustAuth: {
      type: Boolean,
      default: false,
    },
    /**
     * 授权模式
     * 'all'       — 默认：同时展示"手机号授权"和"志愿者编号"两个 TAB，二选一
     * 'resident'  — 居民模式：仅手机号一键授权
     * 'volunteer' — 志愿者模式：仅展示两个 TAB（和 all 一样，标题不同）
     */
    mode: {
      type: String,
      default: "all",
      validator: (v) => ["all", "resident", "volunteer"].includes(v),
    },
  },
  watch: {
    show(val) {
      if (val) {
        this.initPhoneNumber();
        this.activeTab = "phone";
        this.volunteerCode = "";
        this.verifying = false;
      }
    },
  },
  data() {
    return {
      activeTab: "phone",
      volunteerCode: "",
      verifying: false,
    };
  },
  methods: {
    handlePopupClose() {
      if (this.mustAuth && !this.phoneNumber) {
        uni.vibrateShort({});
        uni.showToast({
          title: "需完成身份认证后方可继续浏览哦~",
          icon: "none",
          duration: 2000,
        });
      } else {
        this.$emit("update:show", false);
        this.$emit("close");
      }
    },

    // 手机号授权后直接派发成功（针对 volunteer 模式点确认登录）
    emitPhoneSuccess() {
      this.$emit("auth-success", this.phoneNumber);
      this.$emit("update:show", false);
    },

    // 志愿者编号验证
    async handleVolunteerVerify() {
      if (!this.volunteerCode.trim()) return;
      this.verifying = true;
      try {
        const res = await request({
          url: "/api/mine/volunteer/verify",
          method: "post",
          data: { volunteerCode: this.volunteerCode.trim() },
        });
        if (res.code === "00000" && res.data) {
          const phone = res.data.phoneNumber || res.data.cellphone || res.data;
          this.phoneNumber = phone;
          uni.setStorageSync("user_phone_number", phone);
          uni.showToast({ title: "验证成功", icon: "success" });
          this.$emit("auth-success", phone);
          this.$emit("volunteer-verified", {
            phone,
            volunteerCode: this.volunteerCode.trim(),
            ...res.data,
          });
          this.$emit("update:show", false);
        } else {
          uni.showToast({
            title: res.msg || "验证失败，请检查志愿者编号",
            icon: "none",
          });
        }
      } catch (e) {
        uni.showToast({ title: "验证失败，请重试", icon: "none" });
      } finally {
        this.verifying = false;
      }
    },

    mockNonWechatAuth() {
      uni.showModal({
        title: "模拟授权",
        content: "是否一键绑定手机号 138****8000？",
        success: (res) => {
          if (res.confirm) {
            this.phoneNumber = "138****8000";
            uni.setStorageSync("user_phone_number", "138****8000");
            this.$emit("auth-success", "138****8000");
            this.$emit("update:show", false);
          }
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.phone-auth-container {
  background-color: #ffffff;
  padding: 20px 24px calc(30px + env(safe-area-inset-bottom)) 24px;
  display: flex;
  flex-direction: column;

  .popup-header {
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    padding-bottom: 24px;

    .title {
      font-size: 17px;
      font-weight: bold;
      color: #333333;
    }

    .close-btn {
      position: absolute;
      right: 0;
      top: 50%;
      transform: translateY(-50%);
      padding: 4px;
      cursor: pointer;
    }
  }

  .mode-desc {
    text-align: center;
    font-size: 24rpx;
    color: #95a5a6;
    margin-bottom: 24rpx;
  }

  /* ------ TAB 切换 ------ */
  .auth-tabs {
    display: flex;
    gap: 20rpx;
    margin-bottom: 28rpx;

    .auth-tab {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10rpx;
      padding: 20rpx 0;
      border-radius: 16rpx;
      font-size: 28rpx;
      color: #666;
      background: #f5f7fa;

      &.active {
        background: #fff;
        color: #333;
        font-weight: bold;
        box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
      }
    }
  }

  .auth-panel {
    margin-bottom: 20rpx;
  }

  /* ------ 手机授权行 ------ */
  .phone-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #f7f9fb;
    border-radius: 12rpx;
    padding: 20rpx 24rpx;
    box-sizing: border-box;

    .item-label {
      font-size: 28rpx;
      font-weight: bold;
      color: #333333;
    }

    .item-value-box {
      display: flex;
      align-items: center;

      .phone-display {
        font-size: 28rpx;
        color: #7f8c8d;
        font-weight: bold;
      }

      .auth-action-btn {
        margin: 0;
        padding: 0 14px;
        height: 28px;
        line-height: 28px;
        background-color: #07c160;
        color: #ffffff;
        font-size: 12px;
        font-weight: bold;
        border-radius: 14px;
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;

        &::after {
          border: none;
        }

        &:active {
          opacity: 0.9;
        }
      }
    }
  }

  /* ------ 志愿者编号表单 ------ */
  .volunteer-form {
    .form-item {
      margin-bottom: 16rpx;

      .form-label {
        font-size: 26rpx;
        color: #333;
        font-weight: bold;
        margin-bottom: 10rpx;
        display: block;
      }

      .form-input {
        width: 100%;
        height: 80rpx;
        background: #f7f9fb;
        border-radius: 12rpx;
        padding: 0 20rpx;
        font-size: 28rpx;
        box-sizing: border-box;
        border: 2rpx solid #e8ecf1;
      }
    }

    .volunteer-tips {
      font-size: 22rpx;
      color: #95a5a6;
      line-height: 1.6;
      padding: 16rpx;
      background: #f7f9fb;
      border-radius: 10rpx;
    }
  }

  /* ------ 确认按钮 ------ */
  .confirm-box {
    margin-top: 24rpx;

    .confirm-btn {
      width: 100%;
      height: 88rpx;
      line-height: 88rpx;
      font-size: 30rpx;
      font-weight: bold;
      border-radius: 44rpx;
      border: none;

      &::after { border: none; }
      &[disabled] { opacity: 0.5; }

      &.code-btn {
        background-color: #3b82f6;
        color: #ffffff;
      }

      &.phone-btn {
        background-color: #07c160;
        color: #ffffff;
      }
    }
  }

  /* ------ 底部安全提示 ------ */
  .security-tips {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    margin-top: 8rpx;

    .tips-text {
      font-size: 11px;
      color: #95a5a6;
    }
  }
}
</style>
