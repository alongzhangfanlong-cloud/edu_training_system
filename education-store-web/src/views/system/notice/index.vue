<!-- 通知公告管理 -->
<template>
  <div class="page-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="公告标题" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="请输入公告标题" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="公告类型" clearable style="width:200px">
          <el-option label="通知" value="notice" />
          <el-option label="公告" value="announcement" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="公告状态" clearable style="width:200px">
          <el-option label="已发布" :value="1" />
          <el-option label="草稿" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['system:notice:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['system:notice:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['system:notice:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <div class="table-container">
      <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange" border stripe>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="80" />
        <el-table-column label="公告标题" align="center" prop="title" min-width="200" :show-overflow-tooltip="true" />
        <el-table-column label="类型" align="center" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'announcement' ? 'primary' : 'info'" size="small">
              {{ row.type === 'announcement' ? '公告' : '通知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="级别" align="center" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.level === 'urgent' ? 'danger' : row.level === 'important' ? 'warning' : 'info'"
              size="small"
            >
              {{ row.level === 'urgent' ? '紧急' : row.level === 'important' ? '重要' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布人" align="center" prop="publisherName" width="100" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['system:notice:edit']">修改</el-button>
            <el-button link type="primary" size="small" @click="handleDelete(row)" v-permission="['system:notice:delete']">删除</el-button>
            <el-button v-if="row.status === 0" link type="success" size="small" @click="handlePublish(row)" v-permission="['system:notice:edit']">发布</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleUnpublish(row)" v-permission="['system:notice:edit']">撤回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0" :total="total"
        v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="open" :title="title" width="700px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="公告类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择公告类型" style="width:100%">
                <el-option label="通知" value="notice" />
                <el-option label="公告" value="announcement" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急程度" prop="level">
              <el-select v-model="form.level" placeholder="请选择紧急程度" style="width:100%">
                <el-option label="普通" value="normal" />
                <el-option label="重要" value="important" />
                <el-option label="紧急" value="urgent" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">草稿</el-radio>
            <el-radio :value="1">发布</el-radio>
          </el-radio-group>
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
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { listNotice, getNotice, addNotice, updateNotice, delNotice } from '@/api/system/notice';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';

const loading = ref(false);
const showSearch = ref(true);
const noticeList = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '', type: null, status: null });

const open = ref(false);
const title = ref('');
const formRef = ref(null);
const editId = ref(null);
const form = ref({
  title: '',
  content: '',
  type: 'notice',
  level: 'normal',
  status: 0,
});

const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
};

function getList() {
  loading.value = true;
  const params = { ...queryParams.value };
  if (!params.type) delete params.type;
  if (params.status === null || params.status === '') delete params.status;
  listNotice(params).then(res => {
    noticeList.value = res.data?.records || res.rows || [];
    total.value = res.data?.total || res.total || 0;
  }).finally(() => { loading.value = false; });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() {
  queryParams.value = { page: 1, pageSize: 10, keyword: '', type: null, status: null };
  getList();
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

function handleAdd() {
  title.value = '新增公告';
  editId.value = null;
  form.value = { title: '', content: '', type: 'notice', level: 'normal', status: 0 };
  open.value = true;
}

function handleUpdate(row) {
  title.value = '修改公告';
  const id = row ? row.id : selectedIds.value[0];
  if (!id) { ElMessage.warning('请选择要修改的公告'); return; }
  editId.value = id;
  getNotice(id).then(res => {
    const data = res.data || res;
    form.value = {
      title: data.title || '',
      content: data.content || '',
      type: data.type || 'notice',
      level: data.level || 'normal',
      status: data.status,
    };
    open.value = true;
  });
}

function handleDelete(row) {
  const noticeIds = row ? [row.id] : selectedIds.value;
  if (!noticeIds || noticeIds.length === 0) { ElMessage.warning('请选择要删除的公告'); return; }
  ElMessageBox.confirm('是否确认删除选中的公告？', '警告', { type: 'warning' })
    .then(() => delNotice(noticeIds.join(',')))
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

function handlePublish(row) {
  ElMessageBox.confirm('确认发布该公告？', '提示', { type: 'warning' })
    .then(() => updateNotice({ id: row.id, status: 1 }))
    .then(res => {
      if (res.success) {
        getList();
        ElMessage.success('发布成功');
      } else {
        ElMessage.error(res.msg || '发布失败');
      }
    })
    .catch(() => {});
}

function handleUnpublish(row) {
  ElMessageBox.confirm('确认撤回该公告？', '提示', { type: 'warning' })
    .then(() => updateNotice({ id: row.id, status: 0 }))
    .then(res => {
      if (res.success) {
        getList();
        ElMessage.success('撤回成功');
      } else {
        ElMessage.error(res.msg || '撤回失败');
      }
    })
    .catch(() => {});
}

function submitForm() {
  formRef.value?.validate(valid => {
    if (!valid) return;
    if (editId.value) {
      updateNotice({ id: editId.value, ...form.value }).then(res => {
        if (res.success) {
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        } else {
          ElMessage.error(res.msg || '操作失败');
        }
      });
    } else {
      addNotice(form.value).then(res => {
        if (res.success) {
          ElMessage.success('新增成功');
          open.value = false;
          getList();
        } else {
          ElMessage.error(res.msg || '操作失败');
        }
      });
    }
  });
}

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.page-container { padding: 16px; }
.table-container { margin-top: 12px; background: #fff; border-radius: 4px; padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
