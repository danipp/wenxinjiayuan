<script>
import Vue from "vue";
export default {
  globalData: {
    API_URL: '',
    hasRedirected: false,
    orderStatus: {
      // 支付状态(0待支付,1支付中,2已关闭,3已取消,4已完成,5已作废,6退款中,7退款成功,8退款失败,9退款取消)
      0: '待支付',
      1: '支付中',
      2: '已关闭',
      3: '已取消',
      4: '已完成',
      5: '已作废',
      6: '退款中',
      7: '退款成功',
      8: '退款失败',
      9: '退款取消'
    },
    math(num1, num2, symbol) {
      // 将输入转换为数字
      const number1 = parseFloat(num1);
      const number2 = parseFloat(num2);

      // 检查是否为有效数字
      if (isNaN(number1) || isNaN(number2)) {
        throw new Error("Invalid number input");
      }

      // 定义一个函数来处理浮点数精度问题
      function preciseCalculation(a, b, operation) {
        const factor = Math.pow(10, 10); // 使用一个大的因子来避免精度问题
        switch (operation) {
          case '+':
            return (Math.round(a * factor) + Math.round(b * factor)) / factor;
          case '-':
            return (Math.round(a * factor) - Math.round(b * factor)) / factor;
          case '*':
            return (Math.round(a * factor) * Math.round(b * factor)) / (factor * factor);
          case '/':
            if (b === 0) {
              throw new Error("Division by zero");
            }
            return (Math.round(a * factor) / Math.round(b * factor));
          default:
            throw new Error("Invalid operation symbol");
        }
      }

      // 执行运算并返回结果
      return preciseCalculation(number1, number2, symbol);
    }
  },
  onLaunch: function () {
    uni.getSystemInfo({
      success: function (e) {
        let model = ["X", "XR", "XS", "11", "12", "13", "14", "15", "16"];
        for (let item of model) {
          //适配iphoneX以上的底部，给tabbar一定高度的padding-bottom
          if (
            e.model.indexOf(item) != -1 &&
            e.model.indexOf("iPhone") != -1
          ) {
            Vue.prototype.paddingBottomHeight = 40;
            break; // Exit the loop once a match is found
          } else {
            Vue.prototype.paddingBottomHeight = 20;
          }
        }
        Vue.prototype.CustomBar = e.statusBarHeight + 50;

        // #ifndef MP
        Vue.prototype.StatusBar = e.statusBarHeight;
        if (e.platform == "android") {
          Vue.prototype.CustomBar = e.statusBarHeight + 50;
        } else {
          Vue.prototype.CustomBar = e.statusBarHeight + 45;
        }
        // #endif

        // #ifdef MP-WEIXIN || MP-QQ
        Vue.prototype.StatusBar = e.statusBarHeight;
        let capsule = wx.getMenuButtonBoundingClientRect();
        if (capsule) {
          Vue.prototype.Custom = capsule;
          // Vue.prototype.capsuleSafe = uni.upx2px(750) - capsule.left + uni.upx2px(750) - capsule.right;
          Vue.prototype.CustomBar =
            capsule.bottom + capsule.top - e.statusBarHeight;
        } else {
          Vue.prototype.CustomBar = e.statusBarHeight + 50;
        }
        // #endif

        // #ifdef MP-ALIPAY
        Vue.prototype.StatusBar = e.statusBarHeight;
        Vue.prototype.CustomBar = e.statusBarHeight + e.titleBarHeight;
        // #endif
      },
    });
    // #ifdef MP-WEIXIN
    // 在App.vue或者是主要的JS文件中
    // 小程序启动后检查更新
    const updateManager = uni.getUpdateManager();
    updateManager.onCheckForUpdate(function (res) {
      // 请求完新版本信息的回调
      if (res.hasUpdate) {
        // 有新版本，提示用户更新
        uni.showModal({
          title: '更新提示',
          content: '发现新版本，是否重启应用？',
          success: function (res) {
            if (res.confirm) {
              // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
              updateManager.applyUpdate();
            }
          }
        });
      }
    });
    // updateManager.onUpdateReady(function () {
    //   // 新版本已经准备好，等待用户重启
    //   uni.showModal({
    //     title: '更新提示',
    //     content: '新版本已经准备好，是否重启应用？',
    //     success: function (res) {
    //       if (res.confirm) {
    //         // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
    //         updateManager.applyUpdate();
    //       }
    //     }
    //   });
    // });
    uni.getNetworkType({
      success: (res) => {
        if (res.networkType === 'none') {
          uni.showToast({
            title: "当前无网络,请检查网络环境",
            icon: "none"
          })
        }
      }
    });
    // #endif
  },
  methods: {

  },
  onShow: function () {
    console.log("App Show");
  },
  onHide: function () {
    console.log("App Hide");
  },
};
</script>
<style lang="scss">
/*每个页面公共css */
@import "./colorui/main.css";
// @import "./colorui/icon.css";
@import "@/uni_modules/uview-ui/index.scss";

uni-page-body,
html,
body,
page {}

::v-deep .u-sticky {
  // #ifdef H5
  top: 0 !important;
  // #endif
}

image {
  will-change: transform;
}

::v-deep .u-tabbar__content {
  // height: 140rpx;
  padding-top: 20rpx;
  justify-content: center;
  border-radius: 30rpx 30rpx 0rpx 0rpx;
  box-shadow: 0rpx 17rpx 87rpx 0rpx rgba(79, 78, 77, 0.15);
}
</style>