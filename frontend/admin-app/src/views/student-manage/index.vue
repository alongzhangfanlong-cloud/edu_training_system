<template>
  <div>
    <!-- 筛选栏 -->
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="搜索">
          <el-input v-model="filters.keyword" placeholder="姓名/手机号/编号" clearable style="width:220px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width:130px">
            <el-option label="意向" :value="0" /><el-option label="在读" :value="1" />
            <el-option label="停课" :value="2" /><el-option label="结课" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="success" @click="showCreateDialog">+ 新增学员</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 学员表格 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="studentNo" label="编号" width="120" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="grade" label="年级" width="80" align="center" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="totalCourses" label="在读课程" width="90" align="center" />
        <el-table-column label="课时" width="110" align="center">
          <template #default="{ row }">
            <span :class="(row.remainHours || 0) <= 5 ? 'text-danger' : 'text-primary'" style="font-weight:600">
              {{ row.remainHours || 0 }}
            </span>
            <span style="color:#999;font-size:12px">/{{ row.totalHours || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="viewDetail(row)">详情</el-button>
            <el-button size="small" text type="primary" @click="editStudent(row)">编辑</el-button>
            <el-button size="small" text type="success" @click="enrollStudent(row)">报名</el-button>
            <el-popconfirm title="确定删除?" @confirm="deleteStudent(row)">
              <template #reference>
                <el-button size="small" text type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="580px">
      <el-form :model="form" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="姓名" required><el-input v-model="form.name" placeholder="学员姓名" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" placeholder="手机号" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" style="width:100%"><el-option label="男" :value="1" /><el-option label="女" :value="2" /></el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级"><el-input v-model="form.grade" placeholder="如: 初三" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="学校"><el-input v-model="form.school" placeholder="就读学校" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:200px">
            <el-option label="意向" :value="0" /><el-option label="在读" :value="1" />
            <el-option label="停课" :value="2" /><el-option label="结课" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" /></el-form-item>
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

const filters = reactive({ keyword: '', status: null })
const tableData = ref([]); const loading = ref(false)
const page = ref(1); const pageSize = ref(20); const total = ref(0)
const dialogVisible = ref(false); const dialogTitle = ref('新增学员')
const form = reactive({ name: '', phone: '', gender: null, grade: '', school: '', status: 1, remark: '' })
const editingId = ref(null)

const statusMap = { 0:'意向', 1:'在读', 2:'停课', 3:'结课', 4:'流失' }
const statusType = (s) => ({ 0:'info', 1:'success', 2:'warning', 3:'', 4:'danger' }[s])

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const data = await request.get('/students', { params: { ...filters, page: page.value, pageSize: pageSize.value } })
    if (data) { tableData.value = data.list; total.value = data.total }
  } catch(e) {} finally { loading.value = false }
}

function showCreateDialog() {
  dialogTitle.value = '新增学员'
  Object.assign(form, { name: '', phone: '', gender: null, grade: '', school: '', status: 1, remark: '' })
  editingId.value = null; dialogVisible.value = true
}
function editStudent(row) {
  dialogTitle.value = '编辑学员'
  Object.assign(form, { id: row.id, name: row.name || '', phone: row.phone || '', gender: row.gender, grade: row.grade || '', school: row.school || '', status: row.status, remark: row.remark || '' })
  editingId.value = row.id; dialogVisible.value = true
}
async function submitForm() {
  try {
    if (editingId.value) { await request.put(`/students/${editingId.value}`, form) }
    else { await request.post('/students', form) }
    ElMessage.success('保存成功'); dialogVisible.value = false; loadData()
  } catch(e) {}
}
function viewDetail(row) { ElMessage.info('详情功能完善中') }
function enrollStudent(row) { ElMessage.info('请前往【报名缴费】页面操作') }
async function deleteStudent(row) {
  try { await request.delete(`/students/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {}
}
</script>

<style scoped>
.filter-form { margin-bottom: 0; }
.text-danger { color: #ff4d4f; }
.text-primary { color: #1890ff; }
</style>
