import { get, post } from '../utils/request'

export const createOrder = (data) => post('/orders', data)
export const payOrder = (id, payMethod, remark) => post(`/orders/${id}/pay`, { payMethod, offlineRemark: remark })
export const getOrderDetail = (id) => get(`/orders/${id}`)
export const getOrderList = (params) => get('/orders', params)
