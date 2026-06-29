<template>
  <div class="page">
    <van-nav-bar title="校长工作台" fixed placeholder />
    <div class="content">
      <!-- 个人信息 -->
      <div class="user-card">
        <van-icon name="manager-o" size="44" />
        <div class="user-info">
          <div class="user-name">{{ userName }}</div>
          <div class="user-tag">校长 · {{ campusName }}</div>
        </div>
      </div>
      <van-grid :column-num="2" gutter="12">
        <van-grid-item icon="friends-o" text="校区学员"><template #default><div class="sv">{{ stats.students }}</div></template></van-grid-item>
        <van-grid-item icon="manager-o" text="校区教师"><template #default><div class="sv">{{ stats.teachers }}</div></template></van-grid-item>
        <van-grid-item icon="clock-o" text="今日课程"><template #default><div class="sv">{{ stats.classes }}</div></template></van-grid-item>
        <van-grid-item icon="chart-trending-o" text="出勤率"><template #default><div class="sv">{{ stats.attRate }}%</div></template></van-grid-item>
      </van-grid>
      <van-cell-group inset style="margin-top:12px">
        <van-cell title="教师课时消耗" icon="clock-o" is-link to="/principal/teachers" />
        <van-cell title="学员课时消耗" icon="balance-o" is-link to="/principal/students" />
      </van-cell-group>
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
const campusName = computed(() => userStore.userInfo.campusName || JSON.parse(localStorage.getItem('userInfo')||'{}').campusName || '')
const userName = computed(() => userStore.userName || JSON.parse(localStorage.getItem('userInfo')||'{}').name || '校长')
const stats = ref({ students: 0, teachers: 0, classes: 0, attRate: 95 })
onMounted(async () => {
  try { const d = await request.get('/students', { params: { pageSize: 1 } }); stats.value.students = d?.total || 0 } catch(e){}
  try { const d = await request.get('/teachers', { params: { pageSize: 1 } }); stats.value.teachers = d?.total || 0 } catch(e){}
  try { const s = await request.get('/schedules', { params: { startDate: new Date().toISOString().slice(0,10) } }); stats.value.classes = s?.schedules?.length || 0 } catch(e){}
})
function logout() { userStore.logout(); router.push('/login') }
</script>
<style scoped>
.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}
.user-card{background:linear-gradient(135deg,#1989fa,#36a3f7);border-radius:16px;padding:20px;display:flex;align-items:center;gap:14px;margin-bottom:14px;color:#fff}
.user-card :deep(.van-icon){background:rgba(255,255,255,.2);border-radius:50%;padding:8px}
.user-name{font-size:20px;font-weight:700}.user-tag{font-size:13px;opacity:.85;margin-top:2px}
.sv{font-size:22px;font-weight:700;color:#1989fa}</style>
