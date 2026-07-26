import dayjs from 'dayjs';

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
