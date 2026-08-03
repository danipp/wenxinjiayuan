<template>
  <u-popup
    :show="show"
    mode="center"
    round="20"
    @close="handleClose"
    @touchmove.stop.prevent
  >
    <view class="audit-popup">
      <view class="audit-title">审核捐赠</view>

      <view class="audit-info">
        <text class="info-line">捐赠人：{{ donation.contactName || '--' }}</text>
        <text class="info-line" v-if="donation.donationType === 'money'">
          金额：¥{{ donation.amount || 0 }}
        </text>
        <text class="info-line" v-else>
          物资：{{ donation.goodsName }} × {{ donation.goodsQuantity || 0 }}
        </text>
      </view>

      <view class="audit-form">
        <text class="form-label">审核备注</text>
        <textarea
          class="audit-textarea"
          v-model="auditRemark"
          placeholder="请输入审核备注（选填）"
          maxlength="200"
        />
      </view>

      <view class="audit-footer">
        <button class="btn-reject" @click="handleAudit(false)" :disabled="loading">
          {{ loading ? '处理中...' : '驳回' }}
        </button>
        <button class="btn-approve" @click="handleAudit(true)" :disabled="loading">
          {{ loading ? '处理中...' : '通过' }}
        </button>
      </view>
    </view>
  </u-popup>
</template>

<script>
import { audit1 } from '@/spages/api/donation';

export default {
  name: 'AuditPopup',
  props: {
    show: { type: Boolean, default: false },
    donation: { type: Object, default: () => ({}) },
  },
  watch: {
    show(val) {
      if (val) {
        this.auditRemark = '';
      }
    },
  },
  data() {
    return {
      auditRemark: '',
      loading: false,
    };
  },
  methods: {
    async handleAudit(approved) {
      this.loading = true;
      try {
        const res = await audit1({
          donationId: this.donation.donationId,
          approved,
          auditRemark: this.auditRemark,
        });
        if (res.code === '00000') {
          uni.showToast({
            title: approved ? '审核通过' : '已驳回',
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
        this.loading = false;
      }
    },

    handleClose() {
      this.$emit('update:show', false);
    },
  },
};
</script>

<style lang="scss" scoped>
.audit-popup {
  width: 580rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx 36rpx 32rpx;

  .audit-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    margin-bottom: 28rpx;
  }

  .audit-info {
    background: #f7f8fa;
    border-radius: 12rpx;
    padding: 20rpx 24rpx;
    margin-bottom: 24rpx;

    .info-line {
      font-size: 26rpx;
      color: #333;
      display: block;
      margin-bottom: 8rpx;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  .audit-form {
    margin-bottom: 28rpx;

    .form-label {
      font-size: 26rpx;
      color: #333;
      margin-bottom: 10rpx;
      display: block;
    }

    .audit-textarea {
      width: 100%;
      min-height: 120rpx;
      background: #f7f8fa;
      border-radius: 12rpx;
      padding: 16rpx 20rpx;
      font-size: 26rpx;
      box-sizing: border-box;
    }
  }

  .audit-footer {
    display: flex;
    gap: 20rpx;

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

      &[disabled] {
        opacity: 0.6;
      }
    }

    .btn-reject {
      background: #fde8e8;
      color: #ef4444;
    }

    .btn-approve {
      background: #07c160;
      color: #fff;
    }
  }
}
</style>
