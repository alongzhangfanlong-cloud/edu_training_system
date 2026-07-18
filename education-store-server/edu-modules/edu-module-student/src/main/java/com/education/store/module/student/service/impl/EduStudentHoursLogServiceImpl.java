package com.education.store.module.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.entity.EduStudentHoursLog;
import com.education.store.module.student.mapper.EduStudentHoursLogMapper;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.student.service.IEduStudentHoursLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课时变动日志 Service 实现（只追加不可变，类似银行流水）
 */
@Service
@RequiredArgsConstructor
public class EduStudentHoursLogServiceImpl extends ServiceImpl<EduStudentHoursLogMapper, EduStudentHoursLog>
        implements IEduStudentHoursLogService {

    private final EduStudentHoursLogMapper hoursLogMapper;
    private final EduStudentMapper studentMapper;

    @Override
    public IPage<EduStudentHoursLog> selectHoursLogList(
            IPage<EduStudentHoursLog> page, Long studentId, Long courseId) {
        LambdaQueryWrapper<EduStudentHoursLog> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(EduStudentHoursLog::getStudentId, studentId);
        }
        if (courseId != null) {
            wrapper.eq(EduStudentHoursLog::getCourseId, courseId);
        }
        wrapper.orderByDesc(EduStudentHoursLog::getCreateTime);
        return hoursLogMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHoursLog(EduStudentHoursLog log) {
        // 1. 校验学员存在
        EduStudent student = studentMapper.selectById(log.getStudentId());
        if (student == null) {
            throw new BusinessException("学员不存在");
        }

        // 2. 计算变动后余额
        // 注：MySQL FOR UPDATE 行锁需要在 mapper 层使用原生 SQL
        // 此处使用聚合查询（简化版，生产应加 FOR UPDATE）
        BigDecimal previousBalance;
        if (log.getCourseId() != null) {
            previousBalance = hoursLogMapper.sumBalanceByStudentAndCourse(
                    log.getStudentId(), log.getCourseId());
        } else {
            previousBalance = hoursLogMapper.sumBalanceByStudent(log.getStudentId());
        }

        if (previousBalance == null) {
            previousBalance = BigDecimal.ZERO;
        }
        BigDecimal balanceAfter = previousBalance.add(log.getChangeAmount());

        if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("课时变动后余额不能为负数（当前余额: " + previousBalance + "）");
        }

        log.setBalanceAfter(balanceAfter);
        log.setCreateTime(LocalDateTime.now());

        // 3. 插入日志记录（只追加）
        int rows = hoursLogMapper.insert(log);

        // 4. 同步更新学员课时缓存
        BigDecimal totalHours = hoursLogMapper.sumTotalGainedByStudent(log.getStudentId());
        BigDecimal remainingHours = hoursLogMapper.sumBalanceByStudent(log.getStudentId());

        EduStudent updateStudent = new EduStudent();
        updateStudent.setId(log.getStudentId());
        updateStudent.setTotalHours(totalHours != null ? totalHours : BigDecimal.ZERO);
        updateStudent.setRemainingHours(remainingHours != null ? remainingHours : BigDecimal.ZERO);
        studentMapper.updateById(updateStudent);

        return rows;
    }

    @Override
    public BigDecimal getRemainingHours(Long studentId) {
        BigDecimal balance = hoursLogMapper.sumBalanceByStudent(studentId);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalGainedHours(Long studentId) {
        BigDecimal total = hoursLogMapper.sumTotalGainedByStudent(studentId);
        return total != null ? total : BigDecimal.ZERO;
    }

    // ========== 禁止修改和删除 ==========

    @Override
    public boolean updateById(EduStudentHoursLog entity) {
        throw new BusinessException("课时变动日志不可修改（只追加）");
    }

    @Override
    public boolean removeById(EduStudentHoursLog entity) {
        throw new BusinessException("课时变动日志不可删除（只追加）");
    }
}
