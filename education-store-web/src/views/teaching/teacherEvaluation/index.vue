<!-- 学评教：评价明细 + 评价统计 双Tab -->
<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="📋 评价明细" name="list" />
      <el-tab-pane label="📊 评价统计" name="stats" />
    </el-tabs>

    <!-- ==================== 评价明细 Tab ==================== -->
    <template v-if="activeTab === 'list'">
      <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="评价内容" prop="keyword">
          <el-input v-model="queryParams.keyword" placeholder="搜索评价内容" clearable style="width:200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="queryParams.teacherId" placeholder="选择教师" clearable filterable remote :remote-method="remoteSearchTeacher" :loading="teacherLoading" style="width:150px">
            <el-option v-for="t in teacherOptions" :key="t.id" :label="t.realName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="queryParams.studentId" placeholder="选择学生" clearable filterable remote :remote-method="remoteSearchStudent" :loading="studentLoading" style="width:150px">
            <el-option v-for="s in studentOptions" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width:240px" />
        </el-form-item>
        <el-form-item label="匿名" prop="isAnonymous">
          <el-select v-model="queryParams.isAnonymous" placeholder="全部" clearable style="width:100px">
            <el-option label="匿名" :value="1" />
            <el-option label="实名" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table v-loading="listLoading" :data="list" border stripe>
        <el-table-column label="序号" align="center" type="index" width="60" />
        <el-table-column label="学员" align="center" width="100">
          <template #default="{ row }">
            <span v-if="row.isAnonymous === 1" class="anonymous-name">匿名用户</span>
            <span v-else>{{ row.studentName || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="被评教师" align="center" prop="teacherName" width="100" />
        <el-table-column label="评分" align="center" width="110">
          <template #default="{ row }">
            <el-rate :model-value="row.ratingScore" :max="5" disabled show-score size="small" />
          </template>
        </el-table-column>
        <el-table-column label="评价内容" align="center" prop="content" :show-overflow-tooltip="true" min-width="200">
          <template #default="{ row }">{{ row.content || '—' }}</template>
        </el-table-column>
        <el-table-column label="评价标签" align="center" min-width="200">
          <template #default="{ row }">
            <el-tag v-for="tag in parseTags(row.tags)" :key="tag" size="small" style="margin:2px">{{ tag }}</el-tag>
            <span v-if="!row.tags || parseTags(row.tags).length === 0">—</span>
          </template>
        </el-table-column>
        <el-table-column label="匿名" align="center" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isAnonymous === 1 ? 'warning' : 'info'" size="small">{{ row.isAnonymous === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" align="center" prop="submitTime" width="160" />
        <el-table-column label="操作" align="center" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleDetail(row)">查看</el-button>
            <el-button v-if="row.status !== 'HIDDEN'" link type="danger" size="small" @click="handleHide(row)" v-permission="['teaching:teacher-evaluation:export']">隐藏</el-button>
            <el-button v-else link type="success" size="small" @click="handleRestore(row)" v-permission="['teaching:teacher-evaluation:export']">恢复</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!listLoading && list.length === 0" description="暂无学评教记录。家长通过小程序提交评价后将在此展示。" />
      <div v-if="listError" class="error-box">
        <el-result icon="error" title="加载失败" :sub-title="listError">
          <template #extra><el-button type="primary" @click="getList">重试</el-button></template>
        </el-result>
      </div>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />
    </template>

    <!-- ==================== 评价统计 Tab ==================== -->
    <template v-if="activeTab === 'stats'">
      <div v-loading="statsLoading" class="stats-container">
        <div v-if="statsError" class="error-box">
          <el-result icon="error" title="加载失败" :sub-title="statsError">
            <template #extra><el-button type="primary" @click="loadStats">重试</el-button></template>
          </el-result>
        </div>

        <template v-if="!statsLoading && !statsError">
          <!-- 教师评分总览卡片 -->
          <h4 class="section-title">📊 综合评价概览</h4>
          <el-row :gutter="16">
            <el-col v-for="t in teacherStatsList" :key="t.teacherId" :span="6" style="margin-bottom:16px">
              <el-card shadow="hover" class="stats-card" @click="showTeacherDetail(t)">
                <div class="card-teacher-name">{{ t.teacherName }}</div>
                <div class="card-score">{{ t.avgScore || '—' }}</div>
                <div class="card-label">综合评分</div>
                <el-rate :model-value="Math.round(t.avgScore || 0)" :max="5" disabled size="small" />
                <div class="card-count">{{ t.evalCount || 0 }} 次评价</div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="teacherStatsList.length === 0" description="暂无统计数据" :image-size="80" />

          <!-- 评分分布 + 标签频率 -->
          <el-row :gutter="16" style="margin-top:24px">
            <el-col :span="12">
              <h4 class="section-title">评分分布</h4>
              <div class="chart-box">
                <div v-for="d in distribution" :key="d.label" class="bar-row">
                  <span class="bar-label">{{ d.label }}</span>
                  <div class="bar-track"><div class="bar-fill" :style="{ width: d.percent + '%' }" :class="'bar-' + d.rating" /></div>
                  <span class="bar-value">{{ d.count }}次 ({{ d.percent }}%)</span>
                </div>
                <el-empty v-if="distribution.length === 0" description="暂无评分数据" :image-size="60" />
              </div>
            </el-col>
            <el-col :span="12">
              <h4 class="section-title">评价标签频率</h4>
              <div class="chart-box">
                <div v-for="t in tagFrequency" :key="t.tag" class="bar-row">
                  <span class="bar-label">{{ t.tag }}</span>
                  <div class="bar-track"><div class="bar-fill tag-bar" :style="{ width: t.percent + '%' }" /></div>
                  <span class="bar-value">{{ t.count }}次</span>
                </div>
                <el-empty v-if="tagFrequency.length === 0" description="暂无标签数据" :image-size="60" />
              </div>
            </el-col>
          </el-row>
        </template>
      </div>
    </template>

    <TeacherEvalDetail v-model:visible="detailOpen" :eval-id="currentId" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listTeacherEvaluation, hideEvaluation, restoreEvaluation, getTeacherStats } from '@/api/teaching/teacherEvaluation';
import { listTeacher } from '@/api/teaching/teachers';
import { listStudent } from '@/api/student/students';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';
import TeacherEvalDetail from './components/TeacherEvalDetail.vue';

const activeTab = ref('list');
const showSearch = ref(true);

// ---- 明细 Tab ----
const listLoading = ref(false); const list = ref([]); const total = ref(0); const listError = ref('');
const queryParams = ref({ page: 1, size: 10, keyword: '', teacherId: '', studentId: '', isAnonymous: '' });
const dateRange = ref([]);
const teacherOptions = ref([]); const teacherLoading = ref(false);
const studentOptions = ref([]); const studentLoading = ref(false);
let querySeq = 0; let teacherSeq = 0; let studentSeq = 0;

// ---- 统计 Tab ----
const statsLoading = ref(false); const statsError = ref('');
const teacherStatsList = ref([]);
const distribution = ref([]);
const tagFrequency = ref([]);

// ---- 详情 ----
const detailOpen = ref(false); const currentId = ref(null);

watch(dateRange, () => { if (dateRange.value && dateRange.value.length === 2) { queryParams.value.startDate = dateRange.value[0]; queryParams.value.endDate = dateRange.value[1]; } else { delete queryParams.value.startDate; delete queryParams.value.endDate; } });

function parseTags(tags) {
  if (!tags) return [];
  try { return typeof tags === 'string' ? JSON.parse(tags) : tags; }
  catch { return []; }
}

// ---- 明细 Tab methods ----
function getList() {
  listLoading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value };
  ['keyword', 'teacherId', 'studentId', 'isAnonymous', 'startDate', 'endDate'].forEach(k => { if (!params[k] && params[k] !== 0) delete params[k]; });
  listTeacherEvaluation(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; listError.value = ''; }
    else { listError.value = res.msg || '加载失败'; }
  }).catch(() => { if (seq === querySeq) listError.value = '网络错误，请检查网络后重试'; })
  .finally(() => { if (seq === querySeq) listLoading.value = false; });
}
function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '', teacherId: '', studentId: '', isAnonymous: '' }; dateRange.value = []; getList(); }
function handleDetail(row) { currentId.value = row.id; detailOpen.value = true; }
function handleHide(row) {
  ElMessageBox.confirm('确认隐藏该评价？隐藏后教师和家长均不可见，但保留审计记录。', '警告', { type: 'warning' })
    .then(() => hideEvaluation(row.id)).then(res => {
      if (res.success) { getList(); ElMessage.success('评价已隐藏'); }
      else { ElMessage.error(res.msg || '操作失败'); }
    }).catch(() => {});
}
function handleRestore(row) {
  restoreEvaluation(row.id).then(res => {
    if (res.success) { getList(); ElMessage.success('评价已恢复'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  });
}

function remoteSearchTeacher(query) {
  if (!query) { teacherOptions.value = []; return; }
  teacherLoading.value = true; teacherSeq++; const seq = teacherSeq;
  listTeacher({ keyword: query }).then(res => {
    if (seq !== teacherSeq) return;
    teacherOptions.value = res.success ? (res.data?.records || res.rows || res.data || []) : [];
  }).finally(() => { if (seq === teacherSeq) teacherLoading.value = false; });
}

function remoteSearchStudent(query) {
  if (!query) { studentOptions.value = []; return; }
  studentLoading.value = true; studentSeq++; const seq = studentSeq;
  listStudent({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== studentSeq) return;
    studentOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === studentSeq) studentLoading.value = false; });
}

