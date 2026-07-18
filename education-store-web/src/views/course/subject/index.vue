<!-- 科目管理（含行内编辑） -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="科目名称" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['course:subject:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['course:subject:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 行内编辑表格：新增行在顶部 -->
    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="科目名称" align="center" prop="name" min-width="150">
        <template #default="{ row }">
          <template v-if="editingId === row.id">
            <el-input v-model="editForm.name" size="small" placeholder="科目名称" @blur="saveInline(row)" @keyup.enter="saveInline(row)" />
          </template>
          <template v-else>
            <span>{{ row.name }}</span>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="排序号" align="center" prop="sortOrder" width="100">
        <template #default="{ row }">
          <template v-if="editingId === row.id">
            <el-input-number v-model="editForm.sortOrder" :min="0" size="small" style="width:100%" @blur="saveInline(row)" />
          </template>
          <template v-else>
            <el-input-number v-model="row.sortOrder" :min="0" size="small" controls-position="right" style="width:100%"
              @change="(val) => quickUpdateSort(row, val)" @blur="(e) => { if (e?.target?.value !== undefined) quickUpdateSort(row, Number(e.target.value)); }" />
          </template>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template #default="{ row }">
          <template v-if="editingId === row.id">
            <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" size="small" @change="saveInline(row)" />
          </template>
          <template v-else>
            <dict-tag :options="dicts.sys_normal_disable" :value="String(row.status)" />
          </template>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center" prop="description" min-width="150">
        <template #default="{ row }">
          <template v-if="editingId === row.id">
            <el-input v-model="editForm.description" size="small" placeholder="描述" @blur="saveInline(row)" />
          </template>
          <template v-else>
            <span :title="row.description">{{ row.description || '-' }}</span>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="{ row }">
          <template v-if="editingId === row.id">
            <el-button link type="success" size="small" @click="saveInline(row)">保存</el-button>
            <el-button link type="info" size="small" @click="cancelEdit">取消</el-button>
          </template>
          <template v-else-if="row._isNew">
            <el-button link type="success" size="small" @click="startEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="cancelNew(row)">取消</el-button>
          </template>
          <template v-else>
            <el-button link type="primary" size="small" @click="startEdit(row)" v-permission="['course:subject:edit']">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['course:subject:delete']">删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无科目数据" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <!-- 传统弹窗保留用于复杂编辑场景（可选） -->
    <el-dialog :model-value="open" :title="title" width="500px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => open = val">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="科目名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科目名称" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="d in dicts.sys_normal_disable" :key="d.value" :value="Number(d.value)">{{ d.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue';
import { listSubject, getSubject, addSubject, updateSubject, delSubject } from '@/api/course/subjects';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';

const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const error = ref('');
const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, size: 10, keyword: '' });

// 行内编辑
const editingId = ref(null);
const editForm = ref({ name: '', sortOrder: 0, status: 1, description: '' });
const editingBefore = ref(null); // 进入编辑前的快照，用于取消

// 传统弹窗（保留兼容）
const open = ref(false);
const title = ref('');
const currentId = ref(null);
const formRef = ref(null);
const submitting = ref(false);
const form = ref({ name: '', sortOrder: 0, status: 1, description: '' });
const rules = { name: [{ required: true, message: '请输入科目名称', trigger: 'blur' }] };

let querySeq = 0;
let _tempIdCounter = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  listSubject(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '' }; getList(); }

// ====== 行内编辑 ======
function startEdit(row) {
  if (editingId.value && editingId.value !== row.id) {
    ElMessage.warning('请先完成当前编辑');
    return;
  }
  editingId.value = row.id;
  editingBefore.value = { name: row.name, sortOrder: row.sortOrder, status: row.status, description: row.description };
  editForm.value = { name: row.name, sortOrder: row.sortOrder, status: row.status ?? 1, description: row.description || '' };
}

function cancelEdit() {
  if (editingBefore.value) {
    const row = list.value.find(r => r.id === editingId.value);
    if (row && !row._isNew) {
      Object.assign(row, editingBefore.value);
    }
  }
  editingId.value = null;
  editingBefore.value = null;
}

async function saveInline(row) {
  if (!editForm.value.name?.trim()) {
    ElMessage.warning('科目名称不能为空');
    return;
  }
  const data = { ...editForm.value };
  let res;
  if (row._isNew) {
    res = await addSubject(data);
    if (res.success) {
      ElMessage.success('新增成功');
      editingId.value = null;
      getList();
    } else {
      ElMessage.error(res.msg || '新增失败');
    }
  } else {
    data.id = row.id;
    res = await updateSubject(data);
    if (res.success) {
      ElMessage.success('修改成功');
      Object.assign(row, { name: data.name, sortOrder: data.sortOrder, status: data.status, description: data.description });
      editingId.value = null;
      editingBefore.value = null;
    } else {
      ElMessage.error(res.msg || '修改失败');
    }
  }
}

// 排序号快速更新（失焦自动保存）
async function quickUpdateSort(row, val) {
  if (val === undefined || val === null || val === row._prevSort) return;
  row._prevSort = val;
  try {
    const res = await updateSubject({ id: row.id, sortOrder: val });
    if (res.success) {
      ElMessage.success('排序已更新');
    } else {
      ElMessage.error(res.msg || '更新失败');
    }
  } catch {
    // 静默处理
  }
}

// ====== 新增：顶部插入行 ======
function handleAdd() {
  if (editingId.value) {
    ElMessage.warning('请先完成当前编辑');
    return;
  }
  _tempIdCounter++;
  const newRow = {
    id: -_tempIdCounter, // 负数临时ID
    _isNew: true,
    name: '',
    sortOrder: 0,
    status: 1,
    description: '',
    createTime: '',
  };
  list.value.unshift(newRow); // 顶部插入
  startEdit(newRow);
}

function cancelNew(row) {
  const idx = list.value.findIndex(r => r.id === row.id);
  if (idx >= 0) list.value.splice(idx, 1);
  editingId.value = null;
  editingBefore.value = null;
}

// ====== 传统弹窗（保留兼容） ======
function handleUpdate(row) { title.value = '修改科目'; currentId.value = row.id; open.value = true; }
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择科目'); return; }
  ElMessageBox.confirm('确认删除选中的科目？删除前请确保无课程引用。', '警告', { type: 'warning' })
    .then(() => delSubject(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    });
}
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

async function onOpen() {
  reset(); await loadDict('sys_normal_disable');
  if (currentId.value) {
    const res = await getSubject(currentId.value);
    if (res.success && res.data) {
      const s = res.data.data || res.data;
      form.value = { name: s.name || '', sortOrder: s.sortOrder || 0, status: s.status ?? 1, description: s.description || '' };
    }
  }
}
function reset() { form.value = { name: '', sortOrder: 0, status: 1, description: '' }; nextTick(() => formRef.value?.clearValidate()); }

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value }; let res;
    if (currentId.value) { data.id = currentId.value; res = await updateSubject(data); }
    else { res = await addSubject(data); }
    if (res.success) { ElMessage.success(currentId.value ? '修改成功' : '新增成功'); open.value = false; getList(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}

onMounted(() => { getList(); loadDict('sys_normal_disable'); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
