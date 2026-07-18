package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.teaching.entity.EduEvaluationStudent;
import com.education.store.module.teaching.mapper.EduEvaluationStudentMapper;
import com.education.store.module.teaching.service.IEduEvaluationStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduEvaluationStudentServiceImpl extends ServiceImpl<EduEvaluationStudentMapper, EduEvaluationStudent>
        implements IEduEvaluationStudentService {

    private final EduEvaluationStudentMapper studentMapper;

    @Override
    public List<EduEvaluationStudent> selectByEvaluationId(Long evaluationId) {
        return studentMapper.selectByEvaluationId(evaluationId);
    }

    @Override
    public List<EduEvaluationStudent> selectByStudentId(Long studentId) {
        return studentMapper.selectByStudentId(studentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByEvaluationId(Long evaluationId) {
        List<EduEvaluationStudent> list = studentMapper.selectByEvaluationId(evaluationId);
        if (!list.isEmpty()) {
            studentMapper.deleteBatchIds(list.stream()
                    .map(EduEvaluationStudent::getId).collect(Collectors.toList()));
        }
    }
}
