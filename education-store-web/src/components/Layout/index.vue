<!--
 * ---------------------------------------------
 * 页面布局Layout
 * ---------------------------------------------
-->

<template>
  <div class="app-wrapper">
    <!-- 左侧菜单栏 -->
    <div class="app-sider-menus" :class="{ 'sider-collapsed': siderIsCollapsed }">
      <div class="logo-info">
        <img src="@/assets/logo/logo.svg" alt="logo" />
        <strong v-if="!siderIsCollapsed">{{ systemTitle }}</strong>
      </div>
      <div class="menu-container">
        <div class="menu-container-inner">
          <SiderMenu />
        </div>
      </div>
    </div>

    <!-- 右侧内容栏 -->
    <div class="app-container">
      <div class="layout-header">
        <Header />
        <ViewTags />
      </div>
      <div class="layout-container">
        <div class="layout-container-inner">
          <router-view v-slot="{ Component, route }">
            <transition name="page-fade" mode="out-in">
              <keep-alive :include="cacheRoutes">
                <component :is="Component" :key="route.path" />
              </keep-alive>
            </transition>
          </router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import useSiderStateStore from "@/stores/siderState";
import UseViewTagsStore from "@/stores/viewTags.js";

import Header from "./components/Header";
import ViewTags from "./components/ViewTags";
import SiderMenu from "./components/SiderMenu";
// import RouterWrapper from "./components/RouterWrapper";

const siderStateStore = useSiderStateStore();
const siderIsCollapsed = computed(() => siderStateStore.siderIsCollapsed);

const viewTagsStore = UseViewTagsStore();
const cacheRoutes = computed(() => viewTagsStore.cacheTagNames);

const systemTitle = import.meta.env.VITE_DOCUMENT_TITLE;
</script>

<style lang="less" scoped>
@import url("./index.less");
</style>
