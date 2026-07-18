package com.education.store.module.schedule.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.schedule.entity.EduSchedulePlan;
import com.education.store.module.schedule.mapper.EduSchedulePlanMapper;
import com.education.store.module.schedule.service.IEduSchedulePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EduSchedulePlanServiceImpl extends ServiceImpl<EduSchedulePlanMapper, EduSchedulePlan>
        implements IEduSchedulePlanService {

    private final EduSchedulePlanMapper planMapper;

    @Override
    public IPage<EduSchedulePlan> selectPlanList(IPage<EduSchedulePlan> page, EduSchedulePlan plan) {
        LambdaQueryWrapper<EduSchedulePlan> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(plan.getStatus())) {
            wrapper.eq(EduSchedulePlan::getStatus, plan.getStatus());
        }
        wrapper.orderByDesc(EduSchedulePlan::getCreateTime);
        return planMapper.selectPage(page, wrapper);
    }

    @Override
    public EduSchedulePlan selectPlanById(Long id) {
        return planMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPlan(EduSchedulePlan plan) {
        plan.setStatus("UNGENERATED");
        return planMapper.insert(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePlan(EduSchedulePlan plan) {
        EduSchedulePlan exist = planMapper.selectById(plan.getId());
        if (exist == null) throw new BusinessException("排课计划不存在");
        if ("GENERATED".equals(exist.getStatus())) throw new BusinessException("已生成的排课计划不可修改");
        return planMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePlanByIds(Long[] ids) {
        for (Long id : ids) {
            EduSchedulePlan plan = planMapper.selectById(id);
            if (plan != null && "GENERATED".equals(plan.getStatus())) {
                throw new BusinessException("已生成的排课计划不可删除");
            }
        }
        return planMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
