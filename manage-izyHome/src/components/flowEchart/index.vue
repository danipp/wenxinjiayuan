<template>
    <div class="workflow-container">
        <div class="workflow-canvas" ref="canvasContainer">
            <svg width="1200" height="600" viewBox="0 0 1200 600">
                <!-- 背景 -->
                <rect width="1200" height="600" fill="#f5f5f5" />

                <!-- 开始节点 -->
                <g class="start-node">
                    <rect x="50" y="240" width="80" height="40" rx="20" fill="#409eff" />
                    <text x="90" y="265" text-anchor="middle" fill="white" font-size="14">开始</text>
                </g>

                <!-- 待审核节点 -->
                <g class="pending-review-node">
                    <circle cx="220" cy="260" r="30" fill="white" stroke="#666" stroke-width="2" />
                    <text x="220" y="265" text-anchor="middle" font-size="12">待审核</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="240" cy="240" r="12" fill="#409eff" />
                    <text x="240" y="245" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderAuditing }}
                    </text>
                </g>

                <!-- 审核拒绝节点 -->
                <g class="rejected-node">
                    <circle cx="380" cy="140" r="30" fill="white" stroke="#f56c6c" stroke-width="2" />
                    <text x="380" y="135" text-anchor="middle" font-size="12" fill="#f56c6c">审核</text>
                    <text x="380" y="150" text-anchor="middle" font-size="12" fill="#f56c6c">拒绝</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="400" cy="120" r="12" fill="#f56c6c" />
                    <text x="400" y="125" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderReject }}
                    </text>
                </g>

                <!-- 待接单节点 -->
                <g class="pending-order-node">
                    <circle cx="380" cy="340" r="30" fill="white" stroke="#666" stroke-width="2" />
                    <text x="380" y="345" text-anchor="middle" font-size="12">待接单</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="400" cy="320" r="12" fill="#409eff" />
                    <text x="400" y="325" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderToAccept }}
                    </text>
                </g>

                <!-- 进行中节点 -->
                <g class="in-progress-node">
                    <circle cx="580" cy="280" r="30" fill="white" stroke="#666" stroke-width="2" />
                    <text x="580" y="285" text-anchor="middle" font-size="12">进行中</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="600" cy="260" r="12" fill="#409eff" />
                    <text x="600" y="265" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderProcessing }}
                    </text>
                </g>

                <!-- 待确认节点 -->
                <g class="pending-confirm-node">
                    <circle cx="750" cy="280" r="30" fill="white" stroke="#666" stroke-width="2" />
                    <text x="750" y="285" text-anchor="middle" font-size="12">待确认</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="770" cy="260" r="12" fill="#409eff" />
                    <text x="770" y="265" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderToConfirm }}
                    </text>
                </g>

                <!-- 已完成节点 -->
                <g class="completed-node">
                    <circle cx="920" cy="280" r="30" fill="white" stroke="#67c23a" stroke-width="2" />
                    <text x="920" y="285" text-anchor="middle" font-size="12" fill="#67c23a">已完成</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="940" cy="260" r="12" fill="#67c23a" />
                    <text x="940" y="265" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderFinished }}
                    </text>
                </g>

                <!-- 已失败节点 -->
                <g class="failed-node">
                    <circle cx="750" cy="120" r="30" fill="white" stroke="#f56c6c" stroke-width="2" />
                    <text x="750" y="125" text-anchor="middle" font-size="12" fill="#f56c6c">已失败</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="770" cy="100" r="12" fill="#f56c6c" />
                    <text x="770" y="105" text-anchor="middle" font-size="10" fill="white" font-weight="bold">{{
                        statusData.subOrderFailedOfSystem }}</text>
                </g>

                <!-- 已拒绝节点 -->
                <g class="order-rejected-node">
                    <circle cx="580" cy="450" r="30" fill="white" stroke="#f56c6c" stroke-width="2" />
                    <text x="580" y="455" text-anchor="middle" font-size="12" fill="#f56c6c">已拒绝</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="600" cy="430" r="12" fill="#f56c6c" />
                    <text x="600" y="435" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderMediaRefused }}
                    </text>
                </g>

                <!-- 已撤销节点 -->
                <g class="cancelled-node">
                    <circle cx="380" cy="520" r="30" fill="white" stroke="#f56c6c" stroke-width="2" />
                    <text x="380" y="525" text-anchor="middle" font-size="12" fill="#f56c6c">已撤销</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="400" cy="500" r="12" fill="#f56c6c" />
                    <text x="400" y="505" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderCancel }}
                    </text>
                </g>

                <!-- 已失败节点2 -->
                <g class="failed-node-2">
                    <circle cx="920" cy="460" r="30" fill="white" stroke="#f56c6c" stroke-width="2" />
                    <text x="920" y="465" text-anchor="middle" font-size="12" fill="#f56c6c">已失败</text>
                    <!-- 数据条数 - 右上角 -->
                    <circle cx="940" cy="440" r="12" fill="#f56c6c" />
                    <text x="940" y="445" text-anchor="middle" font-size="10" fill="white" font-weight="bold">
                        {{ statusData.subOrderFailed }}
                    </text>
                </g>

                <!-- 连接线 - 使用直角转弯 -->
                <!-- 开始 -> 待审核 -->
                <path d="M 130 260 L 190 260" stroke="#666" stroke-width="2" fill="none" marker-end="url(#arrowhead)" />
                <text x="160" y="250" text-anchor="middle" font-size="10" fill="#67c23a">广告主 派单</text>

                <!-- 待审核 -> 审核拒绝 -->
                <path d="M 220 230 L 220 140 L 350 140" stroke="#666" stroke-width="2" fill="none"
                    marker-end="url(#arrowhead)" />
                <text x="285" y="130" text-anchor="middle" font-size="10" fill="#f56c6c">高教平台审核 N</text>

                <!-- 待审核 -> 待接单 -->
                <path d="M 220 290 L 220 340 L 350 340" stroke="#666" stroke-width="2" fill="none"
                    marker-end="url(#arrowhead)" />
                <text x="285" y="330" text-anchor="middle" font-size="10" fill="#67c23a">高教平台审核 Y</text>

                <!-- 待接单 -> 进行中 -->
                <path d="M 410 340 L 480 340 L 480 280 L 550 280" stroke="#666" stroke-width="2" fill="none"
                    marker-end="url(#arrowhead)" />
                <text x="480" y="320" text-anchor="middle" font-size="10">媒体接单</text>

                <!-- 进行中 -> 待确认 -->
                <path d="M 610 280 L 680 280 L 680 280 L 720 280" stroke="#666" stroke-width="2" fill="none"
                    marker-end="url(#arrowhead)" />
                <text x="655" y="260" text-anchor="middle" font-size="10">媒体提交</text>
                <text x="655" y="275" text-anchor="middle" font-size="10">链接地址</text>

                <!-- 待确认 -> 已完成 -->
                <path d="M 780 280 L 890 280" stroke="#666" stroke-width="2" fill="none" marker-end="url(#arrowhead)" />
                <text x="835" y="270" text-anchor="middle" font-size="10" fill="#67c23a">广告主确认 Y</text>

                <!-- 待确认 -> 已失败 -->
                <path d="M 750 250 L 750 150" stroke="#666" stroke-width="2" fill="none" marker-end="url(#arrowhead)" />

                <!-- 待接单 -> 已拒绝 -->
                <path d="M 380 370 L 380 450 L 550 450" stroke="#666" stroke-width="2" fill="none"
                    marker-end="url(#arrowhead)" />
                <text x="465" y="440" text-anchor="middle" font-size="10" fill="#f56c6c">媒体拒单</text>

                <!-- 待审核 -> 已撤销 -->
                <path d="M 190 260 L 160 260 L 160 520 L 350 520" stroke="#666" stroke-width="2" fill="none"
                    marker-end="url(#arrowhead)" />
                <text x="255" y="510" text-anchor="middle" font-size="10" fill="#f56c6c">广告主 撤回</text>

                <!-- 待接单 -> 已撤销 -->
                <path d="M 350 340 L 320 340 L 320 520 L 350 520" stroke="#666" stroke-width="2" fill="none"
                    marker-end="url(#arrowhead)" />
                <text x="335" y="430" text-anchor="middle" font-size="10" fill="#f56c6c">广告主 撤回</text>

                <!-- 待确认 -> 已失败2 -->
                <path d="M 780 280 L 850 280 L 850 460 L 890 460" stroke="#666" stroke-width="2" fill="none"
                    marker-end="url(#arrowhead)" />
                <text x="850" y="370" text-anchor="middle" font-size="10" fill="#f56c23a">广告主确认 N</text>

                <!-- 箭头标记定义 -->
                <defs>
                    <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
                        <polygon points="0 0, 10 3.5, 0 7" fill="#666" />
                    </marker>
                </defs>

                <!-- 定时器1说明框 -->
                <g class="timer-note-1">
                    <ellipse cx="580" cy="60" rx="80" ry="35" fill="#fff2cc" stroke="#d6b656" stroke-width="1" />
                    <text x="580" y="50" text-anchor="middle" font-size="10">定时器1：自媒体方没</text>
                    <text x="580" y="62" text-anchor="middle" font-size="10">有及时上传作品，超时</text>
                    <text x="580" y="74" text-anchor="middle" font-size="10">自动标记失败</text>
                </g>

                <!-- 定时器2说明框 -->
                <g class="timer-note-2">
                    <ellipse cx="1050" cy="160" rx="80" ry="35" fill="#fff2cc" stroke="#d6b656" stroke-width="1" />
                    <text x="1050" y="150" text-anchor="middle" font-size="10">定时器2：广告主没及</text>
                    <text x="1050" y="162" text-anchor="middle" font-size="10">时确认作品，超时自</text>
                    <text x="1050" y="174" text-anchor="middle" font-size="10">动确认完成</text>
                </g>

                <!-- 定时器连接线 -->
                <path d="M 610 80 L 680 80 L 680 120 L 720 120" stroke="#d6b656" stroke-width="1" stroke-dasharray="5,5"
                    fill="none" />
                <path d="M 1020 180 L 980 180 L 980 260 L 950 260" stroke="#d6b656" stroke-width="1"
                    stroke-dasharray="5,5" fill="none" />
            </svg>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { adminSubOrderDashboard } from "@/api/modules/admin"
