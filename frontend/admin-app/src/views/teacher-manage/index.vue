<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-form :inline="true">
          <el-form-item><el-input v-model="filters.keyword" placeholder="搜索教师姓名/手机号" clearable style="width:220px" /></el-form-item>
          <el-form-item><el-select v-model="filters.status" placeholder="状态" clearable style="width:120px"><el-option label="在职" :value="1" /><el-option label="离职" :value="0" /><el-option label="休假" :value="2" /></el-select></el-form-item>
          <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
        </el-form>
        <el-button type="success" @click="openDialog()">+ 新增教师</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="teacherNo" label="编号" width="110" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="性别" width="70"><template #default="{row}">{{ {1:'男',2:'女'}[row.gender]||'-' }}</template></el-table-column>
        <el-table-column label="可授科目" min-width="160">
          <template #default="{row}"><el-tag v-for="s in (row.subjects||[])" :key="s" size="small" style="margin:2px">{{ s }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="hireDate" label="入职日期" width="110" />
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':row.status===0?'danger':'warning'" size="small">{{ {0:'离职',1:'在职',2:'休假'}[row.status] }}</el-tag></template>
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

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑教师':'新增教师'" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" maxlength="11" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="性别"><el-select v-model="form.gender" style="width:100%"><el-option label="男" :value="1" /><el-option label="女" :value="2" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="入职日期"><el-date-picker v-model="form.hireDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="可授科目">
          <el-select v-model="form.subjects" multiple filterable allow-create placeholder="输入科目名称回车添加" style="width:100%">
            <el-option v-for="s in subjectOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="个人简介"><el-input v-model="form.introduction" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:150px"><el-option label="在职" :value="1" /><el-option label="离职" :value="0" /><el-option label="休假" :value="2" /></el-select></el-form-item>
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
const filters = reactive({ keyword: '', status: null })
const dialogVisible = ref(false); const isEdit = ref(false); const saving = ref(false)
const formRef = ref(null)
const form = reactive({ name: '', phone: '', gender: null, hireDate: '', subjects: [], introduction: '', status: 1 })
const rules = { name: [{ required: true, message: '请输入姓名' }], phone: [{ required: true, message: '请输入手机号' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误' }] }
const subjectOptions = ['语文','数学','英语','物理','化学','生物','历史','地理','政治','音乐','美术','体育','舞蹈','钢琴','书法','编程','机器人','演讲口才']

onMounted(loadData)
async function loadData() {
  loading.value = true
  try { const d = await request.get('/teachers', { params: { ...filters, page: page.value, pageSize: pageSize.value } }); tableData.value = d?.list || []; total.value = d?.total || 0 } catch(e) {} finally { loading.value = false }
}
function openDialog(row) {
  if (row) { isEdit.value = true; Object.assign(form, { ...row, subjects: row.subjects || [] }) } else { isEdit.value = false; resetForm() }
  dialogVisible.value = true
}
function resetForm() { Object.assign(form, { name: '', phone: '', gender: null, hireDate: '', subjects: [], introduction: '', status: 1 }) }
async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) { await request.put(`/teachers/${form.id}`, form); ElMessage.success('更新成功') }
    else { await request.post('/teachers', form); ElMessage.success('创建成功') }
    dialogVisible.value = false; loadData()
  } catch(e) {} finally { saving.value = false }
}
async function remove(row) { try { await request.delete(`/teachers/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {} }
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; flex-wrap: wrap; }
</style>
