package com.education.store.module.classinfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.entity.EduClassStudent;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.classinfo.mapper.EduClassStudentMapper;
import com.education.store.module.classinfo.service.IEduClassStudentService;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.mapper.EduStudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 班级学员关联 Service 实现（跨模块桥接，事务保证 current_count 一致性）
 */
@Service
@RequiredArgsConstructor
public class EduClassStudentServiceImpl extends ServiceImpl<EduClassStudentMapper, EduClassStudent>
        implements IEduClassStudentService {

    private final EduClassStudentMapper classStudentMapper;
    private final EduClassMapper classMapper;
    private final EduStudentMapper studentMapper;

    @Override
    public IPage<EduClassStudent> selectClassStudentList(IPage<EduClassStudent> page, Long classId) {
        LambdaQueryWrapper<EduClassStudent> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(EduClassStudent::getClassId, classId);
        }
        wrapper.orderByDesc(EduClassStudent::getJoinTime);
        return classStudentMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enrollStudent(EduClassStudent classStudent) {
        // 1. 校验班级存在且可加入
        EduClass clazz = classMapper.selectById(classStudent.getClassId());
        if (clazz == null) {
            throw new BusinessException("班级不存在");
        }
        if ("FINISHED".equals(clazz.getStatus()) || "CANCELLED".equals(clazz.getStatus())) {
            throw new BusinessException("班级已" + clazz.getStatus() + "，无法加入");
        }

        // 2. 校验学员存在且状态为在读
        EduStudent student = studentMapper.selectById(classStudent.getStudentId());
        if (student == null) {
            throw new BusinessException("学员不存在");
        }
        if (!"ENROLLED".equals(student.getStatus())) {
            throw new BusinessException("非在读学员无法加入班级");
        }

        // 3. 校验未满员
        if (clazz.getMaxStudents() > 0 && clazz.getCurrentCount() >= clazz.getMaxStudents()) {
            throw new BusinessException("班级已满员（" + clazz.getCurrentCount() + "/" + clazz.getMaxStudents() + "）");
        }

        // 4. 校验重复加入
        Long count = lambdaQuery()
                .eq(EduClassStudent::getClassId, classStudent.getClassId())
                .eq(EduClassStudent::getStudentId, classStudent.getStudentId())
                .count();
        if (count > 0) {
            throw new BusinessException("该学员已加入此班级");
        }

        // 5. 设置默认值并插入
        classStudent.setStatus("ENROLLED");
        classStudent.setJoinTime(LocalDateTime.now());
        int rows = classStudentMapper.insert(classStudent);

        // 6. 班级人数 +1
        classMapper.incrementCurrentCount(clazz.getId());

        // 7. 检查是否需要标记为 FULL
        EduClass updatedClass = classMapper.selectById(clazz.getId());
        if (updatedClass.getMaxStudents() > 0
                && updatedClass.getCurrentCount() >= updatedClass.getMaxStudents()
                && !"FULL".equals(updatedClass.getStatus())) {
            updatedClass.setStatus("FULL");
            classMapper.updateById(updatedClass);
        }

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long id, String newStatus) {
        EduClassStudent classStudent = classStudentMapper.selectById(id);
        if (classStudent == null) {
            throw new BusinessException("班级学员关联不存在");
        }

        String oldStatus = classStudent.getStatus();
        if (oldStatus.equals(newStatus)) {
            return 0;
        }

        // ENROLLED → SUSPENDED/EXITED：人数 -1
        if ("ENROLLED".equals(oldStatus) && !"ENROLLED".equals(newStatus)) {
            classMapper.decrementCurrentCount(classStudent.getClassId());
        }
        // SUSPENDED/EXITED → ENROLLED：人数 +1
        if (!"ENROLLED".equals(oldStatus) && "ENROLLED".equals(newStatus)) {
            classMapper.incrementCurrentCount(classStudent.getClassId());
        }

        classStudent.setStatus(newStatus);
        int rows = classStudentMapper.updateById(classStudent);

        // 检查 FULL↔RECRUITING 自动切换
        checkAndUpdateClassStatus(classStudent.getClassId());

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeStudent(Long id) {
        EduClassStudent classStudent = classStudentMapper.selectById(id);
        if (classStudent == null) {
            throw new BusinessException("关联记录不存在");
        }

        // 如果在读状态，人数 -1
        if ("ENROLLED".equals(classStudent.getStatus())) {
            classMapper.decrementCurrentCount(classStudent.getClassId());
        }

        int rows = classStudentMapper.deleteById(id);

        // 检查 FULL→RECRUITING
        checkAndUpdateClassStatus(classStudent.getClassId());

        return rows;
    }

    /** 根据当前人数和最大人数，自动更新班级 FULL/RECRUITING 状态 */
    private void checkAndUpdateClassStatus(Long classId) {
        EduClass clazz = classMapper.selectById(classId);
        if (clazz == null) {
            return;
        }
        boolean shouldBeFull = clazz.getMaxStudents() > 0
                && clazz.getCurrentCount() >= clazz.getMaxStudents();

        if (shouldBeFull && "RECRUITING".equals(clazz.getStatus())) {
            clazz.setStatus("FULL");
            classMapper.updateById(clazz);
        } else if (!shouldBeFull && "FULL".equals(clazz.getStatus())) {
            clazz.setStatus("RECRUITING");
            classMapper.updateById(clazz);
        }
    }
}
