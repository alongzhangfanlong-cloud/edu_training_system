<template>
  <div>
    <el-card>
      <div style="margin-bottom:16px">
        <el-button type="primary" @click="openDialog()">+ 新增菜单</el-button>
        <el-button @click="loadData">刷新</el-button>
      </div>
      <el-table :data="flatList" v-loading="loading" row-key="id" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="menuName" label="菜单名称" min-width="160">
          <template #default="{row}">
            <span :style="{ paddingLeft: (row._level * 24) + 'px' }">
              {{ row._level > 0 ? '└ ' : '' }}{{ row.menuIcon ? row.menuIcon + ' ' : '' }}{{ row.menuName }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="menuPath" label="路由路径" width="180" />
        <el-table-column label="类型" width="80">
          <template #default="{row}"><el-tag size="small" :type="row.menuType===1?'':'success'">{{ row.menuType===1?'目录':'菜单' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="permCode" label="权限编码" width="130" />
        <el-table-column prop="sortOrder" label="排序" width="60" />
        <el-table-column label="可见" width="60">
          <template #default="{row}"><el-tag size="small" :type="row.visible===1?'success':'info'">{{ row.visible===1?'是':'否' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{row}">
            <el-button size="small" text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除?子菜单也将删除" @confirm="remove(row)"><template #reference><el-button size="small" text type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑菜单':'新增菜单'" width="550px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级菜单"><el-select v-model="form.parentId" placeholder="顶级菜单" clearable style="width:100%"><el-option v-for="m in flatList" :key="m.id" :label="m.menuName" :value="m.id" /></el-select></el-form-item>
        <el-form-item label="菜单名称" prop="menuName"><el-input v-model="form.menuName" /></el-form-item>
        <el-form-item label="路由路径"><el-input v-model="form.menuPath" placeholder="如 /dashboard" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.menuIcon" placeholder="Element图标名, 如 User" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="8"><el-form-item label="类型"><el-select v-model="form.menuType" style="width:100%"><el-option label="目录" :value="1" /><el-option label="菜单" :value="2" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="可见"><el-switch v-model="form.visible" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="权限编码"><el-input v-model="form.permCode" placeholder="如 student:read" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="saving" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const flatList = ref([]); const loading = ref(false)
const dialogVisible = ref(false); const isEdit = ref(false); const saving = ref(false)
const formRef = ref(null)
const form = reactive({ parentId: null, menuName: '', menuPath: '', menuIcon: '', menuType: 2, permCode: '', sortOrder: 0, visible: 1 })
const rules = { menuName: [{ required: true, message: '请输入菜单名称' }] }

onMounted(loadData)
async function loadData() {
  loading.value = true
  try { flatList.value = flatten(await request.get('/menus/tree') || []) } catch(e) {} finally { loading.value = false }
}
function flatten(tree, level = 0) {
  let result = []
  for (const node of tree) {
    result.push({ ...node, _level: level })
    if (node.children) result = result.concat(flatten(node.children, level + 1))
  }
  return result
}
function openDialog(row) {
  if (row) { isEdit.value = true; Object.assign(form, row) } else { isEdit.value = false; Object.assign(form, { parentId: null, menuName: '', menuPath: '', menuIcon: '', menuType: 2, permCode: '', sortOrder: 0, visible: 1 }) }
  dialogVisible.value = true
}
async function submit() {
  const valid = await formRef.value?.validate().catch(() => false); if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) await request.put(`/menus/${form.id}`, form)
    else await request.post('/menus', form)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功'); dialogVisible.value = false; loadData()
  } catch(e) {} finally { saving.value = false }
}
async function remove(row) { try { await request.delete(`/menus/${row.id}`); ElMessage.success('已删除'); loadData() } catch(e) {} }
</script>
