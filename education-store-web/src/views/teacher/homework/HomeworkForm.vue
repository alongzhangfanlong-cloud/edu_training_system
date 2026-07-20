<!-- 教师端 — 布置/编辑作业表单弹窗 -->
<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :title="isEdit ? '编辑作业' : '布置作业'"
    width="640px"
    destroy-on-close
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="关联班级" prop="classId">
        <el-select
          v-model="form.classId"
          placeholder="请选择班级"
          filterable
          remote
          :remote-method="searchClasses"
          :loading="classLoading"
          clearable
          style="width: 100%"
        >
          <el-option
            v-for="c in classOptions"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="关联课程" prop="courseId">
        <el-select
          v-model="form.courseId"
          placeholder="请选择课程"
          filterable
          clearable
          style="width: 100%"
        >
          <el-option
            v-for="c in courseOptions"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="作业内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="6"
          placeholder="请输入作业内容（最长5000字）"
          maxlength="5000"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ isEdit ? '保存' : '发布' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { listClass } from '@/api/class/classes.js';
import { listCourse } from '@/api/course/courses.js';
import { addHomework, updateHomework } from '@/api/teaching/homework.js';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  data: { type: Object, default: null },
});

const emit = defineEmits(['update:modelValue', 'saved']);

const isEdit = ref(false);
const formRef = ref(null);
const submitting = ref(false);

const form = reactive({
  id: null,
  classId: null,
  courseId: null,
  content: '',
});

const rules = {
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }],
};

// 班级远程搜索
const classOptions = ref([]);
const classLoading = ref(false);
let classSearchTimer = null;
let classSearchSeq = 0;

function searchClasses(keyword) {
  clearTimeout(classSearchTimer);
  if (!keyword) { classOptions.value = []; return; }

  classLoading.value = true;
  classSearchTimer = setTimeout(() => {
    classSearchSeq++;
    const seq = classSearchSeq;
    listClass({ page: 1, pageSize: 100, keyword }).then(res => {
      if (seq !== classSearchSeq) return;
      if (res.success) {
        classOptions.value = res.data?.records || [];
      }
    }).finally(() => {
      if (seq === classSearchSeq) classLoading.value = false;
    });
  }, 300);
}

// 课程选项
const courseOptions = ref([]);

watch(() => form.classId, async (val) => {
  form.courseId = null;
  courseOptions.value = [];
  if (val) {
    try {
      const res = await listCourse({ page: 1, pageSize: 200 });
      if (res.success) {
        courseOptions.value = res.data?.records || [];
      }
    } catch { /* ignore */ }
  }
});

// 编辑模式
watch(() => props.modelValue, (visible) => {
  if (visible && props.data) {
    isEdit.value = true;
    form.id = props.data.id;
    form.classId = props.data.classId;
    form.courseId = props.data.courseId;
    form.content = props.data.content || '';
  } else if (visible) {
    isEdit.value = false;
    resetForm();
  }
});

function resetForm() {
  form.id = null;
  form.classId = null;
  form.courseId = null;
  form.content = '';
  formRef.value?.resetFields();
}

function handleClosed() {
  resetForm();
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const apiCall = isEdit.value
      ? updateHomework(form.id, { classId: form.classId, courseId: form.courseId, content: form.content })
      : addHomework({ classId: form.classId, courseId: form.courseId, content: form.content });
    const res = await apiCall;
    if (res.success) {
      ElMessage.success(isEdit.value ? '保存成功' : '发布成功');
      emit('update:modelValue', false);
      emit('saved');
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } catch {
    ElMessage.error('操作失败，请检查网络');
  } finally {
    submitting.value = false;
  }
}
</script>
