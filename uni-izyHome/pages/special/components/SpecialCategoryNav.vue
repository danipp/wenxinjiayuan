<template>
  <view class="level-1-container">
    <view 
      v-for="(cat, idx) in categories" 
      :key="idx" 
      class="cat-1-item"
      :class="{ 'cat-1-active': activeIdx === idx }"
      @click="$emit('select', idx)"
    >
      <image class="cat-icon" :src="cat.icon" mode="aspectFill"></image>
      <text class="cat-label">{{ cat.name }}</text>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    categories: { type: Array, default: () => [] },
    activeIdx: { type: Number, default: 0 }
  }
};
</script>

<style lang="scss" scoped>
.level-1-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 20rpx 16rpx;
  background-color: #ffffff;
  box-sizing: border-box;
  border-bottom: 1rpx solid #edf2f7;

  .cat-1-item {
    flex: 1; // 100% 平均分布，消除右侧留白
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;

    .cat-icon {
      width: 72rpx;
      height: 72rpx;
      border-radius: 50%;
      border: 3rpx solid transparent;
      transition: all 0.2s;
    }

    .cat-label {
      font-size: 22rpx;
      color: #64748b;
      margin-top: 10rpx;
      font-weight: bold;
      white-space: nowrap;
    }

    &.cat-1-active {
      .cat-icon {
        border-color: #ff4d4f;
      }
      .cat-label {
        color: #ff4d4f;
        font-weight: 800;
      }
    }
  }
}
</style>