package com.education.store.module.dashboard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.attendance.entity.EduAttendance;
import com.education.store.module.attendance.mapper.EduAttendanceMapper;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.entity.EduStudentParent;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.student.mapper.EduStudentParentMapper;
import com.education.store.module.teaching.entity.EduEvaluation;
import com.education.store.module.teaching.entity.EduEvaluationStudent;
import com.education.store.module.teaching.entity.EduExamScore;
import com.education.store.module.teaching.entity.EduHomework;
import com.education.store.module.teaching.mapper.EduEvaluationMapper;
import com.education.store.module.teaching.mapper.EduEvaluationStudentMapper;
import com.education.store.module.teaching.mapper.EduExamScoreMapper;
import com.education.store.module.teaching.mapper.EduHomeworkMapper;
import com.education.store.module.classinfo.entity.EduClassStudent;
import com.education.store.module.classinfo.mapper.EduClassStudentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 家长端首页动态聚合 API — 签到+评价+作业+成绩混合时间线
 *
 * <p>多孩家庭按孩子分组，每组内按时间倒序排列，默认最近 30 天。
 */
@Tag(name = "家长端-首页")
@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentTimelineController {

    private final EduStudentParentMapper parentMapper;
    private final EduStudentMapper studentMapper;
    private final EduAttendanceMapper attendanceMapper;
    private final EduExamScoreMapper examScoreMapper;
    private final EduHomeworkMapper homeworkMapper;
    private final EduEvaluationMapper evaluationMapper;
    private final EduEvaluationStudentMapper evaluationStudentMapper;
    private final EduClassStudentMapper classStudentMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    @Operation(summary = "首页动态聚合时间线（多孩分组，最近30天）")
    @GetMapping("/timeline")
    public Result<Map<String, Object>> timeline(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "20") int limit) {
        Long userId = SecurityUtils.getUserId();

        // 获取家长的所有孩子
        List<EduStudentParent> relations = parentMapper.selectList(
                new LambdaQueryWrapper<EduStudentParent>().eq(EduStudentParent::getUserId, userId));
        if (relations.isEmpty()) {
            return Result.success(Map.of("children", Collections.emptyList()));
        }

        List<Long> studentIds = relations.stream()
                .map(EduStudentParent::getStudentId).distinct().collect(Collectors.toList());
        List<EduStudent> students = studentMapper.selectBatchIds(studentIds);
        Map<Long, EduStudent> studentMap = students.stream()
                .collect(Collectors.toMap(EduStudent::getId, s -> s, (a, b) -> a));

        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<Map<String, Object>> children = new ArrayList<>();
        for (EduStudent student : students) {
            List<Map<String, Object>> events = new ArrayList<>();

            // 1. 签到事件
            List<EduAttendance> attendances = attendanceMapper.selectList(
                    new LambdaQueryWrapper<EduAttendance>()
                            .eq(EduAttendance::getStudentId, student.getId())
                            .ge(EduAttendance::getCreateTime, since)
                            .orderByDesc(EduAttendance::getCreateTime)
                            .last("LIMIT " + limit));
            for (EduAttendance a : attendances) {
                events.add(Map.of(
                        "type", "SIGN_IN",
                        "time", fmt(a.getCreateTime()),
                        "sortTime", toString(a.getCreateTime()),
                        "summary", statusLabel(a.getStatus()),
                        "data", Map.of(
                                "status", a.getStatus(),
                                "signinTime", fmt(a.getSigninTime()),
                                "scheduleId", a.getScheduleId()
                        )
                ));
            }

            // 2. 课堂评价事件（通过评价明细关联）
            List<EduEvaluationStudent> evalStudents = evaluationStudentMapper.selectList(
                    new LambdaQueryWrapper<EduEvaluationStudent>()
                            .eq(EduEvaluationStudent::getStudentId, student.getId())
                            .ge(EduEvaluationStudent::getCreateTime, since)
                            .orderByDesc(EduEvaluationStudent::getCreateTime)
                            .last("LIMIT " + limit));
            if (!evalStudents.isEmpty()) {
                List<Long> evalIds = evalStudents.stream()
                        .map(EduEvaluationStudent::getEvaluationId).distinct().collect(Collectors.toList());
                Map<Long, EduEvaluation> evalMap = evaluationMapper.selectBatchIds(evalIds).stream()
                        .collect(Collectors.toMap(EduEvaluation::getId, e -> e, (a, b) -> a));
                for (EduEvaluationStudent es : evalStudents) {
                    EduEvaluation eval = evalMap.get(es.getEvaluationId());
                    String summary = "课堂表现";
                    if (es.getStarRating() != null) {
                        summary += " " + stars(es.getStarRating().intValue());
                    }
                    if (eval != null && eval.getSummary() != null && !eval.getSummary().isEmpty()) {
                        String s = eval.getSummary();
                        summary += " " + (s.length() > 30 ? s.substring(0, 30) + "…" : s);
                    }
                    events.add(Map.of(
                            "type", "EVALUATION",
                            "time", fmt(es.getCreateTime()),
                            "sortTime", toString(es.getCreateTime()),
                            "summary", summary,
                            "data", Map.of(
                                    "evaluationId", es.getEvaluationId(),
                                    "scheduleId", eval != null ? Objects.requireNonNullElse(eval.getScheduleId(), 0L) : 0L,
                                    "starRating", es.getStarRating() != null ? es.getStarRating().toString() : "",
                                    "comment", Objects.requireNonNullElse(es.getComment(), "")
                            )
                    ));
                }
            }

            // 3. 作业事件（通过班级关联）
            List<EduClassStudent> csList = classStudentMapper.selectList(
                    new LambdaQueryWrapper<EduClassStudent>()
                            .eq(EduClassStudent::getStudentId, student.getId()));
            if (!csList.isEmpty()) {
                List<Long> classIds = csList.stream().map(EduClassStudent::getClassId).distinct().collect(Collectors.toList());
                List<EduHomework> homeworks = homeworkMapper.selectList(
                        new LambdaQueryWrapper<EduHomework>()
                                .in(EduHomework::getClassId, classIds)
                                .ge(EduHomework::getCreateTime, since)
                                .orderByDesc(EduHomework::getCreateTime)
                                .last("LIMIT " + limit));
                for (EduHomework h : homeworks) {
                    String content = h.getTitle() != null ? h.getTitle() : h.getContent();
                    String preview = content != null && content.length() > 30
                            ? content.substring(0, 30) + "…" : content;
                    events.add(Map.of(
                            "type", "HOMEWORK",
                            "time", fmt(h.getCreateTime()),
                            "sortTime", toString(h.getCreateTime()),
                            "summary", "作业：" + Objects.requireNonNullElse(preview, ""),
                            "data", Map.of(
                                    "id", h.getId(),
                                    "title", Objects.requireNonNullElse(h.getTitle(), ""),
                                    "content", Objects.requireNonNullElse(h.getContent(), ""),
                                    "classId", h.getClassId()
                            )
                    ));
                }
            }

            // 4. 成绩事件（EduExamScore 为班级级表，无 studentId 直接关联，跳过避免报错；家长端已通过独立 exam-scores 接口查成绩）
            // try { /* 通过班级-学员-考试分数关联表查询，此处容错跳过 */ } catch (Exception e) { /* skip */ }

            // 按 sortTime 倒序，取前 limit 条
            events.sort((a, b) -> ((String) b.get("sortTime")).compareTo((String) a.get("sortTime")));
            if (events.size() > limit) {
                events = events.subList(0, limit);
            }

            Map<String, Object> child = new LinkedHashMap<>();
            child.put("studentId", student.getId());
            child.put("name", student.getName());
            child.put("grade", student.getGrade());
            child.put("events", events);
            children.add(child);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("children", children);
        return Result.success(result);
    }

    // ======== 辅助方法 ========

    private static String fmt(LocalDateTime dt) {
        return dt != null ? dt.format(FMT) : "";
    }

    private static String toString(LocalDateTime dt) {
        return dt != null ? dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
    }

    private static String statusLabel(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case EduAttendance.STATUS_PRESENT -> "已到校";
            case EduAttendance.STATUS_LATE -> "迟到";
            case EduAttendance.STATUS_ABSENT -> "缺勤";
            case EduAttendance.STATUS_LEAVE -> "已请假";
            default -> "";
        };
    }

    private static String stars(int n) {
        return "⭐".repeat(Math.max(1, Math.min(5, n)));
    }
}
