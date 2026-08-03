<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="20"
    @close="handleClose"
    :safeAreaInsetBottom="true"
    @touchmove.stop.prevent
  >
    <view class="form-popup">
      <view class="form-title">{{ isEdit ? '编辑企业' : '新增企业' }}</view>

      <scroll-view class="form-body" scroll-y>
        <view class="form-item">
          <text class="form-label required">企业名称</text>
          <input
            class="form-input"
            v-model="form.name"
            placeholder="请输入企业名称"
            maxlength="50"
          />
        </view>

        <view class="form-item">
          <text class="form-label">企业 Logo</text>
          <oss-image-upload
            v-model="logoFiles"
            :maxCount="1"
            :minCount="0"
            uploadText="上传 Logo"
            ossPath="enterprise/logo"
          />
        </view>

        <view class="form-item">
          <text class="form-label required">企业简介</text>
          <textarea
            class="form-textarea"
            v-model="form.description"
            placeholder="请输入企业简介"
            maxlength="500"
          />
        </view>

        <view class="form-item">
          <text class="form-label required">联系人</text>
          <input
            class="form-input"
            v-model="form.contactName"
            placeholder="请输入联系人姓名"
            maxlength="20"
          />
        </view>

        <view class="form-item">
          <text class="form-label required">联系电话</text>
          <input
            class="form-input"
            v-model="form.contactPhone"
            placeholder="请输入联系电话"
            type="number"
            maxlength="11"
          />
        </view>

        <view class="form-item">
          <text class="form-label">企业地址</text>
          <input
            class="form-input"
            v-model="form.address"
            placeholder="请输入企业地址"
            maxlength="100"
          />
        </view>

        <view class="form-item">
          <text class="form-label">排序权重</text>
          <input
            class="form-input"
            v-model.number="form.sort"
            placeholder="数字越大越靠前（默认0）"
            type="number"
          />
        </view>
      </scroll-view>

      <view class="form-footer">
        <button class="btn-cancel" @click="handleClose">取消</button>
        <button class="btn-submit" @click="handleSubmit" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交' }}
        </button>
      </view>
    </view>
  </u-popup>
</template>

<script>
import { save4 } from '@/spages/api/company';
import OssImageUpload from '@/components/upload.vue';

export default {
  name: 'EnterpriseForm',
  components: { OssImageUpload },
  props: {
    show: { type: Boolean, default: false },
    /** 编辑时传入企业数据，新增则为 null */
    editData: { type: Object, default: null },
    /** 当前社区ID，数据隔离用 */
    communityId: { type: [Number, String], default: '' },
  },
  computed: {
    isEdit() {
      return this.editData && this.editData.enterpriseId;
    },
  },
  watch: {
    show(val) {
      if (val) {
        this.initForm();
      }
    },
  },
  data() {
    return {
      submitting: false,
      logoFiles: [],
      form: {
        enterpriseId: '',
        name: '',
        logo: '',
        description: '',
        contactName: '',
        contactPhone: '',
        address: '',
        sort: 0,
        communityId: '',
      },
    };
  },
  methods: {
    initForm() {
      this.logoFiles = [];
      if (this.editData && this.editData.enterpriseId) {
        this.form = {
          enterpriseId: this.editData.enterpriseId || '',
          name: this.editData.name || '',
          logo: this.editData.logo || '',
          description: this.editData.description || '',
          contactName: this.editData.contactName || '',
          contactPhone: this.editData.contactPhone || '',
          address: this.editData.address || '',
          sort: this.editData.sort || 0,
          communityId: String(this.editData.communityId || this.communityId || ''),
        };
        if (this.editData.logo) {
          this.logoFiles = [{ url: this.editData.logo }];
        }
      } else {
        this.form = {
          enterpriseId: '',
          name: '',
          logo: '',
          description: '',
          contactName: '',
          contactPhone: '',
          address: '',
          sort: 0,
          communityId: String(this.communityId || ''),
        };
      }
    },

    validate() {
      if (!this.form.name.trim()) {
        uni.showToast({ title: '请输入企业名称', icon: 'none' });
        return false;
      }
      if (!this.form.description.trim()) {
        uni.showToast({ title: '请输入企业简介', icon: 'none' });
        return false;
      }
      if (!this.form.contactName.trim()) {
        uni.showToast({ title: '请输入联系人', icon: 'none' });
        return false;
      }
      if (!this.form.contactPhone.trim()) {
        uni.showToast({ title: '请输入联系电话', icon: 'none' });
        return false;
      }
      return true;
    },

    async handleSubmit() {
      if (!this.validate()) return;
      this.submitting = true;
      try {
        // 取上传组件第一张图的 url 作为 logo
        const logoUrl =
          this.logoFiles.length > 0 ? this.logoFiles[0].url : '';
        const payload = {
          ...this.form,
          logo: logoUrl,
        };
        if (!payload.enterpriseId) {
          delete payload.enterpriseId;
        }
        const res = await save4(payload);
        if (res.code === '00000') {
          uni.showToast({
            title: this.isEdit ? '编辑成功' : '新增成功',
            icon: 'none',
          });
          this.$emit('done');
          this.handleClose();
        } else {
          uni.showToast({ title: res.msg || '操作失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '操作失败', icon: 'none' });
      } finally {
        this.submitting = false;
      }
    },

    handleClose() {
      this.$emit('update:show', false);
    },
  },
};
</script>

<style lang="scss" scoped>
.form-popup {
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  display: flex;
  flex-direction: column;
  max-height: 85vh;
  overflow: hidden;

  .form-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    padding: 36rpx 0 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
    flex-shrink: 0;
  }

  .form-body {
    flex: 1;
    overflow-y: auto;
    padding: 24rpx 40rpx 0;
    max-height: 60vh;

    .form-item {
      margin-bottom: 24rpx;

      .form-label {
        font-size: 26rpx;
        color: #333;
        margin-bottom: 10rpx;
        display: block;

        &.required::after {
          content: ' *';
          color: #ef4444;
        }
      }

      .form-input {
        width: 100%;
        height: 72rpx;
        background: #f7f8fa;
        border-radius: 12rpx;
        padding: 0 20rpx;
        font-size: 26rpx;
        box-sizing: border-box;
      }

      .form-textarea {
        width: 100%;
        min-height: 140rpx;
        background: #f7f8fa;
        border-radius: 12rpx;
        padding: 16rpx 20rpx;
        font-size: 26rpx;
        box-sizing: border-box;
      }
    }
  }

  .form-footer {
    display: flex;
    gap: 20rpx;
    padding: 28rpx 40rpx calc(28rpx + env(safe-area-inset-bottom));

    button {
      flex: 1;
      height: 80rpx;
      line-height: 80rpx;
      font-size: 28rpx;
      font-weight: bold;
      border-radius: 40rpx;
      border: none;
      padding: 0;

      &::after {
        border: none;
      }
    }

    .btn-cancel {
      background: #f0f0f0;
      color: #666;
    }

    .btn-submit {
      background: #07c160;
      color: #fff;

      &[disabled] {
        opacity: 0.6;
      }
    }
  }
}
</style>
