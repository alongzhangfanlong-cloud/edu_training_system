package com.education.store.module.system.listener;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.store.common.event.*;
import com.education.store.module.system.config.SubscribeTemplateConfig;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.entity.system.UserNotificationSetting;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.system.mapper.UserNotificationSettingMapper;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 微信订阅消息事件监听器
 * <p>异步监听业务事件并推送微信订阅消息给家长/用户</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final WxMaService wxMaService;
    private final SubscribeTemplateConfig templateConfig;
    private final UserNotificationSettingMapper settingMapper;
    private final SysUserMapper userMapper;
    private final EduStudentMapper studentMapper;

    /** 限频: 同一 openid + 通知类型，60 秒内不重复 */
    private final Cache<String, Boolean> rateLimitCache =
        CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();

    // ==================== SIGN_IN ====================

    @Async
    @EventListener
    public void onSignIn(SignInEvent event) {
        String templateId = getTemplateId("SIGN_IN");
        if (templateId == null) return;

        EduStudent student = studentMapper.selectById(event.getStudentId());
        if (student == null || student.getParentId() == null) return;

        SysUser parent = userMapper.selectById(student.getParentId());
        if (parent == null || isBlank(parent.getWxOpenid())) return;
        if (!isNotifyEnabled(parent.getId(), "SIGN_IN")) return;
        if (!rateLimitOk(parent.getWxOpenid(), "SIGN_IN")) return;

        Map<String, String> data = new LinkedHashMap<>();
        data.put("thing1", event.getCourseName());
        data.put("time2", formatTime(event.getSigninTime()));
        data.put("thing3", student.getName());

        send(toMsg(templateId, parent.getWxOpenid(), data), parent.getWxOpenid(), "SIGN_IN");
    }

    // ==================== HOMEWORK ====================

    @Async
    @EventListener
    public void onHomeworkPublished(HomeworkPublishedEvent event) {
        String templateId = getTemplateId("HOMEWORK");
        if (templateId == null) return;

        List<EduStudent> students;
        try {
            students = studentMapper.selectByClassId(event.getClassId());
        } catch (DataAccessException e) {
            log.warn("查询班级学员失败: classId={}", event.getClassId(), e);
            return;
        }
        if (students == null || students.isEmpty()) return;

        // 批量查询家长，避免 N+1
        List<Long> parentIds = students.stream()
            .map(EduStudent::getParentId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        if (parentIds.isEmpty()) return;
        Map<Long, SysUser> parentMap = userMapper.selectBatchIds(parentIds).stream()
            .collect(Collectors.toMap(SysUser::getId, Function.identity()));

        for (EduStudent s : students) {
            if (s.getParentId() == null) continue;
            SysUser parent = parentMap.get(s.getParentId());
            if (parent == null || isBlank(parent.getWxOpenid())) continue;
            if (!isNotifyEnabled(parent.getId(), "HOMEWORK")) continue;
            if (!rateLimitOk(parent.getWxOpenid(), "HOMEWORK")) continue;

            Map<String, String> data = new LinkedHashMap<>();
            data.put("thing1", event.getClassName());
            data.put("thing2", event.getTitle());
            data.put("date3", event.getDeadline() != null ? event.getDeadline().toString() : "未设截止日期");

            send(toMsg(templateId, parent.getWxOpenid(), data), parent.getWxOpenid(), "HOMEWORK");
        }
    }

    // ==================== LEAVE ====================

    @Async
    @EventListener
    public void onLeaveApproved(LeaveApprovedEvent event) {
        String templateId = getTemplateId("LEAVE");
        if (templateId == null) return;

        SysUser user = userMapper.selectById(event.getUserId());
        if (user == null || isBlank(user.getWxOpenid())) return;
        if (!isNotifyEnabled(user.getId(), "LEAVE")) return;
        if (!rateLimitOk(user.getWxOpenid(), "LEAVE")) return;

        Map<String, String> data = new LinkedHashMap<>();
        data.put("thing1", event.getName());
        data.put("phrase2", event.getResult());
        data.put("thing3", event.getReason() != null ? event.getReason() : "");

        send(toMsg(templateId, user.getWxOpenid(), data), user.getWxOpenid(), "LEAVE");
    }

    // ==================== EVALUATION ====================

    @Async
    @EventListener
    public void onEvaluationSubmitted(EvaluationSubmittedEvent event) {
        String templateId = getTemplateId("EVALUATION");
        if (templateId == null) return;

        EduStudent student = studentMapper.selectById(event.getStudentId());
        if (student == null || student.getParentId() == null) return;

        SysUser parent = userMapper.selectById(student.getParentId());
        if (parent == null || isBlank(parent.getWxOpenid())) return;
        if (!isNotifyEnabled(parent.getId(), "EVALUATION")) return;
        if (!rateLimitOk(parent.getWxOpenid(), "EVALUATION")) return;

        Map<String, String> data = new LinkedHashMap<>();
        data.put("thing1", event.getCourseName() != null ? event.getCourseName() : "");
        data.put("thing2", event.getContent() != null ? event.getContent() : "");
        data.put("thing3", event.getScore() != null ? event.getScore() : "");

        send(toMsg(templateId, parent.getWxOpenid(), data), parent.getWxOpenid(), "EVALUATION");
    }

    // ==================== POINTS ====================

    @Async
    @EventListener
    public void onPointsRedemption(PointsRedemptionEvent event) {
        String templateId = getTemplateId("REDEMPTION");
        if (templateId == null) return;

        EduStudent student = studentMapper.selectById(event.getStudentId());
        if (student == null || student.getParentId() == null) return;

        SysUser parent = userMapper.selectById(student.getParentId());
        if (parent == null || isBlank(parent.getWxOpenid())) return;
        if (!isNotifyEnabled(parent.getId(), "REDEMPTION")) return;
        if (!rateLimitOk(parent.getWxOpenid(), "REDEMPTION")) return;

        String statusText = "CLAIMED".equals(event.getStatus()) ? "已发放" : "已取消";

        Map<String, String> data = new LinkedHashMap<>();
        data.put("thing1", event.getItemName());
        data.put("phrase2", statusText);
        data.put("amount3", (event.getAmount() != null ? event.getAmount() : 0) + "积分");

        send(toMsg(templateId, parent.getWxOpenid(), data), parent.getWxOpenid(), "REDEMPTION");
    }

    // ==================== HELPERS ====================

    private String getTemplateId(String type) {
        Map<String, String> templates = templateConfig.getSubscribeTemplates();
        if (templates == null) return null;
        String id = templates.get(type);
        return (id == null || id.isBlank()) ? null : id;
    }

    private WxMaSubscribeMessage toMsg(String templateId, String openid, Map<String, String> data) {
        WxMaSubscribeMessage msg = WxMaSubscribeMessage.builder()
            .toUser(openid)
            .templateId(templateId)
            .build();
        for (Map.Entry<String, String> e : data.entrySet()) {
            msg.addData(new WxMaSubscribeMessage.MsgData(e.getKey(), e.getValue()));
        }
        return msg;
    }

    private void send(WxMaSubscribeMessage msg, String openid, String type) {
        try {
            WxMaMsgService msgService = wxMaService.getMsgService();
            msgService.sendSubscribeMsg(msg);
            log.info("订阅消息发送成功: openid={} type={}", openid, type);
        } catch (Exception e) {
            String errMsg = e.getMessage() != null ? e.getMessage() : "";
            if (errMsg.contains("43101")) {
                log.debug("用户未订阅该消息: openid={} type={}", openid, type);
            } else {
                log.warn("订阅消息发送失败: openid={} type={} err={}", openid, type, errMsg);
            }
        }
    }

    private boolean isNotifyEnabled(Long userId, String notifyType) {
        LambdaQueryWrapper<UserNotificationSetting> w = new LambdaQueryWrapper<>();
        w.eq(UserNotificationSetting::getUserId, userId)
         .eq(UserNotificationSetting::getNotifyType, notifyType);
        UserNotificationSetting s = settingMapper.selectOne(w);
        return s != null && Integer.valueOf(1).equals(s.getEnabled());
    }

    private boolean rateLimitOk(String openid, String notifyType) {
        String key = openid + ":" + notifyType;
        return rateLimitCache.asMap().putIfAbsent(key, Boolean.TRUE) == null;
    }

    private String formatTime(LocalDateTime time) {
        return time != null ? time.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) : "";
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}
