# 微信订阅消息接入 · 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为小程序接入微信订阅消息，5 类业务事件（签到/作业/请假/评价/兑换）发生时向用户推送模板通知。

**Architecture:** Spring Event 驱动 — 业务 Service 发布 ApplicationEvent，NotificationEventListener 异步监听、查偏好、查 openid、调 WxMaService 发送。前端 subscribe.js 封装 wx.requestSubscribeMessage，在消息设置页提供订阅入口。

**Tech Stack:** Spring ApplicationEventPublisher + @EventListener + @Async, binarywang WxMaService, Guava Cache 限频, 小程序 wx.requestSubscribeMessage

---

## Task 1: 事件类（5 个）

**Files:**
- Create: `education-store-server/edu-common/src/main/java/com/education/store/common/event/SignInEvent.java`
- Create: `education-store-server/edu-common/src/main/java/com/education/store/common/event/HomeworkPublishedEvent.java`
- Create: `education-store-server/edu-common/src/main/java/com/education/store/common/event/LeaveApprovedEvent.java`
- Create: `education-store-server/edu-common/src/main/java/com/education/store/common/event/EvaluationSubmittedEvent.java`
- Create: `education-store-server/edu-common/src/main/java/com/education/store/common/event/PointsRedemptionEvent.java`

- [ ] **Step 1: 创建 5 个事件类**

```java
// SignInEvent.java
package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class SignInEvent extends ApplicationEvent {
    private final Long studentId;
    private final Long courseId;
    private final String courseName;
    private final LocalDateTime signinTime;

    public SignInEvent(Object source, Long studentId, Long courseId,
                       String courseName, LocalDateTime signinTime) {
        super(source);
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.signinTime = signinTime;
    }
}
```

```java
// HomeworkPublishedEvent.java
package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

@Getter
public class HomeworkPublishedEvent extends ApplicationEvent {
    private final Long homeworkId;
    private final Long classId;
    private final String className;
    private final String title;
    private final LocalDate deadline;

    public HomeworkPublishedEvent(Object source, Long homeworkId, Long classId,
                                  String className, String title, LocalDate deadline) {
        super(source);
        this.homeworkId = homeworkId;
        this.classId = classId;
        this.className = className;
        this.title = title;
        this.deadline = deadline;
    }
}
```

```java
// LeaveApprovedEvent.java
package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveApprovedEvent extends ApplicationEvent {
    private final Long leaveId;
    private final String role;      // STUDENT / TEACHER
    private final Long userId;      // 请假人 userId
    private final String name;      // 请假人姓名
    private final String result;    // 通过 / 驳回
    private final String reason;

    public LeaveApprovedEvent(Object source, Long leaveId, String role, Long userId,
                              String name, String result, String reason) {
        super(source);
        this.leaveId = leaveId;
        this.role = role;
        this.userId = userId;
        this.name = name;
        this.result = result;
        this.reason = reason;
    }
}
```

```java
// EvaluationSubmittedEvent.java
package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EvaluationSubmittedEvent extends ApplicationEvent {
    private final Long evaluationId;
    private final Long studentId;
    private final String courseName;
    private final String content;     // 评价摘要（≤20字）
    private final String score;

    public EvaluationSubmittedEvent(Object source, Long evaluationId, Long studentId,
                                    String courseName, String content, String score) {
        super(source);
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        this.courseName = courseName;
        this.content = content;
        this.score = score;
    }
}
```

```java
// PointsRedemptionEvent.java
package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PointsRedemptionEvent extends ApplicationEvent {
    private final Long pointsRecordId;
    private final Long studentId;
    private final String itemName;
    private final String status;      // CLAIMED / CANCELLED
    private final Integer amount;

    public PointsRedemptionEvent(Object source, Long pointsRecordId, Long studentId,
                                 String itemName, String status, Integer amount) {
        super(source);
        this.pointsRecordId = pointsRecordId;
        this.studentId = studentId;
        this.itemName = itemName;
        this.status = status;
        this.amount = amount;
    }
}
```

