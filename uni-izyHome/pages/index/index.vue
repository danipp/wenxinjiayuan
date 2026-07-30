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

    <!-- 3. 双核核心入口（找点乐子 & 做好事） -->
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

    <!-- 4. 热门推荐瀑布流区 -->
    <view class="section-title-bar">
      <text class="title">热门推荐</text>
    </view>

    <view class="content-flow-layout">
      <!-- 左列：纯活动流 -->
      <view class="flow-column">
        <view
          v-for="item in leftActivities"
          :key="item.id"
          class="activity-card"
          @click="navigateTo('activity', item.id)"
        >
          <image
            class="cover-image"
            :src="item.image"
            mode="aspectFill"
          ></image>
          <view class="card-info">
            <text class="activity-title">{{ item.title }}</text>
            <view class="location-badge">
              <u-icon name="map-fill" color="#999" size="12"></u-icon>
              <text class="location-text text-ellipsis">{{
                item.location
              }}</text>
            </view>
            <!-- 参与人头像堆叠 -->
            <view class="user-avatars">
              <image
                v-for="(avatar, idx) in item.avatars.slice(0, 3)"
                :key="idx"
                class="avatar-item"
                :src="avatar"
              >
              </image>
              <view v-if="item.avatars.length > 3" class="avatar-more"
                >+{{ item.avatars.length }}</view
              >
            </view>
          </view>
        </view>
      </view>

      <!-- 右列：荣誉榜单 + 活动混合流 -->
      <view class="flow-column">
        <!-- 城市达人荣誉榜卡片 -->
        <view class="daren-honor-card" @click="navigateTo('daren')">
          <view class="card-header">
            <text class="header-text">🏆 志愿者达人榜</text>
          </view>

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
            <text class="desc">“帮助居民1人”</text>
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
            <text class="desc">“参与活动1次”</text>
          </view>
        </view>
        <!-- 活动卡片（原有的活动列表） -->
        <view
          v-for="item in rightActivities"
          :key="item.id"
          class="activity-card"
          @click="navigateTo('activity', item)"
        >
          <image
            class="cover-image"
            :src="item.image"
            mode="aspectFill"
          ></image>
          <view class="card-info">
            <view class="activity-title"
              >{{ item.title }}
              <text class="ad-tag" v-if="item.is_ad">广告</text>
            </view>
            <view class="location-badge">
              <u-icon name="map-fill" color="#999" size="12"></u-icon>
              <text class="location-text text-ellipsis">{{
                item.location
              }}</text>
            </view>
            <view class="user-avatars">
              <image
                v-for="(avatar, idx) in item.avatars.slice(0, 3)"
                :key="idx"
                class="avatar-item"
                :src="avatar"
              >
              </image>
              <view v-if="item.avatars.length > 3" class="avatar-more"
                >+{{ item.avatars.length }}</view
              >
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部引入我们之前封装好的社区选择弹窗组件 -->
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
  </view>
</template>

<script>
// 引入您此前封装好的社区选择器组件
import CommunitySelector from "@/components/community.vue";
import NfcCheckinSuccess from "./components/NfcCheckinSuccess.vue";

