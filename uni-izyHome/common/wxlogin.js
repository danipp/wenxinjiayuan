let code;
async function getUserInfo() {
  return new Promise((resolve, reject) => {
    uni.login({
      provider: "weixin",
      success: (res) => {
        code = res.code
        console.log('获取code', res);
        if (res.errMsg.indexOf("ok") != -1) {
          uni.showModal({
            title: '温馨提示',
            content: '亲，授权微信登录后才能正常使用小程序功能',
            success(res) {
              //如果用户点击了确定按钮
              if (res.confirm) {
                uni.getUserProfile({
                  desc: '获取你的昵称、头像、地区及性别',
                  success: (res) => {
                    resolve(res)
                  },
                  fail: res => {
                    console.log('fail', res);
                    //拒绝授权
                    uni.showToast({
                      title: '您拒绝了请求,不能正常使用小程序',
                      icon: 'none',
                      duration: 2000
                    });
                    return;
                  }
                });
              } else if (res.cancel) {
                //如果用户点击了取消按钮
                // console.log(3);
                uni.showToast({
                  title: '您拒绝了请求,不能正常使用小程序',
                  icon: 'error',
                  duration: 2000
                });
                return;
              }
            }
          });
        }
      }
    });

  })
}
/* 用户授权手机号 */
function getPhoneNumber(e) {

}
export {
  getPhoneNumber,
  getUserInfo
}
