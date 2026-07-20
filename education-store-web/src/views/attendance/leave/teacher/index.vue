<!-- 教师请假管理列表页 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="教师">
        <el-select v-model="queryParams.teacherId" filterable remote :remote-method="searchTeachers" :loading="teacherLoading" placeholder="搜索教师姓名" clearable style="width:180px">
          <el-option v-for="t in teacherOptions" :key="t.id" :label="t.name" :value="t.id" />
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
          <el-option label="待审批" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
          <el-option label="已处理" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['attendance:leave:teacher:add']">
          <el-icon><Plus /></el-icon>教师请假申请
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="编号" align="center" prop="id" width="70" />
      <el-table-column label="教师" align="center" prop="teacherName" min-width="100" />
      <el-table-column label="请假日期" align="center" width="160">
        <template #default="{ row }">
          {{ row.leaveStartDate }}{{ row.leaveEndDate && row.leaveEndDate !== row.leaveStartDate ? ' ~ ' + row.leaveEndDate : '' }}
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" width="80">
        <template #default="{ row }">{{ leaveTypeLabel(row.leaveType) }}</template>
      </el-table-column>
      <el-table-column label="原因" align="center" prop="reason" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="冲突课程" align="center" width="90">
        <template #default="{ row }">
          <span v-if="row.status === 1 || row.status === 3" :class="{ 'text-danger': (row.conflictCount || 0) > 0 }">
            {{ row.conflictCount ?? '-' }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <el-tag :type="leaveStatusType(row.status)" size="small">{{ leaveStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请人" align="center" width="90">
        <template #default="{ row }">{{ row.applicantType === 'ADMIN' ? '管理员' : '教师本人' }}</template>
      </el-table-column>
      <el-table-column label="审批时间" align="center" prop="approveTime" width="160" />
      <el-table-column label="操作" align="center" width="210" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button link type="success" size="small" @click="handleApprove(row)" v-permission="['attendance:leave:teacher:approve']">通过</el-button>
            <el-button link type="danger" size="small" @click="handleReject(row)" v-permission="['attendance:leave:teacher:approve']">驳回</el-button>
          </template>
          <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleConflict(row)" v-permission="['attendance:leave:teacher:resolve']">
            处理冲突
          </el-button>
          <el-button link type="primary" size="small" @click="handleDetail(row)" v-permission="['attendance:leave:teacher:query']">详情</el-button>
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

    <!-- 教师请假申请弹窗 -->
    <TeacherLeaveFormModal v-model:visible="open" title="教师请假申请" @submit-success="getList" />

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
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { listTeacherLeave, approveTeacherLeave, rejectTeacherLeave } from '@/api/attendance/leaveTeacher';
import { listTeacher as listTeacherApi } from '@/api/teaching/teachers';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import TeacherLeaveFormModal from './components/TeacherLeaveFormModal.vue';

const router = useRouter();

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const queryParams = ref({ page: 1, pageSize: 10, teacherId: '', leaveType: '', status: '' });
const teacherOptions = ref([]);
const teacherLoading = ref(false);

const open = ref(false);
const rejectOpen = ref(false);
const rejectForm = ref({ id: null, reason: '' });
const rejectSubmitting = ref(false);

let querySeq = 0;

function leaveTypeLabel(v) { const m = { 0: '事假', 1: '病假', 2: '其他' }; return m[v] ?? v; }
function leaveStatusLabel(v) { const m = { 0: '待审批', 1: '已通过', 2: '已驳回', 3: '已处理', 4: '已撤销' }; return m[v] ?? v; }
function leaveStatusType(v) { const m = { 0: 'warning', 1: 'success', 2: 'danger', 3: '', 4: 'info' }; return m[v] ?? ''; }

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.teacherId) params.teacherId = queryParams.value.teacherId;
  if (queryParams.value.status !== '') params.status = queryParams.value.status;
  if (queryParams.value.leaveType !== '') params.leaveType = queryParams.value.leaveType;
  listTeacherLeave(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      const records = res.data?.records || res.rows || [];
      list.value = records;
      total.value = res.data?.total || res.total || 0;
      error.value = '';
      enrichTeacherNames(records);
    } else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

async function enrichTeacherNames(records) {
  const teacherIds = [...new Set(records.map(r => r.teacherId).filter(Boolean))];
  if (teacherIds.length === 0) return;
  try {
    const res = await listTeacherApi({ page: 1, pageSize: 500 });
    if (res.success) {
      const map = {};
      (res.data?.records || res.rows || []).forEach(t => { map[t.id] = t.name; });
      records.forEach(r => { r.teacherName = map[r.teacherId] || `教师${r.teacherId}`; });
    }
  } catch { /* 静默处理 */ }
}

let sTimer = null; let sSeq = 0;
function searchTeachers(keyword) {
  clearTimeout(sTimer);
  if (!keyword) { teacherOptions.value = []; return; }
  teacherLoading.value = true;
  sTimer = setTimeout(() => { sSeq++; const seq = sSeq;
    listTeacherApi({ page: 1, pageSize: 50, keyword }).then(res => {
      if (seq !== sSeq) return;
      if (res.success) teacherOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === sSeq) teacherLoading.value = false; });
  }, 300);
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, pageSize: 10, teacherId: '', leaveType: '', status: '' }; getList(); }
function handleAdd() { open.value = true; }

function handleApprove(row) {
  ElMessageBox.confirm(`确认通过 ${row.teacherName || ''} 的请假申请？通过后将自动检测排课冲突。`, '确认通过', { type: 'warning' })
    .then(async () => {
      try {
        const res = await approveTeacherLeave(row.id, 0);
        if (res.success) { getList(); ElMessage.success('已通过，系统已自动检测排课冲突'); }
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
  rejectTeacherLeave(rejectForm.value.id, 0, rejectForm.value.reason)
    .then(res => {
      if (res.success) { rejectOpen.value = false; getList(); ElMessage.success('已驳回'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); })
    .finally(() => { rejectSubmitting.value = false; });
}

function handleDetail(row) { router.push(`/attendance/leave/teacher/${row.id}`); }
function handleConflict(row) { router.push(`/attendance/leave/teacher/${row.id}`); }

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.text-danger { color: #f56c6c; font-weight: 600; }
.error-box { margin: 20px 0; }
</style>
