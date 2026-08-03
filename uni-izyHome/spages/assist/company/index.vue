<template>
  <view class="company-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <u-search
        v-model="keyword"
        placeholder="搜索企业名称"
        :show-action="false"
        @search="onSearch"
        @clear="onSearchClear"
      />
    </view>

    <!-- 状态筛选 Tab -->
    <!-- <view class="filter-tabs">
      <view
        v-for="tab in statusTabs"
        :key="tab.value"
        class="filter-tab"
        :class="{ active: activeStatus === tab.value }"
        @click="switchTab(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view> -->

    <!-- 企业列表 -->
    <scroll-view
      class="list-scroll"
      scroll-y
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view v-if="list.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无爱心企业</text>
      </view>

      <EnterpriseCard
        v-for="item in list"
        :key="item.enterpriseId"
        :enterprise="item"
        @click="editEnterprise(item)"
      />

      <view v-if="loading" class="loading-tip">
        <u-loading-icon></u-loading-icon>
        <text>加载中...</text>
      </view>
      <view v-if="noMore && list.length > 0" class="no-more-tip">
        已加载全部数据
      </view>
    </scroll-view>

    <!-- 新增按钮 -->
    <view class="fab-btn" @click="addEnterprise">
      <u-icon name="plus" color="#fff" size="28" />
    </view>

    <!-- 新增 / 编辑弹窗 -->
    <EnterpriseForm
      :show.sync="showForm"
      :editData="currentEdit"
      :communityId="communityId"
      @done="onFormDone"
    />

    <!-- 详情 / 操作弹窗 -->
    <u-popup
      :show="showAction"
      mode="bottom"
      round="20"
      @close="showAction = false"
      :safeAreaInsetBottom="true"
      @touchmove.stop.prevent
    >
      <view class="action-sheet">
        <view class="action-title">{{
          currentEdit ? currentEdit.name : ""
        }}</view>
        <view class="action-item" @click="toggleStatusAction">
          {{
            currentEdit && currentEdit.status === "0" ? "上架企业" : "下架企业"
          }}
        </view>
        <view class="action-item action-edit" @click="openEditFromAction">
          编辑信息
        </view>
        <view class="action-item action-delete" @click="confirmDelete">
          删除企业
        </view>
        <view class="action-cancel" @click="showAction = false">取消</view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import EnterpriseCard from "./components/EnterpriseCard.vue";
import EnterpriseForm from "./components/EnterpriseForm.vue";
import { page8, delete3, toggleStatus4 } from "@/spages/api/company";

