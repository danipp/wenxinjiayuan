<template>
  <view class="material-page">
    <!-- 搜索 -->
    <view class="search-bar">
      <u-search
        v-model="keyword"
        placeholder="搜索商品标题"
        :show-action="false"
        @search="onSearch"
        @clear="onSearchClear"
      />
    </view>

    <!-- 状态筛选 -->
    <view class="filter-row">
      <view class="filter-tabs">
        <view
          v-for="t in statusTabs"
          :key="t.value"
          class="filter-tab"
          :class="{ active: activeStatus === t.value }"
          @click="switchStatusTab(t.value)"
        >{{ t.label }}</view>
      </view>
    </view>

    <!-- 列表 -->
    <scroll-view
      class="list-scroll"
      scroll-y
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view v-if="list.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">📦</text>
        <text class="empty-text">暂无申领记录</text>
      </view>

      <ClaimCard
        v-for="item in list"
        :key="item.claimId"
        :claim="item"
        @click="openDetail(item)"
      />

      <view v-if="loading" class="loading-tip">
        <u-loading-icon></u-loading-icon>
        <text>加载中...</text>
      </view>
      <view v-if="noMore && list.length > 0" class="no-more-tip">已加载全部数据</view>
    </scroll-view>

    <!-- 详情底部弹窗 -->
    <u-popup
      :show="showDetail"
      mode="bottom"
      round="20"
      @close="showDetail = false"
      :safeAreaInsetBottom="true"
      @touchmove.stop.prevent
    >
      <view class="detail-sheet">
        <view class="detail-title">{{ currentClaim.goodsTitle || '--' }}</view>
        <view class="detail-body">
          <view class="detail-item" v-if="currentClaim.goodsImage">
            <image class="detail-img" :src="currentClaim.goodsImage" mode="widthFix" />
          </view>
          <view class="detail-item"><text class="dl">申领数量</text><text class="dv">{{ currentClaim.claimCount || 0 }}</text></view>
          <view class="detail-item"><text class="dl">申领人</text><text class="dv">{{ currentClaim.contactName || '--' }}</text></view>
          <view class="detail-item"><text class="dl">联系电话</text><text class="dv">{{ currentClaim.contactPhone || '--' }}</text></view>
          <view class="detail-item" v-if="currentClaim.address"><text class="dl">收货地址</text><text class="dv">{{ currentClaim.address }}</text></view>
          <view class="detail-item"><text class="dl">申领原因</text><text class="dv">{{ currentClaim.claimReason || '--' }}</text></view>
          <view class="detail-item"><text class="dl">状态</text><text class="dv">{{ statusMap[currentClaim.status] || currentClaim.status }}</text></view>
          <view class="detail-item" v-if="currentClaim.auditRemark"><text class="dl">审核备注</text><text class="dv">{{ currentClaim.auditRemark }}</text></view>
          <view class="detail-item" v-if="currentClaim.createTime"><text class="dl">创建时间</text><text class="dv">{{ formatTime(currentClaim.createTime) }}</text></view>
        </view>

        <!-- 操作按钮 -->
        <view class="detail-actions" v-if="currentClaim.status">
          <button
            v-if="currentClaim.status === 'pending'"
            class="btn-audit"
            @click="openAudit(currentClaim)"
          >审核</button>
          <button
            v-if="currentClaim.status === 'approved'"
            class="btn-distribute"
            @click="handleDistribute(currentClaim)"
          >确认发放</button>
        </view>
        <view class="action-cancel" @click="showDetail = false">关闭</view>
      </view>
    </u-popup>

    <!-- 审核弹窗 -->
    <ClaimAuditPopup
      :show.sync="showAudit"
      :claim="currentClaim"
      @done="onAuditDone"
    />

    <!-- 提交申领弹窗 -->
    <ClaimForm
      :show.sync="showForm"
      @done="onFormDone"
    />

    <!-- 底部提交按钮 -->
    <view class="bottom-bar">
      <button class="submit-btn" @click="showForm = true">提交物资申领</button>
    </view>
  </view>
</template>

<script>
import ClaimCard from './components/ClaimCard.vue';
import ClaimAuditPopup from './components/ClaimAuditPopup.vue';
import ClaimForm from './components/ClaimForm.vue';
import { page10, distribute } from '@/spages/api/material';

