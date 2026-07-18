package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.points.service.PointsInternalService;
import com.education.store.module.teaching.entity.EduEvaluation;
import com.education.store.module.teaching.entity.EduEvaluationStudent;
import com.education.store.module.teaching.mapper.EduEvaluationMapper;
import com.education.store.module.teaching.mapper.EduEvaluationStudentMapper;
import com.education.store.module.teaching.service.IEduEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduEvaluationServiceImpl extends ServiceImpl<EduEvaluationMapper, EduEvaluation>
        implements IEduEvaluationService {

    private final EduEvaluationMapper evaluationMapper;
    private final EduEvaluationStudentMapper studentMapper;
    private final PointsInternalService pointsInternalService;

    @Override
    public IPage<EduEvaluation> selectEvaluationList(IPage<EduEvaluation> page, Long scheduleId, Long teacherId, Long studentId, String startDate, String endDate, Integer status) {
        LambdaQueryWrapper<EduEvaluation> wrapper = new LambdaQueryWrapper<>();
        if (scheduleId != null) {
            wrapper.eq(EduEvaluation::getScheduleId, scheduleId);
        }
        if (teacherId != null) {
            wrapper.eq(EduEvaluation::getTeacherId, teacherId);
        }
        if (status != null) {
            wrapper.eq(EduEvaluation::getStatus, status);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(EduEvaluation::getCreateTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(EduEvaluation::getCreateTime, endDate + " 23:59:59");
        }
        wrapper.orderByDesc(EduEvaluation::getCreateTime);
        return evaluationMapper.selectPage(page, wrapper);
    }

    @Override
    public EduEvaluation getDetail(Long id) {
        return evaluationMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduEvaluation saveDraft(Long scheduleId, Long classId, Long teacherId, String summary, List<EduEvaluationStudent> students) {
        // 检查是否已有评价
        EduEvaluation existing = evaluationMapper.selectByScheduleId(scheduleId);
        if (existing != null) {
            // 更新草稿
            existing.setSummary(summary);
            evaluationMapper.updateById(existing);
            // 删除旧明细
            List<EduEvaluationStudent> oldStudents = studentMapper.selectByEvaluationId(existing.getId());
            if (!oldStudents.isEmpty()) {
                studentMapper.deleteBatchIds(oldStudents.stream()
                        .map(EduEvaluationStudent::getId).collect(Collectors.toList()));
            }
            // 保存新明细
            for (EduEvaluationStudent student : students) {
                student.setEvaluationId(existing.getId());
                student.setCreateTime(LocalDateTime.now());
                student.setUpdateTime(LocalDateTime.now());
                studentMapper.insert(student);
            }
            return existing;
        }

        // 新建草稿
        EduEvaluation evaluation = new EduEvaluation();
        evaluation.setScheduleId(scheduleId);
        evaluation.setClassId(classId);
        evaluation.setTeacherId(teacherId);
        evaluation.setSummary(summary);
        evaluation.setStatus(0); // 草稿
        evaluationMapper.insert(evaluation);

        for (EduEvaluationStudent student : students) {
            student.setEvaluationId(evaluation.getId());
            student.setCreateTime(LocalDateTime.now());
            student.setUpdateTime(LocalDateTime.now());
            studentMapper.insert(student);
        }
        return evaluation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduEvaluation submitEvaluation(Long id, String summary, List<EduEvaluationStudent> students, Long teacherId) {
        EduEvaluation evaluation = evaluationMapper.selectById(id);
        if (evaluation == null) {
            throw new BusinessException("404", "评价不存在");
        }
        if (evaluation.getStatus() == 1) {
            throw new BusinessException("STATUS_NOT_ALLOWED", "已提交的评价不可修改");
        }
        evaluation.setSummary(summary);
        evaluation.setStatus(1); // 提交
        evaluation.setSubmitTime(LocalDateTime.now());
        evaluationMapper.updateById(evaluation);

        // 删除旧明细，重新写入
        List<EduEvaluationStudent> oldStudents = studentMapper.selectByEvaluationId(id);
        if (!oldStudents.isEmpty()) {
            studentMapper.deleteBatchIds(oldStudents.stream()
                    .map(EduEvaluationStudent::getId).collect(Collectors.toList()));
        }
        for (EduEvaluationStudent student : students) {
            student.setEvaluationId(id);
            student.setCreateTime(LocalDateTime.now());
            student.setUpdateTime(LocalDateTime.now());
            studentMapper.insert(student);

            // 积分奖励：教评学提交时自动发放积分
            if (student.getRewardPoints() != null && student.getRewardPoints() > 0) {
                try {
                    pointsInternalService.earnPoints(
                            student.getStudentId(),
                            teacherId,
                            "TEACHER",
                            student.getId(),
                            student.getRewardPoints(),
                            student.getRewardReason() != null ? student.getRewardReason() : "课堂表现奖励"
                    );
                } catch (Exception e) {
                    log.error("积分发放失败：evaluationStudentId={}, studentId={}, points={}",
                            student.getId(), student.getStudentId(), student.getRewardPoints(), e);
                }
            }
        }
        return evaluation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEvaluation(Long id, Long currentUserId) {
        EduEvaluation evaluation = evaluationMapper.selectById(id);
        if (evaluation == null) {
            throw new BusinessException("404", "评价不存在");
        }
        if (evaluation.getStatus() == 1) {
            throw new BusinessException("STATUS_NOT_ALLOWED", "已提交的评价不可删除");
        }
        // 删除明细
        List<EduEvaluationStudent> students = studentMapper.selectByEvaluationId(id);
        if (!students.isEmpty()) {
            studentMapper.deleteBatchIds(students.stream()
                    .map(EduEvaluationStudent::getId).collect(Collectors.toList()));
        }
        return removeById(id);
    }
}
