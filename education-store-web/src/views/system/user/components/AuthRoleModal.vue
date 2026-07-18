<!-- 分配角色弹窗 -->
<template>
  <el-dialog :model-value="visible" title="分配角色" width="500px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-form label-width="80px">
      <el-form-item label="用户名称">
        <span>{{ user?.realName || user?.username }}</span>
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="roleIds" multiple placeholder="请选择角色" style="width:100%">
          <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getAuthRole, updateAuthRole } from '@/api/system/user';
import { listRoleAll } from '@/api/system/role';

const props = defineProps({
  visible: { type: Boolean, default: false },
  user: { type: Object, default: () => ({}) },
});
const emit = defineEmits(['update:visible', 'submit-success']);

const roles = ref([]);
const roleIds = ref([]);

async function onOpen() {
  const [roleRes, authRes] = await Promise.all([listRoleAll(), getAuthRole(props.user.id)]);
  if (roleRes.success) roles.value = roleRes.data || [];
  if (authRes.success) roleIds.value = (authRes.data?.roleIds || authRes.data?.roles || []).map(r => typeof r === 'object' ? r.id : r);
}

async function handleSubmit() {
  const res = await updateAuthRole(props.user.id, roleIds.value);
  if (res.success) { ElMessage.success('角色分配成功'); emit('update:visible', false); emit('submit-success'); }
}
</script>
