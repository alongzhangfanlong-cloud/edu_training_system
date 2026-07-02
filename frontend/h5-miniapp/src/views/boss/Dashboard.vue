<template>
  <div class="page">
    <van-nav-bar title="老板工作台" fixed placeholder />
    <div class="content">
      <div class="user-card">
        <van-icon name="contact" size="44" />
        <div class="user-info">
          <div class="user-name">{{ userName }}</div>
          <div class="user-tag">老板 · 超级管理员</div>
        </div>
      </div>

      <!-- 核心指标 -->
      <div class="kpi-grid">
        <div class="kpi-card">
          <div class="kpi-val">¥{{ fmtKpi(stats.monthRevenue) }}</div>
          <div class="kpi-label">本月营收</div>
          <div class="kpi-sub">今日 ¥{{ stats.todayRevenue || '0' }}</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-val">{{ stats.campuses }}<span class="kpi-unit">所</span></div>
          <div class="kpi-label">校区数</div>
          <div class="kpi-sub">运营中</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-val">{{ stats.students }}<span class="kpi-unit">人</span></div>
          <div class="kpi-label">在读学员</div>
          <div class="kpi-sub">总学员</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-val">{{ stats.teachers }}<span class="kpi-unit">位</span></div>
          <div class="kpi-label">在职教师</div>
          <div class="kpi-sub">全部教师</div>
        </div>
      </div>

      <!-- 功能入口 -->
      <van-cell-group inset style="margin-top:12px">
        <van-cell title="校区管理" icon="shop-o" is-link to="/boss/campus" label="管理多校区信息" />
        <van-cell title="用户管理" icon="manager-o" is-link to="/boss/users" label="管理员工账号" />
        <van-cell title="财务管理" icon="balance-list" is-link to="/boss/finance" label="营收统计与订单" />
        <van-cell title="教务管理" icon="bookmark-o" is-link to="/boss/jiaowu" label="排课/课程/考勤" />
        <van-cell title="系统设置" icon="setting-o" is-link to="/boss/system" label="权限/配置/公众号" />
      </van-cell-group>

      <div style="text-align:center;padding:20px">
        <van-button type="danger" size="small" @click="logout">退出登录</van-button>
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
const userName = computed(() => userStore.userName || JSON.parse(localStorage.getItem('userInfo')||'{}').name || '老板')
const stats = ref({ todayRevenue: '0', monthRevenue: '0', campuses: 0, students: 0, teachers: 0 })

function fmtKpi(v) {
  const n = Number(v)
  if (n >= 10000) return (n/10000).toFixed(1) + '万'
  return n.toLocaleString()
}

onMounted(async () => {
  // 财务摘要
  try { const d = await request.get('/finance/summary'); if (d) { stats.value.todayRevenue = d.todayRevenue||'0'; stats.value.monthRevenue = d.monthRevenue||'0' } } catch(e){}
  // 校区数
  try { stats.value.campuses = (await request.get('/campuses'))?.length || 0 } catch(e){}
  // 学员数
  try { const d = await request.get('/students', { params: { pageSize: 1 } }); stats.value.students = d?.total || 0 } catch(e){}
  // 教师数
  try { const d = await request.get('/teachers', { params: { pageSize: 1 } }); stats.value.teachers = d?.total || 0 } catch(e){}
})
function logout() { userStore.logout(); router.push('/login') }
</script>
<style scoped>
.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}
.user-card{background:linear-gradient(135deg,#1989fa,#36a3f7);border-radius:16px;padding:20px;display:flex;align-items:center;gap:14px;margin-bottom:14px;color:#fff}
.user-card :deep(.van-icon){background:rgba(255,255,255,.2);border-radius:50%;padding:8px}
.user-name{font-size:20px;font-weight:700}.user-tag{font-size:13px;opacity:.85;margin-top:2px}
.kpi-grid{display:grid;grid-template-columns:1fr 1fr;gap:10px}
.kpi-card{background:#fff;border-radius:14px;padding:16px;text-align:center}
.kpi-val{font-size:26px;font-weight:700;color:#1989fa;line-height:1.2}
.kpi-unit{font-size:14px;font-weight:400;color:#999;margin-left:2px}
.kpi-label{font-size:13px;font-weight:600;color:#333;margin-top:4px}
.kpi-sub{font-size:11px;color:#bbb;margin-top:2px}
</style>
