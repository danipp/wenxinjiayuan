import request from '../utils/request.js'

export function loginByCode(code) {
    return request({
        url: `/api/mini/loginByCode/${code}`,
        method: "get",
        data: {},
    })
}

// /api/mini/auth
export function loginByAuth(data) {
    return request({
        url: `/api/mini/auth`,
        method: "post",
        data: data || {},
    })
}
/**
 * 接口名称: 更新用户资料（头像/昵称/描述）
 * 路径: POST /api/mini/updateProfile
 * 请求参数 (Request):
 * {
 *   nickName: string, // 昵称
 *   avatar: string, // 头像URL
 *   description: string, // 个人描述
 * }
 *
 * 返回参数 (Response):
 * --- 响应 200 ---
 * {
 *   code: string, // 状态码 {00000：成功 | A0401：未登录 | A0403：账号冻结 | B0001 系统执行出错，错误信息msg }
 *   data: Object, // 数据
 *   msg: string, // 成功/失败消息
 *   total: integer,
 * }
 *
 * --- 成功信息 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   userId: integer,
 *   nickName: string,
 *   passWord: string,
 *   cellphone: string,
 *   avatar: string,
 *   description: string,
 *   openId: string,
 *   unionId: string,
 *   developer: boolean, // 是否开发者
 *   developerId: integer, // 开发者Id
 *   owner: boolean, // 是否商户
 *   ownerId: integer, // 商户Id
 *   agent: boolean, // 是否代理
 *   agentId: integer, // 代理Id
 *   type: integer, // 1微信 2支付宝
 *   extParams: string, // 冗余参数
 *   id: integer,
 * }
 */
export function updateProfile(data) {
    return request({
        url: `/api/mini/updateProfile`,
        method: 'post',
        data: data || {}
    })
}



