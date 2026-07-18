package com.education.store.module.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.points.entity.EduPointsLog;
import com.education.store.module.points.mapper.EduPointsLogMapper;
import com.education.store.module.points.service.IEduPointsLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 积分日志 Service 实现（只追加，不修改不删除）
 */
@Service
@RequiredArgsConstructor
public class EduPointsLogServiceImpl extends ServiceImpl<EduPointsLogMapper, EduPointsLog>
        implements IEduPointsLogService {

    private final EduPointsLogMapper logMapper;

    @Override
    public IPage<EduPointsLog> selectLogList(IPage<EduPointsLog> page, Long studentId, String changeType) {
        LambdaQueryWrapper<EduPointsLog> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(EduPointsLog::getStudentId, studentId);
        }
        if (changeType != null && !changeType.isEmpty()) {
            wrapper.eq(EduPointsLog::getChangeType, changeType);
        }
        wrapper.orderByDesc(EduPointsLog::getCreateTime);
        return logMapper.selectPage(page, wrapper);
    }

    @Override
    public void writeLog(Long studentId, String changeType, Integer amount, Integer balanceAfter,
                         String sourceType, Long sourceId, String remark) {
        EduPointsLog log = new EduPointsLog();
        log.setStudentId(studentId);
        log.setChangeType(changeType);
        log.setAmount(amount);
        log.setBalanceAfter(balanceAfter);
        log.setSourceType(sourceType);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);
    }

    @Override
    public boolean existsBySource(String sourceType, Long sourceId) {
        return logMapper.countBySource(sourceType, sourceId) > 0;
    }
}
