<template>
  <view class="detail-container">
    <!-- <u-sticky>
      <u-notice-bar :text="goods.noticeText" mode="closable"></u-notice-bar>
    </u-sticky> -->
    <!-- 1. 商品大图轮播 -->
    <swiper
      class="goods-swiper"
      indicator-dots
      indicator-active-color="#e2ab5b"
    >
      <swiper-item v-for="(img, idx) in goods.carouselImages" :key="idx">
        <image class="swiper-image" :src="img" mode="aspectFill"></image>
      </swiper-item>
    </swiper>
    <view class="charity-donation-badge">
      <view class="badge-left">
        <view class="heart-pulse-icon">
          <u-icon name="heart-fill" color="#ff4d4f" size="20"></u-icon>
        </view>
      </view>
      <view class="badge-right">
        <view class="badge-title">好物相伴 · 善意同行</view>
        <view class="badge-desc">
          本店将从每笔成交订单中拿出
          <text class="highlight">1%</text> 资金投身公益
        </view>
      </view>
    </view>
    <view class="info-content-box">
      <!-- 2. 商品定价与标题 -->
      <view class="price-title-card">
        <!-- 积分兑换模式 -->
        <view v-if="isPointsExchange" class="price-box">
          <text class="points-num">{{ goods.pointsPrice }}</text>
          <text class="points-unit">积分</text>
          <text v-if="goods.cashPrice" class="cash-num"
            >+ ¥{{ goods.cashPrice }}</text
          >
        </view>
        <!-- 金额购买模式 -->
        <view v-else class="price-box">
          <text class="cash-num-big"
            >¥{{ goods.cashPrice || goods.price }}</text
          >
        </view>
        <text class="goods-title">{{ goods.title }}</text>
        <view class="postage-row">
          <text class="post-item">方式：自提</text>
          <text class="post-item">发货地：社区志愿者之家服务站</text>
        </view>
      </view>

      <!-- 3. 兑换预算看板 -->
      <view v-if="isPointsExchange" class="points-budget-card">
        <view class="budget-top">
          <u-icon name="integral-fill" color="#d97706" size="18"></u-icon>
          <text class="budget-title">兑换预算对比</text>
        </view>
        <view class="budget-body">
          <view class="budget-item">
            <text class="val">{{ userPoints }}</text>
            <text class="label">当前可用积分</text>
          </view>
          <view class="budget-arrow">→</view>
          <view class="budget-item">
            <text class="val highlight">{{
              userPoints - goods.pointsPrice
            }}</text>
            <text class="label">兑换后剩余积分</text>
          </view>
        </view>
      </view>

      <!-- 4. 商品评价模块 -->
      <view class="comments-section-card">
        <view class="card-header">
          <text class="card-title">宝贝评价 ({{ comments.length }})</text>
          <view
            v-if="comments.length > 2"
            class="more-link"
            @click="goToAllComments"
          >
            <text>查看更多</text>
            <u-icon name="arrow-right" color="#94a3b8" size="12"></u-icon>
          </view>
        </view>

        <view class="comments-list">
          <view
            v-for="(item, index) in comments.slice(0, 2)"
            :key="index"
            class="comment-row-item"
          >
            <view class="user-info-row">
              <image
                class="u-avatar"
                :src="item.avatar"
                mode="aspectFill"
              ></image>
              <text class="u-name">{{ item.name }}</text>
              <view class="stars">⭐️⭐️⭐️⭐️⭐️</view>
            </view>
            <text class="comment-text-content">{{ item.content }}</text>
            <view
              v-if="item.images && item.images.length"
              class="comment-images"
            >
              <image
                v-for="(img, imgIdx) in item.images"
                :key="imgIdx"
                class="comment-img"
                :src="img"
                mode="aspectFill"
                @click="previewImage(img, item.images)"
              ></image>
            </view>
          </view>
        </view>
      </view>

      <!-- 5. 商品详情介绍 -->
      <view class="description-card">
        <text class="card-title">商品详情</text>
        <view class="specs-list">
          <view class="spec-row">
            <text class="spec-label">材质</text>
            <text class="spec-value">{{ goods.specs.material }}</text>
          </view>
          <view class="spec-row">
            <text class="spec-label">规格</text>
            <text class="spec-value">{{ goods.specs.size }}</text>
          </view>
        </view>
        <u-line color="#edf2f7" style="margin: 32rpx 0"></u-line>
        <text class="desc-content">{{ goods.description }}</text>
      </view>
    </view>

    <!-- 6. 底部固定栏 -->
    <view class="footer-bar">
      <view class="footer-icon-btn" @click="goToShop">
        <u-icon name="home" color="#64748b" size="20"></u-icon>
        <text class="icon-label">店铺</text>
      </view>

      <view class="footer-icon-btn" @click="toggleFavorite">
        <u-icon
          :name="isFavorited ? 'star-fill' : 'star'"
          :color="isFavorited ? '#e2ab5b' : '#64748b'"
          size="20"
        ></u-icon>
        <text class="icon-label" :class="{ 'gold-text': isFavorited }">
          {{ isFavorited ? "已收藏" : "收藏" }}
        </text>
      </view>

      <!-- C. 主按钮 -->
      <!-- 纯积分(goodsType=1): 立即兑换 -->
      <button
        v-if="goods.goodsType === 1"
        class="buy-action-btn"
        @click="handleRedeem"
      >
        立即兑换
      </button>
      <!-- 混合(goodsType=3): 积分+现金 一个按钮 -->
      <button
        v-if="goods.goodsType === 3"
        class="buy-action-btn pay-btn"
        @click="handlePurchase"
      >
        {{ goods.pointsPrice }}积分 + ¥{{ goods.cashPrice || goods.price }}
        立即兑换
      </button>
      <!-- 纯现金(goodsType=2): 立即购买 -->
      <button v-else class="buy-action-btn pay-btn" @click="handlePurchase">
        立即购买 ¥{{ goods.cashPrice || goods.price }}
      </button>
    </view>
  </view>
