package com.education.store.module.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.student.entity.EduStudent;

/**
 * 在学学员 Service 接口
 */
public interface IEduStudentService extends IService<EduStudent> {

    /** 分页查询学员列表 */
    IPage<EduStudent> selectStudentList(IPage<EduStudent> page, EduStudent student);

    /** 根据ID查询学员 */
    EduStudent selectStudentById(Long id);

    /** 新增学员 */
    int insertStudent(EduStudent student);

    /** 更新学员 */
    int updateStudent(EduStudent student);

    /** 根据ID数组删除学员 */
    int deleteStudentByIds(Long[] ids);

    /** 校验手机号是否唯一（软唯一，含 deleted 标记） */
    boolean checkMobileUnique(EduStudent student);

    /** 更新学员状态（含状态转换校验） */
    int updateStudentStatus(Long id, String newStatus);
}
