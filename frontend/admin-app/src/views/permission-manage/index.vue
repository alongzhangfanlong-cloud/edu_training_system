<template>
  <div>
    <!-- 角色权限分配 -->
    <el-card header="角色权限分配" style="margin-bottom:16px">
      <el-row :gutter="16">
        <el-col :span="6" v-for="role in roleList" :key="role.id">
          <el-card shadow="hover" class="role-card">
            <div class="role-header">
              <span class="role-name">{{ role.role_name }}</span>
              <el-tag size="small" effect="dark">{{ role.role_code }}</el-tag>
            </div>
            <div class="role-perms">
              <template v-if="role._perms && role._perms.length">
                <el-tag v-for="p in role._perms" :key="p.permissionId" size="small" class="perm-tag"
                  :type="p.permType === 'menu' ? 'success' : ''">
                  {{ p.permCode }}
                </el-tag>
              </template>
              <span v-else class="empty-perms">暂无权限</span>
            </div>
            <el-button type="primary" size="small" style="width:100%;margin-top:12px" @click="editPerms(role)">
              编辑权限
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 所有权限列表 -->
    <el-card>
      <template #header>
        <span>所有权限列表</span>
        <el-tag style="margin-left:8px" type="info">{{ allPermissions.length }}项</el-tag>
      </template>
      <el-table :data="allPermissions" stripe size="small" max-height="400" v-loading="loading"
        empty-text="暂无数据，请确认已登录">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="permCode" label="权限编码" width="180" />
        <el-table-column prop="permName" label="权限名称" width="160" />
        <el-table-column label="类型" width="80" align="center">
          <template #default="scope">
            <el-tag size="small" :type="scope.row.permType === 'menu' ? 'success' : ''">
              {{ scope.row.permType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="parentId" label="父级ID" width="70" align="center" />
        <el-table-column prop="sortOrder" label="排序" width="60" align="center" />
      </el-table>
    </el-card>

    <!-- 编辑权限弹窗 -->
    <el-dialog v-model="dialogVisible" :title="'编辑权限 - ' + editingRole?.role_name" width="650px">
      <div style="max-height:420px;overflow-y:auto;padding:8px">
        <el-checkbox-group v-model="checkedPermIds">
          <el-checkbox v-for="perm in allPermissions" :key="perm.id" :label="perm.id"
            style="display:block;margin-bottom:6px">
            <b>{{ perm.permCode }}</b>
            <span style="color:#888;margin-left:6px">{{ perm.permName }}</span>
            <el-tag size="small" style="margin-left:6px">{{ perm.permType }}</el-tag>
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="savePerms">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const roleList = ref([])
const allPermissions = ref([])
const loading = ref(true)
const dialogVisible = ref(false)
const editingRole = ref(null)
const checkedPermIds = ref([])
const saving = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const perms = await request.get('/permissions')
    allPermissions.value = (Array.isArray(perms) ? perms : []).map(p => ({
      id: p.id, permCode: p.perm_code, permName: p.perm_name,
      permType: p.perm_type, parentId: p.parent_id, sortOrder: p.sort_order
    }))
  } catch (e) {
    ElMessage.error('权限列表加载失败')
  }

  try {
    const res = await request.get('/permissions/role-permissions')
    const roles = res?.roles || []
    const matrix = res?.matrix || []
    roleList.value = roles.map(role => {
      const perms = []
      if (matrix) {
        for (const row of matrix) {
          if (row.roles && row.roles[role.role_code]) {
            perms.push({
              permissionId: row.permissionId,
              permCode: row.permCode,
              permName: row.permName,
              permType: row.permType
            })
          }
        }
      }
      return { ...role, _perms: perms }
    })
  } catch (e) {
    ElMessage.error('角色权限加载失败')
  } finally {
    loading.value = false
  }
})

function editPerms(role) {
  editingRole.value = role
  checkedPermIds.value = (role._perms || []).map(p => p.permissionId)
  dialogVisible.value = true
}

async function savePerms() {
  saving.value = true
  try {
    await request.post('/permissions/assign', {
      roleId: editingRole.value.id,
      permissionIds: checkedPermIds.value
    })
    ElMessage.success('权限已更新')
    dialogVisible.value = false
    window.location.reload()
  } catch (e) {
    /* ok */
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.role-card {
  transition: all 0.2s;
}
.role-card:hover {
  transform: translateY(-2px);
}
.role-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 10px;
}
.role-name {
  font-size: 15px;
  font-weight: 600;
}
.role-perms {
  min-height: 60px;
}
.perm-tag {
  margin: 2px;
}
.empty-perms {
  color: #ccc;
  font-size: 13px;
}
</style>
