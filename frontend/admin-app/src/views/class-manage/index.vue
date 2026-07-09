<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-form :inline="true">
          <el-form-item><el-input v-model="filters.keyword" placeholder="搜索班级名称" clearable style="width:200px" /></el-form-item>
          <el-form-item><el-select v-model="filters.courseId" placeholder="课程筛选" clearable style="width:180px"><el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
          <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
        </el-form>
        <el-button type="success" @click="openDialog()">+ 新增班级</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="班级名称" min-width="140" />
        <el-table-column prop="courseName" label="所属课程" min-width="120" />
        <el-table-column prop="teacherName" label="授课教师" width="100" />
        <el-table-column prop="classroomName" label="教室" width="120" />
        <el-table-column label="人数" width="100">
          <template #default="{row}">{{ row.currentStudents || 0 }}/{{ row.maxStudents || 0 }}</template>
        </el-table-column>
        <el-table-column label="开班日期" width="110">
          <template #default="{row}">{{ row.startDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'正常':'停用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="remove(row)"><template #reference><el-button size="small" type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑班级':'新增班级'" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="班级名称" prop="name"><el-input v-model="form.name" placeholder="如: 初三数学冲刺班(周六班)" /></el-form-item>
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="选择课程" filterable style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="`${c.name} (${c.subject||''})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId">
          <el-select v-model="form.teacherId" placeholder="选择教师" filterable style="width:100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教室">
          <el-select v-model="form.classroomId" placeholder="选择教室（可选）" filterable clearable style="width:100%">
            <el-option v-for="r in classrooms" :key="r.id" :label="`${r.name} (容纳${r.capacity}人)`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="最大人数"><el-input-number v-model="form.maxStudents" :min="1" :max="500" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="开班日期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结课日期"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="排课规则"><el-input v-model="form.scheduleRule" placeholder="如: 每周六 14:00-16:00" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="saving" @click="submitForm">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const tableData = ref([]); const loading = ref(false); const total = ref(0); const page = ref(1); const pageSize = ref(20)
const filters = reactive({ keyword: '', courseId: null })
const courses = ref([]); const teachers = ref([]); const classrooms = ref([])
const dialogVisible = ref(false); const isEdit = ref(false); const saving = ref(false)
const formRef = ref(null)
const form = reactive({ name: '', courseId: null, teacherId: null, classroomId: null, maxStudents: 20, startDate: '', endDate: '', scheduleRule: '', status: 1 })
const rules = {
  name: [{ required: true, message: '请输入班级名称' }],
  courseId: [{ required: true, message: '请选择课程' }],
  teacherId: [{ required: true, message: '请选择教师' }]
}

onMounted(() => { loadData(); loadRefs() })

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (filters.courseId) params.courseId = filters.courseId
    const d = await request.get('/classes', { params })
    tableData.value = d?.list || []; total.value = d?.total || 0
  } catch(e) {} finally { loading.value = false }
}

async function loadRefs() {
  try { courses.value = (await request.get('/courses', { params: { pageSize: 100 } }))?.list || [] } catch(e) {}
  try { teachers.value = (await request.get('/teachers', { params: { pageSize: 100 } }))?.list || [] } catch(e) {}
  try { classrooms.value = (await request.get('/classrooms', { params: { pageSize: 100 } }))?.list || [] } catch(e) {}
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    Object.assign(form, {
      id: row.id, name: row.name, courseId: row.courseId, teacherId: row.teacherId,
      classroomId: row.classroomId, maxStudents: row.maxStudents,
      startDate: row.startDate, endDate: row.endDate,
      scheduleRule: row.scheduleRule, status: row.status
    })
  } else {
    isEdit.value = false; resetForm()
  }
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, { name: '', courseId: null, teacherId: null, classroomId: null, maxStudents: 20, startDate: '', endDate: '', scheduleRule: '', status: 1 })
  formRef.value?.clearValidate()
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) { await request.put(`/classes/${form.id}`, form); ElMessage.success('更新成功') }
    else { await request.post('/classes', form); ElMessage.success('班级创建成功！可在排课管理中排课') }
    dialogVisible.value = false; loadData()
  } catch(e) {} finally { saving.value = false }
}

async function remove(row) {
  try { await request.delete(`/classes/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {}
}
</script>
<style scoped>.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; flex-wrap: wrap; }</style>
