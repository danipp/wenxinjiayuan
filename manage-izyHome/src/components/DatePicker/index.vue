<template>
  <div class="h5-date-picker" :class="{ disabled }">
    <div class="h5-date-picker__display" @click="open">
      <el-icon><Calendar /></el-icon>
      <span v-if="label" class="h5-date-picker__label">{{ label }}</span>
      <span :class="['h5-date-picker__value', { placeholder: !displayText }]">
        {{ displayText || placeholder }}
      </span>
    </div>

    <!-- <button v-if="clearable && innerValue" class="h5-date-picker__clear" @click.stop="clear">清空</button> -->

    <el-drawer v-model="isOpen" class="h5-date-picker__sheet" direction="btt" :show-close="false" size="35%">
      <template #header>
        <div class="h5-date-picker__header">
          <el-button type="info" text @click="close" class="cancel">取消</el-button>
          <div class="h5-date-picker__title">
            {{ props.type === "date" ? "选择日期" : "选择年份范围" }}
          </div>
          <el-button type="primary" text @click="confirm" class="confirm">确定</el-button>
        </div>
      </template>
      <div class="h5-date-picker__columns">
        <!-- 日期选择模式 -->
        <template v-if="props.type === 'date'">
          <select v-model.number="tempYear" class="h5-date-picker__select">
            <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}年</option>
          </select>
          <select v-model.number="tempMonth" class="h5-date-picker__select">
            <option v-for="m in monthOptions" :key="m" :value="m">{{ m }}月</option>
          </select>
          <select v-model.number="tempDay" class="h5-date-picker__select">
            <option v-for="d in dayOptions" :key="d" :value="d">{{ d }}日</option>
          </select>
        </template>

        <!-- 年份范围选择模式 -->
        <template v-else-if="props.type === 'daterange'">
          <div class="h5-date-picker__range-item">
            <span class="h5-date-picker__range-label">开始年份</span>
            <select v-model.number="tempStartYear" class="h5-date-picker__select">
              <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}年</option>
            </select>
          </div>
          <div class="h5-date-picker__range-item">
            <span class="h5-date-picker__range-label">结束年份</span>
            <select v-model.number="tempEndYear" class="h5-date-picker__select">
              <option v-for="y in yearOptions" :key="y" :value="y">{{ y }}年</option>
            </select>
          </div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="H5DatePicker">
import { computed, ref, watch } from "vue";
import { Calendar } from "@element-plus/icons-vue";

type DateLike = string | Date | null | undefined;

const props = withDefaults(
  defineProps<{
    modelValue: DateLike | [DateLike, DateLike] | null;
    placeholder?: string;
    label?: string;
    format?: string;
    valueFormat?: string; // 目前仅支持 YYYY-MM-DD
    minDate?: DateLike;
    maxDate?: DateLike;
    clearable?: boolean;
    disabled?: boolean;
    type?: "date" | "daterange"; // 新增类型：日期或年份范围
  }>(),
  {
    placeholder: "请选择日期",
    format: "YYYY-MM-DD",
    valueFormat: "YYYY-MM-DD",
    clearable: false,
    disabled: false,
    type: "date" // 默认是日期选择模式
  }
);

const emit = defineEmits<{
  (e: "update:modelValue", value: DateLike | [DateLike, DateLike] | null): void;
  (e: "change", value: DateLike | [DateLike, DateLike] | null): void;
  (e: "open"): void;
  (e: "close"): void;
}>();

const isOpen = ref(false);
type InnerValue = DateLike | [DateLike, DateLike] | null;
const innerValue = ref<InnerValue>(
  props.modelValue ? (Array.isArray(props.modelValue) ? props.modelValue : props.modelValue) : null
);

// 监听props的modelValue变化，同步到innerValue
watch(
  () => props.modelValue,
  val => {
    innerValue.value = val ?? null;
  }
);

// 监听innerValue变化，触发更新事件
watch(innerValue, val => {
  emit("update:modelValue", val ?? null);
  emit("change", val ?? null);
});

const placeholder = computed(() => {
  if (props.type === "daterange") {
    return props.placeholder || "请选择年份范围";
  }
  return props.placeholder;
});

