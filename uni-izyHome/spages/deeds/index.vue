<template>
  <view class="deeds-container">
    <!-- 1. 顶部挂载公共下拉筛选组件 -->
    <view class="filter-sticky-box">
      <DropdownFilter v-model="filterParams" @change="onFilterChange" />
    </view>

    <!-- 2. 互助信息卡片流 -->
    <scroll-view scroll-y class="list-scroll-view" @scrolltolower="loadMore">
      <view class="card-list" v-if="filteredList.length > 0">
        <view
          v-for="item in filteredList"
          :key="item.id"
          class="deeds-card"
          @click="goDetail(item)"
          :class="{ 'card-inactive': isTerminalState(item.status) }"
        >
          <!-- 【视觉重构点 1】: 右上角状态显示 -->
          <!-- A. 归档状态：渲染复古双内圈印章 -->
          <div
            v-if="isTerminalState(item.status)"
            class="status-stamp"
            :class="item.status"
          >
            <span class="stamp-text">{{ item.statusText }}</span>
          </div>

          <!-- B. 活跃状态：渲染彩色微标 (Badge) -->
          <view v-else class="status-badge" :class="item.status">
            <text class="badge-dot"></text>
            <text class="badge-text">{{ item.statusText }}</text>
          </view>

          <!-- 卡片头部：标题与发布者 -->
          <view class="card-header">
            <text class="deeds-title text-ellipsis">{{ item.title }}</text>
            <view class="author-row">
              <image
                class="author-avatar"
                :src="item.avatar"
                mode="aspectFill"
              ></image>
              <text class="publish-time">
                {{ item.publishDate }}
                <text v-if="item.distance" class="distance-text">
                  | {{ item.distance }}</text
                >
              </text>
            </view>
          </view>

          <!-- 卡片身体：详情属性组 -->
          <view class="card-body">
            <view
              v-for="(field, fIdx) in item.fields"
              :key="fIdx"
              class="info-item"
            >
              <text class="item-label">{{ field.label }}：</text>
              <text class="item-value">{{ field.value }}</text>
            </view>
          </view>

          <!-- 【视觉重构点 2】: 底部动态操作按钮（仅活跃状态显示，增加卡片深度） -->
          <view
            v-if="!isTerminalState(item.status)"
            class="card-footer-actions"
          >
            <!-- 待帮忙状态：点击接单 -->
            <button
              v-if="item.status === 'pending'"
              class="action-btn btn-apply"
              @click.stop="handleApplyHelp(item)"
            >
              <u-icon
                name="heart-fill"
                color="#ffffff"
                size="28rpx"
                style="margin-right: 8rpx"
              ></u-icon>
              我来帮忙
            </button>

            <!-- 帮助中状态：联系沟通 -->
            <button
              v-if="item.status === 'helping' && item.isMine"
              class="action-btn btn-contact"
              @click.stop="handleContact(item)"
            >
              <u-icon
                name="chat-fill"
                color="#555555"
                size="28rpx"
                style="margin-right: 8rpx"
              ></u-icon>
              联系Ta
            </button>
            <button
              v-if="item.status === 'helping' && !item.isMine"
              class="action-btn btn-contact"
            >
              已被接单
            </button>
          </view>
        </view>
      </view>

      <!-- 筛选为空缺省页 -->
      <view v-else class="empty-state">
        <u-icon name="empty-list" color="#c0c4cc" size="128rpx"></u-icon>
        <text class="empty-text">当前筛选条件下暂无互助信息</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import DropdownFilter from "@/components/DropdownFilter.vue";
import { publicPage, accept } from "@/spages/api/demand";

