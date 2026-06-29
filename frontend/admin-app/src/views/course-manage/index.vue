<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-form :inline="true">
          <el-form-item><el-input v-model="filters.keyword" placeholder="搜索课程名称/编码" clearable style="width:220px" /></el-form-item>
          <el-form-item><el-select v-model="filters.type" placeholder="课程类型" clearable style="width:130px"><el-option label="班课" :value="1" /><el-option label="一对一" :value="2" /><el-option label="小班" :value="3" /></el-select></el-form-item>
          <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
        </el-form>
        <el-button type="success" @click="openDialog()">+ 新增课程</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="code" label="编码" width="110" />
        <el-table-column prop="name" label="课程名称" min-width="140" />
        <el-table-column label="类型" width="90">
          <template #default="{row}">{{ {1:'班课',2:'一对一',3:'小班'}[row.type] }}</template>
        </el-table-column>
        <el-table-column prop="subject" label="科目" width="90" />
        <el-table-column prop="gradeRange" label="适用年级" width="100" />
        <el-table-column prop="duration" label="课时(分)" width="90" />
        <el-table-column label="单价" width="100">
          <template #default="{row}">¥{{ row.unitPrice || 0 }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'上架':'下架' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" :type="row.status===1?'warning':'success'" @click="toggleStatus(row)">{{ row.status===1?'下架':'上架' }}</el-button>
            <el-popconfirm title="确定删除?" @confirm="remove(row)"><template #reference><el-button size="small" type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑课程':'新增课程'" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="课程名称" prop="name"><el-input v-model="form.name" placeholder="如: 初三数学冲刺班" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="课程编码" prop="code"><el-input v-model="form.code" placeholder="自动生成" :disabled="isEdit" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="课程类型" prop="type"><el-select v-model="form.type" style="width:100%"><el-option label="班课" :value="1" /><el-option label="一对一" :value="2" /><el-option label="小班" :value="3" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="科目"><el-input v-model="form.subject" placeholder="数学/英语/钢琴" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="适用年级"><el-input v-model="form.gradeRange" placeholder="如: 初三-高三" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="课时(分钟)"><el-input-number v-model="form.duration" :min="30" :max="480" :step="30" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="单价(元/课时)"><el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="课程介绍"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="课程描述..." /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const tableData = ref([]); const loading = ref(false); const total = ref(0)
const page = ref(1); const pageSize = ref(20)
const filters = reactive({ keyword: '', type: null })
const dialogVisible = ref(false); const isEdit = ref(false); const saving = ref(false)
const formRef = ref(null)
const form = reactive({ name: '', code: '', type: 1, subject: '', gradeRange: '', duration: 120, unitPrice: null, description: '', status: 1 })
const rules = { name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }], type: [{ required: true, message: '请选择课程类型', trigger: 'change' }] }

onMounted(loadData)
async function loadData() {
  loading.value = true
  try { const d = await request.get('/courses', { params: { ...filters, page: page.value, pageSize: pageSize.value } }); tableData.value = d?.list || []; total.value = d?.total || 0 } catch(e) {} finally { loading.value = false }
}
function openDialog(row) {
  if (row) { isEdit.value = true; Object.assign(form, row) } else { isEdit.value = false; resetForm() }
  dialogVisible.value = true
}
function resetForm() { Object.assign(form, { name: '', code: '', type: 1, subject: '', gradeRange: '', duration: 120, unitPrice: null, description: '', status: 1 }) }
async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) { await request.put(`/courses/${form.id}`, form); ElMessage.success('更新成功') }
    else { await request.post('/courses', form); ElMessage.success('创建成功') }
    dialogVisible.value = false; loadData()
  } catch(e) {} finally { saving.value = false }
}
async function toggleStatus(row) {
  try { await request.put(`/courses/${row.id}`, { ...row, status: row.status === 1 ? 0 : 1 }); ElMessage.success(row.status===1?'已下架':'已上架'); loadData() } catch(e) {}
}
async function remove(row) { try { await request.delete(`/courses/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {} }
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; flex-wrap: wrap; }
</style>
