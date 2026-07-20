<!-- 兑换记录管理列表页 -->
<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="学生">
        <el-select
          v-model="queryParams.studentId" filterable remote
          :remote-method="searchStudents" :loading="studentLoading"
          placeholder="搜索学生姓名" clearable style="width:180px"
          @change="handleQuery"
        >
          <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="礼品">
        <el-select
          v-model="queryParams.giftId" filterable remote
          :remote-method="searchGifts" :loading="giftLoading"
          placeholder="搜索礼品名称" clearable style="width:200px"
          @change="handleQuery"
        >
          <el-option v-for="g in giftOptions" :key="g.id" :label="g.name" :value="g.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px" @change="handleQuery">
          <el-option label="待领取" value="PENDING_CLAIM" />
          <el-option label="已领取" value="CLAIMED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="兑换时间">
        <el-date-picker
          v-model="dateRange" type="daterange" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期"
          value-format="YYYY-MM-DD" style="width:240px"
          @change="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success" plain size="small"
          :disabled="total === 0" :loading="exporting"
          @click="handleExport"
          v-permission="['points:redemption:export']"
        >
          <el-icon><Download /></el-icon>导出Excel
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading" :data="list"
      border stripe
      @expand-change="onExpandChange"
    >
      <el-table-column type="expand">
        <template #default="{ row }">
          <RedemptionDetail :redemption-id="row.id" :visible="expandedRows.has(row.id)" />
        </template>
      </el-table-column>
      <el-table-column label="编号" align="center" prop="id" width="70" />
      <el-table-column label="学生姓名" align="left" width="100">
        <template #default="{ row }">{{ row._studentName ?? `学员${row.studentId}` }}</template>
      </el-table-column>
      <el-table-column label="礼品名称" align="left" :show-overflow-tooltip="true" min-width="140">
        <template #default="{ row }">{{ row._giftName ?? `礼品${row.giftId}` }}</template>
      </el-table-column>
      <el-table-column label="消耗积分" align="center" width="100">
        <template #default="{ row }">{{ formatNumber(row.pointsSpent) }}</template>
      </el-table-column>
      <el-table-column label="兑换时间" align="center" width="160">
        <template #default="{ row }">{{ row.redeemTime || row.createTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 'PENDING_CLAIM'">
            <el-button link type="success" size="small" @click="handleClaim(row)" v-permission="['points:redemption:claim']">
              标记已领取
            </el-button>
            <el-button link type="danger" size="small" @click="handleCancel(row)" v-permission="['points:redemption:cancel']">
              取消兑换
            </el-button>
          </template>
          <span v-else class="text-muted">—</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空状态 -->
    <el-empty v-if="!loading && list.length === 0" description="暂无兑换记录" />

    <!-- 错误状态 -->
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.page"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Download } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { listRedemption, claimRedemption, cancelRedemption } from '@/api/points/index';
import { listGift } from '@/api/points/index';
import { listStudent } from '@/api/student/students';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import RedemptionDetail from './RedemptionDetail.vue';

const userStore = useUserStore();

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const expandedRows = ref(new Set());

const queryParams = ref({ page: 1, pageSize: 10, studentId: '', giftId: '', status: '' });
const dateRange = ref([]);

const studentOptions = ref([]);
const studentLoading = ref(false);
const giftOptions = ref([]);
const giftLoading = ref(false);

const exporting = ref(false);

let querySeq = 0;

// ── 状态映射 ──

function statusLabel(v) { const m = { PENDING_CLAIM: '待领取', CLAIMED: '已领取', CANCELLED: '已取消' }; return m[v] ?? v; }
function statusType(v) { const m = { PENDING_CLAIM: 'warning', CLAIMED: 'success', CANCELLED: 'info' }; return m[v] ?? ''; }
function formatNumber(num) { if (num == null) return '-'; return Number(num).toLocaleString(); }

// ── 列表加载 ──

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.studentId) params.studentId = queryParams.value.studentId;
  if (queryParams.value.giftId) params.giftId = queryParams.value.giftId;
  if (queryParams.value.status) params.status = queryParams.value.status;
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = dateRange.value[0];
    params.endDate = dateRange.value[1];
  }

  listRedemption(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      const records = res.data?.records || res.rows || [];
      list.value = records;
      total.value = res.data?.total || res.total || 0;
      error.value = '';
      enrichNames(records);
    } else {
      error.value = res.msg || '加载失败';
    }
  }).catch(() => {
    if (seq === querySeq) error.value = '网络错误，请检查网络后重试';
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

/** 补充学生姓名和礼品名称 */
async function enrichNames(records) {
  const studentIds = [...new Set(records.map(r => r.studentId).filter(Boolean))];
  const giftIds = [...new Set(records.map(r => r.giftId).filter(Boolean))];

  const [studentMap, giftMap] = await Promise.allSettled([
    studentIds.length > 0
      ? listStudent({ page: 1, pageSize: 500 }).then(res => {
          const m = {};
          if (res.success) (res.data?.records || res.rows || []).forEach(s => { m[s.id] = s.name; });
          return m;
        })
      : Promise.resolve({}),
    giftIds.length > 0
      ? listGift({ page: 1, pageSize: 500 }).then(res => {
          const m = {};
          if (res.success) (res.data?.records || res.rows || []).forEach(g => { m[g.id] = g.name; });
          return m;
        })
      : Promise.resolve({}),
  ]);

  const sMap = studentMap.status === 'fulfilled' ? studentMap.value : {};
  const gMap = giftMap.status === 'fulfilled' ? giftMap.value : {};

  records.forEach(r => {
    r._studentName = sMap[r.studentId] || null;
    r._giftName = gMap[r.giftId] || null;
  });
}

// ── 远程搜索 ──

let sTimer = null; let sSeq = 0;
function searchStudents(keyword) {
  clearTimeout(sTimer);
  if (!keyword) { studentOptions.value = []; return; }
  studentLoading.value = true;
  sTimer = setTimeout(() => { sSeq++; const seq = sSeq;
    listStudent({ page: 1, pageSize: 50, keyword }).then(res => {
      if (seq !== sSeq) return;
      if (res.success) studentOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === sSeq) studentLoading.value = false; });
  }, 300);
}

let gTimer = null; let gSeq = 0;
function searchGifts(keyword) {
  clearTimeout(gTimer);
  if (!keyword) { giftOptions.value = []; return; }
  giftLoading.value = true;
  gTimer = setTimeout(() => { gSeq++; const seq = gSeq;
    listGift({ page: 1, pageSize: 50, keyword, status: 1 }).then(res => {
      if (seq !== gSeq) return;
      if (res.success) giftOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === gSeq) giftLoading.value = false; });
  }, 300);
}

