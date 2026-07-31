import request from '../utils/request.js'

export function loginByCode(code) {
    return request({
        url: `/api/mini/loginByCode/${code}`,
        method: "get",
        data: {},
    })
}

// /api/mini/auth
export function loginByAuth(data) {
    return request({
        url: `/api/mini/auth`,
        method: "post",
        data: data || {},
    })
}

