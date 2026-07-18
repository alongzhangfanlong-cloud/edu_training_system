<!--
 * ---------------------------------------------
 * 标签包裹层
 * ---------------------------------------------
-->

<template>
  <el-scrollbar ref="scrollContainer" :vertical="false" class="scroll-container" @wheel.prevent="handleScroll">
    <slot />
  </el-scrollbar>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";

const scrollContainer = ref(null);

// 鼠标中键滚动事件
const handleScroll = e => {
  const eventDelta = e.wheelDelta || -e.deltaY * 40;
  scrollContainer.value.$refs.wrapRef.scrollLeft = scrollContainer.value.$refs.wrapRef.scrollLeft - eventDelta / 4;
};
</script>

<style lang="less" scoped>
:deep(.el-scrollbar__bar) {
  bottom: -6px;
}

.el-scrollbar {
  overflow: visible;
}
</style>
