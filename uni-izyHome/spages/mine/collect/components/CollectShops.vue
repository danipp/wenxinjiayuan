<template>
  <view class="component-wrapper">
    <scroll-view scroll-y class="list-scroll-view" @scrolltolower="loadMore">
      <view class="shop-list" v-if="shopList.length > 0">
        <!-- 店铺卡片结构 -->
        <view
          v-for="shop in shopList"
          :key="shop.id"
          class="shop-card"
          @click="goShopHome(shop.id)"
        >
          <!-- 头像与名称 -->
          <view class="card-left">
            <image
              class="shop-avatar"
              :src="shop.avatar"
              mode="aspectFill"
            ></image>
            <view class="shop-info">
              <view class="title-row">
                <text class="shop-name text-ellipsis">{{ shop.name }}</text>
                <text class="volunteer-tag">爱心店主</text>
              </view>
              <view class="stats-row">
                <text class="stat-text">粉丝 {{ shop.fans }}</text>
                <text class="divider">|</text>
                <text class="stat-text">月销 {{ shop.sales }} 件</text>
              </view>
            </view>
          </view>

          <!-- 右侧：高保真胶囊跳转按钮 -->
          <view class="card-right">
            <button class="enter-btn">进入店铺</button>
          </view>
        </view>
      </view>

      <!-- 空白页 -->
      <view class="empty-state" v-else>
        <u-icon name="empty-address" color="#cbd5e1" size="128rpx"></u-icon>
        <text class="empty-text">暂无收藏店铺记录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      // 模拟用户收藏的店
      shopList: [
        {
          id: 1001,
          name: "石头的小店",
          avatar: "https://cdn.uviewui.com/uview/album/1.jpg",
          fans: "1,420",
          sales: 580,
        },
        {
          id: 1002,
          name: "秉治的公益小铺",
          avatar: "https://cdn.uviewui.com/uview/album/2.jpg",
          fans: "960",
          sales: 320,
        },
      ],
    };
  },
  methods: {
    goShopHome(id) {
      uni.navigateTo({
        url: `/spages/store/shop/index?id=${id}`,
      });
    },
    loadMore() {
      console.log("加载更多店铺收藏...");
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

  .shop-list {
    padding: 28rpx;
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }

  /* 精致店铺卡片 */
  .shop-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.015);
    display: flex;
    align-items: center;
    justify-content: space-between;
    border: 2rpx solid #edf2f7;

    &:active {
      background-color: #fafbfc;
    }

    .card-left {
      display: flex;
      align-items: center;
      flex: 1;
      overflow: hidden;

      .shop-avatar {
        width: 96rpx;
        height: 96rpx;
        border-radius: 50%;
        background-color: #f1f5f9;
        margin-right: 24rpx;
        border: 2rpx solid #e2e8f0;
      }

      .shop-info {
        display: flex;
        flex-direction: column;
        flex: 1;
        overflow: hidden;

        .title-row {
          display: flex;
          align-items: center;
          gap: 12rpx;
          margin-bottom: 12rpx;

          .shop-name {
            font-size: 30rpx;
            font-weight: bold;
            color: #1e293b;
            max-width: 220rpx;
          }

          .volunteer-tag {
            font-size: 18rpx;
            font-weight: bold;
            color: #2b5c9c;
            background-color: #eff6ff;
            padding: 4rpx 12rpx;
            border-radius: 8rpx;
            white-space: nowrap;
          }
        }

        .stats-row {
          display: flex;
          align-items: center;
          gap: 12rpx;

          .stat-text {
            font-size: 22rpx;
            color: #64748b;
          }

          .divider {
            font-size: 20rpx;
            color: #cbd5e1;
          }
        }
      }
    }

    /* 右侧：进入店铺胶囊按钮 */
    .card-right {
      .enter-btn {
        margin: 0;
        height: 56rpx;
        line-height: 56rpx;
        font-size: 22rpx;
        font-weight: bold;
        background-color: #ffffff;
        color: #07c160;
        border: 2rpx solid #07c160;
        border-radius: 28rpx;
        padding: 0 24rpx;

        &::after {
          border: none;
        }

        &:active {
          background-color: #e8f9f0;
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

  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>