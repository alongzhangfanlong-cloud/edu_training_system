<template>
  <div class="page">
    <van-nav-bar title="家长中心" fixed placeholder />
    <div class="content">
      <!-- 个人信息 -->
      <div class="user-card">
        <van-icon name="friends-o" size="44" />
        <div class="user-info">
          <div class="user-name">{{ userName }}</div>
          <div class="user-tag">家长</div>
        </div>
      </div>
      <div class="hours-card">
        <div class="label">孩子剩余课时</div>
        <div class="big">{{ remainHours }} <span class="unit">课时</span></div>
        <van-progress :percentage="usePercent" stroke-color="#1989fa" />
        <div class="sub">已用 {{ usedHours }} / 总计 {{ totalHours }} 课时</div>
      </div>
      <van-grid :column-num="3" gutter="8" style="margin-top:12px">
        <van-grid-item v-for="m in menus" :key="m.label" :icon="m.icon" :text="m.label" :to="m.to" />
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
const userName = computed(() => userStore.userName || JSON.parse(localStorage.getItem('userInfo')||'{}').name || '家长')
const totalHours = ref(40); const usedHours = ref(12); const remainHours = ref(28)
const usePercent = computed(() => totalHours.value ? Math.round(usedHours.value / totalHours.value * 100) : 0)
const menus = [
  { label: '课表', icon: 'clock-o', to: '/parent/schedule' },
  { label: '请假', icon: 'notes-o', to: '/parent/leave' },
  { label: '成长', icon: 'chart-trending-o', to: '/parent/growth' },
  { label: '积分', icon: 'gift-o', to: '/parent/points' },
  { label: '明细', icon: 'balance-o', to: '/parent/hours' },
  { label: '提醒', icon: 'bell', to: '/parent/dashboard' }
]
onMounted(async () => {
  const uid = userStore.userInfo.id
  try { const d = await request.get(`/students/${uid}/hours`, { params: { pageSize: 1 } }); if (d?.list?.length) { const h = d.list[0]; totalHours.value = h.totalHours || 40; usedHours.value = h.usedHours || 12; remainHours.value = h.remainHours || 28 } } catch(e){}
})
function logout() { userStore.logout(); router.push('/login') }
</script>
<style scoped>
.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}
.user-card{background:linear-gradient(135deg,#1989fa,#36a3f7);border-radius:16px;padding:20px;display:flex;align-items:center;gap:14px;margin-bottom:14px;color:#fff}
.user-card :deep(.van-icon){background:rgba(255,255,255,.2);border-radius:50%;padding:8px}
.user-name{font-size:20px;font-weight:700}.user-tag{font-size:13px;opacity:.85;margin-top:2px}
.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}
.hours-card{background:#fff;border-radius:16px;padding:24px;text-align:center}
.label{font-size:14px;color:#999}.big{font-size:52px;font-weight:700;color:#1989fa}.unit{font-size:16px;color:#999}
.sub{font-size:13px;color:#999;margin-top:8px}
</style>
