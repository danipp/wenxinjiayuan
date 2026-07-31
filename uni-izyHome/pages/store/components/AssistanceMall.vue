<template>
  <view class="container">
    <!-- 顶部轮播图 -->
    <view class="banner-box">
      <u-swiper
        :list="bannerList"
        keyName="image"
        height="380rpx"
        circular
        indicator
        indicatorActiveColor="#FF4242"
        radius="16rpx"
      ></u-swiper>
    </view>

    <!-- 广播通知栏 -->
    <view class="notice-box">
      <u-notice-bar
        :text="noticeText"
        icon="volume"
        color="#333333"
        bgColor="#FFFFFF"
        fontSize="26rpx"
      ></u-notice-bar>
    </view>

    <!-- 4个统计卡片网格 -->
    <view class="stats-grid">
      <view class="stat-card bg-green">
        <text class="title">爱心联盟商家</text>
        <view class="num-wrap">
          <text class="num">74</text>
          <text class="unit">家</text>
        </view>
      </view>
      <view class="stat-card bg-blue">
        <text class="title">社区慈善超市</text>
        <view class="num-wrap">
          <text class="num">11</text>
          <text class="unit">家</text>
        </view>
      </view>
      <view class="stat-card bg-light-blue">
        <text class="title">爱心帮扶企业</text>
        <view class="num-wrap">
          <text class="num">9</text>
          <text class="unit">家</text>
        </view>
      </view>
      <view class="stat-card bg-teal">
        <text class="title">爱心物资总数</text>
        <view class="num-wrap">
          <text class="num">24180</text>
          <text class="unit">件</text>
        </view>
      </view>
    </view>

    <!-- 金刚区 -->
    <view class="kingkong-box">
      <view
        class="kingkong-item"
        v-for="(item, index) in kingkongList"
        :key="index"
        @click="handleKingkongClick"
      >
        <image class="icon" :src="item.icon" mode="aspectFit"></image>
        <text class="text">{{ item.name }}</text>
      </view>
    </view>

    <!-- Tabs 选项卡分类 -->
    <view class="tabs-box">
      <view
        class="tab-item"
        :class="{ active: currentTab === 0 }"
        @click="currentTab = 0"
      >
        <text class="tab-text">爱心物资</text>
        <view class="line" v-if="currentTab === 0"></view>
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 1 }"
        @click="currentTab = 1"
      >
        <text class="tab-text">爱心YI餐</text>
        <view class="line" v-if="currentTab === 1"></view>
      </view>
    </view>

    <!-- 物资列表 -->
    <scroll-view scroll-y class="goods-list-scroll" @scrolltolower="loadMore">
      <view class="goods-list" v-if="goodsList.length">
        <view
          class="goods-card"
          v-for="item in goodsList"
          :key="item.id"
          @click="goToDetail(item.id)"
        >
          <view class="goods-img-wrap">
            <image
              class="goods-img"
              :src="item.image"
              mode="aspectFill"
            ></image>
            <view class="time-tag">{{ item.endTime }}结束</view>
          </view>

          <view class="goods-info">
            <view class="title-wrap">
              <text class="type-tag">捐赠</text>
              <text class="goods-title u-line-1">{{ item.title }}</text>
            </view>

            <view class="price-wrap">
              <text class="points">{{ item.points }}</text>
              <text class="points-unit">积分</text>
              <text class="price-orig">原价 ¥{{ item.originalPrice }}</text>
            </view>

            <view class="company-wrap u-line-1">
              <image
                class="company-logo"
                :src="item.companyLogo"
                mode="aspectFill"
              ></image>
              <text class="company-name">{{ item.companyName }}</text>
            </view>

            <view class="progress-box">
              <view class="progress-info">
                <text>已申领 {{ item.applied }} / 总计 {{ item.total }}</text>
                <text>{{ calculatePercent(item.applied, item.total) }}%</text>
              </view>
              <u-line-progress
                :percentage="calculatePercent(item.applied, item.total)"
                activeColor="#FF5505"
                height="10rpx"
                :showText="false"
              ></u-line-progress>
            </view>

            <view class="btn-wrap">
              <button
                v-if="item.status === 0"
                class="claim-btn btn-primary"
                @click.stop="handleClaim(item)"
              >
                立即申领
              </button>
              <button
                v-else-if="item.status === 1"
                class="claim-btn btn-warning"
                @click.stop="goToDetail(item.id)"
              >
                申领中
              </button>
              <button
                v-else-if="item.status === 2"
                class="claim-btn btn-disabled"
                disabled
              >
                已领完
              </button>
            </view>
          </view>
        </view>

        <view class="load-more-tips">
          <text v-if="loading">加载中...</text>
          <text v-else-if="noMore && goodsList.length > 0"
            >—— 已经是最后一页了 ——</text
          >
        </view>
      </view>
      <view v-else>
        <u-empty text="暂无商品" mode="search"></u-empty>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { page1 } from "@/api/goods";

