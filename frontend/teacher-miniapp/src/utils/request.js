// 复用学员端的请求封装
const BASE_URL = 'http://localhost:8080/api/v1'
let token = ''
try { token = uni.getStorageSync('token') || '' } catch(e) {}

const request = (options) => {
  return new Promise((resolve, reject) => {
    const header = {}
    if (token) header['Authorization'] = `Bearer ${token}`
    header['Content-Type'] = 'application/json'
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) resolve(res.data.data)
        else if (res.data.code === 40101 || res.data.code === 40102) {
          uni.reLaunch({ url: '/pages/index/index' })
          reject(res.data)
        } else {
          uni.showToast({ title: res.data.message || '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => { uni.showToast({ title: '网络异常', icon: 'none' }); reject(err) }
    })
  })
}
export const setToken = (t) => { token = t; uni.setStorageSync('token', t) }
export const get = (url, data) => request({ url, method: 'GET', data })
export const post = (url, data) => request({ url, method: 'POST', data })
export const put = (url, data) => request({ url, method: 'PUT', data })
export default request
