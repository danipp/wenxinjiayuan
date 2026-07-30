<template>
  <view class="comments-page">
    <view class="comments-header">
      <view class="header-left">
        <text class="page-title">全部评价</text>
        <text class="page-subtitle">共{{ total }}条活动评价</text>
      </view>
      <view class="score-pill">
        <text class="score-num">4.9</text>
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
export default {
  data() {
    return {
      activityId: "",
      page: 1,
      pageSize: 6,
      loading: true,
      finished: false,
      isRefreshing: false,
      commentsList: [],
      mockComments: [
        {
          id: 1,
          name: "秉治",
          avatar: "https://cdn.uviewui.com/uview/album/1.jpg",
          time: "1小时前",
          emoji: "😆",
          statusText: "远超预期",
          score: 5,
          content: "活动组织得很好，现场氛围很轻松，邻居们都聊得很开心。",
        },
        {
          id: 2,
          name: "石头",
          avatar: "https://cdn.uviewui.com/uview/album/2.jpg",
          time: "2小时前",
          emoji: "👏",
          statusText: "特别好",
          score: 5,
          content: "工作人员很热心，指引清楚，活动体验非常好。",
        },
        {
          id: 3,
          name: "陈阿姨",
          avatar: "https://cdn.uviewui.com/uview/album/3.jpg",
          time: "1天前",
          emoji: "😆",
          statusText: "热心细致",
          score: 5,
          content: "很久没有参加这么温暖的社区活动了，认识了不少邻居。",
        },
        {
          id: 4,
          name: "时光山哥",
          avatar: "https://cdn.uviewui.com/uview/album/4.jpg",
          time: "1天前",
          emoji: "😊",
          statusText: "满意",
          score: 4,
          content: "活动内容挺丰富的，如果下次时间再长一点就更好了。",
        },
        {
          id: 5,
          name: "小林",
          avatar: "https://cdn.uviewui.com/uview/album/5.jpg",
          time: "2天前",
          emoji: "🌿",
          statusText: "轻松自在",
          score: 5,
          content: "现场茶水和小点心准备得很贴心，聊天也没有压力。",
        },
        {
          id: 6,
          name: "王姐",
          avatar: "https://cdn.uviewui.com/uview/album/6.jpg",
          time: "2天前",
          emoji: "👍",
          statusText: "值得参加",
          score: 4,
          content: "整体不错，适合带家人一起参加，希望后面多办一些。",
        },
        {
          id: 7,
          name: "阿明",
          avatar: "https://cdn.uviewui.com/uview/album/7.jpg",
          time: "3天前",
          emoji: "😄",
          statusText: "气氛很好",
          score: 5,
          content: "邻里互动很多，也有志愿者帮忙维持秩序，很不错。",
        },
        {
          id: 8,
          name: "林姨",
          avatar: "https://cdn.uviewui.com/uview/album/8.jpg",
          time: "3天前",
          emoji: "😊",
          statusText: "满意",
          score: 4,
          content: "活动地点比较好找，工作人员提前提醒也很及时。",
        },
        {
          id: 9,
          name: "小周",
          avatar: "https://cdn.uviewui.com/uview/album/9.jpg",
          time: "4天前",
          emoji: "💚",
          statusText: "很暖心",
          score: 5,
          content: "这种社区活动很有意义，让大家更熟悉身边的邻居。",
        },
      ],
    };
  },
  computed: {
    total() {
      return this.mockComments.length;
    },
  },
  onLoad(options) {
    this.activityId = options && options.id ? options.id : "";
    uni.setNavigationBarTitle({
      title: "活动评价",
    });
    this.getList();
  },
  methods: {
    getStars(score) {
      const count = score || 5;
      return "⭐️".repeat(count);
    },
    onRefresh() {
      this.isRefreshing = true;
      this.page = 1;
      this.finished = false;
      this.commentsList = [];
      this.getList(true);
    },
    getList(isRefresh = false) {
      // 模拟接口请求，可替换为实际接口：activityId、page、pageSize
      this.loading = true;
      setTimeout(() => {
        const start = (this.page - 1) * this.pageSize;
        const nextList = this.mockComments.slice(start, start + this.pageSize);

        if (this.page === 1) {
          this.commentsList = nextList;
        } else {
          this.commentsList = this.commentsList.concat(nextList);
        }

        this.page += 1;
        this.loading = false;
        this.finished = this.commentsList.length >= this.mockComments.length;

        if (isRefresh) {
          this.isRefreshing = false;
          uni.showToast({ title: "刷新成功", icon: "none" });
        }
      }, 1500);
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
