// import proxy from '@/utils/proxy.js'
let API_URL;
let envVersion = uni.getAccountInfoSync ? uni.getAccountInfoSync().miniProgram.envVersion : 'develop';
import { getToken, removeToken } from './auth'
import store from '../store'
switch (envVersion) {
  case "develop": // 开发版
    API_URL = "http://192.168.0.143:8091/warm-home";
    // API_URL = "https://warmhome.shengshitongda.cn/warm-home"; 
    break;
  case "trial": // 体验版
    API_URL = "https://warmhome.shengshitongda.cn/warm-home";
    break;
  case "release": // 正式版
    API_URL = "https://warmhome.shengshitongda.cn/warm-home"; //正式环境 
    break;
}
function isNextWork() {
  return new Promise((resolve, reject) => {
    uni.getNetworkType({
      success: (res) => {
        if (res.networkType === 'none') {
          uni.showToast({
            title: "当前无网络,请检查网络环境",
            icon: "none"
          })
          resolve(false)
        } else {
          resolve(true)
        }
      }
    });
  })
}
function getUrlParm() {
  let url = location.href;
  let requestParams = {};
  if (url.indexOf("?") !== -1) {
    let str = url.substr(url.indexOf("?") + 1); //截取?后面的内容作为字符串
    let strs = str.split("&"); //将字符串内容以&分隔为一个数组
    for (let i = 0; i < strs.length; i++) {
      requestParams[strs[i].split("=")[0]] = decodeURI(strs[i].split("=")[1]);
      // 将数组元素中'='左边的内容作为对象的属性名，'='右边的内容作为对象对应属性的属性值
    }
  }
  return requestParams;
  /* this.urlParm = theRequest */
}
export default async (options) => { //传入的options是一个json对象
  let data = await isNextWork();
  if (!data) {
    return false;
  }
  let hasRedirected = store.state.isLogin.hasRedirected
  return new Promise((resolve, reject) => {
    let token = getToken() || '';
    // if (!options.dis_loading) {
    //   uni.showLoading({
    //     mask: true
    //   });
    // }
    // #ifdef H5
    const res = getUrlParm()
    token = res.token
    // #endif
    uni.request({
      url: options.url.includes('http') ? options.url : API_URL + options.url,
      method: options.method || "GET",
      data: options.data || {},
      header: {
        "token": getToken(),
        "Content-Type": "application/json",
      },
      success: res => {
        if (res.data.code == 'A0401' && !hasRedirected) {
          store.commit('isLogin/SET_REDIRECTED', true)
          removeToken()

          // uni.removeStorageSync('_token')
          // uni.removeStorageSync('_key')
          // uni.removeStorageSync('_profile')
          // uni.removeStorageSync('_openid')
          // uni.reLaunch({
          //   url: '/pages/login/login'
          // })
          return
        } else if (res.data.code != '00000') {
          uni.showToast({
            title: res.data.msg,
            icon: "none"
          })
          reject(res.data)
          return
        }
        // 请求的函数
        resolve(res.data);
      },
      fail: err => {
        console.log(err);
        if (err.errMsg.includes('request:fail timeout')) {
          uni.showToast({
            title: "网络超时，请稍后重试",
            icon: "none"
          })
        } else {
          uni.showToast({
            title: "系统繁忙，请稍后重试",
            icon: "none"
          })
        }

        resolve(err)
      },
      complete: res => {
        uni.hideLoading({
          noConflict: true
        })
      }
    })
  })
}