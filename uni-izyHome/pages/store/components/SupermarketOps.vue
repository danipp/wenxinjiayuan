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
            <view class="tag-row"><text class="ops-tag">超市直营</text></view>
            <text class="goods-title text-ellipsis-2">{{ item.title }}</text>
            <view class="price-row">
              <text class="price-points">￥{{ item.cashPrice }}</text>
            </view>
            <text class="sales-text">超市提货 {{ item.sales }} 件</text>
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
          id: 308,
          title: "公益超市直营 · 原木纯品抽取式面巾纸 (6包)",
          cashPrice: 5.0,
          sales: 1200,
          image: "https://image.16pic.com/00/80/26/16pic_8026825_s.jpg",
        },
        {
          id: 309,
          title: "线下自提 · 社区爱心烘焙坊新鲜吐司券",
          cashPrice: 3.0,
          sales: 890,
          image:
            "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=400&q=80",
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
        uni.showToast({ title: "已加载超市直营宝贝", icon: "none" });
      }, 800);
    },
    goDetail(id) {
      uni.navigateTo({ url: `/spages/store/detail?id=1` });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./storeGrid.scss";
</style>