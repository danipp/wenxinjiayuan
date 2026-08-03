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
            <text class="status-text" :class="order.statusCls">
              {{ order.statusText }}
            </text>
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

          <!-- 卖家操作行 -->
          <view class="card-footer">
            <!-- 待核销（status=20）：卖家核销 -->
            <button
              v-if="order.status === 20"
              class="footer-btn btn-primary-solid"
              @click.stop="handleVerifyOrder(order)"
            >
              核销订单
            </button>

            <!-- 退款申请（status=40/41/42）：同意退款 / 拒绝退款 -->
            <template
              v-if="
                order.status === 40 ||
                order.status === 41 ||
                order.status === 42
              "
            >
              <button
                class="footer-btn btn-primary-solid"
                @click.stop="handleApproveRefund(order)"
              >
                同意退款
              </button>
              <button
                class="footer-btn btn-deny"
                @click.stop="handleRejectRefund(order)"
              >
                拒绝退款
              </button>
            </template>

            <text v-if="order.status === 30" class="completed-tips"
              >买家已核销成功，已出库</text
            >

            <text v-if="order.status === 50" class="completed-tips"
              >已退款</text
            >
          </view>
        </view>
      </view>

      <!-- 空白页 -->
      <view class="empty-state" v-else>
        <u-icon name="empty-order" color="#cbd5e1" size="64"></u-icon>
        <text class="empty-text">暂无卖家相关订单记录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { page, verify, approveRefund, rejectRefund } from "@/spages/api/order";

const STATUS_MAP = {
  10: { text: "待支付", cls: "status-pending" },
  20: { text: "待核销", cls: "status-pending" },
  30: { text: "已完成", cls: "status-completed" },
  40: { text: "退款申请", cls: "status-refund" },
  41: { text: "退款申请", cls: "status-refund" },
  42: { text: "退款申请", cls: "status-refund" },
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
      pageNum: 1,
      pageSize: 10,
      sellerOrders: [],
    };
  },
  computed: {
    filteredList() {
      return this.sellerOrders;
    },
  },
  watch: {
    status() {
      this.pageNum = 1;
      this.noMore = false;
      this.sellerOrders = [];
      this.fetchOrders();
    },
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    buildParams() {
      const params = {
        pageNumber: this.pageNum,
        pageSize: this.pageSize,
        role: "seller",
      };
      if (this.status === "pending") {
        params.status = 20;
      } else if (this.status === "completed") {
        params.status = 30;
      } else if (this.status === "refund") {
        params.statusList = [40, 41, 42, 50];
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
            refundReason: item.refundReason || "",
            priceText:
              item.payType === 1
                ? `已收 ${item.totalPoints || 0} 积分`
                : `已收 ¥${(item.totalAmount || 0).toFixed(2)}`,
            count: item.count || 1,
          };
        });

        this.sellerOrders =
          this.pageNum === 1 ? mapped : [...this.sellerOrders, ...mapped];
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
        url: `/spages/store/order/detail?id=${id}&type=sell`,
      });
    },
    // 卖家核销
    async handleVerifyOrder(order) {
      uni.showModal({
        title: "核销商品验证",
        placeholderText: "请输入买家的核销码",
        editable: true,
        success: async (res) => {
          if (res.confirm) {
            const code = (res.content || "").replace(/\s+/g, "");
            try {
              await verify({ orderId: order.id, redeemCode: code });
              uni.showToast({ title: "核销成功！已出库", icon: "success" });
              this.onRefresh();
            } catch (e) {
              uni.showToast({ title: "核销失败，请检查核销码", icon: "none" });
            }
          }
        },
      });
    },
    // 同意退款
    async handleApproveRefund(order) {
      uni.showModal({
        title: "确认退款",
        content: "同意后将自动退款给买家，确定吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await approveRefund(order.id);
              uni.showToast({ title: "已同意退款", icon: "success" });
              this.onRefresh();
            } catch (e) {
              uni.showToast({ title: "操作失败", icon: "none" });
            }
          }
        },
      });
    },
    // 拒绝退款
    async handleRejectRefund(order) {
      uni.showModal({
        title: "拒绝退款",
        content: "拒绝后将恢复订单为待核销状态，确定吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await rejectRefund(order.id);
              uni.showToast({ title: "已拒绝退款", icon: "success" });
              this.onRefresh();
            } catch (e) {
              uni.showToast({ title: "操作失败", icon: "none" });
            }
          }
        },
      });
    },
    onRefresh() {
      this.isRefreshing = true;
      this.pageNum = 1;
      this.noMore = false;
      this.sellerOrders = [];
      this.fetchOrders();
    },
    loadMore() {
      if (!this.noMore && !this.loading) {
        this.fetchOrders();
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./orderComponent.scss";

.btn-deny {
  background-color: #f5f7fa;
  color: #555;
  border-radius: 44rpx;
  height: 64rpx;
  line-height: 64rpx;
  font-size: 26rpx;
  font-weight: bold;
  padding: 0 28rpx;

  &::after {
    border: none;
  }
}
</style>
