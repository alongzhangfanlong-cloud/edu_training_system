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
      <el-table-column label="开始日期" align="center" prop="startDate" width="110" />
      <el-table-column label="结束日期" align="center" prop="endDate" width="110" />
      <el-table-column label="计划课次" align="center" prop="plannedSessions" width="90" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.schedule_plan_status" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['schedule:plan:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['schedule:plan:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <el-dialog :model-value="open" :title="title" width="550px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => open = val">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
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
        <el-form-item label="上课时间" prop="timeSlot">
          <el-input v-model="form.timeSlot" placeholder="如：每周一、三 09:00-10:30" />
        </el-form-item>
        <el-form-item label="计划课次">
          <el-input-number v-model="form.plannedSessions" :min="1" style="width:100%" />
        </el-form-item>
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
import { listSchedulePlan, getSchedulePlan, addSchedulePlan, updateSchedulePlan, delSchedulePlan } from '@/api/schedule/schedulePlans';
import { listClass } from '@/api/class/classes';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';

const { dicts, loadDict } = useDictsStore();
const loading = ref(false); const showSearch = ref(true);
const list = ref([]); const total = ref(0); const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const classOptions = ref([]);
const queryParams = ref({ page: 1, size: 10, classId: '' });
const open = ref(false); const title = ref(''); const currentId = ref(null);
const formRef = ref(null); const submitting = ref(false);
const form = ref({ classId: null, startDate: '', endDate: '', timeSlot: '', plannedSessions: 1, remark: '' });
const rules = {
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
};
let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value }; if (!params.classId) delete params.classId;
  listSchedulePlan(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; }
  }).finally(() => { if (seq === querySeq) loading.value = false; });
}
function loadClasses() { listClass({ page: 1, size: 999 }).then(res => { if (res.success) classOptions.value = res.data?.records || res.rows || []; }); }
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
async function onOpen() {
  reset(); loadClasses(); await loadDict('schedule_plan_status');
  if (currentId.value) {
    const res = await getSchedulePlan(currentId.value);
    if (res.success && res.data) {
      const p = res.data.data || res.data;
      form.value = { classId: p.classId || null, startDate: p.startDate || '', endDate: p.endDate || '', timeSlot: p.timeSlot || '', plannedSessions: p.plannedSessions || 1, remark: p.remark || '' };
    }
  }
}
function reset() { form.value = { classId: null, startDate: '', endDate: '', timeSlot: '', plannedSessions: 1, remark: '' }; nextTick(() => formRef.value?.clearValidate()); }
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value }; let res;
    if (currentId.value) { data.id = currentId.value; res = await updateSchedulePlan(data); }
    else { res = await addSchedulePlan(data); }
    if (res.success) { ElMessage.success(currentId.value ? '修改成功' : '新增成功'); open.value = false; getList(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
onMounted(() => { getList(); loadDict('schedule_plan_status'); loadClasses(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
