package com.edu.modules.attendance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("attendances")
public class AttendanceEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scheduleId;
    private Long studentId;
    private Long classId;
    private Integer status;
    private LocalDateTime checkInTime;
    private Integer checkInMethod;
    private BigDecimal checkInLat;
    private BigDecimal checkInLng;
    private Integer leaveType;
    private String leaveReason;
    private LocalDateTime leaveApplyTime;
    private Integer leaveApproveStatus;
    private LocalDateTime leaveApproveTime;
    private BigDecimal consumeHours;
    private Integer isMakeup;
    private Long originalAttendanceId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
