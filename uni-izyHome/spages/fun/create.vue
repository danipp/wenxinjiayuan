<template>
  <view class="create-container">
    <!-- 1. 顶部解锁灵感入口 -->
    <view class="inspiration-banner" @click="goToSquare">
      <view class="banner-left">
        <u-icon name="tags-fill" color="#e6a23c" size="20"></u-icon>
        <text class="banner-text">解锁更多活动灵感？去活动广场</text>
      </view>
      <u-icon name="arrow-right" color="#999999" size="14"></u-icon>
    </view>

    <view class="form-wrapper">
      <!-- 2. 标题和内容 -->
      <view class="form-card">
        <u-input
          type="text"
          :border="false"
          v-model="form.title"
          placeholder="请输入活动标题 (最多20字)"
          :maxlength="20"
          class="title-input"
        />
        <u-line color="#edf0f2"></u-line>
        <u-textarea
          :border="false"
          v-model="form.content"
          placeholder="请输入活动内容，可以让大家了解你的活动 (最多500字)"
          :maxlength="500"
          class="content-textarea"
          :height="100"
        ></u-textarea>
      </view>

      <!-- 2.5 活动封面 -->
      <view class="form-card upload-card">
        <view class="section-title">活动封面</view>
        <OssImageUpload
          v-model="coverImageList"
          :maxCount="1"
          :minCount="0"
          uploadText="上传封面图"
          ossPath="activityCover"
        />
      </view>

      <!-- 3. 活动地点 -->
      <view class="form-card">
        <view class="section-title">活动地点</view>
        <view class="picker-selector-item" @click="chooseLocation">
          <text
            class="picker-value"
            :class="{ 'placeholder-style': !form.location }"
          >
            {{ form.location || "请选择" }}
          </text>
          <u-icon name="arrow-right" color="#b2b2b2" size="14"></u-icon>
        </view>
      </view>

      <!-- 4. 活动时间 -->
      <view class="form-card">
        <view class="section-title">活动时间</view>
        <view class="time-select-row">
          <text class="time-label">开始时间</text>
          <view class="time-picker-box" @click="openTimePicker('start')">
            <text
              class="time-text"
              :class="{ 'placeholder-style': !form.startTime }"
            >
              {{ form.startTime || "请选择时间" }}
            </text>
          </view>
        </view>
        <view class="time-select-row">
          <text class="time-label">结束时间</text>
          <view class="time-picker-box" @click="openTimePicker('end')">
            <text
              class="time-text"
              :class="{ 'placeholder-style': !form.endTime }"
            >
              {{ form.endTime || "请选择时间" }}
            </text>
          </view>
        </view>
      </view>

      <!-- 5. 补充字段（社区选择、人数、收集手机） -->
      <view class="form-card">
        <!-- 社区选择 -->
        <view class="field-row" @click="openCommunitySelector">
          <text class="field-label">所属社区</text>
          <view class="field-value">
            <text>{{ form.community || "选择社区" }}</text>
            <u-icon
              name="arrow-right"
              color="#b2b2b2"
              size="14"
              style="margin-left: 12rpx"
            ></u-icon>
          </view>
        </view>
        <u-line color="#f5f7fa"></u-line>

        <!-- 人数限制 -->
        <view class="field-row">
          <text class="field-label">人数限制 (人)</text>
          <input
            type="number"
            v-model="form.maxLimit"
            placeholder="不限人数"
            class="limit-input"
          />
        </view>
        <u-line color="#f5f7fa"></u-line>

        <!-- 是否收集手机号 -->
        <view class="field-row">
          <view class="label-box">
            <text class="field-label">收集手机号</text>
            <text class="label-tips">开启后，居民报名需填写手机号</text>
          </view>
          <u-switch
            v-model="form.collectPhone"
            activeColor="#07c160"
            size="20"
          ></u-switch>
        </view>
        <u-line color="#f5f7fa"></u-line>

        <!-- 活动类型 -->
        <view class="field-row">
          <text class="field-label">活动类型</text>
          <view class="type-tag-row">
            <view
              v-for="t in typeOptions"
              :key="t.val"
              class="type-tag"
              :class="{ 'type-tag-active': form.type === t.val }"
              @click="form.type = t.val"
            >
              {{ t.name }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 6. 底部固定操作按钮 -->
    <view class="footer-buttons">
      <button class="btn-cancel" @click="handleCancel">取消</button>
      <button
        class="btn-submit"
        :class="{ 'btn-submit-active': isFormValid }"
        @click="handleSubmit"
      >
        立即发布
      </button>
    </view>

    <!-- uView 2.x 时间选择器 minDate绑定动态变量pickerMinTime -->
    <u-datetime-picker
      :show="timePickerShow"
      v-model="tempTime"
      mode="datetime"
      :minDate="pickerMinTime"
      :formatter="formatter"
      @confirm="confirmTime"
      @cancel="timePickerShow = false"
    ></u-datetime-picker>

    <!-- 社区选择组件 -->
    <CommunitySelector
      :show.sync="showCommunitySelector"
      title="请选择活动社区"
      mode="select"
      @confirm="handleCommunityChange"
      nocache
    />
  </view>
</template>

<script>
import CommunitySelector from "@/components/community.vue";
import OssImageUpload from "@/components/upload.vue";
import { create5 } from "@/spages/api/activity";

export default {
  components: {
    CommunitySelector,
    OssImageUpload,
  },
  data() {
    return {
      // 当前时间戳
      currentTimestamp: new Date().getTime(),
      // 时间选择器动态最小时间
      pickerMinTime: new Date().getTime(),
      form: {
        title: "",
        content: "",
        location: "",
        startTime: "",
        endTime: "",
        community: "",
        maxLimit: "",
        collectPhone: false,
        type: 2,
      },
      timePickerShow: false,
      tempTime: Number(new Date()),
      activeTimeType: "start", // 'start' 或 'end'
      showCommunitySelector: false,
      coverImageList: [],
      typeOptions: [
        { name: "线上活动", val: 1 },
        { name: "线下活动", val: 2 },
        { name: "招募活动", val: 3 },
      ],
    };
  },
  computed: {
    // 表单基础非空判定
    isFormValid() {
      return (
        this.form.title &&
        this.form.content &&
        this.form.location &&
        this.form.startTime &&
        this.form.endTime
      );
    },
  },
  onLoad(options) {
    // 核心逻辑：解析从活动广场详情“制作同款活动”带过来的字段
    if (options) {
      if (options.title) this.form.title = decodeURIComponent(options.title);
      if (options.content)
        this.form.content = decodeURIComponent(options.content);
      if (options.maxLimit) this.form.maxLimit = options.maxLimit;
    }

    // 社区字段初始化，默认读取本地常驻缓存
    const cachedLocation = uni.getStorageSync("selected_community");
    if (cachedLocation && cachedLocation.name) {
      this.form.community = cachedLocation.name;
    }
  },
  methods: {
    formatter(type, value) {
      switch (type) {
        case "year":
          return `${value} 年`;
        case "month":
          return `${value} 月`;
        case "day":
          return `${value} 日`;
        case "hour":
          return `${value} 时`;
        case "minute":
          return `${value} 分`;
        default:
          return value;
      }
    },
    goToSquare() {
      uni.navigateTo({ url: "/spages/fun/square" });
    },
    // 地图选点
    chooseLocation() {
      uni.chooseLocation({
        success: (res) => {
          this.form.location = res.address + " (" + res.name + ")";
        },
      });
    },
    // 打开时间选择器，动态设置最小可选时间
    openTimePicker(type) {
      this.activeTimeType = type;
      const now = new Date().getTime();
      if (type === "start") {
        // 开始时间：最小为当前时间，只能选未来
        this.pickerMinTime = now;
      } else {
        // 结束时间：有开始时间则最小=开始时间，否则最小=当前
        if (this.form.startTime) {
          this.pickerMinTime = new Date(this.form.startTime).getTime();
        } else {
          this.pickerMinTime = now;
        }
      }
      // 默认选中最小时间
      this.tempTime = this.pickerMinTime;
      this.timePickerShow = true;
    },
    confirmTime(e) {
      const selectTs = e.value;
      const date = new Date(selectTs);
      const formatted = `${date.getFullYear()}-${String(
        date.getMonth() + 1
      ).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(
        date.getHours()
      ).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;

      if (this.activeTimeType === "start") {
        this.form.startTime = formatted;
        // 如果原有结束时间早于新开始时间，清空结束
        if (
          this.form.endTime &&
          new Date(this.form.endTime).getTime() < selectTs
        ) {
          this.form.endTime = "";
        }
      } else {
        this.form.endTime = formatted;
      }
      this.timePickerShow = false;
    },
    openCommunitySelector() {
      this.showCommunitySelector = true;
    },
    handleCommunityChange(data) {
      if (data && data.community) {
        this.form.community = data.community.name;
      }
    },
    handleCancel() {
      uni.navigateBack();
    },
    handleSubmit() {
      if (!this.isFormValid) {
        uni.showToast({ title: "请填写完所有必填信息", icon: "none" });
        return;
      }
      uni.showLoading({ title: "发布中..." });
      create5({
        title: this.form.title,
        content: this.form.content,
        location: this.form.location,
        startTime: this.form.startTime + ":00",
        endTime: this.form.endTime + ":00",
        community: this.form.community,
        maxLimit: this.form.maxLimit ? Number(this.form.maxLimit) : 0,
        collectPhone: this.form.collectPhone,
        type: this.form.type,
        coverImage:
          this.coverImageList.length > 0 ? this.coverImageList[0].url : "",
      })
        .then(() => {
          uni.hideLoading();
          uni.showToast({ title: "发布成功！", icon: "success" });
          setTimeout(() => uni.navigateBack(), 1200);
        })
        .catch(() => {
          uni.hideLoading();
          uni.showToast({ title: "发布失败，请重试", icon: "none" });
        });
    },
  },
};
</script>

<style lang="scss" scoped>
.create-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 24rpx 32rpx calc(200rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;

  /* 灵感引导 */
  .inspiration-banner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #ffffff;
    padding: 24rpx 32rpx;
    border-radius: 24rpx;
    margin-bottom: 32rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.01);

    .banner-left {
      display: flex;
      align-items: center;

      .banner-text {
        font-size: 28rpx;
        color: #333333;
        font-weight: 600;
        margin-left: 16rpx;
      }
    }
  }

  .form-wrapper {
    display: flex;
    flex-direction: column;
    gap: 32rpx;
  }

  /* 通用卡片容器 */
  .form-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.01);

    .title-input {
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
      padding-bottom: 24rpx;
    }

    .content-textarea {
      width: 100%;
      height: 240rpx;
      font-size: 28rpx;
      color: #555555;
      padding-top: 24rpx;
    }

    .section-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 24rpx;
    }

    .upload-card {
      ::v-deep .image-upload-wrapper {
        padding: 0;
      }
    }

    /* 选项点选框 */
    .picker-selector-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background-color: #f5f7fa;
      padding: 24rpx 32rpx;
      border-radius: 16rpx;

      .picker-value {
        font-size: 28rpx;
        color: #333;
        flex: 1;
        margin-right: 16rpx;
      }
    }

    /* 时间选择行 */
    .time-select-row {
      display: flex;
      align-items: center;
      margin-bottom: 24rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .time-label {
        font-size: 28rpx;
        color: #333;
        width: 160rpx;
      }

      .time-picker-box {
        flex: 1;
        background-color: #f5f7fa;
        padding: 20rpx 32rpx;
        border-radius: 16rpx;
        text-align: center;

        .time-text {
          font-size: 28rpx;
          color: #333;
        }
      }
    }

    /* 补充字段行 */
    .field-row {
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

      .field-label {
        font-size: 28rpx;
        font-weight: bold;
        color: #333333;
      }

      .label-box {
        display: flex;
        flex-direction: column;

        .label-tips {
          font-size: 22rpx;
          color: #999;
          margin-top: 8rpx;
        }
      }

      .field-value {
        display: flex;
        align-items: center;
        font-size: 28rpx;
        color: #555555;
      }

      .limit-input {
        text-align: right;
        font-size: 28rpx;
        color: #333;
        width: 240rpx;
      }

      .type-tag-row {
        display: flex;
        gap: 20rpx;

        .type-tag {
          padding: 12rpx 28rpx;
          border-radius: 12rpx;
          font-size: 26rpx;
          color: #555;
          background-color: #f5f7fa;

          &.type-tag-active {
            background-color: #e8f9f0;
            color: #07c160;
            font-weight: bold;
          }
        }
      }
    }
  }

  .placeholder-style {
    color: #b2b2b2 !important;
  }

  /* 底部固定操作栏 */
  .footer-buttons {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.04);
    padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
    box-sizing: border-box;
    display: flex;
    gap: 32rpx;
    z-index: 100;

    .btn-cancel {
      width: 200rpx;
      height: 96rpx;
      line-height: 96rpx;
      background-color: #f5f7fa;
      color: #333333;
      font-size: 32rpx;
      font-weight: bold;
      border-radius: 20rpx;

      &::after {
        border: none;
      }
    }

    .btn-submit {
      flex: 1;
      height: 96rpx;
      line-height: 96rpx;
      background-color: #a3e9c5; // 未达到发布标准时的浅灰绿色
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
      border-radius: 20rpx;
      transition: background-color 0.2s ease;

      &::after {
        border: none;
      }

      &.btn-submit-active {
        background-color: #07c160; // 微信志愿绿
      }
    }
  }
}
</style>