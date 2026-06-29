package com.edu.modules.student.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.enums.HourTransactionTypeEnum;
import com.edu.common.core.enums.StudentStatusEnum;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.consume.entity.HourTransactionEntity;
import com.edu.modules.consume.mapper.HourTransactionMapper;
import com.edu.modules.consume.mapper.StudentClassMapper;
import com.edu.modules.student.dto.*;
import com.edu.modules.student.entity.StudentEntity;
import com.edu.modules.student.entity.StudentParentEntity;
import com.edu.modules.student.mapper.StudentMapper;
import com.edu.modules.student.mapper.StudentParentMapper;
import com.edu.modules.student.spi.StudentExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService implements StudentExternalService {

    private final StudentMapper studentMapper;
    private final StudentParentMapper studentParentMapper;
    private final HourTransactionMapper hourTransactionMapper;
    private final StudentClassMapper studentClassMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String STUDENT_SEQ_KEY = "student:seq";

    /**
     * 学员列表（分页 + 搜索 + 筛选）
     */
    public PageResult<StudentListItemDTO> list(StudentQueryParam param) {
        Page<StudentListItemDTO> page = new Page<>(param.getPage(), param.getSafePageSize());
        Page<StudentListItemDTO> result = studentMapper.selectStudentPage(
                page, param.getKeyword(), param.getStatus(), param.getClassId());

        return PageResult.of(result.getRecords(), result.getTotal(), param.getPage(), param.getSafePageSize());
    }

    /**
     * 学员详情
     */
    @Override
    public StudentDetailDTO getStudentDetail(Long studentId) {
        StudentEntity student = studentMapper.selectById(studentId);
        if (student == null || student.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }

        StudentDetailDTO dto = new StudentDetailDTO();
        dto.setId(student.getId());
        dto.setStudentNo(student.getStudentNo());
        dto.setName(student.getName());
        dto.setAvatar(student.getAvatar());
        dto.setGender(student.getGender());
        dto.setBirthday(student.getBirthday());
        dto.setGrade(student.getGrade());
        dto.setSchool(student.getSchool());
        dto.setPhone(student.getPhone());
        dto.setTags(parseTags(student.getTags()));
        dto.setStatus(student.getStatus());
        dto.setSource(student.getSource());
        dto.setRemark(student.getRemark());

        // 家长信息
        List<StudentParentEntity> parents = studentParentMapper.selectList(
                new LambdaQueryWrapper<StudentParentEntity>()
                        .eq(StudentParentEntity::getStudentId, studentId));
        dto.setParents(parents.stream().map(p -> {
            StudentDetailDTO.ParentDTO pd = new StudentDetailDTO.ParentDTO();
            pd.setId(p.getId());
            pd.setParentName(p.getParentName());
            pd.setRelation(p.getRelation());
            pd.setPhone(p.getPhone());
            pd.setIsPrimary(p.getIsPrimary() == 1);
            return pd;
        }).collect(Collectors.toList()));

        // 课程信息 - Phase 1简化: 空列表, Week 5完善
        dto.setCourses(Collections.emptyList());

        // 考勤统计 - Phase 1简化
        StudentDetailDTO.AttendanceStatsDTO stats = new StudentDetailDTO.AttendanceStatsDTO();
        stats.setTotal(0);
        stats.setPresent(0);
        stats.setLeave(0);
        stats.setAbsent(0);
        stats.setPresentRate(java.math.BigDecimal.ZERO);
        dto.setAttendanceStats(stats);

        return dto;
    }

    /**
     * 新增学员
     */
    @Transactional
    public Long create(CreateStudentRequest request) {
        // 生成学员编号
        String studentNo = generateStudentNo();

        StudentEntity student = new StudentEntity();
        student.setStudentNo(studentNo);
        student.setName(request.getName());
        student.setPhone(request.getPhone());
        student.setGender(request.getGender());
        student.setBirthday(request.getBirthday());
        student.setGrade(request.getGrade());
        student.setSchool(request.getSchool());
        student.setTags(request.getTags() != null ? JSONUtil.toJsonStr(request.getTags()) : null);
        student.setSource(request.getSource());
        student.setRemark(request.getRemark());
        student.setStatus(StudentStatusEnum.ACTIVE.getCode()); // 默认在读

        studentMapper.insert(student);
        log.info("创建学员成功: id={}, studentNo={}", student.getId(), studentNo);

        // 保存家长信息
        if (request.getParents() != null && !request.getParents().isEmpty()) {
            for (CreateStudentRequest.ParentInfo pi : request.getParents()) {
                StudentParentEntity parent = new StudentParentEntity();
                parent.setStudentId(student.getId());
                parent.setParentName(pi.getName());
                parent.setRelation(pi.getRelation());
                parent.setPhone(pi.getPhone());
                parent.setIsPrimary(pi.getIsPrimary() != null && pi.getIsPrimary() ? 1 : 0);
                studentParentMapper.insert(parent);
            }
        }

        return student.getId();
    }

    /**
     * 更新学员
     */
    @Transactional
    public void update(Long id, UpdateStudentRequest request) {
        StudentEntity student = studentMapper.selectById(id);
        if (student == null || student.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }

        if (StrUtil.isNotBlank(request.getName())) {
            student.setName(request.getName());
        }
        if (request.getPhone() != null) {
            student.setPhone(request.getPhone());
        }
        if (request.getGender() != null) {
            student.setGender(request.getGender());
        }
        if (request.getGrade() != null) {
            student.setGrade(request.getGrade());
        }
        if (request.getSchool() != null) {
            student.setSchool(request.getSchool());
        }
        if (request.getTags() != null) {
            student.setTags(JSONUtil.toJsonStr(request.getTags()));
        }
        if (request.getSource() != null) {
            student.setSource(request.getSource());
        }
        if (request.getRemark() != null) {
            student.setRemark(request.getRemark());
        }
        if (request.getStatus() != null) {
            student.setStatus(request.getStatus());
        }

        studentMapper.updateById(student);
        log.info("更新学员成功: id={}", id);
    }

    /**
     * 删除学员（逻辑删除）
     */
    public void delete(Long id) {
        StudentEntity student = studentMapper.selectById(id);
        if (student == null || student.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }
        studentMapper.deleteById(id);
        log.info("删除学员成功: id={}", id);
    }

    @Override
    public boolean existsById(Long studentId) {
        StudentEntity student = studentMapper.selectById(studentId);
        return student != null && student.getIsDeleted() == 0;
    }

    /**
     * 生成学员编号 STU20240001
     */
    private String generateStudentNo() {
        String year = String.valueOf(Year.now().getValue());
        Long seq = redisTemplate.opsForValue().increment(STUDENT_SEQ_KEY + ":" + year);
        return "STU" + year + String.format("%05d", seq);
    }

    /**
     * 学员课时明细（真实实现，查询hour_transactions表）
     */
    public PageResult<HourTransactionDTO> hourDetails(Long studentId, Long classId, int page, int pageSize) {
        LambdaQueryWrapper<HourTransactionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HourTransactionEntity::getStudentId, studentId);
        wrapper.orderByDesc(HourTransactionEntity::getCreatedAt);

        Page<HourTransactionEntity> p = new Page<>(page, pageSize);
        Page<HourTransactionEntity> result = hourTransactionMapper.selectPage(p, wrapper);

        List<HourTransactionDTO> list = result.getRecords().stream().map(e -> {
            HourTransactionDTO dto = new HourTransactionDTO();
            dto.setId(e.getId());
            dto.setType(e.getType());
            dto.setTypeName(HourTransactionTypeEnum.getDesc(e.getType()));
            dto.setChangeHours(e.getChangeHours());
            dto.setBeforeHours(e.getBeforeHours());
            dto.setAfterHours(e.getAfterHours());
            dto.setRemark(e.getRemark());
            dto.setCreatedAt(e.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        return PageResult.of(list, result.getTotal(), page, pageSize);
    }

    @SuppressWarnings("unchecked")
    private List<String> parseTags(String tagsJson) {
        if (StrUtil.isBlank(tagsJson)) {
            return Collections.emptyList();
        }
        try {
            return JSONUtil.toBean(tagsJson, List.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
