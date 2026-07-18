<!-- 用户管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="请输入用户名称/手机号" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width:200px">
          <el-option v-for="d in dicts.sys_normal_disable" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['system:user:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['system:user:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['system:user:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain size="small" @click="handleImport" v-permission="['system:user:import']">
          <el-icon><Upload /></el-icon>导入
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain size="small" @click="handleExport" v-permission="['system:user:export']">
          <el-icon><Download /></el-icon>导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户编号" align="center" prop="id" width="80" />
      <el-table-column label="用户名称" align="center" prop="username" :show-overflow-tooltip="true" />
      <el-table-column label="真实姓名" align="center" prop="realName" :show-overflow-tooltip="true" />
      <el-table-column label="手机号码" align="center" prop="mobile" width="120" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="{ row }">
          <span>{{ row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['system:user:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['system:user:delete']">删除</el-button>
          <el-dropdown @command="(cmd) => handleCommand(cmd, row)">
            <el-button link type="primary" size="small">更多<el-icon><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="userStore.permissions.includes('system:user:resetPwd')" command="resetPwd">重置密码</el-dropdown-item>
                <el-dropdown-item v-if="userStore.permissions.includes('system:user:edit')" command="authRole">分配角色</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <!-- 新增/编辑弹窗 -->
    <UserFormModal v-model:visible="open" :title="title" :userId="currentUserId" @submit-success="getList" />

    <!-- 分配角色弹窗 -->
    <AuthRoleModal v-model:visible="roleOpen" :user="currentUser" @submit-success="getList" />

    <!-- 导入用户弹窗 -->
    <ImportUserModal v-model:visible="importOpen" @submit-success="getList" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete, ArrowDown, Upload, Download } from '@element-plus/icons-vue';
import { listUser, delUser, changeUserStatus, resetUserPwd, exportUser } from '@/api/system/user';
import { useDictsStore } from '@/stores/dicts';
import { useUserStore } from '@/stores/user';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import { downloadFromBlob } from '@/utils/download';
import UserFormModal from './components/UserFormModal.vue';
import AuthRoleModal from './components/AuthRoleModal.vue';
import ImportUserModal from './components/ImportUserModal.vue';

const { dicts, loadDict } = useDictsStore();
const userStore = useUserStore();

const loading = ref(false);
const showSearch = ref(true);
const userList = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, size: 10, keyword: '', status: null });

const open = ref(false);
const title = ref('');
const currentUserId = ref(null);
const currentUser = ref({});
const roleOpen = ref(false);
const importOpen = ref(false);

// 竞态保护：丢弃非最新请求的过期响应
let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  if (params.status === null || params.status === '') delete params.status;
  listUser(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      userList.value = res.data?.records || res.rows || [];
      total.value = res.data?.total || res.total || 0;
    } else {
      ElMessage.error(res.msg || '查询失败');
    }
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() {
  queryParams.value = { page: 1, size: 10, keyword: '', status: null };
  getList();
}

function handleAdd() { title.value = '添加用户'; currentUserId.value = null; open.value = true; }
function handleUpdate(row) {
  title.value = '修改用户';
  currentUserId.value = row ? row.id : selectedIds.value[0];
  open.value = true;
}
function handleDelete(row) {
  const userIds = row ? [row.id] : selectedIds.value;
  if (!userIds || userIds.length === 0) { ElMessage.warning('请选择要删除的用户'); return; }
  ElMessageBox.confirm('是否确认删除选中的用户？', '警告', { type: 'warning' })
    .then(() => delUser(userIds.join(',')))
    .then(res => {
      if (res.success) {
        getList();
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    });
}
function handleStatusChange(row) {
  const text = row.status === 1 ? '启用' : '停用';
  ElMessageBox.confirm(`确认要${text}该用户吗？`, '提示', { type: 'warning' })
    .then(() => changeUserStatus(row.id, row.status))
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
function handleCommand(command, row) {
  currentUser.value = row;
  if (command === 'resetPwd') {
    ElMessageBox.prompt('请输入新密码', '重置密码', { type: 'warning' })
      .then(({ value }) => resetUserPwd(row.id, value))
      .then(res => {
        if (res.success) {
          ElMessage.success('密码重置成功');
        } else {
          ElMessage.error(res.msg || '密码重置失败');
        }
      });
  } else if (command === 'authRole') {
    roleOpen.value = true;
  }
}
function handleImport() { importOpen.value = true; }
function handleExport() {
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  if (params.status === null || params.status === '') delete params.status;
  delete params.page;
  delete params.size;

  exportUser(params)
    .then(res => downloadFromBlob(res, '用户数据.xlsx'))
    .catch(() => ElMessage.error('导出失败'));
}
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

onMounted(() => { getList(); loadDict('sys_normal_disable'); });
</script>

<style scoped lang="less">
.app-container {
  padding: 16px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>