- [ ] **Step 2: 验证编译通过**

```bash
cd education-store-server && mvn compile -q -pl edu-common
```

- [ ] **Step 3: 提交**

```bash
git add education-store-server/edu-common/src/main/java/com/education/store/common/event/
git commit -m "feat: 订阅消息事件类 — 签到/作业/请假/评价/兑换 5 个事件"
```

---

## Task 2: 配置类 + 监听器

**Files:**
- Create: `education-store-server/edu-modules/edu-module-system/src/main/java/com/education/store/module/system/config/SubscribeTemplateConfig.java`
- Create: `education-store-server/edu-modules/edu-module-system/src/main/java/com/education/store/module/system/listener/NotificationEventListener.java`
- Modify: `education-store-server/edu-server-app/src/main/resources/application.yml` — 添加模板 ID 配置段
- Modify: `education-store-server/edu-server-app/src/main/java/com/education/store/EduApplication.java` — 添加 @EnableAsync

- [ ] **Step 1: 创建模板配置类**

```java
// SubscribeTemplateConfig.java
package com.education.store.module.system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "wechat.mini-app")
public class SubscribeTemplateConfig {
    /** 订阅消息模板ID映射，key=通知类型, value=模板ID */
    private Map<String, String> subscribeTemplates;
}
```

- [ ] **Step 2: 在 application.yml 中添加配置**

在 `wechat.mini-app` 段下添加：

```yaml
    subscribe-templates:
      SIGN_IN: ""
      HOMEWORK: ""
      LEAVE: ""
      EVALUATION: ""
      REDEMPTION: ""
```

编辑位置为 `education-store-server/edu-server-app/src/main/resources/application.yml`，在 `wechat.mini-app.app-secret` 行后插入。

- [ ] **Step 3: 创建监听器**

