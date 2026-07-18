package com.education.store.module.tuition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.enrollment.entity.EduEnrollment;
import com.education.store.module.enrollment.mapper.EduEnrollmentMapper;
import com.education.store.module.student.entity.EduStudentHoursLog;
import com.education.store.module.student.mapper.EduStudentHoursLogMapper;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.tuition.entity.EduTuitionFee;
import com.education.store.module.tuition.mapper.EduTuitionFeeMapper;
import com.education.store.module.tuition.service.IEduTuitionFeeService;
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
public class EduTuitionFeeServiceImpl extends ServiceImpl<EduTuitionFeeMapper, EduTuitionFee>
        implements IEduTuitionFeeService {

    private final EduTuitionFeeMapper feeMapper;
    private final EduEnrollmentMapper enrollmentMapper;
    private final EduStudentHoursLogMapper hoursLogMapper;
    private final EduStudentMapper studentMapper;

    @Override
    public IPage<EduTuitionFee> selectFeeList(IPage<EduTuitionFee> page, EduTuitionFee fee) {
        LambdaQueryWrapper<EduTuitionFee> wrapper = new LambdaQueryWrapper<>();
        if (fee.getStudentId() != null) wrapper.eq(EduTuitionFee::getStudentId, fee.getStudentId());
        if (fee.getEnrollmentId() != null) wrapper.eq(EduTuitionFee::getEnrollmentId, fee.getEnrollmentId());
        if (StrUtil.isNotBlank(fee.getStatus())) wrapper.eq(EduTuitionFee::getStatus, fee.getStatus());
        wrapper.orderByDesc(EduTuitionFee::getCreateTime);
        return feeMapper.selectPage(page, wrapper);
    }

    @Override public EduTuitionFee selectFeeById(Long id) { return feeMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertFee(EduTuitionFee fee) {
        fee.setFeeNo(generateFeeNo("T"));
        fee.setStatus("CONFIRMED");
        fee.setPayTime(LocalDateTime.now());
        int rows = feeMapper.insert(fee);

        // 回写报名记录收费状态
        if (fee.getEnrollmentId() != null) {
            EduEnrollment enrollment = enrollmentMapper.selectById(fee.getEnrollmentId());
            if (enrollment != null) {
                boolean fullPaid = fee.getAmountPaid().compareTo(enrollment.getAmount()) >= 0;
                enrollment.setPaymentStatus(fullPaid ? "PAID" : "PARTIAL");
                if (enrollment.getTuitionRecordId() == null) enrollment.setTuitionRecordId(fee.getId());
                enrollmentMapper.updateById(enrollment);
            }
        }

        // 课时费 → 课时入账
        if ("TUITION".equals(fee.getFeeType()) && fee.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            writeHoursLog(fee.getStudentId(), fee.getCourseId(), fee.getAmountPaid(),
                    "ENROLL_GAIN", "TUITION", fee.getId(), fee.getFeeNo());
        }
        return rows;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updateFee(EduTuitionFee fee) {
        EduTuitionFee exist = feeMapper.selectById(fee.getId());
        if (exist == null) throw new BusinessException("收费记录不存在");
        return feeMapper.updateById(fee);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteFeeByIds(Long[] ids) {
        return feeMapper.deleteBatchIds(Arrays.asList(ids));
    }

    private String generateFeeNo(String prefix) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String todayPrefix = prefix + dateStr;
        LambdaQueryWrapper<EduTuitionFee> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(EduTuitionFee::getFeeNo, todayPrefix);
        long count = feeMapper.selectCount(wrapper);
        return todayPrefix + String.format("%04d", count + 1);
    }

    private void writeHoursLog(Long studentId, Long courseId, BigDecimal amount, String changeType, String sourceType, Long sourceId, String refNo) {
        EduStudentHoursLog log = new EduStudentHoursLog();
        log.setStudentId(studentId);
        log.setCourseId(courseId);
        log.setChangeType(changeType);
        log.setChangeAmount(amount);
        log.setSourceType(sourceType);
        log.setSourceId(sourceId);
        log.setRemark(refNo);
        // 查当前余额
        BigDecimal currentBalance = hoursLogMapper.sumBalanceByStudent(studentId);
        if (currentBalance == null) currentBalance = BigDecimal.ZERO;
        log.setBalanceAfter(currentBalance.add(amount));
        hoursLogMapper.insert(log);
        // 更新学员课时缓存
        updateStudentHoursCache(studentId);
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
