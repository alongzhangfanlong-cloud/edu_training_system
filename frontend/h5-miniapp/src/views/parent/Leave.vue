<template>
  <div class="page">
    <van-nav-bar title="请假申请" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <van-cell-group inset>
        <van-field v-model="form.studentId" label="学员ID" type="digit" placeholder="请输入学员ID" />
        <van-field v-model="form.scheduleId" label="排课ID" type="digit" placeholder="选填" />
        <van-field label="请假类型" is-link readonly :model-value="leaveTypeText" @click="showPicker=true" />
        <van-field v-model="form.reason" label="请假原因" type="textarea" rows="3" placeholder="请输入请假原因" />
        <van-button type="primary" block style="margin:12px" @click="submit" :loading="submitting">提交申请</van-button>
      </van-cell-group>
      <van-popup v-model:show="showPicker" position="bottom" round>
        <van-picker :columns="leaveTypes" @confirm="onConfirm" @cancel="showPicker=false" />
      </van-popup>

      <!-- 我的请假记录 -->
      <div style="margin-top:16px">
        <h4 style="padding:0 12px;color:#666">请假记录</h4>
        <van-cell v-for="l in leaveList" :key="l.id" :title="leaveTypeMap[l.leaveType]" :label="l.reason">
          <template #right-icon>
            <van-tag :type="statusType(l.status)">{{ statusMap[l.status] }}</van-tag>
          </template>
        </van-cell>
        <van-empty v-if="!leaveList.length" description="暂无请假记录" />
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { showToast } from 'vant'
import request from '../../utils/request'

const leaveTypes = [{ text: '病假', value: 1 }, { text: '事假', value: 2 }, { text: '其他', value: 3 }]
const leaveTypeMap = { 1: '病假', 2: '事假', 3: '其他' }
const statusMap = { 0: '待审批', 1: '已通过', 2: '已拒绝' }
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s])

const form = reactive({ studentId: '', scheduleId: '', leaveType: 1, reason: '' })
const showPicker = ref(false)
const leaveList = ref([])
const submitting = ref(false)

const leaveTypeText = computed(() => leaveTypeMap[form.leaveType] || '病假')

function onConfirm({ selectedOptions }) {
  form.leaveType = selectedOptions[0].value
  showPicker.value = false
}

onMounted(() => loadLeaves())

async function loadLeaves() {
  try {
    const data = await request.get('/leaves', { params: { status: 0 } })
    leaveList.value = data?.list || []
    // 也加载已审批的
    const data2 = await request.get('/leaves')
    if (data2?.list) leaveList.value = data2.list
  } catch(e) {}
}

async function submit() {
  if (!form.studentId) return showToast('请输入学员ID')
  if (!form.reason) return showToast('请输入请假原因')
  submitting.value = true
  try {
    await request.post('/leaves', {
      studentId: Number(form.studentId),
      scheduleId: form.scheduleId ? Number(form.scheduleId) : null,
      leaveType: form.leaveType,
      reason: form.reason
    })
    showToast('申请已提交')
    form.reason = ''
    loadLeaves()
  } catch(e) {} finally { submitting.value = false }
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}</style>
