<!-- Tab5: 财务对账（只读统计） -->
<template>
  <div v-loading="loading">
    <el-form :model="queryParams" size="small" :inline="true">
      <el-form-item label="日期范围">
        <el-date-picker v-model="queryParams.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" unlink-panels style="width:240px" />
      </el-form-item>
      <el-form-item label="费用类型">
        <el-select v-model="queryParams.feeType" placeholder="全部" clearable style="width:130px">
          <el-option label="报名费" value="REGISTRATION" />
          <el-option label="课时费" value="TUITION" />
          <el-option label="教材费" value="MATERIAL" />
          <el-option label="其他" value="OTHER" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>查询</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header><span>总收入</span></template>
          <div class="stat-value">{{ formatMoney(stats.totalIncome) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header><span>退费总额</span></template>
          <div class="stat-value" style="color:#f56c6c">{{ formatMoney(stats.totalRefund) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header><span>净收入</span></template>
          <div class="stat-value" style="color:#67c23a">{{ formatMoney(stats.netIncome) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header><span>收费笔数</span></template>
          <div class="stat-value">{{ stats.feeCount }} 笔</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 费用类型汇总 -->
    <el-card class="mt16">
      <template #header><span>按费用类型汇总</span></template>
      <el-table :data="feeTypeStats" border stripe size="small">
        <el-table-column label="费用类型" prop="label" width="120" />
        <el-table-column label="笔数" prop="count" width="100" align="center" />
        <el-table-column label="金额" prop="amount" align="right">
          <template #default="{ row }">{{ formatMoney(row.amount) }}</template>
        </el-table-column>
        <el-table-column label="占比" min-width="200">
          <template #default="{ row }">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: row.percent + '%' }"></div>
              <span class="progress-text">{{ row.percent }}%</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 赠送课时汇总 -->
    <el-card class="mt16">
      <template #header><span>赠送课时统计</span></template>
      <el-table :data="giftStats" border stripe size="small">
        <el-table-column label="赠送原因" prop="label" width="120" />
        <el-table-column label="课时数" prop="hours" width="100" align="center" />
        <el-table-column label="笔数" prop="count" width="100" align="center" />
      </el-table>
      <el-empty v-if="giftStats.length === 0" description="暂无赠送课时数据" :image-size="60" />
    </el-card>

    <el-empty v-if="!loading && feeTypeStats.length === 0" description="暂无财务对账数据，请先录入收费和退费记录" />
    <div v-if="error" class="error-box">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra><el-button type="primary" @click="loadData">重试</el-button></template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listFee } from '@/api/finance/tuitionFees';
import { listRefund } from '@/api/finance/tuitionRefunds';
import { listGift } from '@/api/finance/tuitionGifts';

const loading = ref(false);
const error = ref('');
const queryParams = ref({ dateRange: [], feeType: '' });

const stats = reactive({ totalIncome: 0, totalRefund: 0, netIncome: 0, feeCount: 0 });
const feeTypeStats = ref([]);
const giftStats = ref([]);

let querySeq = 0;

function formatMoney(val) { return '¥' + (Number(val) || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2 }); }

function resetStats() {
  stats.totalIncome = 0; stats.totalRefund = 0; stats.netIncome = 0; stats.feeCount = 0;
  feeTypeStats.value = []; giftStats.value = [];
}

function loadData() {
  // 重置旧数据，避免显示陈旧结果
  resetStats();
  loading.value = true; error.value = '';
  querySeq++; const seq = querySeq;

  const feeParams = { page: 1, pageSize: 9999, status: 'PAID' };
  const refundParams = { page: 1, pageSize: 9999 };
  const giftParams = { page: 1, pageSize: 9999 };

  Promise.all([
    listFee(feeParams),
    listRefund(refundParams),
    listGift(giftParams),
  ]).then(([feeRes, refundRes, giftRes]) => {
    if (seq !== querySeq) return;

    const fees = feeRes.success ? (feeRes.data?.records || feeRes.rows || []) : [];
    const refunds = refundRes.success ? (refundRes.data?.records || refundRes.rows || []) : [];
    const gifts = giftRes.success ? (giftRes.data?.records || giftRes.rows || []) : [];

    // 费用类型过滤
    let filteredFees = fees;
    if (queryParams.value.feeType) {
      filteredFees = fees.filter(f => f.feeType === queryParams.value.feeType);
    }
    // 日期范围过滤
    if (queryParams.value.dateRange && queryParams.value.dateRange.length === 2) {
      const [start, end] = queryParams.value.dateRange;
      filteredFees = filteredFees.filter(f => f.payTime >= start && f.payTime <= end + ' 23:59:59');
    }

    // 四大卡片
    const totalIncome = filteredFees.reduce((sum, f) => sum + (Number(f.amountPaid) || 0), 0);
    const totalRefund = refunds.filter(r => r.status === 'COMPLETED')
      .reduce((sum, r) => sum + (Number(r.amount) || 0), 0);
    stats.totalIncome = totalIncome;
    stats.totalRefund = totalRefund;
    stats.netIncome = totalIncome - totalRefund;
    stats.feeCount = filteredFees.length;

    // 费用类型分布
    const typeMap = { REGISTRATION: '报名费', TUITION: '课时费', MATERIAL: '教材费', OTHER: '其他' };
    const typeAgg = {};
    filteredFees.forEach(f => {
      const key = f.feeType || 'OTHER';
      if (!typeAgg[key]) typeAgg[key] = { count: 0, amount: 0 };
      typeAgg[key].count++;
      typeAgg[key].amount += Number(f.amountPaid) || 0;
    });
    const totalAmount = Object.values(typeAgg).reduce((s, v) => s + v.amount, 0);
    feeTypeStats.value = Object.entries(typeAgg).map(([k, v]) => ({
      key: k, label: typeMap[k] || k,
      count: v.count, amount: v.amount,
      percent: totalAmount > 0 ? Math.round(v.amount / totalAmount * 100) : 0,
    }));

    // 赠送课时统计
    const giftReasonMap = { PROMOTION: '促销活动', COMPENSATION: '补偿', OTHER: '其他' };
    const giftAgg = {};
    gifts.forEach(g => {
      const key = g.reason || 'OTHER';
      if (!giftAgg[key]) giftAgg[key] = { hours: 0, count: 0 };
      giftAgg[key].hours += Number(g.hours) || 0;
      giftAgg[key].count++;
    });
    giftStats.value = Object.entries(giftAgg).map(([k, v]) => ({
      key: k, label: giftReasonMap[k] || k, hours: v.hours, count: v.count,
    }));
  }).catch(() => { if (seq === querySeq) error.value = '数据加载失败'; })
  .finally(() => { if (seq === querySeq) loading.value = false; });
}

function handleQuery() { loadData(); }
function resetQuery() { queryParams.value = { dateRange: [], feeType: '' }; loadData(); }

onMounted(() => { loadData(); });
</script>

<style scoped lang="less">
.stat-cards { margin-bottom: 16px; }
.stat-value { font-size: 24px; font-weight: bold; text-align: center; padding: 8px 0; }
.mt16 { margin-top: 16px; }
.progress-bar { display: flex; align-items: center; gap: 8px; }
.progress-fill { height: 16px; background: #409eff; border-radius: 4px; min-width: 2px; }
.progress-text { font-size: 12px; color: #909399; white-space: nowrap; }
.error-box { margin: 20px 0; }
</style>
