<template>
  <view class="messages-page">
    <u-navbar title="消息通知" :border-bottom="false" bg-color="#fff" :auto-back="showBackButton" @leftClick="handleBack">
      <template #right>
        <view class="nav-right" @click="handleMarkAllRead" v-if="unreadCount > 0">
          <u-icon name="checkmark" size="18" color="#409eff"></u-icon>
          <text class="mark-all-text">全部已读</text>
        </view>
      </template>
    </u-navbar>

    <view v-if="loading && list.length === 0">
      <u-skeleton :rows="5" title animated></u-skeleton>
    </view>

    <u-empty v-else-if="error" mode="car" text="加载失败" @click="loadData(true)">
      <template #bottom>
        <u-button type="primary" size="mini" text="点击重试" @click="loadData(true)"></u-button>
      </template>
    </u-empty>

    <u-empty v-else-if="!list.length" mode="message" text="暂无消息"></u-empty>

    <view v-else class="list-wrapper">
      <u-list @scrolltolower="loadMore" :show-scrollbar="false">
        <u-list-item v-for="item in list" :key="item.id || item.notificationId" @click="handleItemClick(item)">
          <view class="message-card" :class="{ 'is-unread': isItemUnread(item) }">
            <view class="message-header">
              <view class="message-title-wrapper">
                <u-badge :is-dot="isItemUnread(item)" :offset="[8, 8]"></u-badge>
                <text class="message-type" :style="{ color: getTypeMeta(item.type || item.eventType).color }">
                  {{ getTypeMeta(item.type || item.eventType).label }}
                </text>
                <text class="message-title">{{ item.title || '无标题' }}</text>
              </view>
              <text class="message-time">{{ formatMessageTime(item.createTime || item.time) }}</text>
            </view>
            <text class="message-content">{{ item.content || '' }}</text>
            <view class="message-footer">
              <text v-if="isItemUnread(item)" class="unread-tag">未读</text>
              <text v-else class="read-tag">已读</text>
            </view>
          </view>
        </u-list-item>
        <u-loadmore :status="loadStatus" :bg-color="'transparent'" margin-top="20" margin-bottom="20"></u-loadmore>
      </u-list>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app';
import {
  getNotifications,
  markNotificationRead,
  markAllNotificationsRead,
  getUnreadCount,
} from '@/api/notification';
import { fromNow, normalizePaginationParams, parsePaginationResponse, goBack } from '@/utils';

const list = ref([]);
const loading = ref(false);
const error = ref(false);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);
const unreadCount = ref(0);

const hasMore = computed(() => list.value.length < total.value);
const loadStatus = computed(() => {
  if (loading.value) return 'loading';
  if (!hasMore.value && list.value.length > 0) return 'nomore';
  return 'loadmore';
});

// 是否显示返回按钮（页面栈>1时显示）
const showBackButton = computed(() => {
  const pages = getCurrentPages();
  return pages.length > 1;
});

function handleBack() {
  goBack();
}

const typeMetaMap = {
  SIGN_IN: { label: '签到', color: '#67c23a' },
  EVALUATION: { label: '评价', color: '#e6a23c' },
  HOMEWORK: { label: '作业', color: '#409eff' },
  EXAM: { label: '成绩', color: '#9b59b6' },
  LEAVE: { label: '请假', color: '#f56c6c' },
  REDEMPTION: { label: '兑换', color: '#ff9500' },
  SYSTEM: { label: '系统', color: '#909399' },
};

function getTypeMeta(type) {
  return typeMetaMap[type] || { label: '通知', color: '#909399' };
}

function isItemUnread(item) {
  return item.isRead === false || item.read === false || !item.readTime;
}

function formatMessageTime(date) {
  return fromNow(date);
}

async function loadData(isRefresh = false) {
  if (loading.value) return;

  if (isRefresh) {
    page.value = 1;
    error.value = false;
  }

  loading.value = true;

  try {
    const params = normalizePaginationParams({
      page: page.value,
      pageSize: pageSize.value,
    });
    const res = await getNotifications(params);

    if (res.code === 200) {
      const { list: data, total: totalCount } = parsePaginationResponse(res);
      total.value = totalCount || 0;

      if (isRefresh) {
        list.value = data;
      } else {
        list.value.push(...data);
      }
      if (data.length > 0) page.value++;
    } else {
      if (isRefresh) error.value = true;
    }
  } catch (e) {
    if (isRefresh) error.value = true;
    uni.showToast({ title: '加载失败', icon: 'none' });
  } finally {
    loading.value = false;
  }
}

async function loadUnreadCount() {
  try {
    const res = await getUnreadCount();
    if (res.code === 200) {
      unreadCount.value = res.data?.count ?? res.data ?? 0;
    }
  } catch (e) {
  }
}

async function handleItemClick(item) {
  if (isItemUnread(item)) {
    try {
      await markNotificationRead(item.id || item.notificationId);
      const index = list.value.findIndex(n => (n.id || n.notificationId) === (item.id || item.notificationId));
      if (index !== -1) {
        list.value[index].isRead = true;
        list.value[index].read = true;
      }
      if (unreadCount.value > 0) {
        unreadCount.value--;
      }
    } catch (e) {
    }
  }

  uni.showModal({
    title: item.title || '消息详情',
    content: item.content || '暂无内容',
    showCancel: false,
    confirmText: '确定',
  });
}

async function handleMarkAllRead() {
  uni.showLoading({ title: '处理中...' });
  try {
    const res = await markAllNotificationsRead();
    if (res.code === 200) {
      list.value.forEach(item => {
        item.isRead = true;
        item.read = true;
      });
      unreadCount.value = 0;
      uni.showToast({ title: '已全部标记为已读', icon: 'success' });
    }
  } catch (e) {
    uni.showToast({ title: '操作失败', icon: 'none' });
  } finally {
    uni.hideLoading();
  }
}

function loadMore() {
  if (hasMore.value && !loading.value) {
    loadData(false);
  }
}

onShow(() => {
  loadData(true);
  loadUnreadCount();
});

onPullDownRefresh(async () => {
  await Promise.all([loadData(true), loadUnreadCount()]);
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  loadMore();
});
</script>

<style scoped lang="scss">
.messages-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.nav-right {
  display: flex;
  align-items: center;
  padding: 8rpx 16rpx;
}

.mark-all-text {
  font-size: 26rpx;
  color: #409eff;
  margin-left: 4rpx;
}

.list-wrapper {
  padding: 0 24rpx 24rpx;
}

.message-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  position: relative;
  transition: all 0.3s;

  &.is-unread {
    background: #f0f7ff;
  }
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12rpx;
}

.message-title-wrapper {
  display: flex;
  align-items: center;
  flex: 1;
  margin-right: 16rpx;
}

.message-type {
  font-size: 24rpx;
  font-weight: 500;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  background: rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
  margin-right: 12rpx;
}

.message-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  font-size: 22rpx;
  color: #c0c4cc;
  flex-shrink: 0;
  margin-top: 6rpx;
}

.message-content {
  font-size: 28rpx;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.message-footer {
  margin-top: 16rpx;
  display: flex;
  justify-content: flex-end;
}

.unread-tag {
  font-size: 22rpx;
  color: #f56c6c;
  padding: 4rpx 12rpx;
  background: rgba(245, 108, 108, 0.1);
  border-radius: 8rpx;
}

.read-tag {
  font-size: 22rpx;
  color: #909399;
  padding: 4rpx 12rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
}
</style>
