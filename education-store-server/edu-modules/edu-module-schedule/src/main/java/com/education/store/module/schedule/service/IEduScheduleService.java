package com.education.store.module.schedule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.schedule.entity.EduSchedule;

public interface IEduScheduleService extends IService<EduSchedule> {
    IPage<EduSchedule> selectScheduleList(IPage<EduSchedule> page, EduSchedule schedule);
    EduSchedule selectScheduleById(Long id);
    int insertSchedule(EduSchedule schedule);
    int updateSchedule(EduSchedule schedule);
    int deleteScheduleByIds(Long[] ids);
    int cancelSchedule(Long id, String reason);
    int reschedule(EduSchedule updated);
    int consumeSchedule(Long id);
    boolean checkConflict(EduSchedule schedule);
}
