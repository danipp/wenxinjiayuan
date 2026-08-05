import router from "@/routers/index";
import { LOGIN_URL } from "@/config";
import { RouteRecordRaw } from "vue-router";
import { ElNotification } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
import { useAuthStore } from "@/stores/modules/auth";
import { staticRouter, errorRouter } from "./staticRouter";
// 引入 views 文件夹下所有 vue 文件
const modules = import.meta.glob("@/views/**/*.vue");

/**
 * @description 初始化动态路由
 */
export const initDynamicRouter = async () => {

  const userStore = useUserStore();
  const authStore = useAuthStore();

  try {
    if (!authStore.authMenuListGet.length) {
      // 1. 如果菜单数据为空（首次加载或持久化失效），重新请求接口
      await authStore.getAuthMenuList();
      await authStore.getAuthButtonList();
    }
    // 2.判断当前用户有没有菜单权限
    if (!authStore.authMenuListGet.length) {
      ElNotification({
        title: "无权限访问",
        message: "当前账号无任何菜单权限，请联系系统管理员！",
        type: "warning",
        duration: 3000
      });
      userStore.setToken("");
      router.replace(LOGIN_URL);
      return Promise.reject("No permission");
    }

    // 3.添加动态路由
    authStore.flatMenuListGet.forEach(item => {
      item.children && delete item.children;
      if (item.component && typeof item.component == "string") {
        item.component = modules["/src/views" + item.component + ".vue"];
      }
      if (item.meta.isFull) {
        router.addRoute(item as unknown as RouteRecordRaw);
      } else {
        router.addRoute("layout", item as unknown as RouteRecordRaw);
      }
    });
  } catch (error) {
    // 当按钮 || 菜单请求出错时，重定向到登陆页
    userStore.setToken("");
    router.replace(LOGIN_URL);
    return Promise.reject(error);
  }
};
// 获取存储了的路由
export const getStoredRouter = () => {
  const authItems = JSON.parse(localStorage.getItem("geeker-auth") || "{}");
  const flatMenuListGet = authItems.authMenuList || [];
  if (!flatMenuListGet.length) {
    console.log('调接口取路由');
    return [...staticRouter]
  }
  const newRouter = mapRouteComponents(flatMenuListGet);
  // 将flatMenuListGet添加到staticRouter路由中的layout的children里
  staticRouter.forEach(item => {
    if (item.name === 'layout') {
      item.children.push(...newRouter)
    }
  })
  console.log('取缓存路由', staticRouter);
  return [...staticRouter, ...errorRouter]
}

const mapRouteComponents = (routes: any[]): any[] => {
  // 遍历路由数组中的每个路由项
  return routes.map(route => {
    // 创建一个新对象，避免直接修改原数据
    const processedRoute = { ...route };

    // 处理当前路由项的component
    if (processedRoute.component && typeof processedRoute.component === "string") {
      // 处理路径拼接，确保格式正确
      const componentPath = processedRoute.component.trim();
      // 处理路径开头的斜杠，避免重复
      const fullPath = `/src/views${componentPath.startsWith('/') ? '' : '/'}${componentPath}.vue`;

      // 调试用：检查组件路径是否存在
      if (!modules[fullPath]) {
        console.warn(`警告：未找到组件文件 - ${fullPath}`);
      }

      // 映射到实际组件
      processedRoute.component = modules[fullPath];
    }

    // 递归处理子路由（如果存在）
    if (processedRoute.children && Array.isArray(processedRoute.children) && processedRoute.children.length > 0) {
      processedRoute.children = mapRouteComponents(processedRoute.children);
    }

    return processedRoute;
  });
};
