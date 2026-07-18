<!-- 班级管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="班级名称/编号" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="班级状态" clearable style="width:150px">
          <el-option v-for="d in dicts.class_status" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['class:class:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['class:class:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="班级编号" align="center" prop="code" width="130" />
      <el-table-column label="班级名称" align="center" prop="name" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="课程" align="center" prop="courseName" width="120" />
      <el-table-column label="教师" align="center" prop="teacherName" width="80" />
      <el-table-column label="教室" align="center" prop="classroomName" width="100" />
      <el-table-column label="学员数" align="center" width="90">
        <template #default="{ row }">
          <span :class="{ 'text-danger': row.maxStudents > 0 && row.currentCount >= row.maxStudents }">
            {{ row.currentCount || 0 }} / {{ row.maxStudents || 0 }}
          </span>
          <el-tag v-if="row.maxStudents > 0 && row.currentCount >= row.maxStudents" type="danger" size="small" style="margin-left: 4px">满员</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.class_status" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="开课日期" align="center" prop="startDate" width="110" />
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['class:class:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['class:class:delete']">删除</el-button>
          <el-dropdown @command="(cmd) => handleCommand(cmd, row)">
            <el-button link type="primary" size="small">更多<el-icon><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="students">学员管理</el-dropdown-item>
                <el-dropdown-item v-if="row.status === 'RECRUITING'" command="finish" divided>结课</el-dropdown-item>
                <el-dropdown-item v-if="row.status === 'RECRUITING'" command="suspend">停招</el-dropdown-item>
                <el-dropdown-item v-if="row.status === 'SUSPENDED'" command="resume">恢复招生</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无班级数据" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <ClassFormModal v-model:visible="open" :title="title" :class-id="currentId" @submit-success="getList" />
    <ClassStudentModal v-model:visible="studentOpen" :class-id="currentId" :class-name="currentName" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete, ArrowDown } from '@element-plus/icons-vue';
import { listClass, delClass, changeClassStatus } from '@/api/class/classes';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';
import ClassFormModal from './components/ClassFormModal.vue';
import ClassStudentModal from './components/ClassStudentModal.vue';

const { dicts, loadDict } = useDictsStore();
const loading = ref(false); const showSearch = ref(true);
const list = ref([]); const total = ref(0); const error = ref('');
const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, size: 10, keyword: '', status: '' });
const open = ref(false); const title = ref(''); const currentId = ref(null); const currentName = ref('');
const studentOpen = ref(false);
let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value }; if (!params.keyword) delete params.keyword; if (!params.status) delete params.status;
  listClass(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}
function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '', status: '' }; getList(); }
function handleAdd() { title.value = '新增班级'; currentId.value = null; open.value = true; }
function handleUpdate(row) { title.value = '修改班级'; currentId.value = row.id; open.value = true; }
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择班级'); return; }
  ElMessageBox.confirm('确认删除选中的班级？', '警告', { type: 'warning' })
    .then(() => delClass(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    });
}
function handleCommand(cmd, row) {
  currentId.value = row.id; currentName.value = row.name;
  if (cmd === 'students') { studentOpen.value = true; return; }
  if (cmd === 'finish') {
    ElMessageBox.confirm('确认将该班级设为"已结课"？结课后无法再加入学员。', '确认', { type: 'warning' })
      .then(() => changeClassStatus(row.id, 'FINISHED'))
      .then(res => { if (res.success) { getList(); ElMessage.success('已结课'); } else { ElMessage.error(res.msg || '操作失败'); } });
    return;
  }
  if (cmd === 'suspend') {
    ElMessageBox.confirm('确认暂停该班级招生？', '确认', { type: 'warning' })
      .then(() => changeClassStatus(row.id, 'SUSPENDED'))
      .then(res => { if (res.success) { getList(); ElMessage.success('已停招'); } else { ElMessage.error(res.msg || '操作失败'); } });
    return;
  }
  if (cmd === 'resume') {
    changeClassStatus(row.id, 'RECRUITING')
      .then(res => { if (res.success) { getList(); ElMessage.success('已恢复招生'); } else { ElMessage.error(res.msg || '操作失败'); } });
  }
}
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }
onMounted(() => { getList(); loadDict('class_status'); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.text-danger { color: #f56c6c; font-weight: 600; }
</style>
