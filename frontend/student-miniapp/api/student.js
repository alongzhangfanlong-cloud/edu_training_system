import { get, post, put, del } from '../utils/request'

export const getStudentList = (params) => get('/students', params)
export const getStudentDetail = (id) => get(`/students/${id}`)
export const createStudent = (data) => post('/students', data)
export const updateStudent = (id, data) => put(`/students/${id}`, data)
export const deleteStudent = (id) => del(`/students/${id}`)
export const getStudentHours = (id, params) => get(`/students/${id}/hours`, params)
