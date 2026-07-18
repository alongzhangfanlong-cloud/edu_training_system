package com.education.store.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("edu_attendance")
public class EduAttendance implements Serializable {

    /** 签到状态常量 */
    public static final int STATUS_PRESENT = 0;  // 到校
    public static final int STATUS_LATE = 1;     // 迟到
    public static final int STATUS_ABSENT = 2;   // 缺勤
    public static final int STATUS_LEAVE = 3;    // 请假
    public static final int STATUS_LEAVE_PENDING = 4; // 请假未审批

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
    @TableLogic
    private Integer deleted;
}
