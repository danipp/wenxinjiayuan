import request from '@/utils/request'
/**
 * 接口名称: 累计打卡数
 * 路径: GET /api/mine/checkin/count
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
 * integer
 */
export function count() {
    return request({
        url: `/api/mine/checkin/count`,
        method: 'get'
    })
}

/**
 * 接口名称: 打卡记录分页查询
 * 路径: POST /api/mine/checkin/page
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
 *   recordId: integer,
 *   userId: integer, // 用户ID
 *   frameNo: string, // 相框编号
 *   frameName: string, // 相框名称
 *   frameImage: string, // 相框图片
 *   location: string, // 打卡位置
 *   checkinTime: string, // 打卡时间
 *   status: integer, // 打卡状态：1成功
 *   id: integer,
 * }
 */
export function page4(data) {
    return request({
        url: `/api/mine/checkin/page`,
        method: 'post',
        data: data || {}
    })
}

