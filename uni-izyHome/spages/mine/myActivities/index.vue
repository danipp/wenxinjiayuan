<template>
  <view class="activities-page">
    <!-- 双 Tab 切换栏 -->
    <view class="tabs-header">
      <view
        class="tab-item"
        :class="{ 'tab-item-active': activeTab === 0 }"
        @click="activeTab = 0"
      >
        我参与的
      </view>
      <view
        class="tab-item"
        :class="{ 'tab-item-active': activeTab === 1 }"
        @click="activeTab = 1"
      >
        我发布的
      </view>
    </view>

    <!-- 滚动列表 -->
    <scroll-view scroll-y class="list-scroll-view" @scrolltolower="loadMore">
      <view class="card-list" v-if="currentList.length > 0">
        <view v-for="item in currentList" :key="item.id" class="activity-card">
          <view class="card-title-row">
            <text class="card-title text-ellipsis">{{ item.title }}</text>
            <text class="status-badge" :class="item.status">{{
              item.statusText
            }}</text>
          </view>

          <view class="card-info">
            <text class="info-text">时间：{{ item.time }}</text>
            <view class="info-row">
              <text class="info-text text-ellipsis"
                >介绍：{{ item.intro }}</text
              >
              <view class="detail-link" @click="goDetail(item.id)">
                <text>详情</text>
                <u-icon
                  name="arrow-right"
                  color="#a0aec0"
                  size="22rpx"
                  style="margin-left: 4rpx"
                ></u-icon>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 缺省提示 -->
      <view v-else class="empty-state">
        <u-icon name="empty-history" color="#cbd5e1" size="128rpx"></u-icon>
        <text class="empty-text">暂无相关活动记录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      activeTab: 0, // 0:我参与的, 1:我发布的
      page: 1,
      // 模拟数据
      participatedList: [
        {
          id: 1,
          title: "中山一社区居民瓜子会",
          status: "ongoing",
          statusText: "进行中",
          time: "2026.07.01-2026.07.01",
          intro: "邀请大家嗑瓜子",
        },
      ],
      publishedList: [
        {
          id: 2,
          title: "同城周未桌游狼人杀交友社",
          status: "ongoing",
          statusText: "进行中",
          time: "2026.07.08-2026.07.08",
          intro: "逻辑推理，快乐交友",
        },
      ],
    };
  },
  computed: {
    currentList() {
      return this.activeTab === 0 ? this.participatedList : this.publishedList;
    },
  },
  methods: {
    loadMore() {
      console.log("加载更多活动...");
    },
    goDetail(id) {
      uni.navigateTo({
        url: `/spages/activity/detail?id=${id}`,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.activities-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f7f9fb;

  .tabs-header {
    height: 100rpx;
    background-color: #ffffff;
    display: flex;
    justify-content: space-around;
    align-items: center;
    border-bottom: 2rpx solid #edf2f7;

    .tab-item {
      font-size: 32rpx;
      color: #718096;
      padding: 20rpx 32rpx;
      position: relative;
      font-weight: bold;

      &.tab-item-active {
        color: #1a202c;
        &::after {
          content: "";
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 64rpx;
          height: 8rpx;
          background-color: #07c160;
          border-radius: 4rpx;
        }
      }
    }
  }

  .list-scroll-view {
    height: calc(100vh - 200rpx);
  }

  .card-list {
    padding: 32rpx;
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }

  .activity-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

    .card-title-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 24rpx;

      .card-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #1a202c;
        width: 75%;
      }

      .status-badge {
        font-size: 22rpx;
        font-weight: bold;
        padding: 6rpx 16rpx;
        border-radius: 8rpx;

        &.ongoing {
          background-color: #fff7e6;
          color: #d97706;
        }
      }
    }

    .card-info {
      display: flex;
      flex-direction: column;
      gap: 12rpx;

      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .info-text {
        font-size: 26rpx;
        color: #718096;
        flex: 1;
      }

      .detail-link {
        display: flex;
        align-items: center;
        font-size: 26rpx;
        color: #4a5568;
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
}
</style>