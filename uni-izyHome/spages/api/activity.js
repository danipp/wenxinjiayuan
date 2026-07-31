import request from '@/utils/request'

/**
 * 接口名称: 写活动评价
 * 路径: POST /api/activity/comment
 * 请求参数 (Request):
 * {
 *   activityId: integer, // 活动ID
 *   score: integer, // 评分（1-5星）
 *   emoji: string, // 评价表情（如emoji表情符号）
 *   statusText: string, // 状态标签文本（如"非常满意"、"值得推荐"）
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
 *   commentId: integer,
 *   activityId: integer, // 活动ID
 *   userId: integer, // 评价用户ID
 *   nickName: string, // 用户昵称
 *   avatar: string, // 用户头像
 *   score: integer, // 评分1-5
 *   emoji: string, // 评价表情
 *   statusText: string, // 状态标签文本
 *   content: string, // 评价内容
 *   id: integer,
 * }
 */
export function comment(data) {
    return request({
        url: `/api/activity/comment`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 活动评价列表（分页）
 * 路径: GET /api/activity/comments/{activityId}
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 
 *   pageSize: integer, // 
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
 *   commentId: integer,
 *   activityId: integer, // 活动ID
 *   userId: integer, // 评价用户ID
 *   nickName: string, // 用户昵称
 *   avatar: string, // 用户头像
 *   score: integer, // 评分1-5
 *   emoji: string, // 评价表情
 *   statusText: string, // 状态标签文本
 *   content: string, // 评价内容
 *   id: integer,
 * }
 */
export function comments(activityId, data) {
    return request({
        url: `/api/activity/comments/${activityId}`,
        method: 'get',
        data: data || {}
    })
}

/**
 * 接口名称: 创建活动
 * 路径: POST /api/activity/create
 * 请求参数 (Request):
 * {
 *   title: string, // 活动标题
 *   content: string, // 活动内容/描述
 *   location: string, // 活动地点
 *   startTime: string, // 活动开始时间
 *   endTime: string, // 活动结束时间
 *   community: string, // 所属社区
 *   maxLimit: integer, // 人数限制（0表示不限）
 *   collectPhone: boolean, // 是否收集手机号
 *   type: integer, // 活动类型：1线上活动 2线下活动 3招募活动
 *   coverImage: string, // 封面图URL
 *   tag: string, // 活动标签
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
 *   activityId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   title: string, // 活动标题
 *   content: string, // 活动内容/描述
 *   location: string, // 活动地点
 *   startTime: string, // 活动开始时间
 *   endTime: string, // 活动结束时间
 *   community: string, // 所属社区
 *   maxLimit: integer, // 人数限制（0表示不限）
 *   collectPhone: boolean, // 是否收集手机号
 *   type: integer, // 活动类型：1线上活动 2线下活动 3招募活动
 *   status: integer, // 活动状态：1未开始 2进行中 3已结束
 *   coverImage: string, // 封面图URL
 *   tag: string, // 活动标签
 *   participantCount: integer, // 参与人数
 *   signedUp: boolean, // 当前用户是否已报名
 *   authorName: string, // 发布者昵称
 *   authorAvatar: string, // 发布者头像
 *   statusText: string, // 状态文本
 *   id: integer,
 * }
 */
export function create5(data) {
    return request({
        url: `/api/activity/create`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 活动详情
 * 路径: GET /api/activity/detail/{activityId}
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
 *   activityId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   title: string, // 活动标题
 *   content: string, // 活动内容/描述
 *   location: string, // 活动地点
 *   startTime: string, // 活动开始时间
 *   endTime: string, // 活动结束时间
 *   community: string, // 所属社区
 *   maxLimit: integer, // 人数限制（0表示不限）
 *   collectPhone: boolean, // 是否收集手机号
 *   type: integer, // 活动类型：1线上活动 2线下活动 3招募活动
 *   status: integer, // 活动状态：1未开始 2进行中 3已结束
 *   coverImage: string, // 封面图URL
 *   tag: string, // 活动标签
 *   participantCount: integer, // 参与人数
 *   signedUp: boolean, // 当前用户是否已报名
 *   authorName: string, // 发布者昵称
 *   authorAvatar: string, // 发布者头像
 *   statusText: string, // 状态文本
 *   id: integer,
 * }
 */
export function detail5(activityId) {
    return request({
        url: `/api/activity/detail/${activityId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 已加入的邻居列表
 * 路径: GET /api/activity/joined/{activityId}
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
 *   signupId: integer,
 *   activityId: integer, // 活动ID
 *   userId: integer, // 报名用户ID
 *   nickName: string, // 用户昵称
 *   avatar: string, // 用户头像
 *   phone: string, // 联系手机号
 *   joinTime: string, // 加入时间
 *   id: integer,
 * }
 */
export function joined(activityId) {
    return request({
        url: `/api/activity/joined/${activityId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 我的活动列表（分页）
 * 路径: POST /api/activity/myActivities
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 *   role: string, // 角色筛选：published=我发布的, joined=我参与的
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
 *   activityId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   title: string, // 活动标题
 *   content: string, // 活动内容/描述
 *   location: string, // 活动地点
 *   startTime: string, // 活动开始时间
 *   endTime: string, // 活动结束时间
 *   community: string, // 所属社区
 *   maxLimit: integer, // 人数限制（0表示不限）
 *   collectPhone: boolean, // 是否收集手机号
 *   type: integer, // 活动类型：1线上活动 2线下活动 3招募活动
 *   status: integer, // 活动状态：1未开始 2进行中 3已结束
 *   coverImage: string, // 封面图URL
 *   tag: string, // 活动标签
 *   participantCount: integer, // 参与人数
 *   signedUp: boolean, // 当前用户是否已报名
 *   authorName: string, // 发布者昵称
 *   authorAvatar: string, // 发布者头像
 *   statusText: string, // 状态文本
 *   id: integer,
 * }
 */
export function myActivities(data) {
    return request({
        url: `/api/activity/myActivities`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 照片点赞/取消点赞
 * 路径: POST /api/activity/photo/like/{photoId}
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
 * boolean
 */
export function toggleLike(photoId) {
    return request({
        url: `/api/activity/photo/like/${photoId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 上传活动照片
 * 路径: POST /api/activity/photo/upload
 * 请求参数 (Request):
 * {
 *   activityId: integer, // 活动ID
 *   imageUrl: string, // 照片URL（OSS地址）
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
 *   photoId: integer,
 *   activityId: integer, // 活动ID
 *   userId: integer, // 上传用户ID
 *   nickName: string, // 上传用户昵称
 *   imageUrl: string, // 图片URL
 *   likes: integer, // 点赞数
 *   likedUserIds: Array<integer>, // 点赞用户ID列表
 *   isLiked: boolean, // 当前用户是否已点赞
 *   id: integer,
 * }
 */
export function uploadPhoto(data) {
    return request({
        url: `/api/activity/photo/upload`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 活动照片列表
 * 路径: GET /api/activity/photos/{activityId}
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
 *   photoId: integer,
 *   activityId: integer, // 活动ID
 *   userId: integer, // 上传用户ID
 *   nickName: string, // 上传用户昵称
 *   imageUrl: string, // 图片URL
 *   likes: integer, // 点赞数
 *   likedUserIds: Array<integer>, // 点赞用户ID列表
 *   isLiked: boolean, // 当前用户是否已点赞
 *   id: integer,
 * }
 */
export function photos(activityId) {
    return request({
        url: `/api/activity/photos/${activityId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 获取活动平均评分
 * 路径: GET /api/activity/score/{activityId}
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
 * number
 */
export function averageScore(activityId) {
    return request({
        url: `/api/activity/score/${activityId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 报名活动
 * 路径: POST /api/activity/signup/{activityId}
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
 *   signupId: integer,
 *   activityId: integer, // 活动ID
 *   userId: integer, // 报名用户ID
 *   nickName: string, // 用户昵称
 *   avatar: string, // 用户头像
 *   phone: string, // 联系手机号
 *   joinTime: string, // 加入时间
 *   id: integer,
 * }
 */
export function signup(activityId) {
    return request({
        url: `/api/activity/signup/${activityId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 活动广场列表（分页）
 * 路径: POST /api/activity/square
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 *   sort: string, // 排序方式：new按创建时间排序, use按参与人数排序, user按参与人数排序
 *   range: string, // 参与人数范围筛选：0-50/50-100/100+
 *   type: integer, // 活动类型筛选：1线上活动 2线下活动
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
 *   activityId: integer,
 *   publisherUserId: integer, // 发布者用户ID
 *   title: string, // 活动标题
 *   content: string, // 活动内容/描述
 *   location: string, // 活动地点
 *   startTime: string, // 活动开始时间
 *   endTime: string, // 活动结束时间
 *   community: string, // 所属社区
 *   maxLimit: integer, // 人数限制（0表示不限）
 *   collectPhone: boolean, // 是否收集手机号
 *   type: integer, // 活动类型：1线上活动 2线下活动 3招募活动
 *   status: integer, // 活动状态：1未开始 2进行中 3已结束
 *   coverImage: string, // 封面图URL
 *   tag: string, // 活动标签
 *   participantCount: integer, // 参与人数
 *   signedUp: boolean, // 当前用户是否已报名
 *   authorName: string, // 发布者昵称
 *   authorAvatar: string, // 发布者头像
 *   statusText: string, // 状态文本
 *   id: integer,
 * }
 */
export function square(data) {
    return request({
        url: `/api/activity/square`,
        method: 'post',
        data: data || {}
    })
}

