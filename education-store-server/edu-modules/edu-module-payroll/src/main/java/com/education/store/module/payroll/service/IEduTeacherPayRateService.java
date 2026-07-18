package com.education.store.module.payroll.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.payroll.entity.EduTeacherPayRate;

public interface IEduTeacherPayRateService extends IService<EduTeacherPayRate> {
    IPage<EduTeacherPayRate> selectPayRateList(IPage<EduTeacherPayRate> page, EduTeacherPayRate rate);
    EduTeacherPayRate selectPayRateById(Long id);
    int insertPayRate(EduTeacherPayRate rate);
    int updatePayRate(EduTeacherPayRate rate);
    int deletePayRateByIds(Long[] ids);
    boolean checkPeriodOverlap(EduTeacherPayRate rate);
}
