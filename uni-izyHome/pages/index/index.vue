<template>
  <view class="home-container">
    <!-- 1. 顶部自定义导航与社区选择 -->
    <view class="header-section">
      <view class="brand-title">温馨家园</view>
      <view class="community-selector" @click="openCommunitySelector">
        <text class="community-name">{{ currentCommunityName }}</text>
        <text class="switch-btn">切换</text>
        <u-icon name="arrow-right" color="#2c405a" size="12"></u-icon>
      </view>
    </view>

    <!-- 2. 上下轮播的消息动态面板 -->
    <view class="notice-swiper-bar">
      <u-icon name="volume-fill" color="#07c160" size="18"></u-icon>
      <swiper class="notice-swiper" vertical autoplay circular interval="3000">
        <swiper-item
          v-for="(item, index) in noticeList"
          :key="index"
          class="swiper-item"
        >
          <image class="avatar" :src="item.avatar" mode="aspectFill"></image>
          <text class="nickname">{{ item.nickname }}</text>
          <text class="action">完成</text>
          <text class="task text-ellipsis">[{{ item.taskName }}]</text>
          <text class="time">{{ item.date }}</text>
        </swiper-item>
      </swiper>
    </view>

    <!-- 3. 双核核心入口 -->
    <view class="action-grid">
      <view class="action-card card-fun" @click="navigateTo('fun')">
        <view class="card-text">
          <text class="main-title">找点乐子</text>
          <text class="sub-title">丰富社区精彩生活</text>
        </view>
      </view>
      <view class="action-card card-deeds" @click="navigateTo('deeds')">
        <view class="card-text">
          <text class="main-title">做好事</text>
          <text class="sub-title">传递温暖点亮身边</text>
        </view>
      </view>
    </view>

    <!-- 4. Tab 切换 -->
    <view class="section-title-bar">
      <view class="tabs-box">
        <text
          class="tab-item"
          :class="{ active: currentTab === 'recruitment' }"
          @click="switchTab('recruitment')"
          >招募活动</text
        >
        <text
          class="tab-item"
          :class="{ active: currentTab === 'community' }"
          @click="switchTab('community')"
          >社区活动</text
        >
      </view>
    </view>

    <!-- 5. 社区活动 tab: scroll-view 瀑布流 -->
    <scroll-view
      v-show="currentTab === 'community'"
      scroll-y
      class="community-scroll"
      refresher-enabled
      :refresher-triggered="communityRefreshing"
      @refresherrefresh="onCommunityRefresh"
      @scrolltolower="onCommunityLoadMore"
    >
      <!-- 有数据 -->
      <view v-if="communityList.length > 0" class="content-flow-layout">
        <view class="flow-column">
          <view
            v-for="item in leftActivities"
            :key="item.id"
            class="activity-card"
            @click="goDetail(item.id)"
          >
            <image
              class="cover-image"
              :src="item.coverImage"
              mode="aspectFill"
            ></image>
            <view class="card-info">
              <text class="activity-title">{{ item.title }}</text>
              <view class="location-badge">
                <u-icon name="map-fill" color="#999" size="12"></u-icon>
                <text class="location-text text-ellipsis">{{
                  item.location || "待定"
                }}</text>
              </view>
              <text class="participant-info"
                >{{ item.participantCount || 0 }}人参与</text
              >
            </view>
          </view>
        </view>

        <view class="flow-column">
          <!-- 达人榜 -->
          <view class="daren-honor-card" @click="navigateTo('daren')">
            <view class="card-header"
              ><text class="header-text">🏆 志愿者达人榜</text></view
            >
            <view class="daren-item">
              <text class="badge-tag tag-orange">@互助达人 :</text>
              <view class="user-info">
                <image
                  class="daren-avatar"
                  src="https://cdn.uviewui.com/uview/album/1.jpg"
                  mode="aspectFill"
                ></image>
                <text class="daren-name">秉治</text>
              </view>
              <text class="desc">"帮助居民1人"</text>
            </view>
            <view class="daren-item">
              <text class="badge-tag tag-blue">@活动达人 :</text>
              <view class="user-info">
                <image
                  class="daren-avatar"
                  src="https://cdn.uviewui.com/uview/album/2.jpg"
                  mode="aspectFill"
                ></image>
                <text class="daren-name">秉治</text>
              </view>
              <text class="desc">"参与活动1次"</text>
            </view>
          </view>

          <view
            v-for="item in rightActivities"
            :key="item.id"
            class="activity-card"
            @click="goDetail(item.id)"
          >
            <image
              class="cover-image"
              :src="item.coverImage"
              mode="aspectFill"
            ></image>
            <view class="card-info">
              <text class="activity-title">{{ item.title }}</text>
              <view class="location-badge">
                <u-icon name="map-fill" color="#999" size="12"></u-icon>
                <text class="location-text text-ellipsis">{{
                  item.location || "待定"
                }}</text>
              </view>
              <text class="participant-info"
                >{{ item.participantCount || 0 }}人参与</text
              >
            </view>
          </view>
        </view>

        <!-- 加载状态 -->
        <view class="load-more-tips">
          <text v-if="communityLoading">加载中...</text>
          <text v-else-if="communityNoMore">—— 没有更多了 ——</text>
        </view>
      </view>

      <!-- 空数据 -->
      <view v-else-if="!communityLoading" class="empty-state">
        <view class="empty-icon">📋</view>
        <text class="empty-title">暂无社区活动</text>
        <text class="empty-sub">下拉刷新试试看</text>
      </view>
    </scroll-view>

    <!-- 6. 招募活动 tab: scroll-view 列表 -->
    <scroll-view
      v-show="currentTab === 'recruitment'"
      scroll-y
      class="recruitment-scroll"
      refresher-enabled
      :refresher-triggered="recruitRefreshing"
      @refresherrefresh="onRecruitRefresh"
      @scrolltolower="onRecruitLoadMore"
    >
      <!-- 有数据 -->
      <view v-if="recruitList.length > 0" class="recruitment-list">
        <view
          v-for="item in recruitList"
          :key="item.id"
          class="recruitment-item"
          @click="goDetail(item.id)"
        >
          <image class="cover" :src="item.coverImage" mode="aspectFill"></image>
          <view class="info">
            <view class="title">{{ item.title }}</view>
            <view class="desc">
              <text class="organizer"
                >发起方: {{ item.authorName || "社区" }}</text
              >
              <text
                class="status"
                :class="
                  item.status === 1
                    ? 'status-upcoming'
                    : item.status === 2
                    ? 'status-ongoing'
                    : 'status-ended'
                "
                >{{ item.statusText || "招募中" }}</text
              >
            </view>
            <text class="join-count"
              >{{ item.participantCount || 0 }}人已参与</text
            >
          </view>
        </view>

        <view class="load-more-tips">
          <text v-if="recruitLoading">加载中...</text>
          <text v-else-if="recruitNoMore">—— 没有更多了 ——</text>
        </view>
      </view>

      <!-- 空数据 -->
      <view v-else-if="!recruitLoading" class="empty-state">
        <view class="empty-icon">📋</view>
        <text class="empty-title">暂无招募活动</text>
        <text class="empty-sub">下拉刷新试试看</text>
      </view>
    </scroll-view>

    <!-- 社区选择弹窗 -->
    <CommunitySelector
      :show.sync="showCommunitySelector"
      title="请选择我的社区"
      mode="select"
      @confirm="handleCommunityChange"
    />

    <NfcCheckinSuccess
      :show="showNfcCheckinSuccess"
      :checkinParams="nfcCheckinParams"
      @submit-success="handleNfcSubmitSuccess"
      @close="showNfcCheckinSuccess = false"
    />
    <PhoneAuthPopup :show.sync="showPhoneAuth" />
  </view>
