<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" class="filter-form">
        <el-form-item>
          <el-button type="success" @click="showSendDialog">+ 发送祝福</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="studentId" label="学员ID" width="100" />
        <el-table-column prop="greetingDate" label="祝福日期" width="130" />
        <el-table-column prop="message" label="祝福内容" min-width="280" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="发送时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定删除?" @confirm="deleteRow(row)">
              <template #reference><el-button size="small" text type="danger">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="sendVisible" title="发送生日祝福" width="480px">
      <el-form :model="sendForm" label-width="80px">
        <el-form-item label="学员ID" required><el-input v-model.number="sendForm.studentId" placeholder="学员ID" /></el-form-item>
        <el-form-item label="祝福语" required><el-input v-model="sendForm.message" type="textarea" :rows="3" placeholder="如：祝你生日快乐！学业进步！" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const tableData = ref([]); const loading = ref(false)
const sendVisible = ref(false); const sendForm = reactive({ studentId: null, message: '' })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const data = await request.get('/birthday-greetings')
    tableData.value = data?.list || []
  } catch(e) {} finally { loading.value = false }
}

function showSendDialog() {
  sendForm.studentId = null; sendForm.message = ''
  sendVisible.value = true
}

async function submitSend() {
  try {
    await request.post('/birthday-greetings', null, { params: { studentId: sendForm.studentId, message: sendForm.message } })
    ElMessage.success('发送成功'); sendVisible.value = false; loadData()
  } catch(e) {}
}

async function deleteRow(row) {
  try { await request.delete(`/birthday-greetings/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {}
}
</script>
<style scoped>.filter-form { margin-bottom: 0; }</style>
