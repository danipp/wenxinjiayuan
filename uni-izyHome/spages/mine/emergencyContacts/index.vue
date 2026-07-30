<template>
  <view class="contacts-page">
    <view class="page-header">
      <text class="page-title">紧急联系人</text>
      <text class="page-desc">遇到紧急情况时，可快速联系家人或邻里联系人</text>
    </view>

    <view v-if="contactsList.length > 0" class="contacts-list">
      <view
        v-for="item in contactsList"
        :key="item.id"
        class="contact-card"
        @click="goEditContact(item)"
      >
        <view class="avatar-circle">
          <text>{{ item.name.slice(0, 1) }}</text>
        </view>
        <view class="contact-info">
          <view class="name-row">
            <text class="name">{{ item.name }}</text>
            <text class="relation-tag">{{ item.relation }}</text>
          </view>
          <text class="phone">{{ item.phone }}</text>
        </view>
        <view class="action-group">
          <view
            class="action-btn call-btn"
            @click.stop="callContact(item.phone)"
          >
            <u-icon name="phone-fill" color="#07c160" size="36rpx"></u-icon>
          </view>
          <view class="action-btn delete-btn" @click.stop="deleteContact(item)">
            <u-icon name="trash" color="#94a3b8" size="36rpx"></u-icon>
          </view>
        </view>
      </view>
    </view>

    <view v-else class="empty-state">
      <view class="empty-icon">☎</view>
      <text class="empty-title">暂无紧急联系人</text>
      <text class="empty-desc">添加联系人后，紧急情况可以更快获得帮助</text>
    </view>

    <view class="footer-bar">
      <button class="add-btn" @click="goAddContact">添加紧急联系人</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      contactsList: [
        {
          id: 1,
          name: "陈阿姨",
          phone: "13800138000",
          relation: "母亲",
        },
        {
          id: 2,
          name: "李先生",
          phone: "13900139000",
          relation: "邻居",
        },
      ],
    };
  },
  onLoad() {
    uni.setNavigationBarTitle({
      title: "紧急联系人",
    });
  },
  methods: {
    goAddContact() {
      uni.navigateTo({
        url: "/spages/mine/emergencyContacts/add",
      });
    },
    goEditContact(item) {
      uni.navigateTo({
        url: `/spages/mine/emergencyContacts/add?id=${item.id}&name=${item.name}&phone=${item.phone}&relation=${item.relation}`,
      });
    },
    deleteContact(item) {
      uni.showModal({
        title: "删除联系人",
        content: `确定删除${item.name}吗？`,
        confirmText: "删除",
        success: (res) => {
          if (res.confirm) {
            this.contactsList = this.contactsList.filter(
              (contact) => contact.id !== item.id
            );
            uni.showToast({ title: "删除成功", icon: "none" });
          }
        },
      });
    },
    callContact(phone) {
      uni.makePhoneCall({
        phoneNumber: phone,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.contacts-page {
  min-height: 100vh;
  background-color: #f7f9fb;
  padding: 32rpx 32rpx calc(176rpx + env(safe-area-inset-bottom)) 32rpx;
  box-sizing: border-box;

  .page-header {
    background: linear-gradient(135deg, #f0faf5 0%, #ffffff 100%);
    border-radius: 32rpx;
    padding: 36rpx 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    margin-bottom: 28rpx;
    display: flex;
    flex-direction: column;

    .page-title {
      font-size: 40rpx;
      font-weight: 800;
      color: #1a202c;
    }

    .page-desc {
      font-size: 24rpx;
      color: #718096;
      line-height: 1.5;
      margin-top: 16rpx;
    }
  }

  .contacts-list {
    display: flex;
    flex-direction: column;
    gap: 24rpx;

    .contact-card {
      background-color: #ffffff;
      border-radius: 28rpx;
      padding: 32rpx;
      display: flex;
      align-items: center;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      .avatar-circle {
        width: 92rpx;
        height: 92rpx;
        border-radius: 50%;
        background-color: #e6f7ee;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #07c160;
        font-size: 36rpx;
        font-weight: 800;
        margin-right: 24rpx;
        flex-shrink: 0;
      }

      .contact-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        min-width: 0;

        .name-row {
          display: flex;
          align-items: center;
          margin-bottom: 12rpx;

          .name {
            font-size: 32rpx;
            font-weight: bold;
            color: #1a202c;
            max-width: 300rpx;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .relation-tag {
            font-size: 22rpx;
            color: #07c160;
            background-color: #f0faf5;
            border-radius: 20rpx;
            padding: 4rpx 16rpx;
            margin-left: 16rpx;
          }
        }

        .phone {
          font-size: 26rpx;
          color: #718096;
        }
      }

      .action-group {
        display: flex;
        align-items: center;
        gap: 16rpx;
        flex-shrink: 0;

        .action-btn {
          width: 76rpx;
          height: 76rpx;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .call-btn {
          background-color: #f0faf5;
        }

        .delete-btn {
          background-color: #f8fafc;
        }
      }
    }
  }

  .empty-state {
    height: 720rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .empty-icon {
      width: 128rpx;
      height: 128rpx;
      border-radius: 40rpx;
      background-color: #f0faf5;
      color: #07c160;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 60rpx;
      margin-bottom: 32rpx;
    }

    .empty-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #4a5568;
    }

    .empty-desc {
      font-size: 24rpx;
      color: #a0aec0;
      margin-top: 12rpx;
    }
  }

  .footer-bar {
    position: fixed;
    left: 0;
    bottom: 0;
    width: 100%;
    background-color: #ffffff;
    box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
    padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
    box-sizing: border-box;
    z-index: 100;

    .add-btn {
      width: 100%;
      height: 96rpx;
      line-height: 96rpx;
      border-radius: 48rpx;
      background-color: #07c160;
      color: #ffffff;
      font-size: 32rpx;
      font-weight: bold;
      box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.25);

      &::after {
        border: none;
      }
    }
  }
}
</style>