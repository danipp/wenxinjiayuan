<template>
    <el-drawer v-model="visible" title="志愿者详细信息" size="600px" destroy-on-close>
        <div v-loading="loading">
            <el-descriptions :column="2" border>
                <el-descriptions-item label="用户ID">{{ detailData.userId }}</el-descriptions-item>
                <el-descriptions-item label="志愿者ID">{{ detailData.volunteerId }}</el-descriptions-item>
                <el-descriptions-item label="姓名/昵称">{{ detailData.nickName }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ detailData.cellphone || '-' }}</el-descriptions-item>
                <el-descriptions-item label="所属社区">{{ detailData.communityName || '-' }} (ID: {{ detailData.communityId
                    || '-' }})</el-descriptions-item>
                <el-descriptions-item label="状态">
                    <el-tag :type="statusTagType(detailData.volunteerStatus)">
                        {{ statusText(detailData.volunteerStatus) }}
                    </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="账号类型">{{ detailData.type === 1 ? '微信' : detailData.type === 2 ? '支付宝' :
                    '未知' }}</el-descriptions-item>
                <el-descriptions-item label="用户角色">{{ detailData.role === 2 ? '志愿者' : '居民' }}</el-descriptions-item>
                <el-descriptions-item label="OpenID" :span="2">{{ detailData.openId || '-' }}</el-descriptions-item>
                <el-descriptions-item label="UnionID" :span="2">{{ detailData.unionId || '-' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
                <el-descriptions-item label="描述" :span="2">{{ detailData.description || '无' }}</el-descriptions-item>
            </el-descriptions>
        </div>
    </el-drawer>
</template>

<script setup>
import { ref } from 'vue'
import { detail as getVolunteerDetail } from "@/api/modules/index"
import { ElMessage } from 'element-plus'

const visible = ref(false)
const loading = ref(false)
const detailData = ref({})

const open = async (userId) => {
    visible.value = true
    loading.value = true
    try {
        const res = await getVolunteerDetail({ userId })
        if (res.code === '00000') {
            detailData.value = res.data || {}
        } else {
            ElMessage.error(res.msg || '获取详情失败')
        }
    } finally {
        loading.value = false
    }
}

const statusTagType = (status) => ({ 0: 'info', 1: 'success', 2: 'danger' }[status] || 'info')
const statusText = (status) => ({ 0: '未激活', 1: '正常', 2: '停用' }[status] || '未知')

defineExpose({ open })
</script>