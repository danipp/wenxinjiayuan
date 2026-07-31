<template>
  <view class="comments-page">
    <view class="comments-header">
      <view class="header-left">
        <text class="page-title">全部评价</text>
        <text class="page-subtitle">共{{ total }}条活动评价</text>
      </view>
      <view class="score-pill">
        <text class="score-num">{{ avgScore.toFixed(1) }}</text>
        <text class="score-label">综合评分</text>
      </view>
    </view>

    <scroll-view
      scroll-y
      class="comments-scroll"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view v-if="commentsList.length > 0" class="review-list-card">
        <view
          v-for="item in commentsList"
          :key="item.id"
          class="review-item-row"
        >
          <image class="avatar" :src="item.avatar" mode="aspectFill"></image>
          <view class="review-right">
            <view class="name-time-row">
              <text class="name">{{ item.name }}</text>
              <text class="time">{{ item.time }}</text>
            </view>
            <view class="rating-bubble">
              <text class="emoji">{{ item.emoji }}</text>
              <text class="rating-text">{{ item.statusText }}</text>
              <view class="stars">{{ getStars(item.score) }}</view>
            </view>
            <text class="review-content">{{ item.content }}</text>
          </view>
        </view>

        <view class="load-status">
          <view v-if="loading" class="flex" style="gap: 10rpx"
            ><u-loading-icon size="20"></u-loading-icon>加载中...</view
          >
          <text v-else-if="finished">没有更多评价了</text>
          <text v-else>上拉加载更多</text>
        </view>
      </view>

      <view v-else-if="loading" class="loading-state">
        <view class="loading-emoji"><u-loading-icon></u-loading-icon></view>
        <text class="loading-text">评价加载中...</text>
      </view>

      <view v-else class="empty-state">
        <view class="empty-emoji">💬</view>
        <text class="empty-text">暂无评价</text>
        <text class="empty-tips">下拉刷新看看是否有新的活动评价</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { comments, averageScore } from "@/spages/api/activity";

export default {
  data() {
    return {
      activityId: "",
      page: 1,
      pageSize: 10,
      loading: false,
      finished: false,
      isRefreshing: false,
      commentsList: [],
      total: 0,
      avgScore: 0,
    };
  },
  onLoad(options) {
    this.activityId = options && options.id ? options.id : "";
    uni.setNavigationBarTitle({
      title: "活动评价",
    });
    this.getList();
    this.fetchScore();
  },
  methods: {
    getStars(score) {
      const count = score || 5;
      return "⭐️".repeat(count);
    },
    // 获取评分
    async fetchScore() {
      try {
        const res = await averageScore(this.activityId);
        this.avgScore = res.data || 0;
      } catch (e) {
        // ignore
      }
    },
    onRefresh() {
      this.isRefreshing = true;
      this.page = 1;
      this.finished = false;
      this.commentsList = [];
      this.getList(true);
    },
    async getList(isRefresh = false) {
      if (this.loading || (this.finished && !isRefresh)) return;
      this.loading = true;
      try {
        const res = await comments(this.activityId, {
          pageNumber: this.page,
          pageSize: this.pageSize,
        });
        const pageData = res.data || {};
        const list = pageData.content || [];
        const isLast = pageData.last !== undefined ? pageData.last : list.length < this.pageSize;
        this.total = pageData.totalElements || 0;

        const mappedList = list.map((item) => ({
          id: item.commentId || item.id,
          name: item.nickName || "",
          avatar: item.avatar || "",
          time: this.formatTime(item.createTime),
          emoji: item.emoji || "😊",
          statusText: item.statusText || "",
          score: item.score || 5,
          content: item.content || "",
        }));

        if (this.page === 1) {
          this.commentsList = mappedList;
        } else {
          this.commentsList = [...this.commentsList, ...mappedList];
        }

        this.finished = isLast;
        if (!isLast) this.page++;
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
        if (isRefresh) this.isRefreshing = false;
      }
    },
    formatTime(timeStr) {
      if (!timeStr) return "";
      const d = new Date(timeStr.replace(/-/g, "/"));
      if (isNaN(d.getTime())) return timeStr;
      const diff = Date.now() - d.getTime();
      const minutes = Math.floor(diff / 60000);
      if (minutes < 1) return "刚刚";
      if (minutes < 60) return `${minutes}分钟前`;
      const hours = Math.floor(minutes / 60);
      if (hours < 24) return `${hours}小时前`;
      const days = Math.floor(hours / 24);
      return `${days}天前`;
    },
    loadMore() {
      if (this.loading || this.finished) return;
      this.getList();
    },
  },
};
</script>

