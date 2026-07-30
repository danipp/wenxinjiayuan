<template>
  <view class="collect-page">
    <!-- 1. 顶部二级 Tabs 切换（商品 VS 店铺） -->
    <view class="tabs-header">
      <view
        class="tab-item"
        :class="{ 'tab-item-active': activeTab === 0 }"
        @click="activeTab = 0"
      >
        商品
      </view>
      <view
        class="tab-item"
        :class="{ 'tab-item-active': activeTab === 1 }"
        @click="activeTab = 1"
      >
        店铺
      </view>
    </view>

    <!-- 2. 【组件化容器】高度自适应 -->
    <view class="scroll-container">
      <CollectGoods v-if="activeTab === 0" />
      <CollectShops v-else-if="activeTab === 1" />
    </view>
  </view>
</template>

<script>
// 导入商品与店铺子组件
import CollectGoods from "./components/CollectGoods.vue";
import CollectShops from "./components/CollectShops.vue";

export default {
  components: {
    CollectGoods,
    CollectShops,
  },
  data() {
    return {
      activeTab: 0, // 0: 商品收藏, 1: 店铺收藏
    };
  },
  onLoad() {
    uni.setNavigationBarTitle({
      title: "我的收藏",
    });
  },
};
</script>

<style lang="scss" scoped>
.collect-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f7f9fb;

  /* 顶部选项卡 */
  .tabs-header {
    height: 100rpx;
    background-color: #ffffff;
    display: flex;
    justify-content: space-around;
    align-items: center;
    border-bottom: 2rpx solid #edf2f7;
    z-index: 10;

    .tab-item {
      font-size: 30rpx;
      color: #718096;
      padding: 20rpx 32rpx;
      position: relative;
      font-weight: bold;
      transition: color 0.15s ease;

      &.tab-item-active {
        color: #07c160;

        &::after {
          content: "";
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 48rpx;
          height: 8rpx;
          background-color: #07c160;
          border-radius: 4rpx;
        }
      }
    }
  }

  /* 自适应滚动层 */
  .scroll-container {
    flex: 1;
    overflow: hidden;
  }
}
</style>