import request from '../utils/request.js'
// 列表课后课
export function coursePage(data) {
  return request({
    url: '/app/charge/goods/listPage',
    method: "post",
    data: data || {},
  })
}
// 列表课后课数量
export function courseCount(data) {
  return request({
    url: '/app/charge/goods/selectEduCourseGoodsPageCount',
    method: "post",
    data: data || {},
  })
}
// 查询课后课所有分类
export function getClassCategory() {
  return request({
    url: '/charge/type/querySchoolAll',
    method: "get",
  })
}
  //查询课程商品列表
  export function goodsListPage(data){
    return request({
      url: '/v1/app/courseInfo/selectPage',
      method: "post",
      data: data || {}
    })
  }