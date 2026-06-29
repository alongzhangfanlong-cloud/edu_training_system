<template>
  <div>
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6"><el-card shadow="hover"><div class="stat"><div class="val">¥{{ summary.todayRevenue }}</div><div class="lbl">今日收入</div></div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><div class="stat"><div class="val">¥{{ summary.monthRevenue }}</div><div class="lbl">本月收入</div></div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><div class="stat"><div class="val">{{ summary.monthOrderCount }}笔</div><div class="lbl">本月订单</div></div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><div class="stat"><div class="val">{{ summary.pendingCount }}笔</div><div class="lbl">待支付</div></div></el-card></el-col>
    </el-row>

    <el-card header="订单明细" style="margin-top:16px">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="payStatus" placeholder="全部" clearable style="width:120px" @change="loadOrders">
            <el-option label="未支付" :value="0" /><el-option label="已支付" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadOrders">查询</el-button></el-form-item>
      </el-form>
      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="studentId" label="学员ID" width="80" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{row}">{{ {1:'新报',2:'续费',3:'扩科',4:'补费'}[row.type] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="100" />
        <el-table-column prop="payAmount" label="实付" width="100">
          <template #default="{row}"><span style="font-weight:600;color:#52c41a">¥{{ row.payAmount || 0 }}</span></template>
        </el-table-column>
        <el-table-column prop="payStatusName" label="支付状态" width="90" align="center">
          <template #default="{row}"><el-tag :type="row.payStatus===1?'success':'warning'" size="small">{{ row.payStatusName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="90">
          <template #default="{row}">{{ {1:'微信',2:'支付宝',3:'线下',4:'余额'}[row.payMethod] || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="loadOrders" />
      </div>
    </el-card>

    <el-card header="教师课时费概览" style="margin-top:16px">
      <el-table :data="salaryData" empty-text="加载中...">
        <el-table-column prop="teacherName" label="教师" />
        <el-table-column prop="scheduleCount" label="本月排课" width="100" />
        <el-table-column prop="estimatedSalary" label="预估课时费" width="120">
          <template #default="{row}"><span style="color:#1989fa;font-weight:600">¥{{ row.estimatedSalary }}</span></template>
        </el-table-column>
      </el-table>
      <el-button type="primary" style="margin-top:12px" @click="exportExcel">导出Excel</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const summary = reactive({ todayRevenue: '0', monthRevenue: '0', monthOrderCount: 0, pendingCount: 0 })
const orders = ref([]); const loading = ref(false)
const page = ref(1); const pageSize = ref(10); const total = ref(0); const payStatus = ref(null)
const salaryData = ref([])

onMounted(() => { loadSummary(); loadOrders(); loadSalary() })

async function loadSummary() {
  try {
    const data = await request.get('/finance/summary')
    if (data) Object.assign(summary, {
      todayRevenue: data.todayRevenue || '0',
      monthRevenue: data.monthRevenue || '0',
      monthOrderCount: data.monthOrderCount || 0,
      pendingCount: data.pendingCount || 0
    })
  } catch(e) {}
}

async function loadOrders() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (payStatus.value !== null && payStatus.value !== '') params.payStatus = payStatus.value
    const data = await request.get('/orders', { params })
    orders.value = data?.list || []
    total.value = data?.total || 0
  } catch(e) {} finally { loading.value = false }
}

async function loadSalary() {
  try {
    const teachers = await request.get('/teachers', { params: { pageSize: 50 } })
    const list = teachers?.list || []
    salaryData.value = list.map(t => ({
      teacherName: t.name,
      scheduleCount: '-',
      estimatedSalary: '-'
    }))
  } catch(e) {}
}

function exportExcel() { ElMessage.info('导出功能完善中') }
</script>

<style scoped>
.stat { text-align: center; }
.val { font-size: 28px; font-weight: 700; color: #1890ff; }
.lbl { font-size: 14px; color: #999; margin-top: 4px; }
</style>
