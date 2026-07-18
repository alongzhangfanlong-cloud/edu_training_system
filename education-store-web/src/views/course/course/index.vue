<!-- 课程管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="课程名称/编号" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="科目" prop="subjectId">
        <el-select v-model="queryParams.subjectId" placeholder="请选择科目" clearable style="width:180px">
          <el-option v-for="s in subjectOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="课程状态" clearable style="width:150px">
          <el-option v-for="d in dicts.sys_normal_disable" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['course:course:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['course:course:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['course:course:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="课程编号" align="center" prop="code" width="120" />
      <el-table-column label="课程名称" align="center" prop="name" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="所属科目" align="center" prop="subjectName" width="100" />
      <el-table-column label="课程类型" align="center" prop="courseType" width="100">
        <template #default="{ row }">
          <dict-tag :options="dicts.course_type" :value="row.courseType" />
        </template>
      </el-table-column>
      <el-table-column label="单价" align="center" prop="price" width="80" />
      <el-table-column label="适用年级" align="center" prop="applicableGrade" width="100" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="70" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <dict-tag :options="dicts.sys_normal_disable" :value="String(row.status)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['course:course:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['course:course:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <CourseFormModal v-model:visible="open" :title="title" :course-id="currentId" @submit-success="getList" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { listCourse, delCourse } from '@/api/course/courses';
import { listSubject } from '@/api/course/subjects';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';
import CourseFormModal from './components/CourseFormModal.vue';

const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const subjectOptions = ref([]);
const queryParams = ref({ page: 1, size: 10, keyword: '', subjectId: '', status: '' });

const open = ref(false);
const title = ref('');
const currentId = ref(null);

let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  if (!params.subjectId) delete params.subjectId;
  if (params.status === '' || params.status === null) delete params.status;
  listCourse(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; }
  }).finally(() => { if (seq === querySeq) loading.value = false; });
}

function loadSubjects() {
  listSubject({ page: 1, size: 999 }).then(res => {
    if (res.success) subjectOptions.value = res.data?.records || res.rows || [];
  });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '', subjectId: '', status: '' }; getList(); }

function handleAdd() { title.value = '新增课程'; currentId.value = null; open.value = true; }
function handleUpdate(row) { title.value = '修改课程'; currentId.value = row ? row.id : selectedIds.value[0]; open.value = true; }
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择课程'); return; }
  ElMessageBox.confirm('确认删除选中的课程？', '警告', { type: 'warning' })
    .then(() => delCourse(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    });
}
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

onMounted(() => { getList(); loadDict('sys_normal_disable'); loadDict('course_type'); loadSubjects(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
