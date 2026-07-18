<!-- N06.2 消课课酬管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="教师">
        <el-select v-model="queryParams.teacherId" filterable remote :remote-method="searchTeachers" :loading="teacherLoading" placeholder="请搜索教师" clearable style="width:180px">
          <el-option v-for="t in teacherOptions" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="月份">
        <el-month-picker v-model="queryParams.month" placeholder="选择月份" value-format="YYYY-MM" clearable style="width:160px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已确认" value="CONFIRMED" />
          <el-option label="已发放" value="PAID" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['payroll:payroll:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="disabledBatchConfirm" @click="batchConfirm" v-permission="['payroll:payroll:confirm']">
          <el-icon><Check /></el-icon>批量确认
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain size="small" :disabled="disabledBatchCancelConfirm" @click="batchCancelConfirm" v-permission="['payroll:payroll:confirm']">
          <el-icon><Close /></el-icon>批量取消确认
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['payroll:payroll:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe show-summary :summary-method="getSummaries">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="序号" align="center" width="60" type="index" />
      <el-table-column label="教师" align="center" prop="teacherId" width="80" />
      <el-table-column label="月份" align="center" prop="month" width="100" />
      <el-table-column label="课程" align="center" prop="courseId" width="80" />
      <el-table-column label="消课数" align="center" prop="lessonCount" width="90" />
      <el-table-column label="单节课酬" align="right" width="110">
        <template #default="{ row }">¥{{ Number(row.payPerLesson || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="小计" align="right" width="120">
        <template #default="{ row }"><span style="color:#67c23a;font-weight:bold">¥{{ Number(row.totalPay || 0).toFixed(2) }}</span></template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <el-tag :type="payrollStatusType(row.status)" size="small">{{ payrollStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="确认时间" align="center" prop="confirmedAt" width="160" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleConfirm(row)" v-if="row.status === 'DRAFT'" v-permission="['payroll:payroll:confirm']">确认</el-button>
          <el-button link type="warning" size="small" @click="handleCancelConfirm(row)" v-if="row.status === 'CONFIRMED'" v-permission="['payroll:payroll:confirm']">取消确认</el-button>
          <el-button link type="success" size="small" @click="handlePay(row)" v-if="row.status === 'CONFIRMED'" v-permission="['payroll:payroll:pay']">发放</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['payroll:payroll:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无课酬记录" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <PayrollFormModal v-model:visible="open" title="新增课酬" @submit-success="getList" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Check, Close, Delete } from '@element-plus/icons-vue';
import { listPayroll, confirmPayroll, cancelConfirmPayroll, payPayroll, delPayroll } from '@/api/finance/payrolls';
import { listTeacher } from '@/api/teaching/teachers';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import PayrollFormModal from './components/PayrollFormModal.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);
const selectedRows = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const disabledBatchConfirm = computed(() => !selectedRows.value.length || !selectedRows.value.every(r => r.status === 'DRAFT'));
const disabledBatchCancelConfirm = computed(() => !selectedRows.value.length || !selectedRows.value.every(r => r.status === 'CONFIRMED'));
const queryParams = ref({ page: 1, pageSize: 10, teacherId: '', month: '', status: '' });
const teacherOptions = ref([]);
const teacherLoading = ref(false);
const open = ref(false);
let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.teacherId) params.teacherId = queryParams.value.teacherId;
  if (queryParams.value.month) params.month = queryParams.value.month;
  if (queryParams.value.status) params.status = queryParams.value.status;
  listPayroll(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

let tTimer = null; let tSeq = 0;
function searchTeachers(keyword) {
  clearTimeout(tTimer);
  if (!keyword) { teacherOptions.value = []; return; }
  teacherLoading.value = true;
  tTimer = setTimeout(() => { tSeq++; const seq = tSeq;
    listTeacher({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== tSeq) return;
      if (res.success) teacherOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === tSeq) teacherLoading.value = false; });
  }, 300);
}

function payrollStatusLabel(v) { const m = { DRAFT: '草稿', CONFIRMED: '已确认', PAID: '已发放' }; return m[v] || v; }
function payrollStatusType(v) { const m = { DRAFT: 'info', CONFIRMED: 'warning', PAID: 'success' }; return m[v] || ''; }

function getSummaries({ columns, data }) {
  const sums = [];
  columns.forEach((col, idx) => {
    if (idx === 0) { sums[idx] = '合计'; return; }
    if (col.property === 'lessonCount') {
      sums[idx] = data.reduce((s, r) => s + (Number(r.lessonCount) || 0), 0);
    } else if (!col.property || col.property === 'totalPay') {
      const total = data.reduce((s, r) => s + (Number(r.totalPay) || 0), 0);
      sums[idx] = '¥' + total.toFixed(2);
    } else { sums[idx] = ''; }
  });
  return sums;
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, pageSize: 10, teacherId: '', month: '', status: '' }; getList(); }
function handleAdd() { open.value = true; }

function handleConfirm(row) {
  ElMessageBox.confirm('确认该课酬记录？确认后将锁定消课数和单价。', '确认课酬', { type: 'warning' })
    .then(() => confirmPayroll(row.id)).then(res => {
      if (res.success) { getList(); ElMessage.success('确认成功'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handleCancelConfirm(row) {
  ElMessageBox.confirm('取消确认该课酬记录？将退回草稿状态。', '取消确认', { type: 'warning' })
    .then(() => cancelConfirmPayroll(row.id)).then(res => {
      if (res.success) { getList(); ElMessage.success('已取消确认'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handlePay(row) {
  ElMessageBox.confirm('确认发放该课酬？发放后不可逆。', '发放确认', { type: 'warning' })
    .then(() => payPayroll(row.id)).then(res => {
      if (res.success) { getList(); ElMessage.success('发放成功'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function batchConfirm() {
  ElMessageBox.confirm(`确认选中的 ${selectedRows.value.length} 条课酬记录？`, '批量确认', { type: 'warning' })
    .then(() => Promise.allSettled(selectedRows.value.map(r => confirmPayroll(r.id)))).then(results => {
      const ok = results.filter(r => r.status === 'fulfilled' && r.value?.success).length;
      const fail = results.length - ok;
      getList();
      if (fail === 0) ElMessage.success(`批量确认成功（${ok}条）`);
      else ElMessage.warning(`确认完成：成功${ok}条，失败${fail}条`);
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function batchCancelConfirm() {
  ElMessageBox.confirm(`取消确认选中的 ${selectedRows.value.length} 条课酬记录？`, '批量取消确认', { type: 'warning' })
    .then(() => Promise.allSettled(selectedRows.value.map(r => cancelConfirmPayroll(r.id)))).then(results => {
      const ok = results.filter(r => r.status === 'fulfilled' && r.value?.success).length;
      const fail = results.length - ok;
      getList();
      if (fail === 0) ElMessage.success(`批量取消确认成功（${ok}条）`);
      else ElMessage.warning(`取消确认完成：成功${ok}条，失败${fail}条`);
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择记录'); return; }
  ElMessageBox.confirm('确认删除选中的课酬记录？', '警告', { type: 'warning' })
    .then(() => delPayroll(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); selectedRows.value = selection; }

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.error-box { margin: 20px 0; }
</style>
