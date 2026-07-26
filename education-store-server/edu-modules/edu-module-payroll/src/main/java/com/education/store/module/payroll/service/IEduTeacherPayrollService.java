package com.education.store.module.payroll.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.payroll.entity.EduTeacherPayroll;

import java.util.List;

public interface IEduTeacherPayrollService extends IService<EduTeacherPayroll> {
    IPage<EduTeacherPayroll> selectPayrollList(IPage<EduTeacherPayroll> page, EduTeacherPayroll payroll);
    EduTeacherPayroll selectPayrollById(Long id);
    int insertPayroll(EduTeacherPayroll payroll);
    int updatePayroll(EduTeacherPayroll payroll);
    int deletePayrollByIds(Long[] ids);
    int confirmPayroll(Long id);
    int cancelConfirmPayroll(Long id);
    int markPaid(Long id);

    /**
     * 自动统计指定月份所有教师的课酬（草稿状态）
     * @param month 月份 YYYY-MM
     * @return 生成的课酬记录数
     */
    int autoGeneratePayroll(String month);

    /**
     * 自动统计指定教师指定月份的课酬
     * @param teacherId 教师ID
     * @param month 月份 YYYY-MM
     * @return 生成的课酬记录数
     */
    int autoGenerateTeacherPayroll(Long teacherId, String month);
}
