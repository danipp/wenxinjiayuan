<template>
    <view class="fun-page-container">
        <!-- 1. 顶部社区选择区 -->
        <view class="top-header-bar">
            <view class="community-pill" @click="openCommunitySelector">
                <u-icon name="home-fill" color="#07c160" size="16"></u-icon>
                <text class="pill-text text-ellipsis">{{ currentCommunityName }}</text>
                <u-icon name="arrow-right" color="#999" size="10"></u-icon>
            </view>
        </view>

        <!-- 2. 滚动活动列表区 -->
        <scroll-view scroll-y class="list-scroll-view" @scrolltolower="loadMore" refresher-enabled
            :refresher-triggered="isRefreshing" @refresherrefresh="onRefresh">
            <!-- 有数据状态 -->
            <view v-if="activityList.length > 0" class="activity-list">
                <view v-for="item in activityList" :key="item.id" class="activity-card">
                    <image class="card-cover" :src="item.image" mode="aspectFill"></image>

                    <view class="card-body">
                        <text class="card-title">{{ item.title }}</text>

                        <view class="info-row">
                            <u-icon name="map-fill" color="#8c9ba5" size="14"></u-icon>
                            <text class="info-text text-ellipsis">地点：{{ item.location }}</text>
                        </view>

                        <view class="info-row">
                            <u-icon name="clock-fill" color="#8c9ba5" size="14"></u-icon>
                            <text class="info-text">时间：{{ item.time }}</text>
                        </view>

                        <view class="participants-box">
                            <view class="avatar-group">
                                <image v-for="(avatar, idx) in item.avatars.slice(0, 3)" :key="idx" class="avatar-img"
                                    :src="avatar" mode="aspectFill"></image>
                                <view v-if="item.avatars.length > 3" class="avatar-more">+{{ item.avatars.length }}
                                </view>
                            </view>
                            <text class="participants-text">同社区 {{ item.enrollCount }} 人已报名</text>
                        </view>

                        <!-- 操作按钮组（修复：通过 dataset 传递分享参数） -->
                        <view class="card-actions">
                            <button class="action-btn btn-share" open-type="share" :data-id="item.id"
                                :data-title="item.title" :data-image="item.image">
                                分享
                            </button>
                            <button class="action-btn btn-detail" @click="goDetail(item.id)">查看详情</button>
                        </view>
                    </view>
                </view>

                <view class="load-status">
                    <text v-if="loading">加载中...</text>
                    <text v-else-if="noMore" class="no-more-text">已经是最后一页了</text>
                </view>
            </view>

            <!-- 无数据缺省状态 -->
            <view v-else class="empty-state">
                <view class="empty-icon-wrapper">
                    <u-icon name="hourglass-half-fill" color="#c0c4cc" size="64"></u-icon>
                </view>
                <text class="empty-title">当前社区暂无娱乐活动</text>
                <text class="empty-sub">换个社区看看，或者赶紧去发布一个吧！</text>
                <button class="empty-create-btn" @click="handleCreate">去发起活动</button>
            </view>
        </scroll-view>

        <!-- 3. 右下角悬浮创建活动按钮 -->
        <view class="float-create-button" @click="handleCreate">
            <u-icon name="plus" color="#ffffff" size="16"></u-icon>
            <text class="btn-text">创建活动</text>
        </view>

        <!-- 社区选择弹窗组件 -->
        <CommunitySelector :show.sync="showCommunitySelector" title="请选择我的社区" mode="select"
            @confirm="handleCommunityChange" />
    </view>
</template>

<script>
import CommunitySelector from '@/components/community.vue';

