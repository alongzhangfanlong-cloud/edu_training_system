package com.education.store.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.store.module.system.entity.system.UserNotificationSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserNotificationSettingMapper extends BaseMapper<UserNotificationSetting> {

    @Select("SELECT * FROM user_notification_setting WHERE user_id = #{userId}")
    List<UserNotificationSetting> selectByUserId(@Param("userId") Long userId);
}
