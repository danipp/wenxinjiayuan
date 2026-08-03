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
          <text class="info-value refund-reason">{{ order.refundReason }}</text>
        </view>
        <view class="info-row" v-if="order.auditRemark">
          <text class="info-label">审核备注</text>
          <text class="info-value">{{ order.auditRemark }}</text>
        </view>
      </view>

      <!-- 4. 暂无数据兜底 -->
      <view v-if="!order.orderId && !loading" class="empty-state">
        <text class="empty-text">订单信息加载失败</text>
      </view>
    </scroll-view>

    <!-- 5. 底部操作栏：买家端 -->
    <view class="footer-bar" v-if="role === 'buyer'">
      <!-- 待支付 -->
      <template v-if="order.status === 10">
        <button class="footer-btn btn-cancel" @click="handleCancel">取消订单</button>
        <button class="footer-btn btn-primary" @click="handlePay">去支付</button>
      </template>

      <!-- 待领取 -->
      <template v-else-if="order.status === 20">
        <button class="footer-btn btn-outline" @click="handleShowCode">查看兑换码</button>
        <button class="footer-btn btn-danger" @click="handleRefund">申请退款</button>
      </template>

      <!-- 已完成未评价 -->
      <button
        v-else-if="order.status === 30 && !order.commentId"
        class="footer-btn btn-primary"
        @click="goWriteReview"
      >去评价</button>
    </view>

    <!-- 6. 底部操作栏：卖家端 -->
    <view class="footer-bar" v-if="role === 'seller'">
      <!-- 待核销 -->
      <button
        v-if="order.status === 20"
        class="footer-btn btn-primary"
        @click="handleVerify"
      >核销订单</button>

      <!-- 退款申请 -->
      <template v-if="order.status === 40 || order.status === 41 || order.status === 42">
        <button class="footer-btn btn-primary" @click="handleApproveRefund">同意退款</button>
        <button class="footer-btn btn-deny" @click="handleRejectRefund">拒绝退款</button>
      </template>
    </view>

    <!-- 7. 兑换码弹窗 -->
    <u-popup
      :show="codePopupShow"
      mode="center"
      round="16"
      @close="codePopupShow = false"
    >
      <view class="code-popup-panel">
        <text class="popup-title">请出示兑换码给社区管理员</text>
        <text class="barcode-num">{{ order.redeemCode || activeCode }}</text>
        <text class="popup-tips">核销成功后即可领取对应商品，严防截图外泄</text>
        <button class="popup-close-btn" @click="codePopupShow = false">确认</button>
      </view>
    </u-popup>

    <!-- 8. 退款申请底部弹窗 -->
    <u-popup
      :show="refundPopupShow"
      mode="bottom"
      round="16"
      @close="refundPopupShow = false"
      :safeAreaInsetBottom="true"
      @touchmove.stop.prevent
    >
      <view class="refund-popup">
        <view class="refund-title">申请退款</view>
        <view class="refund-goods-info">
          <text class="refund-goods-title">{{ order.goodsTitle || '' }}</text>
          <text class="refund-goods-price">{{ priceText }}</text>
        </view>
        <view class="refund-form">
          <text class="refund-label">退款原因</text>
          <textarea
            class="refund-textarea"
            v-model="refundReason"
            placeholder="请填写退款原因（选填）"
            maxlength="300"
          />
        </view>
        <view class="refund-footer">
          <button class="refund-btn-cancel" @click="refundPopupShow = false">取消</button>
          <button class="refund-btn-submit" @click="doRefund">提交申请</button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import {
  detail1,
  cancel,
  refund,
  redeemCode as getRedeemCode,
  verify,
  approveRefund,
  rejectRefund,
} from "@/spages/api/order";

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
      role: "buyer", // 'buyer' | 'seller'
      codePopupShow: false,
      activeCode: "",
      refundPopupShow: false,
      refundReason: "",
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
    this.role = options.role || "buyer";
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

    // ---- 买家操作 ----

    // 取消订单
    async handleCancel() {
      uni.showModal({
        title: "取消订单",
        content: "确定要取消该订单吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await cancel(this.orderId);
              uni.showToast({ title: "已取消", icon: "success" });
              this.fetchDetail();
            } catch (e) {
              uni.showToast({ title: "取消失败", icon: "none" });
            }
          }
        },
      });
    },

    // 去支付
    handlePay() {
      let payParams = null;
      try {
        payParams = this.order.payParamsJson
          ? JSON.parse(this.order.payParamsJson)
          : null;
      } catch (e) { /* ignore */ }
      if (payParams) {
        uni.requestPayment({
          ...payParams,
          success: () => {
            uni.showToast({ title: "支付成功", icon: "success" });
            this.fetchDetail();
          },
          fail: () => {
            uni.showToast({ title: "支付取消", icon: "none" });
          },
        });
      }
    },

    // 查看兑换码
    async handleShowCode() {
      try {
        const res = await getRedeemCode(this.orderId);
        this.activeCode = res.data || this.order.redeemCode || "";
      } catch (e) {
        this.activeCode = this.order.redeemCode || "";
      }
      this.codePopupShow = true;
    },

    // 申请退款
    handleRefund() {
      this.refundReason = "";
      this.refundPopupShow = true;
    },
    async doRefund() {
      this.refundPopupShow = false;
      try {
        await refund({ orderId: this.orderId, reason: this.refundReason.trim() });
        uni.showToast({ title: "退款申请已提交", icon: "success" });
        this.fetchDetail();
      } catch (e) {
        uni.showToast({ title: "申请失败，请重试", icon: "none" });
      }
    },

    // 去评价
    goWriteReview() {
      const title = encodeURIComponent(this.order.goodsTitle || "");
      uni.navigateTo({
        url: `/spages/store/order/comments?id=${this.orderId}&communityName=${title}`,
      });
    },

    // ---- 卖家操作 ----

    // 核销订单
    async handleVerify() {
      uni.showModal({
        title: "核销商品验证",
        placeholderText: "请输入买家的核销码",
        editable: true,
        success: async (res) => {
          if (res.confirm) {
            const code = (res.content || "").replace(/\s+/g, "");
            try {
              await verify({ orderId: this.orderId, redeemCode: code });
              uni.showToast({ title: "核销成功！已出库", icon: "success" });
              this.fetchDetail();
            } catch (e) {
              uni.showToast({ title: "核销失败，请检查核销码", icon: "none" });
            }
          }
        },
      });
    },

    // 同意退款
    async handleApproveRefund() {
      uni.showModal({
        title: "确认退款",
        content: "同意后将自动退款给买家，确定吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await approveRefund(this.orderId);
              uni.showToast({ title: "已同意退款", icon: "success" });
              this.fetchDetail();
            } catch (e) {
              uni.showToast({ title: "操作失败", icon: "none" });
            }
          }
        },
      });
    },

    // 拒绝退款
    async handleRejectRefund() {
      uni.showModal({
        title: "拒绝退款",
        content: "拒绝后将恢复订单为待核销状态，确定吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await rejectRefund(this.orderId);
              uni.showToast({ title: "已拒绝退款", icon: "success" });
              this.fetchDetail();
            } catch (e) {
              uni.showToast({ title: "操作失败", icon: "none" });
            }
          }
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.order-detail-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));

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

        &.refund-reason {
          max-width: 55%;
          text-align: right;
          word-break: break-all;
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

/* ------ 底部操作栏 ------ */
.footer-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
  box-sizing: border-box;
  display: flex;
  align-items: center;
  gap: 20rpx;
  z-index: 100;

  .footer-btn {
    flex: 1;
    height: 88rpx;
    line-height: 88rpx;
    font-size: 28rpx;
    font-weight: bold;
    border-radius: 44rpx;
    border: none;
    padding: 0;

    &::after { border: none; }
    &:active { opacity: 0.9; }
  }

  .btn-primary {
    background: #07c160;
    color: #fff;
  }

  .btn-outline {
    background: #fff;
    color: #07c160;
    border: 2rpx solid #07c160;
  }

  .btn-cancel {
    background: #f5f7fa;
    color: #666;
  }

  .btn-danger {
    background: #ef4444;
    color: #fff;
  }

  .btn-deny {
    background: #f5f7fa;
    color: #555;
  }
}

