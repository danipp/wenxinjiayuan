<template>
    <view class="bgImage"
        :style="'background-image: url(' + bgImage + ');background-size:100%;background-repeat:no-repeat;min-height:' + bgHeight + ';'">
        <slot></slot>
    </view>
</template>

<script>
export default {
    props: {
        bgImage: String,
        bgHeight: String,
    }
}
</script>

<style></style>