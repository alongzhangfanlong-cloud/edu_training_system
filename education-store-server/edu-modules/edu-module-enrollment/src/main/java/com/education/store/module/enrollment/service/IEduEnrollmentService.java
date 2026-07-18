package com.education.store.module.enrollment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.enrollment.dto.EnrollmentUpdateDTO;
import com.education.store.module.enrollment.entity.EduEnrollment;

public interface IEduEnrollmentService extends IService<EduEnrollment> {
    IPage<EduEnrollment> selectEnrollmentList(IPage<EduEnrollment> page, EduEnrollment enrollment);
    EduEnrollment selectEnrollmentById(Long id);
    int insertEnrollment(EduEnrollment enrollment);
    int updateEnrollment(EnrollmentUpdateDTO dto);
    int deleteEnrollmentByIds(Long[] ids);
    int updatePaymentStatus(Long id, String newStatus);
}
