/**
 * phoneAuthMixin.js
 * 专用于微信小程序一键获取并安全存储手机号的公共逻辑混入
 */
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

            setTimeout(() => {
                uni.hideLoading();

                // 实际开发中，这里需要发起 uni.request 请求您的后端解密接口：
                // const res = await requestDecryptPhone({ code });

                // 以下为模拟解密成功返回的手机号
                const mockDecryptedPhone = '13800138000';

                // 格式化展示用 (如 138****8000)
                const formattedPhone = mockDecryptedPhone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');

                this.phoneNumber = formattedPhone;

                // 写入本地持久化缓存
                uni.setStorageSync('user_phone_number', formattedPhone);

                uni.showToast({
                    title: '授权成功',
                    icon: 'success'
                });

                // 派发成功事件给父组件
                this.$emit('auth-success', formattedPhone);
                this.$emit('update:show', false);
            }, 800);
        },

        // 4. 清除本地授权缓存（备用方法）
        clearPhoneAuth() {
            this.phoneNumber = '';
            uni.removeStorageSync('user_phone_number');
        }
    }
};