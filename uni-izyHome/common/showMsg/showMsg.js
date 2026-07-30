
function isObject(value) {
  return toString.call(value) === '[object Object]'
}

/** 封装uni.showModal
 * @param {content} content 输入内容
 * @param {title} 输入提示标题
 * @return {function} 返回promise函数
 */
function showModal() {
  if (isObject(arguments[0])) {
    var { title, content,confirmText,cancelText } = arguments[0]
  } else {
    var content = arguments[0]
  }
  return new Promise((resolve, reject) => {
    uni.showModal({
      title: title ?? "提示",
      content,
      confirmText: confirmText??'确定',
      cancelText: cancelText??'取消',
      success: (res) => {
        if (res.confirm) {
          resolve(true)
        } else if (res.cancel) {
          resolve(false)
        }
      },
    });
  })
}


/** 封装uni.showToast方法
 * @param {icon} icon 输入图标类型
 * @param {title} 输入提示标题内容
 * @return {function} 返回promise函数
 */
function showToast() {
  if (isObject(arguments[0])) {
    var { icon, title } = arguments[0]
  } else {
    var title = arguments[0]
  }
  return new Promise((resolve, reject) => {
    uni.showToast({
      title: title,
      icon: icon ?? 'none',
      duration: 2000,
    });
  })
}

export default {
  showModal, showToast
}

