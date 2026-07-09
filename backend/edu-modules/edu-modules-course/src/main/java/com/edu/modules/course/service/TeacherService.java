package com.edu.modules.course.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.mybatis.PageParam;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.course.dto.TeacherDTO;
import com.edu.modules.course.entity.TeacherEntity;
import com.edu.modules.course.mapper.TeacherMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherMapper teacherMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TEACHER_SEQ_KEY = "teacher:seq";

    public PageResult<TeacherDTO> list(PageParam param, String keyword, Integer status) {
        Page<TeacherEntity> page = new Page<>(param.getPage(), param.getSafePageSize());
        LambdaQueryWrapper<TeacherEntity> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(TeacherEntity::getName, keyword)
                    .or().like(TeacherEntity::getPhone, keyword));
        }
        if (status != null) wrapper.eq(TeacherEntity::getStatus, status);
        wrapper.orderByDesc(TeacherEntity::getCreatedAt);
        Page<TeacherEntity> result = teacherMapper.selectPage(page, wrapper);
        return PageResult.of(
                result.getRecords().stream().map(this::toDTO).toList(),
                result.getTotal(), param.getPage(), param.getSafePageSize());
    }

    public TeacherDTO getById(Long id) {
        TeacherEntity e = teacherMapper.selectById(id);
        if (e == null) throw new BusinessException(ErrorCode.TEACHER_NOT_FOUND);
        return toDTO(e);
    }

    public Long create(TeacherDTO dto) {
        TeacherEntity e = new TeacherEntity();
        e.setTeacherNo(generateTeacherNo());
        e.setName(dto.getName()); e.setPhone(dto.getPhone()); e.setAvatar(dto.getAvatar());
        e.setGender(dto.getGender());
        e.setSubjects(dto.getSubjects() != null ? JSONUtil.toJsonStr(dto.getSubjects()) : null);
        e.setIntroduction(dto.getIntroduction()); e.setHireDate(dto.getHireDate());
        e.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        teacherMapper.insert(e);
        return e.getId();
    }

    public void update(Long id, TeacherDTO dto) {
        TeacherEntity e = teacherMapper.selectById(id);
        if (e == null) throw new BusinessException(ErrorCode.TEACHER_NOT_FOUND);
        if (StrUtil.isNotBlank(dto.getName())) e.setName(dto.getName());
        if (StrUtil.isNotBlank(dto.getPhone())) e.setPhone(dto.getPhone());
        if (dto.getGender() != null) e.setGender(dto.getGender());
        if (dto.getSubjects() != null) e.setSubjects(JSONUtil.toJsonStr(dto.getSubjects()));
        if (StrUtil.isNotBlank(dto.getIntroduction())) e.setIntroduction(dto.getIntroduction());
        if (dto.getHireDate() != null) e.setHireDate(dto.getHireDate());
        if (dto.getStatus() != null) e.setStatus(dto.getStatus());
        teacherMapper.updateById(e);
    }

    public void delete(Long id) {
        if (teacherMapper.selectById(id) == null) throw new BusinessException(ErrorCode.TEACHER_NOT_FOUND);
        teacherMapper.deleteById(id);
    }

    private String generateTeacherNo() {
        String year = String.valueOf(Year.now().getValue());

        // 确保 Redis 序列号不低于数据库中已存在的最大编号
        String maxNo = teacherMapper.selectMaxTeacherNo(year);
        Long dbMax = 0L;
        if (maxNo != null && maxNo.startsWith("TCH" + year)) {
            try { dbMax = Long.parseLong(maxNo.substring(7)); } catch (NumberFormatException ignored) {}
        }
        Long redisSeq = redisTemplate.opsForValue().get(TEACHER_SEQ_KEY + ":" + year) != null
                ? Long.parseLong(redisTemplate.opsForValue().get(TEACHER_SEQ_KEY + ":" + year))
                : 0L;
        if (redisSeq < dbMax) {
            redisTemplate.opsForValue().set(TEACHER_SEQ_KEY + ":" + year, String.valueOf(dbMax));
        }

        Long seq = redisTemplate.opsForValue().increment(TEACHER_SEQ_KEY + ":" + year);
        return "TCH" + year + String.format("%05d", seq);
    }

    @SuppressWarnings("unchecked")
    private TeacherDTO toDTO(TeacherEntity e) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(e.getId()); dto.setTeacherNo(e.getTeacherNo());
        dto.setName(e.getName()); dto.setAvatar(e.getAvatar()); dto.setPhone(e.getPhone());
        dto.setGender(e.getGender()); dto.setIntroduction(e.getIntroduction());
        dto.setHireDate(e.getHireDate()); dto.setStatus(e.getStatus());
        if (StrUtil.isNotBlank(e.getSubjects())) {
            try { dto.setSubjects(JSONUtil.toBean(e.getSubjects(), List.class)); }
            catch (Exception ex) { dto.setSubjects(Collections.emptyList()); }
        }
        return dto;
    }
}
