<template>
  <view class="create-service-container">
    <!-- 1. 服务团队卡片 -->
    <view class="team-brief-card">
      <text class="team-label">服务团队：社区志愿者</text>
      <text class="free-badge">(免费)</text>
    </view>

    <!-- 2. 服务人信息卡片 -->
    <view class="form-card">
      <view class="card-header">
        <text class="card-title">服务人信息</text>
        <view class="record-link" @click="goToMemberList">
          <u-icon name="file-text" color="#07c160" size="14"></u-icon>
          <text class="link-text">查看记录</text>
        </view>
      </view>

      <!-- 输入域 -->
      <view class="input-row">
        <input
          type="text"
          v-model="form.name"
          placeholder="填写需求人姓名"
          placeholder-class="placeholder-style"
          class="form-input"
        />
      </view>
      <u-line color="#f1f5f9"></u-line>

      <view class="input-row">
        <input
          type="number"
          v-model="form.phone"
          placeholder="填写手机号"
          placeholder-class="placeholder-style"
          class="form-input"
          maxlength="11"
        />
      </view>
      <u-line color="#f1f5f9"></u-line>

      <view class="input-row" @click="chooseLocation">
        <text class="form-text" :class="{ 'placeholder-style': !form.address }">
          {{ form.address || "选择地址" }}
        </text>
        <u-icon name="arrow-right" color="#cbd5e1" size="14"></u-icon>
      </view>
      <u-line color="#f1f5f9"></u-line>

      <view class="input-row">
        <input
          type="text"
          v-model="form.detailAddress"
          placeholder="填写详细门牌号 (非必填)"
          placeholder-class="placeholder-style"
          class="form-input"
        />
      </view>
    </view>

    <!-- 3. 期望时间卡片 -->
    <view class="form-card">
      <view class="card-title" style="margin-bottom: 28rpx">期望时间</view>
      <view class="radio-group-row">
        <view class="radio-item" @click="form.timeType = 'negotiate'">
          <view
            class="radio-icon"
            :class="{ 'radio-active': form.timeType === 'negotiate' }"
          >
            <view class="inner-dot"></view>
          </view>
          <text class="radio-label">双方协商</text>
        </view>
        <view class="radio-item" @click="form.timeType = 'specific'">
          <view
            class="radio-icon"
            :class="{ 'radio-active': form.timeType === 'specific' }"
          >
            <view class="inner-dot"></view>
          </view>
          <text class="radio-label">指定时间</text>
        </view>
      </view>

      <!-- 【条件联动】：选择指定时间后，平滑多出该行 -->
      <view
        v-if="form.timeType === 'specific'"
        class="specific-time-row"
        @click="openTimeSelector"
      >
        <text class="label">选择帮助时间</text>
        <view class="val-box">
          <text
            class="val-text"
            :class="{ 'placeholder-style': !form.specificTime }"
          >
            {{ form.specificTime || "请选择帮助时间" }}
          </text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
    </view>

    <!-- 4. 详细要求卡片 -->
    <view class="form-card">
      <view class="card-title" style="margin-bottom: 28rpx">详细要求选填</view>
      <view class="remark-row">
        <textarea
          v-model="form.remark"
          placeholder="其他说明，300字以内"
          maxlength="300"
          class="remark-textarea"
          placeholder-class="placeholder-style"
        ></textarea>
      </view>
    </view>

    <!-- 5. 底部提交 -->
    <view class="footer-bar">
      <button
        class="submit-btn"
        :class="{ 'submit-btn-active': isFormValid }"
        @click="handleSubmit"
      >
        提交
      </button>
    </view>

    <!-- 左右联动时间选择弹窗 (升级支持未来 30 天纵向滑动选择) -->
    <u-popup
      :show="showTimePopup"
      mode="bottom"
      round="16"
      @close="showTimePopup = false"
      @touchmove.stop.prevent
    >
      <view class="time-popup-panel">
        <text class="popup-header-title">请选择帮助时间</text>

        <!-- 双列联动滑块区 -->
        <view class="picker-split-box">
          <!-- 左列：日期选项（支持向上/向下滑动选择未来30天） -->
          <scroll-view scroll-y class="col-left-dates">
            <view
              v-for="(date, dIdx) in timeSelectionData"
              :key="dIdx"
              class="date-option-item"
              :class="{ 'time-active': activeDateIndex === dIdx }"
              @click="onDateSelect(dIdx)"
            >
              {{ date.label }}
            </view>
          </scroll-view>

          <!-- 右列：具体时间段选择器 -->
          <scroll-view scroll-y class="col-right-slots">
            <view
              v-for="(slot, sIdx) in currentActiveSlots"
              :key="sIdx"
              class="slot-option-item"
              :class="{ 'slot-active': tempSelectedSlot === slot }"
              @click="tempSelectedSlot = slot"
            >
              <text class="slot-text">{{ slot }}</text>
              <u-icon
                v-if="tempSelectedSlot === slot"
                name="checkbox-mark"
                color="#07c160"
                size="14"
              ></u-icon>
            </view>
          </scroll-view>
        </view>

        <!-- 动作确认栏 -->
        <view class="popup-actions">
          <button class="action-btn btn-cancel" @click="showTimePopup = false">
            取消
          </button>
          <button
            class="action-btn btn-confirm"
            :class="{ 'btn-confirm-active': tempSelectedSlot }"
            @click="confirmTimeSelection"
          >
            确定
          </button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