export default {
  components: { EnterpriseCard, EnterpriseForm },
  data() {
    return {
      keyword: "",
      activeStatus: "",
      statusTabs: [
        { label: "全部", value: "" },
        { label: "上架", value: "1" },
        { label: "下架", value: "0" },
      ],
      list: [],
      pageNumber: 0,
      pageSize: 15,
      totalElements: 0,
      loading: false,
      refreshing: false,
      noMore: false,
      showForm: false,
      showAction: false,
      currentEdit: null,
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
  onShow() {
    // 返回页面时刷新（新建/编辑后可能已刷新）
  },
  methods: {
    // -------- 列表请求 --------
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
          communityId: this.communityId || undefined,
        };
        if (this.keyword) payload.name = this.keyword;
        if (this.activeStatus)
          payload.status =
            this.activeStatus == 1
              ? "上架"
              : this.activeStatus == 0
              ? "下架"
              : "";

        const res = await page8(payload);
        if (res.code === "00000" && res.data) {
          const { content = [], last, totalElements } = res.data;
          this.list = append ? this.list.concat(content) : content;
          this.totalElements = totalElements || 0;
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

    // -------- 搜索 --------
    onSearch() {
      this.fetchList();
    },
    onSearchClear() {
      this.fetchList();
    },

    // -------- 状态筛选 --------
    switchTab(value) {
      if (this.activeStatus === value) return;
      this.activeStatus = value;
      this.fetchList();
    },

    // -------- 下拉刷新 --------
    async onRefresh() {
      this.refreshing = true;
      await this.fetchList();
      this.refreshing = false;
    },

    // -------- 加载更多 --------
    onLoadMore() {
      if (this.noMore || this.loading) return;
      this.pageNumber++;
      this.fetchList(true);
    },

    // -------- 新增 --------
    addEnterprise() {
      this.currentEdit = null;
      this.showForm = true;
    },

    // -------- 编辑 --------
    editEnterprise(item) {
      this.currentEdit = item;
      this.showAction = true;
    },
    openEditFromAction() {
      this.showAction = false;
      this.showForm = true;
    },

    // -------- 表单提交后 --------
    onFormDone() {
      this.fetchList();
    },

    // -------- 删除 --------
    async confirmDelete() {
      const id = this.currentEdit && this.currentEdit.enterpriseId;
      if (!id) return;
      this.showAction = false;

      const confirmRes = await new Promise((resolve) => {
        uni.showModal({
          title: "确认删除",
          content: `确定要删除"${this.currentEdit.name}"吗？此操作不可恢复。`,
          confirmColor: "#ef4444",
          success: (r) => resolve(r.confirm),
        });
      });
      if (!confirmRes) return;

      try {
        const res = await delete3(id);
        if (res.code === "00000") {
          uni.showToast({ title: "删除成功", icon: "none" });
          this.currentEdit = null;
          this.fetchList();
        } else {
          uni.showToast({ title: res.msg || "删除失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "删除失败", icon: "none" });
      }
    },

    // -------- 上下架切换 --------
    async toggleStatusAction() {
      const id = this.currentEdit && this.currentEdit.enterpriseId;
      if (!id) return;
      this.showAction = false;

      const newStatus = this.currentEdit.status === "0" ? "上架" : "下架";
      const confirmRes = await new Promise((resolve) => {
        uni.showModal({
          title: "操作确认",
          content: `确定要${newStatus}"${this.currentEdit.name}"吗？`,
          confirmColor: "#07c160",
          success: (r) => resolve(r.confirm),
        });
      });
      if (!confirmRes) return;

      try {
        const res = await toggleStatus4(id);
        if (res.code === "00000") {
          uni.showToast({ title: `${newStatus}成功`, icon: "none" });
          this.currentEdit = null;
          this.fetchList();
        } else {
          uni.showToast({ title: res.msg || "操作失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "操作失败", icon: "none" });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.company-page {
  min-height: 100vh;
  background: #f7f9fb;

  .search-bar {
    padding: 16rpx 32rpx;
    background: #fff;
  }

  .filter-tabs {
    display: flex;
    background: #fff;
    padding: 0 32rpx 16rpx;

    .filter-tab {
      font-size: 26rpx;
      color: #666;
      padding: 10rpx 28rpx;
      border-radius: 28rpx;
      margin-right: 20rpx;
      background: #f5f5f5;

      &.active {
        background: #e6f7ed;
        color: #07c160;
        font-weight: bold;
      }
    }
  }

  .list-scroll {
    height: calc(100vh - 100rpx);
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

  .fab-btn {
    position: fixed;
    bottom: calc(80rpx + env(safe-area-inset-bottom));
    right: 40rpx;
    width: 96rpx;
    height: 96rpx;
    border-radius: 48rpx;
    background: #07c160;
    box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.35);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 99;
  }

  .action-sheet {
    background: #fff;
    padding: 20rpx 0 calc(20rpx + env(safe-area-inset-bottom));

    .action-title {
      font-size: 28rpx;
      font-weight: bold;
      color: #333;
      text-align: center;
      padding: 20rpx 0 30rpx;
      border-bottom: 1rpx solid #f0f0f0;
      margin: 0 40rpx;
    }

    .action-item {
      height: 96rpx;
      line-height: 96rpx;
      text-align: center;
      font-size: 30rpx;
      color: #333;
      border-bottom: 1rpx solid #f5f5f5;

      &.action-delete {
        color: #ef4444;
      }
    }

    .action-cancel {
      height: 96rpx;
      line-height: 96rpx;
      text-align: center;
      font-size: 28rpx;
      color: #999;
      margin-top: 12rpx;
      background: #f5f5f5;
    }
  }
}
</style>
