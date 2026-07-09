import { get, post } from '../utils/request'

export const submitFeedback = (data) => post('/feedbacks', data)
export const getFeedbackBySchedule = (scheduleId) => get(`/feedbacks/schedule/${scheduleId}`)
export const getStudentFeedback = (scheduleId, studentId) => get(`/feedbacks/schedule/${scheduleId}/student/${studentId}`)
