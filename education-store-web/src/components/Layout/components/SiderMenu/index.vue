<!--
 * ---------------------------------------------
 * 侧边菜单
 * ---------------------------------------------
-->

<template>
  <el-scrollbar>
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :background-color="'var(--sider-background-color)'"
      :text-color="'var(--sider-text-color)'"
      :unique-opened="true"
      :collapse-transition="false"
      mode="vertical"
      @select="handleSelect"
    >
      <MenuItem v-for="(route, index) in siderMenus" :key="route.path + index" :item="route" :base-path="route.path" />
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import useRouterStore from "@/stores/routers";
import useSiderStateStore from "@/stores/siderState";

import MenuItem from "./menuItem";

const route = useRoute();
const router = useRouter();
const routerStore = useRouterStore();
const siderStateStore = useSiderStateStore();

const isCollapse = computed(() => siderStateStore.siderIsCollapsed);

const activeMenu = computed(() => route.path);
const siderMenus = computed(() => routerStore.totalMenus);

const handleSelect = path => {
  router.push(path);
};
</script>

<style lang="less" scoped>
@import url("./index.less");
</style>
