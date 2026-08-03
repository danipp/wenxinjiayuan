<template>
  <view class="mine-container">
    <!-- 1. 顶部个人资料卡片 -->
    <view class="profile-card">
      <!-- 家园码角标 -->
      <view class="community-code-ribbon" @click="openQrCode">
        <u-icon name="grid" color="#ffffff" size="14"></u-icon>
        <text class="ribbon-text">家园码</text>
      </view>

      <!-- 基本信息（点击头像/编辑可唤起个人资料修改弹窗） -->
      <view class="user-main-info" @click="goToMenu('profile')">
        <u-avatar
          class="avatar"
          :src="userInfo.avatar"
          mode="aspectFill"
        ></u-avatar>
        <view class="info-right">
          <view class="name-row">
            <text class="username text-ellipsis">{{
              userInfo.nickname || "未设置昵称"
            }}</text>
            <text class="edit-label">编辑</text>
            <u-icon name="arrow-right" color="#4a5568" size="10"></u-icon>
          </view>
        </view>

        <!-- 邀请加入按钮 -->
        <!-- <view class="invite-btn" @click.stop="openInviteSelector">
          <text>邀请加入</text>
          <u-icon
            name="arrow-right"
            color="#4a5568"
            size="10"
            style="margin-left: 4rpx"
          ></u-icon>
        </view> -->
      </view>

      <!-- 荣誉指标数据 -->
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">{{ pointDetail.balance }}</text>
          <text class="stat-label">可用积分余额</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-num">{{ pointDetail.totalEarned }}</text>
          <text class="stat-label">累计获得积分</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-num">{{ pointDetail.totalSpent }}</text>
          <text class="stat-label">累计消耗积分</text>
        </view>
      </view>
    </view>

    <!-- 2. 社区新人等级荣誉卡片（去除了查看保险按钮） -->
    <view class="level-card">
      <view class="level-left">
        <view class="level-title-row">
          <text class="level-title">志愿者等级</text>
          <u-icon
            name="question-circle"
            color="#a0aec0"
            size="14"
            style="margin-left: 8rpx"
          ></u-icon>
        </view>
        <text class="level-subtitle"></text>
      </view>
      <text class="level-badge-v1">V1</text>
    </view>
    <view class="menu-list-card" style="margin-bottom: 20rpx">
      <view class="menu-item" @click="goToOrder()">
        <view class="item-left">
          <view class="icon-wrapper icon-green">
            <u-icon name="file-text-fill" color="#fff" size="18"></u-icon>
          </view>
          <text class="menu-label">我的订单</text>
        </view>
        <view class="item-right">
          <text class="right-action">查看</text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
    </view>
    <!-- 商品收藏 / 个人云店 快捷入口 -->
    <view class="shop-entry-row">
      <view class="shop-entry-item" @click="goToMenu('collect')">
        <view class="shop-entry-icon icon-orange">
          <text class="shop-entry-icon-text">★</text>
        </view>
        <view class="shop-entry-info">
          <text class="shop-entry-title">商品/店铺 </text>
          <text class="shop-entry-desc">我的收藏</text>
        </view>
        <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
      </view>
      <view class="shop-entry-divider"></view>
      <view class="shop-entry-item" @click="goToMenu('store')">
        <view class="shop-entry-icon icon-blue">
          <text class="shop-entry-icon-text">☰</text>
        </view>
        <view class="shop-entry-info">
          <text class="shop-entry-title">个人云店</text>
          <text class="shop-entry-desc">我的店铺</text>
        </view>
        <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
      </view>
    </view>

    <view class="menu-list-card" style="margin-bottom: 20rpx">
      <view class="menu-item" @click="goToMenu('followedList')">
        <view class="item-left">
          <view class="icon-wrapper icon-red">
            <u-icon name="account-fill" color="#fff" size="18"></u-icon>
          </view>
          <text class="menu-label">关注列表</text>
        </view>
        <view class="item-right">
          <text class="right-action">查看</text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
    </view>

    <!-- 4. 功能菜单列表 -->
    <view class="menu-list-card">
      <view class="menu-item" @click="goToMenu('myActivities')">
        <view class="item-left">
          <view class="icon-wrapper icon-blue">
            <u-icon name="file-text-fill" color="#ffffff" size="18"></u-icon>
          </view>
          <text class="menu-label">我的活动</text>
        </view>
        <view class="item-right">
          <text class="right-action">查看</text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
      <u-line color="#f1f5f9" style="margin: 0 32rpx"></u-line>

      <view class="menu-item" @click="goToMenu('helpRecords')">
        <view class="item-left">
          <view class="icon-wrapper icon-orange">
            <u-icon name="heart-fill" color="#ffffff" size="18"></u-icon>
          </view>
          <text class="menu-label">帮忙记录</text>
        </view>
        <view class="item-right">
          <text class="right-action">查看</text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
      <u-line color="#f1f5f9" style="margin: 0 32rpx"></u-line>

      <view class="menu-item" @click="goToMenu('demandRecords')">
        <view class="item-left">
          <view class="icon-wrapper icon-green">
            <u-icon name="star-fill" color="#ffffff" size="18"></u-icon>
          </view>
          <text class="menu-label">发布需求记录</text>
        </view>
        <view class="item-right">
          <text class="right-action">查看</text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
      <u-line color="#f1f5f9" style="margin: 0 32rpx"></u-line>

      <view class="menu-item" @click="goToMenu('checkinRecords')">
        <view class="item-left">
          <view class="icon-wrapper icon-purple">
            <u-icon name="file-text-fill" color="#ffffff" size="18"></u-icon>
          </view>
          <text class="menu-label">打卡记录</text>
        </view>
        <view class="item-right">
          <text class="right-action">查看</text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
      <u-line color="#f1f5f9" style="margin: 0 32rpx"></u-line>

      <view class="menu-item" @click="goToMenu('checkinFrame')">
        <view class="item-left">
          <view class="icon-wrapper icon-pink">
            <u-icon name="grid" color="#ffffff" size="18"></u-icon>
          </view>
          <text class="menu-label">购买打卡相框</text>
        </view>
        <view class="item-right">
          <text class="right-action">查看</text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
      <u-line color="#f1f5f9" style="margin: 0 32rpx"></u-line>

      <view class="menu-item" @click="goToMenu('emergencyContacts')">
        <view class="item-left">
          <view class="icon-wrapper icon-red">
            <u-icon name="heart-fill" color="#ffffff" size="18"></u-icon>
          </view>
          <text class="menu-label">紧急联系人</text>
        </view>
        <view class="item-right">
          <text class="right-action">查看</text>
          <u-icon name="arrow-right" color="#cbd5e1" size="12"></u-icon>
        </view>
      </view>
    </view>
    <!-- A. 社区码居中弹窗 -->
    <u-popup
      :show="showQrCode"
      mode="center"
      round="16"
      @close="showQrCode = false"
      @touchmove.stop.prevent
    >
      <view class="qr-popup-card">
        <text class="qr-title">我的家园码</text>
        <!-- 纯 CSS 绘制高精度二维码 -->
        <view class="qr-code-box">
          <image
            src="data:image/png;base64, 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"
            mode="widthFix"
            show-menu-by-longpress
          />
        </view>
        <view class="qr-tips-list">
          <view class="qr-tip-item">
            <text class="qr-tip-text">1、家园码是您在社区中的唯一身份凭证</text>
          </view>
          <view class="qr-tip-item">
            <text class="qr-tip-text">2、参与活动时出示家园码即可快速签到</text>
          </view>
          <view class="qr-tip-item">
            <text class="qr-tip-text">3、可长按保存家园码，方便随时使用</text>
          </view>
        </view>
      </view>
    </u-popup>

    <!-- B. 邀请确认转发弹窗（解决微信直接分享限制的闭环设计） -->
    <u-popup
      :show="showInviteShare"
      mode="bottom"
      round="16"
      @close="showInviteShare = false"
      @touchmove.stop.prevent
    >
      <view class="invite-share-panel">
        <text class="invite-title">已选择邀请社区</text>
        <text class="invite-community-name">📍 {{ inviteCommunityName }}</text>
        <text class="invite-desc"
          >点击下方按钮直接微信转发给好友，邀请他们加入社区！</text
        >
        <button
          class="invite-native-btn"
          open-type="share"
          @click="showInviteShare = false"
        >
          立即发送邀请
        </button>
      </view>
    </u-popup>

    <!-- 关联：我们之前封装好的社区选择弹窗组件 -->
    <CommunitySelector
      :show.sync="showInviteSelector"
      title="邀请加入哪个社区？"
      mode="invite"
      @confirm="handleInviteCommunityConfirm"
    />
  </view>
