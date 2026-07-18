<!--
 * ---------------------------------------------
 * 菜单项
 * ---------------------------------------------
-->

<template>
  <template v-if="hasChildren">
    <el-sub-menu :index="item.realPath" popper-append-to-body>
      <template #title>
        <el-icon>
          <i :class="item.meta?.icon || 'ifont-cherry'"></i>
        </el-icon>
        <span>{{ item.meta?.title || item.name }}</span>
      </template>

      <MenuItem v-for="child in item.children" :key="child.realPath" :item="child" />
    </el-sub-menu>
  </template>

  <template v-else>
    <el-menu-item :index="item.realPath">
      <el-icon>
        <i :class="item.meta?.icon || 'ifont-cherry'"></i>
      </el-icon>
      <span>{{ item.meta?.title || item.name }}</span>
    </el-menu-item>
  </template>
</template>

<script setup>
import { computed } from 'vue';
import MenuItem from './menuItem';

const props = defineProps({
  item: {
    type: Object,
    required: true,
    default: () => ({})
  }
});

// 展示的菜单，考虑是否是菜单组
const hasChildren = computed(() => {
  const showingChildren = props.item?.children?.filter(item => {
    if (item.hidden) {
      return false;
    }
    return true;
  });

  // 没有children
  if (!showingChildren || showingChildren.length === 0) {
    return false;
  }

  return true;
});
</script>

<style lang="less" scoped>
</style>
