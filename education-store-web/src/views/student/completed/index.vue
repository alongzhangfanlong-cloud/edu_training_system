<!-- 结业学员管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="学员姓名/手机号" clearable style="width:200px" @keyup.enter="handleQuery" />
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
        <el-button type="warning" plain size="small" :disabled="single" @click="handleReactivate(null)" v-permission="['student:enrolled:edit']">
          <el-icon><RefreshLeft /></el-icon>重新激活
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="studentList" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="学员姓名" align="center" prop="name" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="联系电话" align="center" prop="mobile" width="120" />
      <el-table-column label="性别" align="center" width="60">
        <template #default="{ row }">
          <dict-tag :options="dicts.sys_user_sex" :value="row.gender" />
        </template>
      </el-table-column>
      <el-table-column label="年级" align="center" prop="grade" width="80" />
      <el-table-column label="顾问" align="center" prop="counselorName" width="80" />
      <el-table-column label="总课时" align="center" prop="totalHours" width="80" />
      <el-table-column label="剩余课时" align="center" prop="remainingHours" width="80" />
      <el-table-column label="结业时间" align="center" prop="completedAt" width="160" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="操作" align="center" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="warning" size="small" @click="handleReactivate(row)" v-permission="['student:enrolled:edit']">重新激活</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, RefreshLeft } from '@element-plus/icons-vue';
import { listStudent, changeStudentStatus } from '@/api/student/students';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';

const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const studentList = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '', grade: '', status: 'COMPLETED' });

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
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
  queryParams.value = { page: 1, pageSize: 10, keyword: '', grade: '', status: 'COMPLETED' };
  getList();
}

function handleReactivate(row) {
  const id = row ? row.id : selectedIds.value[0];
  if (!id) { ElMessage.warning('请选择要重新激活的学员'); return; }
  ElMessageBox.confirm('确认将该学员重新激活为在学状态？', '提示', { type: 'warning' })
    .then(() => changeStudentStatus(id, 'ENROLLED'))
    .then(res => {
      if (res.success) { getList(); ElMessage.success('激活成功'); }
      else { ElMessage.error(res.msg || '激活失败'); }
    }).catch(() => {});
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

onMounted(() => { getList(); loadDict('sys_user_sex'); });
</script>

<style scoped lang="less">
.app-container {
  padding: 16px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>
