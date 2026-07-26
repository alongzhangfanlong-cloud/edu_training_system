<!-- 排课计划管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="班级" prop="classId">
        <el-select v-model="queryParams.classId" placeholder="请选择班级" clearable style="width:200px">
          <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
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
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['schedule:plan:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="班级" align="center" prop="className" min-width="120" />
      <el-table-column label="课程" align="center" prop="courseName" width="100" />
      <el-table-column label="教师" align="center" prop="teacherName" width="80" />
      <el-table-column label="教室" align="center" prop="roomName" width="100" />
      <el-table-column label="开始日期" align="center" prop="startDate" width="110" />
      <el-table-column label="结束日期" align="center" prop="endDate" width="110" />
      <el-table-column label="计划课次" align="center" prop="plannedSessions" width="90" />
      <el-table-column label="冲突" align="center" width="80">
        <template #default="{ row }">
          <el-tooltip v-if="row.conflictDetail" :content="row.conflictDetail" placement="top">
            <el-tag type="danger" size="small">⚠ 冲突</el-tag>
          </el-tooltip>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.schedule_plan_status" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
          <el-button v-if="row.status === 'UNGENERATED'" link type="info" size="small" @click="handlePreview(row)">预览</el-button>
          <el-button v-if="row.status === 'UNGENERATED'" link type="success" size="small" @click="handleGenerate(row)" v-permission="['schedule:plan:edit']">生成课表</el-button>
          <el-button v-if="row.status === 'GENERATED'" link type="warning" size="small" @click="handleRegenerate(row)" v-permission="['schedule:plan:edit']">重新生成</el-button>
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['schedule:plan:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['schedule:plan:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无排课计划数据" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <!-- 预览弹窗：排课明细表格 + 冲突高亮 + 操作分支 -->
    <el-dialog :model-value="previewOpen" title="排课预览" width="900px" destroy-on-close append-to-body
      @open="loadPreview" @update:model-value="(val) => previewOpen = val">
      <div v-if="previewLoading" v-loading="previewLoading" style="min-height:200px" />
      <template v-else>
        <el-alert v-if="previewData.conflictCount > 0" :title="`检测到 ${previewData.conflictCount} 个冲突`"
          type="warning" :closable="false" show-icon style="margin-bottom:12px" />
        <el-alert v-else title="未检测到冲突，可以安全生成" type="success" :closable="false" show-icon style="margin-bottom:12px" />
        <el-table :data="previewData.slots || []" border stripe max-height="400">
          <el-table-column label="班级" prop="className" width="100" />
          <el-table-column label="日期" prop="scheduleDate" width="110" />
          <el-table-column label="时间" width="120">
            <template #default="{ row }">{{ row.startTime }} - {{ row.endTime }}</template>
          </el-table-column>
          <el-table-column label="教师" prop="teacherName" width="80" />
          <el-table-column label="教室" prop="roomName" width="80" />
          <el-table-column label="冲突" width="180">
            <template #default="{ row }">
              <el-tag v-if="row.conflict" type="danger" size="small">{{ row.conflictDetail }}</el-tag>
              <el-tag v-else type="success" size="small">无冲突</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top:12px; color:#909399; font-size:13px">
          共 {{ previewData.totalCount || 0 }} 个排课槽位，{{ previewData.conflictCount || 0 }} 个冲突
        </div>
      </template>
      <template #footer>
        <el-button @click="previewOpen = false">取消</el-button>
        <el-button v-if="previewData.conflictCount > 0" type="warning" @click="handleGenerateWithSkip">
          跳过冲突并生成
        </el-button>
        <el-button type="primary" @click="handleGenerateAfterPreview" :disabled="previewData.conflictCount > 0">
          确认生成
        </el-button>
      </template>
    </el-dialog>

    <el-dialog :model-value="open" :title="title" width="660px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => open = val">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width:100%" :disabled="!!currentId">
            <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="教室">
          <el-select v-model="form.roomId" placeholder="请选择教室（可选，默认使用班级教室）" style="width:100%" clearable>
            <el-option v-for="r in roomOptions" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <!-- 上课规则 -->
        <el-divider content-position="left">上课规则</el-divider>
        <div class="rule-list">
          <div v-for="(rule, index) in form.rules" :key="index" class="rule-item">
            <div class="rule-item-head">
              <span class="rule-item-num">规则 {{ index + 1 }}</span>
              <el-button type="danger" link size="small" @click="removeRule(index)">删除</el-button>
            </div>
            <el-row :gutter="10">
              <el-col :span="10">
                <el-select v-model="rule.ruleType" size="small" style="width:100%">
                  <el-option label="每周" value="WEEKLY" />
                  <el-option label="指定日期" value="SPECIFIC" />
                </el-select>
              </el-col>
              <el-col :span="14">
                <el-select v-if="rule.ruleType === 'WEEKLY'" v-model="rule.weekDay" size="small" placeholder="选择星期" style="width:100%">
                  <el-option v-for="d in weekDayOptions" :key="d.value" :label="d.label" :value="d.value" />
                </el-select>
                <el-date-picker v-else v-model="rule.scheduleDate" type="date" size="small" style="width:100%" value-format="YYYY-MM-DD" placeholder="选择日期" />
              </el-col>
            </el-row>
            <el-row :gutter="10" style="margin-top: 6px">
              <el-col :span="10">
                <el-time-picker v-model="rule.startTime" size="small" style="width:100%" format="HH:mm" value-format="HH:mm" placeholder="开始时间" />
              </el-col>
              <el-col :span="4" class="rule-time-sep">至</el-col>
              <el-col :span="10">
                <el-time-picker v-model="rule.endTime" size="small" style="width:100%" format="HH:mm" value-format="HH:mm" placeholder="结束时间" />
              </el-col>
            </el-row>
            <div v-if="ruleDuration(rule)" class="rule-duration">{{ ruleDuration(rule) }}</div>
          </div>
          <div v-if="form.rules.length === 0" class="rule-empty">暂未添加上课规则</div>
        </div>
        <el-button class="add-rule-btn" size="small" @click="addRule"><el-icon><Plus /></el-icon>添加规则</el-button>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue';
import { listSchedulePlan, getSchedulePlan, addSchedulePlan, updateSchedulePlan, delSchedulePlan, previewSchedulePlan, regenerateSchedulePlan, generateSchedulePlan, getPlanRules, savePlanRules } from '@/api/schedule/schedulePlans';
import { listClass } from '@/api/class/classes';
import { listClassroom } from '@/api/course/classrooms';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';

const { dicts, loadDict } = useDictsStore();
const loading = ref(false); const showSearch = ref(true);
const list = ref([]); const total = ref(0); const error = ref('');
const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const classOptions = ref([]);
const roomOptions = ref([]);
const queryParams = ref({ page: 1, size: 10, classId: '' });
const open = ref(false); const title = ref(''); const currentId = ref(null);
const formRef = ref(null); const submitting = ref(false);
const form = ref({ classId: null, startDate: '', endDate: '', roomId: null, rules: [], remark: '' });
const weekDayOptions = [
  { value: 1, label: '周一' }, { value: 2, label: '周二' }, { value: 3, label: '周三' },
  { value: 4, label: '周四' }, { value: 5, label: '周五' }, { value: 6, label: '周六' }, { value: 7, label: '周日' },
];
const formRules = {
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
};
let querySeq = 0;

// ===== 规则管理 =====
function addRule() {
  form.value.rules.push({ ruleType: 'WEEKLY', weekDay: null, scheduleDate: '', startTime: '', endTime: '' });
}
function removeRule(index) { form.value.rules.splice(index, 1); }

function ruleDuration(rule) {
  const mins = calcDuration(rule.startTime, rule.endTime);
  if (!mins || mins <= 0) return '';
  if (mins < 60) return `${mins}分钟`;
  const h = Math.floor(mins / 60); const m = mins % 60;
  return m > 0 ? `${h}小时${m}分钟` : `${h}小时`;
}
function calcDuration(startTime, endTime) {
  if (!startTime || !endTime) return null;
  const [sh, sm] = startTime.split(':').map(Number);
  const [eh, em] = endTime.split(':').map(Number);
  const mins = (eh * 60 + em) - (sh * 60 + sm);
  return mins > 0 ? mins : null;
}

async function onOpen() {
  reset(); loadClasses(); loadRooms(); await loadDict('schedule_plan_status');
  if (currentId.value) {
    const res = await getSchedulePlan(currentId.value);
    if (res.success && res.data) {
      const p = res.data.data || res.data;
      form.value = { classId: p.classId, startDate: p.startDate || '', endDate: p.endDate || '', roomId: p.roomId || null, rules: [], remark: p.remark || '' };
      // 加载已有规则
      try {
        const rr = await getPlanRules(currentId.value);
        if (rr.success && rr.data) {
          form.value.rules = rr.data.map(r => ({ id: r.id, ruleType: r.ruleType || 'WEEKLY', weekDay: r.weekDay, scheduleDate: r.scheduleDate || '', startTime: r.startTime || '', endTime: r.endTime || '' }));
        }
      } catch { /* 静默 */ }
    }
  }
}
function reset() { form.value = { classId: null, startDate: '', endDate: '', roomId: null, rules: [], remark: '' }; nextTick(() => formRef.value?.clearValidate()); }

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value }; const rules = data.rules || []; delete data.rules;
    let res;
    if (currentId.value) {
      res = await updateSchedulePlan({ id: currentId.value, classId: data.classId, startDate: data.startDate, endDate: data.endDate, roomId: data.roomId, remark: data.remark });
      if (res.success) {
        await savePlanRules(currentId.value, rules.map(r => {
          const d = calcDuration(r.startTime, r.endTime);
          return { ruleType: r.ruleType || 'WEEKLY', weekDay: r.ruleType === 'WEEKLY' ? r.weekDay : null, scheduleDate: r.ruleType === 'SPECIFIC' ? r.scheduleDate : null, startTime: r.startTime || null, endTime: r.endTime || null, duration: d || 45 };
        }));
      }
    } else {
      res = await addSchedulePlan(data);
      if (res.success) {
        const planId = res.data?.id || res.data?.data?.id;
        if (planId) {
          await savePlanRules(planId, rules.map(r => {
            const d = calcDuration(r.startTime, r.endTime);
            return { ruleType: r.ruleType || 'WEEKLY', weekDay: r.ruleType === 'WEEKLY' ? r.weekDay : null, scheduleDate: r.ruleType === 'SPECIFIC' ? r.scheduleDate : null, startTime: r.startTime || null, endTime: r.endTime || null, duration: d || 45 };
          }));
        }
      }
    }
    if (res.success) { ElMessage.success(currentId.value ? '修改成功' : '新增成功'); open.value = false; getList(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value }; if (!params.classId) delete params.classId;
  listSchedulePlan(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}
function loadClasses() { listClass({ page: 1, size: 999 }).then(res => { if (res.success) classOptions.value = res.data?.records || res.rows || []; }); }
function loadRooms() { listClassroom({ page: 1, size: 999 }).then(res => { if (res.success) roomOptions.value = res.data?.records || res.rows || []; }); }
function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, classId: '' }; getList(); }
function handleAdd() { title.value = '新增排课计划'; currentId.value = null; open.value = true; }
function handleUpdate(row) { title.value = '修改排课计划'; currentId.value = row.id; open.value = true; }
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择排课计划'); return; }
  ElMessageBox.confirm('确认删除选中的排课计划？', '警告', { type: 'warning' })
    .then(() => delSchedulePlan(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    });
}
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }
// 预览
const previewOpen = ref(false);
const previewLoading = ref(false);
const previewPlanId = ref(null);
const previewData = ref({ slots: [], conflictCount: 0, totalCount: 0 });

