<template>
  <view class="donation-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <u-search
        v-model="keyword"
        placeholder="搜索联系人"
        :show-action="false"
        @search="onSearch"
        @clear="onSearchClear"
      />
    </view>

    <!-- 筛选 -->
    <view class="filter-row">
      <!-- 类型 -->
      <!-- <view class="filter-tabs">
        <view
          v-for="t in typeTabs"
          :key="t.value"
          class="filter-tab"
          :class="{ active: activeDonationType === t.value }"
          @click="switchTypeTab(t.value)"
        >
          {{ t.label }}
        </view>
      </view> -->
      <!-- 状态 -->
      <view class="filter-tabs">
        <view
          v-for="t in statusTabs"
          :key="t.value"
          class="filter-tab"
          :class="{ active: activeStatus === t.value }"
          @click="switchStatusTab(t.value)"
        >
          {{ t.label }}
        </view>
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
        <text class="empty-icon">💝</text>
        <text class="empty-text">暂无捐赠记录</text>
      </view>

      <DonationCard
        v-for="item in list"
        :key="item.donationId"
        :donation="item"
        @click="openDonationDetail(item)"
      />

      <view v-if="loading" class="loading-tip">
        <u-loading-icon></u-loading-icon>
        <text>加载中...</text>
      </view>
      <view v-if="noMore && list.length > 0" class="no-more-tip">
        已加载全部数据
      </view>
    </scroll-view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <button class="submit-btn" @click="showForm = true">提交捐赠申请</button>
    </view>

    <!-- 捐赠详情 / 操作为底部弹窗 -->
    <u-popup
      :show="showDetail"
      mode="bottom"
      round="20"
      @close="showDetail = false"
      :safeAreaInsetBottom="true"
      @touchmove.stop.prevent
    >
      <view class="detail-sheet">
        <view class="detail-title">{{
          currentDonation.contactName || "--"
        }}</view>
        <view class="detail-body">
          <view class="detail-item">
            <text class="dl">捐赠类型</text>
            <text class="dv">{{
              currentDonation.donationType === "money" ? "资金" : "物资"
            }}</text>
          </view>
          <view
            class="detail-item"
            v-if="currentDonation.donationType === 'money'"
          >
            <text class="dl">捐赠金额</text>
            <text class="dv amount">¥{{ currentDonation.amount || 0 }}</text>
          </view>
          <template v-else>
            <view class="detail-item">
              <text class="dl">物资名称</text>
              <text class="dv">{{ currentDonation.goodsName }}</text>
            </view>
            <view class="detail-item">
              <text class="dl">数量/估值</text>
              <text class="dv"
                >{{ currentDonation.goodsQuantity || 0 }} / ¥{{
                  currentDonation.goodsValue || 0
                }}</text
              >
            </view>
          </template>
          <view class="detail-item">
            <text class="dl">申请人</text>
            <text class="dv">{{
              currentDonation.userType === "enterprise" ? "企业" : "个人"
            }}</text>
          </view>
          <view class="detail-item">
            <text class="dl">联系电话</text>
            <text class="dv">{{ currentDonation.contactPhone || "--" }}</text>
          </view>
          <view class="detail-item">
            <text class="dl">状态</text>
            <text class="dv">{{
              statusMap[currentDonation.status] || currentDonation.status
            }}</text>
          </view>
          <view class="detail-item" v-if="currentDonation.remark">
            <text class="dl">备注</text>
            <text class="dv">{{ currentDonation.remark }}</text>
          </view>
          <view class="detail-item" v-if="currentDonation.auditRemark">
            <text class="dl">审核备注</text>
            <text class="dv">{{ currentDonation.auditRemark }}</text>
          </view>
          <view class="detail-item" v-if="currentDonation.createTime">
            <text class="dl">创建时间</text>
            <text class="dv">{{ formatTime(currentDonation.createTime) }}</text>
          </view>
        </view>
        <!-- 管理员审核：待审核时显示审核按钮 -->
        <view
          v-if="currentDonation.status === 'pending'"
          class="detail-actions"
        >
          <button class="btn-audit" @click="openAudit(currentDonation)">
            审核
          </button>
        </view>
        <view class="action-cancel" @click="showDetail = false">关闭</view>
      </view>
    </u-popup>

    <!-- 提交申请弹窗 -->
    <DonationForm
      :show.sync="showForm"
      :communityId="communityId"
      @done="onFormDone"
    />

    <!-- 审核弹窗 -->
    <AuditPopup
      :show.sync="showAudit"
      :donation="currentDonation"
      @done="onAuditDone"
    />
  </view>
</template>

<script>
import DonationCard from "./components/DonationCard.vue";
import DonationForm from "./components/DonationForm.vue";
import AuditPopup from "./components/AuditPopup.vue";
import { page9 } from "@/spages/api/donation";

