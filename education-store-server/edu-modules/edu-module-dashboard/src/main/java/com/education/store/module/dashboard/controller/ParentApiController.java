package com.education.store.module.dashboard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.entity.EduLeaveStudent;
import com.education.store.module.attendance.mapper.EduAttendanceMapper;
import com.education.store.module.attendance.mapper.EduLeaveStudentMapper;
import com.education.store.module.classinfo.entity.EduClassStudent;
import com.education.store.module.classinfo.mapper.EduClassStudentMapper;
import com.education.store.module.course.entity.EduClassroom;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduClassroomMapper;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.enrollment.entity.EduEnrollment;
import com.education.store.module.enrollment.mapper.EduEnrollmentMapper;
import com.education.store.module.points.entity.EduPointsAccount;
import com.education.store.module.points.entity.EduPointsLog;
import com.education.store.module.points.mapper.EduPointsAccountMapper;
import com.education.store.module.points.mapper.EduPointsLogMapper;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.student.entity.EduStudentParent;
import com.education.store.module.student.mapper.EduStudentParentMapper;
import com.education.store.module.teaching.entity.EduEvaluation;
import com.education.store.module.teaching.entity.EduEvaluationStudent;
import com.education.store.module.teaching.entity.EduExamScore;
import com.education.store.module.teaching.entity.EduHomework;
import com.education.store.module.teaching.mapper.EduEvaluationMapper;
import com.education.store.module.teaching.mapper.EduEvaluationStudentMapper;
import com.education.store.module.teaching.mapper.EduExamScoreMapper;
import com.education.store.module.teaching.mapper.EduHomeworkMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 家长端跨模块 API — 聚合查询孩子的考勤/作业/请假/报名/课表/成绩/评价/积分
 *
 * <p>放在 edu-module-dashboard 是因为需要跨模块访问多个 Mapper，
 * 而 edu-module-student 如果引入全部子模块会造成循环依赖。
 *
 * <p>基础的孩子列表/详情仍由 edu-module-student 的 {@code ParentController} 提供。
 */
