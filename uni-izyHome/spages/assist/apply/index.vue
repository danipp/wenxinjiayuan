<template>
  <view class="apply-page">
    <!-- 搜索 -->
    <view class="search-bar">
      <u-search
        v-model="keyword"
        placeholder="搜索申请人"
        :show-action="false"
        @search="onSearch"
        @clear="onSearchClear"
      />
    </view>

    <!-- 筛选：类型 + 状态 -->
    <view class="filter-row">
      <view class="filter-tabs">
        <view
          v-for="t in typeTabs"
          :key="t.value"
          class="filter-tab"
          :class="{ active: activeAssistanceType === t.value }"
          @click="switchTypeTab(t.value)"
        >{{ t.label }}</view>
      </view>
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
        <text class="empty-icon">🤝</text>
        <text class="empty-text">暂无帮扶申请</text>
      </view>

      <ApplyCard
        v-for="item in list"
        :key="item.applyId"
        :apply="item"
        @click="openDetail(item)"
      />

      <view v-if="loading" class="loading-tip">
        <u-loading-icon></u-loading-icon>
        <text>加载中...</text>
      </view>
      <view v-if="noMore && list.length > 0" class="no-more-tip">已加载全部数据</view>
    </scroll-view>

    <!-- 底部提交按钮 -->
    <view class="bottom-bar">
      <button class="submit-btn" @click="showForm = true">提交帮扶申请</button>
    </view>

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
        <view class="detail-title">{{ currentApply.applicantName || '--' }}</view>
        <view class="detail-body">
          <view class="detail-item"><text class="dl">帮扶类型</text><text class="dv">{{ typeLabel(currentApply) }}</text></view>
          <view class="detail-item"><text class="dl">联系电话</text><text class="dv">{{ currentApply.applicantPhone || '--' }}</text></view>
          <view class="detail-item" v-if="currentApply.idCard"><text class="dl">身份证号</text><text class="dv">{{ currentApply.idCard }}</text></view>
          <view class="detail-item" v-if="currentApply.address"><text class="dl">居住地址</text><text class="dv">{{ currentApply.address }}</text></view>
          <view class="detail-item" v-if="currentApply.familySituation"><text class="dl">家庭情况</text><text class="dv">{{ currentApply.familySituation }}</text></view>
          <view class="detail-item"><text class="dl">困难描述</text><text class="dv">{{ currentApply.difficultyDesc || '--' }}</text></view>
          <view class="detail-item"><text class="dl">期望帮扶</text><text class="dv">{{ currentApply.desiredHelp || '--' }}</text></view>
          <view class="detail-item" v-if="currentApply.remark"><text class="dl">备注</text><text class="dv">{{ currentApply.remark }}</text></view>
          <view class="detail-item"><text class="dl">状态</text><text class="dv">{{ statusMap[currentApply.status] || currentApply.status }}</text></view>
          <view class="detail-item" v-if="currentApply.auditRemark"><text class="dl">审核备注</text><text class="dv">{{ currentApply.auditRemark }}</text></view>
          <view class="detail-item" v-if="currentApply.createTime"><text class="dl">创建时间</text><text class="dv">{{ formatTime(currentApply.createTime) }}</text></view>
        </view>
        <view v-if="currentApply.status === 'pending'" class="detail-actions">
          <button class="btn-audit" @click="openAudit(currentApply)">审核</button>
        </view>
        <view class="action-cancel" @click="showDetail = false">关闭</view>
      </view>
    </u-popup>

    <!-- 提交弹窗 -->
    <ApplyForm :show.sync="showForm" :communityId="communityId" @done="onFormDone" />

    <!-- 审核弹窗 -->
    <ApplyAuditPopup :show.sync="showAudit" :apply="currentApply" @done="onAuditDone" />
  </view>
</template>

<script>
import ApplyCard from './components/ApplyCard.vue';
import ApplyForm from './components/ApplyForm.vue';
import ApplyAuditPopup from './components/ApplyAuditPopup.vue';
import { page11 } from '@/spages/api/apply';

