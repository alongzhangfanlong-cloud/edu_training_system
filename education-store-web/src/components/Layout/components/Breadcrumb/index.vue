<!--
 * ---------------------------------------------
 * 面包屑导航
 * ---------------------------------------------
-->

<template>
  <el-breadcrumb separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="item in breadList" :key="item.path">
        <span>{{ item.meta?.title }}</span>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import useRouterStore from "@/stores/routers";
import { findTargetInArray } from "@/utils";

const route = useRoute();
const routerStore = useRouterStore();

const breadList = computed(() => {
  const currentPath = route.path;
  const allMenus = routerStore.totalMenus;

  const targetMenu = findTargetInArray(allMenus, "realPath", currentPath);

  const breadArray = targetMenu?.breadcrumbs || [];

  return breadArray;
});
</script>

<style lang="less" scoped>
@import url("./index.less");
</style>
