<template>
  <view class="order-detail-container">
    <scroll-view scroll-y class="detail-scroll">
      <!-- 1. 订单状态卡片 -->
      <view class="status-card" :class="statusCardClass">
        <text class="status-title">{{ statusText }}</text>
        <text class="status-sub">{{ statusSubText }}</text>
      </view>

      <!-- 2. 商品信息卡片 -->
      <view class="info-card">
        <view class="card-title-row">
          <text class="card-section-title">商品信息</text>
        </view>
        <view class="goods-info-row">
          <image class="goods-img" :src="order.goodsImage" mode="aspectFill"></image>
          <view class="goods-right">
            <text class="goods-title text-ellipsis-2">{{ order.goodsTitle }}</text>
            <view class="goods-price-row">
              <text class="goods-price">{{ priceText }}</text>
              <text class="goods-count">x{{ order.count || 1 }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 3. 订单信息卡片 -->
      <view class="info-card">
        <view class="card-title-row">
          <text class="card-section-title">订单信息</text>
        </view>
        <view class="info-row">
          <text class="info-label">订单编号</text>
          <text class="info-value">{{ order.orderNum }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">支付方式</text>
          <text class="info-value">{{ order.payType === 1 ? '积分兑换' : '现金购买' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">积分总额</text>
          <text class="info-value highlight">{{ order.totalPoints || 0 }} 积分</text>
        </view>
        <view class="info-row" v-if="order.totalAmount">
          <text class="info-label">现金总额</text>
          <text class="info-value highlight">¥{{ (order.totalAmount || 0).toFixed(2) }}</text>
        </view>
        <view class="info-row" v-if="order.redeemCode">
          <text class="info-label">核销码</text>
          <text class="info-value code-text">{{ order.redeemCode }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">下单时间</text>
          <text class="info-value">{{ formatTime(order.createTime) }}</text>
        </view>
        <view class="info-row" v-if="order.payTime">
          <text class="info-label">支付时间</text>
          <text class="info-value">{{ formatTime(order.payTime) }}</text>
        </view>
        <view class="info-row" v-if="order.verifyTime">
          <text class="info-label">核销时间</text>
          <text class="info-value">{{ formatTime(order.verifyTime) }}</text>
        </view>
        <view class="info-row" v-if="order.refundTime">
          <text class="info-label">退款时间</text>
          <text class="info-value">{{ formatTime(order.refundTime) }}</text>
        </view>
        <view class="info-row" v-if="order.refundReason">
          <text class="info-label">退款原因</text>
          <text class="info-value">{{ order.refundReason }}</text>
        </view>
      </view>

      <!-- 4. 暂无数据兜底 -->
      <view v-if="!order.orderId && !loading" class="empty-state">
        <text class="empty-text">订单信息加载失败</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { detail1 } from "@/spages/api/order";

const STATUS_MAP = {
  10: { text: "待支付", cls: "status-pending" },
  20: { text: "待领取", cls: "status-pending" },
  30: { text: "已完成", cls: "status-completed" },
  40: { text: "退款中", cls: "status-refund" },
  41: { text: "退款中", cls: "status-refund" },
  42: { text: "退款中", cls: "status-refund" },
  50: { text: "已退款", cls: "status-refund" },
};

export default {
  data() {
    return {
      orderId: null,
      loading: false,
      order: {},
    };
  },
  computed: {
    statusInfo() {
      return STATUS_MAP[this.order.status] || { text: "未知", cls: "" };
    },
    statusText() {
      return this.statusInfo.text;
    },
    statusCardClass() {
      return this.statusInfo.cls;
    },
    statusSubText() {
      const s = this.order.status;
      if (s === 10) return "请尽快完成支付";
      if (s === 20) return "请联系社区管理员出示核销码";
      if (s === 30) return "交易已完成，感谢您的参与";
      if (s === 40 || s === 41 || s === 42) return "退款处理中，请耐心等待";
      if (s === 50) return "退款已完成";
      return "";
    },
    priceText() {
      if (this.order.payType === 1) {
        return `${this.order.totalPoints || 0} 积分`;
      }
      return `¥${(this.order.totalAmount || 0).toFixed(2)}`;
    },
  },
  onLoad(options) {
    this.orderId = options.id;
    if (this.orderId) {
      this.fetchDetail();
    }
  },
  methods: {
    async fetchDetail() {
      this.loading = true;
      try {
        const res = await detail1(this.orderId);
        this.order = res.data || {};
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },
    formatTime(str) {
      if (!str) return "";
      const d = new Date(str.replace(/-/g, "/"));
      if (isNaN(d.getTime())) return str;
      const pad = (n) => String(n).padStart(2, "0");
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.order-detail-container {
  min-height: 100vh;
  background-color: #f7f9fb;

  .detail-scroll {
    padding: 32rpx;
  }

  .status-card {
    background: linear-gradient(135deg, #e8f9f0, #d1fae5);
    border-radius: 24rpx;
    padding: 48rpx 32rpx;
    margin-bottom: 28rpx;
    text-align: center;

    &.status-pending {
      background: linear-gradient(135deg, #fef3c7, #fde68a);
    }
    &.status-completed {
      background: linear-gradient(135deg, #e8f9f0, #d1fae5);
    }
    &.status-refund {
      background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
    }

    .status-title {
      font-size: 40rpx;
      font-weight: 800;
      color: #1a202c;
      display: block;
      margin-bottom: 12rpx;
    }

    .status-sub {
      font-size: 26rpx;
      color: #64748b;
    }
  }

  .info-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    margin-bottom: 28rpx;

    .card-title-row {
      margin-bottom: 28rpx;

      .card-section-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #1a202c;
      }
    }

    .goods-info-row {
      display: flex;
      gap: 24rpx;

      .goods-img {
        width: 160rpx;
        height: 160rpx;
        border-radius: 16rpx;
        background-color: #f1f5f9;
      }

      .goods-right {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .goods-title {
          font-size: 28rpx;
          font-weight: bold;
          color: #1e293b;
          line-height: 1.4;
        }

        .goods-price-row {
          display: flex;
          justify-content: space-between;
          align-items: baseline;

          .goods-price {
            font-size: 32rpx;
            font-weight: bold;
            color: #d97706;
          }

          .goods-count {
            font-size: 26rpx;
            color: #94a3b8;
          }
        }
      }
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 22rpx 0;
      border-bottom: 2rpx solid #f5f7fa;

      &:last-child {
        border-bottom: none;
      }

      .info-label {
        font-size: 27rpx;
        color: #718096;
      }

      .info-value {
        font-size: 27rpx;
        color: #1e293b;

        &.highlight {
          color: #d97706;
          font-weight: bold;
        }

        &.code-text {
          font-family: monospace;
          font-size: 32rpx;
          font-weight: bold;
          color: #07c160;
          letter-spacing: 4rpx;
        }
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 120rpx 0;

    .empty-text {
      font-size: 28rpx;
      color: #94a3b8;
    }
  }

  .text-ellipsis-2 {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }
}
</style>
