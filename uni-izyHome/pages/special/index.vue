<template>
  <view class="special-page-container">
    <!-- 1. 自定义顶部搜索栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="search-input-box">
        <u-icon name="search" color="#b2b2b2" size="32rpx"></u-icon>
        <input type="text" v-model="searchKeyword" placeholder="搜索店铺" class="search-input" @confirm="onSearch" />
      </view>
    </view>
    <!-- 2. 内容区 -->
    <view class="main-content-body" :style="{ paddingTop: statusBarHeight + 44 + 'px' }">
      <view class="top-header-bar" style="margin-bottom:0">
        <view class="community-pill" @click="showCommunitySelector = true;">
          <u-icon name="home-fill" color="#07c160" size="16"></u-icon>
          <text class="pill-text text-ellipsis">{{ currentCommunity.name }}</text>
          <u-icon name="arrow-right" color="#999" size="10"></u-icon>
        </view>
      </view>
      <!-- 一级分类导航（全屏等宽） -->
      <SpecialCategoryNav :categories="level1Categories" :activeIdx="active1Idx" @select="handleLevel1Select" />

      <!-- 左右分栏 -->
      <view class="split-view-wrapper">
        <!-- 左侧二级侧边栏 -->
        <SpecialSidebar :subCategories="currentSubCategories" :activeIdx="active2Idx"
          :headerHeight="navAndHeaderTotalHeightPx" @select="handleLevel2Select" />

        <!-- 右侧店铺列表（含筛选 Bar + 分页列表） -->
        <view style="flex: 1">
          <SpecialShopList ref="shopList" :shopList="shopItems" :loading="shopLoading" :noMore="shopNoMore"
            :headerHeight="navAndHeaderTotalHeightPx" @go-detail="goToShopDetail" @filter-change="handleFilterChange"
            @load-more="loadMoreShops" />
        </view>
      </view>
    </view>
    <CommunitySelector :show.sync="showCommunitySelector" title="请选择我的社区" mode="select"
      @confirm="handleCommunityChange" />
  </view>
</template>

