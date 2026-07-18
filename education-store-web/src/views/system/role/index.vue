<!-- 角色管理 -->
<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="请输入角色名称/编码" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="角色状态" clearable style="width:200px">
          <el-option v-for="d in dicts.sys_normal_disable" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="dateRange">
        <el-date-picker
          v-model="queryParams.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width:240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['system:role:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['system:role:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['system:role:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <div class="table-container">
      <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange" border stripe>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="角色编号" align="center" prop="id" width="80" />
        <el-table-column label="角色名称" align="center" prop="name" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column label="角色编码" align="center" prop="code" width="150" :show-overflow-tooltip="true" />
        <el-table-column label="显示排序" align="center" prop="sort" width="80" />
        <el-table-column label="状态" align="center" width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.status" :active-value="1" :inactive-value="0" @change="(val) => handleStatusChange(row, val)" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="230" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['system:role:edit']">修改</el-button>
            <el-button link type="primary" size="small" @click="handleDelete(row)" v-permission="['system:role:delete']">删除</el-button>
            <el-dropdown @command="(cmd) => handleCommand(cmd, row)">
              <el-button link type="primary" size="small">更多<el-icon><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="authUser" v-if="userStore.permissions.includes('system:role:edit')">分配用户</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="open" :title="title" width="600px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码" :disabled="!!editId" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="d in dicts.sys_normal_disable" :key="d.value" :value="Number(d.value)">{{ d.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限" prop="menuIds">
          <el-tree
            ref="menuTreeRef"
            :data="menuOptions"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            show-checkbox
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配用户弹窗 -->
    <el-dialog v-model="authUserOpen" :title="'分配用户 - ' + currentRole.name" width="650px" destroy-on-close append-to-body>
      <el-row :gutter="20">
        <!-- 左侧：已分配用户 -->
        <el-col :span="12">
          <div class="auth-user-section">
            <div class="auth-user-title">已分配用户（{{ allocatedUsers.length }}）</div>
            <div class="auth-user-list">
              <el-tag
                v-for="user in allocatedUsers"
                :key="user.id"
                closable
                size="small"
                @close="removeAllocatedUser(user)"
                style="margin: 2px 4px 2px 0;"
              >
                {{ user.realName || user.username }}
              </el-tag>
              <el-empty v-if="allocatedUsers.length === 0" :image-size="40" description="暂无用户" />
            </div>
          </div>
        </el-col>
        <!-- 右侧：搜索添加用户 -->
        <el-col :span="12">
          <div class="auth-user-section">
            <div class="auth-user-title">添加用户</div>
            <el-input
              v-model="candidateKeyword"
              placeholder="搜索用户名/姓名/手机号"
              size="small"
              clearable
              @keyup.enter="searchCandidates"
              @clear="candidates = []"
            >
              <template #append>
                <el-button @click="searchCandidates">搜索</el-button>
              </template>
            </el-input>
            <div class="auth-user-list" style="margin-top: 10px;">
              <el-checkbox-group v-model="selectedCandidateIds">
                <div
                  v-for="user in candidates"
                  :key="user.id"
                  style="padding: 4px 0;"
                >
                  <el-checkbox :label="user.id" :value="user.id">
                    {{ user.realName || user.username }}
                  </el-checkbox>
                </div>
              </el-checkbox-group>
              <el-empty v-if="candidates.length === 0 && candidateSearched" :image-size="40" description="无匹配用户" />
              <span v-if="!candidateSearched" style="color: #999; font-size: 13px;">请输入关键字搜索用户</span>
            </div>
            <el-button
              type="primary"
              size="small"
              :disabled="selectedCandidateIds.length === 0"
              @click="addCandidateUsers"
              style="margin-top: 10px;"
            >
              添加选中用户
            </el-button>
          </div>
        </el-col>
      </el-row>
      <template #footer>
        <el-button @click="authUserOpen = false">取消</el-button>
        <el-button type="primary" @click="submitAuthUsers">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete, ArrowDown } from '@element-plus/icons-vue';
import { listRole, getRole, addRole, updateRole, delRole, changeRoleStatus, getAuthUsers, updateAuthUsers } from '@/api/system/role';
import { listMenu } from '@/api/system/menu';
import { listUser } from '@/api/system/user';
import { useDictsStore } from '@/stores/dicts';
import { useUserStore } from '@/stores/user';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';

const { dicts, loadDicts } = useDictsStore();
const userStore = useUserStore();

const loading = ref(false);
const showSearch = ref(true);
const roleList = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryFormRef = ref(null);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '', status: null, dateRange: [] });

