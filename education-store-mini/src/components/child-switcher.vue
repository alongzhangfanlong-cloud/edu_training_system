<template>
  <view v-if="children.length > 1" class="child-switcher">
    <picker mode="selector" :range="childNames" :value="currentIndex" @change="onChange">
      <view class="switcher-btn">
        <text class="switcher-label">{{ currentLabel }}</text>
        <text class="switcher-arrow">▼</text>
      </view>
    </picker>
  </view>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  children: { type: Array, default: () => [] },
  currentId: { type: [Number, String], default: null },
});

const emit = defineEmits(['change']);

const childNames = computed(() => props.children.map((c) => c.name || '未命名'));
const currentIndex = computed(() => {
  const idx = props.children.findIndex((c) => c.studentId === props.currentId);
  return idx >= 0 ? idx : 0;
});
const currentLabel = computed(() => {
  const c = props.children.find((c) => c.studentId === props.currentId);
  return c ? `👶 ${c.name}` : '选择孩子';
});

function onChange(e) {
  const child = props.children[e.detail.value];
  if (child) emit('change', child.studentId);
}
</script>

<style scoped lang="scss">
.child-switcher {
  display: flex;
  justify-content: center;
  padding: 16rpx 0;
}
.switcher-btn {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 12rpx 32rpx;
  background: #f5f7fa;
  border-radius: 40rpx;
}
.switcher-label { font-size: 28rpx; color: #303133; font-weight: 500; }
.switcher-arrow { font-size: 20rpx; color: #909399; }
</style>
