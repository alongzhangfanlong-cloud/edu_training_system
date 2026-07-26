package com.education.store.module.payroll.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.payroll.entity.EduTeacherPayRate;
import com.education.store.module.payroll.entity.EduTeacherPayroll;
import com.education.store.module.payroll.mapper.EduTeacherPayRateMapper;
import com.education.store.module.payroll.mapper.EduTeacherPayrollMapper;
import com.education.store.module.payroll.service.IEduTeacherPayrollService;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduTeacherPayrollServiceImpl extends ServiceImpl<EduTeacherPayrollMapper, EduTeacherPayroll>
        implements IEduTeacherPayrollService {

    private final EduTeacherPayrollMapper payrollMapper;
    private final EduTeacherPayRateMapper payRateMapper;
    private final EduScheduleMapper scheduleMapper;
    private final SysUserMapper sysUserMapper;
    private final EduCourseMapper courseMapper;

    @Override
    public IPage<EduTeacherPayroll> selectPayrollList(IPage<EduTeacherPayroll> page, EduTeacherPayroll payroll) {
        LambdaQueryWrapper<EduTeacherPayroll> wrapper = new LambdaQueryWrapper<>();
        if (payroll.getTeacherId() != null) wrapper.eq(EduTeacherPayroll::getTeacherId, payroll.getTeacherId());
        if (StrUtil.isNotBlank(payroll.getMonth())) wrapper.eq(EduTeacherPayroll::getMonth, payroll.getMonth());
        if (StrUtil.isNotBlank(payroll.getStatus())) wrapper.eq(EduTeacherPayroll::getStatus, payroll.getStatus());
        wrapper.orderByDesc(EduTeacherPayroll::getMonth);
        IPage<EduTeacherPayroll> result = payrollMapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
    }

    @Override public EduTeacherPayroll selectPayrollById(Long id) {
        EduTeacherPayroll payroll = payrollMapper.selectById(id);
        if (payroll != null) {
            fillNames(payroll);
        }
        return payroll;
    }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoGeneratePayroll(String month) {
        if (StrUtil.isBlank(month)) {
            throw new BusinessException("月份不能为空");
        }
        List<Map<String, Object>> lessonStats = scheduleMapper.countAllConsumedLessonsByMonth(month);
        int count = 0;
        for (Map<String, Object> stat : lessonStats) {
            Long teacherId = ((Number) stat.get("teacherId")).longValue();
            Long courseId = ((Number) stat.get("courseId")).longValue();
            Integer lessonCount = ((Number) stat.get("lessonCount")).intValue();
            if (createPayrollIfNotExists(teacherId, courseId, month, lessonCount)) {
                count++;
            }
        }
        log.info("自动生成课酬记录：month={}, count={}", month, count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoGenerateTeacherPayroll(Long teacherId, String month) {
        if (teacherId == null) {
            throw new BusinessException("教师ID不能为空");
        }
        if (StrUtil.isBlank(month)) {
            throw new BusinessException("月份不能为空");
        }
        // 先删除该教师该月份所有草稿记录，重新生成
        LambdaQueryWrapper<EduTeacherPayroll> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacherPayroll::getTeacherId, teacherId)
                .eq(EduTeacherPayroll::getMonth, month)
                .eq(EduTeacherPayroll::getStatus, "DRAFT");
        payrollMapper.delete(wrapper);

        List<Map<String, Object>> lessonStats = scheduleMapper.countConsumedLessonsByTeacherAndMonth(teacherId, month);
        int count = 0;
        for (Map<String, Object> stat : lessonStats) {
            Long courseId = ((Number) stat.get("courseId")).longValue();
            Integer lessonCount = ((Number) stat.get("lessonCount")).intValue();
            if (createPayrollIfNotExists(teacherId, courseId, month, lessonCount)) {
                count++;
            }
        }
        log.info("自动生成教师课酬：teacherId={}, month={}, count={}", teacherId, month, count);
        return count;
    }

    /**
     * 查询该月有效的课酬单价
     */
    private BigDecimal findEffectivePayRate(Long teacherId, Long courseId, String month) {
        YearMonth ym = YearMonth.parse(month);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        LambdaQueryWrapper<EduTeacherPayRate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacherPayRate::getTeacherId, teacherId)
                .eq(EduTeacherPayRate::getCourseId, courseId)
                .le(EduTeacherPayRate::getEffectiveFrom, monthEnd)
                .and(w -> w.isNull(EduTeacherPayRate::getEffectiveTo)
                        .or().ge(EduTeacherPayRate::getEffectiveTo, monthStart))
                .orderByDesc(EduTeacherPayRate::getEffectiveFrom)
                .last("LIMIT 1");

        EduTeacherPayRate rate = payRateMapper.selectOne(wrapper);
        if (rate == null) {
            // 查找该教师的通用课酬（courseId为null？不允许，默认0）
            log.warn("未找到课酬单价：teacherId={}, courseId={}, month={}", teacherId, courseId, month);
            return BigDecimal.ZERO;
        }
        return rate.getPayPerLesson();
    }

    /**
     * 创建课酬记录（如果不存在）；如果存在DRAFT状态记录则更新
     */
    private boolean createPayrollIfNotExists(Long teacherId, Long courseId, String month, int lessonCount) {
        // 查找已存在的记录
        LambdaQueryWrapper<EduTeacherPayroll> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacherPayroll::getTeacherId, teacherId)
                .eq(EduTeacherPayroll::getCourseId, courseId)
                .eq(EduTeacherPayroll::getMonth, month);
        EduTeacherPayroll existing = payrollMapper.selectOne(wrapper);
        
        if (existing != null) {
            // 如果是DRAFT状态，更新消课数和金额
            if ("DRAFT".equals(existing.getStatus())) {
                BigDecimal payPerLesson = findEffectivePayRate(teacherId, courseId, month);
                existing.setLessonCount(lessonCount);
                existing.setPayPerLesson(payPerLesson);
                existing.setTotalPay(payPerLesson.multiply(BigDecimal.valueOf(lessonCount)));
                existing.setUpdateBy(SecurityUtils.getUserIdAsString());
                payrollMapper.updateById(existing);
                return true;
            }
            // 非DRAFT状态（已确认/已发放），跳过不更新
            return false;
        }

        BigDecimal payPerLesson = findEffectivePayRate(teacherId, courseId, month);
        EduTeacherPayroll payroll = new EduTeacherPayroll();
        payroll.setTeacherId(teacherId);
        payroll.setCourseId(courseId);
        payroll.setMonth(month);
        payroll.setLessonCount(lessonCount);
        payroll.setPayPerLesson(payPerLesson);
        payroll.setTotalPay(payPerLesson.multiply(BigDecimal.valueOf(lessonCount)));
        payroll.setStatus("DRAFT");
        payroll.setCreateBy(SecurityUtils.getUserIdAsString());
        payrollMapper.insert(payroll);
        return true;
    }

    /** 服务端计算 totalPay = lessonCount × payPerLesson */
    private BigDecimal calcTotalPay(EduTeacherPayroll payroll) {
        if (payroll.getLessonCount() == null || payroll.getPayPerLesson() == null) return BigDecimal.ZERO;
        return payroll.getPayPerLesson().multiply(BigDecimal.valueOf(payroll.getLessonCount()));
    }

    // ========== 名称填充 ==========

    private void fillNamesForList(List<EduTeacherPayroll> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> teacherIds = new HashSet<>();
        Set<Long> courseIds = new HashSet<>();
        for (EduTeacherPayroll p : list) {
            if (p.getTeacherId() != null) teacherIds.add(p.getTeacherId());
            if (p.getCourseId() != null) courseIds.add(p.getCourseId());
        }
        Map<Long, SysUser> userMap = teacherIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(teacherIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, EduCourse> courseMap = courseIds.isEmpty() ? Collections.emptyMap()
                : courseMapper.selectBatchIds(courseIds).stream().collect(Collectors.toMap(EduCourse::getId, c -> c));
        for (EduTeacherPayroll p : list) {
            fillPayrollNames(p, userMap, courseMap);
        }
    }

    private void fillNames(EduTeacherPayroll payroll) {
        if (payroll.getTeacherId() != null) {
            SysUser user = sysUserMapper.selectById(payroll.getTeacherId());
            if (user != null) {
                payroll.setTeacherName(getDisplayName(user));
            }
        }
        if (payroll.getCourseId() != null) {
            EduCourse course = courseMapper.selectById(payroll.getCourseId());
            if (course != null) {
                payroll.setCourseName(course.getName());
            }
        }
    }

    private void fillPayrollNames(EduTeacherPayroll payroll, Map<Long, SysUser> userMap, Map<Long, EduCourse> courseMap) {
        if (payroll.getTeacherId() != null) {
            SysUser user = userMap.get(payroll.getTeacherId());
            if (user != null) {
                payroll.setTeacherName(getDisplayName(user));
            }
        }
        if (payroll.getCourseId() != null) {
            EduCourse course = courseMap.get(payroll.getCourseId());
            if (course != null) {
                payroll.setCourseName(course.getName());
            }
        }
    }

    private String getDisplayName(SysUser user) {
        if (user == null) return "--";
        if (user.getRealName() != null && !user.getRealName().isEmpty()) return user.getRealName();
        if (user.getNickName() != null && !user.getNickName().isEmpty()) return user.getNickName();
        return user.getUsername() != null ? user.getUsername() : "--";
    }
}
