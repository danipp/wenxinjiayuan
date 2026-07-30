<template>
  <!-- 核心修复：硬性绑定 calc(100vh - headerHeight)，彻底解除 @scrolltolower 触底失效 Bug -->
  <scroll-view 
    scroll-y 
    class="content-right-col" 
    :style="{ height: 'calc(100vh - ' + headerHeight + 'px)' }"
    @scrolltolower="handleLoadMore"
  >
    <!-- 1. 重构：多维自由筛选过滤栏（非互斥单选，各自拥有独立状态开关） -->
    <view class="sort-filter-bar">
      <!-- 销量筛选按钮 -->
      <view 
        class="filter-tag-btn" 
        :class="{ 'tag-active': isSalesFiltered }"
        @click="toggleSalesFilter"
      >
        销量高
      </view>
      
      <!-- 价格升降序切换 -->
      <view 
        class="filter-tag-btn" 
        :class="{ 'tag-active': priceOrder !== 'none' }"
        @click="togglePriceSort"
      >
        价格 <text class="arrow">{{ priceOrder === 'asc' ? '↑' : (priceOrder === 'desc' ? '↓' : '↕') }}</text>
      </view>

      <!-- 推荐开关 -->
      <view 
        class="filter-tag-btn" 
        :class="{ 'tag-active': isRatingFiltered }"
        @click="isRatingFiltered = !isRatingFiltered"
      >
        高评分
      </view>

      <!-- 新品开关 -->
      <view 
        class="filter-tag-btn" 
        :class="{ 'tag-active': isNewOnly }"
        @click="isNewOnly = !isNewOnly"
      >
        新品
      </view>
    </view>

    <!-- 2. 动态渲染店铺列表 -->
    <view class="shop-list-wrapper" v-if="processedShops.length > 0">
      <view 
        v-for="shop in processedShops" 
        :key="shop.id" 
        class="shop-row-card"
        @click="$emit('go-detail', shop.id)"
      >
        <image class="shop-cover" :src="shop.image" mode="aspectFill"></image>
        <view class="shop-right-info">
          <text class="shop-title text-ellipsis-2">{{ shop.title }}</text>
          <view class="price-sales-row">
            <text class="start-price">￥{{ shop.price }}<text class="unit">起</text></text>
            <text class="sales-count">月售 {{ shop.sales }}</text>
          </view>
          <view class="tag-row">
            <text class="tag-label">社区特惠</text>
            <text v-if="shop.rating" class="tag-label score">★ {{ shop.rating }}</text>
            <text v-if="shop.isNew" class="tag-label new">新品</text>
          </view>
        </view>
      </view>

      <!-- 分页加载指示 -->
      <view class="load-more-tips">
        <text v-if="loading">加载中...</text>
        <text v-else class="no-more">已经是最后一页了</text>
      </view>
    </view>

    <!-- 缺省页 -->
    <view v-else class="empty-state">
      <u-icon name="empty-list" color="#cbd5e1" size="100rpx"></u-icon>
      <text class="empty-text">该条件下暂无匹配店铺</text>
    </view>
  </scroll-view>
</template>

