package com.education.store.module.prospect.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.prospect.entity.EduProspectFollowup;

/**
 * 跟进记录 Service 接口
 */
public interface IEduProspectFollowupService extends IService<EduProspectFollowup> {

    /** 查询某意向学员的跟进记录列表 */
    IPage<EduProspectFollowup> selectFollowupList(IPage<EduProspectFollowup> page, Long prospectId);

    /** 新增跟进记录（含自动更新 prospect.last_followup_date + 首次 NEW→CONTACTED） */
    int insertFollowup(EduProspectFollowup followup);

    /** 更新跟进记录 */
    int updateFollowup(EduProspectFollowup followup);

    /** 删除跟进记录（回退 last_followup_date 到次新记录） */
    int deleteFollowupByIds(Long[] ids);
}