const open = ref(false);
const title = ref('');
const formRef = ref(null);
const menuTreeRef = ref(null);
const editId = ref(null);
const menuOptions = ref([]);

const form = ref({
  name: '',
  code: '',
  sort: 0,
  status: 1,
  menuIds: [],
  remark: '',
});

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入显示排序', trigger: 'blur' }],
};

// 分配用户弹窗
const authUserOpen = ref(false);
const currentRole = ref({});
const allocatedUsers = ref([]);
const candidates = ref([]);
const candidateKeyword = ref('');
const candidateSearched = ref(false);
const selectedCandidateIds = ref([]);

function getList() {
  loading.value = true;
  const params = { ...queryParams.value };
  if (params.dateRange && params.dateRange.length === 2) {
    params.beginTime = params.dateRange[0];
    params.endTime = params.dateRange[1];
  }
  delete params.dateRange;
  if (params.status === null || params.status === '') delete params.status;
  listRole(params).then((res) => {
    if (res.success) {
      roleList.value = res.data?.records || res.rows || [];
      total.value = res.data?.total || res.total || 0;
    } else {
      ElMessage.error(res.message || res.msg || '查询失败');
    }
  }).finally(() => { loading.value = false; });
}

function getMenuTree() {
  listMenu().then((res) => {
    if (res.success) {
      menuOptions.value = res.data || [];
    } else {
      ElMessage.error(res.message || res.msg || '获取菜单树失败');
    }
  });
}

function handleQuery() {
  queryParams.value.page = 1;
  getList();
}

function resetQuery() {
  queryParams.value = { page: 1, pageSize: 10, keyword: '', status: null, dateRange: [] };
  getList();
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map((i) => i.id);
}

function handleAdd() {
  title.value = '添加角色';
  editId.value = null;
  resetForm();
  open.value = true;
}

function handleUpdate(row) {
  title.value = '修改角色';
  const roleId = row ? row.id : selectedIds.value[0];
  if (!roleId) {
    ElMessage.warning('请选择要修改的角色');
    return;
  }
  editId.value = roleId;
  getRole(roleId).then((res) => {
    if (!res.success) {
      ElMessage.error(res.msg || '获取角色信息失败');
      return;
    }
    const data = res.data || {};
    form.value = {
      name: data.name || '',
      code: data.code || '',
      sort: data.sort || 0,
      status: data.status !== undefined ? data.status : 1,
      menuIds: data.menuIds || [],
      remark: data.remark || '',
    };
    // 设置菜单树选中
    setTimeout(() => {
      if (menuTreeRef.value && form.value.menuIds) {
        menuTreeRef.value.setCheckedKeys(form.value.menuIds);
      }
    }, 100);
    open.value = true;
  });
}

function resetForm() {
  form.value = { name: '', code: '', sort: 0, status: 1, menuIds: [], remark: '' };
  editId.value = null;
  formRef.value?.resetFields();
  // 清空菜单选中
  setTimeout(() => {
    menuTreeRef.value?.setCheckedKeys([]);
  }, 100);
}

function submitForm() {
  formRef.value?.validate((valid) => {
    if (!valid) return;
    const checkedKeys = menuTreeRef.value?.getCheckedKeys() || [];
    const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || [];
    const data = {
      ...form.value,
      menuIds: [...checkedKeys, ...halfCheckedKeys],
    };
    if (editId.value) {
      updateRole(editId.value, data).then(res => {
        if (res.success) {
          ElMessage.success('修改成功');
          open.value = false;
          getList();
        } else {
          ElMessage.error(res.msg || '修改失败');
        }
      });
    } else {
      addRole(data).then(res => {
        if (res.success) {
          ElMessage.success('新增成功');
          open.value = false;
          getList();
        } else {
          ElMessage.error(res.msg || '新增失败');
        }
      });
    }
  });
}

