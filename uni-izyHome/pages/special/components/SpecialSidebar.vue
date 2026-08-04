<template>
  <scroll-view
    scroll-y
    class="sidebar-left-col"
    :style="{ height: 'calc(100vh - ' + headerHeight + 'px)' }"
  >
    <view
      v-for="(sub, sIdx) in subCategories"
      :key="sIdx"
      class="sidebar-item"
      :class="{ 'sidebar-item-active': activeIdx === sIdx }"
      @click="$emit('select', sIdx)"
    >
      {{ sub.name }}
    </view>
  </scroll-view>
</template>

<script>
export default {
  props: {
    // [{ name, categoryId }]
    subCategories: { type: Array, default: () => [] },
    activeIdx: { type: Number, default: 0 },
    headerHeight: { type: Number, default: 120 },
  },
};
</script>

<style lang="scss" scoped>
.sidebar-left-col {
  width: 200rpx;
  background-color: #f8fafc;

  .sidebar-item {
    height: 110rpx;
    line-height: 110rpx;
    text-align: center;
    font-size: 26rpx;
    color: #64748b;
    font-weight: bold;
    transition: all 0.2s;
    position: relative;

    &.sidebar-item-active {
      background-color: #ffffff;
      color: #ff4d4f;
      font-weight: 800;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 25%;
        width: 8rpx;
        height: 50%;
        background-color: #ff4d4f;
        border-radius: 0 4rpx 4rpx 0;
      }
    }
  }
}
</style>
