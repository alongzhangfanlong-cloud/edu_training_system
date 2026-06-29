// API请求封装 - 学员/家长端
const BASE_URL = 'http://localhost:8080/api/v1'

let token = ''
let refreshToken = ''

// 从本地存储读取token
try {
  token = uni.getStorageSync('token') || ''
  refreshToken = uni.getStorageSync('refreshToken') || ''
} catch (e) {}

const request = (options) => {
  return new Promise((resolve, reject) => {
    const header = {}
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }
    header['Content-Type'] = 'application/json'

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success: (res) => {
        if (res.statusCode === 200) {
          const body = res.data
          if (body.code === 200) {
            resolve(body.data)
          } else if (body.code === 40101 || body.code === 40102) {
            // Token过期，尝试刷新
            refreshAccessToken().then(() => {
              request(options).then(resolve).catch(reject)
            }).catch(() => {
              uni.reLaunch({ url: '/pages/index/index' })
              reject(body)
            })
          } else {
            uni.showToast({ title: body.message || '请求失败', icon: 'none' })
            reject(body)
          }
        } else {
          uni.showToast({ title: '网络异常', icon: 'none' })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

const refreshAccessToken = () => {
  return new Promise((resolve, reject) => {
    if (!refreshToken) { reject(new Error('no refresh token')); return }
    uni.request({
      url: BASE_URL + '/auth/refresh',
      method: 'POST',
      data: { refreshToken },
      success: (res) => {
        if (res.data.code === 200) {
          token = res.data.data.token
          refreshToken = res.data.data.refreshToken
          uni.setStorageSync('token', token)
          uni.setStorageSync('refreshToken', refreshToken)
          resolve()
        } else { reject(res.data) }
      },
      fail: reject
    })
  })
}

export const setToken = (t, rt) => {
  token = t
  refreshToken = rt
  uni.setStorageSync('token', t)
  uni.setStorageSync('refreshToken', rt)
}

export const clearToken = () => {
  token = ''
  refreshToken = ''
  uni.removeStorageSync('token')
  uni.removeStorageSync('refreshToken')
}

export const get = (url, data) => request({ url, method: 'GET', data })
export const post = (url, data) => request({ url, method: 'POST', data })
export const put = (url, data) => request({ url, method: 'PUT', data })
export const del = (url, data) => request({ url, method: 'DELETE', data })

export default request
