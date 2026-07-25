<!-- 教室管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch" @submit.prevent>
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="教室名称/位置" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="button" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button native-type="button" @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['course:classroom:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['course:classroom:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="教室名称" align="center" prop="name" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="容纳人数" align="center" prop="capacity" width="100" />
      <el-table-column label="位置" align="center" prop="location" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="设施" align="center" prop="facilities" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'danger' : 'success'">{{ row.status === 0 ? '停用' : '正常' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['course:classroom:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['course:classroom:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && list.length === 0" description="暂无教室数据" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
      </el-result>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <el-dialog :model-value="open" :title="title" width="500px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => open = val">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="教室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入教室名称" />
        </el-form-item>
        <el-form-item label="容纳人数">
          <el-input-number v-model="form.capacity" :min="1" style="width:160px" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" placeholder="如：3楼301室" />
        </el-form-item>
        <el-form-item label="设施配置">
          <el-checkbox-group v-model="facilityTags" @change="onFacilityChange">
            <el-checkbox v-for="tag in facilityPool" :key="tag" :label="tag" :value="tag" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="d in dicts.sys_normal_disable" :key="d.value" :value="Number(d.value)">{{ d.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
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
import { listClassroom, getClassroom, addClassroom, updateClassroom, delClassroom } from '@/api/course/classrooms';
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
const open = ref(false);
const title = ref('');
const currentId = ref(null);
const formRef = ref(null);
const submitting = ref(false);
const form = ref({ name: '', capacity: 6, location: '', facilities: '', status: 1, description: '' });
const rules = { name: [{ required: true, message: '请输入教室名称', trigger: 'blur' }] };

// 设施标签池
const facilityTags = ref([]);
const facilityPool = ['投影仪', '白板', '空调', '音响', '电脑', '网络', '摄像头', '电子白板', '实验台', '钢琴', '画架', '舞蹈镜'];

function onFacilityChange(tags) {
  form.value.facilities = tags.join(',');
}
let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value }; if (!params.keyword) delete params.keyword;
  listClassroom(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; error.value = ''; }
    else { error.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) error.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}
function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '' }; getList(); }
function handleAdd() { title.value = '新增教室'; currentId.value = null; open.value = true; }
function handleUpdate(row) { title.value = '修改教室'; currentId.value = row.id; open.value = true; }
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择教室'); return; }
  ElMessageBox.confirm('确认删除选中的教室？', '警告', { type: 'warning' })
    .then(() => delClassroom(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    });
}
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }
async function onOpen() {
  reset(); await loadDict('sys_normal_disable');
  if (currentId.value) {
    const res = await getClassroom(currentId.value);
    if (res.success && res.data) {
      const s = res.data.data || res.data;
      form.value = { name: s.name || '', capacity: s.capacity || 6, location: s.location || '', facilities: s.facilities || '', status: Number(s.status ?? 1), description: s.description || '' };
      facilityTags.value = s.facilities ? s.facilities.split(',').filter(Boolean) : [];
    }
  }
}
function reset() { form.value = { name: '', capacity: 6, location: '', facilities: '', status: 1, description: '' }; facilityTags.value = []; nextTick(() => formRef.value?.clearValidate()); }
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value }; let res;
    if (currentId.value) { data.id = currentId.value; res = await updateClassroom(data); }
    else { res = await addClassroom(data); }
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
