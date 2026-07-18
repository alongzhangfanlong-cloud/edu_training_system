package com.education.store.module.points.service;

/**
 * 积分模块内部服务接口 — 供其他模块（N05 教评学等）调用
 *
 * @see com.education.store.module.points.service.impl.PointsInternalServiceImpl
 */
public interface PointsInternalService {

    /**
     * 学生获取积分（教师课后发放 / 管理员手动发放等）
     *
     * @param studentId  学生ID
     * @param teacherId  教师ID（操作人）
     * @param sourceType 来源类型：TEACHER / ADMIN
     * @param sourceId   来源业务记录ID（如评价明细ID）
     * @param amount     积分数（正数）
     * @param remark     备注说明
     */
    void earnPoints(Long studentId, Long teacherId, String sourceType,
                    Long sourceId, int amount, String remark);
}
