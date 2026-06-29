<template>
  <div class="page">
    <van-nav-bar title="老板工作台" fixed placeholder />
    <div class="content">
      <!-- 个人信息 -->
      <div class="user-card">
        <van-icon name="contact" size="44" />
        <div class="user-info">
          <div class="user-name">{{ userName }}</div>
          <div class="user-tag">老板 · 超级管理员</div>
        </div>
      </div>
      <van-grid :column-num="2" gutter="12">
        <van-grid-item icon="balance-o" text="总收入"><template #default><div class="sv">{{ stats.revenue }}</div></template></van-grid-item>
        <van-grid-item icon="shop-o" text="校区数"><template #default><div class="sv">{{ stats.campuses }}</div></template></van-grid-item>
        <van-grid-item icon="friends-o" text="学员数"><template #default><div class="sv">{{ stats.students }}</div></template></van-grid-item>
        <van-grid-item icon="manager-o" text="教师数"><template #default><div class="sv">{{ stats.teachers }}</div></template></van-grid-item>
      </van-grid>

      <van-cell-group inset style="margin-top:12px">
        <van-cell title="校区管理" icon="shop-o" is-link to="/boss/campus" />
        <van-cell title="用户管理" icon="manager-o" is-link to="/boss/users" />
        <van-cell title="财务管理" icon="balance-list" is-link to="/boss/finance" />
        <van-cell title="教务管理" icon="bookmark-o" is-link to="/boss/jiaowu" />
        <van-cell title="系统设置" icon="setting-o" is-link to="/boss/system" />
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
const stats = ref({ revenue: '¥0', campuses: 0, students: 0, teachers: 0 })
onMounted(async () => {
  try { const d = await request.get('/statistics/dashboard'); stats.value.revenue = '¥'+(d.todayRevenue||0) } catch(e){}
  try { stats.value.campuses = (await request.get('/campuses'))?.length || 0 } catch(e){}
  try { const d = await request.get('/students', { params: { pageSize: 1 } }); stats.value.students = d?.total || 0 } catch(e){}
  try { const d = await request.get('/teachers', { params: { pageSize: 1 } }); stats.value.teachers = d?.total || 0 } catch(e){}
})
function logout() { userStore.logout(); router.push('/login') }
</script>
<style scoped>
.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}
.user-card{background:linear-gradient(135deg,#1989fa,#36a3f7);border-radius:16px;padding:20px;display:flex;align-items:center;gap:14px;margin-bottom:14px;color:#fff}
.user-card :deep(.van-icon){background:rgba(255,255,255,.2);border-radius:50%;padding:8px}
.user-name{font-size:20px;font-weight:700}.user-tag{font-size:13px;opacity:.85;margin-top:2px}
.sv{font-size:22px;font-weight:700;color:#1989fa}</style>
