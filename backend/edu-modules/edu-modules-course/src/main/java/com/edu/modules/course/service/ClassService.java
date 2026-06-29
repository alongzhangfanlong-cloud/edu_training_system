package com.edu.modules.course.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.mybatis.PageParam;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.course.dto.ClassDTO;
import com.edu.modules.course.entity.ClassEntity;
import com.edu.modules.course.entity.ClassroomEntity;
import com.edu.modules.course.entity.CourseEntity;
import com.edu.modules.course.entity.TeacherEntity;
import com.edu.modules.course.mapper.ClassMapper;
import com.edu.modules.course.mapper.ClassroomMapper;
import com.edu.modules.course.mapper.CourseMapper;
import com.edu.modules.course.mapper.TeacherMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassMapper classMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final ClassroomMapper classroomMapper;

    public PageResult<ClassDTO> list(PageParam param, Long courseId, Long teacherId, Integer status) {
        Page<ClassEntity> page = new Page<>(param.getPage(), param.getSafePageSize());
        LambdaQueryWrapper<ClassEntity> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) wrapper.eq(ClassEntity::getCourseId, courseId);
        if (teacherId != null) wrapper.eq(ClassEntity::getTeacherId, teacherId);
        if (status != null) wrapper.eq(ClassEntity::getStatus, status);
        wrapper.orderByDesc(ClassEntity::getCreatedAt);
        Page<ClassEntity> result = classMapper.selectPage(page, wrapper);
        return PageResult.of(
                result.getRecords().stream().map(this::toDTO).toList(),
                result.getTotal(), param.getPage(), param.getSafePageSize());
    }

    public ClassDTO getById(Long id) {
        ClassEntity e = classMapper.selectById(id);
        if (e == null) throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        return toDTO(e);
    }

    @Transactional
    public Long create(ClassDTO dto) {
        // 校验课程存在
        CourseEntity course = courseMapper.selectById(dto.getCourseId());
        if (course == null) throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);

        // 校验教师存在
        TeacherEntity teacher = teacherMapper.selectById(dto.getTeacherId());
        if (teacher == null) throw new BusinessException(ErrorCode.TEACHER_NOT_FOUND);

        // 校验教室（可选）
        if (dto.getClassroomId() != null) {
            ClassroomEntity classroom = classroomMapper.selectById(dto.getClassroomId());
            if (classroom == null) throw new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND);
            // 校验容量
            if (dto.getMaxStudents() != null && dto.getMaxStudents() > classroom.getCapacity()) {
                throw new BusinessException(ErrorCode.CLASSROOM_CAPACITY_EXCEEDED,
                        "教室容量(" + classroom.getCapacity() + "人)不足");
            }
        }

        ClassEntity e = new ClassEntity();
        e.setCourseId(dto.getCourseId()); e.setTeacherId(dto.getTeacherId());
        e.setAssistantId(dto.getAssistantId()); e.setClassroomId(dto.getClassroomId());
        e.setName(dto.getName()); e.setMaxStudents(dto.getMaxStudents() != null ? dto.getMaxStudents() : 20);
        e.setCurrentStudents(0); e.setScheduleRule(dto.getScheduleRule());
        e.setStartDate(dto.getStartDate()); e.setEndDate(dto.getEndDate());
        e.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        classMapper.insert(e);
        log.info("创建班级成功: id={}, name={}", e.getId(), e.getName());
        return e.getId();
    }

    @Transactional
    public void update(Long id, ClassDTO dto) {
        ClassEntity e = classMapper.selectById(id);
        if (e == null) throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);

        if (StrUtil.isNotBlank(dto.getName())) e.setName(dto.getName());
        if (dto.getTeacherId() != null) e.setTeacherId(dto.getTeacherId());
        if (dto.getClassroomId() != null) e.setClassroomId(dto.getClassroomId());
        if (dto.getMaxStudents() != null) e.setMaxStudents(dto.getMaxStudents());
        if (dto.getScheduleRule() != null) e.setScheduleRule(dto.getScheduleRule());
        if (dto.getStartDate() != null) e.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) e.setEndDate(dto.getEndDate());
        if (dto.getStatus() != null) e.setStatus(dto.getStatus());
        classMapper.updateById(e);
    }

    public void delete(Long id) {
        if (classMapper.selectById(id) == null) throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        // 如果有学员在读则不允许删除
        classMapper.deleteById(id);
    }

    /**
     * 更新班级当前人数（报名/退课时调用）
     */
    public void incrementStudentCount(Long classId, int delta) {
        ClassEntity e = classMapper.selectById(classId);
        if (e == null) throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        e.setCurrentStudents(e.getCurrentStudents() + delta);
        classMapper.updateById(e);
    }

    private ClassDTO toDTO(ClassEntity e) {
        ClassDTO dto = new ClassDTO();
        dto.setId(e.getId()); dto.setCourseId(e.getCourseId());
        dto.setTeacherId(e.getTeacherId()); dto.setAssistantId(e.getAssistantId());
        dto.setClassroomId(e.getClassroomId()); dto.setName(e.getName());
        dto.setMaxStudents(e.getMaxStudents()); dto.setCurrentStudents(e.getCurrentStudents());
        dto.setScheduleRule(e.getScheduleRule()); dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate()); dto.setStatus(e.getStatus());

        // 填充名称
        if (e.getCourseId() != null) {
            CourseEntity c = courseMapper.selectById(e.getCourseId());
            if (c != null) dto.setCourseName(c.getName());
        }
        if (e.getTeacherId() != null) {
            TeacherEntity t = teacherMapper.selectById(e.getTeacherId());
            if (t != null) dto.setTeacherName(t.getName());
        }
        if (e.getClassroomId() != null) {
            ClassroomEntity cr = classroomMapper.selectById(e.getClassroomId());
            if (cr != null) dto.setClassroomName(cr.getName());
        }
        return dto;
    }
}
