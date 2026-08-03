<template>
  <view class="service-page-container">
    <!-- 1. 顶部社区选择区 -->
    <view class="top-header-bar">
      <view class="community-pill" @click="openCommunitySelector">
        <u-icon name="home-fill" color="#07c160" size="16"></u-icon>
        <text class="pill-text text-ellipsis">{{ currentCommunityName }}</text>
        <u-icon name="arrow-right" color="#999" size="10"></u-icon>
      </view>
    </view>

    <!-- 2. 搜索框区域 -->
    <view class="search-section">
      <view class="search-input-box">
        <u-icon name="search" color="#b2b2b2" size="18"></u-icon>
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="你需要什么帮助？"
          class="search-input"
          @focus="showSearchDropdown = true"
          @blur="handleSearchBlur"
        />
        <u-icon
          v-if="searchKeyword"
          name="close-circle-fill"
          color="#ccc"
          size="16"
          @click="clearSearch"
        ></u-icon>
      </view>

      <!-- 智能检索下拉悬浮框 -->
      <view
        class="search-dropdown-panel"
        v-if="showSearchDropdown && searchKeyword"
      >
        <scroll-view scroll-y class="dropdown-scroll">
          <!-- 匹配到的服务项 -->
          <view
            v-for="(item, idx) in searchResults"
            :key="idx"
            class="dropdown-row"
            @click="selectServiceAndGo(item)"
          >
            <u-icon
              name="search"
              color="#94a3b8"
              size="14"
              style="margin-right: 16rpx"
            ></u-icon>
            <text class="matched-text">{{ item }}</text>
          </view>

          <!-- 兜底：未匹配到或支持自定义创建 -->
          <view
            class="dropdown-row custom-create-row"
            @click="selectServiceAndGo(searchKeyword)"
          >
            <u-icon
              name="plus-circle"
              color="#07c160"
              size="14"
              style="margin-right: 16rpx"
            ></u-icon>
            <text class="custom-text"
              >自定义创建服务： “{{ searchKeyword }}”</text
            >
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- 3. 2x2 主功能分类网格 -->
    <view class="category-grid-box">
      <view
        v-for="(cat, idx) in serviceData"
        :key="idx"
        class="grid-cell"
        @click="openCategoryPopup(cat)"
      >
        <text style="margin-right: 10rpx; font-size: 30rpx">{{
          cat.icon
        }}</text>
        <text class="cell-title">{{ cat.title }}</text>
      </view>
    </view>

    <!-- 4. 服务机构板块 -->
    <!-- <view class="agency-section-bar">
      <text class="section-title">服务机构</text>
      <view class="agency-empty-tips">
        <u-icon name="info-circle" color="#cbd5e1" size="16"></u-icon>
        <text class="tips-text">当前社区暂无签约服务机构</text>
      </view>
    </view> -->

    <!-- 5. 服务细项选择底部抽屉弹窗 (限制最多9项可见，超出的支持滑动) -->
    <u-popup
      :show="showDetailPopup"
      mode="bottom"
      round="16"
      @close="closeDetailPopup"
    >
      <view class="detail-popup-panel">
        <text class="popup-title">请选择{{ activeCategory.title }}</text>

        <!-- 核心控制：限高滚动容器 -->
        <scroll-view scroll-y class="options-limit-scroll">
          <view class="options-grid">
            <view
              v-for="(item, index) in activeCategory.options"
              :key="index"
              class="option-card"
              :class="{ 'option-active': selectedOption === item }"
              @click="selectedOption = item"
            >
              <text class="option-text text-ellipsis">{{ item }}</text>
            </view>
          </view>
        </scroll-view>

        <!-- 双动作按钮 -->
        <view class="popup-actions">
          <button class="action-btn btn-cancel" @click="closeDetailPopup">
            取消
          </button>
          <button
            class="action-btn btn-confirm"
            :class="{ 'btn-confirm-active': selectedOption }"
            @click="confirmServiceSelection"
          >
            确认
          </button>
        </view>
      </view>
    </u-popup>

    <!-- 社区选择弹窗组件 -->
    <CommunitySelector
      :show.sync="showCommunitySelector"
      title="请选择我的社区"
      mode="select"
      @confirm="handleCommunityChange"
    />
    <PhoneAuthPopup :show.sync="showPhoneAuth" />
  </view>
</template>

<script>
import CommunitySelector from "@/components/community.vue";
import PhoneAuthPopup from "@/components/PhoneAuthPopup.vue";

