package com.edu.modules.interaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.modules.interaction.dto.LeaveRequest;
import com.edu.modules.interaction.dto.LeaveDTO;
import com.edu.modules.interaction.entity.LeaveEntity;
import com.edu.modules.interaction.mapper.LeaveMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveMapper leaveMapper;

    public Long apply(LeaveRequest request) {
        LeaveEntity entity = new LeaveEntity();
        entity.setStudentId(request.getStudentId());
        entity.setScheduleId(request.getScheduleId());
        entity.setLeaveType(request.getLeaveType());
        entity.setReason(request.getReason());
        entity.setStatus(0); // 待审批
        leaveMapper.insert(entity);
        log.info("请假申请: studentId={}, id={}", request.getStudentId(), entity.getId());
        return entity.getId();
    }

    public List<LeaveDTO> list(Integer status, Long studentId) {
        LambdaQueryWrapper<LeaveEntity> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(LeaveEntity::getStatus, status);
        if (studentId != null) wrapper.eq(LeaveEntity::getStudentId, studentId);
        wrapper.orderByDesc(LeaveEntity::getCreatedAt);

        return leaveMapper.selectList(wrapper).stream().map(e -> {
            LeaveDTO dto = new LeaveDTO();
            dto.setId(e.getId());
            dto.setStudentId(e.getStudentId());
            dto.setScheduleId(e.getScheduleId());
            dto.setLeaveType(e.getLeaveType());
            dto.setReason(e.getReason());
            dto.setStatus(e.getStatus());
            dto.setApproveRemark(e.getApproveRemark());
            dto.setCreatedAt(e.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void approve(Long id, boolean approved, String remark) {
        LeaveEntity entity = leaveMapper.selectById(id);
        if (entity == null) throw new RuntimeException("请假记录不存在");
        if (entity.getStatus() != 0) throw new RuntimeException("该请假已处理");
        entity.setStatus(approved ? 1 : 2);
        entity.setApproveRemark(remark);
        leaveMapper.updateById(entity);
        log.info("请假审批: id={}, result={}", id, approved ? "通过" : "拒绝");
    }

    public void delete(Long id) {
        leaveMapper.deleteById(id);
    }
}
