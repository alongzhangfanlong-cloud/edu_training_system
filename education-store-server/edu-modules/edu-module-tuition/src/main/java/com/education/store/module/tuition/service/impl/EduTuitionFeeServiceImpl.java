package com.education.store.module.tuition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.enrollment.entity.EduEnrollment;
import com.education.store.module.enrollment.mapper.EduEnrollmentMapper;
import com.education.store.module.student.entity.EduStudent;
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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduTuitionFeeServiceImpl extends ServiceImpl<EduTuitionFeeMapper, EduTuitionFee>
        implements IEduTuitionFeeService {

    private final EduTuitionFeeMapper feeMapper;
    private final EduEnrollmentMapper enrollmentMapper;
    private final EduStudentHoursLogMapper hoursLogMapper;
    private final EduStudentMapper studentMapper;
    private final EduClassMapper classMapper;
    private final EduCourseMapper courseMapper;

    @Override
    public IPage<EduTuitionFee> selectFeeList(IPage<EduTuitionFee> page, EduTuitionFee fee) {
        LambdaQueryWrapper<EduTuitionFee> wrapper = new LambdaQueryWrapper<>();
        if (fee.getStudentId() != null) wrapper.eq(EduTuitionFee::getStudentId, fee.getStudentId());
        if (fee.getEnrollmentId() != null) wrapper.eq(EduTuitionFee::getEnrollmentId, fee.getEnrollmentId());
        if (StrUtil.isNotBlank(fee.getStatus())) wrapper.eq(EduTuitionFee::getStatus, fee.getStatus());
        wrapper.orderByDesc(EduTuitionFee::getCreateTime);
        IPage<EduTuitionFee> result = feeMapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
    }

    @Override public EduTuitionFee selectFeeById(Long id) {
        EduTuitionFee fee = feeMapper.selectById(id);
        if (fee != null) {
            fillNamesForList(Collections.singletonList(fee));
        }
        return fee;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertFee(EduTuitionFee fee) {
        fee.setFeeNo(generateFeeNo("T"));
        fee.setStatus("CONFIRMED");
        fee.setPayTime(LocalDateTime.now());
        if (fee.getOperatorId() == null) {
            fee.setOperatorId(SecurityUtils.getUserId());
        }
        int rows = feeMapper.insert(fee);

        // 回写报名记录收费状态（累计所有已确认的收费记录判断是否全额付清）
        if (fee.getEnrollmentId() != null) {
            EduEnrollment enrollment = enrollmentMapper.selectById(fee.getEnrollmentId());
            if (enrollment != null) {
                // 累加该报名下所有已确认收费记录的金额
                LambdaQueryWrapper<EduTuitionFee> sumWrapper = new LambdaQueryWrapper<>();
                sumWrapper.eq(EduTuitionFee::getEnrollmentId, fee.getEnrollmentId())
                          .eq(EduTuitionFee::getStatus, "CONFIRMED");
                java.util.List<EduTuitionFee> paidRecords = feeMapper.selectList(sumWrapper);
                BigDecimal totalPaid = paidRecords.stream()
                        .map(EduTuitionFee::getAmountPaid)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                boolean fullPaid = totalPaid.compareTo(enrollment.getAmount()) >= 0;
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
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return prefix + dateStr + timeStr + random;
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

    private void fillNamesForList(List<EduTuitionFee> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> studentIds = new HashSet<>();
        Set<Long> classIds = new HashSet<>();
        Set<Long> courseIds = new HashSet<>();
        for (EduTuitionFee fee : list) {
            if (fee.getStudentId() != null) studentIds.add(fee.getStudentId());
            if (fee.getClassId() != null) classIds.add(fee.getClassId());
            if (fee.getCourseId() != null) courseIds.add(fee.getCourseId());
        }
        Map<Long, EduStudent> studentMap = studentIds.isEmpty() ? Collections.emptyMap()
                : studentMapper.selectBatchIds(studentIds).stream().collect(Collectors.toMap(EduStudent::getId, s -> s));
        Map<Long, EduClass> classMap = classIds.isEmpty() ? Collections.emptyMap()
                : classMapper.selectBatchIds(classIds).stream().collect(Collectors.toMap(EduClass::getId, c -> c));
        Map<Long, EduCourse> courseMap = courseIds.isEmpty() ? Collections.emptyMap()
                : courseMapper.selectBatchIds(courseIds).stream().collect(Collectors.toMap(EduCourse::getId, c -> c));
        for (EduTuitionFee fee : list) {
            if (fee.getStudentId() != null) {
                EduStudent student = studentMap.get(fee.getStudentId());
                if (student != null) fee.setStudentName(student.getName());
            }
            if (fee.getClassId() != null) {
                EduClass clazz = classMap.get(fee.getClassId());
                if (clazz != null) fee.setClassName(clazz.getName());
            }
            if (fee.getCourseId() != null) {
                EduCourse course = courseMap.get(fee.getCourseId());
                if (course != null) fee.setCourseName(course.getName());
            }
        }
    }
}
