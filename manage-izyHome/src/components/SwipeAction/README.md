# SwipeAction 左滑操作组件

一个支持左滑显示操作按钮的移动端组件，具有流畅的动画效果和触摸手势支持。

## 特性

- 🎯 支持左滑手势操作
- ✨ 流畅的过渡动画效果
- 📱 移动端触摸友好
- 🎨 可自定义操作按钮样式
- 🔧 支持插槽和事件回调
- 📏 可配置滑动距离和阈值

## 基础用法

```vue
<template>
  <SwipeAction>
    <!-- 默认内容插槽 -->
    <div class="content">
      这是主要内容
    </div>
    
    <!-- 右侧操作按钮插槽 -->
    <template #right>
      <el-button type="primary" @click="handleEdit">编辑</el-button>
      <el-button type="danger" @click="handleDelete">删除</el-button>
    </template>
  </SwipeAction>
</template>

<script setup>
import SwipeAction from '@/components/SwipeAction';
</script>
```

## 高级用法

```vue
<template>
  <SwipeAction 
    :max-slide="-120"
    :threshold="-50"
    :disabled="false"
    :prevent-click-when-open="true"
    :click-delay="200"
    @slide-open="handleSlideOpen"
    @slide-close="handleSlideClose"
  >
    <div class="content">内容</div>
    <template #right>
      <el-button>操作</el-button>
    </template>
  </SwipeAction>
</template>
```

## 点击行为配置

### preventClickWhenOpen 选项

- `true`（默认）：在左滑展开状态下，点击内容区域会先关闭展开项，不会触发其他点击事件
- `false`：允许在展开状态下直接触发点击事件（如跳转详情）

```vue
<!-- 允许在展开状态下跳转详情 -->
<SwipeAction :prevent-click-when-open="false" @click="goToDetail">
  <div class="content">点击跳转详情</div>
  <template #right>
    <el-button>编辑</el-button>
  </template>
</SwipeAction>

<!-- 默认行为：展开状态下点击先关闭展开项 -->
<SwipeAction :prevent-click-when-open="true">
  <div class="content">展开状态下点击先关闭</div>
  <template #right>
    <el-button>编辑</el-button>
  </template>
</SwipeAction>
```

## Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| disabled | 是否禁用左滑 | Boolean | false |
| maxSlide | 最大滑动距离（负值） | Number | -150 |
| threshold | 触发展开的滑动距离（负值） | Number | -60 |
| preventClickWhenOpen | 是否在展开状态下阻止点击事件 | Boolean | true |
| clickDelay | 点击延迟时间（毫秒） | Number | 150 |

## Events

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| slide-start | 开始滑动时触发 | { slideOffset } |
| slide-move | 滑动过程中触发 | { slideOffset, deltaX } |
| slide-end | 滑动结束时触发 | { slideOffset } |
| slide-open | 滑动展开时触发 | { slideOffset } |
| slide-close | 滑动关闭时触发 | { slideOffset } |

## 方法

| 方法名 | 说明 | 参数 |
|--------|------|------|
| open | 手动展开 | - |
| close | 手动关闭 | - |
| reset | 重置状态 | - |

## 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 主要内容区域 |
| right | 右侧操作按钮区域 |

## 样式定制

组件使用CSS变量，可以通过以下方式自定义样式：

```scss
.swipe-action {
  --swipe-transition-duration: 0.3s;
  --swipe-border-radius: 12px;
  --swipe-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
```

## 注意事项

1. 组件会自动处理触摸事件，阻止页面滚动冲突
2. 同时只能展开一个SwipeAction项
3. 点击其他地方会自动关闭已展开的项
4. 建议在移动端使用，桌面端可能体验不佳
