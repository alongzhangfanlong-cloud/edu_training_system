<!-- 课时记录弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="`课时记录 — ${studentName}`" width="750px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <!-- 课时余额 -->
    <el-descriptions :column="3" border style="margin-bottom: 16px">
      <el-descriptions-item label="总课时">{{ balance.totalHours ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="已消耗">{{ balance.consumedHours ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="剩余课时">
        <el-tag type="warning">{{ balance.remainingHours ?? '-' }}</el-tag>
      </el-descriptions-item>
    </el-descriptions>

    <!-- 手动调整课时 -->
    <div style="margin-bottom: 16px">
      <el-button type="primary" plain size="small" @click="adjustOpen = true" v-permission="['student:hours:add']">
        <el-icon><Plus /></el-icon>手动调整课时
      </el-button>
    </div>

    <!-- 课时变动记录列表 -->
    <el-table v-loading="loading" :data="logList" border stripe>
      <el-table-column label="变动类型" align="center" prop="changeType" width="100">
        <template #default="{ row }">
          <el-tag :type="row.changeAmount > 0 ? 'success' : 'danger'" size="small">{{ row.changeType || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="变动课时" align="center" prop="changeAmount" width="100">
        <template #default="{ row }">
          <span :style="{ color: row.changeAmount > 0 ? '#67c23a' : '#f56c6c' }">{{ row.changeAmount > 0 ? '+' : '' }}{{ row.changeAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余课时" align="center" prop="balanceAfter" width="100" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="来源业务" align="center" prop="sourceType" width="120" />
      <el-table-column label="操作时间" align="center" prop="createTime" width="160" />
    </el-table>

    <pagination
      v-show="total > 0" :total="total"
      v-model:page="queryParams.page" v-model:limit="queryParams.pageSize"
      @pagination="fetchLogs"
    />

    <!-- 手动调整课时弹窗 -->
    <el-dialog :model-value="adjustOpen" title="手动调整课时" width="400px" destroy-on-close append-to-body @update:model-value="(val) => adjustOpen = val">
      <el-form ref="adjustFormRef" :model="adjustForm" :rules="adjustRules" label-width="90px">
        <el-form-item label="变动方向" prop="direction">
          <el-radio-group v-model="adjustForm.direction">
            <el-radio value="INCREASE">增加(+)</el-radio>
            <el-radio value="DECREASE">减少(-)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="课时数" prop="hours">
          <el-input-number v-model="adjustForm.hours" :min="0.5" :step="0.5" :precision="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="原因" prop="remark">
          <el-input v-model="adjustForm.remark" type="textarea" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustOpen = false">取消</el-button>
        <el-button type="primary" @click="handleAdjust" :loading="adjustSubmitting">确定</el-button>
      </template>
    </el-dialog>

    <template #footer>
      <el-button @click="emit('update:visible', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { listHoursLog, getHoursBalance, adjustHours } from '@/api/student/hoursLogs';
import Pagination from '@/components/Pagination/index.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  studentId: { type: [Number, String], default: null },
  studentName: { type: String, default: '' },
});
const emit = defineEmits(['update:visible']);

const loading = ref(false);
const logList = ref([]);
const total = ref(0);
const balance = ref({ totalHours: null, consumedHours: null, remainingHours: null });
const queryParams = ref({ page: 1, pageSize: 10 });

const adjustOpen = ref(false);
const adjustFormRef = ref(null);
const adjustSubmitting = ref(false);
const adjustForm = ref({ direction: 'INCREASE', hours: 1, remark: '' });
const adjustRules = {
  direction: [{ required: true, message: '请选择变动方向', trigger: 'change' }],
  hours: [{ required: true, message: '请输入课时数', trigger: 'blur' }],
  remark: [{ required: true, message: '请输入调整原因', trigger: 'blur' }],
};

let querySeq = 0;

function onOpen() {
  if (props.studentId) {
    fetchBalance();
    fetchLogs();
  }
}

function fetchBalance() {
  getHoursBalance(props.studentId).then(res => {
    if (res.success) balance.value = res.data || {};
  });
}

function fetchLogs() {
  if (!props.studentId) return;
  loading.value = true;
  querySeq++;
  const seq = querySeq;
  listHoursLog(props.studentId, queryParams.value).then(res => {
    if (seq !== querySeq) return;
    if (res.success) {
      logList.value = res.data?.records || res.rows || [];
      total.value = res.data?.total || res.total || 0;
    }
  }).finally(() => {
    if (seq === querySeq) loading.value = false;
  });
}

async function handleAdjust() {
  const valid = await adjustFormRef.value?.validate().catch(() => false);
  if (!valid) return;
  adjustSubmitting.value = true;
  try {
    const { direction, hours, remark } = adjustForm.value;
    const payload = {
      changeType: 'MANUAL_ADJUST',
      changeAmount: direction === 'INCREASE' ? hours : -hours,
      remark,
    };
    const res = await adjustHours(props.studentId, payload);
    if (res.success) {
      ElMessage.success('课时调整成功');
      adjustOpen.value = false;
      fetchBalance();
      fetchLogs();
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } finally { adjustSubmitting.value = false; }
}
</script>
