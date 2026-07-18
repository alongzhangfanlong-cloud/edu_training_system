package com.education.store.module.prospect.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.prospect.entity.EduProspect;

/**
 * 意向学员 Service 接口
 */
public interface IEduProspectService extends IService<EduProspect> {

    /** 分页查询意向学员列表 */
    IPage<EduProspect> selectProspectList(IPage<EduProspect> page, EduProspect prospect);

    /** 根据ID查询 */
    EduProspect selectProspectById(Long id);

    /** 新增意向学员 */
    int insertProspect(EduProspect prospect);

    /** 更新意向学员 */
    int updateProspect(EduProspect prospect);

    /** 删除意向学员 */
    int deleteProspectByIds(Long[] ids);

    /** 校验手机号唯一 */
    boolean checkMobileUnique(EduProspect prospect);

    /** 转化意向学员为在学学员（核心业务：创建 student + parent + 回填） */
    Long convertToStudent(Long prospectId);
}
