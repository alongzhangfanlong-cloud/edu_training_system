<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" :style="{ background: card.color }">{{ card.icon }}</div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="16">
        <el-card header="营收趋势 (近30天)">
          <div ref="revenueChart" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card header="课程满班率">
          <div ref="fillRateChart" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷功能 + 实时动态 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card header="快捷功能">
          <el-row :gutter="12">
            <el-col :span="12" v-for="btn in quickActions" :key="btn.label" style="margin-bottom:12px">
              <el-button style="width:100%" @click="$router.push(btn.path)">{{ btn.label }}</el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="出勤率趋势">
          <div ref="attendanceChart" style="height:280px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import request from '../../utils/request'

let echarts = null
async function loadEcharts() {
  if (echarts) return echarts
  try { const m = await import('echarts'); echarts = m.default || m; return echarts } catch(e) { return null }
}

const revenueChart = ref(null)
const fillRateChart = ref(null)
const attendanceChart = ref(null)

const statCards = ref([
  { title: '今日上课', value: '0节', icon: '📅', color: '#409eff' },
  { title: '今日营收', value: '¥0', icon: '💰', color: '#67c23a' },
  { title: '新增学员', value: '0人', icon: '👤', color: '#e6a23c' },
  { title: '待处理', value: '0项', icon: '📋', color: '#f56c6c' }
])

const quickActions = [
  { label: '排课管理', path: '/schedule-manage' },
  { label: '报名缴费', path: '/enrollment' },
  { label: '学员管理', path: '/student-manage' },
  { label: '考勤查询', path: '/attendance-query' }
]

onMounted(async () => {
  loadEcharts()
  try {
    const data = await request.get('/statistics/dashboard', { params: { date_range: 'month' } })
    if (data) {
      statCards.value[0].value = (data.todayClasses || 0) + '节'
      statCards.value[1].value = '¥' + (data.todayRevenue || 0)
      statCards.value[2].value = (data.newStudents || 0) + '人'
      statCards.value[3].value = (data.pendingLeaves || 0) + '项'
    }
    await nextTick()
    if (data) {
      try { renderRevenueChart(data.revenueTrend || []) } catch(e) {}
      try { renderFillRateChart(data.classFillRate || []) } catch(e) {}
      try { renderAttendanceChart(data.attendanceTrend || []) } catch(e) {}
    }
  } catch(e) { console.error('dashboard error:', e) }
})

function renderRevenueChart(data) {
  if (!revenueChart.value || !echarts || !data.length) return
  const chart = echarts.init(revenueChart.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: data.map(d => (d.date || '').slice(5)), axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', axisLabel: { formatter: v => '¥' + (v/1000).toFixed(1) + 'k' } },
    series: [{
      name: '营收', type: 'line', data: data.map(d => d.amount || 0),
      smooth: true, areaStyle: { color: 'rgba(64,158,255,0.15)' },
      lineStyle: { color: '#409eff', width: 2 },
      itemStyle: { color: '#409eff' }
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}

function renderFillRateChart(data) {
  if (!fillRateChart.value || !echarts || !data.length) return
  const chart = echarts.init(fillRateChart.value)
  chart.setOption({
    tooltip: { trigger: 'axis', formatter: p => `${p[0].name}: ${p[0].value}%` },
    grid: { left: 100, right: 30, top: 10, bottom: 20 },
    xAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
    yAxis: { type: 'category', data: data.map(d => d.name), inverse: true, axisLabel: { fontSize: 11, width: 80, overflow: 'truncate' } },
    series: [{
      type: 'bar', data: data.map(d => d.rate || 0),
      barMaxWidth: 20,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#409eff' }, { offset: 1, color: '#67c23a' }
        ]),
        borderRadius: [0, 4, 4, 0]
      },
      label: { show: true, position: 'right', formatter: '{c}%', fontSize: 10 }
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}

function renderAttendanceChart(data) {
  if (!attendanceChart.value || !echarts || !data.length) return
  const chart = echarts.init(attendanceChart.value)
  chart.setOption({
    tooltip: { trigger: 'axis', formatter: p => `${p[0].name}: ${p[0].value}%` },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: data.map(d => (d.date || '').slice(5)), axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', min: 80, max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{
      name: '出勤率', type: 'line', data: data.map(d => d.rate || 95),
      smooth: true, lineStyle: { color: '#67c23a', width: 2 },
      itemStyle: { color: '#67c23a' },
      areaStyle: { color: 'rgba(103,194,58,0.1)' }
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}
</script>

<style scoped>
.stats-row { }
.stat-card { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 28px; color: #fff; flex-shrink: 0; }
.stat-value { font-size: 28px; font-weight: 700; }
.stat-label { font-size: 14px; color: #999; margin-top: 2px; }
</style>
