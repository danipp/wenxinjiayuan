<template>
  <view class="edit-info-container">
    <view class="form-card">
      <!-- 1. 店铺Logo -->
      <view class="form-row logo-row">
        <text class="label">店铺Logo</text>
        <OssImageUpload
          v-model="logoList"
          :maxCount="1"
          :minCount="0"
          :showText="false"
          uploadText="点击上传"
          ossPath="shopLogo"
        />
      </view>
      <u-line color="#f1f5f9"></u-line>

      <!-- 2. 店铺名称 -->
      <view class="form-row">
        <text class="label">店铺名称</text>
        <input
          type="text"
          v-model="form.name"
          placeholder="请输入店铺名称"
          placeholder-class="placeholder-style"
          class="val-input"
        />
      </view>
      <u-line color="#f1f5f9"></u-line>

      <!-- 3. 手机号码 -->
      <view class="form-row">
        <text class="label">手机号码</text>
        <input
          type="number"
          v-model="form.phone"
          placeholder="请输入联系手机号"
          placeholder-class="placeholder-style"
          class="val-input"
          maxlength="11"
        />
      </view>
      <u-line color="#f1f5f9"></u-line>

      <!-- 4. 地址 (地图选点) -->
      <view class="form-row" @click="chooseLocation">
        <text class="label">店铺地址</text>
        <view class="right-box text-ellipsis-wrapper">
          <text class="value" :class="{ 'placeholder-style': !form.address }">
            {{ form.address || "选择地图位置" }}
          </text>
          <u-icon
            name="arrow-right"
            color="#cbd5e1"
            size="28rpx"
            style="margin-left: 12rpx"
          ></u-icon>
        </view>
      </view>
      <u-line color="#f1f5f9"></u-line>
      <view class="form-row">
        <text class="label">详细地址</text>
        <input
          type="text"
          v-model="form.detailAddress"
          placeholder="请输入详细地址：如：1号楼101室"
          placeholder-class="placeholder-style"
          class="val-input"
        />
      </view>
      <u-line color="#f1f5f9"></u-line>
    </view>

    <!-- 6. 店铺简介 -->
    <view class="textarea-card">
      <text class="textarea-title">店铺简介</text>
      <textarea
        v-model="form.intro"
        placeholder="请输入简短的店铺介绍（最多150字），展示在店铺主页"
        maxlength="150"
        class="desc-textarea"
        placeholder-class="placeholder-style"
      ></textarea>
      <view class="char-count">{{ form.intro.length }}/150</view>
    </view>

    <!-- 底部固定保存修改按钮 -->
    <view class="footer-bar">
      <button
        class="save-btn"
        :class="{ 'save-btn-active': isFormValid }"
        @click="handleSave"
      >
        保存修改
      </button>
    </view>
  </view>
</template>

<script>
import OssImageUpload from "@/components/upload.vue";
import { myShop, save } from "@/spages/api/store";

export default {
  components: {
    OssImageUpload,
  },
  data() {
    return {
      shopId: null,
      defaultLogo: "https://cdn.uviewui.com/uview/album/1.jpg",
      logoList: [],
      form: {
        logo: "",
        name: "",
        phone: "",
        address: "",
        detailAddress: "",
        qrCode: "",
        intro: "",
      },
    };
  },
  computed: {
    // 名称、电话、地址为必填
    isFormValid() {
      return this.form.name && this.form.phone && this.form.address;
    },
  },
  onLoad() {
    this.loadShopData();
  },
  methods: {
    // 加载店铺数据回填
    async loadShopData() {
      try {
        const res = await myShop();
        const shop = res.data || {};
        if (shop && shop.shopId) {
          this.shopId = shop.shopId;
          this.form.logo = shop.logo || "";
          this.form.name = shop.name || "";
          this.form.phone = shop.phone || "";
          this.form.address = shop.address || "";
          this.form.intro = shop.description || "";
          if (shop.logo) {
            this.logoList = [{ url: shop.logo }];
          }
        }
      } catch (e) {
        uni.showToast({ title: "加载店铺信息失败", icon: "none" });
      }
    },
    chooseLocation() {
      uni.chooseLocation({
        success: (res) => {
          this.form.address = res.address + " (" + res.name + ")";
        },
      });
    },
    async handleSave() {
      if (!this.isFormValid) {
        uni.showToast({ title: "请填写店铺名称、电话和地址", icon: "none" });
        return;
      }
      uni.showLoading({ title: "资料保存中..." });
      try {
        const data = {
          name: this.form.name,
          logo:
            this.logoList.length > 0 ? this.logoList[0].url : this.form.logo,
          phone: this.form.phone,
          address: this.form.address,
          description: this.form.intro,
          detailAddress: this.form.detailAddress,
        };
        // 编辑时传 shopId
        if (this.shopId) {
          data.shopId = this.shopId;
        }
        await save(data);
        uni.hideLoading();
        uni.showToast({ title: "店铺资料更新成功！", icon: "success" });
        setTimeout(() => uni.navigateBack(), 1200);
      } catch (e) {
        uni.hideLoading();
        uni.showToast({ title: "保存失败，请重试", icon: "none" });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.edit-info-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx 32rpx calc(180rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 32rpx;

  .form-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 8rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
  }

  .form-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 32rpx 0;

    .label {
      font-size: 30rpx;
      font-weight: bold;
      color: #333333;
      white-space: nowrap;
    }

    .val-input {
      text-align: right;
      font-size: 28rpx;
      color: #333333;
      flex: 1;
      padding-left: 40rpx;
    }

    .right-box {
      display: flex;
      align-items: center;

      .avatar-preview {
        width: 88rpx;
        height: 88rpx;
        border-radius: 50%;
        background-color: #f1f5f9;
      }

      .qr-thumbnail {
        width: 264rpx;
        height: 264rpx;
        border-radius: 8rpx;
        background-color: #f1f5f9;
      }

      .upload-tips {
        font-size: 26rpx;
        color: #94a3b8;
      }

      .value {
        font-size: 28rpx;
        color: #333333;
        font-weight: bold;
      }
    }

    .text-ellipsis-wrapper {
      flex: 1;
      justify-content: flex-end;
      padding-left: 40rpx;
    }

    .logo-row {
      align-items: flex-start;
      :v-deep .image-upload-wrapper {
        padding: 0;
      }
    }
  }

  /* 简介大文本域 */
  .textarea-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    position: relative;

    .textarea-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 24rpx;
      display: block;
    }

    .desc-textarea {
      width: 100%;
      height: 200rpx;
      font-size: 28rpx;
      color: #555555;
      line-height: 1.5;
    }

    .char-count {
      position: absolute;
      right: 32rpx;
      bottom: 24rpx;
      font-size: 22rpx;
      color: #cbd5e1;
    }
  }

  .placeholder-style {
    color: #cbd5e1 !important;
  }

  /* 底部保存按钮 */
  .footer-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
    padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
    box-sizing: border-box;
    z-index: 100;

    .save-btn {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      background-color: #a3e9c5;
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
      border-radius: 48rpx;

      &::after {
        border: none;
      }

      &.save-btn-active {
        background-color: #07c160;
        box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
      }
    }
  }
}
</style>