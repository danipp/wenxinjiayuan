<template>
  <view class="rank-container">
    <!-- 顶部渐变装饰背景与标签页 -->
    <view class="rank-header-bg">
      <view class="tabs-row">
        <view
          class="tab-item"
          :class="{ 'tab-item-active': activeTab === 'mutual' }"
          @click="switchTab('mutual')"
        >
          互助达人
        </view>
        <view
          class="tab-item"
          :class="{ 'tab-item-active': activeTab === 'activity' }"
          @click="switchTab('activity')"
        >
          活动达人
        </view>
      </view>
    </view>

    <!-- 达人排行榜列表 -->
    <scroll-view scroll-y class="list-scroll-view" @scrolltolower="loadMore">
      <template v-if="!currentEmpty">
        <view class="rank-list">
          <view
            v-for="(item, index) in currentList"
            :key="item.id"
            class="rank-row"
          >
            <!-- 排名数字（前三名特殊着色） -->
            <text class="rank-number" :class="'rank-' + (index + 1)">
              {{ index + 1 }}
            </text>

            <!-- 头像 -->
            <image
              class="user-avatar"
              :src="item.avatar"
              mode="aspectFill"
            ></image>

            <!-- 达人信息 -->
            <view class="user-info">
              <text class="user-name">{{ item.name }}</text>
              <text class="user-score">
                {{
                  activeTab === "mutual"
                    ? "帮助 " + item.score + " 人"
                    : "参与活动 " + item.score + " 次"
                }}
              </text>
            </view>
          </view>
        </view>
        <view class="loadmore-container" style="padding: 20rpx 0 40rpx 0">
          <u-loadmore :status="currentStatus" />
        </view>
      </template>
      <view v-else class="empty-container" style="padding-top: 200rpx">
        <u-empty mode="data" text="暂无达人数据"></u-empty>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { list as getDarenList } from "../api/daren";

