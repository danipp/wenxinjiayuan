<template>
  <view class="records-page">
    <view class="filter-box">
      <DropdownFilter v-model="filterParams" />
    </view>

    <!-- 列表（高度扣除筛选栏高度） -->
    <scroll-view
      scroll-y
      class="list-scroll"
      refresher-enabled
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view class="card-list" v-if="recordsList.length > 0">
        <!-- 复用“做好事”板式的卡片结构 -->
        <view
          v-for="item in recordsList"
          :key="item.id"
          class="deeds-card"
          @click="goDetail(item.id)"
        >
          <view class="card-header">
            <text class="title">{{ item.title }}</text>
            <text class="badge-status" :class="item.status">{{ item.statusText }}</text>
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
        </view>
      </view>

      <!-- 完美还原：绿色眨眼药盒缺省页 -->
      <view v-else class="empty-state">
        <view class="pill-box-icon">
          <view class="face">
            <view class="eye winking"></view>
            <view class="eye normal"></view>
            <view class="smile"></view>
          </view>
        </view>
        <text class="empty-text">暂无记录</text>
        <text class="pull-tips">（下拉刷新可加载模拟数据测试）</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import DropdownFilter from "@/components/DropdownFilter.vue";

export default {
  components: { DropdownFilter },
  data() {
    return {
      filterParams: { status: "all", requirement: "all", sort: "default" },
      recordsList: [], // 默认为空
      isRefreshing: false,
    };
  },
  methods: {
    goDetail(id) {
      uni.navigateTo({
        url: `/spages/deeds/detail?id=${id}`,
      });
    },
    onRefresh() {
      this.isRefreshing = true;
      // 模拟下拉加载出数据
      setTimeout(() => {
        this.recordsList = [
          {
            id: 2,
            title: "上门除尘与衣物整理",
            status: "pending",
            statusText: "待帮忙",
            time: "本周六下午 (双方协商)",
            location: "越秀区青菜岗43号启东楼",
          },
          {
            id: 3,
            title: "陪同医院做检查与取药",
            status: "helping",
            statusText: "已接单",
            time: "12-28 09:00",
            location: "广州市越秀区中医医院",
          },
          {
            id: 5,
            title: "陪同复诊取药",
            status: "toEvaluate",
            statusText: "待评价",
            time: "07-05 09:00 至 07-05 11:00",
            location: "广州医科大学附属第二医院",
          },
          {
            id: 1,
            title: "代买东西",
            status: "completed",
            statusText: "已完成",
            time: "双方协商",
            location: "海心沙亚运公园",
          },
          {
            id: 4,
            title: "地点位置校验",
            status: "expired",
            statusText: "已过期",
            time: "2025年12月27日 星期六 17:15",
            location: "启东楼-广东省广州市越秀区青菜岗43号",
          },
        ];
        this.isRefreshing = false;
        uni.showToast({ title: "刷新成功", icon: "none" });
      }, 1000);
    },
    loadMore() {
      console.log("加载下一页...");
    },
  },
};
</script>

<style lang="scss" scoped>
@import "./records.scss"; // 抽取复用卡片与精美缺省页样式至 scss
</style>