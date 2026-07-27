package com.education.store.module.system.listener;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.store.common.event.*;
import com.education.store.module.system.config.SubscribeTemplateConfig;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.entity.system.UserNotification;
import com.education.store.module.system.entity.system.UserNotificationSetting;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.system.mapper.UserNotificationSettingMapper;
import com.education.store.module.system.service.UserNotificationService;
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
 * 消息通知事件监听器
 * <p>异步监听业务事件，同时：1) 推送微信订阅消息；2) 写入站内消息(user_notification)</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    /** 系统消息发送者ID (0表示系统) */
    private static final Long SYSTEM_SENDER_ID = 0L;

    private final WxMaService wxMaService;
    private final SubscribeTemplateConfig templateConfig;
    private final UserNotificationSettingMapper settingMapper;
    private final SysUserMapper userMapper;
    private final EduStudentMapper studentMapper;
    private final UserNotificationService notificationService;

    /** 限频: 同一 openid + 通知类型，60 秒内不重复发微信消息 */
    private final Cache<String, Boolean> rateLimitCache =
        CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();

    // ==================== SIGN_IN 签到 ====================

    @Async
    @EventListener
    public void onSignIn(SignInEvent event) {
        try {
            if (event == null || event.getStudentId() == null) {
                log.warn("签到事件数据不完整，跳过处理");
                return;
            }

            String templateId = getTemplateId("SIGN_IN");

            EduStudent student = studentMapper.selectById(event.getStudentId());
            if (student == null || student.getParentId() == null) {
                log.debug("签到事件: 学生不存在或无家长ID, studentId={}", event.getStudentId());
                return;
            }

            SysUser parent = userMapper.selectById(student.getParentId());
            if (parent == null) {
                log.debug("签到事件: 家长用户不存在, parentId={}", student.getParentId());
                return;
            }

            Long parentUserId = parent.getId();
            String signinTimeStr = formatTime(event.getSigninTime());
            String courseName = event.getCourseName() != null ? event.getCourseName() : "课程";
            String title = "签到通知";
            String content = String.format("您的孩子%s已于%s在%s完成签到",
                student.getName(), signinTimeStr, courseName);

            // 写入站内消息（relatedId使用scheduleId，SignInEvent中courseId字段实际存储的是scheduleId）
            saveNotification(parentUserId, "SIGN_IN", title, content, event.getCourseId(), "SCHEDULE");

            // 发送微信订阅消息（需用户授权）
            if (!isBlank(parent.getWxOpenid()) && templateId != null
                && isNotifyEnabled(parentUserId, "SIGN_IN")
                && rateLimitOk(parent.getWxOpenid(), "SIGN_IN")) {
                Map<String, String> data = new LinkedHashMap<>();
                data.put("thing1", truncateForWeChat(courseName, 20));
                data.put("time2", signinTimeStr);
                data.put("thing3", truncateForWeChat(student.getName(), 20));
                send(toMsg(templateId, parent.getWxOpenid(), data), parent.getWxOpenid(), "SIGN_IN");
            }
        } catch (Exception e) {
            log.error("处理签到事件异常", e);
        }
    }

    // ==================== HOMEWORK 作业发布 ====================

    @Async
    @EventListener
    public void onHomeworkPublished(HomeworkPublishedEvent event) {
        try {
            if (event == null || event.getClassId() == null) {
                log.warn("作业发布事件数据不完整，跳过处理");
                return;
            }

            String templateId = getTemplateId("HOMEWORK");

            List<EduStudent> students;
            try {
                students = studentMapper.selectByClassId(event.getClassId());
            } catch (DataAccessException e) {
                log.warn("查询班级学员失败: classId={}", event.getClassId(), e);
                return;
            }
            if (students == null || students.isEmpty()) return;

            List<Long> parentIds = students.stream()
                .map(EduStudent::getParentId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
            if (parentIds.isEmpty()) return;

            Map<Long, SysUser> parentMap;
            try {
                parentMap = userMapper.selectBatchIds(parentIds).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(SysUser::getId, Function.identity(), (a, b) -> a));
            } catch (Exception e) {
                log.warn("批量查询家长用户失败", e);
                return;
            }

            String title = "新作业通知";
            String homeworkTitle = event.getTitle() != null ? event.getTitle() : "";
            String className = event.getClassName() != null ? event.getClassName() : "课程";
            String deadlineStr = event.getDeadline() != null ? event.getDeadline().toString() : "未设截止日期";

            for (EduStudent s : students) {
                if (s == null || s.getParentId() == null) continue;
                SysUser parent = parentMap.get(s.getParentId());
                if (parent == null) continue;

                String content = String.format("%s布置了新作业：%s，截止日期：%s",
                    className, homeworkTitle, deadlineStr);

                // 写入站内消息
                saveNotification(parent.getId(), "HOMEWORK", title, content, event.getHomeworkId(), "HOMEWORK");

                // 微信订阅消息
                if (templateId != null && !isBlank(parent.getWxOpenid())
                    && isNotifyEnabled(parent.getId(), "HOMEWORK")
                    && rateLimitOk(parent.getWxOpenid(), "HOMEWORK")) {
                    Map<String, String> data = new LinkedHashMap<>();
                    data.put("thing1", truncateForWeChat(className, 20));
                    data.put("thing2", truncateForWeChat(homeworkTitle, 20));
                    data.put("date3", deadlineStr);
                    send(toMsg(templateId, parent.getWxOpenid(), data), parent.getWxOpenid(), "HOMEWORK");
                }
            }
            log.info("作业发布通知处理完成: homeworkId={}, classId={}, 通知家长数={}",
                event.getHomeworkId(), event.getClassId(), parentMap.size());
        } catch (Exception e) {
            log.error("处理作业发布事件异常", e);
        }
    }

    // ==================== LEAVE 请假审批 ====================

    @Async
    @EventListener
    public void onLeaveApproved(LeaveApprovedEvent event) {
        try {
            if (event == null || event.getUserId() == null) {
                log.warn("请假审批事件数据不完整，跳过处理");
                return;
            }

            String templateId = getTemplateId("LEAVE");

            SysUser user = userMapper.selectById(event.getUserId());
            if (user == null) {
                log.debug("请假审批事件: 用户不存在, userId={}", event.getUserId());
                return;
            }

            String result = event.getResult() != null ? event.getResult() : "";
            String resultText = "通过".equals(result) ? "已通过" : "已驳回";
            String title = "请假审批结果";
            String reason = event.getReason() != null ? event.getReason() : "";
            String content = String.format("您的请假申请%s%s%s",
                resultText,
                reason.isEmpty() ? "" : "，原因：" + reason,
                event.getName() != null ? "（" + event.getName() + "）" : "");

            // 写入站内消息
            saveNotification(event.getUserId(), "LEAVE", title, content, event.getLeaveId(), "LEAVE");

            // 微信订阅消息
            if (templateId != null && !isBlank(user.getWxOpenid())
                && isNotifyEnabled(event.getUserId(), "LEAVE")
                && rateLimitOk(user.getWxOpenid(), "LEAVE")) {
                Map<String, String> data = new LinkedHashMap<>();
                data.put("thing1", truncateForWeChat(event.getName() != null ? event.getName() : "", 20));
                data.put("phrase2", truncateForWeChat(resultText, 5));
                data.put("thing3", truncateForWeChat(reason, 20));
                send(toMsg(templateId, user.getWxOpenid(), data), user.getWxOpenid(), "LEAVE");
            }
        } catch (Exception e) {
            log.error("处理请假审批事件异常", e);
        }
    }

    // ==================== EVALUATION 课堂评价 ====================

    @Async
    @EventListener
    public void onEvaluationSubmitted(EvaluationSubmittedEvent event) {
        try {
            if (event == null || event.getStudentId() == null) {
                log.warn("课堂评价事件数据不完整，跳过处理");
                return;
            }

            String templateId = getTemplateId("EVALUATION");

            EduStudent student = studentMapper.selectById(event.getStudentId());
            if (student == null || student.getParentId() == null) {
                log.debug("课堂评价事件: 学生不存在或无家长ID, studentId={}", event.getStudentId());
                return;
            }

            SysUser parent = userMapper.selectById(student.getParentId());
            if (parent == null) return;

            String scoreStr = event.getScore() != null ? event.getScore() : "";
            String courseName = event.getCourseName() != null ? event.getCourseName() : "";
            String evalContent = event.getContent() != null ? event.getContent() : "";
            String title = "课堂评价通知";
            String content = String.format("您的孩子%s在%s课程收到老师评价：%s%s",
                student.getName(), courseName, evalContent,
                !scoreStr.isEmpty() ? "，评分：" + scoreStr : "");

            // 写入站内消息
            saveNotification(parent.getId(), "EVALUATION", title, content, event.getEvaluationId(), "EVALUATION");

            // 微信订阅消息
            if (templateId != null && !isBlank(parent.getWxOpenid())
                && isNotifyEnabled(parent.getId(), "EVALUATION")
                && rateLimitOk(parent.getWxOpenid(), "EVALUATION")) {
                Map<String, String> data = new LinkedHashMap<>();
                data.put("thing1", truncateForWeChat(courseName, 20));
                data.put("thing2", truncateForWeChat(evalContent, 20));
                data.put("thing3", truncateForWeChat(scoreStr, 20));
                send(toMsg(templateId, parent.getWxOpenid(), data), parent.getWxOpenid(), "EVALUATION");
            }
        } catch (Exception e) {
            log.error("处理课堂评价事件异常", e);
        }
    }

    // ==================== POINTS 积分兑换 ====================

    @Async
    @EventListener
    public void onPointsRedemption(PointsRedemptionEvent event) {
        try {
            if (event == null || event.getStudentId() == null) {
                log.warn("积分兑换事件数据不完整，跳过处理");
                return;
            }

            String templateId = getTemplateId("REDEMPTION");

            EduStudent student = studentMapper.selectById(event.getStudentId());
            if (student == null || student.getParentId() == null) {
                log.debug("积分兑换事件: 学生不存在或无家长ID, studentId={}", event.getStudentId());
                return;
            }

            SysUser parent = userMapper.selectById(student.getParentId());
            if (parent == null) return;

            String status = event.getStatus() != null ? event.getStatus() : "";
            String statusText = "CLAIMED".equals(status) ? "已发放" : "已取消";
            String itemName = event.getItemName() != null ? event.getItemName() : "礼品";
            int amount = event.getAmount() != null ? event.getAmount() : 0;
            String title = "积分兑换" + statusText;
            String content = String.format("您的孩子%s兑换的%s%s，消耗%d积分",
                student.getName(), itemName, statusText, amount);

            // 写入站内消息（pointsRecordId就是兑换记录ID）
            saveNotification(parent.getId(), "REDEMPTION", title, content, event.getPointsRecordId(), "REDEMPTION");

            // 微信订阅消息
            if (templateId != null && !isBlank(parent.getWxOpenid())
                && isNotifyEnabled(parent.getId(), "REDEMPTION")
                && rateLimitOk(parent.getWxOpenid(), "REDEMPTION")) {
                Map<String, String> data = new LinkedHashMap<>();
                data.put("thing1", truncateForWeChat(itemName, 20));
                data.put("phrase2", truncateForWeChat(statusText, 5));
                data.put("amount3", amount + "积分");
                send(toMsg(templateId, parent.getWxOpenid(), data), parent.getWxOpenid(), "REDEMPTION");
            }
        } catch (Exception e) {
            log.error("处理积分兑换事件异常", e);
        }
    }

    // ==================== 站内消息写入 ====================

    private void saveNotification(Long userId, String type, String title, String content,
                                   Long relatedId, String relatedType) {
        if (userId == null) {
            log.warn("写入站内消息失败: userId为空, type={}", type);
            return;
        }
        try {
            UserNotification n = new UserNotification();
            n.setUserId(userId);
            n.setSenderId(SYSTEM_SENDER_ID);
            n.setType(type);
            n.setEventType(type);
            n.setTitle(title);
            n.setContent(content);
            n.setRelatedId(relatedId);
            n.setRelatedType(relatedType);
            n.setIsRead(0);
            n.setStatus(1);
            notificationService.insertNotification(n);
        } catch (Exception e) {
            log.warn("写入站内消息失败: userId={} type={} title={}", userId, type, title, e);
        }
    }

    // ==================== HELPERS ====================

    /** 微信订阅消息thing类型字段限长20字符，phrase限长5字符 */
    private String truncateForWeChat(String s, int maxLen) {
        if (s == null) return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 1) + "…";
    }

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
            if (e.getValue() != null) {
                msg.addData(new WxMaSubscribeMessage.MsgData(e.getKey(), e.getValue()));
            }
        }
        return msg;
    }

    private void send(WxMaSubscribeMessage msg, String openid, String type) {
        try {
            WxMaMsgService msgService = wxMaService.getMsgService();
            msgService.sendSubscribeMsg(msg);
            log.debug("订阅消息发送成功: openid={} type={}", openid, type);
        } catch (Exception e) {
            String errMsg = e.getMessage() != null ? e.getMessage() : "";
            if (errMsg.contains("43101")) {
                log.debug("用户未订阅该消息: openid={} type={}", openid, type);
            } else if (errMsg.contains("43104") || errMsg.contains("template") || errMsg.contains("param")) {
                log.warn("订阅消息模板/参数错误: openid={} type={} err={}", openid, type, errMsg);
            } else {
                log.warn("订阅消息发送失败: openid={} type={} err={}", openid, type, errMsg);
            }
        }
    }

    private boolean isNotifyEnabled(Long userId, String notifyType) {
        try {
            LambdaQueryWrapper<UserNotificationSetting> w = new LambdaQueryWrapper<>();
            w.eq(UserNotificationSetting::getUserId, userId)
             .eq(UserNotificationSetting::getNotifyType, notifyType);
            UserNotificationSetting s = settingMapper.selectOne(w);
            return s != null && Integer.valueOf(1).equals(s.getEnabled());
        } catch (Exception e) {
            log.warn("查询用户通知设置失败: userId={}, notifyType={}", userId, notifyType, e);
            return true; // 查询失败时默认允许发送，不影响业务
        }
    }

    private boolean rateLimitOk(String openid, String notifyType) {
        if (openid == null) return false;
        String key = openid + ":" + notifyType;
        return rateLimitCache.asMap().putIfAbsent(key, Boolean.TRUE) == null;
    }

    private String formatTime(LocalDateTime time) {
        return time != null ? time.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) : "";
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}
