<!-- 教师端 — 课件共享 -->
<template>
  <div class="app-container">
    <el-row :gutter="16">
      <!-- 左侧分类 -->
      <el-col :span="4">
        <el-card shadow="never" class="tree-card">
          <template #header><span class="panel-title">课件分类</span></template>
          <div v-loading="treeLoading">
            <el-empty v-if="!treeLoading && !treeData.length" description="暂无分类" :image-size="60" />
            <el-tree
              v-else
              :data="treeData"
              :props="{ children: 'children', label: 'name' }"
              node-key="id"
              highlight-current
              @node-click="handleTreeClick"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="20">
        <!-- 搜索栏 -->
        <el-card shadow="never" class="search-card">
          <el-form :model="query" inline>
            <el-form-item label="课件名称">
              <el-input v-model="query.keyword" placeholder="输入名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="共享范围">
              <el-select v-model="query.scope" placeholder="全部" clearable style="width: 120px">
                <el-option label="公开" value="PUBLIC" />
                <el-option label="私有" value="PRIVATE" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 工具栏 -->
        <el-card shadow="never" class="toolbar-card">
          <div class="toolbar">
            <el-button v-permission="['course:resource:add']" type="primary" @click="uploadVisible = true">+ 上传课件</el-button>
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button value="card">📋 卡片</el-radio-button>
              <el-radio-button value="table">📄 表格</el-radio-button>
            </el-radio-group>
          </div>
        </el-card>

        <!-- 卡片视图 -->
        <div v-if="viewMode === 'card'" v-loading="loading" class="card-grid">
          <el-empty v-if="!loading && !list.length" description="暂无课件数据，点击上方按钮上传第一个课件" :image-size="100" />
          <div v-for="item in list" :key="item.id" class="resource-card">
            <div class="card-type-icon">{{ fileTypeIcon(item.fileType) }}</div>
            <div class="card-name">{{ item.name }}</div>
            <div class="card-tags">
              <el-tag size="small">{{ item.subject }}</el-tag>
              <el-tag size="small" type="success">{{ item.grade }}</el-tag>
            </div>
            <div class="card-meta">
              <span>{{ item.uploaderName }}</span>
              <span>{{ item.createTime }}</span>
            </div>
            <div class="card-footer">
              <el-tag :type="item.isPublic ? 'success' : 'warning'" size="small" effect="plain">
                {{ item.isPublic ? '公开' : '私有' }}
              </el-tag>
              <div class="card-actions">
                <el-button size="small" text @click="handleDownload(item)">下载</el-button>
                <el-button v-if="item.canEdit" v-permission="['course:resource:remove']" size="small" text type="danger" @click="handleDeleteRes(item)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 表格视图 -->
        <el-card v-else shadow="never">
          <el-table v-loading="loading" :data="list" stripe border>
            <el-table-column label="序号" type="index" width="60" />
            <el-table-column label="课件名称" prop="name" min-width="180" show-overflow-tooltip />
            <el-table-column label="文件类型" prop="fileType" width="90" />
            <el-table-column label="科目" prop="subject" width="80" />
            <el-table-column label="年级" prop="grade" width="80" />
            <el-table-column label="类型" prop="type" width="100" />
            <el-table-column label="上传人" prop="uploaderName" width="100" />
            <el-table-column label="上传时间" prop="createTime" width="160" />
            <el-table-column label="下载次数" prop="downloadCount" width="90" align="center" />
            <el-table-column label="共享范围" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.isPublic ? 'success' : 'warning'" size="small">
                  {{ row.isPublic ? '公开' : '私有' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" align="center" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
                <el-button v-if="row.canEdit" v-permission="['course:resource:remove']" link type="danger" @click="handleDeleteRes(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="query.page"
            v-model:page-size="query.pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next"
            @change="getList"
          />
        </div>
      </el-col>
    </el-row>

    <UploadDialog v-model="uploadVisible" @saved="getList" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listResource, delResource, recordDownload, getCategoryTree } from '@/api/course/resources.js';
import UploadDialog from './UploadDialog.vue';

const loading = ref(false);
const list = ref([]);
const total = ref(0);
const viewMode = ref('card');

const query = reactive({
  page: 1,
  pageSize: 12,
  keyword: '',
  scope: null,
  categoryId: null,
});

// 分类树
const treeData = ref([]);
const treeLoading = ref(false);

async function loadTree() {
  treeLoading.value = true;
  try {
    const res = await getCategoryTree();
    if (res.success) treeData.value = res.data || [];
  } catch { /* ignore */ }
  treeLoading.value = false;
}

function handleTreeClick(node) {
  query.categoryId = node.id;
  query.page = 1;
  getList();
}

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { page: query.page, pageSize: query.pageSize };
  if (query.keyword?.trim()) params.keyword = query.keyword.trim();
  if (query.scope) params.scope = query.scope;
  if (query.categoryId) params.categoryId = query.categoryId;
  listResource(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      list.value = res.data?.records || [];
      total.value = res.data?.total || 0;
    }
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

function handleQuery() {
  query.page = 1;
  getList();
}

function handleReset() {
  query.keyword = '';
  query.scope = null;
  query.categoryId = null;
  query.page = 1;
  getList();
}

function handleDownload(row) {
  recordDownload(row.id).then(() => {
    if (row.fileUrl) window.open(row.fileUrl, '_blank');
  }).catch(() => {
    ElMessage.error('下载失败');
  });
}

function handleDeleteRes(row) {
  ElMessageBox.confirm(`确认删除课件「${row.name}」？删除后将无法恢复。`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await delResource(row.id);
    if (res.success) {
      ElMessage.success('删除成功');
      getList();
    } else {
      ElMessage.error(res.msg || '删除失败');
    }
  }).catch(() => {});
}

function fileTypeIcon(type) {
  const map = { pdf: '📄', doc: '📝', ppt: '📊', xls: '📈', image: '🖼️' };
  return map[(type || '').toLowerCase()] || '📁';
}

const uploadVisible = ref(false);

onMounted(() => {
  loadTree();
  getList();
});
</script>

<style scoped lang="less">
.tree-card { margin-bottom: 0; }

.search-card, .toolbar-card { margin-bottom: 12px; }

.panel-title { font-size: 14px; font-weight: 600; }

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.resource-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;
  &:hover { box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08); }
}

.card-type-icon { font-size: 32px; margin-bottom: 8px; }

.card-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-tags {
  display: flex;
  gap: 4px;
  margin-bottom: 8px;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #909399;
  margin-bottom: 8px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #ebeef5;
  padding-top: 8px;
}

.card-actions {
  display: flex;
  gap: 4px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
