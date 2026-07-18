<!-- 课程表管理（含多视图切换 + 周期导航） -->
<template>
  <div class="app-container">
    <!-- 视图切换 + 周期导航 -->
    <div class="view-toolbar mb8">
      <el-radio-group v-model="viewMode" size="small" @change="onViewModeChange">
        <el-radio-button value="list">列表</el-radio-button>
        <el-radio-button value="week">周</el-radio-button>
        <el-radio-button value="month">月</el-radio-button>
        <el-radio-button value="day">日</el-radio-button>
      </el-radio-group>
      <div class="period-nav" v-if="viewMode !== 'list'">
        <el-button size="small" :icon="ArrowLeft" @click="prevPeriod">上一{{ periodLabel }}</el-button>
        <span class="period-label">{{ periodDisplay }}</span>
        <el-button size="small" :icon="ArrowRight" @click="nextPeriod">下一{{ periodLabel }}</el-button>
        <el-button size="small" @click="goToday">今天</el-button>
      </div>
    </div>

    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="班级" prop="classId">
        <el-select v-model="queryParams.classId" placeholder="请选择班级" clearable style="width:200px" @change="handleQuery">
          <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="date" v-if="viewMode === 'list'">
        <el-date-picker v-model="queryParams.date" type="date" placeholder="选择日期" clearable style="width:180px" value-format="YYYY-MM-DD" @change="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="课表状态" clearable style="width:150px" @change="handleQuery">
          <el-option v-for="d in dicts.schedule_status" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['schedule:plan:add']">
          <el-icon><Plus /></el-icon>新增排课
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="上课日期" align="center" prop="scheduleDate" width="120" />
      <el-table-column label="时间" align="center" width="130">
        <template #default="{ row }">{{ row.startTime }} - {{ row.endTime }}</template>
      </el-table-column>
      <el-table-column label="班级" align="center" prop="className" min-width="120" />
      <el-table-column label="课程" align="center" prop="courseName" width="100" />
      <el-table-column label="教师" align="center" prop="teacherName" width="80" />
      <el-table-column label="教室" align="center" prop="roomName" width="100" />
      <el-table-column label="类型" align="center" width="80">
        <template #default="{ row }">
          <dict-tag :options="dicts.schedule_type" :value="row.scheduleType" />
        </template>
      </el-table-column>
      <el-table-column label="签到" align="center" width="70">
        <template #default="{ row }">
          <span>{{ row.checkInCount ?? '-' }}/{{ row.totalCount ?? '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="到课率" align="center" width="70">
        <template #default="{ row }">
          <span v-if="row.attendanceRate !== undefined && row.attendanceRate !== null">
            {{ (row.attendanceRate * 100).toFixed(0) }}%
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="上课情况" align="center" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'NORMAL'" type="success" size="small">待上</el-tag>
          <el-tag v-else-if="row.status === 'SUSPENDED'" type="warning" size="small">已停</el-tag>
          <el-tag v-else-if="row.consumed === 1" type="info" size="small">已上</el-tag>
          <el-tag v-else type="info" size="small">{{ row.status || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="info" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button link type="warning" size="small" @click="handleCancel(row)"
            v-if="row.status === 'NORMAL' || row.status === 'MAKEUP'">停课</el-button>
          <el-button link type="primary" size="small" @click="handleReschedule(row)"
            v-if="row.status === 'NORMAL'">调课</el-button>
          <el-button link type="success" size="small" @click="handleConsume(row)"
            v-if="row.consumed !== 1 && (row.status === 'NORMAL' || row.status === 'MAKEUP')">消课</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无排课数据" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <!-- 新增排课弹窗 -->
    <el-dialog :model-value="open" title="新增排课" width="550px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => open = val">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width:100%" @change="onClassChange">
            <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上课日期" prop="scheduleDate">
              <el-date-picker v-model="form.scheduleDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型">
              <el-select v-model="form.scheduleType" style="width:100%">
                <el-option v-for="d in dicts.schedule_type" :key="d.value" :label="d.label" :value="d.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker v-model="form.startTime" format="HH:mm" value-format="HH:mm" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker v-model="form.endTime" format="HH:mm" value-format="HH:mm" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog :model-value="detailOpen" title="排课详情" width="500px" destroy-on-close append-to-body @update:model-value="(val) => detailOpen = val">
      <el-descriptions v-if="currentDetail" :column="2" border size="small">
        <el-descriptions-item label="班级">{{ currentDetail.className || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ currentDetail.courseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="教师">{{ currentDetail.teacherName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="教室">{{ currentDetail.roomName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ currentDetail.scheduleDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ currentDetail.startTime }} - {{ currentDetail.endTime }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentDetail.scheduleType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentDetail.status || '-' }}</el-descriptions-item>
        <el-descriptions-item label="消课">{{ currentDetail.consumed === 1 ? '已消课' : '未消课' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentDetail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 调课弹窗 -->
    <el-dialog :model-value="rescheduleOpen" title="调课" width="450px" destroy-on-close append-to-body @update:model-value="(val) => rescheduleOpen = val">
      <el-form ref="rescheduleFormRef" :model="rescheduleForm" label-width="80px">
        <el-form-item label="新日期">
          <el-date-picker v-model="rescheduleForm.newDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="新时间">
          <el-time-picker v-model="rescheduleForm.newStartTime" format="HH:mm" value-format="HH:mm" style="width:100%" />
        </el-form-item>
        <el-form-item label="调课原因">
          <el-input v-model="rescheduleForm.reason" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rescheduleOpen = false">取消</el-button>
        <el-button type="primary" @click="handleRescheduleSubmit" :loading="rescheduleSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import { listSchedule, addSchedule, cancelSchedule, rescheduleSchedule, consumeSchedule } from '@/api/schedule/schedules';
import { listClass } from '@/api/class/classes';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';

const { dicts, loadDicts } = useDictsStore();
const loading = ref(false); const showSearch = ref(true);
const list = ref([]); const total = ref(0); const error = ref('');
const classOptions = ref([]);
const queryParams = ref({ page: 1, size: 10, classId: '', date: '', status: '' });
const open = ref(false); const formRef = ref(null); const submitting = ref(false);
const form = ref({ classId: null, scheduleDate: '', scheduleType: 'NORMAL', startTime: '', endTime: '', remark: '' });
const rules = {
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择上课日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
};
const rescheduleOpen = ref(false); const currentScheduleId = ref(null);
const rescheduleFormRef = ref(null); const rescheduleSubmitting = ref(false);
const rescheduleForm = ref({ newDate: '', newStartTime: '', reason: '' });
const detailOpen = ref(false); const currentDetail = ref(null);

// 视图模式
const viewMode = ref('list');
const periodDate = ref(new Date());
const periodLabel = computed(() => {
  const map = { week: '周', month: '月', day: '日' };
  return map[viewMode.value] || '';
});
const periodDisplay = computed(() => {
  const d = periodDate.value;
  if (viewMode.value === 'day') return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  if (viewMode.value === 'week') {
    const day = d.getDay() || 7;
    const monday = new Date(d); monday.setDate(d.getDate() - day + 1);
    const sunday = new Date(monday); sunday.setDate(monday.getDate() + 6);
    return `${monday.getMonth() + 1}/${monday.getDate()} - ${sunday.getMonth() + 1}/${sunday.getDate()}`;
  }
  return `${d.getFullYear()}年${d.getMonth() + 1}月`;
});

let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.classId) delete params.classId;
  if (!params.date) delete params.date;
  if (!params.status) delete params.status;
  if (viewMode.value !== 'list') {
    if (viewMode.value === 'day') { params.date = toDateStr(periodDate.value); }
    else if (viewMode.value === 'week') {
      const day = periodDate.value.getDay() || 7;
      const monday = new Date(periodDate.value); monday.setDate(periodDate.value.getDate() - day + 1);
      const sunday = new Date(monday); sunday.setDate(monday.getDate() + 6);
      params.startDate = toDateStr(monday);
      params.endDate = toDateStr(sunday);
    } else if (viewMode.value === 'month') {
      params.startDate = toDateStr(new Date(periodDate.value.getFullYear(), periodDate.value.getMonth(), 1));
      params.endDate = toDateStr(new Date(periodDate.value.getFullYear(), periodDate.value.getMonth() + 1, 0));
    }
  }
  listSchedule(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

function toDateStr(d) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

function loadClasses() { listClass({ page: 1, size: 999 }).then(res => { if (res.success) classOptions.value = res.data?.records || res.rows || []; }); }
function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, classId: '', date: '', status: '' }; getList(); }
function handleAdd() { open.value = true; }
function onClassChange() { form.value.teacherId = null; form.value.roomId = null; }

function onViewModeChange() { queryParams.value.page = 1; getList(); }
function prevPeriod() {
  const d = new Date(periodDate.value);
  if (viewMode.value === 'day') d.setDate(d.getDate() - 1);
  else if (viewMode.value === 'week') d.setDate(d.getDate() - 7);
  else if (viewMode.value === 'month') d.setMonth(d.getMonth() - 1);
  periodDate.value = d; getList();
}
function nextPeriod() {
  const d = new Date(periodDate.value);
  if (viewMode.value === 'day') d.setDate(d.getDate() + 1);
  else if (viewMode.value === 'week') d.setDate(d.getDate() + 7);
  else if (viewMode.value === 'month') d.setMonth(d.getMonth() + 1);
  periodDate.value = d; getList();
}
function goToday() { periodDate.value = new Date(); getList(); }

function handleDetail(row) { currentDetail.value = row; detailOpen.value = true; }

function handleCancel(row) {
  ElMessageBox.prompt('请输入停课原因', '停课', { type: 'warning' }).then(({ value }) =>
    cancelSchedule(row.id, { cancelReason: value })
  ).then(res => {
    if (res.success) { getList(); ElMessage.success('停课成功'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  });
}

function handleReschedule(row) {
  currentScheduleId.value = row.id;
  rescheduleForm.value = { newDate: row.scheduleDate, newStartTime: row.startTime, reason: '' };
  rescheduleOpen.value = true;
}
async function handleRescheduleSubmit() {
  rescheduleSubmitting.value = true;
  try {
    const data = {
      newDate: rescheduleForm.value.newDate,
      newStartTime: rescheduleForm.value.newStartTime,
      reason: rescheduleForm.value.reason,
    };
    const res = await rescheduleSchedule(currentScheduleId.value, data);
    if (res.success) { ElMessage.success('调课成功'); rescheduleOpen.value = false; getList(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { rescheduleSubmitting.value = false; }
}

function handleConsume(row) {
  ElMessageBox.confirm('确认将该排课标记为已上？消课操作不可撤销。', '警告', { type: 'warning' })
    .then(() => consumeSchedule(row.id))
    .then(res => {
      if (res.success) { getList(); ElMessage.success('消课成功'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    });
}

async function onOpen() { reset(); loadClasses(); await loadDicts(['schedule_type', 'schedule_status']); }
function reset() { form.value = { classId: null, scheduleDate: '', scheduleType: 'NORMAL', startTime: '', endTime: '', remark: '' }; nextTick(() => formRef.value?.clearValidate()); }
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const res = await addSchedule(form.value);
    if (res.success) { ElMessage.success('新增成功'); open.value = false; getList(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
onMounted(() => { getList(); loadDicts(['schedule_type', 'schedule_status']); loadClasses(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.view-toolbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 0; border-bottom: 1px solid #ebeef5; margin-bottom: 12px;
}
.period-nav {
  display: flex; align-items: center; gap: 8px;
  .period-label { font-weight: 600; font-size: 14px; min-width: 100px; text-align: center; }
}
</style>
