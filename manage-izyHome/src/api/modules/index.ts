import http from "@/api";
/**
 * 接口名称: 清理历史操作日志（仅限超级管理员）
 * 路径: DELETE /manage/api/auditLog/clear/{daysAgo}
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
 * string
 */
export const clearOldLogs = (daysAgo) => {
    return http.delete(`/manage/api/auditLog/clear/${daysAgo}`);
}

/**
 * 接口名称: 查询后台操作审计日志列表
 * 路径: POST /manage/api/auditLog/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 *   module: string, // 操作模块
 *   operatorName: string, // 操作人姓名
 *   status: integer, // 操作状态 (1:成功, 0:失败)
 *   startTime: string, // 开始时间
 *   endTime: string, // 结束时间
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
 *   id: integer,
 *   operatorId: integer, // 操作人用户ID
 *   operatorName: string, // 操作人姓名/账号
 *   module: string, // 操作模块（如：点位管理、广告管理）
 *   action: string, // 操作行为描述（如：修改WiFi密码、更新广告预算）
 *   method: string, // 执行的Java方法名
 *   requestUrl: string, // 请求URL
 *   requestMethod: string, // 请求方式 (GET/POST/PUT/DELETE)
 *   requestParams: string, // 请求参数 (JSON格式)
 *   responseResult: string, // 响应结果 (JSON格式)
 *   status: integer, // 操作状态 (1:成功, 0:失败)
 *   errorMessage: string, // 异常报错堆栈信息
 *   ip: string, // 客户端IP地址
 *   userAgent: string, // 客户端浏览器User-Agent
 *   costTime: integer, // 方法执行耗时(毫秒)
 * }
 */
export const queryLogPage = (data) => {
    return http.post(`/manage/api/auditLog/page`, data);
}

/**
 * 接口名称: 获取志愿者详情
 * 路径: GET /manage/api/volunteer/detail
 * 请求参数 (Request):
 * {
 *   userId: integer, // 
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
 *   role: integer, // 用户角色：1=居民 2=志愿者
 *   volunteerId: string, // 志愿者ID
 *   volunteerStatus: integer, // 志愿者状态：0=未激活 1=正常 2=停用
 *   id: integer,
 * }
 */
export const detail = (data) => {
    return http.get(`/manage/api/volunteer/detail`, data);
}

/**
 * 接口名称: 录入志愿者
 * 路径: POST /manage/api/volunteer/import
 * 请求参数 (Request):
 * {
 *   volunteerId: string, // 志愿者ID（唯一，管理员手动录入或第三方平台返回）
 *   nickName: string, // 志愿者姓名/昵称
 *   cellphone: string, // 手机号（可选，用于微信授权登录时自动关联志愿者身份）
 *   communityId: integer, // 所属社区ID（可选）
 *   communityName: string, // 所属社区名称（可选，冗余字段）
 *   userId: integer, // 志愿者ID（编辑时必传，新增时不传）
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
 *   role: integer, // 用户角色：1=居民 2=志愿者
 *   volunteerId: string, // 志愿者ID
 *   volunteerStatus: integer, // 志愿者状态：0=未激活 1=正常 2=停用
 *   id: integer,
 * }
 */
export const importVolunteer = (data) => {
    return http.post(`/manage/api/volunteer/import`, data);
}

/**
 * 接口名称: 分页查询志愿者列表
 * 路径: POST /manage/api/volunteer/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 *   volunteerStatus: integer, // 志愿者状态：0=未激活 1=正常 2=停用
 *   keyword: string, // 关键词（模糊搜索志愿者ID或昵称）
 *   communityId: integer, // 所属社区ID
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
 *   totalElements: integer,
 *   totalPages: integer,
 *   size: integer,
 *   content: Array<Object>,
 *   number: integer,
 *   sort: {
 *     empty: boolean,
 *     sorted: boolean,
 *     unsorted: boolean,
 *   },
 *   first: boolean,
 *   last: boolean,
 *   numberOfElements: integer,
 *   pageable: {
 *     offset: integer,
 *     sort: {
 *       empty: boolean,
 *       sorted: boolean,
 *       unsorted: boolean,
 *     },
 *     pageSize: integer,
 *     pageNumber: integer,
 *     paged: boolean,
 *     unpaged: boolean,
 *   },
 *   empty: boolean,
 * }
 */
export const page = (data) => {
    return http.post(`/manage/api/volunteer/page`, data);
}

/**
 * 接口名称: 切换志愿者状态（启用/停用）
 * 路径: POST /manage/api/volunteer/toggleStatus
 * 请求参数 (Request):
 * {
 *   userId: integer, // 
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
 * string
 */
export const toggleStatus = (data) => {
    return http.post(`/manage/api/volunteer/toggleStatus`, data);
}

/**
 * 接口名称: 编辑志愿者
 * 路径: POST /manage/api/volunteer/update
 * 请求参数 (Request):
 * {
 *   volunteerId: string, // 志愿者ID（唯一，管理员手动录入或第三方平台返回）
 *   nickName: string, // 志愿者姓名/昵称
 *   cellphone: string, // 手机号（可选，用于微信授权登录时自动关联志愿者身份）
 *   communityId: integer, // 所属社区ID（可选）
 *   communityName: string, // 所属社区名称（可选，冗余字段）
 *   userId: integer, // 志愿者ID（编辑时必传，新增时不传）
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
 *   role: integer, // 用户角色：1=居民 2=志愿者
 *   volunteerId: string, // 志愿者ID
 *   volunteerStatus: integer, // 志愿者状态：0=未激活 1=正常 2=停用
 *   id: integer,
 * }
 */
export const updateVolunteer = (data) => {
    return http.post(`/manage/api/volunteer/update`, data);
}

