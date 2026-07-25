<!-- 意向学员管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="姓名/手机号" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="跟进状态" prop="followStatus">
        <el-select v-model="queryParams.followStatus" placeholder="跟进状态" clearable style="width:200px">
          <el-option v-for="d in dicts.prospect_follow_status" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="年级" prop="grade">
        <el-input v-model="queryParams.grade" placeholder="请输入年级" clearable style="width:200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['student:prospect:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain size="small" :disabled="single" @click="handleUpdate(null)" v-permission="['student:prospect:edit']">
          <el-icon><Edit /></el-icon>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['student:prospect:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain size="small" :disabled="single" @click="handleConvert(null)" v-permission="['student:prospect:convert']">
          <el-icon><Switch /></el-icon>转为在学
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="prospectList" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="学员姓名" align="center" prop="name" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="跟进状态" align="center" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.prospect_follow_status" :value="row.followStatus" />
        </template>
      </el-table-column>
      <el-table-column label="家长姓名" align="center" prop="parentName" :show-overflow-tooltip="true" min-width="90" />
      <el-table-column label="亲属关系" align="center" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.parent_relation" :value="row.parentRelation" />
        </template>
      </el-table-column>
      <el-table-column label="联系电话" align="center" prop="mobile" width="120" />
      <el-table-column label="顾问" align="center" prop="counselorName" :show-overflow-tooltip="true" width="80" />
      <el-table-column label="体验课次" align="center" prop="trialLessonCount" width="80" />
      <el-table-column label="性别" align="center" width="60">
        <template #default="{ row }">
          <dict-tag :options="dicts.sys_user_sex" :value="row.gender" />
        </template>
      </el-table-column>
      <el-table-column label="年级" align="center" prop="grade" width="80" />
      <el-table-column label="来源渠道" align="center" prop="source" width="90">
        <template #default="{ row }">
          <dict-tag :options="dicts.student_source" :value="row.source" />
        </template>
      </el-table-column>
      <el-table-column label="最新跟进日期" align="center" prop="lastFollowupDate" width="120" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['student:prospect:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['student:prospect:delete']">删除</el-button>
          <el-dropdown @command="(cmd) => handleCommand(cmd, row)">
            <el-button link type="primary" size="small">更多<el-icon><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="convert">转为在学</el-dropdown-item>
                <el-dropdown-item command="followup">跟进记录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/编辑弹窗 -->
    <ProspectFormModal v-model:visible="open" :title="title" :prospect-id="currentProspectId" @submit-success="getList" />

    <!-- 跟进记录弹窗 -->
    <FollowupModal v-model:visible="followupOpen" :prospect-id="currentProspectId" :prospect-name="currentProspectName" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete, ArrowDown, Switch } from '@element-plus/icons-vue';
import { listProspect, delProspect, convertProspect } from '@/api/student/prospects';
import { useDictsStore } from '@/stores/dicts';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import DictTag from '@/components/DictTag/index.vue';
import ProspectFormModal from './components/ProspectFormModal.vue';
import FollowupModal from './components/FollowupModal.vue';

const { dicts, loadDicts } = useDictsStore();

const loading = ref(false);
const showSearch = ref(true);
const prospectList = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const single = computed(() => selectedIds.value.length !== 1);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, pageSize: 10, keyword: '', followStatus: '', grade: '' });

const open = ref(false);
const title = ref('');
const currentProspectId = ref(null);
const currentProspectName = ref('');
const followupOpen = ref(false);

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { ...queryParams.value };
  if (!params.keyword) delete params.keyword;
  if (!params.followStatus) delete params.followStatus;
  if (!params.grade) delete params.grade;
  listProspect(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      prospectList.value = res.data?.records || res.rows || [];
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
  queryParams.value = { page: 1, pageSize: 10, keyword: '', followStatus: '', grade: '' };
  getList();
}

function handleAdd() { title.value = '新增意向学员'; currentProspectId.value = null; open.value = true; }
function handleUpdate(row) {
  title.value = '修改意向学员';
  currentProspectId.value = row ? row.id : selectedIds.value[0];
  open.value = true;
}
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择要删除的意向学员'); return; }
  ElMessageBox.confirm('是否确认删除选中的意向学员？', '警告', { type: 'warning' })
    .then(() => delProspect(ids.join(',')))
    .then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => {});
}
function handleConvert(row) {
  const id = row ? row.id : selectedIds.value[0];
  if (!id) { ElMessage.warning('请选择要转化的意向学员'); return; }
  ElMessageBox.confirm('确认将该意向学员转化为在学学员？转化后不可撤销。', '警告', { type: 'warning' })
    .then(() => convertProspect(id))
    .then(res => {
      if (res.success) { getList(); ElMessage.success('转化成功'); }
      else { ElMessage.error(res.msg || '转化失败'); }
    }).catch(() => {});
}

function handleCommand(command, row) {
  currentProspectId.value = row.id;
  currentProspectName.value = row.name;
  if (command === 'convert') {
    handleConvert(row);
  } else if (command === 'followup') {
    followupOpen.value = true;
  }
}

function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }

onMounted(() => { getList(); loadDicts(['prospect_follow_status', 'student_source', 'parent_relation', 'sys_user_sex']); });
</script>

<style scoped lang="less">
.app-container {
  padding: 16px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>
