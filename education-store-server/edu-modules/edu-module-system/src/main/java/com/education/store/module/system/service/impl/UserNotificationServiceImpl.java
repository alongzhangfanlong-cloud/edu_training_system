package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.system.entity.system.UserNotification;
import com.education.store.module.system.mapper.UserNotificationMapper;
import com.education.store.module.system.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户消息通知 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl extends ServiceImpl<UserNotificationMapper, UserNotification>
        implements UserNotificationService {

    private final UserNotificationMapper notificationMapper;

    @Override
    public IPage<UserNotification> selectNotificationList(Page<UserNotification> page, Long userId, String type) {
        LambdaQueryWrapper<UserNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserNotification::getUserId, userId);
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(UserNotification::getType, type);
        }
        wrapper.orderByDesc(UserNotification::getCreateTime);
        wrapper.eq(UserNotification::getStatus, 1);
        return notificationMapper.selectPage(page, wrapper);
    }

    @Override
    public UserNotification selectNotificationById(Long id) {
        return notificationMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNotification(UserNotification notification) {
        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        if (notification.getStatus() == null) {
            notification.setStatus(1);
        }
        return notificationMapper.insert(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markAsRead(Long id, Long userId) {
        return notificationMapper.markAsRead(id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markAllAsRead(Long userId) {
        return notificationMapper.markAllAsRead(userId);
    }

    @Override
    public Map<String, Object> getUnreadCount(Long userId) {
        int count = notificationMapper.countUnreadByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return result;
    }
}
