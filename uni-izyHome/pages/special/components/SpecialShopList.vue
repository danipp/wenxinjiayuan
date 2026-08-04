<template>
  <scroll-view
    scroll-y
    class="content-right-col"
    :style="{ height: 'calc(100vh - ' + headerHeight + 'px)' }"
    @scrolltolower="$emit('load-more')"
  >
    <!-- 多维筛选栏 -->
    <view class="sort-filter-bar">
      <view
        class="filter-tag-btn"
        :class="{ 'tag-active': sortMode !== '' }"
        @click="toggleSort"
      >
        销量 <text class="arrow">{{ sortMode === 'sales' ? '↓' : '↕' }}</text>
      </view>

      <view
        class="filter-tag-btn"
        :class="{ 'tag-active': priceOrder !== '' }"
        @click="togglePrice"
      >
        价格 <text class="arrow">{{ priceOrder === 'price_asc' ? '↑' : priceOrder === 'price_desc' ? '↓' : '↕' }}</text>
      </view>

      <view
        class="filter-tag-btn"
        :class="{ 'tag-active': highRating }"
        @click="toggleHighRating"
      >高评分</view>

      <view
        class="filter-tag-btn"
        :class="{ 'tag-active': isNewOnly }"
        @click="toggleNew"
      >新品</view>
    </view>

    <!-- 店铺列表 -->
    <view class="shop-list-wrapper" v-if="shopList.length > 0">
      <view
        v-for="shop in shopList"
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

      <view class="load-more-tips">
        <view v-if="loading" class="loading-tip-inner">
          <u-loading-icon></u-loading-icon>
          <text>加载中...</text>
        </view>
        <text v-else-if="noMore" class="no-more">已加载全部</text>
      </view>
    </view>

    <view v-else class="empty-state">
      <u-icon name="empty-list" color="#cbd5e1" size="100rpx"></u-icon>
      <text class="empty-text">暂无匹配店铺</text>
    </view>
  </scroll-view>
</template>

<script>
export default {
  props: {
    shopList: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false },
    noMore: { type: Boolean, default: false },
    headerHeight: { type: Number, default: 120 },
  },
  data() {
    return {
      sortMode: '',     // 'sales' | ''
      priceOrder: '',    // 'price_asc' | 'price_desc' | ''
      highRating: false,
      isNewOnly: false,
    };
  },
  methods: {
    // 销量排序
    toggleSort() {
      this.sortMode = this.sortMode === 'sales' ? '' : 'sales';
      this.priceOrder = ''; // 互斥
      this.emitFilters();
    },
    // 价格排序
    togglePrice() {
      if (this.priceOrder === '') {
        this.priceOrder = 'price_asc';
      } else if (this.priceOrder === 'price_asc') {
        this.priceOrder = 'price_desc';
      } else {
        this.priceOrder = '';
      }
      this.sortMode = ''; // 互斥
      this.emitFilters();
    },
    toggleHighRating() {
      this.highRating = !this.highRating;
      this.emitFilters();
    },
    toggleNew() {
      this.isNewOnly = !this.isNewOnly;
      this.emitFilters();
    },

    emitFilters() {
      let sort = '';
      if (this.sortMode === 'sales') sort = 'sales';
      else if (this.priceOrder) sort = this.priceOrder;
      // false 时不通知父组件
      if (sort !== this._lastSort || this.highRating !== this._lastHigh || this.isNewOnly !== this._lastNew) {
        this._lastSort = sort;
        this._lastHigh = this.highRating;
        this._lastNew = this.isNewOnly;
        this.$emit('filter-change', {
          sort,
          highRating: this.highRating,
          isNew: this.isNewOnly,
        });
      }
    },
  },
  watch: {
    // 父组件 fetchShops 重置后，本地也重置过滤
    shopList(val, old) {
      if (val.length === 0 && old && old.length > 0) {
        // 可能是搜索/切换分类的结果，保持过滤状态
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.content-right-col {
  flex: 1;
  background-color: #ffffff;
  box-sizing: border-box;

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

      .arrow {
        font-size: 18rpx;
        margin-left: 2rpx;
      }

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

    .loading-tip-inner {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12rpx;
    }
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
