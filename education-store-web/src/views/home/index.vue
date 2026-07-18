<!--
 * 首页 — 新艺智源管理平台
-->
<template>
  <div class="page-container">
    <div class="welcome-section">
      <h2>欢迎使用 新艺智源管理平台</h2>
      <p>在线教育课程展示与交易平台</p>
    </div>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>系统管理</template>
          <div>用户、角色、菜单、字典、参数、通知公告、操作日志等基础管理功能。</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>待建设</template>
          <div>课程管理、订单交易等核心业务模块即将上线。</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>欢迎</template>
          <div>{{ userStore.realName || userStore.username }}，欢迎回来。</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
</script>

<style scoped lang="less">
.page-container {
  padding: 24px;
}

.welcome-section {
  text-align: center;
  padding: 40px 0 20px;

  h2 {
    font-size: 24px;
    color: #303133;
    margin: 0 0 8px;
  }

  p {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}
</style>
