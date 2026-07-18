<!-- 布置/编辑作业 Drawer -->
<template>
  <el-drawer :model-value="visible" :title="title" size="720px" destroy-on-close append-to-body direction="rtl" @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="homework-form">
      <el-form-item label="关联方式" prop="linkType">
        <el-radio-group v-model="form.linkType" @change="onLinkTypeChange">
          <el-radio value="class">关联班级</el-radio>
          <el-radio value="schedule">关联排课记录</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="关联班级" prop="classId">
            <el-select v-model="form.classId" placeholder="选择班级" filterable remote :remote-method="remoteSearchClass" :loading="classLoading" style="width:100%" @change="onClassChange">
              <el-option v-for="c in classOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="关联课程" prop="courseId">
            <el-select v-model="form.courseId" placeholder="选择课程" filterable remote :remote-method="remoteSearchCourse" :loading="courseLoading" style="width:100%">
              <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item v-if="form.linkType === 'schedule'" label="关联排课" prop="scheduleId">
            <el-select v-model="form.scheduleId" placeholder="选择排课" filterable style="width:100%">
              <el-option v-for="s in scheduleOptions" :key="s.id" :label="s.scheduleDate + ' ' + (s.startTime || '') + '-' + (s.endTime || '')" :value="s.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="作业内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入作业内容" maxlength="5000" show-word-limit />
      </el-form-item>

      <el-form-item label="图片附件">
        <el-upload :file-list="imageFiles" :http-request="uploadImage" :before-upload="beforeImageUpload" :on-remove="removeImage" list-type="picture-card" :limit="9" accept="image/jpeg,image/png,image/gif,image/webp">
          <el-icon><Plus /></el-icon>
        </el-upload>
        <div class="upload-tip">jpg/png/gif/webp，单张 ≤ 10MB，最多 9 张</div>
      </el-form-item>

      <el-form-item label="文件附件">
        <el-upload :file-list="docFiles" :http-request="uploadDocument" :before-upload="beforeDocumentUpload" :on-remove="removeDocument" :limit="5" accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx">
          <el-button type="primary" plain size="small"><el-icon><Upload /></el-icon>上传文件</el-button>
        </el-upload>
        <div class="upload-tip">pdf/doc/docx/xls/xlsx/ppt/pptx，单个 ≤ 20MB，最多 5 个</div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="drawer-footer">
        <el-button @click="emit('update:visible', false)">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">发布</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, Upload } from '@element-plus/icons-vue';
import { addHomework, updateHomework, getHomework } from '@/api/teaching/homework';
import { listClass } from '@/api/class/classes';
import { listCourse } from '@/api/course/courses';
import { listSchedule } from '@/api/schedule/schedules';
import { uploadFile } from '@/api/common/file';

const props = defineProps({ visible: Boolean, title: String, homeworkId: { type: [Number, String], default: null } });
const emit = defineEmits(['update:visible', 'submit-success']);

const formRef = ref(null); const submitting = ref(false);
const form = ref({ linkType: 'class', classId: '', courseId: '', scheduleId: '', content: '' });
const rules = {
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }],
};
const classOptions = ref([]); const classLoading = ref(false);
const courseOptions = ref([]); const courseLoading = ref(false);
const scheduleOptions = ref([]);
const imageFiles = ref([]); const docFiles = ref([]);
const imageAttachmentIds = ref([]); const docAttachmentIds = ref([]);
let classSeq = 0; let courseSeq = 0;

function onLinkTypeChange() {
  form.value.scheduleId = '';
  scheduleOptions.value = [];
}

function onClassChange() { form.value.courseId = ''; form.value.scheduleId = ''; scheduleOptions.value = []; }

function remoteSearchClass(query) {
  if (!query) { classOptions.value = []; return; }
  classLoading.value = true; classSeq++; const seq = classSeq;
  listClass({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== classSeq) return;
    classOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === classSeq) classLoading.value = false; });
}

function remoteSearchCourse(query) {
  if (!query) { courseOptions.value = []; return; }
  courseLoading.value = true; courseSeq++; const seq = courseSeq;
  listCourse({ page: 1, size: 20, keyword: query }).then(res => {
    if (seq !== courseSeq) return;
    courseOptions.value = res.success ? (res.data?.records || res.rows || []) : [];
  }).finally(() => { if (seq === courseSeq) courseLoading.value = false; });
}

