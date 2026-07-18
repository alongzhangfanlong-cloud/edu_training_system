package com.education.store.module.classinfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.classinfo.entity.EduClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 班级 Mapper
 */
@Mapper
public interface EduClassMapper extends BaseMapper<EduClass> {

    /** 查询当天最大序号（用于编号生成 CLS+yyyyMMdd+4位序号） */
    @Select("SELECT COALESCE(MAX(CAST(SUBSTRING(code, 12) AS UNSIGNED)), 0) FROM edu_class WHERE code LIKE CONCAT(#{datePrefix}, '%')")
    int selectTodayMaxSeq(@Param("datePrefix") String datePrefix);

    /** 原子递增当前人数 */
    @Update("UPDATE edu_class SET current_count = current_count + 1 WHERE id = #{classId}")
    int incrementCurrentCount(@Param("classId") Long classId);

    /** 原子递减当前人数 */
    @Update("UPDATE edu_class SET current_count = current_count - 1 WHERE id = #{classId} AND current_count > 0")
    int decrementCurrentCount(@Param("classId") Long classId);
}
