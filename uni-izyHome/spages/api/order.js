import request from '@/utils/request'
/**
 * 接口名称: 卖家同意退款（自动执行退款处理）
 * 路径: POST /api/store/order/approveRefund/{orderId}
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
 *   orderId: integer,
 *   orderNum: string, // 订单号
 *   buyerUserId: integer, // 买家用户ID
 *   sellerUserId: integer, // 卖家用户ID
 *   shopId: integer, // 店铺ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
 *   pointsPrice: integer, // 积分单价
 *   cashPrice: number, // 现金单价
 *   count: integer, // 购买数量
 *   totalPoints: integer, // 积分总额
 *   totalAmount: number, // 现金总额
 *   status: integer, // 订单状态
 *   redeemCode: string, // 核销码
 *   payTime: string, // 支付时间
 *   verifyTime: string, // 核销时间
 *   refundTime: string, // 退款时间
 *   refundReason: string, // 退款原因
 *   commentId: integer, // 评价ID
 *   payParamsJson: string, // 微信支付参数JSON
 *   paymentStatus: string, // 财务支付状态
 *   transactionId: string, // 微信支付流水号
 *   refundId: string, // 微信退款流水号
 *   expireTime: string, // 订单超时关闭时间
 *   statusHistories: Array<{
 *       status: integer, // 变更后的订单状态
 *       paymentStatus: string, // 变更后的支付状态
 *       operator: string, // 操作人
 *       remark: string, // 变更事由
 *       createTime: string, // 操作时间
 *     }>, // 订单状态变更履历
 *   id: integer,
 * }
 */
