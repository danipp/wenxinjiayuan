<template>
  <view class="enterprise-card" @click="$emit('click')">
    <view class="card-left">
      <image
        class="enterprise-logo"
        :src="enterprise.logo || '/static/default-logo.png'"
        mode="aspectFill"
      />
    </view>
    <view class="card-center">
      <view class="enterprise-name">{{ enterprise.name || '未命名企业' }}</view>
      <view class="enterprise-desc line-clamp-2">
        {{ enterprise.description || '暂无简介' }}
      </view>
      <view class="enterprise-meta">
        <text class="meta-item">联系人: {{ enterprise.contactName || '--' }}</text>
        <text class="meta-item">{{ enterprise.contactPhone || '--' }}</text>
      </view>
      <view class="enterprise-donation" v-if="enterprise.totalDonationCount > 0">
        <text class="donation-text">
          累计捐赠 <text class="donation-highlight">{{ enterprise.totalDonationCount }}</text> 次，
          共 <text class="donation-highlight">¥{{ enterprise.totalDonationAmount || 0 }}</text>
        </text>
      </view>
    </view>
    <view class="card-right">
      <view class="status-tag" :class="statusClass">
        {{ statusText }}
      </view>
      <u-icon name="arrow-right" color="#cbd5e1" size="12" />
    </view>
  </view>
</template>

<script>
export default {
  name: 'EnterpriseCard',
  props: {
    enterprise: { type: Object, default: () => ({}) },
  },
  computed: {
    statusText() {
      return this.enterprise.status === '0' ? '下架' : '上架';
    },
    statusClass() {
      return this.enterprise.status === '0' ? 'status-off' : 'status-on';
    },
  },
};
</script>

<style lang="scss" scoped>
.enterprise-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 20rpx;
  padding: 28rpx 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.03);

  .card-left {
    margin-right: 20rpx;
    flex-shrink: 0;

    .enterprise-logo {
      width: 100rpx;
      height: 100rpx;
      border-radius: 16rpx;
      background: #f5f5f5;
    }
  }

  .card-center {
    flex: 1;
    min-width: 0;

    .enterprise-name {
      font-size: 30rpx;
      font-weight: bold;
      color: #1a202c;
      margin-bottom: 8rpx;
    }

    .enterprise-desc {
      font-size: 24rpx;
      color: #718096;
      line-height: 1.5;
      margin-bottom: 10rpx;
    }

    .line-clamp-2 {
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .enterprise-meta {
      display: flex;
      gap: 20rpx;
      font-size: 22rpx;
      color: #a0aec0;
      margin-bottom: 8rpx;
    }

    .enterprise-donation {
      .donation-text {
        font-size: 22rpx;
        color: #718096;
      }
      .donation-highlight {
        color: #f59e0b;
        font-weight: bold;
      }
    }
  }

  .card-right {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 12rpx;
    flex-shrink: 0;
    margin-left: 12rpx;

    .status-tag {
      font-size: 22rpx;
      padding: 4rpx 16rpx;
      border-radius: 8rpx;
      font-weight: bold;

      &.status-on {
        background: #e6f7ed;
        color: #10b981;
      }
      &.status-off {
        background: #fef3e2;
        color: #f59e0b;
      }
    }
  }
}
</style>
