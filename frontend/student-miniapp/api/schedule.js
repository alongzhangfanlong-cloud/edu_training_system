import { get, post, put, del } from '../utils/request'

export const getSchedules = (params) => get('/schedules', params)
export const getScheduleDetail = (id) => get(`/schedules/${id}`)
export const createSchedule = (data) => post('/schedules', data)
export const updateSchedule = (id, data) => put(`/schedules/${id}`, data)
export const deleteSchedule = (id) => del(`/schedules/${id}`)
export const batchSchedule = (data) => post('/schedules/batch', data)
export const checkConflict = (data) => post('/schedules/conflict-check', data)