function handlePreview(row) {
  previewPlanId.value = row.id;
  previewOpen.value = true;
}

async function loadPreview() {
  if (!previewPlanId.value) return;
  previewLoading.value = true;
  try {
    const plan = await getSchedulePlan(previewPlanId.value);
    if (!plan.success || !plan.data) { ElMessage.error('获取计划详情失败'); previewOpen.value = false; return; }
    const p = plan.data.data || plan.data;
    // 构建预览请求数据
    const dto = {
      classId: p.classId,
      startDate: p.startDate,
      endDate: p.endDate,
      roomId: p.roomId || null,
      // 默认周一，默认 09:00-10:30
      weekDays: p.weekDays || [1],
      startTime: '09:00:00',
      endTime: '10:30:00',
    };
    const res = await previewSchedulePlan(dto);
    if (res.success) { previewData.value = res.data || { slots: [], conflictCount: 0, totalCount: 0 }; }
    else { ElMessage.error(res.msg || '预览失败'); previewOpen.value = false; }
  } catch { ElMessage.error('预览请求失败'); previewOpen.value = false; }
  finally { previewLoading.value = false; }
}

function handleGenerateAfterPreview() {
  if (previewData.value.conflictCount > 0) {
    ElMessage.warning('存在冲突，请先解决冲突或使用"跳过冲突并生成"');
    return;
  }
  ElMessageBox.confirm('确认生成课程表？', '提示', { type: 'info' })
    .then(() => generateSchedulePlan(previewPlanId.value, { skipConflict: false })).then(res => {
      if (res.success) { ElMessage.success('课程表生成成功'); previewOpen.value = false; getList(); }
      else { ElMessage.error(res.msg || '生成失败'); }
    });
}

