import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getChildren } from '@/api/parent';

export const useChildrenStore = defineStore('children', () => {
  const children = ref([]);
  const currentChildId = ref(null);
  const loading = ref(false);

  const currentChild = computed(() =>
    children.value.find((c) => c.studentId === currentChildId.value) || children.value[0] || null
  );

  async function fetchChildren() {
    loading.value = true;
    try {
      const res = await getChildren();
      if (res.code === 200) {
        children.value = res.data || [];
        if (!currentChildId.value && children.value.length > 0) {
          currentChildId.value = children.value[0].studentId;
        }
      }
    } catch {
      // ignore — handled by request interceptor
    } finally {
      loading.value = false;
    }
  }

  function setCurrentChild(childId) {
    currentChildId.value = childId;
  }

  return { children, currentChildId, currentChild, loading, fetchChildren, setCurrentChild };
});
