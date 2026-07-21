import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getUserInfo, getStatistics, getTodaySchedule, getTodos } from '@/api/teacher';

export const useTeacherStore = defineStore('teacher', () => {
  const userInfo = ref(null);
  const statistics = ref(null);
  const todaySchedule = ref([]);
  const todos = ref(null);
  const loading = ref(false);

  const teacherName = computed(() => userInfo.value?.userName || '老师');
  const todayCourseCount = computed(() => statistics.value?.todayCourseCount || 0);
  const uncheckedCount = computed(() => statistics.value?.todayUncheckedClassCount || 0);

  async function fetchDashboard() {
    loading.value = true;
    try {
      const [infoRes, statsRes, schedRes, todosRes] = await Promise.all([
        getUserInfo(),
        getStatistics(),
        getTodaySchedule(),
        getTodos(),
      ]);
      if (infoRes.code === 200) userInfo.value = infoRes.data;
      if (statsRes.code === 200) statistics.value = statsRes.data;
      if (schedRes.code === 200) todaySchedule.value = schedRes.data || [];
      if (todosRes.code === 200) todos.value = todosRes.data;
    } catch { /* ignore */ }
    finally { loading.value = false; }
  }

  return {
    userInfo, statistics, todaySchedule, todos, loading,
    teacherName, todayCourseCount, uncheckedCount,
    fetchDashboard,
  };
});
