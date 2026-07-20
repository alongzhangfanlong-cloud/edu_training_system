<!-- 学生请假管理列表页 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="学生">
        <el-select v-model="queryParams.studentId" filterable remote :remote-method="searchStudents" :loading="studentLoading" placeholder="搜索学生姓名" clearable style="width:180px">
          <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="请假类型">
        <el-select v-model="queryParams.leaveType" placeholder="全部" clearable style="width:120px" @change="handleQuery">
          <el-option label="事假" :value="0" />
          <el-option label="病假" :value="1" />
          <el-option label="其他" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px" @change="handleQuery">
          <el-option label="待确认" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['attendance:leave:student:add']">
          <el-icon><Plus /></el-icon>代学生请假
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="编号" align="center" prop="id" width="70" />
      <el-table-column label="学生" align="center" prop="studentName" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="请假日期" align="center" prop="leaveDate" width="110" />
      <el-table-column label="类型" align="center" width="80">
        <template #default="{ row }">{{ leaveTypeLabel(row.leaveType) }}</template>
      </el-table-column>
      <el-table-column label="原因" align="center" prop="reason" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <el-tag :type="leaveStatusType(row.status)" size="small">{{ leaveStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请人" align="center" width="90">
        <template #default="{ row }">{{ row.applicantType === 'TEACHER' ? '教师' : '家长' }}</template>
      </el-table-column>
      <el-table-column label="确认教师" align="center" prop="confirmTeacherName" width="90" />
      <el-table-column label="确认时间" align="center" prop="confirmTime" width="160" />
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button link type="success" size="small" @click="handleApprove(row)" v-permission="['attendance:leave:student:approve']">通过</el-button>
            <el-button link type="danger" size="small" @click="handleReject(row)" v-permission="['attendance:leave:student:approve']">驳回</el-button>
          </template>
          <el-button link type="primary" size="small" @click="handleDetail(row)" v-permission="['attendance:leave:student:query']">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无请假记录" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 代学生请假弹窗 -->
    <StudentLeaveFormModal v-model:visible="open" title="代学生请假" :proxy-mode="true" @submit-success="getList" />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailOpen" title="请假详情" width="500px" destroy-on-close>
      <el-descriptions v-if="detailRow" :column="2" border size="small">
        <el-descriptions-item label="学生">{{ detailRow.studentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请假日期">{{ detailRow.leaveDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ leaveTypeLabel(detailRow.leaveType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ leaveStatusLabel(detailRow.status) }}</el-descriptions-item>
        <el-descriptions-item label="原因" :span="2">{{ detailRow.reason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailRow.applicantType === 'TEACHER' ? '教师' : '家长' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailRow.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="确认教师">{{ detailRow.confirmTeacherName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="确认时间">{{ detailRow.confirmTime || '-' }}</el-descriptions-item>
        <el-descriptions-item v-if="detailRow.status === 2" label="驳回原因" :span="2">{{ detailRow.rejectReason || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 驳回原因弹窗 -->
    <el-dialog v-model="rejectOpen" title="驳回请假" width="420px" destroy-on-close>
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="驳回原因" required>
          <el-input v-model="rejectForm.reason" type="textarea" :rows="3" placeholder="请填写驳回原因（必填）" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectOpen = false">取消</el-button>
        <el-button type="danger" :loading="rejectSubmitting" @click="submitReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { listStudentLeave, approveStudentLeave, rejectStudentLeave } from '@/api/attendance/leaveStudent';
import { listStudent } from '@/api/student/students';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import StudentLeaveFormModal from './components/StudentLeaveFormModal.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const queryParams = ref({ page: 1, pageSize: 10, studentId: '', leaveType: '', status: '' });
const studentOptions = ref([]);
const studentLoading = ref(false);

const open = ref(false);
const detailOpen = ref(false);
const detailRow = ref(null);
const rejectOpen = ref(false);
const rejectForm = ref({ id: null, reason: '' });
const rejectSubmitting = ref(false);

let querySeq = 0;

function leaveTypeLabel(v) { const m = { 0: '事假', 1: '病假', 2: '其他' }; return m[v] ?? v; }
function leaveStatusLabel(v) { const m = { 0: '待确认', 1: '已通过', 2: '已驳回', 3: '已撤销' }; return m[v] ?? v; }
function leaveStatusType(v) { const m = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }; return m[v] ?? ''; }

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.studentId) params.studentId = queryParams.value.studentId;
  if (queryParams.value.status !== '') params.status = queryParams.value.status;
  if (queryParams.value.leaveType !== '') params.leaveType = queryParams.value.leaveType;
  listStudentLeave(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      const records = res.data?.records || res.rows || [];
      list.value = records;
      total.value = res.data?.total || res.total || 0;
      error.value = '';
      // 补充学生姓名
      enrichStudentNames(records);
    } else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

async function enrichStudentNames(records) {
  const studentIds = [...new Set(records.map(r => r.studentId).filter(Boolean))];
  if (studentIds.length === 0) return;
  try {
    const res = await listStudent({ page: 1, pageSize: 500 });
    if (res.success) {
      const studentMap = {};
      (res.data?.records || res.rows || []).forEach(s => { studentMap[s.id] = s.name; });
      records.forEach(r => { r.studentName = studentMap[r.studentId] || `学员${r.studentId}`; });
    }
  } catch { /* 静默处理 */ }
}

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

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, pageSize: 10, studentId: '', leaveType: '', status: '' }; getList(); }
function handleAdd() { open.value = true; }

function handleApprove(row) {
  ElMessageBox.confirm(`确认通过 ${row.studentName || ''} 的请假申请？通过后该学生当天签到自动标记为"请假"。`, '确认通过', { type: 'warning' })
    .then(async () => {
      try {
        const res = await approveStudentLeave(row.id, 0);
        if (res.success) { getList(); ElMessage.success('已通过'); }
        else { ElMessage.error(res.msg || '操作失败'); }
      } catch { ElMessage.error('网络异常，请稍后重试'); }
    })
    .catch(() => {});
}

function handleReject(row) {
  rejectForm.value = { id: row.id, reason: '' };
  rejectOpen.value = true;
}

function submitReject() {
  if (!rejectForm.value.reason.trim()) { ElMessage.warning('请填写驳回原因'); return; }
  rejectSubmitting.value = true;
  rejectStudentLeave(rejectForm.value.id, 0, rejectForm.value.reason)
    .then(res => {
      if (res.success) { rejectOpen.value = false; getList(); ElMessage.success('已驳回'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); })
    .finally(() => { rejectSubmitting.value = false; });
}

function handleDetail(row) { detailRow.value = row; detailOpen.value = true; }

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.error-box { margin: 20px 0; }
</style>
