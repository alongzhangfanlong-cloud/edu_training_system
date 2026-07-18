<template>
  <div class="icon-picker">
    <!-- 触发器：显示已选图标或占位文字 -->
    <div ref="triggerRef" class="icon-picker__trigger" @click="togglePanel">
      <span v-if="modelValue" class="icon-picker__selected">
        <i :class="modelValue"></i>
        <span class="icon-picker__name">{{ modelValue }}</span>
      </span>
      <span v-else class="icon-picker__placeholder">点击选择图标</span>
      <el-icon class="icon-picker__arrow" :class="{ 'is-open': panelVisible }">
        <ArrowDown />
      </el-icon>
    </div>

    <!-- Teleport 到 body，摆脱外层弹窗的 overflow/z-index 限制 -->
    <Teleport to="body">
      <div
        v-show="panelVisible"
        ref="panelRef"
        class="icon-picker__panel"
        :style="panelStyle"
      >
        <div class="icon-picker__search">
          <el-input
            v-model="searchText"
            placeholder="搜索图标..."
            size="small"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="icon-picker__grid">
          <div
            v-for="icon in filteredIcons"
            :key="icon"
            class="icon-picker__item"
            :class="{ 'is-active': modelValue === icon }"
            :title="icon"
            @click="selectIcon(icon)"
          >
            <i :class="icon"></i>
            <span class="icon-picker__item-name">{{ icon.replace('ifont-', '') }}</span>
          </div>
          <div v-if="filteredIcons.length === 0" class="icon-picker__empty">
            无匹配图标
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { ArrowDown, Search } from '@element-plus/icons-vue';
import allIcons from '@/assets/svgfont/icons';

const props = defineProps({
  modelValue: { type: String, default: '' }
});

const emit = defineEmits(['update:modelValue']);

const triggerRef = ref(null);
const panelRef = ref(null);
const panelVisible = ref(false);
const searchText = ref('');
const panelStyle = ref({});

const filteredIcons = computed(() => {
  if (!searchText.value) return allIcons;
  const keyword = searchText.value.toLowerCase();
  return allIcons.filter(icon => icon.toLowerCase().includes(keyword));
});

function updatePosition() {
  if (!triggerRef.value) return;
  const rect = triggerRef.value.getBoundingClientRect();
  const panelWidth = 520;
  const panelHeight = 380;

  // 水平：左侧对齐触发器
  let left = rect.left;
  // 超出右边界则向左收
  if (left + panelWidth > window.innerWidth - 8) {
    left = Math.max(8, window.innerWidth - panelWidth - 8);
  }

  // 垂直：默认显示在触发器下方
  let top = rect.bottom + 6;
  // 下方空间不够则显示在触发器上方
  if (top + panelHeight > window.innerHeight - 8) {
    top = rect.top - panelHeight - 6;
    if (top < 8) top = 8;
  }

  panelStyle.value = {
    position: 'fixed',
    left: `${left}px`,
    top: `${top}px`,
    zIndex: 9999
  };
}

function togglePanel() {
  panelVisible.value = !panelVisible.value;
  if (panelVisible.value) {
    searchText.value = '';
    nextTick(updatePosition);
  }
}

function selectIcon(icon) {
  emit('update:modelValue', icon);
  panelVisible.value = false;
  searchText.value = '';
}

// 点击外部关闭面板
function onDocumentClick(e) {
  if (!panelVisible.value) return;
  const el = e.target;
  // 点击触发器或当前实例的面板内部不关闭
  if (triggerRef.value && triggerRef.value.contains(el)) return;
  if (panelRef.value && panelRef.value.contains(el)) return;
  panelVisible.value = false;
}

function onResizeOrScroll() {
  if (panelVisible.value) {
    updatePosition();
  }
}

onMounted(() => {
  document.addEventListener('click', onDocumentClick, true);
  window.addEventListener('resize', onResizeOrScroll);
  window.addEventListener('scroll', onResizeOrScroll, true);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocumentClick, true);
  window.removeEventListener('resize', onResizeOrScroll);
  window.removeEventListener('scroll', onResizeOrScroll, true);
});
</script>

<style scoped lang="less">
.icon-picker {
  width: 100%;

  &__trigger {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 32px;
    padding: 0 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    cursor: pointer;
    background: #fff;
    transition: border-color .2s;

    &:hover { border-color: #c0c4cc; }
  }

  &__selected {
    display: flex;
    align-items: center;
    gap: 6px;
    overflow: hidden;

    i {
      font-size: 16px;
      flex-shrink: 0;
    }
  }

  &__name {
    font-size: 13px;
    color: #606266;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__placeholder {
    font-size: 13px;
    color: #c0c4cc;
  }

  &__arrow {
    font-size: 12px;
    color: #c0c4cc;
    transition: transform .3s;

    &.is-open {
      transform: rotate(180deg);
    }
  }
}

// panel 用 Teleport 传送到 body，样式必须是非 scoped 的
</style>

<style lang="less">
// 全局样式：panel 被 Teleport 传到 body 上，不能写在 scoped 里
.icon-picker__panel {
  width: 520px;
  max-height: 380px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
}

.icon-picker__search {
  padding: 10px 12px;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
}

.icon-picker__grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 6px;
  padding: 10px 12px;
  overflow-y: auto;
  max-height: 310px;
}

.icon-picker__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 8px 4px;
  border: 1px solid transparent;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
    background: #ecf5ff;
  }

  &.is-active {
    border-color: #409eff;
    background: #ecf5ff;
    box-shadow: 0 0 0 1px #409eff;
  }

  i {
    font-size: 22px;
    color: #606266;
    height: 28px;
    line-height: 28px;
    display: flex;
    align-items: center;
  }
}

.icon-picker__item-name {
  font-size: 10px;
  color: #909399;
  margin-top: 2px;
  text-align: center;
  line-height: 1.2;
  height: 24px;               // 固定高度容纳最多2行(10px × 1.2 × 2 = 24px)
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  max-width: 100%;
}

.icon-picker__empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 30px 0;
  color: #c0c4cc;
  font-size: 13px;
}
</style>
