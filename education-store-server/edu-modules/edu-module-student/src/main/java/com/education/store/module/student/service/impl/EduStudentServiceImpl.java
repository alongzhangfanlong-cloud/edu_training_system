package com.education.store.module.student.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.student.service.IEduStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 在学学员 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduStudentServiceImpl extends ServiceImpl<EduStudentMapper, EduStudent>
        implements IEduStudentService {

    private final EduStudentMapper studentMapper;

    @Override
    public IPage<EduStudent> selectStudentList(IPage<EduStudent> page, EduStudent student) {
        LambdaQueryWrapper<EduStudent> wrapper = new LambdaQueryWrapper<>();
        // keyword 搜索：姓名和手机号 OR 匹配
        if (StrUtil.isNotBlank(student.getName()) && StrUtil.isNotBlank(student.getMobile())
                && student.getName().equals(student.getMobile())) {
            // 同值 = keyword 模式：OR 模糊匹配
            wrapper.and(w -> w.like(EduStudent::getName, student.getName())
                    .or().like(EduStudent::getMobile, student.getName()));
        } else {
            if (StrUtil.isNotBlank(student.getName())) {
                wrapper.like(EduStudent::getName, student.getName());
            }
            if (StrUtil.isNotBlank(student.getMobile())) {
                wrapper.like(EduStudent::getMobile, student.getMobile());
            }
        }
        if (StrUtil.isNotBlank(student.getGrade())) {
            wrapper.eq(EduStudent::getGrade, student.getGrade());
        }
        if (StrUtil.isNotBlank(student.getStatus())) {
            wrapper.eq(EduStudent::getStatus, student.getStatus());
        }
        if (student.getCounselorId() != null) {
            wrapper.eq(EduStudent::getCounselorId, student.getCounselorId());
        }
        if (StrUtil.isNotBlank(student.getSource())) {
            wrapper.eq(EduStudent::getSource, student.getSource());
        }
        wrapper.orderByDesc(EduStudent::getCreateTime);
        IPage<EduStudent> result = studentMapper.selectPage(page, wrapper);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override
    public EduStudent selectStudentById(Long id) {
        EduStudent student = studentMapper.selectById(id);
        if (student != null) {
            fillNames(student);
        }
        return student;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertStudent(EduStudent student) {
        student.setStatus("ENROLLED");
        student.setTotalHours(BigDecimal.ZERO);
        student.setRemainingHours(BigDecimal.ZERO);
        return studentMapper.insert(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStudent(EduStudent student) {
        EduStudent exist = studentMapper.selectById(student.getId());
        if (exist == null) {
            throw new BusinessException("学员不存在");
        }
        // 状态变更必须走状态机校验，防止绕过 updateStudentStatus 的转换规则
        if (student.getStatus() != null && !student.getStatus().equals(exist.getStatus())) {
            updateStudentStatus(student.getId(), student.getStatus());
            // 状态已单独处理，清空避免后面 updateById 重复写入（虽然不是问题，但语义清晰）
            student.setStatus(null);
        }
        return studentMapper.updateById(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteStudentByIds(Long[] ids) {
        for (Long id : ids) {
            int activeCount = studentMapper.countActiveClassEnrollments(id);
            if (activeCount > 0) {
                EduStudent student = studentMapper.selectById(id);
                throw new BusinessException(
                        String.format("学员「%s」仍有 %d 个活跃班级关联，无法删除", student.getName(), activeCount));
            }
        }
        return studentMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkMobileUnique(EduStudent student) {
        Long id = student.getId() != null ? student.getId() : -1L;
        EduStudent exist = lambdaQuery()
                .eq(EduStudent::getMobile, student.getMobile())
                .one();
        return exist == null || exist.getId().equals(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStudentStatus(Long id, String newStatus) {
        EduStudent student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学员不存在");
        }

        String oldStatus = student.getStatus();
        validateStatusTransition(oldStatus, newStatus, id);

        // COMPLETED 条件检查：所有班级 EXITED + 剩余课时为 0
        if ("COMPLETED".equals(newStatus)) {
            int nonExited = studentMapper.countNonExitedEnrollments(id);
            if (nonExited > 0) {
                throw new BusinessException("学员仍有 " + nonExited + " 个班级未退出，无法标记为结业");
            }
            if (student.getRemainingHours() != null && student.getRemainingHours().compareTo(BigDecimal.ZERO) > 0) {
                throw new BusinessException("学员仍有剩余课时，无法标记为结业");
            }
        }

        student.setStatus(newStatus);
        if ("COMPLETED".equals(newStatus)) {
            student.setCompletedAt(LocalDateTime.now());
        }
        // LOST → ENROLLED：新报名自动恢复
        if ("LOST".equals(oldStatus) && "ENROLLED".equals(newStatus)) {
            // 通过新报名触发，此处仅做状态变更
        }

        return studentMapper.updateById(student);
    }

    @Override
    public java.util.List<EduStudent> selectStudentSimpleList(String keyword) {
        LambdaQueryWrapper<EduStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(EduStudent::getId, EduStudent::getName, EduStudent::getGrade,
                EduStudent::getStatus, EduStudent::getMobile);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(EduStudent::getName, keyword)
                    .or().like(EduStudent::getMobile, keyword));
        }
        wrapper.orderByDesc(EduStudent::getCreateTime);
        wrapper.last("LIMIT 50");
        return studentMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateStatus(Long[] ids, String status) {
        int count = 0;
        for (Long id : ids) {
            updateStudentStatus(id, status);
            count++;
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateConsultant(Long[] ids, Long consultantId) {
        int count = 0;
        for (Long id : ids) {
            EduStudent student = new EduStudent();
            student.setId(id);
            student.setCounselorId(consultantId);
            studentMapper.updateById(student);
            count++;
        }
        return count;
    }

    /** 填充顾问姓名和报名课程名称（跨表查询，避免模块循环依赖） */
    private void fillNames(EduStudent s) {
        if (s.getCounselorId() != null) {
            s.setCounselorName(studentMapper.selectCounselorName(s.getCounselorId()));
        }
        if (s.getIntendedCourseId() != null) {
            s.setIntendedCourseName(studentMapper.selectCourseName(s.getIntendedCourseId()));
        }
    }

    /** 状态转换校验 */
    private void validateStatusTransition(String oldStatus, String newStatus, Long studentId) {
        if (oldStatus.equals(newStatus)) {
            return;
        }
        switch (oldStatus) {
            case "ENROLLED":
                if (!"SUSPENDED".equals(newStatus) && !"COMPLETED".equals(newStatus)
                        && !"LOST".equals(newStatus)) {
                    throw new BusinessException("在读状态只能变更为停课/结业/流失");
                }
                break;
            case "SUSPENDED":
                if (!"ENROLLED".equals(newStatus)) {
                    throw new BusinessException("停课状态只能恢复为在读");
                }
                break;
            case "COMPLETED":
                if (!"ENROLLED".equals(newStatus)) {
                    throw new BusinessException("结业学员只能重新激活为在读");
                }
                break;
            case "LOST":
                if (!"ENROLLED".equals(newStatus)) {
                    throw new BusinessException("流失学员只能通过新报名恢复为在读");
                }
                break;
            default:
                throw new BusinessException("未知状态: " + oldStatus);
        }
    }}
