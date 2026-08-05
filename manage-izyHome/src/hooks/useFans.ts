import { computed } from 'vue';

/**
 * 格式化粉丝数量的hooks
 * @param {Ref<number>} fansCountRef - 粉丝数量的响应式引用
 * @param {number} decimalPlaces - 保留的小数位数，默认1位
 * @returns {ComputedRef<string>} 格式化后的粉丝数量
 */
export function useFans() {
    // 格式化粉丝数量的计算属性
    const getFans = computed(() => {
        return function (fansCountRef) {
            const num = fansCountRef;

            // 处理非数字情况
            if (isNaN(num) || num < 0) return '0';

            // 定义单位和对应的数值范围
            const units = [
                { unit: '亿', value: 100000000 },
                { unit: 'W', value: 10000 },
                { unit: 'K', value: 1000 }
            ];

            // 遍历单位，找到合适的转换
            for (const { unit, value } of units) {
                if (num >= value) {
                    const formatted = (num / value).toFixed(1);
                    // 移除末尾的.0（如2.0W -> 2W）
                    return formatted.endsWith('.0')
                        ? `${formatted.slice(0, -2)}${unit}`
                        : `${formatted}${unit}`;
                }
            }

            // 小于1000的数字直接返回整数
            return Math.floor(num).toString();
        }

    });

    return {
        getFans
    };
}