// ---- 统计 Tab methods ----
function loadStats() {
  statsLoading.value = true; statsError.value = '';
  // 管理员视图：传 0 获取全部教师概览；教师视图自动过滤
  getTeacherStats(0).then(res => {
    if (res.success && res.data) {
      const d = res.data.data || res.data;
      teacherStatsList.value = d.teacherList || [];
      distribution.value = (d.distribution || []).map(item => ({ ...item, percent: item.total > 0 ? Math.round(item.count / item.total * 100) : 0 }));
      tagFrequency.value = (d.tagFrequency || []).map(item => {
        const max = Math.max(...(d.tagFrequency || []).map(t => t.count), 1);
        return { ...item, percent: Math.round(item.count / max * 100) };
      });
    } else { statsError.value = res.msg || '加载失败'; }
  }).catch(() => { statsError.value = '网络错误，请检查网络后重试'; })
  .finally(() => { statsLoading.value = false; });
}

function showTeacherDetail(t) {
  // Open teacher detail stats
}

function onTabChange(tab) {
  if (tab === 'stats' && teacherStatsList.value.length === 0 && !statsError.value) loadStats();
}

onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
.anonymous-name { color: #909399; font-style: italic; }
.section-title { margin: 16px 0 12px; font-size: 15px; font-weight: 600; color: #303133; }

.stats-card { text-align: center; cursor: pointer; transition: box-shadow 0.3s; }
.stats-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
.card-teacher-name { font-size: 14px; color: #606266; margin-bottom: 8px; }
.card-score { font-size: 28px; font-weight: 700; color: #409eff; }
.card-label { font-size: 12px; color: #909399; margin-bottom: 8px; }
.card-count { margin-top: 8px; font-size: 12px; color: #c0c4cc; }

.chart-box { padding: 8px 0; }
.bar-row { display: flex; align-items: center; margin-bottom: 10px; }
.bar-label { width: 80px; font-size: 13px; color: #606266; text-align: right; margin-right: 12px; flex-shrink: 0; }
.bar-track { flex: 1; height: 20px; background: #f0f2f5; border-radius: 4px; overflow: hidden; }
.bar-fill { height: 100%; border-radius: 4px; transition: width 0.4s; }
.bar-5 { background: #67c23a; } .bar-4 { background: #85ce61; } .bar-3 { background: #e6a23c; } .bar-2 { background: #f56c6c; } .bar-1 { background: #f56c6c; }
.tag-bar { background: #409eff; }
.bar-value { width: 100px; font-size: 12px; color: #909399; margin-left: 12px; flex-shrink: 0; }
.error-box { padding: 40px 0; }
</style>
