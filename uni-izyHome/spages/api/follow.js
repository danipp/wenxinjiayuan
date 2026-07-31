import request from '@/utils/request'
/**
 * 接口名称: 关注记录分页查询
 * 路径: POST /api/mine/follow/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 * }
 *
 * 返回参数 (Response):
 * --- 响应 200 ---
 * {
 *   code: string, // 状态码 {00000：成功 | A0401：未登录 | A0403：账号冻结 | B0001 系统执行出错，错误信息msg }
 *   data: {
 *      content:[],//数据列表
 *       last:false,//是否最后一页
 *      totalElements:0,//总条数
 *    }, // 数据

 *   msg: string, // 成功/失败消息
 *   total: integer,
 * }
 *
 * --- 成功信息 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   followId: integer,
 *   targetUserId: integer, // 被关注者用户ID
 *   followerUserId: integer, // 关注者用户ID
 *   followerName: string, // 关注者姓名
 *   followerPhone: string, // 关注者电话
 *   followerAvatar: string, // 关注者头像
 *   id: integer,
 * }
 */
export function page2(data) {
    return request({
        url: `/api/mine/follow/page`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 关注操作
 * 路径: POST /api/mine/follow/follow
 * 请求参数 (Request):
 * {
 *   targetUserId: integer, // 被关注者用户ID
 *   followerName: string, // 关注者姓名（可选，冗余存储）
 *   followerPhone: string, // 关注者电话（可选，冗余存储）
 *   followerAvatar: string, // 关注者头像URL（可选，冗余存储）
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
 *   followId: integer,
 *   targetUserId: integer, // 被关注者用户ID
 *   followerUserId: integer, // 关注者用户ID
 *   followerName: string, // 关注者姓名
 *   followerPhone: string, // 关注者电话
 *   followerAvatar: string, // 关注者头像
 *   id: integer,
 * }
 */
export function follow(data) {
    return request({
        url: `/api/mine/follow/follow`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 取消关注
 * 路径: DELETE /api/mine/follow/unfollow/{targetUserId}
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
export function unfollow(targetUserId) {
    return request({
        url: `/api/mine/follow/unfollow/${targetUserId}`,
        method: 'delete'
    })
}

