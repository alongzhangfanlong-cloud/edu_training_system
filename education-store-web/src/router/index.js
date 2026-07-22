// router 模块 定义路由
import {
  createRouter, // 创建路由实例的工厂函数
  createWebHistory
} from "vue-router";

export const routes = [
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/login/index.vue"),
    meta: {}
  },
  {
    path: "/",
    name: "topLayout",
    component: () => import("@/components/Layout/index.vue"),
    children: [
      {
        path: "/",
        redirect: "/dashboard"
      },
      {
        // 用来刷新页面的过程页面
        path: "/freshing",
        name: "freshing",
        component: () => import("@/views/freshing/index.vue"),
        meta: {
          noCache: true, // 不缓存
          noTags: true // 不展示在标签栏
        }
      }
    ]
  },
  {
    path: "/:pathMatch(.*)*",
    // 💢注意这里的name属性，如果设置，那么这个路由匹配的优先级就会很高
    // 💢会在动态生成路由后第一次匹配时优先匹配到这个路由，所以一定不要加
    // name: "not-found",
    meta: {},
    component: () => import("@/views/404/index.vue")
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes: routes
});

export default router;
