<template>
  <el-card>
    <el-result :icon="pageIcon" :title="pageTitle" sub-title="该功能模块开发中，敬请期待">
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">返回工作台</el-button>
      </template>
    </el-result>
  </el-card>
</template>
<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const pageTitle = computed(() => route.meta?.title || '功能')
const pageIcon = computed(() => 'warning')
</script>
