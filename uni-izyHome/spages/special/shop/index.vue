<template>
  <view class="shop-detail-container">
    <!-- 1. 顶部店铺荣誉头图 -->
    <view class="shop-hero-banner">
      <image class="banner-bg" :src="shopInfo.image" mode="aspectFill"></image>
      <view class="banner-overlay"></view>
      <view class="shop-header-info">
        <view class="title-row">
          <text class="shop-name">{{ shopInfo.title }}</text>
          <text class="special-badge">特惠合作店</text>
        </view>
        <view class="stats-row">
          <!-- <text class="stat-text">关注收藏 {{ shopInfo.collects }}</text>
          <text class="divider">|</text>
          <text class="stat-text">粉丝 {{ shopInfo.fans }}</text>
          <text class="divider">|</text> -->
          <text class="stat-text">月销量 {{ shopInfo.sales }} 件</text>
        </view>
      </view>
    </view>

    <view class="scroll-body-content">
      <!-- 2. 店铺地址一栏 (集成微信原生 openLocation 物理导航) -->
      <view class="address-card" @click="handleOpenMap">
        <view class="address-left">
          <u-icon name="map-fill" color="#ff4d4f" size="32rpx"></u-icon>
          <text class="address-text text-ellipsis">{{ shopInfo.address }}</text>
        </view>
        <u-icon name="arrow-right" color="#cbd5e1" size="24rpx"></u-icon>
      </view>

      <!-- 3. 本店特惠专享券 (点击即可一键秒领，高亮改变状态) -->
      <view class="coupon-section">
        <text class="section-title">店铺代金券</text>
        <scroll-view scroll-x class="coupon-scroll" :show-scrollbar="false">
          <view class="coupon-row">
            <view
              v-for="(c, cIdx) in couponList"
              :key="cIdx"
              class="coupon-card"
              :class="{ 'coupon-claimed': c.claimed }"
              @click="claimCoupon(cIdx)"
            >
              <view class="coupon-left">
                <text class="symbol">￥</text>
                <text class="money">{{ c.money }}</text>
              </view>
              <view class="coupon-right">
                <text class="title">{{ c.title }}</text>
                <text class="btn">{{ c.claimed ? "已领取" : "一键领" }}</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 4. 本店在售特惠服务项目列表 -->
      <view class="services-list-section" v-if="serviceItems.length">
        <text class="section-title">特惠服务项目</text>
        <view class="services-list">
          <view
            v-for="item in serviceItems"
            :key="item.id"
            class="service-item-row"
            @click="handleServiceBuy(item)"
          >
            <image class="s-cover" :src="item.image" mode="aspectFill"></image>
            <view class="s-right-info">
              <text class="s-title text-ellipsis-2">{{ item.title }}</text>
              <view class="s-price-row">
                <text class="s-price">￥{{ item.price }}</text>
                <button class="s-buy-btn">抢购</button>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 5. 用户好评模块 -->
      <view class="review-section">
        <text class="section-title">顾客好评</text>
        <view class="review-list">
          <view v-for="(r, rIdx) in reviews" :key="rIdx" class="review-card">
            <view class="review-header">
              <image class="r-avatar" :src="r.avatar" mode="aspectFill"></image>
              <view class="r-user-info">
                <text class="r-username">{{ r.name }}</text>
                <text class="r-time">{{ r.time }}</text>
              </view>
              <text class="r-stars">⭐️⭐️⭐️⭐️⭐️</text>
            </view>
            <text class="review-text">{{ r.content }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 6. 底部固定工具栏 -->
    <view class="footer-action-bar">
      <!-- 分享 -->
      <view class="footer-share-wrapper">
        <view class="share-icon-circle">
          <u-icon name="share" color="#3b6de6" size="24"></u-icon>
        </view>
        <text class="footer-label">分享给好友</text>
        <button class="transparent-share-btn" open-type="share"></button>
      </view>

      <!-- 打电话 -->
      <button class="footer-phone-btn" @click="handleCall">
        <u-icon name="phone-fill" color="#ffffff" size="24"></u-icon>
        <text>拨打电话</text>
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      shopInfo: {
        id: 401,
        title: "厨卫下水道 / 马桶深度清洁惠民小铺",
        image:
          "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=400&q=80",
        collects: 184,
        fans: "1,241",
        sales: 580,
        address: "广州市越秀区青菜岗43号启东楼",
        // 绑定真实的地图选点经纬度 (目澜社区坐标示例)
        latitude: 23.12908,
        longitude: 113.26436,
        phone: "13812345678",
      },
      // 优惠券
      couponList: [
        { money: 10, title: "满100元可用", claimed: false },
        { money: 5, title: "社区新人礼", claimed: false },
      ],
      // 店内特惠服务项目
      serviceItems: [
        // { id: 40101, title: '厨卫管道高压深度清洗、马桶/洗手池除垢除味套餐', price: 98, image: 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=400&q=80' },
        // { id: 40102, title: '管道堵塞应急疏通、强力管道防返味密封圈更换', price: 58, image: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=400&q=80' }
      ],
      // 真实评价
      reviews: [
        {
          name: "石头",
          avatar: "https://cdn.uviewui.com/uview/album/1.jpg",
          time: "1天前",
          content:
            "师傅上门速度非常快，管道清洗得很干净，还帮忙把周边的卫生擦了，服务态度太棒了。",
        },
        {
          name: "秉治",
          avatar: "https://cdn.uviewui.com/uview/album/2.jpg",
          time: "3天前",
          content:
            "价格真的很实惠，比外面随便找的便宜一大截，属于咱社区自己的实惠！",
        },
      ],
    };
  },
  onLoad() {},
  onShareAppMessage() {
    return {
      title: this.shopInfo.title,
      path: `/spages/special/shop/index?id=${this.shopInfo.id}`,
    };
  },
  methods: {
    // 微信地理位置原生唤醒导航
    handleOpenMap() {
      uni.openLocation({
        latitude: this.shopInfo.latitude,
        longitude: this.shopInfo.longitude,
        name: this.shopInfo.title,
        address: this.shopInfo.address,
      });
    },
    // 一键秒领优惠券
    claimCoupon(index) {
      const c = this.couponList[index];
      if (c.claimed) return;
      c.claimed = true;
      uni.showToast({ title: "代金券领取成功！", icon: "success" });
    },
    // 唤起物理拨号
    handleCall() {
      uni.makePhoneCall({
        phoneNumber: this.shopInfo.phone,
      });
    },
    // 点击抢购服务
    handleServiceBuy(item) {
      uni.showModal({
        title: "抢购确认",
        content: `确定以优惠价 ¥${item.price} 抢购“${item.title}”服务吗？`,
        confirmColor: "#ff4d4f",
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({ title: "订单创建中..." });
            setTimeout(() => {
              uni.hideLoading();
              uni.showModal({
                title: "抢购成功！🎉",
                content:
                  "您的服务券码已发送，请凭“我的 - 我的活动”中的核销凭证在服务上门时出示。",
                showCancel: false,
              });
            }, 600);
          }
        },
      });
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

  /* 1. 店铺大图 */
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

  /* 2. 地址栏 */
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

  /* 3. 优惠券 */
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

    /* 精美双耳圆角券 */
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
      cursor: pointer;

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

        .title {
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

      /* 已经领取置灰状态 */
      &.coupon-claimed {
        background: #f1f5f9;
        border-color: #cbd5e1;

        .coupon-left {
          background-color: #94a3b8;
        }
        .coupon-right {
          .title {
            color: #94a3b8;
          }
          .btn {
            color: #94a3b8;
          }
        }
      }
    }
  }

  /* 4. 特惠项目列表 */
  .services-list-section {
    display: flex;
    flex-direction: column;

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

  /* 5. 顾客好评 */
  .review-section {
    display: flex;
    flex-direction: column;

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

  /* 6. 底部固定操作栏 */
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
    gap: 20rpx;
    z-index: 100;

    /* 分享 - 大版图标按钮 */
    .footer-share-wrapper {
      flex: 1;
      height: 88rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12rpx;
      background-color: #f0f5ff;
      border: 2rpx solid #d6e4ff;
      border-radius: 44rpx;
      position: relative;
      cursor: pointer;

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
        font-size: 26rpx;
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

    /* 拨打电话 - 红色按钮 */
    .footer-phone-btn {
      flex: 1;
      height: 88rpx;
      line-height: 88rpx;
      background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%);
      color: #ffffff;
      font-size: 26rpx;
      font-weight: bold;
      border-radius: 44rpx;
      box-shadow: 0 4rpx 16rpx rgba(255, 77, 79, 0.25);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10rpx;

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