export default {
  components: { ApplyCard, ApplyForm, ApplyAuditPopup },
  data() {
    return {
      keyword: '',
      activeAssistanceType: '',
      activeStatus: '',
      typeTabs: [
        { label: '全部', value: '' },
        { label: '生活', value: 'living' },
        { label: '医疗', value: 'medical' },
        { label: '教育', value: 'education' },
        { label: '就业', value: 'employment' },
      ],
      statusTabs: [
        { label: '全部', value: '' },
        { label: '待审核', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
      ],
      statusMap: { pending: '待审核', approved: '已通过', rejected: '已驳回' },
      list: [],
      pageNumber: 0,
      pageSize: 15,
      loading: false,
      refreshing: false,
      noMore: false,
      showForm: false,
      showDetail: false,
      showAudit: false,
      currentApply: {},
      communityId: '',
    };
  },
  onLoad() {
    const community = uni.getStorageSync('selected_community');
    this.communityId = (community && community.communityId) ? community.communityId : '';
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
        if (this.activeAssistanceType) payload.assistanceType = this.activeAssistanceType;
        if (this.activeStatus) payload.status = this.activeStatus;

        const res = await page11(payload);
        if (res.code === '00000' && res.data) {
          let { content = [], last } = res.data;
          if (this.keyword) {
            content = content.filter(
              (item) => item.applicantName && item.applicantName.indexOf(this.keyword) !== -1,
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

    switchTypeTab(v) { if (this.activeAssistanceType === v) return; this.activeAssistanceType = v; this.fetchList(); },
    switchStatusTab(v) { if (this.activeStatus === v) return; this.activeStatus = v; this.fetchList(); },

    async onRefresh() { this.refreshing = true; await this.fetchList(); this.refreshing = false; },
    onLoadMore() { if (this.noMore || this.loading) return; this.pageNumber++; this.fetchList(true); },

    openDetail(item) { this.currentApply = item; this.showDetail = true; },
    openAudit(item) { this.showDetail = false; this.currentApply = item; this.$nextTick(() => { this.showAudit = true; }); },

    onFormDone() { this.fetchList(); },
    onAuditDone() { this.fetchList(); },

    typeLabel(apply) {
      const map = { living: '生活', medical: '医疗', education: '教育', employment: '就业' };
      return map[apply.assistanceType] || apply.assistanceType || '--';
    },
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
.apply-page {
  min-height: 100vh;
  background: #f7f9fb;

  .search-bar { padding: 16rpx 32rpx; background: #fff; }

  .filter-row {
    background: #fff;
    padding: 0 32rpx 16rpx;
    display: flex;
    flex-direction: column;
    gap: 14rpx;

    .filter-tabs {
      display: flex;
      gap: 14rpx;
      flex-wrap: wrap;

      .filter-tab {
        font-size: 24rpx;
        color: #666;
        padding: 8rpx 22rpx;
        border-radius: 24rpx;
        background: #f5f5f5;

        &.active { background: #e6f7ed; color: #07c160; font-weight: bold; }
      }
    }
  }

  .list-scroll {
    height: calc(100vh - 340rpx);
    padding: 20rpx 32rpx;
    box-sizing: border-box;
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 200rpx;
    .empty-icon { font-size: 72rpx; margin-bottom: 20rpx; }
    .empty-text { font-size: 28rpx; color: #999; }
  }

  .loading-tip, .no-more-tip {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12rpx;
    padding: 24rpx 0;
    font-size: 24rpx;
    color: #999;
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

  .detail-sheet {
    background: #fff;
    padding: 0 0 calc(20rpx + env(safe-area-inset-bottom));

    .detail-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
      text-align: center;
      padding: 28rpx 0 24rpx;
      border-bottom: 1rpx solid #f0f0f0;
    }

    .detail-body {
      padding: 24rpx 40rpx;
      .detail-item {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        padding: 14rpx 0;
        border-bottom: 1rpx solid #f5f5f5;
        .dl { font-size: 26rpx; color: #999; flex-shrink: 0; }
        .dv { font-size: 26rpx; color: #333; text-align: right; max-width: 60%; line-height: 1.5; }
      }
    }

    .detail-actions {
      padding: 20rpx 40rpx 0;
      .btn-audit {
        width: 100%; height: 80rpx; line-height: 80rpx;
        background: #07c160; color: #fff;
        font-size: 28rpx; font-weight: bold;
        border-radius: 40rpx; border: none;
        &::after { border: none; }
      }
    }

    .action-cancel {
      height: 80rpx; line-height: 80rpx;
      text-align: center; font-size: 26rpx;
      color: #999; margin-top: 12rpx;
    }
  }
}
</style>
