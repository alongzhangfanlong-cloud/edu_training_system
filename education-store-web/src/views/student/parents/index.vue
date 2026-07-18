<!-- 家长账号管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="家长姓名/手机号/关联学生" clearable style="width:220px" @keyup.enter="handleQuery" />
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
      <el-table-column label="家长姓名" align="center" prop="parentName" :show-overflow-tooltip="true" min-width="90" />
      <el-table-column label="亲属关系" align="center" prop="parentRelation" width="90" />
      <el-table-column label="联系电话" align="center" prop="mobile" width="120" />
      <el-table-column label="家长手机号" align="center" prop="parentMobile" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleViewParents(row)">查看家长</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 查看家长详情弹窗（复用 ParentManageModal） -->
    <ParentManageModal v-model:visible="parentOpen" :student-id="currentStudentId" :student-name="currentStudentName" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listStudent } from '@/api/student/students';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import ParentManageModal from '../enrolled/components/ParentManageModal.vue';

const loading = ref(false);
const showSearch = ref(true);
const studentList = ref([]);
const total = ref(0);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '' });

const parentOpen = ref(false);
const currentStudentId = ref(null);
const currentStudentName = ref('');

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
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
  queryParams.value = { page: 1, pageSize: 10, keyword: '' };
  getList();
}

function handleViewParents(row) {
  currentStudentId.value = row.id;
  currentStudentName.value = row.name;
  parentOpen.value = true;
}

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container {
  padding: 16px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>
