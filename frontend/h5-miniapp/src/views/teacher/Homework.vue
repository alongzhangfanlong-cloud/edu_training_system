<template>
  <div class="page">
    <van-nav-bar title="布置作业" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell-group inset>
        <van-field v-model="form.classId" label="班级ID" type="digit" placeholder="请输入班级ID" />
        <van-field v-model="form.scheduleId" label="排课ID" type="digit" placeholder="选填" />
        <van-field v-model="form.content" label="作业内容" type="textarea" rows="4" placeholder="请输入今日作业内容" />
        <van-button type="primary" block style="margin:12px" @click="submit" :loading="submitting">确认布置</van-button>
      </van-cell-group>
      <div style="margin-top:16px">
        <h4 style="padding:0 12px;color:#666">已布置作业</h4>
        <van-cell v-for="h in homeworkList" :key="h.id" :title="h.content" :label="h.createdAt" />
        <van-empty v-if="!homeworkList.length" description="暂无作业" />
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { showToast } from 'vant'
import request from '../../utils/request'

const form = reactive({ classId: '', scheduleId: '', content: '' })
const homeworkList = ref([])
const submitting = ref(false)

onMounted(() => loadHomework())

async function loadHomework() {
  try {
    const data = await request.get('/homework')
    homeworkList.value = data?.list || []
  } catch(e) {}
}

async function submit() {
  if (!form.content) return showToast('请输入作业内容')
  submitting.value = true
  try {
    await request.post('/homework', {
      classId: form.classId ? Number(form.classId) : null,
      scheduleId: form.scheduleId ? Number(form.scheduleId) : null,
      content: form.content
    })
    showToast('作业已布置')
    form.content = ''
    loadHomework()
  } catch(e) {} finally { submitting.value = false }
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}</style>