export default {
  components: {
    CommunitySelector,
    PhoneAuthPopup,
  },
  data() {
    return {
      showPhoneAuth: false,
      currentCommunityName: "请选择社区",
      showCommunitySelector: false,
      searchKeyword: "",
      showSearchDropdown: false,
      showDetailPopup: false,
      activeCategory: {}, // 当前点击的分类数据
      selectedOption: "", // 选中的细项服务
      // 完备的服务四分类底层数据库
      serviceData: [
        {
          title: "生活照料",
          icon: "🏠", // 橙房图标占位，可替换为 static 图标
          options: ["上门理发", "修剪指甲", "外出买菜", "上门做菜", "陪同外出"],
        },
        {
          title: "健康护理",
          icon: "🩺", // 绿袋图标占位
          options: [
            "血压测量",
            "陪同就医",
            "陪同买药",
            "陪同体检",
            "提醒服药",
            "陪聊天",
            "陪散步",
            "心理咨询",
          ],
        },
        {
          title: "代办跑腿",
          icon: "🛒", // 黄帽图标占位
          options: [
            "代取快递",
            "代买东西",
            "代缴费",
            "代办服务",
            "代申请社区服务",
          ],
        },
        {
          title: "家政协助",
          icon: "🧹", // 蓝桶图标占位
          options: ["衣服缝补", "刀具打磨", "扫地", "床单清洗", "故障修理"],
        },
      ],
    };
  },
  computed: {
    // 扁平化所有子服务用于智能检索
    allServiceItems() {
      let list = [];
      this.serviceData.forEach((cat) => {
        list = list.concat(cat.options);
      });
      return list;
    },
    // 计算实时模糊检索结果
    searchResults() {
      if (!this.searchKeyword.trim()) return [];
      return this.allServiceItems.filter(
        (item) => item.indexOf(this.searchKeyword.trim()) !== -1
      );
    },
  },
  onShow() {
    // 初始化同步本地存储中的社区
    const cachedLocation = uni.getStorageSync("selected_community");
    if (cachedLocation && cachedLocation.name) {
      this.currentCommunityName = cachedLocation.name;
    }
  },
  methods: {
    openCommunitySelector() {
      this.showCommunitySelector = true;
    },
    handleCommunityChange(data) {
      if (data && data.community) {
        this.currentCommunityName = data.community.name;
        uni.setStorageSync("selected_community", data.community);
      }
    },
    clearSearch() {
      this.searchKeyword = "";
    },
    handleSearchBlur() {
      // 延迟关闭下拉狂防止阻碍点击事件触发
      setTimeout(() => {
        this.showSearchDropdown = false;
      }, 200);
    },
    // 打开大分类弹窗
    openCategoryPopup(cat) {
      this.activeCategory = cat;
      this.selectedOption = ""; // 重置选中
      this.showDetailPopup = true;
    },
    closeDetailPopup() {
      this.showDetailPopup = false;
    },
    // 分类弹窗内“确认”跳转
    confirmServiceSelection() {
      const user_phone_number = uni.getStorageSync("user_phone_number") || null;
      if (!user_phone_number) {
        this.showPhoneAuth = true;
        return;
      }
      if (!this.selectedOption) return;
      this.selectServiceAndGo(this.selectedOption);
      this.closeDetailPopup();
    },
    // 统一收口跳转至创建服务页，通过 query 参数带过去
    selectServiceAndGo(serviceName) {
      uni.navigateTo({
        url: `/spages/service/create?name=${encodeURIComponent(serviceName)}`,
        fail: () => {
          uni.showToast({
            title: `即将跳转创建：${serviceName}`,
            icon: "none",
          });
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.service-page-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  /* 1. 顶部社区选择区 */
  .top-header-bar {
    height: 90rpx;
    display: flex;
    align-items: center;
    margin-bottom: 24rpx;

    .community-pill {
      display: inline-flex;
      align-items: center;
      background-color: #ffffff;
      border: 2rpx solid #edf2f7;
      padding: 12rpx 24rpx;
      border-radius: 40rpx;
      max-width: 60%;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.01);

      .pill-text {
        font-size: 26rpx;
        font-weight: bold;
        color: #2c405a;
        margin: 0 12rpx;
      }
    }
  }

  /* 2. 搜索框 */
  .search-section {
    position: relative;
    margin-bottom: 32rpx;
    z-index: 99;

    .search-input-box {
      display: flex;
      align-items: center;
      background-color: #ffffff;
      border: 2rpx solid #e2e8f0;
      border-radius: 24rpx;
      padding: 20rpx 32rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      .search-input {
        flex: 1;
        font-size: 28rpx;
        color: #333333;
        margin-left: 16rpx;
        margin-right: 16rpx;
      }
    }

    /* 检索悬浮面板 */
    .search-dropdown-panel {
      position: absolute;
      top: 100rpx;
      left: 0;
      width: 100%;
      background-color: #ffffff;
      border-radius: 24rpx;
      box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
      z-index: 100;
      overflow: hidden;

      .dropdown-scroll {
        max-height: 480rpx;
      }

      .dropdown-row {
        display: flex;
        align-items: center;
        padding: 28rpx 32rpx;
        border-bottom: 2rpx solid #f8fafc;
        font-size: 28rpx;
        color: #334155;

        &:active {
          background-color: #f1f5f9;
        }

        &.custom-create-row {
          border-bottom: none;
          .custom-text {
            color: #07c160;
            font-weight: bold;
          }
        }
      }
    }
  }

  /* 3. 2x2 主功能分类网格 */
  .category-grid-box {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: 1fr 1fr;
    background-color: #ffffff;
    border-radius: 24rpx;
    overflow: hidden;
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.015);
    margin-bottom: 48rpx;
    border: 2rpx solid #edf2f7;

    .grid-cell {
      display: flex;
      align-items: center;
      padding: 48rpx 32rpx;
      box-sizing: border-box;
      cursor: pointer;
      position: relative;
      justify-content: center;

      // 纯 CSS 极致还原 2x2 细分割线
      &:nth-child(1) {
        border-right: 2rpx solid #f1f5f9;
        border-bottom: 2rpx solid #f1f5f9;
      }
      &:nth-child(2) {
        border-bottom: 2rpx solid #f1f5f9;
      }
      &:nth-child(3) {
        border-right: 2rpx solid #f1f5f9;
      }

      &:active {
        background-color: #fbfcfd;
      }

      .cell-icon {
        width: 76rpx;
        height: 76rpx;
        border-radius: 50%;
        margin-right: 24rpx;
      }

      .cell-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #1a202c;
      }
    }
  }

  /* 4. 服务机构 */
  .agency-section-bar {
    display: flex;
    flex-direction: column;

    .section-title {
      font-size: 36rpx;
      font-weight: 800;
      color: #1a202c;
      margin-bottom: 28rpx;
    }

    .agency-empty-tips {
      background-color: #ffffff;
      border-radius: 24rpx;
      padding: 32rpx;
      display: flex;
      align-items: center;
      gap: 16rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      .tips-text {
        font-size: 26rpx;
        color: #94a3b8;
      }
    }
  }

  /* 5. 下拉服务细项弹窗面板 */
  .detail-popup-panel {
    background-color: #ffffff;
    padding: 48rpx 40rpx calc(48rpx + env(safe-area-inset-bottom)) 40rpx;
    display: flex;
    flex-direction: column;

    .popup-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #1a202c;
      text-align: center;
      margin-bottom: 40rpx;
    }

    /* 选项限高滚动区（限制最多展示 9 个，溢出自适应滚动） */
    .options-limit-scroll {
      max-height: 420rpx; // 约束在最多 4.5 行的高度上，强制产生滚动条
      margin-bottom: 48rpx;
    }

    .options-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 24rpx;

      .option-card {
        background-color: #f5f7fa;
        border-radius: 16rpx;
        padding: 28rpx 24rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        box-sizing: border-box;
        border: 3rpx solid transparent;
        transition: all 0.2s ease;
        cursor: pointer;

        .option-text {
          font-size: 28rpx;
          color: #334155;
          font-weight: bold;
        }

        /* 选中态高亮 */
        &.option-active {
          background-color: #e8f9f0;
          border-color: #07c160;

          .option-text {
            color: #07c160;
          }
        }
      }
    }

    /* 底部弹窗按钮组 */
    .popup-actions {
      display: flex;
      gap: 32rpx;

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
          background-color: #a3e9c5; // 未选中服务时置浅
          color: #ffffff;
          transition: background-color 0.2s ease;

          &.btn-confirm-active {
            background-color: #07c160; // 选中服务后转为高亮志愿绿
            box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);
          }
        }
      }
    }
  }

  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>