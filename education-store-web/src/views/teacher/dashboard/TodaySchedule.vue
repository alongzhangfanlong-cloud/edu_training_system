<!-- 教师工作台 — 今日课表时间线 -->
<template>
  <el-card shadow="hover" class="today-schedule-panel">
    <template #header>
      <span class="panel-title">📅 我的今日课表</span>
    </template>

    <!-- 加载骨架 -->
    <div v-if="loading" class="skeleton-list">
      <div v-for="i in 4" :key="i" class="skeleton-card">
        <el-skeleton :rows="2" animated />
      </div>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="error" class="error-block">
      <el-result icon="error" title="课表加载失败" sub-title="请检查网络后重试">
        <template #extra>
          <el-button type="primary" @click="$emit('retry')">重试</el-button>
        </template>
      </el-result>
    </div>

    <!-- 无课程 -->
    <el-empty
      v-else-if="!schedules || schedules.length === 0"
      description="📅 今日无课程安排，好好休息吧 ☕"
      :image-size="100"
    />

    <!-- 课表列表 -->
    <div v-else class="schedule-list">
      <ScheduleCard
        v-for="s in schedules"
        :key="s.scheduleId"
        :schedule="s"
        :expanded="expandedId === s.scheduleId"
        @toggle-students="onToggleStudents"
        @start-signin="$emit('startSignin', s)"
        @manage-signin="$emit('manageSignin', s)"
      >
        <template #students>
          <div v-loading="studentLoading" class="student-list">
            <div class="student-summary">
              签到状态：已签到 {{ checkedCount }} / {{ totalCount }}
            </div>
            <div v-for="stu in currentStudents" :key="stu.studentId" class="student-item">
              <span>{{ stu.studentName }}</span>
              <el-tag
                v-if="stu.attendanceStatus === 'PRESENT'"
                size="small" type="success"
              >已签到</el-tag>
              <el-tag
                v-else-if="stu.attendanceStatus === 'LATE'"
                size="small" type="warning"
              >迟到</el-tag>
              <el-tag
                v-else-if="stu.attendanceStatus === 'ABSENT'"
                size="small" type="danger"
              >缺勤</el-tag>
              <el-tag
                v-else-if="stu.attendanceStatus === 'LEAVE' || stu.attendanceStatus === 'LEAVE_PENDING'"
                size="small" type="info"
              >请假</el-tag>
              <span v-else class="text-muted">未签到</span>
            </div>
          </div>
        </template>
      </ScheduleCard>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getScheduleStudents } from '@/api/teacher/dashboard.js';
import ScheduleCard from './ScheduleCard.vue';

defineProps({
  schedules: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: Boolean, default: false },
});

defineEmits(['retry', 'startSignin', 'manageSignin']);

// 学生名单展开状态
const expandedId = ref(null);
const studentLoading = ref(false);
const currentStudents = ref([]);

let studentReqId = 0;

async function onToggleStudents(scheduleId) {
  if (expandedId.value === scheduleId) {
    expandedId.value = null;
    return;
  }

  expandedId.value = scheduleId;
  studentLoading.value = true;
  currentStudents.value = [];

  const reqId = ++studentReqId;
  try {
    const res = await getScheduleStudents(scheduleId);
    if (reqId !== studentReqId) return;
    if (res.success) {
      currentStudents.value = res.data || [];
    } else {
      ElMessage.error(res.msg || '加载学生名单失败');
    }
  } catch {
    if (reqId !== studentReqId) return;
    ElMessage.error('加载学生名单失败');
  } finally {
    if (reqId === studentReqId) studentLoading.value = false;
  }
}

const totalCount = computed(() => currentStudents.value.length);
// 只统计真正已签到的学生（PRESENT + LATE），不含 ABSENT/LEAVE/LEAVE_PENDING
const checkedCount = computed(() =>
  currentStudents.value.filter(s =>
    s.attendanceStatus === 'PRESENT' || s.attendanceStatus === 'LATE'
  ).length
);
</script>

<style scoped lang="less">
.today-schedule-panel {
  height: 100%;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-card {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.error-block {
  padding: 20px 0;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.student-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.student-summary {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.student-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 8px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;
  color: #303133;
}

.text-muted { color: #c0c4cc; }
</style>
