<template>
  <view class="component-wrapper">
    <scroll-view
      scroll-y
      class="list-scroll-view"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view class="card-list" v-if="filteredList.length > 0">
        <view
          v-for="order in filteredList"
          :key="order.id"
          class="order-card"
          @click="goDetail(order.id)"
        >
          <view class="card-header">
            <text class="order-num">订单号：{{ order.orderNum }}</text>
            <text class="status-text" :class="order.statusCls">{{
              order.statusText
            }}</text>
          </view>

          <view class="card-body">
            <image class="goods-cover" :src="order.image"></image>
            <view class="goods-right">
              <text class="goods-title text-ellipsis-2">{{ order.title }}</text>
              <view class="reason" v-if="order.refundReason"
                >退款原因：{{ order.refundReason }}</view
              >
              <view class="price-count-row">
                <text class="goods-price">{{ order.priceText }}</text>
                <text class="goods-count">x{{ order.count }}</text>
              </view>
            </view>
          </view>

          <!-- 操作行 -->
          <view class="card-footer">
            <!-- 待支付：取消订单 + 去支付 -->
            <template v-if="order.status === 10">
              <button
                class="footer-btn btn-cancel"
                @click.stop="handleCancel(order)"
              >
                取消订单
              </button>
              <button
                class="footer-btn btn-primary-solid"
                @click.stop="handlePay(order)"
              >
                去支付
              </button>
            </template>

            <!-- 待领取：出示核销 + 申请退款 -->
            <template v-else-if="order.status === 20">
              <button
                class="footer-btn btn-primary-border"
                @click.stop="showRedeemCode(order)"
              >
                查看兑换码
              </button>
              <button
                class="footer-btn btn-cancel"
                @click.stop="handleRefund(order)"
              >
                申请退款
              </button>
            </template>

            <!-- 待评价：引流去评价页 -->
            <button
              v-else-if="order.status === 30 && !order.commentId"
              class="footer-btn btn-primary-solid"
              @click.stop="goWriteReview(order)"
            >
              去评价
            </button>

            <!-- 已完成（已评价） -->
            <text v-else-if="order.status === 30" class="completed-tips"
              >已完成</text
            >

            <!-- 退款申请中 -->
            <text
              v-else-if="
                order.status === 40 ||
                order.status === 41 ||
                order.status === 42
              "
              class="completed-tips"
              >退款处理中</text
            >

            <!-- 已退款 -->
            <text v-else-if="order.status === 50" class="completed-tips"
              >已退款</text
            >
          </view>
        </view>
      </view>

      <!-- 空白页 -->
      <view v-else class="empty-state">
        <u-icon name="empty-order" color="#cbd5e1" size="64"></u-icon>
        <text class="empty-text">暂无买家相关订单记录</text>
      </view>
    </scroll-view>

    <!-- 兑换码弹窗 -->
    <u-popup
      :show="codePopupShow"
      mode="center"
      round="16"
      @close="codePopupShow = false"
    >
      <view class="code-popup-panel">
        <text class="popup-title">请出示兑换码给社区管理员</text>
        <text class="popup-community">📍 财厅前社区志愿者中心</text>
        <view class="barcode-container">
          <!-- <view class="barcode-bars">
            <span class="bar w2"></span><span class="bar w1"></span
            ><span class="bar w3"></span> <span class="bar w1"></span
            ><span class="bar w2"></span><span class="bar w4"></span>
          </view> -->
          <text class="barcode-num">{{ activeOrder.redeemCode }}</text>
        </view>
        <text class="popup-tips">核销成功后即可领取对应商品，严防截图外泄</text>
        <button class="popup-close-btn" @click="codePopupShow = false">
          确认
        </button>
      </view>
    </u-popup>
    <!-- 退款申请底部弹窗 -->
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
          <text class="refund-goods-title">{{ refundOrder.title || "" }}</text>
          <text class="refund-goods-price">{{
            refundOrder.priceText || ""
          }}</text>
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
          <button class="refund-btn-cancel" @click="refundPopupShow = false">
            取消
          </button>
          <button class="refund-btn-submit" @click="doRefund">提交申请</button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import {
  page,
  redeemCode as getRedeemCode,
  cancel,
  refund,
} from "@/spages/api/order";

