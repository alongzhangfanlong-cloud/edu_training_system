<!-- 教师端 — 课后作业列表 -->
<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="作业标题">
          <el-input v-model="query.keyword" placeholder="输入关键词" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select
            v-model="query.classId"
            placeholder="全部"
            clearable
            filterable
            remote
            :remote-method="searchClasses"
            :loading="classLoading"
            style="width: 180px"
          >
            <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="query.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card shadow="never" class="toolbar-card">
      <el-button v-permission="['teaching:homework:add']" type="primary" @click="openAdd">+ 布置作业</el-button>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe border>
        <el-table-column label="布置日期" prop="createTime" width="120" />
        <el-table-column label="作业标题" prop="title" min-width="200" show-overflow-tooltip />
        <el-table-column label="班级" prop="className" width="120" />
        <el-table-column label="课程" prop="courseName" width="100" />
        <el-table-column label="附件数" width="80" align="center">
          <template #default="{ row }">
            {{ row.attachmentCount ? row.attachmentCount + '个' : '无' }}
          </template>
        </el-table-column>
        <el-table-column label="发布状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="['teaching:homework:edit']" link type="primary" @click="openView(row)">查看</el-button>
            <el-button v-permission="['teaching:homework:edit']" link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-permission="['teaching:homework:remove']" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="getList"
        />
      </div>
    </el-card>

    <!-- 作业表单弹窗 -->
    <HomeworkForm v-model="formVisible" :data="currentRow" @saved="getList" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listHomework, delHomework } from '@/api/teaching/homework.js';
import { listClass } from '@/api/class/classes.js';
import { useUserStore } from '@/stores/user';
import HomeworkForm from './HomeworkForm.vue';

const userStore = useUserStore();

const loading = ref(false);
const list = ref([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  classId: null,
  dateRange: null,
});

// 班级远程搜索
const classOptions = ref([]);
const classLoading = ref(false);
let classSearchTimer = null;
let classSearchSeq = 0;

function searchClasses(keyword) {
  clearTimeout(classSearchTimer);
  if (!keyword) { classOptions.value = []; return; }
  classLoading.value = true;
  classSearchTimer = setTimeout(() => {
    classSearchSeq++;
    const seq = classSearchSeq;
    listClass({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== classSearchSeq) return;
      if (res.success) classOptions.value = res.data?.records || [];
    }).finally(() => {
      if (seq === classSearchSeq) classLoading.value = false;
    });
  }, 300);
}

let querySeq = 0;

function getList() {
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  const params = { page: query.page, pageSize: query.pageSize };
  if (query.keyword?.trim()) params.keyword = query.keyword.trim();
  if (query.classId) params.classId = query.classId;
  if (query.dateRange?.length === 2) {
    params.startDate = query.dateRange[0];
    params.endDate = query.dateRange[1];
  }
  listHomework(params).then(res => {
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
  query.classId = null;
  query.dateRange = null;
  query.page = 1;
  getList();
}

// 表单弹窗
const formVisible = ref(false);
const currentRow = ref(null);

function openAdd() {
  currentRow.value = null;
  formVisible.value = true;
}

function openEdit(row) {
  currentRow.value = { ...row };
  formVisible.value = true;
}

function openView(row) {
  currentRow.value = { ...row };
  formVisible.value = true;
}

function handleDelete(row) {
  ElMessageBox.confirm('确定删除该作业？删除后学生家长端将不可见。', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await delHomework(row.id, userStore.userId);
    if (res.success) {
      ElMessage.success('删除成功');
      getList();
    } else {
      ElMessage.error(res.msg || '删除失败');
    }
  }).catch(() => {});
}

onMounted(() => {
  getList();
});
</script>

<style scoped lang="less">
.search-card, .toolbar-card { margin-bottom: 12px; }

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
