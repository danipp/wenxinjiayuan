import request from '../utils/request.js'
/**
 * 接口名称: 查询当前用户积分账户详情
 * 路径: GET /api/mine/points/detail
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
 *   userPointsId: integer,
 *   userId: integer, // 用户ID
 *   balance: integer, // 可用积分余额
 *   frozenBalance: integer, // 冻结积分
 *   totalEarned: integer, // 累计获得积分
 *   totalSpent: integer, // 累计消耗积分
 *   id: integer,
 * }
 */
export function getPointsDetail() {
    return request({
        url: `/api/mine/points/detail`,
        method: 'get'
    })
}

