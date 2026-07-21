package com.education.store.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.store.module.system.entity.system.UserNotificationSetting;
import com.education.store.module.system.mapper.UserNotificationSettingMapper;
import com.education.store.module.system.service.IUserNotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNotificationSettingServiceImpl implements IUserNotificationSettingService {

    private final UserNotificationSettingMapper mapper;

    /** 所有通知类型默认开启 */
    private static final List<String> ALL_TYPES = List.of(
            "SIGN_IN", "EVALUATION", "HOMEWORK", "EXAM", "LEAVE", "REDEMPTION");

    @Override
    public Map<String, Boolean> getSettings(Long userId) {
        List<UserNotificationSetting> list = mapper.selectByUserId(userId);
        Map<String, Boolean> existing = list.stream()
                .collect(Collectors.toMap(
                        UserNotificationSetting::getNotifyType,
                        s -> s.getEnabled() == 1));
        // 补齐缺失的类型（默认开启）
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (String type : ALL_TYPES) {
            result.put(type, existing.getOrDefault(type, true));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSettings(Long userId, Map<String, Boolean> settings) {
        for (Map.Entry<String, Boolean> entry : settings.entrySet()) {
            String type = entry.getKey();
            int enabled = Boolean.TRUE.equals(entry.getValue()) ? 1 : 0;

            // 查询是否存在
            LambdaQueryWrapper<UserNotificationSetting> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserNotificationSetting::getUserId, userId)
                    .eq(UserNotificationSetting::getNotifyType, type);
            UserNotificationSetting exist = mapper.selectOne(wrapper);

            if (exist != null) {
                exist.setEnabled(enabled);
                mapper.updateById(exist);
            } else {
                UserNotificationSetting s = new UserNotificationSetting();
                s.setUserId(userId);
                s.setNotifyType(type);
                s.setEnabled(enabled);
                mapper.insert(s);
            }
        }
    }

    @Override
    public boolean isEnabled(Long userId, String notifyType) {
        LambdaQueryWrapper<UserNotificationSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserNotificationSetting::getUserId, userId)
                .eq(UserNotificationSetting::getNotifyType, notifyType);
        UserNotificationSetting s = mapper.selectOne(wrapper);
        // 未配置视为默认开启
        return s == null || s.getEnabled() == 1;
    }
}
