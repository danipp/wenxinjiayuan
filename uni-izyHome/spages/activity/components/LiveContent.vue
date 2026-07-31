<template>
  <view>
    <!-- 活动信息 -->
    <view class="activity-info-card">
      <view class="section-header">
        <text class="section-title">活动信息</text>
      </view>
      <text class="info-title">{{ activity.infoTitle }}</text>
      <text class="info-desc">{{ activity.description }}</text>
    </view>

    <!-- 进行中才有已加入的邻居 -->
    <view v-if="isStarted && neighbors.length" class="joined-neighbor-card">
      <view class="section-header">
        <text class="section-title">已加入的邻居</text>
        <text class="joined-count">{{ neighbors.length }}人已加入</text>
      </view>
      <swiper
        class="neighbor-swiper"
        vertical
        circular
        autoplay
        :interval="2400"
        :duration="450"
      >
        <swiper-item
          v-for="(group, gIdx) in neighborGroups"
          :key="gIdx"
          class="neighbor-swiper-item"
        >
          <view
            v-for="(neighbor, nIdx) in group"
            :key="nIdx"
            class="neighbor-row"
          >
            <view class="neighbor-left">
              <u-avatar
                class="neighbor-avatar"
                :src="neighbor.avatar"
                size="64rpx"
                mode="aspectFill"
              ></u-avatar>
              <text class="neighbor-name">{{ neighbor.name }}</text>
            </view>
            <text class="join-time">{{ neighbor.joinTime }}</text>
          </view>
        </swiper-item>
      </swiper>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    activity: { type: Object, default: () => ({}) },
    neighbors: { type: Array, default: () => [] },
    isStarted: { type: Boolean, default: false },
  },
  computed: {
    neighborGroups() {
      const groups = [];
      for (let i = 0; i < this.neighbors.length; i += 2) {
        groups.push(this.neighbors.slice(i, i + 2));
      }
      return groups;
    },
  },
};
</script>

<style lang="scss" scoped>
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28rpx;

  .section-title {
    font-size: 34rpx;
    font-weight: bold;
    color: #1a202c;
  }
}

.activity-info-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
  margin-bottom: 28rpx;

  .info-title {
    display: block;
    font-size: 32rpx;
    font-weight: 800;
    color: #1a202c;
    margin-bottom: 20rpx;
  }

  .info-desc {
    display: block;
    font-size: 27rpx;
    color: #4a5568;
    line-height: 1.7;
  }
}

.joined-neighbor-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

  .joined-count {
    font-size: 24rpx;
    color: #07c160;
    font-weight: bold;
  }

  .neighbor-swiper {
    height: 208rpx;
  }

  .neighbor-swiper-item {
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }

  .neighbor-row {
    height: 92rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #f8fafc;
    border-radius: 20rpx;
    padding: 0 24rpx;
    box-sizing: border-box;
  }

  .neighbor-left {
    display: flex;
    align-items: center;
    min-width: 0;
  }

  .neighbor-avatar {
    width: 64rpx;
    height: 64rpx;
    border-radius: 50%;
    background-color: #edf2f7;
    margin-right: 20rpx;
    flex-shrink: 0;
  }

  .neighbor-name {
    font-size: 28rpx;
    font-weight: bold;
    color: #2d3748;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-left: 20rpx;
  }

  .join-time {
    font-size: 24rpx;
    color: #94a3b8;
    margin-left: 24rpx;
    white-space: nowrap;
  }
}
</style>
