<template>
  <view class="square-container">
    <!-- 头部栏 -->
    <view class="square-header">
      <text class="header-title">热门活动</text>
      <view class="filter-btn" @click="openFilter">
        <text class="filter-text">筛选</text>
        <u-icon name="grid-fill" color="#555" size="14"></u-icon>
      </view>
    </view>

    <!-- 瀑布流活动列表 -->
    <view class="grid-list" v-if="activities.length">
      <view
        v-for="item in activities"
        :key="item.templateId"
        class="grid-item"
        @click="goDetail(item.templateId)"
      >
        <view class="image-box">
          <image
            class="activity-img"
            :src="item.image"
            mode="aspectFill"
          ></image>
          <!-- 参与人数遮罩 -->
          <view class="p-mask">
            <text class="p-text">{{ item.participants || "0" }}人参与</text>
          </view>
          <!-- 活动标签 -->
          <view v-if="item.tag" class="tag-badge">{{ item.tag }}</view>
        </view>
        <view class="content-box">
          <text class="title-text text-ellipsis-2">{{ item.title }}</text>
        </view>
      </view>
    </view>
    <u-empty v-else :text="loading ? '' : '暂无活动模板'"></u-empty>
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-tip">加载中...</view>
    <view v-if="!hasMore && activities.length > 0" class="loading-tip"
      >没有更多了</view
    >

    <!-- 筛选顶部半屏弹窗 -->
    <u-popup
      :show="filterShow"
      mode="top"
      @close="filterShow = false"
      round="16"
    >
      <view class="filter-panel-box">
        <!-- 排列方式 -->
        <view class="filter-group">
          <view class="group-title">排列方式</view>
          <view class="tag-row">
            <view
              v-for="t in sortOptions"
              :key="t.val"
              class="filter-tag"
              :class="{ 'tag-active': tempFilters.sort === t.val }"
              @click="tempFilters.sort = t.val"
            >
              {{ t.name }}
            </view>
          </view>
        </view>

        <!-- 参与人数 -->
        <view class="filter-group">
          <view class="group-title">参与人数</view>
          <view class="tag-row">
            <view
              v-for="p in rangeOptions"
              :key="p.val"
              class="filter-tag"
              :class="{ 'tag-active': tempFilters.range === p.val }"
              @click="tempFilters.range = p.val"
            >
              {{ p.name }}
            </view>
          </view>
        </view>

        <!-- 活动类型 -->
        <view class="filter-group">
          <view class="group-title">活动类型</view>
          <view class="tag-row">
            <view
              v-for="y in typeOptions"
              :key="y.val"
              class="filter-tag"
              :class="{ 'tag-active': tempFilters.type === y.val }"
              @click="tempFilters.type = y.val"
            >
              {{ y.name }}
            </view>
          </view>
        </view>

        <!-- 确认重置按钮 -->
        <view class="filter-actions">
          <button class="f-btn btn-reset" @click="resetFilters">重置</button>
          <button class="f-btn btn-done" @click="applyFilters">完成</button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { list3 } from "@/spages/api/square";

export default {
  data() {
    return {
      filterShow: false,
      loading: false,
      // 分页参数
      pageNumber: 1,
      pageSize: 10,
      hasMore: true,
      // 临时选择的筛选状态
      tempFilters: {
        sort: "new",
        range: "0-50",
        type: "online",
      },
      // 确认生效的筛选状态
      activeFilters: {
        sort: "new",
        range: "0-50",
        type: "online",
      },
      sortOptions: [
        { name: "新发布", val: "new" },
        { name: "使用次数多", val: "use" },
        { name: "参与用户多", val: "user" },
      ],
      rangeOptions: [
        { name: "0-50", val: "0-50" },
        { name: "50-100", val: "50-100" },
        { name: "100以上", val: "100+" },
      ],
      typeOptions: [
        { name: "线上活动", val: "online" },
        { name: "线下活动", val: "offline" },
      ],
      activities: [],
    };
  },
  onLoad() {
    this.fetchList();
  },
  methods: {
    // 获取活动模板列表
    async fetchList() {
      if (this.loading || !this.hasMore) return;
      this.loading = true;
      try {
        const res = await list3({
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          category: this.activeFilters.type,
        });
        // 接口返回的是数组（分页数据在 data 中）
        const list = res.data || res;
        if (Array.isArray(list) && list.length > 0) {
          this.activities =
            this.pageNumber === 1 ? list : [...this.activities, ...list];
          this.hasMore = list.length >= this.pageSize;
          this.pageNumber++;
        } else {
          this.hasMore = false;
        }
      } catch (e) {
        uni.showToast({ title: "加载失败，请重试", icon: "none" });
      } finally {
        this.loading = false;
      }
    },
    openFilter() {
      this.tempFilters = { ...this.activeFilters };
      this.filterShow = true;
    },
    resetFilters() {
      this.tempFilters = {
        sort: "new",
        range: "0-50",
        type: "online",
      };
    },
    applyFilters() {
      this.activeFilters = { ...this.tempFilters };
      this.filterShow = false;
      // 筛选变更后重新加载
      this.pageNumber = 1;
      this.hasMore = true;
      this.activities = [];
      this.fetchList();
    },
    goDetail(id) {
      uni.navigateTo({
        url: `/spages/fun/squareDetail?id=${id}`,
      });
    },
  },
  // 下拉刷新
  onPullDownRefresh() {
    this.pageNumber = 1;
    this.hasMore = true;
    this.activities = [];
    this.fetchList().finally(() => {
      uni.stopPullDownRefresh();
    });
  },
  // 触底加载更多
  onReachBottom() {
    this.fetchList();
  },
};
</script>

