<template>
  <div>
    <!-- 预览，弹窗展示 -->
    <el-dialog :title="previewForm.title" v-model="previewForm.show" width="375px" :close-on-click-modal="false">
      <div class="preview_content">
        <div v-html="previewForm.desc"></div>
      </div>
      <template #footer>
        <el-button @click="previewForm.show = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="previewDialog">
defineProps({
  previewForm: {
    type: Object,
    default: () => {}
  }
});
</script>

<style lang="scss" scoped>
.preview_content {
  max-height: 500px;
  overflow-y: auto;
}
</style>