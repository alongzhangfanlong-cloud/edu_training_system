package com.education.store.module.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.student.entity.EduStudentHoursLog;

import java.math.BigDecimal;

/**
 * 课时变动日志 Service 接口（只追加不可变）
 */
public interface IEduStudentHoursLogService extends IService<EduStudentHoursLog> {

    /** 查询学员课时变动记录（分页，按时间倒序） */
    IPage<EduStudentHoursLog> selectHoursLogList(IPage<EduStudentHoursLog> page, Long studentId, Long courseId);

    /** 新增课时变动记录（原子余额计算 + 同步更新学员课时缓存） */
    int insertHoursLog(EduStudentHoursLog log);

    /** 查询学员剩余总课时 */
    BigDecimal getRemainingHours(Long studentId);

    /** 查询学员已获得总课时 */
    BigDecimal getTotalGainedHours(Long studentId);
}
