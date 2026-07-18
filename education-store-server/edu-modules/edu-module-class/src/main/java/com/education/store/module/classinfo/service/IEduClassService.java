package com.education.store.module.classinfo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.classinfo.entity.EduClass;

/**
 * 班级 Service 接口
 */
public interface IEduClassService extends IService<EduClass> {

    /** 分页查询班级列表 */
    IPage<EduClass> selectClassList(IPage<EduClass> page, EduClass clazz);

    /** 根据ID查询班级 */
    EduClass selectClassById(Long id);

    /** 新增班级（含编号生成 + 同步创建排课规则） */
    int insertClass(EduClass clazz);

    /** 更新班级 */
    int updateClass(EduClass clazz);

    /** 删除班级 */
    int deleteClassByIds(Long[] ids);

    /** 更新班级状态（含状态转换校验） */
    int updateClassStatus(Long id, String newStatus);
}
