<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="排课ID">
          <el-input v-model="filters.scheduleId" placeholder="排课ID" clearable style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="scheduleId" label="排课ID" width="100" />
        <el-table-column prop="content" label="课堂内容" min-width="180" show-overflow-tooltip />
        <el-table-column prop="homework" label="课堂作业" min-width="160" show-overflow-tooltip />
        <el-table-column prop="overallComment" label="总体评价" width="140" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="提交时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="viewDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="detailVisible" title="课堂反馈详情" width="640px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="排课ID">{{ detail.scheduleId }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="课堂内容" :span="2">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="课堂作业" :span="2">{{ detail.homework }}</el-descriptions-item>
        <el-descriptions-item label="总体评价" :span="2">{{ detail.overallComment }}</el-descriptions-item>
        <el-descriptions-item label="下节预告" :span="2">{{ detail.nextPreview }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="detail.studentFeedbacks && detail.studentFeedbacks.length" style="margin-top:16px">
        <h4 style="margin-bottom:8px">学员个人反馈</h4>
        <el-table :data="detail.studentFeedbacks" size="small" stripe>
          <el-table-column prop="studentId" label="学员ID" width="80" />
          <el-table-column prop="performanceScore" label="表现评分" width="100">
            <template #default="{ row }">{{ '★'.repeat(row.performanceScore || 0) }}</template>
          </el-table-column>
          <el-table-column prop="performanceComment" label="表现评语" min-width="200" />
          <el-table-column prop="homeworkStatus" label="作业状态" width="100">
            <template #default="{ row }">{{ {1:'已完成',2:'部分完成',3:'未完成'}[row.homeworkStatus] || '-' }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'

const filters = reactive({ scheduleId: '' })
const tableData = ref([]); const loading = ref(false)
const detailVisible = ref(false); const detail = ref({})

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (filters.scheduleId) params.scheduleId = filters.scheduleId
    const data = await request.get('/feedbacks', { params })
    tableData.value = data?.list || []
  } catch(e) {} finally { loading.value = false }
}

async function viewDetail(row) {
  try {
    const data = await request.get(`/feedbacks/schedule/${row.scheduleId}`)
    detail.value = data || {}
    detailVisible.value = true
  } catch(e) {}
}
</script>
<style scoped>.filter-form { margin-bottom: 0; }</style>