export default {
    components: {
        CommunitySelector
    },
    data() {
        return {
            currentCommunityName: '中山一社区',
            showCommunitySelector: false,
            activityList: [],
            page: 1,
            loading: false,
            noMore: false,
            isRefreshing: false,
            mockDatabase: {
                '中山一社区': [
                    {
                        id: 1,
                        title: '中山一社区居民瓜子会',
                        image: 'https://cdn.uviewui.com/uview/album/3.jpg',
                        location: '越秀区富力新天地中心(雄镇外街南)',
                        time: '07-01 16:02 至 07-01 23:02',
                        enrollCount: 2,
                        avatars: ['https://cdn.uviewui.com/uview/album/1.jpg', 'https://cdn.uviewui.com/uview/album/2.jpg']
                    },
                    {
                        id: 2,
                        title: '同城周未桌游狼人杀交友社',
                        image: 'https://cdn.uviewui.com/uview/album/4.jpg',
                        location: '越秀区中山一路创意园B栋',
                        time: '07-08 14:00 至 07-08 19:00',
                        enrollCount: 5,
                        avatars: ['https://cdn.uviewui.com/uview/album/5.jpg', 'https://cdn.uviewui.com/uview/album/6.jpg', 'https://cdn.uviewui.com/uview/album/7.jpg']
                    }
                ],
                '都府社区': [
                    {
                        id: 3,
                        title: '都府社区中老年太极拳交流会',
                        image: 'https://cdn.uviewui.com/uview/album/8.jpg',
                        location: '都府社区文化小广场',
                        time: '07-15 08:00 至 07-15 10:00',
                        enrollCount: 8,
                        avatars: ['https://cdn.uviewui.com/uview/album/2.jpg', 'https://cdn.uviewui.com/uview/album/4.jpg']
                    }
                ]
            }
        };
    },
    onLoad() {
        const cachedLocation = uni.getStorageSync('selected_community');
        if (cachedLocation && cachedLocation.name) {
            this.currentCommunityName = cachedLocation.name;
        }
        this.fetchData();
    },
    methods: {
        openCommunitySelector() {
            this.showCommunitySelector = true;
        },

        handleCommunityChange(data) {
            if (data && data.community) {
                this.currentCommunityName = data.community.name;
                uni.setStorageSync('selected_community', data.community);
                this.resetPagination();
            }
        },

        resetPagination() {
            this.page = 1;
            this.noMore = false;
            this.activityList = [];
            this.fetchData();
        },

        fetchData() {
            if (this.loading || this.noMore) return;
            this.loading = true;
            uni.showLoading({
                title: '加载中...',
                mask: true
            })
            setTimeout(() => {
                const dbData = this.mockDatabase[this.currentCommunityName] || [];

                if (dbData.length === 0) {
                    this.activityList = [];
                    this.noMore = true;
                } else {
                    if (this.page === 1) {
                        this.activityList = dbData;
                        this.noMore = dbData.length < 2;
                    } else {
                        this.noMore = true;
                    }
                }

                this.loading = false;
                this.isRefreshing = false;
                this.$hide()
            }, 800);
        },

        loadMore() {
            if (!this.noMore && !this.loading) {
                this.page++;
                this.fetchData();
            }
        },

        onRefresh() {
            this.isRefreshing = true;
            this.page = 1;
            this.noMore = false;
            this.fetchData();
        },

        // 修复后的分享逻辑
        onShareAppMessage(res) {
            // 1. 如果是通过卡片上的分享按钮点击进入
            if (res.from === 'button') {
                const target = res.target;
                const id = target.dataset.id;
                const title = target.dataset.title;
                const imageUrl = target.dataset.image;

                return {
                    title: `[活动推荐] ${title}`,
                    path: `/spages/activity/detail?id=${id}`, // 精准指向详情页
                    imageUrl: imageUrl // 使用该活动的封面图作为分享卡片配图
                };
            }

            // 2. 如果是通过小程序右上角三个点（胶囊按钮）进行全局分享
            return {
                title: '发现社区新乐子，快来一起参与吧！',
                path: '/spages/fun/index'
            };
        },

        goDetail(id) {
            uni.navigateTo({
                url: `/spages/activity/detail?id=${id}`
            });
        },

        handleCreate() {
            uni.navigateTo({
                url: `/spages/fun/create`
            });
        }
    }
};
</script>

