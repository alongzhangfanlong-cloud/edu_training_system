<!-- 教评学列表 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="评价内容" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="搜索评价内容" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="教师" prop="teacherId">
        <el-select v-model="queryParams.teacherId" placeholder="选择教师" clearable filterable remote :remote-method="remoteSearchTeacher" :loading="teacherLoading" style="width:150px">
          <el-option v-for="t in teacherOptions" :key="t.id" :label="t.realName" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="学生" prop="studentId">
        <el-select v-model="queryParams.studentId" placeholder="选择学生" clearable filterable remote :remote-method="remoteSearchStudent" :loading="studentLoading" style="width:150px">
          <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围" prop="dateRange">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width:240px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="评价状态" clearable style="width:120px">
          <el-option label="草稿" :value="0" />
          <el-option label="已提交" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['teaching:evaluation:add']">
          <el-icon><Plus /></el-icon>新建评价
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="序号" align="center" type="index" width="60" />
      <el-table-column label="课次标题" align="center" prop="scheduleTitle" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="上课日期" align="center" prop="scheduleDate" width="110" />
      <el-table-column label="点评老师" align="center" prop="teacherName" width="100" />
      <el-table-column label="课堂总结" align="center" prop="summary" :show-overflow-tooltip="true" min-width="180">
        <template #default="{ row }">{{ row.summary ? row.summary.slice(0, 30) + (row.summary.length > 30 ? '…' : '') : '—' }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">已评</el-tag>
          <el-tag v-else-if="row.status === 0" type="warning">草稿</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="160" />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" link type="primary" size="small" @click="handleView(row)">查看</el-button>
          <el-button v-else link type="primary" size="small" @click="handleEdit(row)">继续编辑</el-button>
          <el-button v-if="row.status === 0" link type="danger" size="small" @click="handleDelete(row)" v-permission="['teaching:evaluation:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无评价记录。点击「新建评价」开始。" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <EvaluationDrawer v-model:visible="drawerOpen" :title="drawerTitle" :evaluation-id="currentId" :schedule-id="scheduleId" :readonly="readonly" @submit-success="getList" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { listEvaluation, delEvaluation } from '@/api/teaching/evaluation';
import { listTeacher } from '@/api/teaching/teachers';
import { listStudent } from '@/api/student/students';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import EvaluationDrawer from './components/EvaluationDrawer.vue';

const loading = ref(false); const showSearch = ref(true);
const list = ref([]); const total = ref(0); const error = ref('');
const queryParams = ref({ page: 1, size: 10, keyword: '', teacherId: '', studentId: '', status: '' });
const dateRange = ref([]);
const teacherOptions = ref([]); const teacherLoading = ref(false);
const studentOptions = ref([]); const studentLoading = ref(false);
const drawerOpen = ref(false); const drawerTitle = ref(''); const currentId = ref(null);
const scheduleId = ref(null); const readonly = ref(false);
let querySeq = 0; let teacherSeq = 0; let studentSeq = 0;

watch(dateRange, () => { if (dateRange.value && dateRange.value.length === 2) { queryParams.value.startDate = dateRange.value[0]; queryParams.value.endDate = dateRange.value[1]; } else { delete queryParams.value.startDate; delete queryParams.value.endDate; } });

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  ['keyword', 'teacherId', 'studentId', 'status', 'startDate', 'endDate'].forEach(k => { if (!params[k] && params[k] !== 0) delete params[k]; });
  listEvaluation(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '', teacherId: '', studentId: '', status: '' }; dateRange.value = []; getList(); }
function handleAdd() { drawerTitle.value = '新建评价'; currentId.value = null; scheduleId.value = null; readonly.value = false; drawerOpen.value = true; }
function handleEdit(row) { drawerTitle.value = '编辑评价'; currentId.value = row.id; scheduleId.value = null; readonly.value = false; drawerOpen.value = true; }
function handleView(row) { drawerTitle.value = '评价详情'; currentId.value = row.id; scheduleId.value = null; readonly.value = true; drawerOpen.value = true; }
function handleDelete(row) {
  ElMessageBox.confirm('确定删除该评价？仅草稿可删除。', '警告', { type: 'warning' })
    .then(() => delEvaluation(row.id, row.teacherId)).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => {});
}

function remoteSearchTeacher(query) {
  if (!query) { teacherOptions.value = []; return; }
  teacherLoading.value = true; teacherSeq++; const seq = teacherSeq;
  listTeacher({ keyword: query }).then(res => {
    if (seq !== teacherSeq) return;
    teacherOptions.value = res.success ? (res.data?.records || res.rows || res.data || []) : [];
  }).finally(() => { if (seq === teacherSeq) teacherLoading.value = false; });
}

function remoteSearchStudent(query) {
  if (!query) { studentOptions.value = []; return; }
  studentLoading.value = true; studentSeq++; const seq = studentSeq;
  listStudent({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== studentSeq) return;
    studentOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === studentSeq) studentLoading.value = false; });
}

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
