package com.education.store.module.tuition.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.tuition.entity.EduTuitionRefund;

public interface IEduTuitionRefundService extends IService<EduTuitionRefund> {
    IPage<EduTuitionRefund> selectRefundList(IPage<EduTuitionRefund> page, EduTuitionRefund refund);
    EduTuitionRefund selectRefundById(Long id);
    int insertRefund(EduTuitionRefund refund);
    int updateRefund(EduTuitionRefund refund);
    int deleteRefundByIds(Long[] ids);
    int approveRefund(Long id, Long approverId);
    int rejectRefund(Long id, Long approverId, String reason);
}
