package com.education.store.module.points.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.points.entity.EduPointsAccount;

/**
 * 积分账户 Service
 */
public interface IEduPointsAccountService extends IService<EduPointsAccount> {

    /**
     * 按学生ID查询积分账户
     */
    EduPointsAccount getByStudentId(Long studentId);

    /**
     * 初始化学生积分账户（balance=0），已存在则跳过
     */
    EduPointsAccount initAccount(Long studentId);
}
