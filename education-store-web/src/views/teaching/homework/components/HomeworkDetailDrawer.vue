<!-- 作业详情 Drawer：基本信息 + 附件 + 查阅记录 + 提交列表 + 批改 -->
<template>
  <el-drawer :model-value="visible" title="作业详情" size="720px" destroy-on-close append-to-body direction="rtl" @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <div v-loading="loading" class="detail-body">
      <div v-if="error" class="error-box">
        <el-result icon="error" title="加载失败" :sub-title="error">
          <template #extra><el-button type="primary" @click="onOpen">重试</el-button></template>
        </el-result>
      </div>

      <template v-if="homework && !error">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border size="small" title="基本信息">
          <el-descriptions-item label="班级">{{ homework.className || '—' }}</el-descriptions-item>
          <el-descriptions-item label="课程">{{ homework.courseName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="排课日期">{{ homework.scheduleDate || '—' }}</el-descriptions-item>
          <el-descriptions-item label="发布人">{{ homework.teacherName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ homework.createTime || '—' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="homework.isUpdated === 1" type="warning">已更新</el-tag>
            <span v-else>正常</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 作业内容 -->
        <h4 class="section-title">作业内容</h4>
        <div class="content-box">{{ homework.content }}</div>

        <!-- 图片附件 -->
        <template v-if="images.length > 0">
          <h4 class="section-title">图片附件</h4>
          <div class="image-list">
            <el-image v-for="(img, index) in images" :key="img.id" :src="img.fileUrl" :preview-src-list="images.map(i => i.fileUrl)" :initial-index="index" fit="cover" style="width:120px;height:120px;margin-right:8px;border-radius:4px;" />
          </div>
        </template>

        <!-- 文件附件 -->
        <template v-if="documents.length > 0">
          <h4 class="section-title">文件附件</h4>
          <div class="file-list">
            <div v-for="doc in documents" :key="doc.id" class="file-item">
              <span>📄 {{ doc.fileName }} ({{ formatSize(doc.fileSize) }})</span>
              <el-button link type="primary" size="small" @click="downloadFile(doc.fileUrl)">下载</el-button>
            </div>
          </div>
        </template>

        <!-- 家长查阅记录 -->
        <h4 class="section-title">家长查阅记录</h4>
        <div class="view-log">
          <span>已查阅 {{ viewCount }} / 应查阅 {{ totalStudents }} 人</span>
          <el-button v-if="viewLogs.length > 0" link type="primary" size="small" @click="showViewLogs = !showViewLogs">{{ showViewLogs ? '收起' : '查看明细' }}</el-button>
        </div>
        <el-table v-if="showViewLogs && viewLogs.length > 0" :data="viewLogs" size="small" border class="view-log-table">
          <el-table-column label="家长姓名" prop="parentName" />
          <el-table-column label="学生姓名" prop="studentName" />
          <el-table-column label="首次查看时间" prop="firstViewTime" />
          <el-table-column label="查看次数" prop="viewCount" />
        </el-table>

        <!-- 作业提交列表 -->
        <h4 class="section-title">学生提交</h4>
        <el-table v-if="submissions.length > 0" :data="submissions" size="small" border>
          <el-table-column label="学生" prop="studentName" width="100" />
          <el-table-column label="提交内容" min-width="200">
            <template #default="{ row }">
              <span v-if="row.content">{{ row.content }}</span>
              <span v-else style="color:#999">仅图片</span>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" prop="submittedAt" width="160" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'GRADED'" type="success" size="small">已批改</el-tag>
              <el-tag v-else-if="row.status === 'LATE'" type="warning" size="small">迟交</el-tag>
              <el-tag v-else type="info" size="small">待批改</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="评星" width="80">
            <template #default="{ row }">{{ row.score ? '★'.repeat(row.score) + '☆'.repeat(5 - row.score) : '—' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="openGrade(row)">批改</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无学生提交" :image-size="60" />
      </template>
    </div>

    <!-- 批改弹窗 -->
    <el-dialog v-model="gradeOpen" title="批改作业" width="450px" destroy-on-close append-to-body>
      <el-form :model="gradeForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="gradeForm.score" :max="5" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="gradeForm.comment" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="选填，最多500字" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gradeOpen = false">取消</el-button>
        <el-button type="primary" @click="handleGrade" :loading="grading">提交批改</el-button>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getHomework } from '@/api/teaching/homework';
import { listSubmission, gradeSubmission } from '@/api/teaching/homeworkSubmission';

const props = defineProps({ visible: Boolean, homeworkId: { type: [Number, String], default: null } });
const emit = defineEmits(['update:visible']);

const loading = ref(false); const error = ref('');
const homework = ref(null);
const images = ref([]); const documents = ref([]);
const viewCount = ref(0); const totalStudents = ref(0); const viewLogs = ref([]);
const showViewLogs = ref(false);
const submissions = ref([]);
const gradeOpen = ref(false); const grading = ref(false);
const gradingSubmissionId = ref(null);
const gradeForm = ref({ score: 0, comment: '' });

function formatSize(bytes) {
  if (!bytes) return '0 B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
}

function downloadFile(url) { window.open(url, '_blank'); }

async function onOpen() {
  if (!props.homeworkId) return;
  loading.value = true; error.value = '';
  homework.value = null; images.value = []; documents.value = [];
  viewCount.value = 0; viewLogs.value = []; showViewLogs.value = false;
  submissions.value = [];

  try {
    const res = await getHomework(props.homeworkId);
    if (res.success && res.data) {
      const h = res.data.data || res.data;
      homework.value = h;
      // attachments
      const allAttachments = h.attachments || [];
      images.value = allAttachments.filter(a => a.fileType === 'IMAGE');
      documents.value = allAttachments.filter(a => a.fileType === 'DOCUMENT');
      // view logs
      viewCount.value = h.viewCount || 0;
      totalStudents.value = h.totalStudents || 0;
      viewLogs.value = h.viewLogs || [];
      // submissions
      submissions.value = h.submissions || [];
    } else {
      error.value = res.msg || '加载失败';
    }
  } catch (e) {
    error.value = '网络错误，请检查网络后重试';
  } finally {
    loading.value = false;
  }
}

function openGrade(row) {
  gradingSubmissionId.value = row.id;
  gradeForm.value = { score: row.score || 0, comment: row.comment || '' };
  gradeOpen.value = true;
}

async function handleGrade() {
  grading.value = true;
  try {
    const res = await gradeSubmission(gradingSubmissionId.value, { score: gradeForm.value.score, comment: gradeForm.value.comment });
    if (res.success) {
      ElMessage.success('批改成功');
      gradeOpen.value = false;
      // refresh submission in list
      const sub = submissions.value.find(s => s.id === gradingSubmissionId.value);
      if (sub) { sub.score = gradeForm.value.score; sub.comment = gradeForm.value.comment; sub.status = 'GRADED'; }
    } else {
      ElMessage.error(res.msg || '批改失败');
    }
  } finally { grading.value = false; }
}
</script>

<style scoped lang="less">
.detail-body { padding: 0 16px; }
.section-title { margin: 20px 0 12px; font-size: 15px; font-weight: 600; color: #303133; }
.content-box { padding: 12px; background: #f5f7fa; border-radius: 4px; white-space: pre-wrap; line-height: 1.8; }
.image-list { display: flex; flex-wrap: wrap; }
.file-list { display: flex; flex-direction: column; gap: 8px; }
.file-item { display: flex; align-items: center; justify-content: space-between; padding: 8px 12px; background: #f5f7fa; border-radius: 4px; }
.view-log { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; color: #606266; }
.view-log-table { margin-top: 8px; }
.error-box { padding: 40px 0; }
</style>
