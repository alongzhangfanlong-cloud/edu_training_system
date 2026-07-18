package com.education.store.module.enrollment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.classinfo.entity.EduClassStudent;
import com.education.store.module.classinfo.mapper.EduClassStudentMapper;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.enrollment.dto.EnrollmentUpdateDTO;
import com.education.store.module.enrollment.entity.EduEnrollment;
import com.education.store.module.enrollment.mapper.EduEnrollmentMapper;
import com.education.store.module.enrollment.service.IEduEnrollmentService;
import com.education.store.module.student.mapper.EduStudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EduEnrollmentServiceImpl extends ServiceImpl<EduEnrollmentMapper, EduEnrollment>
        implements IEduEnrollmentService {

    private final EduEnrollmentMapper enrollmentMapper;
    private final EduClassStudentMapper classStudentMapper;
    private final EduStudentMapper studentMapper;
    private final EduCourseMapper courseMapper;

    @Override
    public IPage<EduEnrollment> selectEnrollmentList(IPage<EduEnrollment> page, EduEnrollment enrollment) {
        LambdaQueryWrapper<EduEnrollment> wrapper = new LambdaQueryWrapper<>();
        if (enrollment.getStudentId() != null) wrapper.eq(EduEnrollment::getStudentId, enrollment.getStudentId());
        if (enrollment.getCourseId() != null) wrapper.eq(EduEnrollment::getCourseId, enrollment.getCourseId());
        if (StrUtil.isNotBlank(enrollment.getPaymentStatus())) wrapper.eq(EduEnrollment::getPaymentStatus, enrollment.getPaymentStatus());
        wrapper.orderByDesc(EduEnrollment::getCreateTime);
        return enrollmentMapper.selectPage(page, wrapper);
    }

    @Override public EduEnrollment selectEnrollmentById(Long id) { return enrollmentMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertEnrollment(EduEnrollment enrollment) {
        // 校验学员和课程存在性
        if (enrollment.getStudentId() == null) throw new BusinessException("学员ID不能为空");
        if (enrollment.getCourseId() == null) throw new BusinessException("课程ID不能为空");
        if (studentMapper.selectById(enrollment.getStudentId()) == null)
            throw new BusinessException("学员不存在");
        if (courseMapper.selectById(enrollment.getCourseId()) == null)
            throw new BusinessException("课程不存在");

        // 检查重复报名（同学生+同课程且未取消）
        Long dupCount = enrollmentMapper.selectCount(
            new LambdaQueryWrapper<EduEnrollment>()
                .eq(EduEnrollment::getStudentId, enrollment.getStudentId())
                .eq(EduEnrollment::getCourseId, enrollment.getCourseId())
                .ne(EduEnrollment::getPaymentStatus, "CANCELLED"));
        if (dupCount > 0) throw new BusinessException("该学员已报名此课程，请勿重复报名");

        enrollment.setPaymentStatus("PENDING");
        enrollment.setCreateBy(SecurityUtils.getUserIdAsString());
        int rows = enrollmentMapper.insert(enrollment);

        // 如果选择了班级，自动创建班级学员关联
        if (enrollment.getClassId() != null) {
            Long count = classStudentMapper.selectCount(
                new LambdaQueryWrapper<EduClassStudent>()
                    .eq(EduClassStudent::getClassId, enrollment.getClassId())
                    .eq(EduClassStudent::getStudentId, enrollment.getStudentId())
                    .eq(EduClassStudent::getDeleted, 0));
            if (count == 0) {
                EduClassStudent cs = new EduClassStudent();
                cs.setClassId(enrollment.getClassId());
                cs.setStudentId(enrollment.getStudentId());
                cs.setStatus("ENROLLED");
                cs.setJoinTime(LocalDateTime.now());
                classStudentMapper.insert(cs);
            }
        }
        return rows;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updateEnrollment(EnrollmentUpdateDTO dto) {
        EduEnrollment exist = enrollmentMapper.selectById(dto.getId());
        if (exist == null) throw new BusinessException("报名记录不存在");
        if ("PAID".equals(exist.getPaymentStatus()) || "REFUNDED".equals(exist.getPaymentStatus()))
            throw new BusinessException("已收费/已退费的报名记录不可编辑");
        // 仅拷贝允许客户端修改的字段，防止 paymentStatus/tuitionRecordId 等字段注入
        if (dto.getClassId() != null) exist.setClassId(dto.getClassId());
        if (dto.getAmount() != null) exist.setAmount(dto.getAmount());
        if (dto.getHandlerId() != null) exist.setHandlerId(dto.getHandlerId());
        if (dto.getEnrollmentDate() != null) exist.setEnrollmentDate(dto.getEnrollmentDate());
        exist.setRemark(dto.getRemark());
        exist.setUpdateBy(SecurityUtils.getUserIdAsString());
        return enrollmentMapper.updateById(exist);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteEnrollmentByIds(Long[] ids) {
        // 禁止删除已收费/已退费的记录
        List<EduEnrollment> records = enrollmentMapper.selectBatchIds(Arrays.asList(ids));
        for (EduEnrollment e : records) {
            if ("PAID".equals(e.getPaymentStatus()) || "REFUNDED".equals(e.getPaymentStatus())) {
                throw new BusinessException("报名记录 " + e.getId() + " 已收费/已退费，不可删除");
            }
        }
        return enrollmentMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /** 支付状态合法转移规则：终态(CANCELLED/REFUNDED)不可再变更 */
    private static final Map<String, Set<String>> ALLOWED_PAYMENT_TRANSITIONS = Map.of(
        "DRAFT",    Set.of("PENDING", "CANCELLED"),
        "PENDING",  Set.of("PAID", "CANCELLED"),
        "PARTIAL",  Set.of("PAID", "CANCELLED"),
        "PAID",     Set.of("REFUNDED")
    );

    @Override @Transactional(rollbackFor = Exception.class)
    public int updatePaymentStatus(Long id, String newStatus) {
        EduEnrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) throw new BusinessException("报名记录不存在");
        String current = enrollment.getPaymentStatus();
        Set<String> allowed = ALLOWED_PAYMENT_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new BusinessException("不允许从 " + current + " 变更到 " + newStatus);
        }
        enrollment.setPaymentStatus(newStatus);
        return enrollmentMapper.updateById(enrollment);
    }
}
