<template>
  <view class="comments-page-container">
    <scroll-view scroll-y class="comments-scroll-view">
      <view class="comments-list">
        <view
          v-for="(item, index) in comments"
          :key="index"
          class="comment-card"
        >
          <!-- 评价头部 -->
          <view class="user-header">
            <image class="avatar" :src="item.avatar" mode="aspectFill"></image>
            <view class="user-info">
              <text class="username">{{ item.name }}</text>
              <text class="time">{{ item.time }}</text>
            </view>
            <view class="star-rating">⭐️⭐️⭐️⭐️⭐️</view>
          </view>

          <!-- 评价正文 -->
          <text class="comment-content">{{ item.content }}</text>

          <!-- 评价素材图片 -->
          <view v-if="item.images && item.images.length" class="comment-images">
            <image
              v-for="(img, imgIdx) in item.images"
              :key="imgIdx"
              class="comment-img"
              :src="img"
              mode="aspectFill"
              @click="previewImage(img, item.images)"
            ></image>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      comments: [
        {
          name: "秉治",
          avatar: "https://cdn.uviewui.com/uview/album/1.jpg",
          time: "1小时前",
          content: "保温杯质量很赞，保温时间非常持久，拿出去做志愿服务很合适。",
          images: [
            "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=200&q=80",
            "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=200&q=80",
          ],
        },
        {
          name: "罗完成",
          avatar: "https://cdn.uviewui.com/uview/album/2.jpg",
          time: "5小时前",
          content: "拿积分换的，感觉非常有社区温度，支持！",
          images: [
            "https://images.unsplash.com/photo-1572635196237-14b3f281503f?auto=format&fit=crop&w=200&q=80",
          ],
        },
        {
          name: "张阿姨",
          avatar: "https://cdn.uviewui.com/uview/album/3.jpg",
          time: "1天前",
          content: "杯子很轻便，不锈钢材质也放心，很适合晨跑的时候带着。",
          images: [],
        },
        {
          name: "李大哥",
          avatar: "https://cdn.uviewui.com/uview/album/4.jpg",
          time: "3天前",
          content: "非常扎实的做工，对得起我的爱心积分！五星好评！",
          images: [
            "https://images.unsplash.com/photo-1563225409-127c18758bd5?auto=format&fit=crop&w=200&q=80",
            "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?auto=format&fit=crop&w=200&q=80",
            "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?auto=format&fit=crop&w=200&q=80",
          ],
        },
      ],
    };
  },
  onLoad(options) {
    uni.setNavigationBarTitle({ title: "全部评价" });
  },
  methods: {
    previewImage(current, urls) {
      uni.previewImage({ current, urls });
    },
  },
};
</script>

<style lang="scss" scoped>
.comments-page-container {
  height: 100vh;
  background-color: #f7f9fb;
  display: flex;
  flex-direction: column;

  .comments-scroll-view {
    flex: 1;
    overflow: hidden;
  }

  .comments-list {
    padding: 32rpx;
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }

  .comment-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.015);
    display: flex;
    flex-direction: column;

    .user-header {
      display: flex;
      align-items: center;
      margin-bottom: 24rpx;

      .avatar {
        width: 76rpx;
        height: 72rpx;
        border-radius: 50%;
        background-color: #f1f5f9;
        margin-right: 20rpx;
      }

      .user-info {
        display: flex;
        flex-direction: column;
        flex: 1;

        .username {
          font-size: 28rpx;
          font-weight: bold;
          color: #334155;
        }

        .time {
          font-size: 22rpx;
          color: #94a3b8;
          margin-top: 4rpx;
        }
      }

      .star-rating {
        font-size: 16rpx;
      }
    }

    .comment-content {
      font-size: 27rpx;
      color: #334155;
      line-height: 1.6;
    }

    .comment-images {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      margin-top: 20rpx;

      .comment-img {
        width: 160rpx;
        height: 160rpx;
        border-radius: 12rpx;
        background-color: #f1f5f9;
      }
    }
  }
}
</style>