<style lang="scss" scoped>
.square-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx;
  box-sizing: border-box;

  .square-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 32rpx;

    .header-title {
      font-size: 40rpx;
      font-weight: 800;
      color: #1a202c;
    }

    .filter-btn {
      display: flex;
      align-items: center;
      background-color: #ffffff;
      padding: 12rpx 24rpx;
      border-radius: 30rpx;
      border: 2rpx solid #e2e8f0;

      .filter-text {
        font-size: 26rpx;
        color: #555555;
        margin-right: 8rpx;
      }
    }
  }

  /* 瀑布流/网格样式 */
  .grid-list {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24rpx;

    .grid-item {
      background-color: #ffffff;
      border-radius: 24rpx;
      overflow: hidden;
      box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.02);
      display: flex;
      flex-direction: column;

      .image-box {
        position: relative;
        width: 100%;
        height: 280rpx;

        .activity-img {
          width: 100%;
          height: 100%;
        }

        .p-mask {
          position: absolute;
          left: 0;
          bottom: 0;
          width: 100%;
          background: linear-gradient(
            180deg,
            rgba(0, 0, 0, 0) 0%,
            rgba(0, 0, 0, 0.6) 100%
          );
          padding: 16rpx;
          box-sizing: border-box;

          .p-text {
            color: #ffffff;
            font-size: 24rpx;
            font-weight: bold;
          }
        }

        .tag-badge {
          position: absolute;
          top: 16rpx;
          left: 16rpx;
          background-color: #fce8cd;
          color: #c27d1a;
          font-size: 22rpx;
          font-weight: bold;
          padding: 4rpx 12rpx;
          border-radius: 8rpx;
        }
      }

      .content-box {
        padding: 20rpx;
        display: flex;
        flex-direction: column;

        .title-text {
          font-size: 26rpx;
          font-weight: bold;
          color: #333333;
          height: 76rpx;
          line-height: 1.4;
          margin-bottom: 16rpx;
        }

        .author-box {
          display: flex;
          align-items: center;

          .avatar {
            width: 32rpx;
            height: 32rpx;
            border-radius: 50%;
            margin-right: 12rpx;
          }

          .name {
            font-size: 22rpx;
            color: #718096;
            flex: 1;
          }
        }
      }
    }
  }

  /* 筛选抽屉 */
  .filter-panel-box {
    padding: 48rpx 40rpx;
    background-color: #ffffff;

    .filter-group {
      margin-bottom: 40rpx;

      .group-title {
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 20rpx;
      }

      .tag-row {
        display: flex;
        flex-wrap: wrap;
        gap: 20rpx;

        .filter-tag {
          background-color: #f5f7fa;
          padding: 16rpx 28rpx;
          border-radius: 16rpx;
          font-size: 26rpx;
          color: #555555;
          transition: all 0.2s;

          &.tag-active {
            background-color: #e8f9f0;
            color: #07c160;
            font-weight: bold;
          }
        }
      }
    }

    .filter-actions {
      display: flex;
      gap: 24rpx;
      margin-top: 56rpx;

      .f-btn {
        flex: 1;
        height: 88rpx;
        line-height: 88rpx;
        font-size: 30rpx;
        font-weight: bold;
        border-radius: 44rpx;

        &::after {
          border: none;
        }

        &.btn-reset {
          background-color: #f5f7fa;
          color: #555555;
        }

        &.btn-done {
          background-color: #07c160;
          color: #ffffff;
        }
      }
    }
  }

  .text-ellipsis-2 {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

  .loading-tip {
    text-align: center;
    padding: 32rpx;
    font-size: 26rpx;
    color: #999;
  }
}
</style>