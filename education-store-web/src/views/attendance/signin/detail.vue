<!-- 签到详情页 — 某次排课的学生签到明细 -->
<template>
  <div class="app-container">
    <el-page-header @back="goBack">
      <template #content>
        <span class="page-title">签到详情</span>
      </template>
    </el-page-header>

    <!-- 排课信息卡片 -->
    <el-card class="info-card" shadow="never">
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="日期">{{ scheduleInfo.scheduleDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="时间段">{{ scheduleInfo.startTime }} - {{ scheduleInfo.endTime }}</el-descriptions-item>
        <el-descriptions-item label="课程">{{ scheduleInfo.courseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ scheduleInfo.className || '-' }}</el-descriptions-item>
        <el-descriptions-item label="教师">{{ scheduleInfo.teacherName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <dict-tag :options="dicts.schedule_type" :value="scheduleInfo.scheduleType" />
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 签到汇总 -->
    <el-card class="stats-card" shadow="never">
      <template #header>签到汇总</template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-num">{{ stats.total || 0 }}</div>
            <div class="stat-label">应到</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item stat-present">
            <div class="stat-num">{{ stats.present || 0 }}</div>
            <div class="stat-label">到校</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item stat-late">
            <div class="stat-num">{{ stats.late || 0 }}</div>
            <div class="stat-label">迟到</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item stat-absent">
            <div class="stat-num">{{ (stats.absent || 0) + (stats.leave || 0) }}</div>
            <div class="stat-label">缺勤/请假</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 学生签到明细表格 -->
    <el-card shadow="never">
      <template #header>
        <span>学生签到明细</span>
        <el-button size="small" type="primary" link style="float:right" @click="refreshList">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </template>

      <el-table v-loading="loading" :data="detailList" border stripe>
        <el-table-column label="序号" align="center" width="60" type="index" />
        <el-table-column label="学生姓名" align="center" prop="studentName" min-width="100" />
        <el-table-column label="签到状态" align="center" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="success" size="small">到校</el-tag>
            <el-tag v-else-if="row.status === 1" type="warning" size="small">迟到</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger" size="small">缺勤</el-tag>
            <el-tag v-else-if="row.status === 3" type="info" size="small">请假</el-tag>
            <el-tag v-else-if="row.status === 4" type="warning" size="small">请假未审批</el-tag>
            <el-tag v-else type="info" size="small">未签到</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到时间" align="center" prop="signinTime" width="170" />
        <el-table-column label="操作端" align="center" width="90">
          <template #default="{ row }">
            {{ row.operatorType === 'PC' ? 'PC端' : row.operatorType === 'MINI_PROGRAM' ? '小程序' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" min-width="120" />
      </el-table>

      <el-empty v-if="!loading && detailList.length === 0" description="暂无签到记录" />
      <div v-if="error" class="error-box">
        <el-result icon="error" title="加载失败" :sub-title="error">
          <template #extra><el-button type="primary" @click="refreshList">重试</el-button></template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Refresh } from '@element-plus/icons-vue';
import { listSignin, getSigninStats } from '@/api/attendance/signin';
import { listStudent } from '@/api/student/students';
import { useDictsStore } from '@/stores/dicts';
import DictTag from '@/components/DictTag/index.vue';

const route = useRoute();
const router = useRouter();
const { dicts, loadDict } = useDictsStore();

const scheduleId = ref(route.params.id);
const scheduleInfo = ref({
  scheduleDate: route.query.scheduleDate || '',
  startTime: route.query.startTime || '',
  endTime: route.query.endTime || '',
  courseName: route.query.courseName || '',
  className: route.query.className || '',
  teacherName: route.query.teacherName || '',
  scheduleType: ''
});

const stats = ref({ total: 0, present: 0, late: 0, absent: 0, leave: 0 });
const loading = ref(false);
const detailList = ref([]);
const error = ref('');

function goBack() { router.push('/attendance/signin'); }

async function refreshList() {
  loading.value = true;
  error.value = '';

  try {
    // 并行请求签到记录和统计
    const [listRes, statsRes] = await Promise.allSettled([
      listSignin({ page: 1, pageSize: 500, scheduleId: Number(scheduleId.value) }),
      getSigninStats(Number(scheduleId.value))
    ]);

    // 处理签到记录
    if (listRes.status === 'fulfilled' && listRes.value.success) {
      const records = listRes.value.data?.records || listRes.value.rows || [];
      // 补充学生姓名
      detailList.value = records;
      if (records.length > 0) {
        const studentIds = [...new Set(records.map(r => r.studentId).filter(Boolean))];
        if (studentIds.length > 0) {
          try {
            const studentMap = {};
            const sRes = await listStudent({ page: 1, pageSize: 500 });
            if (sRes.success) {
              const students = sRes.data?.records || sRes.rows || [];
              students.forEach(s => { studentMap[s.id] = s.name; });
            }
            detailList.value = records.map(r => ({ ...r, studentName: studentMap[r.studentId] || `学员${r.studentId}` }));
          } catch { /* 静默处理 */ }
        }
      }
    } else if (listRes.status === 'rejected') {
      error.value = '网络错误，请检查网络后重试';
    } else {
      error.value = listRes.value.msg || '加载失败';
    }

    // 处理统计
    if (statsRes.status === 'fulfilled' && statsRes.value.success) {
      stats.value = statsRes.value.data || {};
    }
  } finally {
    loading.value = false;
  }
}

onMounted(() => { loadDict('schedule_type'); refreshList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.page-title { font-size: 16px; font-weight: 600; }
.info-card { margin: 16px 0; }
.stats-card { margin-bottom: 16px; }
.stat-item { text-align: center; padding: 12px 0; }
.stat-num { font-size: 28px; font-weight: 700; line-height: 1.2; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.stat-present .stat-num { color: #67c23a; }
.stat-late .stat-num { color: #e6a23c; }
.stat-absent .stat-num { color: #f56c6c; }
.error-box { margin: 20px 0; }
</style>
