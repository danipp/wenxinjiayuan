import { loginByCode } from "@/api/login"; // 自行替换你的接口路径
import { setToken } from "@/utils/auth"; // 存储token工具方法
export default async function () {
    // 仅微信小程序执行
    // #ifdef MP-WEIXIN
    uni.showLoading({
        title: '加载中',
        mask: true
    })
    return new Promise((resolve, reject) => {
        uni.login({
            provider: "weixin",
            success: async (loginRes) => {
                if (loginRes.code) {
                    try {
                        const res = await loginByCode(loginRes.code);
                        setToken(res.data.token);
                        resolve(res.data.token)
                    } catch (err) {
                        uni.hideLoading({
                            noConflict: true
                        })
                        reject(err)
                        uni.showToast({
                            title: "网络错误，无法获取会话密钥",
                            icon: "none",
                        });
                    }
                } else {
                    reject(loginRes.errMsg)
                    uni.hideLoading({
                        noConflict: true
                    })
                    uni.showToast({
                        title: `获取code失败: ${loginRes.errMsg}`,
                        icon: "none",
                    });
                }
            },
            fail: (err) => {
                reject(err)
                uni.hideLoading({
                    noConflict: true
                })
                uni.showToast({
                    title: `登录失败: ${err.errMsg}`,
                    icon: "none",
                });
            },
        });
    })
    // #endif
}