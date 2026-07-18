package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生评价明细实体
 */
@Data
@TableName("edu_evaluation_student")
public class EduEvaluationStudent implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联评价ID */
    private Long evaluationId;

    /** 学生ID */
    private Long studentId;

    /** 星级评分 1.0-5.0 */
    private BigDecimal starRating;

    /** 文字评语 */
    private String comment;

    /** 奖励积分数值 */
    private Integer rewardPoints;

    /** 积分奖励原因 */
    private String rewardReason;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
