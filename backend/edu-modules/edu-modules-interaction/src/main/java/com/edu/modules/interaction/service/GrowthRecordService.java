package com.edu.modules.interaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.modules.course.mapper.TeacherMapper;
import com.edu.modules.interaction.dto.GrowthRecordDTO;
import com.edu.modules.interaction.dto.GrowthRecordRequest;
import com.edu.modules.interaction.entity.GrowthRecordEntity;
import com.edu.modules.interaction.mapper.GrowthRecordMapper;
import com.edu.modules.student.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrowthRecordService {

    private final GrowthRecordMapper growthRecordMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;

    public Long create(GrowthRecordRequest request) {
        GrowthRecordEntity entity = new GrowthRecordEntity();
        entity.setStudentId(request.getStudentId());
        entity.setTeacherId(request.getTeacherId());
        entity.setRecordDate(LocalDate.now());
        entity.setContent(request.getContent());
        entity.setScore(request.getScore() != null ? request.getScore() : 3);
        growthRecordMapper.insert(entity);
        log.info("新增成长档案: studentId={}, id={}", request.getStudentId(), entity.getId());
        return entity.getId();
    }

    public List<GrowthRecordDTO> list(Long studentId, Long teacherId) {
        LambdaQueryWrapper<GrowthRecordEntity> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) wrapper.eq(GrowthRecordEntity::getStudentId, studentId);
        if (teacherId != null) wrapper.eq(GrowthRecordEntity::getTeacherId, teacherId);
        wrapper.orderByDesc(GrowthRecordEntity::getCreatedAt);

        List<GrowthRecordDTO> dtos = growthRecordMapper.selectList(wrapper).stream().map(e -> {
            GrowthRecordDTO dto = new GrowthRecordDTO();
            dto.setId(e.getId());
            dto.setStudentId(e.getStudentId());
            dto.setTeacherId(e.getTeacherId());
            dto.setRecordDate(e.getRecordDate());
            dto.setContent(e.getContent());
            dto.setScore(e.getScore());
            dto.setCreatedAt(e.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        fillNames(dtos);
        return dtos;
    }

    private void fillNames(List<GrowthRecordDTO> dtos) {
        if (dtos.isEmpty()) return;
        Set<Long> studentIds = new HashSet<>();
        Set<Long> teacherIds = new HashSet<>();
        for (GrowthRecordDTO d : dtos) {
            if (d.getStudentId() != null) studentIds.add(d.getStudentId());
            if (d.getTeacherId() != null) teacherIds.add(d.getTeacherId());
        }
        Map<Long, String> studentNameMap = new HashMap<>();
        Map<Long, String> teacherNameMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            studentMapper.selectBatchIds(studentIds).forEach(s -> studentNameMap.put(s.getId(), s.getName()));
        }
        if (!teacherIds.isEmpty()) {
            teacherMapper.selectBatchIds(teacherIds).forEach(t -> teacherNameMap.put(t.getId(), t.getName()));
        }
        for (GrowthRecordDTO d : dtos) {
            d.setStudentName(studentNameMap.get(d.getStudentId()));
            d.setTeacherName(teacherNameMap.get(d.getTeacherId()));
        }
    }

    public GrowthRecordDTO getById(Long id) {
        GrowthRecordEntity entity = growthRecordMapper.selectById(id);
        if (entity == null) return null;
        GrowthRecordDTO dto = new GrowthRecordDTO();
        dto.setId(entity.getId());
        dto.setStudentId(entity.getStudentId());
        dto.setTeacherId(entity.getTeacherId());
        dto.setRecordDate(entity.getRecordDate());
        dto.setContent(entity.getContent());
        dto.setScore(entity.getScore());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public void update(Long id, GrowthRecordRequest request) {
        GrowthRecordEntity entity = growthRecordMapper.selectById(id);
        if (entity == null) throw new RuntimeException("成长档案不存在");
        entity.setContent(request.getContent());
        if (request.getScore() != null) entity.setScore(request.getScore());
        growthRecordMapper.updateById(entity);
    }

    public void delete(Long id) {
        growthRecordMapper.deleteById(id);
    }
}
