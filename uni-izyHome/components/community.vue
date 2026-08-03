<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="16"
    @close="handleClose"
    :safeAreaInsetBottom="true"
    @touchmove.stop.prevent
  >
    <div class="community-popup-container">
      <!-- 头部标题与关闭按钮 -->
      <div class="popup-header">
        <span class="title">{{ title }}</span>
        <div class="close-btn" @click="handleClose">
          <u-icon name="close" color="#999" size="18"></u-icon>
        </div>
      </div>

      <!-- 模式一：邀请认证说明 -->
      <div v-if="mode === 'invite'" class="invite-desc">
        通过此邀请链接，用户可快速认证成为社区的居民
      </div>

      <!-- 模式二：地址选择栏 -->
      <!-- <div v-if="mode === 'select'" class="address-bar" @click="chooseLocation">
        <span class="label">地址：</span>
        <span class="value text-ellipsis">{{
          locationInfo.address || "点击选择地理位置"
        }}</span>
        <u-icon name="arrow-right" color="#b2b2b2" size="14"></u-icon>
      </div> -->

      <!-- 社区滚动选择器 -->
      <div class="picker-container">
        <picker-view
          v-if="communityList.length > 0"
          :value="pickerValue"
          @change="onPickerChange"
          class="community-picker-view"
          indicator-style="height: 50px;"
        >
          <picker-view-column>
            <div
              v-for="(item, index) in communityList"
              :key="index"
              class="picker-item"
              :class="{ 'active-item': pickerValue[0] === index }"
            >
              {{ item.name }}
            </div>
          </picker-view-column>
        </picker-view>

        <!-- 修复：根据不同模式展示对应的无数据提示 -->
        <div v-else class="empty-tips">
          <template v-if="mode === 'select'">
            {{
              locationInfo.address
                ? "当前地址附近暂无社区信息"
                : "请先选择上方地址以获取附近社区"
            }}
          </template>
          <template v-else> 暂无受邀社区信息 </template>
        </div>
      </div>

      <!-- 底部确定按钮 -->
      <div class="btn-box">
        <button class="confirm-btn" @click="handleConfirm">确定</button>
      </div>
    </div>
  </u-popup>
</template>

<script>
import { list3, switchCommunity } from "@/api/index.js";

