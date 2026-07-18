<!-- Tab2: 收费管理 -->
<template>
  <div>
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="学员">
        <el-select v-model="queryParams.studentId" filterable remote :remote-method="searchStudents" :loading="studentLoading" placeholder="请搜索学员" clearable style="width:180px">
          <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:130px">
          <el-option label="已收费" value="PAID" />
          <el-option label="已退费" value="REFUNDED" />
          <el-option label="部分退费" value="PARTIAL_REFUNDED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['tuition:fee:add']">
          <el-icon><Plus /></el-icon>确认收费
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['tuition:fee:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['tuition:fee:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="序号" align="center" width="60" type="index" />
      <el-table-column label="收费编号" align="center" prop="feeNo" width="150" />
      <el-table-column label="学员" align="center" prop="studentName" :show-overflow-tooltip="true" width="100" />
      <el-table-column label="课程" align="center" prop="courseName" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="班级" align="center" prop="className" :show-overflow-tooltip="true" width="100" />
      <el-table-column label="费用类型" align="center" width="90">
        <template #default="{ row }">{{ feeTypeLabel(row.feeType) }}</template>
      </el-table-column>
      <el-table-column label="应缴金额" align="right" prop="amountReceivable" width="110" />
      <el-table-column label="实缴金额" align="right" prop="amountPaid" width="110" />
      <el-table-column label="支付方式" align="center" width="90">
        <template #default="{ row }">{{ payMethodLabel(row.paymentMethod) }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收费时间" align="center" prop="payTime" width="160" />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['tuition:fee:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['tuition:fee:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无收费记录" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <TuitionFeeFormModal v-model:visible="open" :title="title" :fee-id="currentId" @submit-success="getList" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { listFee, delFee } from '@/api/finance/tuitionFees';
import { listStudent } from '@/api/student/students';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import TuitionFeeFormModal from './TuitionFeeFormModal.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, studentId: '', status: '' });
const studentOptions = ref([]);
const studentLoading = ref(false);
const open = ref(false);
const title = ref('');
const currentId = ref(null);

let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.studentId) params.studentId = queryParams.value.studentId;
  if (queryParams.value.status) params.status = queryParams.value.status;
  listFee(params).then(res => {
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

function feeTypeLabel(v) { const m = { REGISTRATION: '报名费', TUITION: '课时费', MATERIAL: '教材费', OTHER: '其他' }; return m[v] || v; }
function payMethodLabel(v) { const m = { CASH: '现金', WECHAT: '微信', ALIPAY: '支付宝', BANK_TRANSFER: '银行转账' }; return m[v] || v; }
function statusLabel(v) { const m = { PAID: '已收费', REFUNDED: '已退费', PARTIAL_REFUNDED: '部分退费' }; return m[v] || v; }
function statusType(v) { const m = { PAID: 'success', REFUNDED: 'danger', PARTIAL_REFUNDED: 'warning' }; return m[v] || ''; }

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, pageSize: 10, studentId: '', status: '' }; getList(); }
function handleAdd() { title.value = '确认收费'; currentId.value = null; open.value = true; }
function handleUpdate(row) { title.value = '修改收费'; currentId.value = row ? row.id : selectedIds.value[0]; open.value = true; }

function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择记录'); return; }
  ElMessageBox.confirm('确认删除选中的收费记录？', '警告', { type: 'warning' })
    .then(() => delFee(ids.join(','))).then(res => {
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