async function uploadImage(options) {
  const fd = new FormData();
  fd.append('file', options.file);
  try {
    const res = await uploadFile(fd);
    if (res.success) {
      const attachId = res.data?.id || res.data;
      imageAttachmentIds.value.push(attachId);
      options.onSuccess(res, options.file);
    } else { options.onError(new Error(res.msg || '上传失败')); }
  } catch (e) { options.onError(e); }
}

function beforeImageUpload(file) {
  const isValid = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type);
  if (!isValid) { ElMessage.error('仅支持 jpg/png/gif/webp 格式'); return false; }
  if (file.size > 10 * 1024 * 1024) { ElMessage.error('单张图片不能超过 10MB'); return false; }
  return true;
}

function removeImage(file) {
  const idx = imageAttachmentIds.value.indexOf(file.response?.data?.id || file.uid);
  if (idx > -1) imageAttachmentIds.value.splice(idx, 1);
}

async function uploadDocument(options) {
  const fd = new FormData();
  fd.append('file', options.file);
  try {
    const res = await uploadFile(fd);
    if (res.success) {
      const attachId = res.data?.id || res.data;
      docAttachmentIds.value.push(attachId);
      options.onSuccess(res, options.file);
    } else { options.onError(new Error(res.msg || '上传失败')); }
  } catch (e) { options.onError(e); }
}

function beforeDocumentUpload(file) {
  const ext = file.name.split('.').pop().toLowerCase();
  const allowed = ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'];
  if (!allowed.includes(ext)) { ElMessage.error('不支持的文件格式'); return false; }
  if (file.size > 20 * 1024 * 1024) { ElMessage.error('单个文件不能超过 20MB'); return false; }
  return true;
}

function removeDocument(file) {
  const idx = docAttachmentIds.value.indexOf(file.response?.data?.id || file.uid);
  if (idx > -1) docAttachmentIds.value.splice(idx, 1);
}

async function onOpen() {
  reset();
  if (props.homeworkId) {
    const res = await getHomework(props.homeworkId);
    if (res.success && res.data) {
      const h = res.data.data || res.data;
      form.value = {
        linkType: h.scheduleId ? 'schedule' : 'class',
        classId: h.classId || '',
        courseId: h.courseId || '',
        scheduleId: h.scheduleId || '',
        content: h.content || '',
      };
      // preload options
      if (h.classId) { classOptions.value = [{ id: h.classId, name: h.className || '—' }]; }
      if (h.courseId) { courseOptions.value = [{ id: h.courseId, name: h.courseName || '—' }]; }
      if (h.attachmentIds) { imageAttachmentIds.value = []; docAttachmentIds.value = []; }
      if (h.imageAttachments) { imageFiles.value = h.imageAttachments.map(a => ({ name: a.fileName, url: a.fileUrl, response: { data: { id: a.id } } })); imageAttachmentIds.value = h.imageAttachments.map(a => a.id); }
      if (h.docAttachments) { docFiles.value = h.docAttachments.map(a => ({ name: a.fileName, url: a.fileUrl, response: { data: { id: a.id } } })); docAttachmentIds.value = h.docAttachments.map(a => a.id); }
    }
  }
}

function reset() {
  form.value = { linkType: 'class', classId: '', courseId: '', scheduleId: '', content: '' };
  imageFiles.value = []; docFiles.value = [];
  imageAttachmentIds.value = []; docAttachmentIds.value = [];
  classOptions.value = []; courseOptions.value = []; scheduleOptions.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const data = {
      classId: form.value.classId,
      courseId: form.value.courseId,
      scheduleId: form.value.linkType === 'schedule' ? form.value.scheduleId : null,
      content: form.value.content,
      attachmentIds: [...imageAttachmentIds.value, ...docAttachmentIds.value],
    };
    let res;
    if (props.homeworkId) {
      data.id = props.homeworkId;
      res = await updateHomework(props.homeworkId, data);
    } else {
      res = await addHomework(data);
    }
    if (res.success) { ElMessage.success(props.homeworkId ? '编辑成功' : '发布成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>

<style scoped lang="less">
.homework-form { padding: 0 16px; }
.upload-tip { font-size: 12px; color: #999; margin-top: 4px; }
.drawer-footer { display: flex; justify-content: flex-end; gap: 8px; }
</style>
