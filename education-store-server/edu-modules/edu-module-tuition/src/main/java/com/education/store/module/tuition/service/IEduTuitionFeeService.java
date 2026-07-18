package com.education.store.module.tuition.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.tuition.entity.EduTuitionFee;

public interface IEduTuitionFeeService extends IService<EduTuitionFee> {
    IPage<EduTuitionFee> selectFeeList(IPage<EduTuitionFee> page, EduTuitionFee fee);
    EduTuitionFee selectFeeById(Long id);
    int insertFee(EduTuitionFee fee);
    int updateFee(EduTuitionFee fee);
    int deleteFeeByIds(Long[] ids);
}
