import request from '../utils/request.js'
// 二次授权，如果是审核模式就不用给手机号
export function loginByAuth(data) {
  return request({
    url: '/api/wx/mini/auth',
    method: "post",
    data: data || {},
  })
}
// 微信登录接口  - 根据code
export function loginByCode(data) {
  return request({
    url: `/api/wx/mini/loginByCode/${data}`,
    method: "get",
    data: {},
  })
}
// 微信登录接口  - 根据openid
export function loginByOpenId(data) {
  return request({
    url: `/api/wx/mini/loginByOpenId/${data}`,
    method: "get",
    data: {},
  })
}
