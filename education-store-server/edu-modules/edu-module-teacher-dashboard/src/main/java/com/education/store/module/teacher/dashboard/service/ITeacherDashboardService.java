package com.education.store.module.teacher.dashboard.service;

import com.education.store.module.teacher.dashboard.model.*;

import java.util.List;
import java.util.Map;

/**
 * 教师工作台 Service 接口
 */
public interface ITeacherDashboardService {

    /**
     * 获取欢迎区信息（教师姓名、上次登录时间、服务器日期）
     */
    TeacherUserInfoVO getUserInfo();

    /**
     * 获取统计卡片数据
     */
    TeacherStatisticsVO getStatistics();

    /**
     * 获取今日课表
     */
    List<TeacherScheduleVO> getSchedule();

    /**
     * 获取某日期范围的课表（周视图/月视图）
     */
    List<TeacherScheduleVO> getScheduleByDateRange(String startDate, String endDate);

    /**
     * 获取某排课的班级学生名单 + 签到状态
     */
    List<TeacherScheduleStudentVO> getScheduleStudents(Long scheduleId);

    /**
     * 获取待办数据
     */
    TeacherTodosVO getTodos();

    /**
     * 批量签到
     * @param scheduleId 排课ID
     * @param records 签到记录列表 (studentId, status)
     */
    void batchSignin(Long scheduleId, List<Map<String, Object>> records);

    /**
     * 获取某排课签到统计
     */
    Map<String, Long> getScheduleSigninStats(Long scheduleId);

    /**
     * 获取我教的学生列表
     */
    List<TeacherStudentVO> getMyStudents();
}
