<template>
  <view class="add-contact-page">
    <view class="tips-card">
      <view class="tips-icon">!</view>
      <view class="tips-content">
        <text class="tips-title">添加紧急联系人</text>
        <text class="tips-desc"
          >请填写真实有效的信息，方便紧急情况下快速联系。</text
        >
      </view>
    </view>

    <view class="form-card">
      <view class="form-item">
        <text class="label">姓名</text>
        <input
          v-model="form.name"
          class="input"
          placeholder="请输入联系人姓名"
          placeholder-class="placeholder"
          maxlength="20"
        />
      </view>
      <u-line color="#f1f5f9"></u-line>

      <view class="form-item">
        <text class="label">手机号</text>
        <input
          v-model="form.phone"
          class="input"
          type="number"
          placeholder="请输入联系人手机号"
          placeholder-class="placeholder"
          maxlength="11"
        />
      </view>
      <u-line color="#f1f5f9"></u-line>

      <view class="form-item relation-item">
        <text class="label">关系</text>
        <input
          v-model="form.relation"
          class="input"
          placeholder="请输入关系，例如：父亲、邻居"
          placeholder-class="placeholder"
          maxlength="10"
        />
      </view>

      <view class="relation-options">
        <view
          v-for="item in relationOptions"
          :key="item"
          class="relation-chip"
          :class="{ active: form.relation === item }"
          @click="selectRelation(item)"
        >
          {{ item }}
        </view>
      </view>
    </view>

    <view class="footer-bar">
      <button
        class="submit-btn"
        :class="{ active: isFormValid }"
        @click="handleSubmit"
      >
        保存联系人
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      contactId: "",
      form: {
        name: "",
        phone: "",
        relation: "",
      },
      relationOptions: [
        "父亲",
        "母亲",
        "子女",
        "配偶",
        "亲属",
        "邻居",
        "朋友",
        "其他",
      ],
    };
  },
  computed: {
    isFormValid() {
      return (
        this.form.name.trim() &&
        /^1\d{10}$/.test(this.form.phone) &&
        this.form.relation.trim()
      );
    },
  },
  onLoad(options) {
    if (options && options.id) {
      this.contactId = options.id;
      this.form.name = options.name ? decodeURIComponent(options.name) : "";
      this.form.phone = options.phone || "";
      this.form.relation = options.relation
        ? decodeURIComponent(options.relation)
        : "";
    }

    uni.setNavigationBarTitle({
      title: this.contactId ? "编辑联系人" : "添加联系人",
    });
  },
  methods: {
    selectRelation(item) {
      this.form.relation = item;
    },
    handleSubmit() {
      if (!this.form.name.trim()) {
        uni.showToast({ title: "请输入联系人姓名", icon: "none" });
        return;
      }
      if (!/^1\d{10}$/.test(this.form.phone)) {
        uni.showToast({ title: "请输入正确手机号", icon: "none" });
        return;
      }
      if (!this.form.relation.trim()) {
        uni.showToast({ title: "请输入联系人关系", icon: "none" });
        return;
      }

      uni.showToast({ title: "保存成功", icon: "success" });
      setTimeout(() => {
        uni.navigateBack();
      }, 800);
    },
  },
};
</script>

<style lang="scss" scoped>
.add-contact-page {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx 32rpx calc(176rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;

  .tips-card {
    background: linear-gradient(135deg, #f0faf5 0%, #ffffff 100%);
    border-radius: 32rpx;
    padding: 32rpx;
    display: flex;
    align-items: center;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    margin-bottom: 28rpx;

    .tips-icon {
      width: 72rpx;
      height: 72rpx;
      border-radius: 50%;
      background-color: #07c160;
      color: #ffffff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 40rpx;
      font-weight: 800;
      margin-right: 24rpx;
      flex-shrink: 0;
    }

    .tips-content {
      display: flex;
      flex-direction: column;

      .tips-title {
        font-size: 32rpx;
        font-weight: 800;
        color: #1a202c;
      }

      .tips-desc {
        font-size: 24rpx;
        color: #718096;
        line-height: 1.5;
        margin-top: 8rpx;
      }
    }
  }

  .form-card {
    background-color: #ffffff;
    border-radius: 28rpx;
    padding: 0 32rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

    .form-item {
      height: 116rpx;
      display: flex;
      align-items: center;

      .label {
        width: 144rpx;
        font-size: 30rpx;
        color: #2d3748;
        font-weight: bold;
        flex-shrink: 0;
      }

      .input {
        flex: 1;
        height: 116rpx;
        font-size: 30rpx;
        color: #1a202c;
      }

      .placeholder {
        color: #b8c2cc;
        font-size: 28rpx;
      }
    }

    .relation-item {
      margin-bottom: 4rpx;
    }

    .relation-options {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;
      padding-left: 144rpx;

      .relation-chip {
        padding: 12rpx 24rpx;
        border-radius: 32rpx;
        border: 2rpx solid #edf2f7;
        background-color: #ffffff;
        color: #718096;
        font-size: 26rpx;

        &.active {
          border-color: #07c160;
          background-color: #f0faf5;
          color: #07c160;
          font-weight: bold;
        }
      }
    }
  }

  .footer-bar {
    position: fixed;
    left: 0;
    bottom: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
    padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
    box-sizing: border-box;
    z-index: 100;

    .submit-btn {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      border-radius: 48rpx;
      background-color: #a3e9c5;
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;

      &.active {
        background-color: #07c160;
        box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
      }

      &::after {
        border: none;
      }
    }
  }
}
</style>