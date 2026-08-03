import request from '@/utils/request'

/**
 * 接口名称: 删除统计配置（管理员）
 * 路径: POST /api/assistance/stat/delete/{statId}
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
export function delete2(statId) {
    return request({
        url: `/api/assistance/stat/delete/${statId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 初始化默认统计项（管理员）
 * 路径: POST /api/assistance/stat/initDefault
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
export function initDefault() {
    return request({
        url: `/api/assistance/stat/initDefault`,
        method: 'post'
    })
}

/**
 * 接口名称: 获取统计列表
 * 路径: GET /api/assistance/stat/list
 * 请求参数 (Request):
 * {
 *   mode: string, // 
 *   communityId: integer, // 
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
 * --- 成功返回统计列表 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   statId: integer,
 *   statKey: string, // 统计项标识
 *   statLabel: string, // 统计项标签
 *   statValue: integer, // 统计值
 *   isCustom: boolean, // 是否自定义值
 *   displayOrder: integer, // 展示顺序
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function list4(data) {
    return request({
        url: `/api/assistance/stat/list`,
        method: 'get',
        data: data || {}
    })
}

/**
 * 接口名称: 保存统计配置（管理员）
 * 路径: POST /api/assistance/stat/save
 * 请求参数 (Request):
 * {
 *   statId: integer, // 统计项ID（编辑时传入）
 *   statKey: string, // 统计项标识
 *   statLabel: string, // 统计项标签
 *   statValue: integer, // 统计值
 *   isCustom: boolean, // 是否自定义值
 *   displayOrder: integer, // 展示顺序
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
 * --- 成功返回统计配置 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   statId: integer,
 *   statKey: string, // 统计项标识
 *   statLabel: string, // 统计项标签
 *   statValue: integer, // 统计值
 *   isCustom: boolean, // 是否自定义值
 *   displayOrder: integer, // 展示顺序
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function save3(data) {
    return request({
        url: `/api/assistance/stat/save`,
        method: 'post',
        data: data || {}
    })
}

