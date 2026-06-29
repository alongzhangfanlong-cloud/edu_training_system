package com.edu.modules.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final JdbcTemplate jdbc;

    public Map<String, Object> dashboard(String dateRange) {
        LocalDate today = LocalDate.now();

        // 今日课程数
        Long todayClasses = jdbc.queryForObject(
            "SELECT COUNT(*) FROM schedules WHERE date = ? AND status = 1", Long.class, today);

        // 今日上课学员
        Long todayStudents = jdbc.queryForObject(
            "SELECT COUNT(DISTINCT student_id) FROM attendances WHERE schedule_id IN " +
            "(SELECT id FROM schedules WHERE date = ?) AND status = 1", Long.class, today);

        // 今日营收
        BigDecimal todayRevenue = jdbc.queryForObject(
            "SELECT COALESCE(SUM(pay_amount), 0) FROM orders WHERE pay_status = 1 AND DATE(pay_time) = ?",
            BigDecimal.class, today);

        // 本月新增学员
        Long newStudents = jdbc.queryForObject(
            "SELECT COUNT(*) FROM students WHERE created_at >= DATE_FORMAT(NOW(), '%Y-%m-01') AND is_deleted = 0",
            Long.class);

        // 出勤率
        Long totalAtt = jdbc.queryForObject(
            "SELECT COUNT(*) FROM attendances WHERE DATE(created_at) = ?", Long.class, today);
        Long presentAtt = jdbc.queryForObject(
            "SELECT COUNT(*) FROM attendances WHERE DATE(created_at) = ? AND status = 1", Long.class, today);
        double attendanceRate = totalAtt != null && totalAtt > 0
            ? Math.round(presentAtt * 10000.0 / totalAtt) / 100.0 : 95.0;

        // 课时预警学员数
        Long warningCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM student_classes WHERE remain_hours <= 5 AND remain_hours > 0 AND status = 1",
            Long.class);

        // 待审批请假
        Long pendingLeaves = jdbc.queryForObject(
            "SELECT COUNT(*) FROM attendances WHERE leave_approve_status = 0", Long.class);

        // 营收趋势 (近30天)
        List<Map<String, Object>> revenueTrend = jdbc.queryForList(
            "SELECT DATE(pay_time) as date, COALESCE(SUM(pay_amount), 0) as amount " +
            "FROM orders WHERE pay_status = 1 AND pay_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY DATE(pay_time) ORDER BY date");

        // 出勤率趋势 (近30天)
        List<Map<String, Object>> attTrend = jdbc.queryForList(
            "SELECT a.date, ROUND(SUM(CASE WHEN att.status = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1) as rate " +
            "FROM (SELECT DATE(created_at) as date, status FROM attendances WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)) att " +
            "RIGHT JOIN (SELECT DATE_SUB(CURDATE(), INTERVAL n DAY) as date FROM " +
            "(SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 " +
            "UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 " +
            "UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 " +
            "UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 " +
            "UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 " +
            "UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29) nums) a ON att.date = a.date " +
            "GROUP BY a.date ORDER BY a.date");

        // 课程满班率
        List<Map<String, Object>> classFillRate = jdbc.queryForList(
            "SELECT c.name, c.current_students, c.max_students, " +
            "ROUND(c.current_students * 100.0 / c.max_students, 1) as rate " +
            "FROM classes c WHERE c.status = 1 ORDER BY rate DESC");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayClasses", todayClasses != null ? todayClasses.intValue() : 0);
        result.put("todayStudents", todayStudents != null ? todayStudents.intValue() : 0);
        result.put("todayRevenue", todayRevenue != null ? todayRevenue : BigDecimal.ZERO);
        result.put("newStudents", newStudents != null ? newStudents.intValue() : 0);
        result.put("attendanceRate", attendanceRate);
        result.put("hourWarningCount", warningCount != null ? warningCount.intValue() : 0);
        result.put("pendingLeaves", pendingLeaves != null ? pendingLeaves.intValue() : 0);
        result.put("revenueTrend", revenueTrend);
        result.put("attendanceTrend", attTrend);
        result.put("classFillRate", classFillRate);
        return result;
    }

    public Map<String, Object> teacherStats(Long teacherId, LocalDate startDate, LocalDate endDate) {
        LocalDate s = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate e = endDate != null ? endDate : LocalDate.now();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalSchedules", jdbc.queryForObject(
            "SELECT COUNT(*) FROM schedules WHERE teacher_id = ? AND date BETWEEN ? AND ?",
            Long.class, teacherId, s, e));
        result.put("completedSchedules", jdbc.queryForObject(
            "SELECT COUNT(*) FROM schedules s JOIN attendances a ON s.id = a.schedule_id " +
            "WHERE s.teacher_id = ? AND s.date BETWEEN ? AND ? AND a.status = 1",
            Long.class, teacherId, s, e));
        result.put("totalStudents", jdbc.queryForObject(
            "SELECT COUNT(DISTINCT a.student_id) FROM attendances a " +
            "JOIN schedules s ON a.schedule_id = s.id " +
            "WHERE s.teacher_id = ? AND s.date BETWEEN ? AND ?",
            Long.class, teacherId, s, e));
        result.put("totalHours", BigDecimal.ZERO);
        result.put("estimatedSalary", BigDecimal.ZERO);
        result.put("scheduleList", Collections.emptyList());
        return result;
    }
}
