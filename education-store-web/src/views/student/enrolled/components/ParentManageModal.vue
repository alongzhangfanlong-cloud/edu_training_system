<!-- 家长管理弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="`家长管理 — ${studentName}`" width="600px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-table v-loading="loading" :data="parentList" border stripe>
      <el-table-column label="家长姓名" align="center" prop="name" />
      <el-table-column label="手机号" align="center" prop="mobile" width="120" />
      <el-table-column label="亲属关系" align="center" prop="relation" width="100" />
      <el-table-column label="排序" align="center" prop="sort" width="80" />
      <el-table-column label="操作" align="center" width="80" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" size="small" @click="handleUnbind(row)">解绑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 16px">
      <el-button type="primary" plain size="small" @click="bindOpen = true" v-permission="['student:parent:add']">
        <el-icon><Plus /></el-icon>绑定家长
      </el-button>
    </div>

    <!-- 绑定家长弹窗 -->
    <el-dialog :model-value="bindOpen" title="绑定家长" width="450px" destroy-on-close append-to-body @open="onBindOpen" @update:model-value="(val) => bindOpen = val">
      <el-form ref="bindFormRef" :model="bindForm" :rules="bindRules" label-width="90px">
        <el-form-item label="家长账号" prop="userId">
          <el-select v-model="bindForm.userId" placeholder="请选择家长账号" filterable remote :remote-method="searchUsers" :loading="userLoading" style="width:100%">
            <el-option v-for="u in userOptions" :key="u.id" :label="`${u.realName || u.username} (${u.mobile || '-'})`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="亲属关系" prop="relationship">
          <el-input v-model="bindForm.relationship" placeholder="如：父亲、母亲等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindOpen = false">取消</el-button>
        <el-button type="primary" @click="handleBind" :loading="bindSubmitting">确定</el-button>
      </template>
    </el-dialog>

    <template #footer>
      <el-button @click="emit('update:visible', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { listParent, addParent, delParent } from '@/api/student/parents';
import { listUser } from '@/api/system/user';
import { useDictsStore } from '@/stores/dicts';
import DictTag from '@/components/DictTag/index.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  studentId: { type: [Number, String], default: null },
  studentName: { type: String, default: '' },
});
const emit = defineEmits(['update:visible']);

const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const parentList = ref([]);
const bindOpen = ref(false);
const bindFormRef = ref(null);
const bindSubmitting = ref(false);
const bindForm = ref({ userId: null, relationship: '' });
const bindRules = {
  userId: [{ required: true, message: '请选择家长账号', trigger: 'change' }],
  relationship: [{ required: true, message: '请输入亲属关系', trigger: 'blur' }],
};

const userOptions = ref([]);
const userLoading = ref(false);
let searchTimer = null;
let searchSeq = 0;

async function onOpen() {
  await loadDict('sys_normal_disable');
  fetchParents();
}

function fetchParents() {
  if (!props.studentId) return;
  loading.value = true;
  listParent(props.studentId).then(res => {
    if (res.success) parentList.value = res.data?.records || res.data?.rows || [];
  }).finally(() => { loading.value = false; });
}

function handleUnbind(row) {
  ElMessageBox.confirm(`确认解绑家长"${row.name}"？解绑不会删除家长账号。`, '警告', { type: 'warning' })
    .then(() => delParent(props.studentId, row.id))
    .then(res => {
      if (res.success) { fetchParents(); ElMessage.success('解绑成功'); }
      else { ElMessage.error(res.msg || '解绑失败'); }
    }).catch(() => {});
}

function onBindOpen() {
  bindForm.value = { userId: null, relationship: '' };
  userOptions.value = [];
}

function searchUsers(keyword) {
  clearTimeout(searchTimer);
  if (!keyword) { userOptions.value = []; return; }
  userLoading.value = true;
  searchTimer = setTimeout(() => {
    searchSeq++;
    const seq = searchSeq;
    listUser({ page: 1, size: 100, keyword }).then(res => {
      if (seq !== searchSeq) return;
      if (res.success) userOptions.value = res.data?.records || res.rows || [];
    }).finally(() => {
      if (seq === searchSeq) userLoading.value = false;
    });
  }, 300);
}

async function handleBind() {
  const valid = await bindFormRef.value?.validate().catch(() => false);
  if (!valid) return;
  bindSubmitting.value = true;
  try {
    const res = await addParent(props.studentId, bindForm.value);
    if (res.success) {
      ElMessage.success('绑定成功');
      bindOpen.value = false;
      fetchParents();
    } else {
      ElMessage.error(res.msg || '绑定失败');
    }
  } finally { bindSubmitting.value = false; }
}
</script>
