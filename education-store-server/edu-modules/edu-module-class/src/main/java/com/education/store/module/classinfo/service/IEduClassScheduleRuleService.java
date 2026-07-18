package com.education.store.module.classinfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.classinfo.entity.EduClassScheduleRule;

/**
 * 班级排课规则 Service 接口
 */
public interface IEduClassScheduleRuleService extends IService<EduClassScheduleRule> {

    /** 根据班级ID查询排课规则 */
    EduClassScheduleRule selectByClassId(Long classId);

    /** 新增排课规则 */
    int insertRule(EduClassScheduleRule rule);

    /** 更新排课规则 */
    int updateRule(EduClassScheduleRule rule);

    /** 删除排课规则 */
    int deleteRuleById(Long id);
}
