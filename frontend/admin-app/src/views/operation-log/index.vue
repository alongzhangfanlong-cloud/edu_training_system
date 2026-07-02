<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="filters">
        <el-form-item label="模块">
          <el-select v-model="filters.module" placeholder="全部" clearable style="width:140px" @change="loadData">
            <el-option v-for="m in modules" :key="m" :label="m" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作">
          <el-select v-model="filters.action" placeholder="全部" clearable style="width:120px" @change="loadData">
            <el-option label="CREATE" value="CREATE" /><el-option label="UPDATE" value="UPDATE" /><el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="用户名/详情" clearable style="width:180px" @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe max-height="600">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="role" label="角色" width="80" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="action" label="操作" width="90" align="center">
          <template #default="{row}">
            <el-tag :type="row.action==='CREATE'?'success':row.action==='DELETE'?'danger':'warning'" size="small">{{ row.action }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="target" label="对象" width="160" show-overflow-tooltip />
        <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="createdAt" label="时间" width="170" />
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="loadData" />
      </div>
    </el-card>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'

const filters = reactive({ module: '', action: '', keyword: '' })
const tableData = ref([]); const loading = ref(false)
const page = ref(1); const pageSize = ref(20); const total = ref(0)
const modules = ['Auth', 'User', 'Student', 'Course', 'Teacher', 'Classroom', 'Class', 'Schedule', 'Attendance', 'Consume', 'Order', 'Feedback', 'Homework', 'GrowthRecord', 'Points', 'Leave', 'BirthdayGreeting', 'Campus', 'Menu', 'Permission', 'SystemConfig']

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (filters.module) params.module = filters.module
    if (filters.action) params.action = filters.action
    if (filters.keyword) params.keyword = filters.keyword
    const d = await request.get('/operation-logs', { params })
    tableData.value = d?.list || []
    total.value = d?.total || 0
  } catch(e) {} finally { loading.value = false }
}
</script>
