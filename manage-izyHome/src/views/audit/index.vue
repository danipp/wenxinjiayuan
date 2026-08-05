<template>
    <div class="app-container">
        <el-card shadow="never">
            <!-- 过滤搜索栏 -->
            <el-form :model="queryParams" ref="queryFormRef" :inline="true">
                <el-form-item label="操作模块" prop="module">
                    <el-input v-model="queryParams.module" placeholder="例如：点位管理" clearable />
                </el-form-item>
                <el-form-item label="操作人" prop="operatorName">
                    <el-input v-model="queryParams.operatorName" placeholder="操作人姓名" clearable />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
                        <el-option label="成功" :value="1" />
                        <el-option label="失败" :value="0" />
                    </el-select>
                </el-form-item>
                <el-form-item label="操作时间">
                    <el-date-picker v-model="dateRange" type="datetimerange" range-separator="至"
                        start-placeholder="开始时间" end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
                    <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 工具栏 -->
            <div class="toolbar">
                <el-button type="danger" icon="Delete" plain @click="handleClear">清理历史日志</el-button>
            </div>

            <!-- 表格数据 -->
            <el-table v-loading="loading" :data="tableData" border style="width: 100%; margin-top: 15px;">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="module" label="模块" width="130" />
                <el-table-column prop="action" label="操作行为" min-width="160" show-overflow-tooltip />
                <el-table-column prop="operatorName" label="操作人" width="120" />
                <el-table-column prop="ip" label="IP地址" width="130" />
                <el-table-column prop="requestMethod" label="请求方式" width="100">
                    <template #default="{ row }">
                        <el-tag :type="methodTagType(row.requestMethod)">{{ row.requestMethod }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="90">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                            {{ row.status === 1 ? '成功' : '失败' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="costTime" label="耗时(ms)" width="100" />
                <el-table-column prop="createTime" label="操作时间" width="170" />
                <el-table-column label="操作" width="100" fixed="right">
                    <template #default="{ row }">
                        <el-button link type="primary" @click="handleOpenDetail(row)">详情</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination v-model:current-page="queryParams.pageNumber" v-model:page-size="queryParams.pageSize"
                    :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="getList" @current-change="getList" />
            </div>
        </el-card>

        <!-- 日志详情弹窗 -->
        <LogDetailModal ref="detailModalRef" />
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryLogPage, clearOldLogs } from "@/api/modules/index"
import LogDetailModal from './components/LogDetailModal.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryFormRef = ref(null)
const detailModalRef = ref(null)
const dateRange = ref([])

const queryParams = reactive({
    pageNumber: 1,
    pageSize: 20,
    module: '',
    operatorName: '',
    status: undefined,
    startTime: '',
    endTime: ''
})

const getList = async () => {
    loading.value = true
    if (dateRange.value && dateRange.value.length === 2) {
        queryParams.startTime = dateRange.value[0]
        queryParams.endTime = dateRange.value[1]
    } else {
        queryParams.startTime = ''
        queryParams.endTime = ''
    }

    try {
        const res = await queryLogPage(queryParams)
        if (res.code === '00000') {
            tableData.value = res.data?.content || res.data?.list || []
            total.value = res.total || res.data?.totalElements || 0
        } else {
            ElMessage.error(res.msg || '查询失败')
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
    dateRange.value = []
    queryFormRef.value?.resetFields()
    handleQuery()
}

// 清理历史日志（支持按天数清理）
const handleClear = () => {
    ElMessageBox.prompt('请输入清理多少天前的日志数据（如：30）', '清理提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^[1-9]\d*$/,
        inputErrorMessage: '请输入大于0的整数天数'
    }).then(async ({ value }) => {
        const res = await clearOldLogs(value)
        if (res.code === '00000') {
            ElMessage.success('清理成功')
            getList()
        } else {
            ElMessage.error(res.msg || '清理失败')
        }
    }).catch(() => { })
}

const handleOpenDetail = (row) => {
    detailModalRef.value.open(row)
}

const methodTagType = (method) => {
    const map = { GET: 'info', POST: 'success', PUT: 'warning', DELETE: 'danger' }
    return map[method] || ''
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