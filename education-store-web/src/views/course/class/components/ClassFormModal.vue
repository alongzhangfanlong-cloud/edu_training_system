<!-- 班级新增/编辑弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="660px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="班级名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入班级名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程" prop="courseId">
            <el-select v-model="form.courseId" placeholder="请选择课程" style="width:100%">
              <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="教师" prop="teacherId">
            <el-select v-model="form.teacherId" placeholder="请选择教师" filterable style="width:100%" clearable>
              <el-option v-for="t in teacherOptions" :key="t.userId" :label="t.realName" :value="t.userId" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="默认教室">
            <el-select v-model="form.classroomId" placeholder="请选择教室" style="width:100%" clearable>
              <el-option v-for="r in roomOptions" :key="r.id" :label="r.name" :value="r.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="最多人数">
            <el-input-number v-model="form.maxStudents" :min="1" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" style="width:100%" :disabled="!classId">
              <el-option v-for="d in statusOptions" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开课日期">
            <el-date-picker v-model="form.startDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结课日期">
            <el-date-picker v-model="form.endDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 上课规则：动态多条 -->
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
          <div v-if="ruleDuration(rule)" class="rule-duration">⏱ {{ ruleDuration(rule) }}</div>
        </div>
        <div v-if="form.rules.length === 0" class="rule-empty">暂未添加上课规则</div>
      </div>
      <el-button class="add-rule-btn" size="small" @click="addRule"><el-icon><Plus /></el-icon>添加规则</el-button>
      <el-form-item label="班级说明">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { getClass, addClass, updateClass, getScheduleRules, saveScheduleRules } from '@/api/class/classes';
import { listCourse } from '@/api/course/courses';
import { listTeacherUsers } from '@/api/teaching/teachers';
import { listClassroom } from '@/api/course/classrooms';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  classId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null); const submitting = ref(false);
const courseOptions = ref([]);
const teacherOptions = ref([]);
const roomOptions = ref([]);

const form = ref({ name: '', courseId: null, teacherId: null, classroomId: null, maxStudents: 20, status: 'RECRUITING', startDate: '', endDate: '', rules: [], description: '' });
const statusOptions = [
  { value: 'RECRUITING', label: '招生中' },
  { value: 'FULL', label: '已满' },
  { value: 'SUSPENDED', label: '停招' },
  { value: 'FINISHED', label: '已结课' },
  { value: 'CANCELLED', label: '已取消' },
];
const weekDayOptions = [
  { value: 1, label: '周一' }, { value: 2, label: '周二' }, { value: 3, label: '周三' },
  { value: 4, label: '周四' }, { value: 5, label: '周五' }, { value: 6, label: '周六' }, { value: 7, label: '周日' },
];
const rules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
};

function addRule() {
  form.value.rules.push({ ruleType: 'WEEKLY', weekDay: null, scheduleDate: '', startTime: '', endTime: '' });
}

function removeRule(index) {
  form.value.rules.splice(index, 1);
}

function ruleDuration(rule) {
  const mins = calcDuration(rule.startTime, rule.endTime);
  if (!mins || mins <= 0) return '';
  if (mins < 60) return `${mins}分钟`;
  const h = Math.floor(mins / 60);
  const m = mins % 60;
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
  reset();
  await Promise.all([loadCourses(), loadTeachers(), loadRooms()]);
  if (props.classId) {
    const res = await getClass(props.classId);
    if (res.success && res.data) {
      const c = res.data.data || res.data;
      form.value = { name: c.name || '', courseId: c.courseId || null, teacherId: c.teacherId || null, classroomId: c.classroomId || null, maxStudents: c.maxStudents || 20, status: c.status || 'RECRUITING', startDate: c.startDate || '', endDate: c.endDate || '', rules: [], description: c.description || '' };
      // 编辑时如果已选教师不在列表中，补入（避免 filterable 搜索不到）
      if (c.teacherId && c.teacherName && !teacherOptions.value.find(t => t.userId === c.teacherId)) {
        teacherOptions.value.push({ userId: c.teacherId, realName: c.teacherName });
      }
      // 加载已有排课规则
      loadRules();
    }
  }
}

async function loadRules() {
  try {
    const res = await getScheduleRules(props.classId);
    if (res.success && res.data && res.data.length > 0) {
      form.value.rules = res.data.map(r => ({
        id: r.id,
        ruleType: r.ruleType || 'WEEKLY',
        weekDay: r.weekDay,
        scheduleDate: r.scheduleDate || '',
        startTime: r.startTime || '',
        endTime: r.endTime || '',
      }));
    }
  } catch { /* 静默处理 */ }
}

function loadCourses() { listCourse({ page: 1, size: 999 }).then(res => { if (res.success) courseOptions.value = res.data?.records || res.rows || []; }); }
function loadTeachers() { listTeacherUsers().then(res => { if (res.success) teacherOptions.value = res.data || []; }); }
function loadRooms() { listClassroom({ page: 1, size: 999 }).then(res => { if (res.success) roomOptions.value = res.data?.records || res.rows || []; }); }

function reset() {
  form.value = { name: '', courseId: null, teacherId: null, classroomId: null, maxStudents: 20, status: 'RECRUITING', startDate: '', endDate: '', rules: [], description: '' };
  teacherOptions.value = []; nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value };
    const rules = data.rules || [];
    delete data.rules;
    let res;
    if (props.classId) { data.id = props.classId; res = await updateClass(data); }
    else { res = await addClass(data); }
    if (res.success) {
      // 新增时从响应获取 classId
      const classId = props.classId || (res.data?.id || res.data);
      // 保存排课规则
      if (classId) {
        await saveScheduleRules(classId, rules.map(r => {
          const duration = calcDuration(r.startTime, r.endTime);
          return {
            ruleType: r.ruleType || 'WEEKLY',
            weekDay: r.ruleType === 'WEEKLY' ? r.weekDay : null,
            scheduleDate: r.ruleType === 'SPECIFIC' ? r.scheduleDate : null,
            startTime: r.startTime || null,
            endTime: r.endTime || null,
            duration: duration || 45,
          };
        }));
      }
      ElMessage.success(props.classId ? '修改成功' : '新增成功');
      emit('update:visible', false);
      emit('submit-success');
    }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>

<style scoped lang="less">
.rule-list {
  margin-bottom: 8px;
}
.rule-item {
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 10px 12px;
  margin-bottom: 8px;
}
.rule-item-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.rule-item-num {
  font-size: 13px;
  font-weight: 500;
  color: #606266;
}
.rule-time-sep {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #909399;
}
.rule-duration {
  margin-top: 6px;
  font-size: 12px;
  color: #67c23a;
}
.rule-empty {
  text-align: center;
  color: #c0c4cc;
  font-size: 13px;
  padding: 12px 0;
}
.add-rule-btn {
  width: 100%;
  border-style: dashed;
}
</style>
