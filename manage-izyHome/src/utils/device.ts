// src/utils/device.js
export const isMobile = () => {
    // 匹配常见移动设备的 User-Agent
    const userAgent = typeof window !== 'undefined' ? window.navigator.userAgent.toLowerCase() : '';
    const mobileReg = /android|iphone|ipod|ipad|windows phone|blackberry|micromessenger|mobile/i;
    return mobileReg.test(userAgent);
};