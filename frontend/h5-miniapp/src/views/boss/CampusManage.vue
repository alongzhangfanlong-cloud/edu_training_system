<template>
  <div class="page">
    <van-nav-bar title="校区管理" left-text="返回" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell v-for="c in list" :key="c.id" :title="c.campus_name" :label="c.address" :value="c.phone" is-link />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
const list = ref([])
onMounted(async () => { try { list.value = await request.get('/campuses') } catch(e){} })
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
