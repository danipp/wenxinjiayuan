import Vue from 'vue'
import defineObj from './showMsg'
// Object.keys(defineObj).forEach(item => {
  Object.defineProperty(Vue.prototype, '$fuck', {
    get() {
      return defineObj
    }
  })
// })