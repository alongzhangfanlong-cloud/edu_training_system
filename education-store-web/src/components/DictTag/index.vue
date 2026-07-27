<!--
 * 字典标签组件
 * 根据字典数据列表和值显示对应标签
 *
 * 用法:
 *   <dict-tag :options="dict.type.sys_normal_disable" :value="row.status" />
-->
<template>
  <span>
    <template v-for="item in options" :key="item.value">
      <el-tag v-if="item.value === String(value)" :type="tagType">
        {{ item.label }}
      </el-tag>
    </template>
    <span v-if="!matched">{{ value }}</span>
  </span>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  options: { type: Array, default: () => [] },
  value: { type: [String, Number], default: '' },
});

const matched = computed(() => {
  return props.options.some((item) => String(item.value) === String(props.value));
});

const tagType = computed(() => {
  const item = props.options.find((d) => String(d.value) === String(props.value));
  if (!item) return undefined;
  const cls = item.listClass || item.cssClass || '';
  if (cls.includes('success')) return 'success';
  if (cls.includes('warning')) return 'warning';
  if (cls.includes('danger')) return 'danger';
  if (cls.includes('info') || cls.includes('primary')) return 'info';
  return undefined;
});
</script>
