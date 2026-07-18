package com.education.store.module.teaching.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private Long examScoreId;

    /** 学生ID */
    private Long studentId;

    /** 分数 */
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