// 日期解析工具函数
function parseToDate(value: DateLike): Date | null {
  if (!value) return null;
  if (value instanceof Date) return value;
  const normalized = typeof value === "string" ? value.replace(/-/g, "/") : value;
  const d = new Date(normalized as string);
  return isNaN(d.getTime()) ? null : d;
}

// 格式化年月日
function formatYMD(y: number, m: number, d: number) {
  const mm = String(m).padStart(2, "0");
  const dd = String(d).padStart(2, "0");
  return `${y}-${mm}-${dd}`;
}

// 获取月份天数
function getDaysInMonth(y: number, m: number) {
  return new Date(y, m, 0).getDate();
}

// 限制日期在min和max范围内
function clampToRange(date: Date): Date {
  const min = parseToDate(props.minDate);
  const max = parseToDate(props.maxDate);
  let t = date.getTime();
  if (min && t < stripTime(min).getTime()) return stripTime(min);
  if (max && t > stripTime(max).getTime()) return stripTime(max);
  return date;
}

// 去除时间部分，只保留日期
function stripTime(d: Date): Date {
  return new Date(d.getFullYear(), d.getMonth(), d.getDate());
}

// 显示文本计算
const displayText = computed(() => {
  if (props.type === "date") {
    const d = parseToDate(innerValue.value as DateLike);
    if (!d) return "";
    const y = d.getFullYear();
    const m = d.getMonth() + 1;
    const day = d.getDate();
    return formatYMD(y, m, day);
  } else if (props.type === "daterange") {
    const range = innerValue.value as [DateLike, DateLike] | null;
    if (!range || !range[0] || !range[1]) return "";
    return `${range[0]} - ${range[1]}`;
  }
  return "";
});

// 日期模式临时选择值
const tempYear = ref<number>(new Date().getFullYear());
const tempMonth = ref<number>(new Date().getMonth() + 1);
const tempDay = ref<number>(new Date().getDate());

// 年份范围模式临时选择值
const tempStartYear = ref<number>(new Date().getFullYear());
const tempEndYear = ref<number>(new Date().getFullYear() + 1);

// 年份选项计算（受min/max限制）
const yearOptions = computed<number[]>(() => {
  const now = new Date();
  const min = parseToDate(props.minDate) ?? new Date(now.getFullYear() - 100, 0, 1);
  const max = parseToDate(props.maxDate) ?? new Date(now.getFullYear() + 20, 11, 31);
  const start = stripTime(min).getFullYear();
  const end = stripTime(max).getFullYear();
  const years: number[] = [];
  for (let y = start; y <= end; y++) years.push(y);
  return years;
});

// 月份选项计算（仅日期模式使用）
const monthOptions = computed<number[]>(() => {
  if (props.type === "daterange") return [];

  const min = parseToDate(props.minDate);
  const max = parseToDate(props.maxDate);
  let start = 1;
  let end = 12;

  if (min && tempYear.value === stripTime(min).getFullYear()) {
    start = stripTime(min).getMonth() + 1;
  }
  if (max && tempYear.value === stripTime(max).getFullYear()) {
    end = stripTime(max).getMonth() + 1;
  }

  const months: number[] = [];
  for (let m = start; m <= end; m++) months.push(m);
  return months;
});

// 日期选项计算（仅日期模式使用）
const dayOptions = computed<number[]>(() => {
  if (props.type === "daterange") return [];

  const min = parseToDate(props.minDate);
  const max = parseToDate(props.maxDate);
  const dim = getDaysInMonth(tempYear.value, tempMonth.value);
  let start = 1;
  let end = dim;

  if (min && tempYear.value === stripTime(min).getFullYear() && tempMonth.value === stripTime(min).getMonth() + 1) {
    start = stripTime(min).getDate();
  }
  if (max && tempYear.value === stripTime(max).getFullYear() && tempMonth.value === stripTime(max).getMonth() + 1) {
    end = stripTime(max).getDate();
  }

  const days: number[] = [];
  for (let d = start; d <= end; d++) days.push(d);

  // 保证当前选择的day不超过范围
  if (tempDay.value > end) tempDay.value = end;
  if (tempDay.value < start) tempDay.value = start;

  return days;
});

// 监听年份和月份变化，调整日期范围
watch([tempYear, tempMonth], () => {
  if (props.type === "date") {
    const dim = getDaysInMonth(tempYear.value, tempMonth.value);
    if (tempDay.value > dim) tempDay.value = dim;
  }
});

