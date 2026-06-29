<template>
  <div class="page">
    <van-nav-bar title="点名签到" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-empty v-if="!schedules.length" description="今日无课程" />
      <div v-for="s in schedules" :key="s.id" class="card">
        <van-cell :title="s.courseName || `课程#${s.courseId}`" :label="`${s.date} ${s.startTime}-${s.endTime} | ${s.classroomName || ''}`" />
        <van-cell v-for="st in getClassStudents(s.classId)" :key="st.id" :title="st.name">
          <template #right-icon>
            <van-radio-group v-model="st._status" direction="horizontal">
              <van-radio :name="1"><span style="font-size:12px;color:#52c41a">出勤</span></van-radio>
              <van-radio :name="2"><span style="font-size:12px;color:#faad14">请假</span></van-radio>
              <van-radio :name="3"><span style="font-size:12px;color:#ff4d4f">旷课</span></van-radio>
            </van-radio-group>
          </template>
        </van-cell>
        <van-button type="primary" block style="margin:8px" @click="submit(s)" :loading="submitting">提交点名</van-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import request from '../../utils/request'

const schedules = ref([])
const students = ref([])
const submitting = ref(false)

onMounted(async () => {
  try {
    const today = new Date().toISOString().slice(0, 10)
    const r = await request.get('/schedules', { params: { startDate: today, endDate: today } })
    schedules.value = r?.schedules || []
  } catch(e) {}
  // 按班级加载学员
  if (schedules.value.length) {
    for (const s of schedules.value) {
      await loadClassStudents(s.classId, s.id)
    }
  }
})

async function loadClassStudents(classId, scheduleId) {
  if (!classId) return
  try {
    const d = await request.get('/students', { params: { classId, pageSize: 200 } })
    const classStudents = (d?.list || []).map(s => ({ ...s, _status: 1 }))
    classStudentMap.value[scheduleId] = classStudents
  } catch(e) {}
}

const classStudentMap = ref({})
function getClassStudents(classId) {
  // 返回该排课对应的班级学员
  for (const [scheduleId, list] of Object.entries(classStudentMap.value)) {
    const sch = schedules.value.find(s => s.id === Number(scheduleId))
    if (sch && sch.classId === classId) return list
  }
  return []
}

async function submit(s) {
  submitting.value = true
  try {
    const classStudents = getClassStudents(s.classId)
    await request.post('/attendances/batch-check', {
      scheduleId: s.id,
      attendances: classStudents.map(st => ({ studentId: st.id, status: st._status }))
    })
    showToast('点名提交成功')
  } catch(e) {} finally { submitting.value = false }
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}.card{background:#fff;border-radius:12px;padding:0 0 8px 0;margin-bottom:12px;overflow:hidden}</style>
