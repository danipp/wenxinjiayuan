<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="20"
    @close="handleClose"
    :safeAreaInsetBottom="true"
    @touchmove.stop.prevent
  >
    <view class="goods-picker">
      <view class="picker-title">选择商品</view>

      <!-- 搜索 -->
      <view class="search-bar-inner">
        <u-search
          v-model="keyword"
          placeholder="搜索商品"
          :show-action="false"
          @search="onSearch"
          @clear="onSearchClear"
        />
      </view>

      <scroll-view
        class="goods-list"
        scroll-y
        @scrolltolower="onLoadMore"
      >
        <view v-if="list.length === 0 && !loading" class="empty-state">
          <text>暂无可用商品</text>
        </view>

        <view
          v-for="item in list"
          :key="item.goodsId"
          class="goods-item"
          :class="{ selected: selectedId === item.goodsId }"
          @click="selectItem(item)"
        >
          <image
            class="goods-img"
            :src="item.coverImage || '/static/default-goods.png'"
            mode="aspectFill"
          />
          <view class="goods-info">
            <text class="goods-title">{{ item.title }}</text>
            <text class="goods-stock">库存: {{ item.stock || 0 }}</text>
            <text class="goods-price" v-if="item.cashPrice">
              ¥{{ item.cashPrice }}
            </text>
          </view>
          <view class="check-mark" v-if="selectedId === item.goodsId">
            <u-icon name="checkbox-mark" color="#07c160" size="22" />
          </view>
        </view>

        <view v-if="loading" class="loading-tip">
          <u-loading-icon></u-loading-icon>
          <text>加载中...</text>
        </view>
        <view v-if="noMore && list.length > 0" class="no-more-tip">已加载全部</view>
      </scroll-view>

      <view class="picker-footer">
        <button class="btn-cancel" @click="handleClose">取消</button>
        <button class="btn-confirm" @click="handleConfirm">确定</button>
      </view>
    </view>
  </u-popup>
</template>

<script>
import { page1 } from '@/api/goods.js';

export default {
  name: 'GoodsPicker',
  props: {
    show: { type: Boolean, default: false },
  },
  watch: {
    show(val) {
      if (val) {
        this.keyword = '';
        this.list = [];
        this.pageNumber = 0;
        this.noMore = false;
        this.selectedId = '';
        this.selectedGoods = null;
        this.fetchList();
      }
    },
  },
  data() {
    return {
      keyword: '',
      list: [],
      pageNumber: 0,
      pageSize: 15,
      loading: false,
      noMore: false,
      selectedId: '',
      selectedGoods: null,
    };
  },
  methods: {
    async fetchList(append = false) {
      if (this.loading) return;
      if (!append) { this.pageNumber = 0; this.list = []; this.noMore = false; }
      this.loading = true;
      try {
        const res = await page1({
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          scene: 'assistance',
          status: 1,
          keyword: this.keyword || undefined,
        });
        if (res.code === '00000' && res.data) {
          const { content = [], last } = res.data;
          this.list = append ? this.list.concat(content) : content;
          this.noMore = last !== false;
        }
      } catch (e) {
        uni.showToast({ title: '加载商品失败', icon: 'none' });
      } finally { this.loading = false; }
    },

    onSearch() { this.fetchList(); },
    onSearchClear() { this.fetchList(); },

    onLoadMore() {
      if (this.noMore || this.loading) return;
      this.pageNumber++;
      this.fetchList(true);
    },

    selectItem(item) {
      this.selectedId = item.goodsId;
      this.selectedGoods = item;
    },

    handleConfirm() {
      if (!this.selectedGoods) {
        uni.showToast({ title: '请先选择商品', icon: 'none' });
        return;
      }
      this.$emit('confirm', this.selectedGoods);
      this.handleClose();
    },

    handleClose() {
      this.$emit('update:show', false);
    },
  },
};
</script>

<style lang="scss" scoped>
.goods-picker {
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  display: flex;
  flex-direction: column;
  max-height: 85vh;
  overflow: hidden;

  .picker-title {
    font-size: 32rpx; font-weight: bold; color: #333;
    text-align: center; padding: 36rpx 0 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
    flex-shrink: 0;
  }

  .search-bar-inner {
    padding: 16rpx 32rpx;
    flex-shrink: 0;
  }

  .goods-list {
    flex: 1;
    max-height: 55vh;
    padding: 0 32rpx;

    .empty-state {
      display: flex; justify-content: center; padding: 80rpx 0;
      font-size: 26rpx; color: #999;
    }

    .goods-item {
      display: flex; align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f5f5f5;
      position: relative;

      .goods-img {
        width: 100rpx; height: 100rpx;
        border-radius: 14rpx; background: #f5f5f5;
        margin-right: 18rpx; flex-shrink: 0;
      }

      .goods-info {
        flex: 1; min-width: 0;
        .goods-title {
          font-size: 28rpx; font-weight: bold; color: #1a202c;
          display: block; margin-bottom: 6rpx;
        }
        .goods-stock {
          font-size: 22rpx; color: #a0aec0;
        }
        .goods-price {
          font-size: 26rpx; color: #f59e0b; font-weight: bold;
        }
      }

      &.selected {
        background: #e6f7ed;
        border-radius: 12rpx;
        margin: 0 -12rpx;
        padding-left: 12rpx;
        padding-right: 12rpx;
      }

      .check-mark {
        margin-left: 12rpx; flex-shrink: 0;
      }
    }

    .loading-tip, .no-more-tip {
      display: flex; justify-content: center; align-items: center;
      gap: 12rpx; padding: 24rpx 0; font-size: 24rpx; color: #999;
    }
  }

  .picker-footer {
    display: flex; gap: 20rpx;
    padding: 28rpx 40rpx calc(28rpx + env(safe-area-inset-bottom));
    flex-shrink: 0;

    button {
      flex: 1; height: 80rpx; line-height: 80rpx;
      font-size: 28rpx; font-weight: bold; border-radius: 40rpx; border: none; padding: 0;
      &::after { border: none; }
    }
    .btn-cancel { background: #f0f0f0; color: #666; }
    .btn-confirm { background: #07c160; color: #fff; }
  }
}
</style>
