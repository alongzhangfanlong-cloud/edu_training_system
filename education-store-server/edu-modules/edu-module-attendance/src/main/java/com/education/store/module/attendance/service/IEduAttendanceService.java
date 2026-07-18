package com.education.store.module.attendance.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.attendance.entity.EduAttendance;
import java.util.List;
import java.util.Map;

public interface IEduAttendanceService extends IService<EduAttendance> {
    IPage<EduAttendance> selectAttendanceList(IPage<EduAttendance> page, Long scheduleId, Long classId, Long studentId);
    List<EduAttendance> selectByScheduleId(Long scheduleId);
    Map<String, Long> getStatsBySchedule(Long scheduleId);
    int batchSignin(Long scheduleId, Long classId, List<EduAttendance> records, Long operatorId, String operatorType);
}