```java
// NotificationEventListener.java
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final WxMaService wxMaService;
    private final SubscribeTemplateConfig templateConfig;
    private final UserNotificationSettingMapper settingMapper;
    private final SysUserMapper userMapper;
    private final EduStudentMapper studentMapper;

    // 限频: 同一 openid + 同一通知类型，60 秒内不重复发送
    private final Cache<String, Boolean> rateLimitCache =
        CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();

    @Async
    @EventListener
    public void onSignIn(SignInEvent event) {
        // 查学生 → 查家长 → 查 openid
        EduStudent student = studentMapper.selectById(event.getStudentId());
        if (student == null || student.getParentId() == null) return;
        String templateId = templateConfig.getSubscribeTemplates().get("SIGN_IN");
        if (isBlank(templateId)) return;

        // 查家长 userId → openid → 偏好
        List<SysUser> parents = userMapper.selectByStudentParentRole(student.getParentId());
        for (SysUser parent : parents) {
            if (!isNotifyEnabled(parent.getId(), "SIGN_IN")) continue;
            String openid = parent.getWxOpenid();
            if (isBlank(openid)) continue;
            if (!rateLimit(openid, "SIGN_IN")) continue;

            WxMaSubscribeMessage msg = WxMaSubscribeMessage.builder()
                .toUser(openid)
                .templateId(templateId)
                .data(buildData(
                    "thing1", event.getCourseName(),      // 课程名称
                    "time2", formatTime(event.getSigninTime()), // 签到时间
                    "thing3", student.getName()            // 学生姓名
                ))
                .build();
            send(msg, openid, "SIGN_IN");
        }
    }

    @Async
    @EventListener
    public void onHomeworkPublished(HomeworkPublishedEvent event) {
        String templateId = templateConfig.getSubscribeTemplates().get("HOMEWORK");
        if (isBlank(templateId)) return;

        // 查班级下所有学生的家长 → 去重 → 发送
        List<EduStudent> students = studentMapper.selectByClassId(event.getClassId());
        List<Long> parentIds = students.stream()
            .map(EduStudent::getParentId).filter(p -> p != null).distinct()
            .collect(Collectors.toList());

        for (Long parentId : parentIds) {
            List<SysUser> parents = userMapper.selectByStudentParentRole(parentId);
            for (SysUser parent : parents) {
                if (!isNotifyEnabled(parent.getId(), "HOMEWORK")) continue;
                String openid = parent.getWxOpenid();
                if (isBlank(openid)) continue;
                if (!rateLimit(openid, "HOMEWORK")) continue;

                WxMaSubscribeMessage msg = WxMaSubscribeMessage.builder()
                    .toUser(openid)
                    .templateId(templateId)
                    .data(buildData(
                        "thing1", event.getClassName(),      // 班级名称
                        "thing2", event.getTitle(),          // 作业标题
                        "date3", event.getDeadline() != null
                            ? event.getDeadline().toString() : "未设截止日期"
                    ))
                    .build();
                send(msg, openid, "HOMEWORK");
            }
        }
    }

    @Async
    @EventListener
    public void onLeaveApproved(LeaveApprovedEvent event) {
        String templateId = templateConfig.getSubscribeTemplates().get("LEAVE");
        if (isBlank(templateId)) return;

        SysUser user = userMapper.selectById(event.getUserId());
        if (user == null) return;
        String openid = user.getWxOpenid();
        if (isBlank(openid)) return;
        if (!isNotifyEnabled(user.getId(), "LEAVE")) return;
        if (!rateLimit(openid, "LEAVE")) return;

        WxMaSubscribeMessage msg = WxMaSubscribeMessage.builder()
            .toUser(openid)
            .templateId(templateId)
            .data(buildData(
                "thing1", event.getName(),     // 请假人姓名
                "phrase2", event.getResult(),  // 审批结果
                "thing3", event.getReason()    // 请假原因
            ))
            .build();
        send(msg, openid, "LEAVE");
    }

    @Async
    @EventListener
    public void onEvaluationSubmitted(EvaluationSubmittedEvent event) {
        String templateId = templateConfig.getSubscribeTemplates().get("EVALUATION");
        if (isBlank(templateId)) return;

        EduStudent student = studentMapper.selectById(event.getStudentId());
        if (student == null || student.getParentId() == null) return;

        List<SysUser> parents = userMapper.selectByStudentParentRole(student.getParentId());
        for (SysUser parent : parents) {
            if (!isNotifyEnabled(parent.getId(), "EVALUATION")) continue;
            String openid = parent.getWxOpenid();
            if (isBlank(openid)) continue;
            if (!rateLimit(openid, "EVALUATION")) continue;

            WxMaSubscribeMessage msg = WxMaSubscribeMessage.builder()
                .toUser(openid)
                .templateId(templateId)
                .data(buildData(
                    "thing1", event.getCourseName(),  // 课程名称
                    "thing2", event.getContent(),     // 评价摘要
                    "thing3", event.getScore()        // 评分
                ))
                .build();
            send(msg, openid, "EVALUATION");
        }
    }

    @Async
    @EventListener
    public void onPointsRedemption(PointsRedemptionEvent event) {
        String templateId = templateConfig.getSubscribeTemplates().get("REDEMPTION");
        if (isBlank(templateId)) return;

        EduStudent student = studentMapper.selectById(event.getStudentId());
        if (student == null || student.getParentId() == null) return;

        List<SysUser> parents = userMapper.selectByStudentParentRole(student.getParentId());
        for (SysUser parent : parents) {
            if (!isNotifyEnabled(parent.getId(), "REDEMPTION")) continue;
            String openid = parent.getWxOpenid();
            if (isBlank(openid)) continue;
            if (!rateLimit(openid, "REDEMPTION")) continue;

            WxMaSubscribeMessage msg = WxMaSubscribeMessage.builder()
                .toUser(openid)
                .templateId(templateId)
                .data(buildData(
                    "thing1", event.getItemName(),     // 商品名称
                    "phrase2", "CLAIMED".equals(event.getStatus()) ? "已发放" : "已取消",
                    "amount3", event.getAmount() + "积分"
                ))
                .build();
            send(msg, openid, "REDEMPTION");
        }
    }

    // --- helpers ---

    private boolean isNotifyEnabled(Long userId, String notifyType) {
        LambdaQueryWrapper<UserNotificationSetting> w = new LambdaQueryWrapper<>();
        w.eq(UserNotificationSetting::getUserId, userId)
         .eq(UserNotificationSetting::getNotifyType, notifyType);
        UserNotificationSetting s = settingMapper.selectOne(w);
        return s != null && Integer.valueOf(1).equals(s.getEnabled());
    }

    private boolean rateLimit(String openid, String notifyType) {
        String key = openid + ":" + notifyType;
        if (rateLimitCache.getIfPresent(key) != null) return false;
        rateLimitCache.put(key, true);
        return true;
    }

    private void send(WxMaSubscribeMessage msg, String openid, String type) {
        try {
            WxMaMsgService msgService = wxMaService.getMsgService();
            msgService.sendSubscribeMsg(msg);
            log.info("订阅消息发送成功: openid={} type={}", openid, type);
        } catch (Exception e) {
            // 43101 = 用户未订阅，静默忽略
            String errMsg = e.getMessage() != null ? e.getMessage() : "";
            if (errMsg.contains("43101")) {
                log.debug("用户未订阅该消息: openid={} type={}", openid, type);
            } else {
                log.warn("订阅消息发送失败: openid={} type={} err={}", openid, type, errMsg);
            }
        }
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static String formatTime(LocalDateTime time) {
        return time != null ? time.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) : "";
    }

    @SafeVarargs
    private static java.util.Map<String, WxMaSubscribeMessage.MsgData> buildData(
            java.util.Map.Entry<String, String>... entries) {
        java.util.Map<String, WxMaSubscribeMessage.MsgData> data = new java.util.LinkedHashMap<>();
        for (var e : entries) {
            data.put(e.getKey(), new WxMaSubscribeMessage.MsgData(e.getValue()));
        }
        return data;
    }
}
```

