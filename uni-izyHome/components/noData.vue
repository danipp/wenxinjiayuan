<template>
    <view class="su-empty">
        <view class="su-empty__image">
            <image :src="getPath('/static/home/noData.png')" mode="aspectFit"></image>
        </view>
        <view class="su-empty__text" style="margin-top: 30rpx;">
            <slot>{{ text }}</slot> 
        </view>
        
    </view>
</template>
   
<script>
export default {
    name: "su-empty",
    props: {
        image:{
            type: String,
            default: 'http://cdn.uviewui.com/uview/empty/data.png'
        },
        mode: {
            type: String,
            default: 'data'
        },
        text: {
            type: String,
            default: '暂无数据'
        },
    }
}
</script>
   
<style lang="scss">
.su-empty {
    padding: 60rpx;

    &__image {
        display: flex;
        justify-content: center;
        align-items: center;

        image {
            width: 520rpx;
            height: 260rpx;
            display: block;
        }
    }

    &__text {
        font-size: 28rpx;
        color: #999;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 20rpx;
    }
}
</style>