export default {
  components: {
    CommunitySelector,
    NfcCheckinSuccess,
  },
  data() {
    return {
      currentCommunityName: "财厅前社区",
      showCommunitySelector: false,
      showNfcCheckinSuccess: false,
      nfcCheckinParams: {},
      // 广告配置
      adLoaded: false,
      adData: null,
      // 轮播动态面板数据
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
      // 社区活动列表数据
      activities: [
        {
          id: 1,
          title: "巧手绕红心 同心颂党恩",
          image: "https://cdn.uviewui.com/uview/album/3.jpg",
          location: "苏州市金谷村",
          avatars: [
            "https://cdn.uviewui.com/uview/album/1.jpg",
            "https://cdn.uviewui.com/uview/album/2.jpg",
            "https://cdn.uviewui.com/uview/album/3.jpg",
            "https://cdn.uviewui.com/uview/album/4.jpg",
          ],
        },
        {
          id: 4,
          title: "绿水青山就是金山银山：垃圾分类，从我做起！",
          is_ad: true,
          image: "https://cdn.uviewui.com/uview/album/2.jpg",
          location: "苏州市香山花园",
          avatars: [
            "https://cdn.uviewui.com/uview/album/2.jpg",
            "https://cdn.uviewui.com/uview/album/4.jpg",
          ],
        },
        {
          id: 2,
          title: "“光荣在党50年”勋章颁发仪式",
          image: "https://cdn.uviewui.com/uview/album/4.jpg",
          location: "苏州市目澜社区",
          avatars: [
            "https://cdn.uviewui.com/uview/album/5.jpg",
            "https://cdn.uviewui.com/uview/album/6.jpg",
            "https://cdn.uviewui.com/uview/album/7.jpg",
          ],
        },
        {
          id: 3,
          title: "邻里守望，公园活动第三十六集",
          image: "https://cdn.uviewui.com/uview/album/8.jpg",
          location: "苏州市香山花园",
          avatars: [
            "https://cdn.uviewui.com/uview/album/2.jpg",
            "https://cdn.uviewui.com/uview/album/4.jpg",
          ],
        },
      ],
    };
  },
  computed: {
    // 数据分配：非对称两栏，左边展示第1、3个活动
    leftActivities() {
      return this.activities.filter((_, index) => index % 2 === 0);
    },
    // 右边除去荣誉卡片外，展示第2个活动
    rightActivities() {
      return this.activities.filter((_, index) => index % 2 !== 0);
    },
  },
  onLoad(options) {
    if (this.hasNfcCheckinParams(options)) {
      this.nfcCheckinParams = this.formatNfcCheckinParams(options);
      this.showNfcCheckinSuccess = true;
    }
    this.loadAd();
  },
  onShow() {
    // 首次载入时，如果本地缓存存在已有社区，则进行渲染
    const cachedLocation = uni.getStorageSync("selected_community");
    if (cachedLocation && cachedLocation.name) {
      this.currentCommunityName = cachedLocation.name;
    }
  },
  methods: {
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
        frameId: options.frameId || "",
        cardId: options.cardId || "",
        nfcId: options.nfcId || "",
        checkinId: options.checkinId || "",
        locationName: cachedLocation.name ? cachedLocation.name : "",
        rawParams: options,
      };
    },
    handleNfcSubmitSuccess(params) {
      console.log("NFC打卡提交成功：", params);
    },
    // 模拟加载广告
    loadAd() {
      setTimeout(() => {
        this.adData = {
          id: 10001,
          title: "绿水青山就是金山银山：垃圾分类，从我做起！",
          image: "https://cdn.uviewui.com/uview/album/2.jpg",
          location: "市政环保倡议宣传",
          avatars: [
            "https://cdn.uviewui.com/uview/album/5.jpg",
            "https://cdn.uviewui.com/uview/album/6.jpg",
            "https://cdn.uviewui.com/uview/album/7.jpg",
          ],
        };
        this.adLoaded = true;
      }, 500);
    },
    // 点击广告逻辑
    handleAdClick() {
      uni.showToast({
        title: "感谢关注公益宣传！",
        icon: "none",
      });
    },
    // 开启社区切换弹窗
    openCommunitySelector() {
      this.showCommunitySelector = true;
    },
    // 接收社区选择器的更改回调
    handleCommunityChange(data) {
      if (data && data.community) {
        this.currentCommunityName = data.community.name;
        uni.showToast({
          title: `已切换至 ${data.community.name}`,
          icon: "none",
        });
      }
    },
    // 集中式页面路由跳转逻辑
    navigateTo(type, item) {
      if (item && item.is_ad) {
        uni.showToast({
          title: "感谢关注公益广告",
          icon: "none",
        });
        return;
      }
      let url = "";
      switch (type) {
        case "fun": // 跳转到 “找点乐子” 分包页面
          url = "/spages/fun/index";
          break;
        case "deeds": // 跳转到 “做好事” 分包页面
          url = "/spages/deeds/index";
          break;
        case "daren": // 跳转到 “志愿者达人详情/榜单” 分包页面
          url = "/spages/daren/index";
          break;
        case "activity": // 跳转到 “活动详情” 分包页面
          url = `/spages/activity/detail?id=${item.id}`;
          break;
      }

      if (url) {
        uni.navigateTo({
          url: url,
        });
      }
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

  /* 1. 自定义头部 */
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

  /* 2. 上下滚动通知面板 */
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

  /* 3. 双动作入口（找点乐子 / 做好事） */
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
          color: #ffffff;
          margin-bottom: 8rpx;
        }

        .sub-title {
          font-size: 22rpx;
          color: rgba(255, 255, 255, 0.85);
        }
      }

      .card-icon {
        width: 76rpx;
        height: 76rpx;
        opacity: 0.9;
      }

      // 找点乐子：元气暖橙色渐变
      &.card-fun {
        background: linear-gradient(135deg, #ff9944 0%, #ff6f22 100%);
      }

      // 做好事：温暖志愿绿渐变
      &.card-deeds {
        background: linear-gradient(135deg, #13d682 0%, #07b160 100%);
      }
    }
  }

  /* 4. 标题栏 */
  .section-title-bar {
    margin-bottom: 24rpx;

    .title {
      font-size: 36rpx;
      font-weight: 800;
      color: #1a202c;
    }
  }

  /* 5. 非对称网格流动布局 */
  .content-flow-layout {
    display: flex;
    gap: 24rpx;

    .flow-column {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 24rpx;
    }

    // 达人卡片（采用优雅的高光浅橙质感）
    .daren-honor-card {
      background: linear-gradient(180deg, #fffcf3 0%, #ffffff 100%);
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

    // 活动基础卡片
    .activity-card {
      background-color: #ffffff;
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

          .ad-tag {
            font-size: 18rpx;
            color: #a0aec0;
            border: 2rpx solid #cbd5e0;
            border-radius: 6rpx;
            padding: 0 8rpx;
            margin-left: 12rpx;
            font-weight: normal;
            display: inline-block;
            vertical-align: middle;
          }
        }

        .location-badge {
          display: flex;
          align-items: center;
          margin-bottom: 16rpx;

          .location-text {
            font-size: 22rpx;
            color: #718096;
            margin-left: 8rpx;
            max-width: 240rpx;
          }
        }

        // 头像堆叠样式
        .user-avatars {
          display: flex;
          align-items: center;

          .avatar-item {
            width: 36rpx;
            height: 36rpx;
            border-radius: 50%;
            border: 2rpx solid #ffffff;
            margin-right: -12rpx;

            &:last-child {
              margin-right: 0;
            }
          }

          .avatar-more {
            font-size: 20rpx;
            color: #718096;
            background-color: #edf2f7;
            border-radius: 20rpx;
            padding: 4rpx 12rpx;
            margin-left: 20rpx;
          }
        }
      }
    }
  }

  /* 基础工具：单行截断 */
  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>