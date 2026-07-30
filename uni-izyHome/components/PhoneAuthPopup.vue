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
      <!-- 1. 顶部状态栏 -->
      <div class="popup-header">
        <span class="title">安全授权验证</span>
        <div class="close-btn" @click="handlePopupClose">
          <u-icon name="close" color="#999" size="18"></u-icon>
        </div>
      </div>

      <!-- 2. 表单式授权内容 -->
      <div class="auth-form-body">
        <div class="form-item-row">
          <span class="item-label">手机号</span>

          <view class="item-value-box">
            <!-- 已授权：直接展示手机号 -->
            <text v-if="phoneNumber" class="phone-display">{{
              phoneNumber
            }}</text>

            <!-- 未授权：一键授权按钮 -->
            <!-- #ifdef MP-WEIXIN -->
            <button
              v-else
              class="auth-action-btn"
              open-type="getPhoneNumber"
              @getphonenumber="onGetPhoneNumber"
            >
              去授权
            </button>
            <!-- #endif -->
          </view>
        </div>
      </div>

      <!-- 温馨提示 -->
      <div class="security-tips">
        <u-icon name="phone-fill" color="#07c160" size="14"></u-icon>
        <text class="tips-text">志愿者家园将严格保护您的个人隐私安全</text>
      </div>
    </div>
  </u-popup>
</template>

<script>
// 引入刚刚编写的核心手机号授权逻辑混入
import phoneAuthMixin from "@/utils/phoneAuthMixin.js";

export default {
  name: "PhoneAuthPopup",
  mixins: [phoneAuthMixin], // 混入手机号授权状态与微信底层方法
  props: {
    // 弹窗显隐
    show: {
      type: Boolean,
      default: false,
    },
    // 是否为强制授权模式 (默认不强制)
    mustAuth: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    show(val) {
      if (val) {
        // 打开时再次同步本地手机号状态
        this.initPhoneNumber();
      }
    },
  },
  methods: {
    // 拦截弹窗关闭事件（核心控制点）
    handlePopupClose() {
      // 如果开启了强制授权，且本地当前没有授权过的手机号，则拦截关闭
      if (this.mustAuth && !this.phoneNumber) {
        // 1. 触发手机短震动，给用户触觉反馈
        // #ifdef MP-WEIXIN || APP-PLUS
        uni.vibrateShort({
          success: () => console.log("haptic vibration triggered"),
          fail: (err) => console.log(err),
        });
        // #endif

        // 2. 弹出弱提示警示用户
        uni.showToast({
          title: "需授权手机号后方可继续浏览哦~",
          icon: "none",
          duration: 2000,
        });
      } else {
        // 非强制模式下，或已授权状态下，正常允许关闭
        this.$emit("update:show", false);
        this.$emit("close");
      }
    },

    // 备用：模拟非微信环境的点击授权
    mockNonWechatAuth() {
      uni.showModal({
        title: "模拟授权",
        content: "是否一键绑定手机号 138****8000？",
        success: (res) => {
          if (res.confirm) {
            this.phoneNumber = "138****8000";
            uni.setStorageSync("user_phone_number", "138****8000");
            this.$emit("auth-success", "138****8000");
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

  /* 顶部标题 */
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

  /* 表单行样式 */
  .auth-form-body {
    margin-bottom: 20px;

    .form-item-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background-color: #f7f9fb;
      border-radius: 10px;
      padding: 14px 16px;
      box-sizing: border-box;

      .item-label {
        font-size: 15px;
        font-weight: bold;
        color: #333333;
      }

      .item-value-box {
        display: flex;
        align-items: center;

        .phone-display {
          font-size: 15px;
          color: #7f8c8d;
          font-weight: bold;
        }

        .unauth-text {
          font-size: 14px;
          color: #07c160;
          font-weight: bold;
          cursor: pointer;
        }

        /* 授权按钮（重置微信按钮样式，做成绿色小胶囊） */
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
  }

  /* 底部安全盾牌提示 */
  .security-tips {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;

    .tips-text {
      font-size: 11px;
      color: #95a5a6;
    }
  }
}
</style>