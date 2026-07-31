
import request from '@/utils/request'
/**
 * 接口名称: 店铺商品列表
 * 路径: GET /api/store/shop/{shopId}/goods
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
 *   goodsId: integer,
 *   shopId: integer, // 所属店铺ID
 *   title: string, // 商品标题
 *   description: string, // 商品详情描述
 *   coverImage: string, // 封面图URL
 *   carouselImages: Array<string>, // 轮播图URL列表
 *   pointsPrice: integer, // 积分价格
 *   cashPrice: number, // 现金价格
 *   originalPrice: number, // 原价/参考价
 *   stock: integer, // 库存数量
 *   salesCount: integer, // 销量
 *   goodsType: integer, // 商品类型：1积分兑换 2现金购买 3混合 4打卡相框
 *   status: integer, // 商品状态：1上架中 2已下架
 *   category: string, // 商品分类
 *   specs: string, // 规格参数JSON
 *   frameNo: string, // 相框编号
 *   frameSize: string, // 规格尺寸
 *   scene: string, // 适用场景
 *   delivery: string, // 配送方式
 *   features: Array<string>, // 功能特性列表
 *   id: integer,
 * }
 */
export function shopGoods(shopId) {
    return request({
        url: `/api/store/shop/${shopId}/goods`,
        method: 'get'
    })
}

/**
 * 接口名称: 店铺详情
 * 路径: GET /api/store/shop/detail/{shopId}
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
 *   shopId: integer,
 *   sellerUserId: integer, // 卖家用户ID
 *   name: string, // 店铺名称
 *   logo: string, // 店铺Logo
 *   phone: string, // 联系电话
 *   address: string, // 店铺地址
 *   description: string, // 店铺简介
 *   status: integer, // 店铺状态：1营业中 2歇业中
 *   goodsCount: integer, // 商品总数
 *   id: integer,
 * }
 */
export function detail(shopId) {
    return request({
        url: `/api/store/shop/detail/${shopId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 获取我的店铺
 * 路径: GET /api/store/shop/my
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
 *   shopId: integer,
 *   sellerUserId: integer, // 卖家用户ID
 *   name: string, // 店铺名称
 *   logo: string, // 店铺Logo
 *   phone: string, // 联系电话
 *   address: string, // 店铺地址
 *   description: string, // 店铺简介
 *   status: integer, // 店铺状态：1营业中 2歇业中
 *   goodsCount: integer, // 商品总数
 *   id: integer,
 * }
 */
export function myShop() {
    return request({
        url: `/api/store/shop/my`,
        method: 'get'
    })
}

/**
 * 接口名称: 创建/编辑店铺（卖家）
 * 路径: POST /api/store/shop/save
 * 请求参数 (Request):
 * {
 *   shopId: integer, // 店铺ID（编辑时传入，新增时不传）
 *   name: string, // 店铺名称
 *   logo: string, // 店铺Logo URL
 *   phone: string, // 联系电话
 *   address: string, // 店铺地址
 *   description: string, // 店铺简介
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
 *   shopId: integer,
 *   sellerUserId: integer, // 卖家用户ID
 *   name: string, // 店铺名称
 *   logo: string, // 店铺Logo
 *   phone: string, // 联系电话
 *   address: string, // 店铺地址
 *   description: string, // 店铺简介
 *   status: integer, // 店铺状态：1营业中 2歇业中
 *   goodsCount: integer, // 商品总数
 *   id: integer,
 * }
 */
export function save(data) {
    return request({
        url: `/api/store/shop/save`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 店铺营业状态切换（卖家）
 * 路径: POST /api/store/shop/toggleStatus/{shopId}
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
export function toggleStatus(shopId) {
    return request({
        url: `/api/store/shop/toggleStatus/${shopId}`,
        method: 'post'
    })
}

