<template>
  <view class="member-page-container">
    <view class="member-header-tips">
      <u-icon name="info-circle" color="#4a5568" size="14"></u-icon>
      <text class="tips-text">请选择您需要服务、看护的联系人记录：</text>
    </view>

    <!-- 联系人历史记录列表 -->
    <scroll-view scroll-y class="member-scroll-view">
      <view class="member-list" v-if="memberList.length > 0">
        <view
          v-for="(member, index) in memberList"
          :key="index"
          class="member-card"
          @click="selectMember(member)"
        >
          <view class="card-top-row">
            <text class="member-name">{{ member.name }}</text>
            <text class="member-phone">{{ member.phone }}</text>
          </view>
          <view class="card-address-row">
            <u-icon
              name="map"
              color="#94a3b8"
              size="14"
              style="margin-right: 12rpx"
            ></u-icon>
            <text class="member-address text-ellipsis">{{
              member.address
            }}</text>
          </view>
          <view class="card-address-row">
            <text class="member-address text-ellipsis">{{
              member.detailAddress
            }}</text>
          </view>

          <!-- 选中右下角小绿勾标记 -->
          <!-- <view class="select-indicator">
            <u-icon name="checkbox-mark" color="#07c160" size="16"></u-icon>
          </view> -->
        </view>
      </view>

      <!-- 缺省页 -->
      <view v-else class="empty-member-state">
        <u-icon name="empty-address" color="#cbd5e1" size="64"></u-icon>
        <text class="empty-text">暂无常用服务人信息</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      memberList: [
        {
          name: "罗大完成",
          phone: "13812345678",
          address: "广东省广州市越秀区青菜岗43号启东楼",
          detailAddress: "12号楼1单元",
        },
        {
          name: "秉治",
          phone: "13987654321",
          address: "广州市海珠区海心沙亚运公园 102 房",
          detailAddress: "12号楼1单元",
        },
        {
          name: "石头",
          phone: "15200008585",
          address: "广州市越秀区富力新天地中心 19 楼",
        },
      ],
    };
  },
  methods: {
    // 核心回传机制
    selectMember(member) {
      // 将选中的历史联系人数据写入本地临时缓存
      uni.setStorageSync("selected_member_data", member);
      // 直接回退，利用父组件的 onShow 去读取，极高复用度且体验极佳
      uni.navigateBack();
    },
  },
};
</script>

<style lang="scss" scoped>
.member-page-container {
  min-height: 100vh;
  background-color: #f7f9fb;
  display: flex;
  flex-direction: column;

  .member-header-tips {
    display: flex;
    align-items: center;
    padding: 24rpx 32rpx;
    background-color: #edf2f7;

    .tips-text {
      font-size: 24rpx;
      color: #4a5568;
      margin-left: 12rpx;
    }
  }

  .member-scroll-view {
    flex: 1;
    overflow: hidden;
  }

  .member-list {
    padding: 32rpx;
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }

  /* 联系人名片 */
  .member-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);
    position: relative;
    border: 2rpx solid #edf2f7;

    &:active {
      background-color: #fbfbfc;
    }

    .card-top-row {
      display: flex;
      align-items: baseline;
      margin-bottom: 16rpx;

      .member-name {
        font-size: 32rpx;
        font-weight: bold;
        color: #1a202c;
        margin-right: 24rpx;
      }

      .member-phone {
        font-size: 28rpx;
        font-weight: bold;
        color: #4a5568;
      }
    }

    .card-address-row {
      display: flex;
      align-items: center;

      .member-address {
        font-size: 26rpx;
        color: #718096;
        flex: 1;
      }
    }

    /* 右下角选中装饰微标 */
    .select-indicator {
      position: absolute;
      right: 24rpx;
      top: 24rpx;
    }
  }

  .empty-member-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 300rpx;

    .empty-text {
      font-size: 28rpx;
      color: #94a3b8;
      margin-top: 24rpx;
    }
  }

  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>