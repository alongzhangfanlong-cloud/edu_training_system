package com.education.store.module.teaching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.teaching.entity.EduHomeworkSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 作业提交 Mapper
 */
@Mapper
public interface EduHomeworkSubmissionMapper extends BaseMapper<EduHomeworkSubmission> {

    /**
     * 按教师ID统计待批改作业数
     */
    @Select("SELECT COUNT(*) FROM edu_homework_submission hs " +
            "INNER JOIN edu_homework h ON hs.homework_id = h.id AND h.deleted = 0 " +
            "WHERE h.teacher_id = #{teacherId} AND hs.status IN ('SUBMITTED', 'LATE') AND hs.deleted = 0")
    int countPendingGrading(@Param("teacherId") Long teacherId);
}
