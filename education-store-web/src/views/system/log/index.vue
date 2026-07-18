<!-- 系统日志管理 -->
<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 操作日志 Tab -->
      <el-tab-pane label="操作日志" name="operlog">
        <el-form :model="operlogParams" size="small" :inline="true" v-show="showSearch">
          <el-form-item label="关键字" prop="keyword">
            <el-input v-model="operlogParams.keyword" placeholder="请输入操作标题" clearable style="width:200px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="操作类型" prop="operType">
            <el-select v-model="operlogParams.operType" placeholder="操作类型" clearable style="width:200px">
              <el-option label="其它" value="OTHER" />
              <el-option label="新增" value="INSERT" />
              <el-option label="修改" value="UPDATE" />
              <el-option label="删除" value="DELETE" />
              <el-option label="授权" value="GRANT" />
              <el-option label="导出" value="EXPORT" />
              <el-option label="导入" value="IMPORT" />
              <el-option label="强退" value="FORCE" />
              <el-option label="清空" value="CLEAN" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="operlogParams.status" placeholder="操作状态" clearable style="width:200px">
              <el-option label="成功" :value="1" />
              <el-option label="失败" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
            <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="danger" plain size="small" :disabled="operlogMultiple" @click="handleOperlogDelete(null)" v-permission="['system:log:delete']">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain size="small" @click="handleOperlogClean" v-permission="['system:log:clean']">
              <el-icon><Delete /></el-icon>清空
            </el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getOperlogList" />
        </el-row>

        <div class="table-container">
          <el-table v-loading="operlogLoading" :data="operlogList" @selection-change="handleOperlogSelectionChange" border stripe>
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="ID" align="center" prop="id" width="80" />
            <el-table-column label="操作标题" align="center" prop="title" min-width="150" :show-overflow-tooltip="true" />
            <el-table-column label="业务类型" align="center" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ getBusinessTypeName(row.businessType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="请求方式" align="center" prop="requestMethod" width="100" />
            <el-table-column label="操作人员" align="center" prop="operName" width="100" />
            <el-table-column label="IP地址" align="center" prop="operIp" width="130" />
            <el-table-column label="操作地点" align="center" prop="operLocation" width="120" :show-overflow-tooltip="true" />
            <el-table-column label="状态" align="center" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="耗时" align="center" prop="costTime" width="80">
              <template #default="{ row }">
                <span>{{ row.costTime !== null && row.costTime !== undefined ? row.costTime + 'ms' : '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作时间" align="center" prop="operTime" width="160" />
          </el-table>

          <pagination
            v-show="operlogTotal > 0" :total="operlogTotal"
            v-model:page="operlogParams.page" v-model:limit="operlogParams.pageSize"
            @pagination="getOperlogList"
          />
        </div>
      </el-tab-pane>

      <!-- 登录日志 Tab -->
      <el-tab-pane label="登录日志" name="logininfor">
        <el-form :model="logininforParams" size="small" :inline="true" v-show="showSearch">
          <el-form-item label="用户名称" prop="username">
            <el-input v-model="logininforParams.username" placeholder="请输入用户名称" clearable style="width:200px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="IP地址" prop="ipaddr">
            <el-input v-model="logininforParams.ipaddr" placeholder="请输入IP地址" clearable style="width:200px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="logininforParams.status" placeholder="登录状态" clearable style="width:200px">
              <el-option label="成功" :value="1" />
              <el-option label="失败" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
            <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="danger" plain size="small" :disabled="logininforMultiple" @click="handleLogininforDelete(null)" v-permission="['system:log:delete']">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain size="small" @click="handleLogininforClean" v-permission="['system:log:clean']">
              <el-icon><Delete /></el-icon>清空
            </el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="handleQuery" />
        </el-row>

        <div class="table-container">
          <el-table v-loading="logininforLoading" :data="logininforList" @selection-change="handleLogininforSelectionChange" border stripe>
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="ID" align="center" prop="id" width="80" />
            <el-table-column label="用户名称" align="center" prop="username" width="100" />
            <el-table-column label="IP地址" align="center" prop="ipaddr" width="130" />
            <el-table-column label="登录地点" align="center" prop="loginLocation" width="120" :show-overflow-tooltip="true" />
            <el-table-column label="浏览器" align="center" prop="browser" width="120" :show-overflow-tooltip="true" />
            <el-table-column label="操作系统" align="center" prop="os" width="120" :show-overflow-tooltip="true" />
            <el-table-column label="状态" align="center" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="提示信息" align="center" prop="msg" min-width="150" :show-overflow-tooltip="true" />
            <el-table-column label="登录时间" align="center" prop="loginTime" width="160" />
          </el-table>

          <pagination
            v-show="logininforTotal > 0" :total="logininforTotal"
            v-model:page="logininforParams.page" v-model:limit="logininforParams.pageSize"
            @pagination="getLogininforList"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Delete } from '@element-plus/icons-vue';
import { listOperlog, delOperlog, cleanOperlog, listLogininfor, delLogininfor, cleanLogininfor } from '@/api/system/log';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';

const activeTab = ref('operlog');
const showSearch = ref(true);

// 操作日志
const operlogLoading = ref(false);
const operlogList = ref([]);
const operlogTotal = ref(0);
const operlogSelectedIds = ref([]);
const operlogMultiple = computed(() => operlogSelectedIds.value.length === 0);
const operlogParams = ref({ page: 1, pageSize: 10, keyword: '', operType: null, status: null });

// 登录日志
const logininforLoading = ref(false);
const logininforList = ref([]);
const logininforTotal = ref(0);
const logininforSelectedIds = ref([]);
const logininforMultiple = computed(() => logininforSelectedIds.value.length === 0);
const logininforParams = ref({ page: 1, pageSize: 10, username: '', ipaddr: '', status: null });

const businessTypeMap = {
  INSERT: '新增',
  UPDATE: '修改',
  DELETE: '删除',
  GRANT: '授权',
  EXPORT: '导出',
  IMPORT: '导入',
  FORCE: '强退',
  CLEAN: '清空',
  OTHER: '其它',
};

function getBusinessTypeName(type) {
  return businessTypeMap[type] || '其它';
}

function getOperlogList() {
  operlogLoading.value = true;
  const params = { ...operlogParams.value };
  if (!params.keyword) delete params.keyword;
  if (params.operType === null || params.operType === '') delete params.operType;
  if (params.status === null || params.status === '') delete params.status;
  listOperlog(params).then(res => {
    operlogList.value = res.data?.records || res.rows || [];
    operlogTotal.value = res.data?.total || res.total || 0;
  }).finally(() => { operlogLoading.value = false; });
}

function getLogininforList() {
  logininforLoading.value = true;
  const params = { ...logininforParams.value };
  if (!params.username) delete params.username;
  if (!params.ipaddr) delete params.ipaddr;
  if (params.status === null || params.status === '') delete params.status;
  listLogininfor(params).then(res => {
    logininforList.value = res.data?.records || res.rows || [];
    logininforTotal.value = res.data?.total || res.total || 0;
  }).finally(() => { logininforLoading.value = false; });
}

function handleQuery() {
  if (activeTab.value === 'operlog') {
    operlogParams.value.page = 1;
    getOperlogList();
  } else {
    logininforParams.value.page = 1;
    getLogininforList();
  }
}

function resetQuery() {
  if (activeTab.value === 'operlog') {
    operlogParams.value = { page: 1, pageSize: 10, keyword: '', operType: null, status: null };
    getOperlogList();
  } else {
    logininforParams.value = { page: 1, pageSize: 10, username: '', ipaddr: '', status: null };
    getLogininforList();
  }
}

function handleTabChange(name) {
  activeTab.value = name;
  if (name === 'operlog') {
    getOperlogList();
  } else {
    getLogininforList();
  }
}

// 操作日志 - 选择
function handleOperlogSelectionChange(selection) {
  operlogSelectedIds.value = selection.map(i => i.id);
}

// 操作日志 - 删除
function handleOperlogDelete(row) {
  const ids = row ? [row.id] : operlogSelectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择要删除的操作日志'); return; }
  ElMessageBox.confirm('是否确认删除选中的操作日志？', '警告', { type: 'warning' })
    .then(() => delOperlog(ids.join(',')))
    .then(res => {
      if (res.success) {
        getOperlogList();
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    })
    .catch(() => {});
}

// 操作日志 - 清空
function handleOperlogClean() {
  ElMessageBox.confirm('是否确认清空所有操作日志？', '警告', { type: 'warning' })
    .then(() => cleanOperlog())
    .then(res => {
      if (res.success) {
        getOperlogList();
        ElMessage.success('清空成功');
      } else {
        ElMessage.error(res.msg || '清空失败');
      }
    })
    .catch(() => {});
}

// 登录日志 - 选择
function handleLogininforSelectionChange(selection) {
  logininforSelectedIds.value = selection.map(i => i.id);
}

// 登录日志 - 删除
function handleLogininforDelete(row) {
  const ids = row ? [row.id] : logininforSelectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择要删除的登录日志'); return; }
  ElMessageBox.confirm('是否确认删除选中的登录日志？', '警告', { type: 'warning' })
    .then(() => delLogininfor(ids.join(',')))
    .then(res => {
      if (res.success) {
        getLogininforList();
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    })
    .catch(() => {});
}

// 登录日志 - 清空
function handleLogininforClean() {
  ElMessageBox.confirm('是否确认清空所有登录日志？', '警告', { type: 'warning' })
    .then(() => cleanLogininfor())
    .then(res => {
      if (res.success) {
        getLogininforList();
        ElMessage.success('清空成功');
      } else {
        ElMessage.error(res.msg || '清空失败');
      }
    })
    .catch(() => {});
}

onMounted(() => { getOperlogList(); });
</script>

<style scoped lang="less">
.page-container { padding: 16px; }
.table-container { margin-top: 12px; background: #fff; border-radius: 4px; padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
