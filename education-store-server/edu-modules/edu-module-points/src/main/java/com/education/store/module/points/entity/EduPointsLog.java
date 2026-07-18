package com.education.store.module.points.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分日志实体（只追加，不修改不删除）
 */
@Data
@TableName("edu_points_log")
public class EduPointsLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生ID */
    private Long studentId;

    /** 变动类型: EARN/REDEEM/ADJUST/EXPIRE */
    private String changeType;

    /** 变动积分数，正数=获取，负数=消耗 */
    private Integer amount;

    /** 变动后账户余额 */
    private Integer balanceAfter;

    /** 来源类型: TEACHER/ADMIN/REDEEM/CANCEL_REDEEM/ADJUST */
    private String sourceType;

    /** 来源业务记录ID（评价ID/兑换ID等） */
    private Long sourceId;

    /** 备注说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;
}
