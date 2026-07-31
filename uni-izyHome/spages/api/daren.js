import request from '@/utils/request'
/**
 * 接口名称: 达人排行榜（互助达人/活动达人）
 * 路径: POST /api/mine/leaderboard/list
 * 请求参数 (Request):
 * {
 *   type: integer, // 排行榜类型：1互助达人 2活动达人
 *   community: string, // 社区名称（可选筛选）
 *   limit: integer, // 限制条数，默认20
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
 *   userId: integer, // 用户ID
 *   nickName: string, // 昵称
 *   avatar: string, // 头像
 *   count: integer, // 统计次数（帮忙次数或参与活动次数）
 *   avgRating: number, // 平均评分（仅互助达人有值）
 * }
 */
export function list(data) {
    return request({
        url: `/api/mine/leaderboard/list`,
        method: 'post',
        data: data || {}
    })
}

