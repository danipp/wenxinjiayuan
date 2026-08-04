import request from '@/utils/request.js'

/**
 * 接口名称: 领取店铺优惠券
 * 路径: POST /api/special/coupon/claim/{couponId}
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
export function claimCoupon(couponId) {
    return request({
        url: `/api/special/coupon/claim/${couponId}`,
        method: 'post'
    })
}

/**
 * 接口名称: 创建店铺评价
 * 路径: POST /api/special/review/create
 * 请求参数 (Request):
 * {
 *   shopId: integer, // 店铺ID
 *   rating: integer, // 评分（1-5）
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
 *   reviewId: integer,
 *   shopId: integer, // 店铺ID
 *   userId: integer, // 评价用户ID
 *   userName: string, // 评价用户昵称
 *   userAvatar: string, // 评价用户头像
 *   rating: integer, // 评分（1-5）
 *   content: string, // 评价内容
 *   images: Array<string>, // 评价图片URL列表
 *   communityId: integer, // 所属社区ID
 *   id: integer,
 * }
 */
export function createReview(data) {
    return request({
        url: `/api/special/review/create`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 查询店铺评价列表（分页）
 * 路径: POST /api/special/review/list
 * 请求参数 (Request):
 * {
 *   shopId: integer, // 店铺ID
 *   communityId: integer, // 社区ID（数据隔离）
 *   pageNumber: integer, // 页码（从1开始）
 *   pageSize: integer, // 每页条数
 * }
 *
 * 返回参数 (Response):
 * --- 响应 200 ---
 * {
 *   code: string, // 状态码 {00000：成功 | A0401：未登录 | A0403：账号冻结 | B0001 系统执行出错，错误信息msg }
 *  data: {
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
 *   reviewId: integer,
 *   shopId: integer, // 店铺ID
 *   userId: integer, // 评价用户ID
 *   userName: string, // 评价用户昵称
 *   userAvatar: string, // 评价用户头像
 *   rating: integer, // 评分（1-5）
 *   content: string, // 评价内容
 *   images: Array<string>, // 评价图片URL列表
 *   communityId: integer, // 所属社区ID
 *   id: integer,
 * }
 */
export function reviewList(data) {
    return request({
        url: `/api/special/review/list`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 获取社区特惠店铺详情
 * 路径: GET /api/special/shop/detail/{shopId}
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
 *   shopInfo: {
 *     del_flag: boolean,
 *     createTime: string, // 创建时间
 *     updateTime: string, // 最后更新时间
 *     shopId: integer,
 *     sellerUserId: integer, // 卖家用户ID
 *     name: string, // 店铺名称
 *     logo: string, // 店铺Logo
 *     phone: string, // 联系电话
 *     address: string, // 店铺地址
 *     description: string, // 店铺简介
 *     status: integer, // 店铺状态：1营业中 2歇业中
 *     goodsCount: integer, // 商品总数
 *     followCount: integer, // 关注数  卖家关注了多少人
 *     fansCount: integer, // 粉丝数  多少人关注了卖家
 *     monthlySales: integer, // 月销量
 *     communityId: integer, // 所属社区ID
 *     communityName: string, // 所属社区名称
 *     cat1Id: integer, // 一级分类ID
 *     cat2Id: integer, // 二级分类ID
 *     coverImage: string, // 封面图URL
 *     latitude: number, // 纬度
 *     longitude: number, // 经度
 *     rating: number, // 店铺评分
 *     startPrice: number, // 起步价
 *     isNew: boolean, // 是否新品
 *     id: integer,
 *   },
 *   couponList: Array<{
 *       del_flag: boolean,
 *       createTime: string, // 创建时间
 *       updateTime: string, // 最后更新时间
 *       couponId: integer,
 *       shopId: integer, // 店铺ID
 *       title: string, // 优惠券标题
 *       money: number, // 抵扣金额
 *       minSpend: number, // 最低消费门槛
 *       total: integer, // 发行总量（0表示不限量）
 *       claimedCount: integer, // 已领取数量
 *       status: integer, // 状态：1进行中 2已过期 3已下架
 *       startTime: string, // 有效期开始时间
 *       endTime: string, // 有效期结束时间
 *       communityId: integer, // 所属社区ID
 *       claimed: boolean, // 当前用户是否已领取
 *       id: integer,
 *     }>, // 优惠券列表（含当前用户领取状态）
 *   serviceItems: Array<{
 *       del_flag: boolean,
 *       createTime: string, // 创建时间
 *       updateTime: string, // 最后更新时间
 *       goodsId: integer,
 *       shopId: integer, // 所属店铺ID
 *       title: string, // 商品标题
 *       description: string, // 商品详情描述
 *       coverImage: string, // 封面图URL
 *       carouselImages: Array<string>, // 轮播图URL列表
 *       pointsPrice: integer, // 积分价格
 *       cashPrice: number, // 现金价格
 *       originalPrice: number, // 原价/参考价
 *       stock: integer, // 库存数量
 *       salesCount: integer, // 销量
 *       goodsType: integer, // 商品类型（支付方式）：1积分兑换 2现金购买 3混合
 *       scene: string, // 商品场景：volunteer志愿者商城 points积分兑换 assistance消费帮扶 frame打卡相框
 *       status: integer, // 商品状态：1上架中 2已下架
 *       category: string, // 商品分类
 *       specs: string, // 规格参数JSON
 *       frameNo: string, // 相框编号
 *       frameSize: string, // 规格尺寸
 *       sceneDesc: string, // 适用场景描述（相框场景专属）
 *       delivery: string, // 配送方式
 *       features: Array<string>, // 功能特性列表
 *       communityId: integer, // 所属社区ID
 *       communityName: string, // 所属社区名称
 *       id: integer,
 *     }>, // 特惠服务项目（店铺商品列表）
 *   reviews: Array<{
 *       del_flag: boolean,
 *       createTime: string, // 创建时间
 *       updateTime: string, // 最后更新时间
 *       reviewId: integer,
 *       shopId: integer, // 店铺ID
 *       userId: integer, // 评价用户ID
 *       userName: string, // 评价用户昵称
 *       userAvatar: string, // 评价用户头像
 *       rating: integer, // 评分（1-5）
 *       content: string, // 评价内容
 *       images: Array<string>, // 评价图片URL列表
 *       communityId: integer, // 所属社区ID
 *       id: integer,
 *     }>, // 顾客评价列表
 *   reviewCount: integer, // 评价总数
 * }
 */
export function shopDetail(shopId) {
    return request({
        url: `/api/special/shop/detail/${shopId}`,
        method: 'get'
    })
}

