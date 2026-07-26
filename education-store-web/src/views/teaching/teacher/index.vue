<!-- 教师管理 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="教师姓名/电话" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd" v-permission="['teaching:teacher:add']">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain size="small" :disabled="multiple" @click="handleDelete(null)" v-permission="['teaching:teacher:delete']">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="教师姓名" align="center" prop="realName" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="可授科目" align="center" prop="subjects" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="课酬单价" align="center" prop="defaultPayPerLesson" width="100" />
      <el-table-column label="入职日期" align="center" prop="hireDate" width="110" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '在职' : '离职' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleUpdate(row)" v-permission="['teaching:teacher:edit']">修改</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="['teaching:teacher:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />

    <el-dialog :model-value="open" :title="title" width="500px" destroy-on-close append-to-body @open="onOpen" @update:model-value="(val) => open = val">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="教师姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入教师姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" @input="form.phone = form.phone?.replace(/\D/g, '')" />
        </el-form-item>
        <el-form-item label="可授科目">
          <el-input v-model="form.subjects" placeholder="多个科目逗号分隔" />
        </el-form-item>
        <el-form-item label="课酬单价" prop="defaultPayPerLesson">
          <el-input-number v-model="form.defaultPayPerLesson" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职日期">
              <el-date-picker v-model="form.hireDate" type="date" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width:100%">
                <el-option label="在职" value="ACTIVE" />
                <el-option label="离职" value="INACTIVE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue';
import { listTeacher, getTeacher, addTeacher, updateTeacher, delTeacher } from '@/api/teaching/teachers';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/Pagination/index.vue';

const loading = ref(false);
const showSearch = ref(true);
const list = ref([]);
const total = ref(0);
const selectedIds = ref([]);
const multiple = computed(() => selectedIds.value.length === 0);
const queryParams = ref({ page: 1, size: 10, keyword: '' });
const open = ref(false); const title = ref(''); const currentId = ref(null);
const formRef = ref(null); const submitting = ref(false);
const form = ref({ realName: '', phone: '', subjects: '', defaultPayPerLesson: null, hireDate: '', status: 'ACTIVE', remark: '' });
const rules = {
  realName: [{ required: true, message: '请输入教师姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  defaultPayPerLesson: [{ required: true, message: '请输入课酬单价', trigger: 'blur' }],
};
let querySeq = 0;

function getList() {
  loading.value = true; querySeq++; const seq = querySeq;
  const params = { ...queryParams.value }; if (!params.keyword) delete params.keyword;
  listTeacher(params).then(res => {
    if (seq !== querySeq) return;
    if (res.success) { list.value = res.data?.records || res.rows || []; total.value = res.data?.total || res.total || 0; }
  }).finally(() => { if (seq === querySeq) loading.value = false; });
}
function handleQuery() { queryParams.value.page = 1; getList(); }
function resetQuery() { queryParams.value = { page: 1, size: 10, keyword: '' }; getList(); }
function handleAdd() { title.value = '新增教师'; currentId.value = null; open.value = true; }
function handleUpdate(row) { title.value = '修改教师'; currentId.value = row.id; open.value = true; }
function handleDelete(row) {
  const ids = row ? [row.id] : selectedIds.value;
  if (!ids || ids.length === 0) { ElMessage.warning('请选择教师'); return; }
  ElMessageBox.confirm('确认删除选中的教师？', '警告', { type: 'warning' })
    .then(() => delTeacher(ids.join(','))).then(res => {
      if (res.success) { getList(); ElMessage.success('删除成功'); }
      else { ElMessage.error(res.msg || '删除失败'); }
    });
}
function handleSelectionChange(selection) { selectedIds.value = selection.map(i => i.id); }
async function onOpen() {
  reset();
  if (currentId.value) {
    const res = await getTeacher(currentId.value);
    if (res.success && res.data) {
      const t = res.data.data || res.data;
      form.value = { realName: t.realName || '', phone: t.phone || '', subjects: t.subjects || '', defaultPayPerLesson: t.defaultPayPerLesson || null, hireDate: t.hireDate || '', status: t.status || 'ACTIVE', remark: t.remark || '' };
    }
  }
}
function reset() { form.value = { realName: '', phone: '', subjects: '', defaultPayPerLesson: null, hireDate: '', status: 'ACTIVE', remark: '' }; nextTick(() => formRef.value?.clearValidate()); }
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return;
  submitting.value = true;
  try {
    const data = { ...form.value }; let res;
    if (currentId.value) { data.id = currentId.value; res = await updateTeacher(data); }
    else { res = await addTeacher(data); }
    if (res.success) { ElMessage.success(currentId.value ? '修改成功' : '新增成功'); open.value = false; getList(); }
    else { ElMessage.error(res.msg || '操作失败'); }
  } finally { submitting.value = false; }
}
onMounted(() => { getList(); });
</script>

<style scoped lang="less">
.app-container { padding: 16px; }
.mb8 { margin-bottom: 8px; }
</style>
