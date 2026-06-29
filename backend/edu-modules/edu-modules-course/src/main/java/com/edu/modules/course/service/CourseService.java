package com.edu.modules.course.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.mybatis.PageParam;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.course.dto.CourseDTO;
import com.edu.modules.course.entity.CourseEntity;
import com.edu.modules.course.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String COURSE_SEQ_KEY = "course:seq";

    public PageResult<CourseDTO> list(PageParam param, String keyword, Integer type, Integer status) {
        Page<CourseEntity> page = new Page<>(param.getPage(), param.getSafePageSize());
        LambdaQueryWrapper<CourseEntity> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(CourseEntity::getName, keyword)
                    .or().like(CourseEntity::getCode, keyword));
        }
        if (type != null) wrapper.eq(CourseEntity::getType, type);
        if (status != null) wrapper.eq(CourseEntity::getStatus, status);
        wrapper.orderByDesc(CourseEntity::getCreatedAt);

        Page<CourseEntity> result = courseMapper.selectPage(page, wrapper);
        return PageResult.of(
                result.getRecords().stream().map(this::toDTO).toList(),
                result.getTotal(), param.getPage(), param.getSafePageSize());
    }

    public CourseDTO getById(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        return toDTO(entity);
    }

    public Long create(CourseDTO dto) {
        CourseEntity entity = new CourseEntity();
        entity.setName(dto.getName());
        entity.setCode(dto.getCode() != null ? dto.getCode() : generateCode());
        entity.setType(dto.getType());
        entity.setSubject(dto.getSubject());
        entity.setGradeRange(dto.getGradeRange());
        entity.setDuration(dto.getDuration() != null ? dto.getDuration() : 120);
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setDescription(dto.getDescription());
        entity.setCoverImage(dto.getCoverImage());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        courseMapper.insert(entity);
        log.info("创建课程成功: id={}, name={}", entity.getId(), entity.getName());
        return entity.getId();
    }

    public void update(Long id, CourseDTO dto) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        if (StrUtil.isNotBlank(dto.getName())) entity.setName(dto.getName());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getSubject() != null) entity.setSubject(dto.getSubject());
        if (dto.getGradeRange() != null) entity.setGradeRange(dto.getGradeRange());
        if (dto.getDuration() != null) entity.setDuration(dto.getDuration());
        if (dto.getUnitPrice() != null) entity.setUnitPrice(dto.getUnitPrice());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        courseMapper.updateById(entity);
    }

    public void delete(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        // 软删除: 下架
        entity.setStatus(0);
        courseMapper.updateById(entity);
    }

    private String generateCode() {
        Long seq = redisTemplate.opsForValue().increment(COURSE_SEQ_KEY);
        return "CRS" + String.format("%06d", seq);
    }

    private CourseDTO toDTO(CourseEntity e) {
        CourseDTO dto = new CourseDTO();
        dto.setId(e.getId()); dto.setName(e.getName()); dto.setCode(e.getCode());
        dto.setType(e.getType()); dto.setSubject(e.getSubject());
        dto.setGradeRange(e.getGradeRange()); dto.setDuration(e.getDuration());
        dto.setUnitPrice(e.getUnitPrice()); dto.setDescription(e.getDescription());
        dto.setCoverImage(e.getCoverImage()); dto.setStatus(e.getStatus());
        return dto;
    }
}
