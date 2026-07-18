package com.education.store.module.tuition.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.tuition.entity.EduTuitionGiftHours;

public interface IEduTuitionGiftHoursService extends IService<EduTuitionGiftHours> {
    IPage<EduTuitionGiftHours> selectGiftList(IPage<EduTuitionGiftHours> page, EduTuitionGiftHours gift);
    EduTuitionGiftHours selectGiftById(Long id);
    int insertGift(EduTuitionGiftHours gift);
    int deleteGiftByIds(Long[] ids);
}