- [ ] **Step 4: 添加 @EnableAsync**

编辑 `EduApplication.java`：

```java
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync   // 新增
@SpringBootApplication
@MapperScan("com.education.store.**.mapper")
@ServletComponentScan(basePackages = "com.education.store.common.filter")
public class EduApplication {
    // ... 其余保持不变
}
```

- [ ] **Step 5: 检查缺失的 Mapper 方法**

监听器需要以下 Mapper 查询，检查是否已有：

- `SysUserMapper` — `selectByStudentParentRole(Long parentId)`：根据家长角色查用户。如果不存在，用 SysUserMapper 已有的 `selectById` + 新建查询代替：
  实际上家长 userId 就是 parentId（sys_user 中的用户ID）。修改逻辑：家长通过 student.parentId 找到关联的 sys_user 记录，直接用 `SysUserMapper.selectById(parentId)` 即可。简化后不需要 `selectByStudentParentRole`。

- `EduStudentMapper` — `selectByClassId(Long classId)`：查班级下所有学生。检查是否有此方法，若无则需添加。

需要检查这两个 Mapper 是否已有对应方法。如果有缺失，在本任务中补齐。

- [ ] **Step 6: 验证编译**

```bash
cd education-store-server && mvn compile -q
```

修复任何编译错误直到通过。

- [ ] **Step 7: 提交**

```bash
git add education-store-server/edu-modules/edu-module-system/src/main/java/com/education/store/module/system/config/SubscribeTemplateConfig.java
git add education-store-server/edu-modules/edu-module-system/src/main/java/com/education/store/module/system/listener/NotificationEventListener.java
git add education-store-server/edu-server-app/src/main/resources/application.yml
git add education-store-server/edu-server-app/src/main/java/com/education/store/EduApplication.java
git commit -m "feat: 订阅消息监听器 + 模板配置 + @EnableAsync"
```

---

## Task 3: 签到事件发布

**File:** Modify `education-store-server/edu-modules/edu-module-attendance/src/main/java/com/education/store/module/attendance/service/impl/EduAttendanceServiceImpl.java`

