<template>
  <view class="teaching-page">
    <view class="teaching-tabs">
      <text :class="{ active: currentTab === 'eval' }" @click="currentTab = 'eval'">评价</text>
      <text :class="{ active: currentTab === 'homework' }" @click="currentTab = 'homework'">作业</text>
    </view>
    <eval-list v-if="currentTab === 'eval'" />
    <homework-list v-else />
  </view>
</template>

<script setup>
import { ref } from 'vue';
import EvalList from './eval-list.vue';
import HomeworkList from './homework-list.vue';

const currentTab = ref('eval');
</script>

<style scoped lang="scss">
.teaching-page { min-height: 100vh; }
.teaching-tabs { display: flex; background: #fff; border-bottom: 1rpx solid #ebeef5; }
.teaching-tabs text { flex: 1; text-align: center; padding: 24rpx 0; font-size: 30rpx; color: #909399; }
.teaching-tabs text.active { color: #409eff; font-weight: 600; border-bottom: 4rpx solid #409eff; }
</style>
