import request from '@/utils/request'
/**
 * 接口名称: 审核捐赠申请（管理员）
 * 路径: POST /api/assistance/donation/audit
 * 请求参数 (Request):
 * {
 *   donationId: integer, // 捐赠申请ID
 *   approved: boolean, // 是否通过
 *   auditRemark: string, // 审核备注
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
 */
export function audit1(data) {
    return request({
        url: `/api/assistance/donation/audit`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 查询捐赠申请列表
 * 路径: POST /api/assistance/donation/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码（从0开始）
 *   pageSize: integer, // 每页条数
 *   userId: integer, // 按用户筛选
 *   donationType: string, // 按捐赠类型筛选：money资金 goods物资
 *   status: string, // 按状态筛选
 *   role: string, // 视角：my我的 apply申请列表
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
 * --- 成功返回捐赠申请分页 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   donationId: integer,
 *   userId: integer, // 申请用户ID
 *   userType: string, // 申请者类型
 *   enterpriseId: integer, // 关联企业ID
 *   donationType: string, // 捐赠类型
 *   amount: number, // 捐赠金额
 *   goodsName: string, // 物资名称
 *   goodsQuantity: integer, // 物资数量
 *   goodsValue: number, // 物资估值
 *   contactName: string, // 联系人姓名
 *   contactPhone: string, // 联系电话
 *   remark: string, // 备注说明
 *   status: string, // 审核状态
 *   auditRemark: string, // 审核备注
 *   auditTime: string, // 审核时间
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function page9(data) {
    return request({
        url: `/api/assistance/donation/page`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 捐赠排行
 * 路径: GET /api/assistance/donation/ranking
 * 请求参数 (Request):
 * {
 *   limit: integer, // 
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
 * --- 成功返回捐赠排行列表 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   donationId: integer,
 *   userId: integer, // 申请用户ID
 *   userType: string, // 申请者类型
 *   enterpriseId: integer, // 关联企业ID
 *   donationType: string, // 捐赠类型
 *   amount: number, // 捐赠金额
 *   goodsName: string, // 物资名称
 *   goodsQuantity: integer, // 物资数量
 *   goodsValue: number, // 物资估值
 *   contactName: string, // 联系人姓名
 *   contactPhone: string, // 联系电话
 *   remark: string, // 备注说明
 *   status: string, // 审核状态
 *   auditRemark: string, // 审核备注
 *   auditTime: string, // 审核时间
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function ranking(data) {
    return request({
        url: `/api/assistance/donation/ranking`,
        method: 'get',
        data: data || {}
    })
}

/**
 * 接口名称: 提交捐赠申请
 * 路径: POST /api/assistance/donation/submit
 * 请求参数 (Request):
 * {
 *   userType: string, // 申请者类型：individual个人 enterprise企业
 *   enterpriseId: integer, // 关联企业ID（企业捐赠时填写）
 *   donationType: string, // 捐赠类型：money资金 goods物资
 *   amount: number, // 捐赠金额（资金捐赠时必填）
 *   goodsName: string, // 物资名称（物资捐赠时必填）
 *   goodsQuantity: integer, // 物资数量
 *   goodsValue: number, // 物资估值
 *   contactName: string, // 联系人姓名
 *   contactPhone: string, // 联系电话
 *   communityId: integer, // 所属社区ID（数据隔离用）
 *   remark: string, // 备注说明
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
 * --- 成功返回捐赠申请记录 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   donationId: integer,
 *   userId: integer, // 申请用户ID
 *   userType: string, // 申请者类型
 *   enterpriseId: integer, // 关联企业ID
 *   donationType: string, // 捐赠类型
 *   amount: number, // 捐赠金额
 *   goodsName: string, // 物资名称
 *   goodsQuantity: integer, // 物资数量
 *   goodsValue: number, // 物资估值
 *   contactName: string, // 联系人姓名
 *   contactPhone: string, // 联系电话
 *   remark: string, // 备注说明
 *   status: string, // 审核状态
 *   auditRemark: string, // 审核备注
 *   auditTime: string, // 审核时间
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function submit1(data) {
    return request({
        url: `/api/assistance/donation/submit`,
        method: 'post',
        data: data || {}
    })
}

