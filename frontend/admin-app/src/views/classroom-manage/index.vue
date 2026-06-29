<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-form :inline="true">
          <el-form-item><el-input v-model="filters.keyword" placeholder="搜索教室名称" clearable style="width:200px" /></el-form-item>
          <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
        </el-form>
        <el-button type="success" @click="openDialog()">+ 新增教室</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="教室名称" min-width="140" />
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="location" label="位置" min-width="180" />
        <el-table-column label="设施" min-width="180">
          <template #default="{row}"><el-tag v-for="f in (row.facilities||[])" :key="f" size="small" style="margin:2px">{{ f }}</el-tag></template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'正常':'停用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="remove(row)"><template #reference><el-button size="small" type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑教室':'新增教室'" width="550px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="教室名称" prop="name"><el-input v-model="form.name" placeholder="如: 301多媒体教室" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="容纳人数"><el-input-number v-model="form.capacity" :min="1" :max="500" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="位置描述"><el-input v-model="form.location" placeholder="如: 3楼东侧, 近电梯" /></el-form-item>
        <el-form-item label="坐标">
          <el-row :gutter="12">
            <el-col :span="12"><el-input v-model="form.latitude" placeholder="纬度" /></el-col>
            <el-col :span="12"><el-input v-model="form.longitude" placeholder="经度" /></el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="设施">
          <el-select v-model="form.facilities" multiple filterable allow-create placeholder="输入设施名称回车添加" style="width:100%">
            <el-option v-for="f in facilityOptions" :key="f" :label="f" :value="f" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="saving" @click="submitForm">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const tableData = ref([]); const loading = ref(false); const total = ref(0); const page = ref(1); const pageSize = ref(20)
const filters = reactive({ keyword: '' })
const dialogVisible = ref(false); const isEdit = ref(false); const saving = ref(false)
const formRef = ref(null)
const form = reactive({ name: '', capacity: 30, location: '', latitude: null, longitude: null, facilities: [], status: 1 })
const rules = { name: [{ required: true, message: '请输入教室名称' }] }
const facilityOptions = ['投影仪','白板','电脑','空调','钢琴','舞蹈镜','画架','实验台','电子琴','音响系统','摄像头','储物柜']

onMounted(loadData)
async function loadData() {
  loading.value = true
  try { const d = await request.get('/classrooms', { params: { ...filters, page: page.value, pageSize: pageSize.value } }); tableData.value = d?.list || []; total.value = d?.total || 0 } catch(e) {} finally { loading.value = false }
}
function openDialog(row) {
  if (row) { isEdit.value = true; Object.assign(form, { ...row, facilities: row.facilities || [] }) } else { isEdit.value = false; resetForm() }
  dialogVisible.value = true
}
function resetForm() { Object.assign(form, { name: '', capacity: 30, location: '', latitude: null, longitude: null, facilities: [], status: 1 }) }
async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) { await request.put(`/classrooms/${form.id}`, form); ElMessage.success('更新成功') }
    else { await request.post('/classrooms', form); ElMessage.success('创建成功') }
    dialogVisible.value = false; loadData()
  } catch(e) {} finally { saving.value = false }
}
async function remove(row) { try { await request.delete(`/classrooms/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {} }
</script>
<style scoped>.toolbar { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; flex-wrap: wrap; }</style>
