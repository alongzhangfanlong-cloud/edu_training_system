package com.education.store.module.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.course.entity.EduCourse;

/**
 * 课程 Service 接口
 */
public interface IEduCourseService extends IService<EduCourse> {

    /** 分页查询课程列表 */
    IPage<EduCourse> selectCourseList(IPage<EduCourse> page, EduCourse course);

    /** 根据ID查询课程 */
    EduCourse selectCourseById(Long id);

    /** 新增课程 */
    int insertCourse(EduCourse course);

    /** 更新课程 */
    int updateCourse(EduCourse course);

    /** 根据ID数组删除课程 */
    int deleteCourseByIds(Long[] ids);

    /** 校验课程编号是否全局唯一 */
    boolean checkCodeUnique(EduCourse course);

    /** 校验同一科目下课程名称是否唯一 */
    boolean checkNameUniqueWithinSubject(EduCourse course);
}
