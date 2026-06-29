import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/common/Login.vue') },
  { path: '/', redirect: '/home' },
  { path: '/home', name: 'Home', component: () => import('../views/common/Home.vue'), meta: { requiresAuth: true } },
  // boss pages
  { path: '/boss/dashboard', name: 'BossDashboard', component: () => import('../views/boss/Dashboard.vue'), meta: { requiresAuth: true, role: 'boss' } },
  { path: '/boss/campus', name: 'BossCampus', component: () => import('../views/boss/CampusManage.vue'), meta: { requiresAuth: true, role: 'boss' } },
  { path: '/boss/users', name: 'BossUsers', component: () => import('../views/boss/UserManage.vue'), meta: { requiresAuth: true, role: 'boss' } },
  { path: '/boss/finance', name: 'BossFinance', component: () => import('../views/boss/Finance.vue'), meta: { requiresAuth: true, role: 'boss' } },
  { path: '/boss/jiaowu', name: 'BossJiaowu', component: () => import('../views/boss/Jiaowu.vue'), meta: { requiresAuth: true, role: 'boss' } },
  { path: '/boss/system', name: 'BossSystem', component: () => import('../views/boss/System.vue'), meta: { requiresAuth: true, role: 'boss' } },
  // principal pages
  { path: '/principal/dashboard', name: 'PrincipalDashboard', component: () => import('../views/principal/Dashboard.vue'), meta: { requiresAuth: true, role: 'principal' } },
  { path: '/principal/teachers', name: 'PrincipalTeachers', component: () => import('../views/principal/Teachers.vue'), meta: { requiresAuth: true, role: 'principal' } },
  { path: '/principal/students', name: 'PrincipalStudents', component: () => import('../views/principal/Students.vue'), meta: { requiresAuth: true, role: 'principal' } },
  // teacher pages
  { path: '/teacher/dashboard', name: 'TeacherDashboard', component: () => import('../views/teacher/Dashboard.vue'), meta: { requiresAuth: true, role: 'teacher' } },
  { path: '/teacher/students', name: 'TeacherStudents', component: () => import('../views/teacher/StudentList.vue'), meta: { requiresAuth: true, role: 'teacher' } },
  { path: '/teacher/courses', name: 'TeacherCourses', component: () => import('../views/teacher/Courses.vue'), meta: { requiresAuth: true, role: 'teacher' } },
  { path: '/teacher/checkin', name: 'TeacherCheckin', component: () => import('../views/teacher/Checkin.vue'), meta: { requiresAuth: true, role: 'teacher' } },
  { path: '/teacher/homework', name: 'TeacherHomework', component: () => import('../views/teacher/Homework.vue'), meta: { requiresAuth: true, role: 'teacher' } },
  { path: '/teacher/growth', name: 'TeacherGrowth', component: () => import('../views/teacher/Growth.vue'), meta: { requiresAuth: true, role: 'teacher' } },
  // parent pages
  { path: '/parent/dashboard', name: 'ParentDashboard', component: () => import('../views/parent/Dashboard.vue'), meta: { requiresAuth: true, role: 'parent' } },
  { path: '/parent/hours', name: 'ParentHours', component: () => import('../views/parent/Hours.vue'), meta: { requiresAuth: true, role: 'parent' } },
  { path: '/parent/schedule', name: 'ParentSchedule', component: () => import('../views/parent/Schedule.vue'), meta: { requiresAuth: true, role: 'parent' } },
  { path: '/parent/leave', name: 'ParentLeave', component: () => import('../views/parent/Leave.vue'), meta: { requiresAuth: true, role: 'parent' } },
  { path: '/parent/growth', name: 'ParentGrowth', component: () => import('../views/parent/Growth.vue'), meta: { requiresAuth: true, role: 'parent' } },
  { path: '/parent/points', name: 'ParentPoints', component: () => import('../views/parent/Points.vue'), meta: { requiresAuth: true, role: 'parent' } },
  // student pages
  { path: '/student/dashboard', name: 'StudentDashboard', component: () => import('../views/student/Dashboard.vue'), meta: { requiresAuth: true, role: 'student' } },
  { path: '/student/schedule', name: 'StudentSchedule', component: () => import('../views/student/Schedule.vue'), meta: { requiresAuth: true, role: 'student' } },
  { path: '/student/checkin', name: 'StudentCheckin', component: () => import('../views/student/Checkin.vue'), meta: { requiresAuth: true, role: 'student' } },
  { path: '/student/hours', name: 'StudentHours', component: () => import('../views/student/Hours.vue'), meta: { requiresAuth: true, role: 'student' } }
]

const router = createRouter({ history: createWebHistory(), routes })
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = JSON.parse(localStorage.getItem('userInfo') || '{}').role
  if (to.meta.requiresAuth && !token) return next('/login')
  if (to.path === '/login' && token) return next('/home')
  if (to.meta.role && to.meta.role !== role) {
    // redirect to role home
    const homeMap = { boss: '/boss/dashboard', principal: '/principal/dashboard', teacher: '/teacher/dashboard', parent: '/parent/dashboard', student: '/student/dashboard' }
    return next(homeMap[role] || '/home')
  }
  next()
})
export default router
