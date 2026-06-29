import { get, post, put } from '../utils/request'

export const batchCheckIn = (data) => post('/attendances/batch-check', data)
export const getAttendanceBySchedule = (scheduleId) => get(`/attendances/schedule/${scheduleId}`)
export const checkIn = (data) => post('/attendances/check-in', data)
export const applyLeave = (data) => post('/attendances/leave', data)
export const approveLeave = (id, data) => put(`/attendances/leave/${id}/approve`, data)
