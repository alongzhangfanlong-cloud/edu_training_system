<template>
  <div>
    <!-- 筛选栏 -->
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="班级">
          <el-select v-model="filters.classId" placeholder="全部班级" clearable filterable style="width:220px" @change="loadData">
            <el-option v-for="c in classes" :key="c.id" :label="`${c.name} (${c.courseName||''})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="success" @click="showCreateDialog">+ 布置作业</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 作业列表 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="className" label="班级" width="130">
          <template #default="{row}">
            <span>{{ row.className || '-' }}</span>
            <div v-if="row.courseName" style="font-size:12px;color:#999">{{ row.courseName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="教师" width="90" />
        <el-table-column prop="content" label="作业内容" min-width="260" show-overflow-tooltip />
        <el-table-column label="图片" width="120">
          <template #default="{row}">
            <div v-if="row.images" class="img-list">
              <el-image v-for="(url, i) in parseImages(row.images)" :key="i"
                :src="url" :preview-src-list="parseImages(row.images)"
                style="width:40px;height:40px;border-radius:4px;margin:2px;object-fit:cover"
                fit="cover" :initial-index="i" />
            </div>
            <span v-else style="color:#ccc">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="布置时间" width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="editRow(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="deleteRow(row)">
              <template #reference><el-button size="small" text type="danger">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑作业' : '布置作业'" width="600px" @close="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="班级" required>
          <el-select v-model="form.classId" placeholder="请选择班级" filterable style="width:100%" @change="onClassChange">
            <el-option v-for="c in classes" :key="c.id" :label="`${c.name} (${c.courseName||''})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排课">
          <el-select v-model="form.scheduleId" placeholder="关联排课（可选）" clearable filterable style="width:100%">
            <el-option v-for="s in filteredSchedules" :key="s.id"
              :label="`${s.date} ${s.startTime}-${s.endTime}`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" required>
          <el-select v-model="form.teacherId" placeholder="请选择教师" filterable style="width:100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="作业内容" required>
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入作业内容..." />
        </el-form-item>
        <el-form-item label="上传图片">
          <div style="width:100%">
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              name="files"
              multiple
              list-type="picture-card"
              :on-success="onUploadSuccess"
              :on-remove="onUploadRemove"
              :file-list="fileList"
              accept="image/*">
              <el-icon><Plus /></el-icon>
            </el-upload>
            <span style="font-size:12px;color:#999">支持多图上传，单张不超过5MB</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../../utils/request'

const filters = reactive({ classId: null })
const tableData = ref([]); const loading = ref(false)
const dialogVisible = ref(false); const editingId = ref(null); const saving = ref(false)
const form = reactive({ classId: null, scheduleId: null, teacherId: null, content: '', images: '' })
const classes = ref([]); const teachers = ref([]); const schedules = ref([])
const fileList = ref([])
const uploadUrl = '/api/v1/upload/image'
const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const filteredSchedules = computed(() => {
  if (!form.classId) return schedules.value
  return schedules.value.filter(s => s.classId === form.classId)
})

onMounted(() => { loadData(); loadRefs() })

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (filters.classId) params.classId = filters.classId
    const data = await request.get('/homework', { params })
    tableData.value = data?.list || []
  } catch(e) {} finally { loading.value = false }
}

async function loadRefs() {
  try { classes.value = (await request.get('/classes', { params: { pageSize: 100 } }))?.list || [] } catch(e) {}
  try { teachers.value = (await request.get('/teachers', { params: { pageSize: 100 } }))?.list || [] } catch(e) {}
  try { schedules.value = (await request.get('/schedules', { params: { pageSize: 200 } }))?.list || [] } catch(e) {}
}

function parseImages(val) {
  if (!val) return []
  if (Array.isArray(val)) return val
  try { return JSON.parse(val) } catch(e) { return [] }
}

function onClassChange(classId) {
  const cls = classes.value.find(c => c.id === classId)
  if (cls) {
    form.teacherId = cls.teacherId || form.teacherId
    form.scheduleId = null
  }
}

function showCreateDialog() {
  editingId.value = null
  form.classId = null; form.scheduleId = null; form.teacherId = null; form.content = ''; form.images = ''
  fileList.value = []
  dialogVisible.value = true
}

function editRow(row) {
  editingId.value = row.id
  form.classId = row.classId; form.scheduleId = row.scheduleId; form.teacherId = row.teacherId
  form.content = row.content; form.images = row.images || ''
  // 重建 fileList
  const urls = parseImages(row.images)
  fileList.value = urls.map((url, i) => ({ name: `image-${i}`, url: url, status: 'success', uid: i }))
  dialogVisible.value = true
}

function resetForm() {
  form.classId = null; form.scheduleId = null; form.teacherId = null; form.content = ''; form.images = ''
  fileList.value = []
}

function onUploadSuccess(response, file) {
  if (response?.code === 200 && response.data) {
    const urls = response.data
    // Append new urls to existing images
    let existing = form.images ? parseImages(form.images) : []
    form.images = JSON.stringify([...existing, ...urls])
  }
}

function onUploadRemove(file) {
  // Remove the image URL from form.images
  let existing = parseImages(form.images)
  const urlToRemove = file.url || (file.response?.data?.[0])
  if (urlToRemove) {
    existing = existing.filter(u => u !== urlToRemove)
    form.images = existing.length > 0 ? JSON.stringify(existing) : ''
  }
}

async function submitForm() {
  if (!form.classId) { ElMessage.warning('请选择班级'); return }
  if (!form.teacherId) { ElMessage.warning('请选择教师'); return }
  if (!form.content) { ElMessage.warning('请输入作业内容'); return }
  saving.value = true
  try {
    const payload = { ...form }
    if (editingId.value) { await request.put(`/homework/${editingId.value}`, payload) }
    else { await request.post('/homework', payload) }
    ElMessage.success('保存成功'); dialogVisible.value = false; loadData()
  } catch(e) {} finally { saving.value = false }
}

async function deleteRow(row) {
  try { await request.delete(`/homework/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {}
}
</script>

<style scoped>
.filter-form { margin-bottom: 0; }
.img-list { display: flex; flex-wrap: wrap; gap: 2px; }
</style>
