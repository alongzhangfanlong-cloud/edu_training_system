package com.education.store.module.prospect.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.prospect.entity.EduProspect;
import com.education.store.module.prospect.mapper.EduProspectMapper;
import com.education.store.module.prospect.service.IEduProspectService;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.entity.EduStudentParent;
import com.education.store.module.student.service.IEduStudentParentService;
import com.education.store.module.student.service.IEduStudentService;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.system.entity.system.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 意向学员 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduProspectServiceImpl extends ServiceImpl<EduProspectMapper, EduProspect>
        implements IEduProspectService {

    private final EduProspectMapper prospectMapper;
    private final IEduStudentService studentService;
    private final IEduStudentParentService parentService;
    private final SysUserMapper sysUserMapper;
    private final EduCourseMapper eduCourseMapper;

    @Override
    public IPage<EduProspect> selectProspectList(IPage<EduProspect> page, EduProspect prospect) {
        LambdaQueryWrapper<EduProspect> wrapper = new LambdaQueryWrapper<>();
        // keyword 搜索：姓名和手机号 OR 匹配
        if (StrUtil.isNotBlank(prospect.getName()) && StrUtil.isNotBlank(prospect.getMobile())
                && prospect.getName().equals(prospect.getMobile())) {
            wrapper.and(w -> w.like(EduProspect::getName, prospect.getName())
                    .or().like(EduProspect::getMobile, prospect.getName()));
        } else {
            if (StrUtil.isNotBlank(prospect.getName())) {
                wrapper.like(EduProspect::getName, prospect.getName());
            }
            if (StrUtil.isNotBlank(prospect.getMobile())) {
                wrapper.like(EduProspect::getMobile, prospect.getMobile());
            }
        }
        if (StrUtil.isNotBlank(prospect.getGrade())) {
            wrapper.eq(EduProspect::getGrade, prospect.getGrade());
        }
        if (StrUtil.isNotBlank(prospect.getFollowStatus())) {
            wrapper.eq(EduProspect::getFollowStatus, prospect.getFollowStatus());
        }
        if (prospect.getCounselorId() != null) {
            wrapper.eq(EduProspect::getCounselorId, prospect.getCounselorId());
        }
        if (StrUtil.isNotBlank(prospect.getInterestLevel())) {
            wrapper.eq(EduProspect::getInterestLevel, prospect.getInterestLevel());
        }
        wrapper.orderByDesc(EduProspect::getCreateTime);
        IPage<EduProspect> result = prospectMapper.selectPage(page, wrapper);
        result.getRecords().forEach(this::fillNames);
        return result;
    }

    @Override
    public EduProspect selectProspectById(Long id) {
        EduProspect prospect = prospectMapper.selectById(id);
        if (prospect != null) {
            fillNames(prospect);
        }
        return prospect;
    }

    /** 填充顾问姓名和意向课程名称 */
    private void fillNames(EduProspect p) {
        if (p.getCounselorId() != null) {
            SysUser user = sysUserMapper.selectById(p.getCounselorId());
            if (user != null) {
                p.setCounselorName(user.getRealName());
            }
        }
        if (p.getIntendedCourseId() != null) {
            EduCourse course = eduCourseMapper.selectById(p.getIntendedCourseId());
            if (course != null) {
                p.setIntendedCourseName(course.getName());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertProspect(EduProspect prospect) {
        prospect.setFollowStatus("NEW");
        prospect.setTrialLessonCount(0);
        prospect.setLastFollowupDate(LocalDateTime.now());
        return prospectMapper.insert(prospect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProspect(EduProspect prospect) {
        EduProspect exist = prospectMapper.selectById(prospect.getId());
        if (exist == null) {
            throw new BusinessException("意向学员不存在");
        }

        // 终态保护：CONVERTED 状态不可更新（convertToStudent 内部更新除外）
        if ("CONVERTED".equals(exist.getFollowStatus()) && !"CONVERTED".equals(prospect.getFollowStatus())) {
            throw new BusinessException("已转化的意向学员不可修改");
        }

        // 状态变更校验 + 自动更新最近跟进日期
        if (prospect.getFollowStatus() != null
                && !prospect.getFollowStatus().equals(exist.getFollowStatus())) {
            validateStatusTransition(exist.getFollowStatus(), prospect.getFollowStatus());
            prospect.setLastFollowupDate(LocalDateTime.now());
        }

        return prospectMapper.updateById(prospect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProspectByIds(Long[] ids) {
        return prospectMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public boolean checkMobileUnique(EduProspect prospect) {
        if (StrUtil.isBlank(prospect.getMobile())) {
            return true;
        }
        Long id = prospect.getId() != null ? prospect.getId() : -1L;
        EduProspect exist = lambdaQuery()
                .eq(EduProspect::getMobile, prospect.getMobile())
                .one();
        return exist == null || exist.getId().equals(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long convertToStudent(Long prospectId) {
        EduProspect prospect = prospectMapper.selectById(prospectId);
        if (prospect == null) {
            throw new BusinessException("意向学员不存在");
        }
        // PRD §3.4.3: 新线索/已联系/已试听 均可转化，已转化/已流失为终态不可转化
        if ("CONVERTED".equals(prospect.getFollowStatus()) || "LOST".equals(prospect.getFollowStatus())) {
            throw new BusinessException("已转化/已流失的意向学员不可再次转化");
        }

        // 1. 创建在学学员（数据继承）
        EduStudent student = new EduStudent();
        student.setName(prospect.getName());
        student.setGender(prospect.getGender());
        student.setBirthDate(prospect.getBirthDate());
        student.setGrade(prospect.getGrade());
        student.setSchool(prospect.getSchool());
        student.setMobile(prospect.getMobile());
        student.setCounselorId(prospect.getCounselorId());
        student.setSource(prospect.getSource());
        student.setParentName(prospect.getParentName());
        student.setParentRelation(prospect.getParentRelation());
        student.setParentMobile(prospect.getParentMobile());
        student.setWechatNickname(prospect.getWechatNickname());
        student.setIntendedCourseId(prospect.getIntendedCourseId());
        // 复制意向备注，添加前缀标识来源（PRD §3.7.3）
        if (StrUtil.isNotBlank(prospect.getRemark())) {
            student.setRemark("[意向转化] " + prospect.getRemark());
        }
        student.setStatus("ENROLLED");
        student.setTotalHours(BigDecimal.ZERO);
        student.setRemainingHours(BigDecimal.ZERO);
        studentService.insertStudent(student);

        // 2. 创建家长关联（数据继承），仅当家长姓名不为空时创建
        if (StrUtil.isNotBlank(prospect.getParentName())) {
            EduStudentParent parent = new EduStudentParent();
            parent.setStudentId(student.getId());
            parent.setName(prospect.getParentName());
            parent.setRelation(StrUtil.isNotBlank(prospect.getParentRelation()) ? prospect.getParentRelation() : "OTHER");
            parent.setMobile(prospect.getParentMobile());
            parent.setSort(0);
            // PRD §7.6: 同一学生下家长手机号不可重复
            if (!parentService.checkMobileUnique(parent)) {
                throw new BusinessException("该学生已存在相同手机号的家长");
            }
            parentService.insertParent(parent);
        }

        // 3. 回填并更新意向学员状态（条件更新防止并发重复转化）
        String oldStatus = prospect.getFollowStatus();
        prospect.setFollowStatus("CONVERTED");
        prospect.setConvertedStudentId(student.getId());
        prospect.setLastFollowupDate(LocalDateTime.now());
        // 仅当状态未被其他事务修改时才执行更新
        boolean updated = lambdaUpdate()
                .eq(EduProspect::getId, prospect.getId())
                .eq(EduProspect::getFollowStatus, oldStatus)
                .update(prospect);
        if (!updated) {
            throw new BusinessException("该意向学员状态已变更，转化失败，请刷新重试");
        }

        return student.getId();
    }

    /** 状态转换校验 */
    private void validateStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus.equals(newStatus)) {
            return;
        }
        switch (oldStatus) {
            case "NEW":
                if (!"CONTACTED".equals(newStatus) && !"TRIAL".equals(newStatus) && !"LOST".equals(newStatus)) {
                    throw new BusinessException("新线索只能变更为已联系/试听/流失");
                }
                break;
            case "CONTACTED":
                if (!"TRIAL".equals(newStatus) && !"LOST".equals(newStatus)) {
                    throw new BusinessException("已联系只能变更为试听/流失");
                }
                break;
            case "TRIAL":
                if (!"CONVERTED".equals(newStatus) && !"LOST".equals(newStatus) && !"NEW".equals(newStatus)) {
                    throw new BusinessException("体验课后只能变更为已转化/流失/重新标记");
                }
                break;
            case "CONVERTED":
            case "LOST":
                throw new BusinessException("已转化/已流失为终态，不可变更");
            default:
                throw new BusinessException("未知状态: " + oldStatus);
        }
    }
}