- [ ] **Step 1: 在 batchSignin 方法末尾发布事件**

添加字段：

```java
import org.springframework.context.ApplicationEventPublisher;
import com.education.store.common.event.SignInEvent;
// ... 在现有的 @RequiredArgsConstructor 字段中添加:
private final ApplicationEventPublisher eventPublisher;
```

在 `batchSignin` 方法的 `return count;` 之前添加：

```java
// 发布签到事件（异步通知家长）
for (EduAttendance record : records) {
    String courseName = schedule != null ? schedule.getCourseName() : "";
    eventPublisher.publishEvent(new SignInEvent(
        this, record.getStudentId(), scheduleId, courseName, LocalDateTime.now()));
}
```

- [ ] **Step 2: 验证编译**

```bash
cd education-store-server && mvn compile -q -pl edu-module-attendance
```

- [ ] **Step 3: 提交**

```bash
git add education-store-server/edu-modules/edu-module-attendance/
git commit -m "feat: 签到事件发布 — batchSignin 末尾发布 SignInEvent"
```

---

## Task 4: 作业事件发布

**File:** Modify `education-store-server/edu-modules/edu-module-teaching/src/main/java/com/education/store/module/teaching/service/impl/EduHomeworkServiceImpl.java`

- [ ] **Step 1: 在 addHomework 末尾发布事件**

添加字段：

```java
import org.springframework.context.ApplicationEventPublisher;
import com.education.store.common.event.HomeworkPublishedEvent;
import com.education.store.module.schedule.entity.EduClass;
import com.education.store.module.schedule.mapper.EduClassMapper;

private final ApplicationEventPublisher eventPublisher;
private final EduClassMapper classMapper;
```

在 `addHomework` 方法的 `return homework;` 之前：

```java
// 发布作业通知事件
EduClass cls = classMapper.selectById(homework.getClassId());
String className = cls != null ? cls.getClassName() : "";
eventPublisher.publishEvent(new HomeworkPublishedEvent(
    this, homework.getId(), homework.getClassId(), className,
    homework.getTitle(), homework.getDeadline()));
```

- [ ] **Step 2: 检查 EduClassMapper 导入路径**

`EduClassMapper` 的位置需要通过 grep 确认：

```bash
grep -r "interface EduClassMapper" education-store-server/
```

若不存在，则改用 `scheduleMapper` 或对应 mapper 查询班级名称，或直接传递空字符串。

- [ ] **Step 3: 验证编译**

```bash
cd education-store-server && mvn compile -q -pl edu-module-teaching
```

- [ ] **Step 4: 提交**

```bash
git add education-store-server/edu-modules/edu-module-teaching/src/main/java/com/education/store/module/teaching/service/impl/EduHomeworkServiceImpl.java
git commit -m "feat: 作业事件发布 — addHomework 末尾发布 HomeworkPublishedEvent"
```

---

## Task 5: 请假事件发布

**File:** Modify `education-store-server/edu-modules/edu-module-attendance/src/main/java/com/education/store/module/attendance/service/impl/EduLeaveStudentServiceImpl.java`

- [ ] **Step 1: 在 approveLeave 和 rejectLeave 末尾发布事件**

添加字段：

```java
import org.springframework.context.ApplicationEventPublisher;
import com.education.store.common.event.LeaveApprovedEvent;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;

private final ApplicationEventPublisher eventPublisher;
private final SysUserMapper sysUserMapper;
```

在 `approveLeave` 的 `return leaveMapper.updateById(leave);` 之前：

```java
// 发布请假审批事件
EduStudent student = studentMapper.selectById(leave.getStudentId());
Long userId = student != null && student.getParentId() != null
    ? student.getParentId() : leave.getStudentId();
String studentName = student != null ? student.getName() : "";
eventPublisher.publishEvent(new LeaveApprovedEvent(
    this, id, "STUDENT", userId, studentName, "通过", leave.getReason()));
```

在 `rejectLeave` 的 `return leaveMapper.updateById(leave);` 之前：

