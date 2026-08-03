<template>
  <view class="stat-card" @click="$emit('longpress', stat)">
    <view class="stat-icon" :style="{ background: iconBg }">
      <text class="stat-icon-text">{{ iconText }}</text>
    </view>
    <view class="stat-info">
      <text class="stat-label">{{ stat.statLabel || "--" }}</text>
      <text class="stat-value">{{
        stat.isCustom ? formatNumber(stat.statValue) : stat.statValue
      }}</text>
    </view>
    <view class="stat-key" v-if="stat.statKey">
      <text class="key-text">{{ stat.statKey }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: "StatCard",
  props: {
    stat: { type: Object, default: () => ({}) },
  },
  computed: {
    iconBg() {
      const colors = [
        "#e6f7ed",
        "#e6f0ff",
        "#fef3e2",
        "#fde8f0",
        "#f0f0ff",
        "#e0fff4",
      ];
      const idx = (this.stat.displayOrder || 0) % colors.length;
      return colors[idx];
    },
    iconText() {
      return (this.stat.statLabel || "统").charAt(0);
    },
  },
  methods: {
    formatNumber(v) {
      if (v >= 10000) return (v / 10000).toFixed(1) + "w";
      if (v >= 1000) return (v / 1000).toFixed(1) + "k";
      return String(v);
    },
  },
};
</script>

<style lang="scss" scoped>
.stat-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx 24rpx 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.03);
  position: relative;

  .stat-icon {
    width: 80rpx;
    height: 80rpx;
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16rpx;

    .stat-icon-text {
      font-size: 34rpx;
      font-weight: bold;
      color: #333;
    }
  }

  .stat-info {
    display: flex;
    flex-direction: column;
    align-items: center;

    .stat-label {
      font-size: 24rpx;
      color: #718096;
      margin-bottom: 6rpx;
    }

    .stat-value {
      font-size: 40rpx;
      font-weight: 900;
      color: #1a202c;
    }
  }

  .stat-key {
    margin-top: 10rpx;
    .key-text {
      font-size: 20rpx;
      color: #cbd5e1;
    }
  }
}
</style>
