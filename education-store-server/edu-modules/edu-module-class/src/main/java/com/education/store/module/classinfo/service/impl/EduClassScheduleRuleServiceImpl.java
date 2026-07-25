package com.education.store.module.classinfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.classinfo.entity.EduClassScheduleRule;
import com.education.store.module.classinfo.mapper.EduClassScheduleRuleMapper;
import com.education.store.module.classinfo.service.IEduClassScheduleRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 排课规则 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduClassScheduleRuleServiceImpl extends ServiceImpl<EduClassScheduleRuleMapper, EduClassScheduleRule>
        implements IEduClassScheduleRuleService {

    private final EduClassScheduleRuleMapper scheduleRuleMapper;

    @Override
    public List<EduClassScheduleRule> selectByClassId(Long classId) {
        LambdaQueryWrapper<EduClassScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduClassScheduleRule::getClassId, classId);
        return scheduleRuleMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long classId, List<EduClassScheduleRule> rules) {
        // 校验每条规则
        for (EduClassScheduleRule rule : rules) {
            validateRule(rule);
        }

        // 删除旧规则
        LambdaQueryWrapper<EduClassScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduClassScheduleRule::getClassId, classId);
        scheduleRuleMapper.delete(wrapper);

        // 批量插入新规则
        for (EduClassScheduleRule rule : rules) {
            rule.setClassId(classId);
            scheduleRuleMapper.insert(rule);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRuleById(Long id) {
        return scheduleRuleMapper.deleteById(id);
    }

    /** 校验规则 */
    private void validateRule(EduClassScheduleRule rule) {
        if (rule.getRuleType() == null) {
            throw new BusinessException("规则类型不能为空");
        }
        if ("WEEKLY".equals(rule.getRuleType())) {
            if (rule.getWeekDay() == null || rule.getWeekDay() < 1 || rule.getWeekDay() > 7) {
                throw new BusinessException("每周模式须指定星期（1-7）");
            }
        } else if ("SPECIFIC".equals(rule.getRuleType())) {
            if (rule.getScheduleDate() == null) {
                throw new BusinessException("指定日期模式须选择日期");
            }
        } else {
            throw new BusinessException("未知规则类型: " + rule.getRuleType());
        }
        validateTime(rule);
    }

    /** 校验上课时间 */
    private void validateTime(EduClassScheduleRule rule) {
        if (rule.getStartTime() != null && rule.getEndTime() != null) {
            if (!rule.getEndTime().isAfter(rule.getStartTime())) {
                throw new BusinessException("下课时间必须在上课时间之后");
            }
        }
        if (rule.getDuration() != null && (rule.getDuration() < 15 || rule.getDuration() > 240)) {
            throw new BusinessException("课时长度应在 15-240 分钟之间");
        }
    }
}