function handleGenerateWithSkip() {
  ElMessageBox.confirm(`存在 ${previewData.value.conflictCount} 个冲突，跳过后将为无冲突的槽位生成排课。确认继续？`, '警告', { type: 'warning' })
    .then(() => generateSchedulePlan(previewPlanId.value, { skipConflict: true })).then(res => {
      if (res.success) { ElMessage.success('课程表生成成功（已跳过冲突）'); previewOpen.value = false; getList(); }
      else { ElMessage.error(res.msg || '生成失败'); }
    });
}

function handleView(row) { handlePreview(row); }
function handleGenerate(row) {
  previewPlanId.value = row.id;
  loadPreview().then(() => {
    previewOpen.value = true;
  });
}

function handleRegenerate(row) {
  ElMessageBox.confirm('重新生成将清除已有的排课记录并重新执行冲突检测，确认继续？', '警告', { type: 'warning' })
    .then(() => regenerateSchedulePlan(row.id)).then(res => {
      if (res.success) { getList(); ElMessage.success('重新生成成功'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    });
}
onMounted(() => { getList(); loadDict('schedule_plan_status'); loadClasses(); loadRooms(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.rule-list { margin-bottom: 8px; }
.rule-item {
  background: #fafafa; border: 1px solid #ebeef5; border-radius: 6px;
  padding: 10px 12px; margin-bottom: 8px;
}
.rule-item-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.rule-item-num { font-size: 13px; font-weight: 500; color: #606266; }
.rule-time-sep { display: flex; align-items: center; justify-content: center; font-size: 13px; color: #909399; }
.rule-duration { margin-top: 6px; font-size: 12px; color: #67c23a; }
.rule-empty { text-align: center; color: #c0c4cc; font-size: 13px; padding: 12px 0; }
.add-rule-btn { width: 100%; border-style: dashed; margin-bottom: 12px; }
</style>
