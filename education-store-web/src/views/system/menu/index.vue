<template>
  <div class="page-container">
    <div class="table-container">
      <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入菜单名称" clearable style="width:200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain size="small" @click="handleAdd()" v-permission="['system:menu:add']">
            <el-icon><Plus /></el-icon>新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="info" plain size="small" @click="toggleExpandAll">
            <el-icon><Sort /></el-icon>展开/折叠
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="menuList"
        row-key="id"
        border
        stripe
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="name" label="菜单名称" min-width="200" :show-overflow-tooltip="true" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <i v-if="row.icon" :class="row.icon" style="font-size:18px;color:#606266" :title="row.icon"></i>
            <span v-else style="color:#c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="90" align="center">
          <template #default="{ row }">
            <dict-tag :options="dicts.sys_menu_type" :value="row.type" />
          </template>
        </el-table-column>
        <el-table-column prop="permission" label="权限标识" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column prop="path" label="路由路径" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column prop="component" label="组件路径" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column label="可见" width="70" align="center">
          <template #default="{ row }">
            <dict-tag :options="dicts.sys_show_hide" :value="row.visible" />
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="60" align="center" />
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAdd(row.id)" v-permission="['system:menu:add']">新增</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)" v-permission="['system:menu:edit']">修改</el-button>
            <el-button link type="primary" size="small" @click="handleDelete(row)" v-permission="['system:menu:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" destroy-on-close append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="formData.parentId"
            :data="parentTreeOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单（留空为根菜单）"
            clearable
            check-strictly
            :filterable="true"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="formData.type" @change="onTypeChange">
            <el-radio v-for="d in dicts.sys_menu_type" :key="d.value" :value="d.value">{{ d.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permission">
          <el-input v-model="formData.permission" placeholder="请输入权限标识，如 system:user:list" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" />
        </el-form-item>
        <template v-if="formData.type === 'M' || formData.type === 'C'">
          <el-form-item label="图标" prop="icon">
            <IconPicker v-model="formData.icon" />
          </el-form-item>
          <el-form-item label="路由路径" prop="path">
            <el-input v-model="formData.path" placeholder="请输入路由路径，如 system/user" />
          </el-form-item>
          <el-form-item label="是否可见" prop="visible">
            <el-radio-group v-model="formData.visible">
              <el-radio v-for="d in dicts.sys_show_hide" :key="d.value" :value="Number(d.value)">{{ d.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
        <template v-if="formData.type === 'C'">
          <el-form-item label="组件路径" prop="component">
            <el-input v-model="formData.component" placeholder="如 system/user/index" />
          </el-form-item>
          <el-form-item label="路由名称" prop="routeName">
            <el-input v-model="formData.routeName" placeholder="请输入路由名称" />
          </el-form-item>
          <el-form-item label="是否外链" prop="isFrame">
            <el-radio-group v-model="formData.isFrame">
              <el-radio v-for="d in dicts.sys_yes_no" :key="d.value" :value="Number(d.value)">{{ d.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="是否缓存" prop="isCache">
            <el-radio-group v-model="formData.isCache">
              <el-radio v-for="d in dicts.sys_yes_no" :key="d.value" :value="Number(d.value)">{{ d.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Sort } from '@element-plus/icons-vue';
import { listMenu, addMenu, updateMenu, delMenu } from '@/api/system/menu';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import IconPicker from '@/components/IconPicker/index.vue';
import DictTag from '@/components/DictTag/index.vue';

const loading = ref(false);
const showSearch = ref(true);
const { dicts, loadDicts } = useDictsStore();
const menuList = ref([]);
const isExpandAll = ref(false);
const refreshTable = ref(true);
const queryParams = ref({ name: '' });

const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const editId = ref(null);
const parentTreeOptions = ref([]);

const formData = ref({
  parentId: null,
  name: '',
  type: 'M',
  icon: '',
  path: '',
  component: '',
  permission: '',
  sort: 1,
  visible: 1,
  routeName: '',
  isFrame: 0,
  isCache: 0,
});

const rules = computed(() => {
  const base = {
    name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  };
  // 目录/菜单：路由路径必填
  if (formData.value.type === 'M' || formData.value.type === 'C') {
    base.path = [{ required: true, message: '请输入路由路径', trigger: 'blur' }];
  }
  // 菜单：组件路径、路由名称必填
  if (formData.value.type === 'C') {
    base.component = [{ required: true, message: '请输入组件路径', trigger: 'blur' }];
    base.routeName = [{ required: true, message: '请输入路由名称', trigger: 'blur' }];
  }
  return base;
});

function getList() {
  loading.value = true;
  listMenu(queryParams.value).then(res => {
    if (res.success) {
      menuList.value = res.data || [];
    } else {
      ElMessage.error(res.message || res.msg || '获取菜单列表失败');
    }
  }).finally(() => { loading.value = false; });
}

function getParentTree() {
  listMenu().then(res => {
    if (res.success) {
      parentTreeOptions.value = res.data || [];
    } else {
      ElMessage.error(res.message || res.msg || '获取菜单树失败');
    }
  });
}

function handleQuery() {
  getList();
}

function resetQuery() {
  queryParams.value = { name: '' };
  getList();
}

function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => { refreshTable.value = true; });
}

function handleAdd(parentId) {
  dialogTitle.value = '新增菜单';
  editId.value = null;
  formData.value = {
    parentId: parentId || null,
    name: '',
    type: 'M',
    icon: '',
    path: '',
    component: '',
    permission: '',
    sort: 1,
    visible: 1,
    routeName: '',
    isFrame: 0,
    isCache: 0,
  };
  getParentTree();
  dialogVisible.value = true;
}

function handleEdit(row) {
  dialogTitle.value = '修改菜单';
  editId.value = row.id;
  formData.value = {
    parentId: row.parentId || null,
    name: row.name || '',
    type: row.type || 'M',
    icon: row.icon || '',
    path: row.path || '',
    component: row.component || '',
    permission: row.permission || '',
    sort: row.sort || 1,
    visible: row.visible !== undefined ? row.visible : 1,
    routeName: row.routeName || '',
    isFrame: row.isFrame !== undefined ? row.isFrame : 0,
    isCache: row.isCache !== undefined ? row.isCache : 0,
  };
  getParentTree();
  dialogVisible.value = true;
}

function onTypeChange(type) {
  if (type === 'F') {
    // 按钮：清理菜单/目录专属字段
    formData.value.icon = '';
    formData.value.path = '';
    formData.value.component = '';
    formData.value.visible = 1;
    formData.value.routeName = '';
    formData.value.isFrame = 0;
    formData.value.isCache = 0;
  } else if (type === 'M') {
    // 目录：清理仅菜单专属字段
    formData.value.component = '';
    formData.value.routeName = '';
    formData.value.isFrame = 0;
    formData.value.isCache = 0;
  }
  // M→C: 无需清理，所有字段都适用
}

function handleSubmit() {
  formRef.value?.validate().then(() => {
    const data = { ...formData.value };
    if (!data.parentId) data.parentId = 0;
    if (editId.value) {
      data.id = editId.value;
      updateMenu(data).then(res => {
        if (res.success) {
          ElMessage.success('修改成功');
          dialogVisible.value = false;
          getList();
        } else {
          ElMessage.error(res.message || res.msg || '修改失败');
        }
      });
    } else {
      addMenu(data).then(res => {
        if (res.success) {
          ElMessage.success('新增成功');
          dialogVisible.value = false;
          getList();
        } else {
          ElMessage.error(res.message || res.msg || '操作失败');
        }
      });
    }
  }).catch(() => {});
}

function handleDelete(row) {
  ElMessageBox.confirm(`确认删除菜单"${row.name}"？子菜单将一并删除`, '提示', { type: 'warning' })
    .then(() => delMenu(row.id))
    .then(res => {
      if (res.success) {
        getList();
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.message || res.msg || '删除失败');
      }
    })
    .catch(() => {});
}

onMounted(() => {
  getList();
  loadDicts(['sys_menu_type', 'sys_show_hide', 'sys_yes_no']);
});
</script>

<style scoped lang="less">
.page-container { padding: 16px; }
.table-container { margin-top: 12px; }
.operator-btns { margin-bottom: 12px; }
.mb8 { margin-bottom: 8px; }
</style>
