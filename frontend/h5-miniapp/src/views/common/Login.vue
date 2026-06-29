<template>
  <div class="login-page">
    <div class="login-header">
      <h1>📚 教务管理系统</h1>
      <p>教育培训 · 多校区管理</p>
    </div>
    <van-form @submit="doLogin" class="login-form">
      <van-field v-model="phone" label="手机号" placeholder="请输入手机号" maxlength="11" type="tel" />
      <van-field v-model="code" center clearable label="验证码" placeholder="输入验证码" maxlength="6">
        <template #button><van-button size="small" type="primary" :disabled="countdown>0" @click="sendSms">{{ countdown>0 ? countdown+'s' : '获取验证码' }}</van-button></template>
      </van-field>
      <van-field v-model="role" is-link readonly label="角色" placeholder="选择角色" @click="showRolePicker = true" />
      <div style="margin:20px 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">登 录</van-button>
      </div>
    </van-form>
    <p class="dev-hint">开发环境验证码: 123456</p>

    <van-popup v-model:show="showRolePicker" position="bottom" round>
      <van-picker :columns="roleOptions" @confirm="onRoleConfirm" @cancel="showRolePicker = false" />
    </van-popup>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import axios from 'axios'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const phone = ref('13800000000')
const code = ref('')
const role = ref('boss')
const loading = ref(false)
const countdown = ref(0)
const showRolePicker = ref(false)
const roleOptions = [
  { text: '老板端', value: 'boss' }, { text: '校长端', value: 'principal' },
  { text: '教师端', value: 'teacher' }, { text: '家长端', value: 'parent' }, { text: '学员端', value: 'student' }
]

async function sendSms() {
  if (phone.value.length !== 11) return showToast('请输入正确手机号')
  await axios.post('/api/v1/auth/send-sms', { phone: phone.value })
  showToast('验证码已发送')
  countdown.value = 60
  const t = setInterval(() => { countdown.value--; if (countdown.value <= 0) clearInterval(t) }, 1000)
}

async function doLogin() {
  loading.value = true
  try {
    const res = await axios.post('/api/v1/auth/login', { phone: phone.value, code: code.value, role: role.value })
    if (res.data.code === 200) {
      const d = res.data.data
      userStore.setToken(d.token)
      userStore.setUser(d.user)
      const homeMap = { boss: '/boss/dashboard', principal: '/principal/dashboard', teacher: '/teacher/dashboard', parent: '/parent/dashboard', student: '/student/dashboard' }
      router.push(homeMap[d.user.role] || '/home')
    } else { showToast(res.data.message || '登录失败') }
  } catch (e) { showToast('登录失败') }
  finally { loading.value = false }
}

function onRoleConfirm({ selectedOptions }) {
  role.value = selectedOptions[0].value
  showRolePicker.value = false
}
</script>

<style scoped>
.login-page { min-height: 100vh; background: linear-gradient(135deg, #667eea, #764ba2); padding: 40px 20px; }
.login-header { text-align: center; color: #fff; margin-bottom: 30px; }
.login-header h1 { font-size: 28px; margin: 0; }
.login-header p { font-size: 14px; opacity: 0.8; margin-top: 8px; }
.login-form { background: #fff; border-radius: 12px; overflow: hidden; margin: 0 10px; }
.dev-hint { text-align: center; color: rgba(255,255,255,0.6); font-size: 12px; margin-top: 20px; }
</style>