</template>

<script>
// 引入关联组件
import CommunitySelector from "@/components/community.vue";
import { getPointsDetail } from "@/api/mine.js";
export default {
  components: {
    CommunitySelector,
  },
  data() {
    return {
      showQrCode: false,
      showInviteSelector: false,
      showInviteShare: false,
      inviteCommunityName: "",
      userInfo: {
        avatar: "",
        nickname: "",
      },
      pointDetail: {
        balance: 0,
        totalEarned: 0,
        totalSpent: 0,
      },
    };
  },
  onLoad(options) {},
  onShow() {
    // 页面初始化时读取本地是否有编辑过的用户信息
    const cachedProfile = uni.getStorageSync("user_profile_data");
    if (cachedProfile) {
      this.userInfo.avatar = cachedProfile.avatarUrl;
      this.userInfo.nickname = cachedProfile.nickname;
    }
    this.getPointsDetail();
  },
  methods: {
    getPointsDetail() {
      getPointsDetail().then((res) => {
        this.pointDetail.balance = res.data.balance;
        this.pointDetail.totalEarned = res.data.totalEarned;
        this.pointDetail.totalSpent = res.data.totalSpent;
      });
    },
    // 1. 打开二维码弹窗
    openQrCode() {
      this.showQrCode = true;
    },
    // 个人信息更新成功回调
    handleProfileUpdate(data) {
      this.userInfo.avatar = data.avatarUrl;
      this.userInfo.nickname = data.nickname;
      uni.setStorageSync("user_profile_data", data);
    },
    // 3. 打开社区邀请弹窗
    openInviteSelector() {
      this.showInviteSelector = true;
    },
    // 选定需要邀请的社区后的确认回调
    handleInviteCommunityConfirm(data) {
      if (data && data.community) {
        this.inviteCommunityName = data.community.name;
        // 唤起底部的原生微信分享确认卡片
        this.showInviteShare = true;
      }
    },
    // 4. 下方菜单跳转
    goToMenu(type) {
      uni.navigateTo({
        url: `/spages/mine/${type}/index`,
      });
    },
    goToOrder() {
      uni.navigateTo({
        url: `/spages/store/order/order`,
      });
    },
    // 5. 原生分享生命周期回调
    onShareAppMessage(res) {
      return {
        title: `Hi！邀请你加入我们温暖的“${this.inviteCommunityName}”！`,
        path: `/spages/invite/index?name=${this.inviteCommunityName}`,
        imageUrl: "https://cdn.uviewui.com/uview/album/5.jpg",
      };
    },
  },
};
</script>

