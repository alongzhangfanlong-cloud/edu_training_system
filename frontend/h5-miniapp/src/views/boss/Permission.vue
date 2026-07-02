<template>
  <div class="page">
    <van-nav-bar title="权限管理" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell-group inset v-for="r in roles" :key="r.roleCode">
        <van-cell :title="r.roleName" :label="r.permissions?.join(', ') || '无权限'" />
      </van-cell-group>
      <van-empty v-if="!roles.length" description="暂无数据" />
      <p style="color:#999;font-size:12px;text-align:center;padding:12px">编辑权限请在PC管理后台操作</p>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const roles = ref([])
onMounted(async () => {
  try {
    const data = await request.get('/permissions/role-permissions')
    if (Array.isArray(data)) {
      roles.value = data.map(r => ({
        roleCode: r.roleCode || r.role_code,
        roleName: r.roleName || r.role_name,
        permissions: (r.permissions || []).map(p => p.permName || p.perm_name || p)
      }))
    }
  } catch(e) {}
})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}</style>
