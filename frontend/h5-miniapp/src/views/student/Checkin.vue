<template>
  <div class="page">
    <van-nav-bar title="签到" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content" style="text-align:center">
      <div class="scan-area" @click="doCheckin">
        <van-icon name="scan" size="60" />
        <p>点击模拟扫码签到</p>
      </div>
      <div v-if="checkinResult" class="result-card">
        <van-icon :name="checkinResult.success ? 'checked' : 'cross'" size="40" :color="checkinResult.success ? '#52c41a' : '#ff4d4f'" />
        <p style="font-size:16px;font-weight:600;margin-top:8px">{{ checkinResult.message }}</p>
        <p style="color:#999;font-size:13px">{{ checkinResult.time }}</p>
      </div>
      <!-- 最近签到记录 -->
      <div style="margin-top:20px;text-align:left">
        <h4 style="color:#666;margin-bottom:8px">最近签到记录</h4>
        <van-cell v-for="a in attendances" :key="a.id" :title="`排课#${a.scheduleId}`" :label="a.checkInTime">
          <template #right-icon>
            <van-tag :type="a.status === 1 ? 'success' : a.status === 2 ? 'warning' : 'danger'">
              {{ {1:'出勤',2:'请假',3:'旷课'}[a.status] }}
            </van-tag>
          </template>
        </van-cell>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import request from '../../utils/request'

const checkinResult = ref(null)
const attendances = ref([])
const studentId = ref(null)

onMounted(async () => {
  // 从登录信息匹配学员ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  try {
    const students = await request.get('/students', { params: { keyword: userInfo.phone || '', pageSize: 10 } })
    const student = students?.list?.find(s => s.phone === userInfo.phone)
    if (student) studentId.value = student.id
  } catch(e) {}
})

async function loadHistory() {
  try {
    // 查询考勤统计作为签到记录
    // 暂无学员维度的考勤查询接口，使用课表接口模拟
  } catch(e) {}
}

async function doCheckin() {
  const now = new Date()
  const timeStr = now.toLocaleString('zh-CN')
  try {
    // 调用考勤接口模拟签到
    await request.post('/attendances/batch-check', {
      scheduleId: 1, // 模拟排课ID
      attendances: [{ studentId: studentId.value, status: 1 }]
    })
    checkinResult.value = { success: true, message: '签到成功！', time: timeStr }
    showToast('签到成功')
  } catch(e) {
    checkinResult.value = { success: false, message: '签到失败，请重试', time: timeStr }
  }
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.scan-area{background:#fff;border-radius:16px;padding:60px 40px;margin:20px;border:2px dashed #ddd;cursor:pointer}.scan-area p{color:#999;margin-top:12px}.result-card{background:#fff;border-radius:12px;padding:20px;margin:0 20px}</style>