const canvasContainer = ref(null);

// 添加状态数据
const statusData = ref({
    subOrderAuditing: 0, // 待审核
    subOrderCancel: 0, // 审核拒绝
    subOrderToAccept: 0, // 待接单
    subOrderProcessing: 0, // 进行中
    subOrderToConfirm: 0, // 待确认
    subOrderFinished: 0, // 已完成
    subOrderFailed: 0, // 已失败
    subOrderMediaRefused: 0, // 已撤销
    subOrderReject: 0, // 已拒绝
    subOrderFailedOfSystem: 0 // 已失败2
});

// 更新状态数据的方法
const updateStatusData = (status, count) => {
    if (statusData.value[status] !== undefined) {
        statusData.value[status] = count;
    }
};

// 模拟数据更新（可以删除，仅用于演示）
const simulateDataUpdate = () => {
    const statuses = Object.keys(statusData.value);
    statuses.forEach(status => {
        statusData.value[status] = Math.floor(Math.random() * 200) + 50;
    });
};
const adminSubOrderDashboardApi = () => {
    adminSubOrderDashboard().then(res => {
        statusData.value = res.data;
    })
}
onMounted(() => {
    // 可以在这里添加一些交互逻辑
    // console.log("工作流程图已加载");
    adminSubOrderDashboardApi()
});