export default {
  components: {
    DropdownFilter,
  },
  data() {
    return {
      filterParams: {
        status: "all",
        requirement: "all",
        sort: "default",
      },
      deedsList: [],
      page: 1,
      pageSize: 10,
      loading: false,
      finished: false,
    };
  },
  computed: {
    // 动态根据筛选头过滤
    filteredList() {
      return this.deedsList;
    },
  },
  onShow() {
    this.refresh();
  },
  methods: {
    goDetail(item) {
      uni.navigateTo({
        url: `/spages/deeds/detail?id=${item.id}`,
      });
    },
    // 判定是否为终结状态
    isTerminalState(status) {
      return (
        status === "completed" ||
        status === "expired" ||
        status === "toEvaluate"
      );
    },
    onFilterChange(e) {
      this.filterParams = e;
      this.refresh();
    },
    loadMore() {
      this.getList();
    },
    refresh() {
      this.page = 1;
      this.finished = false;
      this.deedsList = [];
      this.getList();
    },
    getStatusString(statusInt) {
      switch (statusInt) {
        case 1:
          return "pending";
        case 2:
          return "helping";
        case 3:
          return "toEvaluate";
        case 4:
          return "completed";
        case 5:
          return "expired";
        default:
          return "pending";
      }
    },
    getStatusText(statusInt) {
      switch (statusInt) {
        case 1:
          return "待帮忙";
        case 2:
          return "已接单";
        case 3:
          return "待评价";
        case 4:
          return "已完成";
        case 5:
          return "已过期";
        default:
          return "未知";
      }
    },
    async getList() {
      if (this.loading || this.finished) return;
      this.loading = true;

      let apiStatus = this.filterParams.status;
      if (apiStatus === "matched") apiStatus = "helping";

      let apiSort = "desc";
      if (this.filterParams.sort === "asc") apiSort = "asc";

      try {
        const res = await publicPage({
          pageNumber: this.page,
          pageSize: this.pageSize,
          role: 2, // 帮忙者视角
          status: apiStatus,
          requirement:
            this.filterParams.requirement === "all"
              ? ""
              : this.filterParams.requirement,
          sort: apiSort,
        });

        if (res.code === "00000") {
          const list = (res.data?.content || []).map((item) => {
            const statusStr = this.getStatusString(item.status);
            return {
              id: item.demandId || item.id,
              title: item.title,
              status: statusStr,
              statusText: this.getStatusText(item.status),
              avatar: "https://cdn.uviewui.com/uview/album/4.jpg", // 占位头像
              publishDate: item.createTime || "刚刚发布",
              distance: "",
              phone: item.memberPhone,
              isMine: true, // 简化处理，暂时允许联系
              fields: [
                { label: "时间", value: item.serviceTime || "双方协商" },
                {
                  label: "地点",
                  value: item.location || item.memberAddress || "",
                },
                { label: "说明", value: item.content || item.remark || "" },
              ],
            };
          });

          if (this.page === 1) {
            this.deedsList = list;
          } else {
            this.deedsList = this.deedsList.concat(list);
          }

          this.finished = res.data?.last ?? list.length < this.pageSize;
          this.page++;
        }
      } catch (e) {
        console.error("获取需求列表失败", e);
      } finally {
        this.loading = false;
      }
    },
    // “我来帮忙”按钮响应
    handleApplyHelp(item) {
      uni.showModal({
        title: "确认帮助",
        content: `您确认承接“${item.title}”的互助任务吗？确认后系统将通知求助者。`,
        success: async (res) => {
          if (res.confirm) {
            try {
              uni.showLoading({ title: "接单中..." });
              const result = await accept(item.id);
              uni.hideLoading();
              if (result.code === "00000") {
                uni.showToast({
                  title: "接单成功，请尽快联系求助者",
                  icon: "none",
                });
                this.refresh();
              } else {
                uni.showToast({
                  title: result.msg || "接单失败",
                  icon: "none",
                });
              }
            } catch (error) {
              uni.hideLoading();
              uni.showToast({ title: "接单失败，请重试", icon: "none" });
            }
          }
        },
      });
    },
    // “联系求助者”按钮响应
    handleContact(item) {
      //   拿到手机号码：item.phone 进行电话沟通
      uni.makePhoneCall({
        phoneNumber: item.phone || "15202094685",
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.deeds-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f6f8fa;

  .filter-sticky-box {
    position: relative;
    z-index: 100;
  }

  .list-scroll-view {
    flex: 1;
    overflow: hidden;
  }

  .card-list {
    padding: 28rpx 28rpx calc(40rpx + env(safe-area-inset-bottom)) 28rpx;
    display: flex;
    flex-direction: column;
    gap: 24rpx;
  }

  /* 互助卡片基础布局 */
  .deeds-card {
    background-color: #ffffff;
    border-radius: 24rpx;
    padding: 40rpx 36rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.015);
    position: relative;
    overflow: hidden;
    transition: opacity 0.25s ease;

    /* 历史/过期等归档卡片略微变淡，形成视觉对比 */
    &.card-inactive {
      opacity: 0.85;
    }

    /* 1. 归档状态：右上角双圆环印章（复古风格） */
    .status-stamp {
      position: absolute;
      top: 28rpx;
      right: 28rpx;
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
        letter-spacing: 1rpx;
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

    /* 2. 活跃状态：右上角圆角微标 (Badge) */
    .status-badge {
      position: absolute;
      top: 36rpx;
      right: 36rpx;
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

      /* 待帮忙：温暖的橙色调 */
      &.pending {
        background-color: #fff7e6;

        .badge-dot {
          background-color: #d97706;
        }

        .badge-text {
          color: #d97706;
        }
      }

      /* 帮助中：沉稳的蔚蓝色调 */
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

    /* 卡片头部 */
    .card-header {
      padding-bottom: 24rpx;
      border-bottom: 1rpx solid #f2f2f2;

      .deeds-title {
        font-size: 34rpx;
        font-weight: bold;
        color: #1a202c;
        width: 68%; // 避让右上角徽章/印章
        margin-bottom: 16rpx;
      }

      .author-row {
        display: flex;
        align-items: center;

        .author-avatar {
          width: 40rpx;
          height: 40rpx;
          border-radius: 50%;
          margin-right: 16rpx;
        }

        .publish-time {
          font-size: 24rpx;
          color: #8c8c8c;

          .distance-text {
            color: #b2b2b2;
          }
        }
      }
    }

    /* 卡片身体内容 */
    .card-body {
      padding-top: 28rpx;
      display: flex;
      flex-direction: column;
      gap: 20rpx;

      .info-item {
        display: flex;
        line-height: 1.5;

        .item-label {
          font-size: 27rpx;
          color: #8c8c8c;
          width: 100rpx;
          white-space: nowrap;
        }

        .item-value {
          flex: 1;
          font-size: 27rpx;
          color: #2d3748;
          word-break: break-all;
        }
      }
    }

    /* 卡片底部操作栏 */
    .card-footer-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 32rpx;
      padding-top: 28rpx;
      border-top: 1rpx dashed #edf0f2;

      .action-btn {
        margin: 0;
        height: 68rpx;
        line-height: 68rpx;
        font-size: 26rpx;
        font-weight: bold;
        border-radius: 34rpx;
        padding: 0 32rpx;
        display: flex;
        align-items: center;
        cursor: pointer;

        &::after {
          border: none;
        }

        &.btn-apply {
          background-color: #07c160;
          color: #ffffff;
          box-shadow: 0 6rpx 16rpx rgba(7, 193, 96, 0.2);
        }

        &.btn-contact {
          background-color: #f3f4f6;
          color: #4b5563;
        }
      }
    }
  }

  /* 缺省页 */
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 240rpx;

    .empty-text {
      font-size: 28rpx;
      color: #999999;
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