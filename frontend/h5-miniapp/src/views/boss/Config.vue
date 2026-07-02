<template>
  <div class="page">
    <van-nav-bar title="系统配置" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell-group inset>
        <van-cell v-for="c in configs" :key="c.key" :title="c.key" :label="c.description">
          <template #value><span style="font-weight:600;color:#1989fa">{{ c.value }}</span></template>
        </van-cell>
      </van-cell-group>
      <van-empty v-if="!configs.length" description="暂无配置" />
      <p style="color:#999;font-size:12px;text-align:center;padding:12px">修改配置请在PC管理后台操作</p>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const configs = ref([])
onMounted(async () => {
  try {
    const data = await request.get('/system-configs')
    if (data && typeof data === 'object') {
      configs.value = Object.entries(data).map(([key, val]) => ({
        key,
        value: val?.configValue || val?.config_value || val,
        description: val?.description || ''
      }))
    }
  } catch(e) {}
})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}</style>
