import request from '@/utils/request'
// 获取oss配置
export function getSts(data) {
    return request({
        url: '/api/oss/policyToken',
        method: 'POST',
        data: { purpose: data?data:'SD' }
    })
}