<template>
  <view class="component-page-box">
    <!-- 严格使用 calc(100vh - 上方高度)，禁用 flex:1 -->
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
              <text class="price-cash">+￥{{ item.cashPrice }}</text>
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
          id: 301,
          title: "志愿者定制高品质 304 不锈钢保温杯 (500ml)",
          pointsPrice: 120,
          cashPrice: 0,
          sales: 241,
          image:
            "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=400&q=80",
        },
        {
          id: 303,
          title: "社区多功能应急救援高亮防身手电筒",
          pointsPrice: 150,
          cashPrice: 9.9,
          sales: 92,
          image: "https://image.16pic.com/00/55/57/16pic_5557645_s.jpg",
        },
        {
          id: 304,
          title: "志愿者雨天关怀折叠定制晴雨伞",
          pointsPrice: 200,
          cashPrice: 12.0,
          sales: 114,
          image: "https://image.16pic.com/00/93/44/16pic_9344910_s.png",
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
        uni.showToast({ title: "已加载最新积分商品", icon: "none" });
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