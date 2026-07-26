<!-- N06.3 授课统计 / 课酬单价管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="教师">
        <el-select v-model="queryParams.teacherId" filterable remote :remote-method="searchTeachers" :loading="teacherLoading" placeholder="请搜索教师" clearable style="width:180px">
          <el-option v-for="t in teacherOptions" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" filterable remote :remote-method="searchCourses" :loading="courseLoading" placeholder="请搜索课程" clearable style="width:180px">
          <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['payroll:pay-rate:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['payroll:pay-rate:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['payroll:pay-rate:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="序号" align="center" width="60" type="index" />
      <el-table-column label="教师" align="center" prop="teacherName" width="120" />
      <el-table-column label="课程" align="center" prop="courseName" width="150" />
      <el-table-column label="单节课酬" align="right" width="120">
        <template #default="{ row }">¥{{ Number(row.payPerLesson || 0).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="生效日期" align="center" prop="effectiveFrom" width="120" />
      <el-table-column label="截止日期" align="center" width="120">
        <template #default="{ row }">{{ row.effectiveTo || '长期有效' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['payroll:pay-rate:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['payroll:pay-rate:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无课酬单价配置" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 课酬单价表单弹窗 -->
    <el-dialog v-model="open" :title="title" width="520px" destroy-on-close append-to-body @open="onFormOpen">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="form.teacherId" filterable remote :remote-method="searchTeachers" :loading="teacherLoading" placeholder="请搜索教师" clearable style="width:100%">
            <el-option v-for="t in teacherOptions" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" filterable remote :remote-method="searchCourses" :loading="courseLoading" placeholder="请搜索课程" clearable style="width:100%">
            <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="单节课酬" prop="payPerLesson">
          <el-input-number v-model="form.payPerLesson" :min="0.01" :precision="2" style="width:100%" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生效日期" prop="effectiveFrom">
              <el-date-picker v-model="form.effectiveFrom" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期">
              <el-date-picker v-model="form.effectiveTo" type="date" placeholder="长期有效" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="handleFormSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { listPayRate, getPayRate, addPayRate, updatePayRate, delPayRate } from '@/api/finance/payRates';
import { listTeacherUsers } from '@/api/teaching/teachers';
import { listCourse, listAllCourses } from '@/api/course/courses';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, teacherId: null, courseId: null });
const teacherOptions = ref([]);
const teacherLoading = ref(false);
const courseOptions = ref([]);
const courseLoading = ref(false);
const open = ref(false);
const title = ref('');
const currentId = ref(null);
const submitting = ref(false);
const formRef = ref(null);

const form = ref({ teacherId: null, courseId: null, payPerLesson: null, effectiveFrom: '', effectiveTo: '' });
const rules = {
  teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  payPerLesson: [{ required: true, message: '请输入单节课酬', trigger: 'blur' }],
  effectiveFrom: [{ required: true, message: '请选择生效日期', trigger: 'change' }],
};

let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.teacherId) params.teacherId = queryParams.value.teacherId;
  if (queryParams.value.courseId) params.courseId = queryParams.value.courseId;
  listPayRate(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

let tTimer = null; let tSeq = 0;
function searchTeachers(keyword) {
  clearTimeout(tTimer);
  teacherLoading.value = true;
  tTimer = setTimeout(() => { tSeq++; const seq = tSeq;
    listTeacherUsers(keyword || '').then(res => {
      if (seq !== tSeq) return;
      if (res.success) {
        teacherOptions.value = (res.data || []).map(u => ({ id: u.userId, name: u.realName }));
      }
    }).finally(() => { if (seq === tSeq) teacherLoading.value = false; });
  }, 300);
}

let cTimer = null; let cSeq = 0;
function searchCourses(keyword) {
  clearTimeout(cTimer);
  courseLoading.value = true;
  cTimer = setTimeout(() => { cSeq++; const seq = cSeq;
    // 如果没有关键词，加载所有课程；有关键词时分页搜索
    const apiCall = !keyword 
      ? listAllCourses() 
      : listCourse({ page: 1, pageSize: 100, name: keyword });
    apiCall.then(res => {
      if (seq !== cSeq) return;
      if (res.success) {
        const courses = Array.isArray(res.data) ? res.data : (res.data?.records || res.rows || []);
        courseOptions.value = courses.map(c => ({ id: c.id, name: c.name }));
      }
    }).finally(() => { if (seq === cSeq) courseLoading.value = false; });
  }, 300);
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, pageSize: 10, teacherId: null, courseId: null }; teacherOptions.value = []; courseOptions.value = []; getList(); }
function handleAdd() { title.value = '新增课酬单价'; currentId.value = null; open.value = true; }
function handleUpdate(row) { 
  if (!row && selectedIds.value.length !== 1) {
    ElMessage.warning('请选择一条记录');
    return;
  }
  title.value = '修改课酬单价'; 
  currentId.value = row ? row.id : selectedIds.value[0]; 
  open.value = true; 
}

function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择记录'); return; }
  ElMessageBox.confirm('确认删除选中的课酬单价？', '警告', { type: 'warning' })
    .then(() => delPayRate(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => {});
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

async function onFormOpen() {
  resetForm();
  // 并行加载教师和课程列表
  await Promise.all([searchTeachers(''), searchCourses('')]);
  if (currentId.value) {
    try {
      const res = await getPayRate(currentId.value);
      if (res.success && res.data) {
        const r = res.data;
        form.value = { 
          teacherId: r.teacherId || null, 
          courseId: r.courseId || null, 
          payPerLesson: r.payPerLesson || null, 
          effectiveFrom: r.effectiveFrom || '', 
          effectiveTo: r.effectiveTo || '' 
        };
        // 确保选中的教师在选项中
        if (r.teacherId && r.teacherName) {
          const exists = teacherOptions.value.find(t => t.id === r.teacherId);
          if (!exists) {
            teacherOptions.value.push({ id: r.teacherId, name: r.teacherName });
          }
        }
        // 确保选中的课程在选项中
        if (r.courseId && r.courseName) {
          const exists = courseOptions.value.find(c => c.id === r.courseId);
          if (!exists) {
            courseOptions.value.push({ id: r.courseId, name: r.courseName });
          }
        }
      }
    } catch (e) {
      console.error('加载课酬单价详情失败', e);
    }
  }
}

function resetForm() {
  form.value = { teacherId: null, courseId: null, payPerLesson: null, effectiveFrom: '', effectiveTo: '' };
  nextTick(() => {
    if (formRef.value) {
      formRef.value.clearValidate();
    }
  });
}

async function handleFormSubmit() {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch (e) {
    return;
  }
  submitting.value = true;
  try {
    const data = { ...form.value }; 
    // 格式化日期为字符串
    if (data.effectiveFrom && typeof data.effectiveFrom !== 'string') {
      data.effectiveFrom = data.effectiveFrom;
    }
    let res;
    if (currentId.value) { 
      data.id = currentId.value; 
      res = await updatePayRate(data); 
    } else { 
      res = await addPayRate(data); 
    }
    if (res.success) { 
      ElMessage.success(currentId.value ? '修改成功' : '新增成功'); 
      open.value = false; 
      getList(); 
    } else { 
      ElMessage.error(res.msg || '操作失败'); 
    }
  } catch (e) {
    ElMessage.error('网络异常，请稍后重试');
  } finally { 
    submitting.value = false; 
  }
}

onMounted(() => { 
  getList(); 
  searchTeachers('');
});
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.error-box { margin: 20px 0; }
</style>
