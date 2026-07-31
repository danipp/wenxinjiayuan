<template>
  <view class="component-wrapper">
    <scroll-view scroll-y class="list-scroll-view" @scrolltolower="loadMore">
      <view class="goods-grid" v-if="goodsList.length > 0">
        <view
          v-for="item in goodsList"
          :key="item.id"
          class="goods-item-card"
          @click="goGoodsDetail(item.id)"
        >
          <!-- 封面大图与微标 -->
          <view class="img-box">
            <image
              class="goods-img"
              :src="item.image"
              mode="aspectFill"
            ></image>
            <view class="type-badge" :class="item.payType">
              {{ item.payType === "pure" ? "纯积分兑换" : "积分+现金" }}
            </view>
          </view>

          <!-- 信息描述 -->
          <view class="goods-info">
            <text class="goods-title text-ellipsis-2">{{ item.title }}</text>
            <view class="price-row">
              <text class="price-points"
                >{{ item.pointsPrice }}<text class="p-unit">积分</text></text
              >
              <text v-if="item.cashPrice" class="price-cash"
                >+￥{{ item.cashPrice }}</text
              >
            </view>
            <view class="sales-row">
              <text class="sales-text">已兑 {{ item.sales }} 件</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空缺省 -->
      <view v-else class="empty-state">
        <u-icon name="empty-coupon" color="#cbd5e1" size="128rpx"></u-icon>
        <text class="empty-text">暂无收藏商品记录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { collections } from "@/spages/api/goods";

export default {
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
    this.fetchList();
  },
  methods: {
    async fetchList() {
      if (this.loading || this.noMore) return;
      this.loading = true;
      try {
        const res = await collections({
          targetType: 1,
          page: this.pageNum,
          size: this.pageSize,
        });
        const pageData = res.data || {};
        const list = pageData.content || [];
        const isLast = pageData.last !== undefined ? pageData.last : list.length < this.pageSize;

        const mapped = list.map((item) => ({
          id: item.targetId,
          collectionId: item.collectionId,
          // 列表只返回 collection 基本信息，商品详情需要额外加载
          title: "",
          payType: "pure",
          pointsPrice: 0,
          cashPrice: 0,
          sales: 0,
          image: "",
        }));

        this.goodsList = this.pageNum === 1 ? mapped : [...this.goodsList, ...mapped];
        this.noMore = isLast;
        if (!isLast) this.pageNum++;
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },
    goGoodsDetail(id) {
      uni.navigateTo({ url: `/spages/store/detail?id=${id}` });
    },
    loadMore() {
      if (!this.loading && !this.noMore) {
        this.fetchList();
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.component-wrapper {
  height: 100%;
  width: 100%;

  .list-scroll-view {
    height: 100%;
    width: 100%;
  }

  /* 2列瀑布流 */
  .goods-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24rpx;
    padding: 28rpx;
    box-sizing: border-box;
  }

  .goods-item-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    overflow: hidden;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.015);
    display: flex;
    flex-direction: column;

    .img-box {
      width: 100%;
      height: 280rpx;
      position: relative;
      background-color: #f1f5f9;

      .goods-img {
        width: 100%;
        height: 100%;
      }

      .type-badge {
        position: absolute;
        top: 16rpx;
        left: 16rpx;
        font-size: 20rpx;
        font-weight: bold;
        padding: 4rpx 12rpx;
        border-radius: 8rpx;

        &.pure {
          background-color: #fef3c7;
          color: #d97706;
        }

        &.mix {
          background-color: #e0f2fe;
          color: #0284c7;
        }
      }
    }

    .goods-info {
      padding: 24rpx;
      display: flex;
      flex-direction: column;

      .goods-title {
        font-size: 26rpx;
        font-weight: bold;
        color: #1e293b;
        height: 76rpx;
        line-height: 1.4;
        margin-bottom: 20rpx;
      }

      .price-row {
        display: flex;
        align-items: baseline;
        margin-bottom: 8rpx;

        .price-points {
          font-family: "Georgia", serif;
          font-size: 32rpx;
          font-weight: bold;
          color: #d97706;

          .p-unit {
            font-size: 18rpx;
            margin-left: 2rpx;
            font-weight: normal;
          }
        }

        .price-cash {
          font-size: 22rpx;
          font-weight: bold;
          color: #d97706;
          margin-left: 4rpx;
        }
      }

      .sales-row {
        .sales-text {
          font-size: 20rpx;
          color: #94a3b8;
        }
      }
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 300rpx;

    .empty-text {
      font-size: 28rpx;
      color: #94a3b8;
      margin-top: 24rpx;
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