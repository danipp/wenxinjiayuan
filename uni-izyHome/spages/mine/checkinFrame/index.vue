<template>
  <view class="frame-page">
    <view class="page-header">
      <view class="header-left">
        <text class="page-title">购买打卡相框</text>
        <text class="page-desc">适用于社区活动与志愿服务记录</text>
      </view>
      <view class="order-entry" @click="goOrderPage">
        <u-icon name="order" color="#07c160" size="36rpx"></u-icon>
        <text>订单</text>
      </view>
    </view>

    <scroll-view
      scroll-y
      class="frame-scroll"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view v-if="frameList.length > 0" class="frame-list">
        <view v-for="item in frameList" :key="item.id" class="frame-card">
          <image
            class="frame-image"
            :src="item.image"
            mode="aspectFill"
          ></image>
          <view class="frame-info">
            <view class="title-row">
              <text class="frame-name">{{ item.name }}</text>
              <text class="stock-tag">库存{{ item.stock }}</text>
            </view>
            <text class="frame-desc">{{ item.desc }}</text>
            <view class="meta-row">
              <text class="meta-text">{{ item.size }}</text>
              <text class="dot"></text>
              <text class="meta-text">{{ item.scene }}</text>
            </view>
            <view class="price-row">
              <view class="price-box">
                <text class="symbol">¥</text>
                <text class="price">{{ item.price }}</text>
              </view>
              <button class="buy-btn" @click="openFrameDetail(item)">
                购买
              </button>
            </view>
          </view>
        </view>

        <view class="load-status">
          <view v-if="loading" class="loading-line">
            <u-loading-icon size="36rpx"></u-loading-icon>
            <text>加载中...</text>
          </view>
          <text v-else-if="finished">没有更多相框了</text>
          <text v-else>上拉加载更多</text>
        </view>
      </view>

      <view v-else-if="loading" class="loading-state">
        <view class="state-icon"><u-loading-icon></u-loading-icon></view>
        <text class="state-title">相框加载中...</text>
      </view>

      <view v-else class="empty-state">
        <view class="state-icon">🖼️</view>
        <text class="state-title">暂无可购买相框</text>
        <text class="state-desc">请稍后刷新看看</text>
      </view>
    </scroll-view>

    <FrameDetailPopup
      :show="showFrameDetail"
      :frameInfo="currentFrame"
      @close="showFrameDetail = false"
      @pay="handlePay"
    />
  </view>
</template>

<script>
import FrameDetailPopup from "./components/FrameDetailPopup.vue";

export default {
  components: {
    FrameDetailPopup,
  },
  data() {
    return {
      page: 1,
      pageSize: 6,
      loading: true,
      finished: false,
      isRefreshing: false,
      showFrameDetail: false,
      currentFrame: {},
      frameList: [],
      mockFrames: [
        {
          id: 1,
          frameNo: "FRAME-NFC-001",
          name: "社区标准打卡相框",
          image: "/static/frames/frame1.png",
          price: "68.00",
          stock: 28,
          size: "6寸",
          scene: "社区活动",
          delivery: "社区配送",
          desc: "适合社区活动室、志愿者服务站使用的 NFC 打卡相框。",
          features: ["NFC碰一碰", "快速打卡", "活动记录", "轻量安装"],
        },
        {
          id: 2,
          frameNo: "FRAME-NFC-002",
          name: "长者关怀打卡相框",
          image: "/static/frames/frame2.png",
          price: "88.00",
          stock: 16,
          size: "7寸",
          scene: "长者探访",
          delivery: "社区配送",
          desc: "用于长者探访、上门关怀等服务场景，方便留存服务记录。",
          features: ["探访记录", "NFC识别", "信息同步", "温馨外观"],
        },
      ],
    };
  },
  onLoad() {
    uni.setNavigationBarTitle({
      title: "购买打卡相框",
    });
    this.getList();
  },
  methods: {
    onRefresh() {
      this.isRefreshing = true;
      this.page = 1;
      this.finished = false;
      this.frameList = [];
      this.getList(true);
    },
    getList(isRefresh = false) {
      // 模拟接口请求，可替换为实际接口：page、pageSize
      this.loading = true;
      setTimeout(() => {
        const start = (this.page - 1) * this.pageSize;
        const nextList = this.mockFrames.slice(start, start + this.pageSize);

        if (this.page === 1) {
          this.frameList = nextList;
        } else {
          this.frameList = this.frameList.concat(nextList);
        }

        this.page += 1;
        this.loading = false;
        this.finished = this.frameList.length >= this.mockFrames.length;

        if (isRefresh) {
          this.isRefreshing = false;
          uni.showToast({ title: "刷新成功", icon: "none" });
        }
      }, 900);
    },
    loadMore() {
      if (this.loading || this.finished) return;
      this.getList();
    },
    goOrderPage() {
      uni.navigateTo({
        url: "/spages/mine/checkinFrame/order",
      });
    },
    openFrameDetail(item) {
      this.currentFrame = item;
      this.showFrameDetail = true;
    },
    handlePay(frameInfo) {
      uni.showLoading({ title: "拉起支付中..." });

      // 模拟拉起支付，后续替换为 uni.requestPayment 即可
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({ title: "支付成功", icon: "success" });
        this.showFrameDetail = false;
        console.log("购买打卡相框：", frameInfo);
      }, 1000);
    },
  },
};
</script>

