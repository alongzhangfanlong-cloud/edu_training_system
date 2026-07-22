<!-- 字典管理 -->
<template>
  <div class="page-container">
    <!-- 字典类型 -->
    <el-form :model="typeQueryParams" ref="typeQueryFormRef" size="small" :inline="true" v-show="typeShowSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="typeQueryParams.keyword" placeholder="请输入字典名称" clearable style="width:200px" @keyup.enter="handleTypeQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleTypeQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetTypeQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAddType" v-permission="['system:dict:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain size="small" @click="handleRefreshCache" v-permission="['system:dict:edit']">
          <el-icon><Refresh /></el-icon>刷新缓存
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="typeShowSearch" @queryTable="fetchTypeList" />
    </el-row>

    <el-table v-loading="typeLoading" :data="typeList" border stripe>
      <el-table-column label="字典编号" align="center" prop="id" width="100" />
      <el-table-column label="字典名称" align="center" prop="name" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="字典编码" align="center" prop="code" width="150" :show-overflow-tooltip="true" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleTypeStatusChange(row)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEditType(row)" v-permission="['system:dict:edit']">修改</el-button>
          <el-button link type="primary" size="small" @click="openDrawer(row)">字典数据</el-button>
          <el-button link type="primary" size="small" @click="handleDeleteType(row)" v-permission="['system:dict:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="typeTotal > 0" :total="typeTotal"
      v-model:page="typeQueryParams.page" v-model:limit="typeQueryParams.pageSize"
      @pagination="fetchTypeList"
    />

    <!-- 字典类型弹窗 -->
    <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="500px" destroy-on-close append-to-body>
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="80px">
        <el-form-item label="字典名称" prop="name">
          <el-input v-model="typeForm.name" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典编码" prop="code">
          <el-input v-model="typeForm.code" :disabled="!!typeEditId" placeholder="请输入字典编码" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="typeForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="typeForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTypeSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 字典数据 Drawer -->
    <DictDataDrawer v-model="drawerVisible" :type-code="drawerTypeCode" :type-name="drawerTypeName" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import { listType, addType, updateType, delType, refreshCache } from '@/api/system/dict';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictDataDrawer from './DictDataDrawer.vue';

// ========== 字典类型 ==========
const typeShowSearch = ref(true);
const typeLoading = ref(false);
const typeList = ref([]);
const typeTotal = ref(0);
const typeQueryParams = ref({ page: 1, pageSize: 10, keyword: '' });

const typeDialogVisible = ref(false);
const typeDialogTitle = ref('');
const typeFormRef = ref(null);
const typeEditId = ref(null);
const typeForm = ref({ name: '', code: '', status: 1, remark: '' });
const typeRules = {
  name: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入字典编码', trigger: 'blur' }],
};

function fetchTypeList() {
  typeLoading.value = true;
  const params = { ...typeQueryParams.value };
  if (!params.keyword) delete params.keyword;
  listType(params).then(res => {
    typeList.value = res.data?.records || res.rows || [];
    typeTotal.value = res.data?.total || res.total || 0;
  }).finally(() => { typeLoading.value = false; });
}

function handleTypeQuery() { typeQueryParams.value.page = 1; fetchTypeList(); }
function resetTypeQuery() {
  typeQueryParams.value = { page: 1, pageSize: 10, keyword: '' };
  fetchTypeList();
}

function handleAddType() {
  typeDialogTitle.value = '添加字典类型';
  typeEditId.value = null;
  typeForm.value = { name: '', code: '', status: 1, remark: '' };
  typeDialogVisible.value = true;
}

function handleEditType(row) {
  typeDialogTitle.value = '修改字典类型';
  typeEditId.value = row.id;
  typeForm.value = { name: row.name, code: row.code, status: row.status, remark: row.remark || '' };
  typeDialogVisible.value = true;
}

function handleTypeSubmit() {
  typeFormRef.value?.validate((valid) => {
    if (!valid) return;
    if (typeEditId.value) {
      updateType({ ...typeForm.value, id: typeEditId.value }).then(res => {
        if (res.success) { ElMessage.success('修改成功'); typeDialogVisible.value = false; fetchTypeList(); }
        else { ElMessage.error(res.msg || '操作失败'); }
      });
    } else {
      addType(typeForm.value).then(res => {
        if (res.success) { ElMessage.success('新增成功'); typeDialogVisible.value = false; fetchTypeList(); }
        else { ElMessage.error(res.msg || '操作失败'); }
      });
    }
  });
}

function handleDeleteType(row) {
  ElMessageBox.confirm('是否确认删除该字典类型？删除后会同时删除关联的字典数据。', '警告', { type: 'warning' })
    .then(() => delType(row.id))
    .then(res => {
      if (res.success) {
        fetchTypeList();
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    })
    .catch(() => {});
}

function handleTypeStatusChange(row) {
  const text = row.status === 1 ? '启用' : '停用';
  ElMessageBox.confirm(`确认要${text}该字典类型吗？`, '提示', { type: 'warning' })
    .then(() => updateType({ id: row.id, status: row.status }))
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

function handleRefreshCache() {
  refreshCache().then(res => {
    if (res.success) {
      const dictsStore = useDictsStore();
      dictsStore.clearCache();
      ElMessage.success('缓存刷新成功');
    } else {
      ElMessage.error(res.msg || '刷新失败');
    }
  });
}

// ========== 字典数据 Drawer ==========
const drawerVisible = ref(false);
const drawerTypeCode = ref('');
const drawerTypeName = ref('');

function openDrawer(row) {
  drawerTypeCode.value = row.code;
  drawerTypeName.value = row.name;
  drawerVisible.value = true;
}

onMounted(() => {
  fetchTypeList();
});
</script>

<style scoped lang="less">
.page-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