</template>

<script>
import { detail2 } from "@/spages/api/goods";
import { create } from "@/spages/api/order";
import { collect, isCollected } from "@/spages/api/goods";

export default {
  data() {
    return {
      goodsId: null,
      userPoints: 850,
      isFavorited: false,
      isPointsExchange: true,
      goods: {
        id: null,
        title: "加载中...",
        pointsPrice: 0,
        cashPrice: 0,
        price: 0,
        carouselImages: [],
        specs: {},
        description: "",
        shopId: null,
        goodsType: 1,
      },
      comments: [],
    };
  },
  onLoad(options) {
    uni.setNavigationBarTitle({ title: "商品详情" });
    if (options.id) {
      this.goodsId = Number(options.id);
      this.fetchDetail();
      this.checkCollected();
    }
  },
  methods: {
    async fetchDetail() {
      try {
        const res = await detail2(this.goodsId);
        const d = res.data || {};
        let specs = {};
        if (d.specs) {
          try {
            specs = typeof d.specs === "string" ? JSON.parse(d.specs) : d.specs;
          } catch (e) {
            specs = {};
          }
        }
        this.goods = {
          id: d.goodsId || d.id,
          title: d.title || "",
          pointsPrice: d.pointsPrice || 0,
          cashPrice: d.cashPrice || 0,
          price: d.cashPrice || d.originalPrice || 0,
          carouselImages:
            d.carouselImages && d.carouselImages.length > 0
              ? d.carouselImages
              : d.coverImage
              ? [d.coverImage]
              : [],
          specs: specs,
          description: d.description || "",
          shopId: d.shopId,
          goodsType: d.goodsType || 1,
        };
        this.isPointsExchange = d.goodsType === 1 || d.goodsType === 3;
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      }
    },
    async checkCollected() {
      try {
        const res = await isCollected({
          targetId: this.goodsId,
          targetType: 1,
        });
        this.isFavorited = !!res.data;
      } catch (e) {
        /* ignore */
      }
    },
    goToShop() {
      if (this.goods.shopId) {
        uni.navigateTo({
          url: `/spages/store/shop/index?shopId=${this.goods.shopId}`,
        });
      }
    },
    async toggleFavorite() {
      try {
        await collect({ targetId: this.goodsId, targetType: 1 });
        this.isFavorited = !this.isFavorited;
        uni.showToast({
          title: this.isFavorited ? "商品收藏成功" : "已取消收藏",
          icon: "none",
        });
      } catch (e) {
        uni.showToast({ title: "操作失败", icon: "none" });
      }
    },
    goToAllComments() {
      uni.navigateTo({
        url: `/spages/store/goods/comments?id=${this.goods.id}`,
      });
    },
    // 积分兑换 (payType=1)
    async handleRedeem() {
      if (this.userPoints < this.goods.pointsPrice) {
        uni.showToast({ title: "积分余额不足！", icon: "none" });
        return;
      }
      const cashText = this.goods.cashPrice
        ? ` + ¥${this.goods.cashPrice}`
        : "";
      uni.showModal({
        title: "确认兑换",
        content: `确定消耗 ${this.goods.pointsPrice} 积分${cashText}兑换该商品吗？`,
        confirmColor: "#e2ab5b",
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({ title: "兑换处理中..." });
            try {
              const orderRes = await create({
                goodsId: this.goodsId,
                count: 1,
                payType: this.goods.goodsType,
              });
              uni.hideLoading();
              this.userPoints -= this.goods.pointsPrice;
              const d = orderRes.data || {};
              // 混合商品如果有现金部分，调起支付
              if (this.goods.cashPrice > 0 && d.payParams) {
                uni.requestPayment({
                  ...d.payParams,
                  success: () => {
                    uni.showToast({ title: "兑换成功", icon: "success" });
                  },
                  fail: () => {
                    uni.showToast({
                      title: "支付取消，订单已生成",
                      icon: "none",
                    });
                  },
                });
              } else {
                uni.showModal({
                  title: "兑换成功！",
                  content: `订单号：${d.orderNum || ""}\n核销码：${
                    d.redeemCode || ""
                  }\n凭此核销，请至社区中心核销换取物资。`,
                  showCancel: false,
                });
              }
            } catch (e) {
              uni.hideLoading();
              uni.showToast({ title: "兑换失败，请重试", icon: "none" });
            }
          }
        },
      });
    },
    // 现金购买 (payType=2)
    async handlePurchase() {
      const price = this.goods.cashPrice || this.goods.price;
      uni.showModal({
        title: "确认购买",
        content: `确定以 ¥${price} 购买该商品吗？`,
        confirmColor: "#e2ab5b",
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({ title: "支付处理中..." });
            try {
              const orderRes = await create({
                goodsId: this.goodsId,
                count: 1,
                payType: this.goods.goodsType,
              });
              uni.hideLoading();
              const d = orderRes.data || {};
              if (d.payParams) {
                uni.requestPayment({
                  ...d.payParams,
                  package: d.payParams.packageValue,
                  success: () => {
                    uni.showToast({ title: "支付成功", icon: "success" });
                  },
                  fail: () => {
                    uni.showToast({ title: "支付取消", icon: "none" });
                  },
                });
              } else {
                uni.showModal({
                  title: "下单成功！",
                  content: `订单号：${
                    d.orderNum || ""
                  }，请完成支付后领取商品。`,
                  showCancel: false,
                });
              }
            } catch (e) {
              uni.hideLoading();
              uni.showToast({ title: "下单失败，请重试", icon: "none" });
            }
          }
        },
      });
    },
    previewImage(current, urls) {
      uni.previewImage({ current, urls });
    },
  },
};
</script>

