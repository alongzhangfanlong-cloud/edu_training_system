<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" class="filter-form">
        <el-form-item>
          <el-button type="success" @click="showChangeDialog">+ 积分变动</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="studentId" label="学员ID" width="100" />
        <el-table-column prop="balance" label="积分余额" width="120" align="center">
          <template #default="{ row }">
            <span style="font-weight:600;color:#1989fa">{{ row.balance || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="viewTransactions(row)">流水</el-button>
            <el-button size="small" text type="success" @click="changeForStudent(row)">变动</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="changeVisible" title="积分变动" width="460px">
      <el-form :model="changeForm" label-width="80px">
        <el-form-item label="学员ID"><el-input v-model.number="changeForm.studentId" :disabled="true" /></el-form-item>
        <el-form-item label="变动数量" required>
          <el-input-number v-model="changeForm.changeAmount" :step="100" />
          <span style="color:#999;font-size:12px;margin-left:8px">正数增加，负数扣减</span>
        </el-form-item>
        <el-form-item label="变动原因" required><el-input v-model="changeForm.reason" placeholder="如：课堂表现优秀" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChange">确认变动</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="txVisible" title="积分流水" width="560px">
      <el-table :data="transactions" size="small" stripe>
        <el-table-column prop="changeAmount" label="变动" width="100">
          <template #default="{ row }">
            <span :style="{color: row.changeAmount > 0 ? '#52c41a' : '#ff4d4f', fontWeight: 600}">
              {{ row.changeAmount > 0 ? '+' : '' }}{{ row.changeAmount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="180" />
        <el-table-column prop="createdAt" label="时间" width="170" />
      </el-table>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const tableData = ref([]); const loading = ref(false)
const changeVisible = ref(false); const changeForm = reactive({ studentId: null, changeAmount: 100, reason: '' })
const txVisible = ref(false); const transactions = ref([])

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const data = await request.get('/points')
    tableData.value = data?.list || []
  } catch(e) {} finally { loading.value = false }
}

function showChangeDialog() {
  changeForm.studentId = null; changeForm.changeAmount = 100; changeForm.reason = ''
  changeVisible.value = true
}

function changeForStudent(row) {
  changeForm.studentId = row.studentId; changeForm.changeAmount = 100; changeForm.reason = ''
  changeVisible.value = true
}

async function submitChange() {
  try {
    await request.post('/points/change', changeForm)
    ElMessage.success('变动成功'); changeVisible.value = false; loadData()
  } catch(e) {}
}

async function viewTransactions(row) {
  try {
    const data = await request.get(`/points/student/${row.studentId}/transactions`)
    transactions.value = data?.list || []
    txVisible.value = true
  } catch(e) {}
}
</script>
<style scoped>.filter-form { margin-bottom: 0; }</style>