@Slf4j
@Tag(name = "家长端-跨模块")
@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentApiController {

    private static final int MAX_PAGE_SIZE = 100;

    // ======== 跨模块 Mapper ========
    private final EduAttendanceMapper attendanceMapper;
    private final EduLeaveStudentMapper leaveStudentMapper;
    private final EduHomeworkMapper homeworkMapper;
    private final EduEnrollmentMapper enrollmentMapper;
    private final EduScheduleMapper scheduleMapper;
    private final EduExamScoreMapper examScoreMapper;
    private final EduEvaluationMapper evaluationMapper;
    private final EduEvaluationStudentMapper evaluationStudentMapper;
    private final EduClassStudentMapper classStudentMapper;
    private final EduCourseMapper courseMapper;
    private final EduClassroomMapper classroomMapper;
    private final EduStudentParentMapper parentMapper;
    private final EduPointsAccountMapper pointsAccountMapper;
    private final EduPointsLogMapper pointsLogMapper;

    // ================================================================
    //  考勤
    // ================================================================

    @Operation(summary = "孩子考勤记录（分页，支持月份/日期筛选）")
    @GetMapping("/children/{studentId}/attendance")
    public Result<PageResult<Map<String, Object>>> attendance(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) String date) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        page = Math.max(1, page);
        pageSize = normalizePageSize(pageSize);

        LambdaQueryWrapper<EduAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduAttendance::getStudentId, studentId);
        if (date != null && !date.isEmpty()) {
            // 校验日期格式，防止SQL注入
            try {
                LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                throw new BusinessException("日期格式错误，应为yyyy-MM-dd");
            }
            wrapper.apply("DATE(create_time) = {0}", date);
        } else if (yearMonth != null && !yearMonth.isEmpty()) {
            if (!yearMonth.matches("^\\d{4}-\\d{2}$")) {
                throw new BusinessException("月份格式错误，应为yyyy-MM");
            }
            wrapper.apply("DATE_FORMAT(create_time, '%Y-%m') = {0}", yearMonth);
        }
        wrapper.orderByDesc(EduAttendance::getCreateTime);

        Page<EduAttendance> p = new Page<>(page, pageSize);
        Page<EduAttendance> result = attendanceMapper.selectPage(p, wrapper);

        List<Map<String, Object>> records = result.getRecords().stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("scheduleId", a.getScheduleId());
            m.put("classId", a.getClassId());
            m.put("status", a.getStatus());
            m.put("statusText", statusText(a.getStatus()));
            m.put("signinTime", a.getSigninTime());
            m.put("remark", a.getRemark());
            m.put("createTime", a.getCreateTime());
            m.put("attendanceDate", a.getCreateTime() != null ? a.getCreateTime().toLocalDate() : null);
            m.put("date", a.getCreateTime() != null ? a.getCreateTime().toLocalDate().toString() : null);
            return m;
        }).collect(Collectors.toList());

        return Result.success(PageResult.of(records, result.getTotal(), page, pageSize));
    }

    @Operation(summary = "孩子本月考勤统计")
    @GetMapping("/children/{studentId}/attendance-stats")
    public Result<Map<String, Object>> attendanceStats(@PathVariable Long studentId) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        YearMonth ym = YearMonth.now();
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<EduAttendance> list = attendanceMapper.selectList(
                new LambdaQueryWrapper<EduAttendance>()
                        .eq(EduAttendance::getStudentId, studentId)
                        .ge(EduAttendance::getCreateTime, start.atStartOfDay())
                        .le(EduAttendance::getCreateTime, end.plusDays(1).atStartOfDay()));

        long present = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == EduAttendance.STATUS_PRESENT).count();
        long late = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == EduAttendance.STATUS_LATE).count();
        long absent = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == EduAttendance.STATUS_ABSENT).count();
        long leave = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == EduAttendance.STATUS_LEAVE).count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("yearMonth", ym.toString());
        stats.put("total", list.size());
        stats.put("present", present);
        stats.put("late", late);
        stats.put("absent", absent);
        stats.put("leave", leave);
        long validDays = present + late + absent + leave;
        stats.put("attendanceRate", validDays == 0 ? 0 :
                Math.round((present + late) * 100.0 / validDays));

        return Result.success(stats);
    }

    // ================================================================
    //  作业
    // ================================================================

    @Operation(summary = "孩子作业列表（通过班级关联查询，分页）")
    @GetMapping("/children/{studentId}/homework")
    public Result<PageResult<Map<String, Object>>> homework(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        page = Math.max(1, page);
        pageSize = normalizePageSize(pageSize);

        List<Long> classIds = getStudentClassIds(studentId);
        if (classIds.isEmpty()) {
            return Result.success(PageResult.of(Collections.emptyList(), 0, page, pageSize));
        }

        Page<EduHomework> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<EduHomework> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EduHomework::getClassId, classIds)
                .orderByDesc(EduHomework::getCreateTime);
        Page<EduHomework> result = homeworkMapper.selectPage(p, wrapper);

        List<Map<String, Object>> records = result.getRecords().stream().map(h -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", h.getId());
            m.put("classId", h.getClassId());
            m.put("courseId", h.getCourseId());
            m.put("title", h.getTitle());
            m.put("content", h.getContent());
            m.put("teacherId", h.getTeacherId());
            m.put("createTime", h.getCreateTime());
            return m;
        }).collect(Collectors.toList());

        return Result.success(PageResult.of(records, result.getTotal(), page, pageSize));
    }

    // ================================================================
    //  请假
    // ================================================================

    @Operation(summary = "孩子请假记录")
    @GetMapping("/children/{studentId}/leaves")
    public Result<List<Map<String, Object>>> leaves(@PathVariable Long studentId) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        List<EduLeaveStudent> list = leaveStudentMapper.selectList(
                new LambdaQueryWrapper<EduLeaveStudent>()
                        .eq(EduLeaveStudent::getStudentId, studentId)
                        .orderByDesc(EduLeaveStudent::getCreateTime));

        List<Map<String, Object>> records = list.stream().map(l -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", l.getId());
            m.put("leaveDate", l.getLeaveDate());
            m.put("leaveType", l.getLeaveType());
            m.put("reason", l.getReason());
            m.put("status", l.getStatus());
            m.put("rejectReason", l.getRejectReason());
            m.put("createTime", l.getCreateTime());
            return m;
        }).collect(Collectors.toList());
        return Result.success(records);
    }

    @Operation(summary = "家长帮孩子提交请假申请")
    @PostMapping("/children/{studentId}/leaves")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> submitLeave(@PathVariable Long studentId,
            @RequestBody Map<String, Object> body) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        if (body == null) {
            throw new BusinessException("请求体不能为空");
        }

        Object leaveDateObj = body.get("leaveDate");
        Object leaveTypeObj = body.get("leaveType");
        if (leaveDateObj == null || leaveDateObj.toString().isBlank()) {
            throw new BusinessException("请假日期不能为空");
        }
        if (leaveTypeObj == null || leaveTypeObj.toString().isBlank()) {
            throw new BusinessException("请假类型不能为空");
        }

        LocalDate leaveDate;
        int leaveType;
        try {
            leaveDate = LocalDate.parse(leaveDateObj.toString());
        } catch (DateTimeParseException e) {
            throw new BusinessException("请假日期格式错误，应为yyyy-MM-dd");
        }
        try {
            leaveType = Integer.parseInt(leaveTypeObj.toString());
        } catch (NumberFormatException e) {
            throw new BusinessException("请假类型必须是数字");
        }

        if (leaveDate.isBefore(LocalDate.now())) {
            throw new BusinessException("请假日期不能早于今天");
        }

        String reason = body.get("reason") != null ? body.get("reason").toString() : "";
        if (reason.length() > 500) {
            throw new BusinessException("请假原因不能超过500个字符");
        }

        EduLeaveStudent leave = new EduLeaveStudent();
        leave.setStudentId(studentId);
        leave.setLeaveDate(leaveDate);
        leave.setLeaveType(leaveType);
        leave.setReason(reason);
        leave.setStatus(0);
        leave.setApplicantType("PARENT");
        leave.setApplicantId(userId);
        leaveStudentMapper.insert(leave);

        log.info("家长提交请假申请: userId={}, studentId={}, leaveDate={}, leaveId={}",
                userId, studentId, leaveDate, leave.getId());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", leave.getId());
        result.put("status", leave.getStatus());
        return Result.success(result);
    }

    // ================================================================
    //  报名记录
    // ================================================================

    @Operation(summary = "孩子报名记录")
    @GetMapping("/children/{studentId}/enrollments")
    public Result<List<Map<String, Object>>> enrollments(@PathVariable Long studentId) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        List<EduEnrollment> list = enrollmentMapper.selectList(
                new LambdaQueryWrapper<EduEnrollment>()
                        .eq(EduEnrollment::getStudentId, studentId)
                        .orderByDesc(EduEnrollment::getCreateTime));

        if (list.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        List<Long> courseIds = list.stream()
                .map(EduEnrollment::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> courseNameMap = courseIds.isEmpty() ? Collections.emptyMap() :
                courseMapper.selectBatchIds(courseIds).stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(EduCourse::getId, EduCourse::getName, (a, b) -> a));

        List<Map<String, Object>> records = list.stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId());
            m.put("courseId", e.getCourseId());
            m.put("courseName", courseNameMap.getOrDefault(e.getCourseId(), ""));
            m.put("classId", e.getClassId());
            m.put("amount", e.getAmount());
            m.put("enrollmentDate", e.getEnrollmentDate());
            m.put("paymentStatus", e.getPaymentStatus());
            m.put("remark", e.getRemark());
            return m;
        }).collect(Collectors.toList());
        return Result.success(records);
    }

    // ================================================================
    //  课表
    // ================================================================

    @Operation(summary = "孩子课表（未来排课，按日期升序）")
    @GetMapping("/children/{studentId}/schedule")
    public Result<List<Map<String, Object>>> schedule(
            @PathVariable Long studentId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        List<Long> classIds = getStudentClassIds(studentId);
        if (classIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        LambdaQueryWrapper<EduSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EduSchedule::getClassId, classIds)
                .eq(EduSchedule::getStatus, "NORMAL");

        LocalDate start = LocalDate.now();
        LocalDate end = null;
        if (startDate != null && !startDate.isEmpty()) {
            try {
                start = LocalDate.parse(startDate);
            } catch (DateTimeParseException e) {
                throw new BusinessException("开始日期格式错误，应为yyyy-MM-dd");
            }
        }
        wrapper.ge(EduSchedule::getScheduleDate, start);

        if (endDate != null && !endDate.isEmpty()) {
            try {
                end = LocalDate.parse(endDate);
            } catch (DateTimeParseException e) {
                throw new BusinessException("结束日期格式错误，应为yyyy-MM-dd");
            }
            wrapper.le(EduSchedule::getScheduleDate, end);
        }
        wrapper.orderByAsc(EduSchedule::getScheduleDate).orderByAsc(EduSchedule::getStartTime);

        List<EduSchedule> list = scheduleMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        List<Long> courseIds = list.stream()
                .map(EduSchedule::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> courseNameMap = courseIds.isEmpty() ? Collections.emptyMap() :
                courseMapper.selectBatchIds(courseIds).stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(EduCourse::getId, EduCourse::getName, (a, b) -> a));

        List<Long> roomIds = list.stream()
                .map(EduSchedule::getRoomId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> roomNameMap = roomIds.isEmpty() ? Collections.emptyMap() :
                classroomMapper.selectBatchIds(roomIds).stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(EduClassroom::getId, EduClassroom::getName, (a, b) -> a));

        List<Map<String, Object>> records = list.stream().map(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", s.getId());
            m.put("classId", s.getClassId());
            m.put("courseId", s.getCourseId());
            m.put("courseName", courseNameMap.getOrDefault(s.getCourseId(), ""));
            m.put("teacherId", s.getTeacherId());
            m.put("roomId", s.getRoomId());
            m.put("roomName", roomNameMap.getOrDefault(s.getRoomId(), ""));
            m.put("scheduleDate", s.getScheduleDate());
            m.put("date", s.getScheduleDate() != null ? s.getScheduleDate().toString() : null);
            m.put("startTime", s.getStartTime());
            m.put("endTime", s.getEndTime());
            return m;
        }).collect(Collectors.toList());

        return Result.success(records);
    }

    // ================================================================
    //  考试成绩
    // ================================================================

    @Operation(summary = "孩子考试成绩")
    @GetMapping("/children/{studentId}/exam-scores")
    public Result<List<Map<String, Object>>> examScores(@PathVariable Long studentId) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        // 先通过关联表查询学生的exam_score_id列表，避免SQL拼接风险
        List<EduExamScore> list;
        try {
            list = examScoreMapper.selectList(
                    new LambdaQueryWrapper<EduExamScore>()
                            .apply("id IN (SELECT exam_score_id FROM edu_exam_score_student WHERE student_id = {0} AND deleted = 0)", studentId)
                            .orderByDesc(EduExamScore::getExamDate));
        } catch (Exception e) {
            log.warn("查询考试成绩失败: studentId={}", studentId, e);
            return Result.success(Collections.emptyList());
        }

        List<Map<String, Object>> records = list.stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId());
            m.put("examName", e.getExamName());
            m.put("examType", e.getExamType());
            m.put("classId", e.getClassId());
            m.put("subjectId", e.getSubjectId());
            m.put("totalScore", e.getTotalScore());
            m.put("examDate", e.getExamDate());
            return m;
        }).collect(Collectors.toList());
        return Result.success(records);
    }

    // ================================================================
    //  教评学（课堂评价）
    // ================================================================

    @Operation(summary = "孩子收到的课堂评价")
    @GetMapping("/children/{studentId}/evaluations")
    public Result<List<Map<String, Object>>> evaluations(@PathVariable Long studentId) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        List<EduEvaluationStudent> evalStudents = evaluationStudentMapper.selectList(
                new LambdaQueryWrapper<EduEvaluationStudent>()
                        .eq(EduEvaluationStudent::getStudentId, studentId)
                        .orderByDesc(EduEvaluationStudent::getCreateTime));

        if (evalStudents.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        List<Long> evalIds = evalStudents.stream()
                .map(EduEvaluationStudent::getEvaluationId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, EduEvaluation> evalMap = evalIds.isEmpty() ? Collections.emptyMap() :
                evaluationMapper.selectBatchIds(evalIds).stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(EduEvaluation::getId, e -> e, (a, b) -> a));

        List<Map<String, Object>> records = evalStudents.stream().map(es -> {
            EduEvaluation eval = evalMap.get(es.getEvaluationId());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("evaluationId", es.getEvaluationId());
            m.put("scheduleId", eval != null ? eval.getScheduleId() : null);
            m.put("classId", eval != null ? eval.getClassId() : null);
            m.put("summary", eval != null ? eval.getSummary() : "");
            m.put("starRating", es.getStarRating());
            m.put("comment", es.getComment());
            m.put("createTime", es.getCreateTime());
            return m;
        }).collect(Collectors.toList());

        return Result.success(records);
    }

    // ================================================================
    //  积分
    // ================================================================

    @Operation(summary = "孩子积分余额")
    @GetMapping("/children/{studentId}/points")
    public Result<Map<String, Object>> points(@PathVariable Long studentId) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        EduPointsAccount account = pointsAccountMapper.selectOne(
                new LambdaQueryWrapper<EduPointsAccount>()
                        .eq(EduPointsAccount::getStudentId, studentId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("studentId", studentId);
        result.put("balance", account != null ? account.getBalance() : 0);
        result.put("totalEarned", account != null ? account.getTotalEarned() : 0);
        return Result.success(result);
    }

    @Operation(summary = "孩子积分日志")
    @GetMapping("/children/{studentId}/points/log")
    public Result<List<Map<String, Object>>> pointsLog(@PathVariable Long studentId) {
        Long userId = SecurityUtils.getUserId();
        requireParentOf(userId, studentId);

        List<EduPointsLog> list = pointsLogMapper.selectList(
                new LambdaQueryWrapper<EduPointsLog>()
                        .eq(EduPointsLog::getStudentId, studentId)
                        .orderByDesc(EduPointsLog::getCreateTime)
                        .last("LIMIT 200")); // 限制最多返回200条，防止数据量过大

        List<Map<String, Object>> records = list.stream().map(l -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", l.getId());
            m.put("changeType", l.getChangeType());
            m.put("amount", l.getAmount());
            m.put("balanceAfter", l.getBalanceAfter());
            m.put("sourceType", l.getSourceType());
            m.put("remark", l.getRemark());
            m.put("createTime", l.getCreateTime());
            return m;
        }).collect(Collectors.toList());
        return Result.success(records);
    }

    // ================================================================
    //  辅助方法
    // ================================================================

    private long normalizePageSize(long pageSize) {
        if (pageSize < 1) return 10;
        if (pageSize > MAX_PAGE_SIZE) return MAX_PAGE_SIZE;
        return pageSize;
    }

    /** 获取学生在读班级 ID 列表 */
    private List<Long> getStudentClassIds(Long studentId) {
        return classStudentMapper.selectList(
                new LambdaQueryWrapper<EduClassStudent>()
                        .eq(EduClassStudent::getStudentId, studentId)
                        .eq(EduClassStudent::getStatus, "ENROLLED"))
                .stream()
                .filter(Objects::nonNull)
                .map(EduClassStudent::getClassId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /** 校验亲子关系（管理员可查看所有） */
    private void requireParentOf(Long userId, Long studentId) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        if (userId == null || studentId == null) {
            throw new BusinessException("参数错误");
        }
        if (!isParentOf(userId, studentId)) {
            log.warn("越权访问: userId={}尝试访问studentId={}", userId, studentId);
            throw new BusinessException("无权查看该学员信息");
        }
    }

    private boolean isParentOf(Long userId, Long studentId) {
        Long count = parentMapper.selectCount(
                new LambdaQueryWrapper<EduStudentParent>()
                        .eq(EduStudentParent::getUserId, userId)
                        .eq(EduStudentParent::getStudentId, studentId));
        return count != null && count > 0;
    }

    private static String statusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case EduAttendance.STATUS_PRESENT -> "到校";
            case EduAttendance.STATUS_LATE -> "迟到";
            case EduAttendance.STATUS_ABSENT -> "缺勤";
            case EduAttendance.STATUS_LEAVE -> "请假";
            default -> "未知";
        };
    }
}
