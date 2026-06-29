package com.edu.modules.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.modules.student.dto.StudentListItemDTO;
import com.edu.modules.student.entity.StudentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper extends BaseMapper<StudentEntity> {

    @Select("""
        <script>
        SELECT s.id, s.student_no, s.name, s.grade, s.phone, s.status, s.avatar,
               COUNT(DISTINCT sc.class_id) as total_courses,
               COALESCE(SUM(sc.total_hours), 0) as total_hours,
               COALESCE(SUM(sc.remain_hours), 0) as remain_hours
        FROM students s
        LEFT JOIN student_classes sc ON s.id = sc.student_id AND sc.status = 1
        WHERE s.is_deleted = 0
        <if test='keyword != null and keyword != \"\"'>
            AND (s.name LIKE CONCAT('%', #{keyword}, '%')
                 OR s.phone LIKE CONCAT('%', #{keyword}, '%')
                 OR s.student_no LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        <if test='status != null'>
            AND s.status = #{status}
        </if>
        <if test='classId != null'>
            AND EXISTS (SELECT 1 FROM student_classes sc2 WHERE sc2.student_id = s.id AND sc2.class_id = #{classId} AND sc2.status = 1)
        </if>
        GROUP BY s.id
        ORDER BY s.created_at DESC
        </script>
    """)
    Page<StudentListItemDTO> selectStudentPage(Page<StudentListItemDTO> page,
                                                @Param("keyword") String keyword,
                                                @Param("status") Integer status,
                                                @Param("classId") Long classId);
}
