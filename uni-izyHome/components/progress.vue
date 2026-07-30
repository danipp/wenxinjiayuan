<template>
    <view class="progress-bar">
        <view class="progress" :style="{ width: progressWidth, backgroundColor: barColor }"></view>
    </view>
</template>

<script>
export default {
    name: 'ProgressBar',
    props: {
        // 进度百分比，范围 0 - 100
        progress: {
            type: Number,
            default: 0
        },
        // 进度条颜色
        barColor: {
            type: String,
            default: '#07c160'
        }
    },
    computed: {
        progressWidth() {
            return this.progress + '%';
        }
    }
}
</script>

<style lang="scss" scoped>
.progress-bar {
    width: 100%;
    height: 20rpx;
    background-color: #f2f2f2;
    border-radius: 10rpx;
    overflow: hidden;

    .progress {
        height: 100%;
        border-radius: 10rpx;
        transition: all 0.3s ease-in-out;
    }
}
</style>