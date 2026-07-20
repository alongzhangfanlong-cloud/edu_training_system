<!-- 教师端 — 课件上传弹窗 -->
<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    title="上传课件"
    width="520px"
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="课件名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入课件名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="科目" prop="subject">
        <el-select v-model="form.subject" placeholder="请选择科目" style="width: 100%">
          <el-option label="语文" value="语文" />
          <el-option label="数学" value="数学" />
          <el-option label="英语" value="英语" />
          <el-option label="物理" value="物理" />
          <el-option label="化学" value="化学" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="年级" prop="grade">
        <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
          <el-option v-for="g in grades" :key="g" :label="g" :value="g" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
          <el-option label="课件文档" value="课件文档" />
          <el-option label="教学PPT" value="教学PPT" />
          <el-option label="练习资料" value="练习资料" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="共享范围" prop="isPublic">
        <el-radio-group v-model="form.isPublic">
          <el-radio :value="true">公开</el-radio>
          <el-radio :value="false">私有</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="上传文件" prop="fileUrl">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.jpg,.png,.gif,.zip,.rar"
        >
          <el-button size="small" type="primary">选择文件</el-button>
          <template #tip>
            <div class="upload-tip">支持 PDF/Word/PPT/图片/压缩包，单文件 ≤ 50MB</div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">上传</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { addResource } from '@/api/course/resources.js';
import { uploadFile } from '@/api/common/file.js';

defineProps({
  modelValue: { type: Boolean, default: false },
});

const emit = defineEmits(['update:modelValue', 'saved']);

const grades = ['一年级', '二年级', '三年级', '四年级', '五年级', '六年级',
  '初一', '初二', '初三', '高一', '高二', '高三'];

const formRef = ref(null);
const uploadRef = ref(null);
const submitting = ref(false);
const selectedFile = ref(null);

const form = reactive({
  name: '',
  subject: '',
  grade: '',
  type: '',
  isPublic: true,
  fileUrl: '',
});

const rules = {
  name: [{ required: true, message: '请输入课件名称', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
  grade: [{ required: true, message: '请选择年级', trigger: 'change' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  fileUrl: [{ required: true, message: '请选择文件', trigger: 'change' }],
};

function handleFileChange(file) {
  const maxSize = 50 * 1024 * 1024;
  if (file.size > maxSize) {
    ElMessage.warning('文件大小不能超过 50MB');
    uploadRef.value?.clearFiles();
    return;
  }
  selectedFile.value = file.raw;
  form.fileUrl = file.name;
}

function handleFileRemove() {
  selectedFile.value = null;
  form.fileUrl = '';
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    let fileUrl = '';
    // 第一步：上传文件到文件服务器
    if (selectedFile.value) {
      const fd = new FormData();
      fd.append('file', selectedFile.value);
      const uploadRes = await uploadFile(fd);
      if (uploadRes.success) {
        fileUrl = uploadRes.data || '';
      } else {
        ElMessage.error(uploadRes.msg || '文件上传失败');
        submitting.value = false;
        return;
      }
    }

    // 第二步：创建课件记录
    const res = await addResource({
      name: form.name,
      subject: form.subject,
      grade: form.grade,
      type: form.type,
      isPublic: form.isPublic,
      fileUrl: fileUrl,
    });
    if (res.success) {
      ElMessage.success('上传成功');
      emit('update:modelValue', false);
      emit('saved');
      resetForm();
    } else {
      ElMessage.error(res.msg || '上传失败');
    }
  } catch {
    ElMessage.error('上传失败，请检查网络');
  } finally {
    submitting.value = false;
  }
}

function resetForm() {
  form.name = '';
  form.subject = '';
  form.grade = '';
  form.type = '';
  form.isPublic = true;
  form.fileUrl = '';
  selectedFile.value = null;
  uploadRef.value?.clearFiles();
  formRef.value?.resetFields();
}
</script>

<style scoped lang="less">
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
