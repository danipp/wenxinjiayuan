import request from '@/utils/request'
/**
 * 接口名称: 审核物资申领（管理员）
 * 路径: POST /api/assistance/claim/audit
 * 请求参数 (Request):
 * {
 *   claimId: integer, // 申领ID
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
export function audit2(data) {
    return request({
        url: `/api/assistance/claim/audit`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 发放物资（管理员）
 * 路径: POST /api/assistance/claim/distribute/{claimId}
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
export function distribute(claimId) {
    return request({
        url: `/api/assistance/claim/distribute/${claimId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 查询物资申领列表
 * 路径: POST /api/assistance/claim/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码（从0开始）
 *   pageSize: integer, // 每页条数
 *   userId: integer, // 按用户筛选
 *   goodsId: integer, // 按商品筛选
 *   status: string, // 按状态筛选
 *   role: string, // 视角：my我的 all全部
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
 * --- 成功返回物资申领分页 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   claimId: integer,
 *   userId: integer, // 申领用户ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   claimCount: integer, // 申领数量
 *   claimReason: string, // 申领原因
 *   contactName: string, // 联系人
 *   contactPhone: string, // 联系电话
 *   address: string, // 收货地址
 *   status: string, // 申领状态
 *   auditRemark: string, // 审核备注
 *   auditTime: string, // 审核时间
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function page10(data) {
    return request({
        url: `/api/assistance/claim/page`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 提交物资申领
 * 路径: POST /api/assistance/claim/submit
 * 请求参数 (Request):
 * {
 *   goodsId: integer, // 商品ID
 *   claimCount: integer, // 申领数量
 *   claimReason: string, // 申领原因
 *   contactName: string, // 联系人
 *   contactPhone: string, // 联系电话
 *   address: string, // 收货地址
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
 * --- 成功返回物资申领记录 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   claimId: integer,
 *   userId: integer, // 申领用户ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   claimCount: integer, // 申领数量
 *   claimReason: string, // 申领原因
 *   contactName: string, // 联系人
 *   contactPhone: string, // 联系电话
 *   address: string, // 收货地址
 *   status: string, // 申领状态
 *   auditRemark: string, // 审核备注
 *   auditTime: string, // 审核时间
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function submit2(data) {
    return request({
        url: `/api/assistance/claim/submit`,
        method: 'post',
        data: data || {}
    })
}

