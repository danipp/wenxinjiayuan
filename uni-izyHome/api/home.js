import request from '@/utils/request'
// 根据id查询
export function getList(data) {
    return request({
        url: '/api/ad/list',
        method: 'post',
        data
    })
}
// /api/ad/pos/{posId}  get请求
export function getPosList(posId) {
    return request({
        url: `/api/ad/pos/${posId}`,
        method: 'get'
    })
}
