/**
 * ---------------------------------------------
 * 页面标签相关store
 * ---------------------------------------------
 */

import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";

export default defineStore("viewTags", () => {
  const route = useRoute();
  const router = useRouter();

  // 需要刷新的页面
  const freshTag = ref(null);

  // 所有的tag，给标签栏使用
  const allTags = ref([]);

  // 保留缓存的tag name，给keepalive使用
  const cacheTagNames = computed(() => {
    return allTags.value.filter(item => !item?.meta?.noCache && freshTag.value?.path !== item.path).map(i => i.name);
  });

  // 添加tag
  const addTag = route => {
    const allreadyInList = allTags.value.some(item => item.path === route.path);

    if (!allreadyInList && !route.meta.noTags) {
      allTags.value.push({
        path: route.path,
        name: route.name,
        meta: route.meta,
        query: route.query
      });
    }
  };

  // 删除指定tag
  const closeTag = tag => {
    // 至少保留一个tag
    if (allTags.value.length <= 1) {
      return;
    }

    allTags.value = allTags.value.filter(item => item.path !== tag.path);

    // 校验是否关闭了当前页面页签
    checkCurrentTag();
  };

  // 删除左侧tag
  const closeLeftTags = tag => {
    const _index = getCurrentTagIndex(tag);

    if (_index > -1) {
      allTags.value = allTags.value.filter((item, index) => index >= _index);
    }

    // 校验是否关闭了当前页面页签
    checkCurrentTag();
  };

  // 删除右侧tag
  const closeRightTags = tag => {
    const _index = getCurrentTagIndex(tag);

    if (_index > -1) {
      allTags.value = allTags.value.filter((item, index) => index <= _index);
    }

    // 校验是否关闭了当前页面页签
    checkCurrentTag();
  };

  // 删除其他tag
  const closeOtherTags = tag => {
    const _index = getCurrentTagIndex(tag);
    if (_index > -1) {
      allTags.value = allTags.value.filter((item, index) => index !== _index);
    }

    // 校验是否关闭了当前页面页签
    checkCurrentTag();
  };

  // 刷新目标页面
  const refreshTargetTag = async tag => {
    freshTag.value = tag;
    await router.replace({
      path: "/freshing",
      query: tag.query
    });

    // 再次等待路由就绪
    await router.isReady();

    freshTag.value = null;

    router.replace({
      path: tag.path,
      query: tag.query
    });
  };

  // 获取当前tag索引
  const getCurrentTagIndex = tag => {
    return allTags.value.findIndex(item => item.path === tag.path);
  };

  // 跳转到最后一个tag
  const goLastTag = () => {
    const lastTag = allTags.value[allTags.value.length - 1];

    if (lastTag) {
      router.push({
        path: lastTag.path,
        query: lastTag.query
      });
    }
  };

  // 删除一些tag后，需要判断是否删除了当前页面tag，如果删除了，需要跳转到最后一个tag
  const checkCurrentTag = () => {
    const isExist = allTags.value.some(tag => tag.path === route.path);

    if (!isExist) {
      goLastTag();
    }
  };

  return {
    allTags,
    cacheTagNames,
    addTag,
    closeTag,
    closeRightTags,
    closeLeftTags,
    closeOtherTags,
    refreshTargetTag
  };
});
