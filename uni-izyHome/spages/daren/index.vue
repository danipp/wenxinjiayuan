<template>
    <view class="rank-container">
        <!-- 顶部渐变装饰背景与标签页 -->
        <view class="rank-header-bg">
            <view class="tabs-row">
                <view class="tab-item" :class="{ 'tab-item-active': activeTab === 'mutual' }"
                    @click="switchTab('mutual')">
                    互助达人
                </view>
                <view class="tab-item" :class="{ 'tab-item-active': activeTab === 'activity' }"
                    @click="switchTab('activity')">
                    活动达人
                </view>
            </view>
        </view>

        <!-- 达人排行榜列表 -->
        <scroll-view scroll-y class="list-scroll-view">
            <view class="rank-list">
                <view v-for="(item, index) in currentList" :key="item.id" class="rank-row">
                    <!-- 排名数字（前三名特殊着色） -->
                    <text class="rank-number" :class="'rank-' + (index + 1)">
                        {{ index + 1 }}
                    </text>

                    <!-- 头像 -->
                    <image class="user-avatar" :src="item.avatar" mode="aspectFill"></image>

                    <!-- 达人信息 -->
                    <view class="user-info">
                        <text class="user-name">{{ item.name }}</text>
                        <text class="user-score">
                            {{ activeTab === 'mutual' ? '帮助 ' + item.score + ' 人' : '参与活动 ' + item.score + ' 次' }}
                        </text>
                    </view>
                </view>
            </view>
        </scroll-view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            activeTab: 'mutual', // 当前选中的 Tab：'mutual'(互助达人) 或 'activity'(活动达人)
            communityName: '中山一社区', // 社区名称
            // 互助达人榜单数据
            mutualList: [
                { id: 1, name: '秉治', score: 1, avatar: 'https://cdn.uviewui.com/uview/album/1.jpg' },
                { id: 2, name: '罗完成', score: 5, avatar: 'https://cdn.uviewui.com/uview/album/5.jpg' },
                { id: 3, name: '张姐', score: 3, avatar: 'https://cdn.uviewui.com/uview/album/6.jpg' },
                { id: 4, name: '李阿姨', score: 2, avatar: 'https://cdn.uviewui.com/uview/album/7.jpg' }
            ],
            // 活动达人榜单数据
            activityList: [
                { id: 1, name: '秉治', score: 1, avatar: 'https://cdn.uviewui.com/uview/album/1.jpg' },
                { id: 2, name: '王大哥', score: 8, avatar: 'https://cdn.uviewui.com/uview/album/2.jpg' },
                { id: 3, name: '赵大爷', score: 6, avatar: 'https://cdn.uviewui.com/uview/album/3.jpg' },
                { id: 4, name: '陈阿姨', score: 4, avatar: 'https://cdn.uviewui.com/uview/album/4.jpg' }
            ]
        };
    },
    computed: {
        // 动态获取当前选中 Tab 的榜单列表并进行从高到低排序
        currentList() {
            const list = this.activeTab === 'mutual' ? this.mutualList : this.activityList;
            return [...list].sort((a, b) => b.score - a.score);
        }
    },
    onLoad(options) {
        // 动态获取页面传参的社区名称，默认为“中山一社区”
        const cachedLocation = uni.getStorageSync('selected_community');
        if (cachedLocation && cachedLocation.name) {
            this.communityName = cachedLocation.name;
        }
        this.updatePageTitle();
    },
    methods: {
        // 设置页面导航栏标题
        updatePageTitle() {
            uni.setNavigationBarTitle({
                title: `${this.communityName}达人周榜`
            });
        },
        // 切换 Tab
        switchTab(tab) {
            this.activeTab = tab;
        }
    }
};
</script>

<style lang="scss" scoped>
.rank-container {
    min-height: 100vh;
    background-color: #ffffff;
    display: flex;
    flex-direction: column;

    /* 顶部渐变装饰背景 */
    .rank-header-bg {
        background: linear-gradient(180deg, #fef4e2 0%, rgba(255, 255, 255, 0) 100%);
        padding: 48rpx 0 20rpx 0;
        display: flex;
        justify-content: center;
        position: relative;
        z-index: 10;

        .tabs-row {
            display: flex;
            width: 80%;
            justify-content: space-around;
            position: relative;

            .tab-item {
                font-size: 36rpx;
                color: #8c8c8c;
                padding: 16rpx 32rpx;
                position: relative;
                transition: color 0.2s ease, font-weight 0.2s ease;
                cursor: pointer;

                /* 激活状态字样 */
                &.tab-item-active {
                    color: #2e353f;
                    font-weight: bold;

                    /* 弧形微笑下划线效果 */
                    &::after {
                        content: '';
                        position: absolute;
                        bottom: -12rpx;
                        left: 50%;
                        transform: translateX(-50%);
                        width: 72rpx;
                        height: 20rpx;
                        border-bottom: 7rpx solid #b27341;
                        /* 金褐色微笑指示器 */
                        border-radius: 50%;
                        /* 弧形的关键所在 */
                    }
                }
            }
        }
    }

    /* 列表区域 */
    .list-scroll-view {
        flex: 1;
        overflow: hidden;
    }

    .rank-list {
        padding: 20rpx 48rpx calc(40rpx + env(safe-area-inset-bottom)) 48rpx;

        .rank-row {
            display: flex;
            align-items: center;
            padding: 32rpx 0;
            border-bottom: 2rpx solid #f9f0ec; // 温暖的微粉色分割线

            &:last-child {
                border-bottom: none;
            }

            /* 排名数字样式 */
            .rank-number {
                font-family: "Georgia", "Times New Roman", serif;
                font-style: italic;
                font-size: 40rpx;
                font-weight: bold;
                width: 64rpx;
                text-align: left;
                color: #8c8c8c; // 默认排名灰色

                /* 前三名专属配色 */
                &.rank-1 {
                    color: #ff4d4f; // 第一名 亮红
                }

                &.rank-2 {
                    color: #ff9c6e; // 第二名 橙
                }

                &.rank-3 {
                    color: #ffc069; // 第三名 浅黄
                }
            }

            /* 用户头像 */
            .user-avatar {
                width: 96rpx;
                height: 96rpx;
                border-radius: 50%;
                margin-right: 28rpx;
                background-color: #f5f5f5;
                border: 3rpx solid rgba(254, 244, 226, 0.5);
            }

            /* 文本信息 */
            .user-info {
                display: flex;
                flex-direction: column;

                .user-name {
                    font-size: 30rpx;
                    font-weight: bold;
                    color: #2e353f;
                    margin-bottom: 8rpx;
                }

                .user-score {
                    font-size: 26rpx;
                    color: #7a828e;
                }
            }
        }
    }
}
</style>