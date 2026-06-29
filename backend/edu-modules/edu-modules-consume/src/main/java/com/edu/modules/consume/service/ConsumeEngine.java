package com.edu.modules.consume.service;

import com.edu.common.core.enums.AttendanceStatusEnum;
import com.edu.common.core.enums.HourTransactionTypeEnum;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.core.utils.RedisDistributedLock;
import com.edu.common.core.utils.SystemConfigService;
import com.edu.modules.attendance.entity.AttendanceEntity;
import com.edu.modules.attendance.mapper.AttendanceMapper;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 消课引擎 - 完整的业务规则实现
 *
 * 实现RULE_001(请假消课规则) + RULE_002(签到消课规则) + RULE_003(课时预警)
 *
 * 高并发保护:
 * 1. SELECT ... FOR UPDATE 行锁
 * 2. Redis分布式锁(防止同一学员并发消课)
 * 3. 事务保证原子性
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumeEngine {

    private final AttendanceMapper attendanceMapper;
    private final StudentClassMapper studentClassMapper;
    private final HourTransactionMapper hourTransactionMapper;
    private final ScheduleMapper scheduleMapper;
    private final SystemConfigService configService;
    private final RedisDistributedLock distributedLock;

    /**
     * RULE_001 + RULE_002: 根据考勤状态计算消课规则
     *
     * @param studentId 学员ID
     * @param scheduleId 课表ID
     * @param attendanceStatus 考勤状态
     * @param leaveApplyTime 请假申请时间(仅请假时需要)
     * @return 消课计算结果
     */
    public Map<String, Object> calculate(Long studentId, Long scheduleId,
                                          Integer attendanceStatus, LocalDateTime leaveApplyTime) {
        ScheduleEntity schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);

        // 默认每节课消耗1课时
        BigDecimal defaultConsumeHours = BigDecimal.ONE;
        Map<String, Object> result = new HashMap<>();

        switch (attendanceStatus) {
            case 1: // RULE_002: 正常出勤 → 正常消课
                result.put("consumeHours", defaultConsumeHours);
                result.put("transactionType", HourTransactionTypeEnum.CONSUME.getCode());
                result.put("rule", "正常上课，消课" + defaultConsumeHours + "课时");
                result.put("isFree", false);
                result.put("allowMakeup", false);
                break;

            case 2: // RULE_001: 请假 → 根据提前时间计算
                calculateLeaveConsume(schedule, leaveApplyTime, defaultConsumeHours, result);
                break;

            case 3: // RULE_002: 旷课 → 正常消课，不可补课
                result.put("consumeHours", defaultConsumeHours);
                result.put("transactionType", HourTransactionTypeEnum.CONSUME.getCode());
                result.put("rule", "未请假未到，正常消课" + defaultConsumeHours + "课时，不可补课");
                result.put("isFree", false);
                result.put("allowMakeup", false);
                break;

            case 4: // RULE_002: 补课 → 不额外消课
                result.put("consumeHours", BigDecimal.ZERO);
                result.put("transactionType", HourTransactionTypeEnum.MAKEUP_CONSUME.getCode());
                result.put("rule", "补课完成，不额外消课");
                result.put("isFree", true);
                result.put("allowMakeup", false);
                break;

            default:
                result.put("consumeHours", BigDecimal.ZERO);
                result.put("rule", "未知考勤状态");
                result.put("isFree", true);
        }

        result.put("scheduleId", scheduleId);
        result.put("studentId", studentId);
        return result;
    }

    /**
     * RULE_001: 请假消课规则详细计算
     *
     * 免费请假: 提前 >= leave_free_hours(默认24h) → 不消课，可补课
     * 收费请假: 提前 >= leave_charge_hours(默认2h) 且 < leave_free_hours → 消课50%，可补课
     * 临时请假: 提前 < leave_charge_hours → 正常消课，不可补课(按旷课处理)
     */
    private void calculateLeaveConsume(ScheduleEntity schedule, LocalDateTime leaveApplyTime,
                                        BigDecimal defaultHours, Map<String, Object> result) {
        // 计算提前小时数
        LocalDateTime classStartTime = LocalDateTime.of(schedule.getDate(), schedule.getStartTime());
        long hoursBeforeClass = 0;

        if (leaveApplyTime != null) {
            hoursBeforeClass = ChronoUnit.HOURS.between(leaveApplyTime, classStartTime);
        }

        int freeHours = configService.getLeaveFreeHours();     // 24
        int chargeHours = configService.getLeaveChargeHours(); // 2

        if (hoursBeforeClass >= freeHours) {
            // 免费请假: 不消课，可补课
            result.put("consumeHours", BigDecimal.ZERO);
            result.put("transactionType", HourTransactionTypeEnum.LEAVE_RETURN.getCode());
            result.put("rule", String.format("提前%d小时请假(>=%dh)，不消课，可在%d天内补课",
                    hoursBeforeClass, freeHours, configService.getMakeupValidDays()));
            result.put("isFree", true);
            result.put("allowMakeup", true);
            result.put("makeupValidDays", configService.getMakeupValidDays());
        } else if (hoursBeforeClass >= chargeHours) {
            // 收费请假: 消课50%，可补课
            BigDecimal consumeHours = defaultHours.multiply(new BigDecimal("0.5")).setScale(1, RoundingMode.HALF_UP);
            result.put("consumeHours", consumeHours);
            result.put("transactionType", HourTransactionTypeEnum.CONSUME.getCode());
            result.put("rule", String.format("提前%d小时请假(%dh~%dh)，消课50%%(%s课时)，可补课",
                    hoursBeforeClass, chargeHours, freeHours, consumeHours));
            result.put("isFree", false);
            result.put("allowMakeup", true);
            result.put("makeupValidDays", configService.getMakeupValidDays());
        } else {
            // 临时请假: 按旷课处理
            result.put("consumeHours", defaultHours);
            result.put("transactionType", HourTransactionTypeEnum.CONSUME.getCode());
            result.put("rule", String.format("临时请假(仅提前%d小时，要求>=%dh)，正常消课，不可补课",
                    hoursBeforeClass, chargeHours));
            result.put("isFree", false);
            result.put("allowMakeup", false);
        }
    }

    /**
     * 执行消课 - 核心事务方法
     *
     * 高并发保护:
     * 1. Redis分布式锁: lock:consume:student:{sid}:class:{cid}
     * 2. SELECT ... FOR UPDATE 行级锁
     * 3. @Transactional 事务原子性
     * 4. 课时不足校验 + 乐观锁版本号
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> execute(Long attendanceId, BigDecimal consumeHours, Integer transactionType) {
        // 1. 获取考勤记录
        AttendanceEntity attendance = attendanceMapper.selectById(attendanceId);
        if (attendance == null) throw new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND);

        // 已消课的不重复处理
        if (attendance.getConsumeHours() != null && attendance.getConsumeHours().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("该考勤记录已消课，请勿重复操作");
        }

        // 2. 分布式锁防并发
        String lockKey = RedisDistributedLock.consumeLock(
                attendance.getStudentId(), attendance.getClassId());

        return distributedLock.executeWithLock(lockKey, () -> {
            // 3. 更新考勤消课课时
            attendanceMapper.updateConsumeHours(attendanceId, consumeHours);

            // 4. SELECT FOR UPDATE 锁定学员班级关系
            StudentClassEntity sc = studentClassMapper.selectForUpdate(
                    attendance.getStudentId(), attendance.getClassId());
            if (sc == null) {
                throw new BusinessException(ErrorCode.CLASS_NOT_FOUND, "学员未报名该班级");
            }

            // 5. 课时余额校验
            if (consumeHours.compareTo(BigDecimal.ZERO) > 0) {
                if (sc.getRemainHours().compareTo(consumeHours) < 0) {
                    throw new BusinessException(ErrorCode.HOURS_INSUFFICIENT,
                            String.format("剩余课时不足: %.1f < %.1f", sc.getRemainHours(), consumeHours));
                }
            }

            // 6. 原子更新课时余额
            BigDecimal beforeHours = sc.getRemainHours();
            sc.setUsedHours(sc.getUsedHours().add(consumeHours));
            sc.setRemainHours(sc.getRemainHours().subtract(consumeHours));
            int updated = studentClassMapper.updateById(sc);
            if (updated == 0) {
                throw new BusinessException("课时更新失败，请重试");
            }

            // 7. 记录课时流水
            HourTransactionEntity tx = new HourTransactionEntity();
            tx.setStudentId(attendance.getStudentId());
            tx.setStudentClassId(sc.getId());
            tx.setType(transactionType != null ? transactionType : HourTransactionTypeEnum.CONSUME.getCode());
            tx.setChangeHours(consumeHours.negate());
            tx.setBeforeHours(beforeHours);
            tx.setAfterHours(sc.getRemainHours());
            tx.setScheduleId(attendance.getScheduleId());
            tx.setAttendanceId(attendanceId);
            tx.setRemark("上课消课");
            hourTransactionMapper.insert(tx);

            log.info("消课完成: studentId={}, classId={}, consume={}, remain={}, txId={}",
                    attendance.getStudentId(), attendance.getClassId(),
                    consumeHours, sc.getRemainHours(), tx.getId());

            // 8. RULE_003: 课时预警检查
            checkHourWarning(sc);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("transactionId", tx.getId());
            result.put("remainHours", sc.getRemainHours());
            result.put("beforeHours", beforeHours);
            result.put("afterHours", sc.getRemainHours());
            return result;
        });
    }

    /**
     * 消课回滚 - 带分布式锁
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> rollback(Long attendanceId, String reason) {
        AttendanceEntity attendance = attendanceMapper.selectById(attendanceId);
        if (attendance == null) throw new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND);

        if (attendance.getConsumeHours() == null ||
            attendance.getConsumeHours().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("该考勤记录未消课，无需回滚");
        }

        String lockKey = RedisDistributedLock.consumeLock(
                attendance.getStudentId(), attendance.getClassId());

        return distributedLock.executeWithLock(lockKey, () -> {
            StudentClassEntity sc = studentClassMapper.selectForUpdate(
                    attendance.getStudentId(), attendance.getClassId());

            BigDecimal restoredHours = attendance.getConsumeHours();
            BigDecimal beforeHours = sc.getRemainHours();

            sc.setUsedHours(sc.getUsedHours().subtract(restoredHours));
            sc.setRemainHours(sc.getRemainHours().add(restoredHours));
            studentClassMapper.updateById(sc);

            // 清除考勤消课记录
            attendanceMapper.updateConsumeHours(attendanceId, BigDecimal.ZERO);

            // 记录回滚流水
            HourTransactionEntity tx = new HourTransactionEntity();
            tx.setStudentId(attendance.getStudentId());
            tx.setStudentClassId(sc.getId());
            tx.setType(HourTransactionTypeEnum.LEAVE_RETURN.getCode());
            tx.setChangeHours(restoredHours);
            tx.setBeforeHours(beforeHours);
            tx.setAfterHours(sc.getRemainHours());
            tx.setAttendanceId(attendanceId);
            tx.setRemark("消课回滚: " + (reason != null ? reason : "管理员操作"));
            hourTransactionMapper.insert(tx);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("restoredHours", restoredHours);
            result.put("remainHours", sc.getRemainHours());
            return result;
        });
    }

    /**
     * RULE_003: 课时预警检查
     * 黄色预警: remain <= hour_warning_threshold && > 0
     * 红色预警: remain <= 0
     */
    private void checkHourWarning(StudentClassEntity sc) {
        int threshold = configService.getHourWarningThreshold();
        BigDecimal remain = sc.getRemainHours();

        if (remain.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("🔴 红色预警: 学员{}课程{}课时已用完!", sc.getStudentId(), sc.getClassId());
        } else if (remain.compareTo(new BigDecimal(threshold)) <= 0) {
            log.warn("🟡 黄色预警: 学员{}课程{}剩余{}课时(<=阈值{})",
                    sc.getStudentId(), sc.getClassId(), remain, threshold);
        }
    }

    /**
     * 报名增加课时 - 分布式锁保护
     */
    @Transactional
    public void addHours(Long studentId, Long classId, Long orderId,
                         BigDecimal hours, BigDecimal giftHours) {
        String lockKey = RedisDistributedLock.consumeLock(studentId, classId);

        distributedLock.executeWithLock(lockKey, () -> {
            StudentClassEntity sc = studentClassMapper.selectForUpdate(studentId, classId);
            BigDecimal addHours = hours.add(giftHours);
            BigDecimal beforeHours = (sc != null) ? sc.getRemainHours() : BigDecimal.ZERO;

            if (sc == null) {
                sc = new StudentClassEntity();
                sc.setStudentId(studentId);
                sc.setClassId(classId);
                sc.setOrderId(orderId);
                sc.setTotalHours(addHours);
                sc.setUsedHours(BigDecimal.ZERO);
                sc.setRemainHours(addHours);
                sc.setGiftHours(giftHours);
                sc.setStatus(1);
                studentClassMapper.insert(sc);
            } else {
                sc.setTotalHours(sc.getTotalHours().add(addHours));
                sc.setRemainHours(sc.getRemainHours().add(addHours));
                sc.setGiftHours(sc.getGiftHours().add(giftHours));
                studentClassMapper.updateById(sc);
            }

            // 报名增加流水
            HourTransactionEntity tx = new HourTransactionEntity();
            tx.setStudentId(studentId);
            tx.setStudentClassId(sc.getId());
            tx.setType(HourTransactionTypeEnum.ENROLLMENT.getCode());
            tx.setChangeHours(addHours);
            tx.setBeforeHours(beforeHours);
            tx.setAfterHours(sc.getRemainHours());
            tx.setOrderId(orderId);
            tx.setRemark("报名增加" + hours + "课时" +
                (giftHours.compareTo(BigDecimal.ZERO) > 0 ? "，赠送" + giftHours + "课时" : ""));
            hourTransactionMapper.insert(tx);

            return null;
        });
    }
}
