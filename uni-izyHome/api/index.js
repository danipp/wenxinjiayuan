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


/**
 * 接口名称: 获取启用的社区列表
 * 路径: GET /api/community/list
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
 * --- 成功返回社区列表 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   communityId: integer,
 *   name: string, // 社区名称
 *   address: string, // 社区地址
 *   longitude: number, // 经度
 *   latitude: number, // 纬度
 *   contactName: string, // 联系人
 *   contactPhone: string, // 联系电话
 *   description: string, // 社区简介
 *   logo: string, // 社区Logo URL
 *   status: integer, // 状态：1启用 2禁用
 *   sort: integer, // 排序权重
 *   id: integer,
 * }
 */
export function list3() {
  return request({
    url: `/api/community/list`,
    method: 'get'
  })
}

/**
 * 接口名称: 切换社区（C端用户）
 * 路径: POST /api/community/switch/{communityId}
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
 * --- 成功返回更新后的用户信息 ---
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
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function switchCommunity(communityId) {
  return request({
    url: `/api/community/switch/${communityId}`,
    method: 'post'
  })
}

