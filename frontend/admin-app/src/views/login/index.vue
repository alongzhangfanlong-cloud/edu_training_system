<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">📚 教务管理系统</h2>
      <p class="login-sub">管理后台登录</p>

      <el-form :model="form" label-width="0" size="large">
        <el-form-item>
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item>
          <div class="sms-row">
            <el-input v-model="form.code" placeholder="验证码" maxlength="6" style="flex:1" />
            <el-button :disabled="countdown > 0" @click="sendSms" style="margin-left:12px">
              {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" @click="doLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <p class="dev-hint">开发环境验证码: 123456</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ phone: '', code: '' })
const loading = ref(false)
const countdown = ref(0)

async function sendSms() {
  if (!form.phone || form.phone.length !== 11) {
    return ElMessage.warning('请输入正确的手机号')
  }
  try {
    await axios.post('/api/v1/auth/send-sms', { phone: form.phone })
    ElMessage.success('验证码已发送')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e) {
    ElMessage.error('发送失败')
  }
}

async function doLogin() {
  if (!form.phone || !form.code) {
    return ElMessage.warning('请输入手机号和验证码')
  }
  loading.value = true
  try {
    const res = await axios.post('/api/v1/auth/login', {
      phone: form.phone,
      code: form.code,
      role: 'boss'
    })
    if (res.data.code === 200) {
      const data = res.data.data
      userStore.setToken(data.token)
      userStore.setUser(data.user)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  background: #fff;
  border-radius: 12px;
  padding: 48px 40px;
  width: 420px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}
.login-title {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}
.login-sub {
  text-align: center;
  color: #999;
  margin-bottom: 32px;
  font-size: 14px;
}
.sms-row {
  display: flex;
  width: 100%;
}
.dev-hint {
  text-align: center;
  color: #bbb;
  font-size: 12px;
  margin-top: 16px;
}
</style>
