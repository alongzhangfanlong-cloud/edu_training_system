package com.education.store.module.classinfo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.classinfo.entity.EduClassStudent;

/**
 * 班级学员关联 Service 接口
 */
public interface IEduClassStudentService extends IService<EduClassStudent> {

    /** 查询班级的学员列表 */
    IPage<EduClassStudent> selectClassStudentList(IPage<EduClassStudent> page, Long classId);

    /** 加入班级（含 current_count +1 + 自动 FULL 判断） */
    int enrollStudent(EduClassStudent classStudent);

    /** 更新在班状态（含 current_count +/- + 自动 FULL↔RECRUITING） */
    int updateStatus(Long id, String newStatus);

    /** 退班（含 current_count -1 + 自动 RECRUITING） */
    int removeStudent(Long id);
}
