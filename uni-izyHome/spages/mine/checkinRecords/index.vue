<template>
  <view class="checkin-page">
    <view class="page-header">
      <view class="header-left">
        <text class="page-title">打卡记录</text>
        <text class="page-desc">查看通过打卡相框完成的社区打卡记录</text>
      </view>
      <view class="total-pill">
        <text class="total-num">{{ total }}</text>
        <text class="total-label">累计打卡</text>
      </view>
    </view>

    <scroll-view
      scroll-y
      class="records-scroll"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view v-if="recordsList.length > 0" class="records-list">
        <view v-for="item in recordsList" :key="item.id" class="record-card">
          <image
            class="frame-image"
            :src="item.frameImage"
            mode="aspectFill"
          ></image>
          <view class="record-info">
            <view class="title-row">
              <text class="frame-name">{{ item.frameName }}</text>
              <text class="status-tag">{{ item.statusText }}</text>
            </view>
            <view class="info-row">
              <text class="label">相框编号</text>
              <text class="value">{{ item.frameNo }}</text>
            </view>
            <view class="info-row">
              <text class="label">打卡位置</text>
              <text class="value text-ellipsis">{{ item.location }}</text>
            </view>
            <view class="info-row">
              <text class="label">打卡时间</text>
              <text class="value">{{ item.checkinTime }}</text>
            </view>
          </view>
        </view>

        <view class="load-status">
          <view v-if="loading" class="loading-line">
            <u-loading-icon size="36rpx"></u-loading-icon>
            <text>加载中...</text>
          </view>
          <text v-else-if="finished">没有更多打卡记录了</text>
          <text v-else>上拉加载更多</text>
        </view>
      </view>

      <view v-else-if="loading" class="loading-state">
        <view class="state-icon"><u-loading-icon></u-loading-icon></view>
        <text class="state-title">打卡记录加载中...</text>
      </view>

      <view v-else class="empty-state">
        <view class="state-icon">🖼️</view>
        <text class="state-title">暂无打卡记录</text>
        <text class="state-desc">碰一碰打卡相框后，记录会展示在这里</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { count as getCheckinCount, page4 as getCheckinPage } from '../../api/checkin';

export default {
  data() {
    return {
      page: 1,
      pageSize: 20,
      loading: false,
      finished: false,
      isRefreshing: false,
      recordsList: [],
      totalCount: 0,
    };
  },
  computed: {
    total() {
      return this.totalCount;
    },
  },
  onLoad() {
    uni.setNavigationBarTitle({
      title: "打卡记录",
    });
    this.fetchCount();
    this.getList();
  },
  methods: {
    onRefresh() {
      this.isRefreshing = true;
      this.page = 1;
      this.finished = false;
      this.recordsList = [];
      this.getList(true);
    },
    async fetchCount() {
      try {
        const res = await getCheckinCount();
        if (res.code === '00000') {
          this.totalCount = res.data || 0;
        }
      } catch (error) {
        console.error("Failed to fetch count", error);
      }
    },
    async getList(isRefresh = false) {
      this.loading = true;

      try {
        const res = await getCheckinPage({
          pageNumber: this.page,
          pageSize: this.pageSize
        });

        if (res.code === '00000') {
          const listData = res.data?.content || [];
          const formattedList = listData.map(item => ({
            id: item.id || item.recordId,
            frameNo: item.frameNo,
            frameName: item.frameName,
            frameImage: item.frameImage,
            location: item.location,
            checkinTime: item.checkinTime,
            statusText: item.status === 1 ? "打卡成功" : "打卡失败"
          }));

          if (this.page === 1) {
            this.recordsList = formattedList;
          } else {
            this.recordsList = this.recordsList.concat(formattedList);
          }

          this.finished = res.data?.last ?? (formattedList.length < this.pageSize);
          this.page += 1;
        } else {
          uni.showToast({ title: res.msg || "获取列表失败", icon: "none" });
        }
      } catch (error) {
        console.error("Failed to fetch checkin list", error);
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
.checkin-page {
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
    background-color: #f7f9fb;

    .header-left {
      display: flex;
      flex-direction: column;
      min-width: 0;

      .page-title {
        font-size: 40rpx;
        font-weight: 800;
        color: #1a202c;
      }

      .page-desc {
        font-size: 24rpx;
        color: #718096;
        margin-top: 16rpx;
      }
    }

    .total-pill {
      min-width: 156rpx;
      height: 108rpx;
      border-radius: 32rpx;
      background-color: #f0faf5;
      border: 2rpx solid #d1fae5;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      margin-left: 24rpx;
      flex-shrink: 0;

      .total-num {
        font-size: 40rpx;
        font-weight: 800;
        color: #07c160;
      }

      .total-label {
        font-size: 20rpx;
        color: #07c160;
        margin-top: 4rpx;
      }
    }
  }

  .records-scroll {
    height: calc(100vh - 192rpx);
  }

  .records-list {
    padding: 0 32rpx 32rpx;
    box-sizing: border-box;

    .record-card {
      background-color: #ffffff;
      border-radius: 28rpx;
      padding: 28rpx;
      display: flex;
      margin-bottom: 24rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      .frame-image {
        width: 164rpx;
        height: 164rpx;
        border-radius: 24rpx;
        background-color: #edf2f7;
        margin-right: 24rpx;
        flex-shrink: 0;
      }

      .record-info {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;

        .title-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 16rpx;

          .frame-name {
            flex: 1;
            font-size: 30rpx;
            font-weight: bold;
            color: #1a202c;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .status-tag {
            font-size: 20rpx;
            color: #07c160;
            background-color: #f0faf5;
            border-radius: 20rpx;
            padding: 4rpx 14rpx;
            margin-left: 16rpx;
            flex-shrink: 0;
          }
        }

        .info-row {
          display: flex;
          align-items: center;
          line-height: 44rpx;

          .label {
            width: 116rpx;
            font-size: 24rpx;
            color: #94a3b8;
            flex-shrink: 0;
          }

          .value {
            flex: 1;
            font-size: 24rpx;
            color: #4a5568;
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

  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>