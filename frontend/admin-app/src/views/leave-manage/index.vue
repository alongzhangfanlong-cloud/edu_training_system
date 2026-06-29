<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable style="width:130px">
            <el-option label="待审批" :value="0" /><el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="studentId" label="学员ID" width="90" />
        <el-table-column prop="leaveType" label="请假类型" width="100" align="center">
          <template #default="{ row }">{{ {1:'病假',2:'事假',3:'其他'}[row.leaveType] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="请假原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="{0:'warning',1:'success',2:'danger'}[row.status]" size="small">
              {{ {0:'待审批',1:'已通过',2:'已拒绝'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveRemark" label="审批备注" width="140" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="申请时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button size="small" text type="success" @click="approve(row, true)">通过</el-button>
              <el-button size="small" text type="danger" @click="approve(row, false)">拒绝</el-button>
            </template>
            <el-popconfirm title="确定删除?" @confirm="deleteRow(row)">
              <template #reference><el-button size="small" text type="danger">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="rejectVisible" title="拒绝请假" width="400px">
      <el-input v-model="rejectRemark" type="textarea" :rows="2" placeholder="拒绝原因（选填）" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const filters = reactive({ status: null })
const tableData = ref([]); const loading = ref(false)
const rejectVisible = ref(false); const rejectRemark = ref(''); const rejectRow = ref(null)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (filters.status !== null && filters.status !== '') params.status = filters.status
    const data = await request.get('/leaves', { params })
    tableData.value = data?.list || []
  } catch(e) {} finally { loading.value = false }
}

async function approve(row, approved) {
  if (!approved) {
    rejectRow.value = row; rejectRemark.value = ''; rejectVisible.value = true
    return
  }
  try {
    await request.post(`/leaves/${row.id}/approve`, { approved: true, remark: '审批通过' })
    ElMessage.success('已通过'); loadData()
  } catch(e) {}
}

async function confirmReject() {
  try {
    await request.post(`/leaves/${rejectRow.value.id}/approve`, { approved: false, remark: rejectRemark.value })
    ElMessage.success('已拒绝'); rejectVisible.value = false; loadData()
  } catch(e) {}
}

async function deleteRow(row) {
  try { await request.delete(`/leaves/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {}
}
</script>
<style scoped>.filter-form { margin-bottom: 0; }</style>
