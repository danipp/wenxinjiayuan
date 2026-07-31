import request from '@/utils/request'
/**
 * 接口名称: 删除服务对象
 * 路径: DELETE /api/mine/serviceMember/delete/{memberId}
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
 */

export function deleteServiceMember(memberId) {
    return request({
        url: `/api/mine/serviceMember/delete/${memberId}`,
        method: 'delete'
    })
}

/**
 * 接口名称: 服务对象列表
 * 路径: GET /api/mine/serviceMember/list
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
 * --- 成功返回服务对象列表 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   memberId: integer,
 *   userId: integer, // 用户ID
 *   name: string, // 姓名
 *   phone: string, // 手机号
 *   address: string, // 地址
 *   detailAddress: string, // 详细门牌号
 *   remark: string, // 备注
 *   id: integer,
 * }
 */
export function list1() {
    return request({
        url: `/api/mine/serviceMember/list`,
        method: 'get'
    })
}

/**
 * 接口名称: 添加/编辑服务对象
 * 路径: POST /api/mine/serviceMember/save
 * 请求参数 (Request):
 * {
 *   memberId: integer, // 服务对象ID（为空时新增，非空时编辑）
 *   name: string, // 姓名
 *   phone: string, // 手机号
 *   address: string, // 地址
 *   detailAddress: string, // 详细门牌号
 *   remark: string, // 备注
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
 * --- 成功返回服务对象 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   memberId: integer,
 *   userId: integer, // 用户ID
 *   name: string, // 姓名
 *   phone: string, // 手机号
 *   address: string, // 地址
 *   detailAddress: string, // 详细门牌号
 *   remark: string, // 备注
 *   id: integer,
 * }
 */
export function save1(data) {
    return request({
        url: `/api/mine/serviceMember/save`,
        method: 'post',
        data: data || {}
    })
}

