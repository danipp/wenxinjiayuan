<template>
  <view class="donation-card" @click="$emit('click')">
    <view class="card-header">
      <view class="donor-info">
        <text class="donor-name">{{ donation.contactName || "匿名" }}</text>
        <text class="donor-type">{{ userTypeLabel }}</text>
      </view>
      <view class="status-tag" :class="statusClass">{{ statusLabel }}</view>
    </view>

    <view class="card-body">
      <!-- 资金捐赠 -->
      <view v-if="donation.donationType === 'money'" class="donation-amount">
        <text class="amount-value">¥{{ donation.amount || 0 }}</text>
        <text class="amount-label">捐赠金额</text>
      </view>
      <!-- 物资捐赠 -->
      <view v-else class="donation-goods">
        <text class="goods-name">{{ donation.goodsName || "--" }}</text>
        <view class="goods-meta">
          <text class="meta-item">数量: {{ donation.goodsQuantity || 0 }}</text>
          <text class="meta-item">估值: ¥{{ donation.goodsValue || 0 }}</text>
        </view>
      </view>
    </view>

    <view class="card-footer">
      <view class="footer-left">
        <text class="footer-meta" v-if="donation.createTime">
          {{ formatTime(donation.createTime) }}
        </text>
      </view>
      <view class="footer-right" v-if="donation.remark">
        <text class="remark-label">备注：{{ donation.remark }}</text>
      </view>
    </view>
    <view class="card-footer" v-if="donation.auditRemark">
      <text class="audit-remark">审核：{{ donation.auditRemark }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: "DonationCard",
  props: {
    donation: { type: Object, default: () => ({}) },
  },
  computed: {
    statusLabel() {
      const map = {
        pending: "待审核",
        approved: "已通过",
        rejected: "已驳回",
      };
      return map[this.donation.status] || this.donation.status || "--";
    },
    statusClass() {
      const map = {
        pending: "status-pending",
        approved: "status-approved",
        rejected: "status-rejected",
      };
      return map[this.donation.status] || "";
    },
    userTypeLabel() {
      return this.donation.userType === "enterprise" ? "企业" : "个人";
    },
  },
  methods: {
    formatTime(str) {
      if (!str) return "";
      const d = new Date(str.replace(/-/g, "/"));
      if (isNaN(d.getTime())) return str;
      const pad = (n) => String(n).padStart(2, "0");
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(
        d.getDate()
      )} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.donation-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.03);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 18rpx;

    .donor-info {
      display: flex;
      align-items: center;
      gap: 12rpx;

      .donor-name {
        font-size: 30rpx;
        font-weight: bold;
        color: #1a202c;
      }
      .donor-type {
        font-size: 20rpx;
        color: #fff;
        background: #8b5cf6;
        padding: 2rpx 14rpx;
        border-radius: 6rpx;
      }
    }

    .status-tag {
      font-size: 22rpx;
      padding: 4rpx 16rpx;
      border-radius: 8rpx;
      font-weight: bold;

      &.status-pending {
        background: #fef3e2;
        color: #f59e0b;
      }
      &.status-approved {
        background: #e6f7ed;
        color: #10b981;
      }
      &.status-rejected {
        background: #fde8e8;
        color: #ef4444;
      }
    }
  }

  .card-body {
    margin-bottom: 16rpx;

    .donation-amount {
      display: flex;
      align-items: baseline;
      gap: 12rpx;

      .amount-value {
        font-size: 44rpx;
        font-weight: 900;
        color: #f59e0b;
      }
      .amount-label {
        font-size: 24rpx;
        color: #999;
      }
    }

    .donation-goods {
      .goods-name {
        font-size: 30rpx;
        font-weight: bold;
        color: #1a202c;
        display: block;
        margin-bottom: 8rpx;
      }
      .goods-meta {
        display: flex;
        gap: 24rpx;
        font-size: 24rpx;
        color: #718096;
      }
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 22rpx;
    color: #a0aec0;
    padding-top: 14rpx;
    border-top: 1rpx solid #f5f5f5;

    .footer-left {
      display: flex;
      gap: 20rpx;
    }

    .remark-label {
      max-width: 300rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .audit-remark {
      color: #718096;
    }
  }
}
</style>
