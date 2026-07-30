/* eslint-disable no-empty */
/* eslint-disable no-implicit-coercion */

import App from "./App";
import uView from '@/uni_modules/uview-ui'
Vue.use(uView)
import cuCustom from './colorui/components/cu-custom.vue'
Vue.component('cu-custom', cuCustom);
// 引入uView对小程序分享的mixin封装
import MinCache from './utils/minCache'
Vue.use(MinCache, { timeout: 600 })
//用于让页面的onload在APP onlaunch异步任务后执行
Vue.prototype.$onLaunched = new Promise((resolve) => {
  Vue.prototype.$isResolve = resolve;
});

import './common/showMsg/'
Vue.prototype.staticDir = 'https://cieo.oss-cn-shenzhen.aliyuncs.com/yp';
Vue.prototype.getPath = function (url) {
  return Vue.prototype.staticDir + url;
}
import MinRouter from './utils/router.js'
Vue.prototype.$open = MinRouter.openPage
Vue.prototype.$query = MinRouter.parseURL
Vue.prototype.$back = MinRouter.back
import Vue from "vue";

Vue.config.productionTip = false;
App.mpType = "app";

import NoData from './components/noData.vue'
import bgCustom from './colorui/components/bg-custom.vue'
import imageUpload from './components/upload.vue'
import videoUpload from './components/videoUpload.vue'
Vue.component('bg-custom', bgCustom)
Vue.component('image-upload', imageUpload)
Vue.component('video-upload', videoUpload)
Vue.component('no-data', NoData)
Vue.prototype.$hide = function () {
  uni.hideLoading({ noConflict: true });
};
import i18n from './locales/index.js'
Vue.prototype.$lan = function () {
  return uni.getStorageSync('lan') || 'zh-CN'
}
// unifyPromiseVue2();
import store from './store'
const app = new Vue({
  i18n,
  store,
  ...App
});
app.$mount();

// #ifdef VUE3
import { createSSRApp } from "vue";
export function createApp() {
  const app = createSSRApp(App);
  return {
    app,
  };
}
// #endif
