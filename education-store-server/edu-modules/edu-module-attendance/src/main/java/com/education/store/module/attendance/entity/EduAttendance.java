package com.education.store.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("edu_attendance")
public class EduAttendance implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scheduleId;
    private Long classId;
    private Long studentId;
    private Integer status;
    private LocalDateTime signinTime;
    private Long operatorId;
    private String operatorType;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
