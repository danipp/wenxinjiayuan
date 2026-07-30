<template>
  <div class="dropdown-filter-wrapper" @touchmove.stop.prevent>
    <!-- 1. 筛选头部栏 -->
    <div class="filter-bar">
      <div
        v-for="(menu, index) in localMenus"
        :key="index"
        class="filter-item"
        :class="{
          'active-item': activeMenuIndex === index || hasSelectedValue(menu),
        }"
        @click="toggleMenu(index)"
      >
        <span class="filter-title text-ellipsis">
          {{ getMenuTitle(menu) }}
        </span>
        <!-- 三角形指示器 -->
        <span
          class="triangle-icon"
          :class="{ 'triangle-up': activeMenuIndex === index }"
        ></span>
      </div>
    </div>

    <!-- 2. 下拉容器与遮罩 -->
    <div class="dropdown-panel" v-if="activeMenuIndex !== -1">
      <!-- 遮罩层 -->
      <div class="dropdown-mask" @click="closeMenu"></div>

      <!-- 选项列表 -->
      <div class="dropdown-content">
        <scroll-view scroll-y class="options-scroll">
          <div
            v-for="(option, idx) in currentOptions"
            :key="idx"
            class="option-row"
            :class="{ 'option-row-active': isOptionSelected(option) }"
            @click="selectOption(option)"
          >
            <span class="option-label">{{ getLabel(option) }}</span>
            <!-- 选中的对勾图标 -->
            <u-icon
              v-if="isOptionSelected(option)"
              name="checkmark-circle-fill"
              color="#07c160"
              size="20"
            ></u-icon>
          </div>
        </scroll-view>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "DropdownFilter",
  props: {
    // 双向绑定当前的选中值，格式如：{ status: 'all', requirement: 'free', sort: 'default' }
    value: {
      type: Object,
      default: () => ({}),
    },
    // 自定义数据源，若不传则使用默认的筛选数据
    menus: {
      type: Array,
      default: null,
    },
    // 更改 value 和 label 属性的映射
    fieldProps: {
      type: Object,
      default: () => ({
        value: "value",
        label: "label",
      }),
    },
  },
  data() {
    return {
      activeMenuIndex: -1, // 当前展开的菜单索引，-1 表示全部收起
      selectedMap: {}, // 内部维护的选中状态 { [menuKey]: selectedValue }
    };
  },
  computed: {
    // 生成菜单项（若父组件未传入 menus，则应用内置默认数据）
    localMenus() {
      if (this.menus && this.menus.length > 0) {
        return this.menus;
      }

      // 默认数据源并适配用户自定义的 label/value 字段
      const lKey = this.fieldProps.label || "label";
      const vKey = this.fieldProps.value || "value";

      return [
        {
          title: "全部状态",
          key: "status",
          options: [
            { [lKey]: "全部状态", [vKey]: "all" },
            { [lKey]: "已匹配", [vKey]: "matched" },
            { [lKey]: "已完成", [vKey]: "completed" },
          ],
        },
        {
          title: "全部需求",
          key: "requirement",
          options: [
            { [lKey]: "全部需求", [vKey]: "all" },
            { [lKey]: "免费需求", [vKey]: "free" },
            { [lKey]: "付费需求", [vKey]: "paid" },
          ],
        },
        {
          title: "默认排序",
          key: "sort",
          options: [
            { [lKey]: "默认排序", [vKey]: "default" },
            { [lKey]: "最新排序", [vKey]: "latest" },
          ],
        },
      ];
    },
    // 当前打开的下拉框的选项列表
    currentOptions() {
      if (this.activeMenuIndex === -1) return [];
      return this.localMenus[this.activeMenuIndex].options || [];
    },
  },
  watch: {
    value: {
      handler(newVal) {
        this.selectedMap = { ...newVal };
      },
      immediate: true,
      deep: true,
    },
  },
  methods: {
    // 安全获取配置的 label
    getLabel(item) {
      const key = this.fieldProps.label || "label";
      return item[key];
    },
    // 安全获取配置的 value
    getValue(item) {
      const key = this.fieldProps.value || "value";
      return item[key];
    },
    // 切换菜单展开/收起
    toggleMenu(index) {
      if (this.activeMenuIndex === index) {
        this.closeMenu();
      } else {
        this.activeMenuIndex = index;
      }
    },
    closeMenu() {
      this.activeMenuIndex = -1;
    },
    // 判定选项是否已被选中
    isOptionSelected(option) {
      if (this.activeMenuIndex === -1) return false;
      const menuKey = this.localMenus[this.activeMenuIndex].key;
      return this.selectedMap[menuKey] === this.getValue(option);
    },
    // 判定某栏是否选择了“非默认值”
    hasSelectedValue(menu) {
      const val = this.selectedMap[menu.key];
      if (!val) return false;
      // 如果选中的不是第一项（通常第一项为全部/默认值），则将表头字体变绿
      const defaultVal = this.getValue(menu.options[0]);
      return val !== defaultVal;
    },
    // 获取表头显示的文字（若选了具体项，则表头显示选中项文字）
    getMenuTitle(menu) {
      const activeValue = this.selectedMap[menu.key];
      const selectedOption = menu.options.find(
        (opt) => this.getValue(opt) === activeValue
      );
      return selectedOption ? this.getLabel(selectedOption) : menu.title;
    },
    // 点击并选中某选项
    selectOption(option) {
      const currentMenu = this.localMenus[this.activeMenuIndex];
      const value = this.getValue(option);

      this.$set(this.selectedMap, currentMenu.key, value);

      // 同步给父组件并触发 change 事件
      this.$emit("input", this.selectedMap);
      this.$emit("change", {
        key: currentMenu.key,
        value: value,
        item: option,
        allValues: this.selectedMap,
      });

      this.closeMenu();
    },
  },
};
</script>

