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
        <view v-for="order in filteredList" :key="order.id" class="order-card">
          <view class="card-header">
            <text class="order-num">订单号：{{ order.orderNum }}</text>
            <text class="status-text" :class="order.status">{{
              order.statusText
            }}</text>
          </view>

          <view class="card-body">
            <image
              class="goods-cover"
              :src="order.image"
              mode="aspectFill"
            ></image>
            <view class="goods-right">
              <text class="goods-title text-ellipsis-2">{{ order.title }}</text>
              <view class="price-count-row">
                <text class="goods-price">{{ order.priceText }}</text>
                <text class="goods-count">x{{ order.count }}</text>
              </view>
            </view>
          </view>

          <!-- 操作行 -->
          <view class="card-footer">
            <!-- 待领取：出示核销 -->
            <button
              v-if="order.status === 'pending'"
              class="footer-btn btn-primary-border"
              @click="showRedeemCode(order)"
            >
              查看兑换码
            </button>

            <!-- 待评价：引流去评价页 -->
            <button
              v-if="order.status === 'evaluate'"
              class="footer-btn btn-primary-solid"
              @click="goWriteReview(order)"
            >
              去评价
            </button>

            <!-- 已完成 -->
            <text v-if="order.status === 'completed'" class="completed-tips"
              >已于社区志愿者服务中心领取</text
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
          <view class="barcode-bars">
            <span class="bar w2"></span><span class="bar w1"></span
            ><span class="bar w3"></span> <span class="bar w1"></span
            ><span class="bar w2"></span><span class="bar w4"></span>
          </view>
          <text class="barcode-num">{{ activeOrder.redeemCode }}</text>
        </view>
        <text class="popup-tips">核销成功后即可领取对应商品，严防截图外泄</text>
        <button class="popup-close-btn" @click="codePopupShow = false">
          确认
        </button>
      </view>
    </u-popup>
  </view>
</template>

<script>
export default {
  props: {
    status: { type: String, default: "all" }, // 接收父组件传入的状态过滤字
  },
  data() {
    return {
      isRefreshing: false,
      codePopupShow: false,
      activeOrder: {},
      // 模拟买家拥有的三态数据
      buyerOrders: [
        {
          id: 501,
          orderNum: "B202606010024",
          status: "pending",
          statusText: "待领取",
          title: "志愿者定制高品质 304 不锈钢保温杯 (500ml)",
          image:
            "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=400&q=80",
          priceText: "120 积分",
          count: 1,
          redeemCode: "584 921",
        },
        {
          id: 502,
          orderNum: "B202605200114",
          status: "evaluate",
          statusText: "待评价",
          title: "爱心家园帆布袋（加厚双肩环保袋）",
          image:
            "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=400&q=80",
          priceText: "50 积分",
          count: 1,
          redeemCode: "",
        },
        {
          id: 503,
          orderNum: "B202604100082",
          status: "completed",
          statusText: "已完成",
          title: "社区多功能应急救援高亮防身手电筒",
          image:
            "https://images.unsplash.com/photo-1563201374-1c97a1d3528d?auto=format&fit=crop&w=400&q=80",
          priceText: "150 积分",
          count: 1,
          redeemCode: "",
        },
      ],
    };
  },
  computed: {
    filteredList() {
      if (this.status === "all") return this.buyerOrders;
      return this.buyerOrders.filter((o) => o.status === this.status);
    },
  },
  methods: {
    showRedeemCode(order) {
      this.activeOrder = order;
      this.codePopupShow = true;
    },
    // 跳转去评价页面，携带社区活动信息（带入我们此前写好的评价页）
    goWriteReview(order) {
      const encodedTitle = encodeURIComponent(order.title);
      uni.navigateTo({
        url: `/spages/store/order/comments?id=${order.id}&communityName=${encodedTitle}`,
      });
    },
    onRefresh() {
      this.isRefreshing = true;
      setTimeout(() => {
        this.isRefreshing = false;
      }, 800);
    },
    loadMore() {
      console.log("加载买家下一页...");
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./orderComponent.scss"; // 抽取两端高度复用的公共 SCSS
</style>