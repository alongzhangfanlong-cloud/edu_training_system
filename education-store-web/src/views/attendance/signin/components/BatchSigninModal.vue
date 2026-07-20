<!-- 批量签到弹窗 -->
<template>
  <el-dialog
    :model-value="visible" @update:model-value="$emit('update:visible', $event)"
    :title="`批量签到 - ${scheduleInfo.courseName || ''} · ${scheduleInfo.className || ''}`"
    width="700px" destroy-on-close append-to-body @open="onOpen"
  >
    <div v-if="scheduleInfo.scheduleDate" class="schedule-info">
      <span>{{ scheduleInfo.scheduleDate }}</span>
      <el-divider direction="vertical" />
      <span>{{ scheduleInfo.startTime }} - {{ scheduleInfo.endTime }}</span>
    </div>

    <el-alert v-if="submitError" :title="submitError" type="error" show-icon class="mb8" @close="submitError = ''" />

    <el-table v-loading="loading" :data="studentList" border stripe max-height="400">
      <el-table-column label="学生姓名" align="center" prop="studentName" min-width="100" />
      <el-table-column label="签到状态" align="center" width="220">
        <template #default="{ row }">
          <template v-if="row.leaveStatus === 'APPROVED'">
            <el-tag type="info" size="small">已请假（不可更改）</el-tag>
          </template>
          <template v-else-if="row.leaveStatus === 'PENDING'">
            <el-tag type="warning" size="small">请假未审批</el-tag>
            <el-select v-model="row.signinStatus" size="small" style="width:90px;margin-left:4px" :disabled="true">
              <el-option label="请假" :value="3" />
            </el-select>
          </template>
          <template v-else>
            <el-select v-model="row.signinStatus" size="small" style="width:120px">
              <el-option label="到校" :value="0" />
              <el-option label="迟到" :value="1" />
              <el-option label="缺勤" :value="2" />
            </el-select>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" min-width="120">
        <template #default="{ row }">
          <el-input
            v-if="row.signinStatus === 2"
            v-model="row.remark" size="small" placeholder="缺勤原因（选填）"
          />
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && studentList.length === 0" description="该班级暂无在读学员" />

    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit" :disabled="studentList.length === 0">
        确认签到（{{ studentList.length }}人）
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { batchSignin } from '@/api/attendance/signin';
import { listClassStudent } from '@/api/class/classes';
import { useUserStore } from '@/stores/user';

const props = defineProps({
  visible: { type: Boolean, default: false },
  scheduleId: { type: [Number, String], default: null },
  classId: { type: [Number, String], default: null },
  scheduleInfo: { type: Object, default: () => ({}) }
});

const emit = defineEmits(['update:visible', 'submit-success']);

const loading = ref(false);
const submitting = ref(false);
const submitError = ref('');
const studentList = ref([]);

async function onOpen() {
  submitError.value = '';
  studentList.value = [];
  if (!props.classId) return;

  loading.value = true;
  try {
    const res = await listClassStudent(props.classId, { page: 1, pageSize: 500 });
    if (res.success) {
      const students = (res.data?.records || res.rows || res.data || []).map(s => ({
        ...s,
        signinStatus: 0,  // 默认到校
        remark: '',
        leaveStatus: s.leaveStatus || null  // 后端可能返回请假状态
      }));
      studentList.value = students;
    } else {
      submitError.value = '加载学员列表失败：' + (res.msg || '未知错误');
    }
  } catch {
    submitError.value = '加载学员列表失败，请检查网络';
  } finally {
    loading.value = false;
  }
}

async function handleSubmit() {
  if (!props.scheduleId || !props.classId) {
    submitError.value = '缺少排课或班级信息';
    return;
  }

  const records = studentList.value
    .filter(s => s.leaveStatus !== 'APPROVED')
    .map(s => ({
      scheduleId: Number(props.scheduleId),
      classId: Number(props.classId),
      studentId: s.studentId,
      status: s.leaveStatus === 'PENDING' ? 4 : s.signinStatus,
      remark: s.remark || undefined
    }));

  if (records.length === 0) {
    submitError.value = '没有需要签到的学员';
    return;
  }

  submitting.value = true;
  submitError.value = '';
  try {
    const userStore = useUserStore();
    const res = await batchSignin(Number(props.scheduleId), Number(props.classId), records, userStore.id || 0);
    if (res.success) {
      ElMessage.success('签到成功');
      emit('update:visible', false);
      emit('submit-success');
    } else {
      submitError.value = res.msg || '签到失败';
    }
  } catch {
    submitError.value = '签到请求失败，请检查网络';
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped lang="less">
.schedule-info { padding: 8px 0 16px; color: #606266; font-size: 14px; }
.mb8 { margin-bottom: 8px; }
.text-muted { color: #c0c4cc; }
</style>
