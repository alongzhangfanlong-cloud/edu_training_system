package com.edu.modules.consume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.core.enums.AttendanceStatusEnum;
import com.edu.common.core.enums.HourTransactionTypeEnum;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.modules.attendance.entity.AttendanceEntity;
import com.edu.modules.attendance.mapper.AttendanceMapper;
import com.edu.modules.consume.dto.ConsumeRequest;
import com.edu.modules.consume.entity.HourTransactionEntity;
import com.edu.modules.consume.entity.StudentClassEntity;
import com.edu.modules.consume.mapper.HourTransactionMapper;
import com.edu.modules.consume.mapper.StudentClassMapper;
import com.edu.modules.schedule.entity.ScheduleEntity;
import com.edu.modules.schedule.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumeService {

    private final AttendanceMapper attendanceMapper;
    private final StudentClassMapper studentClassMapper;
    private final HourTransactionMapper hourTransactionMapper;
    private final ScheduleMapper scheduleMapper;

    /**
     * 查询消课记录
     */
    public List<HourTransactionEntity> list(Long studentId) {
        LambdaQueryWrapper<HourTransactionEntity> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) wrapper.eq(HourTransactionEntity::getStudentId, studentId);
        wrapper.orderByDesc(HourTransactionEntity::getCreatedAt);
        return hourTransactionMapper.selectList(wrapper);
    }

    /**
     * 计算消课规则
     * Phase 1: 简单规则
     * - 出勤(1): 正常消课1课时
     * - 请假(2): 根据RULE_001计算
     * - 旷课(3): 正常消课1课时
     * - 补课(4): 不消课
     */
    public Map<String, Object> calculate(Long studentId, Long scheduleId, Integer attendanceStatus) {
        Map<String, Object> result = new HashMap<>();

        ScheduleEntity schedule = scheduleMapper.selectById(scheduleId);
        BigDecimal consumeHours = BigDecimal.ONE; // Phase 1简化为固定1课时

        switch (attendanceStatus) {
            case 1: // 出勤
                result.put("consumeHours", consumeHours);
                result.put("rule", "正常上课，消课1课时");
                result.put("isFree", false);
                break;
            case 2: // 请假
                // Phase 1简化: 请假消课0.5课时
                result.put("consumeHours", new BigDecimal("0.5"));
                result.put("rule", "请假，消课50%");
                result.put("isFree", false);
                break;
            case 3: // 旷课
                result.put("consumeHours", consumeHours);
                result.put("rule", "未请假未到，正常消课1课时");
                result.put("isFree", false);
                break;
            case 4: // 补课
                result.put("consumeHours", BigDecimal.ZERO);
                result.put("rule", "补课完成，不额外消课");
                result.put("isFree", true);
                break;
            default:
                result.put("consumeHours", BigDecimal.ZERO);
                result.put("rule", "未知状态");
                result.put("isFree", true);
        }
        return result;
    }

    /**
     * 执行消课 - 核心事务方法
     * 原子操作: attendance更新 -> student_class锁定并更新 -> hour_transaction记录
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> execute(ConsumeRequest request) {
        // 1. 获取考勤记录
        AttendanceEntity attendance = attendanceMapper.selectById(request.getAttendanceId());
        if (attendance == null) {
            throw new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND);
        }

        BigDecimal consumeHours = request.getConsumeHours() != null
                ? request.getConsumeHours() : BigDecimal.ONE;

        // 2. 更新考勤消课课时
        attendanceMapper.updateConsumeHours(attendance.getId(), consumeHours);

        // 3. 锁定学员班级关系行 (SELECT FOR UPDATE)
        StudentClassEntity sc = studentClassMapper.selectForUpdate(
                attendance.getStudentId(), attendance.getClassId());
        if (sc == null) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND, "学员未报名该班级");
        }

        // 4. 校验课时充足
        if (sc.getRemainHours().compareTo(consumeHours) < 0) {
            throw new BusinessException(ErrorCode.HOURS_INSUFFICIENT,
                    "剩余课时不足: " + sc.getRemainHours() + " < " + consumeHours);
        }

        // 5. 更新课时余额
        BigDecimal beforeHours = sc.getRemainHours();
        sc.setUsedHours(sc.getUsedHours().add(consumeHours));
        sc.setRemainHours(sc.getRemainHours().subtract(consumeHours));
        studentClassMapper.updateById(sc);

        // 6. 记录课时流水
        HourTransactionEntity tx = new HourTransactionEntity();
        tx.setStudentId(attendance.getStudentId());
        tx.setStudentClassId(sc.getId());
        tx.setType(HourTransactionTypeEnum.CONSUME.getCode());
        tx.setChangeHours(consumeHours.negate()); // 负数表示消耗
        tx.setBeforeHours(beforeHours);
        tx.setAfterHours(sc.getRemainHours());
        tx.setScheduleId(attendance.getScheduleId());
        tx.setAttendanceId(attendance.getId());
        tx.setRemark("上课消课");
        hourTransactionMapper.insert(tx);

        log.info("消课完成: studentId={}, classId={}, consume={}, remain={}",
                attendance.getStudentId(), attendance.getClassId(), consumeHours, sc.getRemainHours());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("transactionId", tx.getId());
        result.put("remainHours", sc.getRemainHours());
        return result;
    }

    /**
     * 消课回滚
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> rollback(Long attendanceId, String reason) {
        AttendanceEntity attendance = attendanceMapper.selectById(attendanceId);
        if (attendance == null) {
            throw new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND);
        }

        if (attendance.getConsumeHours() == null || attendance.getConsumeHours().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("该考勤记录未消课，无需回滚");
        }

        // 恢复课时
        StudentClassEntity sc = studentClassMapper.selectForUpdate(
                attendance.getStudentId(), attendance.getClassId());
        BigDecimal restoredHours = attendance.getConsumeHours();
        BigDecimal beforeHours = sc.getRemainHours();
        sc.setUsedHours(sc.getUsedHours().subtract(restoredHours));
        sc.setRemainHours(sc.getRemainHours().add(restoredHours));
        studentClassMapper.updateById(sc);

        // 清除考勤消课记录
        attendanceMapper.updateConsumeHours(attendance.getId(), BigDecimal.ZERO);

        // 记录回滚流水
        HourTransactionEntity tx = new HourTransactionEntity();
        tx.setStudentId(attendance.getStudentId());
        tx.setStudentClassId(sc.getId());
        tx.setType(HourTransactionTypeEnum.LEAVE_RETURN.getCode());
        tx.setChangeHours(restoredHours);
        tx.setBeforeHours(beforeHours);
        tx.setAfterHours(sc.getRemainHours());
        tx.setAttendanceId(attendanceId);
        tx.setRemark("消课回滚: " + (reason != null ? reason : ""));
        hourTransactionMapper.insert(tx);

        log.info("消课回滚完成: attendanceId={}, restored={}", attendanceId, restoredHours);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("restoredHours", restoredHours);
        return result;
    }

    /**
     * 增加课时（报名时调用）
     */
    @Transactional
    public void addHours(Long studentId, Long classId, Long orderId,
                         BigDecimal hours, BigDecimal giftHours) {
        StudentClassEntity sc = studentClassMapper.selectForUpdate(studentId, classId);
        BigDecimal before = sc != null ? sc.getRemainHours() : BigDecimal.ZERO;

        if (sc == null) {
            sc = new StudentClassEntity();
            sc.setStudentId(studentId);
            sc.setClassId(classId);
            sc.setOrderId(orderId);
            sc.setTotalHours(hours.add(giftHours));
            sc.setUsedHours(BigDecimal.ZERO);
            sc.setRemainHours(hours.add(giftHours));
            sc.setGiftHours(giftHours);
            sc.setStatus(1);
            studentClassMapper.insert(sc);
        } else {
            sc.setTotalHours(sc.getTotalHours().add(hours).add(giftHours));
            sc.setRemainHours(sc.getRemainHours().add(hours).add(giftHours));
            sc.setGiftHours(sc.getGiftHours().add(giftHours));
            studentClassMapper.updateById(sc);
        }

        // 记录流水
        HourTransactionEntity tx = new HourTransactionEntity();
        tx.setStudentId(studentId);
        tx.setStudentClassId(sc.getId());
        tx.setType(HourTransactionTypeEnum.ENROLLMENT.getCode());
        tx.setChangeHours(hours.add(giftHours));
        tx.setBeforeHours(before);
        tx.setAfterHours(sc.getRemainHours());
        tx.setOrderId(orderId);
        tx.setRemark("报名增加课时");
        hourTransactionMapper.insert(tx);

        if (giftHours.compareTo(BigDecimal.ZERO) > 0) {
            HourTransactionEntity giftTx = new HourTransactionEntity();
            giftTx.setStudentId(studentId);
            giftTx.setStudentClassId(sc.getId());
            giftTx.setType(HourTransactionTypeEnum.GIFT.getCode());
            giftTx.setChangeHours(giftHours);
            giftTx.setBeforeHours(before.add(hours));
            giftTx.setAfterHours(sc.getRemainHours());
            giftTx.setOrderId(orderId);
            giftTx.setRemark("赠送课时");
            hourTransactionMapper.insert(giftTx);
        }
    }
}
