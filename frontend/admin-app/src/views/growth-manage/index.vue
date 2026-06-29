<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="学员ID">
          <el-input v-model="filters.studentId" placeholder="学员ID" clearable style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="success" @click="showCreateDialog">+ 新增记录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="studentName" label="学员" width="100" />
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column prop="recordDate" label="记录日期" width="120" />
        <el-table-column prop="content" label="评语" min-width="200" show-overflow-tooltip />
        <el-table-column prop="score" label="评分" width="100" align="center">
          <template #default="{ row }">
            <span style="color:#faad14">{{ '★'.repeat(row.score || 0) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
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
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑成长记录' : '新增成长记录'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学员ID" required><el-input v-model.number="form.studentId" placeholder="学员ID" :disabled="!!editingId" /></el-form-item>
        <el-form-item label="评分"><el-rate v-model="form.score" /></el-form-item>
        <el-form-item label="评语" required><el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入评语" /></el-form-item>
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

const filters = reactive({ studentId: '' })
const tableData = ref([]); const loading = ref(false)
const dialogVisible = ref(false); const editingId = ref(null)
const form = reactive({ studentId: null, score: 3, content: '' })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (filters.studentId) params.studentId = filters.studentId
    const data = await request.get('/growth-records', { params })
    tableData.value = data?.list || []
  } catch(e) {} finally { loading.value = false }
}

function showCreateDialog() {
  editingId.value = null
  Object.assign(form, { studentId: null, score: 3, content: '' })
  dialogVisible.value = true
}

function editRow(row) {
  editingId.value = row.id
  Object.assign(form, { studentId: row.studentId, score: row.score || 3, content: row.content })
  dialogVisible.value = true
}

async function submitForm() {
  try {
    if (editingId.value) { await request.put(`/growth-records/${editingId.value}`, form) }
    else { await request.post('/growth-records', form) }
    ElMessage.success('保存成功'); dialogVisible.value = false; loadData()
  } catch(e) {}
}

async function deleteRow(row) {
  try { await request.delete(`/growth-records/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {}
}
</script>
<style scoped>.filter-form { margin-bottom: 0; }</style>
