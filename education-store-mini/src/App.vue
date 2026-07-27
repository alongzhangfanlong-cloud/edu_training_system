<script setup>
import { onLaunch, onShow } from '@dcloudio/uni-app';
import { useAuthStore } from '@/stores/auth';
import { useChildrenStore } from '@/stores/children';

let appInitialized = false;

const TEACHER_PAGES_PREFIX = '/pages/teacher/';
const PARENT_ONLY_PAGES = ['/pages/index/index', '/pages/messages/index', '/pages/profile/index',
  '/pages/schedule/index', '/pages/homework/index', '/pages/attendance/index',
  '/pages/exam/index', '/pages/evaluation/index', '/pages/points/index',
  '/pages/leave/index', '/pages/enrollment/index'];

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

  const isTeacher = authStore.isTeacher;

  if (isTeacher) {
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    if (currentPage) {
      const currentPath = '/' + currentPage.route;
      if (PARENT_ONLY_PAGES.some(p => currentPath === p) || (!currentPath.startsWith(TEACHER_PAGES_PREFIX) && currentPath !== '/pages/login/index')) {
        uni.reLaunch({ url: '/pages/teacher/schedule/index' });
        return;
      }
    }
    return;
  }

  const childrenStore = useChildrenStore();
  if (childrenStore.children.length === 0) {
    await childrenStore.fetchChildren();
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
