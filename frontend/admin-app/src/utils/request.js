import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 15000
})

// 防止401重复跳转
let isRedirecting = false

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  response => {
    const body = response.data
    if (body.code === 200) return body.data
    if (body.code === 40101 || body.code === 40102) {
      localStorage.clear()
      if (!isRedirecting) {
        isRedirecting = true
        // 使用 router 跳转而非 window.location.href 避免整页刷新死循环
        import('../router').then(({ default: router }) => {
          router.push('/login').finally(() => { isRedirecting = false })
        })
      }
      return Promise.reject(body)
    }
    ElMessage.error(body.message || '请求失败')
    return Promise.reject(body)
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.clear()
      if (!isRedirecting) {
        isRedirecting = true
        import('../router').then(({ default: router }) => {
          router.push('/login').finally(() => { isRedirecting = false })
        })
      }
    } else {
      ElMessage.error('网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