<script>
export default {
  props: {
    shopList: { type: Array, default: () => [] },
    headerHeight: { type: Number, default: 120 }
  },
  data() {
    return {
      // 独立自由过滤状态（非排他单选）
      isSalesFiltered: false,  // 是否过滤销量>100
      priceOrder: 'none',      // 'none', 'asc', 'desc'
      isRatingFiltered: false, // 是否过滤评分>=4.8
      isNewOnly: false,        // 是否只看新品
      
      loading: false,
      page: 1
    };
  },
  computed: {
    // 动态计算多维筛选后的店铺列表
    processedShops() {
      let list = [...this.shopList];

      // 1. 销量过滤
      if (this.isSalesFiltered) {
        list = list.filter(s => s.sales >= 100);
      }

      // 2. 评分过滤
      if (this.isRatingFiltered) {
        list = list.filter(s => Number(s.rating) >= 4.8);
      }

      // 3. 新品过滤
      if (this.isNewOnly) {
        list = list.filter(s => s.isNew);
      }

      // 4. 价格排序
      if (this.priceOrder === 'asc') {
        list.sort((a, b) => a.price - b.price);
      } else if (this.priceOrder === 'desc') {
        list.sort((a, b) => b.price - a.price);
      }

      return list;
    }
  },
  methods: {
    toggleSalesFilter() {
      this.isSalesFiltered = !this.isSalesFiltered;
    },
    togglePriceSort() {
      if (this.priceOrder === 'none') this.priceOrder = 'asc';
      else if (this.priceOrder === 'asc') this.priceOrder = 'desc';
      else this.priceOrder = 'none';
    },

    // 核心：scroll-view 触底加载
    handleLoadMore() {
      if (this.loading) return;
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
        uni.showToast({ title: '已滑动触底，加载最新店铺', icon: 'none' });
      }, 800);
    }
  }
};
</script>

<style lang="scss" scoped>
.content-right-col {
  flex: 1;
  background-color: #ffffff;
  box-sizing: border-box;

  /* 自由多维筛选栏 */
  .sort-filter-bar {
    display: flex;
    align-items: center;
    justify-content: space-around;
    height: 80rpx;
    border-bottom: 1rpx solid #f1f5f9;
    position: sticky;
    top: 0;
    background-color: #ffffff;
    z-index: 10;
    padding: 0 10rpx;

    .filter-tag-btn {
      font-size: 22rpx;
      color: #64748b;
      font-weight: bold;
      background-color: #f1f5f9;
      padding: 6rpx 16rpx;
      border-radius: 20rpx;
      transition: all 0.2s ease;
      cursor: pointer;

      .arrow {
        font-size: 18rpx;
        margin-left: 2rpx;
      }

      /* 高亮激活状态 */
      &.tag-active {
        background-color: #fff1f0;
        color: #ff4d4f;
        font-weight: 800;
      }
    }
  }

  .shop-list-wrapper {
    padding: 20rpx;
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }

  .shop-row-card {
    display: flex;
    gap: 20rpx;
    padding-bottom: 24rpx;
    border-bottom: 1rpx solid #f8fafc;

    &:last-child {
      border-bottom: none;
    }

    .shop-cover {
      width: 150rpx;
      height: 150rpx;
      border-radius: 12rpx;
      background-color: #f1f5f9;
    }

    .shop-right-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;

      .shop-title {
        font-size: 26rpx;
        font-weight: bold;
        color: #1e293b;
        line-height: 1.4;
      }

      .price-sales-row {
        display: flex;
        justify-content: space-between;
        align-items: baseline;

        .start-price {
          font-family: "Georgia", serif;
          font-size: 32rpx;
          font-weight: bold;
          color: #ff4d4f;

          .unit {
            font-size: 20rpx;
            font-weight: normal;
            margin-left: 2rpx;
          }
        }

        .sales-count {
          font-size: 20rpx;
          color: #94a3b8;
        }
      }

      .tag-row {
        display: flex;
        gap: 8rpx;

        .tag-label {
          font-size: 18rpx;
          color: #ff4d4f;
          background-color: #fff1f0;
          padding: 2rpx 8rpx;
          border-radius: 4rpx;
          font-weight: bold;

          &.score {
            background-color: #fffbe6;
            color: #d97706;
          }

          &.new {
            background-color: #e0f2fe;
            color: #0284c7;
          }
        }
      }
    }
  }

  .load-more-tips {
    text-align: center;
    padding: 20rpx 0;
    font-size: 22rpx;
    color: #94a3b8;
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 160rpx;

    .empty-text {
      font-size: 26rpx;
      color: #94a3b8;
      margin-top: 16rpx;
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