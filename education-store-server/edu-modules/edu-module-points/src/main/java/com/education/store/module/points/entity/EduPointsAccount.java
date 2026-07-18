package com.education.store.module.points.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分账户实体
 */
@Data
@TableName("edu_points_account")
public class EduPointsAccount implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生ID，关联 edu_student.id */
    private Long studentId;

    /** 累计获取积分 */
    private Integer totalEarned;

    /** 累计消费积分 */
    private Integer totalSpent;

    /** 当前积分余额 */
    private Integer balance;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
