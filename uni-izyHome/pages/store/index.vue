<template>
  <view class="store-master-container">
    <view class="top-header-bar" style="margin-bottom:0">
      <view class="community-pill" @click="showCommunitySelector = true;">
        <u-icon name="home-fill" color="#07c160" size="16"></u-icon>
        <text class="pill-text text-ellipsis">{{ currentCommunity.name }}</text>
        <u-icon name="arrow-right" color="#999" size="10"></u-icon>
      </view>
    </view>
    <!-- 2. 四大核心分类 Tabs 选项卡 (固定高度 110rpx) -->
    <view class="four-tabs-header">
      <view v-for="(tab, idx) in mainTabs" :key="idx" class="tab-btn"
        :class="{ 'tab-btn-active': activeTab === tab.id }" @click="switchTab(tab.id)">
        {{ tab.name }}
      </view>
    </view>

    <!-- 3. 四大分类子组件挂载区 (使用 v-show 避免重复请求) -->
    <view class="sub-component-viewport">
      <!-- 分类一：志愿者商城 (goodsType=3) -->
      <PointsMall v-show="activeTab === 1" :headerHeight="200" :communityId="currentCommunity.communityId"
        ref="pointsMall" />
      <!-- 分类二：积分兑换 (goodsType=1) -->
      <PurePoints v-show="activeTab === 2" :headerHeight="200" :communityId="currentCommunity.communityId"
        ref="purePoints" />
      <!-- 分类三：消费帮扶（暂不接接口） -->
      <AssistanceMall v-show="activeTab === 3" :headerHeight="200" :communityId="currentCommunity.communityId"
        ref="assistanceMall" />
    </view>
    <CommunitySelector :show.sync="showCommunitySelector" title="请选择我的社区" mode="select"
      @confirm="handleCommunityChange" />
  </view>
</template>

<script>
// 引入 4 大独立子组件
import PointsMall from "./components/PointsMall.vue";
import PurePoints from "./components/PurePoints.vue";
import AssistanceMall from "./components/AssistanceMall.vue";
import CommunitySelector from "@/components/community.vue";

export default {
  components: {
    PointsMall,
    PurePoints,
    AssistanceMall,
    CommunitySelector
  },
  data() {
    return {
      currentCommunity: {
        name: "请选择社区",
        communityId: ""
      },
      showCommunitySelector: false,
      activeTab: 1,
      mainTabs: [
        { name: "志愿者商城", id: 1 },
        { name: "积分兑换", id: 2 },
        { name: "消费帮扶", id: 3 },
      ],
    };
  },
  onShow() {
    const cachedLocation = uni.getStorageSync("selected_community");
    if (cachedLocation && cachedLocation.name) {
      this.currentCommunity.name = cachedLocation.name;
      this.currentCommunity.communityId = cachedLocation.communityId;
    }
    uni.setNavigationBarTitle({ title: "公益商店" });
  },
  methods: {
    handleCommunityChange(data) {
      if (data && data.community) {
        this.currentCommunity.name = data.community.name;
        this.currentCommunity.communityId = data.community.communityId;
        uni.setStorageSync("selected_community", data.community);
        this.$nextTick(() => {
          if (this.activeTab == 1) {
            this.$refs.pointsMall?.refreshData();
          } else if (this.activeTab == 2) {
            this.$refs.purePoints?.refreshData();
          } else if (this.activeTab == 3) {
            this.$refs.assistanceMall?.refreshData();
          }
        });
      }
    },
    switchTab(idx) {
      this.activeTab = idx;
    },
  },
};
</script>

<style lang="scss" scoped>
.store-master-container {
  background-color: #f7f9fb;
  box-sizing: border-box;

  /* 顶部社区栏 (90rpx) */
  .top-header-bar {
    height: 90rpx;
    padding: 0 24rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #ffffff;
    box-sizing: border-box;
    border-bottom: 1rpx solid #edf2f7;

    .community-pill {
      display: inline-flex;
      align-items: center;
      background-color: #f1f3f5;
      padding: 10rpx 20rpx;
      border-radius: 30rpx;
      max-width: 60%;

      .pill-text {
        font-size: 26rpx;
        font-weight: bold;
        color: #2c405a;
        margin: 0 10rpx;
      }
    }

    .store-header-tips {
      font-size: 24rpx;
      font-weight: bold;
      color: #94a3b8;
    }
  }

  /* 四大分类选项卡 (110rpx) */
  .four-tabs-header {
    height: 110rpx;
    background-color: #ffffff;
    display: flex;
    align-items: center;
    justify-content: space-around;
    border-bottom: 1rpx solid #edf2f7;

    .tab-btn {
      font-size: 28rpx;
      color: #64748b;
      font-weight: bold;
      padding: 12rpx 10rpx;
      position: relative;
      transition: color 0.2s;

      &.tab-btn-active {
        color: #07c160; // 微信志愿绿
        font-weight: 800;

        &::after {
          content: "";
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 36rpx;
          height: 6rpx;
          background-color: #07c160;
          border-radius: 3rpx;
        }
      }
    }
  }

  .sub-component-viewport {
    width: 100%;
  }

  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

/* 1. 顶部社区选择区 */
.top-header-bar {
  height: 90rpx;
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;

  .community-pill {
    display: inline-flex;
    align-items: center;
    background-color: #ffffff;
    border: 2rpx solid #edf2f7;
    padding: 12rpx 24rpx;
    border-radius: 40rpx;
    max-width: 60%;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.01);

    .pill-text {
      font-size: 26rpx;
      font-weight: bold;
      color: #2c405a;
      margin: 0 12rpx;
    }
  }
}
</style>