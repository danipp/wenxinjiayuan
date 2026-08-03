import request from '@/utils/request'
/**
 * 接口名称: 删除爱心企业（管理员）
 * 路径: POST /api/assistance/enterprise/delete/{enterpriseId}
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
export function delete3(enterpriseId) {
    return request({
        url: `/api/assistance/enterprise/delete/${enterpriseId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 查询爱心企业列表
 * 路径: POST /api/assistance/enterprise/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码（从0开始）
 *   pageSize: integer, // 每页条数
 *   status: string, // 按状态筛选
 *   name: string, // 按名称模糊搜索
 *   communityId: integer, // 所属社区ID（数据隔离用，前端传入当前选中社区ID）
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
 * --- 成功返回企业分页 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   enterpriseId: integer,
 *   name: string, // 企业名称
 *   logo: string, // 企业Logo URL
 *   description: string, // 企业简介
 *   contactName: string, // 联系人
 *   contactPhone: string, // 联系电话
 *   address: string, // 企业地址
 *   totalDonationAmount: number, // 累计捐赠金额
 *   totalDonationCount: integer, // 累计捐赠次数
 *   status: string, // 状态
 *   sort: integer, // 排序权重
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function page8(data) {
    return request({
        url: `/api/assistance/enterprise/page`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 保存/编辑爱心企业（管理员）
 * 路径: POST /api/assistance/enterprise/save
 * 请求参数 (Request):
 * {
 *   enterpriseId: integer, // 企业ID（编辑时传入）
 *   name: string, // 企业名称
 *   logo: string, // 企业Logo URL
 *   description: string, // 企业简介
 *   contactName: string, // 联系人
 *   contactPhone: string, // 联系电话
 *   address: string, // 企业地址
 *   communityId: integer, // 所属社区ID（数据隔离用）
 *   sort: integer, // 排序权重
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
 * --- 成功返回企业记录 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   enterpriseId: integer,
 *   name: string, // 企业名称
 *   logo: string, // 企业Logo URL
 *   description: string, // 企业简介
 *   contactName: string, // 联系人
 *   contactPhone: string, // 联系电话
 *   address: string, // 企业地址
 *   totalDonationAmount: number, // 累计捐赠金额
 *   totalDonationCount: integer, // 累计捐赠次数
 *   status: string, // 状态
 *   sort: integer, // 排序权重
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function save4(data) {
    return request({
        url: `/api/assistance/enterprise/save`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 爱心企业上下架切换（管理员）
 * 路径: POST /api/assistance/enterprise/toggleStatus/{enterpriseId}
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
export function toggleStatus4(enterpriseId) {
    return request({
        url: `/api/assistance/enterprise/toggleStatus/${enterpriseId}`,
        method: 'post'
    })
}

