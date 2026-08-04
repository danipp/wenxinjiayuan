<template>
  <view class="shop-detail-container">
    <!-- 1. 顶部店铺荣誉头图 -->
    <view class="shop-hero-banner">
      <image
        class="banner-bg"
        :src="shopInfo.coverImage || shopInfo.logo"
        mode="aspectFill"
      ></image>
      <view class="banner-overlay"></view>
      <view class="shop-header-info">
        <view class="title-row">
          <text class="shop-name">{{ shopInfo.name }}</text>
          <text class="special-badge">特惠合作店</text>
        </view>
        <view class="stats-row">
          <text class="stat-text"
            >月销量 {{ shopInfo.monthlySales || 0 }} 件</text
          >
          <text class="divider">|</text>
          <text class="stat-text">评分 {{ ratingText }}</text>
          <text class="divider">|</text>
          <text class="stat-text">粉丝 {{ shopInfo.fansCount || 0 }}</text>
        </view>
      </view>
    </view>

    <view class="scroll-body-content">
      <!-- 2. 店铺地址 -->
      <view class="address-card" @click="handleOpenMap">
        <view class="address-left">
          <u-icon name="map-fill" color="#ff4d4f" size="32rpx"></u-icon>
          <text class="address-text text-ellipsis">{{
            shopInfo.address || "暂无地址"
          }}</text>
        </view>
        <u-icon name="arrow-right" color="#cbd5e1" size="24rpx"></u-icon>
      </view>

      <!-- 3. 优惠券 -->
      <view class="coupon-section" v-if="couponList.length > 0">
        <text class="section-title">店铺代金券</text>
        <scroll-view scroll-x class="coupon-scroll" :show-scrollbar="false">
          <view class="coupon-row">
            <view
              v-for="(c, cIdx) in couponList"
              :key="cIdx"
              class="coupon-card"
              :class="{ 'coupon-claimed': c.claimed }"
              @click="handleClaimCoupon(c, cIdx)"
            >
              <view class="coupon-left">
                <text class="symbol">￥</text>
                <text class="money">{{ c.money }}</text>
              </view>
              <view class="coupon-right">
                <text class="ctitle">{{ c.title }}</text>
                <text class="btn">{{ c.claimed ? "已领取" : "一键领" }}</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 4. 特惠服务项目 -->
      <view class="services-list-section" v-if="serviceItems.length > 0">
        <view class="section-header">
          <text class="section-title">特惠服务项目</text>
          <text class="section-count">共 {{ serviceItems.length }} 件</text>
        </view>
        <view class="services-list">
          <view
            v-for="item in serviceItems"
            :key="item.goodsId"
            class="service-item-row"
            @click="handleServiceBuy(item)"
          >
            <image
              class="s-cover"
              :src="item.coverImage"
              mode="aspectFill"
            ></image>
            <view class="s-right-info">
              <text class="s-title text-ellipsis-2">{{ item.title }}</text>
              <view class="s-price-row">
                <text class="s-price"
                  >¥{{ item.cashPrice || item.originalPrice || 0 }}</text
                >
                <button class="s-buy-btn">抢购</button>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 5. 顾客好评 -->
      <view class="review-section" v-if="reviews.length > 0">
        <view class="section-header">
          <text class="section-title">顾客好评</text>
          <view class="section-actions" @click="goToAllReviews">
            <text class="more-link" v-if="reviewCount > 2"
              >查看全部 {{ reviewCount }} 条</text
            >
            <u-icon name="arrow-right" color="#94a3b8" size="12"></u-icon>
          </view>
        </view>
        <view class="review-list">
          <view
            v-for="(r, rIdx) in reviews.slice(0, 2)"
            :key="rIdx"
            class="review-card"
          >
            <view class="review-header">
              <u-avatar
                class="r-avatar"
                :src="r.userAvatar"
                size="54rpx"
                mode="aspectFill"
              ></u-avatar>
              <view class="r-user-info">
                <text class="r-username">{{ r.userName }}</text>
                <text class="r-time">{{ formatTime(r.createTime) }}</text>
              </view>
              <text class="r-stars">{{ starsText(r.rating) }}</text>
            </view>
            <text class="review-text">{{ r.content }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 6. 底部固定工具栏 -->
    <view class="footer-action-bar">
      <view class="footer-share-wrapper">
        <view class="share-icon-circle">
          <u-icon name="share" color="#3b6de6" size="24"></u-icon>
        </view>
        <text class="footer-label">分享给好友</text>
        <button class="transparent-share-btn" open-type="share"></button>
      </view>

      <view class="footer-btn-review" @click="goWriteReview">
        <text>写评价</text>
      </view>

      <button class="footer-phone-btn" @click="handleCall">
        <u-icon name="phone-fill" color="#ffffff" size="24"></u-icon>
        <text>拨打电话</text>
      </button>
    </view>

    <!-- 全部评价弹窗 -->
    <AllReviewsPopup
      :show.sync="showAllReviews"
      :shopId="shopId"
      :totalCount="reviewCount"
    />
  </view>
</template>

<script>
import AllReviewsPopup from './components/AllReviewsPopup.vue';
import { shopDetail, claimCoupon } from "@/spages/api/special.js";

export default {
  components: { AllReviewsPopup },
  data() {
    return {
      shopId: null,
      loading: false,
      shopInfo: {},
      couponList: [],
      serviceItems: [],
      reviews: [],
      reviewCount: 0,
      showAllReviews: false,
      communityId: "",
    };
  },
  computed: {
    ratingText() {
      const r = this.shopInfo.rating;
      return r != null ? Number(r).toFixed(1) : "0.0";
    },
  },
  onLoad(options) {
    this.shopId = options.id;
    const community = uni.getStorageSync("selected_community");
    if (community && community.communityId) {
      this.communityId = community.communityId;
    }
  },
  onShow() {
    if (this.shopId) {
      this.fetchDetail();
    }
  },
  onShareAppMessage() {
    return {
      title: this.shopInfo.name || "特惠店铺",
      path: `/spages/special/shop/index?id=${this.shopId}`,
    };
  },
  methods: {
    async fetchDetail() {
      this.loading = true;
      try {
        const res = await shopDetail(this.shopId);
        if (res.code === "00000" && res.data) {
          const d = res.data;
          this.shopInfo = d.shopInfo || {};
          this.couponList = d.couponList || [];
          this.serviceItems = d.serviceItems || [];
          this.reviews = d.reviews || [];
          this.reviewCount = d.reviewCount || 0;
        } else {
          uni.showToast({ title: res.msg || "加载失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },

    // 领取优惠券
    async handleClaimCoupon(c, idx) {
      if (c.claimed) return;
      try {
        const res = await claimCoupon(c.couponId);
        if (res.code === "00000") {
          this.couponList[idx].claimed = true;
          uni.showToast({ title: "领取成功！", icon: "success" });
        } else {
          uni.showToast({ title: res.msg || "领取失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "领取失败", icon: "none" });
      }
    },

    handleOpenMap() {
      const { latitude, longitude, name, address } = this.shopInfo;
      if (latitude && longitude) {
        uni.openLocation({
          latitude,
          longitude,
          name: name || "",
          address: address || "",
        });
      }
    },

    handleCall() {
      const phone = this.shopInfo.phone;
      if (phone) {
        uni.makePhoneCall({ phoneNumber: phone });
      } else {
        uni.showToast({ title: "暂无联系电话", icon: "none" });
      }
    },

    handleServiceBuy(item) {
      uni.showModal({
        title: "抢购确认",
        content: `确定以优惠价 ¥${
          item.cashPrice || item.originalPrice || 0
        } 抢购"${item.title}"服务吗？`,
        confirmColor: "#ff4d4f",
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({ title: "订单创建中..." });
            setTimeout(() => {
              uni.hideLoading();
              uni.showModal({
                title: "抢购成功！🎉",
                content: "您的服务券码已发送，请凭核销凭证在服务上门时出示。",
                showCancel: false,
              });
            }, 600);
          }
        },
      });
    },

    // 写评价
    goWriteReview() {
      const name = encodeURIComponent(this.shopInfo.name || "");
      uni.navigateTo({
        url: `/spages/special/shop/comments?id=${this.shopId}&name=${name}`,
      });
    },

    // 查看全部评价
    goToAllReviews() {
      this.showAllReviews = true;
    },

    // 星级文字
    starsText(rating) {
      const n = rating || 5;
      return "⭐️".repeat(Math.min(n, 5));
    },

    formatTime(str) {
      if (!str) return "";
      const d = new Date(str.replace(/-/g, "/"));
      if (isNaN(d.getTime())) return str;
      const pad = (n) => String(n).padStart(2, "0");
      return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(
        d.getHours()
      )}:${pad(d.getMinutes())}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.shop-detail-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding-bottom: calc(110rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;

  .shop-hero-banner {
    width: 100%;
    height: 280rpx;
    position: relative;
    overflow: hidden;

    .banner-bg {
      width: 100%;
      height: 100%;
    }

    .banner-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(
        180deg,
        rgba(0, 0, 0, 0) 0%,
        rgba(0, 0, 0, 0.7) 100%
      );
    }

    .shop-header-info {
      position: absolute;
      left: 32rpx;
      bottom: 24rpx;
      right: 32rpx;
      display: flex;
      flex-direction: column;

      .title-row {
        display: flex;
        align-items: center;
        gap: 12rpx;
        margin-bottom: 8rpx;

        .shop-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #ffffff;
        }

        .special-badge {
          font-size: 18rpx;
          font-weight: bold;
          color: #ffffff;
          background-color: #ff4d4f;
          padding: 2rpx 8rpx;
          border-radius: 4rpx;
        }
      }

      .stats-row {
        display: flex;
        align-items: center;
        gap: 12rpx;

        .stat-text {
          font-size: 20rpx;
          color: rgba(255, 255, 255, 0.85);
        }

        .divider {
          font-size: 18rpx;
          color: rgba(255, 255, 255, 0.5);
        }
      }
    }
  }

  .scroll-body-content {
    padding: 24rpx;
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }

  /* 通用栏目标题 */
  .section-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #1a202c;
    margin-bottom: 16rpx;
    display: block;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;

    .section-title {
      margin-bottom: 0;
    }

    .section-count {
      font-size: 22rpx;
      color: #94a3b8;
    }

    .section-actions {
      display: flex;
      align-items: center;
      gap: 4rpx;

      .more-link {
        font-size: 22rpx;
        color: #94a3b8;
      }
    }
  }

  /* 地址栏 */
  .address-card {
    background-color: #ffffff;
    border-radius: 16rpx;
    padding: 24rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.01);

    .address-left {
      display: flex;
      align-items: center;
      flex: 1;
      overflow: hidden;

      .address-text {
        font-size: 26rpx;
        color: #4a5568;
        font-weight: bold;
        margin-left: 12rpx;
      }
    }
  }

  /* 优惠券 */
  .coupon-section {
    display: flex;
    flex-direction: column;

    .coupon-scroll {
      width: 100%;
      white-space: nowrap;
    }

    .coupon-row {
      display: flex;
      gap: 16rpx;
    }

    .coupon-card {
      display: inline-flex;
      background: linear-gradient(135deg, #fff1f0 0%, #ffe8e6 100%);
      border: 1rpx solid #ffa39e;
      border-radius: 12rpx;
      overflow: hidden;
      box-sizing: border-box;
      height: 88rpx;
      width: 240rpx;
      position: relative;

      .coupon-left {
        width: 80rpx;
        height: 100%;
        background-color: #ff4d4f;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #ffffff;

        .symbol {
          font-size: 16rpx;
        }
        .money {
          font-family: "Georgia", serif;
          font-size: 32rpx;
          font-weight: bold;
        }
      }

      .coupon-right {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        padding-left: 12rpx;

        .ctitle {
          font-size: 16rpx;
          color: #ff4d4f;
          font-weight: bold;
          margin-bottom: 2rpx;
        }

        .btn {
          font-size: 14rpx;
          color: #ff4d4f;
          font-weight: 800;
        }
      }

      &.coupon-claimed {
        background: #f1f5f9;
        border-color: #cbd5e1;

        .coupon-left {
          background-color: #94a3b8;
        }
        .coupon-right {
          .ctitle {
            color: #94a3b8;
          }
          .btn {
            color: #94a3b8;
          }
        }
      }
    }
  }

  /* 特惠项目列表 */
  .services-list-section {
    .services-list {
      display: flex;
      flex-direction: column;
      gap: 16rpx;
    }

    .service-item-row {
      background-color: #ffffff;
      border-radius: 16rpx;
      padding: 16rpx;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.015);
      display: flex;
      gap: 16rpx;

      .s-cover {
        width: 110rpx;
        height: 110rpx;
        border-radius: 8rpx;
        background-color: #f1f5f9;
      }

      .s-right-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .s-title {
          font-size: 24rpx;
          font-weight: bold;
          color: #1e293b;
          line-height: 1.4;
        }

        .s-price-row {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .s-price {
            font-family: "Georgia", serif;
            font-size: 28rpx;
            font-weight: bold;
            color: #ff4d4f;
          }

          .s-buy-btn {
            margin: 0;
            height: 44rpx;
            line-height: 44rpx;
            background-color: #ff4d4f;
            color: #ffffff;
            font-size: 20rpx;
            font-weight: bold;
            border-radius: 22rpx;
            padding: 0 20rpx;

            &::after {
              border: none;
            }
          }
        }
      }
    }
  }

  /* 顾客好评 */
  .review-section {
    .review-list {
      display: flex;
      flex-direction: column;
      gap: 16rpx;
    }

    .review-card {
      background-color: #ffffff;
      border-radius: 16rpx;
      padding: 24rpx;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.01);

      .review-header {
        display: flex;
        align-items: center;
        margin-bottom: 12rpx;

        .r-avatar {
          width: 54rpx;
          height: 54rpx;
          border-radius: 50%;
          margin-right: 12rpx;
        }

        .r-user-info {
          display: flex;
          flex-direction: column;
          flex: 1;
          margin-left: 10rpx;
          .r-username {
            font-size: 22rpx;
            font-weight: bold;
            color: #334155;
          }

          .r-time {
            font-size: 16rpx;
            color: #94a3b8;
            margin-top: 2rpx;
          }
        }

        .r-stars {
          font-size: 16rpx;
        }
      }

      .review-text {
        font-size: 22rpx;
        color: #475569;
        line-height: 1.5;
      }
    }
  }

  /* 底部固定操作栏 */
  .footer-action-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.03);
    padding: 20rpx 32rpx calc(20rpx + env(safe-area-inset-bottom)) 32rpx;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    gap: 16rpx;
    z-index: 100;

    .footer-share-wrapper {
      flex: 2;
      height: 88rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8rpx;
      background-color: #f0f5ff;
      border: 2rpx solid #d6e4ff;
      border-radius: 44rpx;
      position: relative;

      .share-icon-circle {
        width: 44rpx;
        height: 44rpx;
        border-radius: 50%;
        background-color: #d6e4ff;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }

      .footer-label {
        font-size: 24rpx;
        font-weight: bold;
        color: #3b6de6;
      }

      .transparent-share-btn {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        opacity: 0;
        z-index: 10;
      }

      &:active {
        background-color: #d6e4ff;
      }
    }

    .footer-btn-review {
      flex: 1;
      height: 88rpx;
      line-height: 88rpx;
      background-color: #fff1f0;
      color: #ff4d4f;
      border-radius: 44rpx;
      font-size: 24rpx;
      font-weight: bold;
      text-align: center;
      border: 2rpx solid #ffa39e;
    }

    .footer-phone-btn {
      flex: 2;
      height: 88rpx;
      line-height: 88rpx;
      background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%);
      color: #ffffff;
      font-size: 24rpx;
      font-weight: bold;
      border-radius: 44rpx;
      box-shadow: 0 4rpx 16rpx rgba(255, 77, 79, 0.25);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8rpx;

      &::after {
        border: none;
      }
      &:active {
        opacity: 0.9;
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
