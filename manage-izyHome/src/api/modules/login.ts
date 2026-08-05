import { Login } from "@/api/interface/index";
import { PORT1 } from "@/api/config/servicePort";
import authMenuList from "@/assets/json/authMenuList.json";
import authButtonList from "@/assets/json/authButtonList.json";
import http from "@/api";

/**
 * @name 登录模块
 */
// 用户登录
export const loginApi = (params: Login.ReqLoginForm) => {
  return http.post<Login.ResLogin>(PORT1 + `/login`, params, { loading: false }); // 正常 post json 请求  ==>  application/json
  // return http.post<Login.ResLogin>(PORT1 + `/login`, params, { loading: false }); // 控制当前请求不显示 loading
  // return http.post<Login.ResLogin>(PORT1 + `/login`, {}, { params }); // post 请求携带 query 参数  ==>  ?username=admin&password=123456
  // return http.post<Login.ResLogin>(PORT1 + `/login`, qs.stringify(params)); // post 请求携带表单参数  ==>  application/x-www-form-urlencoded
  // return http.get<Login.ResLogin>(PORT1 + `/login?${qs.stringify(params, { arrayFormat: "repeat" })}`); // get 请求可以携带数组等复杂参数
};

// 获取菜单列表
export const getAuthMenuListApi = () => {
  // return http.get<Menu.MenuOptions[]>(PORT1 + `/menu/list`, {}, { loading: false });
  // 如果想让菜单变为本地数据，注释上一行代码，并引入本地 authMenuList.json 数据
  // 删除这些name不是tabsIndex的路由
  return authMenuList;
};

// 获取按钮权限
export const getAuthButtonListApi = () => {
  // return http.get<Login.ResAuthButtons>(PORT1 + `/auth/buttons`, {}, { loading: false });
  // 如果想让按钮权限变为本地数据，注释上一行代码，并引入本地 authButtonList.json 数据
  return authButtonList;
};

// 用户退出登录
export const logoutApi = () => {
  return http.post(PORT1 + `/logout`);
};

export const handleLogin = (data) => {
  return http.post('/api/user/login', data)
}
// 注册
export const handleRegister = (data) => {
  return http.post('/api/user/register', data)
}
// 手机验证码 /api/user/smsValiCode/{cellphone}
export const handleSmsValiCode = (cellphone) => {
  return http.post('/api/user/smsValiCode/' + cellphone)
}
// 代理中心登录 /agent/api/account/login post请求
export const handleAgentLogin = (data) => {
  return http.post('/agent/api/account/login', data)
}
// 代理中心注册 /agent/api/account/register post请求
export const handleAgentRegister = (data) => {
  return http.post('/agent/api/account/register', data)
}
// 代理中心验证码 /agent/api/account/smsValiCode/{cellphone} post请求
export const handleAgentSmsValiCode = (cellphone) => {
  return http.post('/agent/api/account/smsValiCode/' + cellphone)
}