export default {
  components: { ClaimCard, ClaimAuditPopup, ClaimForm },
  data() {
    return {
      keyword: '',
      activeStatus: '',
      statusTabs: [
        { label: '全部', value: '' },
        { label: '待审核', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已发放', value: 'distributed' },
        { label: '已驳回', value: 'rejected' },
      ],
      statusMap: { pending: '待审核', approved: '已通过', rejected: '已驳回', distributed: '已发放' },
      list: [],
      pageNumber: 0,
      pageSize: 15,
      loading: false,
      refreshing: false,
      noMore: false,
      showForm: false,
      showDetail: false,
      showAudit: false,
      currentClaim: {},
      communityId: '',
    };
  },
  onLoad() {
    const community = uni.getStorageSync('selected_community');
    if (community && community.communityId) {
      this.communityId = community.communityId;
    }
    this.fetchList();
  },
  methods: {
    async fetchList(append = false) {
      if (this.loading) return;
      if (!append) { this.pageNumber = 0; this.list = []; this.noMore = false; }
      this.loading = true;
      try {
        const payload = {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          role: 'all',
          communityId: this.communityId || undefined,
        };
        if (this.activeStatus) payload.status = this.activeStatus;

        const res = await page10(payload);
        if (res.code === '00000' && res.data) {
          let { content = [], last } = res.data;
          if (this.keyword) {
            content = content.filter(
              (item) => item.goodsTitle && item.goodsTitle.indexOf(this.keyword) !== -1,
            );
          }
          this.list = append ? this.list.concat(content) : content;
          this.noMore = last !== false;
        } else {
          uni.showToast({ title: res.msg || '加载失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' });
      } finally { this.loading = false; }
    },

    onSearch() { this.fetchList(); },
    onSearchClear() { this.fetchList(); },

    switchStatusTab(v) {
      if (this.activeStatus === v) return;
      this.activeStatus = v;
      this.fetchList();
    },

    async onRefresh() { this.refreshing = true; await this.fetchList(); this.refreshing = false; },
    onLoadMore() { if (this.noMore || this.loading) return; this.pageNumber++; this.fetchList(true); },

    openDetail(item) { this.currentClaim = item; this.showDetail = true; },

    openAudit(item) {
      this.showDetail = false;
      this.currentClaim = item;
      this.$nextTick(() => { this.showAudit = true; });
    },

    // 确认发放
    async handleDistribute(item) {
      const id = item && item.claimId;
      if (!id) return;
      this.showDetail = false;
      const confirmRes = await new Promise((resolve) => {
        uni.showModal({
          title: '确认发放',
          content: `确定已向"${item.contactName}"发放"${item.goodsTitle}"吗？`,
          confirmColor: '#07c160',
          success: (r) => resolve(r.confirm),
        });
      });
      if (!confirmRes) return;
      try {
        const res = await distribute(id);
        if (res.code === '00000') {
          uni.showToast({ title: '发放成功', icon: 'none' });
          this.fetchList();
        } else {
          uni.showToast({ title: res.msg || '操作失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '操作失败', icon: 'none' });
      }
    },

    onAuditDone() { this.fetchList(); },
    onFormDone() { this.fetchList(); },

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
.material-page {
  min-height: 100vh;
  background: #f7f9fb;

  .search-bar { padding: 16rpx 32rpx; background: #fff; }

  .filter-row {
    background: #fff;
    padding: 0 32rpx 16rpx;
    .filter-tabs {
      display: flex; flex-wrap: wrap; gap: 14rpx;
      .filter-tab {
        font-size: 24rpx; color: #666;
        padding: 8rpx 22rpx; border-radius: 24rpx; background: #f5f5f5;
        &.active { background: #e6f7ed; color: #07c160; font-weight: bold; }
      }
    }
  }

  .list-scroll {
    height: calc(100vh - 200rpx);
    padding: 20rpx 32rpx;
    box-sizing: border-box;
  }

  .empty-state {
    display: flex; flex-direction: column; align-items: center; padding-top: 200rpx;
    .empty-icon { font-size: 72rpx; margin-bottom: 20rpx; }
    .empty-text { font-size: 28rpx; color: #999; }
  }

  .loading-tip, .no-more-tip {
    display: flex; justify-content: center; align-items: center;
    gap: 12rpx; padding: 24rpx 0; font-size: 24rpx; color: #999;
  }

  .detail-sheet {
    background: #fff;
    padding: 0 0 calc(20rpx + env(safe-area-inset-bottom));

    .detail-title {
      font-size: 30rpx; font-weight: bold; color: #333;
      text-align: center; padding: 28rpx 0 24rpx;
      border-bottom: 1rpx solid #f0f0f0;
    }

    .detail-body {
      padding: 24rpx 40rpx;
      .detail-item {
        display: flex; justify-content: space-between; align-items: flex-start;
        padding: 14rpx 0; border-bottom: 1rpx solid #f5f5f5;
        .dl { font-size: 26rpx; color: #999; flex-shrink: 0; }
        .dv { font-size: 26rpx; color: #333; text-align: right; max-width: 60%; line-height: 1.5; }
      }
      .detail-img {
        width: 100%; border-radius: 12rpx; margin-bottom: 8rpx;
      }
    }

    .detail-actions {
      padding: 20rpx 40rpx 0;
      display: flex; flex-direction: column; gap: 16rpx;
      button {
        width: 100%; height: 80rpx; line-height: 80rpx;
        font-size: 28rpx; font-weight: bold; border-radius: 40rpx; border: none; padding: 0;
        &::after { border: none; }
      }
      .btn-audit { background: #07c160; color: #fff; }
      .btn-distribute { background: #3b82f6; color: #fff; }
    }

    .action-cancel {
      height: 80rpx; line-height: 80rpx; text-align: center;
      font-size: 26rpx; color: #999; margin-top: 12rpx;
    }
  }

  .bottom-bar {
    position: fixed;
    bottom: 0; left: 0; width: 100%;
    background: #fff;
    padding: 20rpx 40rpx calc(20rpx + env(safe-area-inset-bottom));
    box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.04);
    box-sizing: border-box;

    .submit-btn {
      width: 100%; height: 88rpx; line-height: 88rpx;
      background: #07c160; color: #fff;
      font-size: 30rpx; font-weight: bold;
      border-radius: 44rpx; border: none;
      &::after { border: none; }
    }
  }
}
</style>
