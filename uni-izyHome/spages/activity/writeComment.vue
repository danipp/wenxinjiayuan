<template>
  <view class="write-review-container">
    <!-- 1. 顶部活动基本信息（高精还原括号设计） -->
    <view class="activity-header">
      <view class="title-row">
        <text class="bracket">「</text>
        <text class="title-text">{{ communityName }}</text>
        <text class="bracket">」</text>
      </view>
    </view>

    <!-- 2. 精准还原：卡片式星级评分选择器 -->
    <view class="rating-section">
      <view class="star-row">
        <view
          v-for="index in 5"
          :key="index"
          class="star-card-box"
          :class="{ 'star-card-active': index <= rating }"
          @click="setRating(index)"
        >
          <u-icon name="star-fill" color="#ffffff" size="24"></u-icon>
        </view>
      </view>
    </view>

    <!-- 3. 评价输入框卡片 -->
    <view class="input-card">
      <textarea
        v-model="content"
        placeholder="您的评价对其他用户都是很重要的参考(最多100字)"
        maxlength="100"
        class="review-textarea"
        placeholder-class="placeholder-style"
      ></textarea>
      <!-- 字数指示器 -->
      <view class="char-count">
        <text :class="{ 'limit-warn': content.length >= 100 }">{{
          content.length
        }}</text
        >/100
      </view>
    </view>

    <!-- 4. 底部固定提交按钮 -->
    <view class="footer-bar">
      <button
        class="submit-btn"
        :class="{ 'submit-btn-active': isFormValid }"
        @click="handleSubmit"
      >
        提交
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      activityId: "", // 活动ID
      communityName: "", // 活动/社区名称（默认兜底）
      rating: 0, // 选中的星级 (0-5)
      content: "", // 录入的评价正文
    };
  },
  computed: {
    // 只有当打过星且写了评价，提交按钮才激活高亮
    isFormValid() {
      return this.rating > 0 && this.content.trim().length > 0;
    },
  },
  onLoad(options) {
    // 接收从详情页传进来的社区名称和 ID
    if (options) {
      if (options.id) this.activityId = options.id;
      if (options.name) {
        this.communityName = decodeURIComponent(options.name);
      }
    }

    uni.setNavigationBarTitle({
      title: "活动评价",
    });
  },
  methods: {
    // 设置打分
    setRating(val) {
      this.rating = val;
    },
    // 提交数据
    handleSubmit() {
      if (!this.isFormValid) {
        uni.showToast({ title: "请先打分并填写评价内容", icon: "none" });
        return;
      }

      uni.showLoading({ title: "提交评价中..." });

      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({
          title: "评价成功，感谢您的反馈！",
          icon: "success",
        });

        // 成功后延迟返回上一页
        setTimeout(() => {
          uni.navigateBack();
        }, 1200);
      }, 1000);
    },
  },
};
</script>

<style lang="scss" scoped>
.write-review-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx;
  box-sizing: border-box;

  /* 顶部活动标题 */
  .activity-header {
    margin-bottom: 40rpx;
    padding-top: 16rpx;

    .title-row {
      display: flex;
      align-items: center;

      .bracket {
        font-size: 40rpx;
        font-weight: bold;
        color: #2b5c9c;
      }

      .title-text {
        font-size: 36rpx;
        font-weight: 800;
        color: #1a202c;
        background-color: #d1fae5;
        padding: 4rpx 12rpx;
        border-radius: 8rpx;
      }
    }
  }

  /* 评分区域 */
  .rating-section {
    margin-bottom: 40rpx;

    .star-row {
      display: flex;
      gap: 24rpx;

      /* 截图高保真星级圆角卡片 */
      .star-card-box {
        width: 88rpx;
        height: 88rpx;
        background-color: #cbd5e1; // 默认灰色卡片背景
        border-radius: 16rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: background-color 0.2s ease, transform 0.1s ease;
        cursor: pointer;

        &:active {
          transform: scale(0.95);
        }

        /* 选中时，卡片整体转为温暖金橙色 */
        &.star-card-active {
          background-color: #f5a623;
          box-shadow: 0 6rpx 16rpx rgba(245, 166, 35, 0.2);
        }
      }
    }
  }

  /* 文本卡片 */
  .input-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 36rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.015);
    position: relative;

    .review-textarea {
      width: 100%;
      height: 320rpx;
      font-size: 28rpx;
      color: #333333;
      line-height: 1.6;
    }

    .placeholder-style {
      color: #b2b2b2;
    }

    /* 字数统计 */
    .char-count {
      position: absolute;
      right: 32rpx;
      bottom: 24rpx;
      font-size: 22rpx;
      color: #94a3b8;

      .limit-warn {
        color: #ff4d4f;
        font-weight: bold;
      }
    }
  }

  /* 底部固定提交按钮 */
  .footer-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
    padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
    box-sizing: border-box;
    z-index: 100;

    .submit-btn {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      background-color: #a3e9c5; // 未写内容时的未激活淡灰色
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
      border-radius: 20rpx;
      transition: background-color 0.25s ease, box-shadow 0.25s ease;

      &::after {
        border: none;
      }

      /* 表单条件通过，激活动态样式 */
      &.submit-btn-active {
        background-color: #07c160; // 微信志愿绿
        box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
      }
    }
  }
}
</style>