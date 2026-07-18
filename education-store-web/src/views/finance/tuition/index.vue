<!-- N06.1 款项管理主页面：5个Tab切换 -->
<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="报名录入" name="enrollment">
        <EnrollmentTab />
      </el-tab-pane>
      <el-tab-pane label="收费管理" name="fee">
        <TuitionFeeTab />
      </el-tab-pane>
      <el-tab-pane label="退费处理" name="refund">
        <RefundTab />
      </el-tab-pane>
      <el-tab-pane label="赠送课时" name="gift">
        <GiftTab />
      </el-tab-pane>
      <el-tab-pane label="财务对账" name="reconciliation">
        <ReconciliationTab />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import EnrollmentTab from './components/EnrollmentTab.vue';
import TuitionFeeTab from './components/TuitionFeeTab.vue';
import RefundTab from './components/RefundTab.vue';
import GiftTab from './components/GiftTab.vue';
import ReconciliationTab from './components/ReconciliationTab.vue';

const route = useRoute();
const router = useRouter();

const activeTab = ref(route.query.tab || 'fee');

function onTabChange(name) {
  router.replace({ query: { tab: name } });
}
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
</style>
