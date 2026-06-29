<template>
  <div>
    <el-card>
      <div style="margin-bottom:16px">
        <el-button type="primary" @click="showDialog()">+ 新增校区</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="campus_name" label="校区名称" min-width="160" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="principal_id" label="校长ID" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{row.status===1?'正常':'停用'}}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{row}">
            <el-button size="small" text type="primary" @click="showDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="remove(row)"><template #reference><el-button size="small" text type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑校区':'新增校区'" width="500px">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="校区名称" required><el-input v-model="form.campusName" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="校长ID"><el-input-number v-model="form.principalId" :min="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="saving" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const list = ref([]); const loading = ref(false)
const dialogVisible = ref(false); const isEdit = ref(false); const saving = ref(false)
const formRef = ref(null)
const form = reactive({ campusName: '', address: '', phone: '', principalId: null })

onMounted(loadData)
async function loadData() { loading.value = true; try { list.value = await request.get('/campuses') || [] } catch(e){} finally { loading.value = false } }
function showDialog(row) {
  if (row) { isEdit.value = true; Object.assign(form, { id: row.id, campusName: row.campus_name, address: row.address, phone: row.phone, principalId: row.principal_id }) }
  else { isEdit.value = false; Object.assign(form, { campusName: '', address: '', phone: '', principalId: null }) }
  dialogVisible.value = true
}
async function submit() {
  saving.value = true
  try {
    if (isEdit.value) await request.put(`/campuses/${form.id}`, form)
    else await request.post('/campuses', form)
    ElMessage.success('保存成功'); dialogVisible.value = false; loadData()
  } catch(e){} finally { saving.value = false }
}
async function remove(row) { try { await request.delete(`/campuses/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e){} }
</script>
