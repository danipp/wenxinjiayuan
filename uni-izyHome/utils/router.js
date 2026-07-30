const toString = Object.prototype.toString

function isObject(value) {
  return toString.call(value) === '[object Object]'
}

function isString(value) {
  return toString.call(value) === '[object String]'
}

function isDefault(value) {
  return value === void 0
}

function openPage(args) {
  let name, query = {},
    queryStr = null,
    path, type
  switch (true) {
    case isObject(arguments[1]):
      name = arguments[0]
      query = arguments[1]
      type = arguments[2] ? arguments[2] : 'navigateTo'
      break
    case isString(args):
      type = 'navigateTo'
      name = args
      break
    default:
      throw new Error('参数必须是对象或者字符串')
  }
  if (isObject(query) && JSON.stringify(query) !== "{}") {
    queryStr = encodeURIComponent(JSON.stringify(query))
  } else if (isObject(query) && JSON.stringify(query) == "{}") {
    queryStr = null
  } else {
    throw new Error('query数据必须是Object')
  }
  if (['navigateTo', 'switchTab', 'reLaunch', 'redirectTo'].includes(type)) {
    // throw new Error(`name:${name}里面的type必须是以下的值['navigateTo', 'switchTab', 'reLaunch', 'redirectTo']`)
    var url;
    url = queryStr ? `${name}?query=${queryStr}` : name
    return new Promise((resolve, reject) => {
      uni[type]({
        url,
        success: resolve,
        fail: reject
      })
    })
  } else {
    throw new Error(`name:${name}里面的type必须是以下的值['navigateTo', 'switchTab', 'reLaunch', 'redirectTo']`)
  }

}

function back(index = 1) {
  return new Promise((resolve, reject) => {
    uni.navigateBack({
      delta: index
    });
  })
}

function parseURL() {
  let query;
  // #ifdef MP-WEIXIN || H5
  query = this.$root.$mp.query.query
  console.log(query,'query');
  //#endif
  //#ifdef APP-PLUS
  let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
  let curRoute = routes[routes.length - 1].route; //获取当前页面路由
  let curParam = routes[routes.length - 1].options; //获取路由参数
  // 拼接参数
  query = "";
  for (let key in curParam) {
    query = curParam[key];
  }
  //#endif
  if (query) {
    return JSON.parse(decodeURIComponent(query))
  } else {
    return {}
  }
}
function MinRouter(options) {
  if (!(this instanceof MinRouter)) {
    throw Error("MinRouter是一个构造函数，应该用`new`关键字调用")
  }
  isDefault(options) && (options = {})
  this.options = options
  this._router = options.routes || []
}
MinRouter.prototype.openPage = openPage
MinRouter.prototype.parseURL = parseURL
MinRouter.prototype.back = back
export default new MinRouter()