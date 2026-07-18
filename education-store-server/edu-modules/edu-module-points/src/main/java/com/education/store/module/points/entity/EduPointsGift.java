package com.education.store.module.points.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分礼品实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_points_gift")
public class EduPointsGift extends BaseEntity {

    /** 礼品名称 */
    private String name;

    /** 礼品图片URL */
    private String imageUrl;

    /** 兑换所需积分 */
    private Integer pointsRequired;

    /** 总库存数量 */
    private Integer stockTotal;

    /** 剩余库存数量 */
    private Integer stockRemaining;

    /** 状态: 1=启用, 0=禁用 */
    private Integer status;

    /** 礼品描述 */
    private String description;
}
