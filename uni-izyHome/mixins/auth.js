import { getOpenid } from "@/api/index"; // 自行替换你的接口路径
import { setToken } from "@/utils/auth"; // 存储token工具方法

export default {
    data() {
        return {
            token: "",
        };
    },
    methods: {
        /**
         * 微信小程序静默登录获取code、openid、token
         */
        async wxMiniLogin() {
            // 仅微信小程序执行
            // #ifdef MP-WEIXIN
            uni.login({
                provider: "weixin",
                success: async (loginRes) => {
                    if (loginRes.code) {
                        try {
                            const res = await getOpenid(loginRes.code);
                            this.token = res.data.token;
                            setToken(res.data.token);
                            // 可选：登录成功回调，供页面监听
                            this.$emit("wx-login-success", res);
                        } catch (err) {
                            uni.showToast({
                                title: "网络错误，无法获取会话密钥",
                                icon: "none",
                            });
                            this.$emit("wx-login-fail", err);
                        }
                    } else {
                        uni.showToast({
                            title: `获取code失败: ${loginRes.errMsg}`,
                            icon: "none",
                        });
                    }
                },
                fail: (err) => {
                    uni.showToast({
                        title: `登录失败: ${err.errMsg}`,
                        icon: "none",
                    });
                    this.$emit("wx-login-fail", err);
                },
            });
            // #endif
        },
    },
};