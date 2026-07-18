<!--
 * ---------------------------------------------
 * 页面标签
 * ---------------------------------------------
-->

<template>
  <div class="tag-container">
    <div class="tag-container-inner">
      <ScrollWrapper>
        <div class="tags-list" ref="tagsListRef">
          <div
            v-for="tag in allTags"
            :key="tag.path"
            class="tag-item"
            :class="{ active: tag.path === currentPath }"
            @click="handleClickTag(tag)"
            @contextmenu.prevent="handleRightClick(tag, $event)"
            @mousedown.middle="handleMiddleClick(tag)"
          >
            <span>{{ tag.meta.title }}</span>
            <em @click.stop="handleCloseTag(tag)" v-if="!onlyOneTag">
              <i class="ifont-close" />
            </em>
          </div>
        </div>
      </ScrollWrapper>
    </div>

    <div
      class="tag-context-menu"
      ref="contextRef"
      v-show="contextMenuVisible"
      tabindex="-1"
      :style="contextMenuPositionStyle"
      @blur="handleCloseContextMenu"
    >
      <div class="menu-item" v-if="cMenuEnanble.fresh" @click="freshTag">
        <i class="ifont-refresh-right" />
        <span>刷新页面</span>
      </div>
      <div class="menu-item" v-if="cMenuEnanble.close" @click="closeTag">
        <i class="ifont-close" />
        <span>关闭当前</span>
      </div>
      <div class="menu-item" v-if="cMenuEnanble.closeOther" @click="closeOtherTags">
        <i class="ifont-close" />
        <span>关闭其他</span>
      </div>
      <div class="menu-item" v-if="cMenuEnanble.closeLeft" @click="closeLeftTags">
        <i class="ifont-left" />
        <span>关闭左侧</span>
      </div>
      <div class="menu-item" v-if="cMenuEnanble.closeRight" @click="closeRightTags">
        <i class="ifont-right" />
        <span>关闭右侧</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import UseViewTagsStore from "@/stores/viewTags.js";

import ScrollWrapper from "./ScrollWrapper.vue";

const route = useRoute();
const router = useRouter();
const viewTagsStore = UseViewTagsStore();

const allTags = computed(() => viewTagsStore.allTags);
const currentPath = computed(() => route.path);

const onlyOneTag = computed(() => allTags.value.length === 1);

const tagsListRef = ref(null);

// 路由变动，处理tags信息
watch(
  () => route.path,
  () => {
    viewTagsStore.addTag(route);

    // 等待DOM更新完毕后，将当前菜单tag滚动到可视区域
    setTimeout(() => {
      const element = tagsListRef.value?.querySelector(".tag-item.active");

      if (element) {
        element.scrollIntoView({
          behavior: "smooth"
        });
      }
    });
  },
  {
    immediate: true
  }
);

// 点击标签
const handleClickTag = tag => {
  router.push(tag.path);
};

// 关闭标签
const handleCloseTag = tag => {
  viewTagsStore.closeTag(tag);
};

const handleMiddleClick = tag => {
  handleCloseTag(tag);
};

// ## 右键菜单
// #region -------------------------------------------------------
const contextRef = ref(null);
const contextMenuVisible = ref(false);
const contextMenuPositionStyle = ref({});

// 当前右键点击的标签
const currentContextMenuTag = ref(null);

// 右键菜单宽度，用来处理右键菜单展出位置
const contextMenuWidth = 85;

// 右键菜单可用项
const cMenuEnanble = computed(() => {
  const result = {
    fresh: true,
    close: false,
    closeOther: false,
    closeLeft: false,
    closeRight: false
  };

  const allTags = viewTagsStore.allTags;
  const currentPath = currentContextMenuTag.value?.path;

  if (allTags.length > 1) {
    result.close = true;
    result.closeOther = true;
  }

  const index = allTags.findIndex(tag => tag.path === currentPath);

  if (index !== 0) {
    result.closeLeft = true;
  }

  if (index !== allTags.length - 1) {
    result.closeRight = true;
  }

  return result;
});

// 展示标签菜单
const handleRightClick = (tag, e) => {
  contextMenuVisible.value = true;
  currentContextMenuTag.value = tag;

  nextTick(() => {
    contextRef.value && contextRef.value.focus();
  });

  // 距离视口左边距
  const clientX = event.clientX;
  const clientY = event.clientY;
  // 距离视口右边距
  const distanceToRight = window.innerWidth - clientX;

  let left = clientX + 3;
  let top = clientY + 3;

  if (distanceToRight < contextMenuWidth) {
    left = clientX - contextMenuWidth - 3;
  }

  contextMenuPositionStyle.value = {
    left: left + "px",
    top: top + "px"
  };
};

// 关闭标签菜单
const handleCloseContextMenu = () => {
  contextMenuVisible.value = false;
};

// 关闭当前标签
const closeTag = () => {
  viewTagsStore.closeTag(currentContextMenuTag.value);
  handleCloseContextMenu();
};

// 关闭当前标签左侧所有标签
const closeLeftTags = () => {
  viewTagsStore.closeLeftTags(currentContextMenuTag.value);
  handleCloseContextMenu();
};
// 关闭当前标签左侧所有标签
const closeRightTags = () => {
  viewTagsStore.closeRightTags(currentContextMenuTag.value);
  handleCloseContextMenu();
};

// 关闭其他标签
const closeOtherTags = () => {
  viewTagsStore.closeOtherTags(currentContextMenuTag.value);
  handleCloseContextMenu();
};

// 刷新标签页面
const freshTag = () => {
  viewTagsStore.refreshTargetTag(currentContextMenuTag.value);
  handleCloseContextMenu();
};
// #endregion  --------------------------
</script>

<style lang="less" scoped>
@import url("./index.less");
</style>
