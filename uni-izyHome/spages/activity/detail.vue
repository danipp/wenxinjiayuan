<template>
  <view>
    <cu-custom>
      <view style="font-weight: 500; font-size: 32rpx">活动详情</view>
    </cu-custom>
    <view class="detail-container">
      <!-- 1. 顶部活动基本信息 -->
      <ActivityBaseCard
        :activity="activity"
        :isExpired="isExpired"
        :activityTimeText="activityTimeText"
        @open-intro="showIntroPopup = true"
      />

      <!-- 2. 已过期：评价 + 照片 -->
      <template v-if="isExpired">
        <ReviewSection
          :idx="activity.id"
          :reviews="reviews"
          @write="handleDraftPrompt"
          @view-all="goToAllReviews"
        />
        <PhotoSection
          :idx="activity.id"
          :photoList="photoList"
          @refresh="getList"
          @view-photo="openPhotoViewer"
        />
      </template>

      <!-- 3. 未过期：活动信息 + 已加入邻居 -->
      <LiveContent
        v-else
        :activity="activity"
        :neighbors="joinedNeighbors"
        :isStarted="isStarted"
      />

      <!-- 4. 底部操作栏 -->
      <SignupFooter
        :isExpired="isExpired"
        :canSignup="canSignup"
        :signedUp="activity.signedUp"
        :signupText="signupButtonText"
        :countdownPrefix="countdownPrefix"
        :countdownTime="countdownTime"
        @signup="handleSignup"
        @countdown-finish="handleCountdownFinish"
      />

      <!-- 5. 活动介绍弹窗 -->
      <IntroPopup
        :show.sync="showIntroPopup"
        :description="activity.description"
      />

      <!-- 大图查看 -->
      <PhotoViewer
        :show.sync="showPhotoViewer"
        :list="photoList"
        :initialIndex="selectedPhotoIndex"
        @update-list="handlePhotoListUpdate"
      />

      <!-- 资料修改弹窗 -->
      <ProfileEditPopup
        :show.sync="showProfileEdit"
        initNickname="石头"
        @confirm="onProfileConfirm"
      />

      <!-- 手机授权弹窗 -->
      <PhoneAuthPopup
        :show.sync="showPhoneAuth"
        :mustAuth="activity.collectPhone"
        @auth-success="onPhoneAuthSuccess"
      />
    </view>
  </view>
</template>

<script>
import PhotoViewer from "./components/PhotoViewer.vue";
import ProfileEditPopup from "@/components/ProfileEditPopup.vue";
import PhoneAuthPopup from "@/components/PhoneAuthPopup.vue";
import ActivityBaseCard from "./components/ActivityBaseCard.vue";
import ReviewSection from "./components/ReviewSection.vue";
import PhotoSection from "./components/PhotoSection.vue";
import LiveContent from "./components/LiveContent.vue";
import SignupFooter from "./components/SignupFooter.vue";
import IntroPopup from "./components/IntroPopup.vue";

