import request from '@/utils/request'
// 年级列表
export function getGradeList(data){
    return request({
        url:'/v1/toll_schoolGrade/selectList',
        mehtod:'post',
        data:data || {}
    })
}
// 营地列表
export function getCampList(data){
    return request({
        url:'/camp/sku/listPage', 
        method:'post',  
        data:data || {}
    })
}