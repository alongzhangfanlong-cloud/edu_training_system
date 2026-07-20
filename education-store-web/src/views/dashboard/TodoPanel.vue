<!-- 待办提醒面板 -->
<template>
  <el-card shadow="hover" class="todo-panel">
    <template #header>
      <div class="panel-header">
        <span class="panel-title">待办提醒</span>
        <el-badge v-if="totalCount" :value="totalCount" :max="99" class="todo-badge" />
      </div>
    </template>

    <div v-loading="loading">
      <el-empty v-if="!loading && totalCount === 0" description="暂无待办事项" :image-size="80" />

      <!-- 待审批请假 -->
      <div v-if="todos?.pendingLeaves?.items?.length" class="todo-section">
        <div class="todo-section-header" @click="toggleSection('leaves')">
          <div class="todo-section-title">
            <el-icon color="#e6a23c"><Bell /></el-icon>
            <span>待审批请假</span>
            <el-tag size="small" type="warning">{{ todos.pendingLeaves.count }}</el-tag>
          </div>
          <el-icon :class="{ rotated: !expandedSections.leaves }"><ArrowDown /></el-icon>
        </div>
        <div v-show="expandedSections.leaves" class="todo-items">
          <div
            v-for="item in todos.pendingLeaves.items" :key="'leave-' + item.leaveId"
            class="todo-item"
            @click="handleLeaveClick(item)"
          >
            <div class="todo-item-main">
              <span class="todo-item-name">{{ item.teacherName || item.studentName || '--' }}</span>
              <el-tag size="small" :type="item.applicantType === 'TEACHER' ? '' : 'success'">
                {{ item.applicantType === 'TEACHER' ? '教师' : '学生' }}
              </el-tag>
            </div>
            <div class="todo-item-sub">
              <span>{{ item.leaveTypeName }}</span>
              <span>{{ item.startDate }} ~ {{ item.endDate }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 待处理退费 -->
      <div v-if="todos?.pendingRefunds?.items?.length" class="todo-section">
        <div class="todo-section-header" @click="toggleSection('refunds')">
          <div class="todo-section-title">
            <el-icon color="#f56c6c"><Money /></el-icon>
            <span>待处理退费</span>
            <el-tag size="small" type="danger">{{ todos.pendingRefunds.count }}</el-tag>
          </div>
          <el-icon :class="{ rotated: !expandedSections.refunds }"><ArrowDown /></el-icon>
        </div>
        <div v-show="expandedSections.refunds" class="todo-items">
          <div
            v-for="item in todos.pendingRefunds.items" :key="'refund-' + item.refundId"
            class="todo-item"
            @click="$router.push('/finance/tuition')"
          >
            <div class="todo-item-main">
              <span class="todo-item-name">{{ item.studentName || '--' }}</span>
              <span class="todo-item-amount">¥{{ formatMoney(item.amount) }}</span>
            </div>
            <div class="todo-item-sub">{{ item.applyTime }}</div>
          </div>
        </div>
      </div>

      <!-- 排课冲突 -->
      <div v-if="todos?.scheduleConflicts?.items?.length" class="todo-section">
        <div class="todo-section-header" @click="toggleSection('conflicts')">
          <div class="todo-section-title">
            <el-icon color="#409eff"><WarningFilled /></el-icon>
            <span>排课冲突预警</span>
            <el-tag size="small" type="primary">{{ todos.scheduleConflicts.count }}</el-tag>
          </div>
          <el-icon :class="{ rotated: !expandedSections.conflicts }"><ArrowDown /></el-icon>
        </div>
        <div v-show="expandedSections.conflicts" class="todo-items">
          <div
            v-for="(item, idx) in todos.scheduleConflicts.items" :key="'conflict-' + idx"
            class="todo-item"
            @click="$router.push('/course/schedule')"
          >
            <div class="todo-item-main">
              <span class="todo-item-name">{{ item.description }}</span>
            </div>
            <div class="todo-item-sub">
              <span>{{ item.date }} {{ item.timeRange }}</span>
              <span>{{ item.classA }} / {{ item.classB }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Bell, Money, ArrowDown, WarningFilled } from '@element-plus/icons-vue';

const props = defineProps({
  todos: { type: Object, default: null },
  loading: { type: Boolean, default: false },
});

const router = useRouter();

const expandedSections = ref({
  leaves: true,
  refunds: true,
  conflicts: true,
});

const totalCount = computed(() => {
  if (!props.todos) return 0;
  return (props.todos.pendingLeaves?.count || 0)
    + (props.todos.pendingRefunds?.count || 0)
    + (props.todos.scheduleConflicts?.count || 0);
});

function toggleSection(key) {
  expandedSections.value[key] = !expandedSections.value[key];
}

function handleLeaveClick(item) {
  if (item.applicantType === 'TEACHER') {
    router.push('/attendance/leave/teacher');
  } else {
    router.push('/attendance/leave/student');
  }
}

function formatMoney(val) {
  if (val == null) return '--';
  const num = Number(val);
  if (Number.isNaN(num)) return '--';
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
</script>

<style scoped lang="less">
.todo-panel {
  height: 100%;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.todo-section {
  margin-bottom: 12px;

  &:last-child { margin-bottom: 0; }
}

.todo-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
  user-select: none;

  .el-icon { transition: transform 0.2s; }

  .rotated { transform: rotate(-90deg); }
}

.todo-section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.todo-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.todo-item {
  padding: 8px 10px;
  background: #fafafa;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover { background: #f0f2f5; }
}

.todo-item-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px;
}

.todo-item-name {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
}

.todo-item-amount {
  font-size: 13px;
  color: #f56c6c;
  font-weight: 600;
}

.todo-item-sub {
  display: flex;
  gap: 8px;
  font-size: 11px;
  color: #909399;
}
</style>
