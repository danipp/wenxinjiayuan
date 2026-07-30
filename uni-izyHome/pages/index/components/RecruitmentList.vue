<template>
  <view class="recruitment-list">
    <view
      class="recruitment-item"
      v-for="item in list"
      :key="item.id"
      @click="$emit('goDetail', item.id)"
    >
      <image class="cover" :src="item.image" mode="aspectFill"></image>
      <view class="info">
        <view class="title">{{ item.title }}</view>
        <view class="desc">
          <text class="organizer">发起方: {{ item.organizer }}</text>
          <text class="status">{{ item.status }}</text>
        </view>
        <view class="user-avatars" v-if="item.avatars && item.avatars.length">
          <image
            v-for="(avatar, idx) in item.avatars.slice(0, 3)"
            :key="idx"
            class="avatar-item"
            :src="avatar"
          >
          </image>
          <view class="avatar-more">+{{ item.joinCount || item.avatars.length }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: "RecruitmentList",
  data() {
    return {
      list: [
        {
          id: 1,
          title: "周末环保志愿者招募",
          image: "https://cdn.uviewui.com/uview/album/1.jpg",
          organizer: "社区居委会",
          status: "招募中",
          avatars: [
            "https://cdn.uviewui.com/uview/album/1.jpg",
            "https://cdn.uviewui.com/uview/album/2.jpg",
            "https://cdn.uviewui.com/uview/album/3.jpg",
          ],
          joinCount: 12,
        },
        {
          id: 2,
          title: "社区图书管理员招募",
          image: "https://cdn.uviewui.com/uview/album/2.jpg",
          organizer: "社区服务中心",
          status: "招募中",
          avatars: [
            "https://cdn.uviewui.com/uview/album/4.jpg",
            "https://cdn.uviewui.com/uview/album/5.jpg",
          ],
          joinCount: 5,
        },
      ],
    };
  },
};
</script>

<style scoped lang="scss">
.recruitment-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;

  .recruitment-item {
    display: flex;
    background-color: #ffffff;
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
          color: #07c160;
          background-color: #e6f9f0;
          padding: 4rpx 12rpx;
          border-radius: 8rpx;
        }
      }

      .user-avatars {
        display: flex;
        align-items: center;
        margin-top: 12rpx;

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
</style>
