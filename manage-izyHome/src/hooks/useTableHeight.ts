import { ref, onMounted, onUnmounted, Ref,nextTick } from 'vue';

/**
 * 计算表格可用高度的 composable
 * @param containerRef 表格容器的 ref（必须是 DOM 元素）
 * @param bottomOffset 底部预留高度（如分页器高度）
 */
export function useTableHeight(
    containerRef: Ref<HTMLElement | null>,
    bottomOffset: number = 100 // 底部预留高度，可根据需求调整
) {
    const tableHeight = ref<number>(0);

    // 计算高度的核心函数
    const calculateHeight = () => {
        if (!containerRef.value) return;

        // 获取容器到顶部的距离
        const { top: containerTop } = containerRef.value.getBoundingClientRect(); 
        // 窗口高度
        const windowHeight = window.innerHeight;

        // 计算可用高度
        tableHeight.value = windowHeight - containerTop - bottomOffset;
        // 最小高度限制（避免窗口过小时表格消失）
        if (tableHeight.value < 200) {
            tableHeight.value = 200;
        }
    };

    // 初始化计算
    onMounted(() => {
        nextTick(() => {
            calculateHeight();
            window.addEventListener('resize', calculateHeight);
        })
    });

    // 移除监听
    onUnmounted(() => {
        window.removeEventListener('resize', calculateHeight);
    });

    return { tableHeight };
}