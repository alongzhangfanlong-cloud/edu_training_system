package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.course.entity.EduSubject;
import com.education.store.module.course.mapper.EduSubjectMapper;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.teaching.entity.EduExamScore;
import com.education.store.module.teaching.entity.EduExamScoreStudent;
import com.education.store.module.teaching.mapper.EduExamScoreMapper;
import com.education.store.module.teaching.mapper.EduExamScoreStudentMapper;
import com.education.store.module.teaching.service.IEduExamScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduExamScoreServiceImpl extends ServiceImpl<EduExamScoreMapper, EduExamScore>
        implements IEduExamScoreService {

    private final EduExamScoreMapper examMapper;
    private final EduExamScoreStudentMapper studentMapper;
    private final EduClassMapper classMapper;
    private final EduSubjectMapper subjectMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<EduExamScore> selectExamList(IPage<EduExamScore> page, Long classId, Long subjectId, Long studentId, String examType, String startDate, String endDate) {
        LambdaQueryWrapper<EduExamScore> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(EduExamScore::getClassId, classId);
        }
        if (subjectId != null) {
            wrapper.eq(EduExamScore::getSubjectId, subjectId);
        }
        if (studentId != null) {
            wrapper.apply("id IN (SELECT exam_score_id FROM edu_exam_score_student WHERE student_id = {0})", studentId);
        }
        if (examType != null && !examType.isEmpty()) {
            wrapper.eq(EduExamScore::getExamType, examType);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(EduExamScore::getExamDate, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(EduExamScore::getExamDate, endDate);
        }
        wrapper.orderByDesc(EduExamScore::getExamDate);
        IPage<EduExamScore> result = examMapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduExamScore addExam(EduExamScore exam, List<EduExamScoreStudent> students) {
        examMapper.insert(exam);
        for (EduExamScoreStudent student : students) {
            student.setExamScoreId(exam.getId());
            student.setCreateTime(LocalDateTime.now());
            student.setUpdateTime(LocalDateTime.now());
            studentMapper.insert(student);
        }
        return exam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateExam(EduExamScore exam, List<EduExamScoreStudent> students) {
        EduExamScore existing = examMapper.selectById(exam.getId());
        if (existing == null) {
            throw new BusinessException("404", "考试记录不存在");
        }
        examMapper.updateById(exam);

        // 删除旧成绩明细
        List<EduExamScoreStudent> oldStudents = studentMapper.selectByExamScoreId(exam.getId());
        if (!oldStudents.isEmpty()) {
            studentMapper.deleteBatchIds(oldStudents.stream()
                    .map(EduExamScoreStudent::getId).collect(Collectors.toList()));
        }
        // 写入新成绩
        for (EduExamScoreStudent student : students) {
            student.setExamScoreId(exam.getId());
            student.setCreateTime(LocalDateTime.now());
            student.setUpdateTime(LocalDateTime.now());
            studentMapper.insert(student);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExam(Long id, Long currentUserId) {
        EduExamScore existing = examMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("404", "考试记录不存在");
        }
        List<EduExamScoreStudent> students = studentMapper.selectByExamScoreId(id);
        if (!students.isEmpty()) {
            studentMapper.deleteBatchIds(students.stream()
                    .map(EduExamScoreStudent::getId).collect(Collectors.toList()));
        }
        return removeById(id);
    }

    @Override
    public List<EduExamScoreStudent> getStudentsByExamId(Long examScoreId) {
        return studentMapper.selectByExamScoreId(examScoreId);
    }

    private void fillNamesForList(List<EduExamScore> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> classIds = new HashSet<>();
        Set<Long> subjectIds = new HashSet<>();
        Set<Long> teacherIds = new HashSet<>();
        for (EduExamScore e : list) {
            if (e.getClassId() != null) classIds.add(e.getClassId());
            if (e.getSubjectId() != null) subjectIds.add(e.getSubjectId());
            if (e.getTeacherId() != null) teacherIds.add(e.getTeacherId());
        }
        Map<Long, EduClass> classMap = classIds.isEmpty() ? Collections.emptyMap()
                : classMapper.selectBatchIds(classIds).stream().collect(Collectors.toMap(EduClass::getId, c -> c));
        Map<Long, EduSubject> subjectMap = subjectIds.isEmpty() ? Collections.emptyMap()
                : subjectMapper.selectBatchIds(subjectIds).stream().collect(Collectors.toMap(EduSubject::getId, s -> s));
        Map<Long, SysUser> teacherMap = teacherIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(teacherIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        for (EduExamScore e : list) {
            if (e.getClassId() != null) {
                EduClass c = classMap.get(e.getClassId());
                if (c != null) e.setClassName(c.getName());
            }
            if (e.getSubjectId() != null) {
                EduSubject s = subjectMap.get(e.getSubjectId());
                if (s != null) e.setSubjectName(s.getName());
            }
            if (e.getTeacherId() != null) {
                SysUser u = teacherMap.get(e.getTeacherId());
                if (u != null) e.setTeacherName(u.getRealName());
            }
        }
    }
}
