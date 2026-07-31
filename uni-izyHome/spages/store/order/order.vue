<template>
  <view class="order-list-page">
    <!-- 1. 顶部一级 Tabs：我买的 VS 我卖的 -->
    <view class="top-primary-tabs">
      <view
        class="tab-btn"
        :class="{ 'active-primary': topActiveTab === 0 }"
        @click="switchTopTab(0)"
      >
        我买的
      </view>
      <view
        class="tab-btn"
        :class="{ 'active-primary': topActiveTab === 1 }"
        @click="switchTopTab(1)"
      >
        我卖的
      </view>
    </view>

    <!-- 2. 二级状态 Tabs：全部、待领取、已完成、待评价 -->
    <view class="sub-status-tabs">
      <view
        v-for="(sub, idx) in subTabs"
        :key="idx"
        class="sub-tab-item"
        :class="{ 'sub-tab-active': subActiveTab === idx }"
        @click="subActiveTab = idx"
      >
        {{ sub.name }}
      </view>
    </view>

    <!-- 3. 【组件化处理】：下方列表自适应滚动 -->
    <view class="scroll-container">
      <!-- 渲染买家子组件 -->
      <BuyerOrderList
        v-if="topActiveTab === 0"
        :status="subTabs[subActiveTab].status"
      />
      <!-- 渲染卖家子组件 -->
      <SellerOrderList
        v-else-if="topActiveTab === 1"
        :status="subTabs[subActiveTab].status"
      />
    </view>
  </view>
</template>

<script>
// 引入子组件
import BuyerOrderList from "../components/BuyerOrderList.vue";
import SellerOrderList from "../components/SellerOrderList.vue";

export default {
  components: {
    BuyerOrderList,
    SellerOrderList,
  },
  computed: {
    subTabs() {
      if (this.topActiveTab == 0) {
        return [
          { name: "全部", status: "all" },
          { name: "待领取", status: "pending" },
          { name: "已完成", status: "completed" },
          { name: "待评价", status: "evaluate" },
        ];
      } else {
        return [
          { name: "全部", status: "all" },
          { name: "待核销", status: "pending" },
          { name: "已完成", status: "completed" },
          { name: "已退款", status: "refund" },
        ];
      }
    },
  },
  data() {
    return {
      topActiveTab: 0, // 0: 我买的, 1: 我卖的
      subActiveTab: 0, // 当前二级选中的子索引
    };
  },
  methods: {
    // 切换顶部一级标签时，强制重置二级标签为 “全部” (索引为 0)
    switchTopTab(idx) {
      this.topActiveTab = idx;
      this.subActiveTab = 0;
    },
  },
};
</script>

<style lang="scss" scoped>
.order-list-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f7f9fb;

  /* 顶部一级 Tabs */
  .top-primary-tabs {
    display: flex;
    background-color: #ffffff;
    height: 90rpx;
    border-bottom: 2rpx solid #edf2f7;

    .tab-btn {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 30rpx;
      color: #64748b;
      font-weight: bold;
      transition: all 0.2s;

      &.active-primary {
        color: #07c160; // 微信绿
        font-size: 32rpx;
        font-weight: 800;
        position: relative;

        &::after {
          content: "";
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 88rpx;
          height: 6rpx;
          background-color: #07c160;
          border-radius: 4rpx;
        }
      }
    }
  }

  /* 二级子状态 Tabs */
  .sub-status-tabs {
    display: flex;
    background-color: #ffffff;
    height: 80rpx;
    align-items: center;
    justify-content: space-around;
    border-bottom: 2rpx solid #edf2f7;

    .sub-tab-item {
      font-size: 27rpx;
      color: #718096;
      padding: 12rpx 24rpx;
      font-weight: bold;
      transition: color 0.15s;

      &.sub-tab-active {
        color: #07c160;
        background-color: #e8f9f0;
        border-radius: 24rpx;
      }
    }
  }

  /* 列表滚区高度自适应 */
  .scroll-container {
    flex: 1;
    overflow: hidden;
  }
}
</style>