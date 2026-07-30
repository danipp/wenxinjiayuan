<template>
  <view class="detail-container">
    <!-- 1. 顶部服务简评卡片 -->
    <view
      class="service-brief-card"
      v-if="detail.status === 'completed' || detail.status === 'toEvaluate'"
    >
      <view class="brief-left">
        <text class="service-title">{{ detail.title }}</text>
        <text class="service-subtitle"
          >近30天{{ detail.useCount || 0 }}人使用了该服务</text
        >
      </view>
      <view class="brief-right">
        <view class="rate-num-box">
          <text class="rate-num">{{ detail.rating || "100" }}</text>
          <text class="percent">%</text>
        </view>
        <text class="rate-label">好评率</text>
      </view>
    </view>

    <!-- 2. 帮助详情主卡片 -->
    <view class="detail-main-card">
      <!-- 状态显示（右上角） -->
      <!-- A. 终端状态：盖章 -->
      <div
        v-if="isTerminalState(detail.status)"
        class="status-stamp"
        :class="detail.status"
      >
        <span class="stamp-text">{{ detail.statusText }}</span>
      </div>
      <!-- B. 活跃状态：微标 -->
      <view v-else class="status-badge" :class="detail.status">
        <text class="badge-dot"></text>
        <text class="badge-text">{{ detail.statusText }}</text>
      </view>

      <!-- 标题 -->
      <view class="detail-title-row">
        <text class="title-text">帮助详情</text>
        <text class="tag-label">代发布</text>
      </view>
      <text class="publish-date">{{ detail.publishDate }}</text>

      <!-- 【已评价展示区】：已完成且已有评价时展示 -->
      <view
        v-if="detail.status === 'completed' && detail.evaluation"
        class="evaluation-bubble-box"
      >
        <view class="bubble-content">
          <text class="evaluation-title">{{
            detail.evaluation.ratingLabel
          }}</text>
          <u-line color="#ffeed6" style="margin: 16rpx 0"></u-line>
          <text class="evaluation-desc">{{
            detail.evaluation.remark || "暂无备注"
          }}</text>
        </view>
        <!-- 根据评价动态展示表情 -->
        <view class="emoji-face" :class="detail.evaluation.rating">
          {{ getEvaluationEmoji(detail.evaluation.rating) }}
        </view>
      </view>

      <!-- 【待评价区】：待评价、自己发布时展示 -->
      <view v-if="canSubmitEvaluation" class="evaluation-form-card">
        <view class="evaluation-form-header">
          <text class="form-title">给帮助者评价</text>
          <text class="form-subtitle">请选择本次帮助的满意度</text>
        </view>
        <view class="rating-options">
          <view
            v-for="item in satisfactionOptions"
            :key="item.value"
            class="rating-option"
            :class="{ active: evaluationForm.rating === item.value }"
            @click="selectRating(item)"
          >
            <text>{{ item.label }}</text>
          </view>
        </view>
        <textarea
          v-model="evaluationForm.remark"
          class="remark-input"
          placeholder="可以补充一句备注，例如：帮助很及时、沟通很顺畅"
          placeholder-class="remark-placeholder"
          maxlength="120"
        />
        <view class="remark-count">{{ evaluationForm.remark.length }}/120</view>
        <button class="submit-evaluation-btn" @click="submitEvaluation">
          提交评价
        </button>
      </view>

      <!-- 核心属性 -->
      <view class="attributes-list">
        <view class="attr-row">
          <text class="attr-label">时间：</text>
          <text class="attr-value">{{ detail.time }}</text>
        </view>
        <view class="attr-row">
          <text class="attr-label">地点：</text>
          <text class="attr-value">{{ detail.location }}</text>
        </view>
        <view class="attr-row" v-if="detail.memberCount">
          <text class="attr-label">人数：</text>
          <text class="attr-value">{{ detail.memberCount }}</text>
        </view>
        <view class="attr-row">
          <text class="attr-label">说明：</text>
          <text class="attr-value">{{ detail.description }}</text>
        </view>
      </view>
    </view>

    <!-- 3. 底部操作按钮（根据不同状态展示不同功能） -->
    <view
      v-if="detail.status === 'pending' || detail.status === 'helping'"
      class="footer-bar"
    >
      <!-- 待帮忙状态：允许接单 -->
      <button
        v-if="detail.status === 'pending'"
        class="btn-action btn-pending"
        @click="handleApply"
      >
        我来帮忙
      </button>

      <!-- 已接单状态：置灰信息显示 -->
      <button
        v-if="detail.status === 'helping' && detail.isMine"
        class="btn-action"
        @click="callPhone"
      >
        <view class="flex" style="width: 100%; justify-content: center">
          <u-icon
            name="chat-fill"
            color="#555555"
            size="36rpx"
            style="margin-right: 8rpx"
          ></u-icon>
          联系Ta
        </view>
      </button>
      <button
        v-if="detail.status === 'helping' && !detail.isMine"
        class="btn-action btn-helping-disabled"
        disabled
      >
        已被接单
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      detail: {},
      satisfactionOptions: [
        { label: "非常满意", value: "very_satisfied" },
        { label: "满意", value: "satisfied" },
        { label: "一般", value: "normal" },
        { label: "不满意", value: "unsatisfied" },
        { label: "非常不满意", value: "very_unsatisfied" },
      ],
      evaluationForm: {
        rating: "",
        ratingLabel: "",
        remark: "",
      },
      // 模拟 5 种业务场景数据库
      mockDatabase: {
        1: {
          id: 1,
          title: "代买东西",
          useCount: 160,
          rating: "99.92",
          status: "completed",
          statusText: "已完成",
          publishDate: "07月01日发布",
          time: "双方协商",
          location: "海珠区 | 海心沙亚运公园",
          memberCount: "1/1",
          description: "买艘船",
          isMine: true,
          evaluation: {
            rating: "satisfied",
            ratingLabel: "满意！感谢志愿者",
            remark: "很好",
          },
        },
        5: {
          id: 5,
          title: "陪同复诊取药",
          useCount: 32,
          rating: "100.00",
          status: "toEvaluate",
          statusText: "待评价",
          publishDate: "07月03日发布",
          time: "07-05 09:00 至 07-05 11:00",
          location: "海珠区 | 广州医科大学附属第二医院",
          memberCount: "1/1",
          description: "帮忙陪同老人完成复诊，并协助取药后送回家中。",
          isMine: true,
        },
        2: {
          id: 2,
          title: "上门除尘与衣物整理",
          useCount: 12,
          rating: "100.00",
          status: "pending",
          statusText: "待帮忙",
          publishDate: "刚刚发布",
          time: "本周六下午 (双方协商)",
          location: "越秀区青菜岗43号启东楼",
          memberCount: "0/1",
          description:
            "家中有高龄独居老人，平时衣物整理不便，希望能有志愿者协助除尘和简单收纳。",
        },
        3: {
          id: 3,
          title: "陪同医院做检查与取药",
          useCount: 45,
          rating: "98.50",
          status: "helping",
          isMine: false,
          statusText: "已接单", // 按照用户要求，将帮助中换为已接单
          publishDate: "10分钟前发布",
          time: "12-28 09:00 至 12-28 12:00",
          location: "广州市越秀区中医医院",
          memberCount: "1/1",
          description:
            "带老人去门诊大厅签到并做B超检查，随后协助窗口取药送回家中。",
        },
        4: {
          id: 4,
          title: "地点位置校验",
          useCount: 8,
          rating: "95.00",
          status: "expired",
          statusText: "已过期",
          publishDate: "12月27日发布",
          time: "2025年12月27日 星期六 17:15",
          location: "启东楼-广东省广州市越秀区青菜岗43号",
          memberCount: "0/1",
          description: "请前往现场,校验该地点是否还存在",
        },
      },
    };
  },
  computed: {
    canSubmitEvaluation() {
      return (
        this.detail.status === "toEvaluate" &&
        this.detail.isMine &&
        !this.detail.evaluation
      );
    },
  },
  onLoad(options) {
    // 根据列表传进来的 id，渲染对应状态的数据，默认展示已完成 (id=1)
    const activeId = options && options.id ? options.id : "1";
    this.detail = this.mockDatabase[activeId] || this.mockDatabase["1"];

    // 设置页面标题
    uni.setNavigationBarTitle({
      title: "互助详情",
    });
  },
  methods: {
    callPhone() {
      uni.makePhoneCall({
        phoneNumber: this.detail.phone || "13800138000",
      });
    },
    isTerminalState(status) {
      return (
        status === "completed" ||
        status === "toEvaluate" ||
        status === "expired"
      );
    },
    // 接单
    handleApply() {
      uni.showModal({
        title: "温馨提示",
        content: "确定承接该帮扶任务吗？",
        success: (res) => {
          if (res.confirm) {
            uni.showToast({ title: "接单成功！", icon: "success" });
            this.detail.status = "helping";
            this.detail.statusText = "已接单";
          }
        },
      });
    },
    selectRating(item) {
      this.evaluationForm.rating = item.value;
      this.evaluationForm.ratingLabel = item.label;
    },
    getEvaluationEmoji(rating) {
      const emojiMap = {
        very_satisfied: "😆",
        satisfied: "😊",
        normal: "😐",
        unsatisfied: "😕",
        very_unsatisfied: "😞",
      };
      return emojiMap[rating] || "😊";
    },
    submitEvaluation() {
      if (!this.canSubmitEvaluation) return;
      if (!this.evaluationForm.rating) {
        uni.showToast({ title: "请选择满意度", icon: "none" });
        return;
      }

      this.$set(this.detail, "evaluation", {
        rating: this.evaluationForm.rating,
        ratingLabel: this.evaluationForm.ratingLabel,
        remark: this.evaluationForm.remark,
      });
      this.detail.status = "completed";
      this.detail.statusText = "已完成";
      this.evaluationForm = {
        rating: "",
        ratingLabel: "",
        remark: "",
      };
      uni.showToast({ title: "评价提交成功", icon: "success" });
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

  /* 1. 顶部服务卡片 */
  .service-brief-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx 40rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.015);
    margin-bottom: 28rpx;

    .brief-left {
      display: flex;
      flex-direction: column;

      .service-title {
        font-size: 40rpx;
        font-weight: 800;
        color: #1c2438;
        margin-bottom: 12rpx;
      }

      .service-subtitle {
        font-size: 26rpx;
        color: #7f8c8d;
      }
    }

    .brief-right {
      display: flex;
      flex-direction: column;
      align-items: flex-end;

      .rate-num-box {
        display: flex;
        align-items: baseline;
        color: #b05c12; // 暖金木糖色

        .rate-num {
          font-family: "Georgia", serif;
          font-size: 52rpx;
          font-weight: bold;
        }

        .percent {
          font-size: 26rpx;
          margin-left: 4rpx;
        }
      }

      .rate-label {
        font-size: 22rpx;
        color: #95a5a6;
        margin-top: 4rpx;
      }
    }
  }

  /* 2. 帮助详情卡片 */
  .detail-main-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 40rpx;
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.015);
    position: relative;
    overflow: hidden;

    /* A. 已完成/已过期 盖章 */
    .status-stamp {
      position: absolute;
      top: 32rpx;
      right: 32rpx;
      width: 116rpx;
      height: 116rpx;
      border: 4rpx solid #ccc;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      transform: rotate(-15deg);
      box-sizing: border-box;

      &::after {
        content: "";
        position: absolute;
        width: 96rpx;
        height: 96rpx;
        border: 2rpx dashed #ccc;
        border-radius: 50%;
        box-sizing: border-box;
      }

      .stamp-text {
        font-size: 22rpx;
        font-weight: bold;
        z-index: 1;
      }

      &.completed {
        border-color: #b2b9c3;
        &::after {
          border-color: #b2b9c3;
        }
        .stamp-text {
          color: #b2b9c3;
        }
      }

      &.expired {
        border-color: #c0c4cc;
        &::after {
          border-color: #c0c4cc;
        }
        .stamp-text {
          color: #c0c4cc;
        }
      }
    }

    /* B. 待帮忙/已接单 徽章 */
    .status-badge {
      position: absolute;
      top: 40rpx;
      right: 40rpx;
      display: inline-flex;
      align-items: center;
      padding: 8rpx 20rpx;
      border-radius: 40rpx;

      .badge-dot {
        width: 12rpx;
        height: 12rpx;
        border-radius: 50%;
        margin-right: 12rpx;
      }

      .badge-text {
        font-size: 24rpx;
        font-weight: bold;
      }

      &.pending {
        background-color: #fff7e6;
        .badge-dot {
          background-color: #d97706;
        }
        .badge-text {
          color: #d97706;
        }
      }

      &.helping {
        background-color: #e0f2fe;
        .badge-dot {
          background-color: #0284c7;
        }
        .badge-text {
          color: #0284c7;
        }
      }
    }

    /* 标题与代发布 Tag */
    .detail-title-row {
      display: flex;
      align-items: center;
      margin-bottom: 16rpx;

      .title-text {
        font-size: 36rpx;
        font-weight: bold;
        color: #2c3e50;
      }

      .tag-label {
        font-size: 22rpx;
        color: #0284c7;
        background-color: #e0f2fe;
        padding: 4rpx 12rpx;
        border-radius: 8rpx;
        margin-left: 16rpx;
        font-weight: bold;
      }
    }

    .publish-date {
      font-size: 26rpx;
      color: #95a5a6;
      display: block;
      margin-bottom: 40rpx;
    }

    /* 满意评价气泡区 */
    .evaluation-bubble-box {
      background-color: #fff8f0;
      border: 2rpx solid #ffdcb0;
      border-radius: 24rpx;
      padding: 32rpx;
      position: relative;
      margin-bottom: 48rpx;
      box-shadow: 0 4rpx 16rpx rgba(253, 196, 25, 0.05);

      .bubble-content {
        width: 80%;

        .evaluation-title {
          font-size: 32rpx;
          font-weight: 800;
          color: #ff781e;
        }

        .evaluation-desc {
          font-size: 28rpx;
          color: #4a5568;
        }
      }

      /* 3D 萌脸表情 */
      .emoji-face {
        position: absolute;
        right: 24rpx;
        top: 50%;
        transform: translateY(-50%);
        width: 104rpx;
        height: 104rpx;
        background-color: #ffd214;
        border-radius: 50%;
        box-shadow: 0 8rpx 20rpx rgba(253, 216, 53, 0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 56rpx;

        &.normal {
          background-color: #f1c40f;
          box-shadow: 0 8rpx 20rpx rgba(241, 196, 15, 0.35);
        }

        &.unsatisfied {
          background-color: #ffb86c;
          box-shadow: 0 8rpx 20rpx rgba(255, 184, 108, 0.35);
        }

        &.very_unsatisfied {
          background-color: #dfe6e9;
          box-shadow: 0 8rpx 20rpx rgba(149, 165, 166, 0.25);
        }
      }
    }

    /* 待评价表单区 */
    .evaluation-form-card {
      background-color: #fffaf3;
      border: 2rpx solid #ffdcb0;
      border-radius: 24rpx;
      padding: 32rpx;
      margin-bottom: 48rpx;
      box-shadow: 0 4rpx 16rpx rgba(253, 196, 25, 0.05);

      .evaluation-form-header {
        display: flex;
        flex-direction: column;
        margin-bottom: 28rpx;

        .form-title {
          font-size: 32rpx;
          font-weight: 800;
          color: #2c3e50;
        }

        .form-subtitle {
          font-size: 24rpx;
          color: #95a5a6;
          margin-top: 8rpx;
        }
      }

      .rating-options {
        display: flex;
        flex-wrap: wrap;
        gap: 16rpx;
        margin-bottom: 24rpx;

        .rating-option {
          padding: 14rpx 20rpx;
          border-radius: 36rpx;
          border: 2rpx solid #edf2f7;
          background-color: #ffffff;
          color: #4a5568;
          font-size: 26rpx;

          &.active {
            border-color: #ff9f43;
            background-color: #fff1dc;
            color: #d97706;
            font-weight: bold;
          }
        }
      }

      .remark-input {
        width: 100%;
        height: 172rpx;
        border-radius: 20rpx;
        background-color: #ffffff;
        border: 2rpx solid #edf2f7;
        padding: 20rpx;
        box-sizing: border-box;
        font-size: 28rpx;
        color: #2c3e50;
      }

      .remark-placeholder {
        color: #b8c2cc;
        font-size: 26rpx;
      }

      .remark-count {
        text-align: right;
        font-size: 24rpx;
        color: #a0aec0;
        margin-top: 12rpx;
      }

      .submit-evaluation-btn {
        width: 100%;
        height: 84rpx;
        line-height: 84rpx;
        margin-top: 24rpx;
        border-radius: 42rpx;
        background-color: #ff9f43;
        color: #ffffff;
        font-size: 30rpx;
        font-weight: bold;
        box-shadow: 0 8rpx 24rpx rgba(255, 159, 67, 0.25);

        &::after {
          border: none;
        }
      }
    }

    /* 数据详情字段展示区 */
    .attributes-list {
      display: flex;
      flex-direction: column;
      gap: 24rpx;

      .attr-row {
        display: flex;
        line-height: 1.6;

        .attr-label {
          font-size: 30rpx;
          color: #7f8c8d;
          width: 110rpx;
          white-space: nowrap;
        }

        .attr-value {
          flex: 1;
          font-size: 30rpx;
          color: #2c3e50;
          word-break: break-all;
        }
      }
    }
  }

  /* 3. 底部固定按钮区 */
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

    .btn-action {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      font-size: 32rpx;
      font-weight: bold;
      border-radius: 48rpx;

      &::after {
        border: none;
      }

      /* 可接单 */
      &.btn-pending {
        background-color: #07c160;
        color: #ffffff;
        box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
      }

      /* 已接单变灰且禁用 */
      &.btn-helping-disabled {
        background-color: #edf2f7;
        color: #a0aec0;
      }
    }
  }
}
</style>