<template>
  <view class="order-page">
    <view class="status-tabs">
      <view
        v-for="item in statusTabs"
        :key="item.value"
        class="tab-item"
        :class="{ active: currentStatus === item.value }"
        @click="switchStatus(item.value)"
      >
        <text>{{ item.label }}</text>
      </view>
    </view>

    <scroll-view
      scroll-y
      class="order-scroll"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view v-if="orderList.length > 0" class="order-list">
        <view
          v-for="item in orderList"
          :key="item.id"
          class="order-card"
          @click="openFrameDetail(item.frameInfo)"
        >
          <view class="order-header">
            <text class="order-no">订单号：{{ item.orderNo }}</text>
            <text class="status-text" :class="item.status">{{
              item.statusText
            }}</text>
          </view>

          <view class="goods-row">
            <image
              class="frame-image"
              :src="item.frameInfo.image"
              mode="aspectFill"
            ></image>
            <view class="goods-info">
              <view class="title-row">
                <text class="frame-name">{{ item.frameInfo.name }}</text>
                <text class="quantity">x{{ item.quantity }}</text>
              </view>
              <text class="frame-desc">{{ item.frameInfo.desc }}</text>
              <view class="meta-row">
                <text>{{ item.frameInfo.size }}</text>
                <text class="dot"></text>
                <text>{{ item.frameInfo.scene }}</text>
              </view>
              <view class="price-row">
                <text class="time">{{ item.createTime }}</text>
                <view class="amount">
                  <text class="amount-label">实付</text>
                  <text class="symbol">¥</text>
                  <text class="price">{{ item.payAmount }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view class="load-status">
          <view v-if="loading" class="loading-line">
            <u-loading-icon size="36rpx"></u-loading-icon>
            <text>加载中...</text>
          </view>
          <text v-else-if="finished">没有更多订单了</text>
          <text v-else>上拉加载更多</text>
        </view>
      </view>

      <view v-else-if="loading" class="loading-state">
        <view class="state-icon"><u-loading-icon></u-loading-icon></view>
        <text class="state-title">订单加载中...</text>
      </view>

      <view v-else class="empty-state">
        <view class="state-icon">📦</view>
        <text class="state-title">暂无订单</text>
        <text class="state-desc">购买打卡相框后，订单会展示在这里</text>
      </view>
    </scroll-view>

    <FrameDetailPopup
      :show="showFrameDetail"
      :frameInfo="currentFrame"
      :showFooter="false"
      @close="showFrameDetail = false"
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
      currentStatus: "all",
      page: 1,
      pageSize: 5,
      loading: true,
      finished: false,
      isRefreshing: false,
      showFrameDetail: false,
      currentFrame: {},
      orderList: [],
      statusTabs: [
        { label: "全部", value: "all" },
        { label: "待支付", value: "unpaid" },
        { label: "已支付", value: "paid" },
        { label: "已完成", value: "completed" },
      ],
      mockOrders: [
        {
          id: 1,
          orderNo: "CF202607070001",
          status: "paid",
          statusText: "已支付",
          quantity: 1,
          payAmount: "68.00",
          createTime: "2026-07-07 10:18",
          frameInfo: {
            frameNo: "FRAME-NFC-001",
            name: "社区标准打卡相框",
            image: "https://cdn.uviewui.com/uview/album/3.jpg",
            price: "68.00",
            stock: 28,
            size: "6寸",
            scene: "社区活动",
            delivery: "社区配送",
            desc: "适合社区活动室、志愿者服务站使用的 NFC 打卡相框。",
            features: ["NFC碰一碰", "快速打卡", "活动记录", "轻量安装"],
          },
        },
        {
          id: 2,
          orderNo: "CF202607060002",
          status: "unpaid",
          statusText: "待支付",
          quantity: 1,
          payAmount: "88.00",
          createTime: "2026-07-06 16:42",
          frameInfo: {
            frameNo: "FRAME-NFC-002",
            name: "长者关怀打卡相框",
            image: "https://cdn.uviewui.com/uview/album/4.jpg",
            price: "88.00",
            stock: 16,
            size: "7寸",
            scene: "长者探访",
            delivery: "社区配送",
            desc: "用于长者探访、上门关怀等服务场景，方便留存服务记录。",
            features: ["探访记录", "NFC识别", "信息同步", "温馨外观"],
          },
        },
        {
          id: 3,
          orderNo: "CF202607050003",
          status: "completed",
          statusText: "已完成",
          quantity: 2,
          payAmount: "196.00",
          createTime: "2026-07-05 11:30",
          frameInfo: {
            frameNo: "FRAME-NFC-003",
            name: "志愿服务打卡相框",
            image: "https://cdn.uviewui.com/uview/album/5.jpg",
            price: "98.00",
            stock: 12,
            size: "8寸",
            scene: "志愿服务",
            delivery: "快递配送",
            desc: "面向志愿服务点设计，适合高频打卡与服务签到。",
            features: ["高频使用", "签到记录", "防误触", "耐用材质"],
          },
        },
        {
          id: 4,
          orderNo: "CF202607040004",
          status: "paid",
          statusText: "已支付",
          quantity: 1,
          payAmount: "78.00",
          createTime: "2026-07-04 09:56",
          frameInfo: {
            frameNo: "FRAME-NFC-004",
            name: "邻里活动纪念相框",
            image: "https://cdn.uviewui.com/uview/album/6.jpg",
            price: "78.00",
            stock: 20,
            size: "6寸",
            scene: "邻里活动",
            delivery: "社区配送",
            desc: "适合社区聚会、邻里活动现场使用，兼具展示与打卡功能。",
            features: ["活动展示", "碰一碰打卡", "便捷摆放", "清新设计"],
          },
        },
        {
          id: 5,
          orderNo: "CF202607030005",
          status: "completed",
          statusText: "已完成",
          quantity: 1,
          payAmount: "86.00",
          createTime: "2026-07-03 14:20",
          frameInfo: {
            frameNo: "FRAME-NFC-005",
            name: "便民服务打卡相框",
            image: "https://cdn.uviewui.com/uview/album/7.jpg",
            price: "86.00",
            stock: 10,
            size: "7寸",
            scene: "便民服务",
            delivery: "快递配送",
            desc: "适合便民服务点、健康小屋等场景使用。",
            features: ["服务记录", "便民场景", "数据同步", "简易维护"],
          },
        },
        {
          id: 6,
          orderNo: "CF202607020006",
          status: "unpaid",
          statusText: "待支付",
          quantity: 1,
          payAmount: "108.00",
          createTime: "2026-07-02 19:08",
          frameInfo: {
            frameNo: "FRAME-NFC-006",
            name: "党群中心打卡相框",
            image: "https://cdn.uviewui.com/uview/album/8.jpg",
            price: "108.00",
            stock: 8,
            size: "8寸",
            scene: "党群中心",
            delivery: "快递配送",
            desc: "适用于党群服务中心、公共空间展示与打卡。",
            features: ["公共空间", "稳定识别", "大尺寸展示", "统一管理"],
          },
        },
      ],
    };
  },
  computed: {
    filteredOrders() {
      if (this.currentStatus === "all") return this.mockOrders;
      return this.mockOrders.filter(
        (item) => item.status === this.currentStatus
      );
    },
  },
  onLoad() {
    uni.setNavigationBarTitle({
      title: "相框订单",
    });
    this.getList();
  },
  methods: {
    switchStatus(status) {
      if (this.currentStatus === status) return;
      this.currentStatus = status;
      this.page = 1;
      this.finished = false;
      this.orderList = [];
      this.getList();
    },
    onRefresh() {
      this.isRefreshing = true;
      this.page = 1;
      this.finished = false;
      this.orderList = [];
      this.getList(true);
    },
    getList(isRefresh = false) {
      this.loading = true;
      setTimeout(() => {
        const start = (this.page - 1) * this.pageSize;
        const nextList = this.filteredOrders.slice(
          start,
          start + this.pageSize
        );

        if (this.page === 1) {
          this.orderList = nextList;
        } else {
          this.orderList = this.orderList.concat(nextList);
        }

        this.page += 1;
        this.loading = false;
        this.finished = this.orderList.length >= this.filteredOrders.length;

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
    openFrameDetail(frameInfo) {
      this.currentFrame = frameInfo;
      this.showFrameDetail = true;
    },
  },
};
</script>

<style lang="scss" scoped>
.order-page {
  min-height: 100vh;
  background-color: #f7f9fb;
  box-sizing: border-box;

  .status-tabs {
    height: 108rpx;
    padding: 20rpx 24rpx;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    gap: 16rpx;
    background-color: #f7f9fb;

    .tab-item {
      flex: 1;
      height: 68rpx;
      border-radius: 34rpx;
      background-color: #ffffff;
      color: #718096;
      font-size: 26rpx;
      font-weight: bold;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      &.active {
        background-color: #07c160;
        color: #ffffff;
        box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.18);
      }
    }
  }

  .order-scroll {
    height: calc(100vh - 108rpx);
  }

  .order-list {
    padding: 0 32rpx 32rpx;
    box-sizing: border-box;

    .order-card {
      background-color: #ffffff;
      border-radius: 28rpx;
      padding: 28rpx;
      margin-bottom: 24rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      .order-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        border-bottom: 1rpx solid #f1f5f9;
        padding-bottom: 20rpx;
        margin-bottom: 24rpx;

        .order-no {
          font-size: 24rpx;
          color: #718096;
        }

        .status-text {
          font-size: 24rpx;
          font-weight: bold;

          &.unpaid {
            color: #ff8a00;
          }

          &.paid,
          &.completed {
            color: #07c160;
          }
        }
      }

      .goods-row {
        display: flex;

        .frame-image {
          width: 168rpx;
          height: 184rpx;
          border-radius: 24rpx;
          background-color: #edf2f7;
          margin-right: 24rpx;
          flex-shrink: 0;
        }

        .goods-info {
          flex: 1;
          min-width: 0;
          display: flex;
          flex-direction: column;

          .title-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 12rpx;

            .frame-name {
              flex: 1;
              font-size: 30rpx;
              font-weight: bold;
              color: #1a202c;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .quantity {
              font-size: 24rpx;
              color: #94a3b8;
              margin-left: 16rpx;
            }
          }

          .frame-desc {
            font-size: 24rpx;
            color: #718096;
            line-height: 1.45;
            margin-bottom: 12rpx;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .meta-row {
            display: flex;
            align-items: center;
            font-size: 22rpx;
            color: #94a3b8;
            margin-bottom: 16rpx;

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

            .time {
              font-size: 22rpx;
              color: #a0aec0;
            }

            .amount {
              display: flex;
              align-items: baseline;
              color: #1a202c;

              .amount-label {
                font-size: 22rpx;
                color: #718096;
                margin-right: 8rpx;
              }

              .symbol {
                font-size: 24rpx;
                font-weight: bold;
                color: #ff8a00;
              }

              .price {
                font-size: 36rpx;
                font-weight: 800;
                color: #ff8a00;
              }
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
    height: calc(100vh - 108rpx);
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