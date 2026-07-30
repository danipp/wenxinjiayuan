import request from '../utils/request.js'
// 缴费通知单列表
export function getFeeList(data) {
  return request({
    url: '/v1/app/charge/list',
    method: "post",
    data: data || {},
  })
}