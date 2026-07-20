package com.education.store.module.points.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotBlank(message = "礼品名称不能为空")
    private String name;

    /** 礼品图片URL */
    @NotBlank(message = "礼品图片不能为空")
    private String imageUrl;

    /** 兑换所需积分 */
    @NotNull(message = "兑换积分不能为空")
    @Positive(message = "兑换积分必须大于0")
    private Integer pointsRequired;

    /** 总库存数量 */
    @NotNull(message = "总库存不能为空")
    @Positive(message = "总库存必须大于0")
    private Integer stockTotal;

    /** 剩余库存数量 */
    private Integer stockRemaining;

    /** 状态: 1=启用, 0=禁用 */
    private Integer status;

    /** 礼品描述 */
    private String description;

    /** 已兑换数量（非DB字段，查询时动态填充） */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer redemptionCount;
}
