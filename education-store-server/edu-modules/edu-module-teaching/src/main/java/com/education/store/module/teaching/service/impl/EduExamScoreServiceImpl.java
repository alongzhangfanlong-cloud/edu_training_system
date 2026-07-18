package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.teaching.entity.EduExamScore;
import com.education.store.module.teaching.entity.EduExamScoreStudent;
import com.education.store.module.teaching.mapper.EduExamScoreMapper;
import com.education.store.module.teaching.mapper.EduExamScoreStudentMapper;
import com.education.store.module.teaching.service.IEduExamScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EduExamScoreServiceImpl extends ServiceImpl<EduExamScoreMapper, EduExamScore>
        implements IEduExamScoreService {

    private final EduExamScoreMapper examMapper;
    private final EduExamScoreStudentMapper studentMapper;

    @Override
    public IPage<EduExamScore> selectExamList(IPage<EduExamScore> page, Long classId, Long subjectId, String examType, String startDate, String endDate) {
        LambdaQueryWrapper<EduExamScore> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            wrapper.eq(EduExamScore::getClassId, classId);
        }
        if (subjectId != null) {
            wrapper.eq(EduExamScore::getSubjectId, subjectId);
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
        return examMapper.selectPage(page, wrapper);
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
        for (EduExamScoreStudent s : oldStudents) {
            studentMapper.deleteById(s.getId());
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
        for (EduExamScoreStudent s : students) {
            studentMapper.deleteById(s.getId());
        }
        return removeById(id);
    }

    @Override
    public List<EduExamScoreStudent> getStudentsByExamId(Long examScoreId) {
        return studentMapper.selectByExamScoreId(examScoreId);
    }
}