<style lang="scss" scoped>
.mine-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx;
  box-sizing: border-box;

  /* 1. 顶部天青蓝个人卡片 */
  .profile-card {
    background: linear-gradient(135deg, #c3daf6 0%, #a4c2ec 100%);
    border-radius: 32rpx;
    padding: 48rpx 40rpx;
    box-shadow: 0 8rpx 28rpx rgba(164, 194, 236, 0.35);
    position: relative;
    margin-bottom: 32rpx;
    overflow: hidden;

    /* 社区码角标 */
    .community-code-ribbon {
      position: absolute;
      top: 0;
      right: 0;
      background-color: #2b5c9c;
      padding: 12rpx 28rpx;
      border-bottom-left-radius: 32rpx;
      display: flex;
      align-items: center;
      gap: 8rpx;
      cursor: pointer;

      .ribbon-text {
        font-size: 24rpx;
        color: #ffffff;
        font-weight: bold;
      }
    }

    /* 用户主信息行 */
    .user-main-info {
      display: flex;
      align-items: center;
      margin-top: 20rpx;
      margin-bottom: 48rpx;
      position: relative;

      .avatar {
        width: 128rpx;
        height: 128rpx;
        border-radius: 50%;
        border: 4rpx solid #ffffff;
        background-color: #f1f3f5;
      }

      .info-right {
        margin-left: 28rpx;
        flex: 1;

        .name-row {
          display: flex;
          align-items: center;
          cursor: pointer;

          .username {
            font-size: 40rpx;
            font-weight: 800;
            color: #1a202c;
            max-width: 220rpx;
          }

          .edit-label {
            font-size: 22rpx;
            color: #4a5568;
            background-color: rgba(255, 255, 255, 0.5);
            padding: 4rpx 12rpx;
            border-radius: 16rpx;
            margin: 0 8rpx 0 16rpx;
          }
        }
      }

      /* 邀请加入 */
      .invite-btn {
        display: flex;
        align-items: center;
        background-color: rgba(255, 255, 255, 0.4);
        padding: 12rpx 24rpx;
        border-radius: 30rpx;
        font-size: 24rpx;
        color: #2d3748;
        font-weight: bold;
        cursor: pointer;
        margin-left: auto;
      }
    }

    /* 收到红花与助人指标 */
    .stats-row {
      display: flex;
      align-items: center;

      .stat-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;

        .stat-num {
          font-family: "Georgia", serif;
          font-size: 48rpx;
          font-weight: bold;
          color: #1a202c;
          margin-bottom: 8rpx;
        }

        .stat-label {
          font-size: 24rpx;
          color: #4a5568;
        }
      }

      .stat-divider {
        width: 2rpx;
        height: 56rpx;
        background-color: rgba(255, 255, 255, 0.4);
      }
    }
  }

  /* 2. 等级荣誉卡（去除了查看保险按钮） */
  .level-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 36rpx 40rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    margin-bottom: 32rpx;

    .level-left {
      display: flex;
      flex-direction: column;
      max-width: 80%;

      .level-title-row {
        display: flex;
        align-items: center;
        margin-bottom: 8rpx;

        .level-title {
          font-size: 32rpx;
          font-weight: 800;
          color: #2b5c9c;
        }
      }

      .level-subtitle {
        font-size: 22rpx;
        color: #718096;
        line-height: 1.4;
      }
    }

    /* 斜体精致 V1 展示 */
    .level-badge-v1 {
      font-family: "Times New Roman", serif;
      font-style: italic;
      font-weight: 900;
      font-size: 84rpx;
      line-height: 1;
      color: #3b82f6;
      opacity: 0.85;
      margin-right: 8rpx;
    }
  }

  /* 商品收藏 / 个人云店 横向快捷入口 */
  .shop-entry-row {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx 40rpx;
    display: flex;
    align-items: center;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    margin-bottom: 32rpx;

    .shop-entry-item {
      flex: 1;
      display: flex;
      align-items: center;
      cursor: pointer;

      &:active {
        opacity: 0.7;
      }

      .shop-entry-icon {
        width: 72rpx;
        height: 72rpx;
        border-radius: 20rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20rpx;
        flex-shrink: 0;

        &.icon-blue {
          background-color: #3b82f6;
        }
        &.icon-orange {
          background-color: #f59e0b;
        }

        .shop-entry-icon-text {
          font-size: 32rpx;
          color: #ffffff;
          font-weight: bold;
        }
      }

      .shop-entry-info {
        flex: 1;
        display: flex;
        flex-direction: column;

        .shop-entry-title {
          font-size: 28rpx;
          font-weight: bold;
          color: #333333;
          margin-bottom: 4rpx;
        }

        .shop-entry-desc {
          font-size: 22rpx;
          color: #94a3b8;
        }
      }
    }

    .shop-entry-divider {
      width: 2rpx;
      height: 72rpx;
      background-color: #f1f5f9;
      margin: 0 24rpx;
    }
  }
  .menu-list-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    overflow: hidden;

    .menu-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 32rpx 40rpx;
      cursor: pointer;

      &:active {
        background-color: #fafbfc;
      }

      .item-left {
        display: flex;
        align-items: center;

        .icon-wrapper {
          width: 64rpx;
          height: 64rpx;
          border-radius: 16rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 24rpx;

          &.icon-blue {
            background-color: #3b82f6;
          }
          &.icon-orange {
            background-color: #f59e0b;
          }
          &.icon-green {
            background-color: #10b981;
          }
          &.icon-purple {
            background-color: #8b5cf6;
          }
          &.icon-pink {
            background-color: #ec4899;
          }
          &.icon-red {
            background-color: #ef4444;
          }
        }

        .menu-label {
          font-size: 30rpx;
          font-weight: bold;
          color: #333333;
        }
      }

      .item-right {
        display: flex;
        align-items: center;

        .right-action {
          font-size: 26rpx;
          color: #94a3b8;
          margin-right: 12rpx;
        }
      }
    }
  }

  /* 居中二维码卡片 */
  .qr-popup-card {
    padding: 48rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 520rpx;

    .qr-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333333;
      margin-bottom: 40rpx;
    }

    /* 纯 CSS 矢量绘制二维码 */
    .qr-code-box {
      width: 300rpx;
      height: 300rpx;
      border: 2rpx solid #edf2f7;
      border-radius: 16rpx;
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;

      .qr-corner {
        position: absolute;
        width: 60rpx;
        height: 60rpx;
        border: 8rpx solid #333333;
        background-color: #ffffff;

        &.top-left {
          top: 24rpx;
          left: 24rpx;
        }
        &.top-right {
          top: 24rpx;
          right: 24rpx;
        }
        &.bottom-left {
          bottom: 24rpx;
          left: 24rpx;
        }
      }

      .qr-pixel-grid {
        width: 80rpx;
        height: 80rpx;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        align-items: center;

        span {
          width: 16rpx;
          height: 16rpx;
          background-color: #333333;
          border-radius: 2rpx;
        }
      }
    }

    .qr-tips-list {
      width: 100%;
      margin-top: 32rpx;
      display: flex;
      flex-direction: column;
      gap: 20rpx;

      .qr-tip-item {
        display: flex;
        align-items: flex-start;

        .qr-tip-num {
          width: 40rpx;
          height: 40rpx;
          line-height: 40rpx;
          text-align: center;
          background-color: #3b82f6;
          color: #ffffff;
          font-size: 22rpx;
          font-weight: bold;
          border-radius: 50%;
          margin-right: 16rpx;
          flex-shrink: 0;
        }

        .qr-tip-text {
          flex: 1;
          font-size: 24rpx;
          color: #64748b;
          line-height: 1.6;
        }
      }
    }
  }

  /* 邀请微信分享确认弹窗 */
  .invite-share-panel {
    background-color: #ffffff;
    padding: 48rpx 40rpx calc(48rpx + env(safe-area-inset-bottom)) 40rpx;
    display: flex;
    flex-direction: column;
    align-items: center;

    .invite-title {
      font-size: 28rpx;
      color: #64748b;
      margin-bottom: 16rpx;
    }

    .invite-community-name {
      font-size: 40rpx;
      font-weight: 800;
      color: #1e293b;
      margin-bottom: 24rpx;
    }

    .invite-desc {
      font-size: 26rpx;
      color: #94a3b8;
      text-align: center;
      line-height: 1.5;
      margin-bottom: 48rpx;
    }

    .invite-native-btn {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      background-color: #07c160;
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
      border-radius: 48rpx;
      box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);

      &::after {
        border: none;
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