<!-- Tab1: 报名录入 -->
<template>
  <div>
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="学员" prop="studentId">
        <el-select v-model="queryParams.studentId" filterable remote :remote-method="searchStudents" :loading="studentLoading" placeholder="请搜索学员" clearable style="width:180px">
          <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="课程" prop="courseId">
        <el-select v-model="queryParams.courseId" filterable remote :remote-method="searchCourses" :loading="courseLoading" placeholder="请搜索课程" clearable style="width:180px">
          <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="收费状态" prop="paymentStatus">
        <el-select v-model="queryParams.paymentStatus" placeholder="全部" clearable style="width:130px">
          <el-option label="待收费" value="PENDING" />
          <el-option label="已收费" value="PAID" />
          <el-option label="部分收费" value="PARTIAL" />
          <el-option label="已退费" value="REFUNDED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['enrollment:enrollment:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['enrollment:enrollment:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['enrollment:enrollment:cancel']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="序号" align="center" width="60" type="index" />
      <el-table-column label="学员" align="center" prop="studentName" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="课程" align="center" prop="courseName" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="班级" align="center" prop="className" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="应缴金额" align="right" prop="amount" width="110" />
      <el-table-column label="收费状态" align="center" width="100">
        <template #default="{ row }">
          <el-tag :type="paymentStatusType(row.paymentStatus)" size="small">{{ paymentStatusLabel(row.paymentStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名日期" align="center" prop="enrollmentDate" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['enrollment:enrollment:edit']">修改</el-button>
          <el-button link type="warning" size="small" @click="handleCancel(row)" v-permission="['enrollment:enrollment:cancel']" v-if="row.paymentStatus !== 'CANCELLED'">取消</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['enrollment:enrollment:cancel']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无报名数据" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <EnrollmentFormModal v-model:visible="open" :title="title" :enrollment-id="currentId" @submit-success="getList" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { listEnrollment, cancelEnrollment, delEnrollment } from '@/api/finance/enrollments';
import { listStudent } from '@/api/student/students';
import { listCourse } from '@/api/course/courses';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import EnrollmentFormModal from './EnrollmentFormModal.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, studentId: '', courseId: '', paymentStatus: '' });
const studentOptions = ref([]);
const studentLoading = ref(false);
const courseOptions = ref([]);
const courseLoading = ref(false);
const open = ref(false);
const title = ref('');
const currentId = ref(null);

let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = {};
  if (queryParams.value.studentId) params.studentId = queryParams.value.studentId;
  if (queryParams.value.courseId) params.courseId = queryParams.value.courseId;
  if (queryParams.value.paymentStatus) params.paymentStatus = queryParams.value.paymentStatus;
  params.page = queryParams.value.page;
  params.pageSize = queryParams.value.pageSize;
  listEnrollment(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

let sTimer = null; let sSeq = 0;
function searchStudents(keyword) {
  clearTimeout(sTimer);
  if (!keyword) { studentOptions.value = []; return; }
  studentLoading.value = true;
  sTimer = setTimeout(() => { sSeq++; const seq = sSeq;
    listStudent({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== sSeq) return;
      if (res.success) studentOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === sSeq) studentLoading.value = false; });
  }, 300);
}

let cTimer = null; let cSeq = 0;
function searchCourses(keyword) {
  clearTimeout(cTimer);
  if (!keyword) { courseOptions.value = []; return; }
  courseLoading.value = true;
  cTimer = setTimeout(() => { cSeq++; const seq = cSeq;
    listCourse({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== cSeq) return;
      if (res.success) courseOptions.value = res.data?.records || res.rows || [];
    }).finally(() => { if (seq === cSeq) courseLoading.value = false; });
  }, 300);
}

function paymentStatusLabel(s) {
  const map = { DRAFT: '草稿', PENDING: '待收费', PARTIAL: '部分收费', PAID: '已收费', REFUNDED: '已退费', CANCELLED: '已取消' };
  return map[s] || s;
}
function paymentStatusType(s) {
  const map = { PENDING: 'warning', PARTIAL: 'info', PAID: 'success', REFUNDED: 'danger', CANCELLED: 'info' };
  return map[s] || '';
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, pageSize: 10, studentId: '', courseId: '', paymentStatus: '' }; getList(); }
function handleAdd() { title.value = '新增报名'; currentId.value = null; open.value = true; }
function handleUpdate(row) { title.value = '修改报名'; currentId.value = row ? row.id : selectedIds.value[0]; open.value = true; }

function handleCancel(row) {
  ElMessageBox.confirm('确认取消该报名记录？', '警告', { type: 'warning' })
    .then(() => cancelEnrollment(row.id)).then(res => {
      if (res.success) { getList(); ElMessage.success('取消成功'); }
      else { ElMessage.error(res.msg || '取消失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择记录'); return; }
  ElMessageBox.confirm('确认删除选中的报名记录？', '警告', { type: 'warning' })
    .then(() => delEnrollment(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.mb8 { margin-bottom: 8px; }
.error-box { margin: 20px 0; }
</style>
