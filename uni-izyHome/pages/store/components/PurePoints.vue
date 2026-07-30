<template>
  <view class="component-page-box">
    <scroll-view
      scroll-y
      class="scroll-view-panel"
      :style="{ height: 'calc(100vh - ' + headerHeight + 'rpx)' }"
      @scrolltolower="handleLoadMore"
    >
      <view class="goods-grid">
        <view
          v-for="item in goodsList"
          :key="item.id"
          class="goods-card"
          @click="goDetail(item.id)"
        >
          <image class="goods-img" :src="item.image" mode="aspectFill"></image>
          <view class="goods-info">
            <text class="goods-title text-ellipsis-2">{{ item.title }}</text>
            <view class="price-row">
              <text class="price-points"
                >{{ item.pointsPrice }}<text class="unit">积分</text></text
              >
              <text class="free-badge">全额兑换</text>
            </view>
            <text class="sales-text">已兑 {{ item.sales }} 件</text>
          </view>
        </view>
      </view>

      <view class="load-more-tips">
        <text v-if="loading">加载中...</text>
        <text v-else class="no-more">—— 已经是最后一页了 ——</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  props: {
    headerHeight: { type: Number, default: 200 },
  },
  data() {
    return {
      loading: false,
      goodsList: [
        {
          id: 302,
          title: "爱心家园帆布袋（加厚双肩环保袋）",
          pointsPrice: 50,
          sales: 580,
          image:
            "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=400&q=80",
        },
        {
          id: 305,
          title: "爱心志愿者专属纪念金属徽章礼盒",
          pointsPrice: 30,
          sales: 920,
          image:
            "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=400&q=80",
        },
      ],
    };
  },
  methods: {
    handleLoadMore() {
      if (this.loading) return;
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
        uni.showToast({ title: "已加载纯积分商品", icon: "none" });
      }, 800);
    },
    goDetail(id) {
      uni.navigateTo({ url: `/spages/store/detail?id=${id}` });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./storeGrid.scss";
</style>