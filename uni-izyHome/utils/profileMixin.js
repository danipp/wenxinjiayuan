import uploadMixin from './upload.js';

/**
 * profileMixin.js
 * 专用于管理和录入用户“头像与昵称”的公共逻辑混入
 */
export default {
    mixins: [uploadMixin],
    data() {
        return {
            avatarUrl: '', // 当前头像OSS地址
            nickname: '',  // 昵称
            defaultAvatar: '', // 默认占位头像
            avatarOssPath: 'avatar/' // 头像上传目录，传给OSS STS接口
        };
    },
    methods: {
        // 1. 微信原生选择头像回调 (open-type="chooseAvatar")
        async onChooseAvatar(e) {
            if (e.detail && e.detail.avatarUrl) {
                await this.uploadAvatar(e.detail.avatarUrl);
            }
        },

        // 2. 备用非微信平台：选择/拍摄头像 (调用系统相机或相册)
        chooseCustomAvatar() {
            uni.showActionSheet({
                itemList: ['拍照', '从相册选择'],
                success: (res) => {
                    const sourceType = res.tapIndex === 0 ? ['camera'] : ['album'];
                    uni.chooseImage({
                        count: 1,
                        sizeType: ['compressed'],
                        sourceType: sourceType,
                        success: async (chooseRes) => {
                            if (chooseRes.tempFilePaths && chooseRes.tempFilePaths[0]) {
                                const tempFile = chooseRes.tempFiles && chooseRes.tempFiles[0];
                                await this.uploadAvatar(chooseRes.tempFilePaths[0], tempFile);
                            }
                        }
                    });
                }
            });
        },

        // 上传头像到OSS，并向父组件抛出真实头像URL
        async uploadAvatar(filePath, tempFile = {}) {
            if (!filePath) return '';

            uni.showLoading({ title: '头像上传中...', mask: true });

            try {
                const file = {
                    path: filePath,
                    name: tempFile.name || this.getAvatarFileName(filePath),
                    index: 0
                };
                const result = await this.uploadFileToOss(file, null, this.avatarOssPath);
                this.avatarUrl = result.url;
                this.$emit('avatar-change', this.avatarUrl);
                return this.avatarUrl;
            } catch (err) {
                console.error('头像上传失败:', err);
                uni.showToast({ title: '头像上传失败，请重试', icon: 'none' });
                return '';
            } finally {
                uni.hideLoading();
            }
        },

        // 从临时路径中补齐文件名，避免OSS上传时丢失头像文件后缀
        getAvatarFileName(filePath) {
            const path = filePath.split('?')[0];
            const match = path.match(/\.([a-zA-Z0-9]+)$/);
            const suffix = match ? `.${match[1]}` : '.png';
            return `avatar-${Date.now()}${suffix}`;
        },

        // 3. 安全同步微信键盘推荐的一键昵称
        onNicknameInput(e) {
            this.nickname = e.detail.value || '';
        },
        onNicknameBlur(e) {
            this.nickname = e.detail.value || '';
        },

        // 4. 重置组件内部数据
        resetProfileData(initAvatar = '', initNickname = '') {
            this.avatarUrl = initAvatar || 'https://cdn.uviewui.com/uview/album/1.jpg';
            this.nickname = initNickname || '';
        },

        // 5. 统一数据验证（头像昵称是否为空）
        validateProfile() {
            if (!this.avatarUrl) {
                uni.showToast({ title: '请选择或上传头像', icon: 'none' });
                return false;
            }
            if (!this.nickname.trim()) {
                uni.showToast({ title: '请输入或导入昵称', icon: 'none' });
                return false;
            }
            return true;
        }
    }
};