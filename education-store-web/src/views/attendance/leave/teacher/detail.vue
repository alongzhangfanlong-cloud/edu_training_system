<!-- 教师请假详情 + 冲突处理页 -->
<template>
  <div class="app-container">
    <el-page-header @back="goBack">
      <template #content>
        <span class="page-title">教师请假详情</span>
      </template>
    </el-page-header>

    <el-row :gutter="16">
      <!-- 请假信息卡片 -->
      <el-col :span="24">
        <el-card class="info-card" shadow="never" v-loading="leaveLoading">
          <template #header>请假信息</template>
          <el-descriptions v-if="leave" :column="3" border size="small">
            <el-descriptions-item label="教师">{{ leave.teacherName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="请假日期">
              {{ leave.leaveStartDate }}{{ leave.leaveEndDate && leave.leaveEndDate !== leave.leaveStartDate ? ' ~ ' + leave.leaveEndDate : '' }}
            </el-descriptions-item>
            <el-descriptions-item label="类型">{{ leaveTypeLabel(leave.leaveType) }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="leaveStatusType(leave.status)" size="small">{{ leaveStatusLabel(leave.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请人">{{ leave.applicantType === 'ADMIN' ? '管理员' : '教师本人' }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ leave.createTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="原因" :span="3">{{ leave.reason || '-' }}</el-descriptions-item>
            <el-descriptions-item v-if="leave.status === 2" label="驳回原因" :span="3">
              <span class="text-danger">{{ leave.rejectReason || '-' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="审批人">{{ leave.approverName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="审批时间">{{ leave.approveTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="自动通过">
              {{ leave.autoApproved ? '是（超时自动通过）' : '否' }}
            </el-descriptions-item>
          </el-descriptions>
          <el-empty v-else-if="!leaveLoading" description="加载请假信息失败" />
        </el-card>
      </el-col>

      <!-- 冲突排课列表 -->
      <el-col :span="24" v-if="leave && (leave.status === 1 || leave.status === 3)">
        <el-card class="conflict-card" shadow="never">
          <template #header>
            <span>冲突排课处理</span>
            <el-tag v-if="unresolvedCount > 0" type="warning" size="small" style="margin-left:8px">
              待处理 {{ unresolvedCount }} / {{ conflicts.length }}
            </el-tag>
            <el-tag v-else-if="conflicts.length > 0" type="success" size="small" style="margin-left:8px">
              全部已处理
            </el-tag>
          </template>

          <div v-if="conflictError" class="error-box">
            <el-alert :title="conflictError" type="error" show-icon @close="conflictError = ''" />
          </div>

          <el-table v-loading="conflictLoading" :data="conflicts" border stripe>
            <el-table-column label="序号" align="center" width="50" type="index" />
            <el-table-column label="日期" align="center" width="110">
              <template #default="{ row }">{{ row.scheduleDate || '-' }}</template>
            </el-table-column>
            <el-table-column label="时间" align="center" width="130">
              <template #default="{ row }">{{ row.scheduleTime || '-' }}</template>
            </el-table-column>
            <el-table-column label="课程" align="center" prop="courseName" min-width="100" />
            <el-table-column label="班级" align="center" prop="className" min-width="110" />
            <el-table-column label="学生数" align="center" prop="studentCount" width="70" />
            <el-table-column label="处理方式" align="center" width="280">
              <template #default="{ row }">
                <template v-if="row.status === 1">
                  <el-tag :type="resolveTypeTag(row.resolveType)" size="small">
                    {{ resolveTypeLabel(row.resolveType) }}
                  </el-tag>
                  <span v-if="row.substituteTeacherName" style="margin-left:4px;font-size:12px;color:#909399">
                    ({{ row.substituteTeacherName }})
                  </span>
                </template>
                <template v-else>
                  <el-select v-model="row._resolveType" size="small" style="width:90px" @change="onResolveTypeChange(row)">
                    <el-option label="代课" :value="0" />
                    <el-option label="调课" :value="1" />
                    <el-option label="停课" :value="2" />
                  </el-select>
                  <!-- 代课：选择代课教师 -->
                  <template v-if="row._resolveType === 0">
                    <el-select v-model="row._substituteTeacherId" size="small" filterable remote :remote-method="(kw) => searchSubstitute(kw, row)" :loading="row._teacherSearching" placeholder="选择教师" style="width:120px;margin-left:4px">
                      <el-option v-for="t in (row._teacherOptions || [])" :key="t.id" :label="t.name" :value="t.id" />
                    </el-select>
                  </template>
                  <!-- 调课：新日期 + 新时段 -->
                  <template v-if="row._resolveType === 1">
                    <el-date-picker v-model="row._newDate" size="small" type="date" placeholder="新日期" value-format="YYYY-MM-DD" style="width:130px;margin-left:4px" />
                    <el-input v-model="row._newTimeSlot" size="small" placeholder="如 16:00-18:00" style="width:130px;margin-left:4px" />
                  </template>
                </template>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="100">
              <template #default="{ row }">
                <el-button v-if="row.status === 0" link type="primary" size="small" :loading="row._submitting" @click="handleResolve(row)">
                  确认处理
                </el-button>
                <span v-else class="text-muted">已处理</span>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="!conflictLoading && conflicts.length === 0" description="暂无冲突排课" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getTeacherLeave } from '@/api/attendance/leaveTeacher';
import { listConflictsByLeave, resolveConflict } from '@/api/attendance/leaveConflict';
import { listTeacher as listTeacherApi } from '@/api/teaching/teachers';

const route = useRoute();
const router = useRouter();
const leaveId = ref(route.params.id);

const leave = ref(null);
const leaveLoading = ref(false);
const conflicts = ref([]);
const conflictLoading = ref(false);
const conflictError = ref('');

const unresolvedCount = computed(() => conflicts.value.filter(c => c.status === 0).length);

function leaveTypeLabel(v) { const m = { 0: '事假', 1: '病假', 2: '其他' }; return m[v] ?? v; }
function leaveStatusLabel(v) { const m = { 0: '待审批', 1: '已通过', 2: '已驳回', 3: '已处理', 4: '已撤销' }; return m[v] ?? v; }
function leaveStatusType(v) { const m = { 0: 'warning', 1: 'success', 2: 'danger', 3: '', 4: 'info' }; return m[v] ?? ''; }
function resolveTypeLabel(v) { const m = { 0: '代课', 1: '调课', 2: '停课' }; return m[v] ?? v; }
function resolveTypeTag(v) { const m = { 0: 'success', 1: 'warning', 2: 'danger' }; return m[v] ?? ''; }

function goBack() { router.push('/attendance/leave/teacher'); }

async function fetchLeave() {
  leaveLoading.value = true;
  try {
    const res = await getTeacherLeave(leaveId.value);
    if (res.success) leave.value = res.data?.data || res.data;
  } catch { /* 静默 */ }
  finally { leaveLoading.value = false; }
}

async function fetchConflicts() {
  conflictLoading.value = true;
  conflictError.value = '';
  try {
    const res = await listConflictsByLeave(leaveId.value);
    if (res.success) {
      const raw = res.data?.data || res.data || [];
      conflicts.value = (Array.isArray(raw) ? raw : raw.records || []).map(c => ({
        ...c,
        _resolveType: c.resolveType ?? 0,
        _substituteTeacherId: c.substituteTeacherId || null,
        _newDate: c.newScheduleDate || '',
        _newTimeSlot: c.newTimeSlot || '',
        _teacherOptions: [],
        _teacherSearching: false,
        _searchTimer: null,
        _submitting: false
      }));
    } else {
      conflictError.value = res.msg || '加载冲突列表失败';
    }
  } catch {
    conflictError.value = '网络错误，请检查网络后重试';
  } finally {
    conflictLoading.value = false;
  }
}

function onResolveTypeChange(row) {
  row._substituteTeacherId = null;
  row._newDate = '';
  row._newTimeSlot = '';
}

function searchSubstitute(keyword, row) {
  clearTimeout(row._searchTimer);
  if (!keyword) { row._teacherOptions = []; return; }
  row._teacherSearching = true;
  row._searchTimer = setTimeout(() => {
    listTeacherApi({ page: 1, pageSize: 50, keyword }).then(res => {
      if (res.success) row._teacherOptions = res.data?.records || res.rows || [];
    }).finally(() => { row._teacherSearching = false; });
  }, 300);
}

async function handleResolve(row) {
  // 验证
  if (row._resolveType === 0 && !row._substituteTeacherId) {
    ElMessage.warning('请选择代课教师'); return;
  }
  if (row._resolveType === 1) {
    if (!row._newDate) { ElMessage.warning('请选择新日期'); return; }
    if (!row._newTimeSlot) { ElMessage.warning('请填写新时段'); return; }
  }

  row._submitting = true;
  try {
    const data = { resolveType: row._resolveType };
    if (row._resolveType === 0) data.substituteTeacherId = row._substituteTeacherId;
    if (row._resolveType === 1) {
      data.newScheduleDate = row._newDate;
      data.newTimeSlot = row._newTimeSlot;
    }

    const res = await resolveConflict(row.id, data);
    if (res.success) {
      ElMessage.success('冲突已处理');
      fetchConflicts();
      // 检查是否全部处理完毕
      fetchLeave();
    } else {
      ElMessage.error(res.msg || '处理失败');
    }
  } catch {
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    row._submitting = false;
  }
}

onMounted(() => {
  fetchLeave();
  fetchConflicts();
});
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.page-title { font-size: 16px; font-weight: 600; }
.info-card { margin-top: 16px; }
.conflict-card { margin-top: 16px; }
.text-danger { color: #f56c6c; }
.text-muted { color: #c0c4cc; }
.error-box { margin-bottom: 12px; }
</style>
