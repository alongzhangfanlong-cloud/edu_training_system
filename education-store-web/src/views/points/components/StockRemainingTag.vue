<!-- 库存剩余标签 — count < 5 时红色高亮警告 -->
<template>
  <span v-if="count != null" :class="{ 'stock-warn': count < 5 }">
    {{ count }}
  </span>
  <span v-else class="text-muted">—</span>
</template>

<script setup>
defineProps({
  count: { type: Number, default: null },
});
</script>

<style scoped lang="less">
.stock-warn {
  color: #f56c6c;
  font-weight: 600;
}
.text-muted {
  color: #c0c4cc;
}
</style>
