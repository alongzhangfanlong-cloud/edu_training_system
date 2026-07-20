<!-- 教师工作台 — 快捷入口 -->
<template>
  <el-card shadow="hover" class="quick-panel">
    <template #header>
      <span class="panel-title">⚡ 快捷入口</span>
    </template>
    <div class="quick-actions">
      <div v-permission="['course:resource:add']" class="quick-item" @click="$router.push('/teacher/resource')">
        <el-icon :size="22"><UploadFilled /></el-icon>
        <span>课件上传</span>
      </div>
      <div v-permission="['schedule:calendar:list']" class="quick-item" @click="$router.push('/teacher/my-schedule')">
        <el-icon :size="22"><Calendar /></el-icon>
        <span>完整课表</span>
      </div>
      <div v-permission="['teaching:homework:add']" class="quick-item" @click="$router.push('/teacher/homework')">
        <el-icon :size="22"><EditPen /></el-icon>
        <span>布置作业</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { UploadFilled, Calendar, EditPen } from '@element-plus/icons-vue';
</script>

<style scoped lang="less">
.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.quick-actions {
  display: flex;
  gap: 12px;
}

.quick-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 8px;
  border-radius: 8px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.2s;
  color: #606266;
  font-size: 13px;

  &:hover {
    background: #ecf5ff;
    color: #409eff;
    transform: translateY(-1px);
  }
}
</style>
