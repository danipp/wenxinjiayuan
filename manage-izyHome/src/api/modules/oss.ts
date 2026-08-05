import http from "@/api";
/**
 * @name 文件上传模块
 */
// 获取配置
export const getSts = (data) => {
    return http.post(`/manage/api/oss/policyToken`, { purpose: 'userImg' });
};