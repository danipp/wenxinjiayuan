<template>
  <view class="heade_tabar" :style="getStyles">
    <view class="bgImage" :style="getImageStyle" v-if="bgImage"></view>
    <slot name="left">
      <view class="heade_tabar_left" @click="back" :style="'width:'+leftWidth">
        <u-icon name="arrow-left" size="20"></u-icon>
      </view>
    </slot>
    <slot>{{ title }}</slot>
    <view class="heade_tabar_right" :style="'width:'+leftWidth"></view>
  </view>
</template>

<script>
export default {
  props: {
    title: { //标题
      type: String,
      default: '',
    },
    bgImage: { //背景图片
      type: String,
      default: ''
    },
    bgColor: { //背景颜色
      type: String,
      default: '#fff'
    },
    bgHeight: { //图片高度
      type: String,
      default: '200rpx'
    },
    leftWidth:{
      type:String,
      default:'40rpx'
    }
  },
  data() {
    return {
      statusHeight: 0,
      StatusBarHeight: 0,
    }
  },
  computed: {
    getStyles() {
      if (this.bgImage) {
        return `height:${this.statusHeight}px;padding-top:${this.StatusBarHeight}px;`
      }
      return `height:${this.statusHeight}px;padding-top:${this.StatusBarHeight}px;background:${this.bgColor};`
    },
    getImageStyle() {
      return `background-image:url(${this.bgImage});background-size:cover;background-repeat:no-repeat;background-position:center center;height:${this.bgHeight};`
    },
  },
  created() {
    this.statusHeight = this.CustomBar
    this.StatusBarHeight = this.StatusBar
  },
  methods:{
    back(){
      this.$back()
    },
  },
}
</script>

<style lang="scss" scoped>
.heade_tabar {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-left: 20rpx;
  padding-right: 20rpx;
  position: relative;

  .bgImage {
    width: 100%;
    position: absolute;
    left: 0;
    top: 0;
    z-index: -1;
  }

  // .heade_tabar_left,
  // .heade_tabar_right {
  //   min-width: 40rpx;
  // }
}
</style>