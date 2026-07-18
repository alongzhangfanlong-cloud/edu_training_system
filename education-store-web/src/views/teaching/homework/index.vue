<!-- 课后作业列表 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="作业标题" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="搜索作业标题" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="班级" prop="classId">
        <el-select v-model="queryParams.classId" placeholder="选择班级" clearable filterable remote :remote-method="remoteSearchClass" :loading="classLoading" style="width:180px">
          <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布者" prop="teacherId">
        <el-select v-model="queryParams.teacherId" placeholder="选择教师" clearable filterable remote :remote-method="remoteSearchTeacher" :loading="teacherLoading" style="width:150px">
          <el-option v-for="t in teacherOptions" :key="t.id" :label="t.realName" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布日期" prop="dateRange">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width:240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['teaching:homework:add']">
          <el-icon><Plus /></el-icon>布置作业
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="作业标题" align="center" prop="title" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="班级" align="center" prop="className" width="120" />
      <el-table-column label="课程" align="center" prop="courseName" width="120" />
      <el-table-column label="排课日期" align="center" prop="scheduleDate" width="120">
        <template #default="{ row }">{{ row.scheduleDate || '—' }}</template>
      </el-table-column>
      <el-table-column label="发布人" align="center" prop="teacherName" width="100" />
      <el-table-column label="发布时间" align="center" prop="createTime" width="160" />
      <el-table-column label="附件数" align="center" width="110">
        <template #default="{ row }">
          <template v-if="row.imageCount > 0 || row.fileCount > 0">
            <span v-if="row.imageCount > 0">{{ row.imageCount }}图</span>
            <span v-if="row.imageCount > 0 && row.fileCount > 0"> + </span>
            <span v-if="row.fileCount > 0">{{ row.fileCount }}文档</span>
          </template>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.isUpdated === 1" type="warning" size="small">已更新</el-tag>
          <span v-else style="color:#999">—</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleDetail(row)">查看</el-button>
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['teaching:homework:edit']">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['teaching:homework:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无作业记录。点击「布置作业」开始。" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <HomeworkDrawer v-model:visible="drawerOpen" :title="drawerTitle" :homework-id="currentId" @submit-success="getList" />
    <HomeworkDetailDrawer v-model:visible="detailOpen" :homework-id="currentId" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { listHomework, delHomework } from '@/api/teaching/homework';
import { listClass } from '@/api/class/classes';
import { listTeacher } from '@/api/teaching/teachers';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import HomeworkDrawer from './components/HomeworkDrawer.vue';
import HomeworkDetailDrawer from './components/HomeworkDetailDrawer.vue';

const loading = ref(false); const showSearch = ref(true);
const list = ref([]); const total = ref(0); const error = ref('');
const queryParams = ref({ page: 1, size: 10, keyword: '', classId: '', teacherId: '' });
const dateRange = ref([]);
const classOptions = ref([]); const classLoading = ref(false);
const teacherOptions = ref([]); const teacherLoading = ref(false);
const drawerOpen = ref(false); const drawerTitle = ref(''); const currentId = ref(null);
const detailOpen = ref(false);
let querySeq = 0; let classSeq = 0; let teacherSeq = 0;

watch(dateRange, () => { if (dateRange.value && dateRange.value.length === 2) { queryParams.value.startDate = dateRange.value[0]; queryParams.value.endDate = dateRange.value[1]; } else { delete queryParams.value.startDate; delete queryParams.value.endDate; } });

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  ['keyword', 'classId', 'teacherId', 'startDate', 'endDate'].forEach(k => { if (!params[k] && params[k] !== 0) delete params[k]; });
  listHomework(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '', classId: '', teacherId: '' }; dateRange.value = []; getList(); }
function handleAdd() { drawerTitle.value = '布置作业'; currentId.value = null; drawerOpen.value = true; }
function handleUpdate(row) { drawerTitle.value = '编辑作业'; currentId.value = row.id; drawerOpen.value = true; }
function handleDetail(row) { currentId.value = row.id; detailOpen.value = true; }
function handleDelete(row) {
  ElMessageBox.confirm('确定删除该作业？删除后学生家长端将不可见。', '警告', { type: 'warning' })
    .then(() => delHomework(row.id, row.teacherId)).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => {});
}

function remoteSearchClass(query) {
  if (!query) { classOptions.value = []; return; }
  classLoading.value = true; classSeq++; const seq = classSeq;
  listClass({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== classSeq) return;
    classOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === classSeq) classLoading.value = false; });
}

function remoteSearchTeacher(query) {
  if (!query) { teacherOptions.value = []; return; }
  teacherLoading.value = true; teacherSeq++; const seq = teacherSeq;
  listTeacher({ keyword: query }).then(res => {
    if (seq !== teacherSeq) return;
    teacherOptions.value = res.success ? (res.data?.records || res.rows || res.data || []) : [];
  }).finally(() => { if (seq === teacherSeq) teacherLoading.value = false; });
}

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
