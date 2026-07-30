<template>
  <view class="special-page-container">
    <!-- 1. 自定义顶部宽屏搜索 Navbar (适配状态栏高度) -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="search-input-box">
        <u-icon name="search" color="#b2b2b2" size="32rpx"></u-icon>
        <input 
          type="text" 
          v-model="searchKeyword" 
          placeholder="好物、好课、好服务" 
          class="search-input"
        />
      </view>
    </view>

    <!-- 2. 内容区容器 -->
    <view class="main-content-body" :style="{ paddingTop: (statusBarHeight + 44) + 'px' }">
      <!-- A. 一级大类组件 (全屏 100% 平均分布) -->
      <SpecialCategoryNav 
        :categories="level1Categories"
        :activeIdx="active1Idx"
        @select="handleLevel1Select"
      />

      <!-- B. 下方双列分栏区 -->
      <view class="split-view-wrapper">
        <!-- 左侧二级侧边栏组件 (加大高度，传高 100vh 扣除高度) -->
        <SpecialSidebar 
          :subCategories="currentSubCategories"
          :activeIdx="active2Idx"
          :headerHeight="navAndHeaderTotalHeightPx"
          @select="handleLevel2Select"
        />

        <!-- 右侧店铺列表组件 (带多维独立筛选与 calc 高度计算的 scroll-view 触底分页) -->
       <view style="flex: 1;">
         <SpecialShopList 
          :shopList="filteredShops"
          :headerHeight="navAndHeaderTotalHeightPx"
          @go-detail="goToShopDetail"
        />
       </view>
      </view>
    </view>
  </view>
</template>

<script>
import SpecialCategoryNav from './components/SpecialCategoryNav.vue';
import SpecialSidebar from './components/SpecialSidebar.vue';
import SpecialShopList from './components/SpecialShopList.vue';

export default {
  components: {
    SpecialCategoryNav,
    SpecialSidebar,
    SpecialShopList
  },
  data() {
    return {
      statusBarHeight: 44,
      searchKeyword: '',
      active1Idx: 0, // 默认“特惠好物”
      active2Idx: 0, // 默认“全部”

      // 一级大类静态素材
      level1Categories: [
        { name: '特惠好物', icon: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=150&q=80' },
        { name: '教培托育', icon: 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=150&q=80' },
        { name: '生活服务', icon: 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=150&q=80' },
        { name: '餐饮住宿', icon: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=150&q=80' },
        { name: '休闲娱乐', icon: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=150&q=80' }
      ],

      // 修复：补全 5 大一级大类对应的全部二级小类，绝不为空！
      subCategoriesData: {
        '特惠好物': ['全部', '数码家电', '居家日用', '服饰鞋帽', '粮油生鲜', '美妆个护'],
        '教培托育': ['全部', '早教托育', '少儿英语', '艺术兴趣', '学科辅导', '成人培训'],
        '生活服务': ['全部', '家政保洁', '洗衣洗鞋', '家电维修', '美容美甲', '美发养发', '汽车服务', '宠物服务'],
        '餐饮住宿': ['全部', '美食快餐', '火锅烧烤', '甜品饮品', '特色民宿', '酒店住宿'],
        '休闲娱乐': ['全部', '洗浴汗蒸', '足疗按摩', 'KTV唱吧', '密室剧本杀', '运动健身']
      },

      // 补全所有分类下的店铺死数据源
      shopList: [
        // 生活服务 - 家政保洁
        { id: 401, cat1: '生活服务', cat2: '家政保洁', title: '厨卫下水道 / 马桶深度清洁惠民小铺', price: 98, sales: 240, rating: '4.9', isNew: false, image: 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=400&q=80' },
        { id: 402, cat1: '生活服务', cat2: '家政保洁', title: '阿姨来了 · 全屋深度保洁与玻璃擦洗', price: 158, sales: 180, rating: '4.7', isNew: true, image: 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=400&q=80' },
        // 生活服务 - 家电维修
        { id: 403, cat1: '生活服务', cat2: '家电维修', title: '老张家电清洗拆修（空调/油烟机/洗衣机）', price: 120, sales: 95, rating: '4.8', isNew: false, image: 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=400&q=80' },
        // 特惠好物
        { id: 404, cat1: '特惠好物', cat2: '数码家电', title: '社区特惠智能破壁机/养生壶专卖店', price: 199, sales: 310, rating: '4.9', isNew: true, image: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=400&q=80' },
        // 教培托育
        { id: 405, cat1: '教培托育', cat2: '早教托育', title: '阳光少儿乐园与全日制托育中心', price: 299, sales: 60, rating: '5.0', isNew: false, image: 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=400&q=80' },
        // 餐饮住宿
        { id: 406, cat1: '餐饮住宿', cat2: '美食快餐', title: '老广记地道烧腊饭与爱心惠民食堂', price: 22, sales: 880, rating: '4.8', isNew: false, image: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=400&q=80' },
        // 休闲娱乐
        { id: 407, cat1: '休闲娱乐', cat2: '运动健身', title: '社区青年健身房与瑜伽生活馆', price: 88, sales: 150, rating: '4.6', isNew: true, image: 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80' }
      ]
    };
  },
  computed: {
    // 换算需要扣除的总高度 (px)
    navAndHeaderTotalHeightPx() {
      // statusBarHeight + 44px(Navbar) + 70px(一级导航)
      return this.statusBarHeight + 44 + 70;
    },
    currentSubCategories() {
      const cat1Name = this.level1Categories[this.active1Idx].name;
      return this.subCategoriesData[cat1Name] || ['全部'];
    },
    filteredShops() {
      const cat1Name = this.level1Categories[this.active1Idx].name;
      const cat2Name = this.currentSubCategories[this.active2Idx];
      
      return this.shopList.filter(shop => {
        if (shop.cat1 !== cat1Name) return false;
        if (cat2Name && cat2Name !== '全部' && shop.cat2 !== cat2Name) return false;
        return true;
      });
    }
  },
  onLoad() {
    const sys = uni.getSystemInfoSync();
    this.statusBarHeight = sys.statusBarHeight || 44;
    uni.setNavigationBarTitle({ title: '社区特惠' });
  },
  methods: {
    handleLevel1Select(idx) {
      this.active1Idx = idx;
      this.active2Idx = 0; // 自动重置二级分类为“全部”
    },
    handleLevel2Select(sIdx) {
      this.active2Idx = sIdx;
    },
    goToShopDetail(id) {
      uni.navigateTo({
        url: `/spages/special/shop/index?id=${id}`
      });
    }
  }
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
</style>