import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getChildren } from '@/api/parent';

function normalizeChild(child) {
  return {
    ...child,
    studentId: child.studentId || child.id || child.student_id,
    name: child.name || child.studentName || child.student_name,
    avatar: child.avatar || child.avatarUrl || child.avatar_url,
    grade: child.grade || child.gradeName || child.className,
    school: child.school || child.schoolName,
    remainingHours: child.remainingHours ?? child.remainHours ?? child.remaining_hours ?? 0,
    totalHours: child.totalHours ?? child.total ?? child.total_hours ?? 0,
  };
}

export const useChildrenStore = defineStore('children', () => {
  const children = ref([]);
  const currentChildId = ref(uni.getStorageSync('currentChildId') || null);
  const loading = ref(false);

  const currentChild = computed(() =>
    children.value.find((c) => c.studentId === currentChildId.value) || children.value[0] || null
  );

  async function fetchChildren() {
    loading.value = true;
    try {
      const res = await getChildren();
      if (res.code === 200) {
        const rawList = Array.isArray(res.data) ? res.data : (res.data?.records || res.data?.list || res.data || []);
        children.value = rawList.map(normalizeChild);
        const storedId = uni.getStorageSync('currentChildId');
        if (storedId && children.value.find(c => c.studentId === storedId)) {
          currentChildId.value = storedId;
        } else if (!currentChildId.value && children.value.length > 0) {
          currentChildId.value = children.value[0].studentId;
        }
        if (currentChildId.value) {
          uni.setStorageSync('currentChildId', currentChildId.value);
        }
      }
    } catch {
    } finally {
      loading.value = false;
    }
  }

  function setCurrentChild(childId) {
    currentChildId.value = childId;
    uni.setStorageSync('currentChildId', childId);
  }

  return { children, currentChildId, currentChild, loading, fetchChildren, setCurrentChild };
});
