package com.edu.modules.interaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.modules.interaction.dto.HomeworkDTO;
import com.edu.modules.interaction.dto.HomeworkRequest;
import com.edu.modules.interaction.entity.HomeworkEntity;
import com.edu.modules.interaction.mapper.HomeworkMapper;
import com.edu.modules.course.entity.ClassEntity;
import com.edu.modules.course.entity.CourseEntity;
import com.edu.modules.course.entity.TeacherEntity;
import com.edu.modules.course.mapper.ClassMapper;
import com.edu.modules.course.mapper.CourseMapper;
import com.edu.modules.course.mapper.TeacherMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkMapper homeworkMapper;
    private final ClassMapper classMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;

    public Long create(HomeworkRequest request) {
        HomeworkEntity entity = new HomeworkEntity();
        entity.setScheduleId(request.getScheduleId());
        entity.setClassId(request.getClassId());
        entity.setTeacherId(request.getTeacherId());
        entity.setContent(request.getContent());
        entity.setImages(request.getImages());
        homeworkMapper.insert(entity);
        log.info("布置作业成功: id={}", entity.getId());
        return entity.getId();
    }

    public List<HomeworkDTO> list(Long classId, Long teacherId) {
        LambdaQueryWrapper<HomeworkEntity> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) wrapper.eq(HomeworkEntity::getClassId, classId);
        if (teacherId != null) wrapper.eq(HomeworkEntity::getTeacherId, teacherId);
        wrapper.orderByDesc(HomeworkEntity::getCreatedAt);

        return homeworkMapper.selectList(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public HomeworkDTO getById(Long id) {
        HomeworkEntity entity = homeworkMapper.selectById(id);
        if (entity == null) return null;
        return toDTO(entity);
    }

    public void update(Long id, HomeworkRequest request) {
        HomeworkEntity entity = homeworkMapper.selectById(id);
        if (entity == null) throw new RuntimeException("作业不存在");
        entity.setContent(request.getContent());
        entity.setImages(request.getImages());
        if (request.getClassId() != null) entity.setClassId(request.getClassId());
        if (request.getTeacherId() != null) entity.setTeacherId(request.getTeacherId());
        homeworkMapper.updateById(entity);
    }

    public void delete(Long id) {
        homeworkMapper.deleteById(id);
    }

    private HomeworkDTO toDTO(HomeworkEntity e) {
        HomeworkDTO dto = new HomeworkDTO();
        dto.setId(e.getId());
        dto.setScheduleId(e.getScheduleId());
        dto.setClassId(e.getClassId());
        dto.setTeacherId(e.getTeacherId());
        dto.setContent(e.getContent());
        dto.setImages(e.getImages());
        dto.setCreatedAt(e.getCreatedAt());

        // join names
        if (e.getClassId() != null) {
            ClassEntity cls = classMapper.selectById(e.getClassId());
            if (cls != null) {
                dto.setClassName(cls.getName());
                if (cls.getCourseId() != null) {
                    CourseEntity course = courseMapper.selectById(cls.getCourseId());
                    if (course != null) dto.setCourseName(course.getName());
                }
            }
        }
        if (e.getTeacherId() != null) {
            TeacherEntity teacher = teacherMapper.selectById(e.getTeacherId());
            if (teacher != null) dto.setTeacherName(teacher.getName());
        }
        return dto;
    }
}
