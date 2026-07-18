package com.education.store.module.schedule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.schedule.dto.SchedulePlanCreateDTO;
import com.education.store.module.schedule.dto.SchedulePreviewVO;
import com.education.store.module.schedule.entity.EduSchedulePlan;

public interface IEduSchedulePlanService extends IService<EduSchedulePlan> {
    IPage<EduSchedulePlan> selectPlanList(IPage<EduSchedulePlan> page, EduSchedulePlan plan, Long classId);
    EduSchedulePlan selectPlanById(Long id);
    int insertPlan(EduSchedulePlan plan);
    int updatePlan(EduSchedulePlan plan);
    int deletePlanByIds(Long[] ids);

    /** 预览排课计划 — 三维冲突检测，返回每个槽位的冲突信息 */
    SchedulePreviewVO previewPlan(SchedulePlanCreateDTO dto);

    /** 根据计划生成排课记录（预览确认后调用） */
    int generateSchedules(Long planId, SchedulePlanCreateDTO dto);

    /** 重新生成排课 — 删除旧排课记录后重新生成 */
    int regeneratePlan(Long planId);
}
