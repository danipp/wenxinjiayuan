<template>
  <view>
    <u-sticky>
      <cu-custom>
        <view style="font-weight: 500; font-size: 32rpx">活动详情</view>
      </cu-custom>
      <!-- 活动发起者关注栏 -->
      <view v-if="activity.publisherUserId" class="author-follow-bar">
        <text class="author-label">发起者</text>
        <text class="author-name">{{ activity.authorName || "匿名用户" }}</text>
        <view
          class="follow-btn"
          :class="{ 'follow-btn-active': isFollowing }"
          @click="toggleFollow"
        >
          <text>{{ isFollowing ? "已关注" : "+ 关注" }}</text>
        </view>
      </view>
    </u-sticky>
    <view class="banner">
      <image :src="activity.coverImage" mode="widthFix" style="width: 100%" />
    </view>
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
          :reviewTotal="reviewTotal"
          @write="handleDraftPrompt"
          @view-all="goToAllReviews"
        />
        <PhotoSection
          :idx="activity.id"
          :photoList="photoList"
          @refresh="refreshPhotos"
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
import {
  detail5,
  signup,
  joined,
  comments,
  photos,
  averageScore,
} from "@/spages/api/activity";
import { follow, unfollow } from "@/spages/api/follow";
import loginApi from "@/utils/login";
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
      isFollowing: false,
      activityId: "",
      activity: {},
      joinedNeighbors: [],
      reviews: [],
      reviewTotal: 0,
      photoList: [],
      avgScore: 0,
      shareType: null,
    };
  },
  computed: {
    isExpired() {
      if (!this.activity.endTime) return false;
      const end = new Date(this.activity.endTime).getTime();
      if (isNaN(end)) return false;
      return Date.now() > end;
    },
    isStarted() {
      if (!this.activity.startTime) return false;
      const start = new Date(this.activity.startTime).getTime();
      if (isNaN(start)) return false;
      return Date.now() >= start;
    },
    canSignup() {
      if (this.activity.signedUp) return false;
      if (!this.isStarted || this.isExpired) return false;
      // 人数已满
      if (
        this.activity.maxLimit > 0 &&
        this.activity.participantCount >= this.activity.maxLimit
      )
        return false;
      return true;
    },
    countdownTime() {
      const now = Date.now();
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
      if (!this.isStarted) return "未开始";
      if (
        this.activity.maxLimit > 0 &&
        this.activity.participantCount >= this.activity.maxLimit
      )
        return "已满员";
      return "立即报名";
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
    this.activityId = options.id ? options.id : "";
    this.shareType = options.type ? options.type : null;
    if (this.shareType) {
      loginApi().then((res) => {
        this.getList();
      });
      return;
    }
    this.getList();
  },
  onShow() {
    // 从写评价页返回时刷新评价列表；报名/照片通过 getList 已处理
    if (this.activityId) {
      if (this.shareType) {
        loginApi().then((res) => {
          this.refreshReviews();
        });
        return;
      }
      this.refreshReviews();
    }
  },
  methods: {
    // 仅刷新照片列表
    async refreshPhotos() {
      try {
        const photosRes = await photos(this.activityId);
        const photoArr = Array.isArray(photosRes.data) ? photosRes.data : [];
        this.photoList = photoArr.map((p) => ({
          id: p.photoId || p.id,
          image: p.imageUrl || "",
          author: p.nickName || "",
          time: this.formatJoinTime(p.createTime),
          likes: p.likes || 0,
          isLiked: p.isLiked || false,
          comments: [],
        }));
      } catch (e) {
        // ignore
      }
    },
    // 仅刷新评价列表
    async refreshReviews() {
      try {
        const commentsRes = await comments(this.activityId, {
          pageNumber: 1,
          pageSize: 2,
        });
        const commentsData = commentsRes.data || {};
        const reviewList = commentsData.content || [];
        this.reviews = reviewList.map((item) => ({
          name: item.nickName || "",
          avatar: item.avatar || "",
          time: this.formatJoinTime(item.createTime),
          emoji: item.emoji || "😊",
          statusText: item.statusText || "",
          content: item.content || "",
          score: item.score || 5,
        }));
        this.reviewTotal = commentsData.totalElements || 0;
      } catch (e) {
        // ignore
      }
    },
    async getList() {
      try {
        // 并行请求详情、邻居列表、照片列表、前两条评价、评分
        const [detailRes, joinedRes, photosRes, commentsRes, scoreRes] =
          await Promise.all([
            detail5(this.activityId),
            joined(this.activityId),
            photos(this.activityId),
            comments(this.activityId, { pageNumber: 1, pageSize: 2 }),
            averageScore(this.activityId).catch(() => ({ data: 0 })),
          ]);

        // 填充活动详情
        const d = detailRes.data || {};
        this.activity = {
          ...d,
          id: d.activityId || d.id,
          signedUp: d.signedUp || false,
          participantCount: d.participantCount || 0,
          infoTitle: d.title,
          description: d.content,
        };

        // 填充已加入邻居
        const joinedList = Array.isArray(joinedRes.data) ? joinedRes.data : [];
        this.joinedNeighbors = joinedList.map((item) => ({
          name: item.nickName || "",
          avatar: item.avatar || "",
          joinTime: this.formatJoinTime(item.joinTime || item.createTime),
        }));

        // 填充照片列表
        const photoArr = Array.isArray(photosRes.data) ? photosRes.data : [];
        this.photoList = photoArr.map((p) => ({
          id: p.photoId || p.id,
          image: p.imageUrl || "",
          author: p.nickName || "",
          time: this.formatJoinTime(p.createTime),
          likes: p.likes || 0,
          isLiked: p.isLiked || false,
          comments: [],
        }));

        // 填充评价列表（前2条）
        const commentsData = commentsRes.data || {};
        const reviewList = commentsData.content || [];
        this.reviews = reviewList.map((item) => ({
          name: item.nickName || "",
          avatar: item.avatar || "",
          time: this.formatJoinTime(item.createTime),
          emoji: item.emoji || "😊",
          statusText: item.statusText || "",
          content: item.content || "",
          score: item.score || 5,
        }));
        this.reviewTotal = commentsData.totalElements || 0;

        // 评分
        this.avgScore = scoreRes.data || 0;
      } catch (e) {
        uni.showToast({ title: "加载失败，请重试", icon: "none" });
      }
    },

    /** 关注/取消关注切换 */
    toggleFollow() {
      if (!this.activity.publisherUserId) return;
      if (this.isFollowing) {
        this.doUnfollow();
      } else {
        this.doFollow();
      }
    },
    /** 执行关注 */
    async doFollow() {
      try {
        const res = await follow({
          targetUserId: this.activity.publisherUserId,
        });
        if (res.code === "00000") {
          this.isFollowing = true;
          uni.showToast({ title: "关注成功", icon: "none" });
        } else {
          uni.showToast({ title: res.msg || "关注失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: e.msg, icon: "none" });
      }
    },
    /** 执行取消关注 */
    async doUnfollow() {
      try {
        const res = await unfollow(this.activity.publisherUserId);
        if (res.code === "00000") {
          this.isFollowing = false;
          uni.showToast({ title: "已取消关注", icon: "none" });
        } else {
          uni.showToast({ title: res.msg || "取消关注失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: e.msg, icon: "none" });
      }
    },

    // 简单时间格式化 "X分钟前" / "X小时前" 等
    formatJoinTime(timeStr) {
      if (!timeStr) return "";
      const d = new Date(timeStr.replace(/-/g, "/"));
      if (isNaN(d.getTime())) return timeStr;
      const diff = Date.now() - d.getTime();
      const minutes = Math.floor(diff / 60000);
      if (minutes < 1) return "刚刚加入";
      if (minutes < 60) return `${minutes}分钟前加入`;
      const hours = Math.floor(minutes / 60);
      if (hours < 24) return `${hours}小时前加入`;
      const days = Math.floor(hours / 24);
      return `${days}天前加入`;
    },

    handlePhotoListUpdate(updatedList) {
      this.photoList = updatedList;
    },
    handleCountdownFinish() {},
    async handleSignup() {
      if (!this.canSignup) return;
      try {
        await signup(this.activityId);
        uni.showToast({ title: "报名成功", icon: "none" });
        // 报名成功后刷新详情和邻居列表
        const [detailRes, joinedRes] = await Promise.all([
          detail5(this.activityId),
          joined(this.activityId),
        ]);
        const d = detailRes.data || {};
        this.activity = {
          ...this.activity,
          signedUp: d.signedUp || true,
          participantCount:
            d.participantCount || (this.activity.participantCount || 0) + 1,
        };
        const joinedList = Array.isArray(joinedRes.data) ? joinedRes.data : [];
        this.joinedNeighbors = joinedList.map((item) => ({
          name: item.nickName || "",
          avatar: item.avatar || "",
          joinTime: this.formatJoinTime(item.joinTime || item.createTime),
        }));
      } catch (e) {
        uni.showToast({ title: "报名失败，请重试", icon: "none" });
      }
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
        url: `/spages/activity/writeComment?id=${
          this.activityId
        }&name=${encodeURIComponent(this.activity.title || "")}`,
      });
    },
    goToAllReviews() {
      uni.navigateTo({
        url: `/spages/activity/comments?id=${this.activityId}`,
      });
    },
    onShareAppMessage(res) {
      if (res.from === "button" && res.target && res.target.dataset.photoid) {
        const photoId = res.target.dataset.photoid;
        const photoImg = res.target.dataset.photoimg;
        return {
          title: "分享一张超赞的社区活动照片给你！",
          path: `/spages/activity/detail?id=${this.activity.id}&photoId=${photoId}&type=share`,
          imageUrl: photoImg,
        };
      }
      return {
        title: `向你推荐社区活动：${this.activity.title}`,
        path: `/spages/activity/detail?id=${this.activity.id}`,
        imageUrl: this.activity.coverImage || "",
      };
    },
  },
};
</script>

<style lang="scss" scoped>
.author-follow-bar {
  display: flex;
  align-items: center;
  padding: 12rpx 32rpx;
  background-color: #ffffff;
  font-size: 26rpx;
  color: #999;

  .author-label {
    color: #999;
    margin-right: 12rpx;
  }

  .author-name {
    flex: 1;
    color: #333;
    font-weight: 500;
  }

  .follow-btn {
    padding: 8rpx 24rpx;
    border: 2rpx solid #07c160;
    border-radius: 28rpx;
    font-size: 24rpx;
    color: #07c160;
    background: #fff;

    &.follow-btn-active {
      color: #999;
      border-color: #ccc;
      background: #f5f5f5;
    }
  }
}

.detail-container {
  background-color: #f7f9fb;
  padding: 32rpx 32rpx calc(160rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;
}
</style>
