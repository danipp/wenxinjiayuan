<template>
  <view v-if="show" class="photo-viewer-viewport">
    <cu-custom bgColor="transparent">
      <template #left>
        <view class="back-btn" @click="handleClose">
          <u-icon name="arrow-left" color="#ffffff" size="18"></u-icon>
          <text class="back-text">返回活动</text>
        </view>
      </template>
      <text class="progress-title"
        >{{ activeIndex + 1 }}/{{ localList.length }}</text
      >
    </cu-custom>
    <!-- 弹幕层（点击穿透，不阻碍大图滑动） -->
    <view class="barrage-container">
      <view
        v-for="b in barrages"
        :key="b.id"
        class="barrage-item"
        :style="{ top: b.top, animationDuration: b.duration }"
      >
        <image class="barrage-avatar" :src="b.avatar" mode="aspectFill"></image>
        <text class="barrage-text">{{ b.text }}</text>
      </view>
    </view>

    <!-- 2. 主图滑动区域 -->
    <!-- 通过绑定 :disable-touch="showComments" 在评论展开时锁定滑动 -->
    <swiper
      class="image-swiper"
      :current="activeIndex"
      @change="onSwiperChange"
      :disable-touch="showComments"
    >
      <swiper-item
        v-for="(item, index) in localList"
        :key="index"
        class="swiper-item"
      >
        <image class="main-image" :src="item.image" mode="aspectFit"></image>
      </swiper-item>
    </swiper>

    <!-- 3. 底部互动信息面板 -->
    <view class="footer-interaction-panel">
      <!-- 快捷标签横向滚动 -->
      <scroll-view scroll-x class="reaction-scroll">
        <view class="reaction-row">
          <view
            v-for="(tag, idx) in quickTags"
            :key="idx"
            class="reaction-tag"
            @click="sendQuickReaction(tag)"
          >
            <text class="tag-emoji">{{ tag.emoji }}</text>
            <text class="tag-text">{{ tag.text }}</text>
          </view>
        </view>
      </scroll-view>

      <!-- 用户信息与右侧工具栏 -->
      <view class="author-tool-bar">
        <view class="author-info">
          <u-avatar
            class="avatar"
            :src="currentPhoto.avatar"
            mode="aspectFill"
            size="84rpx"
          ></u-avatar>
          <view class="name-time">
            <text class="name">{{ currentPhoto.author }}</text>
            <text class="time">{{ currentPhoto.time }}</text>
          </view>
        </view>

        <!-- 工具按钮组 -->
        <view class="tool-icons">
          <!-- 点赞：利用本地副本与父级引用同步，完美解决爱心变红与计数增加 Bug -->
          <view class="icon-item" @click="toggleLike">
            <u-icon
              :name="currentPhoto.isLiked ? 'heart-fill' : 'heart'"
              :color="currentPhoto.isLiked ? '#ff4d4f' : '#ffffff'"
              size="22"
            ></u-icon>
            <text class="icon-label">{{ currentPhoto.likes }}</text>
          </view>

          <!-- 下载：保存至相册 -->
          <view class="icon-item" @click="downloadImage">
            <u-icon name="download" color="#ffffff" size="22"></u-icon>
            <text class="icon-label">下载</text>
          </view>

          <!-- 分享：原生微信透明全覆盖 -->
          <view class="icon-item relative-box">
            <u-icon name="share" color="#ffffff" size="22"></u-icon>
            <text class="icon-label">分享</text>
            <button
              class="transparent-share-btn"
              open-type="share"
              :data-photoid="currentPhoto.id"
              :data-photoimg="currentPhoto.image"
            ></button>
          </view>

          <!-- 评论 -->
          <view class="icon-item" @click="showComments = true">
            <u-icon name="chat" color="#ffffff" size="22"></u-icon>
            <text class="icon-label">评论</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 4. 评论底部滑动抽屉 -->
    <view class="comment-drawer" :class="{ 'drawer-open': showComments }">
      <view class="drawer-header">
        <view class="drawer-title flex"
          >全部
          {{ currentPhoto.comments ? currentPhoto.comments.length : 0 }}
          条评论</view
        >
        <view class="drawer-close" @click="showComments = false">
          <u-icon name="close" color="#333333" size="18"></u-icon>
        </view>
      </view>

      <!-- 评论列表 -->
      <scroll-view scroll-y class="options-scroll">
        <view
          v-if="currentPhoto.comments && currentPhoto.comments.length > 0"
          class="comment-list"
        >
          <view
            v-for="(c, cIdx) in currentPhoto.comments"
            :key="cIdx"
            class="comment-bubble-item"
          >
            <image class="c-avatar" :src="c.avatar" mode="aspectFill"></image>
            <view class="c-right-body">
              <view class="c-name-row">
                <text class="c-user-name">{{ c.user }}</text>
                <text class="c-time">{{ c.time || "刚刚" }}</text>
              </view>
              <text class="c-main-text">{{ c.text }}</text>
            </view>
          </view>
        </view>

        <!-- 缺省状态 -->
        <view v-else class="empty-comment-box">
          <view class="empty-speech-bubble"></view>
          <text class="empty-tips">暂无评论内容</text>
        </view>
      </scroll-view>

      <!-- 评论发布区 -->
      <view class="drawer-footer-input">
        <scroll-view scroll-x class="quick-reactions-scroll">
          <view class="quick-row">
            <view
              v-for="(tag, idx) in quickTags"
              :key="idx"
              class="q-tag"
              @click="sendComment(tag.text)"
            >
              <text>{{ tag.emoji }} {{ tag.text }}</text>
            </view>
          </view>
        </scroll-view>

        <view class="input-bar">
          <u-avatar
            class="my-avatar"
            :src="myAvatar"
            mode="aspectFill"
          ></u-avatar>
          <input
            type="text"
            v-model="commentText"
            placeholder="写评论"
            class="c-input"
            confirm-type="send"
            @confirm="submitComment"
          />
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { toggleLike } from "@/spages/api/activity";
function getProfile() {
  let user_profile_data = uni.getStorageSync("user_profile_data") || null;
  return user_profile_data ? user_profile_data : {};
}
export default {
  props: {
    show: { type: Boolean, default: false },
    list: { type: Array, default: () => [] }, // 父组件传入的完整列表
    initialIndex: { type: Number, default: 0 },
  },
  data() {
    return {
      statusBarHeight: 44,
      activeIndex: 0,
      showComments: false,
      commentText: "",
      myAvatar: getProfile().avatarUrl,
      barrages: [], // 活跃弹幕
      localList: [], // 【沙箱机制】：存放经过“同作者过滤”后的照片子集
    };
  },
  computed: {
    currentPhoto() {
      if (this.localList.length === 0) return { comments: [] };
      return this.localList[this.activeIndex];
    },
  },
  watch: {
    show(val) {
      if (val) {
        // 【核心修复】：实现点击谁的照片，就只能左右滑动看谁的照片
        const clickedPhoto = this.list[this.initialIndex];
        if (clickedPhoto) {
          const authorName = clickedPhoto.author;

          // 1. 自动过滤列表：只提取该作者名下的照片
          this.localList = this.list.filter((p) => p.author === authorName);

          // 2. 定位到该图在过滤后子集列表中的相对索引
          this.activeIndex = this.localList.findIndex(
            (p) => p.id === clickedPhoto.id
          );
        }

        // 装载弹幕
        this.triggerDefaultBarrages();
      } else {
        this.barrages = [];
        this.showComments = false;
        this.localList = [];
      }
    },
  },
  created() {
    const sys = uni.getSystemInfoSync();
    this.statusBarHeight = sys.statusBarHeight || 44;
  },
  methods: {
    handleClose() {
      // 关闭时将点赞状态同步回父级 photoList
      this.list.forEach((p) => {
        const local = this.localList.find((l) => l.id === p.id);
        if (local) {
          p.isLiked = local.isLiked;
          p.likes = local.likes;
        }
      });
      this.$emit("update:show", false);
      this.$emit("update-list", [...this.list]);
    },
    onSwiperChange(e) {
      this.activeIndex = e.detail.current;
      this.showComments = false;
      this.triggerDefaultBarrages();
    },

    // 点赞与取消点赞
    async toggleLike() {
      const currentLocalPhoto = this.localList[this.activeIndex];
      const parentPhoto = this.list.find((p) => p.id === currentLocalPhoto.id);

      try {
        await toggleLike(currentLocalPhoto.id);
        // 接口成功后更新 UI
        const nextLiked = !currentLocalPhoto.isLiked;
        const nextLikes = nextLiked
          ? currentLocalPhoto.likes + 1
          : Math.max(currentLocalPhoto.likes - 1, 0);

        if (parentPhoto) {
          this.$set(parentPhoto, "isLiked", nextLiked);
          this.$set(parentPhoto, "likes", nextLikes);
        }
        this.$set(currentLocalPhoto, "isLiked", nextLiked);
        this.$set(currentLocalPhoto, "likes", nextLikes);

        uni.showToast({
          title: nextLiked ? "点赞成功" : "已取消点赞",
          icon: "none",
        });
      } catch (e) {
        uni.showToast({ title: "操作失败", icon: "none" });
      }
    },

    // 真实保存至微信相册
    downloadImage() {
      const photo = this.localList[this.activeIndex];
      uni.showLoading({ title: "保存中..." });

      // #ifdef MP-WEIXIN
      uni.downloadFile({
        url: photo.image,
        success: (res) => {
          if (res.statusCode === 200) {
            uni.saveImageToPhotosAlbum({
              filePath: res.tempFilePath,
              success: () => {
                uni.showToast({ title: "已保存至相册", icon: "success" });
              },
              fail: () => {
                uni.showToast({
                  title: "保存失败，请开通相册权限",
                  icon: "none",
                });
              },
            });
          } else {
            uni.showToast({ title: "下载失败", icon: "none" });
          }
        },
        fail: () => {
          uni.showToast({ title: "图片下载出错", icon: "none" });
        },
        complete: () => uni.hideLoading(),
      });
      // #endif

      // #ifndef MP-WEIXIN
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({ title: "非微信环境：模拟保存成功", icon: "success" });
      }, 800);
      // #endif
    },

    // 一键发射弹幕机制
    addBarrage(text, avatar = "https://cdn.uviewui.com/uview/album/1.jpg") {
      const id = new Date().getTime() + Math.random();
      const tops = ["8%", "20%", "32%", "45%", "58%"];
      const randomTop = tops[Math.floor(Math.random() * tops.length)];
      const duration = `${Math.random() * 2 + 5}s`;

      const newItem = { id, text, avatar, top: randomTop, duration };
      this.barrages.push(newItem);

      setTimeout(() => {
        this.barrages = this.barrages.filter((b) => b.id !== id);
      }, 8000);
    },

    triggerDefaultBarrages() {
      this.barrages = [];
      const comments = this.currentPhoto.comments || [];
      comments.forEach((c, index) => {
        setTimeout(() => {
          if (this.show) {
            this.addBarrage(c.text, c.avatar);
          }
        }, index * 1200);
      });
    },

    sendComment(text) {
      const currentLocalPhoto = this.localList[this.activeIndex];
      const parentPhoto = this.list.find((p) => p.id === currentLocalPhoto.id);

      const newComment = {
        user: getProfile().nickname,
        text: text,
        avatar: this.myAvatar,
        time: "刚刚",
      };

      // localList 是从 list 过滤出来的对象引用，通常和 parentPhoto 是同一个对象。
      // 如果对两边都 push，会把同一条评论写入两次，导致评论列表展示两条。
      if (currentLocalPhoto) {
        if (!currentLocalPhoto.comments)
          this.$set(currentLocalPhoto, "comments", []);
        currentLocalPhoto.comments.push(newComment);
      }

      // 仅当未来 localList 改成深拷贝、两者不是同一个对象时，才额外同步父级数据。
      if (parentPhoto && parentPhoto !== currentLocalPhoto) {
        if (!parentPhoto.comments) this.$set(parentPhoto, "comments", []);
        parentPhoto.comments.push(newComment);
      }

      // 3. 触发弹幕
      this.addBarrage(text, this.myAvatar);

      uni.showToast({ title: "评论成功", icon: "none" });
    },

    sendQuickReaction(tag) {
      this.sendComment(tag.text);
    },

    submitComment() {
      if (!this.commentText.trim()) return;
      this.sendComment(this.commentText);
      this.commentText = "";
    },

    openComments() {
      this.showComments = true;
    },
  },
};
</script>

