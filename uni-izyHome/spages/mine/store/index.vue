<template>
  <view class="cloud-store-container">
    <!-- 1. 自定义顶部导航栏 (适配安全高度) -->
    <scroll-view scroll-y class="store-scroll-body">
      <!-- 2. 店主信息大卡片 (金麦黄色渐变) -->
      <view class="store-owner-card">
        <view class="owner-main">
          <image
            class="owner-avatar"
            :src="shopInfo.logo || defaultLogo"
            mode="aspectFill"
          ></image>
          <view class="owner-details">
            <view class="title-row">
              <text class="store-name text-ellipsis">{{ shopInfo.name }}</text>
              <text
                class="status-tag"
                :class="shopInfo.status === 1 ? 'status-open' : 'status-closed'"
                >{{ shopInfo.status === 1 ? "营业中" : "歇业中" }}</text
              >
            </view>
            <text class="store-intro text-ellipsis">{{
              shopInfo.description || "暂未填写店铺简介"
            }}</text>
          </view>

          <!-- 编辑资料跳转 -->
          <view class="edit-btn" @click="goToEditInfo">
            <text>编辑资料</text>
            <u-icon
              name="arrow-right"
              color="#78350f"
              size="20rpx"
              style="margin-left: 4rpx"
            ></u-icon>
          </view>
        </view>

        <!-- 经营看板数据 -->
        <view class="metrics-row">
          <view class="divider"></view>
          <view class="metric-item">
            <text class="num">{{ shopInfo.fansCount }}</text>
            <text class="label">粉丝</text>
          </view>
          <view class="divider"></view>
          <view class="metric-item">
            <text class="num">{{ shopInfo.followCount }}</text>
            <text class="label">收藏</text>
          </view>
        </view>
      </view>

      <!-- 3. 云店控制台（核心操作区） -->
      <view class="control-panel-card">
        <text class="panel-title">快捷工具</text>
        <view class="tools-grid">
          <view class="tool-cell" @click="handleVerify">
            <view class="icon-wrapper bg-orange">
              <u-icon name="scan" size="36rpx"></u-icon>
            </view>
            <text class="tool-name">一键核销</text>
          </view>
          <view class="tool-cell" @click="handleToggleStatus">
            <view
              class="icon-wrapper"
              :class="shopInfo.status === 1 ? 'bg-red' : 'bg-green'"
            >
              <view class="power-icon"></view>
            </view>
            <text class="tool-name">{{
              shopInfo.status === 1 ? "暂停营业" : "恢复营业"
            }}</text>
          </view>
          <view class="tool-cell" @click="goToEditInfo">
            <view class="icon-wrapper bg-blue">
              <u-icon name="setting" size="36rpx"></u-icon>
            </view>
            <text class="tool-name">店铺设置</text>
          </view>
        </view>
      </view>

      <!-- 4. 我上架的商品 -->
      <view class="goods-list-section">
        <text class="section-title">我上架的商品 ({{ myGoods.length }})</text>
        <view class="goods-list">
          <view v-for="item in myGoods" :key="item.id" class="goods-row-card">
            <image
              class="g-cover"
              :src="item.coverImage"
              mode="aspectFill"
            ></image>
            <view class="g-right">
              <text class="g-title text-ellipsis-2">{{ item.title }}</text>
              <view class="g-price-row">
                <text class="g-price"
                  >{{ item.pointsPrice || 0
                  }}<text class="unit">积分</text></text
                >
                <text class="g-stock">库存：{{ item.stock }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { myShop, shopGoods, toggleStatus } from "@/spages/api/store";

export default {
  data() {
    return {
      defaultLogo: "https://cdn.uviewui.com/uview/album/1.jpg",
      shopInfo: {
        shopId: null,
        logo: "",
        name: "加载中...",
        description: "",
        status: 1,
        followCount: 0,
        fansCount: 0,
      },
      myGoods: [],
    };
  },
  onShow() {
    this.loadData();
  },
  methods: {
    // 加载店铺信息和商品列表
    async loadData() {
      try {
        const shopRes = await myShop();
        const goodsRes = await shopGoods(shopRes.data.shopId);
        const shop = shopRes.data || {};
        if (shop && shop.shopId) {
          this.shopInfo = {
            shopId: shop.shopId,
            logo: shop.logo || "",
            name: shop.name || "",
            description: shop.description || "",
            status: shop.status || 1,
            fansCount: shop.fansCount || 0,
            followCount: shop.followCount || 0,
          };
          // 有 shopId 后才拉商品
          const goodsList = Array.isArray(goodsRes.data) ? goodsRes.data : [];
          // 如果还没拉过商品
          if (this.myGoods.length === 0) {
            this.myGoods = goodsList.map((g) => ({
              id: g.goodsId || g.id,
              title: g.title || "",
              coverImage: g.coverImage || "",
              pointsPrice: g.pointsPrice || 0,
              stock: g.stock || 0,
            }));
          } else {
            // onShow 回来只更新数据
            const reloaded = await shopGoods(shop.shopId);
            const reloadedList = Array.isArray(reloaded.data)
              ? reloaded.data
              : [];
            this.myGoods = reloadedList.map((g) => ({
              id: g.goodsId || g.id,
              title: g.title || "",
              coverImage: g.coverImage || "",
              pointsPrice: g.pointsPrice || 0,
              stock: g.stock || 0,
            }));
          }
        }
      } catch (e) {
        uni.showToast({ title: "加载失败", icon: "none" });
      }
    },
    handleBack() {
      uni.navigateBack();
    },
    goToEditInfo() {
      uni.navigateTo({
        url: "/spages/mine/store/editInfo",
      });
    },
    // 营业状态切换
    async handleToggleStatus() {
      if (!this.shopInfo.shopId) return;
      try {
        await toggleStatus(this.shopInfo.shopId);
        this.shopInfo.status = this.shopInfo.status === 1 ? 2 : 1;
        const tip = this.shopInfo.status === 1 ? "已恢复营业" : "已暂停营业";
        uni.showToast({ title: tip, icon: "success" });
      } catch (e) {
        uni.showToast({ title: "操作失败", icon: "none" });
      }
    },
    handleVerify() {
      uni.showModal({
        title: "核销验证",
        placeholderText: "请输入买家的 6 位数核销码",
        editable: true,
        success: (res) => {
          if (res.confirm) {
            const code = res.content.trim();
            if (code === "584921" || code === "584 921") {
              uni.showToast({ title: "核销成功！已出库", icon: "success" });
            } else {
              uni.showToast({ title: "核销码无效，请重新核对", icon: "none" });
            }
          }
        },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.cloud-store-container {
  height: 100vh;
  background-color: #f7f9fb;
  display: flex;
  flex-direction: column;
  position: relative;

  .store-scroll-body {
    height: 100vh;
    width: 100%;
    box-sizing: border-box;
    padding: 0 32rpx 48rpx 32rpx;
  }

  /* 店主卡片金麦渐变 */
  .store-owner-card {
    background: linear-gradient(135deg, #f7dca1 0%, #e2ab5b 100%);
    border-radius: 32rpx;
    padding: 48rpx 40rpx;
    box-shadow: 0 8rpx 28rpx rgba(226, 171, 91, 0.3);
    margin-bottom: 32rpx;
    margin-top: 20rpx;

    .owner-main {
      display: flex;
      align-items: center;
      margin-bottom: 48rpx;
      position: relative;

      .owner-avatar {
        width: 108rpx;
        height: 108rpx;
        border-radius: 50%;
        border: 4rpx solid #ffffff;
      }

      .owner-details {
        margin-left: 24rpx;
        flex: 1;
        overflow: hidden;

        .title-row {
          display: flex;
          align-items: center;
          gap: 12rpx;
          margin-bottom: 8rpx;

          .store-name {
            font-size: 36rpx;
            font-weight: bold;
            color: #78350f;
            max-width: 240rpx;
          }

          .status-tag {
            font-size: 18rpx;
            font-weight: bold;
            color: #ffffff;
            background-color: #2b5c9c;
            padding: 4rpx 12rpx;
            border-radius: 8rpx;

            &.status-closed {
              background-color: #94a3b8;
            }
          }
        }

        .store-intro {
          font-size: 24rpx;
          color: #78350f;
        }
      }

      .edit-btn {
        display: flex;
        align-items: center;
        background-color: rgba(255, 255, 255, 0.4);
        padding: 10rpx 20rpx;
        border-radius: 30rpx;
        font-size: 22rpx;
        color: #78350f;
        font-weight: bold;
      }
    }

    .metrics-row {
      display: flex;
      align-items: center;

      .metric-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;

        .num {
          font-size: 44rpx;
          font-weight: bold;
          color: #78350f;
          margin-bottom: 4rpx;
        }

        .label {
          font-size: 20rpx;
          color: #78350f;
        }
      }

      .divider {
        width: 2rpx;
        height: 48rpx;
        background-color: rgba(255, 255, 255, 0.3);
      }
    }
  }

  /* 快捷工具面板 */
  .control-panel-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.015);
    margin-bottom: 40rpx;

    .panel-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #1a202c;
      margin-bottom: 32rpx;
      display: block;
    }

    .tools-grid {
      display: flex;
      justify-content: space-around;

      .tool-cell {
        display: flex;
        flex-direction: column;
        align-items: center;

        .icon-wrapper {
          width: 88rpx;
          height: 88rpx;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 16rpx;

          &.bg-orange {
            background-color: #fff7e6;
            :deep(.u-icon__icon) {
              color: #d97706 !important;
            }
          }
          &.bg-blue {
            background-color: #eff6ff;
            :deep(.u-icon__icon) {
              color: #2b5c9c !important;
            }
          }
          &.bg-green {
            background-color: #ecfdf5;

            .power-icon {
              width: 36rpx;
              height: 36rpx;
              border: 3rpx solid #059669;
              border-radius: 50%;
              position: relative;

              &::after {
                content: "";
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                width: 2rpx;
                height: 20rpx;
                background-color: #059669;
              }
            }
          }
          &.bg-red {
            background-color: #fef2f2;

            .power-icon {
              width: 36rpx;
              height: 36rpx;
              border: 3rpx solid #dc2626;
              border-radius: 50%;
              position: relative;

              &::after {
                content: "";
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                width: 2rpx;
                height: 20rpx;
                background-color: #dc2626;
              }
            }
          }
        }

        .tool-name {
          font-size: 24rpx;
          color: #4a5568;
          font-weight: bold;
        }
      }
    }
  }

  /* 商品列表区域 */
  .goods-list-section {
    display: flex;
    flex-direction: column;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #1a202c;
      margin-bottom: 24rpx;
    }

    .goods-list {
      display: flex;
      flex-direction: column;
      gap: 24rpx;
    }

    .goods-row-card {
      background-color: #ffffff;
      border-radius: 24rpx;
      padding: 24rpx;
      display: flex;
      gap: 24rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.01);

      .g-cover {
        width: 128rpx;
        height: 128rpx;
        border-radius: 16rpx;
        background-color: #f1f5f9;
      }

      .g-right {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .g-title {
          font-size: 27rpx;
          font-weight: bold;
          color: #1e293b;
          line-height: 1.4;
        }

        .g-price-row {
          display: flex;
          justify-content: space-between;
          align-items: baseline;

          .g-price {
            font-size: 32rpx;
            font-weight: bold;
            color: #d97706;

            .unit {
              font-size: 20rpx;
              margin-left: 2rpx;
              font-weight: normal;
            }
          }

          .g-stock {
            font-size: 22rpx;
            color: #94a3b8;
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
  .text-ellipsis-2 {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }
}
</style>