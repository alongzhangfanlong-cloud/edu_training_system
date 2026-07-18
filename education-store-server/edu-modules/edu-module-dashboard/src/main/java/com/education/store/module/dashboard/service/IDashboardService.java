package com.education.store.module.dashboard.service;

import com.education.store.module.dashboard.model.*;

import java.time.LocalDate;

/**
 * 工作台 Dashboard Service 接口
 */
public interface IDashboardService {

    /**
     * 获取欢迎区信息
     */
    UserInfoVO getUserInfo();

    /**
     * 获取统计卡片数据
     */
    StatisticsVO getStatistics();

    /**
     * 获取排课日历数据
     *
     * @param viewType 视图类型：MONTH / WEEK / DAY
     * @param date     参考日期
     */
    CalendarVO getCalendar(String viewType, LocalDate date);

    /**
     * 获取某排课的学生签到详情
     */
    ScheduleStudentVO getScheduleStudents(Long scheduleId);

    /**
     * 获取待办提醒数据
     */
    TodosVO getTodos();

    /**
     * 获取本月报名统计
     */
    EnrollmentStatsVO getEnrollmentStats();
}