export default {
  data() {
    return {
      serviceName: "上门服务",
      form: {
        name: "",
        phone: "",
        address: "",
        detailAddress: "",
        timeType: "negotiate",
        specificTime: "",
        remark: "",
      },
      showTimePopup: false,
      activeDateIndex: 0,
      tempSelectedSlot: "",
      timeSelectionData: [],
    };
  },
  computed: {
    isFormValid() {
      const baseValid = this.form.name && this.form.phone && this.form.address;
      if (this.form.timeType === "specific") {
        return baseValid && this.form.specificTime;
      }
      return baseValid;
    },
    currentActiveSlots() {
      if (this.timeSelectionData.length === 0) return [];
      return this.timeSelectionData[this.activeDateIndex].slots || [];
    },
  },
  onLoad(options) {
    if (options && options.name) {
      this.serviceName = decodeURIComponent(options.name);
    }
    uni.setNavigationBarTitle({
      title: this.serviceName,
    });
    // 计算未来 30 天滑动数据
    this.calculateFutureSlots();
  },
  onShow() {
    const selectedMember = uni.getStorageSync("selected_member_data");
    if (selectedMember) {
      this.form.name = selectedMember.name;
      this.form.phone = selectedMember.phone;
      this.form.address = selectedMember.address;
      this.form.detailAddress = selectedMember.detailAddress;
      uni.removeStorageSync("selected_member_data");
    }
  },
  methods: {
    // 动态核心计算：绝对过滤过去的时间段，并推算未来30天
    calculateFutureSlots() {
      const now = new Date();
      const currentHour = now.getHours();
      const dates = [];

      const getDayOfWeekLabel = (date) => {
        const days = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
        return days[date.getDay()];
      };

      const formatLabel = (date, offset) => {
        const m = String(date.getMonth() + 1).padStart(2, "0");
        const d = String(date.getDate()).padStart(2, "0");
        let labelText = "";
        if (offset === 0) labelText = "今天";
        else if (offset === 1) labelText = "明天";
        else if (offset === 2) labelText = "后天";
        else labelText = getDayOfWeekLabel(date); // 超过三天展示周几

        return `${m}月${d}日 (${labelText})`;
      };

      // 循环向后推导整整 30 天
      for (let i = 0; i < 30; i++) {
        const targetDate = new Date();
        targetDate.setDate(now.getDate() + i);

        // 默认可供选择的标准时间段
        let slots = ["上午 (8点-13点)", "下午 (13点-18点)", "晚上 (18点-21点)"];

        // 核心过滤：若为今天，截断过去时段
        if (i === 0) {
          const todaySlots = [];
          if (currentHour < 13) {
            todaySlots.push("上午 (8点-13点)");
          }
          if (currentHour < 18) {
            todaySlots.push("下午 (13点-18点)");
          }
          if (currentHour < 21) {
            todaySlots.push("晚上 (18点-21点)");
          }
          slots = todaySlots;
        }

        // 如果今天已经过了晚上9点，直接跳过今天，从明天开始作为首日展示
        if (i === 0 && slots.length === 0) {
          continue;
        }

        dates.push({
          label: formatLabel(targetDate, i),
          slots: slots,
        });
      }

      this.timeSelectionData = dates;
    },

    chooseLocation() {
      uni.chooseLocation({
        success: (res) => {
          this.form.address = res.address + " (" + res.name + ")";
        },
      });
    },

    openTimeSelector() {
      this.activeDateIndex = 0;
      this.tempSelectedSlot = "";
      this.showTimePopup = true;
    },

    onDateSelect(index) {
      this.activeDateIndex = index;
      this.tempSelectedSlot = "";
    },

    confirmTimeSelection() {
      if (!this.tempSelectedSlot) return;
      const selectedDateLabel =
        this.timeSelectionData[this.activeDateIndex].label;
      this.form.specificTime = `${selectedDateLabel} ${this.tempSelectedSlot}`;
      this.showTimePopup = false;
    },

    goToMemberList() {
      uni.navigateTo({
        url: "/spages/service/member",
      });
    },

    handleSubmit() {
      if (!this.isFormValid) {
        uni.showToast({ title: "请完善服务人及时间等信息", icon: "none" });
        return;
      }
      uni.showLoading({ title: "提交中..." });
      setTimeout(() => {
        uni.hideLoading();
        uni.showToast({ title: "提交成功，志愿者会尽快接单", icon: "none" });
        setTimeout(() => uni.navigateBack(), 1200);
      }, 1000);
    },
  },
};
</script>

