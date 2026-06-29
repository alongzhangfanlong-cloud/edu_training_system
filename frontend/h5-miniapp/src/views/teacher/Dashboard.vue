<template>
  <div class="page">
    <van-nav-bar title="教师工作台" fixed placeholder />
    <div class="content">
      <!-- 个人信息 -->
      <div class="user-card">
        <van-icon name="manager-o" size="44" color="#fff" />
        <div class="user-info">
          <div class="user-name">{{ userName }}</div>
          <div class="user-tag">教师</div>
        </div>
      </div>
      <div class="card">
        <div class="title">📅 今日授课</div>
        <van-cell v-for="c in todayClasses" :key="c.id" :title="c.courseName || c.className" :label="`${c.startTime}-${c.endTime} | ${c.classroomName || '待定'}`"/>
        <div v-if="!todayClasses.length" class="empty">今日无课程</div>
      </div>
      <van-grid :column-num="3" gutter="8" style="margin-top:12px">
        <van-grid-item icon="friends-o" text="学员" :to="m.to" v-for="m in menus" :key="m.label" />
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
const userName = computed(() => userStore.userName || JSON.parse(localStorage.getItem('userInfo')||'{}').name || '教师')
const menus = [
  { label: '学员管理', to: '/teacher/students' }, { label: '课程管理', to: '/teacher/courses' },
  { label: '点名签到', to: '/teacher/checkin' }, { label: '布置作业', to: '/teacher/homework' },
  { label: '请假审批', to: '/teacher/dashboard' }, { label: '成长档案', to: '/teacher/growth' }
]
onMounted(async () => {
  try { const d = new Date().toISOString().slice(0,10); const r = await request.get('/schedules', { params: { startDate: d, endDate: d } }); todayClasses.value = r?.schedules || [] } catch(e){}
})
function logout() { userStore.logout(); router.push('/login') }
</script>
<style scoped>
.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}
.user-card{background:linear-gradient(135deg,#1989fa,#36a3f7);border-radius:16px;padding:20px;display:flex;align-items:center;gap:14px;margin-bottom:14px;color:#fff}
.user-card :deep(.van-icon){background:rgba(255,255,255,.2);border-radius:50%;padding:8px}
.user-name{font-size:20px;font-weight:700}.user-tag{font-size:13px;opacity:.85;margin-top:2px}
.card{background:#fff;border-radius:12px;padding:16px}.title{font-size:16px;font-weight:600;margin-bottom:8px}.empty{text-align:center;color:#999;padding:20px}</style>
