<template>
  <div class="swipe-action" :data-sliding="isSliding">
    <!-- 内容部分 -->
    <div
      class="swipe-content"
      :style="{ transform: `translateX(${slideOffset}px)` }"
      @touchstart="handleTouchStart"
      @touchmove="handleTouchMove"
      @touchend="handleTouchEnd"
    >
      <!-- 默认插槽 -->
      <slot></slot>
    </div>

    <!-- 右侧操作按钮插槽 -->
    <div class="swipe-actions" :style="{ transform: `translateX(${slideOffset}px)`, right: `${maxSlide - 10}px` }">
      <slot name="right"></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";

// 定义组件属性
const props = defineProps({
  // 是否禁用左滑
  disabled: {
    type: Boolean,
    default: false
  },
  // 最大滑动距离
  maxSlide: {
    type: Number,
    default: -150
  },
  // 触发展开的滑动距离
  threshold: {
    type: Number,
    default: -60
  }
});

// 定义事件
const emit = defineEmits(["slide-start", "slide-move", "slide-end", "slide-open", "slide-close"]);

// 响应式数据
const slideOffset = ref(0);
const touchStartX = ref(0);
const touchStartY = ref(0);
const isTouching = ref(false);
const isSliding = computed(() => slideOffset.value < 0);

// 触摸事件处理
const handleTouchStart = event => {
  if (props.disabled) return;

  touchStartX.value = event.touches[0].clientX;
  touchStartY.value = event.touches[0].clientY;
  isTouching.value = true;

  emit("slide-start", { slideOffset: slideOffset.value });
};

const handleTouchMove = event => {
  if (props.disabled || !isTouching.value) return;

  const currentX = event.touches[0].clientX;
  const currentY = event.touches[0].clientY;
  const deltaX = currentX - touchStartX.value;
  const deltaY = currentY - touchStartY.value;

  // 判断是否为水平滑动（水平滑动距离大于垂直滑动距离，且水平滑动超过10px）
  if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 10) {
    event.preventDefault(); // 阻止页面滚动

    // 只允许向左滑动（负值），使用缓动函数让滑动更自然
    if (deltaX < 0) {
      const maxSlide = props.maxSlide;
      const slideDistance = Math.max(maxSlide, deltaX);

      // 添加缓动效果，让滑动更自然
      const easeFactor = 1 - Math.abs(slideDistance) / Math.abs(maxSlide);
      const finalSlide = slideDistance * (0.8 + 0.2 * easeFactor);

      slideOffset.value = finalSlide;
      emit("slide-move", { slideOffset: slideOffset.value, deltaX });
    }
  }
};

const handleTouchEnd = event => {
  if (props.disabled || !isTouching.value) return;

  const currentX = event.changedTouches[0].clientX;
  const deltaX = currentX - touchStartX.value;

  // 如果滑动距离超过阈值，则完全展开，否则回弹
  if (deltaX < props.threshold) {
    slideOffset.value = props.maxSlide;
    emit("slide-open", { slideOffset: slideOffset.value });
  } else {
    slideOffset.value = 0;
    emit("slide-close", { slideOffset: slideOffset.value });
  }

  isTouching.value = false;
  emit("slide-end", { slideOffset: slideOffset.value });
};

// 公共方法
const open = () => {
  slideOffset.value = props.maxSlide;
  emit("slide-open", { slideOffset: slideOffset.value });
};

const close = () => {
  slideOffset.value = 0;
  emit("slide-close", { slideOffset: slideOffset.value });
};

const reset = () => {
  slideOffset.value = 0;
};

// 暴露方法给父组件
defineExpose({
  open,
  close,
  reset,
  slideOffset: computed(() => slideOffset.value),
  isSliding
});

// 点击其他地方关闭已展开的项
const closeOnOutsideClick = event => {
  const target = event.target;
  const swipeAction = target.closest(".swipe-action");

  if (!swipeAction && slideOffset.value < 0) {
    close();
  }
};

onMounted(() => {
  // 添加页面点击事件监听，点击其他地方关闭已展开的项
  document.addEventListener("click", closeOnOutsideClick);
  document.addEventListener("touchstart", closeOnOutsideClick);
});

onUnmounted(() => {
  // 清理事件监听
  document.removeEventListener("click", closeOnOutsideClick);
  document.removeEventListener("touchstart", closeOnOutsideClick);
});
</script>

<style scoped lang="scss">
.swipe-action {
  position: relative;
  overflow: hidden;
  border-radius: 12px;

  .swipe-content {
    background: #fff;
    transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    will-change: transform;
    position: relative;
    z-index: 2;
    touch-action: pan-y; // 允许垂直滚动，但控制水平滑动

    &:active {
      background: #fafafa;
    }
  }

  .swipe-actions {
    position: absolute;
    top: 0;
    right: 0;
    height: 100%;
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 0 16px;
    z-index: 1;
    transition: opacity 0.3s ease;
  }
}

// 响应式设计
@media (max-width: 480px) {
  .swipe-action {
    .swipe-actions {
      gap: 6px;
    }
  }
}
</style>
