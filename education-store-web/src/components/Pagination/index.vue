<!-- 分页组件 -->
<template>
  <div :class="{ hidden: hidden }" class="pagination-container">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :layout="layout"
      :page-sizes="pageSizes"
      :background="background"
      v-bind="$attrs"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  total: { type: Number, default: 0 },
  page: { type: Number, default: 1 },
  limit: { type: Number, default: 20 },
  pageSizes: { type: Array, default: () => [10, 20, 50, 100] },
  layout: { type: String, default: 'total, sizes, prev, pager, next, jumper' },
  background: { type: Boolean, default: true },
  hidden: { type: Boolean, default: false },
});

const emit = defineEmits(['update:page', 'update:limit', 'pagination']);

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update:page', val),
});
const pageSize = computed({
  get: () => props.limit,
  set: (val) => emit('update:limit', val),
});

function handleSizeChange(val) {
  emit('pagination', { page: currentPage.value, limit: val });
}
function handleCurrentChange(val) {
  emit('pagination', { page: val, limit: pageSize.value });
}
</script>

<style lang="less" scoped>
.pagination-container {
  padding: 20px 0 0;
  display: flex;
  justify-content: flex-end;
  &.hidden { display: none; }
}
</style>
