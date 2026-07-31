import request from '@/utils/request.js'
/**
 * 接口名称: 收藏/取消收藏（商品或店铺）
 * 路径: POST /api/store/goods/collect
 * 请求参数 (Request):
 * {
 *   targetId: integer, // 收藏目标ID（商品ID或店铺ID）
 *   targetType: integer, // 收藏类型：1商品收藏 2店铺收藏
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
 * boolean
 */
export function collect(data) {
    return request({
        url: `/api/store/goods/collect`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 我的收藏列表
 * 路径: GET /api/store/goods/collections
 * 请求参数 (Request):
 * {
 *   targetType: integer, // 
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
 *   collectionId: integer,
 *   userId: integer, // 用户ID
 *   targetId: integer, // 收藏目标ID
 *   targetType: integer, // 收藏类型：1商品 2店铺
 *   id: integer,
 * }
 */
export function collections(data) {
    return request({
        url: `/api/store/goods/collections`,
        method: 'get',
        data: data || {}
    })
}

/**
 * 接口名称: 创建商品（卖家）
 * 路径: POST /api/store/goods/create
 * 请求参数 (Request):
 * {
 *   goodsId: integer, // 商品ID（编辑时传入，新增时不传）
 *   shopId: integer, // 所属店铺ID
 *   title: string, // 商品标题
 *   description: string, // 商品详情描述
 *   coverImage: string, // 封面图URL
 *   carouselImages: Array<string>, // 轮播图URL列表
 *   pointsPrice: integer, // 积分价格
 *   cashPrice: number, // 现金价格
 *   originalPrice: number, // 原价/参考价
 *   stock: integer, // 库存数量
 *   goodsType: integer, // 商品类型：1积分兑换 2现金购买 3混合 4打卡相框
 *   category: string, // 商品分类
 *   specs: string, // 规格参数JSON
 *   frameNo: string, // 相框编号（如 FRAME-NFC-001，goodsType=4时必填）
 *   frameSize: string, // 规格尺寸（如 6寸、7寸、8寸，goodsType=4时必填）
 *   scene: string, // 适用场景（如 社区活动、长者探访、志愿服务）
 *   delivery: string, // 配送方式（如 社区配送、快递配送）
 *   features: Array<string>, // 功能特性列表（如 ["NFC碰一碰","快速打卡","活动记录"]）
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
export function create1(data) {
    return request({
        url: `/api/store/goods/create`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 商品详情
 * 路径: GET /api/store/goods/detail/{goodsId}
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
export function detail2(goodsId) {
    return request({
        url: `/api/store/goods/detail/${goodsId}`,
        method: 'get'
    })
}

/**
 * 接口名称: 编辑商品（卖家）
 * 路径: POST /api/store/goods/edit
 * 请求参数 (Request):
 * {
 *   goodsId: integer, // 商品ID（编辑时传入，新增时不传）
 *   shopId: integer, // 所属店铺ID
 *   title: string, // 商品标题
 *   description: string, // 商品详情描述
 *   coverImage: string, // 封面图URL
 *   carouselImages: Array<string>, // 轮播图URL列表
 *   pointsPrice: integer, // 积分价格
 *   cashPrice: number, // 现金价格
 *   originalPrice: number, // 原价/参考价
 *   stock: integer, // 库存数量
 *   goodsType: integer, // 商品类型：1积分兑换 2现金购买 3混合 4打卡相框
 *   category: string, // 商品分类
 *   specs: string, // 规格参数JSON
 *   frameNo: string, // 相框编号（如 FRAME-NFC-001，goodsType=4时必填）
 *   frameSize: string, // 规格尺寸（如 6寸、7寸、8寸，goodsType=4时必填）
 *   scene: string, // 适用场景（如 社区活动、长者探访、志愿服务）
 *   delivery: string, // 配送方式（如 社区配送、快递配送）
 *   features: Array<string>, // 功能特性列表（如 ["NFC碰一碰","快速打卡","活动记录"]）
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
export function edit(data) {
    return request({
        url: `/api/store/goods/edit`,
        method: 'post',
        data: data || {}
    })
}

/**
 * 接口名称: 检查是否已收藏
 * 路径: GET /api/store/goods/isCollected
 * 请求参数 (Request):
 * {
 *   targetId: integer, // 
 *   targetType: integer, // 
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
 * boolean
 */
export function isCollected(data) {
    return request({
        url: `/api/store/goods/isCollected`,
        method: 'get',
        data: data || {}
    })
}

/**
 * 接口名称: 商品上下架切换（卖家）
 * 路径: POST /api/store/goods/toggleStatus/{goodsId}
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
export function toggleStatus1(goodsId) {
    return request({
        url: `/api/store/goods/toggleStatus/${goodsId}`,
        method: 'post'
    })
}

