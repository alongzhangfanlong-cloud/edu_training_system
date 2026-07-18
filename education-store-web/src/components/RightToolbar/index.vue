<!--
 * 右侧工具栏 - 搜索切换 + 刷新 + 列显隐控制
 *
 * 用法:
 *   <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns" />
-->
<template>
  <div class="top-right-btn">
    <el-row>
      <el-tooltip content="刷新" placement="top">
        <el-button circle size="small" @click="emit('queryTable')">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </el-tooltip>
      <el-tooltip content="搜索" placement="top" v-if="showSearch !== undefined">
        <el-button circle size="small" @click="emit('update:showSearch', !showSearch)">
          <el-icon><Search /></el-icon>
        </el-button>
      </el-tooltip>
      <el-tooltip content="列设置" placement="top" v-if="columns">
        <el-dropdown trigger="click">
          <el-button circle size="small">
            <el-icon><Menu /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="col in columns" :key="col.key">
                <el-checkbox v-model="col.visible" :label="col.label" @change="emit('queryTable')" />
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-tooltip>
    </el-row>
  </div>
</template>

<script setup>
import { Refresh, Search, Menu } from '@element-plus/icons-vue';

const props = defineProps({
  showSearch: { type: Boolean, default: true },
  columns: { type: Array, default: null },
});

const emit = defineEmits(['update:showSearch', 'queryTable']);
</script>

<style lang="less" scoped>
.top-right-btn {
  float: right;
  margin-left: auto;
}
</style>