// 监听年份范围选择变化，确保开始年份不大于结束年份
watch([tempStartYear, tempEndYear], () => {
  if (props.type === "daterange" && tempStartYear.value > tempEndYear.value) {
    tempEndYear.value = tempStartYear.value;
  }
});

// 从当前值同步到临时选择值
function syncTempFromValue() {
  if (props.type === "date") {
    const base = clampToRange(parseToDate(innerValue.value as DateLike) ?? new Date());
    tempYear.value = base.getFullYear();
    tempMonth.value = base.getMonth() + 1;
    tempDay.value = base.getDate();
  } else if (props.type === "daterange") {
    const range = innerValue.value as [DateLike, DateLike] | null;
    const defaultStart = new Date().getFullYear();
    const defaultEnd = defaultStart + 1;

    if (range && range[0] && range[1]) {
      const startDate = parseToDate(range[0]) || new Date(defaultStart, 0, 1);
      const endDate = parseToDate(range[1]) || new Date(defaultEnd, 0, 1);
      tempStartYear.value = startDate.getFullYear();
      tempEndYear.value = endDate.getFullYear();
    } else {
      tempStartYear.value = defaultStart;
      tempEndYear.value = defaultEnd;
    }
  }
}

// 打开选择器
function open() {
  if (props.disabled) return;
  syncTempFromValue();
  isOpen.value = true;
  emit("open");
}

// 关闭选择器
function close() {
  isOpen.value = false;
  emit("close");
}

// 确认选择
function confirm() {
  if (props.type === "date") {
    const clamped = clampToRange(new Date(tempYear.value, tempMonth.value - 1, tempDay.value));
    innerValue.value = formatYMD(clamped.getFullYear(), clamped.getMonth() + 1, clamped.getDate());
  } else if (props.type === "daterange") {
    // 确保开始年 <= 结束年
    let start = tempStartYear.value;
    let end = tempEndYear.value;
    if (start > end) {
      [start, end] = [end, start];
    }
    innerValue.value = [start.toString(), end.toString()];
  }
  close();
}

// 清空选择
function clear() {
  innerValue.value = null;
}
</script>

<style scoped lang="scss">
.h5-date-picker {
  display: flex;
  min-width: 200px;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-radius: 4px;
  border: 1px solid var(--el-disabled-border-color);
  box-sizing: border-box;
  padding: 0 12px;
}

.h5-date-picker.disabled {
  opacity: 0.6;
  pointer-events: none;
}

.h5-date-picker__display {
  display: flex;
  width: 100%;
  align-items: center;
  cursor: pointer;
}

.h5-date-picker__label {
  color: #333;
  font-size: 14px;
}

.h5-date-picker__value {
  margin-left: 8px;
  flex: 1;
  color: #222;
  font-size: 14px;
}

.h5-date-picker__value.placeholder {
  color: #999;
}

.h5-date-picker__clear {
  margin-left: 8px;
  background: none;
  border: none;
  color: #999;
  font-size: 12px;
  cursor: pointer;
  padding: 4px 8px;
}

.h5-date-picker__sheet {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  padding-bottom: env(safe-area-inset-bottom);
  overflow: visible;
}

.h5-date-picker__header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  :deep(.el-button) {
    font-size: 14px !important;
    padding: 0 !important;
  }
}

.h5-date-picker__title {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.h5-date-picker__columns {
  display: flex;
  padding: 16px;
  gap: 12px;
  box-sizing: border-box;
}

.h5-date-picker__select {
  flex: 1;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  background: #fafafa;
  appearance: none;
  position: relative;
  height: 44px;
  box-sizing: border-box;
}

/* 年份范围模式样式 */
.h5-date-picker__range-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.h5-date-picker__range-label {
  font-size: 14px;
  color: #666;
  text-align: center;
}

/* 修复抽屉内容溢出问题 */
.h5-date-picker__sheet :deep(.el-drawer__body) {
  overflow: visible !important;
  padding: 0 !important;
}

/* 自定义下拉箭头 */
.h5-date-picker__select::-ms-expand {
  display: none;
}

.h5-date-picker__select:after {
  content: "";
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #666;
  pointer-events: none;
}
</style>
