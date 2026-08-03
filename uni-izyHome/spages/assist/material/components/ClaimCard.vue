<template>
  <view class="claim-card" @click="$emit('click')">
    <view class="card-top">
      <image
        class="goods-img"
        :src="claim.goodsImage || '/static/default-goods.png'"
        mode="aspectFill"
      />
      <view class="goods-info">
        <text class="goods-title">{{ claim.goodsTitle || '--' }}</text>
        <text class="goods-count">申领数量：{{ claim.claimCount || 0 }}</text>
      </view>
      <view class="status-tag" :class="statusClass">{{ statusLabel }}</view>
    </view>

    <view class="card-middle">
      <text class="claim-reason line-clamp-2" v-if="claim.claimReason">
        原因：{{ claim.claimReason }}
      </text>
    </view>

    <view class="card-bottom">
      <text class="meta" v-if="claim.contactName">{{ claim.contactName }}</text>
      <text class="meta" v-if="claim.contactPhone">{{ claim.contactPhone }}</text>
      <text class="meta" v-if="claim.createTime">{{ formatTime(claim.createTime) }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ClaimCard',
  props: {
    claim: { type: Object, default: () => ({}) },
  },
  computed: {
    statusLabel() {
      const map = { pending: '待审核', approved: '已通过', rejected: '已驳回', distributed: '已发放' };
      return map[this.claim.status] || this.claim.status || '--';
    },
    statusClass() {
      const map = { pending: 'status-pending', approved: 'status-approved', rejected: 'status-rejected', distributed: 'status-distributed' };
      return map[this.claim.status] || '';
    },
  },
  methods: {
    formatTime(str) {
      if (!str) return '';
      const d = new Date(str.replace(/-/g, '/'));
      if (isNaN(d.getTime())) return str;
      const pad = (n) => String(n).padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.claim-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.03);

  .card-top {
    display: flex;
    align-items: center;
    margin-bottom: 14rpx;

    .goods-img {
      width: 100rpx; height: 100rpx;
      border-radius: 14rpx;
      background: #f5f5f5;
      margin-right: 18rpx;
      flex-shrink: 0;
    }

    .goods-info {
      flex: 1; min-width: 0;

      .goods-title {
        font-size: 28rpx; font-weight: bold; color: #1a202c;
        display: block; margin-bottom: 6rpx;
      }
      .goods-count {
        font-size: 24rpx; color: #718096;
      }
    }

    .status-tag {
      font-size: 22rpx; padding: 4rpx 16rpx;
      border-radius: 8rpx; font-weight: bold;
      flex-shrink: 0; margin-left: 12rpx;

      &.status-pending { background: #fef3e2; color: #f59e0b; }
      &.status-approved { background: #e6f7ed; color: #10b981; }
      &.status-rejected { background: #fde8e8; color: #ef4444; }
      &.status-distributed { background: #e6f0ff; color: #3b82f6; }
    }
  }

  .card-middle {
    margin-bottom: 12rpx;
    .claim-reason {
      font-size: 24rpx; color: #718096; line-height: 1.5;
    }
    .line-clamp-2 {
      display: -webkit-box;
      -webkit-line-clamp: 2; -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }

  .card-bottom {
    display: flex; flex-wrap: wrap; gap: 16rpx;
    padding-top: 12rpx; border-top: 1rpx solid #f5f5f5;
    font-size: 22rpx; color: #a0aec0;
  }
}
</style>
