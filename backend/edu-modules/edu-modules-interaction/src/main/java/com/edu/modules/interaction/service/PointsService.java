package com.edu.modules.interaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.modules.interaction.dto.PointChangeRequest;
import com.edu.modules.interaction.dto.PointsDTO;
import com.edu.modules.interaction.entity.PointTransactionEntity;
import com.edu.modules.interaction.entity.PointsEntity;
import com.edu.modules.interaction.mapper.PointTransactionMapper;
import com.edu.modules.interaction.mapper.PointsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsService {

    private final PointsMapper pointsMapper;
    private final PointTransactionMapper pointTransactionMapper;

    public PointsDTO getByStudentId(Long studentId) {
        LambdaQueryWrapper<PointsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsEntity::getStudentId, studentId);
        PointsEntity entity = pointsMapper.selectOne(wrapper);
        if (entity == null) {
            // 自动初始化
            entity = new PointsEntity();
            entity.setStudentId(studentId);
            entity.setBalance(0);
            pointsMapper.insert(entity);
        }
        PointsDTO dto = new PointsDTO();
        dto.setId(entity.getId());
        dto.setStudentId(entity.getStudentId());
        dto.setBalance(entity.getBalance());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public List<PointsDTO> list() {
        return pointsMapper.selectList(null).stream().map(e -> {
            PointsDTO dto = new PointsDTO();
            dto.setId(e.getId());
            dto.setStudentId(e.getStudentId());
            dto.setBalance(e.getBalance());
            dto.setUpdatedAt(e.getUpdatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public PointsDTO changePoints(PointChangeRequest request) {
        PointsDTO current = getByStudentId(request.getStudentId());
        PointsEntity entity = pointsMapper.selectById(current.getId());

        int newBalance = entity.getBalance() + request.getChangeAmount();
        if (newBalance < 0) throw new RuntimeException("积分余额不足");
        entity.setBalance(newBalance);
        pointsMapper.updateById(entity);

        // 记录流水
        PointTransactionEntity tx = new PointTransactionEntity();
        tx.setStudentId(request.getStudentId());
        tx.setChangeAmount(request.getChangeAmount());
        tx.setReason(request.getReason());
        pointTransactionMapper.insert(tx);

        log.info("积分变动: studentId={}, change={}, newBalance={}",
                request.getStudentId(), request.getChangeAmount(), newBalance);

        current.setBalance(newBalance);
        return current;
    }

    public List<PointTransactionEntity> getTransactions(Long studentId) {
        LambdaQueryWrapper<PointTransactionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointTransactionEntity::getStudentId, studentId)
               .orderByDesc(PointTransactionEntity::getCreatedAt);
        return pointTransactionMapper.selectList(wrapper);
    }
}
