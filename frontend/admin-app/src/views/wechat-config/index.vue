<template>
  <div>
    <el-card>
      <template #header><span style="font-weight:600">公众号设置</span></template>
      <el-form :model="form" label-width="120px" style="max-width:600px">
        <el-form-item label="公众号名称"><el-input v-model="form.appName" placeholder="公众号名称" /></el-form-item>
        <el-form-item label="AppID"><el-input v-model="form.appId" placeholder="微信AppID" /></el-form-item>
        <el-form-item label="AppSecret"><el-input v-model="form.appSecret" type="password" placeholder="微信AppSecret" show-password /></el-form-item>
        <el-form-item label="服务器Token"><el-input v-model="form.token" placeholder="服务器验证Token" /></el-form-item>
        <el-form-item label="消息加解密密钥"><el-input v-model="form.encodingAesKey" placeholder="EncodingAESKey" /></el-form-item>
        <el-form-item label="推送模板ID">
          <el-input v-model="form.templateId" placeholder="上课提醒模板消息ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存配置</el-button>
          <el-button @click="testConnection">测试连接</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const form = reactive({
  appName: '教务管理系统',
  appId: '', appSecret: '', token: '', encodingAesKey: '', templateId: ''
})

async function save() {
  try {
    await request.put('/system-config/wechat', form)
    ElMessage.success('保存成功')
  } catch(e) { ElMessage.warning('保存失败，请检查后端服务') }
}

async function testConnection() {
  try {
    await request.get('/system-config/wechat/test')
    ElMessage.success('连接成功')
  } catch(e) { ElMessage.warning('连接失败，请检查配置') }
}
</script>
