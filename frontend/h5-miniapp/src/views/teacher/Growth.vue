<template>
  <div class="page">
    <van-nav-bar title="成长档案" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="content">
      <!-- 学员列表 -->
      <van-search v-model="keyword" placeholder="搜索学员" />
      <van-cell v-for="s in filteredStudents" :key="s.id" :title="s.name" :label="`${s.grade||''} | 剩余${s.remainHours||0}课时`" is-link @click="selectStudent(s)" />
      <van-empty v-if="!filteredStudents.length" description="暂无学员" />

      <!-- 选中学员的成长记录 -->
      <van-popup v-model:show="showDetail" position="right" :style="{ width: '100%', height: '100%' }">
        <van-nav-bar :title="`${selected.name}的成长档案`" left-arrow @click-left="showDetail=false" fixed placeholder />
        <div style="padding:12px">
          <van-button type="primary" size="small" style="margin-bottom:12px" @click="showAddRecord=true">+ 新增评价</van-button>
          <div v-for="r in records" :key="r.id" class="record-card">
            <div class="date">{{ r.recordDate }}</div>
            <div class="text">{{ r.content }}</div>
            <div class="score">课堂表现: {{ '★'.repeat(r.score||0) }}{{ '☆'.repeat(5-(r.score||0)) }}</div>
          </div>
          <van-empty v-if="!records.length" description="暂无记录" />
        </div>
      </van-popup>

      <!-- 新增评价弹窗 -->
      <van-popup v-model:show="showAddRecord" position="bottom" round :style="{ maxHeight: '70%' }">
        <div style="padding:16px">
          <h4 style="margin-bottom:12px">新增成长评价</h4>
          <van-field v-model="newRecord.content" label="评语" type="textarea" rows="3" placeholder="请输入评价内容" />
          <van-field name="rate" label="评分">
            <template #input><van-rate v-model="newRecord.score" /></template>
          </van-field>
          <van-button type="primary" block style="margin-top:12px" @click="addRecord" :loading="adding">提交评价</van-button>
        </div>
      </van-popup>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import request from '../../utils/request'

const keyword = ref('')
const students = ref([])
const selected = ref({})
const records = ref([])
const showDetail = ref(false)
const showAddRecord = ref(false)
const adding = ref(false)
const newRecord = ref({ content: '', score: 3 })

const filteredStudents = computed(() => {
  if (!keyword.value) return students.value
  return students.value.filter(s => s.name?.includes(keyword.value))
})

onMounted(async () => {
  try {
    const d = await request.get('/students', { params: { pageSize: 50 } })
    students.value = d?.list || []
  } catch(e) {}
})

async function selectStudent(s) {
  selected.value = s
  showDetail.value = true
  await loadRecords(s.id)
}

async function loadRecords(studentId) {
  try {
    const data = await request.get('/growth-records', { params: { studentId } })
    records.value = data?.list || []
  } catch(e) {}
}

async function addRecord() {
  if (!newRecord.value.content) return showToast('请输入评语')
  adding.value = true
  try {
    await request.post('/growth-records', {
      studentId: selected.value.id,
      content: newRecord.value.content,
      score: newRecord.value.score
    })
    showToast('评价已提交')
    newRecord.value = { content: '', score: 3 }
    showAddRecord.value = false
    loadRecords(selected.value.id)
  } catch(e) {} finally { adding.value = false }
}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:0}.record-card{background:#fff;border-radius:12px;padding:16px;margin-bottom:12px}.date{font-size:15px;font-weight:600;margin-bottom:4px}.text{font-size:14px;color:#666;line-height:1.6}.score{font-size:16px;color:#faad14;margin-top:6px}</style>
