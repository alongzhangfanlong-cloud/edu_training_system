<template><div class="page"><van-nav-bar title="考勤签到" left-arrow @click-left="$router.push('/boss/jiaowu')" fixed placeholder/><div class="content"><van-empty v-if="!schedules.length" description="今日无课程"/><div v-for="s in schedules" :key="s.id" class="card"><van-cell :title="s.courseName||`课程#${s.courseId}`" :label="`${s.date} ${s.startTime}-${s.endTime}`"/><van-cell v-for="st in getStudents(s.classId)" :key="st.id" :title="st.name"><template #right-icon><span :style="{color:st._status===1?'#52c41a':st._status===2?'#faad14':'#ff4d4f',fontSize:'13px'}">{{ {1:'出勤',2:'请假',3:'旷课'}[st._status] }}</span></template></van-cell></div></div></div></template>
<script setup>
import { ref, onMounted } from 'vue'; import request from '../../utils/request'
const schedules=ref([])
const classStudentMap=ref({})
onMounted(async()=>{
  const today=new Date().toISOString().slice(0,10)
  try{const r=await request.get('/schedules',{params:{startDate:today,endDate:today}});schedules.value=r?.schedules||[]}catch(e){}
  for(const s of schedules.value){await loadStudents(s.classId)}
})
async function loadStudents(cid){if(!cid)return;try{const d=await request.get('/students',{params:{classId:cid,pageSize:200}});classStudentMap.value[cid]=(d?.list||[]).map(s=>({...s,_status:1}))}catch(e){}}
function getStudents(cid){return classStudentMap.value[cid]||[]}
</script>
<style scoped>.page{background:#f7f8fa;min-height:100vh}.content{padding:12px}.card{background:#fff;border-radius:12px;margin-bottom:12px;overflow:hidden}</style>
