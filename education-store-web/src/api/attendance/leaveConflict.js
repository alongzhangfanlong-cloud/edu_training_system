/**
 * 请假冲突处理 API — 对齐后端 EduLeaveConflictController
 */
import request from '@/utils/request';

const BASE = '/api/v1/attendance/conflicts';

/** 按请假单 ID 查询冲突排课列表 */
export function listConflictsByLeave(leaveId) {
  return request({ url: `${BASE}/leave/${leaveId}`, method: 'get' });
}

/** 处理单条冲突（代课/调课/停课） */
export function resolveConflict(id, data) {
  return request({ url: `${BASE}/${id}/resolve`, method: 'put', data });
}
