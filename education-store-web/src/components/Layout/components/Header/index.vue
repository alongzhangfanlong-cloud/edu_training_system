<!--
 * ---------------------------------------------
 * 顶部Header组件
 * ---------------------------------------------
-->

<template>
  <div class="header-section">
    <div class="header-left">
      <!-- 侧边菜单折叠按钮 -->
      <div class="collapsed-button" @click="toggleSiderCollapsed">
        <i :class="collapsedIcon"></i>
      </div>

      <!-- 面包屑导航 -->
      <Breadcrumb />
    </div>
    <div class="header-right">
      <Avatar />
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import useSiderStateStore from "@/stores/siderState";

import Breadcrumb from "../Breadcrumb";
import Avatar from "../Avatar";

// 侧边菜单栏折叠状态
const siderStateStore = useSiderStateStore();

const collapsedIcon = computed(() => (siderStateStore.siderIsCollapsed ? "ifont-indent" : "ifont-outdent"));

const toggleSiderCollapsed = siderStateStore.toggleSiderCollapsed;
</script>

<style lang="less" scoped>
@import url("./index.less");
</style>
