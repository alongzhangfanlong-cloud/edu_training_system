<!-- 跟进记录弹窗 -->
<template>
  <el-dialog :model-value="visible" :title="`跟进记录 — ${prospectName}`" width="650px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => emit('update:visible', val)">
    <el-table v-loading="loading" :data="followupList" border stripe>
      <el-table-column label="跟进方式" align="center" prop="method" width="100" />
      <el-table-column label="跟进内容" align="center" prop="content" :show-overflow-tooltip="true" min-width="180" />
      <el-table-column label="下次计划" align="center" prop="nextPlan" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="跟进时间" align="center" prop="followupDate" width="160" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 16px">
      <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['student:prospect:followup']">
        <el-icon><Plus /></el-icon>新增跟进记录
      </el-button>
    </div>

    <!-- 新增/编辑跟进记录 -->
    <el-dialog :model-value="followupFormOpen" :title="followupTitle" width="450px" destroy-on-close append-to-body @update:model-value="(val) => followupFormOpen = val">
      <el-form ref="followupFormRef" :model="followupForm" :rules="followupRules" label-width="80px">
        <el-form-item label="跟进方式" prop="method">
          <el-select v-model="followupForm.method" placeholder="请选择跟进方式" style="width:100%">
            <el-option v-for="d in dicts.follow_method" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" prop="content">
          <el-input v-model="followupForm.content" type="textarea" :rows="4" placeholder="请输入跟进内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followupFormOpen = false">取消</el-button>
        <el-button type="primary" @click="handleFollowupSubmit" :loading="followupSubmitting">确定</el-button>
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
import { listFollowup, addFollowup, updateFollowup, delFollowup } from '@/api/student/prospects';
import { useDictsStore } from '@/stores/dicts';

const props = defineProps({
  visible: { type: Boolean, default: false },
  prospectId: { type: [Number, String], default: null },
  prospectName: { type: String, default: '' },
});
const emit = defineEmits(['update:visible']);

const { dicts, loadDict } = useDictsStore();

const loading = ref(false);
const followupList = ref([]);
const followupFormOpen = ref(false);
const followupTitle = ref('');
const followupFormRef = ref(null);
const followupSubmitting = ref(false);
const currentFollowupId = ref(null);
const followupForm = ref({ method: '', content: '' });
const followupRules = {
  method: [{ required: true, message: '请选择跟进方式', trigger: 'change' }],
  content: [{ required: true, message: '请输入跟进内容', trigger: 'blur' }],
};

function onOpen() {
  loadDict('follow_method');
  fetchList();
}

function fetchList() {
  if (!props.prospectId) return;
  loading.value = true;
  listFollowup(props.prospectId).then(res => {
    if (res.success) followupList.value = res.data?.records || res.data?.rows || [];
  }).finally(() => { loading.value = false; });
}

function handleAdd() {
  followupTitle.value = '新增跟进记录';
  currentFollowupId.value = null;
  followupForm.value = { method: '', content: '' };
  followupFormOpen.value = true;
}

function handleEdit(row) {
  followupTitle.value = '编辑跟进记录';
  currentFollowupId.value = row.id;
  followupForm.value = {
    method: row.method || '',
    content: row.content || '',
  };
  followupFormOpen.value = true;
}

function handleDelete(row) {
  ElMessageBox.confirm('确认删除该跟进记录？', '警告', { type: 'warning' })
    .then(() => delFollowup(props.prospectId, row.id))
    .then(res => {
      if (res.success) { fetchList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    }).catch(() => {});
}

async function handleFollowupSubmit() {
  const valid = await followupFormRef.value?.validate().catch(() => false);
  if (!valid) return;
  followupSubmitting.value = true;
  try {
    let res;
    if (currentFollowupId.value) {
      res = await updateFollowup(props.prospectId, currentFollowupId.value, followupForm.value);
    } else {
      res = await addFollowup(props.prospectId, followupForm.value);
    }
    if (res.success) {
      ElMessage.success(currentFollowupId.value ? '编辑成功' : '新增成功');
      followupFormOpen.value = false;
      fetchList();
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } finally { followupSubmitting.value = false; }
}
</script>
