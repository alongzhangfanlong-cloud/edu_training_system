package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.UserNotification;

import java.util.Map;

/**
 * 用户消息通知 Service 接口
 */
public interface UserNotificationService extends IService<UserNotification> {

    /**
     * 分页查询用户通知列表
     */
    IPage<UserNotification> selectNotificationList(Page<UserNotification> page, Long userId, String type);

    /**
     * 根据ID查询通知详情
     */
    UserNotification selectNotificationById(Long id);

    /**
     * 新增通知
     */
    int insertNotification(UserNotification notification);

    /**
     * 标记单条通知为已读
     */
    int markAsRead(Long id, Long userId);

    /**
     * 标记所有通知为已读
     */
    int markAllAsRead(Long userId);

    /**
     * 获取未读消息数量
     */
    Map<String, Object> getUnreadCount(Long userId);
}
