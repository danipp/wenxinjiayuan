<template>
  <!-- 
     示例
     <enhancedSelect v-model:modelValue="domainId" @change="changeDomain" :api="getDomainList" value="id" label="name" /> 
     -->
  <div class="home">
    <el-select v-model="selectedValue" placeholder="请选择" :multiple="multiple" clearable filterable remote :loading="keywordLoading"
      :remote-method="debouncedSearch" @visible-change="handleVisibleChange" @change="handleChange">
      <el-option v-for="item in domainList" :key="item[value]" :label="item[label]" :value="item[value]"></el-option>

      <!-- 加载更多提示 -->
      <el-option v-if="hasMore && !loading" :disabled="true" value="load-more">
        <div style="text-align: center; color: #999; cursor: pointer" @click.stop="loadMore">
          <el-icon>
            <ArrowDown />
          </el-icon>
          点击加载更多
        </div>
      </el-option>
      <!-- 加载中提示 -->
      <el-option v-if="loading" :disabled="true" value="loading">
        <div style="text-align: center; color: #999">
          <el-icon class="is-loading">
            <Loading />
          </el-icon>
          加载中...
        </div>
      </el-option>
      <!-- 无更多数据提示 -->
      <!-- <el-option v-if="!hasMore && domainList.length > 0" :disabled="true" value="no-more">
        <div style="text-align: center; color: #999">没有更多数据了</div>
      </el-option> -->
    </el-select>
  </div>
</template>

<script setup name="home">
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from 'vue-router'
const router = useRouter()
const handleRouterCart = () => {
  router.push("/list")
}
const props = defineProps({
  modelValue: {
    type: String,
    default: ""
  },
  multiple:{
    type: Boolean,
    default: false
  },
  api: {
    type: Function
  },
  label: {
    type: String,
    default: "label"
  },
  value: {
    type: String,
    default: "value"
  },
  materialType: {
    type: String,
    default: "1"
  }
});
const emit = defineEmits(["update:modelValue", "change"]);
const selectedValue = computed({
  get() {
    return props.modelValue;
  },
  set(val) {
    emit("update:modelValue", val);
  }
});
const searchKeyword = ref("");
const loading = ref(false);
const hasMore = ref(true);
const keywordLoading = ref(false);
const pageNumberSize = reactive({
  pageNumber: 1,
  pageSize: 10
});
const domainList = ref([]);

const getDomainApi = async () => {
  loading.value = true;
  let res = await props.api({ ...pageNumberSize, title: searchKeyword.value });
  hasMore.value = !res.data.last;
  // imgList.value = res.data.content.filter(item => item.materialType === 1);
  // videoList.value = res.data.content.filter(item => item.materialType === 2);
  const content = res.data.content
  if (pageNumberSize.pageNumber == 1) {
    domainList.value = content;
  } else {
    domainList.value.push(...content);
  }
  setTimeout(() => {
    keywordLoading.value = false;
    loading.value = false;
  }, 500);
};
onMounted(() => {
  getDomainApi();
});
const handleChange = value => {
  emit("change", value);
};
// 防抖函数
let debounceTimer = null;
const debouncedSearch = query => {
  if (debounceTimer) {
    clearTimeout(debounceTimer);
  }
  debounceTimer = setTimeout(() => {
    // console.log(query, "搜索");
    performSearch(query);
  }, 500);
};
// 处理下拉框显示/隐藏
const handleVisibleChange = async visible => {
  if (visible && domainList.value.length === 0) {
    console.log("无数据的时候搜索");
  }
};
// 执行搜索
const performSearch = async query => {
  if (query) {
    pageNumberSize.pageNumber = 1;
    keywordLoading.value = true;
    searchKeyword.value = query;
    getDomainApi();
  }
};
const loadMore = () => {
  pageNumberSize.pageNumber++;
  getDomainApi();
};
</script>

<style scoped lang="scss"></style>