```java
EduStudent student = studentMapper.selectById(leave.getStudentId());
Long userId = student != null && student.getParentId() != null
    ? student.getParentId() : leave.getStudentId();
String studentName = student != null ? student.getName() : "";
eventPublisher.publishEvent(new LeaveApprovedEvent(
    this, id, "STUDENT", userId, studentName, "驳回", reason));
```

- [ ] **Step 2: 注入缺失的 Mapper**

检查 `EduLeaveStudentServiceImpl` 是否已有 `EduStudentMapper` 字段。若没有，添加 `private final EduStudentMapper studentMapper;`。

- [ ] **Step 3: 验证编译**

```bash
cd education-store-server && mvn compile -q -pl edu-module-attendance
```

- [ ] **Step 4: 提交**

```bash
git add education-store-server/edu-modules/edu-module-attendance/src/main/java/com/education/store/module/attendance/service/impl/EduLeaveStudentServiceImpl.java
git commit -m "feat: 请假事件发布 — approve/rejectLeave 发布 LeaveApprovedEvent"
```

---

## Task 6: 评价事件发布

**File:** Modify `education-store-server/edu-modules/edu-module-teaching/src/main/java/com/education/store/module/teaching/service/impl/EduEvaluationServiceImpl.java`

- [ ] **Step 1: 在 submitEvaluation 末尾为每个学生发布事件**

添加字段：

```java
import org.springframework.context.ApplicationEventPublisher;
import com.education.store.common.event.EvaluationSubmittedEvent;

private final ApplicationEventPublisher eventPublisher;
```

在 `submitEvaluation` 的 `return evaluation;` 之前：

```java
// 发布评价通知事件（每个被评价的学生）
if (students != null) {
    String courseName = ""; // 从排课或评价中取，无法获取则传空
    for (EduEvaluationStudent s : students) {
        String content = evaluation.getSummary() != null
            ? (evaluation.getSummary().length() > 20
                ? evaluation.getSummary().substring(0, 20) + "..."
                : evaluation.getSummary())
            : "";
        String score = s.getRating() != null ? String.valueOf(s.getRating()) : "";
        eventPublisher.publishEvent(new EvaluationSubmittedEvent(
            this, evaluation.getId(), s.getStudentId(), courseName, content, score));
    }
}
```

- [ ] **Step 2: 获取课程名称**

需要从 `EduEvaluation` 实体中获取 `scheduleId`，再查 `EduSchedule` 获取 `courseName`。如果 `EduEvaluation` 没有 `scheduleId` 字段，则从排课表反查，或降级传空字符串。

检查实体是否有 `scheduleId` 字段：

```bash
grep -n "scheduleId\|courseName" education-store-server/edu-modules/edu-module-teaching/src/main/java/com/education/store/module/teaching/entity/EduEvaluation.java
```

- [ ] **Step 3: 验证编译**

```bash
cd education-store-server && mvn compile -q -pl edu-module-teaching
```

- [ ] **Step 4: 提交**

```bash
git add education-store-server/edu-modules/edu-module-teaching/src/main/java/com/education/store/module/teaching/service/impl/EduEvaluationServiceImpl.java
git commit -m "feat: 评价事件发布 — submitEvaluation 末尾发布 EvaluationSubmittedEvent"
```

---

## Task 7: 兑换事件发布

**File:** Modify `education-store-server/edu-modules/edu-module-points/src/main/java/com/education/store/module/points/service/impl/EduPointsRedemptionServiceImpl.java`

- [ ] **Step 1: 在 claim 和 cancel 方法末尾发布事件**

添加字段：

```java
import org.springframework.context.ApplicationEventPublisher;
import com.education.store.common.event.PointsRedemptionEvent;

private final ApplicationEventPublisher eventPublisher;
private final EduPointsGiftMapper giftMapper;
```

在 `claim` 方法的 `redemptionMapper.updateById(redemption);` 之后：

