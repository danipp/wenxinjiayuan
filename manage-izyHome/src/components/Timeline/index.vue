<template>
  <div class="timeLine">
    <el-drawer v-model="drawerVisible" title="订单时间轴" size="450px">
      <template #header>
        <div class="flex_header">
          订单时间轴
          <span>到期时间：{{ TimeForm.deadTime }}</span>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item center placement="top" type="primary">
          <el-card>
            <h4>子订单号：{{ TimeForm.subOrderId }}</h4>
            <p style="cursor: pointer;" @click="handlePreview" v-if="TimeForm.material" class="line1" title="点击预览">内容：{{
              TimeForm.material.title }}</p>
          </el-card>
        </el-timeline-item>

        <el-timeline-item center placement="top" type="primary">
          <span :class="method === 'send' ? 'zhuren' : 'keren'">
            派单方 <span v-if="!userStore.is_admin">{{ method === 'send' ? '（我）' : '（他）' }}</span>
          </span> 派单 {{ TimeForm.createTime }} </el-timeline-item>
        <!-- 待审核 -->
        <el-timeline-item center placement="top" type="primary"
          v-if="TimeForm.status === '待审核' || TimeForm.status === '审核拒绝'">
          等待后台审核
        </el-timeline-item>
        <!-- 审核拒绝 -->
        <el-timeline-item center placement="top" type="primary" v-if="TimeForm.status === '审核拒绝'">
          <div style="color:#e45656">审核不通过</div>
        </el-timeline-item>
        <!-- 已撤销 -->
        <el-timeline-item center placement="top" type="primary" v-if="TimeForm.status === '已撤销'">
          <span :class="method === 'send' ? 'zhuren' : 'keren'">
            派单方 <span v-if="!userStore.is_admin">{{ method === 'send' ? '（我）' : '（他）' }}</span>
          </span>
          已撤销
        </el-timeline-item>
        <!-- 已拒接 -->
        <el-timeline-item center placement="top" type="primary" v-if="TimeForm.status === '已拒接'">
          <span :class="method !== 'send' ? 'zhuren' : 'keren'">
            接单方<span v-if="!userStore">{{ method !== 'send' ? '（我）' : '（他）' }}</span>
          </span>
          已拒接
        </el-timeline-item>
        <el-timeline-item placement="top"
          v-if="TimeForm.status === '待接单' || TimeForm.status === '进行中' || TimeForm.status === '待确认' || TimeForm.status === '已完成' || TimeForm.status === '已失败'"
          type="primary"> 等待<span :class="method !== 'send' ? 'zhuren' : 'keren'">
            接单方<span v-if="!userStore">{{ method !== 'send' ? '（我）' : '（他）' }}</span>
          </span>接单 </el-timeline-item>
        <el-timeline-item placement="top"
          v-if="TimeForm.status === '进行中' || TimeForm.status === '待确认' || TimeForm.status === '已完成' || TimeForm.status === '已失败'"
          type="primary"> 等待<span :class="method !== 'send' ? 'zhuren' : 'keren'">
            接单方<span v-if="!userStore">{{ method !== 'send' ? '（我）' : '（他）' }}</span>
          </span>反馈 </el-timeline-item>
        <el-timeline-item placement="top" type="primary"
          v-if="TimeForm.status === '待确认' || TimeForm.status === '已完成' || TimeForm.status === '已失败'">
          <div>
            <div class="title"><span :class="method !== 'send' ? 'zhuren' : 'keren'">
                接单方<span v-if="!userStore">{{ method !== 'send' ? '（我）' : '（他）' }}</span>
              </span>反馈</div>
            <el-card style="margin-top: 10px;">
              <h4>子订单号：{{ TimeForm.subOrderId }}</h4>
              <p v-if="TimeForm.material" class="line1">内容：{{ TimeForm.material.title }}</p>
              <p>回执链接：<a :href="TimeForm.jobUrl" target="_blank" rel="noopener noreferrer">{{ TimeForm.jobUrl }}</a></p>
              <p>
                <!-- <img :src="TimeForm.jobImage" style="width: 80px; height: 80px; margin: 10px auto;" alt=""> -->
                <ImageViews :images="[TimeForm.jobImage]" :size="120" />
              </p>
              <p v-if="TimeForm.feedbackTime">反馈日期：{{ TimeForm.feedbackTime }}</p>
              <!-- <div style="margin-top: 10px;" v-if="method === 'accept'">
                <el-button type="warning" icon="Edit" @click="handleEditFeedback">修改</el-button>
              </div> -->
            </el-card>
          </div>
        </el-timeline-item>
        <el-timeline-item placement="top" type="primary"
          v-if="TimeForm.status === '待确认' || TimeForm.status === '已完成' || TimeForm.status === '已失败'">
          等待<span :class="method === 'send' ? 'zhuren' : 'keren'">
            派单方 <span v-if="!userStore.is_admin">{{ method === 'send' ? '（我）' : '（他）' }}</span>
          </span>确认
        </el-timeline-item>
        <el-timeline-item placement="top" v-if="method === 'send' && TimeForm.status === '待确认' && !userStore.is_admin">
          <el-button type="warning" @click="handleProblemFeedback">问题反馈</el-button>
          <el-button type="success" @click="handleConfirmOderFinish">确认完成</el-button>
        </el-timeline-item>
        <!-- 派单方已确认完成 -->
        <el-timeline-item center placement="top" type="primary" v-if="TimeForm.status === '已完成'">
          <span :class="method === 'send' ? 'zhuren' : 'keren'">
            派单方 <span v-if="!userStore.is_admin">{{ method === 'send' ? '（我）' : '（他）' }}</span>
          </span>
          已确认完成
        </el-timeline-item>
        <!-- 订单已失败 -->
        <el-timeline-item center placement="top" type="primary" v-if="TimeForm.status === '已失败'">
          <span style="color:#e45656">订单有异常：{{ TimeForm.reason }}</span>
        </el-timeline-item>
      </el-timeline>
    </el-drawer>
    <!-- 问题反馈 -->
    <el-dialog title="问题反馈" v-model="problemVisible" v-if="problemVisible" width="400px" :close-on-click-modal="false"
      :before-close="handleClose">
      <!-- 原因 -->
      <el-form :model="formSubmit" :rules="rules" ref="ruleForm">
        <el-form-item label="原因" prop="reason">
          <el-input v-model="formSubmit.reason" placeholder="请输入原因" :rows="4" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="default" @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleSubmit" :disabled="loadingBtn" :loading="loadingBtn">提交</el-button>
      </template>
    </el-dialog>
    <previewDialog :preview-form="previewForm" v-if="previewForm.show"></previewDialog>
  </div>
