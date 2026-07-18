<!-- 参数配置 -->
<template>
  <div class="page-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="参数名称" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="请输入参数名称" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="参数键名" prop="configType">
        <el-input v-model="queryParams.configType" placeholder="请输入参数键名" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="系统内置" prop="sysBuiltIn">
        <el-select v-model="queryParams.sysBuiltIn" placeholder="是否内置" clearable style="width:200px">
          <el-option v-for="d in dicts.sys_yes_no" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['system:config:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['system:config:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain size="small" @click="handleRefreshCache" v-permission="['system:config:edit']">
          <el-icon><Refresh /></el-icon>刷新缓存
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="参数编号" align="center" prop="id" width="80" />
      <el-table-column label="参数名称" align="center" prop="name" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="参数键名" align="center" prop="key" width="200" :show-overflow-tooltip="true" />
      <el-table-column label="参数键值" align="center" prop="value" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="系统内置" align="center" width="80">
        <template #default="{ row }">
          <el-tag :type="row.type === 'Y' ? 'primary' : 'info'" size="small">
            {{ row.type === 'Y' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['system:config:edit']">修改</el-button>
          <el-button link type="primary" size="small" :disabled="row.type === 'Y'" @click="handleDelete(row)" v-permission="['system:config:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="open" :title="title" width="500px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="参数名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="key">
          <el-input v-model="form.key" :disabled="!!editId" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数键值" prop="value">
          <el-input v-model="form.value" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="Y">是</el-radio>
            <el-radio value="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue';
import { listConfig, addConfig, updateConfig, delConfig, refreshCache } from '@/api/system/config';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';

const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const configList = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '', configType: '', sysBuiltIn: '' });

const open = ref(false);
const title = ref('');
const formRef = ref(null);
const editId = ref(null);
const form = ref({ name: '', key: '', value: '', type: 'Y', remark: '' });
const rules = {
  name: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  key: [{ required: true, message: '请输入参数键名', trigger: 'blur' }],
  value: [{ required: true, message: '请输入参数键值', trigger: 'blur' }],
};

function getList() {
  loading.value = true;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  if (params.configType === '' || params.configType === null) delete params.configType;
  if (params.sysBuiltIn === '' || params.sysBuiltIn === null) delete params.sysBuiltIn;
  listConfig(params).then(res => {
    configList.value = res.data?.records || res.rows || [];
    total.value = res.data?.total || res.total || 0;
  }).finally(() => { loading.value = false; });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() {
  queryParams.value = { page: 1, pageSize: 10, keyword: '', configType: '', sysBuiltIn: '' };
  getList();
}

function handleAdd() {
  title.value = '添加参数配置';
  editId.value = null;
  form.value = { name: '', key: '', value: '', type: 'Y', remark: '' };
  open.value = true;
}

function handleUpdate(row) {
  title.value = '修改参数配置';
  editId.value = row.id;
  form.value = {
    name: row.name,
    key: row.key || row.configKey,
    value: row.value || row.configValue,
    type: row.type,
    remark: row.remark || '',
  };
  open.value = true;
}

function submitForm() {
  formRef.value?.validate((valid) => {
    if (!valid) return;
    if (editId.value) {
      updateConfig({ ...form.value, id: editId.value }).then(res => {
        if (res.success) { ElMessage.success('修改成功'); open.value = false; getList(); }
        else { ElMessage.error(res.msg || '操作失败'); }
      });
    } else {
      addConfig(form.value).then(res => {
        if (res.success) { ElMessage.success('新增成功'); open.value = false; getList(); }
        else { ElMessage.error(res.msg || '操作失败'); }
      });
    }
  });
}

function handleDelete(row) {
  const configIds = row ? [row.id] : selectedIds.value;
  if (!configIds || configIds.length === 0) { ElMessage.warning('请选择要删除的参数配置'); return; }
  if (row && row.type === 'Y') { ElMessage.warning('系统内置参数不能删除'); return; }
  if (!row) {
    const hasBuiltin = configList.value.some(item => selectedIds.value.includes(item.id) && item.type === 'Y');
    if (hasBuiltin) { ElMessage.warning('选中的参数中包含系统内置参数，不能删除'); return; }
  }
  ElMessageBox.confirm('是否确认删除选中的参数配置？', '警告', { type: 'warning' })
    .then(() => delConfig(configIds.join(',')))
    .then(res => {
      if (res.success) {
        getList();
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    })
    .catch(() => {});
}

function handleRefreshCache() {
  refreshCache().then(res => {
    if (res.success) {
      ElMessage.success('缓存刷新成功');
    } else {
      ElMessage.error(res.msg || '缓存刷新失败');
    }
  });
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

onMounted(() => {
  getList();
  loadDict('sys_yes_no');
});
</script>

<style scoped lang="less">
.page-container { padding: 16px; }
.table-container { margin-top: 12px; }
.mb8 { margin-bottom: 8px; }
</style>
