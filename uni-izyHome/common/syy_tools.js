/** 时间戳转换年月日
 * @param {number} time 时间戳
 * @param {string} icon 间隔符号
 * @param {number} length 1输出年月日,2输出时分秒，其他输出全部
 * @return {string} 时间戳转时间
 * @use mytime(时间戳, "-", 1)
 */
export function mytime(time, icon, length) {
  if (!time) {
    return "";
  }
  let TimeText = "";
  let year, month, day, hour, minute, second;
  const date = new Date(time);
  year = date.getFullYear();
  month = date.getMonth() + 1;
  day = date.getDate();
  hour = date.getHours();
  hour = hour > 9 ? hour : "0" + hour;
  minute = date.getMinutes();
  minute = minute > 9 ? minute : "0" + minute;
  second = date.getSeconds();
  second = second > 9 ? second : "0" + second;
  if (length == 1) {
    TimeText = year + icon + month + icon + day;
  } else if (length == 2) {
    TimeText = hour + ":" + minute + ":" + second;
  } else {
    TimeText =
      year +
      icon +
      month +
      icon +
      day +
      " " +
      hour +
      ":" +
      minute +
      ":" +
      second;
  }
  return TimeText;
}
export function pay(result) {
  console.log(result, 'result');
  // #ifdef  MP-WEIXIN	
  return new Promise((resolve, rej) => {
    uni.showLoading({
      mask: true,
    })
    uni.requestPayment({ // 这里是官方需要的参数--一般是后端获取
      provider: 'wxpay',
      timeStamp: result.timeStamp,
      nonceStr: result.nonceStr,
      package: result.package,
      signType: result.signType,
      paySign: result.paySign,
      success: res => {
        resolve(res)
      },
      fail: res => {
        resolve({
          msg: '支付失败',
          code: 0,
          res
        })
        uni.showToast({
          title: '支付失败',
          icon: 'none'
        })
      },
    });
  })
  // #endif
}
/**
 * 函数防抖动封装。
 * @param {Function} func 需要被防抖的函数。
 * @param {number} wait 防抖时间，单位毫秒，默认为500。
 * @return {Function} 返回一个经过防抖处理的函数。
 */
let timer;
export function debounce(func, wait = 1000) {
  if (typeof func !== 'function') {
    throw new TypeError('Expected the first argument to be a function');
  }
  if (typeof wait !== 'number' || wait < 0) {
    throw new TypeError('Expected the second argument to be a non-negative number');
  }
  return function (...args) {
    if (timer) {
      clearTimeout(timer);
    }

    const callNow = !timer;
    timer = setTimeout(() => {
      timer = null;
    }, wait);

    if (callNow) {
      func.apply(this, args);
    }
  };
}
// 写个跟防抖一样用法的节流函数
let lastTime = 0;
export function throttle(func, wait = 500) {
  if (typeof func !== 'function') {
    throw new TypeError('Expected the first argument to be a function');
  }
  if (typeof wait !== 'number' || wait < 0) {
    throw new TypeError('Expected the second argument to be a non-negative number');
  }
  return function (...args) {
    const now = Date.now();
    if (now - lastTime >= wait) {
      func.apply(this, args);
      lastTime = now;
    }
  };
}

export function showTips() {
  return new Promise((resolve, reject) => {
    let token = uni.getStorageSync('_token');
    if (uni.getPrivacySetting && token) {
      uni.getPrivacySetting({
        success: async res => {
          if (res.needAuthorization) {
            uni.showModal({
              title: '授权',
              content: '需要您阅读并同意《用户隐私保护提示》，才能正常使用该功能，是否同意？',
              success: (res) => {
                if (res.confirm) {
                  uni.requirePrivacyAuthorize({
                    success: (res) => {
                      resolve(true)
                    },
                    fail: (res) => {
                      console.log(res, 'error');
                      resolve(false)
                    }
                  })
                } else {
                  resolve(false)
                }
              }
            })
          } else {
            console.log('已授权')
            resolve(true)
          }
        },
        fail: () => {},
        complete: () => {}
      })
    }
  })
}

export function getEleInfo(ele, that) {
  return new Promise((resolve, reject) => {
    const query = uni.createSelectorQuery().in(that);
    query.select(ele).boundingClientRect(data => {
      if (data) {
        // data.height 就是元素的高度
        resolve(data)
      }
    }).exec();
  })
}


/**
 * 验证电子邮箱格式
 */
export function email(value) {
  return /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(
    value
  );
}

/**
 * 验证手机格式
 */
// 写一个验证手机号码的正则表达式
export function phone(value) {
  return /^1[3456789]\d{9}$/.test(value);
}
/**
 * 验证身份证号码
 */
export function idCard(value) {
  return /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/.test(
    value
  );
}

/**
 * 只能输入字母
 */
export function letter(value) {
  return /^[a-zA-Z]*$/.test(value);
}

/**
 * 只能是字母或者数字
 */
export function enOrNum(value) {
  //英文或者数字
  let reg = /^[0-9a-zA-Z]*$/g;
  return reg.test(value);
}

/**
 * 是否对象属性为空
 */
export function isEmpty(params, warning = "请填写完整信息") {
  let flag = true;

  for (var key in params) {
    if (params[key] != '0' && !params[key]) {
      showToast(warning)
      return false; // 终止程序
    }
  }

  return flag;
}