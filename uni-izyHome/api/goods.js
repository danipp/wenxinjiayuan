import request from '../utils/request.js'
/**
 * 接口名称: 分页查询商品列表
 * 路径: POST /api/store/goods/page
 * 请求参数 (Request):
 * {
 *   pageNumber: integer, // 页码，默认为1
 *   pageSize: integer, // 单页数据量，默认为20
 *   shopId: integer, // 所属店铺ID
 *   category: string, // 商品分类
 *   goodsType: integer, // 商品类型：1积分兑换 2现金购买 3混合 4打卡相框
 *   status: integer, // 商品状态：1上架中 2已下架
 *   keyword: string, // 关键词（模糊搜索商品标题）
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
export function page1(data) {
    return request({
        url: `/api/store/goods/page`,
        method: 'post',
        data: data || {}
    })
}



