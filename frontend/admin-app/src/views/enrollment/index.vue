<template>
  <div>
    <el-card>
      <!-- 步骤条 -->
      <el-steps :active="step" finish-status="success" align-center style="margin-bottom:28px">
        <el-step title="选择学员" /><el-step title="选择课程" /><el-step title="确认信息" /><el-step title="完成支付" />
      </el-steps>

      <!-- Step 0: 搜索选择学员 -->
      <div v-show="step === 0">
        <div style="margin-bottom:16px">
          <el-input v-model="studentSearch" placeholder="搜索学员姓名/手机号/编号" clearable style="width:360px" @keyup.enter="searchStudents" />
          <el-button type="primary" style="margin-left:12px" @click="searchStudents">搜索</el-button>
        </div>
        <el-table :data="students" highlight-current-row @row-click="selectStudent" max-height="350" stripe>
          <el-table-column prop="studentNo" label="编号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="grade" label="年级" width="80" />
          <el-table-column label="状态" width="80">
            <template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{ {0:'意向',1:'在读',2:'停课',3:'结课'}[row.status] }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="remainHours" label="剩余课时" width="90" />
          <el-table-column label="选择" width="60">
            <template #default="{row}"><el-radio v-model="selectedStudentId" :value="row.id" @click.native.stop="selectStudent(row)" /></template>
          </el-table-column>
        </el-table>
        <div v-if="selectedStudent" class="selected-info">
          ✅ 已选学员: <b>{{ selectedStudent.name }}</b> ({{ selectedStudent.studentNo }}) {{ selectedStudent.phone }}
        </div>
      </div>

      <!-- Step 1: 选择课程和班级 -->
      <div v-show="step === 1">
        <el-form label-width="80px">
          <el-form-item label="选择课程" required>
            <el-select v-model="enroll.courseId" placeholder="请先选择课程" filterable style="width:400px" @change="onCourseChange">
              <el-option v-for="c in courses" :key="c.id" :label="`${c.name} (${c.subject||''} / ¥${c.unitPrice||0}/课时)`" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="选择班级" required>
            <el-select v-model="enroll.classId" placeholder="请先选择课程后再选班级" filterable style="width:400px" :disabled="!enroll.courseId">
              <el-option v-for="c in availableClasses" :key="c.id"
                :label="`${c.name} | ${c.teacherName||''} | 已报${c.currentStudents||0}/${c.maxStudents||0}人`"
                :value="c.id">
                <span>{{ c.name }}</span>
                <span style="float:right;color:#999;font-size:13px">{{ c.teacherName }} | {{ c.currentStudents }}/{{ c.maxStudents }}</span>
              </el-option>
            </el-select>
            <span v-if="enroll.courseId && availableClasses.length === 0" style="color:#ff4d4f;margin-left:12px">该课程暂无可用班级</span>
          </el-form-item>
          <el-form-item label="报名类型">
            <el-select v-model="enroll.itemType" style="width:200px">
              <el-option label="课时包" :value="1" /><el-option label="按期" :value="2" /><el-option label="按年" :value="3" /><el-option label="次卡" :value="4" />
            </el-select>
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="8"><el-form-item label="课时数"><el-input-number v-model="enroll.hours" :min="1" :max="500" :step="10" style="width:100%" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="单价(元)"><el-input-number v-model="enroll.unitPrice" :min="0" :precision="2" :step="10" style="width:100%" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="赠送课时"><el-input-number v-model="enroll.giftHours" :min="0" :max="100" style="width:100%" /></el-form-item></el-col>
          </el-row>
          <el-form-item label="优惠"><el-input v-model="enroll.discountCode" placeholder="优惠码(选填)" style="width:200px" /></el-form-item>
          <el-divider />
          <div class="price-summary">
            <span class="price-label">课程单价:</span><span>¥{{ enroll.unitPrice }} × {{ enroll.hours }}课时</span>
            <span style="margin-left:20px">=</span>
            <span class="price-total">¥{{ totalPrice }}</span>
            <span v-if="enroll.giftHours > 0" style="color:#52c41a;margin-left:12px">(赠送{{ enroll.giftHours }}课时)</span>
          </div>
        </el-form>
      </div>

      <!-- Step 2: 确认信息 -->
      <div v-show="step === 2">
        <el-descriptions title="订单确认" :column="2" border>
          <el-descriptions-item label="学员">{{ selectedStudent?.name }}</el-descriptions-item>
          <el-descriptions-item label="学员编号">{{ selectedStudent?.studentNo }}</el-descriptions-item>
          <el-descriptions-item label="课程">{{ selectedCourse?.name }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ selectedClass?.name }}</el-descriptions-item>
          <el-descriptions-item label="教师">{{ selectedClass?.teacherName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="教室">{{ selectedClass?.classroomName || '待定' }}</el-descriptions-item>
          <el-descriptions-item label="课时数">{{ enroll.hours }} 课时</el-descriptions-item>
          <el-descriptions-item label="赠送">{{ enroll.giftHours }} 课时</el-descriptions-item>
          <el-descriptions-item label="单价">¥{{ enroll.unitPrice }}/课时</el-descriptions-item>
          <el-descriptions-item label="合计"><span style="color:#ff4d4f;font-size:20px;font-weight:700">¥{{ totalPrice }}</span></el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- Step 3: 支付 -->
      <div v-show="step === 3">
        <el-form label-width="100px">
          <el-form-item label="支付方式">
            <el-radio-group v-model="payMethod">
              <el-radio :value="1">💚 微信支付</el-radio>
              <el-radio :value="2">💙 支付宝</el-radio>
              <el-radio :value="3">💵 线下支付</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="payMethod === 3" label="支付备注">
            <el-input v-model="offlineRemark" placeholder="如: 现金收款、张三刷卡" style="width:300px" />
          </el-form-item>
        </el-form>
        <div style="text-align:center;padding:40px 0">
          <span style="font-size:14px;color:#999">实付金额</span>
          <div style="font-size:36px;font-weight:700;color:#ff4d4f;margin:8px 0">¥{{ totalPrice }}</div>
        </div>

        <!-- 微信/支付宝: 显示二维码 -->
        <div v-if="payQrCode" style="text-align:center;padding:20px">
          <p style="color:#666;margin-bottom:12px">
            {{ payMethod === 1 ? '请使用微信扫码支付' : '请使用支付宝扫码支付' }}
          </p>
          <div style="background:#fff;padding:20px;display:inline-block;border:1px solid #eee;border-radius:8px">
            <div style="width:200px;height:200px;background:#f5f5f5;display:flex;align-items:center;justify-content:center;flex-direction:column">
              <span style="font-size:48px">{{ payMethod === 1 ? '💚' : '💙' }}</span>
              <span style="font-size:13px;color:#999;margin-top:8px">开发模式</span>
              <span style="font-size:11px;color:#bbb;word-break:break-all;text-align:center;padding:0 8px">{{ payQrCode }}</span>
            </div>
          </div>
          <p style="color:#999;font-size:12px;margin-top:8px">生产环境将显示真实支付二维码</p>
        </div>

        <!-- 支付结果 -->
        <el-result v-if="payResult" :icon="payResult.success ? 'success' : 'error'"
          :title="payResult.success ? '支付成功！' : '支付失败'"
          :sub-title="payResult.message">
          <template #extra v-if="payResult.success">
            <el-button type="primary" @click="resetEnroll">继续报名</el-button>
            <el-button @click="$router.push('/student-manage')">查看学员</el-button>
          </template>
        </el-result>
      </div>

      <!-- 步骤按钮 -->
      <div style="text-align:center;margin-top:28px" v-if="!payResult">
        <el-button v-if="step > 0" @click="step--">上一步</el-button>
        <el-button v-if="step === 0" type="primary" :disabled="!selectedStudent" @click="step=1">下一步</el-button>
        <el-button v-if="step === 1" type="primary" :disabled="!canGoStep2" @click="step=2">下一步</el-button>
        <el-button v-if="step === 2" type="primary" @click="step=3">确认，去支付</el-button>
        <el-button v-if="step === 3" type="success" :loading="paying" @click="doPay">💵 确认支付 ¥{{ totalPrice }}</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

// 步骤
const step = ref(0)
const students = ref([])
const courses = ref([])
const availableClasses = ref([])
const studentSearch = ref('')
const selectedStudent = ref(null)
const selectedStudentId = ref(null)
const payMethod = ref(3)
const offlineRemark = ref('')
const paying = ref(false)
const payResult = ref(null)
const payQrCode = ref('')

const enroll = reactive({
  courseId: null, classId: null, itemType: 1,
  hours: 20, unitPrice: 100, giftHours: 0, discountCode: ''
})

const selectedCourse = computed(() => courses.value.find(c => c.id === enroll.courseId))
const selectedClass = computed(() => availableClasses.value.find(c => c.id === enroll.classId))
const totalPrice = computed(() => (enroll.hours * enroll.unitPrice).toFixed(2))
const canGoStep2 = computed(() => enroll.courseId && enroll.classId && enroll.hours > 0 && enroll.unitPrice > 0)

onMounted(() => { searchStudents(); loadCourses() })

async function searchStudents() {
  try {
    const params = { pageSize: 100 }
    if (studentSearch.value) params.keyword = studentSearch.value
    const d = await request.get('/students', { params })
    students.value = d?.list || []
  } catch(e) {}
}
async function loadCourses() {
  try { const d = await request.get('/courses', { params: { pageSize: 100, status: 1 } }); courses.value = d?.list || [] } catch(e) {}
}
function selectStudent(row) {
  selectedStudent.value = row
  selectedStudentId.value = row.id
}
async function onCourseChange(courseId) {
  enroll.classId = null
  if (!courseId) { availableClasses.value = []; return }
  try {
    const d = await request.get('/classes', { params: { courseId, pageSize: 100, status: 1 } })
    availableClasses.value = (d?.list || []).filter(c => (c.currentStudents || 0) < (c.maxStudents || 999))
    // 自动填充单价
    const course = courses.value.find(c => c.id === courseId)
    if (course && course.unitPrice) enroll.unitPrice = course.unitPrice
  } catch(e) {}
}

async function doPay() {
  paying.value = true
  try {
    // 1. 创建订单
    const order = await request.post('/orders', {
      studentId: selectedStudent.value.id,
      type: 1,
      items: [{
        courseId: enroll.courseId,
        classId: enroll.classId,
        itemType: enroll.itemType,
        hours: enroll.hours,
        unitPrice: enroll.unitPrice,
        giftHours: enroll.giftHours
      }]
    })
    // 2. 支付
    const payResp = await request.post(`/orders/${order.orderId}/pay`, null, {
      params: { payMethod: payMethod.value, offlineRemark: offlineRemark.value }
    })
    // 微信/支付宝: 显示支付二维码
    if (payResp?.payment?.qrCodeUrl) {
      payQrCode.value = payResp.payment.qrCodeUrl
    }
    payResult.value = { success: true, message: `订单号: ${order.orderNo}，报名成功！` }
    ElMessage.success(payMethod.value === 3 ? '报名缴费完成！' : '支付参数已生成')
  } catch(e) {
    payResult.value = { success: false, message: e?.message || '支付失败，请重试' }
  } finally { paying.value = false }
}

function resetEnroll() {
  step.value = 0; payResult.value = null; payQrCode.value = ''
  selectedStudent.value = null; selectedStudentId.value = null
  Object.assign(enroll, { courseId: null, classId: null, itemType: 1, hours: 20, unitPrice: 100, giftHours: 0, discountCode: '' })
}
</script>

<style scoped>
.selected-info { padding: 12px 16px; background: #f6ffed; border-radius: 8px; margin-top: 12px; font-size: 14px; }
.price-summary { font-size: 16px; padding: 8px 0; }
.price-label { color: #999; margin-right: 8px; }
.price-total { color: #ff4d4f; font-size: 22px; font-weight: 700; }
</style>
