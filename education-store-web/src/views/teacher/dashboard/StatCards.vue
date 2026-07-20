<!-- 教师工作台 — 统计卡片行 -->
<template>
  <el-row :gutter="16" class="stat-row">
    <el-col :xs="24" :sm="8" v-for="card in cards" :key="card.key">
      <el-card shadow="hover" class="stat-card" @click="card.link ? $router.push(card.link) : null">
        <div class="stat-inner">
          <div class="stat-icon" :style="{ background: card.bg }">
            <el-icon :size="28"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-value">
              <span v-loading="loading">{{ card.formatter(stats) }}</span>
            </div>
            <div v-if="card.link" class="stat-link">{{ card.linkText }}</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { Notebook, Checked, Star } from '@element-plus/icons-vue';

defineProps({
  stats: { type: Object, default: null },
  loading: { type: Boolean, default: false },
});

const cards = [
  {
    key: 'todayCourseCount',
    label: '今日课程',
    icon: Notebook,
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    link: '/teacher/my-schedule',
    linkText: '查看课表 →',
    formatter: (s) => s?.todayCourseCount ?? '--',
  },
  {
    key: 'todayUncheckedClassCount',
    label: '待签到班级',
    icon: Checked,
    bg: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
    link: '/attendance/signin',
    linkText: '去签到 →',
    formatter: (s) => s?.todayUncheckedClassCount ?? '--',
  },
  {
    key: 'pendingEvaluationCount',
    label: '待评价学生',
    icon: Star,
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    link: '/teacher/homework',
    linkText: '去评价 →',
    formatter: (s) => s?.pendingEvaluationCount ?? '--',
  },
];
</script>

<style scoped lang="less">
.stat-row { margin-bottom: 16px; }

.stat-card {
  :deep(.el-card__body) { padding: 16px 20px; cursor: pointer; }
  transition: transform 0.2s;
  &:hover { transform: translateY(-2px); }
}

.stat-inner {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-body { flex: 1; min-width: 0; }

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-link {
  font-size: 12px;
  color: #409eff;
  margin-top: 4px;
}
</style>