<style lang="scss" scoped>
.dropdown-filter-wrapper {
  position: relative;
  width: 100%;
  background-color: #fff;
  z-index: 99;

  /* 筛选头部栏 */
  .filter-bar {
    display: flex;
    align-items: center;
    height: 44px;
    border-bottom: 1px solid #f5f5f5;
    background-color: #fff;
    position: relative;
    z-index: 101;

    .filter-item {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      cursor: pointer;
      color: #333333;
      font-size: 15px;
      transition: color 0.2s ease;

      .filter-title {
        max-width: 80%;
      }

      /* 绿色高亮 */
      &.active-item {
        color: #07c160;
        font-weight: bold;
      }

      /* 箭头样式 */
      .triangle-icon {
        margin-left: 6px;
        width: 0;
        height: 0;
        border-left: 4px solid transparent;
        border-right: 4px solid transparent;
        border-top: 5px solid #cccccc; // 默认向下三角形
        transition: transform 0.25s ease, border-top-color 0.2s ease;

        &.triangle-up {
          transform: rotate(180deg);
          border-top-color: #07c160; // 展开时箭头向上且变绿
        }
      }
    }
  }

  /* 下拉面板区 */
  .dropdown-panel {
    position: absolute;
    top: 44px;
    left: 0;
    width: 100%;

    /* 采用 fixed 遮罩确保覆盖全屏 */
    .dropdown-mask {
      position: fixed;
      top: calc(44px + var(--window-top, 0px)); // 适配小程序与 H5 导航高度差异
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.4);
      z-index: 99;
    }

    .dropdown-content {
      position: relative;
      background-color: #ffffff;
      max-height: 300px;
      z-index: 100;
      border-bottom-left-radius: 12px;
      border-bottom-right-radius: 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      overflow: hidden;

      .options-scroll {
        max-height: 300px;
      }

      .option-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 15px 24px;
        border-bottom: 1px solid #f8f8f8;
        font-size: 15px;
        color: #333333;
        transition: background-color 0.1s ease;

        &:last-child {
          border-bottom: none;
        }

        &:active {
          background-color: #f9f9f9;
        }

        &.option-row-active {
          color: #07c160;
          font-weight: bold;
        }
      }
    }
  }

  /* 辅助的单行省略样式 */
  .text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>