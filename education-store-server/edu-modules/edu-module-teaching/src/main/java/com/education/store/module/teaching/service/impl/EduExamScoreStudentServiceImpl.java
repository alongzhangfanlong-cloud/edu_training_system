package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.teaching.entity.EduExamScoreStudent;
import com.education.store.module.teaching.mapper.EduExamScoreStudentMapper;
import com.education.store.module.teaching.service.IEduExamScoreStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduExamScoreStudentServiceImpl extends ServiceImpl<EduExamScoreStudentMapper, EduExamScoreStudent>
        implements IEduExamScoreStudentService {

    private final EduExamScoreStudentMapper studentMapper;

    @Override
    public List<EduExamScoreStudent> selectByExamScoreId(Long examScoreId) {
        return studentMapper.selectByExamScoreId(examScoreId);
    }

    @Override
    public List<EduExamScoreStudent> selectByStudentId(Long studentId) {
        return studentMapper.selectByStudentId(studentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByExamScoreId(Long examScoreId) {
        List<EduExamScoreStudent> list = studentMapper.selectByExamScoreId(examScoreId);
        if (!list.isEmpty()) {
            studentMapper.deleteBatchIds(list.stream()
                    .map(EduExamScoreStudent::getId).collect(Collectors.toList()));
        }
    }
}
