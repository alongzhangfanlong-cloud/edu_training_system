<script setup>
import { onLaunch } from '@dcloudio/uni-app';
import { useAuthStore } from '@/stores/auth';
import { useChildrenStore } from '@/stores/children';

onLaunch(async () => {
  const authStore = useAuthStore();
  if (authStore.checkAuth()) {
    if (authStore.role === 'TEACHER' || authStore.role === 'teacher') {
      // 教师端：默认进入课表 Tab，数据在各页面 onShow 时按需加载
    } else {
      // 家长端：加载孩子列表
      const childrenStore = useChildrenStore();
      await childrenStore.fetchChildren();
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
