package com.education.store.module.schedule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.classinfo.entity.EduClassScheduleRule;
import com.education.store.module.schedule.dto.SchedulePlanCreateDTO;
import com.education.store.module.schedule.dto.SchedulePreviewVO;
import com.education.store.module.schedule.entity.EduSchedulePlan;

import java.util.List;
import java.util.Map;

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

    /** 生成课表（支持跳过冲突） */
    int generateWithOptions(Long planId, SchedulePlanCreateDTO dto, boolean skipConflict);

    /** 重新生成排课 — 删除旧排课记录后重新生成 */
    int regeneratePlan(Long planId);

    /** 获取排课计划关联班级的排课规则 */
    List<EduClassScheduleRule> getPlanRules(Long planId);

    /** 保存排课计划关联班级的排课规则 */
    void savePlanRules(Long planId, List<EduClassScheduleRule> rules);
}
