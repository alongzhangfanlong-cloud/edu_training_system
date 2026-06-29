<template>
  <div class="page">
    <van-nav-bar title="学员中心" fixed placeholder />
    <div class="content">
      <!-- 个人信息 -->
      <div class="user-card">
        <van-icon name="contact" size="44" color="#1989fa" />
        <div class="user-info">
          <div class="user-name">{{ studentName || userName }}</div>
          <div class="user-tag">学员 · {{ studentGrade || '' }}</div>
        </div>
      </div>
      <div class="card">
        <div class="title">📅 今日课程</div>
        <van-cell v-for="c in todayClasses" :key="c.id" :title="c.courseName || '课程#'+c.courseId" :label="`${c.startTime}-${c.endTime} | ${c.teacherName || ''}`" />
        <div v-if="!todayClasses.length" class="empty">今日无课程安排</div>
      </div>
      <van-grid :column-num="3" gutter="8" style="margin-top:12px">
        <van-grid-item icon="clock-o" text="课表" to="/student/schedule" />
        <van-grid-item icon="checked" text="签到" to="/student/checkin" />
        <van-grid-item icon="balance-o" text="查课时" to="/student/hours" />
      </van-grid>
      <div style="text-align:center;padding:20px">
        <van-button type="danger" size="small" @click="logout">退出</van-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import request from '../../utils/request'

const router = useRouter(); const userStore = useUserStore()
const todayClasses = ref([])
const studentName = ref(''); const studentGrade = ref('')
const userName = computed(() => JSON.parse(localStorage.getItem('userInfo') || '{}').name || '')

onMounted(async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const students = await request.get('/students', { params: { keyword: userInfo.phone || '', pageSize: 10 } })
    const student = students?.list?.find(s => s.phone === userInfo.phone)
    if (student) {
      studentName.value = student.name || ''
      studentGrade.value = student.grade || ''
      const d = new Date().toISOString().slice(0, 10)
      const r = await request.get('/schedules', { params: { startDate: d, endDate: d, studentId: student.id } })
      todayClasses.value = r?.schedules || []
    }
  } catch (e) {}
})
function logout() { userStore.logout(); router.push('/login') }
</script>
<style scoped>
.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}
.user-card{background:linear-gradient(135deg,#1989fa,#36a3f7);border-radius:16px;padding:20px;display:flex;align-items:center;gap:14px;margin-bottom:14px;color:#fff}
.user-card :deep(.van-icon){background:rgba(255,255,255,.2);border-radius:50%;padding:8px}
.user-name{font-size:20px;font-weight:700}.user-tag{font-size:13px;opacity:.85;margin-top:2px}
.card{background:#fff;border-radius:12px;padding:16px}.title{font-size:16px;font-weight:600;margin-bottom:8px}.empty{text-align:center;color:#999;padding:20px}</style>