// ── 操作 ──

function handleQuery() { queryParams.value.page = 1; getList(); }

function resetQuery() {
  queryParams.value = { page: 1, pageSize: 10, studentId: '', giftId: '', status: '' };
  dateRange.value = [];
  getList();
}

/** 标记已领取 */
function handleClaim(row) {
  ElMessageBox.confirm(
    `确认学生「${row._studentName || '学员' + row.studentId}」已领取「${row._giftName || '礼品' + row.giftId}」？`,
    '确认领取',
    { type: 'warning', confirmButtonText: '确认已领取', cancelButtonText: '取消' }
  ).then(async () => {
    try {
      const res = await claimRedemption(row.id, userStore.id || 0);
      if (res.success) { getList(); ElMessage.success('已标记为已领取'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    } catch { ElMessage.error('网络异常，请稍后重试'); }
  }).catch(() => {});
}

/** 取消兑换 */
function handleCancel(row) {
  ElMessageBox.confirm(
    `取消后将退还 ${row.pointsSpent} 积分并恢复库存，确定取消？`,
    '取消兑换',
    { type: 'warning', confirmButtonText: '确定取消', cancelButtonText: '再想想' }
  ).then(async () => {
    try {
      const res = await cancelRedemption(row.id, userStore.id || 0);
      if (res.success) { getList(); ElMessage.success('已取消兑换，积分和库存已退还'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    } catch { ElMessage.error('网络异常，请稍后重试'); }
  }).catch(() => {});
}

/** 行展开 */
function onExpandChange(row, expandedRowsArr) {
  expandedRows.value.clear();
  expandedRowsArr.forEach(r => expandedRows.value.add(r.id));
}

/** 导出 Excel */
function handleExport() {
  if (total.value === 0) { ElMessage.warning('暂无数据可导出'); return; }
  exporting.value = true;
  const params = {
    page: 1,
    pageSize: Math.min(total.value, 10000),
  };
  if (queryParams.value.studentId) params.studentId = queryParams.value.studentId;
  if (queryParams.value.giftId) params.giftId = queryParams.value.giftId;
  if (queryParams.value.status) params.status = queryParams.value.status;
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = dateRange.value[0];
    params.endDate = dateRange.value[1];
  }

  // 纯前端导出：拉取全部数据后生成 CSV
  listRedemption(params).then(async res => {
    if (res.success) {
      const records = res.data?.records || res.rows || [];
      if (records.length === 0) { ElMessage.warning('暂无数据可导出'); return; }
      await enrichNames(records);
      downloadCSV(records);
      ElMessage.success(`导出成功，共 ${records.length} 条记录`);
    } else {
      ElMessage.error(res.msg || '导出失败');
    }
  }).catch(() => {
    ElMessage.error('导出失败，请稍后重试');
  }).finally(() => {
    exporting.value = false;
  });
}

/** 生成 CSV 并下载 */
function downloadCSV(records) {
  const headers = ['序号', '学生姓名', '礼品名称', '消耗积分', '兑换时间', '状态', '领取时间', '取消时间', '操作人'];
  const rows = records.map((r, i) => [
    i + 1,
    r._studentName || `学员${r.studentId}`,
    r._giftName || `礼品${r.giftId}`,
    r.pointsSpent,
    r.redeemTime || r.createTime || '',
    statusLabel(r.status),
    r.claimTime || '',
    r.cancelTime || '',
    r.operatorName || '',
  ]);

  const BOM = '﻿';
  const csvContent = BOM + [headers, ...rows]
    .map(row => row.map(cell => `"${String(cell ?? '').replace(/"/g, '""')}"`).join(','))
    .join('\n');

  const now = new Date();
  const ts = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}_${String(now.getHours()).padStart(2, '0')}${String(now.getMinutes()).padStart(2, '0')}${String(now.getSeconds()).padStart(2, '0')}`;
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `积分兑换记录_${ts}.csv`;
  link.click();
  URL.revokeObjectURL(url);
}

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.text-muted { color: #c0c4cc; }
.error-box { margin: 20px 0; }
</style>
