package com.education.store.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.module.system.entity.system.UserNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户消息通知 Mapper
 */
@Mapper
public interface UserNotificationMapper extends BaseMapper<UserNotification> {

    /**
     * 获取未读消息数量
     */
    @Select("SELECT COUNT(1) FROM user_notification WHERE user_id = #{userId} AND is_read = 0 AND status = 1 AND deleted = 0")
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 标记单条通知为已读
     */
    @Update("UPDATE user_notification SET is_read = 1, read_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 标记所有通知为已读
     */
    @Update("UPDATE user_notification SET is_read = 1, read_time = NOW() WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") Long userId);
}