/* ------ 兑换码弹窗 ------ */
.code-popup-panel {
  padding: 48rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 520rpx;

  .popup-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 32rpx;
  }

  .barcode-num {
    font-family: monospace;
    font-size: 56rpx;
    font-weight: 900;
    color: #07c160;
    letter-spacing: 8rpx;
    padding: 24rpx 40rpx;
    background: #f0fdf4;
    border-radius: 12rpx;
    margin-bottom: 24rpx;
  }

  .popup-tips {
    font-size: 22rpx;
    color: #95a5a6;
    margin-bottom: 32rpx;
    text-align: center;
  }

  .popup-close-btn {
    width: 100%;
    height: 80rpx;
    line-height: 80rpx;
    background: #07c160;
    color: #fff;
    font-size: 28rpx;
    font-weight: bold;
    border-radius: 40rpx;
    border: none;

    &::after { border: none; }
  }
}

/* ------ 退款弹窗 ------ */
.refund-popup {
  background: #fff;
  padding: 28rpx 36rpx calc(28rpx + env(safe-area-inset-bottom));
  border-radius: 20rpx 20rpx 0 0;

  .refund-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    margin-bottom: 24rpx;
  }

  .refund-goods-info {
    background: #f7f8fa;
    border-radius: 12rpx;
    padding: 20rpx 24rpx;
    margin-bottom: 24rpx;

    .refund-goods-title {
      font-size: 26rpx;
      color: #333;
      font-weight: 500;
      display: block;
      margin-bottom: 6rpx;
    }
    .refund-goods-price {
      font-size: 24rpx;
      color: #999;
    }
  }

  .refund-form {
    margin-bottom: 28rpx;
    .refund-label {
      font-size: 26rpx;
      color: #333;
      margin-bottom: 10rpx;
      display: block;
    }
    .refund-textarea {
      width: 100%;
      min-height: 180rpx;
      background: #f7f8fa;
      border-radius: 12rpx;
      padding: 16rpx 20rpx;
      font-size: 26rpx;
      box-sizing: border-box;
    }
  }

  .refund-footer {
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
      &::after { border: none; }
    }
    .refund-btn-cancel { background: #f0f0f0; color: #666; }
    .refund-btn-submit { background: #ef4444; color: #fff; }
  }
}
</style>