export default {
  name: "CommunitySelector",
  props: {
    show: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: "请选择我的社区",
    },
    mode: {
      type: String,
      default: "select", // 'select' 或 'invite'
    },
    initialCommunities: {
      type: Array,
      default: () => [],
    },
    nocache: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      locationInfo: {
        address: "",
        latitude: "",
        longitude: "",
        name: "",
      },
      communityList: [],
      pickerValue: [0],
      loading: false,
    };
  },
  watch: {
    show(val) {
      if (val) {
        this.initData();
      }
    },
    initialCommunities: {
      handler(newVal) {
        if (newVal && newVal.length > 0) {
          this.communityList = newVal;
        }
      },
      immediate: true,
    },
  },
  methods: {
    // 初始化数据逻辑
    async initData() {
      // 1. 邀请模式：加载全部启用社区
      if (this.mode === "invite") {
        if (this.communityList.length === 0) {
          await this.fetchAllCommunities();
        }
      }

      // 2. 选择模式：优先读缓存，无缓存则请求全量列表
      else if (this.mode === "select") {
        const cachedLocation = uni.getStorageSync("user_location_info");
        if (cachedLocation) {
          this.locationInfo = cachedLocation;
        }
        // const cachedCommunities = uni.getStorageSync("cached_community_list");
        // if (cachedCommunities && cachedCommunities.length > 0) {
        //   this.communityList = cachedCommunities;
        // } else {
        //   await this.fetchAllCommunities();
        // }
        await this.fetchAllCommunities();
      }
    },

    // 拉取全部启用社区列表
    async fetchAllCommunities() {
      this.loading = true;
      try {
        const res = await list3();
        if (res.code === "00000" && Array.isArray(res.data)) {
          this.communityList = res.data.filter((item) => item.status === 1);
          this.pickerValue = [0];
          if (!this.nocache) {
            uni.setStorageSync("cached_community_list", this.communityList);
          }
        } else {
          uni.showToast({ title: res.msg || "获取社区列表失败", icon: "none" });
        }
      } catch (e) {
        uni.showToast({ title: "获取社区列表失败", icon: "none" });
      } finally {
        this.loading = false;
      }
    },

    // 接口请求附近社区列表（保留接口，当前使用全量列表）
    async fetchNearbyCommunities() {
      uni.showLoading({ title: "获取附近社区..." });
      try {
        await this.fetchAllCommunities();
      } catch (e) {
        uni.showToast({ title: "获取社区失败", icon: "none" });
      } finally {
        uni.hideLoading();
      }
    },

    onPickerChange(e) {
      this.pickerValue = e.detail.value;
    },

    // 切换社区并更新本地缓存的用户信息
    async handleSwitchCommunity(communityId) {
      try {
        const res = await switchCommunity(communityId);
        if (res.code === "00000") {
          // 更新本地存储的用户信息
          if (res.data) {
            uni.setStorageSync("user_profile_data", {
              nickname: res.data.nickName,
              avatar: res.data.avatar,
              communityId: res.data.communityId,
              communityName: res.data.communityName,
            });
          }
        }
        return res;
      } catch (e) {
        throw e;
      }
    },

    handleConfirm() {
      if (this.communityList.length === 0) {
        uni.showToast({ title: "请先选择有效的社区", icon: "none" });
        return;
      }
      const selectedIndex = this.pickerValue[0] || 0;
      const selectedCommunity = this.communityList[selectedIndex];
      !this.nocache &&
        uni.setStorageSync("selected_community", selectedCommunity);
      this.$emit("confirm", {
        community: selectedCommunity,
        location: this.mode === "select" ? this.locationInfo : null,
      });
      this.handleClose();
    },

    handleClose() {
      this.$emit("update:show", false);
    },
  },
};
</script>

<style lang="scss" scoped>
.community-popup-container {
  background-color: #fff;
  padding: 20px 24px calc(20px + env(safe-area-inset-bottom)) 24px;
  display: flex;
  flex-direction: column;
  position: relative;

  .popup-header {
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    padding-bottom: 16px;

    .title {
      font-size: 18px;
      font-weight: bold;
      color: #333;
    }

    .close-btn {
      position: absolute;
      right: 0;
      top: 50%;
      transform: translateY(-50%);
      padding: 4px;
      cursor: pointer;
    }
  }

  .invite-desc {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
    text-align: left;
    margin-bottom: 20px;
  }

  .address-bar {
    display: flex;
    align-items: center;
    background-color: #f7f7f7;
    border-radius: 8px;
    padding: 12px 14px;
    margin-bottom: 15px;
    cursor: pointer;

    .label {
      font-size: 14px;
      color: #333;
      white-space: nowrap;
    }

    .value {
      flex: 1;
      font-size: 14px;
      color: #555;
      padding-right: 8px;
    }

    .text-ellipsis {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .picker-container {
    height: 160px;
    margin: 10px 0 20px 0;
    position: relative;

    .community-picker-view {
      width: 100%;
      height: 100%;
    }

    .picker-item {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
      color: #b2b2b2;
      transition: color 0.2s ease, font-weight 0.2s ease;

      &.active-item {
        color: #333333;
        font-weight: bold;
        font-size: 18px;
      }
    }

    .empty-tips {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      color: #999;
      text-align: center;
    }
  }

  .btn-box {
    width: 100%;

    .confirm-btn {
      width: 100%;
      height: 48px;
      line-height: 48px;
      background-color: #07c160;
      color: #ffffff;
      font-size: 16px;
      font-weight: bold;
      border-radius: 24px;
      border: none;

      &:active {
        opacity: 0.9;
      }

      &::after {
        border: none;
      }
    }
  }
}
</style>