export default {
  data() {
    return {
      bannerList: [
        {
          image:
            "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=400&q=80",
        },
        {
          image:
            "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=800&auto=format&fit=crop&q=80",
        },
      ],
      noticeText: "素***8申领了广昌鲜莲冷冻软糯香甜纯天然无添加的榴莲",
      kingkongList: [
        {
          name: "捐赠申请",
          icon: "https://img.icons8.com/color/96/clipboard.png",
        },
        {
          name: "申请帮扶",
          icon: "https://img.icons8.com/color/96/open-box.png",
        },
        {
          name: "爱心企业",
          icon: "https://img.icons8.com/color/96/city-buildings.png",
        },
        {
          name: "捐赠排行",
          icon: "https://img.icons8.com/color/96/trophy.png",
        },
        {
          name: "爱心帮扶",
          icon: "https://img.icons8.com/color/96/handshake.png",
        },
      ],
      currentTab: 0,
      loading: false,
      noMore: false,
      pageNum: 1,
      pageSize: 10,
      goodsList: [],
    };
  },
  mounted() {
    this.fetchGoods();
  },
  methods: {
    async fetchGoods() {
      if (this.loading || this.noMore) return;
      this.loading = true;
      try {
        const res = await page1({
          pageNumber: this.pageNum,
          pageSize: this.pageSize,
          goodsType: 3,
          scene: "消费帮扶",
          status: 1,
        });
        const pageData = res.data || {};
        const list = pageData.content || [];
        const isLast =
          pageData.last !== undefined
            ? pageData.last
            : list.length < this.pageSize;

        const mapped = list.map((item) => ({
          id: item.goodsId || item.id,
          title: item.title || "",
          points: item.pointsPrice || 0,
          originalPrice: item.originalPrice || item.cashPrice || 0,
          image: item.coverImage || "",
          companyLogo: "",
          companyName: item.delivery || "",
          applied: item.salesCount || 0,
          total: item.stock || 100,
          endTime: "长期",
          status: item.stock > 0 ? 0 : 2,
        }));

        this.goodsList =
          this.pageNum === 1 ? mapped : [...this.goodsList, ...mapped];
        this.noMore = isLast;
        if (!isLast) this.pageNum++;
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },
    loadMore() {
      if (!this.loading && !this.noMore) {
        this.fetchGoods();
      }
    },
    handleKingkongClick() {
      uni.showToast({ title: "正在开发中", icon: "none" });
    },
    calculatePercent(applied, total) {
      if (!total || total === 0) return 0;
      let percent = Math.floor((applied / total) * 100);
      return percent > 100 ? 100 : percent;
    },
    goToDetail(id) {
      uni.navigateTo({ url: `/spages/store/detail?id=${id}` });
    },
    handleClaim(item) {
      uni.showModal({
        title: "提示",
        content: "确定要申领" + item.title + "吗？",
        success: (res) => {
          if (res.confirm) {
            item.status = 1;
            item.applied += 1;
            uni.showToast({ title: "提交成功，请等待审核", icon: "success" });
          }
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f6f7f9;
  padding: 20rpx 24rpx 40rpx 24rpx;
}

.banner-box {
  border-radius: 16rpx;
  overflow: hidden;
}

.notice-box {
  margin-top: 16rpx;
  border-radius: 12rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.02);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
  margin-top: 20rpx;

  .stat-card {
    height: 140rpx;
    border-radius: 16rpx;
    padding: 24rpx;
    color: #ffffff;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .title {
      font-size: 26rpx;
      opacity: 0.9;
    }

    .num-wrap {
      margin-top: 8rpx;
      display: flex;
      align-items: baseline;

      .num {
        font-size: 40rpx;
        font-weight: bold;
        margin-right: 6rpx;
      }

      .unit {
        font-size: 22rpx;
        opacity: 0.85;
      }
    }
  }

  .bg-green {
    background: linear-gradient(135deg, #32d398, #16ba80);
  }
  .bg-blue {
    background: linear-gradient(135deg, #42b6ff, #1c90ff);
  }
  .bg-light-blue {
    background: linear-gradient(135deg, #3bb3ff, #258bf7);
  }
  .bg-teal {
    background: linear-gradient(135deg, #10cca0, #23b6bf);
  }
}

.kingkong-box {
  margin-top: 24rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 28rpx 10rpx;
  display: flex;
  justify-content: space-around;
  align-items: center;

  .kingkong-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 20%;

    .icon {
      width: 72rpx;
      height: 72rpx;
      margin-bottom: 12rpx;
    }

    .text {
      font-size: 24rpx;
      color: #333333;
      white-space: nowrap;
    }
  }
}

.tabs-box {
  display: flex;
  align-items: center;
  margin-top: 30rpx;
  margin-bottom: 20rpx;

  .tab-item {
    position: relative;
    margin-right: 40rpx;
    padding-bottom: 8rpx;

    .tab-text {
      font-size: 32rpx;
      color: #666666;
      font-weight: 500;
    }

    &.active {
      .tab-text {
        font-size: 36rpx;
        color: #111111;
        font-weight: bold;
      }

      .line {
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 40rpx;
        height: 6rpx;
        background-color: #ff4242;
        border-radius: 4rpx;
      }
    }
  }
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;

  .goods-card {
    background-color: #ffffff;
    border-radius: 20rpx;
    padding: 20rpx;
    display: flex;

    .goods-img-wrap {
      position: relative;
      width: 210rpx;
      height: 210rpx;
      border-radius: 12rpx;
      overflow: hidden;
      flex-shrink: 0;

      .goods-img {
        width: 100%;
        height: 100%;
      }

      .time-tag {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: rgba(0, 0, 0, 0.7);
        color: #ffffff;
        font-size: 20rpx;
        text-align: center;
        padding: 4rpx 0;
      }
    }

    .goods-info {
      flex: 1;
      margin-left: 20rpx;
      display: flex;
      flex-direction: column;
      justify-content: space-between;

      .title-wrap {
        display: flex;
        align-items: center;

        .type-tag {
          background-color: #ffebe8;
          color: #ff4242;
          font-size: 20rpx;
          padding: 2rpx 10rpx;
          border-radius: 6rpx;
          margin-right: 10rpx;
          border: 1rpx solid #ffbebe;
          flex-shrink: 0;
        }

        .goods-title {
          font-size: 28rpx;
          font-weight: bold;
          color: #222222;
        }
      }

      .price-wrap {
        margin-top: 8rpx;
        display: flex;
        align-items: baseline;

        .points {
          font-size: 32rpx;
          font-weight: bold;
          color: #ff4242;
        }

        .points-unit {
          font-size: 22rpx;
          color: #ff4242;
          margin-left: 4rpx;
        }

        .price-orig {
          font-size: 22rpx;
          color: #999999;
          text-decoration: line-through;
          margin-left: 12rpx;
        }
      }

      .company-wrap {
        display: flex;
        align-items: center;
        margin-top: 6rpx;

        .company-logo {
          width: 30rpx;
          height: 30rpx;
          border-radius: 50%;
          margin-right: 8rpx;
        }

        .company-name {
          font-size: 22rpx;
          color: #666666;
        }
      }

      .progress-box {
        margin-top: 10rpx;

        .progress-info {
          display: flex;
          justify-content: space-between;
          font-size: 20rpx;
          color: #999999;
          margin-bottom: 6rpx;
        }
      }

      .btn-wrap {
        display: flex;
        justify-content: flex-end;
        margin-top: 12rpx;

        .claim-btn {
          margin: 0;
          height: 52rpx;
          line-height: 52rpx;
          padding: 0 28rpx;
          font-size: 22rpx;
          border-radius: 26rpx;
          border: none;

          &::after {
            border: none;
          }
        }

        .btn-primary {
          background: linear-gradient(90deg, #ff6034, #ee0a24);
          color: #ffffff;
        }

        .btn-warning {
          background: #fff0e6;
          color: #ff5500;
          border: 1rpx solid #ffbb96;
        }

        .btn-disabled {
          background-color: #f5f5f5;
          color: #cccccc;
        }
      }
    }
  }
}

.load-more-tips {
  text-align: center;
  padding: 24rpx 0 40rpx 0;
  font-size: 22rpx;
  color: #cbd5e1;
}
</style>
