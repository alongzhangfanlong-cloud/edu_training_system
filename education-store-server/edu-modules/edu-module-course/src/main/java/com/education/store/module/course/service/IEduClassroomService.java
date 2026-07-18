package com.education.store.module.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.course.entity.EduClassroom;

/**
 * 教室 Service 接口
 */
public interface IEduClassroomService extends IService<EduClassroom> {

    /** 分页查询教室列表 */
    IPage<EduClassroom> selectClassroomList(IPage<EduClassroom> page, EduClassroom classroom);

    /** 根据ID查询教室 */
    EduClassroom selectClassroomById(Long id);

    /** 新增教室 */
    int insertClassroom(EduClassroom classroom);

    /** 更新教室 */
    int updateClassroom(EduClassroom classroom);

    /** 根据ID数组删除教室 */
    int deleteClassroomByIds(Long[] ids);

    /** 校验教室名称是否唯一 */
    boolean checkNameUnique(EduClassroom classroom);

    /** 获取全部启用教室（用于下拉选项） */
    java.util.List<EduClassroom> selectAllEnabled();

    /** 更新教室启用/停用状态 */
    int updateClassroomStatus(Long id, Integer status);
}
