import dayjs from 'dayjs';

// 所有Tab页面列表（包括custom-tab-bar中显示的页面）
export const TAB_PAGES = [
  '/pages/index/index',
  '/pages/messages/index',
  '/pages/profile/index',
  '/pages/teacher/schedule/index',
  '/pages/teacher/checkin/index',
  '/pages/teacher/students/index',
  '/pages/teacher/teaching/index',
  '/pages/teacher/profile/index',
  '/pages/admin/students/index',
];

/**
 * 安全的页面跳转
 * - Tab页面之间切换使用reLaunch（清空页面栈）
 * - 非Tab页面使用navigateTo（保留页面栈，显示返回按钮）
 */
export function safeNavigate(url) {
  if (!url) return;
  // 处理带参数的URL
  const path = url.split('?')[0];
  if (TAB_PAGES.includes(path)) {
    uni.reLaunch({ url });
  } else {
    uni.navigateTo({ url });
  }
}

/**
 * 切换Tab页面（底部导航点击）
 */
export function switchTabPage(url) {
  if (!url) return;
  uni.reLaunch({ url });
}

/**
 * 统一的返回逻辑
 * - 如果页面栈>1，返回上一页
 * - 否则返回对应角色的首页
 */
export function goBack(delta = 1) {
  const pages = getCurrentPages();
  if (pages.length > delta) {
    uni.navigateBack({ delta });
  } else {
    // 页面栈不足，返回对应角色的首页
    const role = uni.getStorageSync('role') || '';
    let homeUrl = '/pages/index/index';
    if (role === 'TEACHER') {
      homeUrl = '/pages/teacher/schedule/index';
    }
    uni.reLaunch({ url: homeUrl });
  }
}

/** Format date as YYYY-MM-DD */
export function formatDate(date) {
  if (!date) return '';
  return dayjs(date).format('YYYY-MM-DD');
}

/** Format datetime as YYYY-MM-DD HH:mm */
export function formatDateTime(date) {
  if (!date) return '';
  return dayjs(date).format('YYYY-MM-DD HH:mm');
}

/** Relative time (today/yesterday/N days ago) */
export function fromNow(date) {
  if (!date) return '';
  const d = dayjs(date);
  const now = dayjs();
  const diff = now.diff(d, 'day');
  if (diff === 0) return '今天 ' + d.format('HH:mm');
  if (diff === 1) return '昨天 ' + d.format('HH:mm');
  if (diff < 7) return diff + '天前';
  return d.format('MM-DD HH:mm');
}

/** Event type → { label, color } */
export function eventMeta(type) {
  const map = {
    SIGN_IN: { label: '签到', color: '#67c23a' },
    EVALUATION: { label: '评价', color: '#e6a23c' },
    HOMEWORK: { label: '作业', color: '#409eff' },
    EXAM: { label: '成绩', color: '#9b59b6' },
    LEAVE: { label: '请假', color: '#f56c6c' },
  };
  return map[type] || { label: type || '--', color: '#909399' };
}

/** Status maps for display */
export const STATUS_MAP = {
  leave: { 0: '待审批', 1: '已通过', 2: '已拒绝' },
  attendance: { PRESENT: '出勤', LATE: '迟到', ABSENT: '缺勤', LEAVE: '请假' },
};

/**
 * 统一处理分页参数，兼容多种命名格式
 * 支持: page/current, pageSize/size
 */
export function normalizePaginationParams(params = {}) {
  const { page, current, pageNum, pageSize, size, ...rest } = params;
  return {
    page: page || current || pageNum || 1,
    pageSize: pageSize || size || 10,
    ...rest,
  };
}

/**
 * 统一解析分页响应数据，兼容多种格式
 * 支持响应格式: { records, total } / { list, total } / { data, total } / 直接数组
 */
export function parsePaginationResponse(res) {
  if (!res) {
    return { list: [], total: 0, hasMore: false };
  }
  const code = res.code;
  if (code !== 200 && code !== 0 && code !== '200' && code !== '0') {
    return { list: [], total: 0, hasMore: false };
  }
  const data = res.data;
  let list = [];
  let total = 0;

  if (Array.isArray(data)) {
    list = data;
    total = data.length;
  } else if (data) {
    list = data.records || data.list || data.data || data.rows || [];
    total = data.total ?? data.totalCount ?? data.count ?? data.totalElements ?? list.length;
  }

  return { list, total: Number(total) || list.length, hasMore: list.length > 0 };
}
