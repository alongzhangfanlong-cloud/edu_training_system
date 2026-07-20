/**
 * 积分商城 API — 对齐后端 PointsGiftController / PointsRedemptionController / PointsAccountController
 */
import request from '@/utils/request';

const GIFT = '/api/v1/points/gift';
const REDEMPTION = '/api/v1/points/redemption';
const ACCOUNT = '/api/v1/points/account';

// ==================== 积分礼品 ====================

/** 礼品列表（分页） */
export function listGift(query) {
  return request({ url: `${GIFT}/list`, method: 'get', params: query });
}

/** 礼品详情 */
export function getGift(id) {
  return request({ url: `${GIFT}/${id}`, method: 'get' });
}

/** 新增礼品 */
export function addGift(data) {
  return request({ url: GIFT, method: 'post', data });
}

/** 编辑礼品 */
export function updateGift(id, data) {
  return request({ url: `${GIFT}/${id}`, method: 'put', data });
}

/** 启用/禁用礼品 */
export function updateGiftStatus(id, status) {
  return request({ url: `${GIFT}/${id}/status`, method: 'put', data: { status } });
}

/** 删除礼品 */
export function deleteGift(id) {
  return request({ url: `${GIFT}/${id}`, method: 'delete' });
}

// ==================== 兑换记录 ====================

/** 兑换记录列表（分页） */
export function listRedemption(query) {
  return request({ url: `${REDEMPTION}/list`, method: 'get', params: query });
}

/** 兑换记录详情 */
export function getRedemption(id) {
  return request({ url: `${REDEMPTION}/${id}`, method: 'get' });
}

/** 兑换礼品（扣积分 + 减库存） */
export function redeem(studentId, giftId, operatorId) {
  return request({ url: REDEMPTION, method: 'post', params: { studentId, giftId, operatorId } });
}

/** 标记已领取（核销） */
export function claimRedemption(id, operatorId) {
  return request({ url: `${REDEMPTION}/${id}/claim`, method: 'post', params: { operatorId } });
}

/** 取消兑换（退积分 + 恢复库存） */
export function cancelRedemption(id, operatorId) {
  return request({ url: `${REDEMPTION}/${id}/cancel`, method: 'post', params: { operatorId } });
}

// ==================== 积分账户 ====================

/** 查询学生积分余额 */
export function getAccount(studentId) {
  return request({ url: `${ACCOUNT}/${studentId}`, method: 'get' });
}

/** 查询学生积分日志（分页） */
export function listPointsLog(studentId, query) {
  return request({ url: `${ACCOUNT}/log/${studentId}`, method: 'get', params: query });
}

/** 管理员手动调整积分 */
export function adjustPoints(studentId, amount, remark) {
  return request({ url: `${ACCOUNT}/admin/adjust`, method: 'post', params: { studentId, amount, remark } });
}
