import request from '@/utils/request'
// 获取oss
export function uploadImgVideo(data){
    return request({    
        url: '/api/ad/task/upload',
        method: 'post',
        data
    })
}