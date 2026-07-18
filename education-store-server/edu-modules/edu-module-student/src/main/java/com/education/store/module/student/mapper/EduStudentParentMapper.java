package com.education.store.module.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.student.entity.EduStudentParent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 家长关联 Mapper
 */
@Mapper
public interface EduStudentParentMapper extends BaseMapper<EduStudentParent> {

    /** 按手机号查询未绑定用户的家长记录（userId 回填用） */
    @Select("SELECT * FROM edu_student_parent WHERE mobile = #{mobile} AND user_id IS NULL AND deleted = 0")
    List<EduStudentParent> selectByMobile(@Param("mobile") String mobile);
}
