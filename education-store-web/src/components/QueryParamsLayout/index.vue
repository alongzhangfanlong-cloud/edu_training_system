<!--
 * ---------------------------------------------
 * 查询表单组件包裹层
 * 用来处理查询参数单行/多行切换逻辑
 * ---------------------------------------------
-->

<template>
  <div class="query-params-container">
    <!-- 查询参数组 -->
    <div class="query-params-row" :class="{ collapse: paramsCollapse }">
      <div class="query-params-inner" ref="innerRef">
        <slot></slot>
      </div>
    </div>

    <!-- 查询按钮组 -->
    <div class="query-actions">
      <slot name="buttons"></slot>
      <div class="query-filter-collapse-icon" v-show="showCollapseIcon" @click="toggleCollapse">
        <span>{{ paramsCollapse ? "展开" : "收起" }}</span>
        <i class="ifont-arrow-down" :class="{ collapse: paramsCollapse }"></i>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import lodash from "lodash";

const paramsCollapse = ref(false);

const toggleCollapse = () => {
  paramsCollapse.value = !paramsCollapse.value;
};

// ## 监听container尺寸变化
// #region -------------------------------------------------------
onMounted(() => {
  startObserver();
});

onBeforeUnmount(() => {
  stopObserver();
});

const showCollapseIcon = ref(false);
const innerRef = ref(null);

// 单行高度
const sigleRowHeight = 50;

const resizeObserver = new ResizeObserver(entries => {
  for (let entry of entries) {
    sizeChange(entry.contentRect);
  }
});
const startObserver = () => {
  resizeObserver?.observe(innerRef.value);
};

const stopObserver = () => {
  resizeObserver?.disconnect();
};

const sizeChange = lodash.debounce(({ height }) => {
  if (height > sigleRowHeight) {
    showCollapseIcon.value = true;
  } else {
    showCollapseIcon.value = false;
  }
}, 20);
// #endregion  --------------------------
</script>

<style lang="less" scoped>
@import url("./index.less");
</style>
