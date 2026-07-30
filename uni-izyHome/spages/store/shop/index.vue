<template>
  <view class="shop-container">
    <!-- 1. 顶部店铺信息高光卡片 -->
    <view class="shop-header-card">
      <view class="shop-main-info">
        <image
          class="shop-avatar"
          src="https://cdn.uviewui.com/uview/album/1.jpg"
          mode="aspectFill"
        ></image>
        <view class="shop-text-info">
          <view class="title-row">
            <text class="shop-name">石头的小店</text>
            <text class="volunteer-badge">爱心店主</text>
          </view>
          <text class="shop-desc text-ellipsis"
            >财厅前社区官方合作公益小铺</text
          >
        </view>
      </view>

      <!-- 数据指标看板 -->
      <view class="shop-stats-row">
        <view class="stat-item">
          <text class="num">248</text>
          <text class="label">关注收藏</text>
        </view>
        <view class="divider"></view>
        <view class="stat-item">
          <text class="num">1,420</text>
          <text class="label">粉丝</text>
        </view>
        <view class="divider"></view>
        <view class="stat-item">
          <text class="num">580</text>
          <text class="label">月销量</text>
        </view>
      </view>
    </view>

    <!-- 2. 本店商品列表区 -->
    <view class="shop-goods-section">
      <text class="section-title">本店商品 (4)</text>

      <view class="goods-grid">
        <view
          v-for="item in shopGoods"
          :key="item.id"
          class="goods-card"
          @click="goGoodsDetail(item.id)"
        >
          <image
            class="goods-cover"
            :src="item.image"
            mode="aspectFill"
          ></image>
          <view class="goods-details">
            <text class="goods-title text-ellipsis-2">{{ item.title }}</text>
            <text class="goods-price"
              >{{ item.pointsPrice }}<text class="unit">积分</text></text
            >
          </view>
        </view>
      </view>
    </view>

    <!-- 3. 底部操作栏：收藏、分享、打电话 -->
    <view class="footer-bar">
      <!-- 收藏店铺 -->
      <view class="footer-icon-btn" @click="toggleShopFavorite">
        <u-icon
          :name="isShopFavorited ? 'star-fill' : 'star'"
          :color="isShopFavorited ? '#e2ab5b' : '#64748b'"
          size="20"
        ></u-icon>
        <text class="icon-label" :class="{ 'gold-text': isShopFavorited }">
          {{ isShopFavorited ? "已收藏" : "收藏" }}
        </text>
      </view>

      <!-- 微信透明一键转发分享 -->
      <view class="footer-icon-btn relative-box">
        <u-icon name="share" color="#64748b" size="20"></u-icon>
        <text class="icon-label">分享</text>
        <button class="transparent-share-btn" open-type="share"></button>
      </view>

      <!-- 一键拨打电话按钮 -->
      <button class="call-action-btn" @click="handleCall">
        <u-icon
          name="phone"
          color="#ffffff"
          size="16"
          style="margin-right: 12rpx"
        ></u-icon>
        打电话
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      isShopFavorited: false,
      sellerPhone: "13800138000", // 绑定的联系电话
      shopGoods: [
        {
          id: 301,
          title: "志愿者定制高品质 304 不锈钢保温杯",
          pointsPrice: 120,
          image:
            "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=400&q=80",
        },
        {
          id: 302,
          title: "爱心家园帆布袋（加厚双肩环保袋）",
          pointsPrice: 50,
          image:
            "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=400&q=80",
        },
      ],
    };
  },
  onLoad() {
    uni.setNavigationBarTitle({ title: "店铺主页" });
  },
  methods: {
    toggleShopFavorite() {
      this.isShopFavorited = !this.isShopFavorited;
      uni.showToast({
        title: this.isShopFavorited ? "收藏店铺成功" : "已取消收藏店铺",
        icon: "none",
      });
    },
    // 调用真实物理拨号
    handleCall() {
      uni.makePhoneCall({
        phoneNumber: this.sellerPhone,
      });
    },
    goGoodsDetail(id) {
      uni.navigateTo({
        url: `/spages/store/detail?id=${id}`,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.shop-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx 32rpx calc(180rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;

  /* 顶部高光卡片 (天空青蓝色渐变) */
  .shop-header-card {
    background: linear-gradient(135deg, #cee5f2 0%, #adc6eb 100%);
    border-radius: 32rpx;
    padding: 48rpx 40rpx;
    box-shadow: 0 8rpx 28rpx rgba(173, 198, 235, 0.35);
    margin-bottom: 40rpx;

    .shop-main-info {
      display: flex;
      align-items: center;
      margin-bottom: 48rpx;

      .shop-avatar {
        width: 116rpx;
        height: 116rpx;
        border-radius: 50%;
        border: 4rpx solid #ffffff;
      }

      .shop-text-info {
        margin-left: 28rpx;
        flex: 1;

        .title-row {
          display: flex;
          align-items: center;
          gap: 16rpx;
          margin-bottom: 8rpx;

          .shop-name {
            font-size: 36rpx;
            font-weight: bold;
            color: #1a202c;
          }

          .volunteer-badge {
            font-size: 20rpx;
            font-weight: bold;
            color: #2b5c9c;
            background-color: rgba(255, 255, 255, 0.45);
            padding: 4rpx 12rpx;
            border-radius: 8rpx;
          }
        }

        .shop-desc {
          font-size: 24rpx;
          color: #4a5568;
        }
      }
    }

    .shop-stats-row {
      display: flex;
      align-items: center;

      .stat-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;

        .num {
          font-family: "Georgia", serif;
          font-size: 40rpx;
          font-weight: bold;
          color: #1a202c;
          margin-bottom: 4rpx;
        }

        .label {
          font-size: 22rpx;
          color: #4a5568;
        }
      }

      .divider {
        width: 2rpx;
        height: 48rpx;
        background-color: rgba(255, 255, 255, 0.3);
      }
    }
  }

  /* 本店商品 */
  .shop-goods-section {
    display: flex;
    flex-direction: column;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #1a202c;
      margin-bottom: 24rpx;
    }

    .goods-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 24rpx;
    }

    .goods-card {
      background-color: #ffffff;
      border-radius: 24rpx;
      overflow: hidden;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
      display: flex;
      flex-direction: column;

      .goods-cover {
        width: 100%;
        height: 240rpx;
        background-color: #f1f5f9;
      }

      .goods-details {
        padding: 20rpx;
        display: flex;
        flex-direction: column;

        .goods-title {
          font-size: 26rpx;
          font-weight: bold;
          color: #334155;
          height: 72rpx;
          line-height: 1.4;
          margin-bottom: 16rpx;
        }

        .goods-price {
          font-family: "Georgia", serif;
          font-size: 32rpx;
          font-weight: bold;
          color: #d97706;

          .unit {
            font-size: 20rpx;
            margin-left: 2rpx;
            font-weight: normal;
          }
        }
      }
    }
  }

  /* 底部固定操作栏 */
  .footer-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
    padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    gap: 40rpx;
    z-index: 100;

    .footer-icon-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;

      .icon-label {
        font-size: 20rpx;
        color: #64748b;
        margin-top: 8rpx;

        &.gold-text {
          color: #e2ab5b;
          font-weight: bold;
        }
      }
    }

    .relative-box {
      position: relative;
      .transparent-share-btn {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        opacity: 0;
        z-index: 10;
      }
    }

    .call-action-btn {
      flex: 1;
      height: 96rpx;
      line-height: 96rpx;
      background: linear-gradient(135deg, #10b981 0%, #07b160 100%);
      color: #ffffff;
      font-size: 30rpx;
      font-weight: bold;
      border-radius: 48rpx;
      box-shadow: 0 8rpx 24rpx rgba(7, 177, 96, 0.25);
      display: flex;
      align-items: center;
      justify-content: center;

      &::after {
        border: none;
      }
    }
  }

  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .text-ellipsis-2 {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }
}
</style>