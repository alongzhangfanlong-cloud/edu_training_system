<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="班级ID">
          <el-input v-model="filters.classId" placeholder="班级ID" clearable style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="success" @click="showCreateDialog">+ 布置作业</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="classId" label="班级ID" width="90" />
        <el-table-column prop="content" label="作业内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="布置时间" width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="editRow(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="deleteRow(row)">
              <template #reference><el-button size="small" text type="danger">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑作业' : '布置作业'" width="540px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="班级ID"><el-input v-model.number="form.classId" placeholder="班级ID" /></el-form-item>
        <el-form-item label="排课ID"><el-input v-model.number="form.scheduleId" placeholder="排课ID（选填）" /></el-form-item>
        <el-form-item label="作业内容" required><el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入作业内容" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const filters = reactive({ classId: '' })
const tableData = ref([]); const loading = ref(false)
const dialogVisible = ref(false); const editingId = ref(null)
const form = reactive({ classId: null, scheduleId: null, content: '' })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (filters.classId) params.classId = filters.classId
    const data = await request.get('/homework', { params })
    tableData.value = data?.list || []
  } catch(e) {} finally { loading.value = false }
}

function showCreateDialog() {
  editingId.value = null
  Object.assign(form, { classId: null, scheduleId: null, content: '' })
  dialogVisible.value = true
}

function editRow(row) {
  editingId.value = row.id
  Object.assign(form, { classId: row.classId, scheduleId: row.scheduleId, content: row.content })
  dialogVisible.value = true
}

async function submitForm() {
  try {
    if (editingId.value) { await request.put(`/homework/${editingId.value}`, form) }
    else { await request.post('/homework', form) }
    ElMessage.success('保存成功'); dialogVisible.value = false; loadData()
  } catch(e) {}
}

async function deleteRow(row) {
  try { await request.delete(`/homework/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {}
}
</script>
<style scoped>.filter-form { margin-bottom: 0; }</style>
