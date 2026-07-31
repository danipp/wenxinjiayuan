<template>
  <view class="component-page-box">
    <scroll-view
      scroll-y
      class="scroll-view-panel"
      :style="{ height: 'calc(100vh - ' + headerHeight + 'rpx)' }"
      @scrolltolower="handleLoadMore"
    >
      <view class="goods-grid" v-if="goodsList.length">
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
      <view v-else>
        <u-empty text="暂无商品" mode="search"></u-empty>
      </view>
      <view class="load-more-tips">
        <text v-if="loading">加载中...</text>
        <text v-else-if="noMore && goodsList.length > 0" class="no-more"
          >—— 已经是最后一页了 ——</text
        >
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { page1 } from "@/api/goods";

export default {
  props: {
    headerHeight: { type: Number, default: 200 },
  },
  data() {
    return {
      loading: false,
      noMore: false,
      pageNum: 1,
      pageSize: 10,
      goodsList: [],
    };
  },
  mounted() {
    this.fetchGoods();
  },
  methods: {
    async fetchGoods() {
      if (this.loading || this.noMore) return;
      this.loading = true;
      try {
        const res = await page1({
          pageNumber: this.pageNum,
          pageSize: this.pageSize,
          goodsType: 1,
          scene: "积分兑换",
          status: 1,
        });
        const pageData = res.data || {};
        const list = pageData.content || [];
        const isLast =
          pageData.last !== undefined
            ? pageData.last
            : list.length < this.pageSize;

        const mapped = list.map((item) => ({
          id: item.goodsId || item.id,
          title: item.title || "",
          pointsPrice: item.pointsPrice || 0,
          sales: item.salesCount || 0,
          image: item.coverImage || "",
        }));

        this.goodsList =
          this.pageNum === 1 ? mapped : [...this.goodsList, ...mapped];
        this.noMore = isLast;
        if (!isLast) this.pageNum++;
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },
    handleLoadMore() {
      if (!this.loading && !this.noMore) {
        this.fetchGoods();
      }
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