export function approveRefund(orderId) {
    return request({
        url: `/api/store/order/approveRefund/${orderId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 买家取消订单（仅限待支付状态）
 * 路径: POST /api/store/order/cancel/{orderId}
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
 *   orderId: integer,
 *   orderNum: string, // 订单号
 *   buyerUserId: integer, // 买家用户ID
 *   sellerUserId: integer, // 卖家用户ID
 *   shopId: integer, // 店铺ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
 *   pointsPrice: integer, // 积分单价
 *   cashPrice: number, // 现金单价
 *   count: integer, // 购买数量
 *   totalPoints: integer, // 积分总额
 *   totalAmount: number, // 现金总额
 *   status: integer, // 订单状态
 *   redeemCode: string, // 核销码
 *   payTime: string, // 支付时间
 *   verifyTime: string, // 核销时间
 *   refundTime: string, // 退款时间
 *   refundReason: string, // 退款原因
 *   commentId: integer, // 评价ID
 *   payParamsJson: string, // 微信支付参数JSON
 *   paymentStatus: string, // 财务支付状态
 *   transactionId: string, // 微信支付流水号
 *   refundId: string, // 微信退款流水号
 *   expireTime: string, // 订单超时关闭时间
 *   statusHistories: Array<{
 *       status: integer, // 变更后的订单状态
 *       paymentStatus: string, // 变更后的支付状态
 *       operator: string, // 操作人
 *       remark: string, // 变更事由
 *       createTime: string, // 操作时间
 *     }>, // 订单状态变更履历
 *   id: integer,
 * }
 */
export function cancel(orderId) {
    return request({
        url: `/api/store/order/cancel/${orderId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 创建订单（积分兑换同步完成，现金购买返回微信支付参数）
 * 路径: POST /api/store/order/create
 * 请求参数 (Request):
 * {
 *   goodsId: integer, // 商品ID
 *   count: integer, // 购买数量（默认1）
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
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
 *   orderId: integer, // 订单ID
 *   orderNum: string, // 订单号
 *   totalAmount: number, // 现金总额
 *   totalPoints: integer, // 积分总额
 *   payType: string, // 支付类型描述
 *   payParams: Object, // 微信支付参数（createOrderV3返回对象，积分兑换时为null）
 * }
 */
export function create(data) {
    return request({
        url: `/api/store/order/create`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 订单详情
 * 路径: GET /api/store/order/detail/{orderId}
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
 *   orderId: integer,
 *   orderNum: string, // 订单号
 *   buyerUserId: integer, // 买家用户ID
 *   sellerUserId: integer, // 卖家用户ID
 *   shopId: integer, // 店铺ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
 *   pointsPrice: integer, // 积分单价
 *   cashPrice: number, // 现金单价
 *   count: integer, // 购买数量
 *   totalPoints: integer, // 积分总额
 *   totalAmount: number, // 现金总额
 *   status: integer, // 订单状态
 *   redeemCode: string, // 核销码
 *   payTime: string, // 支付时间
 *   verifyTime: string, // 核销时间
 *   refundTime: string, // 退款时间
 *   refundReason: string, // 退款原因
 *   commentId: integer, // 评价ID
 *   payParamsJson: string, // 微信支付参数JSON
 *   paymentStatus: string, // 财务支付状态
 *   transactionId: string, // 微信支付流水号
 *   refundId: string, // 微信退款流水号
 *   expireTime: string, // 订单超时关闭时间
 *   statusHistories: Array<{
 *       status: integer, // 变更后的订单状态
 *       paymentStatus: string, // 变更后的支付状态
 *       operator: string, // 操作人
 *       remark: string, // 变更事由
 *       createTime: string, // 操作时间
 *     }>, // 订单状态变更履历
 *   id: integer,
 * }
 */
export function detail1(orderId) {
    return request({
        url: `/api/store/order/detail/${orderId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 分页查询订单（通过role区分买家/卖家视角）
 * 路径: POST /api/store/order/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 *   role: string, // 视角：buyer买家 seller卖家
 *   status: integer, // 订单状态码（对应OrderStatusEnum.code，不传则查全部）
 *   statusList: Array<integer>, // 多状态筛选（如卖家退款Tab传 [40,41,42,50]）
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
 *   pendingComment: boolean, // 买家专用-是否待评价：true查已完成但未评价的订单
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
 *   orderId: integer,
 *   orderNum: string, // 订单号
 *   buyerUserId: integer, // 买家用户ID
 *   sellerUserId: integer, // 卖家用户ID
 *   shopId: integer, // 店铺ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
 *   pointsPrice: integer, // 积分单价
 *   cashPrice: number, // 现金单价
 *   count: integer, // 购买数量
 *   totalPoints: integer, // 积分总额
 *   totalAmount: number, // 现金总额
 *   status: integer, // 订单状态
 *   redeemCode: string, // 核销码
 *   payTime: string, // 支付时间
 *   verifyTime: string, // 核销时间
 *   refundTime: string, // 退款时间
 *   refundReason: string, // 退款原因
 *   commentId: integer, // 评价ID
 *   payParamsJson: string, // 微信支付参数JSON
 *   paymentStatus: string, // 财务支付状态
 *   transactionId: string, // 微信支付流水号
 *   refundId: string, // 微信退款流水号
 *   expireTime: string, // 订单超时关闭时间
 *   statusHistories: Array<{
 *       status: integer, // 变更后的订单状态
 *       paymentStatus: string, // 变更后的支付状态
 *       operator: string, // 操作人
 *       remark: string, // 变更事由
 *       createTime: string, // 操作时间
 *     }>, // 订单状态变更履历
 *   id: integer,
 * }
 */
export function page(data) {
    return request({
        url: `/api/store/order/page`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 买家获取核销码
 * 路径: GET /api/store/order/redeemCode/{orderId}
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
export function redeemCode(orderId) {
    return request({
        url: `/api/store/order/redeemCode/${orderId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 买家申请退款
 * 路径: POST /api/store/order/refund
 * 请求参数 (Request):
 * {
 *   orderId: integer, // 订单ID
 *   reason: string, // 退款原因
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
 *   orderId: integer,
 *   orderNum: string, // 订单号
 *   buyerUserId: integer, // 买家用户ID
 *   sellerUserId: integer, // 卖家用户ID
 *   shopId: integer, // 店铺ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
 *   pointsPrice: integer, // 积分单价
 *   cashPrice: number, // 现金单价
 *   count: integer, // 购买数量
 *   totalPoints: integer, // 积分总额
 *   totalAmount: number, // 现金总额
 *   status: integer, // 订单状态
 *   redeemCode: string, // 核销码
 *   payTime: string, // 支付时间
 *   verifyTime: string, // 核销时间
 *   refundTime: string, // 退款时间
 *   refundReason: string, // 退款原因
 *   commentId: integer, // 评价ID
 *   payParamsJson: string, // 微信支付参数JSON
 *   paymentStatus: string, // 财务支付状态
 *   transactionId: string, // 微信支付流水号
 *   refundId: string, // 微信退款流水号
 *   expireTime: string, // 订单超时关闭时间
 *   statusHistories: Array<{
 *       status: integer, // 变更后的订单状态
 *       paymentStatus: string, // 变更后的支付状态
 *       operator: string, // 操作人
 *       remark: string, // 变更事由
 *       createTime: string, // 操作时间
 *     }>, // 订单状态变更履历
 *   id: integer,
 * }
 */
export function refund(data) {
    return request({
        url: `/api/store/order/refund`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 获取退款相关状态码列表（供前端退款Tab筛选使用）
 * 路径: GET /api/store/order/refundStatusCodes
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
 * integer
 */
export function refundStatusCodes() {
    return request({
        url: `/api/store/order/refundStatusCodes`,
        method: 'get'
    })
}

/**
 * 接口名称: 卖家拒绝退款（订单恢复待核销）
 * 路径: POST /api/store/order/rejectRefund/{orderId}
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
 *   orderId: integer,
 *   orderNum: string, // 订单号
 *   buyerUserId: integer, // 买家用户ID
 *   sellerUserId: integer, // 卖家用户ID
 *   shopId: integer, // 店铺ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
 *   pointsPrice: integer, // 积分单价
 *   cashPrice: number, // 现金单价
 *   count: integer, // 购买数量
 *   totalPoints: integer, // 积分总额
 *   totalAmount: number, // 现金总额
 *   status: integer, // 订单状态
 *   redeemCode: string, // 核销码
 *   payTime: string, // 支付时间
 *   verifyTime: string, // 核销时间
 *   refundTime: string, // 退款时间
 *   refundReason: string, // 退款原因
 *   commentId: integer, // 评价ID
 *   payParamsJson: string, // 微信支付参数JSON
 *   paymentStatus: string, // 财务支付状态
 *   transactionId: string, // 微信支付流水号
 *   refundId: string, // 微信退款流水号
 *   expireTime: string, // 订单超时关闭时间
 *   statusHistories: Array<{
 *       status: integer, // 变更后的订单状态
 *       paymentStatus: string, // 变更后的支付状态
 *       operator: string, // 操作人
 *       remark: string, // 变更事由
 *       createTime: string, // 操作时间
 *     }>, // 订单状态变更履历
 *   id: integer,
 * }
 */
export function rejectRefund(orderId) {
    return request({
        url: `/api/store/order/rejectRefund/${orderId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 卖家核销订单
 * 路径: POST /api/store/order/verify
 * 请求参数 (Request):
 * {
 *   orderId: integer, // 订单ID
 *   redeemCode: string, // 核销码（8位数字）
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
 *   orderId: integer,
 *   orderNum: string, // 订单号
 *   buyerUserId: integer, // 买家用户ID
 *   sellerUserId: integer, // 卖家用户ID
 *   shopId: integer, // 店铺ID
 *   goodsId: integer, // 商品ID
 *   goodsTitle: string, // 商品标题
 *   goodsImage: string, // 商品图片
 *   payType: integer, // 支付类型：1积分兑换 2现金购买
 *   pointsPrice: integer, // 积分单价
 *   cashPrice: number, // 现金单价
 *   count: integer, // 购买数量
 *   totalPoints: integer, // 积分总额
 *   totalAmount: number, // 现金总额
 *   status: integer, // 订单状态
 *   redeemCode: string, // 核销码
 *   payTime: string, // 支付时间
 *   verifyTime: string, // 核销时间
 *   refundTime: string, // 退款时间
 *   refundReason: string, // 退款原因
 *   commentId: integer, // 评价ID
 *   payParamsJson: string, // 微信支付参数JSON
 *   paymentStatus: string, // 财务支付状态
 *   transactionId: string, // 微信支付流水号
 *   refundId: string, // 微信退款流水号
 *   expireTime: string, // 订单超时关闭时间
 *   statusHistories: Array<{
 *       status: integer, // 变更后的订单状态
 *       paymentStatus: string, // 变更后的支付状态
 *       operator: string, // 操作人
 *       remark: string, // 变更事由
 *       createTime: string, // 操作时间
 *     }>, // 订单状态变更履历
 *   id: integer,
 * }
 */
export function verify(data) {
    return request({
        url: `/api/store/order/verify`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 创建订单评价
 * 路径: POST /api/store/comment/create
 * 请求参数 (Request):
 * {
 *   orderId: integer, // 订单ID
 *   rating: integer, // 评分（1-5星）
 *   content: string, // 评价内容
 *   images: Array<string>, // 评价图片URL列表
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
 *   orderId: integer, // 订单ID
 *   goodsId: integer, // 商品ID
 *   userId: integer, // 评价用户ID
 *   userName: string, // 用户昵称
 *   userAvatar: string, // 用户头像
 *   rating: integer, // 评分1-5
 *   content: string, // 评价内容
 *   images: Array<string>, // 评价图片URL列表
 *   id: integer,
 * }
 */
export function create2(data) {
    return request({
        url: `/api/store/comment/create`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 商品评价列表（分页）
 * 路径: GET /api/store/comment/goodsPage/{goodsId}
 * 请求参数 (Request):
 * {
 *   page: integer, // 
 *   size: integer, // 
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
 *   orderId: integer, // 订单ID
 *   goodsId: integer, // 商品ID
 *   userId: integer, // 评价用户ID
 *   userName: string, // 用户昵称
 *   userAvatar: string, // 用户头像
 *   rating: integer, // 评分1-5
 *   content: string, // 评价内容
 *   images: Array<string>, // 评价图片URL列表
 *   id: integer,
 * }
 */
export function goodsPage(goodsId, data) {
    return request({
        url: `/api/store/comment/goodsPage/${goodsId}`,
        method: 'get',
        data: data || {}
    })
}