</template>

<script name="timeLine" setup>
import { confirmOrder } from "@/api/modules/order"
import { ElMessage } from "element-plus";
import { ref, computed, reactive } from "vue";
import previewDialog from "@/components/previewDialog/index.vue";
import { useRichText } from "@/hooks/useRichText";
import { useUserStore } from "@/stores/modules/user";
import ImageViews from "@/components/ImageViews/index.vue"
const userStore = useUserStore();
const richText = useRichText();
const props = defineProps({
  idx: {
    type: Number,
    default: 0
  },
  modelValue: {
    type: Boolean,
    default: false
  },
  method: {
    type: String,
    default: ""
  },
  TimeForm: {
    type: Object,
    default: {}
  }
});
// 文章预览弹窗属性
const previewForm = reactive({
  show: false,
  desc: "",
  title: ""
});
const handlePreview = () => {
  previewForm.show = true;
  previewForm.desc = props.TimeForm.material?.materialType == 1 ? richText.getRichText(props.TimeForm.material?.materialDoc?.richContent) : richText.getVideoText(props.TimeForm.material?.richContent);
  previewForm.title = props.TimeForm.material?.title;
}
const emit = defineEmits(["update:modelValue", "close", "editFeedback", "confirmOderFinish"]);

const handleConfirmOderFinish = () => {
  confirmOrder({ subOrderId: props.TimeForm.subOrderId, accept: true }).then(res => {
    ElMessage.success("订单完成")
    drawerVisible.value = false
    emit("close", true)
  }).catch(err => { })
}
const problemVisible = ref(false)
const handleClose = () => {
  problemVisible.value = false
  formSubmit.value.reasont = ""
}
const loadingBtn = ref(false)
const formSubmit = ref({
  reason: ""
})
const rules = {
  reason: [
    { required: true, message: "请输入原因", trigger: "blur" },
  ]
}
const ruleForm = ref(null);
const handleSubmit = () => {
  ruleForm.value.validate((valid) => {
    if (valid) {
      loadingBtn.value = true;
      confirmOrder({ subOrderId: props.TimeForm.subOrderId, accept: false, reason: formSubmit.value.reason }).then(res => {
        ElMessage.success("问题反馈成功")
        handleClose();
        drawerVisible.value = false
        emit("close", true)
        setTimeout(() => {
          loadingBtn.value = false;
        }, 500);
      }).catch(err => {
        setTimeout(() => {
          loadingBtn.value = false;
        }, 500);
      })
    } else {
      ElMessage.error("请检查输入项")
    }
  })
}
const handleProblemFeedback = () => {
  // 问题反馈
  problemVisible.value = true
}
const drawerVisible = computed({
  get: () => props.modelValue,
  set: value => emit("update:modelValue", value)
});
</script>

<style lang="scss" scoped>
.timeLine {
  .zhuren {
    color: #409eff;
    font-weight: 600;
  }

  .keren {
    color: #e6a23c;
    font-weight: 600;
  }
}

.flex_header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  span {
    font-size: 12px;
    color: #999;
    margin-right: 20px;
  }
}
</style>