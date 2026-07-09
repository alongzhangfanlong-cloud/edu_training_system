import { post, setToken, clearToken } from '../utils/request'

// 发送验证码
export const sendSms = (phone) => post('/auth/send-sms', { phone })

// 登录
export const login = async (phone, code, role) => {
  const data = await post('/auth/login', { phone, code, role })
  if (data && data.token) {
    setToken(data.token, data.refreshToken)
  }
  return data
}

// 登出
export const logout = async () => {
  try { await post('/auth/logout') } catch (e) {}
  clearToken()
}
