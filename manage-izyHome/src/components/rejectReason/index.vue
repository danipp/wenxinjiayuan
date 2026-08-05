<template>
    <div class="addEditMedia">
        <el-dialog :title="title" width="500px" v-model="visible" :close-on-click-modal="false"
            :before-close="handleClose" :append-to-body="true" custom-class="addEditMediaDialog">
            <el-form :model="form" :rules="rules" label-width="120px" ref="ruleForm">
                <el-form-item prop="rejectReason" label="拒绝原因">
                    <el-input v-model="form.rejectReason" placeholder="请输入拒绝原因" type="textarea" :rows="4"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="handleClose">关闭</el-button>
                <el-button type="primary" @click="handleSubmit">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup name="addEditMedia">
import { ElMessage } from "element-plus";
import { ref, computed, reactive } from "vue";
const props = defineProps({
    idx: {
        type: Number,
        default: 0
    },
    modelValue: {
        type: Boolean,
        default: false
    },
    idxProp: {
        type: String,
        default: "id"
    },
    api: {
        type: Function,
        default: () => { }
    },
    title: {
        type: String,
        default: "拒绝原因"
    }
});
const emit = defineEmits(["update:modelValue", "close"]);
const visible = computed({
    get: () => props.modelValue,
    set: value => emit("update:modelValue", value)
});
const form = reactive({});
const rules = {
    rejectReason: [
        { required: true, message: "请输入拒绝原因", trigger: "blur" }
    ]
};
const handleClose = () => {
    visible.value = false;
};
const ruleForm = ref(null);
const handleSubmit = () => {
    ruleForm.value.validate(valid => {
        if (valid) {
            console.log("submit", form);
            props.api({ ...form, [props.idxProp]: props.idx }).then(res => {
                ElMessage.success("提交成功");
                handleClose();
                // 保存数据
                emit("close", true);
            }).catch(err => { })

        } else {
            console.log("error submit", form);
            return false;
        }
    });
};

</script>

<style lang="scss" scoped>
.addEditMedia {
    :deep(.editor-content) {
        height: 400px !important;
    }
}
</style>