<!-- 积分礼品管理列表页 -->
<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="礼品名称">
        <el-input v-model="queryParams.keyword" placeholder="请输入礼品名称" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px" @change="handleQuery">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['points:gift:add']">
          <el-icon><Plus /></el-icon>新增礼品
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="selectedIds.length === 0" @click="handleBatchEnable" v-permission="['points:gift:edit']">
          批量启用
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain size="small" :disabled="selectedIds.length === 0" @click="handleBatchDisable" v-permission="['points:gift:edit']">
          批量禁用
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="70" />
      <el-table-column label="图片" align="center" width="80">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="row.imageUrl"
            :preview-src-list="[row.imageUrl]"
            style="width:50px;height:50px"
            fit="cover"
            :preview-teleported="true"
          />
          <span v-else class="text-muted">—</span>
        </template>
      </el-table-column>
      <el-table-column label="礼品名称" align="left" prop="name" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="兑换所需积分" align="center" width="120">
        <template #default="{ row }">{{ formatNumber(row.pointsRequired) }}</template>
      </el-table-column>
      <el-table-column label="已兑换数量" align="center" width="110">
        <template #default="{ row }">{{ row.redemptionCount ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="剩余数量" align="center" width="100">
        <template #default="{ row }">
          <StockRemainingTag :count="row.stockRemaining" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)" v-permission="['points:gift:edit']">
            编辑
          </el-button>
          <el-button
            v-if="row.status === 0"
            link type="success" size="small"
            @click="handleToggleStatus(row)"
            v-permission="['points:gift:edit']"
          >
            启用
          </el-button>
          <el-button
            v-if="row.status === 1"
            link type="warning" size="small"
            @click="handleToggleStatus(row)"
            v-permission="['points:gift:edit']"
          >
            禁用
          </el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['points:gift:delete']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空状态 -->
    <el-empty v-if="!loading && list.length === 0" description="暂无礼品数据，点击上方按钮新增" />

    <!-- 错误状态 -->
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.page"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/编辑弹窗 -->
    <GiftDialog
      v-model:visible="dialogOpen"
      :mode="dialogMode"
      :gift-id="editingGiftId"
      @submit-success="onDialogSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { listGift, updateGiftStatus, deleteGift } from '@/api/points/index';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import GiftDialog from './GiftDialog.vue';
import StockRemainingTag from '../components/StockRemainingTag.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);

const queryParams = ref({ page: 1, pageSize: 10, keyword: '', status: '' });

const dialogOpen = ref(false);
const dialogMode = ref('add');
const editingGiftId = ref(null);

let querySeq = 0;

/** 千分位格式化 */
function formatNumber(num) {
  if (num == null) return '-';
  return Number(num).toLocaleString();
}

/** 加载列表 */
function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { page: queryParams.value.page, pageSize: queryParams.value.pageSize };
  if (queryParams.value.keyword) params.keyword = queryParams.value.keyword;
  if (queryParams.value.status !== '') params.status = queryParams.value.status;

  listGift(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      list.value = res.data?.records || res.rows || [];
      total.value = res.data?.total || res.total || 0;
      error.value = '';
    } else {
      error.value = res.msg || '加载失败';
    }
  }).catch(() => {
    if (seq === querySeq) error.value = '网络错误，请检查网络后重试';
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

/** 搜索 */
function handleQuery() { queryParams.value.page = 1; getList(); }

/** 重置 */
function resetQuery() {
  queryParams.value = { page: 1, pageSize: 10, keyword: '', status: '' };
  getList();
}

/** 多选 */
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

/** 新增 */
function handleAdd() { dialogMode.value = 'add'; editingGiftId.value = null; dialogOpen.value = true; }

/** 编辑 */
function handleEdit(row) { dialogMode.value = 'edit'; editingGiftId.value = row.id; dialogOpen.value = true; }

/** 启用/禁用切换 */
function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1;
  const actionText = newStatus === 1 ? '启用' : '禁用';
  updateGiftStatus(row.id, newStatus).then(res => {
    if (res.success) {
      ElMessage.success(`${actionText}成功`);
      getList();
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  }).catch(() => { ElMessage.error('网络异常，请稍后重试'); });
}

/** 批量启用 */
function handleBatchEnable() {
  if (selectedIds.value.length === 0) return;
  Promise.allSettled(selectedIds.value.map(id => updateGiftStatus(id, 1))).then(results => {
    const ok = results.filter(r => r.status === 'fulfilled' && r.value?.success).length;
    const fail = results.length - ok;
    if (fail === 0) ElMessage.success(`已批量启用 ${ok} 条`);
    else ElMessage.warning(`启用成功 ${ok} 条，失败 ${fail} 条`);
    getList();
  });
}

/** 批量禁用 */
function handleBatchDisable() {
  if (selectedIds.value.length === 0) return;
  Promise.allSettled(selectedIds.value.map(id => updateGiftStatus(id, 0))).then(results => {
    const ok = results.filter(r => r.status === 'fulfilled' && r.value?.success).length;
    const fail = results.length - ok;
    if (fail === 0) ElMessage.success(`已批量禁用 ${ok} 条`);
    else ElMessage.warning(`禁用成功 ${ok} 条，失败 ${fail} 条`);
    getList();
  });
}

/** 删除 */
function handleDelete(row) {
  // 先查是否有待领取记录
  const msg = `确定删除礼品「${row.name}」吗？${(row.redemptionCount ?? 0) > 0 ? `已有 ${row.redemptionCount} 条兑换记录将保留。` : '删除后不可恢复。'}`;
  ElMessageBox.confirm(msg, '删除确认', { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' })
    .then(async () => {
      try {
        const res = await deleteGift(row.id);
        if (res.success) {
          ElMessage.success('删除成功');
          getList();
        } else {
          // 后端返回 GIFT_HAS_PENDING_REDEMPTION 时会走到这里
          ElMessage.error(res.msg || '删除失败');
        }
      } catch {
        ElMessage.error('网络异常，请稍后重试');
      }
    })
    .catch(() => {});
}

/** 弹窗提交成功 */
function onDialogSuccess() {
  dialogOpen.value = false;
  // 编辑保持当前页，新增跳第1页
  if (dialogMode.value === 'add') queryParams.value.page = 1;
  getList();
}

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.text-muted { color: #c0c4cc; }
.error-box { margin: 20px 0; }
</style>
