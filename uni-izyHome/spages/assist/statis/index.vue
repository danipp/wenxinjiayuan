<template>
  <view class="statis-page">
    <!-- 顶部总计卡片 -->
    <view class="summary-card">
      <view class="summary-header">
        <text class="summary-title">互助统计</text>
        <text class="summary-community" @click="openCommunitySelector">
          {{ communityName || "选择社区 ▾" }}
        </text>
      </view>
      <view class="summary-grid">
        <view
          class="summary-item"
          v-for="s in list"
          :key="s.statId"
          @longpress="editStat(s)"
        >
          <text class="summary-value">{{
            s.isCustom ? formatNum(s.statValue) : s.statValue
          }}</text>
          <text class="summary-label">{{ s.statLabel }}</text>
        </view>
        <view v-if="list.length === 0 && !loading" class="summary-empty">
          <text class="empty-text">暂无统计数据</text>
        </view>
      </view>
    </view>

    <!-- 统计卡片网格 -->
    <view class="stat-grid" v-if="list.length > 0">
      <view class="stat-grid-item" v-for="s in list" :key="s.statId">
        <StatCard :stat="s" @longpress="editStat" />
      </view>
    </view>

    <view v-if="loading" class="loading-tip">
      <u-loading-icon></u-loading-icon>
      <text>加载中...</text>
    </view>

    <!-- 操作按钮组 -->
    <view class="bottom-bar">
      <button class="btn-init" @click="handleInitDefault">初始化默认项</button>
      <button class="btn-add" @click="addStat">新增统计项</button>
    </view>

    <!-- 新增/编辑弹窗 -->
    <StatForm
      :show.sync="showForm"
      :editData="currentEdit"
      :communityId="communityId"
      @done="onFormDone"
    />

    <!-- 长按操作 -->
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
          currentEdit ? currentEdit.statLabel : ""
        }}</view>
        <view class="action-item" @click="openEditFromAction">编辑</view>
        <view class="action-item action-delete" @click="confirmDelete"
          >删除</view
        >
        <view class="action-cancel" @click="showAction = false">取消</view>
      </view>
    </u-popup>
    <!-- 社区选择弹窗 -->
    <CommunitySelector
      :show.sync="showCommunitySelector"
      title="选择社区查看统计"
      mode="select"
      :nocache="false"
      @confirm="onCommunityConfirm"
    />
  </view>
</template>

<script>
import StatCard from "./components/StatCard.vue";
import StatForm from "./components/StatForm.vue";
import CommunitySelector from "@/components/community.vue";
import { list4, delete2, initDefault } from "@/spages/api/statis";

export default {
  components: { StatCard, StatForm, CommunitySelector },
  data() {
    return {
      list: [],
      loading: false,
      showForm: false,
      showAction: false,
      showCommunitySelector: false,
      currentEdit: null,
      communityId: "",
      communityName: "",
    };
  },
  onLoad() {
    const community = uni.getStorageSync("selected_community");
    if (community && community.communityId) {
      this.communityId = community.communityId;
      this.communityName = community.name || "";
    }
    if (this.communityId) {
      this.fetchList();
    }
  },
  methods: {
    async fetchList() {
      if (!this.communityId) return;
      this.loading = true;
      try {
        const res = await list4({
          mode: "all",
          communityId: this.communityId || undefined,
        });
        if (res.code === "00000") {
          this.list = (Array.isArray(res.data) ? res.data : []).sort(
            (a, b) => (b.displayOrder || 0) - (a.displayOrder || 0)
          );
        } else {
          uni.showToast({ title: res.msg || "加载失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },

    addStat() {
      this.currentEdit = null;
      this.showForm = true;
    },

    editStat(item) {
      this.currentEdit = item;
      this.showAction = true;
    },
    openEditFromAction() {
      this.showAction = false;
      this.showForm = true;
    },

    // 删除
    async confirmDelete() {
      const id = this.currentEdit && this.currentEdit.statId;
      if (!id) return;
      this.showAction = false;
      const confirmRes = await new Promise((resolve) => {
        uni.showModal({
          title: "确认删除",
          content: `确定要删除"${this.currentEdit.statLabel}"吗？`,
          confirmColor: "#ef4444",
          success: (r) => resolve(r.confirm),
        });
      });
      if (!confirmRes) return;
      try {
        const res = await delete2(id);
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

    // 初始化默认
    async handleInitDefault() {
      const confirmRes = await new Promise((resolve) => {
        uni.showModal({
          title: "初始化默认统计项",
          content: "将会创建默认的统计项，已有的不会重复创建，确定吗？",
          confirmColor: "#07c160",
          success: (r) => resolve(r.confirm),
        });
      });
      if (!confirmRes) return;
      try {
        const res = await initDefault();
        if (res.code === "00000") {
          uni.showToast({ title: "初始化成功", icon: "none" });
          this.fetchList();
        } else {
          uni.showToast({ title: res.msg || "初始化失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "初始化失败", icon: "none" });
      }
    },

    onFormDone() {
      this.fetchList();
    },

    // 社区选择
    openCommunitySelector() {
      this.showCommunitySelector = true;
    },
    onCommunityConfirm(data) {
      if (data && data.community) {
        this.communityId = data.community.communityId;
        this.communityName = data.community.name;
        this.fetchList();
      }
    },

    formatNum(v) {
      if (v >= 10000) return (v / 10000).toFixed(1) + "w";
      if (v >= 1000) return (v / 1000).toFixed(1) + "k";
      return String(v);
    },
  },
};
</script>

<style lang="scss" scoped>
.statis-page {
  min-height: 100vh;
  background: #f7f9fb;
  padding-bottom: calc(140rpx + env(safe-area-inset-bottom));

  .summary-card {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    margin: 24rpx 32rpx;
    border-radius: 24rpx;
    padding: 36rpx 32rpx;
    box-shadow: 0 8rpx 28rpx rgba(59, 130, 246, 0.3);

    .summary-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 28rpx;

      .summary-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #fff;
      }
      .summary-community {
        font-size: 22rpx;
        color: rgba(255, 255, 255, 0.9);
        background: rgba(255, 255, 255, 0.15);
        padding: 6rpx 20rpx;
        border-radius: 20rpx;
      }
    }

    .summary-grid {
      display: flex;
      flex-wrap: wrap;

      .summary-item {
        width: 50%;
        margin-bottom: 24rpx;
        .summary-value {
          font-size: 44rpx;
          font-weight: 900;
          color: #fff;
          display: block;
        }
        .summary-label {
          font-size: 22rpx;
          color: rgba(255, 255, 255, 0.8);
          margin-top: 4rpx;
        }
      }

      .summary-empty {
        width: 100%;
        display: flex;
        justify-content: center;
        .empty-text {
          font-size: 26rpx;
          color: rgba(255, 255, 255, 0.6);
        }
      }
    }
  }

  .stat-grid {
    display: flex;
    flex-wrap: wrap;
    padding: 0 32rpx;

    .stat-grid-item {
      width: 50%;
      padding: 0 8rpx 16rpx;
      box-sizing: border-box;
    }
  }

  .loading-tip {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12rpx;
    padding: 48rpx 0;
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
    display: flex;
    gap: 20rpx;

    button {
      flex: 1;
      height: 88rpx;
      line-height: 88rpx;
      font-size: 28rpx;
      font-weight: bold;
      border-radius: 44rpx;
      border: none;
      padding: 0;
      &::after {
        border: none;
      }
    }
    .btn-init {
      background: #f0f0f0;
      color: #333;
    }
    .btn-add {
      background: #07c160;
      color: #fff;
    }
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
