package com.education.store.module.system.service;

import com.education.store.module.system.entity.system.UserNotificationSetting;

import java.util.List;
import java.util.Map;

/**
 * 用户通知偏好设置 Service
 */
public interface IUserNotificationSettingService {

    /** 获取用户所有通知设置 */
    Map<String, Boolean> getSettings(Long userId);

    /** 批量更新通知设置 */
    void updateSettings(Long userId, Map<String, Boolean> settings);

    /** 检查某类通知是否开启（未配置视为开启） */
    boolean isEnabled(Long userId, String notifyType);
}
