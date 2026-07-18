package com.education.store.module.points.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 积分兑换记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_points_redemption")
public class EduPointsRedemption extends BaseEntity {

    /** 学生ID */
    private Long studentId;

    /** 礼品ID */
    private Long giftId;

    /** 消耗积分 */
    private Integer pointsSpent;

    /** 状态: PENDING_CLAIM/CLAIMED/CANCELLED */
    private String status;

    /** 兑换时间 */
    private LocalDateTime redeemTime;

    /** 领取时间 */
    private LocalDateTime claimTime;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 操作人ID（核销/取消时的操作人员） */
    private Long operatorId;
}
