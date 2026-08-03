<template>
  <view class="apply-card" @click="$emit('click')">
    <view class="card-header">
      <view class="applicant-info">
        <text class="applicant-name">{{ apply.applicantName || "匿名" }}</text>
        <text class="type-tag">{{ assistanceTypeLabel }}</text>
      </view>
      <view class="status-tag" :class="statusClass">{{ statusLabel }}</view>
    </view>

    <view class="card-body">
      <view class="body-row">
        <text class="body-label">困难描述：</text>
        <text class="body-text line-clamp-2">{{
          apply.difficultyDesc || "--"
        }}</text>
      </view>
      <view class="body-row" v-if="apply.desiredHelp">
        <text class="body-label">期望帮扶：</text>
        <text class="body-text line-clamp-2">{{ apply.desiredHelp }}</text>
      </view>
      <view class="body-row" v-if="apply.applicantPhone">
        <text class="body-label">电话号码：</text>
        <text class="body-text line-clamp-2">{{ apply.applicantPhone }}</text>
      </view>
      <view class="body-row" v-if="apply.address">
        <text class="body-label">居住地址：</text>
        <text class="body-text line-clamp-2">{{ apply.address }}</text>
      </view>
      <view class="body-row" v-if="apply.createTime">
        <text class="body-label">创建时间：</text>
        <text class="body-text line-clamp-2">{{
          formatTime(apply.createTime)
        }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: "ApplyCard",
  props: {
    apply: { type: Object, default: () => ({}) },
  },
  computed: {
    statusLabel() {
      const map = { pending: "待审核", approved: "已通过", rejected: "已驳回" };
      return map[this.apply.status] || this.apply.status || "--";
    },
    statusClass() {
      const map = {
        pending: "status-pending",
        approved: "status-approved",
        rejected: "status-rejected",
      };
      return map[this.apply.status] || "";
    },
    assistanceTypeLabel() {
      const map = {
        living: "生活",
        medical: "医疗",
        education: "教育",
        employment: "就业",
      };
      return (
        map[this.apply.assistanceType] || this.apply.assistanceType || "--"
      );
    },
  },
  methods: {
    formatTime(str) {
      if (!str) return "";
      const d = new Date(str.replace(/-/g, "/"));
      if (isNaN(d.getTime())) return str;
      const pad = (n) => String(n).padStart(2, "0");
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(
        d.getDate()
      )} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.apply-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.03);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;

    .applicant-info {
      display: flex;
      align-items: center;
      gap: 12rpx;

      .applicant-name {
        font-size: 30rpx;
        font-weight: bold;
        color: #1a202c;
      }
      .type-tag {
        font-size: 20rpx;
        color: #fff;
        background: #3b82f6;
        padding: 2rpx 14rpx;
        border-radius: 6rpx;
      }
    }

    .status-tag {
      font-size: 22rpx;
      padding: 4rpx 16rpx;
      border-radius: 8rpx;
      font-weight: bold;

      &.status-pending {
        background: #fef3e2;
        color: #f59e0b;
      }
      &.status-approved {
        background: #e6f7ed;
        color: #10b981;
      }
      &.status-rejected {
        background: #fde8e8;
        color: #ef4444;
      }
    }
  }

  .card-body {
    margin-bottom: 14rpx;

    .body-row {
      display: flex;
      margin-bottom: 6rpx;

      .body-label {
        font-size: 24rpx;
        color: #999;
        flex-shrink: 0;
      }
      .body-text {
        font-size: 24rpx;
        color: #4a5568;
        line-height: 1.5;
      }
      .line-clamp-2 {
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }
  }

  .card-footer {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
    padding-top: 14rpx;
    border-top: 1rpx solid #f5f5f5;
    font-size: 22rpx;
    color: #a0aec0;
  }
}
</style>
