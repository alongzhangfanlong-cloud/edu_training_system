package com.education.store.module.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.points.entity.EduPointsAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 积分账户 Mapper
 */
@Mapper
public interface EduPointsAccountMapper extends BaseMapper<EduPointsAccount> {

    /**
     * 行锁查询积分账户
     */
    @Select("SELECT * FROM edu_points_account WHERE student_id = #{studentId} FOR UPDATE")
    EduPointsAccount selectByStudentIdForUpdate(@Param("studentId") Long studentId);

    /**
     * 扣减积分
     */
    @Update("UPDATE edu_points_account SET balance = balance - #{points}, total_spent = total_spent + #{points} WHERE student_id = #{studentId}")
    int deductPoints(@Param("studentId") Long studentId, @Param("points") Integer points);

    /**
     * 退还积分
     */
    @Update("UPDATE edu_points_account SET balance = balance + #{points}, total_spent = total_spent - #{points} WHERE student_id = #{studentId}")
    int refundPoints(@Param("studentId") Long studentId, @Param("points") Integer points);

    /**
     * 增加积分
     */
    @Update("UPDATE edu_points_account SET balance = balance + #{points}, total_earned = total_earned + #{points} WHERE student_id = #{studentId}")
    int addPoints(@Param("studentId") Long studentId, @Param("points") Integer points);
}
