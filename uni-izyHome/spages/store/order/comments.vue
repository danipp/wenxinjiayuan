<template>
  <view class="write-comment-container">
    <!-- 1. 顶部商品名称（高精还原括号设计） -->
    <view class="goods-header-bar">
      <view class="title-row">
        <text class="bracket">「</text>
        <text class="title-text">{{ goodsTitle }}</text>
        <text class="bracket">」</text>
      </view>
    </view>

    <!-- 2. 卡片式星级评分选择器 -->
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
        placeholder="请分享您对这件公益商品的体验感受(最多100字)"
        maxlength="100"
        class="review-textarea"
        placeholder-class="placeholder-style"
      ></textarea>
      <!-- 右下角字符指示器 -->
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
        提交评价
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      orderId: "", // 订单ID
      goodsTitle: "公益商品", // 商品标题（兜底）
      rating: 0, // 选中的星级 (0-5)
      content: "", // 评价内容
    };
  },
  computed: {
    // 只有打过星级且输入了内容，提交按钮才高亮并允许点击
    isFormValid() {
      return this.rating > 0 && this.content.trim().length > 0;
    },
  },
  onLoad(options) {
    // 接收买家订单列表跳转带过来的参数
    if (options) {
      if (options.id) this.orderId = options.id;
      if (options.communityName) {
        // 解码并还原商品标题（URL参数带过来的是 encodedTitle）
        this.goodsTitle = decodeURIComponent(options.communityName);
      }
    }

    uni.setNavigationBarTitle({
      title: "商品评价",
    });
  },
  methods: {
    setRating(val) {
      this.rating = val;
    },
    handleSubmit() {
      if (!this.isFormValid) {
        uni.showToast({ title: "请填写评分及体验感受", icon: "none" });
        return;
      }

      uni.showLoading({ title: "发布评价中..." });

      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({
          title: "评价成功，感谢您的反馈！",
          icon: "success",
        });

        // 成功后延迟返回上一级订单列表
        setTimeout(() => {
          uni.navigateBack();
        }, 1200);
      }, 1000);
    },
  },
};
</script>

<style lang="scss" scoped>
.write-comment-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx;
  box-sizing: border-box;

  /* 顶部商品名 */
  .goods-header-bar {
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
        font-size: 32rpx;
        font-weight: 800;
        color: #1a202c;
        background-color: #d1fae5; // 淡绿高亮底
        padding: 4rpx 12rpx;
        border-radius: 8rpx;
      }
    }
  }

  /* 星级卡片评分区 */
  .rating-section {
    margin-bottom: 40rpx;

    .star-row {
      display: flex;
      gap: 24rpx;

      /* 截图高保真星级圆角卡片 */
      .star-card-box {
        width: 88rpx;
        height: 88rpx;
        background-color: #cbd5e1; // 默认灰色
        border-radius: 16rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: background-color 0.2s ease, transform 0.1s ease;
        cursor: pointer;

        &:active {
          transform: scale(0.95);
        }

        /* 选中态高亮温暖金色 */
        &.star-card-active {
          background-color: #e2ab5b;
          box-shadow: 0 6rpx 16rpx rgba(226, 171, 91, 0.2);
        }
      }
    }
  }

  /* 评价文本框卡片 */
  .input-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 36rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.015);
    position: relative;

    .review-textarea {
      width: 100%;
      height: 320rpx;
      font-size: 29rpx;
      color: #333333;
      line-height: 1.6;
    }

    .placeholder-style {
      color: #b2b2b2;
    }

    /* 字符数指示器 */
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
      border-radius: 48rpx; // 胶囊圆角
      transition: background-color 0.25s ease, box-shadow 0.25s ease;

      &::after {
        border: none;
      }

      /* 表单条件通过，激活动态高亮 */
      &.submit-btn-active {
        background-color: #07c160; // 微信志愿绿
        box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
      }
    }
  }
}
</style>