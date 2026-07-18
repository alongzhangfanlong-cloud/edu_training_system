package com.education.store.module.payroll.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.payroll.entity.EduTeacherPayroll;

public interface IEduTeacherPayrollService extends IService<EduTeacherPayroll> {
    IPage<EduTeacherPayroll> selectPayrollList(IPage<EduTeacherPayroll> page, EduTeacherPayroll payroll);
    EduTeacherPayroll selectPayrollById(Long id);
    int insertPayroll(EduTeacherPayroll payroll);
    int updatePayroll(EduTeacherPayroll payroll);
    int deletePayrollByIds(Long[] ids);
    int confirmPayroll(Long id);
    int cancelConfirmPayroll(Long id);
    int markPaid(Long id);
}