<script>
import SpecialCategoryNav from "./components/SpecialCategoryNav.vue";
import SpecialSidebar from "./components/SpecialSidebar.vue";
import SpecialShopList from "./components/SpecialShopList.vue";
import { getCategoryTree, shopList } from "@/api/special.js";
import CommunitySelector from "@/components/community.vue";
export default {
  components: {
    SpecialCategoryNav,
    SpecialSidebar,
    SpecialShopList,
    CommunitySelector
  },
  data() {
    return {
      showCommunitySelector: false,
      currentCommunity: {
        name: "请选择社区",
        communityId: ""
      },
      statusBarHeight: 44,
      searchKeyword: "",

      // 分类数据（从接口获取）
      treeData: [],
      active1Idx: 0,
      active2Idx: 0,

      // 店铺列表
      shopItems: [],
      shopPage: 1,
      shopPageSize: 10,
      shopLoading: false,
      shopNoMore: false,

      // 排序与筛选
      currentSort: "",
      currentHighRating: false,
      currentIsNew: false,
    };
  },
  computed: {
    level1Categories() {
      return this.treeData.map((c) => ({
        categoryId: c.categoryId,
        name: c.name,
        icon: c.icon || "",
      }));
    },
    currentSubCategories() {
      if (this.treeData.length === 0)
        return [{ name: "全部", categoryId: null }];
      const cat1 = this.treeData[this.active1Idx];
      if (!cat1 || !cat1.children || cat1.children.length === 0) {
        return [{ name: "全部", categoryId: null }];
      }
      return [
        { name: "全部", categoryId: null },
        ...cat1.children.map((c) => ({
          name: c.name,
          categoryId: c.categoryId,
        })),
      ];
    },
    navAndHeaderTotalHeightPx() {
      return this.statusBarHeight + 44 + 70;
    },

    // 当前选中分类 ID
    activeCat1Id() {
      const cat = this.level1Categories[this.active1Idx];
      return cat ? cat.categoryId : null;
    },
    activeCat2Id() {
      const subs = this.currentSubCategories;
      const cat2 = subs[this.active2Idx];
      return cat2 ? cat2.categoryId : null;
    },
  },
  onLoad() {
    const sys = uni.getSystemInfoSync();
    this.statusBarHeight = sys.statusBarHeight || 44;
    uni.setNavigationBarTitle({ title: "社区特惠" });



  },
  onShow() {
    const community = uni.getStorageSync("selected_community");
    if (community && community.communityId) {
      this.currentCommunity.communityId = community.communityId;
      this.currentCommunity.name = community.name;
    }
    this.fetchCategories();
  },
  methods: {
    handleCommunityChange(data) {
      if (data && data.community) {
        this.currentCommunity.name = data.community.name;
        this.currentCommunity.communityId = data.community.communityId;
        uni.setStorageSync("selected_community", data.community);
        this.fetchCategories();
      }
    },
    // -------- 分类 --------
    async fetchCategories() {
      try {
        const res = await getCategoryTree({
          communityId: this.currentCommunity.communityId || "",
        });
        if (res.code === "00000" && Array.isArray(res.data)) {
          this.treeData = res.data.sort(
            (a, b) => (a.sort || 0) - (b.sort || 0)
          );
          // 自动加载第一个分类的店铺
          this.fetchShops();
        }
      } catch (e) {
        uni.showToast({ title: "加载分类失败", icon: "none" });
      }
    },

    // -------- 店铺列表 --------
    async fetchShops(append = false) {
      if (this.shopLoading) return;
      if (!append) {
        this.shopPage = 1;
        this.shopItems = [];
        this.shopNoMore = false;
      }

      this.shopLoading = true;
      try {
        const payload = {
          pageNumber: this.shopPage,
          pageSize: this.shopPageSize,
          communityId: this.currentCommunity.communityId || undefined,
        };
        if (this.activeCat1Id) payload.cat1Id = this.activeCat1Id;
        if (this.activeCat2Id) payload.cat2Id = this.activeCat2Id;
        if (this.searchKeyword) payload.keyword = this.searchKeyword;
        if (this.currentSort) payload.sort = this.currentSort;
        if (this.currentHighRating) payload.highRating = true;
        if (this.currentIsNew) payload.isNew = true;

        const res = await shopList(payload);
        if (res.code === "00000" && res.data) {
          const { content = [], last } = res.data;
          const mapped = content.map((item) => ({
            id: item.shopId || item.id,
            title: item.name || "",
            image: item.coverImage || item.logo || "",
            price: item.startPrice || 0,
            sales: item.monthlySales || 0,
            rating:
              typeof item.rating === "number"
                ? item.rating.toFixed(1)
                : item.rating || "0",
            isNew: item.isNew || false,
            address: item.address || "",
            phone: item.phone || "",
            description: item.description || "",
            goodsCount: item.goodsCount || 0,
            fansCount: item.fansCount || 0,
          }));
          this.shopItems = append ? this.shopItems.concat(mapped) : mapped;
          this.shopNoMore = last !== false;
        }
      } catch (e) {
        uni.showToast({ title: "加载店铺失败", icon: "none" });
      } finally {
        this.shopLoading = false;
      }
    },

    loadMoreShops() {
      if (this.shopNoMore || this.shopLoading) return;
      this.shopPage++;
      this.fetchShops(true);
    },

    // -------- 分类切换 --------
    handleLevel1Select(idx) {
      this.active1Idx = idx;
      this.active2Idx = 0;
      this.fetchShops();
    },
    handleLevel2Select(idx) {
      this.active2Idx = idx;
      this.fetchShops();
    },

    // -------- 搜索 --------
    onSearch() {
      this.fetchShops();
    },

    // -------- 排序 / 筛选 --------
    handleFilterChange({ type, value }) {
      if (type === "sort") {
        this.currentSort = value;
      } else if (type === "highRating") {
        this.currentHighRating = value;
      } else if (type === "isNew") {
        this.currentIsNew = value;
      }
      this.fetchShops();
    },

    // -------- 跳转详情 --------
    goToShopDetail(id) {
      uni.navigateTo({
        url: `/spages/special/shop/index?id=${id}`,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.special-page-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f7f9fb;

  .custom-navbar {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    background-color: #ffffff;
    height: 88rpx;
    padding: 0 24rpx;
    box-sizing: content-box;
    display: flex;
    align-items: center;
    z-index: 100;
    border-bottom: 1rpx solid #edf2f7;

    .search-input-box {
      width: 70%;
      height: 64rpx;
      background-color: #f1f3f5;
      border-radius: 32rpx;
      padding: 0 24rpx;
      display: flex;
      align-items: center;
      box-sizing: border-box;

      .search-input {
        flex: 1;
        font-size: 26rpx;
        color: #333333;
        margin-left: 12rpx;
      }
    }
  }

  .main-content-body {
    height: 100vh;
    display: flex;
    flex-direction: column;
    box-sizing: border-box;
  }

  .split-view-wrapper {
    flex: 1;
    display: flex;
    overflow: hidden;
  }
}

/* 顶部社区栏 (90rpx) */
.top-header-bar {
  height: 90rpx;
  padding: 0 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #ffffff;
  box-sizing: border-box;
  border-bottom: 1rpx solid #edf2f7;

  .community-pill {
    display: inline-flex;
    align-items: center;
    background-color: #f1f3f5;
    padding: 10rpx 20rpx;
    border-radius: 30rpx;
    max-width: 60%;

    .pill-text {
      font-size: 26rpx;
      font-weight: bold;
      color: #2c405a;
      margin: 0 10rpx;
    }
  }

  .store-header-tips {
    font-size: 24rpx;
    font-weight: bold;
    color: #94a3b8;
  }
}
</style>
