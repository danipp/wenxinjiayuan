/**
 * phoneAuthMixin.js
 * 专用于微信小程序一键获取并安全存储手机号的公共逻辑混入
 */
import { loginByAuth } from "@/api/login"
export default {
    data() {
        return {
            phoneNumber: '' // 已经授权成功的手机号
        };
    },
    created() {
        // 初始化时先尝试从本地缓存读取已授权的手机号
        this.initPhoneNumber();
    },
    methods: {
        // 1. 初始化读取本地缓存
        initPhoneNumber() {
            const cachedPhone = uni.getStorageSync('user_phone_number');
            if (cachedPhone) {
                this.phoneNumber = cachedPhone;
            }
        },

        // 2. 微信一键授权手机号回调 (open-type="getPhoneNumber")
        onGetPhoneNumber(e) {
            // 用户拒绝了授权
            if (e.detail.errMsg && e.detail.errMsg.indexOf('deny') !== -1) {
                uni.showToast({
                    title: '您拒绝了手机号授权',
                    icon: 'none'
                });
                return;
            }

            // 获取到加密 code (微信新标准)
            const code = e.detail.code;
            if (code) {
                this.decryptPhoneNumber(code);
            } else {
                uni.showToast({
                    title: '获取授权码失败，请重试',
                    icon: 'none'
                });
            }
        },

        // 3. 模拟向您的后端发送 code 换取真实手机号
        decryptPhoneNumber(code) {
            uni.showLoading({ title: '安全解密中...' });
            loginByAuth({ decodeTelCode: code }).then(res => {
                uni.hideLoading();
                uni.setStorageSync('user_phone_number', res.data);

                uni.showToast({
                    title: '授权成功',
                    icon: 'success'
                });

                // 派发成功事件给父组件
                this.$emit('auth-success', res.data);
                this.$emit('update:show', false);
            }).catch(err => {
                uni.hideLoading();
            })
        },

        // 4. 清除本地授权缓存（备用方法）
        clearPhoneAuth() {
            this.phoneNumber = '';
            uni.removeStorageSync('user_phone_number');
        }
    }
};