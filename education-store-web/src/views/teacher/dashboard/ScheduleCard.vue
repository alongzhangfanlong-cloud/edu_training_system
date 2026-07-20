<!-- 教师工作台 — 单条课表卡片 -->
<template>
  <div class="schedule-card" :class="'status-' + (schedule.courseStatus || '').toLowerCase()">
    <!-- 左侧状态条 -->
    <div class="card-bar" />

    <div class="card-body">
      <!-- 时间行 -->
      <div class="card-time-row">
        <span class="card-time">{{ schedule.startTime || '--' }} - {{ schedule.endTime || '--' }}</span>
        <el-tag
          v-if="schedule.courseStatus === 'IN_PROGRESS'"
          size="small"
          type="warning"
          effect="dark"
        >进行中</el-tag>
        <el-tag
          v-else-if="schedule.courseStatus === 'FINISHED'"
          size="small"
          type="info"
        >已结束</el-tag>
      </div>

      <!-- 课程信息 -->
      <div class="card-info">
        <span class="card-course">{{ schedule.courseName || '--' }}</span>
        <el-divider direction="vertical" />
        <span class="card-class">{{ schedule.className || '--' }}</span>
        <el-divider direction="vertical" />
        <span class="card-room">{{ schedule.classroomName || '未分配' }}</span>
      </div>

      <!-- 签到状态 + 应到人数 -->
      <div class="card-att-row">
        <span
          class="card-students"
          :class="{ 'is-clickable': schedule.expectedCount > 0 }"
          @click="schedule.expectedCount > 0 && $emit('toggleStudents', schedule.scheduleId)"
        >
          应到 <strong>{{ schedule.expectedCount || 0 }}</strong> 人
          <el-icon v-if="schedule.expectedCount > 0" class="expand-icon" :class="{ rotated: expanded }">
            <ArrowDown />
          </el-icon>
        </span>

        <!-- 签到状态标签 -->
        <template v-if="schedule.attendanceStatus === 'COMPLETED'">
          <el-tag size="small" type="success">✅ 已签到 {{ schedule.checkedCount }}/{{ schedule.expectedCount }}</el-tag>
        </template>
        <template v-else-if="schedule.attendanceStatus === 'PARTIAL'">
          <el-tag size="small" type="warning">已签到 {{ schedule.checkedCount }}/{{ schedule.expectedCount }}</el-tag>
          <el-button size="small" type="primary" @click.stop="$emit('manageSignin', schedule)">签到管理</el-button>
        </template>
        <template v-else-if="schedule.attendanceStatus === 'UNCHECKED'">
          <el-tag size="small" type="danger">未签到</el-tag>
          <el-button size="small" type="primary" @click.stop="$emit('startSignin', schedule)">可签到</el-button>
        </template>
        <template v-else>
          <span class="text-muted">--</span>
        </template>
      </div>

      <!-- 展开的学生名单（插槽） -->
      <div v-if="expanded" class="card-students-expand">
        <slot name="students" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ArrowDown } from '@element-plus/icons-vue';

defineProps({
  schedule: { type: Object, required: true },
  expanded: { type: Boolean, default: false },
});

defineEmits(['toggleStudents', 'startSignin', 'manageSignin']);
</script>

<style scoped lang="less">
.schedule-card {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #ebeef5;
  transition: all 0.2s;

  &.status-in_progress {
    border-left: none;
    .card-bar { background: #e6a23c; }
  }

  &.status-finished {
    opacity: 0.65;
    .card-bar { background: #c0c4cc; }
  }

  &.status-not_started {
    .card-bar { background: #409eff; }
  }

  &:hover { box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); }
}

.card-bar {
  width: 4px;
  flex-shrink: 0;
  transition: background 0.2s;
}

.card-body {
  flex: 1;
  padding: 14px 16px;
  min-width: 0;
}

.card-time-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.card-time {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.card-info {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 10px;
  font-size: 13px;
  color: #606266;

  .card-course { font-weight: 500; color: #303133; }
  .card-room { color: #909399; }

  :deep(.el-divider--vertical) { height: 14px; margin: 0 4px; }
}

.card-att-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.card-students {
  font-size: 13px;
  color: #606266;
  display: inline-flex;
  align-items: center;
  gap: 4px;

  &.is-clickable { cursor: pointer; color: #409eff; }
  strong { font-size: 15px; }
}

.expand-icon {
  font-size: 12px;
  transition: transform 0.2s;
  &.rotated { transform: rotate(-180deg); }
}

.card-students-expand {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #ebeef5;
}

.text-muted { color: #c0c4cc; font-size: 13px; }
</style>
