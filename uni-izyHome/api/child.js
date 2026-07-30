import request from '../utils/request.js'
// 孩子信息列表
export function getChildList(data) {
  return request({
    url: '/v1/toll_stu/getStudentByParentId',
    method: "get",
    data: data || {},
  })
}