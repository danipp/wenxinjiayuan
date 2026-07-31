import request from '@/utils/request'
/**
 * 接口名称: 活动模板详情
 * 路径: GET /api/activity/template/detail/{templateId}
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
 * --- 成功返回模板详情 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   templateId: integer,
 *   title: string, // 模板标题
 *   content: string, // 活动内容
 *   location: string, // 活动地点（模板预设值，用户可在此基础上修改）
 *   startTime: string, // 活动开始时间（模板预设值，用户可在此基础上修改）
 *   endTime: string, // 活动结束时间（模板预设值，用户可在此基础上修改）
 *   community: string, // 所属社区（模板预设值，用户可在此基础上修改）
 *   collectPhone: boolean, // 是否收集手机号（模板预设值，用户可在此基础上修改）
 *   coverImage: string, // 封面图URL
 *   image: string, // 列表展示图URL
 *   tag: string, // 活动标签
 *   type: integer, // 活动类型
 *   maxLimit: integer, // 人数限制
 *   participants: string, // 参与人数展示文本
 *   usedCount: integer, // 使用次数
 *   totalJoined: integer, // 累计参与人数
 *   category: string, // 模板分类
 *   sort: integer, // 排序权重
 *   id: integer,
 * }
 */
export function detail4(templateId) {
    return request({
        url: `/api/activity/template/detail/${templateId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 活动模板列表（分页）
 * 路径: GET /api/activity/template/list
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 
 *   pageSize: integer, // 
 *   category: string, // 
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
 * --- 成功返回分页模板列表 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   templateId: integer,
 *   title: string, // 模板标题
 *   content: string, // 活动内容
 *   location: string, // 活动地点（模板预设值，用户可在此基础上修改）
 *   startTime: string, // 活动开始时间（模板预设值，用户可在此基础上修改）
 *   endTime: string, // 活动结束时间（模板预设值，用户可在此基础上修改）
 *   community: string, // 所属社区（模板预设值，用户可在此基础上修改）
 *   collectPhone: boolean, // 是否收集手机号（模板预设值，用户可在此基础上修改）
 *   coverImage: string, // 封面图URL
 *   image: string, // 列表展示图URL
 *   tag: string, // 活动标签
 *   type: integer, // 活动类型
 *   maxLimit: integer, // 人数限制
 *   participants: string, // 参与人数展示文本
 *   usedCount: integer, // 使用次数
 *   totalJoined: integer, // 累计参与人数
 *   category: string, // 模板分类
 *   sort: integer, // 排序权重
 *   id: integer,
 * }
 */
export function list3(data) {
    return request({
        url: `/api/activity/template/list`,
        method: 'get',
        data: data || {}
    })
}

/**
 * 接口名称: 使用模板创建活动（递增使用次数并返回模板数据）
 * 路径: POST /api/activity/template/use/{templateId}
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
 * --- 成功返回模板数据用于填充创建表单 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   templateId: integer,
 *   title: string, // 模板标题
 *   content: string, // 活动内容
 *   location: string, // 活动地点（模板预设值，用户可在此基础上修改）
 *   startTime: string, // 活动开始时间（模板预设值，用户可在此基础上修改）
 *   endTime: string, // 活动结束时间（模板预设值，用户可在此基础上修改）
 *   community: string, // 所属社区（模板预设值，用户可在此基础上修改）
 *   collectPhone: boolean, // 是否收集手机号（模板预设值，用户可在此基础上修改）
 *   coverImage: string, // 封面图URL
 *   image: string, // 列表展示图URL
 *   tag: string, // 活动标签
 *   type: integer, // 活动类型
 *   maxLimit: integer, // 人数限制
 *   participants: string, // 参与人数展示文本
 *   usedCount: integer, // 使用次数
 *   totalJoined: integer, // 累计参与人数
 *   category: string, // 模板分类
 *   sort: integer, // 排序权重
 *   id: integer,
 * }
 */
export function use(templateId) {
    return request({
        url: `/api/activity/template/use/${templateId}`,
        method: 'post'
    })
}

