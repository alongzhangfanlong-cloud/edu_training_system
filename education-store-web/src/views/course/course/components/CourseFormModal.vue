<!-- 课程新增/编辑弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="600px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="课程名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入课程名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程编号" prop="code">
            <el-input v-model="form.code" placeholder="唯一编号，如 ENG-001" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="所属科目" prop="subjectId">
            <el-select v-model="form.subjectId" placeholder="请选择科目" style="width:100%">
              <el-option v-for="s in subjectOptions" :key="s.id" :label="s.name" :value="s.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程类型" prop="courseType">
            <el-select v-model="form.courseType" placeholder="请选择类型" style="width:100%">
              <el-option v-for="d in dicts.course_type" :key="d.value" :label="d.label" :value="d.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="单价" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="适用年级">
            <el-input v-model="form.applicableGrade" placeholder="如：一年级,二年级" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="排序号">
            <el-input-number v-model="form.sortOrder" :min="0" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="封面图">
            <el-upload
              :file-list="coverFileList"
              :http-request="uploadCover"
              :before-upload="beforeCoverUpload"
              :on-remove="removeCover"
              list-type="picture-card"
              :limit="1"
              accept="image/jpeg,image/png,image/gif,image/webp"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio v-for="d in dicts.sys_normal_disable" :key="d.value" :value="Number(d.value)">{{ d.label }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="课程介绍">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程介绍" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { getCourse, addCourse, updateCourse } from '@/api/course/courses';
import { listSubject } from '@/api/course/subjects';
import { uploadFile } from '@/api/common/file';
import { useDictsStore } from '@/stores/dicts';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  courseId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const { dicts, loadDicts } = useDictsStore();
const formRef = ref(null);
const submitting = ref(false);
const subjectOptions = ref([]);
const coverFileList = ref([]);

const form = ref({
  name: '', code: '', subjectId: null, courseType: '',
  price: null, applicableGrade: '', coverImage: '',
  sortOrder: 0, status: 1, description: '',
});

const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
};

async function onOpen() {
  reset();
  await loadDicts(['sys_normal_disable', 'course_type']);
  loadSubjects();
  if (props.courseId) {
    const res = await getCourse(props.courseId);
    if (res.success && res.data) {
      const c = res.data.data || res.data;
      form.value = {
        name: c.name || '', code: c.code || '', subjectId: c.subjectId || null,
        courseType: c.courseType || '', price: c.price || null,
        applicableGrade: c.applicableGrade || '', coverImage: c.coverImage || '',
        sortOrder: c.sortOrder || 0, status: c.status ?? 1, description: c.description || '',
      };
      // 已有封面图时回显
      if (c.coverImage) {
        coverFileList.value = [{ name: c.coverImage, url: c.coverImage.startsWith('http') ? c.coverImage : `/upload/${c.coverImage}` }];
      }
    }
  }
}

function loadSubjects() {
  listSubject({ page: 1, size: 999 }).then(res => {
    if (res.success) subjectOptions.value = res.data?.records || res.rows || [];
  });
}

// ---- 封面上传 ----
async function uploadCover(options) {
  const fd = new FormData();
  fd.append('file', options.file);
  try {
    const res = await uploadFile(fd);
    if (res.success) {
      form.value.coverImage = res.data.filePath;
      options.onSuccess(res, options.file);
    } else {
      options.onError(new Error(res.msg || '上传失败'));
    }
  } catch (e) {
    options.onError(e);
  }
}

function beforeCoverUpload(file) {
  const isValid = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type);
  if (!isValid) { ElMessage.error('仅支持 jpg/png/gif/webp 格式'); return false; }
  if (file.size > 2 * 1024 * 1024) { ElMessage.error('图片大小不能超过 2MB'); return false; }
  return true;
}

function removeCover() {
  form.value.coverImage = '';
}

function reset() {
  form.value = { name: '', code: '', subjectId: null, courseType: '', price: null, applicableGrade: '', coverImage: '', sortOrder: 0, status: 1, description: '' };
  coverFileList.value = [];
  nextTick(() => formRef.value?.clearValidate());
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value }; let res;
    if (props.courseId) { data.id = props.courseId; res = await updateCourse(data); }
    else { res = await addCourse(data); }
    if (res.success) { ElMessage.success(props.courseId ? '修改成功' : '新增成功'); emit('update:visible', false); emit('submit-success'); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
</script>
