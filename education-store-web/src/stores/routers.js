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

    // 将后端菜单字段映射到 Vue Router meta（保留后端已有字段）
    route.meta = {
      ...route.meta,
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
  let foundPath = null;
  for (const path in modules) {
    const dir = path.split("views/")[1].split(".vue")[0];
    if (dir === view || dir === view + '/index') {
      foundPath = path;
      res = () =>
        modules[path]()
          .then(r => {
            if (r.default) {
              r.default.name = name;
            }
            return r;
          })
          .catch(err => {
            console.error(`[Router] Failed to load component: ${view} (name=${name}), path=${path}`, err);
            return {
              default: {
                name: name + 'Error',
                template: `<div style="padding:40px;text-align:center;color:#f56c6c;">
                  <h3>组件加载失败</h3>
                  <p>组件路径: ${view}</p>
                  <p>文件: ${path}</p>
                  <p style="color:#909399;font-size:12px;margin-top:10px;">${err?.message || '未知错误'}</p>
                </div>`
              }
            };
          });
      break;
    }
  }
  if (!res) {
    console.error(`[Router] Component not found: ${view} (name=${name}). Available paths:`, Object.keys(modules).map(p => p.split("views/")[1]?.split(".vue")[0]).filter(Boolean));
    res = () => Promise.resolve({
      default: {
        name: name + 'NotFound',
        template: `<div style="padding:40px;text-align:center;color:#e6a23c;">
          <h3>组件文件不存在</h3>
          <p>组件路径: ${view}</p>
          <p style="color:#909399;font-size:12px;">请检查 src/views/${view}/index.vue 是否存在</p>
        </div>`
      }
    });
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
