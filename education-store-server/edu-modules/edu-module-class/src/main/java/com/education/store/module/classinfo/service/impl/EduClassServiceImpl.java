package com.education.store.module.classinfo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.classinfo.service.IEduClassService;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduCourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 班级 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduClassServiceImpl extends ServiceImpl<EduClassMapper, EduClass>
        implements IEduClassService {

    private final EduClassMapper classMapper;
    private final EduCourseMapper courseMapper;

    @Override
    public IPage<EduClass> selectClassList(IPage<EduClass> page, EduClass clazz) {
        LambdaQueryWrapper<EduClass> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(clazz.getName())) {
            wrapper.like(EduClass::getName, clazz.getName());
        }
        if (StrUtil.isNotBlank(clazz.getCode())) {
            wrapper.like(EduClass::getCode, clazz.getCode());
        }
        if (clazz.getCourseId() != null) {
            wrapper.eq(EduClass::getCourseId, clazz.getCourseId());
        }
        if (clazz.getTeacherId() != null) {
            wrapper.eq(EduClass::getTeacherId, clazz.getTeacherId());
        }
        if (StrUtil.isNotBlank(clazz.getStatus())) {
            wrapper.eq(EduClass::getStatus, clazz.getStatus());
        }
        wrapper.orderByDesc(EduClass::getCreateTime);
        return classMapper.selectPage(page, wrapper);
    }

    @Override
    public EduClass selectClassById(Long id) {
        return classMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertClass(EduClass clazz) {
        // 1. 校验课程存在且类型合规
        EduCourse course = courseMapper.selectById(clazz.getCourseId());
        if (course == null) {
            throw new BusinessException("关联课程不存在");
        }
        if ("ONE_ON_ONE".equals(course.getCourseType())) {
            if (clazz.getMaxStudents() == null || clazz.getMaxStudents() > 1) {
                clazz.setMaxStudents(1);
            }
        }

        // 2. 自动生成班级编号
        clazz.setCode(generateClassCode());

        // 3. 设置初始状态
        if (StrUtil.isBlank(clazz.getStatus())) {
            clazz.setStatus("RECRUITING");
        }
        clazz.setCurrentCount(0);

        // 4. 插入班级
        int rows = classMapper.insert(clazz);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateClass(EduClass clazz) {
        EduClass exist = classMapper.selectById(clazz.getId());
        if (exist == null) {
            throw new BusinessException("班级不存在");
        }

        // 扩容量自动恢复 RECRUITING
        if (clazz.getMaxStudents() != null
                && clazz.getMaxStudents() > exist.getMaxStudents()
                && "FULL".equals(exist.getStatus())
                && exist.getCurrentCount() < clazz.getMaxStudents()) {
            clazz.setStatus("RECRUITING");
        }

        return classMapper.updateById(clazz);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteClassByIds(Long[] ids) {
        for (Long id : ids) {
            EduClass clazz = classMapper.selectById(id);
            if (clazz == null) {
                continue;
            }
            if (clazz.getCurrentCount() != null && clazz.getCurrentCount() > 0) {
                throw new BusinessException(
                        String.format("班级「%s」仍有 %d 名学员，无法删除", clazz.getName(), clazz.getCurrentCount()));
            }
        }
        return classMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateClassStatus(Long id, String newStatus) {
        EduClass clazz = classMapper.selectById(id);
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }

        String oldStatus = clazz.getStatus();
        validateClassStatusTransition(oldStatus, newStatus, clazz);

        // CANCELLED 要求无在读学员
        if ("CANCELLED".equals(newStatus) && clazz.getCurrentCount() > 0) {
            throw new BusinessException("班级仍有 " + clazz.getCurrentCount() + " 名学员，无法取消");
        }

        clazz.setStatus(newStatus);
        return classMapper.updateById(clazz);
    }

    // ========== 私有方法 ==========

    /** 生成班级编号：CLS+yyyyMMdd+HHmmss+2位随机数 */
    private String generateClassCode() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return "CLS" + dateStr + timeStr + random;
    }

    /** 班级状态转换校验 */
    private void validateClassStatusTransition(String oldStatus, String newStatus, EduClass clazz) {
        if (oldStatus.equals(newStatus)) {
            return;
        }
        switch (oldStatus) {
            case "RECRUITING":
                if (!"FULL".equals(newStatus) && !"SUSPENDED".equals(newStatus)
                        && !"FINISHED".equals(newStatus) && !"CANCELLED".equals(newStatus)) {
                    throw new BusinessException("招生中状态只能变更为已满/停招/结课/取消");
                }
                break;
            case "FULL":
                if (!"RECRUITING".equals(newStatus) && !"SUSPENDED".equals(newStatus)
                        && !"FINISHED".equals(newStatus) && !"CANCELLED".equals(newStatus)) {
                    throw new BusinessException("已满状态只能变更为招生中/停招/结课/取消");
                }
                break;
            case "SUSPENDED":
                if (!"RECRUITING".equals(newStatus) && !"FULL".equals(newStatus)
                        && !"FINISHED".equals(newStatus) && !"CANCELLED".equals(newStatus)) {
                    throw new BusinessException("停招状态只能变更为招生中/已满/结课/取消");
                }
                break;
            case "FINISHED":
            case "CANCELLED":
                throw new BusinessException("结课/已取消为终态，不可变更");
            default:
                throw new BusinessException("未知状态: " + oldStatus);
        }
    }
}
