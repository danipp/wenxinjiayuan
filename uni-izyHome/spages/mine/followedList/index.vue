<template>
  <view class="follow-page">
    <!-- <view class="page-header">
      <view class="header-left">
        <text class="page-title">关注记录</text>
        <text class="page-desc">用户关注你后，每位关注用户可获得10积分</text>
      </view>
      <view class="points-pill">
        <text class="points-num">{{ totalPoints }}</text>
        <text class="points-label">累计积分</text>
      </view>
    </view> -->

    <scroll-view
      scroll-y
      class="follow-scroll"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view v-if="followList.length > 0" class="follow-list">
        <view v-for="item in followList" :key="item.id" class="follow-card">
          <image class="avatar" :src="item.avatar" mode="aspectFill"></image>
          <view class="follow-info">
            <text class="name">{{ item.name || item.phone }}</text>
            <text class="phone" v-if="item.name">{{ item.phone }}</text>
          </view>
          <view class="points-tag">
            <text>+10积分</text>
          </view>
        </view>

        <view class="load-status">
          <view v-if="loading" class="loading-line">
            <u-loading-icon size="36rpx"></u-loading-icon>
            <text>加载中...</text>
          </view>
          <text v-else-if="finished">没有更多关注记录了</text>
          <text v-else>上拉加载更多</text>
        </view>
      </view>

      <view v-else-if="loading" class="loading-state">
        <view class="state-icon"><u-loading-icon></u-loading-icon></view>
        <text class="state-title">关注记录加载中...</text>
      </view>

      <view v-else class="empty-state">
        <view class="state-icon">⭐</view>
        <text class="state-title">暂无关注记录</text>
        <text class="state-desc">有用户关注你后，记录会展示在这里</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { page2 as getFollowPage } from '../../api/follow';

export default {
  data() {
    return {
      page: 1,
      pageSize: 20,
      loading: false,
      finished: false,
      isRefreshing: false,
      followList: [],
      totalFollows: 0,
    };
  },
  computed: {
    totalPoints() {
      return this.totalFollows * 10;
    },
  },
  onLoad() {
    uni.setNavigationBarTitle({
      title: "关注记录",
    });
    this.getList();
  },
  methods: {
    onRefresh() {
      this.isRefreshing = true;
      this.page = 1;
      this.finished = false;
      this.followList = [];
      this.getList(true);
    },
    async getList(isRefresh = false) {
      if (this.loading && !isRefresh) return;
      this.loading = true;

      try {
        const res = await getFollowPage({
          pageNumber: this.page,
          pageSize: this.pageSize
        });

        if (res.code === '00000') {
          const listData = res.data?.content || [];
          this.totalFollows = res.data?.totalElements || 0;

          const formattedList = listData.map(item => ({
            id: item.id || item.followId,
            name: item.followerName,
            phone: item.followerPhone,
            avatar: item.followerAvatar
          }));

          if (this.page === 1) {
            this.followList = formattedList;
          } else {
            this.followList = this.followList.concat(formattedList);
          }

          this.finished = res.data?.last ?? (formattedList.length < this.pageSize);
          this.page += 1;
        } else {
          uni.showToast({ title: res.msg || "获取记录失败", icon: "none" });
        }
      } catch (error) {
        console.error("Failed to fetch follow list", error);
      } finally {
        this.loading = false;
        if (isRefresh) {
          this.isRefreshing = false;
          uni.showToast({ title: "刷新成功", icon: "none" });
        }
      }
    },
    loadMore() {
      if (this.loading || this.finished) return;
      this.getList();
    },
  },
};
</script>

<style lang="scss" scoped>
.follow-page {
  min-height: 100vh;
  background-color: #f7f9fb;
  box-sizing: border-box;

  .page-header {
    height: 192rpx;
    padding: 32rpx;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: linear-gradient(135deg, #f0faf5 0%, #f7f9fb 100%);

    .header-left {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;

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

    .points-pill {
      min-width: 156rpx;
      height: 108rpx;
      border-radius: 32rpx;
      background-color: #ffffff;
      border: 2rpx solid #d1fae5;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      margin-left: 24rpx;
      flex-shrink: 0;
      box-shadow: 0 4rpx 16rpx rgba(7, 193, 96, 0.06);

      .points-num {
        font-size: 40rpx;
        font-weight: 800;
        color: #07c160;
      }

      .points-label {
        font-size: 20rpx;
        color: #07c160;
        margin-top: 4rpx;
      }
    }
  }

  .follow-scroll {
    height: 100vh;
  }

  .follow-list {
    padding: 0 32rpx 32rpx;
    box-sizing: border-box;

    .follow-card {
      background-color: #ffffff;
      border-radius: 28rpx;
      padding: 28rpx;
      display: flex;
      align-items: center;
      margin-bottom: 24rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      .avatar {
        width: 104rpx;
        height: 104rpx;
        border-radius: 50%;
        background-color: #edf2f7;
        margin-right: 24rpx;
        flex-shrink: 0;
      }

      .follow-info {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;

        .name {
          font-size: 32rpx;
          font-weight: bold;
          color: #1a202c;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .phone {
          font-size: 24rpx;
          color: #718096;
          margin-top: 12rpx;
        }
      }

      .points-tag {
        height: 64rpx;
        padding: 0 24rpx;
        border-radius: 32rpx;
        background-color: #fff7e6;
        color: #ff8a00;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 26rpx;
        font-weight: bold;
        margin-left: 20rpx;
        flex-shrink: 0;
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