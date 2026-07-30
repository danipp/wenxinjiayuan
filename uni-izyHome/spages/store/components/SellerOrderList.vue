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
            <!-- 卖家端待领取显示为待核销 -->
            <text class="status-text" :class="order.status">
              {{ order.status === "pending" ? "待核销" : order.statusText }}
            </text>
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

          <!-- 卖家操作行 -->
          <view class="card-footer">
            <!-- 待核销：卖家专属绿色一键核销确认机制 -->
            <button
              v-if="order.status === 'pending'"
              class="footer-btn btn-primary-solid"
              @click="handleVerifyOrder(order)"
            >
              核销订单
            </button>

            <text v-if="order.status === 'completed'" class="completed-tips"
              >买家已核销成功，已出库</text
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
export default {
  props: {
    status: { type: String, default: "all" },
  },
  data() {
    return {
      isRefreshing: false,
      // 模拟卖家接单数据
      sellerOrders: [
        {
          id: 601,
          orderNum: "S202606010998",
          status: "pending",
          statusText: "待领取",
          title: "志愿者定制高品质 304 不锈钢保温杯 (500ml)",
          image:
            "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=400&q=80",
          priceText: "已收 120 积分",
          count: 1,
          correctCode: "584921", // 模拟正确的提取验证码
        },
        {
          id: 602,
          orderNum: "S202605200885",
          status: "completed",
          statusText: "已完成",
          title: "志愿者雨天关怀折叠定制晴雨伞",
          image:
            "https://images.unsplash.com/photo-1527786356703-4b100091cdb0?auto=format&fit=crop&w=400&q=80",
          priceText: "已收 200 积分",
          count: 1,
          correctCode: "",
        },
      ],
    };
  },
  computed: {
    filteredList() {
      // 卖家没有“待评价”，若选待评价，展示空以体现精细结构
      if (this.status === "evaluate") return [];
      if (this.status === "all") return this.sellerOrders;
      return this.sellerOrders.filter((o) => o.status === this.status);
    },
  },
  methods: {
    // 卖家一键核销闭环逻辑
    handleVerifyOrder(order) {
      uni.showModal({
        title: "核销商品验证",
        placeholderText: "请输入买家的 6 位数核销码",
        editable: true, // 开启 uni 弹窗的原生输入框模式！
        success: (res) => {
          if (res.confirm) {
            const inputVal = res.content.replace(/\s+/g, ""); // 过滤空格
            if (inputVal === order.correctCode) {
              uni.showLoading({ title: "核销出库中..." });
              setTimeout(() => {
                uni.hideLoading();
                order.status = "completed";
                order.statusText = "已完成";
                uni.showToast({ title: "核销成功！已出库", icon: "success" });
              }, 600);
            } else {
              uni.showToast({
                title: "核销码错误，请重新向买家确认",
                icon: "none",
              });
            }
          }
        },
      });
    },
    onRefresh() {
      this.isRefreshing = true;
      setTimeout(() => {
        this.isRefreshing = false;
      }, 800);
    },
    loadMore() {
      console.log("加载卖家下一页...");
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./orderComponent.scss";
</style>