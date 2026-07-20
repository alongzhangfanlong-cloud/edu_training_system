<!-- 教师工作台 — 待办区 -->
<template>
  <el-card shadow="hover" class="todo-panel">
    <template #header>
      <div class="panel-header">
        <span class="panel-title">📋 待办区</span>
        <el-badge v-if="totalCount" :value="totalCount" :max="99" />
      </div>
    </template>

    <div v-loading="loading">
      <!-- 无待办 -->
      <el-empty v-if="!loading && totalCount === 0" description="✓ 暂无待办事项，今天一切顺利！" :image-size="80" />

      <!-- 待批改作业 -->
      <div
        v-if="todos?.pendingGradingCount != null"
        class="todo-item"
        :class="{ 'is-disabled': !todos.pendingGradingCount }"
        @click="todos.pendingGradingCount && handleGrading()"
      >
        <div class="todo-left">
          <el-icon color="#e6a23c" :size="20"><EditPen /></el-icon>
          <div class="todo-info">
            <div class="todo-label">待批改作业</div>
            <div class="todo-desc">{{ todos.pendingGradingCount }} 份作业等待批改</div>
          </div>
        </div>
        <el-button
          size="small"
          :disabled="!todos.pendingGradingCount"
          @click.stop="handleGrading"
        >查看 →</el-button>
      </div>

      <!-- 待回复请假 -->
      <div
        v-if="todos?.pendingLeaveReplyCount != null"
        class="todo-item"
        :class="{ 'is-disabled': !todos.pendingLeaveReplyCount }"
        @click="todos.pendingLeaveReplyCount && handleLeave()"
      >
        <div class="todo-left">
          <el-icon color="#f56c6c" :size="20"><Bell /></el-icon>
          <div class="todo-info">
            <div class="todo-label">待回复请假</div>
            <div class="todo-desc">{{ todos.pendingLeaveReplyCount }} 条请假待回复</div>
          </div>
        </div>
        <el-button
          size="small"
          :disabled="!todos.pendingLeaveReplyCount"
          @click.stop="handleLeave"
        >处理 →</el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { EditPen, Bell } from '@element-plus/icons-vue';

const props = defineProps({
  todos: { type: Object, default: null },
  loading: { type: Boolean, default: false },
});

const router = useRouter();

const totalCount = computed(() => {
  if (!props.todos) return 0;
  return (props.todos.pendingGradingCount || 0) + (props.todos.pendingLeaveReplyCount || 0);
});

function handleGrading() {
  router.push('/teacher/homework');
}

function handleLeave() {
  router.push('/attendance/leave/student');
}
</script>

<style scoped lang="less">
.todo-panel { height: 100%; }

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

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  margin-bottom: 10px;
  background: #fafafa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover { background: #f0f2f5; transform: translateY(-1px); }
  &:last-child { margin-bottom: 0; }

  &.is-disabled {
    cursor: default;
    opacity: 0.6;
    &:hover { transform: none; }
  }
}

.todo-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.todo-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.todo-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.todo-desc {
  font-size: 12px;
  color: #909399;
}
</style>
