<!--
 * 动态表单渲染器
 * 根据字段定义数组动态生成表单控件
-->
<template>
  <el-form :model="formData" label-width="100px">
    <el-form-item
      v-for="field in fields"
      :key="field.name"
      :label="field.label"
      :required="field.required"
    >
      <el-input
        v-if="field.type === 'text' || field.type === 'textarea'"
        v-model="formData[field.name]"
        :type="field.type === 'textarea' ? 'textarea' : 'text'"
        :rows="field.type === 'textarea' ? 4 : 1"
        :placeholder="field.placeholder || '请输入' + field.label"
      />
      <el-input-number
        v-else-if="field.type === 'number' || field.type === 'money'"
        v-model="formData[field.name]"
        :min="0"
        :precision="field.type === 'money' ? 2 : 0"
        style="width: 100%"
      />
      <el-date-picker
        v-else-if="field.type === 'date'"
        v-model="formData[field.name]"
        type="date"
        value-format="YYYY-MM-DD"
        style="width: 100%"
      />
      <el-select
        v-else-if="field.type === 'select'"
        v-model="formData[field.name]"
        style="width: 100%"
        :placeholder="'请选择' + field.label"
      >
        <el-option
          v-for="opt in (field.options || '').split(',')"
          :key="opt.trim()"
          :label="opt.trim()"
          :value="opt.trim()"
        />
      </el-select>
      <el-input
        v-else-if="field.type === 'file'"
        :placeholder="field.placeholder || '选择文件...'"
        disabled
      >
        <template #append><el-button>上传</el-button></template>
      </el-input>
      <el-input v-else v-model="formData[field.name]" :placeholder="field.placeholder" />
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  fields: { type: Array, default: () => [] },
  modelValue: { type: Object, default: () => ({}) },
});

const emit = defineEmits(['update:modelValue']);

const formData = ref({ ...props.modelValue });

watch(() => props.modelValue, (v) => { formData.value = { ...v }; }, { deep: true });
watch(formData, (v) => { emit('update:modelValue', { ...v }); }, { deep: true });
</script>

<style lang="less" scoped>
@import url('./index.less');
</style>