</template>

<script>
import CommunitySelector from "@/components/community.vue";
import NfcCheckinSuccess from "./components/NfcCheckinSuccess.vue";
import PhoneAuthPopup from "@/components/PhoneAuthPopup.vue";
import loginApi from "@/utils/login.js";
import { create4 } from "@/api/index";
import { square } from "@/spages/api/activity";

export default {
  components: { CommunitySelector, PhoneAuthPopup, NfcCheckinSuccess },
  data() {
    return {
      currentTab: "community",
      showPhoneAuth: false,
      currentCommunityName: "请选择社区",
      showCommunitySelector: false,
      showNfcCheckinSuccess: false,
      nfcCheckinParams: {},
      // 社区活动
      communityList: [],
      communityPage: 1,
      communityPageSize: 10,
      communityLoading: false,
      communityNoMore: false,
      communityRefreshing: false,
      // 招募活动
      recruitList: [],
      recruitPage: 1,
      recruitPageSize: 10,
      recruitLoading: false,
      recruitNoMore: false,
      recruitRefreshing: false,
      // 轮播
      noticeList: [
        {
          avatar: "https://cdn.uviewui.com/uview/album/5.jpg",
          nickname: "罗*完成",
          taskName: "床单清洗",
          date: "08月16日",
        },
        {
          avatar: "https://cdn.uviewui.com/uview/album/6.jpg",
          nickname: "张*完成",
          taskName: "陪同就医",
          date: "08月17日",
        },
        {
          avatar: "https://cdn.uviewui.com/uview/album/7.jpg",
          nickname: "李*完成",
          taskName: "卫生打扫",
          date: "08月18日",
        },
      ],
    };
  },
  computed: {
    leftActivities() {
      return this.communityList.filter((_, i) => i % 2 === 0);
    },
    rightActivities() {
      return this.communityList.filter((_, i) => i % 2 !== 0);
    },
  },
  onLoad(options) {
    loginApi().then((res) => {
      this.loadAd();
      if (this.hasNfcCheckinParams(options)) {
        this.nfcCheckinParams = this.formatNfcCheckinParams(options);
        this.create4(this.nfcCheckinParams);
      }
    });
    this.fetchCommunityList();
    this.fetchRecruitList();
  },
  onShow() {
    const cachedLocation = uni.getStorageSync("selected_community");
    if (cachedLocation && cachedLocation.name) {
      this.currentCommunityName = cachedLocation.name;
    }
  },
  methods: {
    // ==================== 社区活动 ====================
    async fetchCommunityList() {
      if (this.communityLoading || this.communityNoMore) return;
      this.communityLoading = true;
      try {
        const res = await square({
          pageNumber: this.communityPage,
          pageSize: this.communityPageSize,
          type: 2,
        });
        const pageData = res.data || {};
        const list = pageData.content || [];
        const isLast =
          pageData.last !== undefined
            ? pageData.last
            : list.length < this.communityPageSize;

        const mapped = list.map((item) => ({
          id: item.activityId || item.id,
          title: item.title || "",
          coverImage: item.coverImage || "",
          location: item.location || "",
          participantCount: item.participantCount || 0,
          startTime: item.startTime || "",
          endTime: item.endTime || "",
          status: item.status,
          statusText: item.statusText || "",
          authorName: item.authorName || "",
          authorAvatar: item.authorAvatar || "",
          tag: item.tag || "",
        }));

        this.communityList =
          this.communityPage === 1
            ? mapped
            : [...this.communityList, ...mapped];
        this.communityNoMore = isLast;
        if (!isLast) this.communityPage++;
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.communityLoading = false;
        this.communityRefreshing = false;
      }
    },
    onCommunityRefresh() {
      this.communityRefreshing = true;
      this.communityPage = 1;
      this.communityNoMore = false;
      this.communityList = [];
      this.fetchCommunityList();
    },
    onCommunityLoadMore() {
      if (!this.communityLoading && !this.communityNoMore) {
        this.fetchCommunityList();
      }
    },

    // ==================== 招募活动 ====================
    async fetchRecruitList() {
      if (this.recruitLoading || this.recruitNoMore) return;
      this.recruitLoading = true;
      try {
        const res = await square({
          pageNumber: this.recruitPage,
          pageSize: this.recruitPageSize,
          type: 3,
        });
        const pageData = res.data || {};
        const list = pageData.content || [];
        const isLast =
          pageData.last !== undefined
            ? pageData.last
            : list.length < this.recruitPageSize;

        const mapped = list.map((item) => ({
          id: item.activityId || item.id,
          title: item.title || "",
          coverImage: item.coverImage || "",
          location: item.location || "",
          participantCount: item.participantCount || 0,
          status: item.status,
          statusText: item.statusText || "",
          authorName: item.authorName || "",
          authorAvatar: item.authorAvatar || "",
        }));

        this.recruitList =
          this.recruitPage === 1 ? mapped : [...this.recruitList, ...mapped];
        this.recruitNoMore = isLast;
        if (!isLast) this.recruitPage++;
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.recruitLoading = false;
        this.recruitRefreshing = false;
      }
    },
    onRecruitRefresh() {
      this.recruitRefreshing = true;
      this.recruitPage = 1;
      this.recruitNoMore = false;
      this.recruitList = [];
      this.fetchRecruitList();
    },
    onRecruitLoadMore() {
      if (!this.recruitLoading && !this.recruitNoMore) {
        this.fetchRecruitList();
      }
    },

    switchTab(tab) {
      this.currentTab = tab;
    },

    create4(options) {
      create4(options).then((res) => {
        this.showNfcCheckinSuccess = true;
      });
    },
    hasNfcCheckinParams(options) {
      return !!(
        options &&
        (options.frameId ||
          options.cardId ||
          options.nfcId ||
          options.checkinId)
      );
    },
    formatNfcCheckinParams(options) {
      const cachedLocation = uni.getStorageSync("selected_community");
      return {
        frameNo: options.frameId || "",
        frameName: options.frameName || "",
        frameImage: options.frameImage || "",
        location: cachedLocation.name ? cachedLocation.name : "",
        rawParams: options,
      };
    },
    handleNfcSubmitSuccess(params) {
      console.log("NFC打卡提交成功：", params);
    },
    loadAd() {},
    openCommunitySelector() {
      this.showCommunitySelector = true;
    },
    handleCommunityChange(data) {
      if (data && data.community) {
        this.currentCommunityName = data.community.name;
        uni.showToast({
          title: `已切换至 ${data.community.name}`,
          icon: "none",
        });
      }
    },
    goDetail(id) {
      let user_phone_number = uni.getStorageSync("user_phone_number") || null;
      if (!user_phone_number) {
        this.showPhoneAuth = true;
        return;
      }
      uni.navigateTo({ url: `/spages/activity/detail?id=${id}` });
    },
    navigateTo(type, item) {
      if (item && item.is_ad) {
        uni.showToast({ title: "感谢关注公益广告", icon: "none" });
        return;
      }
      let url = "";
      switch (type) {
        case "fun":
          url = "/spages/fun/index";
          break;
        case "deeds":
          url = "/spages/deeds/index";
          break;
        case "daren":
          url = "/spages/daren/index";
          break;
        case "activity":
          url = `/spages/activity/detail?id=${item.id}`;
          break;
      }
      if (url) uni.navigateTo({ url });
    },
  },
};
</script>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx 32rpx calc(48rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;

  .header-section {
    display: flex;
    flex-direction: column;
    margin-bottom: 32rpx;
    padding-top: 20rpx;
    .brand-title {
      font-size: 44rpx;
      font-weight: 800;
      color: #1a202c;
      letter-spacing: 1rpx;
      margin-bottom: 12rpx;
    }
    .community-selector {
      display: inline-flex;
      align-items: center;
      background: rgba(255, 255, 255, 0.8);
      border: 2rpx solid #e2e8f0;
      padding: 12rpx 24rpx;
      border-radius: 40rpx;
      width: fit-content;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.02);
      .community-name {
        font-size: 28rpx;
        font-weight: 600;
        color: #2d3748;
      }
      .switch-btn {
        font-size: 22rpx;
        color: #4a5568;
        background-color: #edf2f7;
        padding: 4rpx 16rpx;
        border-radius: 20rpx;
        margin: 0 12rpx;
      }
    }
  }

  .notice-swiper-bar {
    display: flex;
    align-items: center;
    background-color: #e6f9f0;
    border-radius: 24rpx;
    padding: 16rpx 28rpx;
    height: 80rpx;
    margin-bottom: 32rpx;
    box-sizing: border-box;
    .notice-swiper {
      flex: 1;
      height: 100%;
      margin-left: 16rpx;
      .swiper-item {
        display: flex;
        align-items: center;
        font-size: 26rpx;
        color: #2d3748;
        .avatar {
          width: 44rpx;
          height: 44rpx;
          border-radius: 50%;
          margin-right: 16rpx;
          border: 2rpx solid #fff;
        }
        .nickname {
          font-weight: 600;
          color: #2d3748;
          margin-right: 8rpx;
        }
        .action {
          color: #718096;
          margin-right: 12rpx;
        }
        .task {
          flex: 1;
          color: #07c160;
          font-weight: 700;
          max-width: 280rpx;
        }
        .time {
          font-size: 24rpx;
          color: #a0aec0;
          margin-left: auto;
        }
      }
    }
  }

  .action-grid {
    display: flex;
    gap: 24rpx;
    margin-bottom: 48rpx;
    .action-card {
      flex: 1;
      height: 180rpx;
      border-radius: 32rpx;
      padding: 32rpx;
      box-sizing: border-box;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 8rpx 28rpx rgba(0, 0, 0, 0.04);
      transition: transform 0.15s ease;
      &:active {
        transform: scale(0.98);
      }
      .card-text {
        display: flex;
        flex-direction: column;
        .main-title {
          font-size: 36rpx;
          font-weight: 800;
          color: #fff;
          margin-bottom: 8rpx;
        }
        .sub-title {
          font-size: 22rpx;
          color: rgba(255, 255, 255, 0.85);
        }
      }
      &.card-fun {
        background: linear-gradient(135deg, #ff9944 0%, #ff6f22 100%);
      }
      &.card-deeds {
        background: linear-gradient(135deg, #13d682 0%, #07b160 100%);
      }
    }
  }

  .section-title-bar {
    margin-bottom: 24rpx;
    .tabs-box {
      display: flex;
      align-items: center;
      gap: 32rpx;
      .tab-item {
        font-size: 32rpx;
        color: #718096;
        font-weight: 600;
        position: relative;
        padding-bottom: 8rpx;
        transition: all 0.2s;
        &.active {
          font-size: 36rpx;
          color: #1a202c;
          font-weight: 800;
          &::after {
            content: "";
            position: absolute;
            left: 50%;
            bottom: 0;
            transform: translateX(-50%);
            width: 32rpx;
            height: 6rpx;
            background-color: #07c160;
            border-radius: 4rpx;
          }
        }
      }
    }
  }

  /* scroll-view 占满剩余高度 */
  .community-scroll,
  .recruitment-scroll {
    height: calc(100vh - 620rpx);
  }

  /* 社区活动瀑布流 */
  .content-flow-layout {
    display: flex;
    gap: 24rpx;
    .flow-column {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 24rpx;
    }

    .daren-honor-card {
      background: linear-gradient(180deg, #fffcf3 0%, #fff 100%);
      border: 2rpx solid #fce7c8;
      border-radius: 32rpx;
      padding: 32rpx 28rpx;
      box-shadow: 0 8rpx 24rpx rgba(251, 191, 36, 0.05);
      .card-header {
        margin-bottom: 28rpx;
        .header-text {
          font-size: 30rpx;
          font-weight: 800;
          color: #b75e12;
        }
      }
      .daren-item {
        margin-bottom: 24rpx;
        &:last-child {
          margin-bottom: 0;
        }
        .badge-tag {
          font-size: 22rpx;
          font-weight: 700;
          padding: 4rpx 16rpx;
          border-radius: 12rpx;
          display: inline-block;
          margin-bottom: 12rpx;
          &.tag-orange {
            background-color: #fff0db;
            color: #d97706;
          }
          &.tag-blue {
            background-color: #e0f2fe;
            color: #0284c7;
          }
        }
        .user-info {
          display: flex;
          align-items: center;
          margin-bottom: 8rpx;
          .daren-avatar {
            width: 40rpx;
            height: 40rpx;
            border-radius: 50%;
            margin-right: 12rpx;
          }
          .daren-name {
            font-size: 26rpx;
            font-weight: 700;
            color: #2d3748;
          }
        }
        .desc {
          font-size: 24rpx;
          color: #4a5568;
          font-style: italic;
        }
      }
    }

    .activity-card {
      background-color: #fff;
      border-radius: 32rpx;
      overflow: hidden;
      box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.03);
      display: flex;
      flex-direction: column;
      .cover-image {
        width: 100%;
        height: 240rpx;
        background-color: #edf2f7;
      }
      .card-info {
        padding: 24rpx;
        display: flex;
        flex-direction: column;
        .activity-title {
          font-size: 28rpx;
          font-weight: 700;
          color: #1a202c;
          line-height: 1.4;
          margin-bottom: 16rpx;
        }
        .location-badge {
          display: flex;
          align-items: center;
          margin-bottom: 12rpx;
          .location-text {
            font-size: 22rpx;
            color: #718096;
            margin-left: 8rpx;
            max-width: 240rpx;
          }
        }
        .participant-info {
          font-size: 22rpx;
          color: #94a3b8;
        }
      }
    }
  }

  /* 招募活动列表 */
  .recruitment-list {
    display: flex;
    flex-direction: column;
    gap: 24rpx;
    .recruitment-item {
      display: flex;
      background-color: #fff;
      border-radius: 24rpx;
      padding: 24rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.03);
      .cover {
        width: 160rpx;
        height: 160rpx;
        border-radius: 16rpx;
        margin-right: 24rpx;
        background-color: #f7f9fb;
      }
      .info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        .title {
          font-size: 30rpx;
          font-weight: 700;
          color: #1a202c;
        }
        .desc {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 24rpx;
          .organizer {
            color: #718096;
          }
          .status {
            padding: 4rpx 12rpx;
            border-radius: 8rpx;
            &.status-upcoming {
              color: #0284c7;
              background-color: #e0f2fe;
            }
            &.status-ongoing {
              color: #07c160;
              background-color: #e6f9f0;
            }
            &.status-ended {
              color: #94a3b8;
              background-color: #f1f5f9;
            }
          }
        }
        .join-count {
          font-size: 22rpx;
          color: #94a3b8;
        }
      }
    }
  }

  .load-more-tips {
    text-align: center;
    padding: 24rpx 0;
    font-size: 24rpx;
    color: #a0aec0;
  }

  /* 空状态 */
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 200rpx;
    .empty-icon {
      width: 128rpx;
      height: 128rpx;
      border-radius: 40rpx;
      background-color: #f0faf5;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 60rpx;
      margin-bottom: 28rpx;
    }
    .empty-title {
      font-size: 28rpx;
      color: #718096;
      font-weight: bold;
    }
    .empty-sub {
      font-size: 22rpx;
      color: #cbd5e1;
      margin-top: 12rpx;
    }
  }

  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
