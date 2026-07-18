package com.education.store.module.payroll.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.payroll.entity.EduTeacherPayRate;
import com.education.store.module.payroll.mapper.EduTeacherPayRateMapper;
import com.education.store.module.payroll.service.IEduTeacherPayRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EduTeacherPayRateServiceImpl extends ServiceImpl<EduTeacherPayRateMapper, EduTeacherPayRate>
        implements IEduTeacherPayRateService {

    private final EduTeacherPayRateMapper payRateMapper;

    @Override
    public IPage<EduTeacherPayRate> selectPayRateList(IPage<EduTeacherPayRate> page, EduTeacherPayRate rate) {
        LambdaQueryWrapper<EduTeacherPayRate> wrapper = new LambdaQueryWrapper<>();
        if (rate.getTeacherId() != null) wrapper.eq(EduTeacherPayRate::getTeacherId, rate.getTeacherId());
        if (rate.getCourseId() != null) wrapper.eq(EduTeacherPayRate::getCourseId, rate.getCourseId());
        wrapper.orderByDesc(EduTeacherPayRate::getEffectiveFrom);
        return payRateMapper.selectPage(page, wrapper);
    }

    @Override public EduTeacherPayRate selectPayRateById(Long id) { return payRateMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertPayRate(EduTeacherPayRate rate) {
        return payRateMapper.insert(rate);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updatePayRate(EduTeacherPayRate rate) {
        EduTeacherPayRate exist = payRateMapper.selectById(rate.getId());
        if (exist == null) throw new BusinessException("课酬单价不存在");
        if (!checkPeriodOverlap(rate)) throw new BusinessException("该教师在该课程下已有重叠的有效期配置");
        return payRateMapper.updateById(rate);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deletePayRateByIds(Long[] ids) {
        return payRateMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkPeriodOverlap(EduTeacherPayRate rate) {
        Long id = rate.getId() != null ? rate.getId() : -1L;
        // 查同日期间教师+课程组合是否已有重叠记录
        LambdaQueryWrapper<EduTeacherPayRate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacherPayRate::getTeacherId, rate.getTeacherId());
        wrapper.eq(EduTeacherPayRate::getCourseId, rate.getCourseId());
        wrapper.and(w -> w.isNull(EduTeacherPayRate::getEffectiveTo).or().ge(EduTeacherPayRate::getEffectiveTo, rate.getEffectiveFrom()));
        wrapper.le(EduTeacherPayRate::getEffectiveFrom, rate.getEffectiveTo() != null ? rate.getEffectiveTo() : java.time.LocalDate.of(2099, 12, 31));
        EduTeacherPayRate exist = payRateMapper.selectOne(wrapper);
        return exist == null || exist.getId().equals(id);
    }
}