<style lang="scss" scoped>
/* 样式保持不变 */
.fun-page-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
    background-color: #f6f8fa;
    position: relative;

    .top-header-bar {
        height: 110rpx;
        padding: 0 32rpx;
        display: flex;
        align-items: center;
        background-color: #ffffff;
        box-sizing: border-box;
        z-index: 10;
        box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.02);

        .community-pill {
            display: inline-flex;
            align-items: center;
            background-color: #f1f3f5;
            padding: 12rpx 24rpx;
            border-radius: 40rpx;
            max-width: 60%;

            .pill-text {
                font-size: 26rpx;
                font-weight: bold;
                color: #2c405a;
                margin: 0 12rpx;
            }
        }
    }

    .list-scroll-view {
        height: calc(100vh - 110rpx);
        width: 100%;
        box-sizing: border-box;
    }

    .activity-list {
        padding: 32rpx;
        display: flex;
        flex-direction: column;
        gap: 32rpx;
    }

    .activity-card {
        background-color: #ffffff;
        border-radius: 32rpx;
        overflow: hidden;
        box-shadow: 0 8rpx 28rpx rgba(0, 0, 0, 0.03);
        display: flex;
        flex-direction: column;

        .card-cover {
            width: 100%;
            height: 320rpx;
            background-color: #f1f3f5;
        }

        .card-body {
            padding: 32rpx;
            display: flex;
            flex-direction: column;

            .card-title {
                font-size: 34rpx;
                font-weight: 800;
                color: #1c2438;
                line-height: 1.4;
                margin-bottom: 24rpx;
            }

            .info-row {
                display: flex;
                align-items: center;
                margin-bottom: 16rpx;

                .info-text {
                    font-size: 26rpx;
                    color: #5d6d7e;
                    margin-left: 16rpx;
                }
            }

            .participants-box {
                display: flex;
                align-items: center;
                margin-top: 12rpx;
                padding-top: 24rpx;
                border-top: 2rpx dashed #edf0f2;
                margin-bottom: 32rpx;

                .avatar-group {
                    display: flex;
                    align-items: center;
                    margin-right: 24rpx;

                    .avatar-img {
                        width: 48rpx;
                        height: 48rpx;
                        border-radius: 50%;
                        border: 3rpx solid #ffffff;
                        margin-right: -16rpx;

                        &:last-child {
                            margin-right: 0;
                        }
                    }

                    .avatar-more {
                        font-size: 22rpx;
                        color: #7f8c8d;
                        background-color: #eaeded;
                        border-radius: 50%;
                        width: 48rpx;
                        height: 48rpx;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-left: 20rpx;
                    }
                }

                .participants-text {
                    font-size: 26rpx;
                    color: #2c3e50;
                    font-weight: 600;
                }
            }

            .card-actions {
                display: flex;
                gap: 24rpx;

                .action-btn {
                    flex: 1;
                    height: 88rpx;
                    line-height: 88rpx;
                    font-size: 30rpx;
                    font-weight: bold;
                    border-radius: 44rpx;
                    cursor: pointer;

                    &::after {
                        border: none;
                    }

                    &.btn-share {
                        background-color: #ffffff;
                        color: #07c160;
                        border: 3rpx solid #07c160;
                    }

                    &.btn-detail {
                        background-color: #07c160;
                        color: #ffffff;
                    }
                }
            }
        }
    }

    .load-status {
        text-align: center;
        padding: 20rpx 0 40rpx 0;
        font-size: 26rpx;
        color: #999999;
    }

    .empty-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 160rpx 64rpx 0 64rpx;
        text-align: center;

        .empty-icon-wrapper {
            width: 240rpx;
            height: 240rpx;
            background-color: #f0f2f5;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 48rpx;
        }

        .empty-title {
            font-size: 34rpx;
            font-weight: bold;
            color: #333333;
            margin-bottom: 16rpx;
        }

        .empty-sub {
            font-size: 26rpx;
            color: #999999;
            margin-bottom: 48rpx;
            line-height: 1.5;
        }

        .empty-create-btn {
            padding: 0 48rpx;
            height: 80rpx;
            line-height: 80rpx;
            background-color: #07c160;
            color: #ffffff;
            font-size: 28rpx;
            font-weight: bold;
            border-radius: 40rpx;

            &::after {
                border: none;
            }
        }
    }

    .float-create-button {
        position: absolute;
        right: 40rpx;
        bottom: 60rpx;
        display: flex;
        align-items: center;
        background-color: #07c160;
        padding: 24rpx 36rpx;
        border-radius: 60rpx;
        box-shadow: 0 12rpx 36rpx rgba(7, 193, 96, 0.35);
        z-index: 99;
        cursor: pointer;
        transition: transform 0.1s ease;

        &:active {
            transform: scale(0.95);
        }

        .btn-text {
            font-size: 28rpx;
            font-weight: bold;
            color: #ffffff;
            margin-left: 12rpx;
        }
    }

    .text-ellipsis {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
}
</style>