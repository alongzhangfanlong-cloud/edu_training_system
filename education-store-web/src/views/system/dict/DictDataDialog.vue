<!-- 字典数据编辑弹窗 — 供 Data Tab 和 Drawer 共用 -->
<template>
  <el-dialog v-model="visible" :title="isEdit ? '修改字典数据' : '添加字典数据'" width="500px" destroy-on-close append-to-body>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="字典类型">
        <el-input :model-value="typeCode" disabled />
      </el-form-item>
      <el-form-item label="字典标签" prop="label">
        <el-input v-model="form.label" placeholder="请输入字典标签" />
      </el-form-item>
      <el-form-item label="字典键值" prop="value">
        <el-input v-model="form.value" placeholder="请输入字典键值" />
      </el-form-item>
      <el-form-item label="显示排序" prop="sort">
        <el-input-number v-model="form.sort" :min="0" :max="999" />
      </el-form-item>
      <el-form-item label="是否默认" prop="isDefault">
        <el-radio-group v-model="form.isDefault">
          <el-radio :value="1">是</el-radio>
          <el-radio :value="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="样式类名" prop="cssClass">
        <el-input v-model="form.cssClass" placeholder="请输入样式类名" />
      </el-form-item>
      <el-form-item label="回显样式" prop="listClass">
        <el-input v-model="form.listClass" placeholder="请输入回显样式" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">正常</el-radio>
          <el-radio :value="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { addData, updateData } from '@/api/system/dict';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  typeCode: { type: String, default: '' },
  data: { type: Object, default: null },
});

const emit = defineEmits(['update:modelValue', 'saved']);

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
});

const isEdit = computed(() => !!(props.data && props.data.id));

const formRef = ref(null);
const form = ref(getDefaultForm());

const rules = {
  label: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  value: [{ required: true, message: '请输入字典键值', trigger: 'blur' }],
};

function getDefaultForm() {
  return { label: '', value: '', sort: 1, isDefault: 0, cssClass: '', listClass: '', status: 1, remark: '' };
}

watch(() => props.modelValue, (val) => {
  if (val) {
    if (props.data && props.data.id) {
      form.value = {
        label: props.data.label || '',
        value: props.data.value || '',
        sort: props.data.sort ?? 1,
        isDefault: props.data.isDefault ?? 0,
        cssClass: props.data.cssClass || '',
        listClass: props.data.listClass || '',
        status: props.data.status ?? 1,
        remark: props.data.remark || '',
      };
    } else {
      form.value = getDefaultForm();
    }
  }
}, { immediate: true });

function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (!valid) return;
    const payload = { ...form.value, typeCode: props.typeCode };
    const api = isEdit.value
      ? updateData({ ...payload, id: props.data.id })
      : addData(payload);
    api.then(res => {
      if (res.success) {
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功');
        visible.value = false;
        emit('saved');
      } else {
        ElMessage.error(res.msg || '操作失败');
      }
    });
  });
}
</script>
