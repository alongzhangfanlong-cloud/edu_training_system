package com.education.store.module.enrollment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.enrollment.entity.EduEnrollment;
import com.education.store.module.enrollment.mapper.EduEnrollmentMapper;
import com.education.store.module.enrollment.service.IEduEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EduEnrollmentServiceImpl extends ServiceImpl<EduEnrollmentMapper, EduEnrollment>
        implements IEduEnrollmentService {

    private final EduEnrollmentMapper enrollmentMapper;

    @Override
    public IPage<EduEnrollment> selectEnrollmentList(IPage<EduEnrollment> page, EduEnrollment enrollment) {
        LambdaQueryWrapper<EduEnrollment> wrapper = new LambdaQueryWrapper<>();
        if (enrollment.getStudentId() != null) wrapper.eq(EduEnrollment::getStudentId, enrollment.getStudentId());
        if (enrollment.getCourseId() != null) wrapper.eq(EduEnrollment::getCourseId, enrollment.getCourseId());
        if (StrUtil.isNotBlank(enrollment.getPaymentStatus())) wrapper.eq(EduEnrollment::getPaymentStatus, enrollment.getPaymentStatus());
        wrapper.orderByDesc(EduEnrollment::getCreateTime);
        return enrollmentMapper.selectPage(page, wrapper);
    }

    @Override public EduEnrollment selectEnrollmentById(Long id) { return enrollmentMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertEnrollment(EduEnrollment enrollment) {
        enrollment.setPaymentStatus("PENDING");
        return enrollmentMapper.insert(enrollment);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updateEnrollment(EduEnrollment enrollment) {
        EduEnrollment exist = enrollmentMapper.selectById(enrollment.getId());
        if (exist == null) throw new BusinessException("报名记录不存在");
        if ("PAID".equals(exist.getPaymentStatus()) || "REFUNDED".equals(exist.getPaymentStatus()))
            throw new BusinessException("已收费/已退费的报名记录不可编辑");
        return enrollmentMapper.updateById(enrollment);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteEnrollmentByIds(Long[] ids) {
        return enrollmentMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updatePaymentStatus(Long id, String newStatus) {
        EduEnrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) throw new BusinessException("报名记录不存在");
        enrollment.setPaymentStatus(newStatus);
        return enrollmentMapper.updateById(enrollment);
    }
}
