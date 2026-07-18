<!-- 班级学员管理弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="`学员管理 — ${className}`" width="650px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-table v-loading="loading" :data="studentList" border stripe>
      <el-table-column label="学员姓名" align="center" prop="studentName" min-width="100" />
      <el-table-column label="联系电话" align="center" prop="studentPhone" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.class_student_status" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="加入时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" size="small" @click="handleRemove(row)">退班</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 16px">
      <el-button type="primary" plain size="small" @click="addOpen = true" v-permission="['class:student:add']">
        <el-icon><Plus /></el-icon>添加学员
      </el-button>
    </div>

    <el-dialog :model-value="addOpen" title="添加学员" width="450px" destroy-on-close append-to-body @update:model-value="(val) => addOpen = val">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="80px">
        <el-form-item label="学员" prop="studentId">
          <el-select v-model="addForm.studentId" placeholder="请选择学员" filterable remote :remote-method="searchStudents" :loading="studentLoading" style="width:100%">
            <el-option v-for="s in studentOptions" :key="s.id" :label="`${s.name} (${s.mobile || '-'})`" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOpen = false">取消</el-button>
        <el-button type="primary" @click="handleAddStudent" :loading="addSubmitting">确定</el-button>
      </template>
    </el-dialog>

    <template #footer>
      <el-button @click="emit('update:visible', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { listClassStudent, addClassStudent, removeClassStudent } from '@/api/class/classes';
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
const loading = ref(false); const studentList = ref([]);
const addOpen = ref(false); const addFormRef = ref(null); const addSubmitting = ref(false);
const addForm = ref({ studentId: null });
const addRules = { studentId: [{ required: true, message: '请选择学员', trigger: 'change' }] };
const studentOptions = ref([]); const studentLoading = ref(false);
let searchTimer = null; let searchSeq = 0;

function onOpen() { loadDict('class_student_status'); fetchList(); }
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
function searchStudents(keyword) {
  clearTimeout(searchTimer); if (!keyword) { studentOptions.value = []; return; }
  studentLoading.value = true;
  searchTimer = setTimeout(() => { searchSeq++; const seq = searchSeq;
    listStudent({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== searchSeq) return;
      if (res.success) studentOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === searchSeq) studentLoading.value = false; });
  }, 300);
}
async function handleAddStudent() {
  const valid = await addFormRef.value?.validate().catch(() => false); if (!valid) return;
  addSubmitting.value = true;
  try {
    const res = await addClassStudent(props.classId, addForm.value);
    if (res.success) { ElMessage.success('添加成功'); addOpen.value = false; fetchList(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { addSubmitting.value = false; }
}
</script>