<style lang="scss" scoped>
.comments-page {
  min-height: 100vh;
  background-color: #f7f9fb;
  box-sizing: border-box;

  .comments-header {
    height: 184rpx;
    padding: 32rpx;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #f7f9fb;

    .header-left {
      display: flex;
      flex-direction: column;

      .page-title {
        font-size: 40rpx;
        font-weight: 800;
        color: #1a202c;
      }

      .page-subtitle {
        font-size: 26rpx;
        color: #718096;
        margin-top: 12rpx;
      }
    }

    .score-pill {
      min-width: 164rpx;
      height: 108rpx;
      border-radius: 32rpx;
      background-color: #f0faf5;
      border: 2rpx solid #d1fae5;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      .score-num {
        font-size: 40rpx;
        font-weight: 800;
        color: #07c160;
      }

      .score-label {
        font-size: 20rpx;
        color: #07c160;
        margin-top: 4rpx;
      }
    }
  }

  .comments-scroll {
    height: calc(100vh - 184rpx);
  }

  .review-list-card {
    margin: 0 32rpx 32rpx;
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    box-sizing: border-box;

    .review-item-row {
      display: flex;
      gap: 24rpx;
      padding-bottom: 32rpx;
      margin-bottom: 32rpx;
      border-bottom: 2rpx dashed #f1f5f9;

      &:last-of-type {
        margin-bottom: 0;
      }

      .avatar {
        width: 72rpx;
        height: 72rpx;
        border-radius: 50%;
        background-color: #f1f3f5;
        flex-shrink: 0;
      }

      .review-right {
        flex: 1;
        display: flex;
        flex-direction: column;

        .name-time-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12rpx;

          .name {
            font-size: 28rpx;
            font-weight: bold;
            color: #2d3748;
          }

          .time {
            font-size: 22rpx;
            color: #a0aec0;
          }
        }

        .rating-bubble {
          display: flex;
          align-items: center;
          background-color: #f0faf5;
          padding: 12rpx 24rpx;
          border-radius: 24rpx;
          width: fit-content;
          margin-bottom: 16rpx;

          .emoji {
            font-size: 26rpx;
          }

          .rating-text {
            font-size: 24rpx;
            color: #07c160;
            font-weight: bold;
            margin: 0 12rpx 0 8rpx;
          }

          .stars {
            font-size: 20rpx;
          }
        }

        .review-content {
          font-size: 27rpx;
          color: #4a5568;
          line-height: 1.5;
        }
      }
    }

    .load-status {
      height: 76rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24rpx;
      color: #a0aec0;
    }
  }

  .loading-state,
  .empty-state {
    height: calc(100vh - 184rpx);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .loading-emoji,
    .empty-emoji {
      width: 116rpx;
      height: 116rpx;
      border-radius: 36rpx;
      background-color: #f0faf5;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 60rpx;
      margin-bottom: 28rpx;
    }

    .loading-text,
    .empty-text {
      font-size: 28rpx;
      color: #718096;
      font-weight: bold;
    }

    .empty-tips {
      font-size: 22rpx;
      color: #cbd5e1;
      margin-top: 12rpx;
    }
  }
}
</style>
