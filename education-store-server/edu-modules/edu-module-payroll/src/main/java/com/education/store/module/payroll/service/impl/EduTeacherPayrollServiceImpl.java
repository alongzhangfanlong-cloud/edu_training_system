package com.education.store.module.payroll.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.payroll.entity.EduTeacherPayroll;
import com.education.store.module.payroll.mapper.EduTeacherPayrollMapper;
import com.education.store.module.payroll.service.IEduTeacherPayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EduTeacherPayrollServiceImpl extends ServiceImpl<EduTeacherPayrollMapper, EduTeacherPayroll>
        implements IEduTeacherPayrollService {

    private final EduTeacherPayrollMapper payrollMapper;

    @Override
    public IPage<EduTeacherPayroll> selectPayrollList(IPage<EduTeacherPayroll> page, EduTeacherPayroll payroll) {
        LambdaQueryWrapper<EduTeacherPayroll> wrapper = new LambdaQueryWrapper<>();
        if (payroll.getTeacherId() != null) wrapper.eq(EduTeacherPayroll::getTeacherId, payroll.getTeacherId());
        if (StrUtil.isNotBlank(payroll.getMonth())) wrapper.eq(EduTeacherPayroll::getMonth, payroll.getMonth());
        if (StrUtil.isNotBlank(payroll.getStatus())) wrapper.eq(EduTeacherPayroll::getStatus, payroll.getStatus());
        wrapper.orderByDesc(EduTeacherPayroll::getMonth);
        return payrollMapper.selectPage(page, wrapper);
    }

    @Override public EduTeacherPayroll selectPayrollById(Long id) { return payrollMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertPayroll(EduTeacherPayroll payroll) {
        payroll.setStatus("DRAFT");
        return payrollMapper.insert(payroll);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updatePayroll(EduTeacherPayroll payroll) {
        EduTeacherPayroll exist = payrollMapper.selectById(payroll.getId());
        if (exist == null) throw new BusinessException("课酬记录不存在");
        if (!"DRAFT".equals(exist.getStatus())) throw new BusinessException("仅草稿状态可编辑");
        return payrollMapper.updateById(payroll);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deletePayrollByIds(Long[] ids) {
        return payrollMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int confirmPayroll(Long id) {
        EduTeacherPayroll payroll = payrollMapper.selectById(id);
        if (payroll == null) throw new BusinessException("课酬记录不存在");
        if (!"DRAFT".equals(payroll.getStatus())) throw new BusinessException("仅草稿状态可确认");
        payroll.setStatus("CONFIRMED");
        payroll.setConfirmedAt(LocalDateTime.now());
        return payrollMapper.updateById(payroll);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int cancelConfirmPayroll(Long id) {
        EduTeacherPayroll payroll = payrollMapper.selectById(id);
        if (payroll == null) throw new BusinessException("课酬记录不存在");
        if (!"CONFIRMED".equals(payroll.getStatus())) throw new BusinessException("仅已确认状态可取消确认");
        payroll.setStatus("DRAFT");
        payroll.setConfirmedAt(null);
        payroll.setConfirmedBy(null);
        return payrollMapper.updateById(payroll);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int markPaid(Long id) {
        EduTeacherPayroll payroll = payrollMapper.selectById(id);
        if (payroll == null) throw new BusinessException("课酬记录不存在");
        if (!"CONFIRMED".equals(payroll.getStatus())) throw new BusinessException("仅已确认状态可发放");
        payroll.setStatus("PAID");
        return payrollMapper.updateById(payroll);
    }
}
