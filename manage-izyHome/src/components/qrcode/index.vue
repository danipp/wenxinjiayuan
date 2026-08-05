<template>
    <div class="qrcode_content">
        <QrcodeVue :value="url" :size="size" />
    </div>
</template>

<script setup name="qrcode">
import QrcodeVue from 'qrcode.vue'
const props = defineProps({
    url:{
        type:String,
        default:"https://www.baidu.com"
    },
    size:{
        type:Number,
        default:200
    }
})
</script>

<style lang="scss" scoped></style>