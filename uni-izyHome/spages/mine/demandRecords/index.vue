<template>
  <view class="records-page">
    <view class="filter-box">
      <DropdownFilter v-model="filterParams" />
    </view>

    <scroll-view
      scroll-y
      class="list-scroll"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view class="card-list" v-if="recordsList.length > 0">
        <view
          v-for="item in recordsList"
          :key="item.id"
          class="deeds-card"
          @click="goDetail(item.id)"
        >
          <view class="card-header">
            <text class="title">{{ item.title }}</text>
            <text class="badge-status" :class="item.status">{{
              item.statusText
            }}</text>
          </view>
          <view class="card-body">
            <view class="info-row"
              ><text class="label">时间：</text
              ><text class="val">{{ item.time }}</text></view
            >
            <view class="info-row"
              ><text class="label">地点：</text
              ><text class="val">{{ item.location }}</text></view
            >
          </view>
          <view
            v-if="item.status === 'helping'"
            class="card-footer"
            style="
              display: flex;
              justify-content: flex-end;
              margin-top: 16rpx;
              border-top: 1rpx dashed #edf0f2;
              padding-top: 24rpx;
            "
          >
            <button
              style="
                margin: 0;
                padding: 0 32rpx;
                font-size: 26rpx;
                height: 68rpx;
                line-height: 68rpx;
                border-radius: 34rpx;
                background-color: #07c160;
                color: #fff;
                box-shadow: 0 6rpx 16rpx rgba(7, 193, 96, 0.2);
                font-weight: bold;
              "
              @click.stop="handleComplete(item)"
            >
              确认完成
            </button>
          </view>
        </view>
      </view>

      <!-- 完美还原：缺省页 -->
      <view v-else class="empty-state">
        <div class="pill-box-icon">
          <div class="face">
            <div class="eye winking"></div>
            <div class="eye normal"></div>
            <div class="smile"></div>
          </div>
        </div>
        <text class="empty-text">暂无记录</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import DropdownFilter from "@/components/DropdownFilter.vue";
import { page3, complete } from "../../api/demand";

export default {
  components: { DropdownFilter },
  data() {
    return {
      filterParams: { status: "all", requirement: "all", sort: "default" },
      recordsList: [],
      isRefreshing: false,
      page: 1,
      pageSize: 10,
      loading: false,
      finished: false,
    };
  },
  onShow() {
    this.refresh();
  },
  watch: {
    filterParams: {
      deep: true,
      handler() {
        this.refresh();
      },
    },
  },
  methods: {
    goDetail(id) {
      uni.navigateTo({
        url: `/spages/deeds/detail?id=${id}`,
      });
    },
    getStatusString(statusInt) {
      switch (statusInt) {
        case 1:
          return "pending";
        case 2:
          return "helping";
        case 3:
          return "toEvaluate";
        case 4:
          return "completed";
        case 5:
          return "expired";
        default:
          return "pending";
      }
    },
    getStatusText(statusInt) {
      switch (statusInt) {
        case 1:
          return "待帮忙";
        case 2:
          return "已接单";
        case 3:
          return "待评价";
        case 4:
          return "已完成";
        case 5:
          return "已过期";
        default:
          return "未知";
      }
    },
    refresh() {
      this.page = 1;
      this.finished = false;
      this.recordsList = [];
      this.getList();
    },
    onRefresh() {
      this.isRefreshing = true;
      this.refresh();
      setTimeout(() => {
        this.isRefreshing = false;
      }, 500);
    },
    loadMore() {
      this.getList();
    },
    async getList() {
      if (this.loading || this.finished) return;
      this.loading = true;

      let apiStatus = this.filterParams.status;
      if (apiStatus === "matched") apiStatus = "helping";

      let apiSort = "desc";
      if (this.filterParams.sort === "asc") apiSort = "asc";

      try {
        const res = await page3({
          pageNumber: this.page,
          pageSize: this.pageSize,
          role: 1, // 1 是发布者视角
          status: apiStatus,
          requirement:
            this.filterParams.requirement === "all"
              ? ""
              : this.filterParams.requirement,
          sort: apiSort,
        });

        if (res.code === "00000") {
          const list = (res.data?.content || []).map((item) => {
            return {
              id: item.demandId || item.id,
              title: item.title,
              status: this.getStatusString(item.status),
              statusText: this.getStatusText(item.status),
              time: item.serviceTime || "双方协商",
              location: item.location || item.memberAddress || "",
            };
          });

          if (this.page === 1) {
            this.recordsList = list;
          } else {
            this.recordsList = this.recordsList.concat(list);
          }

          this.finished = res.data?.last ?? list.length < this.pageSize;
          this.page++;
        }
      } catch (error) {
        console.error("获取记录失败", error);
      } finally {
        this.loading = false;
      }
    },
    handleComplete(item) {
      uni.showModal({
        title: "确认完成",
        content: `确定该需求 (${item.title}) 已由志愿者服务完成吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              uni.showLoading({ title: "提交中..." });
              const result = await complete(item.id);
              uni.hideLoading();
              if (result.code === "00000") {
                uni.showToast({ title: "已确认完成", icon: "success" });
                this.refresh();
              } else {
                uni.showToast({
                  title: result.msg || "操作失败",
                  icon: "none",
                });
              }
            } catch (error) {
              uni.hideLoading();
              uni.showToast({ title: "操作失败", icon: "none" });
            }
          }
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./records.scss";
</style>