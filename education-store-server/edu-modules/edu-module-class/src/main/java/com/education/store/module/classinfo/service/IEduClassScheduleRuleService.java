package com.education.store.module.classinfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.classinfo.entity.EduClassScheduleRule;

import java.util.List;

/**
 * 班级排课规则 Service 接口
 */
public interface IEduClassScheduleRuleService extends IService<EduClassScheduleRule> {

    /** 根据班级ID查询排课规则列表 */
    List<EduClassScheduleRule> selectByClassId(Long classId);

    /** 批量保存排课规则（先删旧规则，再插入新规则） */
    void batchSave(Long classId, List<EduClassScheduleRule> rules);

    /** 删除排课规则 */
    int deleteRuleById(Long id);
}
