/**
 * 路由 store — 动态路由加载，对齐后端 getRouters
 */
import { defineStore } from "pinia";
import { ref } from "vue";
import { getRouters } from "@/api/system/user";
import lodash from "lodash";

// 匹配views里面所有的.vue文件
const modules = import.meta.glob("../views/**/*.vue");

export default defineStore("routers", () => {
  const isLoadedFlag = ref(false);
  const totalMenus = ref([]);
  const permissionRouters = ref([]);

  const formatRoutes = async () => {
    let result = [];
    const res = await getRouters();

    isLoadedFlag.value = true;

    if (res?.code === 200 && res?.data) {
      const flatData = lodash.cloneDeep(res.data);
      // 补充组件路径映射
      completeAsyncRouter(flatData);
      totalMenus.value = formatMenuInfo(flatData);
      result = flatData;
    }

    permissionRouters.value = result;
    return result;
  };

  return { totalMenus, permissionRouters, formatRoutes, isLoadedFlag };
});

// 遍历后台传来的路由字符串，转换为组件对象
const completeAsyncRouter = (asyncRouters) => {
  return asyncRouters.forEach(route => {
    if (route.children) {
      route.children = filterChildren(route.children, route);
    }

    // 将后端菜单字段映射到 Vue Router meta
    route.meta = {
      title: route.meta?.title || route.name,
      icon: route.meta?.icon || route.icon
    };

    if (route.component) {
      if (route.component === "Layout") {
        delete route.component;
      } else {
        route.component = getComponent(route.component, route.name);
      }
    }

    if (route.children && route.children.length) {
      completeAsyncRouter(route.children);
    } else {
      delete route["children"];
      delete route["redirect"];
    }
  });
};

const getComponent = (view, name) => {
  let res;
  for (const path in modules) {
    const dir = path.split("views/")[1].split(".vue")[0];
    if (dir === view || dir === view + '/index') {
      res = () =>
        modules[path]().then(r => {
          if (r.default) {
            r.default.name = name;
          }
          return r;
        });
    }
  }
  return res;
};

const filterChildren = (childrenMap, parentRouter) => {
  return childrenMap.map(el => {
    if (parentRouter && !el.path.startsWith("/") && !el.path.startsWith("http")) {
      el.path = parentRouter.path + "/" + el.path;
    }
    return el;
  });
};

const formatMenuInfo = (menus, parentPath = "", parentBreadcrumbs = []) => {
  if (!Array.isArray(menus)) return [];

  return menus.map(menu => {
    const currentBreadcrumb = {
      name: menu.name,
      path: menu.path,
      hidden: menu.hidden,
      component: menu.component,
      meta: menu.meta
    };

    if (menu.redirect) currentBreadcrumb.redirect = menu.redirect;
    if (menu.alwaysShow) currentBreadcrumb.alwaysShow = menu.alwaysShow;

    const breadcrumbs = [...parentBreadcrumbs, currentBreadcrumb];

    let realPath = menu.path;
    if (parentPath && !menu.path.startsWith("/") && !menu.path.startsWith("http")) {
      realPath = `${parentPath.endsWith("/") ? parentPath : parentPath + "/"}${menu.path}`;
    }
    if (!realPath.startsWith("/") && !realPath.startsWith("http")) {
      realPath = "/" + realPath;
    }

    const formattedMenu = {
      ...menu,
      realPath,
      breadcrumbs
    };

    if (menu.children && menu.children.length > 0) {
      formattedMenu.children = formatMenuInfo(menu.children, realPath, breadcrumbs);
    }

    return formattedMenu;
  });
};
