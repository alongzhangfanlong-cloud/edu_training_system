import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login', name: 'Login', meta: { title: '登录' },
    component: () => import('../views/login/index.vue')
  },
  { path: '/', redirect: '/dashboard' },
  {
    path: '/dashboard', name: 'Dashboard', meta: { title: '工作台', requiresAuth: true },
    component: () => import('../views/dashboard/index.vue')
  },
  {
    path: '/student-manage', name: 'StudentManage', meta: { title: '学员管理', requiresAuth: true },
    component: () => import('../views/student-manage/index.vue')
  },
  {
    path: '/schedule-manage', name: 'ScheduleManage', meta: { title: '排课管理', requiresAuth: true },
    component: () => import('../views/schedule-manage/index.vue')
  },
  {
    path: '/course-manage', name: 'CourseManage', meta: { title: '课程管理', requiresAuth: true },
    component: () => import('../views/course-manage/index.vue')
  },
  {
    path: '/classroom-manage', name: 'ClassroomManage', meta: { title: '教室管理', requiresAuth: true },
    component: () => import('../views/classroom-manage/index.vue')
  },
  {
    path: '/teacher-manage', name: 'TeacherManage', meta: { title: '教师管理', requiresAuth: true },
    component: () => import('../views/teacher-manage/index.vue')
  },
  {
    path: '/enrollment', name: 'Enrollment', meta: { title: '报名缴费', requiresAuth: true },
    component: () => import('../views/enrollment/index.vue')
  },
  {
    path: '/finance', name: 'Finance', meta: { title: '财务管理', requiresAuth: true },
    component: () => import('../views/finance/index.vue')
  },
  {
    path: '/attendance-query', name: 'AttendanceQuery', meta: { title: '考勤查询', requiresAuth: true },
    component: () => import('../views/attendance-query/index.vue')
  },
  {
    path: '/user-manage', name: 'UserManage', meta: { title: '用户管理', requiresAuth: true },
    component: () => import('../views/user-manage/index.vue')
  },
  {
    path: '/system-config', name: 'SystemConfig', meta: { title: '系统配置', requiresAuth: true },
    component: () => import('../views/system-config/index.vue')
  },
  {
    path: '/menu-manage', name: 'MenuManage', meta: { title: '菜单管理', requiresAuth: true },
    component: () => import('../views/menu-manage/index.vue')
  },
  {
    path: '/permission-manage', name: 'PermissionManage', meta: { title: '权限管理', requiresAuth: true },
    component: () => import('../views/permission-manage/index.vue')
  },
  { path: '/campus-manage', name: 'CampusManage', meta: { title: '校区管理', requiresAuth: true }, component: () => import('../views/campus-manage/index.vue') },
  { path: '/feedback-manage', name: 'FeedbackManage', meta: { title: '课堂反馈', requiresAuth: true }, component: () => import('../views/feedback-manage/index.vue') },
  { path: '/growth-manage', name: 'GrowthManage', meta: { title: '成长档案', requiresAuth: true }, component: () => import('../views/growth-manage/index.vue') },
  { path: '/leave-manage', name: 'LeaveManage', meta: { title: '请假管理', requiresAuth: true }, component: () => import('../views/leave-manage/index.vue') },
  { path: '/homework-manage', name: 'HomeworkManage', meta: { title: '布置作业', requiresAuth: true }, component: () => import('../views/homework-manage/index.vue') },
  { path: '/points-manage', name: 'PointsManage', meta: { title: '积分管理', requiresAuth: true }, component: () => import('../views/points-manage/index.vue') },
  { path: '/birthday-manage', name: 'BirthdayManage', meta: { title: '生日祝福', requiresAuth: true }, component: () => import('../views/birthday-manage/index.vue') },
  { path: '/statistics', name: 'Statistics', meta: { title: '数据分析', requiresAuth: true }, component: () => import('../views/dashboard/index.vue') },
  { path: '/wechat-config', name: 'WechatConfig', meta: { title: '公众号设置', requiresAuth: true }, component: () => import('../views/wechat-config/index.vue') }
]

const router = createRouter({ history: createWebHistory(), routes })

// 路由守卫：未登录跳转到登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
