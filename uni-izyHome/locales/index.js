import Vue from 'vue'
import VueI18n from 'vue-i18n'
 import en from './en.json'
 import zh from './zh.json'
Vue.use(VueI18n)
 
const messages = {
  en,
  zh
}
 
const i18n = new VueI18n({
  locale: uni.getStorageSync('lan') || 'zh-CN', // 默认语言
  messages,
})
 export default i18n