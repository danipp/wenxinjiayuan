import request from '@/utils/request'
/**
 * 接口名称: 接单
 * 路径: POST /api/mine/demand/accept/{demandId}
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
 *   demandId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   helperUserId: integer, // 帮忙者用户ID
 *   title: string, // 需求标题
 *   content: string, // 需求内容
 *   location: string, // 服务地点
 *   serviceTime: string, // 服务时间
 *   status: integer, // 状态：1待帮忙 2已接单 3待评价 4已完成 5已过期
 *   requirement: string, // 需求类型
 *   memberName: string, // 服务对象姓名
 *   memberPhone: string, // 服务对象电话
 *   memberAddress: string, // 服务对象地址
 *   memberDetailAddress: string, // 服务对象详细门牌号
 *   remark: string, // 备注
 *   role: integer, // 视角：1发布者视角 2帮忙者视角（当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角）
 *   rating: integer, // 评价评分：1-5
 *   evaluateContent: string, // 评价内容
 *   evaluateTime: string, // 评价时间
 *   id: integer,
 * }
 */
export function accept(demandId) {
    return request({
        url: `/api/mine/demand/accept/${demandId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 确认服务完成（已接单→待评价）
 * 路径: POST /api/mine/demand/complete/{demandId}
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
 *   demandId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   helperUserId: integer, // 帮忙者用户ID
 *   title: string, // 需求标题
 *   content: string, // 需求内容
 *   location: string, // 服务地点
 *   serviceTime: string, // 服务时间
 *   status: integer, // 状态：1待帮忙 2已接单 3待评价 4已完成 5已过期
 *   requirement: string, // 需求类型
 *   memberName: string, // 服务对象姓名
 *   memberPhone: string, // 服务对象电话
 *   memberAddress: string, // 服务对象地址
 *   memberDetailAddress: string, // 服务对象详细门牌号
 *   remark: string, // 备注
 *   role: integer, // 视角：1发布者视角 2帮忙者视角（当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角）
 *   rating: integer, // 评价评分：1-5
 *   evaluateContent: string, // 评价内容
 *   evaluateTime: string, // 评价时间
 *   id: integer,
 * }
 */
export function complete(demandId) {
    return request({
        url: `/api/mine/demand/complete/${demandId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 发布需求
 * 路径: POST /api/mine/demand/create
 * 请求参数 (Request):
 * {
 *   title: string, // 需求标题
 *   content: string, // 需求详细内容
 *   location: string, // 服务地点
 *   serviceTime: string, // 服务时间
 *   requirement: string, // 需求类型（如：代购、陪护、维修等）
 *   memberName: string, // 服务对象姓名
 *   memberPhone: string, // 服务对象电话
 *   memberAddress: string, // 服务对象地址
 *   memberDetailAddress: string, // 服务对象详细门牌号
 *   remark: string, // 备注（其他说明，300字以内）
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
 *   demandId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   helperUserId: integer, // 帮忙者用户ID
 *   title: string, // 需求标题
 *   content: string, // 需求内容
 *   location: string, // 服务地点
 *   serviceTime: string, // 服务时间
 *   status: integer, // 状态：1待帮忙 2已接单 3待评价 4已完成 5已过期
 *   requirement: string, // 需求类型
 *   memberName: string, // 服务对象姓名
 *   memberPhone: string, // 服务对象电话
 *   memberAddress: string, // 服务对象地址
 *   memberDetailAddress: string, // 服务对象详细门牌号
 *   remark: string, // 备注
 *   role: integer, // 视角：1发布者视角 2帮忙者视角（当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角）
 *   rating: integer, // 评价评分：1-5
 *   evaluateContent: string, // 评价内容
 *   evaluateTime: string, // 评价时间
 *   id: integer,
 * }
 */
export function create3(data) {
    return request({
        url: `/api/mine/demand/create`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 评价需求（待评价→已完成）
 * 路径: POST /api/mine/demand/evaluate
 * 请求参数 (Request):
 * {
 *   demandId: integer, // 需求ID
 *   rating: integer, // 评分（1-5）
 *   content: string, // 评价内容
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
 *   demandId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   helperUserId: integer, // 帮忙者用户ID
 *   title: string, // 需求标题
 *   content: string, // 需求内容
 *   location: string, // 服务地点
 *   serviceTime: string, // 服务时间
 *   status: integer, // 状态：1待帮忙 2已接单 3待评价 4已完成 5已过期
 *   requirement: string, // 需求类型
 *   memberName: string, // 服务对象姓名
 *   memberPhone: string, // 服务对象电话
 *   memberAddress: string, // 服务对象地址
 *   memberDetailAddress: string, // 服务对象详细门牌号
 *   remark: string, // 备注
 *   role: integer, // 视角：1发布者视角 2帮忙者视角（当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角）
 *   rating: integer, // 评价评分：1-5
 *   evaluateContent: string, // 评价内容
 *   evaluateTime: string, // 评价时间
 *   id: integer,
 * }
 */
export function evaluate(data) {
    return request({
        url: `/api/mine/demand/evaluate`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 需求/帮忙记录分页查询
 * 路径: POST /api/mine/demand/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 *   role: integer, // 视角：1发布者视角 2帮忙者视角
 *   status: string, // 状态筛选：all全部/pending待帮忙/helping已接单/toEvaluate待评价/completed已完成/expired已过期
 *   requirement: string, // 需求类型（如：代购、陪护、维修等）
 *   sort: string, // 排序方式：asc升序/desc降序（按服务时间排序）
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
 *   demandId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   helperUserId: integer, // 帮忙者用户ID
 *   title: string, // 需求标题
 *   content: string, // 需求内容
 *   location: string, // 服务地点
 *   serviceTime: string, // 服务时间
 *   status: integer, // 状态：1待帮忙 2已接单 3待评价 4已完成 5已过期
 *   requirement: string, // 需求类型
 *   memberName: string, // 服务对象姓名
 *   memberPhone: string, // 服务对象电话
 *   memberAddress: string, // 服务对象地址
 *   memberDetailAddress: string, // 服务对象详细门牌号
 *   remark: string, // 备注
 *   role: integer, // 视角：1发布者视角 2帮忙者视角（当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角）
 *   rating: integer, // 评价评分：1-5
 *   evaluateContent: string, // 评价内容
 *   evaluateTime: string, // 评价时间
 *   id: integer,
 * }
 */
export function page3(data) {
    return request({
        url: `/api/mine/demand/page`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 需求详情（公共视角）
 * 路径: GET /api/mine/demand/public/detail/{demandId}
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
 *   demandId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   helperUserId: integer, // 帮忙者用户ID
 *   title: string, // 需求标题
 *   content: string, // 需求内容
 *   location: string, // 服务地点
 *   serviceTime: string, // 服务时间
 *   status: integer, // 状态：1待帮忙 2已接单 3待评价 4已完成 5已过期
 *   requirement: string, // 需求类型
 *   memberName: string, // 服务对象姓名
 *   memberPhone: string, // 服务对象电话
 *   memberAddress: string, // 服务对象地址
 *   memberDetailAddress: string, // 服务对象详细门牌号
 *   remark: string, // 备注
 *   role: integer, // 视角：1发布者视角 2帮忙者视角（当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角）
 *   rating: integer, // 评价评分：1-5
 *   evaluateContent: string, // 评价内容
 *   evaluateTime: string, // 评价时间
 *   id: integer,
 * }
 */
export function publicDetail(demandId) {
    return request({
        url: `/api/mine/demand/public/detail/${demandId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 公共需求列表（好事广场）
 * 路径: POST /api/mine/demand/public/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 *   role: integer, // 视角：1发布者视角 2帮忙者视角
 *   status: string, // 状态筛选：all全部/pending待帮忙/helping已接单/toEvaluate待评价/completed已完成/expired已过期
 *   requirement: string, // 需求类型（如：代购、陪护、维修等）
 *   sort: string, // 排序方式：asc升序/desc降序（按服务时间排序）
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
 *   demandId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   helperUserId: integer, // 帮忙者用户ID
 *   title: string, // 需求标题
 *   content: string, // 需求内容
 *   location: string, // 服务地点
 *   serviceTime: string, // 服务时间
 *   status: integer, // 状态：1待帮忙 2已接单 3待评价 4已完成 5已过期
 *   requirement: string, // 需求类型
 *   memberName: string, // 服务对象姓名
 *   memberPhone: string, // 服务对象电话
 *   memberAddress: string, // 服务对象地址
 *   memberDetailAddress: string, // 服务对象详细门牌号
 *   remark: string, // 备注
 *   role: integer, // 视角：1发布者视角 2帮忙者视角（当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角）
 *   rating: integer, // 评价评分：1-5
 *   evaluateContent: string, // 评价内容
 *   evaluateTime: string, // 评价时间
 *   id: integer,
 * }
 */
export function publicPage(data) {
    return request({
        url: `/api/mine/demand/public/page`,
        method: 'post',
        data: data || {}
    })
}

