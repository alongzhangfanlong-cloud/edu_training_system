<!-- 在学学员管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="学员姓名/手机号" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="学员状态" clearable style="width:200px">
          <el-option v-for="d in dicts.student_status" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="年级" prop="grade">
        <el-input v-model="queryParams.grade" placeholder="请输入年级" clearable style="width:200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['student:enrolled:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['student:enrolled:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['student:enrolled:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain size="small" :disabled="single" @click="handleGraduate(null)" v-permission="['student:enrolled:edit']">
          <el-icon><Finished /></el-icon>结业
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="studentList" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="学员姓名" align="center" prop="name" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="联系电话" align="center" prop="mobile" width="120" />
      <el-table-column label="顾问" align="center" prop="counselorName" :show-overflow-tooltip="true" width="80">
        <template #default="{ row }">{{ row.counselorName || '-' }}</template>
      </el-table-column>
      <el-table-column label="性别" align="center" prop="gender" width="60">
        <template #default="{ row }">
          <span>{{ row.gender === 'M' ? '男' : row.gender === 'F' ? '女' : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="年龄" align="center" width="60">
        <template #default="{ row }">
          <span>{{ row.birthDate ? Math.floor((Date.now() - new Date(row.birthDate).getTime()) / 31557600000) : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="年级" align="center" prop="grade" width="80" />
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.student_status" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['student:enrolled:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['student:enrolled:delete']">删除</el-button>
          <el-dropdown @command="(cmd) => handleCommand(cmd, row)">
            <el-button link type="primary" size="small">更多<el-icon><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="graduate">结业</el-dropdown-item>
                <el-dropdown-item command="parent">家长管理</el-dropdown-item>
                <el-dropdown-item command="hours">课时记录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/编辑弹窗 -->
    <StudentFormModal v-model:visible="open" :title="title" :student-id="currentStudentId" @submit-success="getList" />

    <!-- 家长管理弹窗 -->
    <ParentManageModal v-model:visible="parentOpen" :student-id="currentStudentId" :student-name="currentStudentName" />

    <!-- 课时记录弹窗 -->
    <HoursLogModal v-model:visible="hoursOpen" :student-id="currentStudentId" :student-name="currentStudentName" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete, ArrowDown, Finished } from '@element-plus/icons-vue';
import { listStudent, delStudent, changeStudentStatus } from '@/api/student/students';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';
import StudentFormModal from './components/StudentFormModal.vue';
import ParentManageModal from './components/ParentManageModal.vue';
import HoursLogModal from './components/HoursLogModal.vue';

const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const studentList = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '', status: '', grade: '' });

const open = ref(false);
const title = ref('');
const currentStudentId = ref(null);
const currentStudentName = ref('');
const parentOpen = ref(false);
const hoursOpen = ref(false);

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  if (!params.status) delete params.status;
  if (!params.grade) delete params.grade;
  listStudent(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      studentList.value = res.data?.records || res.rows || [];
      total.value = res.data?.total || res.total || 0;
    } else {
      ElMessage.error(res.msg || '查询失败');
    }
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() {
  queryParams.value = { page: 1, pageSize: 10, keyword: '', status: '', grade: '' };
  getList();
}

function handleAdd() { title.value = '新增学员'; currentStudentId.value = null; open.value = true; }
function handleUpdate(row) {
  title.value = '修改学员';
  currentStudentId.value = row ? row.id : selectedIds.value[0];
  open.value = true;
}
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择要删除的学员'); return; }
  ElMessageBox.confirm('是否确认删除选中的学员？', '警告', { type: 'warning' })
    .then(() => delStudent(ids.join(',')))
    .then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => {});
}
function handleGraduate(row) {
  const id = row ? row.id : selectedIds.value[0];
  if (!id) { ElMessage.warning('请选择要结业的学员'); return; }
  ElMessageBox.confirm('确认将该学员标记为结业？结业后不可恢复为在学状态。', '警告', { type: 'warning' })
    .then(() => changeStudentStatus(id, 'CLOSED'))
    .then(res => {
      if (res.success) { getList(); ElMessage.success('结业成功'); }
      else { ElMessage.error(res.msg || '结业失败'); }
    }).catch(() => {});
}

function handleCommand(command, row) {
  currentStudentId.value = row.id;
  currentStudentName.value = row.name;
  if (command === 'graduate') {
    handleGraduate(row);
  } else if (command === 'parent') {
    parentOpen.value = true;
  } else if (command === 'hours') {
    hoursOpen.value = true;
  }
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

onMounted(() => { getList(); loadDict('student_status'); });
</script>

<style scoped lang="less">
.app-container {
  padding: 16px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>
