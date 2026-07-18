package com.education.store.module.payroll.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.payroll.entity.EduTeacherPayroll;
import com.education.store.module.payroll.mapper.EduTeacherPayrollMapper;
import com.education.store.module.payroll.service.IEduTeacherPayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        // 服务端强制重算 totalPay，防止客户端篡改
        payroll.setTotalPay(calcTotalPay(payroll));
        payroll.setCreateBy(SecurityUtils.getUserIdAsString());
        return payrollMapper.insert(payroll);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updatePayroll(EduTeacherPayroll payroll) {
        EduTeacherPayroll exist = payrollMapper.selectById(payroll.getId());
        if (exist == null) throw new BusinessException("课酬记录不存在");
        if (!"DRAFT".equals(exist.getStatus())) throw new BusinessException("仅草稿状态可编辑");
        // 服务端强制重算 totalPay，并覆盖不允许客户端修改的字段
        payroll.setTotalPay(calcTotalPay(payroll));
        payroll.setStatus("DRAFT");
        payroll.setConfirmedAt(null);
        payroll.setConfirmedBy(null);
        payroll.setUpdateBy(SecurityUtils.getUserIdAsString());
        return payrollMapper.updateById(payroll);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deletePayrollByIds(Long[] ids) {
        // 禁止删除非 DRAFT 状态的记录
        for (Long id : ids) {
            EduTeacherPayroll p = payrollMapper.selectById(id);
            if (p != null && !"DRAFT".equals(p.getStatus())) {
                throw new BusinessException("仅草稿状态可删除，记录 " + id + " 状态为 " + p.getStatus());
            }
        }
        return payrollMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int confirmPayroll(Long id) {
        EduTeacherPayroll payroll = payrollMapper.selectById(id);
        if (payroll == null) throw new BusinessException("课酬记录不存在");
        if (!"DRAFT".equals(payroll.getStatus())) throw new BusinessException("仅草稿状态可确认");
        payroll.setStatus("CONFIRMED");
        payroll.setConfirmedAt(LocalDateTime.now());
        payroll.setConfirmedBy(SecurityUtils.getUserId());
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

    /** 服务端计算 totalPay = lessonCount × payPerLesson */
    private BigDecimal calcTotalPay(EduTeacherPayroll payroll) {
        if (payroll.getLessonCount() == null || payroll.getPayPerLesson() == null) return BigDecimal.ZERO;
        return payroll.getPayPerLesson().multiply(BigDecimal.valueOf(payroll.getLessonCount()));
    }
}
