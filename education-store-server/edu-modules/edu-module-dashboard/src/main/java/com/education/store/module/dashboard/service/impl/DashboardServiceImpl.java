package com.education.store.module.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.entity.EduLeaveStudent;
import com.education.store.module.attendance.entity.EduLeaveTeacher;
import com.education.store.module.attendance.mapper.EduAttendanceMapper;
import com.education.store.module.attendance.mapper.EduLeaveStudentMapper;
import com.education.store.module.attendance.mapper.EduLeaveTeacherMapper;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.classinfo.mapper.EduClassStudentMapper;
import com.education.store.module.classinfo.entity.EduClassStudent;
import com.education.store.module.course.entity.EduClassroom;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduClassroomMapper;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.dashboard.model.*;
import com.education.store.module.dashboard.service.IDashboardService;
import com.education.store.module.enrollment.entity.EduEnrollment;
import com.education.store.module.enrollment.mapper.EduEnrollmentMapper;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.teaching.entity.EduTeacher;
import com.education.store.module.teaching.mapper.EduTeacherMapper;
import com.education.store.module.tuition.entity.EduTuitionFee;
import com.education.store.module.tuition.entity.EduTuitionRefund;
import com.education.store.module.tuition.mapper.EduTuitionFeeMapper;
import com.education.store.module.tuition.mapper.EduTuitionRefundMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作台 Dashboard Service 实现
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final SysUserMapper sysUserMapper;
    private final EduStudentMapper studentMapper;
    private final EduScheduleMapper scheduleMapper;
    private final EduClassMapper classMapper;
    private final EduCourseMapper courseMapper;
    private final EduClassroomMapper classroomMapper;
    private final EduClassStudentMapper classStudentMapper;
    private final EduTuitionFeeMapper tuitionFeeMapper;
    private final EduTuitionRefundMapper tuitionRefundMapper;
    private final EduEnrollmentMapper enrollmentMapper;
    private final EduLeaveTeacherMapper leaveTeacherMapper;
    private final EduLeaveStudentMapper leaveStudentMapper;
    private final EduAttendanceMapper attendanceMapper;
    private final EduTeacherMapper teacherMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public UserInfoVO getUserInfo() {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserMapper.selectById(userId);

        LocalDate today = LocalDate.now();
        String dayOfWeek = getDayOfWeekChinese(today.getDayOfWeek());

        return UserInfoVO.builder()
                .userName(getDisplayName(user))
                .lastLoginTime(user != null && user.getLoginDate() != null
                        ? user.getLoginDate().format(DATETIME_FMT) : null)
                .isFirstLogin(user == null || user.getLoginDate() == null)
                .serverDate(today.format(DATE_FMT))
                .serverDayOfWeek(dayOfWeek)
                .serverDateFormatted(today.getYear() + "年" + today.getMonthValue() + "月"
                        + today.getDayOfMonth() + "日 " + dayOfWeek)
                .build();
    }

    @Override
    public StatisticsVO getStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate todayEnd = today;

        // 1. 在学学员数
        LambdaQueryWrapper<EduStudent> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(EduStudent::getStatus, "ENROLLED")
                .eq(EduStudent::getDeleted, 0);
        long studentCount = studentMapper.selectCount(studentWrapper);

        // 2. 当月排课次数
        LambdaQueryWrapper<EduSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.between(EduSchedule::getScheduleDate, monthStart, todayEnd)
                .notIn(EduSchedule::getStatus, "CANCELLED", "SUSPENDED")
                .eq(EduSchedule::getDeleted, 0);
        long scheduleCount = scheduleMapper.selectCount(scheduleWrapper);

        // 3. 在职教师数（从 edu_teacher 表查询）
        LambdaQueryWrapper<EduTeacher> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(EduTeacher::getStatus, "ACTIVE")
                .eq(EduTeacher::getDeleted, 0);
        long teacherCount = teacherMapper.selectCount(teacherWrapper);

        // 4. 本月营收
        LambdaQueryWrapper<EduTuitionFee> feeWrapper = new LambdaQueryWrapper<>();
        feeWrapper.between(EduTuitionFee::getPayTime,
                        monthStart.atStartOfDay(), todayEnd.plusDays(1).atStartOfDay())
                .eq(EduTuitionFee::getStatus, "CONFIRMED")
                .eq(EduTuitionFee::getDeleted, 0);
        List<EduTuitionFee> fees = tuitionFeeMapper.selectList(feeWrapper);
        BigDecimal monthlyIncome = fees.stream()
                .map(EduTuitionFee::getAmountPaid)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<EduTuitionRefund> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.between(EduTuitionRefund::getApprovalTime,
                        monthStart.atStartOfDay(), todayEnd.plusDays(1).atStartOfDay())
                .eq(EduTuitionRefund::getStatus, "COMPLETED")
                .eq(EduTuitionRefund::getDeleted, 0);
        List<EduTuitionRefund> refunds = tuitionRefundMapper.selectList(refundWrapper);
        BigDecimal monthlyRefund = refunds.stream()
                .map(EduTuitionRefund::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyRevenue = monthlyIncome.subtract(monthlyRefund);

        // 5. 上月同期营收（环比）
        LocalDate lastMonthStart = monthStart.minusMonths(1);
        LocalDate lastMonthEnd = today.minusMonths(1);

        LambdaQueryWrapper<EduTuitionFee> lastFeeWrapper = new LambdaQueryWrapper<>();
        lastFeeWrapper.between(EduTuitionFee::getPayTime,
                        lastMonthStart.atStartOfDay(), lastMonthEnd.plusDays(1).atStartOfDay())
                .eq(EduTuitionFee::getStatus, "CONFIRMED")
                .eq(EduTuitionFee::getDeleted, 0);
        List<EduTuitionFee> lastFees = tuitionFeeMapper.selectList(lastFeeWrapper);
        BigDecimal lastMonthIncome = lastFees.stream()
                .map(EduTuitionFee::getAmountPaid)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<EduTuitionRefund> lastRefundWrapper = new LambdaQueryWrapper<>();
        lastRefundWrapper.between(EduTuitionRefund::getApprovalTime,
                        lastMonthStart.atStartOfDay(), lastMonthEnd.plusDays(1).atStartOfDay())
                .eq(EduTuitionRefund::getStatus, "COMPLETED")
                .eq(EduTuitionRefund::getDeleted, 0);
        List<EduTuitionRefund> lastRefunds = tuitionRefundMapper.selectList(lastRefundWrapper);
        BigDecimal lastMonthRefund = lastRefunds.stream()
                .map(EduTuitionRefund::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lastMonthRevenue = lastMonthIncome.subtract(lastMonthRefund);

        // 计算趋势
        String trend;
        BigDecimal trendPercentage = null;
        if (lastMonthRevenue.compareTo(BigDecimal.ZERO) == 0) {
            trend = monthlyRevenue.compareTo(BigDecimal.ZERO) > 0 ? "NONE" : "FLAT";
        } else {
            BigDecimal diff = monthlyRevenue.subtract(lastMonthRevenue);
            trendPercentage = diff.divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
            int cmp = diff.compareTo(BigDecimal.ZERO);
            if (cmp > 0) {
                trend = "UP";
            } else if (cmp < 0) {
                trend = "DOWN";
            } else {
                trend = "FLAT";
            }
        }

        return StatisticsVO.builder()
                .studentCount((int) studentCount)
                .scheduleCount((int) scheduleCount)
                .teacherCount((int) teacherCount)
                .monthlyRevenue(monthlyRevenue)
                .lastMonthRevenue(lastMonthRevenue)
                .revenueTrend(trend)
                .revenueTrendPercentage(trendPercentage)
                .build();
    }

    @Override
    public CalendarVO getCalendar(String viewType, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        LocalDate rangeStart, rangeEnd;
        String viewLabel;
        boolean hasPrev, hasNext;
        boolean isCurrentPeriod;

        switch (viewType.toUpperCase()) {
            case "WEEK":
                rangeStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                rangeEnd = rangeStart.plusDays(6);
                WeekFields wf = WeekFields.of(DayOfWeek.MONDAY, 4);
                int weekNum = date.get(wf.weekOfWeekBasedYear());
                viewLabel = rangeStart.format(DateTimeFormatter.ofPattern("MM/dd"))
                        + " - " + rangeEnd.format(DateTimeFormatter.ofPattern("MM/dd"))
                        + " 第" + weekNum + "周";
                isCurrentPeriod = !LocalDate.now().isBefore(rangeStart)
                        && !LocalDate.now().isAfter(rangeEnd);
                hasPrev = rangeStart.isAfter(LocalDate.now().minusWeeks(12).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
                hasNext = rangeEnd.isBefore(LocalDate.now());
                break;
            case "DAY":
                rangeStart = date;
                rangeEnd = date;
                viewLabel = date.getYear() + "年" + date.getMonthValue() + "月" + date.getDayOfMonth() + "日";
                isCurrentPeriod = date.equals(LocalDate.now());
                hasPrev = date.isAfter(LocalDate.now().minusWeeks(12));
                hasNext = date.isBefore(LocalDate.now());
                break;
            default: // MONTH
                rangeStart = date.withDayOfMonth(1);
                rangeEnd = date.withDayOfMonth(date.lengthOfMonth());
                viewLabel = date.getYear() + "年" + date.getMonthValue() + "月";
                isCurrentPeriod = YearMonth.from(date).equals(YearMonth.from(LocalDate.now()));
                hasPrev = date.isAfter(LocalDate.now().minusMonths(3).withDayOfMonth(1));
                hasNext = date.isBefore(LocalDate.now().withDayOfMonth(1));
                break;
        }

        // 查询排课数据
        LambdaQueryWrapper<EduSchedule> sw = new LambdaQueryWrapper<>();
        sw.between(EduSchedule::getScheduleDate, rangeStart, rangeEnd)
                .notIn(EduSchedule::getStatus, "CANCELLED", "SUSPENDED")
                .eq(EduSchedule::getDeleted, 0)
                .orderByAsc(EduSchedule::getScheduleDate, EduSchedule::getStartTime);
        List<EduSchedule> schedules = scheduleMapper.selectList(sw);

        // 预加载关联数据
        Set<Long> classIds = schedules.stream().map(EduSchedule::getClassId).collect(Collectors.toSet());
        Map<Long, EduClass> classMap = new HashMap<>();
        if (!classIds.isEmpty()) {
            classMap = classMapper.selectBatchIds(classIds).stream()
                    .collect(Collectors.toMap(EduClass::getId, c -> c));
        }

        Set<Long> courseIds = classMap.values().stream().map(EduClass::getCourseId).collect(Collectors.toSet());
        Map<Long, EduCourse> courseMap = new HashMap<>();
        if (!courseIds.isEmpty()) {
            courseMap = courseMapper.selectBatchIds(courseIds).stream()
                    .collect(Collectors.toMap(EduCourse::getId, c -> c));
        }

        Set<Long> roomIds = classMap.values().stream().map(EduClass::getClassroomId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, EduClassroom> roomMap = new HashMap<>();
        if (!roomIds.isEmpty()) {
            roomMap = classroomMapper.selectBatchIds(roomIds).stream()
                    .collect(Collectors.toMap(EduClassroom::getId, r -> r));
        }

        // 批量查询签到数据
        List<Long> scheduleIds = schedules.stream().map(EduSchedule::getId).collect(Collectors.toList());
        Map<Long, Integer> attendedCountMap = new HashMap<>();
        if (!scheduleIds.isEmpty()) {
            LambdaQueryWrapper<EduAttendance> attWrapper = new LambdaQueryWrapper<>();
            attWrapper.in(EduAttendance::getScheduleId, scheduleIds)
                    .eq(EduAttendance::getDeleted, 0);
            List<EduAttendance> attendances = attendanceMapper.selectList(attWrapper);
            Map<Long, List<EduAttendance>> attBySchedule = attendances.stream()
                    .collect(Collectors.groupingBy(EduAttendance::getScheduleId));
            attBySchedule.forEach((sId, atts) -> {
                int count = (int) atts.stream()
                        .filter(a -> a.getStatus() != null && (a.getStatus() == EduAttendance.STATUS_PRESENT || a.getStatus() == EduAttendance.STATUS_LATE))
                        .count();
                attendedCountMap.put(sId, count);
            });
        }

        // 按日期分组排课
        Map<LocalDate, List<EduSchedule>> scheduleByDate = schedules.stream()
                .collect(Collectors.groupingBy(EduSchedule::getScheduleDate));

        // 构建日期列表
        List<CalendarVO.DateEntry> dates = new ArrayList<>();
        LocalDate current = rangeStart;
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        while (!current.isAfter(rangeEnd)) {
            List<EduSchedule> daySchedules = scheduleByDate.getOrDefault(current, Collections.emptyList());
            List<CalendarVO.ScheduleItem> items = new ArrayList<>();

            for (EduSchedule s : daySchedules) {
                EduClass clazz = classMap.get(s.getClassId());
                EduCourse course = clazz != null ? courseMap.get(clazz.getCourseId()) : null;
                EduClassroom room = clazz != null ? roomMap.get(clazz.getClassroomId()) : null;

                // 计算排课状态
                String scheduleStatus;
                if (current.isBefore(today)) {
                    scheduleStatus = "FINISHED";
                } else if (current.isAfter(today)) {
                    scheduleStatus = "NOT_STARTED";
                } else {
                    // 今天：按时间判定
                    if (s.getEndTime() != null && now.isAfter(s.getEndTime())) {
                        scheduleStatus = "FINISHED";
                    } else if (s.getStartTime() != null && s.getEndTime() != null
                            && !now.isBefore(s.getStartTime()) && !now.isAfter(s.getEndTime())) {
                        scheduleStatus = "IN_PROGRESS";
                    } else {
                        scheduleStatus = "NOT_STARTED";
                    }
                }

                items.add(CalendarVO.ScheduleItem.builder()
                        .scheduleId(s.getId())
                        .startTime(s.getStartTime() != null ? s.getStartTime().format(TIME_FMT) : null)
                        .endTime(s.getEndTime() != null ? s.getEndTime().format(TIME_FMT) : null)
                        .courseName(course != null ? course.getName() : "--")
                        .className(clazz != null ? clazz.getName() : "--")
                        .teacherName(getTeacherName(clazz != null ? clazz.getTeacherId() : null))
                        .classroomName(room != null ? room.getName() : "未分配")
                        .expectedCount(clazz != null ? clazz.getCurrentCount() : 0)
                        .attendedCount(attendedCountMap.getOrDefault(s.getId(), 0))
                        .status(scheduleStatus)
                        .build());
            }

            dates.add(CalendarVO.DateEntry.builder()
                    .date(current.format(DATE_FMT))
                    .dayOfWeek(current.getDayOfWeek().getValue())
                    .dayLabel(getDayOfWeekChinese(current.getDayOfWeek()))
                    .isToday(current.equals(today))
                    .isCurrentMonth("MONTH".equals(viewType.toUpperCase())
                            ? YearMonth.from(current).equals(YearMonth.from(date)) : true)
                    .schedules(items)
                    .totalCount(items.size())
                    .displayCount(Math.min(items.size(), 3))
                    .build());

            current = current.plusDays(1);
        }

        return CalendarVO.builder()
                .viewType(viewType.toUpperCase())
                .viewLabel(viewLabel)
                .hasPrev(hasPrev)
                .hasNext(hasNext)
                .isCurrentPeriod(isCurrentPeriod)
                .dates(dates)
                .build();
    }

    @Override
    public ScheduleStudentVO getScheduleStudents(Long scheduleId) {
        // 查询排课
        EduSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            return ScheduleStudentVO.builder()
                    .scheduleId(scheduleId).totalCount(0).attendedCount(0)
                    .students(Collections.emptyList()).build();
        }

        // 查询班级学生
        LambdaQueryWrapper<EduClassStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(EduClassStudent::getClassId, schedule.getClassId())
                .eq(EduClassStudent::getStatus, "ENROLLED")
                .eq(EduClassStudent::getDeleted, 0);
        List<EduClassStudent> classStudents = classStudentMapper.selectList(csWrapper);

        List<Long> studentIds = classStudents.stream()
                .map(EduClassStudent::getStudentId).collect(Collectors.toList());

        // 查询学生信息
        Map<Long, EduStudent> studentMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            studentMap = studentMapper.selectBatchIds(studentIds).stream()
                    .collect(Collectors.toMap(EduStudent::getId, s -> s));
        }

        // 查询签到记录
        LambdaQueryWrapper<EduAttendance> attWrapper = new LambdaQueryWrapper<>();
        attWrapper.eq(EduAttendance::getScheduleId, scheduleId)
                .eq(EduAttendance::getDeleted, 0);
        List<EduAttendance> attendances = attendanceMapper.selectList(attWrapper);
        Map<Long, Integer> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(EduAttendance::getStudentId, EduAttendance::getStatus));

        int totalCount = classStudents.size();
        int attendedCount = (int) attendances.stream()
                .filter(a -> a.getStatus() != null && (a.getStatus() == 0 || a.getStatus() == 1)).count();

        List<ScheduleStudentVO.StudentEntry> students = new ArrayList<>();
        for (EduClassStudent cs : classStudents) {
            EduStudent student = studentMap.get(cs.getStudentId());
            Integer attStatus = attendanceMap.get(cs.getStudentId());
            String attStatusStr = null;
            if (attStatus != null) {
                switch (attStatus) {
                    case 0: attStatusStr = "PRESENT"; break;
                    case 1: attStatusStr = "LATE"; break;
                    case 2: attStatusStr = "ABSENT"; break;
                }
            }
            students.add(ScheduleStudentVO.StudentEntry.builder()
                    .studentId(cs.getStudentId())
                    .studentName(student != null ? student.getName() : "--")
                    .attendanceStatus(attStatusStr)
                    .build());
        }

        return ScheduleStudentVO.builder()
                .scheduleId(scheduleId)
                .totalCount(totalCount)
                .attendedCount(attendedCount)
                .students(students)
                .build();
    }

    @Override
    public TodosVO getTodos() {
        // 1. 待审批请假（教师 + 学生）
        // 1a. 教师请假
        LambdaQueryWrapper<EduLeaveTeacher> leaveTeacherWrapper = new LambdaQueryWrapper<>();
        leaveTeacherWrapper.eq(EduLeaveTeacher::getStatus, 0) // 0 = PENDING
                .eq(EduLeaveTeacher::getDeleted, 0)
                .orderByDesc(EduLeaveTeacher::getCreateTime)
                .last("LIMIT 3");
        List<EduLeaveTeacher> teacherLeaves = leaveTeacherMapper.selectList(leaveTeacherWrapper);

        List<TodosVO.PendingLeaveItem> leaveItems = new java.util.ArrayList<>();
        for (EduLeaveTeacher l : teacherLeaves) {
            String leaveTypeName;
            switch (l.getLeaveType() != null ? l.getLeaveType() : 0) {
                case 1: leaveTypeName = "事假"; break;
                case 2: leaveTypeName = "病假"; break;
                default: leaveTypeName = "其他";
            }
            leaveItems.add(TodosVO.PendingLeaveItem.builder()
                    .leaveId(l.getId())
                    .applicantType("TEACHER")
                    .teacherName(getTeacherName(l.getTeacherId()))
                    .leaveType(String.valueOf(l.getLeaveType()))
                    .leaveTypeName(leaveTypeName)
                    .startDate(l.getLeaveStartDate() != null ? l.getLeaveStartDate().format(DATE_FMT) : null)
                    .endDate(l.getLeaveEndDate() != null ? l.getLeaveEndDate().format(DATE_FMT) : null)
                    .period("FULL_DAY")
                    .applyTime(l.getCreateTime() != null ? l.getCreateTime().format(DATETIME_FMT) : null)
                    .build());
        }
        long teacherLeaveCount = leaveTeacherMapper.selectCount(
                new LambdaQueryWrapper<EduLeaveTeacher>()
                        .eq(EduLeaveTeacher::getStatus, 0)
                        .eq(EduLeaveTeacher::getDeleted, 0));

        // 1b. 学生请假
        LambdaQueryWrapper<EduLeaveStudent> leaveStudentWrapper = new LambdaQueryWrapper<>();
        leaveStudentWrapper.eq(EduLeaveStudent::getStatus, 0) // 0 = 待确认
                .eq(EduLeaveStudent::getDeleted, 0)
                .orderByDesc(EduLeaveStudent::getCreateTime)
                .last("LIMIT 3");
        List<EduLeaveStudent> studentLeaves = leaveStudentMapper.selectList(leaveStudentWrapper);

        for (EduLeaveStudent l : studentLeaves) {
            String leaveTypeName;
            switch (l.getLeaveType() != null ? l.getLeaveType() : 0) {
                case 1: leaveTypeName = "事假"; break;
                case 2: leaveTypeName = "病假"; break;
                default: leaveTypeName = "其他";
            }
            leaveItems.add(TodosVO.PendingLeaveItem.builder()
                    .leaveId(l.getId())
                    .applicantType("STUDENT")
                    .studentName(getStudentName(l.getStudentId()))
                    .leaveType(String.valueOf(l.getLeaveType()))
                    .leaveTypeName(leaveTypeName)
                    .startDate(l.getLeaveDate() != null ? l.getLeaveDate().format(DATE_FMT) : null)
                    .endDate(l.getLeaveDate() != null ? l.getLeaveDate().format(DATE_FMT) : null)
                    .period("FULL_DAY")
                    .applyTime(l.getCreateTime() != null ? l.getCreateTime().format(DATETIME_FMT) : null)
                    .build());
        }
        long studentLeaveCount = leaveStudentMapper.selectCount(
                new LambdaQueryWrapper<EduLeaveStudent>()
                        .eq(EduLeaveStudent::getStatus, 0)
                        .eq(EduLeaveStudent::getDeleted, 0));

        // 合并后按申请时间倒序，取前 3 条
        leaveItems.sort((a, b) -> {
            String t1 = a.getApplyTime() != null ? a.getApplyTime() : "";
            String t2 = b.getApplyTime() != null ? b.getApplyTime() : "";
            return t2.compareTo(t1);
        });
        if (leaveItems.size() > 3) {
            leaveItems = leaveItems.subList(0, 3);
        }
        long leaveCount = teacherLeaveCount + studentLeaveCount;

        // 2. 待处理退费
        LambdaQueryWrapper<EduTuitionRefund> pendingRefundWrapper = new LambdaQueryWrapper<>();
        pendingRefundWrapper.eq(EduTuitionRefund::getStatus, "PENDING")
                .eq(EduTuitionRefund::getDeleted, 0)
                .orderByDesc(EduTuitionRefund::getCreateTime)
                .last("LIMIT 3");
        List<EduTuitionRefund> pendingRefunds = tuitionRefundMapper.selectList(pendingRefundWrapper);

        List<Long> refundStudentIds = pendingRefunds.stream()
                .map(EduTuitionRefund::getStudentId).collect(Collectors.toList());
        final Map<Long, EduStudent> refundStudentMap;
        if (!refundStudentIds.isEmpty()) {
            refundStudentMap = studentMapper.selectBatchIds(refundStudentIds).stream()
                    .collect(Collectors.toMap(EduStudent::getId, s -> s));
        } else {
            refundStudentMap = Collections.emptyMap();
        }

        List<TodosVO.PendingRefundItem> refundItems = pendingRefunds.stream().map(r -> {
            EduStudent student = refundStudentMap.get(r.getStudentId());
            return TodosVO.PendingRefundItem.builder()
                    .refundId(r.getId())
                    .studentName(student != null ? student.getName() : "--")
                    .amount(r.getAmount())
                    .applyTime(r.getCreateTime() != null ? r.getCreateTime().format(DATETIME_FMT) : null)
                    .build();
        }).collect(Collectors.toList());

        LambdaQueryWrapper<EduTuitionRefund> pendingRefundCountWrapper = new LambdaQueryWrapper<>();
        pendingRefundCountWrapper.eq(EduTuitionRefund::getStatus, "PENDING")
                .eq(EduTuitionRefund::getDeleted, 0);
        long refundCount = tuitionRefundMapper.selectCount(pendingRefundCountWrapper);

        // 3. 排课冲突预警：检测同一教师/教室在同一时间段有多个排课
        List<TodosVO.ScheduleConflictItem> conflictItems = new ArrayList<>();
        long conflictCount = 0;
        LocalDate today = LocalDate.now();
        LocalDate conflictStart = today.minusDays(1);
        LocalDate conflictEnd = today.plusDays(7);

        LambdaQueryWrapper<EduSchedule> conflictSw = new LambdaQueryWrapper<>();
        conflictSw.between(EduSchedule::getScheduleDate, conflictStart, conflictEnd)
                .notIn(EduSchedule::getStatus, "CANCELLED", "SUSPENDED")
                .eq(EduSchedule::getDeleted, 0);
        List<EduSchedule> recentSchedules = scheduleMapper.selectList(conflictSw);

        // 检测教师冲突
        Set<String> reportedConflicts = new HashSet<>();
        for (int i = 0; i < recentSchedules.size() && conflictItems.size() < 3; i++) {
            EduSchedule s1 = recentSchedules.get(i);
            for (int j = i + 1; j < recentSchedules.size() && conflictItems.size() < 3; j++) {
                EduSchedule s2 = recentSchedules.get(j);
                if (!s1.getScheduleDate().equals(s2.getScheduleDate())) continue;
                if (s1.getStartTime() == null || s1.getEndTime() == null
                        || s2.getStartTime() == null || s2.getEndTime() == null) continue;

                boolean timeOverlap = s1.getStartTime().isBefore(s2.getEndTime())
                        && s2.getStartTime().isBefore(s1.getEndTime());
                if (!timeOverlap) continue;

                // 教师冲突
                if (s1.getTeacherId() != null && s1.getTeacherId().equals(s2.getTeacherId())) {
                    String key = "T" + s1.getTeacherId() + "_" + s1.getScheduleDate();
                    if (reportedConflicts.add(key)) {
                        conflictItems.add(TodosVO.ScheduleConflictItem.builder()
                                .description("教师时间冲突")
                                .date(s1.getScheduleDate().format(DATE_FMT))
                                .timeRange(s1.getStartTime().format(TIME_FMT) + "-" + s1.getEndTime().format(TIME_FMT))
                                .classA(getClassName(s1.getClassId()))
                                .classB(getClassName(s2.getClassId()))
                                .build());
                    }
                }
                // 教室冲突
                if (s1.getRoomId() != null && s1.getRoomId().equals(s2.getRoomId())) {
                    String key = "R" + s1.getRoomId() + "_" + s1.getScheduleDate();
                    if (reportedConflicts.add(key)) {
                        conflictItems.add(TodosVO.ScheduleConflictItem.builder()
                                .description("教室时间冲突")
                                .date(s1.getScheduleDate().format(DATE_FMT))
                                .timeRange(s1.getStartTime().format(TIME_FMT) + "-" + s1.getEndTime().format(TIME_FMT))
                                .classA(getClassName(s1.getClassId()))
                                .classB(getClassName(s2.getClassId()))
                                .build());
                    }
                }
            }
        }
        conflictCount = conflictItems.size();

        return TodosVO.builder()
                .pendingLeaves(TodosVO.TodoSection.<TodosVO.PendingLeaveItem>builder()
                        .count((int) leaveCount).items(leaveItems).build())
                .pendingRefunds(TodosVO.TodoSection.<TodosVO.PendingRefundItem>builder()
                        .count((int) refundCount).items(refundItems).build())
                .scheduleConflicts(TodosVO.TodoSection.<TodosVO.ScheduleConflictItem>builder()
                        .count((int) conflictCount).items(conflictItems).build())
                .build();
    }

    @Override
    public EnrollmentStatsVO getEnrollmentStats() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        LambdaQueryWrapper<EduEnrollment> ew = new LambdaQueryWrapper<>();
        ew.between(EduEnrollment::getEnrollmentDate, monthStart, today)
                .eq(EduEnrollment::getDeleted, 0);
        List<EduEnrollment> enrollments = enrollmentMapper.selectList(ew);

        // 按课程分组统计
        Map<Long, Long> courseCountMap = enrollments.stream()
                .collect(Collectors.groupingBy(EduEnrollment::getCourseId, Collectors.counting()));

        // 按报名人数降序排列
        List<Map.Entry<Long, Long>> sorted = courseCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        // 查询课程名称
        Set<Long> courseIds = sorted.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        final Map<Long, EduCourse> courseMap;
        if (!courseIds.isEmpty()) {
            courseMap = courseMapper.selectBatchIds(courseIds).stream()
                    .collect(Collectors.toMap(EduCourse::getId, c -> c));
        } else {
            courseMap = Collections.emptyMap();
        }

        List<EnrollmentStatsVO.CourseEnrollment> courses = sorted.stream().map(e -> {
            EduCourse course = courseMap.get(e.getKey());
            return EnrollmentStatsVO.CourseEnrollment.builder()
                    .courseId(e.getKey())
                    .courseName(course != null ? course.getName() : "--")
                    .enrollmentCount(e.getValue().intValue())
                    .build();
        }).collect(Collectors.toList());

        return EnrollmentStatsVO.builder()
                .monthLabel(today.getYear() + "年" + today.getMonthValue() + "月")
                .totalEnrollments(enrollments.size())
                .courses(courses)
                .build();
    }

    // ============ 辅助方法 ============

    private String getDisplayName(SysUser user) {
        if (user == null) return "--";
        if (user.getRealName() != null && !user.getRealName().isEmpty()) return user.getRealName();
        if (user.getNickName() != null && !user.getNickName().isEmpty()) return user.getNickName();
        return user.getUsername() != null ? user.getUsername() : "--";
    }

    private String getTeacherName(Long teacherId) {
        if (teacherId == null) return "--";
        SysUser user = sysUserMapper.selectById(teacherId);
        return user != null ? getDisplayName(user) : "--";
    }

    private String getStudentName(Long studentId) {
        if (studentId == null) return "--";
        EduStudent student = studentMapper.selectById(studentId);
        return student != null ? student.getName() : "--";
    }

    private String getClassName(Long classId) {
        if (classId == null) return "--";
        EduClass clazz = classMapper.selectById(classId);
        return clazz != null ? clazz.getName() : "--";
    }

    private String getDayOfWeekChinese(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "周一";
            case TUESDAY: return "周二";
            case WEDNESDAY: return "周三";
            case THURSDAY: return "周四";
            case FRIDAY: return "周五";
            case SATURDAY: return "周六";
            case SUNDAY: return "周日";
            default: return "";
        }
    }

    // 辅助类：年月比较
    private static class YearMonth {
        final int year, month;
        YearMonth(int y, int m) { year = y; month = m; }
        static YearMonth from(LocalDate d) { return new YearMonth(d.getYear(), d.getMonthValue()); }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof YearMonth)) return false;
            YearMonth ym = (YearMonth) o;
            return year == ym.year && month == ym.month;
        }
        @Override
        public int hashCode() { return year * 100 + month; }
    }
}
