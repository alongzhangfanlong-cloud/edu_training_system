<template>
  <div class="page">
    <van-nav-bar title="我的课表" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell v-for="c in list" :key="c.id" :title="c.courseName || '课程#'+c.courseId" :label="`${c.date} ${c.startTime}-${c.endTime}`" :value="c.teacherName||''" />
      <van-empty v-if="!list.length" description="暂无课程安排" />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const list = ref([])
const studentId = ref(null)

onMounted(async () => {
  try {
    // 从登录信息获取用户手机号，查找对应学员ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userPhone = userInfo.phone || ''
    if (userPhone) {
      const students = await request.get('/students', { params: { keyword: userPhone, pageSize: 10 } })
      const student = students?.list?.find(s => s.phone === userPhone)
      if (student) studentId.value = student.id
    }
    // 用学员ID查询课表
    if (studentId.value) {
      const d = new Date().toISOString().slice(0, 10)
      const r = await request.get('/schedules', {
        params: {
          startDate: d,
          endDate: new Date(Date.now() + 7 * 86400000).toISOString().slice(0, 10),
          studentId: studentId.value
        }
      })
      list.value = r?.schedules || []
    }
  } catch (e) { console.error('load schedule error', e) }
})
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}</style>
