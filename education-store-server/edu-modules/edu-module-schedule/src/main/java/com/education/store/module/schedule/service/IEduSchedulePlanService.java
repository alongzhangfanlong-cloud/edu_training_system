package com.education.store.module.schedule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.schedule.entity.EduSchedulePlan;

public interface IEduSchedulePlanService extends IService<EduSchedulePlan> {
    IPage<EduSchedulePlan> selectPlanList(IPage<EduSchedulePlan> page, EduSchedulePlan plan);
    EduSchedulePlan selectPlanById(Long id);
    int insertPlan(EduSchedulePlan plan);
    int updatePlan(EduSchedulePlan plan);
    int deletePlanByIds(Long[] ids);
}
