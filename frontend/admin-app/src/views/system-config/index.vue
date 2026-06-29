<template>
  <div>
    <el-card header="系统参数配置">
      <el-form label-width="180px">
        <el-form-item v-for="c in configs" :key="c.key" :label="c.description">
          <el-input-number v-if="c.key.includes('hours')||c.key.includes('days')" v-model="c.value" :min="0" :max="9999" style="width:200px" />
          <el-input-number v-else-if="c.key.includes('meters')" v-model="c.value" :min="10" :max="10000" style="width:200px" />
          <el-input-number v-else-if="c.key.includes('minutes')" v-model="c.value" :min="1" :max="120" style="width:200px" />
          <el-input-number v-else-if="c.key.includes('threshold')" v-model="c.value" :min="0" :max="100" style="width:200px" />
          <el-button style="margin-left:12px" type="primary" size="small" @click="save(c)">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const configs = ref([])

onMounted(async () => {
  try { configs.value = await request.get('/system-configs') } catch(e) {}
})

async function save(c) {
  try {
    await request.put(`/system-configs/${c.key}`, { value: String(c.value) })
    ElMessage.success(`${c.description} 已更新为 ${c.value}`)
  } catch(e) {}
}
</script>
