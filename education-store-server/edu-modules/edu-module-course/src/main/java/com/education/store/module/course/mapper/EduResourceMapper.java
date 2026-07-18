package com.education.store.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.course.entity.EduResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 课件资料 Mapper
 */
@Mapper
public interface EduResourceMapper extends BaseMapper<EduResource> {

    /** 原子增加下载次数 */
    @Update("UPDATE edu_resource SET download_count = download_count + 1 WHERE id = #{id}")
    int incrementDownloadCount(@Param("id") Long id);
}