export default {
  data() {
    return {
      activeTab: "mutual", // 当前选中的 Tab：'mutual'(互助达人) 或 'activity'(活动达人)
      communityName: "中山一社区", // 社区名称
      // 互助达人榜单数据
      mutualList: [],
      mutualPage: 1,
      mutualStatus: "loadmore",
      mutualEmpty: false,
      // 活动达人榜单数据
      activityList: [],
      activityPage: 1,
      activityStatus: "loadmore",
      activityEmpty: false,
    };
  },
  computed: {
    // 动态获取当前选中 Tab 的榜单列表
    currentList() {
      return this.activeTab === "mutual" ? this.mutualList : this.activityList;
    },
    currentStatus() {
      return this.activeTab === "mutual"
        ? this.mutualStatus
        : this.activityStatus;
    },
    currentEmpty() {
      return this.activeTab === "mutual"
        ? this.mutualEmpty
        : this.activityEmpty;
    },
  },
  onLoad(options) {
    // 动态获取页面传参的社区名称，默认为“中山一社区”
    const cachedLocation = uni.getStorageSync("selected_community");
    if (cachedLocation && cachedLocation.name) {
      this.communityName = cachedLocation.name;
    }
    this.updatePageTitle();
    this.fetchDarenList(1);
    this.fetchDarenList(2);
  },
  methods: {
    // 设置页面导航栏标题
    updatePageTitle() {
      uni.setNavigationBarTitle({
        title: `${this.communityName}达人周榜`,
      });
    },
    // 获取达人列表
    async fetchDarenList(type, isLoadMore = false) {
      let page = type === 1 ? this.mutualPage : this.activityPage;
      let status = type === 1 ? this.mutualStatus : this.activityStatus;

      if (status === "nomore" && isLoadMore) return;

      if (isLoadMore) {
        page++;
        if (type === 1) this.mutualPage = page;
        else this.activityPage = page;
      } else {
        page = 1;
        if (type === 1) {
          this.mutualPage = page;
          this.mutualList = [];
        } else {
          this.activityPage = page;
          this.activityList = [];
        }
      }

      if (type === 1) this.mutualStatus = "loading";
      else this.activityStatus = "loading";

      try {
        const res = await getDarenList({
          type: type,
          community: this.communityName,
          pageNumber: page,
          limit: 20,
        });
        if (res.code === "00000") {
          let listData = [];
          if (Array.isArray(res.data)) {
            listData = res.data;
          } else if (res.data && Array.isArray(res.data.records)) {
            listData = res.data.records;
          } else if (res.data && Array.isArray(res.data.list)) {
            listData = res.data.list;
          } else if (res.data && Array.isArray(res.data.rows)) {
            listData = res.data.rows;
          }
          const formattedList = listData.map((item) => ({
            id: item.userId,
            name: item.nickName,
            score: item.count,
            avatar: item.avatar,
          }));

          if (type === 1) {
            this.mutualList = isLoadMore
              ? [...this.mutualList, ...formattedList]
              : formattedList;
            this.mutualStatus = listData.length < 20 ? "nomore" : "loadmore";
            this.mutualEmpty = this.mutualList.length === 0;
          } else {
            this.activityList = isLoadMore
              ? [...this.activityList, ...formattedList]
              : formattedList;
            this.activityStatus = listData.length < 20 ? "nomore" : "loadmore";
            this.activityEmpty = this.activityList.length === 0;
          }
        } else {
          if (type === 1) this.mutualStatus = "loadmore";
          else this.activityStatus = "loadmore";
          uni.showToast({
            title: res.msg || "获取榜单失败",
            icon: "none",
          });
        }
      } catch (error) {
        if (type === 1) this.mutualStatus = "loadmore";
        else this.activityStatus = "loadmore";
        console.error("Failed to fetch daren list", error);
      }
    },
    // 触底加载更多
    loadMore() {
      const type = this.activeTab === "mutual" ? 1 : 2;
      this.fetchDarenList(type, true);
    },
    // 切换 Tab
    switchTab(tab) {
      if (this.activeTab !== tab) {
        this.activeTab = tab;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.rank-container {
  min-height: 100vh;
  background-color: #ffffff;
  display: flex;
  flex-direction: column;

  /* 顶部渐变装饰背景 */
  .rank-header-bg {
    background: linear-gradient(
      180deg,
      #fef4e2 0%,
      rgba(255, 255, 255, 0) 100%
    );
    padding: 48rpx 0 20rpx 0;
    display: flex;
    justify-content: center;
    position: relative;
    z-index: 10;

    .tabs-row {
      display: flex;
      width: 80%;
      justify-content: space-around;
      position: relative;

      .tab-item {
        font-size: 36rpx;
        color: #8c8c8c;
        padding: 16rpx 32rpx;
        position: relative;
        transition: color 0.2s ease, font-weight 0.2s ease;
        cursor: pointer;

        /* 激活状态字样 */
        &.tab-item-active {
          color: #2e353f;
          font-weight: bold;

          /* 弧形微笑下划线效果 */
          &::after {
            content: "";
            position: absolute;
            bottom: -12rpx;
            left: 50%;
            transform: translateX(-50%);
            width: 72rpx;
            height: 20rpx;
            border-bottom: 7rpx solid #b27341;
            /* 金褐色微笑指示器 */
            border-radius: 50%;
            /* 弧形的关键所在 */
          }
        }
      }
    }
  }

  /* 列表区域 */
  .list-scroll-view {
    flex: 1;
    overflow: hidden;
  }

  .rank-list {
    padding: 20rpx 48rpx calc(40rpx + env(safe-area-inset-bottom)) 48rpx;

    .rank-row {
      display: flex;
      align-items: center;
      padding: 32rpx 0;
      border-bottom: 2rpx solid #f9f0ec; // 温暖的微粉色分割线

      &:last-child {
        border-bottom: none;
      }

      /* 排名数字样式 */
      .rank-number {
        font-family: "Georgia", "Times New Roman", serif;
        font-style: italic;
        font-size: 40rpx;
        font-weight: bold;
        width: 64rpx;
        text-align: left;
        color: #8c8c8c; // 默认排名灰色

        /* 前三名专属配色 */
        &.rank-1 {
          color: #ff4d4f; // 第一名 亮红
        }

        &.rank-2 {
          color: #ff9c6e; // 第二名 橙
        }

        &.rank-3 {
          color: #ffc069; // 第三名 浅黄
        }
      }

      /* 用户头像 */
      .user-avatar {
        width: 96rpx;
        height: 96rpx;
        border-radius: 50%;
        margin-right: 28rpx;
        background-color: #f5f5f5;
        border: 3rpx solid rgba(254, 244, 226, 0.5);
      }

      /* 文本信息 */
      .user-info {
        display: flex;
        flex-direction: column;

        .user-name {
          font-size: 30rpx;
          font-weight: bold;
          color: #2e353f;
          margin-bottom: 8rpx;
        }

        .user-score {
          font-size: 26rpx;
          color: #7a828e;
        }
      }
    }
  }
}
</style>