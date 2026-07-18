package com.education.store.module.teacher.dashboard.service;

import com.education.store.module.teacher.dashboard.model.*;

import java.util.List;

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
     * 获取某排课的班级学生名单 + 签到状态
     */
    List<TeacherScheduleStudentVO> getScheduleStudents(Long scheduleId);

    /**
     * 获取待办数据
     */
    TeacherTodosVO getTodos();
}
