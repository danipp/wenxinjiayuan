import request from '../utils/request.js'
/**
 * 接口名称: 创建打卡记录
 * 路径: POST /api/mine/checkin/create
 * 请求参数 (Request):
 * {
 *   frameNo: string, // 相框编号
 *   frameName: string, // 相框名称
 *   frameImage: string, // 相框图片URL
 *   location: string, // 打卡位置
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
export function create4(data) {
  return request({
    url: `/api/mine/checkin/create`,
    method: 'post',
    data: data || {}
  })
}


