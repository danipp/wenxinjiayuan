import request from '../utils/request.js'
/**
 * 接口名称: 获取社区特惠分类树
 * 路径: GET /api/special/categories
 * 请求参数 (Request):
 * {
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
 * --- 成功信息 ---
 * {
 *   categoryId: integer, // 分类ID
 *   parentId: integer, // 父分类ID（0表示一级分类）
 *   name: string, // 分类名称
 *   icon: string, // 分类图标URL
 *   sort: integer, // 排序值
 *   children: Array<Object (Circular)>, // 子分类列表（仅一级分类有值）
 * }
 */
export function getCategoryTree(data) {
    return request({
        url: `/api/special/categories`,
        method: 'get',
        data: data || {}
    })
}

/**
 * 接口名称: 社区特惠店铺列表（分页）
 * 路径: POST /api/special/shop/list
 * 请求参数 (Request):
 * {
 *   cat1Id: integer, // 一级分类ID（可选）
 *   cat2Id: integer, // 二级分类ID（可选）
 *   keyword: string, // 关键词搜索（匹配店铺名称）
 *   communityId: integer, // 社区ID（数据隔离）
 *   sort: string, // 排序方式：sales=销量降序，price_asc=价格升序，price_desc=价格降序，rating=评分降序
 *   highRating: boolean, // 是否只看高评分（>=4.8）
 *   isNew: boolean, // 是否只看新品（7天内创建）
 *   pageNumber: integer, // 页码（从1开始）
 *   pageSize: integer, // 每页条数
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
 *   shopId: integer,
 *   sellerUserId: integer, // 卖家用户ID
 *   name: string, // 店铺名称
 *   logo: string, // 店铺Logo
 *   phone: string, // 联系电话
 *   address: string, // 店铺地址
 *   description: string, // 店铺简介
 *   status: integer, // 店铺状态：1营业中 2歇业中
 *   goodsCount: integer, // 商品总数
 *   followCount: integer, // 关注数  卖家关注了多少人
 *   fansCount: integer, // 粉丝数  多少人关注了卖家
 *   monthlySales: integer, // 月销量
 *   communityId: integer, // 所属社区ID
 *   communityName: string, // 所属社区名称
 *   cat1Id: integer, // 一级分类ID
 *   cat2Id: integer, // 二级分类ID
 *   coverImage: string, // 封面图URL
 *   latitude: number, // 纬度
 *   longitude: number, // 经度
 *   rating: number, // 店铺评分
 *   startPrice: number, // 起步价
 *   isNew: boolean, // 是否新品
 *   id: integer,
 * }
 */
export function shopList(data) {
    return request({
        url: `/api/special/shop/list`,
        method: 'post',
        data: data || {}
    })
}