```java
// 发布兑换通知事件
EduPointsGift gift = giftMapper.selectById(redemption.getGiftId());
String giftName = gift != null ? gift.getName() : "礼品";
eventPublisher.publishEvent(new PointsRedemptionEvent(
    this, id, redemption.getStudentId(), giftName, "CLAIMED",
    redemption.getPointsSpent()));
```

在 `cancel` 方法的 `return;` 之前（或方法末尾）：

```java
// 发布兑换取消通知
EduPointsGift gift = giftMapper.selectById(redemption.getGiftId());
String giftName = gift != null ? gift.getName() : "礼品";
eventPublisher.publishEvent(new PointsRedemptionEvent(
    this, id, redemption.getStudentId(), giftName, "CANCELLED",
    redemption.getPointsSpent()));
```

- [ ] **Step 2: 验证编译**

```bash
cd education-store-server && mvn compile -q -pl edu-module-points
```

- [ ] **Step 3: 提交**

```bash
git add education-store-server/edu-modules/edu-module-points/src/main/java/com/education/store/module/points/service/impl/EduPointsRedemptionServiceImpl.java
git commit -m "feat: 兑换事件发布 — claim/cancel 发布 PointsRedemptionEvent"
```

---

## Task 8: 后端正体验证

- [ ] **Step 1: 全量编译**

```bash
cd education-store-server && mvn compile -q
```

修复任何编译错误直至通过。

- [ ] **Step 2: 启动验证**

```bash
cd education-store-server/edu-server-app && mvn spring-boot:run
```

检查启动日志中无 ERROR，确认 `SubscribeTemplateConfig` 和 `NotificationEventListener` 加载成功。

---

## Task 9: 小程序 subscribe.js 工具

**File:** Create `education-store-mini/src/utils/subscribe.js`

- [ ] **Step 1: 创建文件**

```js
/**
 * 微信订阅消息工具
 * 封装 wx.requestSubscribeMessage，统一处理授权结果
 */

// 模板 ID 常量（注册时替换为微信后台的真实模板 ID）
export const TMPL_IDS = {
  SIGN_IN: '',       // 签到通知
  HOMEWORK: '',      // 作业通知
  LEAVE: '',         // 请假通知
  EVALUATION: '',    // 评价通知
  REDEMPTION: '',    // 兑换通知
};

/**
 * 请求订阅消息授权
 * @param {string[]} tmplIds - 模板 ID 列表
 * @returns {Promise<{accepted: string[], rejected: string[]}>}
 */
export function requestSubscribe(tmplIds) {
  // 过滤掉空 ID（模板未配置时跳过）
  const ids = tmplIds.filter(Boolean);
  if (!ids.length) return Promise.resolve({ accepted: [], rejected: [] });

  return new Promise((resolve) => {
    wx.requestSubscribeMessage({
      tmplIds: ids,
      success(res) {
        // res[templateId] = 'accept' | 'reject' | 'ban'
        const accepted = [];
        const rejected = [];
        for (const [id, status] of Object.entries(res)) {
          if (status === 'accept') accepted.push(id);
          else rejected.push(id);
        }
        resolve({ accepted, rejected });
      },
      fail(err) {
        console.warn('订阅消息授权失败:', err.errMsg || err);
        resolve({ accepted: [], rejected: ids });
      },
    });
  });
}

/**
 * 请求单个模板订阅
 * @param {string} type - TMPL_IDS 中的 key
 * @returns {Promise<boolean>} 是否授权成功
 */
export async function subscribeOne(type) {
  const tmplId = TMPL_IDS[type];
  if (!tmplId) return false;
  const { accepted } = await requestSubscribe([tmplId]);
  return accepted.includes(tmplId);
}
```

- [ ] **Step 2: 提交**

```bash
git add education-store-mini/src/utils/subscribe.js
git commit -m "feat: 小程序订阅消息工具 — wx.requestSubscribeMessage 封装"
```

---

## Task 10: 消息页订阅按钮改造

**Files:**
- Modify: `education-store-mini/src/pages/messages/index.vue`
- Modify: `education-store-mini/src/pages/teacher/settings/index.vue`

