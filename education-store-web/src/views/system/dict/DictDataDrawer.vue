<!-- 字典数据 Drawer — 在字典类型 Tab 中展示某类型的字典数据列表 -->
<template>
  <el-drawer v-model="visible" :title="typeName + ' — 字典数据'" size="65%" destroy-on-close>
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="请输入字典标签" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['system:dict:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="fetchList" />
    </el-row>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="字典编号" align="center" prop="id" width="88" />
      <el-table-column label="字典标签" align="center" prop="label" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="字典键值" align="center" prop="value" width="120" />
      <el-table-column label="排序" align="center" prop="sort" width="80" />
      <el-table-column label="默认" align="center" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isDefault === 1 ? 'success' : 'info'" size="small">
            {{ row.isDefault === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="样式" align="center" prop="cssClass" width="100" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)" v-permission="['system:dict:edit']">修改</el-button>
          <el-button link type="primary" size="small" @click="handleDelete(row)" v-permission="['system:dict:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="fetchList"
    />

    <!-- 编辑弹窗 -->
    <DictDataDialog v-model="dialogVisible" :type-code="typeCode" :data="editData" @saved="onSaved" />
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { listData, delData, updateData } from '@/api/system/dict';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictDataDialog from './DictDataDialog.vue';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  typeCode: { type: String, default: '' },
  typeName: { type: String, default: '' },
});

const emit = defineEmits(['update:modelValue', 'saved']);

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
});

// draw 打开时自动加载数据
watch(visible, (val) => {
  if (val) {
    queryParams.value = { page: 1, pageSize: 10, keyword: '' };
    fetchList();
  }
});

// ========== 列表 ==========
const showSearch = ref(true);
const loading = ref(false);
const list = ref([]);
const total = ref(0);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '' });

function fetchList() {
  loading.value = true;
  const params = { ...queryParams.value, typeCode: props.typeCode };
  if (!params.keyword) delete params.keyword;
  listData(params).then(res => {
    list.value = res.data?.records || res.rows || [];
    total.value = res.data?.total || res.total || 0;
  }).finally(() => { loading.value = false; });
}

function handleQuery() { queryParams.value.page = 1; fetchList(); }
function resetQuery() {
  queryParams.value = { page: 1, pageSize: 10, keyword: '' };
  fetchList();
}

// ========== 编辑弹窗 ==========
const dialogVisible = ref(false);
const editData = ref(null);

function handleAdd() {
  editData.value = null;
  dialogVisible.value = true;
}

function handleEdit(row) {
  editData.value = row;
  dialogVisible.value = true;
}

function onSaved() {
  fetchList();
  emit('saved');
}

// ========== 删除 & 状态 ==========
function handleDelete(row) {
  ElMessageBox.confirm('是否确认删除该字典数据？', '警告', { type: 'warning' })
    .then(() => delData(row.id))
    .then(res => {
      if (res.success) {
        fetchList();
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    })
    .catch(() => {});
}

function handleStatusChange(row) {
  const text = row.status === 1 ? '启用' : '停用';
  ElMessageBox.confirm(`确认要${text}该字典数据吗？`, '提示', { type: 'warning' })
    .then(() => updateData({ id: row.id, status: row.status }))
    .then(res => {
      if (res.success) {
        ElMessage.success(`${text}成功`);
      } else {
        ElMessage.error(res.msg || `${text}失败`);
        row.status = row.status === 1 ? 0 : 1;
      }
    })
    .catch(() => { row.status = row.status === 1 ? 0 : 1; });
}

// ========== 初始化 ==========
// expose fetchList so parent can trigger initial load
defineExpose({ fetchList });
</script>

<style scoped lang="less">
.mb8 { margin-bottom: 8px; }
</style>
