<!-- 用户新增/编辑弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="title" width="650px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户账号" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户账号" :disabled="!!userId" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" :placeholder="userId ? '留空则不修改' : '请输入密码'" show-password />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="昵称" prop="nickName">
            <el-input v-model="form.nickName" placeholder="请输入昵称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="form.mobile" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="性别">
            <el-select v-model="form.gender" style="width:100%">
              <el-option v-for="d in dicts.sys_user_sex" :key="d.value" :label="d.label" :value="Number(d.value)" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio v-for="d in dicts.sys_normal_disable" :key="d.value" :value="Number(d.value)">{{ d.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="角色">
            <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width:100%">
              <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { getUser, addUser, updateUser } from '@/api/system/user';
import { listRoleAll } from '@/api/system/role';
import { useDictsStore } from '@/stores/dicts';

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  userId: { type: [Number, String], default: null },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const { dicts, loadDicts } = useDictsStore();
const formRef = ref(null);
const submitting = ref(false);
const roles = ref([]);

const form = ref({
  username: '', password: '', realName: '', nickName: '',
  email: '', mobile: '',
  gender: 0, status: 1, roleIds: [], remark: '',
});

const rules = computed(() => ({
  username: [{ required: true, message: '请输入用户账号', trigger: 'blur' }],
  password: props.userId
    ? [{ min: 6, message: '密码不少于6位', trigger: 'blur' }]
    : [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  mobile: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
}));

async function onOpen() {
  reset();
  await Promise.all([loadRoles(), loadDicts(['sys_user_sex', 'sys_normal_disable'])]);
  if (props.userId) {
    const res = await getUser(props.userId);
    if (res.success && res.data) {
      const user = res.data.data || res.data;
      form.value = {
        username: user.username || '',
        password: '',
        realName: user.realName || '',
        nickName: user.nickName || '',
        email: user.email || '',
        mobile: user.mobile || '',
        gender: user.gender || 0,
        status: user.status ?? 1,
        roleIds: res.data.roleIds || user.roleIds || [],
        remark: user.remark || '',
      };
    }
  }
}

function reset() {
  form.value = { username: '', password: '', realName: '', nickName: '', email: '', mobile: '', gender: 0, status: 1, roleIds: [], remark: '' };
  nextTick(() => formRef.value?.clearValidate());
}

async function loadRoles() { const res = await listRoleAll(); if (res.success) roles.value = res.data || []; }

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value };
    if (props.userId) {
      data.id = props.userId;
      if (!data.password) delete data.password;
      const res = await updateUser(props.userId, data);
      if (res.success) { ElMessage.success('修改成功'); emit('update:visible', false); emit('submit-success'); }
    } else {
      const res = await addUser(data);
      if (res.success) { ElMessage.success('新增成功'); emit('update:visible', false); emit('submit-success'); }
    }
  } finally { submitting.value = false; }
}
</script>
