package com.education.store.module.classinfo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 班级实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_class")
public class EduClass extends BaseEntity {

    /** 班级名称 */
    private String name;

    /** 班级编号（自动生成 CLS+yyyyMMdd+4位序号） */
    private String code;

    /** 关联课程ID → edu_course.id */
    private Long courseId;

    /** 授课教师ID → sys_user.id */
    private Long teacherId;

    /** 默认教室ID → edu_classroom.id（可空） */
    private Long classroomId;

    /** 最大学员数 */
    private Integer maxStudents;

    /** 当前在读人数（冗余缓存，由edu_class_student增删触发+1/-1） */
    private Integer currentCount;

    /** 状态(RECRUITING/FULL/SUSPENDED/FINISHED/CANCELLED) */
    private String status;

    /** 开课日期 */
    private LocalDate startDate;

    /** 结课日期 */
    private LocalDate endDate;

    /** 班级说明 */
    private String description;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
