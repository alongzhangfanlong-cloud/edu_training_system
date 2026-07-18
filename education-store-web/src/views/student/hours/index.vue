<!-- 课时汇总 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="学员姓名" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="请输入学员姓名" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="课程" prop="courseKeyword">
        <el-input v-model="queryParams.courseKeyword" placeholder="请输入课程名称" clearable style="width:200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="studentList" border stripe>
      <el-table-column label="学员姓名" align="center" prop="name" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="联系电话" align="center" prop="mobile" width="120" />
      <el-table-column label="总课时" align="center" prop="totalHours" width="100" />
      <el-table-column label="已消耗" align="center" width="100">
        <template #default="{ row }">
          <span>{{ calcConsumed(row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余课时" align="center" width="100">
        <template #default="{ row }">
          <el-tag :type="getRemainingType(row)">{{ row.remainingHours ?? '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最近到校" align="center" prop="lastVisitTime" width="160" />
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.student_status" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleViewLog(row)">课时记录</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 课时记录弹窗（复用） -->
    <HoursLogModal v-model:visible="hoursOpen" :student-id="currentStudentId" :student-name="currentStudentName" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listStudent } from '@/api/student/students';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';
import HoursLogModal from '../enrolled/components/HoursLogModal.vue';

const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const studentList = ref([]);
const total = ref(0);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '', courseKeyword: '' });

const hoursOpen = ref(false);
const currentStudentId = ref(null);
const currentStudentName = ref('');

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  if (!params.courseKeyword) delete params.courseKeyword;
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

function calcConsumed(row) {
  if (row.totalHours !== null && row.totalHours !== undefined && row.remainingHours !== null && row.remainingHours !== undefined) {
    return (Number(row.totalHours) - Number(row.remainingHours)).toFixed(1);
  }
  return '-';
}

function getRemainingType(row) {
  if (row.remainingHours === null || row.remainingHours === undefined) return 'info';
  const val = Number(row.remainingHours);
  if (val <= 0) return 'danger';
  if (val < 5) return 'warning';
  return 'success';
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() {
  queryParams.value = { page: 1, pageSize: 10, keyword: '', courseKeyword: '' };
  getList();
}

function handleViewLog(row) {
  currentStudentId.value = row.id;
  currentStudentName.value = row.name;
  hoursOpen.value = true;
}

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
