import { createApp } from "vue";

// 引入路由及路由守卫
import router from "./router";
// 添加路由守卫
import "./router/guard";

// 引入pinia
import { createPinia } from "pinia";

// 使用Element Plus UI库
import ElementPlus from "element-plus";
import zhCn from "element-plus/dist/locale/zh-cn";
import "element-plus/dist/index.css";

import App from "./App.vue";

// 注册自定义指令
import permission from "@/directives/permission";

import "./styles/themes.css";
import "./styles/element-override.less";
import "./styles/reset.css";
import "./styles/common.less";
import "@/assets/svgfont/svgfont.css";

// 创建pinia
const pinia = createPinia();

// 创建实例
const app = createApp(App);

app
  .use(router)
  .use(ElementPlus, {
    locale: zhCn
  })
  .use(pinia)
  .directive("permission", permission)
  .mount("#app");
