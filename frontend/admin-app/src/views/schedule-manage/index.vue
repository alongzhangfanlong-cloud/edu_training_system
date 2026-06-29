<template>
  <div>
    <!-- 筛选栏 -->
    <el-card style="margin-bottom:16px">
      <el-form :inline="true">
        <el-form-item label="日期范围">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
            start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width:240px" />
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="filters.teacherId" placeholder="全部" clearable filterable style="width:160px">
            <el-option v-for="t in teachers" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教室">
          <el-select v-model="filters.classroomId" placeholder="全部" clearable style="width:160px">
            <el-option v-for="r in classrooms" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="success" @click="openSingleDialog()">+ 单条排课</el-button>
          <el-button type="warning" @click="openBatchDialog()">📅 批量排课</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 课表列表 -->
    <el-card>
      <el-table :data="schedules" v-loading="loading" stripe max-height="600">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="date" label="日期" width="110" sortable />
        <el-table-column label="时间" width="130">
          <template #default="{row}">{{ row.startTime }} - {{ row.endTime }}</template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程" min-width="130" />
        <el-table-column prop="className" label="班级" width="110" />
        <el-table-column prop="teacherName" label="教师" width="90" />
        <el-table-column prop="classroomName" label="教室" width="100" />
        <el-table-column label="状态" width="80">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':row.status===0?'danger':row.status===2?'warning':'info'" size="small">
              {{ {0:'已取消',1:'正常',2:'调课',3:'补课',4:'停课'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lessonNo" label="课次" width="60" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{row}">
            <el-button size="small" text type="primary" @click="openSingleDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="doDelete(row)">
              <template #reference><el-button size="small" text type="danger">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 单条排课弹窗 -->
    <el-dialog v-model="singleVisible" :title="isEdit?'编辑排课':'新增排课'" width="500px">
      <el-form ref="singleFormRef" :model="singleForm" :rules="singleRules" label-width="80px">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="singleForm.classId" placeholder="选择班级" filterable style="width:100%" @change="onClassChange">
            <el-option v-for="c in classes" :key="c.id" :label="`${c.name} (${c.courseName||''})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="singleForm.teacherId" placeholder="选择教师" filterable style="width:100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教室" prop="classroomId">
          <el-select v-model="singleForm.classroomId" placeholder="选择教室" filterable style="width:100%">
            <el-option v-for="r in classrooms" :key="r.id" :label="`${r.name} (容纳${r.capacity}人)`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date"><el-date-picker v-model="singleForm.date" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="开始" prop="startTime"><el-time-picker v-model="singleForm.startTime" format="HH:mm" value-format="HH:mm" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束" prop="endTime"><el-time-picker v-model="singleForm.endTime" format="HH:mm" value-format="HH:mm" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="课次"><el-input-number v-model="singleForm.lessonNo" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="singleForm.remark" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="singleVisible=false">取消</el-button><el-button type="primary" :loading="saving" @click="submitSingle">保存</el-button></template>
    </el-dialog>

    <!-- 批量排课弹窗 -->
    <el-dialog v-model="batchVisible" title="📅 批量排课" width="650px" @close="batchResult=null">
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="90px" v-if="!batchResult">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="batchForm.classId" placeholder="选择要排课的班级" filterable style="width:100%">
            <el-option v-for="c in classes" :key="c.id" :label="`${c.name} (${c.courseName||''})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="batchForm.teacherId" placeholder="选择授课教师" filterable style="width:100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教室" prop="classroomId">
          <el-select v-model="batchForm.classroomId" placeholder="选择教室" filterable style="width:100%">
            <el-option v-for="r in classrooms" :key="r.id" :label="`${r.name} (容纳${r.capacity}人)`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker v-model="batchForm.dateRange" type="daterange" range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="上课日" prop="weekdays">
          <el-checkbox-group v-model="batchForm.weekdays">
            <el-checkbox v-for="(name, idx) in weekDayNames" :key="idx" :label="idx+1" :value="idx+1">{{ name }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="上课时间" prop="startTime"><el-time-picker v-model="batchForm.startTime" format="HH:mm" value-format="HH:mm" style="width:100%" placeholder="开始时间" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="下课时间" prop="endTime"><el-time-picker v-model="batchForm.endTime" format="HH:mm" value-format="HH:mm" style="width:100%" placeholder="结束时间" /></el-form-item></el-col>
        </el-row>
        <el-alert v-if="previewCount > 0" :title="`预计生成 ${previewCount} 节课 (共 ${previewDays} 天)`" type="info" :closable="false" show-icon style="margin-bottom:12px" />
      </el-form>

      <!-- 批量排课结果 -->
      <div v-if="batchResult" class="batch-result">
        <el-result :icon="batchResult.skippedCount > 0 ? 'warning' : 'success'"
          :title="`成功创建 ${batchResult.createdCount} 节，跳过 ${batchResult.skippedCount} 节`">
        </el-result>
        <div v-if="batchResult.conflicts && batchResult.conflicts.length" style="margin-top:16px">
          <el-alert v-for="(c, i) in batchResult.conflicts" :key="i" :title="c.date" type="warning" :closable="false" style="margin-bottom:8px">
            <template #default><span v-for="cc in c.conflicts" :key="cc.type">{{ cc.message }}; </span></template>
          </el-alert>
        </div>
      </div>

      <template #footer>
        <el-button @click="batchVisible=false">{{ batchResult ? '关闭' : '取消' }}</el-button>
        <el-button v-if="!batchResult" type="primary" :loading="batchSaving" @click="submitBatch">开始批量排课</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

// ===== 数据 =====
const loading = ref(false); const saving = ref(false); const batchSaving = ref(false)
const schedules = ref([])
const teachers = ref([]); const classrooms = ref([]); const classes = ref([])
const dateRange = ref([])
const filters = reactive({ teacherId: null, classroomId: null })

// ===== 单条排课 =====
const singleVisible = ref(false); const isEdit = ref(false)
const singleFormRef = ref(null)
const singleForm = reactive({ classId: null, teacherId: null, classroomId: null, date: '', startTime: '', endTime: '', lessonNo: null, remark: '' })
const singleRules = {
  classId: [{ required: true, message: '请选择班级' }],
  teacherId: [{ required: true, message: '请选择教师' }],
  classroomId: [{ required: true, message: '请选择教室' }],
  date: [{ required: true, message: '请选择日期' }],
  startTime: [{ required: true, message: '请选择开始时间' }],
  endTime: [{ required: true, message: '请选择结束时间' }]
}

// ===== 批量排课 =====
const batchVisible = ref(false); const batchResult = ref(null)
const batchFormRef = ref(null)
const batchForm = reactive({ classId: null, teacherId: null, classroomId: null, dateRange: [], weekdays: [1,2,3,4,5], startTime: '', endTime: '' })
const batchRules = {
  classId: [{ required: true, message: '请选择班级' }],
  teacherId: [{ required: true, message: '请选择教师' }],
  classroomId: [{ required: true, message: '请选择教室' }],
  dateRange: [{ required: true, message: '请选择日期范围', trigger: 'change', validator: (r, v, cb) => v && v.length === 2 ? cb() : cb(new Error('请选择日期范围')) }],
  weekdays: [{ required: true, message: '请选择至少一个上课日', trigger: 'change', validator: (r, v, cb) => v && v.length > 0 ? cb() : cb(new Error('请选择至少一个上课日')) }],
  startTime: [{ required: true, message: '请选择时间' }],
  endTime: [{ required: true, message: '请选择时间' }]
}
const weekDayNames = ['周一','周二','周三','周四','周五','周六','周日']

const previewCount = computed(() => {
  if (!batchForm.dateRange || batchForm.dateRange.length !== 2 || !batchForm.weekdays.length) return 0
  let count = 0; const d = new Date(batchForm.dateRange[0]); const end = new Date(batchForm.dateRange[1])
  while (d <= end) { if (batchForm.weekdays.includes(d.getDay() === 0 ? 7 : d.getDay())) count++; d.setDate(d.getDate()+1) }
  return count
})
const previewDays = computed(() => { if (!batchForm.dateRange || batchForm.dateRange.length !== 2) return 0; return Math.ceil((new Date(batchForm.dateRange[1]) - new Date(batchForm.dateRange[0])) / 86400000) + 1 })

onMounted(() => { loadData(); loadRefs() })

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (dateRange.value?.length === 2) { params.startDate = dateRange.value[0]; params.endDate = dateRange.value[1] }
    if (filters.teacherId) params.teacherId = filters.teacherId
    if (filters.classroomId) params.classroomId = filters.classroomId
    const d = await request.get('/schedules', { params })
    schedules.value = d?.schedules || []
  } catch(e) {} finally { loading.value = false }
}

async function loadRefs() {
  try { teachers.value = (await request.get('/teachers', { params: { pageSize: 100 } }))?.list || [] } catch(e) {}
  try { classrooms.value = (await request.get('/classrooms', { params: { pageSize: 100 } }))?.list || [] } catch(e) {}
  try { classes.value = (await request.get('/classes', { params: { pageSize: 100 } }))?.list || [] } catch(e) {}
}

function onClassChange(classId) {
  const cls = classes.value.find(c => c.id === classId)
  if (cls) { singleForm.teacherId = cls.teacherId || singleForm.teacherId; singleForm.classroomId = cls.classroomId || singleForm.classroomId }
}

// ===== 单条操作 =====
function openSingleDialog(row) {
  if (row) { isEdit.value = true; Object.assign(singleForm, { id: row.id, classId: row.classId, teacherId: row.teacherId, classroomId: row.classroomId, date: row.date, startTime: row.startTime, endTime: row.endTime, lessonNo: row.lessonNo, remark: row.remark }) }
  else { isEdit.value = false; Object.assign(singleForm, { classId: null, teacherId: null, classroomId: null, date: '', startTime: '', endTime: '', lessonNo: null, remark: '' }) }
  singleVisible.value = true
}
async function submitSingle() {
  const valid = await singleFormRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) { await request.put(`/schedules/${singleForm.id}`, singleForm); ElMessage.success('更新成功') }
    else { await request.post('/schedules', singleForm); ElMessage.success('创建成功') }
    singleVisible.value = false; loadData()
  } catch(e) {} finally { saving.value = false }
}
async function doDelete(row) {
  try { await request.delete(`/schedules/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {}
}

// ===== 批量排课 =====
function openBatchDialog() {
  batchResult.value = null
  Object.assign(batchForm, { classId: null, teacherId: null, classroomId: null, dateRange: [], weekdays: [1,2,3,4,5], startTime: '', endTime: '' })
  batchVisible.value = true
}
async function submitBatch() {
  const valid = await batchFormRef.value?.validate().catch(() => false)
  if (!valid) return
  batchSaving.value = true
  try {
    const res = await request.post('/schedules/batch', {
      classId: batchForm.classId,
      teacherId: batchForm.teacherId,
      classroomId: batchForm.classroomId,
      startDate: batchForm.dateRange[0],
      endDate: batchForm.dateRange[1],
      weekdays: batchForm.weekdays,
      startTime: batchForm.startTime,
      endTime: batchForm.endTime,
      skipHolidays: false
    })
    batchResult.value = res
    ElMessage.success(`批量排课完成: 成功${res.createdCount}节, 跳过${res.skippedCount}节`)
    loadData()
  } catch(e) {} finally { batchSaving.value = false }
}
</script>

<style scoped>
.batch-result { padding: 20px 0; }
</style>
