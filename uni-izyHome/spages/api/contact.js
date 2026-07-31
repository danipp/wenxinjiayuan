import request from '@/utils/request'
/**
 * 接口名称: 删除联系人
 * 路径: DELETE /api/mine/contact/delete/{contactId}
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
export function delete1(contactId) {
    return request({
        url: `/api/mine/contact/delete/${contactId}`,
        method: 'delete'
    })
}

/**
 * 接口名称: 紧急联系人列表
 * 路径: GET /api/mine/contact/list
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
 *   contactId: integer,
 *   userId: integer, // 用户ID
 *   name: string, // 联系人姓名
 *   phone: string, // 联系人电话
 *   relation: string, // 关系
 *   id: integer,
 * }
 */
export function list2() {
    return request({
        url: `/api/mine/contact/list`,
        method: 'get'
    })
}

/**
 * 接口名称: 添加/编辑联系人
 * 路径: POST /api/mine/contact/save
 * 请求参数 (Request):
 * {
 *   contactId: integer, // 联系人ID（为空时新增，非空时编辑）
 *   name: string, // 联系人姓名
 *   phone: string, // 联系人电话
 *   relation: string, // 关系（如：父母、子女、配偶、朋友等）
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
 *   contactId: integer,
 *   userId: integer, // 用户ID
 *   name: string, // 联系人姓名
 *   phone: string, // 联系人电话
 *   relation: string, // 关系
 *   id: integer,
 * }
 */
export function save2(data) {
    return request({
        url: `/api/mine/contact/save`,
        method: 'post',
        data: data || {}
    })
}