<style lang="scss" scoped>
.frame-page {
  min-height: 100vh;
  background-color: #f7f9fb;
  box-sizing: border-box;

  .page-header {
    height: 192rpx;
    padding: 32rpx;
    box-sizing: border-box;
    background: linear-gradient(135deg, #f0faf5 0%, #f7f9fb 100%);
    display: flex;
    align-items: center;

    .header-left {
      display: flex;
      flex-direction: column;
      flex: 1;
      min-width: 0;

      .page-title {
        font-size: 40rpx;
        font-weight: 800;
        color: #1a202c;
      }

      .page-desc {
        font-size: 24rpx;
        color: #718096;
        line-height: 1.5;
        margin-top: 16rpx;
      }
    }

    .order-entry {
      width: 124rpx;
      height: 108rpx;
      border-radius: 32rpx;
      background-color: #ffffff;
      border: 2rpx solid #d1fae5;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      margin-left: 24rpx;
      color: #07c160;
      font-size: 22rpx;
      font-weight: bold;
      box-shadow: 0 4rpx 16rpx rgba(7, 193, 96, 0.06);

      text {
        margin-top: 6rpx;
      }
    }
  }

  .frame-scroll {
    height: calc(100vh - 192rpx);
  }

  .frame-list {
    padding: 0 32rpx 32rpx;
    box-sizing: border-box;

    .frame-card {
      background-color: #ffffff;
      border-radius: 28rpx;
      padding: 28rpx;
      display: flex;
      margin-bottom: 24rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      .frame-image {
        width: 188rpx;
        height: 216rpx;
        border-radius: 24rpx;
        background-color: #edf2f7;
        margin-right: 24rpx;
        flex-shrink: 0;
      }

      .frame-info {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;

        .title-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 14rpx;

          .frame-name {
            flex: 1;
            font-size: 30rpx;
            font-weight: bold;
            color: #1a202c;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .stock-tag {
            font-size: 20rpx;
            color: #07c160;
            background-color: #f0faf5;
            border-radius: 20rpx;
            padding: 4rpx 14rpx;
            margin-left: 16rpx;
            flex-shrink: 0;
          }
        }

        .frame-desc {
          font-size: 24rpx;
          color: #718096;
          line-height: 1.45;
          margin-bottom: 16rpx;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .meta-row {
          display: flex;
          align-items: center;
          margin-bottom: 16rpx;

          .meta-text {
            font-size: 22rpx;
            color: #94a3b8;
          }

          .dot {
            width: 6rpx;
            height: 6rpx;
            border-radius: 50%;
            background-color: #cbd5e1;
            margin: 0 12rpx;
          }
        }

        .price-row {
          margin-top: auto;
          display: flex;
          align-items: center;
          justify-content: space-between;

          .price-box {
            display: flex;
            align-items: baseline;
            color: #ff8a00;

            .symbol {
              font-size: 24rpx;
              font-weight: bold;
            }

            .price {
              font-size: 40rpx;
              font-weight: 800;
              margin-left: 2rpx;
            }
          }

          .buy-btn {
            width: 144rpx;
            height: 64rpx;
            line-height: 64rpx;
            border-radius: 32rpx;
            background-color: #07c160;
            color: #ffffff;
            font-size: 26rpx;
            font-weight: bold;
            padding: 0;
            margin: 0;

            &::after {
              border: none;
            }
          }
        }
      }
    }

    .load-status {
      height: 84rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24rpx;
      color: #a0aec0;

      .loading-line {
        display: flex;
        align-items: center;
        gap: 12rpx;
      }
    }
  }

  .loading-state,
  .empty-state {
    height: calc(100vh - 192rpx);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .state-icon {
      width: 128rpx;
      height: 128rpx;
      border-radius: 40rpx;
      background-color: #f0faf5;
      color: #07c160;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 60rpx;
      margin-bottom: 28rpx;
    }

    .state-title {
      font-size: 28rpx;
      color: #718096;
      font-weight: bold;
    }

    .state-desc {
      font-size: 22rpx;
      color: #cbd5e1;
      margin-top: 12rpx;
    }
  }
}
</style>