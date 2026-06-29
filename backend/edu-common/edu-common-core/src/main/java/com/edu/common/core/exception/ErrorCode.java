package com.edu.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一错误码定义
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400xx - 参数校验
    PARAM_INVALID(40001, "参数校验失败"),
    PARAM_MISSING(40002, "必填参数缺失"),

    // 401xx - 认证
    TOKEN_EXPIRED(40101, "Token已过期"),
    TOKEN_INVALID(40102, "Token无效"),
    LOGIN_FAILED(40103, "登录失败"),
    SMS_CODE_ERROR(40104, "验证码错误"),

    // 403xx - 权限
    NO_PERMISSION(40301, "无访问权限"),
    ROLE_NOT_ALLOWED(40302, "角色无此操作权限"),

    // 404xx - 资源不存在
    STUDENT_NOT_FOUND(40401, "学员不存在"),
    TEACHER_NOT_FOUND(40402, "教师不存在"),
    COURSE_NOT_FOUND(40403, "课程不存在"),
    CLASS_NOT_FOUND(40404, "班级不存在"),
    CLASSROOM_NOT_FOUND(40405, "教室不存在"),
    SCHEDULE_NOT_FOUND(40406, "排课不存在"),
    ORDER_NOT_FOUND(40407, "订单不存在"),
    ATTENDANCE_NOT_FOUND(40408, "考勤记录不存在"),
    FEEDBACK_NOT_FOUND(40409, "反馈不存在"),

    // 409xx - 业务冲突
    SCHEDULE_CONFLICT_TEACHER(40901, "教师时间冲突"),
    SCHEDULE_CONFLICT_CLASSROOM(40902, "教室时间冲突"),
    SCHEDULE_CONFLICT_STUDENT(40903, "学员时间冲突"),
    CLASSROOM_CAPACITY_EXCEEDED(40904, "教室容量超限"),
    DUPLICATE_ENROLLMENT(40905, "学员已报名该班级"),
    CLASS_FULL(40906, "班级已满"),
    HOURS_INSUFFICIENT(40907, "课时余额不足"),

    // 500xx - 服务器错误
    INTERNAL_ERROR(50001, "服务器内部错误"),
    DB_ERROR(50002, "数据库操作失败"),
    REDIS_ERROR(50003, "缓存服务异常"),

    // 业务通用
    BUSINESS_ERROR(40000, "业务处理失败");

    private final int code;
    private final String message;
}
