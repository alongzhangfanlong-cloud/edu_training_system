<script setup>
import { onLaunch, onShow } from '@dcloudio/uni-app';
import { useAuthStore } from '@/stores/auth';
import { useChildrenStore } from '@/stores/children';

let appInitialized = false;

// 公共页面（所有角色都可以访问）
const PUBLIC_PAGES = ['/pages/login/index'];

// 教师专属页面前缀 + 教师允许访问的公共页面
const TEACHER_PAGES_PREFIX = '/pages/teacher/';
const TEACHER_ALLOWED_PAGES = [
  '/pages/messages/index',
  '/pages/teacher/profile/index'
];

// 家长/管理员专属页面（教师不能访问）
const PARENT_ADMIN_ONLY_PAGES = [
  '/pages/index/index',
  '/pages/schedule/index',
  '/pages/homework/index',
  '/pages/attendance/index',
  '/pages/exam/index',
  '/pages/evaluation/index',
  '/pages/points/index',
  '/pages/leave/index',
  '/pages/enrollment/index',
  '/pages/profile/index',
  '/pages/profile/child-detail',
  '/pages/leave/submit'
];

// 管理员专属页面
const ADMIN_PAGES_PREFIX = '/pages/admin/';

function isTeacherPage(path) {
  return path.startsWith(TEACHER_PAGES_PREFIX) || TEACHER_ALLOWED_PAGES.includes(path) || PUBLIC_PAGES.includes(path);
}

function isParentAdminPage(path) {
  return !path.startsWith(TEACHER_PAGES_PREFIX) || PUBLIC_PAGES.includes(path);
}

function isAdminPage(path) {
  return path.startsWith(ADMIN_PAGES_PREFIX);
}

async function initAppData() {
  const authStore = useAuthStore();
  const isAuthed = authStore.checkAuth();

  if (!isAuthed) {
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    if (!currentPage || currentPage.route !== 'pages/login/index') {
      uni.reLaunch({ url: '/pages/login/index' });
    }
    return;
  }

  const role = authStore.role;
  const isTeacher = role === 'TEACHER' || role === 'teacher';
  const isAdmin = role === 'ADMIN' || role === 'admin';

  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  if (!currentPage) return;

  const currentPath = '/' + currentPage.route;

  // 公共页面直接放行
  if (PUBLIC_PAGES.includes(currentPath)) {
    return;
  }

  if (isTeacher) {
    // 教师：只能访问教师页面
    if (!isTeacherPage(currentPath)) {
      uni.reLaunch({ url: '/pages/teacher/schedule/index' });
      return;
    }
  } else if (isAdmin) {
    // 管理员：不能访问教师专属页面
    if (currentPath.startsWith(TEACHER_PAGES_PREFIX) && !TEACHER_ALLOWED_PAGES.includes(currentPath)) {
      uni.reLaunch({ url: '/pages/index/index' });
      return;
    }
  } else {
    // 家长：不能访问教师专属页面和管理员专属页面
    if (currentPath.startsWith(TEACHER_PAGES_PREFIX) || currentPath.startsWith(ADMIN_PAGES_PREFIX)) {
      uni.reLaunch({ url: '/pages/index/index' });
      return;
    }
  }

  // 加载家长孩子数据
  if (!isTeacher) {
    const childrenStore = useChildrenStore();
    if (childrenStore.children.length === 0) {
      await childrenStore.fetchChildren();
    }
  }
}

onLaunch(async () => {
  appInitialized = true;
  await initAppData();
});

onShow(async () => {
  if (!appInitialized) return;
  const authStore = useAuthStore();
  if (authStore.checkAuth()) {
    if (!authStore.isTeacher) {
      const childrenStore = useChildrenStore();
      if (childrenStore.children.length === 0) {
        await childrenStore.fetchChildren();
      }
    }
  }
});
</script>

<style lang="scss">
@import 'uview-plus/index.scss';
@import '@/uni.scss';

page {
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 28rpx;
  color: #303133;
}
</style>
