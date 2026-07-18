<!-- Tab3: 退费处理 -->
<template>
  <div>
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="学员">
        <el-select v-model="queryParams.studentId" filterable remote :remote-method="searchStudents" :loading="studentLoading" placeholder="请搜索学员" clearable style="width:180px">
          <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:140px">
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="待审批" value="PENDING_APPROVAL" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['tuition:refund:apply']">
          <el-icon><Plus /></el-icon>发起退费
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['tuition:refund:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="序号" align="center" width="60" type="index" />
      <el-table-column label="退费编号" align="center" prop="refundNo" width="150" />
      <el-table-column label="学员" align="center" prop="studentId" width="80" />
      <el-table-column label="课程" align="center" prop="courseId" width="80" />
      <el-table-column label="退费金额" align="right" prop="amount" width="110" />
      <el-table-column label="退费原因" align="center" width="100">
        <template #default="{ row }">{{ reasonLabel(row.reason) }}</template>
      </el-table-column>
      <el-table-column label="退费方式" align="center" width="90">
        <template #default="{ row }">{{ methodLabel(row.refundMethod) }}</template>
      </el-table-column>
      <el-table-column label="扣回课时" align="center" prop="hoursDeducted" width="80" />
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <el-tag :type="refundStatusType(row.status)" size="small">{{ refundStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="success" size="small" @click="handleApprove(row)" v-permission="['tuition:refund:approve']" v-if="row.status === 'PENDING_APPROVAL'">通过</el-button>
          <el-button link type="warning" size="small" @click="handleReject(row)" v-permission="['tuition:refund:approve']" v-if="row.status === 'PENDING_APPROVAL'">驳回</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['tuition:refund:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无退费记录" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <RefundFormModal v-model:visible="open" title="发起退费" @submit-success="getList" />

    <!-- 驳回弹窗 -->
    <el-dialog v-model="rejectVisible" title="驳回退费" width="450px" destroy-on-close>
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="驳回原因">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue';
import { listRefund, approveRefund, rejectRefund, delRefund } from '@/api/finance/tuitionRefunds';
import { listStudent } from '@/api/student/students';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import RefundFormModal from './RefundFormModal.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, studentId: '', status: '' });
const studentOptions = ref([]);
const studentLoading = ref(false);
const open = ref(false);
const rejectVisible = ref(false);
const rejectForm = ref({ id: null, reason: '' });

let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.studentId) params.studentId = queryParams.value.studentId;
  if (queryParams.value.status) params.status = queryParams.value.status;
  listRefund(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

let sTimer = null; let sSeq = 0;
function searchStudents(keyword) {
  clearTimeout(sTimer);
  if (!keyword) { studentOptions.value = []; return; }
  studentLoading.value = true;
  sTimer = setTimeout(() => { sSeq++; const seq = sSeq;
    listStudent({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== sSeq) return;
      if (res.success) studentOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === sSeq) studentLoading.value = false; });
  }, 300);
}

function reasonLabel(v) { const m = { STUDENT_WITHDRAW: '学生退课', COURSE_ADJUST: '课程调整', SERVICE_ISSUE: '服务质量', DUPLICATE_CHARGE: '重复收费', OTHER: '其他' }; return m[v] || v; }
function methodLabel(v) { const m = { ORIGINAL: '原路退回', CASH: '现金', WECHAT: '微信', ALIPAY: '支付宝', BANK_TRANSFER: '银行转账' }; return m[v] || v; }
function refundStatusLabel(v) { const m = { COMPLETED: '已完成', PENDING_APPROVAL: '待审批', APPROVED: '已通过', REJECTED: '已驳回' }; return m[v] || v; }
function refundStatusType(v) { const m = { COMPLETED: 'success', PENDING_APPROVAL: 'warning', APPROVED: 'info', REJECTED: 'danger' }; return m[v] || ''; }

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, pageSize: 10, studentId: '', status: '' }; getList(); }
function handleAdd() { open.value = true; }

function handleApprove(row) {
  ElMessageBox.confirm('确认审批通过该退费申请？', '确认', { type: 'warning' })
    .then(() => approveRefund(row.id, 1)).then(res => {
      if (res.success) { getList(); ElMessage.success('审批通过'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handleReject(row) { rejectForm.value = { id: row.id, reason: '' }; rejectVisible.value = true; }
function submitReject() {
  rejectRefund(rejectForm.value.id, 1, rejectForm.value.reason).then(res => {
    if (res.success) { getList(); rejectVisible.value = false; ElMessage.success('已驳回'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择记录'); return; }
  ElMessageBox.confirm('确认删除选中的退费记录？', '警告', { type: 'warning' })
    .then(() => delRefund(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.mb8 { margin-bottom: 8px; }
.error-box { margin: 20px 0; }
</style>
