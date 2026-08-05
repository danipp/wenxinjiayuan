<template>
    <el-dialog v-model="visible" :title="mode === 'add' ? '录入志愿者' : '编辑志愿者'" width="500px" destroy-on-close
        @closed="reset">
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="110px">
            <el-form-item label="志愿者编号" prop="volunteerId">
                <el-input v-model="formData.volunteerId" placeholder="请输入志愿者ID" />
            </el-form-item>
            <el-form-item label="姓名/昵称" prop="nickName">
                <el-input v-model="formData.nickName" placeholder="请输入姓名或昵称" />
            </el-form-item>
            <el-form-item label="手机号" prop="cellphone">
                <el-input v-model="formData.cellphone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
            <el-form-item label="所属社区ID" prop="communityId">
                <el-input-number v-model="formData.communityId" placeholder="社区ID" :controls="false"
                    style="width: 100%" />
            </el-form-item>
            <el-form-item label="所属社区名称" prop="communityName">
                <el-input v-model="formData.communityName" placeholder="社区名称" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="visible = false">取消</el-button>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { importVolunteer, updateVolunteer } from "@/api/modules/index"

const emit = defineEmits(['success'])

const visible = ref(false)
const submitting = ref(false)
const mode = ref('add')
const formRef = ref(null)

const formData = reactive({
    userId: undefined,
    volunteerId: '',
    nickName: '',
    cellphone: '',
    communityId: undefined,
    communityName: ''
})

const rules = {
    volunteerId: [{ required: true, message: '请输入志愿者ID', trigger: 'blur' }],
    nickName: [{ required: true, message: '请输入姓名/昵称', trigger: 'blur' }]
}

const open = (type, row) => {
    mode.value = type
    visible.value = true
    if (type === 'edit' && row) {
        Object.assign(formData, {
            userId: row.userId,
            volunteerId: row.volunteerId,
            nickName: row.nickName,
            cellphone: row.cellphone,
            communityId: row.communityId,
            communityName: row.communityName
        })
    }
}

const reset = () => {
    formRef.value?.resetFields()
    Object.assign(formData, {
        userId: undefined,
        volunteerId: '',
        nickName: '',
        cellphone: '',
        communityId: undefined,
        communityName: ''
    })
}

const handleSubmit = async () => {
    await formRef.value.validate()
    submitting.value = true
    try {
        const api = mode.value === 'add' ? importVolunteer : updateVolunteer
        const res = await api(formData)
        if (res.code === '00000') {
            ElMessage.success(mode.value === 'add' ? '录入成功' : '编辑成功')
            visible.value = false
            emit('success')
        } else {
            ElMessage.error(res.msg || '保存失败')
        }
    } finally {
        submitting.value = false
    }
}

defineExpose({ open })
</script>