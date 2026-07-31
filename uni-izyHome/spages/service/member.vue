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
          <view class="card-content">
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
            <view class="card-address-row" v-if="member.detailAddress">
              <text
                class="member-address text-ellipsis"
                style="margin-left: 40rpx"
                >{{ member.detailAddress }}</text
              >
            </view>
          </view>

          <view class="card-actions">
            <view class="action-btn" @click.stop="openEdit(member)">
              <u-icon name="edit-pen" color="#94a3b8" size="20"></u-icon>
            </view>
            <view class="action-btn" @click.stop="confirmDelete(member)">
              <u-icon name="trash" color="#ff4d4f" size="20"></u-icon>
            </view>
          </view>
        </view>
      </view>

      <!-- 缺省页 -->
      <view v-else class="empty-member-state">
        <u-icon name="empty-address" color="#cbd5e1" size="64"></u-icon>
        <text class="empty-text">暂无常用服务人信息</text>
      </view>
    </scroll-view>

    <!-- 底部添加按钮 -->
    <view class="footer-bar" @click="openAdd">
      <button class="submit-btn submit-btn-active" @click="handleSubmit">
        添加服务对象
      </button>
    </view>

    <!-- 表单弹窗 -->
    <u-popup
      :show="showForm"
      mode="bottom"
      round="32"
      closeable
      @close="showForm = false"
    >
      <view class="form-container">
        <view class="form-header">
          <text class="form-title">{{
            form.memberId ? "编辑服务对象" : "新增服务对象"
          }}</text>
          <text class="form-desc">请完善以下服务对象的基本信息</text>
        </view>
        <view class="form-content">
          <u-form :model="form" ref="uForm" labelWidth="160rpx">
            <u-form-item label="姓名" prop="name" borderBottom>
              <u-input
                v-model="form.name"
                placeholder="请输入姓名"
                border="none"
                clearable
              ></u-input>
            </u-form-item>
            <u-form-item label="手机号" prop="phone" borderBottom>
              <u-input
                v-model="form.phone"
                placeholder="请输入手机号"
                border="none"
                type="number"
                maxlength="11"
                clearable
              ></u-input>
            </u-form-item>
            <u-form-item label="服务地址" prop="address" borderBottom>
              <view @click="chooseLocation" class="location-picker-box">
                <text
                  :class="
                    form.address
                      ? 'value-text text-ellipsis'
                      : 'placeholder-text'
                  "
                  >{{ form.address || "点击选择地址" }}</text
                >
                <u-icon name="map" color="#07c160" size="18"></u-icon>
              </view>
            </u-form-item>
            <u-form-item label="详细门牌" prop="detailAddress" borderBottom>
              <u-input
                v-model="form.detailAddress"
                placeholder="例: 12号楼1单元201室"
                border="none"
                clearable
              ></u-input>
            </u-form-item>
            <u-form-item label="备注说明" prop="remark" borderBottom>
              <u-input
                v-model="form.remark"
                placeholder="请输入备注说明（选填）"
                border="none"
                clearable
              ></u-input>
            </u-form-item>
          </u-form>
        </view>
        <view class="form-btn-group">
          <u-button
            type="primary"
            shape="circle"
            text="确认保存"
            @click="submitForm"
            color="#07c160"
            customStyle="height: 88rpx; font-size: 32rpx; font-weight: bold; box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.2);"
          ></u-button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { list1, save1, deleteServiceMember } from "../api/serviceMember";

export default {
  data() {
    return {
      memberList: [],
      showForm: false,
      form: {
        memberId: null,
        name: "",
        phone: "",
        address: "",
        detailAddress: "",
        remark: "",
      },
    };
  },
  onShow() {
    this.getList();
  },
  methods: {
    async getList() {
      try {
        const res = await list1();
        if (res.code === "00000") {
          this.memberList = res.data || [];
        }
      } catch (error) {
        console.error("获取服务对象列表失败", error);
      }
    },
    openAdd() {
      this.form = {
        memberId: null,
        name: "",
        phone: "",
        address: "",
        detailAddress: "",
        remark: "",
      };
      this.showForm = true;
    },
    openEdit(member) {
      this.form = { ...member };
      this.showForm = true;
    },
    confirmDelete(member) {
      uni.showModal({
        title: "提示",
        content: `确定删除联系人 ${member.name} 吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              const delRes = await deleteServiceMember(member.memberId);
              if (delRes.code === "00000") {
                uni.showToast({ title: "删除成功", icon: "success" });
                this.getList();
              } else {
                uni.showToast({
                  title: delRes.msg || "删除失败",
                  icon: "none",
                });
              }
            } catch (error) {
              console.error("删除失败", error);
            }
          }
        },
      });
    },
    chooseLocation() {
      uni.chooseLocation({
        success: (res) => {
          if (res.address || res.name) {
            this.form.address = res.address + (res.name ? `(${res.name})` : "");
          }
        },
        fail: (err) => {
          console.log("选择位置失败", err);
        },
      });
    },
    async submitForm() {
      if (!this.form.name || !this.form.phone || !this.form.address) {
        uni.showToast({ title: "请填写完整姓名、手机号和地址", icon: "none" });
        return;
      }
      try {
        const res = await save1(this.form);
        if (res.code === "00000") {
          uni.showToast({ title: "保存成功", icon: "success" });
          this.showForm = false;
          this.getList();
        } else {
          uni.showToast({ title: res.msg || "保存失败", icon: "none" });
        }
      } catch (error) {
        console.error("保存失败", error);
      }
    },
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
    display: flex;
    justify-content: space-between;
    align-items: center;

    &:active {
      background-color: #fbfbfc;
    }

    .card-content {
      flex: 1;
      min-width: 0;
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

    .card-actions {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      align-items: center;
      margin-left: 24rpx;
      gap: 32rpx;

      .action-btn {
        padding: 8rpx;
        background-color: #f7f9fb;
        border-radius: 50%;
      }
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

  .bottom-footer {
    padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom));
    background-color: #ffffff;
    box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
  }

  .form-container {
    padding: 48rpx 40rpx calc(48rpx + env(safe-area-inset-bottom));
    background-color: #ffffff;

    .form-header {
      display: flex;
      flex-direction: column;
      margin-bottom: 40rpx;

      .form-title {
        font-size: 40rpx;
        font-weight: 800;
        color: #1a202c;
      }

      .form-desc {
        font-size: 26rpx;
        color: #94a3b8;
        margin-top: 12rpx;
      }
    }

    .form-content {
      background: #f8fafc;
      border-radius: 24rpx;
      padding: 8rpx 32rpx;
      margin-bottom: 48rpx;

      :deep(.u-form-item__body) {
        padding: 32rpx 0;
      }

      :deep(.u-form-item__body__left__content__label) {
        font-weight: 600;
        color: #4a5568;
      }
    }

    .location-picker-box {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      padding: 8rpx 0;

      .value-text {
        font-size: 30rpx;
        color: #303133;
        flex: 1;
        margin-right: 16rpx;
      }

      .placeholder-text {
        font-size: 30rpx;
        color: #c0c4cc;
        flex: 1;
      }
    }

    .form-btn-group {
      margin-top: 32rpx;
    }
  }
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
</style>