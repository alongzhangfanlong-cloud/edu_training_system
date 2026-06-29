import axios from 'axios'
import { showToast } from 'vant'

const request = axios.create({ baseURL: '/api/v1', timeout: 15000 })

// 防止401重复跳转
let isRedirecting = false

request.interceptors.request.use(c => {
  const t = localStorage.getItem('token')
  if (t) c.headers.Authorization = `Bearer ${t}`
  return c
})
request.interceptors.response.use(r => {
  if (r.data.code === 200) return r.data.data
  if (r.data.code === 40101 || r.data.code === 40102) {
    localStorage.clear()
    if (!isRedirecting) {
      isRedirecting = true
      import('../router').then(({ default: router }) => {
        router.push('/login').finally(() => { isRedirecting = false })
      })
    }
    return Promise.reject(r.data)
  }
  showToast(r.data.message || '请求失败')
  return Promise.reject(r.data)
}, e => {
  if (e.response && e.response.status === 401) {
    localStorage.clear()
    if (!isRedirecting) {
      isRedirecting = false
      import('../router').then(({ default: router }) => {
        router.push('/login').finally(() => { isRedirecting = false })
      })
    }
  } else {
    showToast('网络异常')
  }
  return Promise.reject(e)
})
export default request
