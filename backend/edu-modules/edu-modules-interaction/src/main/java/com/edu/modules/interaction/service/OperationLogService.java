package com.edu.modules.interaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.interaction.entity.OperationLogEntity;
import com.edu.modules.interaction.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public void log(Long userId, String userName, String role, String module,
                    String action, String target, Long targetId, String detail, String ip) {
        OperationLogEntity entity = new OperationLogEntity();
        entity.setUserId(userId);
        entity.setUserName(userName);
        entity.setRole(role);
        entity.setModule(module);
        entity.setAction(action);
        entity.setTarget(target);
        entity.setTargetId(targetId);
        entity.setDetail(detail);
        entity.setIp(ip);
        entity.setStatus(1);
        operationLogMapper.insert(entity);
    }

    public PageResult<Map<String, Object>> list(String module, String action, String keyword,
                                                  int page, int pageSize) {
        LambdaQueryWrapper<OperationLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (module != null && !module.isEmpty()) wrapper.eq(OperationLogEntity::getModule, module);
        if (action != null && !action.isEmpty()) wrapper.eq(OperationLogEntity::getAction, action);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(OperationLogEntity::getUserName, keyword)
                    .or().like(OperationLogEntity::getDetail, keyword)
                    .or().like(OperationLogEntity::getTarget, keyword));
        }
        wrapper.orderByDesc(OperationLogEntity::getCreatedAt);

        Page<OperationLogEntity> p = new Page<>(page, pageSize);
        Page<OperationLogEntity> result = operationLogMapper.selectPage(p, wrapper);

        List<Map<String, Object>> list = result.getRecords().stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId());
            m.put("userName", e.getUserName());
            m.put("role", e.getRole());
            m.put("module", e.getModule());
            m.put("action", e.getAction());
            m.put("target", e.getTarget());
            m.put("detail", e.getDetail());
            m.put("ip", e.getIp());
            m.put("status", e.getStatus());
            m.put("createdAt", e.getCreatedAt());
            return m;
        }).toList();

        return PageResult.of(list, result.getTotal(), page, pageSize);
    }
}
