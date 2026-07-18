package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduParentViewLog;

public interface IEduParentViewLogService extends IService<EduParentViewLog> {

    void recordView(Long parentUserId, Long studentId, String bizType, Long bizId);

    int countViews(String bizType, Long bizId);
}
