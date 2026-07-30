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
                已使用 <text class="highlight">{{ detail.usedCount }}</text> 次，共 <text class="highlight">{{
                    detail.totalJoined }}</text> 居民参加
            </text>
            <u-icon name="arrow-right" color="#a0aec0" size="14"></u-icon>
        </view>

        <!-- 3. 活动介绍区 -->
        <view class="intro-card">
            <text class="intro-title">活动介绍</text>
            <text class="intro-body">{{ detail.content }}</text>
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
export default {
    data() {
        return {
            detail: {
                id: 101,
                title: '时光AI讲堂—AI公益课进社区专题',
                tag: 'AI时光课堂',
                type: '线下活动',
                participants: '25728',
                usedCount: '2121',
                totalJoined: '25728',
                image: 'https://cdn.uviewui.com/uview/album/5.jpg',
                maxLimit: 100, // 模板默认的人数限制，可用于填充同款表单
                content: `时光AI讲堂-AI公益课进社区”在全国开展1000场以上免费AI教学活动，由青年志愿者为老年人授课。课程内容涵盖AI基础认知、智能问答、求医问诊、趣味图片生成等实用技能，注重操作实践与安全防护。课件通俗易懂，教学耐心细致，全程免费。通过代际互助，让老年人敢用、会用、爱用AI，享受智慧生活便利。`
            }
        };
    },
    methods: {
        // 制作同款核心跳转逻辑：带参跳回发布页面
        handleMakeSame() {
            const title = encodeURIComponent(this.detail.title);
            const content = encodeURIComponent(this.detail.content);
            const maxLimit = this.detail.maxLimit || '';

            uni.navigateTo({
                url: `/spages/fun/create?title=${title}&content=${content}&maxLimit=${maxLimit}`
            });
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