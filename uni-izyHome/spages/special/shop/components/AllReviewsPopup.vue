<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="20"
    @close="handleClose"
    :safeAreaInsetBottom="true"
    @touchmove.stop.prevent
  >
    <view class="all-reviews-popup">
      <view class="popup-header">
        <text class="popup-title">全部评价（{{ totalCount }}）</text>
        <view class="close-btn" @click="handleClose">
          <u-icon name="close" color="#999" size="18"></u-icon>
        </view>
      </view>

      <scroll-view class="review-scroll" scroll-y @scrolltolower="loadMore">
        <view v-if="list.length === 0 && !loading" class="empty-state">
          <text class="empty-text">暂无评价</text>
        </view>

        <view v-for="r in list" :key="r.reviewId" class="review-card">
          <view class="review-header">
            <u-avatar
              class="r-avatar"
              :src="r.userAvatar"
              size="54rpx"
              mode="aspectFill"
            ></u-avatar>
            <view class="r-user-info">
              <text class="r-username">{{ r.userName }}</text>
              <text class="r-time">{{ formatTime(r.createTime) }}</text>
            </view>
            <text class="r-stars">{{ starsText(r.rating) }}</text>
          </view>
          <text class="review-text">{{ r.content }}</text>
          <view v-if="r.images && r.images.length" class="review-images">
            <image
              v-for="(img, i) in r.images"
              :key="i"
              class="review-img"
              :src="img"
              mode="aspectFill"
              @click.stop="previewImage(img, r.images)"
            ></image>
          </view>
        </view>

        <view v-if="loading" class="loading-tip">
          <u-loading-icon></u-loading-icon>
          <text>加载中...</text>
        </view>
        <view v-if="noMore && list.length > 0" class="no-more-tip"
          >已加载全部</view
        >
      </scroll-view>
    </view>
  </u-popup>
</template>

<script>
import { reviewList } from "@/spages/api/special.js";

export default {
  name: "AllReviewsPopup",
  props: {
    show: { type: Boolean, default: false },
    shopId: { type: [Number, String], default: "" },
    totalCount: { type: Number, default: 0 },
  },
  watch: {
    show(val) {
      if (val && this.list.length === 0) {
        this.fetchList();
      }
    },
  },
  data() {
    return {
      list: [],
      pageNumber: 1,
      pageSize: 10,
      loading: false,
      noMore: false,
    };
  },
  methods: {
    async fetchList(append = false) {
      if (this.loading || this.noMore) return;
      if (!append) {
        this.pageNumber = 1;
        this.list = [];
        this.noMore = false;
      }
      this.loading = true;
      try {
        const res = await reviewList({
          shopId: this.shopId,
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
        });
        if (res.code === "00000" && res.data) {
          const { content = [], last } = res.data;
          this.list = append ? this.list.concat(content) : content;
          this.noMore = last !== false;
          if (!last) this.pageNumber++;
        }
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },

    loadMore() {
      if (this.noMore || this.loading) return;
      this.fetchList(true);
    },

    // 预览图片
    previewImage(current, urls) {
      uni.previewImage({ current, urls });
    },

    // 星级文字
    starsText(rating) {
      const n = rating || 5;
      return "⭐️".repeat(Math.min(n, 5));
    },

    formatTime(str) {
      if (!str) return "";
      const d = new Date(str.replace(/-/g, "/"));
      if (isNaN(d.getTime())) return str;
      const pad = (n) => String(n).padStart(2, "0");
      return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(
        d.getHours()
      )}:${pad(d.getMinutes())}`;
    },

    handleClose() {
      this.$emit("update:show", false);
    },
  },
};
</script>

<style lang="scss" scoped>
.all-reviews-popup {
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  display: flex;
  flex-direction: column;
  max-height: 85vh;
  overflow: hidden;

  .popup-header {
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    padding: 36rpx 0 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
    flex-shrink: 0;

    .popup-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .close-btn {
      position: absolute;
      right: 36rpx;
      top: 50%;
      transform: translateY(-50%);
      padding: 4rpx;
    }
  }

  .review-scroll {
    flex: 1;
    max-height: 70vh;
    padding: 20rpx 32rpx;

    .empty-state {
      display: flex;
      justify-content: center;
      padding: 120rpx 0;
      .empty-text {
        font-size: 28rpx;
        color: #999;
      }
    }
  }

  .review-card {
    background: #f7f9fb;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;

    .review-header {
      display: flex;
      align-items: center;
      margin-bottom: 12rpx;

      .r-avatar {
        margin-right: 12rpx;
        flex-shrink: 0;
      }

      .r-user-info {
        display: flex;
        flex-direction: column;
        flex: 1;
        margin-left: 10rpx;
        .r-username {
          font-size: 24rpx;
          font-weight: bold;
          color: #334155;
        }

        .r-time {
          font-size: 20rpx;
          color: #94a3b8;
          margin-top: 2rpx;
        }
      }

      .r-stars {
        font-size: 18rpx;
        flex-shrink: 0;
      }
    }

    .review-text {
      font-size: 26rpx;
      color: #475569;
      line-height: 1.6;
    }

    .review-images {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      margin-top: 16rpx;

      .review-img {
        width: 150rpx;
        height: 150rpx;
        border-radius: 10rpx;
        background: #e2e8f0;
      }
    }
  }

  .loading-tip,
  .no-more-tip {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12rpx;
    padding: 24rpx 0;
    font-size: 24rpx;
    color: #999;
  }
}
</style>
