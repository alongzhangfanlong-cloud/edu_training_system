<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="filters">
        <el-form-item label="日期">
          <el-date-picker v-model="filters.date" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:160px" />
        </el-form-item>
        <el-form-item label="排课">
          <el-select v-model="filters.scheduleId" placeholder="选择排课" clearable filterable style="width:240px" @change="loadData">
            <el-option v-for="s in scheduleList" :key="s.id" :label="`${s.date} ${s.courseName||''} ${s.startTime}-${s.endTime}`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadSchedules">查询排课</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="studentId" label="学员ID" width="90" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':row.status===2?'warning':'danger'" size="small">
              {{ {1:'出勤',2:'请假',3:'旷课',4:'补课'}[row.status] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="签到时间" width="170" />
        <el-table-column prop="checkInMethod" label="签到方式" width="100" align="center">
          <template #default="{row}">{{ {1:'扫码',2:'教师点名',3:'人脸',4:'蓝牙',5:'补签'}[row.checkInMethod] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="leaveType" label="请假类型" width="100" align="center">
          <template #default="{row}">{{ {1:'病假',2:'事假',3:'其他'}[row.leaveType] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="leaveReason" label="请假原因" min-width="160" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" width="120" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
      </el-table>

      <!-- 出勤统计 -->
      <div v-if="tableData.length" style="margin-top:16px;display:flex;gap:24px;padding:12px;background:#f5f7fa;border-radius:8px">
        <span>📊 总计: <b>{{ tableData.length }}</b>人</span>
        <span style="color:#52c41a">✅ 出勤: <b>{{ tableData.filter(r=>r.status===1).length }}</b>人</span>
        <span style="color:#faad14">📝 请假: <b>{{ tableData.filter(r=>r.status===2).length }}</b>人</span>
        <span style="color:#ff4d4f">❌ 旷课: <b>{{ tableData.filter(r=>r.status===3).length }}</b>人</span>
        <span>📈 出勤率: <b>{{ tableData.length ? Math.round(tableData.filter(r=>r.status===1).length/tableData.length*100) : 0 }}%</b></span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'

const filters = reactive({ date: '', scheduleId: null })
const scheduleList = ref([])
const tableData = ref([])
const loading = ref(false)

onMounted(() => loadSchedules())

async function loadSchedules() {
  try {
    const params = { pageSize: 100 }
    if (filters.date) { params.startDate = filters.date; params.endDate = filters.date }
    else { params.startDate = new Date().toISOString().slice(0,10); params.endDate = params.startDate }
    const d = await request.get('/schedules', { params })
    scheduleList.value = d?.schedules || []
  } catch(e) {}
}

async function loadData() {
  if (!filters.scheduleId) { tableData.value = []; return }
  loading.value = true
  try {
    const d = await request.get(`/attendances/schedule/${filters.scheduleId}`)
    tableData.value = d || []
  } catch(e) {} finally { loading.value = false }
}
</script>
