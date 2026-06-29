package com.edu.modules.course.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.mybatis.PageParam;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.course.dto.ClassroomDTO;
import com.edu.modules.course.entity.ClassroomEntity;
import com.edu.modules.course.mapper.ClassroomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomMapper classroomMapper;

    public PageResult<ClassroomDTO> list(PageParam param, String keyword) {
        Page<ClassroomEntity> page = new Page<>(param.getPage(), param.getSafePageSize());
        LambdaQueryWrapper<ClassroomEntity> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(ClassroomEntity::getName, keyword);
        }
        wrapper.orderByDesc(ClassroomEntity::getCreatedAt);
        Page<ClassroomEntity> result = classroomMapper.selectPage(page, wrapper);
        return PageResult.of(
                result.getRecords().stream().map(this::toDTO).toList(),
                result.getTotal(), param.getPage(), param.getSafePageSize());
    }

    public ClassroomDTO getById(Long id) {
        ClassroomEntity e = classroomMapper.selectById(id);
        if (e == null) throw new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND);
        return toDTO(e);
    }

    public Long create(ClassroomDTO dto) {
        ClassroomEntity e = new ClassroomEntity();
        e.setName(dto.getName()); e.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : 30);
        e.setLocation(dto.getLocation()); e.setLatitude(dto.getLatitude());
        e.setLongitude(dto.getLongitude());
        e.setFacilities(dto.getFacilities() != null ? JSONUtil.toJsonStr(dto.getFacilities()) : null);
        e.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        classroomMapper.insert(e);
        return e.getId();
    }

    public void update(Long id, ClassroomDTO dto) {
        ClassroomEntity e = classroomMapper.selectById(id);
        if (e == null) throw new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND);
        if (StrUtil.isNotBlank(dto.getName())) e.setName(dto.getName());
        if (dto.getCapacity() != null) e.setCapacity(dto.getCapacity());
        if (dto.getLocation() != null) e.setLocation(dto.getLocation());
        if (dto.getStatus() != null) e.setStatus(dto.getStatus());
        classroomMapper.updateById(e);
    }

    public void delete(Long id) {
        if (classroomMapper.selectById(id) == null) throw new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND);
        classroomMapper.deleteById(id);
    }

    @SuppressWarnings("unchecked")
    private ClassroomDTO toDTO(ClassroomEntity e) {
        ClassroomDTO dto = new ClassroomDTO();
        dto.setId(e.getId()); dto.setName(e.getName()); dto.setCapacity(e.getCapacity());
        dto.setLocation(e.getLocation()); dto.setLatitude(e.getLatitude());
        dto.setLongitude(e.getLongitude()); dto.setStatus(e.getStatus());
        if (StrUtil.isNotBlank(e.getFacilities())) {
            try { dto.setFacilities(JSONUtil.toBean(e.getFacilities(), List.class)); }
            catch (Exception ex) { dto.setFacilities(Collections.emptyList()); }
        }
        return dto;
    }
}
