package com.education.store.module.course.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.course.service.IEduCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 课程 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse>
        implements IEduCourseService {

    private final EduCourseMapper courseMapper;

    @Override
    public IPage<EduCourse> selectCourseList(IPage<EduCourse> page, EduCourse course) {
        LambdaQueryWrapper<EduCourse> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(course.getName())) {
            wrapper.like(EduCourse::getName, course.getName());
        }
        if (StrUtil.isNotBlank(course.getCode())) {
            wrapper.like(EduCourse::getCode, course.getCode());
        }
        if (course.getSubjectId() != null) {
            wrapper.eq(EduCourse::getSubjectId, course.getSubjectId());
        }
        if (StrUtil.isNotBlank(course.getCourseType())) {
            wrapper.eq(EduCourse::getCourseType, course.getCourseType());
        }
        if (course.getStatus() != null) {
            wrapper.eq(EduCourse::getStatus, course.getStatus());
        }
        wrapper.orderByAsc(EduCourse::getSortOrder);
        return courseMapper.selectPage(page, wrapper);
    }

    @Override
    public EduCourse selectCourseById(Long id) {
        return courseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCourse(EduCourse course) {
        return courseMapper.insert(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCourse(EduCourse course) {
        EduCourse exist = courseMapper.selectById(course.getId());
        if (exist == null) {
            throw new BusinessException("课程不存在");
        }

        // ONE_ON_ONE 类型变更校验：检查是否有活跃班级且人数 > 1
        if ("ONE_ON_ONE".equals(course.getCourseType())) {
            int activeCount = courseMapper.countActiveClasses(course.getId());
            if (activeCount > 0) {
                throw new BusinessException(
                        "该课程下存在 " + activeCount + " 个活跃班级，无法变更为一对一课程类型");
            }
        }

        return courseMapper.updateById(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCourseByIds(Long[] ids) {
        for (Long id : ids) {
            int refCount = courseMapper.countReferencedByClasses(id);
            if (refCount > 0) {
                EduCourse course = courseMapper.selectById(id);
                throw new BusinessException(
                        String.format("课程「%s」下存在 %d 个班级，无法删除", course.getName(), refCount));
            }
        }
        return courseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkCodeUnique(EduCourse course) {
        Long id = course.getId() != null ? course.getId() : -1L;
        EduCourse exist = lambdaQuery()
                .eq(EduCourse::getCode, course.getCode())
                .one();
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public boolean checkNameUniqueWithinSubject(EduCourse course) {
        Long id = course.getId() != null ? course.getId() : -1L;
        EduCourse exist = lambdaQuery()
                .eq(EduCourse::getSubjectId, course.getSubjectId())
                .eq(EduCourse::getName, course.getName())
                .one();
        return exist == null || exist.getId().equals(id);
    }

    @Override
    public java.util.List<EduCourse> selectAllUpCourses() {
        return lambdaQuery()
                .eq(EduCourse::getStatus, 1) // 1=上架
                .orderByAsc(EduCourse::getSortOrder)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCourseStatus(Long id, Integer status) {
        EduCourse course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        course.setStatus(status);
        return courseMapper.updateById(course);
    }
}
