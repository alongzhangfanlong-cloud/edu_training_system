package com.education.store.module.classinfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.classinfo.entity.EduClassStudent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 班级学员关联 Mapper（N02↔N03 跨模块桥接）
 */
@Mapper
public interface EduClassStudentMapper extends BaseMapper<EduClassStudent> {
}