// 订单状态码映射
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
  props: {
    status: { type: String, default: "all" },
  },
  data() {
    return {
      isRefreshing: false,
      loading: false,
      noMore: false,
      refundPopupShow: false,
      refundOrder: {},
      refundReason: "",
      pageSize: 10,
      buyerOrders: [],
      codePopupShow: false,
      activeOrder: {
        redeemCode: "",
      },
    };
  },
  computed: {
    filteredList() {
      return this.buyerOrders;
    },
  },
  watch: {
    status() {
      this.pageNum = 1;
      this.noMore = false;
      this.buyerOrders = [];
      this.fetchOrders();
    },
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    // 根据 tab 构建请求参数
    buildParams() {
      const params = {
        pageNumber: this.pageNum,
        pageSize: this.pageSize,
        role: "buyer",
      };
      if (this.status === "pending") {
        params.status = 20;
      } else if (this.status === "completed") {
        params.status = 30;
      } else if (this.status === "evaluate") {
        params.pendingComment = true;
      }
      return params;
    },
    async fetchOrders() {
      if (this.loading || this.noMore) return;
      this.loading = true;
      try {
        const res = await page(this.buildParams());
        const pageData = res.data || {};
        const list = pageData.content || [];
        const isLast =
          pageData.last !== undefined
            ? pageData.last
            : list.length < this.pageSize;

        const mapped = list.map((item) => {
          const statusInfo = STATUS_MAP[item.status] || {
            text: "未知",
            cls: "",
          };
          return {
            id: item.orderId || item.id,
            orderNum: item.orderNum || "",
            status: item.status || 0,
            statusText: statusInfo.text,
            statusCls: statusInfo.cls,
            title: item.goodsTitle || "",
            image: item.goodsImage || "",
            priceText:
              item.payType === 1
                ? `${item.totalPoints || 0} 积分`
                : `¥${(item.totalAmount || 0).toFixed(2)}`,
            count: item.count || 1,
            redeemCode: item.redeemCode || "",
            commentId: item.commentId,
            payParams: item.payParamsJson
              ? JSON.parse(item.payParamsJson)
              : null,
            refundReason: item.refundReason || "",
          };
        });

        this.buyerOrders =
          this.pageNum === 1 ? mapped : [...this.buyerOrders, ...mapped];
        this.noMore = isLast;
        if (!isLast) this.pageNum++;
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
        this.isRefreshing = false;
      }
    },
    goDetail(id) {
      uni.navigateTo({
        url: `/spages/store/order/detail?id=${id}&type=buy`,
      });
    },
    async showRedeemCode(order) {
      try {
        const res = await getRedeemCode(order.id);
        const code = res.data || "";
        this.activeOrder = { ...order, redeemCode: code };
        this.codePopupShow = true;
        console.log("123qqqq");
      } catch (e) {
        this.activeOrder = order;
      }
    },
    goWriteReview(order) {
      const encodedTitle = encodeURIComponent(order.title);
      uni.navigateTo({
        url: `/spages/store/order/comments?id=${order.id}&communityName=${encodedTitle}`,
      });
    },
    onRefresh() {
      this.isRefreshing = true;
      this.pageNum = 1;
      this.noMore = false;
      this.buyerOrders = [];
      this.fetchOrders();
    },
    loadMore() {
      if (!this.noMore && !this.loading) {
        this.fetchOrders();
      }
    },
    // 买家取消订单
    async handleCancel(order) {
      uni.showModal({
        title: "取消订单",
        content: "确定要取消该订单吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await cancel(order.id);
              uni.showToast({ title: "已取消", icon: "success" });
              this.onRefresh();
            } catch (e) {
              uni.showToast({ title: "取消失败", icon: "none" });
            }
          }
        },
      });
    },
    // 买家申请退款
    handleRefund(order) {
      this.refundOrder = order;
      this.refundReason = "";
      this.refundPopupShow = true;
    },
    async doRefund() {
      const orderId = this.refundOrder.id;
      if (!orderId) return;
      this.refundPopupShow = false;
      try {
        await refund({ orderId, reason: this.refundReason.trim() });
        uni.showToast({ title: "退款申请已提交", icon: "success" });
        this.onRefresh();
      } catch (e) {
        uni.showToast({ title: "申请失败，请重试", icon: "none" });
      }
    },
    // 去支付
    handlePay(order) {
      // 如果后端返回了微信支付参数，则调起支付
      if (order.payParams) {
        uni.requestPayment({
          ...order.payParams,
          package: order.payParams.packageValue,
          success: () => {
            uni.showToast({ title: "支付成功", icon: "success" });
            this.onRefresh();
          },
          fail: () => {
            uni.showToast({ title: "支付取消", icon: "none" });
          },
        });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./orderComponent.scss"; // 抽取两端高度复用的公共 SCSS

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
      &::after {
        border: none;
      }
    }

    .refund-btn-cancel {
      background: #f0f0f0;
      color: #666;
    }

    .refund-btn-submit {
      background: #ef4444;
      color: #fff;
    }
  }
}
</style>