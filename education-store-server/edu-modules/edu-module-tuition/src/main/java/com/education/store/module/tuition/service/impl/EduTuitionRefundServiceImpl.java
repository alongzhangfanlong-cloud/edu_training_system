package com.education.store.module.tuition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.student.entity.EduStudentHoursLog;
import com.education.store.module.student.mapper.EduStudentHoursLogMapper;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.tuition.entity.EduTuitionRefund;
import com.education.store.module.tuition.mapper.EduTuitionRefundMapper;
import com.education.store.module.tuition.service.IEduTuitionRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EduTuitionRefundServiceImpl extends ServiceImpl<EduTuitionRefundMapper, EduTuitionRefund>
        implements IEduTuitionRefundService {

    private final EduTuitionRefundMapper refundMapper;
    private final EduStudentHoursLogMapper hoursLogMapper;
    private final EduStudentMapper studentMapper;

    @Override
    public IPage<EduTuitionRefund> selectRefundList(IPage<EduTuitionRefund> page, EduTuitionRefund refund) {
        LambdaQueryWrapper<EduTuitionRefund> wrapper = new LambdaQueryWrapper<>();
        if (refund.getStudentId() != null) wrapper.eq(EduTuitionRefund::getStudentId, refund.getStudentId());
        if (StrUtil.isNotBlank(refund.getStatus())) wrapper.eq(EduTuitionRefund::getStatus, refund.getStatus());
        wrapper.orderByDesc(EduTuitionRefund::getCreateTime);
        return refundMapper.selectPage(page, wrapper);
    }

    @Override public EduTuitionRefund selectRefundById(Long id) { return refundMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertRefund(EduTuitionRefund refund) {
        refund.setRefundNo(generateRefundNo());
        refund.setStatus("PENDING");
        int rows = refundMapper.insert(refund);

        // 扣回课时
        if (refund.getHoursDeducted() != null && refund.getHoursDeducted() > 0) {
            BigDecimal deductAmount = new BigDecimal(-refund.getHoursDeducted());
            EduStudentHoursLog log = new EduStudentHoursLog();
            log.setStudentId(refund.getStudentId());
            log.setCourseId(refund.getCourseId());
            log.setChangeType("REFUND_DEDUCT");
            log.setChangeAmount(deductAmount);
            log.setSourceType("REFUND");
            log.setSourceId(refund.getId());
            log.setRemark(refund.getRefundNo());
            BigDecimal current = hoursLogMapper.sumBalanceByStudent(refund.getStudentId());
            if (current == null) current = BigDecimal.ZERO;
            log.setBalanceAfter(current.add(deductAmount));
            hoursLogMapper.insert(log);
            updateStudentHoursCache(refund.getStudentId());
        }
        return rows;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updateRefund(EduTuitionRefund refund) {
        EduTuitionRefund exist = refundMapper.selectById(refund.getId());
        if (exist == null) throw new BusinessException("退费记录不存在");
        return refundMapper.updateById(refund);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteRefundByIds(Long[] ids) { return refundMapper.deleteBatchIds(Arrays.asList(ids)); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int approveRefund(Long id, Long approverId) {
        EduTuitionRefund refund = refundMapper.selectById(id);
        if (refund == null) throw new BusinessException("退费记录不存在");
        refund.setStatus("COMPLETED");
        refund.setApproverId(approverId);
        refund.setApprovalTime(LocalDateTime.now());
        return refundMapper.updateById(refund);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int rejectRefund(Long id, Long approverId, String reason) {
        EduTuitionRefund refund = refundMapper.selectById(id);
        if (refund == null) throw new BusinessException("退费记录不存在");
        refund.setStatus("REJECTED");
        refund.setApproverId(approverId);
        refund.setRejectReason(reason);
        refund.setApprovalTime(LocalDateTime.now());
        return refundMapper.updateById(refund);
    }

    private String generateRefundNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "R" + dateStr;
        LambdaQueryWrapper<EduTuitionRefund> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(EduTuitionRefund::getRefundNo, prefix);
        long count = refundMapper.selectCount(wrapper);
        return prefix + String.format("%04d", count + 1);
    }

    private void updateStudentHoursCache(Long studentId) {
        var total = hoursLogMapper.sumTotalGainedByStudent(studentId);
        var remaining = hoursLogMapper.sumBalanceByStudent(studentId);
        var student = studentMapper.selectById(studentId);
        if (student != null) {
            student.setTotalHours(total != null ? total : BigDecimal.ZERO);
            student.setRemainingHours(remaining != null ? remaining : BigDecimal.ZERO);
            studentMapper.updateById(student);
        }
    }
}
