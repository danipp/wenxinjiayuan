import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

// 引入模块
import floor from './floor/index';
import isLogin from './isLogin/index';

export default new Vuex.Store({
  modules: {
    floor,isLogin
  }
});