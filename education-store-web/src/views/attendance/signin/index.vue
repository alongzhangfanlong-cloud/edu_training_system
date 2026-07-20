<!-- 签到管理列表页 — 按排课展示签到状态 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="日期">
        <el-date-picker
          v-model="dateRange" type="daterange" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期"
          value-format="YYYY-MM-DD" style="width:240px" @change="handleQuery"
        />
      </el-form-item>
      <el-form-item label="班级">
        <el-select v-model="queryParams.classId" placeholder="请选择班级" clearable style="width:180px" @change="handleQuery">
          <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="排课状态" clearable style="width:130px" @change="handleQuery">
          <el-option v-for="d in dicts.schedule_status" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column label="编号" align="center" prop="id" width="70" />
      <el-table-column label="日期" align="center" prop="scheduleDate" width="110" sortable />
      <el-table-column label="时间段" align="center" width="140">
        <template #default="{ row }">{{ row.startTime }} - {{ row.endTime }}</template>
      </el-table-column>
      <el-table-column label="课程" align="center" prop="courseName" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="班级" align="center" prop="className" :show-overflow-tooltip="true" min-width="110" />
      <el-table-column label="教师" align="center" prop="teacherName" width="80" />
      <el-table-column label="类型" align="center" width="80">
        <template #default="{ row }">
          <dict-tag :options="dicts.schedule_type" :value="row.scheduleType" />
        </template>
      </el-table-column>
      <el-table-column label="应到" align="center" width="60">
        <template #default="{ row }">{{ row.totalCount ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="实到" align="center" width="60">
        <template #default="{ row }">
          <span :class="{ 'text-success': (row.checkInCount ?? 0) > 0 }">{{ row.checkInCount ?? '-' }}</span>
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
      <el-table-column label="签到状态" align="center" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'CANCELLED'" type="danger" size="small">已取消</el-tag>
          <el-tag v-else-if="row.status === 'SUSPENDED'" type="warning" size="small">已停课</el-tag>
          <el-tag v-else-if="row.checkInCount > 0" type="success" size="small">已签到</el-tag>
          <el-tag v-else type="info" size="small">未签到</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleDetail(row)" v-permission="['attendance:signin:list']">
            签到详情
          </el-button>
          <el-button
            v-if="isToday(row.scheduleDate) && row.status === 'NORMAL'"
            link type="success" size="small"
            @click="handleBatchSignin(row)" v-permission="['attendance:signin:add']"
          >
            批量签到
          </el-button>
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

    <BatchSigninModal
      v-model:visible="signinOpen" :schedule-id="currentScheduleId"
      :class-id="currentClassId" :schedule-info="currentScheduleInfo"
      @submit-success="getList"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listSchedule } from '@/api/schedule/schedules';
import { listClass } from '@/api/class/classes';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';
import BatchSigninModal from './components/BatchSigninModal.vue';

const router = useRouter();
const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);
const classOptions = ref([]);

const queryParams = ref({ page: 1, size: 10 });
const dateRange = ref([]);

const signinOpen = ref(false);
const currentScheduleId = ref(null);
const currentClassId = ref(null);
const currentScheduleInfo = ref({});

let querySeq = 0;

function isToday(dateStr) {
  if (!dateStr) return false;
  const today = new Date().toISOString().slice(0, 10);
  return dateStr === today;
}

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.size };
  if (queryParams.value.classId) params.classId = queryParams.value.classId;
  if (queryParams.value.status) params.status = queryParams.value.status;
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = dateRange.value[0];
    params.endDate = dateRange.value[1];
  }
  listSchedule(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      list.value = res.data?.records || res.rows || [];
      total.value = res.data?.total || res.total || 0;
      error.value = '';
    } else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

function fetchClassOptions() {
  listClass({ page: 1, pageSize: 200 }).then(res => {
    if (res.success) classOptions.value = res.data?.records || res.rows || [];
  });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { dateRange.value = []; queryParams.value = { page: 1, size: 10 }; getList(); }
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

function handleDetail(row) {
  router.push({ path: `/attendance/signin/${row.id}`, query: { scheduleDate: row.scheduleDate, className: row.className, courseName: row.courseName, teacherName: row.teacherName, startTime: row.startTime, endTime: row.endTime } });
}

function handleBatchSignin(row) {
  currentScheduleId.value = row.id;
  currentClassId.value = row.classId;
  currentScheduleInfo.value = {
    scheduleDate: row.scheduleDate,
    startTime: row.startTime, endTime: row.endTime,
    courseName: row.courseName, className: row.className
  };
  signinOpen.value = true;
}

onMounted(() => {
  getList();
  fetchClassOptions();
  loadDict('schedule_type');
  loadDict('schedule_status');
});
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.text-success { color: #67c23a; font-weight: 600; }
.error-box { margin: 20px 0; }
</style>
