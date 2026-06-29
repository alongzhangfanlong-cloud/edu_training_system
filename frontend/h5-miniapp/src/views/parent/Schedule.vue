<template>
  <div class="page">
    <van-nav-bar title="孩子课表" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-field v-model="studentId" label="学员ID" type="digit" placeholder="输入学员ID" />
      <van-button type="primary" size="small" style="margin:8px 0" @click="loadSchedules">查询</van-button>
      <van-cell v-for="c in list" :key="c.id" :title="c.courseName || '课程#'+c.courseId" :label="`${c.date} ${c.startTime}-${c.endTime} | ${c.teacherName||''}`" />
      <van-empty v-if="!list.length && loaded" description="暂无课程" />
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import request from '../../utils/request'
const list = ref([]); const studentId = ref(''); const loaded = ref(false)

async function loadSchedules() {
  if (!studentId.value) return
  try {
    const d = new Date().toISOString().slice(0, 10)
    const r = await request.get('/schedules', {
      params: {
        startDate: d,
        endDate: new Date(Date.now() + 7 * 86400000).toISOString().slice(0, 10),
        studentId: studentId.value
      }
    })
    list.value = r?.schedules || []
    loaded.value = true
  } catch (e) {}
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}</style>
