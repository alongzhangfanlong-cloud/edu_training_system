package com.education.store.module.teacher.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.entity.EduLeaveStudent;
import com.education.store.module.attendance.mapper.EduAttendanceMapper;
import com.education.store.module.attendance.mapper.EduLeaveStudentMapper;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.entity.EduClassStudent;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.classinfo.mapper.EduClassStudentMapper;
import com.education.store.module.course.entity.EduClassroom;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduClassroomMapper;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.teaching.entity.EduTeacher;
import com.education.store.module.teaching.mapper.EduHomeworkSubmissionMapper;
import com.education.store.module.teaching.mapper.EduTeacherMapper;
import com.education.store.module.teacher.dashboard.model.*;
import com.education.store.module.teacher.dashboard.service.ITeacherDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 教师工作台 Service 实现
 */
@Service
@RequiredArgsConstructor
public class TeacherDashboardServiceImpl implements ITeacherDashboardService {

    private final EduScheduleMapper scheduleMapper;
    private final EduClassMapper classMapper;
    private final EduCourseMapper courseMapper;
    private final EduClassroomMapper classroomMapper;
    private final EduClassStudentMapper classStudentMapper;
    private final EduStudentMapper studentMapper;
    private final EduAttendanceMapper attendanceMapper;
    private final EduLeaveStudentMapper leaveStudentMapper;
    private final EduHomeworkSubmissionMapper homeworkSubmissionMapper;
    private final EduTeacherMapper teacherMapper;
    private final SysUserMapper sysUserMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public TeacherUserInfoVO getUserInfo() {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        LocalDate today = LocalDate.now();
        String dayOfWeek = getDayOfWeekChinese(today.getDayOfWeek());

        return TeacherUserInfoVO.builder()
                .userName(user != null && user.getRealName() != null ? user.getRealName()
                        : (user != null ? user.getUsername() : "--"))
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
    public TeacherStatisticsVO getStatistics() {
        Long teacherId = getTeacherId();
        LocalDate today = LocalDate.now();

        // 1. 今日课程数
        LambdaQueryWrapper<EduSchedule> sw = new LambdaQueryWrapper<>();
        sw.eq(EduSchedule::getTeacherId, teacherId)
                .eq(EduSchedule::getScheduleDate, today)
                .notIn(EduSchedule::getStatus, "CANCELLED", "SUSPENDED")
                .eq(EduSchedule::getDeleted, 0);
        List<EduSchedule> todaySchedules = scheduleMapper.selectList(sw);
        int todayCourseCount = todaySchedules.size();

        // 批量查询每个班级的学生数（供后续遍历复用，避免 N+1）
        Map<Long, Long> classStudentCountMap = new HashMap<>();
        if (!todaySchedules.isEmpty()) {
            Set<Long> statClassIds = todaySchedules.stream()
                    .map(EduSchedule::getClassId).collect(Collectors.toSet());
            if (!statClassIds.isEmpty()) {
                LambdaQueryWrapper<EduClassStudent> csAllWrapper = new LambdaQueryWrapper<>();
                csAllWrapper.in(EduClassStudent::getClassId, statClassIds)
                        .eq(EduClassStudent::getStatus, "ENROLLED")
                        .eq(EduClassStudent::getDeleted, 0);
                List<EduClassStudent> allClassStudents = classStudentMapper.selectList(csAllWrapper);
                classStudentCountMap = allClassStudents.stream()
                        .collect(Collectors.groupingBy(EduClassStudent::getClassId, Collectors.counting()));
            }
        }

        // 2. 待签到班级数（至少1人未签到的班级）
        int uncheckedClassCount = 0;
        Set<Long> checkedScheduleIds = new HashSet<>();
        if (!todaySchedules.isEmpty()) {
            List<Long> scheduleIds = todaySchedules.stream()
                    .map(EduSchedule::getId).collect(Collectors.toList());

            // 查询签到记录
            LambdaQueryWrapper<EduAttendance> attWrapper = new LambdaQueryWrapper<>();
            attWrapper.in(EduAttendance::getScheduleId, scheduleIds)
                    .eq(EduAttendance::getDeleted, 0);
            List<EduAttendance> attendances = attendanceMapper.selectList(attWrapper);
            Map<Long, Set<Long>> checkedStudentIdsBySchedule = new HashMap<>();
            Map<Long, Set<Long>> leaveStudentIdsBySchedule = new HashMap<>();
            for (EduAttendance a : attendances) {
                int status = a.getStatus() != null ? a.getStatus() : -1;
                if (status == EduAttendance.STATUS_PRESENT || status == EduAttendance.STATUS_LATE) {
                    checkedStudentIdsBySchedule
                            .computeIfAbsent(a.getScheduleId(), k -> new HashSet<>())
                            .add(a.getStudentId());
                } else if (status == EduAttendance.STATUS_LEAVE || status == EduAttendance.STATUS_LEAVE_PENDING) {
                    leaveStudentIdsBySchedule
                            .computeIfAbsent(a.getScheduleId(), k -> new HashSet<>())
                            .add(a.getStudentId());
                }
            }

            for (EduSchedule s : todaySchedules) {
                long totalStudents = classStudentCountMap.getOrDefault(s.getClassId(), 0L);
                Set<Long> checked = checkedStudentIdsBySchedule.getOrDefault(s.getId(), Collections.emptySet());
                // 签到基数 = 应到 - 请假（请假学生视为已处理，不算入"待签到"）
                Set<Long> leaveStudentIds = leaveStudentIdsBySchedule.getOrDefault(s.getId(), Collections.emptySet());
                long effectiveTotal = totalStudents - leaveStudentIds.size();
                if (effectiveTotal > 0 && checked.size() < effectiveTotal) {
                    uncheckedClassCount++;
                } else if (effectiveTotal > 0 && checked.size() >= effectiveTotal) {
                    checkedScheduleIds.add(s.getId());
                }
            }
        }

        // 3. 待评价学生人次
        // 查询该教师已完成但未评价的课程中涉及的学生数
        // 本期简化：统计该教师今日已完成且已全部签到的排课 × 对应班级学生数
        int pendingEvaluationCount = 0;
        for (EduSchedule s : todaySchedules) {
            if (checkedScheduleIds.contains(s.getId()) && s.getEndTime() != null
                    && LocalTime.now().isAfter(s.getEndTime())) {
                long count = classStudentCountMap.getOrDefault(s.getClassId(), 0L);
                pendingEvaluationCount += (int) count;
            }
        }

        return TeacherStatisticsVO.builder()
                .todayCourseCount(todayCourseCount)
                .todayUncheckedClassCount(uncheckedClassCount)
                .pendingEvaluationCount(pendingEvaluationCount)
                .build();
    }

    @Override
    public List<TeacherScheduleVO> getSchedule() {
        Long teacherId = getTeacherId();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        LambdaQueryWrapper<EduSchedule> sw = new LambdaQueryWrapper<>();
        sw.eq(EduSchedule::getTeacherId, teacherId)
                .eq(EduSchedule::getScheduleDate, today)
                .notIn(EduSchedule::getStatus, "CANCELLED", "SUSPENDED")
                .eq(EduSchedule::getDeleted, 0)
                .orderByAsc(EduSchedule::getStartTime);
        List<EduSchedule> schedules = scheduleMapper.selectList(sw);

        if (schedules.isEmpty()) {
            return Collections.emptyList();
        }

        // 预加载关联数据
        Set<Long> classIds = schedules.stream().map(EduSchedule::getClassId).collect(Collectors.toSet());
        Map<Long, EduClass> classMap = classMapper.selectBatchIds(classIds).stream()
                .collect(Collectors.toMap(EduClass::getId, c -> c));

        Set<Long> courseIds = classMap.values().stream().map(EduClass::getCourseId).collect(Collectors.toSet());
        Map<Long, EduCourse> courseMap = courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(EduCourse::getId, c -> c));

        Set<Long> roomIds = classMap.values().stream().map(EduClass::getClassroomId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, EduClassroom> roomMap = classroomMapper.selectBatchIds(roomIds).stream()
                .collect(Collectors.toMap(EduClassroom::getId, r -> r));

        // 查询签到数据
        List<Long> scheduleIds = schedules.stream().map(EduSchedule::getId).collect(Collectors.toList());
        LambdaQueryWrapper<EduAttendance> attWrapper = new LambdaQueryWrapper<>();
        attWrapper.in(EduAttendance::getScheduleId, scheduleIds)
                .eq(EduAttendance::getDeleted, 0);
        List<EduAttendance> attendances = attendanceMapper.selectList(attWrapper);
        Map<Long, List<EduAttendance>> attBySchedule = attendances.stream()
                .collect(Collectors.groupingBy(EduAttendance::getScheduleId));

        List<TeacherScheduleVO> result = new ArrayList<>();
        for (EduSchedule s : schedules) {
            EduClass clazz = classMap.get(s.getClassId());
            EduCourse course = clazz != null ? courseMap.get(clazz.getCourseId()) : null;
            EduClassroom room = clazz != null ? roomMap.get(clazz.getClassroomId()) : null;

            // 应到人数
            int expectedCount = clazz != null ? (clazz.getCurrentCount() != null ? clazz.getCurrentCount() : 0) : 0;

            // 签到状态
            List<EduAttendance> attList = attBySchedule.getOrDefault(s.getId(), Collections.emptyList());
            int checkedCount = (int) attList.stream()
                    .filter(a -> a.getStatus() != null
                            && (a.getStatus() == EduAttendance.STATUS_PRESENT || a.getStatus() == EduAttendance.STATUS_LATE))
                    .count();

            // 课程状态
            String courseStatus;
            if (s.getEndTime() != null && now.isAfter(s.getEndTime())) {
                courseStatus = "FINISHED";
            } else if (s.getStartTime() != null && s.getEndTime() != null
                    && !now.isBefore(s.getStartTime()) && !now.isAfter(s.getEndTime())) {
                courseStatus = "IN_PROGRESS";
            } else {
                courseStatus = "NOT_STARTED";
            }

            // 签到状态
            String attendanceStatus;
            if ("NOT_STARTED".equals(courseStatus)) {
                attendanceStatus = "NOT_STARTED";
            } else if (attList.isEmpty()) {
                attendanceStatus = "UNCHECKED";
            } else if (expectedCount > 0 && checkedCount < expectedCount) {
                attendanceStatus = "PARTIAL";
            } else {
                attendanceStatus = "COMPLETED";
            }

            result.add(TeacherScheduleVO.builder()
                    .scheduleId(s.getId())
                    .startTime(s.getStartTime() != null ? s.getStartTime().format(TIME_FMT) : null)
                    .endTime(s.getEndTime() != null ? s.getEndTime().format(TIME_FMT) : null)
                    .courseName(course != null ? course.getName() : "--")
                    .className(clazz != null ? clazz.getName() : "--")
                    .classroomName(room != null ? room.getName() : "未分配")
                    .expectedCount(expectedCount)
                    .checkedCount(checkedCount)
                    .courseStatus(courseStatus)
                    .attendanceStatus(attendanceStatus)
                    .build());
        }

        return result;
    }

    @Override
    public List<TeacherScheduleStudentVO> getScheduleStudents(Long scheduleId) {
        if (scheduleId == null) {
            return Collections.emptyList();
        }
        Long teacherId = getTeacherId();

        // 校验排课属于当前教师
        EduSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null || !teacherId.equals(schedule.getTeacherId())) {
            return Collections.emptyList();
        }

        // 查询班级学生
        LambdaQueryWrapper<EduClassStudent> csWrapper = new LambdaQueryWrapper<>();
        csWrapper.eq(EduClassStudent::getClassId, schedule.getClassId())
                .eq(EduClassStudent::getStatus, "ENROLLED")
                .eq(EduClassStudent::getDeleted, 0);
        List<EduClassStudent> classStudents = classStudentMapper.selectList(csWrapper);

        if (classStudents.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询学生信息
        List<Long> studentIds = classStudents.stream()
                .map(EduClassStudent::getStudentId).collect(Collectors.toList());
        Map<Long, EduStudent> studentMap = studentMapper.selectBatchIds(studentIds).stream()
                .collect(Collectors.toMap(EduStudent::getId, s -> s));

        // 查询签到记录
        LambdaQueryWrapper<EduAttendance> attWrapper = new LambdaQueryWrapper<>();
        attWrapper.eq(EduAttendance::getScheduleId, scheduleId)
                .eq(EduAttendance::getDeleted, 0);
        List<EduAttendance> attendances = attendanceMapper.selectList(attWrapper);
        Map<Long, Integer> attStatusMap = attendances.stream()
                .collect(Collectors.toMap(
                        EduAttendance::getStudentId,
                        EduAttendance::getStatus,
                        (existing, replacement) -> replacement));

        List<TeacherScheduleStudentVO> result = new ArrayList<>();
        for (EduClassStudent cs : classStudents) {
            EduStudent student = studentMap.get(cs.getStudentId());
            Integer attStatus = attStatusMap.get(cs.getStudentId());
            String attStatusStr = null;
            if (attStatus != null) {
                if (attStatus == EduAttendance.STATUS_PRESENT) {
                    attStatusStr = "PRESENT";
                } else if (attStatus == EduAttendance.STATUS_LATE) {
                    attStatusStr = "LATE";
                } else if (attStatus == EduAttendance.STATUS_ABSENT) {
                    attStatusStr = "ABSENT";
                } else if (attStatus == EduAttendance.STATUS_LEAVE) {
                    attStatusStr = "LEAVE";
                } else if (attStatus == EduAttendance.STATUS_LEAVE_PENDING) {
                    attStatusStr = "LEAVE_PENDING";
                }
            }
            result.add(TeacherScheduleStudentVO.builder()
                    .studentId(cs.getStudentId())
                    .studentName(student != null ? student.getName() : "--")
                    .attendanceStatus(attStatusStr)
                    .build());
        }

        return result;
    }

    @Override
    public TeacherTodosVO getTodos() {
        Long teacherId = getTeacherId();

        // 1. 待批改作业份数
        int pendingGradingCount = homeworkSubmissionMapper.countPendingGrading(teacherId);

        // 2. 待回复请假条数（PENDING=0）
        LambdaQueryWrapper<EduLeaveStudent> leaveWrapper = new LambdaQueryWrapper<>();
        leaveWrapper.eq(EduLeaveStudent::getConfirmTeacherId, teacherId)
                .eq(EduLeaveStudent::getStatus, 0)
                .eq(EduLeaveStudent::getDeleted, 0);
        long pendingLeaveReplyCount = leaveStudentMapper.selectCount(leaveWrapper);

        return TeacherTodosVO.builder()
                .pendingGradingCount(pendingGradingCount)
                .pendingLeaveReplyCount((int) pendingLeaveReplyCount)
                .build();
    }

    // ============ 辅助方法 ============

    /**
     * 获取当前登录用户对应的教师ID
     * edu_schedule.teacher_id 关联的是 edu_teacher.id，而非 sys_user.id
     */
    private Long getTeacherId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("无法获取当前用户信息");
        }
        LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacher::getUserId, userId)
                .eq(EduTeacher::getDeleted, 0)
                .last("LIMIT 1");
        EduTeacher teacher = teacherMapper.selectOne(wrapper);
        if (teacher == null) {
            throw new RuntimeException("当前用户未关联教师档案");
        }
        return teacher.getId();
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
}
