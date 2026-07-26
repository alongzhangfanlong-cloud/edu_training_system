package com.education.store.module.payroll.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.payroll.entity.EduTeacherPayRate;
import com.education.store.module.payroll.mapper.EduTeacherPayRateMapper;
import com.education.store.module.payroll.service.IEduTeacherPayRateService;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduTeacherPayRateServiceImpl extends ServiceImpl<EduTeacherPayRateMapper, EduTeacherPayRate>
        implements IEduTeacherPayRateService {

    private final EduTeacherPayRateMapper payRateMapper;
    private final SysUserMapper sysUserMapper;
    private final EduCourseMapper courseMapper;

    @Override
    public IPage<EduTeacherPayRate> selectPayRateList(IPage<EduTeacherPayRate> page, EduTeacherPayRate rate) {
        LambdaQueryWrapper<EduTeacherPayRate> wrapper = new LambdaQueryWrapper<>();
        if (rate.getTeacherId() != null) wrapper.eq(EduTeacherPayRate::getTeacherId, rate.getTeacherId());
        if (rate.getCourseId() != null) wrapper.eq(EduTeacherPayRate::getCourseId, rate.getCourseId());
        wrapper.orderByDesc(EduTeacherPayRate::getEffectiveFrom);
        IPage<EduTeacherPayRate> result = payRateMapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
    }

    @Override
    public EduTeacherPayRate selectPayRateById(Long id) {
        EduTeacherPayRate rate = payRateMapper.selectById(id);
        if (rate != null) {
            fillNames(rate);
        }
        return rate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayRate(EduTeacherPayRate rate) {
        if (!checkPeriodOverlap(rate)) {
            throw new BusinessException("该教师在该课程下已有重叠的有效期配置");
        }
        return payRateMapper.insert(rate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayRate(EduTeacherPayRate rate) {
        EduTeacherPayRate exist = payRateMapper.selectById(rate.getId());
        if (exist == null) throw new BusinessException("课酬单价不存在");
        if (!checkPeriodOverlap(rate)) throw new BusinessException("该教师在该课程下已有重叠的有效期配置");
        return payRateMapper.updateById(rate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayRateByIds(Long[] ids) {
        return payRateMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkPeriodOverlap(EduTeacherPayRate rate) {
        Long id = rate.getId() != null ? rate.getId() : -1L;
        LocalDate effectiveTo = rate.getEffectiveTo() != null ? rate.getEffectiveTo() : LocalDate.of(2099, 12, 31);
        LambdaQueryWrapper<EduTeacherPayRate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduTeacherPayRate::getTeacherId, rate.getTeacherId());
        wrapper.eq(EduTeacherPayRate::getCourseId, rate.getCourseId());
        wrapper.and(w -> w.isNull(EduTeacherPayRate::getEffectiveTo).or().ge(EduTeacherPayRate::getEffectiveTo, rate.getEffectiveFrom()));
        wrapper.le(EduTeacherPayRate::getEffectiveFrom, effectiveTo);
        EduTeacherPayRate exist = payRateMapper.selectOne(wrapper);
        return exist == null || exist.getId().equals(id);
    }

    // ========== 名称填充 ==========

    private void fillNamesForList(List<EduTeacherPayRate> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> teacherIds = new HashSet<>();
        Set<Long> courseIds = new HashSet<>();
        for (EduTeacherPayRate r : list) {
            if (r.getTeacherId() != null) teacherIds.add(r.getTeacherId());
            if (r.getCourseId() != null) courseIds.add(r.getCourseId());
        }
        Map<Long, SysUser> userMap = teacherIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(teacherIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, EduCourse> courseMap = courseIds.isEmpty() ? Collections.emptyMap()
                : courseMapper.selectBatchIds(courseIds).stream().collect(Collectors.toMap(EduCourse::getId, c -> c));
        for (EduTeacherPayRate r : list) {
            fillNames(r, userMap, courseMap);
        }
    }

    private void fillNames(EduTeacherPayRate rate) {
        if (rate.getTeacherId() != null) {
            SysUser user = sysUserMapper.selectById(rate.getTeacherId());
            if (user != null) {
                rate.setTeacherName(getDisplayName(user));
            }
        }
        if (rate.getCourseId() != null) {
            EduCourse course = courseMapper.selectById(rate.getCourseId());
            if (course != null) {
                rate.setCourseName(course.getName());
            }
        }
    }

    private void fillNames(EduTeacherPayRate rate, Map<Long, SysUser> userMap, Map<Long, EduCourse> courseMap) {
        if (rate.getTeacherId() != null) {
            SysUser user = userMap.get(rate.getTeacherId());
            if (user != null) {
                rate.setTeacherName(getDisplayName(user));
            }
        }
        if (rate.getCourseId() != null) {
            EduCourse course = courseMap.get(rate.getCourseId());
            if (course != null) {
                rate.setCourseName(course.getName());
            }
        }
    }

    private String getDisplayName(SysUser user) {
        if (user == null) return "--";
        if (StrUtil.isNotBlank(user.getRealName())) return user.getRealName();
        if (StrUtil.isNotBlank(user.getNickName())) return user.getNickName();
        return StrUtil.isNotBlank(user.getUsername()) ? user.getUsername() : "--";
    }
}
