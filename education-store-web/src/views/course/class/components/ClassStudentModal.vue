<!-- 班级学员管理 Drawer（含班级信息卡片 + 学员列表 + 剩余课时预警） -->
<template>
  <el-drawer :model-value="visible" :title="`学员管理 — ${className}`" size="650px" destroy-on-close
    direction="rtl" @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <!-- 班级信息卡片 -->
    <el-card shadow="never" class="class-card">
      <el-descriptions :column="2" size="small" border>
        <el-descriptions-item label="班级名称">{{ classInfo.name || className || '-' }}</el-descriptions-item>
        <el-descriptions-item label="班级编号">{{ classInfo.code || '-' }}</el-descriptions-item>
        <el-descriptions-item label="教师">{{ classInfo.teacherName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="教室">{{ classInfo.classroomName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学员数">
          <span :class="{ 'text-danger': classInfo.maxStudents > 0 && classInfo.currentCount >= classInfo.maxStudents }">
            {{ classInfo.currentCount || 0 }} / {{ classInfo.maxStudents || 0 }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dicts.class_status" :value="classInfo.status" />
        </el-descriptions-item>
        <el-descriptions-item label="开课日期">{{ classInfo.startDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结课日期">{{ classInfo.endDate || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <div style="margin: 16px 0 8px">
      <el-button type="primary" plain size="small" @click="addOpen = true" v-permission="['class:student:add']">
        <el-icon><Plus /></el-icon>添加学员
      </el-button>
      <el-button
        v-if="selectedStudents.length > 0"
        type="danger" plain size="small"
        @click="handleBatchRemove">
        <el-icon><Delete /></el-icon>批量移除 ({{ selectedStudents.length }})
      </el-button>
    </div>

    <!-- 学员列表 -->
    <el-table v-loading="loading" :data="studentList" border stripe
      @selection-change="handleStudentSelection">
      <el-table-column type="selection" width="45" align="center" />
      <el-table-column label="学员姓名" align="center" prop="studentName" min-width="100" />
      <el-table-column label="性别" align="center" width="60">
        <template #default="{ row }">
          {{ row.studentGender || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="联系电话" align="center" prop="studentPhone" width="130" />
      <el-table-column label="剩余课时" align="center" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.remainingHours !== undefined && row.remainingHours <= 2" type="danger" size="small">
            {{ row.remainingHours }}
          </el-tag>
          <span v-else>{{ row.remainingHours ?? '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.class_student_status" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="加入时间" align="center" prop="joinTime" width="160" />
      <el-table-column label="操作" align="center" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" size="small" @click="handleRemove(row)">退班</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加学员弹窗 -->
    <el-dialog :model-value="addOpen" title="添加学员" width="450px" destroy-on-close append-to-body
      @update:model-value="(val) => addOpen = val">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="80px">
        <el-form-item label="学员" prop="studentId">
          <el-select v-model="addForm.studentId" placeholder="请选择学员" filterable remote
            :remote-method="searchStudents" :loading="studentLoading" style="width:100%">
            <el-option v-for="s in studentOptions" :key="s.id"
              :label="`${s.name} (${s.mobile || '-'})`" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOpen = false">取消</el-button>
        <el-button type="primary" @click="handleAddStudent" :loading="addSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Delete } from '@element-plus/icons-vue';
import { getClass, listClassStudent, addClassStudent, removeClassStudent } from '@/api/class/classes';
import { listStudent } from '@/api/student/students';
import { useDictsStore } from '@/stores/dicts';
import DictTag from '@/components/DictTag/index.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  classId: { type: [Number, String], default: null },
  className: { type: String, default: '' },
});
const emit = defineEmits(['update:visible']);

const { dicts, loadDict } = useDictsStore();
const loading = ref(false);
const studentList = ref([]);
const selectedStudents = ref([]);
const classInfo = ref({});

// 添加学员子流程
const addOpen = ref(false);
const addFormRef = ref(null);
const addSubmitting = ref(false);
const addForm = ref({ studentId: null });
const addRules = { studentId: [{ required: true, message: '请选择学员', trigger: 'change' }] };
const studentOptions = ref([]);
const studentLoading = ref(false);
let searchTimer = null;
let searchSeq = 0;

async function onOpen() {
  await loadDict('class_student_status');
  await loadDict('class_status');
  await fetchClassInfo();
  fetchList();
}

async function fetchClassInfo() {
  if (!props.classId) return;
  try {
    const res = await getClass(props.classId);
    if (res.success && res.data) {
      classInfo.value = res.data.data || res.data;
    }
  } catch { /* 静默处理 */ }
}

function fetchList() {
  if (!props.classId) return;
  loading.value = true;
  listClassStudent(props.classId).then(res => {
    if (res.success) studentList.value = res.data || [];
  }).finally(() => { loading.value = false; });
}

function handleRemove(row) {
  ElMessageBox.confirm(`确认将"${row.studentName}"从班级移除？`, '警告', { type: 'warning' })
    .then(() => removeClassStudent(props.classId, row.id))
    .then(res => { if (res.success) { fetchList(); ElMessage.success('移除成功'); } else { ElMessage.error(res.msg || '操作失败'); } });
}

function handleBatchRemove() {
  const ids = selectedStudents.value.map(s => s.id);
  if (ids.length === 0) { ElMessage.warning('请选择学员'); return; }
  ElMessageBox.confirm(`确认批量移除 ${ids.length} 名学员？`, '警告', { type: 'warning' })
    .then(async () => {
      for (const id of ids) {
        await removeClassStudent(props.classId, id);
      }
      fetchList();
      selectedStudents.value = [];
      ElMessage.success(`已移除 ${ids.length} 名学员`);
    });
}

function handleStudentSelection(selection) {
  selectedStudents.value = selection;
}

function searchStudents(keyword) {
  clearTimeout(searchTimer);
  if (!keyword) { studentOptions.value = []; return; }
  studentLoading.value = true;
  searchTimer = setTimeout(() => {
    searchSeq++; const seq = searchSeq;
    listStudent({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== searchSeq) return;
      if (res.success) studentOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === searchSeq) studentLoading.value = false; });
  }, 300);
}

async function handleAddStudent() {
  const valid = await addFormRef.value?.validate().catch(() => false);
  if (!valid) return;
  addSubmitting.value = true;
  try {
    const res = await addClassStudent(props.classId, addForm.value);
    if (res.success) { ElMessage.success('添加成功'); addOpen.value = false; fetchList(); fetchClassInfo(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { addSubmitting.value = false; }
}
</script>

<style scoped lang="less">
.class-card {
  margin-bottom: 8px;
  :deep(.el-card__body) { padding: 12px; }
}
.text-danger { color: #f56c6c; font-weight: 600; }
</style>
