<!-- 兑换记录行展开详情 — 学生信息 + 礼品信息 + 状态时间线 -->
<template>
  <div v-loading="loading" class="redemption-detail" v-if="visible">
    <div v-if="error" class="detail-error">
      <span>{{ error }}</span>
      <el-button link type="primary" size="small" @click="loadDetail">重试</el-button>
    </div>
    <template v-else-if="detail">
      <!-- 信息卡片 -->
      <el-row :gutter="16">
        <el-col :span="8">
          <div class="info-card">
            <div class="info-card__title">学生信息</div>
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="姓名">{{ studentName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ studentMobile || '-' }}</el-descriptions-item>
              <el-descriptions-item label="当前积分余额">{{ currentBalance != null ? currentBalance : '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-card">
            <div class="info-card__title">礼品信息</div>
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="礼品名称">{{ giftName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="礼品图片">
                <el-image
                  v-if="giftImageUrl"
                  :src="giftImageUrl"
                  :preview-src-list="[giftImageUrl]"
                  style="width:40px;height:40px"
                  fit="cover"
                />
                <span v-else>—</span>
              </el-descriptions-item>
              <el-descriptions-item label="兑换所需积分">{{ giftPointsRequired ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="当前库存">{{ giftStockRemaining ?? '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-card">
            <div class="info-card__title">兑换信息</div>
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="消耗积分">{{ detail.pointsSpent ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="兑换时间">{{ detail.redeemTime || detail.createTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="操作人">{{ operatorName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="当前状态">
                <el-tag :type="statusType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>
      </el-row>

      <!-- 状态时间线 -->
      <div class="info-card timeline-card">
        <div class="info-card__title">兑换时间线</div>
        <el-timeline v-if="timeline.length > 0">
          <el-timeline-item
            v-for="(item, idx) in timeline"
            :key="idx"
            :timestamp="item.time"
            :color="item.color"
            placement="top"
          >
            {{ item.content }}
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无时间线" :image-size="40" />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { getRedemption } from '@/api/points/index';
import { getGift } from '@/api/points/index';
import { getAccount } from '@/api/points/index';
import { getStudent } from '@/api/student/students';

const props = defineProps({
  redemptionId: { type: [Number, String], required: true },
  visible: { type: Boolean, default: false },
});

const loading = ref(false);
const error = ref('');
const detail = ref(null);

const studentName = ref('');
const studentMobile = ref('');
const currentBalance = ref(null);
const giftName = ref('');
const giftImageUrl = ref('');
const giftPointsRequired = ref(null);
const giftStockRemaining = ref(null);
const operatorName = computed(() => {
  if (detail.value?.operatorName) return detail.value.operatorName;
  if (detail.value?.operatorId) return `用户${detail.value.operatorId}`;
  return '';
});

const timeline = ref([]);

let loadSeq = 0;

function statusLabel(v) { const m = { PENDING_CLAIM: '待领取', CLAIMED: '已领取', CANCELLED: '已取消' }; return m[v] ?? v; }
function statusType(v) { const m = { PENDING_CLAIM: 'warning', CLAIMED: 'success', CANCELLED: 'info' }; return m[v] ?? ''; }

function loadDetail() {
  if (!props.redemptionId || !props.visible) return;
  loading.value = true; error.value = ''; loadSeq++; const seq = loadSeq;

  getRedemption(props.redemptionId).then(async res => {
    if (seq !== loadSeq) return;
    if (!res.success || !res.data) { error.value = res.msg || '加载详情失败'; return; }
    const d = res.data;
    detail.value = d;

    // 并行加载关联数据
    const [studentRes, giftRes, accountRes] = await Promise.allSettled([
      getStudent(d.studentId),
      getGift(d.giftId),
      getAccount(d.studentId),
    ]);

    if (studentRes.status === 'fulfilled' && studentRes.value?.success && studentRes.value?.data) {
      studentName.value = studentRes.value.data.name || '';
      studentMobile.value = studentRes.value.data.mobile || '';
    }
    if (giftRes.status === 'fulfilled' && giftRes.value?.success && giftRes.value?.data) {
      giftName.value = giftRes.value.data.name || '';
      giftImageUrl.value = giftRes.value.data.imageUrl || '';
      giftPointsRequired.value = giftRes.value.data.pointsRequired;
      giftStockRemaining.value = giftRes.value.data.stockRemaining;
    }
    if (accountRes.status === 'fulfilled' && accountRes.value?.success && accountRes.value?.data) {
      currentBalance.value = accountRes.value.data.balance;
    }

    // 构建时间线
    buildTimeline(d);
  }).catch(() => {
    if (seq === loadSeq) error.value = '加载详情失败';
  }).finally(() => {
    if (seq === loadSeq) loading.value = false;
  });
}

function buildTimeline(d) {
  const items = [];

  // 提交兑换
  if (d.redeemTime || d.createTime) {
    items.push({
      time: d.redeemTime || d.createTime,
      content: `提交兑换 → 待领取（消耗${d.pointsSpent ?? '?'}积分）`,
      color: '#e6a23c',
    });
  }

  // 已领取
  if (d.status === 'CLAIMED' && d.claimTime) {
    items.push({
      time: d.claimTime,
      content: `待领取 → 已领取（学生确认签收）`,
      color: '#67c23a',
    });
  }

  // 已取消
  if (d.status === 'CANCELLED' && d.cancelTime) {
    items.push({
      time: d.cancelTime,
      content: `待领取 → 已取消（退还${d.pointsSpent ?? '?'}积分并恢复库存）`,
      color: '#909399',
    });
  }

  timeline.value = items;
}

watch(() => [props.redemptionId, props.visible], () => {
  if (props.visible) loadDetail();
}, { immediate: true });
</script>

<style scoped lang="less">
.redemption-detail {
  padding: 16px 20px;
  min-height: 120px;
}
.detail-error {
  color: #f56c6c;
  display: flex;
  align-items: center;
  gap: 8px;
}
.info-card {
  margin-bottom: 12px;
  &__title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
    padding-left: 8px;
    border-left: 3px solid #409eff;
  }
}
.timeline-card {
  margin-top: 16px;
}
</style>
