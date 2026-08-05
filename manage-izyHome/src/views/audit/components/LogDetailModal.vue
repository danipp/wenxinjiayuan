<template>
    <el-dialog v-model="visible" title="日志详情" width="750px" destroy-on-close>
        <el-descriptions :column="2" border>
            <el-descriptions-item label="操作人ID">{{ logData.operatorId }}</el-descriptions-item>
            <el-descriptions-item label="操作人姓名">{{ logData.operatorName }}</el-descriptions-item>
            <el-descriptions-item label="请求模块">{{ logData.module }}</el-descriptions-item>
            <el-descriptions-item label="操作行为">{{ logData.action }}</el-descriptions-item>
            <el-descriptions-item label="请求URL" :span="2">{{ logData.requestUrl }}</el-descriptions-item>
            <el-descriptions-item label="Java方法" :span="2">
                <code>{{ logData.method }}</code>
            </el-descriptions-item>
            <el-descriptions-item label="IP地址">{{ logData.ip }}</el-descriptions-item>
            <el-descriptions-item label="耗时">{{ logData.costTime }} ms</el-descriptions-item>
            <el-descriptions-item label="User-Agent" :span="2">{{ logData.userAgent }}</el-descriptions-item>
        </el-descriptions>

        <div class="json-container">
            <h4>请求参数 (Request Params)</h4>
            <pre class="code-block">{{ formatJson(logData.requestParams) }}</pre>

            <h4>响应结果 (Response Result)</h4>
            <pre class="code-block">{{ formatJson(logData.responseResult) }}</pre>

            <template v-if="logData.errorMessage">
                <h4 style="color: #F56C6C;">异常堆栈 (Error Message)</h4>
                <pre class="code-block error">{{ logData.errorMessage }}</pre>
            </template>
        </div>
    </el-dialog>
</template>

<script setup>
import { ref } from 'vue'

const visible = ref(false)
const logData = ref({})

const open = (row) => {
    logData.value = row
    visible.value = true
}

// 格式化 JSON 字符串
const formatJson = (jsonStr) => {
    if (!jsonStr) return '无'
    try {
        return JSON.stringify(JSON.parse(jsonStr), null, 2)
    } catch (e) {
        return jsonStr
    }
}

defineExpose({ open })
</script>

<style scoped>
.json-container {
    margin-top: 15px;
}

.json-container h4 {
    margin: 10px 0 5px 0;
    font-size: 14px;
    font-weight: bold;
}

.code-block {
    background-color: #f5f7fa;
    padding: 10px;
    border-radius: 4px;
    font-family: monospace;
    font-size: 12px;
    max-height: 200px;
    overflow-y: auto;
    white-space: pre-wrap;
    word-wrap: break-word;
}

.code-block.error {
    background-color: #fef0f0;
    color: #f56c6c;
}
</style>