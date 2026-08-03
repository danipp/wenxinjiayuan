import request from "@/utils/request";
/**
 * 接口名称: 审核帮扶申请（管理员）
 * 路径: POST /api/assistance/apply/audit
 * 请求参数 (Request):
 * {
 *   applyId: integer, // 帮扶申请ID
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
export function audit3(data) {
    return request({
        url: `/api/assistance/apply/audit`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 查询帮扶申请列表
 * 路径: POST /api/assistance/apply/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码（从0开始）
 *   pageSize: integer, // 每页条数
 *   userId: integer, // 按用户筛选
 *   status: string, // 按状态筛选
 *   assistanceType: string, // 按帮扶类型筛选：living生活 medical医疗 education教育 employment就业
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
 * --- 成功返回帮扶申请分页 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   applyId: integer,
 *   userId: integer, // 申请用户ID
 *   applicantName: string, // 申请人姓名
 *   applicantPhone: string, // 联系电话
 *   idCard: string, // 身份证号
 *   address: string, // 居住地址
 *   familySituation: string, // 家庭情况说明
 *   assistanceType: string, // 帮扶类型
 *   difficultyDesc: string, // 困难描述
 *   desiredHelp: string, // 期望帮扶内容
 *   remark: string, // 备注
 *   status: string, // 审核状态
 *   auditRemark: string, // 审核备注
 *   auditTime: string, // 审核时间
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function page11(data) {
    return request({
        url: `/api/assistance/apply/page`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 提交帮扶申请
 * 路径: POST /api/assistance/apply/submit
 * 请求参数 (Request):
 * {
 *   applicantName: string, // 申请人姓名
 *   applicantPhone: string, // 联系电话
 *   idCard: string, // 身份证号
 *   address: string, // 居住地址
 *   familySituation: string, // 家庭情况说明
 *   assistanceType: string, // 帮扶类型：living生活 medical医疗 education教育 employment就业
 *   difficultyDesc: string, // 困难描述
 *   desiredHelp: string, // 期望帮扶内容
 *   communityId: integer, // 所属社区ID（数据隔离用）
 *   remark: string, // 备注
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
 * --- 成功返回帮扶申请记录 ---
 * {
 *   del_flag: boolean,
 *   createTime: string, // 创建时间
 *   updateTime: string, // 最后更新时间
 *   applyId: integer,
 *   userId: integer, // 申请用户ID
 *   applicantName: string, // 申请人姓名
 *   applicantPhone: string, // 联系电话
 *   idCard: string, // 身份证号
 *   address: string, // 居住地址
 *   familySituation: string, // 家庭情况说明
 *   assistanceType: string, // 帮扶类型
 *   difficultyDesc: string, // 困难描述
 *   desiredHelp: string, // 期望帮扶内容
 *   remark: string, // 备注
 *   status: string, // 审核状态
 *   auditRemark: string, // 审核备注
 *   auditTime: string, // 审核时间
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   id: integer,
 * }
 */
export function submit3(data) {
    return request({
        url: `/api/assistance/apply/submit`,
        method: 'post',
        data: data || {}
    })
}

