<template>
    <div class="app-container">
        <el-card shadow="never">
            <!-- 搜索表单 -->
            <el-form :model="queryParams" ref="queryFormRef" :inline="true">
                <el-form-item label="关键词" prop="keyword">
                    <el-input v-model="queryParams.keyword" placeholder="搜索志愿者ID或昵称" clearable
                        @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="状态" prop="volunteerStatus">
                    <el-select v-model="queryParams.volunteerStatus" placeholder="选择状态" clearable style="width: 150px">
                        <el-option label="未激活" :value="0" />
                        <el-option label="正常" :value="1" />
                        <el-option label="停用" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="社区ID" prop="communityId">
                    <el-input-number v-model="queryParams.communityId" placeholder="社区ID" :controls="false" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                    <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 操作按钮栏 -->
            <div class="toolbar">
                <el-button type="primary" icon="Plus" @click="handleOpenForm('add')">录入志愿者</el-button>
            </div>

            <!-- 数据表格 -->
            <el-table v-loading="loading" :data="tableData" border style="width: 100%; margin-top: 15px;">
                <el-table-column prop="volunteerId" label="志愿者ID" width="120" />
                <el-table-column prop="nickName" label="姓名/昵称" min-width="120" />
                <el-table-column prop="cellphone" label="手机号" width="130" />
                <el-table-column prop="communityName" label="所属社区" min-width="140" />
                <el-table-column prop="volunteerStatus" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="statusTagType(row.volunteerStatus)">
                            {{ statusText(row.volunteerStatus) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170" />
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
                        <el-button link type="primary" @click="handleOpenForm('edit', row)">编辑</el-button>
                        <el-popconfirm :title="`确定要${row.volunteerStatus === 1 ? '停用' : '启用'}该志愿者吗？`"
                            @confirm="handleToggleStatus(row)">
                            <template #reference>
                                <el-button link :type="row.volunteerStatus === 1 ? 'danger' : 'success'">
                                    {{ row.volunteerStatus === 1 ? '停用' : '启用' }}
                                </el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页组件 -->
            <div class="pagination-container">
                <el-pagination v-model:current-page="queryParams.pageNumber" v-model:page-size="queryParams.pageSize"
                    :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="getList" @current-change="getList" />
            </div>
        </el-card>

        <!-- 弹窗及抽屉子组件 -->
        <VolunteerFormModal ref="formModalRef" @success="getList" />
        <VolunteerDetailDrawer ref="detailDrawerRef" />
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { page as pageVolunteer, toggleStatus } from "@/api/modules/index"
import VolunteerFormModal from './components/VolunteerFormModal.vue'
import VolunteerDetailDrawer from './components/VolunteerDetailDrawer.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryFormRef = ref(null)
const formModalRef = ref(null)
const detailDrawerRef = ref(null)

const queryParams = reactive({
    pageNumber: 1,
    pageSize: 20,
    keyword: '',
    volunteerStatus: undefined,
    communityId: undefined
})

// 获取列表数据
const getList = async () => {
    loading.value = true
    try {
        const res = await pageVolunteer(queryParams)
        if (res.code === '00000') {
            // JPA 翻页结构适配
            tableData.value = res.data?.content || []
            total.value = res.data?.totalElements || 0
        } else {
            ElMessage.error(res.msg || '获取列表失败')
        }
    } finally {
        loading.value = false
    }
}

const handleQuery = () => {
    queryParams.pageNumber = 1
    getList()
}

const resetQuery = () => {
    queryFormRef.value?.resetFields()
    handleQuery()
}

// 切换志愿者状态
const handleToggleStatus = async (row) => {
    const res = await toggleStatus({ userId: row.userId })
    if (res.code === '00000') {
        ElMessage.success('操作成功')
        getList()
    } else {
        ElMessage.error(res.msg || '操作失败')
    }
}

// 打开表单弹窗 (新增 / 编辑)
const handleOpenForm = (type, row = null) => {
    formModalRef.value.open(type, row)
}

// 打开详情抽屉
const handleDetail = (row) => {
    detailDrawerRef.value.open(row.userId)
}

// 状态字典映射
const statusTagType = (status) => {
    const map = { 0: 'info', 1: 'success', 2: 'danger' }
    return map[status] || 'info'
}

const statusText = (status) => {
    const map = { 0: '未激活', 1: '正常', 2: '停用' }
    return map[status] || '未知'
}

onMounted(() => {
    getList()
})
</script>

<style scoped>
.app-container {
    padding: 20px;
}

.toolbar {
    margin-bottom: 10px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>