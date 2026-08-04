<template>
  <view class="activities-page">
    <!-- 双 Tab 切换栏 -->
    <view class="tabs-header">
      <view
        class="tab-item"
        :class="{ 'tab-item-active': activeTab === 0 }"
        @click="switchTab(0)"
        >我参与的</view
      >
      <view
        class="tab-item"
        :class="{ 'tab-item-active': activeTab === 1 }"
        @click="switchTab(1)"
        >我发布的</view
      >
    </view>
    <!-- 滚动列表 -->
    <scroll-view
      scroll-y
      class="list-scroll-view"
      style="height: calc(100vh - 100rpx)"
      @scrolltolower="loadMore"
    >
      <view class="card-list" v-if="currentList && currentList.length > 0">
        <view
          v-for="item in currentList"
          :key="item.activityId"
          class="activity-card"
          @click="goDetail(item.activityId)"
        >
          <view class="card-title-row">
            <text class="card-title text-ellipsis">{{ item.title }}</text>
            <text class="status-badge" :class="statusMap[item.status] || ''">{{
              item.statusText || statusLabel(item.status)
            }}</text>
          </view>

          <view class="card-body">
            <image
              v-if="item.coverImage"
              class="card-cover"
              :src="item.coverImage"
              mode="aspectFill"
            ></image>
            <view class="card-info">
              <text class="info-text"
                >时间：{{ formatRange(item.startTime, item.endTime) }}</text
              >
              <text class="info-text" v-if="item.location"
                >地点：{{ item.location }}</text
              >
              <text class="info-text"
                >参与：{{ item.participantCount || 0 }} 人</text
              >
              <view class="detail-link">
                <text>查看详情</text>
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

      <view v-if="loading" class="loading-tip">
        <u-loading-icon></u-loading-icon>
        <text>加载中...</text>
      </view>
      <view v-if="noMore && currentList.length > 0" class="no-more-tip"
        >已加载全部</view
      >

      <!-- 缺省提示 -->
      <view v-if="currentList.length === 0 && !loading" class="empty-state">
        <u-icon name="empty-history" color="#cbd5e1" size="128rpx"></u-icon>
        <text class="empty-text">暂无相关活动记录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { myActivities } from "@/spages/api/activity";

const STATUS_MAP = {
  1: "未开始",
  2: "进行中",
  3: "已结束",
};

export default {
  data() {
    return {
      activeTab: 0,
      joinedList: [],
      publishedList: [],
      joinedPage: 1,
      publishedPage: 1,
      joinedNoMore: false,
      publishedNoMore: false,
      pageSize: 10,
      loading: false,
      statusMap: {
        1: "status-upcoming",
        2: "status-ongoing",
        3: "status-ended",
      },
    };
  },
  computed: {
    currentList() {
      return this.activeTab === 0 ? this.joinedList : this.publishedList;
    },
    noMore() {
      return this.activeTab === 0 ? this.joinedNoMore : this.publishedNoMore;
    },
  },
  onLoad() {
    this.fetchList();
  },
  methods: {
    switchTab(idx) {
      if (this.activeTab === idx) return;
      this.activeTab = idx;
      // 首次进入某个 tab 若没有数据则加载
      if (this.currentList.length === 0) {
        this.fetchList();
      }
    },

    async fetchList(append = false) {
      if (this.loading) return;
      const isJoined = this.activeTab === 0;
      if (!append) {
        if (isJoined) {
          this.joinedPage = 1;
          this.joinedList = [];
          this.joinedNoMore = false;
        } else {
          this.publishedPage = 1;
          this.publishedList = [];
          this.publishedNoMore = false;
        }
      }
      if (this.noMore) return;

      this.loading = true;
      try {
        const pageNum = isJoined ? this.joinedPage : this.publishedPage;
        const res = await myActivities({
          pageNumber: pageNum,
          pageSize: this.pageSize,
          role: isJoined ? "joined" : "published",
        });
        if (res.code === "00000" && res.data) {
          const { content = [], last } = res.data;
          if (isJoined) {
            this.joinedList = append
              ? this.joinedList.concat(content)
              : content;
            this.joinedNoMore = last !== false;
            if (!last) this.joinedPage++;
          } else {
            this.publishedList = append
              ? this.publishedList.concat(content)
              : content;
            this.publishedNoMore = last !== false;
            if (!last) this.publishedPage++;
          }
        } else {
          uni.showToast({ title: res.msg || "加载失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },

    loadMore() {
      if (this.noMore || this.loading) return;
      const isJoined = this.activeTab === 0;
      if (isJoined) {
        this.joinedPage++;
      } else {
        this.publishedPage++;
      }
      this.fetchList(true);
    },

    goDetail(id) {
      uni.navigateTo({
        url: `/spages/activity/detail?id=${id}`,
      });
    },

    statusLabel(s) {
      return STATUS_MAP[s] || "";
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
    formatRange(start, end) {
      return `${this.formatTime(start)} - ${this.formatTime(end)}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.activities-page {
  min-height: 100vh;
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
    padding-bottom: env(safe-area-inset-bottom);
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
    padding: 24rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

    .card-title-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 18rpx;

      .card-title {
        font-size: 30rpx;
        font-weight: bold;
        color: #1a202c;
        flex: 1;
        min-width: 0;
        margin-right: 16rpx;
      }

      .status-badge {
        font-size: 22rpx;
        font-weight: bold;
        padding: 4rpx 14rpx;
        border-radius: 8rpx;
        flex-shrink: 0;

        &.status-upcoming {
          background-color: #e6f0ff;
          color: #3b82f6;
        }
        &.status-ongoing {
          background-color: #fff7e6;
          color: #d97706;
        }
        &.status-ended {
          background-color: #f1f5f9;
          color: #94a3b8;
        }
      }
    }

    .card-body {
      display: flex;
      gap: 18rpx;

      .card-cover {
        width: 140rpx;
        height: 140rpx;
        border-radius: 12rpx;
        background-color: #f1f5f9;
        flex-shrink: 0;
      }

      .card-info {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        gap: 8rpx;

        .info-text {
          font-size: 24rpx;
          color: #718096;
        }

        .detail-link {
          display: flex;
          align-items: center;
          font-size: 24rpx;
          color: #4a5568;
          margin-top: 4rpx;
        }
      }
    }
  }

  .loading-tip,
  .no-more-tip {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12rpx;
    padding: 24rpx 0;
    font-size: 24rpx;
    color: #999;
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
