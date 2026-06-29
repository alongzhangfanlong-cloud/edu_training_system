<template>
  <div>
    <el-card>
      <el-form :inline="true">
        <el-form-item><el-input v-model="filters.keyword" placeholder="用户名/姓名/手机号" clearable style="width:240px" /></el-form-item>
        <el-form-item><el-select v-model="filters.status" placeholder="状态" clearable style="width:120px"><el-option label="正常" :value="1" /><el-option label="禁用" :value="0" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'正常':'禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="100">
          <template #default="{row}">
            <el-button v-if="row.status===1" size="small" type="warning" @click="toggleStatus(row, 0)">禁用</el-button>
            <el-button v-else size="small" type="success" @click="toggleStatus(row, 1)">启用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const tableData = ref([]); const loading = ref(false); const total = ref(0)
const page = ref(1); const pageSize = ref(20)
const filters = reactive({ keyword: '', status: null })

onMounted(loadData)
async function loadData() {
  loading.value = true
  try {
    const d = await request.get('/users', { params: { ...filters, page: page.value, pageSize: pageSize.value } })
    if (d) { tableData.value = d.list || []; total.value = d.total || 0 }
  } catch(e) {} finally { loading.value = false }
}
async function toggleStatus(row, status) {
  try {
    await request.put(`/users/${row.id}/status`, { status })
    ElMessage.success(status === 1 ? '已启用' : '已禁用'); loadData()
  } catch(e) {}
}
</script>
