<!-- 学评教详情弹窗 -->
<template>
  <el-dialog :model-value="visible" title="学评教详情" width="550px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <div v-loading="loading">
      <div v-if="error" class="error-box">
        <el-result icon="error" title="加载失败" :sub-title="error" />
      </div>

      <template v-if="detail && !error">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="评价学生">
            <span v-if="detail.isAnonymous === 1" class="anonymous-name">匿名用户</span>
            <span v-else>{{ detail.studentName || '—' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="被评教师">{{ detail.teacherName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="评价时间">{{ detail.submitTime || '—' }}</el-descriptions-item>
          <el-descriptions-item label="匿名评价">
            <el-tag :type="detail.isAnonymous === 1 ? 'warning' : 'info'" size="small">{{ detail.isAnonymous === 1 ? '是' : '否' }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div style="margin-top:16px">
          <h4 class="section-title">评分</h4>
          <el-rate :model-value="detail.ratingScore" :max="5" disabled show-score size="large" />
        </div>

        <div style="margin-top:16px">
          <h4 class="section-title">评价内容</h4>
          <div class="content-box">{{ detail.content || '—' }}</div>
        </div>

        <div v-if="parseTags(detail.tags).length > 0" style="margin-top:16px">
          <h4 class="section-title">评价标签</h4>
          <div class="tag-list">
            <el-tag v-for="tag in parseTags(detail.tags)" :key="tag" size="default">{{ tag }}</el-tag>
          </div>
        </div>
      </template>
    </div>

    <template #footer>
      <el-button @click="emit('update:visible', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { getTeacherEvaluation } from '@/api/teaching/teacherEvaluation';

const props = defineProps({ visible: Boolean, evalId: { type: [Number, String], default: null } });
const emit = defineEmits(['update:visible']);

const loading = ref(false); const error = ref(''); const detail = ref(null);

function parseTags(tags) {
  if (!tags) return [];
  try { return typeof tags === 'string' ? JSON.parse(tags) : tags; }
  catch { return []; }
}

async function onOpen() {
  if (!props.evalId) return;
  loading.value = true; error.value = ''; detail.value = null;
  try {
    const res = await getTeacherEvaluation(props.evalId);
    if (res.success && res.data) {
      detail.value = res.data.data || res.data;
    } else { error.value = res.msg || '加载失败'; }
  } catch (e) { error.value = '网络错误，请检查网络后重试'; }
  finally { loading.value = false; }
}
</script>

<style scoped lang="less">
.section-title { margin-bottom: 8px; font-size: 14px; font-weight: 600; color: #303133; }
.content-box { padding: 12px; background: #f5f7fa; border-radius: 4px; line-height: 1.8; white-space: pre-wrap; }
.tag-list { display: flex; gap: 8px; flex-wrap: wrap; }
.anonymous-name { color: #909399; font-style: italic; }
</style>
