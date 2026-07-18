package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.teaching.entity.EduParentViewLog;
import com.education.store.module.teaching.mapper.EduParentViewLogMapper;
import com.education.store.module.teaching.service.IEduParentViewLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EduParentViewLogServiceImpl extends ServiceImpl<EduParentViewLogMapper, EduParentViewLog>
        implements IEduParentViewLogService {

    private final EduParentViewLogMapper viewLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordView(Long parentUserId, Long studentId, String bizType, Long bizId) {
        EduParentViewLog existing = viewLogMapper.selectByUnique(parentUserId, studentId, bizType, bizId);
        if (existing != null) {
            viewLogMapper.incrementView(existing.getId());
        } else {
            EduParentViewLog log = new EduParentViewLog();
            log.setParentUserId(parentUserId);
            log.setStudentId(studentId);
            log.setBizType(bizType);
            log.setBizId(bizId);
            log.setFirstViewTime(LocalDateTime.now());
            log.setLastViewTime(LocalDateTime.now());
            log.setViewCount(1);
            viewLogMapper.insert(log);
        }
    }

    @Override
    public int countViews(String bizType, Long bizId) {
        return viewLogMapper.countByBiz(bizType, bizId);
    }
}
