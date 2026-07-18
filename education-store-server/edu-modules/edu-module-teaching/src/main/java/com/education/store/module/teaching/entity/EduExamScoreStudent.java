package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生成绩明细实体
 */
@Data
@TableName("edu_exam_score_student")
public class EduExamScoreStudent implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联成绩ID */
    @NotNull(message = "成绩ID不能为空")
    private Long examScoreId;

    /** 学生ID */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    /** 分数 */
    @NotNull(message = "分数不能为空")
    private BigDecimal score;

    /** 是否缺考: 0=否, 1=是 */
    private Integer isAbsent;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