function handleDelete(row) {
  const roleIds = row ? [row.id] : selectedIds.value;
  if (!roleIds || roleIds.length === 0) {
    ElMessage.warning('请选择要删除的角色');
    return;
  }
  ElMessageBox.confirm('是否确认删除选中的角色？', '警告', { type: 'warning' })
    .then(() => delRole(roleIds.join(',')))
    .then(res => {
      if (res.success) {
        ElMessage.success('删除成功');
        getList();
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    })
    .catch(() => {});
}

function handleStatusChange(row, newValue) {
  const text = newValue === 1 ? '启用' : '停用';
  ElMessageBox.confirm(`确认要${text}该角色吗？`, '提示', { type: 'warning' })
    .then(() => changeRoleStatus(row.id, newValue))
    .then(res => {
      if (res.success) {
        row.status = newValue;
        ElMessage.success(`${text}成功`);
      } else {
        ElMessage.error(res.msg || `${text}失败`);
      }
    })
    .catch(() => {}); // 取消：不做任何操作，:model-value 确保开关自动回到原位
}

// ======== 分配用户 ========

function handleAuthUser(row) {
  currentRole.value = row;
  allocatedUsers.value = [];
  candidates.value = [];
  candidateKeyword.value = '';
  candidateSearched.value = false;
  selectedCandidateIds.value = [];
  // 加载已分配用户
  getAuthUsers(row.id).then(res => {
    if (res.success && res.data?.userIds) {
      const userIds = res.data.userIds;
      if (userIds.length > 0) {
        // 分页加载全量用户列表，筛选已分配用户
        loadAllUsers().then(allUsers => {
          allocatedUsers.value = allUsers.filter(u => userIds.includes(u.id));
        });
      }
    }
  });
  authUserOpen.value = true;
}

/** 分页循环加载所有用户（避免 pageSize 硬编码截断） */
async function loadAllUsers() {
  const all = [];
  let page = 1;
  const pageSize = 500;
  let hasMore = true;
  while (hasMore) {
    const res = await listUser({ page, size: pageSize });
    if (res.success) {
      const records = res.data?.records || res.rows || [];
      all.push(...records);
      hasMore = records.length === pageSize;
      page++;
    } else {
      hasMore = false;
    }
  }
  return all;
}

// 竞态保护：丢弃非最新请求的过期响应
let candidateSearchSeq = 0;

function searchCandidates() {
  if (!candidateKeyword.value.trim()) return;
  candidateSearched.value = true;
  candidateSearchSeq++;
  const seq = candidateSearchSeq;
  listUser({ page: 1, size: 50, keyword: candidateKeyword.value }).then(res => {
    if (seq !== candidateSearchSeq) return;
    if (res.success) {
      const all = res.data?.records || res.rows || [];
      // 过滤掉已分配的用户
      const allocatedIds = allocatedUsers.value.map(u => u.id);
      candidates.value = all.filter(u => !allocatedIds.includes(u.id));
      // 清除之前的选择
      selectedCandidateIds.value = [];
    }
  });
}

function addCandidateUsers() {
  if (selectedCandidateIds.value.length === 0) return;
  const toAdd = candidates.value.filter(u => selectedCandidateIds.value.includes(u.id));
  allocatedUsers.value = [...allocatedUsers.value, ...toAdd];
  candidates.value = candidates.value.filter(u => !selectedCandidateIds.value.includes(u.id));
  selectedCandidateIds.value = [];
}

function removeAllocatedUser(user) {
  allocatedUsers.value = allocatedUsers.value.filter(u => u.id !== user.id);
}

function submitAuthUsers() {
  const userIds = allocatedUsers.value.map(u => u.id);
  updateAuthUsers(currentRole.value.id, { userIds }).then(res => {
    if (res.success) {
      ElMessage.success('用户分配成功');
      authUserOpen.value = false;
    } else {
      ElMessage.error(res.msg || '用户分配失败');
    }
  });
}

function handleCommand(command, row) {
  if (command === 'authUser') {
    handleAuthUser(row);
  }
}

onMounted(() => {
  getList();
  getMenuTree();
  loadDicts(['sys_normal_disable']);
});
</script>

<style scoped lang="less">
.app-container {
  padding: 16px;
}

.head-container {
  margin-bottom: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.table-container {
  margin-top: 12px;
}

.auth-user-section {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 12px;
  min-height: 300px;
}

.auth-user-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.auth-user-list {
  min-height: 120px;
  max-height: 300px;
  overflow-y: auto;
}
</style>
