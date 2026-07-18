/**
 * 路由守卫设置
 */

import { getToken } from "@/utils/validate";
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import useRouterStore from "@/stores/routers";
import { useUserStore } from "@/stores/user";
import router from "./index";

// 不需要登录的页面
const whitelist = ["/login"];

router.beforeEach(async (to, from, next) => {
  NProgress.start();

  if (isWhiteList(to.path)) {
    next();
  } else {
    if (!getToken()) {
      // 跳转到登录页面
      next("/login");
    } else {
      const permissionStore = useRouterStore();
      const userStore = useUserStore();

      // 加载用户权限信息（页面刷新时 userStore 为空）
      if (!userStore.permissions || userStore.permissions.length === 0) {
        await userStore.loadUserInfo();
      }

      // 用户菜单权限只加载一次就可以了
      if (!permissionStore.isLoadedFlag) {
        const routes = await permissionStore.formatRoutes();

        routes.forEach(route => {
          router.addRoute("topLayout", route);
        });

        // 添加完路由需要重新执行一次路由跳转，否则会出现空白页面
        next({ ...to, replace: true });
      } else {
        next();
      }
    }
  }
});

router.afterEach(() => {
  NProgress.done();
});

// ## 方法工具
// #region -------------------------------------------------------
const isWhiteList = path => {
  return whitelist.some(pattern => isPathMatch(pattern, path));
};

const isPathMatch = (pattern, path) => {
  const regexPattern = pattern.replace(/\//g, "\\/").replace(/\*\*/g, ".*").replace(/\*/g, "[^\\/]*");
  const regex = new RegExp(`^${regexPattern}$`);
  return regex.test(path);
};
// #endregion  --------------------------
