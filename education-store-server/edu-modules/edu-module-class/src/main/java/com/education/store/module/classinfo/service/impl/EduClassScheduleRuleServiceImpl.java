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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排课规则 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduClassScheduleRuleServiceImpl extends ServiceImpl<EduClassScheduleRuleMapper, EduClassScheduleRule>
        implements IEduClassScheduleRuleService {

    private final EduClassScheduleRuleMapper scheduleRuleMapper;

    @Override
    public EduClassScheduleRule selectByClassId(Long classId) {
        LambdaQueryWrapper<EduClassScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduClassScheduleRule::getClassId, classId);
        return scheduleRuleMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRule(EduClassScheduleRule rule) {
        validateWeekDays(rule.getWeekDays());
        validateTime(rule);
        return scheduleRuleMapper.insert(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRule(EduClassScheduleRule rule) {
        EduClassScheduleRule exist = scheduleRuleMapper.selectById(rule.getId());
        if (exist == null) {
            throw new BusinessException("排课规则不存在");
        }
        validateWeekDays(rule.getWeekDays());
        validateTime(rule);
        return scheduleRuleMapper.updateById(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRuleById(Long id) {
        return scheduleRuleMapper.deleteById(id);
    }

    /** 校验星期格式：逗号分隔的 1-7 */
    private void validateWeekDays(String weekDays) {
        if (weekDays == null || weekDays.isBlank()) {
            throw new BusinessException("上课日不能为空");
        }
        try {
            List<Integer> days = Arrays.stream(weekDays.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            for (Integer day : days) {
                if (day < 1 || day > 7) {
                    throw new BusinessException("星期值必须在 1-7 之间（1=周一, 7=周日）");
                }
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("上课日格式错误，应为逗号分隔的数字（如 1,3,5）");
        }
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