export default {
  components: {
    PhotoViewer,
    ProfileEditPopup,
    PhoneAuthPopup,
    ActivityBaseCard,
    ReviewSection,
    PhotoSection,
    LiveContent,
    SignupFooter,
    IntroPopup,
  },
  data() {
    return {
      showPhotoViewer: false,
      selectedPhotoIndex: 0,
      showProfileEdit: false,
      showPhoneAuth: false,
      showIntroPopup: false,
      activityId: "",
      activity: {
        id: 1,
        title: "中山一社区居民瓜子会",
        location: "越秀区富力新天地中心",
        startTime: "2026-07-01 16:02:00",
        endTime: "2026-07-02 23:02:00",
        // isExpired: false,
        collectPhone: true,
        infoTitle: "中山一社区居民瓜子会",
        signedUp: false,
        description:
          "欢迎大家携家带口来中山一社区参加线下居民嗑瓜子闲聊交友会！现场我们提供多口味爱心瓜子与茶水。在这里，您可以和邻居唠唠家常，分享日常生活趣事，增进邻里感情。我们还邀请了社区音乐达人现场弹唱，氛围轻松自在。欢迎广大志愿者与居民报名！",
      },

      joinedNeighbors: [
        {
          name: "石头",
          avatar: "https://cdn.uviewui.com/uview/album/1.jpg",
          joinTime: "刚刚加入",
        },
        {
          name: "秉治",
          avatar: "https://cdn.uviewui.com/uview/album/2.jpg",
          joinTime: "5分钟前加入",
        },
        {
          name: "陈阿姨",
          avatar: "https://cdn.uviewui.com/uview/album/3.jpg",
          joinTime: "18分钟前加入",
        },
        {
          name: "时光山哥",
          avatar: "https://cdn.uviewui.com/uview/album/4.jpg",
          joinTime: "32分钟前加入",
        },
        {
          name: "小林",
          avatar: "https://cdn.uviewui.com/uview/album/5.jpg",
          joinTime: "1小时前加入",
        },
        {
          name: "王姐",
          avatar: "https://cdn.uviewui.com/uview/album/6.jpg",
          joinTime: "2小时前加入",
        },
      ],

      reviews: [
        {
          name: "秉治",
          avatar: "https://cdn.uviewui.com/uview/album/1.jpg",
          time: "1小时前",
          emoji: "😆",
          statusText: "远超预期",
          content: "满意！感谢志愿者",
        },
        {
          name: "石头",
          avatar: "https://cdn.uviewui.com/uview/album/2.jpg",
          time: "2小时前",
          emoji: "👏",
          statusText: "特别好",
          content: "很热心，点赞！",
        },
        {
          name: "陈阿姨",
          avatar: "https://cdn.uviewui.com/uview/album/3.jpg",
          time: "1天前",
          emoji: "😆",
          statusText: "热心细致",
          content: "小伙子非常周到，很满意！",
        },
      ],

      photoList: [
        {
          id: 201,
          image: "https://cdn.uviewui.com/uview/album/8.jpg",
          author: "秉治",
          time: "1小时前",
          likes: 1,
          isLiked: false,
          comments: [],
        },
        {
          id: 202,
          image: "https://cdn.uviewui.com/uview/album/6.jpg",
          author: "时光山哥",
          time: "1小时前",
          likes: 2,
          isLiked: false,
          comments: [],
        },
      ],
    };
  },
  computed: {
    isExpired() {
      if (this.activity.isExpired) return true;
      const end = new Date(this.activity.endTime).getTime();
      if (isNaN(end)) return false;
      return Date.now() > end;
    },
    isStarted() {
      const start = new Date(this.activity.startTime).getTime();
      if (isNaN(start)) return false;
      return Date.now() >= start;
    },
    canSignup() {
      // 已报名则不需要再报名
      if (this.activity.signedUp) return false;
      return this.isStarted && !this.isExpired;
    },
    countdownTime() {
      const now = Date.now();
      // 活动已结束或已报名，不需要倒计时
      if (this.isExpired || this.activity.signedUp) return 0;
      const target = this.isStarted
        ? new Date(this.activity.endTime).getTime()
        : new Date(this.activity.startTime).getTime();
      const diff = target - now;
      return diff > 0 ? diff : 0;
    },
    countdownPrefix() {
      if (this.activity.signedUp) return "";
      return this.isStarted ? "距结束" : "距开始";
    },
    signupButtonText() {
      if (this.isExpired) return "已结束";
      if (this.activity.signedUp) return "已报名";
      if (this.isStarted) return "立即报名";
      return "未开始";
    },
    activityTimeText() {
      const fmt = (str) => {
        if (!str) return "";
        const d = new Date(str);
        if (isNaN(d.getTime())) return str;
        const pad = (n) => String(n).padStart(2, "0");
        return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(
          d.getHours()
        )}:${pad(d.getMinutes())}`;
      };
      return `${fmt(this.activity.startTime)} 至 ${fmt(this.activity.endTime)}`;
    },
  },
  onLoad(options) {
    if (options && options.photoId) {
      const pIdx = this.photoList.findIndex((p) => p.id == options.photoId);
      if (pIdx !== -1) {
        this.selectedPhotoIndex = pIdx;
        this.showPhotoViewer = true;
      }
    }

    if (this.activity.collectPhone) {
      const cacheProfile = uni.getStorageSync("user_profile_data");
      if (!cacheProfile) {
        this.showProfileEdit = true;
      } else {
        const cachedPhone = uni.getStorageSync("user_phone_number");
        if (!cachedPhone) {
          this.showPhoneAuth = true;
        }
      }
    }
    this.activityId = options.id ? options.id : "";
    this.getList();
  },
  methods: {
    getList() {
      // 获取详情接口 this.activityId
    },
    handlePhotoListUpdate(updatedList) {
      this.photoList = updatedList;
    },
    handleCountdownFinish() {},
    handleSignup() {
      if (!this.canSignup) return;
      // 模拟调后端接口报名成功，后端返回后置为 true
      uni.showToast({ title: "报名成功", icon: "none" });
      this.activity.signedUp = true;
    },
    onProfileConfirm(data) {
      uni.setStorageSync("user_profile_data", data);
      const cachedPhone = uni.getStorageSync("user_phone_number");
      if (!cachedPhone) this.showPhoneAuth = true;
    },
    onPhoneAuthSuccess(phone) {
      console.log("手机强制认证通过：", phone);
    },
    openPhotoViewer(index) {
      this.selectedPhotoIndex = index;
      this.showPhotoViewer = true;
    },
    handleDraftPrompt() {
      uni.navigateTo({
        url: `/spages/activity/writeComment?id=${this.activityId}&name=${this.activity.title}`,
      });
    },
    goToAllReviews() {
      uni.navigateTo({ url: "/spages/activity/comments" });
    },
    onShareAppMessage(res) {
      if (res.from === "button" && res.target && res.target.dataset.photoid) {
        const photoId = res.target.dataset.photoid;
        const photoImg = res.target.dataset.photoimg;
        return {
          title: "分享一张超赞的社区活动照片给你！",
          path: `/spages/activity/detail?id=${this.activity.id}&photoId=${photoId}`,
          imageUrl: photoImg,
        };
      }
      return {
        title: `向你推荐社区活动：${this.activity.title}`,
        path: `/spages/activity/detail?id=${this.activity.id}`,
        imageUrl: "https://cdn.uviewui.com/uview/album/3.jpg",
      };
    },
  },
};
</script>

<style lang="scss" scoped>
.detail-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx 32rpx calc(160rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;
}
</style>
