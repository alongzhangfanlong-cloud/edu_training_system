package com.education.store.module.points.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.points.entity.EduPointsLog;

/**
 * 积分日志 Service（只追加）
 */
public interface IEduPointsLogService extends IService<EduPointsLog> {

    /**
     * 分页查询积分日志
     */
    IPage<EduPointsLog> selectLogList(IPage<EduPointsLog> page, Long studentId, String changeType);

    /**
     * 写入积分日志
     */
    void writeLog(Long studentId, String changeType, Integer amount, Integer balanceAfter,
                  String sourceType, Long sourceId, String remark);

    /**
     * 检查重复（同一来源是否已有日志记录）
     */
    boolean existsBySource(String sourceType, Long sourceId);
}