<style lang="scss" scoped>
.photo-viewer-viewport {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: #000000;
  z-index: 200;
  display: flex;
  flex-direction: column;

  .back-btn {
    display: flex;
    align-items: center;
    .back-text {
      color: #ffffff;
      font-size: 30rpx;
      margin-left: 12rpx;
      font-weight: bold;
    }
  }

  .progress-title {
    color: #ffffff;
    font-size: 30rpx;
    font-weight: bold;
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
  }

  /* 弹幕文字横向不换行机制 */
  .barrage-container {
    position: absolute;
    top: 200rpx;
    left: 0;
    width: 100%;
    height: 40%;
    pointer-events: none;
    z-index: 205;
    overflow: hidden;

    .barrage-item {
      position: absolute;
      left: 100vw;
      display: flex !important;
      flex-direction: row !important;
      align-items: center !important;
      white-space: nowrap !important;
      background-color: rgba(0, 0, 0, 0.65);
      padding: 10rpx 24rpx;
      border-radius: 40rpx;
      animation: flyBarrage 6s linear forwards;

      .barrage-avatar {
        width: 36rpx;
        height: 36rpx;
        border-radius: 50%;
        margin-right: 12rpx;
      }

      .barrage-text {
        color: #ffffff;
        font-size: 24rpx;
        font-weight: bold;
      }
    }
  }

  @keyframes flyBarrage {
    from {
      transform: translateX(0);
    }
    to {
      transform: translateX(-125vw);
    }
  }

  .image-swiper {
    flex: 1;
    width: 100%;

    .swiper-item {
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .main-image {
      width: 100%;
      height: 80%;
    }
  }

  /* 底部面板 */
  .footer-interaction-panel {
    background: linear-gradient(
      180deg,
      rgba(0, 0, 0, 0) 0%,
      rgba(0, 0, 0, 0.85) 100%
    );
    padding: 40rpx 32rpx calc(48rpx + env(safe-area-inset-bottom)) 32rpx;
    box-sizing: border-box;

    .reaction-scroll {
      margin-bottom: 32rpx;
      .reaction-row {
        display: flex;
        gap: 20rpx;

        .reaction-tag {
          background-color: rgba(255, 255, 255, 0.2);
          border-radius: 40rpx;
          padding: 12rpx 28rpx;
          display: flex;
          align-items: center;
          gap: 12rpx;

          .tag-emoji {
            font-size: 28rpx;
          }
          .tag-text {
            color: #ffffff;
            font-size: 26rpx;
            font-weight: bold;
          }
        }
      }
    }

    .author-tool-bar {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .author-info {
        display: flex;
        align-items: center;

        .avatar {
          width: 84rpx;
          height: 84rpx;
          border-radius: 50%;
          border: 3rpx solid #fff;
          margin-right: 20rpx;
        }

        .name-time {
          display: flex;
          flex-direction: column;
          .name {
            color: #ffffff;
            font-size: 28rpx;
            font-weight: bold;
          }
          .time {
            color: #94a3b8;
            font-size: 22rpx;
            margin-top: 4rpx;
          }
        }
      }

      .tool-icons {
        display: flex;
        gap: 36rpx;

        .icon-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          cursor: pointer;

          .icon-label {
            color: #ffffff;
            font-size: 20rpx;
            margin-top: 8rpx;
          }
        }

        .relative-box {
          position: relative;
          .transparent-share-btn {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            opacity: 0;
            z-index: 10;
          }
        }
      }
    }
  }

  /* 4. 评论底部抽屉 */
  .comment-drawer {
    position: fixed;
    bottom: -100%;
    left: 0;
    width: 100%;
    height: 70vh;
    background-color: #ffffff;
    border-top-left-radius: 32rpx;
    border-top-right-radius: 32rpx;
    z-index: 210;
    display: flex;
    flex-direction: column;
    transition: bottom 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
    box-shadow: 0 -8rpx 32rpx rgba(0, 0, 0, 0.15);

    &.drawer-open {
      bottom: 0;
    }

    .drawer-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 32rpx 40rpx;
      border-bottom: 2rpx solid #f1f5f9;

      .drawer-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #1a202c;
      }
    }

    /* 抽屉滚动区 */
    .options-scroll {
      flex: 1;
      overflow: hidden;

      .comment-list {
        padding: 32rpx 40rpx;
        display: flex;
        flex-direction: column;
        gap: 32rpx;
      }

      .comment-bubble-item {
        display: flex;
        gap: 24rpx;

        .c-avatar {
          width: 68rpx;
          height: 68rpx;
          border-radius: 50%;
          background-color: #f1f3f5;
        }

        .c-right-body {
          flex: 1;
          display: flex;
          flex-direction: column;

          .c-name-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8rpx;

            .c-user-name {
              font-size: 26rpx;
              font-weight: bold;
              color: #475569;
            }

            .c-time {
              font-size: 22rpx;
              color: #94a3b8;
            }
          }

          .c-main-text {
            font-size: 27rpx;
            color: #1e293b;
            line-height: 1.5;
            background-color: #f8fafc;
            padding: 16rpx 24rpx;
            border-radius: 0 24rpx 24rpx 24rpx;
            width: fit-content;
            max-width: 85%;
          }
        }
      }

      .empty-comment-box {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding-top: 160rpx;

        .empty-speech-bubble {
          width: 128rpx;
          height: 96rpx;
          background-color: #e6f7ee;
          border-radius: 24rpx;
          position: relative;
          border: 6rpx solid #bce6cf;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 32rpx;

          &::after {
            content: "><";
            font-weight: bold;
            color: #07c160;
            font-size: 30rpx;
            letter-spacing: -4rpx;
          }

          &::before {
            content: "";
            position: absolute;
            bottom: -22rpx;
            left: 40rpx;
            border-width: 12rpx;
            border-style: solid;
            border-color: #bce6cf transparent transparent transparent;
          }
        }

        .empty-tips {
          font-size: 26rpx;
          color: #94a3b8;
          font-weight: bold;
        }
      }
    }

    .drawer-footer-input {
      border-top: 2rpx solid #f1f5f9;
      background-color: #ffffff;
      padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)) 32rpx;

      .quick-reactions-scroll {
        margin-bottom: 20rpx;
        .quick-row {
          display: flex;
          gap: 16rpx;

          .q-tag {
            background-color: #f1f5f9;
            padding: 12rpx 24rpx;
            border-radius: 30rpx;
            font-size: 24rpx;
            color: #334155;
            cursor: pointer;

            &:active {
              background-color: #e2e8f0;
            }
          }
        }
      }

      .input-bar {
        display: flex;
        align-items: center;
        background-color: #f1f5f9;
        border-radius: 20rpx;
        padding: 16rpx 24rpx;

        .my-avatar {
          width: 56rpx;
          height: 56rpx;
          border-radius: 50%;
          margin-right: 20rpx;
        }

        .c-input {
          flex: 1;
          font-size: 28rpx;
          color: #333333;
        }
      }
    }
  }
}
</style>