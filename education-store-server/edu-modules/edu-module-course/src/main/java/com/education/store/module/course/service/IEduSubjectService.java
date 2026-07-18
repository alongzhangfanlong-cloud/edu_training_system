package com.education.store.module.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.course.entity.EduSubject;

/**
 * 科目 Service 接口
 */
public interface IEduSubjectService extends IService<EduSubject> {

    /** 分页查询科目列表 */
    IPage<EduSubject> selectSubjectList(IPage<EduSubject> page, EduSubject subject);

    /** 根据ID查询科目 */
    EduSubject selectSubjectById(Long id);

    /** 新增科目 */
    int insertSubject(EduSubject subject);

    /** 更新科目 */
    int updateSubject(EduSubject subject);

    /** 根据ID数组删除科目 */
    int deleteSubjectByIds(Long[] ids);

    /** 校验科目名称是否唯一 */
    boolean checkNameUnique(EduSubject subject);

    /** 获取全部科目（用于下拉选项） */
    java.util.List<EduSubject> selectAllSubjects();

    /** 更新科目启用/停用状态 */
    int updateSubjectStatus(Long id, Integer status);

    /** 批量更新排序 */
    int updateSortBatch(java.util.List<com.education.store.module.course.dto.SubjectUpdateDTO> list);
}