export default {
  components: { DonationCard, DonationForm, AuditPopup },
  data() {
    return {
      keyword: "",
      activeDonationType: "",
      activeStatus: "",
      typeTabs: [
        { label: "全部", value: "" },
        { label: "资金", value: "money" },
        { label: "物资", value: "goods" },
      ],
      statusTabs: [
        { label: "全部", value: "" },
        { label: "待审核", value: "pending" },
        { label: "已通过", value: "approved" },
        { label: "已驳回", value: "rejected" },
      ],
      statusMap: { pending: "待审核", approved: "已通过", rejected: "已驳回" },
      list: [],
      pageNumber: 0,
      pageSize: 15,
      loading: false,
      refreshing: false,
      noMore: false,
      showForm: false,
      showDetail: false,
      showAudit: false,
      currentDonation: {},
      communityId: "",
    };
  },
  onLoad() {
    const community = uni.getStorageSync("selected_community");
    if (community && community.communityId) {
      this.communityId = community.communityId;
    }
    this.fetchList();
  },
  methods: {
    async fetchList(append = false) {
      if (this.loading) return;

      if (!append) {
        this.pageNumber = 0;
        this.list = [];
        this.noMore = false;
      }

      this.loading = true;
      try {
        const payload = {
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          role: "apply",
          communityId: this.communityId || undefined,
        };
        if (this.keyword)
          payload.userId =
            undefined; /* search by contactName not supported; pass-through */
        if (this.activeDonationType)
          payload.donationType = this.activeDonationType;
        if (this.activeStatus) payload.status = this.activeStatus;

        const res = await page9(payload);
        if (res.code === "00000" && res.data) {
          const { content = [], last } = res.data;
          // 客户端侧按联系人搜索
          let filtered = content;
          if (this.keyword) {
            filtered = content.filter(
              (item) =>
                item.contactName &&
                item.contactName.indexOf(this.keyword) !== -1
            );
          }
          this.list = append ? this.list.concat(filtered) : filtered;
          this.noMore = last !== false;
        } else {
          uni.showToast({ title: res.msg || "加载失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },

    onSearch() {
      this.fetchList();
    },
    onSearchClear() {
      this.fetchList();
    },

    switchTypeTab(v) {
      if (this.activeDonationType === v) return;
      this.activeDonationType = v;
      this.fetchList();
    },
    switchStatusTab(v) {
      if (this.activeStatus === v) return;
      this.activeStatus = v;
      this.fetchList();
    },

    async onRefresh() {
      this.refreshing = true;
      await this.fetchList();
      this.refreshing = false;
    },
    onLoadMore() {
      if (this.noMore || this.loading) return;
      this.pageNumber++;
      this.fetchList(true);
    },

    openDonationDetail(item) {
      this.currentDonation = item;
      this.showDetail = true;
    },

    openAudit(item) {
      this.showDetail = false;
      this.currentDonation = item;
      this.$nextTick(() => {
        this.showAudit = true;
      });
    },

    onFormDone() {
      this.fetchList();
    },
    onAuditDone() {
      this.fetchList();
    },

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
.donation-page {
  min-height: 100vh;
  background: #f7f9fb;

  .search-bar {
    padding: 16rpx 32rpx;
    background: #fff;
  }

  .filter-row {
    background: #fff;
    padding: 0 32rpx 16rpx;
    display: flex;
    flex-direction: column;
    gap: 14rpx;

    .filter-tabs {
      display: flex;
      gap: 14rpx;

      .filter-tab {
        font-size: 24rpx;
        color: #666;
        padding: 8rpx 22rpx;
        border-radius: 24rpx;
        background: #f5f5f5;

        &.active {
          background: #e6f7ed;
          color: #07c160;
          font-weight: bold;
        }
      }
    }
  }

  .list-scroll {
    height: calc(100vh - 276rpx);
    padding: 20rpx 32rpx;
    box-sizing: border-box;
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 200rpx;
    .empty-icon {
      font-size: 72rpx;
      margin-bottom: 20rpx;
    }
    .empty-text {
      font-size: 28rpx;
      color: #999;
    }
  }

  .loading-tip,
  .no-more-tip {
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
    bottom: 0;
    left: 0;
    width: 100%;
    background: #fff;
    padding: 20rpx 40rpx calc(20rpx + env(safe-area-inset-bottom));
    box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.04);
    box-sizing: border-box;

    .submit-btn {
      width: 100%;
      height: 88rpx;
      line-height: 88rpx;
      background: #07c160;
      color: #fff;
      font-size: 30rpx;
      font-weight: bold;
      border-radius: 44rpx;
      border: none;

      &::after {
        border: none;
      }
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
        align-items: center;
        padding: 16rpx 0;
        border-bottom: 1rpx solid #f5f5f5;

        .dl {
          font-size: 26rpx;
          color: #999;
        }
        .dv {
          font-size: 26rpx;
          color: #333;
          font-weight: 500;

          &.amount {
            color: #f59e0b;
            font-weight: bold;
            font-size: 30rpx;
          }
        }
      }
    }

    .detail-actions {
      padding: 20rpx 40rpx 0;

      .btn-audit {
        width: 100%;
        height: 80rpx;
        line-height: 80rpx;
        background: #07c160;
        color: #fff;
        font-size: 28rpx;
        font-weight: bold;
        border-radius: 40rpx;
        border: none;

        &::after {
          border: none;
        }
      }
    }

    .action-cancel {
      height: 80rpx;
      line-height: 80rpx;
      text-align: center;
      font-size: 26rpx;
      color: #999;
      margin-top: 12rpx;
    }
  }
}
</style>