- [ ] **Step 1: 家长消息页 - 移除占位卡 + 添加订阅按钮**

编辑 `messages/index.vue`：

**模板改动：** 在每个 `setting-item` 的 switch 后面加订阅按钮；删除 `<view class="placeholder-card">` 及其全部内容。

```html
<!-- 每个 setting-item 末尾，switch 后面添加: -->
<text class="subscribe-btn" @click.stop="handleSubscribe(item.key)">订阅</text>
```

**脚本改动：**

```js
import { subscribeOne, TMPL_IDS } from '@/utils/subscribe';

// 模板类型 → 消息 key 映射
const TYPE_TO_TMPL = {
  SIGN_IN: 'SIGN_IN',
  EVALUATION: 'EVALUATION',
  HOMEWORK: 'HOMEWORK',
  EXAM: 'EXAM',
  LEAVE: 'LEAVE',
  REDEMPTION: 'REDEMPTION',
};

async function handleSubscribe(key) {
  const tmplType = TYPE_TO_TMPL[key];
  if (!tmplType) {
    uni.showToast({ title: '模板未配置', icon: 'none' });
    return;
  }
  const ok = await subscribeOne(tmplType);
  if (ok) {
    uni.showToast({ title: '订阅成功', icon: 'success' });
  } else {
    uni.showToast({ title: '已跳过订阅', icon: 'none' });
  }
}
```

**样式改动：**

```scss
.subscribe-btn {
  font-size: 24rpx;
  color: #409eff;
  padding: 8rpx 16rpx;
  border: 1rpx solid #409eff;
  border-radius: 8rpx;
  margin-left: 16rpx;
  flex-shrink: 0;
}
```

- [ ] **Step 2: 教师设置页 - 同样改造**

编辑 `teacher/settings/index.vue`，步骤同上（添加订阅按钮 + 导入 `subscribeOne`）。

教师端通知类型映射：

```js
const TYPE_TO_TMPL = {
  SCHEDULE_REMINDER: '',    // 第二批实现
  CHECKIN_REMINDER: '',     // 第二批实现
  LEAVE_RESULT: 'LEAVE',
  EVALUATION_REMINDER: '',  // 第二批实现
  SYSTEM_NOTICE: '',        // 第二批实现
};
```

对于模板未配置的类型，`subscribeOne` 返回 `false`，显示 "该模板暂未开通"。

- [ ] **Step 3: 验证编译**

```bash
cd education-store-mini && npx uni build -p mp-weixin
```

- [ ] **Step 4: 提交**

```bash
git add education-store-mini/src/pages/messages/index.vue education-store-mini/src/pages/teacher/settings/index.vue
git commit -m "feat: 消息页订阅按钮 — 家长6类+教师5类模板订阅入口"
```

---

## Task 11: 签到页引导订阅

**File:** Modify `education-store-mini/src/pages/teacher/checkin/batch.vue`

- [ ] **Step 1: 签到成功后引导订阅**

在 `handleSubmit` 中的签到成功逻辑后添加：

```js
// 在 uni.showToast({ title: '签到成功', icon: 'success' }); 之后:
import { subscribeOne } from '@/utils/subscribe';

// ... 在签到成功的 showToast 之后:
// 引导教师订阅签到通知
subscribeOne('SIGN_IN');
```

- [ ] **Step 2: 验证编译**

```bash
cd education-store-mini && npx uni build -p mp-weixin
```

- [ ] **Step 3: 提交**

```bash
git add education-store-mini/src/pages/teacher/checkin/batch.vue
git commit -m "feat: 签到成功引导订阅 — batch 签到后弹出订阅授权"
```

---

## Task 12: 最终验证

- [ ] **Step 1: 后端全量编译**

```bash
cd education-store-server && mvn compile -q
```

确认零错误。

- [ ] **Step 2: 小程序编译**

```bash
cd education-store-mini && npx uni build -p mp-weixin
```

确认零警告零错误。

- [ ] **Step 3: 查看提交历史**

```bash
git log --oneline -12
```

预期 12 个提交，覆盖全部改动。
