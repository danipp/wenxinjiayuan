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
      <view class="form-title">提交捐赠申请</view>

      <scroll-view class="form-body" scroll-y>
        <!-- 申请者类型 -->
        <view class="form-item">
          <text class="form-label required">申请者类型</text>
          <view class="radio-row">
            <view
              v-for="opt in userTypeOptions"
              :key="opt.value"
              class="radio-item"
              :class="{ active: form.userType === opt.value }"
              @click="form.userType = opt.value"
            >
              {{ opt.label }}
            </view>
          </view>
        </view>

        <!-- 捐赠类型 -->
        <view class="form-item">
          <text class="form-label required">捐赠类型</text>
          <view class="radio-row">
            <view
              v-for="opt in donationTypeOptions"
              :key="opt.value"
              class="radio-item"
              :class="{ active: form.donationType === opt.value }"
              @click="form.donationType = opt.value"
            >
              {{ opt.label }}
            </view>
          </view>
        </view>

        <!-- 资金捐赠 -->
        <template v-if="form.donationType === 'money'">
          <view class="form-item">
            <text class="form-label required">捐赠金额 (¥)</text>
            <input
              class="form-input"
              v-model.number="form.amount"
              placeholder="请输入捐赠金额"
              type="digit"
            />
          </view>
        </template>

        <!-- 物资捐赠 -->
        <template v-if="form.donationType === 'goods'">
          <view class="form-item">
            <text class="form-label required">物资名称</text>
            <input
              class="form-input"
              v-model="form.goodsName"
              placeholder="请输入物资名称"
              maxlength="50"
            />
          </view>
          <view class="form-item">
            <text class="form-label">物资数量</text>
            <input
              class="form-input"
              v-model.number="form.goodsQuantity"
              placeholder="请输入物资数量"
              type="number"
            />
          </view>
          <view class="form-item">
            <text class="form-label">物资估值 (¥)</text>
            <input
              class="form-input"
              v-model.number="form.goodsValue"
              placeholder="请输入物资估值"
              type="digit"
            />
          </view>
        </template>

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
          <text class="form-label">备注说明</text>
          <textarea
            class="form-textarea"
            v-model="form.remark"
            placeholder="选填，补充说明"
            maxlength="300"
          />
        </view>
      </scroll-view>

      <view class="form-footer">
        <button class="btn-cancel" @click="handleClose">取消</button>
        <button class="btn-submit" @click="handleSubmit" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交申请' }}
        </button>
      </view>
    </view>
  </u-popup>
</template>

<script>
import { submit1 } from '@/spages/api/donation';

export default {
  name: 'DonationForm',
  props: {
    show: { type: Boolean, default: false },
    communityId: { type: [Number, String], default: '' },
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
      userTypeOptions: [
        { label: '个人', value: 'individual' },
        { label: '企业', value: 'enterprise' },
      ],
      donationTypeOptions: [
        { label: '资金', value: 'money' },
        { label: '物资', value: 'goods' },
      ],
      form: {
        userType: 'individual',
        donationType: 'money',
        amount: '',
        goodsName: '',
        goodsQuantity: '',
        goodsValue: '',
        contactName: '',
        contactPhone: '',
        remark: '',
        communityId: '',
      },
    };
  },
  methods: {
    initForm() {
      this.form = {
        userType: 'individual',
        donationType: 'money',
        amount: '',
        goodsName: '',
        goodsQuantity: '',
        goodsValue: '',
        contactName: '',
        contactPhone: '',
        remark: '',
        communityId: String(this.communityId || ''),
      };
    },

    validate() {
      if (!this.form.userType) {
        uni.showToast({ title: '请选择申请者类型', icon: 'none' });
        return false;
      }
      if (!this.form.donationType) {
        uni.showToast({ title: '请选择捐赠类型', icon: 'none' });
        return false;
      }
      if (this.form.donationType === 'money') {
        if (!this.form.amount || this.form.amount <= 0) {
          uni.showToast({ title: '请输入有效的捐赠金额', icon: 'none' });
          return false;
        }
      }
      if (this.form.donationType === 'goods') {
        if (!this.form.goodsName.trim()) {
          uni.showToast({ title: '请输入物资名称', icon: 'none' });
          return false;
        }
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
        const payload = { ...this.form };
        if (payload.donationType === 'money') {
          delete payload.goodsName;
          delete payload.goodsQuantity;
          delete payload.goodsValue;
        } else {
          delete payload.amount;
        }
        const res = await submit1(payload);
        if (res.code === '00000') {
          uni.showToast({ title: '提交成功', icon: 'none' });
          this.$emit('done');
          this.handleClose();
        } else {
          uni.showToast({ title: res.msg || '提交失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '提交失败', icon: 'none' });
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
        min-height: 120rpx;
        background: #f7f8fa;
        border-radius: 12rpx;
        padding: 16rpx 20rpx;
        font-size: 26rpx;
        box-sizing: border-box;
      }

      .radio-row {
        display: flex;
        gap: 20rpx;

        .radio-item {
          flex: 1;
          height: 64rpx;
          line-height: 64rpx;
          text-align: center;
          background: #f7f8fa;
          border-radius: 12rpx;
          font-size: 26rpx;
          color: #666;
          border: 2rpx solid transparent;

          &.active {
            background: #e6f7ed;
            color: #07c160;
            border-color: #07c160;
            font-weight: bold;
          }
        }
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
