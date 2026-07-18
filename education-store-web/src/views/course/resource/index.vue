<!-- 课件管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="课件名称" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="资源类型" clearable style="width:150px">
          <el-option v-for="d in dicts.resource_type" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['course:resource:add']">
          <el-icon><Plus /></el-icon>上传课件
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['course:resource:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="课件名称" align="center" prop="name" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="资源类型" align="center" prop="resourceType" width="100">
        <template #default="{ row }">
          <dict-tag :options="dicts.resource_type" :value="row.resourceType" />
        </template>
      </el-table-column>
      <el-table-column label="文件大小" align="center" width="90">
        <template #default="{ row }">
          <span>{{ formatSize(row.fileSize) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下载次数" align="center" prop="downloadCount" width="90" />
      <el-table-column label="共享" align="center" prop="isPublic" width="70">
        <template #default="{ row }">
          <dict-tag :options="dicts.sys_normal_disable" :value="String(row.isPublic)" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <dict-tag :options="dicts.sys_normal_disable" :value="String(row.status)" />
        </template>
      </el-table-column>
      <el-table-column label="上传时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['course:resource:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['course:resource:delete']">删除</el-button>
          <el-button link type="success" size="small" @click="handleDownload(row)">下载</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <!-- 上传/编辑弹窗 -->
    <el-dialog :model-value="open" :title="title" width="550px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => open = val">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课件名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入课件名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资源类型" prop="resourceType">
              <el-select v-model="form.resourceType" style="width:100%">
                <el-option v-for="d in dicts.resource_type" :key="d.value" :label="d.label" :value="d.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用年级">
              <el-input v-model="form.grade" placeholder="如：三年级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="上传文件" v-if="!currentId">
          <el-upload ref="uploadRef" :before-upload="handleBeforeUpload" :http-request="handleUploadHttp" :show-file-list="true" :limit="1" accept="*">
            <el-button type="primary" size="small">选择文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="当前文件" v-if="currentId && form.fileName">
          <span>{{ form.fileName }} ({{ formatSize(form.fileSize) }})</span>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="共享范围">
              <el-radio-group v-model="form.isPublic">
                <el-radio :value="1">公开</el-radio>
                <el-radio :value="0">私有</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
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
import { listResource, getResource, addResource, updateResource, delResource, recordDownload } from '@/api/course/resources';
import { uploadFile } from '@/api/common/file';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';

const { dicts, loadDicts } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, size: 10, keyword: '', type: '' });

const open = ref(false);
const title = ref('');
const currentId = ref(null);
const formRef = ref(null);
const uploadRef = ref(null);
const submitting = ref(false);
const form = ref({
  name: '', resourceType: '', grade: '', fileName: '', filePath: '', fileUrl: '',
  fileSize: null, fileType: '', isPublic: 1, status: 1, description: '',
});
const rules = {
  name: [{ required: true, message: '请输入课件名称', trigger: 'blur' }],
  resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
};

let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  if (!params.type) delete params.type;
  listResource(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; }
  }).finally(() => { if (seq === querySeq) loading.value = false; });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '', type: '' }; getList(); }

function handleAdd() { title.value = '上传课件'; currentId.value = null; open.value = true; }
function handleUpdate(row) { title.value = '编辑课件'; currentId.value = row.id; open.value = true; }
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择课件'); return; }
  ElMessageBox.confirm('确认删除选中的课件？', '警告', { type: 'warning' })
    .then(() => delResource(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    });
}

function handleDownload(row) {
  recordDownload(row.id).then(() => {
    if (row.fileUrl) { window.open(row.fileUrl, '_blank'); getList(); }
  });
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

function formatSize(bytes) {
  if (!bytes) return '-';
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / 1048576).toFixed(1) + ' MB';
}

function handleBeforeUpload(file) {
  const maxSize = 100 * 1024 * 1024; // 100MB
  if (file.size > maxSize) { ElMessage.error('文件大小不能超过100MB'); return false; }
  return true;
}

async function handleUploadHttp(options) {
  const fd = new FormData();
  fd.append('file', options.file);
  try {
    const res = await uploadFile(fd);
    if (res.success && res.data) {
      form.value.fileName = res.data.fileName || options.file.name;
      form.value.filePath = res.data.filePath || '';
      form.value.fileUrl = res.data.filePath || '';
      form.value.fileSize = res.data.fileSize || options.file.size;
      form.value.fileType = res.data.fileType || '';
      ElMessage.success('文件上传成功');
    } else {
      ElMessage.error(res.msg || '上传失败');
    }
  } catch { ElMessage.error('上传失败'); }
}

async function onOpen() {
  reset();
  await loadDicts(['sys_normal_disable', 'resource_type']);
  if (currentId.value) {
    const res = await getResource(currentId.value);
    if (res.success && res.data) {
      const r = res.data.data || res.data;
      form.value = {
        name: r.name || '', resourceType: r.resourceType || '', grade: r.grade || '',
        fileName: r.fileName || '', filePath: r.filePath || '', fileUrl: r.fileUrl || '',
        fileSize: r.fileSize || null, fileType: r.fileType || '',
        isPublic: r.isPublic ?? 1, status: r.status ?? 1, description: r.description || '',
      };
    }
  }
}

function reset() {
  form.value = { name: '', resourceType: '', grade: '', fileName: '', filePath: '', fileUrl: '', fileSize: null, fileType: '', isPublic: 1, status: 1, description: '' };
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  if (!currentId.value && !form.value.fileName) { ElMessage.warning('请选择要上传的文件'); return; }
  submitting.value = true;
  try {
    const data = { ...form.value }; let res;
    if (currentId.value) { res = await updateResource(currentId.value, data); }
    else { res = await addResource(data); }
    if (res.success) { ElMessage.success(currentId.value ? '修改成功' : '上传成功'); open.value = false; getList(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}

onMounted(() => { getList(); loadDicts(['sys_normal_disable', 'resource_type']); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