<style lang="scss" scoped>
.create-service-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx 32rpx calc(200rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;

  /* 服务团队 */
  .team-brief-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 36rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    margin-bottom: 28rpx;
    display: flex;
    align-items: center;

    .team-label {
      font-size: 30rpx;
      font-weight: bold;
      color: #333333;
    }

    .free-badge {
      font-size: 30rpx;
      font-weight: bold;
      color: #07c160;
      margin-left: 12rpx;
    }
  }

  /* 表单卡片 */
  .form-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    margin-bottom: 28rpx;

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 28rpx;

      .card-title {
        font-size: 30rpx;
        font-weight: bold;
        color: #333333;
      }

      .record-link {
        display: flex;
        align-items: center;
        gap: 8rpx;

        .link-text {
          font-size: 26rpx;
          color: #07c160;
          font-weight: bold;
        }
      }
    }

    .card-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333333;
    }

    .input-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 28rpx 0;

      &:first-child {
        padding-top: 0;
      }
      &:last-child {
        padding-bottom: 0;
      }

      .form-input {
        width: 100%;
        font-size: 28rpx;
        color: #333333;
      }

      .form-text {
        font-size: 28rpx;
        color: #333333;
        flex: 1;
        margin-right: 16rpx;
      }
    }

    /* 期望时间 */
    .radio-group-row {
      display: flex;
      gap: 72rpx;

      .radio-item {
        display: flex;
        align-items: center;
        cursor: pointer;

        .radio-icon {
          width: 36rpx;
          height: 36rpx;
          border: 3rpx solid #cbd5e1;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16rpx;
          box-sizing: border-box;

          .inner-dot {
            width: 16rpx;
            height: 16rpx;
            border-radius: 50%;
            background-color: transparent;
            transition: background-color 0.15s ease;
          }

          &.radio-active {
            border-color: #07c160;
            .inner-dot {
              background-color: #07c160;
            }
          }
        }

        .radio-label {
          font-size: 28rpx;
          color: #333333;
          font-weight: bold;
        }
      }
    }

    .specific-time-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 32rpx;
      padding-top: 32rpx;
      border-top: 2rpx dashed #f1f5f9;

      .label {
        font-size: 28rpx;
        color: #333333;
        white-space: nowrap;
        margin-right: 10rpx;
      }

      .val-box {
        display: flex;
        align-items: center;

        .val-text {
          font-size: 28rpx;
          color: #333333;
          font-weight: bold;
          margin-right: 12rpx;
        }
      }
    }

    /* 详细描述 */
    .remark-row {
      display: flex;
      align-items: flex-start;
      gap: 24rpx;
      background-color: #f5f7fa;
      border-radius: 20rpx;
      padding: 24rpx;

      .mic-btn {
        width: 64rpx;
        height: 64rpx;
        border-radius: 50%;
        background-color: #ffffff;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.02);
      }

      .remark-textarea {
        flex: 1;
        height: 160rpx;
        font-size: 28rpx;
        color: #333333;
        line-height: 1.5;
        padding-top: 8rpx;
      }
    }
  }

  .placeholder-style {
    color: #b2b2b2 !important;
  }

  /* 底部固定操作栏 */
  .footer-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.04);
    padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
    box-sizing: border-box;
    z-index: 100;

    .submit-btn {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      background-color: #a3e9c5;
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
      border-radius: 20rpx;
      transition: background-color 0.2s ease;

      &::after {
        border: none;
      }

      &.submit-btn-active {
        background-color: #07c160;
        box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
      }
    }
  }

  /* 30天纵向滑动选择器 */
  .time-popup-panel {
    background-color: #ffffff;
    padding: 48rpx 0 calc(48rpx + env(safe-area-inset-bottom)) 0;
    display: flex;
    flex-direction: column;

    .popup-header-title {
      font-size: 34rpx;
      font-weight: bold;
      color: #1a202c;
      text-align: center;
      margin-bottom: 40rpx;
      padding: 0 40rpx;
    }

    /* 双列分栏纵向滑动滚动 */
    .picker-split-box {
      display: flex;
      height: 440rpx;
      border-top: 2rpx solid #edf2f7;
      border-bottom: 2rpx solid #edf2f7;
      box-sizing: border-box;

      /* 左列：日期纵向选择滑块（天数增加，触发 scroll 滚动效果） */
      .col-left-dates {
        flex: 1.1; // 略微放宽，防止溢出换行
        background-color: #f8fafc;
        height: 100%;

        .date-option-item {
          height: 96rpx;
          line-height: 96rpx;
          text-align: center;
          font-size: 27rpx;
          color: #64748b;
          font-weight: bold;
          transition: all 0.2s;

          &.time-active {
            background-color: #ffffff;
            color: #1e293b;
            font-weight: 800;
            position: relative;

            &::before {
              content: "";
              position: absolute;
              left: 0;
              top: 30%;
              width: 8rpx;
              height: 40%;
              background-color: #07c160;
              border-radius: 0 8rpx 8rpx 0;
            }
          }
        }
      }

      /* 右列：时间段 */
      .col-right-slots {
        flex: 1.1;
        background-color: #ffffff;
        height: 100%;

        .slot-option-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          height: 96rpx;
          padding: 0 40rpx;
          font-size: 27rpx;
          color: #475569;
          font-weight: bold;
          box-sizing: border-box;

          .slot-text {
            transition: color 0.15s;
          }

          &.slot-active {
            .slot-text {
              color: #07c160;
              font-weight: 800;
            }
          }
        }
      }
    }

    .popup-actions {
      display: flex;
      gap: 32rpx;
      margin-top: 48rpx;
      padding: 0 40rpx;

      .action-btn {
        flex: 1;
        height: 96rpx;
        line-height: 96rpx;
        font-size: 32rpx;
        font-weight: bold;
        border-radius: 48rpx;

        &::after {
          border: none;
        }

        &.btn-cancel {
          background-color: #f5f7fa;
          color: #555555;
        }

        &.btn-confirm {
          background-color: #a3e9c5;
          color: #ffffff;

          &.btn-confirm-active {
            background-color: #07c160;
            box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
          }
        }
      }
    }
  }
}
</style>