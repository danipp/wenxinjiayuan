import http from "@/api";
/**
 * 接口名称: 账户基本信息
 * 路径: GET /manage/api/admin/basicInfo
 * 请求参数 (Request):
 * 无请求参数
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
 *   adminId: integer,
 *   nickName: string, // 昵称
 *   passWord: string, // 密码，使用rsa加密传给后台
 *   cellphone: string, // 手机
 *   avatar: string, // 头像url
 *   description: string,
 *   openId: string,
 *   id: integer,
 * }
 */
export const basicInfo1 = () => {
    return http.get(`/manage/api/admin/basicInfo`);
}

/**
 * 接口名称: 账号密码登录
 * 路径: POST /manage/api/admin/login
 * 请求参数 (Request):
 * {
 *   cellphone: string, // 手机号
 *   passWord: string, // 密码
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
 * string
 */
export const loginApi = (data) => {
    return http.post(`/manage/api/admin/login`, data);
}