<style lang="scss" scoped>
.detail-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;

  .goods-swiper {
    width: 100%;
    height: 600rpx;
    background-color: #ffffff;
    .swiper-image {
      width: 100%;
      height: 100%;
    }
  }
  .charity-donation-badge {
    display: flex;
    align-items: center;
    background: linear-gradient(135deg, #fff9f0 0%, #fff0f2 100%);
    border: 3rpx solid #ffd0d4;
    padding: 28rpx 32rpx;
    box-shadow: 0 8rpx 28rpx rgba(255, 77, 79, 0.06);
    box-sizing: border-box;

    .badge-left {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 24rpx;

      .heart-pulse-icon {
        animation: heartBeat 2s infinite ease-in-out;
      }
    }

    .badge-right {
      display: flex;
      flex-direction: column;
      flex: 1;

      .badge-title {
        font-weight: 800;
        color: #b71c1c;
        letter-spacing: 2rpx;
        margin-bottom: 8rpx;
      }

      .badge-desc {
        font-size: 28rpx;
        color: #5d6d7e;
        line-height: 1.5;

        .highlight {
          font-family: "Georgia", serif;
          font-weight: 900;
          color: #07c160;
          margin: 0 8rpx;
        }
      }
    }
  }

  @keyframes heartBeat {
    0% {
      transform: scale(1);
    }
    25% {
      transform: scale(1.15);
    }
    50% {
      transform: scale(1);
    }
    75% {
      transform: scale(1.15);
    }
    100% {
      transform: scale(1);
    }
  }
  .info-content-box {
    padding: 32rpx;
    display: flex;
    flex-direction: column;
    gap: 14rpx;
  }

  .price-title-card {
    background-color: #ffffff;
    border-radius: 28rpx;
    padding: 40rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

    .price-box {
      display: flex;
      align-items: baseline;
      color: #b75e12;
      margin-bottom: 24rpx;
      .points-num {
        font-family: "Georgia", serif;
        font-size: 56rpx;
        font-weight: 900;
      }
      .points-unit {
        font-size: 24rpx;
        margin-left: 4rpx;
        font-weight: bold;
      }
      .cash-num {
        font-family: "Georgia", serif;
        font-size: 40rpx;
        font-weight: bold;
        margin-left: 8rpx;
      }
      .cash-num-big {
        font-family: "Georgia", serif;
        font-size: 56rpx;
        font-weight: 900;
        color: #e53935;
      }
    }

    .goods-title {
      font-size: 34rpx;
      font-weight: bold;
      color: #1a202c;
      line-height: 1.5;
      margin-bottom: 24rpx;
      display: block;
    }

    .postage-row {
      display: flex;
      justify-content: space-between;
      border-top: 2rpx solid #f1f5f9;
      padding-top: 24rpx;
      .post-item {
        font-size: 22rpx;
        color: #94a3b8;
      }
    }
  }

  .points-budget-card {
    background-color: #fffbeb;
    border: 2rpx solid #fde68a;
    border-radius: 28rpx;
    padding: 32rpx;

    .budget-top {
      display: flex;
      align-items: center;
      gap: 12rpx;
      margin-bottom: 28rpx;
      .budget-title {
        font-size: 26rpx;
        font-weight: bold;
        color: #b45c12;
      }
    }

    .budget-body {
      display: flex;
      align-items: center;
      justify-content: space-around;
      .budget-arrow {
        font-size: 44rpx;
        color: #f59e0b;
        font-weight: bold;
      }
      .budget-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        .val {
          font-family: "Georgia", serif;
          font-size: 44rpx;
          font-weight: bold;
          color: #475569;
          margin-bottom: 8rpx;
          &.highlight {
            color: #07c160;
          }
        }
        .label {
          font-size: 20rpx;
          color: #94a3b8;
        }
      }
    }
  }

  .comments-section-card {
    background-color: #ffffff;
    border-radius: 28rpx;
    padding: 36rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 32rpx;

      .card-title {
        font-size: 30rpx;
        font-weight: bold;
        color: #1a202c;
      }

      .more-link {
        display: flex;
        align-items: center;
        gap: 4rpx;
        font-size: 24rpx;
        color: #94a3b8;
      }
    }

    .comments-list {
      display: flex;
      flex-direction: column;
      gap: 28rpx;
    }

    .comment-row-item {
      display: flex;
      flex-direction: column;
      border-bottom: 2rpx dashed #f1f5f9;
      padding-bottom: 24rpx;

      &:last-child {
        border-bottom: none;
        padding-bottom: 0;
      }

      .user-info-row {
        display: flex;
        align-items: center;
        margin-bottom: 12rpx;

        .u-avatar {
          width: 48rpx;
          height: 48rpx;
          border-radius: 50%;
          margin-right: 16rpx;
        }

        .u-name {
          font-size: 26rpx;
          font-weight: bold;
          color: #475569;
          margin-right: 16rpx;
        }

        .stars {
          font-size: 16rpx;
        }
      }

      .comment-text-content {
        font-size: 26rpx;
        color: #334155;
        line-height: 1.5;
      }

      .comment-images {
        display: flex;
        flex-wrap: wrap;
        gap: 12rpx;
        margin-top: 16rpx;

        .comment-img {
          width: 140rpx;
          height: 140rpx;
          border-radius: 12rpx;
          background-color: #f1f5f9;
        }
      }
    }
  }

  .description-card {
    background-color: #ffffff;
    border-radius: 28rpx;
    padding: 40rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

    .card-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #1a202c;
      margin-bottom: 28rpx;
      display: block;
    }
    .specs-list {
      display: flex;
      flex-direction: column;
      gap: 16rpx;
      .spec-row {
        display: flex;
        font-size: 26rpx;
        .spec-label {
          width: 120rpx;
          color: #94a3b8;
        }
        .spec-value {
          color: #475569;
          font-weight: bold;
        }
      }
    }
    .desc-content {
      font-size: 27rpx;
      color: #475569;
      line-height: 1.6;
      display: block;
    }
  }

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
    gap: 20rpx;
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

    .buy-action-btn {
      flex: 1;
      height: 96rpx;
      line-height: 96rpx;
      background: linear-gradient(135deg, #f59e0b 0%, #b75e12 100%);
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
      border-radius: 48rpx;
      box-shadow: 0 8rpx 24rpx rgba(183, 94, 18, 0.25);

      &::after {
        border: none;
      }
      &:active {
        opacity: 0.9;
      }

      &.pay-btn {
        background: linear-gradient(135deg, #e53935 0%, #b71c1c 100%);
        box-shadow: 0 8rpx 24rpx rgba(229, 57, 53, 0.25);
      }
    }
  }
}
</style>