// 节点点击事件
const handleNodeClick = nodeType => {
    console.log(`点击了节点: ${nodeType}`);
    // 这里可以添加节点点击的处理逻辑
};
</script>

<style scoped>
.workflow-container {
    padding: 20px;
    background-color: #f5f5f5;
}

.workflow-header {
    text-align: center;
    margin-bottom: 20px;
}

.workflow-header h2 {
    color: #303133;
    font-size: 24px;
    font-weight: 600;
}

.workflow-canvas {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    padding: 20px;
    margin-bottom: 20px;
    overflow-x: auto;
    display: flex;
    justify-content: center;
}

.workflow-canvas svg {
    max-width: 100%;
    height: auto;
}

.status-legend {
    max-width: 600px;
    margin: 0 auto;
}

.legend-card {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.legend-items {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
}

.legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
}

.legend-color {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    border: 1px solid #dcdfe6;
}

/* 响应式设计 */
/* @media (max-width: 768px) {
    .workflow-canvas {
      padding: 10px;
    }
  
    .workflow-canvas svg {
      width: 100%;
      height: auto;
    }
  
    .legend-items {
      flex-direction: column;
      gap: 10px;
    }
  } */

/* 节点悬停效果 */
.workflow-canvas svg g:hover {
    cursor: pointer;
    opacity: 0.8;
}

/* 数据标识悬停效果 */
.workflow-canvas svg circle[fill="#409eff"]:hover,
.workflow-canvas svg circle[fill="#67c23a"]:hover,
.workflow-canvas svg circle[fill="#f56c6c"]:hover {
    opacity: 0.8;
    cursor: pointer;
}

/* 文字样式 */
.workflow-canvas svg text {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    user-select: none;
}
</style>