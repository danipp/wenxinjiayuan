<template>
    <view class="detail-container">
        <!-- 1. 活动名片区 -->
        <view class="activity-hero-card">
            <image class="hero-image" :src="detail.image" mode="aspectFill"></image>
            <view class="hero-right">
                <text class="hero-title">{{ detail.title }}</text>
                <view class="badge-tag">{{ detail.tag }}</view>
                <text class="hero-sub">类型：{{ detail.type }}</text>
                <text class="hero-sub">人数：{{ detail.participants }} 人</text>
            </view>
        </view>

        <!-- 2. 数据指标横栏 -->
        <view class="status-metric-bar">
            <text class="metric-text">
                已使用 <text class="highlight">{{ detail.usedCount || 0 }}</text> 次，共 <text class="highlight">{{
                    detail.totalJoined || 0 }}</text> 居民参加
            </text>
            <u-icon name="arrow-right" color="#a0aec0" size="14"></u-icon>
        </view>

        <!-- 3. 活动介绍区 -->
        <view class="intro-card">
            <text class="intro-title">活动介绍</text>
            <text class="intro-body">{{ detail.content || '暂无介绍' }}</text>
        </view>

        <!-- 4. 底部悬浮制作同款按钮 -->
        <view class="footer-bar">
            <button class="btn-make-same" @click="handleMakeSame">
                制作同款活动
            </button>
        </view>
    </view>
</template>

<script>
import { detail4, use } from '@/spages/api/square'

export default {
    data() {
        return {
            templateId: null,
            detail: {}
        };
    },
    onLoad(options) {
        this.templateId = options.id
        if (this.templateId) {
            this.fetchDetail()
        }
    },
    methods: {
        // 获取模板详情
        async fetchDetail() {
            try {
                const res = await detail4(this.templateId)
                this.detail = res.data || res
            } catch (e) {
                uni.showToast({ title: '加载失败，请重试', icon: 'none' })
            }
        },
        // 制作同款：先调用 use 接口递增使用次数，再跳转
        async handleMakeSame() {
            try {
                const res = await use(this.templateId)
                const templateData = res.data || res
                const title = encodeURIComponent(templateData.title || '');
                const content = encodeURIComponent(templateData.content || '');
                const maxLimit = templateData.maxLimit || '';

                uni.navigateTo({
                    url: `/spages/fun/create?title=${title}&content=${content}&maxLimit=${maxLimit}`
                });
            } catch (e) {
                uni.showToast({ title: '操作失败，请重试', icon: 'none' })
            }
        }
    }
};
</script>

<style lang="scss" scoped>
.detail-container {
    min-height: 100vh;
    background-color: #f7f9fb;
    padding: 32rpx 32rpx calc(160rpx + env(safe-area-inset-bottom)) 32rpx;
    box-sizing: border-box;

    /* 顶部活动名片 */
    .activity-hero-card {
        background-color: #ffffff;
        border-radius: 24rpx;
        padding: 32rpx;
        display: flex;
        gap: 32rpx;
        box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.01);
        margin-bottom: 28rpx;

        .hero-image {
            width: 200rpx;
            height: 200rpx;
            border-radius: 16rpx;
            background-color: #f5f7fa;
        }

        .hero-right {
            flex: 1;
            display: flex;
            flex-direction: column;

            .hero-title {
                font-size: 32rpx;
                font-weight: bold;
                color: #333333;
                line-height: 1.4;
                margin-bottom: 12rpx;
            }

            .badge-tag {
                width: fit-content;
                background-color: #f0f4f8;
                color: #4a5568;
                font-size: 22rpx;
                padding: 4rpx 16rpx;
                border-radius: 8rpx;
                margin-bottom: 16rpx;
            }

            .hero-sub {
                font-size: 24rpx;
                color: #718096;
                margin-top: 4rpx;
            }
        }
    }

    /* 数据状态栏 */
    .status-metric-bar {
        background-color: #ffffff;
        border-radius: 24rpx;
        padding: 28rpx 32rpx;
        display: flex;
        align-items: center;
        justify-content: space-between;
        box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.01);
        margin-bottom: 28rpx;

        .metric-text {
            font-size: 26rpx;
            color: #4a5568;

            .highlight {
                color: #07c160;
                font-weight: bold;
            }
        }
    }

    /* 活动介绍卡片 */
    .intro-card {
        background-color: #ffffff;
        border-radius: 24rpx;
        padding: 36rpx 32rpx;
        box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.01);

        .intro-title {
            font-size: 32rpx;
            font-weight: bold;
            color: #333333;
            margin-bottom: 24rpx;
            display: block;
        }

        .intro-body {
            font-size: 28rpx;
            color: #555555;
            line-height: 1.6;
        }
    }

    /* 底部固定动作按钮 */
    .footer-bar {
        position: fixed;
        bottom: 0;
        left: 0;
        width: 100%;
        background-color: #ffffff;
        box-shadow: 0 -8rpx 24rpx rgba(0, 0, 0, 0.03);
        padding: 24rpx 48rpx calc(24rpx + env(safe-area-inset-bottom)) 48rpx;
        box-sizing: border-box;
        z-index: 100;

        .btn-make-same {
            width: 100%;
            height: 96rpx;
            line-height: 96rpx;
            background-color: #07c160;
            color: #ffffff;
            font-size: 32rpx;
            font-weight: bold;
            border-radius: 48rpx;

            &::after {
                border: none;
            }
        }
    }
}
</style>