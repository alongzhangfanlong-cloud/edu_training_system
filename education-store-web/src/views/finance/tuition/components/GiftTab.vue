<!-- Tab4: 赠送课时 -->
<template>
  <div>
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="学员">
        <el-select v-model="queryParams.studentId" filterable remote :remote-method="searchStudents" :loading="studentLoading" placeholder="请搜索学员" clearable style="width:180px">
          <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['tuition:gift:add']">
          <el-icon><Plus /></el-icon>赠送课时
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['tuition:gift:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="序号" align="center" width="60" type="index" />
      <el-table-column label="赠送编号" align="center" prop="giftNo" width="150" />
      <el-table-column label="学员" align="center" prop="studentId" width="80" />
      <el-table-column label="课程" align="center" prop="courseId" width="80" />
      <el-table-column label="赠送课时" align="center" width="100">
        <template #default="{ row }"><span style="color:#409eff;font-weight:bold">+{{ row.hours }} 课时</span></template>
      </el-table-column>
      <el-table-column label="赠送原因" align="center" width="100">
        <template #default="{ row }">{{ giftReasonLabel(row.reason) }}</template>
      </el-table-column>
      <el-table-column label="经手人" align="center" prop="operatorId" width="80" />
      <el-table-column label="赠送时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['tuition:gift:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无赠送课时记录" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <GiftFormModal v-model:visible="open" title="赠送课时" @submit-success="getList" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue';
import { listGift, delGift } from '@/api/finance/tuitionGifts';
import { listStudent } from '@/api/student/students';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import GiftFormModal from './GiftFormModal.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, studentId: '' });
const studentOptions = ref([]);
const studentLoading = ref(false);
const open = ref(false);

let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.studentId) params.studentId = queryParams.value.studentId;
  listGift(params).then(res => {
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

function giftReasonLabel(v) { const m = { PROMOTION: '促销活动', COMPENSATION: '补偿', OTHER: '其他' }; return m[v] || v; }

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, pageSize: 10, studentId: '' }; getList(); }
function handleAdd() { open.value = true; }

function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择记录'); return; }
  ElMessageBox.confirm('确认删除选中的赠送记录？删除后课时扣回可能影响学员数据。', '警告', { type: 'warning' })
    .then(() => delGift(ids.join(','))).then(res => {
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
