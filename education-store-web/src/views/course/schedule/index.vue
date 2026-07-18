<!-- 课程表管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="班级" prop="classId">
        <el-select v-model="queryParams.classId" placeholder="请选择班级" clearable style="width:200px" @change="handleQuery">
          <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="date">
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
      <el-table-column label="时间" align="center" width="120">
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
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.schedule_status" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="消课" align="center" width="70">
        <template #default="{ row }">
          <el-tag :type="row.consumed === 1 ? 'success' : 'info'" size="small">{{ row.consumed === 1 ? '已消' : '未消' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="warning" size="small" @click="handleCancel(row)" v-if="row.status === 'NORMAL'">停课</el-button>
          <el-button link type="primary" size="small" @click="handleReschedule(row)" v-if="row.status === 'NORMAL'">调课</el-button>
          <el-button link type="success" size="small" @click="handleConsume(row)" v-if="row.consumed !== 1 && row.status === 'NORMAL'">消课</el-button>
        </template>
      </el-table-column>
    </el-table>

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
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { listSchedule, addSchedule, cancelSchedule, rescheduleSchedule, consumeSchedule } from '@/api/schedule/schedules';
import { listClass } from '@/api/class/classes';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';

const { dicts, loadDicts } = useDictsStore();
const loading = ref(false); const showSearch = ref(true);
const list = ref([]); const total = ref(0);
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
let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.classId) delete params.classId; if (!params.date) delete params.date; if (!params.status) delete params.status;
  listSchedule(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; }
  }).finally(() => { if (seq === querySeq) loading.value = false; });
}
function loadClasses() { listClass({ page: 1, size: 999 }).then(res => { if (res.success) classOptions.value = res.data?.records || res.rows || []; }); }
function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, classId: '', date: '', status: '' }; getList(); }
function handleAdd() { open.value = true; }
function onClassChange() { form.value.teacherId = null; form.value.roomId = null; }

function handleCancel(row) {
  ElMessageBox.prompt('请输入停课原因', '停课', { type: 'warning' }).then(({ value }) =>
    cancelSchedule(row.id, { reason: value })
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
  ElMessageBox.confirm('确认消课？消课后将扣减学员课时。', '警告', { type: 'warning' })
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
</style>
