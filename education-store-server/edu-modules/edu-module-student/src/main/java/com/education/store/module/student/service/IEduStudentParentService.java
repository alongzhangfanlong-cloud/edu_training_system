package com.education.store.module.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.student.entity.EduStudentParent;

/**
 * 家长关联 Service 接口
 */
public interface IEduStudentParentService extends IService<EduStudentParent> {

    /** 查询学员的所有家长 */
    IPage<EduStudentParent> selectParentList(IPage<EduStudentParent> page, Long studentId);

    /** 新增家长关联 */
    int insertParent(EduStudentParent parent);

    /** 更新家长关联 */
    int updateParent(EduStudentParent parent);

    /** 删除家长关联（仅删关联，不删 sys_user） */
    int deleteParentByIds(Long[] ids);

    /** 校验同学生下手机号唯一 */
    boolean checkMobileUnique(EduStudentParent parent);
}
