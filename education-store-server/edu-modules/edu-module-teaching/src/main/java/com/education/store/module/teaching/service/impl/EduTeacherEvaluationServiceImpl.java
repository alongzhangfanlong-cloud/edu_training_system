package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.teaching.entity.EduTeacher;
import com.education.store.module.teaching.entity.EduTeacherEvaluation;
import com.education.store.module.teaching.mapper.EduTeacherEvaluationMapper;
import com.education.store.module.teaching.mapper.EduTeacherMapper;
import com.education.store.module.teaching.service.IEduTeacherEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduTeacherEvaluationServiceImpl extends ServiceImpl<EduTeacherEvaluationMapper, EduTeacherEvaluation>
        implements IEduTeacherEvaluationService {

    private final EduTeacherEvaluationMapper mapper;
    private final EduTeacherMapper teacherMapper;
    private final SysUserMapper sysUserMapper;
    private final EduStudentMapper studentMapper;

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
        IPage<EduTeacherEvaluation> result = mapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
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
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> result = new HashMap<>();
        // 全部教师列表
        List<EduTeacher> teachers = teacherMapper.selectList(null);
        List<Map<String, Object>> teacherList = new ArrayList<>();
        for (EduTeacher t : teachers) {
            Map<String, Object> item = new HashMap<>();
            item.put("teacherId", t.getId());
            item.put("teacherName", t.getRealName());
            BigDecimal avgScore = mapper.getAvgScoreByTeacherId(t.getId());
            item.put("avgScore", avgScore != null ? avgScore.setScale(1, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            item.put("evalCount", mapper.countByTeacherId(t.getId()));
            teacherList.add(item);
        }
        result.put("teacherList", teacherList);

        // 评分分布（1-5星计数）
        List<EduTeacherEvaluation> allActive = mapper.selectList(
                new LambdaQueryWrapper<EduTeacherEvaluation>().eq(EduTeacherEvaluation::getStatus, "ACTIVE"));
        int[] ratingCounts = new int[6]; // 0 unused
        for (EduTeacherEvaluation e : allActive) {
            int score = e.getRatingScore() != null ? e.getRatingScore() : 0;
            if (score >= 1 && score <= 5) ratingCounts[score]++;
        }
        int total = Arrays.stream(ratingCounts).sum();
        List<Map<String, Object>> distribution = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> d = new HashMap<>();
            d.put("rating", i);
            d.put("label", i + "星");
            d.put("count", ratingCounts[i]);
            d.put("total", total);
            distribution.add(d);
        }
        result.put("distribution", distribution);

        // 标签频率（简单汇总）
        Map<String, Integer> tagCount = new HashMap<>();
        for (EduTeacherEvaluation e : allActive) {
            if (e.getTags() != null && !e.getTags().isEmpty()) {
                try {
                    String[] tags = e.getTags().replace("[", "").replace("]", "").replace("\"", "").split(",");
                    for (String tag : tags) {
                        String trimmed = tag.trim();
                        if (!trimmed.isEmpty()) {
                            tagCount.merge(trimmed, 1, Integer::sum);
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        List<Map<String, Object>> tagFrequency = tagCount.entrySet().stream()
                .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("tag", e.getKey()); m.put("count", e.getValue()); return m; })
                .sorted((a, b) -> Integer.compare((int) b.get("count"), (int) a.get("count")))
                .collect(Collectors.toList());
        result.put("tagFrequency", tagFrequency);

        return result;
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

    private void fillNamesForList(List<EduTeacherEvaluation> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> teacherIds = new HashSet<>();
        Set<Long> studentIds = new HashSet<>();
        for (EduTeacherEvaluation e : list) {
            if (e.getTeacherId() != null) teacherIds.add(e.getTeacherId());
            if (e.getStudentId() != null) studentIds.add(e.getStudentId());
        }
        Map<Long, SysUser> teacherMap = teacherIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(teacherIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, EduStudent> studentMap = studentIds.isEmpty() ? Collections.emptyMap()
                : studentMapper.selectBatchIds(studentIds).stream().collect(Collectors.toMap(EduStudent::getId, s -> s));
        for (EduTeacherEvaluation e : list) {
            if (e.getTeacherId() != null) {
                SysUser u = teacherMap.get(e.getTeacherId());
                if (u != null) e.setTeacherName(u.getRealName());
            }
            if (e.getStudentId() != null) {
                EduStudent s = studentMap.get(e.getStudentId());
                if (s != null) e.setStudentName(s.getName());
            }
        }
    }
}
