<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="16"
    @close="handleCancel"
    :safeAreaInsetBottom="true"
    @touchmove.stop.prevent
  >
    <div class="profile-popup-content">
      <!-- 头部 -->
      <div class="popup-header">
        <span class="title">设置个人资料</span>
        <div class="close-btn" @click="handleCancel">
          <u-icon name="close" color="#999" size="18"></u-icon>
        </div>
      </div>

      <!-- 表单主体 -->
      <div class="profile-form-body">
        <!-- 头像编辑区 -->
        <div class="avatar-select-container">
          <!-- #ifdef MP-WEIXIN -->
          <!-- 微信环境下，包裹住原生 chooseAvatar 按钮 -->
          <button
            class="native-avatar-btn"
            open-type="chooseAvatar"
            @chooseavatar="onChooseAvatar"
          >
            <!-- <image class="avatar-preview" :src="avatarUrl || defaultAvatar" mode="aspectFill"></image> -->
            <u-avatar
              :src="avatarUrl || defaultAvatar"
              size="160rpx"
            ></u-avatar>
            <div class="camera-icon-badge">
              <u-icon name="camera-fill" color="#ffffff" size="12"></u-icon>
            </div>
          </button>
          <!-- #endif -->

          <!-- #ifndef MP-WEIXIN -->
          <!-- 非微信小程序，回退到常规系统相册/相机 -->
          <div class="native-avatar-btn" @click="chooseCustomAvatar">
            <image
              class="avatar-preview"
              :src="avatarUrl || defaultAvatar"
              mode="aspectFill"
            ></image>
            <div class="camera-icon-badge">
              <u-icon name="camera-fill" color="#ffffff" size="12"></u-icon>
            </div>
          </div>
          <!-- #endif -->
          <text class="tips-text">点击设置您的社区头像</text>
        </div>

        <!-- 昵称输入区 -->
        <div class="input-card">
          <text class="input-label">昵称</text>
          <!-- 采用 type="nickname" 确保微信环境下键盘能推荐微信昵称 -->
          <input
            type="nickname"
            v-model="nickname"
            placeholder="请输入您的昵称"
            class="nickname-input"
            placeholder-class="placeholder-style"
            @input="onNicknameInput"
            @blur="onNicknameBlur"
          />
        </div>
      </div>

      <!-- 底部双按钮组 -->
      <div class="footer-btn-box">
        <button class="action-btn btn-cancel" @click="handleCancel">
          取消
        </button>
        <button class="action-btn btn-confirm" @click="handleConfirm">
          确认
        </button>
      </div>
    </div>
  </u-popup>
</template>

<script>
// 引入刚刚编写的核心逻辑混入
import profileMixin from "@/utils/profileMixin.js";

export default {
  name: "ProfileEditPopup",
  mixins: [profileMixin], // 引入混入，当前组件立刻拥有所有头像/昵称逻辑
  props: {
    // 控制弹窗显隐
    show: {
      type: Boolean,
      default: false,
    },
    // 初始化头像，可从父组件传入
    initAvatar: {
      type: String,
      default: "",
    },
    // 初始化昵称，可从父组件传入
    initNickname: {
      type: String,
      default: "",
    },
  },
  watch: {
    show(val) {
      if (val) {
        // 弹窗打开时，利用 Mixin 内的 reset 方法填充父组件传过来的初始值
        this.resetProfileData(this.initAvatar, this.initNickname);
      }
    },
  },
  methods: {
    // 点击取消
    handleCancel() {
      this.$emit("update:show", false);
      this.$emit("cancel");
    },
    // 点击确认
    handleConfirm() {
      // 调用 Mixin 的公共表单验证方法
      if (!this.validateProfile()) return;

      // 验证通过，向父组件抛出最新设置的值
      this.$emit("confirm", {
        avatarUrl: this.avatarUrl,
        nickname: this.nickname,
      });
      this.$emit("update:show", false);
    },
  },
};
</script>

<style lang="scss" scoped>
.profile-popup-content {
  background-color: #ffffff;
  padding: 20px 24px calc(20px + env(safe-area-inset-bottom)) 24px;
  display: flex;
  flex-direction: column;

  /* 头部 */
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

  .profile-form-body {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 30px;

    /* 头像框及微标 */
    .avatar-select-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 24px;

      .native-avatar-btn {
        position: relative;
        width: 80px;
        height: 80px;
        border-radius: 50%;
        padding: 0;
        margin: 0;
        background: none;
        overflow: visible;
        border: none;
        cursor: pointer;

        &::after {
          border: none;
        }

        .avatar-preview {
          width: 80px;
          height: 80px;
          border-radius: 50%;
          background-color: #f1f3f5;
        }

        .camera-icon-badge {
          position: absolute;
          right: 0;
          bottom: 0;
          width: 24px;
          height: 24px;
          background-color: #07c160;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 2px solid #ffffff;
        }
      }

      .tips-text {
        font-size: 12px;
        color: #999999;
        margin-top: 10px;
      }
    }

    /* 昵称卡片栏 */
    .input-card {
      width: 100%;
      display: flex;
      align-items: center;
      background-color: #f7f9fb;
      border-radius: 10px;
      padding: 14px 16px;
      box-sizing: border-box;

      .input-label {
        font-size: 15px;
        font-weight: bold;
        color: #333333;
        width: 60px;
      }

      .nickname-input {
        flex: 1;
        font-size: 15px;
        color: #333333;
        background: none;
        border: none;
        padding: 0;
      }

      .placeholder-style {
        color: #b2b2b2;
      }
    }
  }

  /* 底部按钮栏 */
  .footer-btn-box {
    display: flex;
    gap: 16px;

    .action-btn {
      flex: 1;
      height: 48px;
      line-height: 48px;
      font-size: 16px;
      font-weight: bold;
      border-radius: 24px;

      &::after {
        border: none;
      }

      &.btn-cancel {
        background-color: #f5f7fa;
        color: #555555;
      }

      &.btn-confirm {
        background-color: #07c160; // 微信绿色主题
        color: #ffffff;
      }
    }
  }
}
</style>