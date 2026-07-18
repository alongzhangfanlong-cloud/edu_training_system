<!-- 成绩管理列表 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="班级" prop="classId">
        <el-select v-model="queryParams.classId" placeholder="选择班级" clearable filterable remote :remote-method="remoteSearchClass" :loading="classLoading" style="width:180px">
          <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="科目" prop="subjectId">
        <el-select v-model="queryParams.subjectId" placeholder="选择科目" clearable filterable remote :remote-method="remoteSearchSubject" :loading="subjectLoading" style="width:150px">
          <el-option v-for="s in subjectOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="考试类型" prop="examType">
        <el-select v-model="queryParams.examType" placeholder="全部" clearable style="width:130px">
          <el-option label="月考" value="MONTHLY" />
          <el-option label="期中" value="MIDTERM" />
          <el-option label="期末" value="FINAL" />
          <el-option label="测验" value="QUIZ" />
          <el-option label="自定义" value="CUSTOM" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围" prop="dateRange">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width:240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['teaching:exam:add']">
          <el-icon><Plus /></el-icon>录入成绩
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['teaching:exam:remove']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="考试名称" align="center" prop="examName" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="班级" align="center" prop="className" width="120" />
      <el-table-column label="科目" align="center" prop="subjectName" width="100" />
      <el-table-column label="类型" align="center" width="90">
        <template #default="{ row }">
          <el-tag size="small">{{ examTypeMap[row.examType] || row.examType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="考试日期" align="center" prop="examDate" width="110" />
      <el-table-column label="满分" align="center" prop="totalScore" width="80" />
      <el-table-column label="教师" align="center" prop="teacherName" width="100" />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['teaching:exam:edit']">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['teaching:exam:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无成绩记录。点击「录入成绩」开始。" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <ExamFormDrawer v-model:visible="drawerOpen" :title="drawerTitle" :exam-id="currentId" @submit-success="getList" />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue';
import { listExam, delExam } from '@/api/teaching/exam';
import { listClass } from '@/api/class/classes';
import { listSubject } from '@/api/course/subjects';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import ExamFormDrawer from './components/ExamFormDrawer.vue';

const examTypeMap = { MONTHLY: '月考', MIDTERM: '期中', FINAL: '期末', QUIZ: '测验', CUSTOM: '自定义' };

const loading = ref(false); const showSearch = ref(true);
const list = ref([]); const total = ref(0); const error = ref('');
const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, size: 10, classId: '', subjectId: '', examType: '' });
const dateRange = ref([]);
const classOptions = ref([]); const classLoading = ref(false);
const subjectOptions = ref([]); const subjectLoading = ref(false);
const drawerOpen = ref(false); const drawerTitle = ref(''); const currentId = ref(null);
let querySeq = 0; let classSeq = 0; let subjectSeq = 0;

watch(dateRange, () => { if (dateRange.value && dateRange.value.length === 2) { queryParams.value.startDate = dateRange.value[0]; queryParams.value.endDate = dateRange.value[1]; } else { delete queryParams.value.startDate; delete queryParams.value.endDate; } });

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  ['classId', 'subjectId', 'examType', 'startDate', 'endDate'].forEach(k => { if (!params[k] && params[k] !== 0) delete params[k]; });
  listExam(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}
function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, classId: '', subjectId: '', examType: '' }; dateRange.value = []; getList(); }
function handleAdd() { drawerTitle.value = '录入成绩'; currentId.value = null; drawerOpen.value = true; }
function handleUpdate(row) { drawerTitle.value = '编辑成绩'; currentId.value = row.id; drawerOpen.value = true; }
function handleView(row) { drawerTitle.value = '查看成绩'; currentId.value = row.id; drawerOpen.value = true; }
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择成绩记录'); return; }
  ElMessageBox.confirm('确认删除选中的成绩记录？', '警告', { type: 'warning' })
    .then(() => Promise.all(ids.map(id => delExam(id, row?.teacherId))))
    .then(results => {
      const allOk = results.every(r => r.success);
      if (allOk) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error('部分删除失败'); getList(); }
    }).catch(() => {});
}
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

function remoteSearchClass(query) {
  if (!query) { classOptions.value = []; return; }
  classLoading.value = true; classSeq++; const seq = classSeq;
  listClass({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== classSeq) return;
    classOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === classSeq) classLoading.value = false; });
}

function remoteSearchSubject(query) {
  if (!query) { subjectOptions.value = []; return; }
  subjectLoading.value = true; subjectSeq++; const seq = subjectSeq;
  listSubject({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== subjectSeq) return;
    subjectOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === subjectSeq) subjectLoading.value = false; });
}

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
