package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.teaching.entity.EduTeacherEvaluation;
import com.education.store.module.teaching.mapper.EduTeacherEvaluationMapper;
import com.education.store.module.teaching.service.IEduTeacherEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EduTeacherEvaluationServiceImpl extends ServiceImpl<EduTeacherEvaluationMapper, EduTeacherEvaluation>
        implements IEduTeacherEvaluationService {

    private final EduTeacherEvaluationMapper mapper;

    @Override
    public IPage<EduTeacherEvaluation> selectList(IPage<EduTeacherEvaluation> page, Long teacherId, Long studentId, String status, String startDate, String endDate) {
        LambdaQueryWrapper<EduTeacherEvaluation> wrapper = new LambdaQueryWrapper<>();
        if (teacherId != null) {
            wrapper.eq(EduTeacherEvaluation::getTeacherId, teacherId);
        }
        if (studentId != null) {
            wrapper.eq(EduTeacherEvaluation::getStudentId, studentId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(EduTeacherEvaluation::getStatus, status);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(EduTeacherEvaluation::getSubmitTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(EduTeacherEvaluation::getSubmitTime, endDate + " 23:59:59");
        }
        wrapper.orderByDesc(EduTeacherEvaluation::getSubmitTime);
        return mapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduTeacherEvaluation submit(EduTeacherEvaluation evaluation) {
        // 校验是否重复评价
        LambdaQueryWrapper<EduTeacherEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacherEvaluation::getTeacherId, evaluation.getTeacherId());
        wrapper.eq(EduTeacherEvaluation::getStudentId, evaluation.getStudentId());
        wrapper.eq(EduTeacherEvaluation::getScheduleId, evaluation.getScheduleId());
        if (mapper.selectCount(wrapper) > 0) {
            throw new BusinessException("DUPLICATE_EVALUATION", "该课次已有评价记录");
        }
        mapper.insert(evaluation);
        return evaluation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean hideEvaluation(Long id) {
        EduTeacherEvaluation eval = mapper.selectById(id);
        if (eval == null) {
            throw new BusinessException("404", "评价不存在");
        }
        eval.setStatus("HIDDEN");
        return updateById(eval);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreEvaluation(Long id) {
        EduTeacherEvaluation eval = mapper.selectById(id);
        if (eval == null) {
            throw new BusinessException("404", "评价不存在");
        }
        eval.setStatus("ACTIVE");
        return updateById(eval);
    }

    @Override
    public Map<String, Object> getTeacherStats(Long teacherId) {
        Map<String, Object> stats = new HashMap<>();
        BigDecimal avgScore = mapper.getAvgScoreByTeacherId(teacherId);
        int count = mapper.countByTeacherId(teacherId);
        stats.put("teacherId", teacherId);
        stats.put("avgScore", avgScore != null ? avgScore.setScale(1, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        stats.put("evaluationCount", count);
        return stats;
    }
}
