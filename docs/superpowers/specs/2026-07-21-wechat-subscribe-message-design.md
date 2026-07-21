# 微信订阅消息接入 · 设计文档

> 日期: 2026-07-21 · 状态: 已确认

## 1. 概述

为小程序接入微信订阅消息能力，在关键业务事件发生时向家长/教师推送模板消息通知，打通"业务动作→用户感知"的闭环。

---

## 2. 架构

```
┌─ 业务层 (各模块 Service) ─────────────────────┐
│  SignInService      → publisher.publishEvent() │
│  HomeworkService    → publisher.publishEvent() │
│  LeaveService       → publisher.publishEvent() │
│  EvaluationService  → publisher.publishEvent() │
│  PointsService      → publisher.publishEvent() │
└──────────────────────┬─────────────────────────┘
                       ▼
┌─ 通知层 (edu-module-system) ──────────────────┐
│  NotificationEventListener                     │
│    ├ 查 UserNotificationSetting (是否开启)     │
│    ├ 查 sys_user.wechat_openid (接收者openid)  │
│    ├ 限频检查 (Guava Cache, 60s TTL)           │
│    └ 调 WxMaService.getMsgService()            │
│       .sendSubscribeMsg(...)                   │
└──────────────────────┬─────────────────────────┘
                       ▼
┌─ 微信服务 (binarywang SDK) ───────────────────┐
│  WxMaService → 调用微信官方订阅消息 API        │
└────────────────────────────────────────────────┘
```

| 决策项 | 选择 | 原因 |
|--------|------|------|
| 事件机制 | Spring `ApplicationEventPublisher` | 零依赖，与 Spring 事务兼容 |
| 事件类位置 | `edu-common/com...common.event` | 多模块可用，避免循环依赖 |
| 监听器位置 | `edu-module-system` | 需要 `UserNotificationSettingMapper` + `WxMaService` |
| 限频 | Guava Cache，TTL 60s，key=`openid:notifyType` | 避免同一事件重复推送 |
| 失败处理 | log.warn，不抛异常 | 消息推送失败不应阻塞主业务 |

---

## 3. 事件定义

### 3.1 事件类清单（5 个，`edu-common` 模块）

```java
// 签到事件
public class SignInEvent extends ApplicationEvent {
    Long studentId;
    Long courseId;
    String courseName;
    LocalDateTime time;
}

// 作业发布事件
public class HomeworkPublishedEvent extends ApplicationEvent {
    Long homeworkId;
    Long classId;
    String className;
    String title;
    LocalDate deadline;
}

// 请假审批事件
public class LeaveApprovedEvent extends ApplicationEvent {
    Long leaveId;
    String role;          // STUDENT / TEACHER
    Long userId;          // 请假人 userId
    String name;          // 请假人姓名
    String result;        // 通过 / 驳回
    String reason;
}

// 评价提交事件
public class EvaluationSubmittedEvent extends ApplicationEvent {
    Long evaluationId;
    Long studentId;
    String courseName;
    String content;       // 评价摘要（≤20字）
    String score;
}

// 积分兑换事件
public class PointsRedemptionEvent extends ApplicationEvent {
    Long pointsRecordId;
    Long studentId;
    String itemName;
    String status;        // 已发放 / 已驳回
    Integer amount;
}
```

### 3.2 与通知类型的映射

| 事件 | 通知类型 Key | 接收者 |
|------|-------------|--------|
| `SignInEvent` | `SIGN_IN` | 学生家长 |
| `HomeworkPublishedEvent` | `HOMEWORK` | 班级下所有学生家长 |
| `LeaveApprovedEvent` | `LEAVE` | 请假人（家长或教师） |
| `EvaluationSubmittedEvent` | `EVALUATION` | 被评价学生家长 |
| `PointsRedemptionEvent` | `REDEMPTION` | 学生家长 |

---

## 4. 消息模板

微信模板字段使用标准命名，具体 templateId 需在微信公众平台申请后填入 `application.yml`。

### 4.1 模板参数映射

```
签到通知 (SIGN_IN)
  thing1 = 学生姓名
  time2 = 签到时间
  thing3 = 课程名称

作业通知 (HOMEWORK)
  thing1 = 课程名称
  thing2 = 作业标题
  date3 = 截止日期

请假通知 (LEAVE)
  thing1 = 请假人姓名
  phrase2 = 审批结果
  thing3 = 请假原因

评价通知 (EVALUATION)
  thing1 = 课程名称
  thing2 = 评价摘要
  thing3 = 评分

兑换通知 (REDEMPTION)
  thing1 = 商品名称
  phrase2 = 兑换状态
  amount3 = 积分数
```

### 4.2 模板 ID 配置（`application.yml`）

```yaml
wechat:
  mini-app:
    app-id: ${WECHAT_APP_ID}
    app-secret: ${WECHAT_APP_SECRET}
    subscribe-templates:
      SIGN_IN:       "xxxxxxxx"   # 签到通知模板ID
      HOMEWORK:      "xxxxxxxx"   # 作业通知模板ID
      LEAVE:         "xxxxxxxx"   # 请假通知模板ID
      EVALUATION:    "xxxxxxxx"   # 评价通知模板ID
      REDEMPTION:    "xxxxxxxx"   # 兑换通知模板ID
```

---

## 5. 监听器实现

### 5.1 NotificationEventListener

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final WxMaService wxMaService;
    private final UserNotificationSettingMapper settingMapper;
    private final SysUserMapper userMapper;

    // 限频缓存: key = "openid:notifyType", TTL = 60s
    private final Cache<String, Boolean> rateLimitCache =
        CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();

    @EventListener
    @Async  // 异步发送，不阻塞主业务
    public void onSignIn(SignInEvent event) {
        // 1. 查家长 userId
        // 2. 查偏好 + openid
        // 3. 限频检查
        // 4. 构造 WxMaSubscribeMessage 并发送
        // 5. log 结果
    }

    // 其余 4 个 handler 同理
}
```

### 5.2 关键细节

- `@Async` 异步发送，需在主类上加 `@EnableAsync`
- 模板 ID 通过 `@ConfigurationProperties` 注入 `SubscribeTemplateConfig`
- 发送失败只记录 warn 日志，不重试（微信订阅消息不支持服务端重试）
- 限频用 Guava Cache（内存级），服务重启后重置，可接受

---

## 6. 小程序前端改动

### 6.1 涉及文件

| 文件 | 改动 | 说明 |
|------|------|------|
| `utils/subscribe.js` | 新建 | 封装 `wx.requestSubscribeMessage` |
| `pages/messages/index.vue` | 改 | 移除占位卡，6 个类型各加订阅按钮 |
| `pages/teacher/settings/index.vue` | 改 | 5 个类型各加订阅按钮 |
| `pages/teacher/checkin/batch.vue` | 改 | 批量签到完成引导订阅签到通知 |

### 6.2 订阅按钮 UI

```
通知偏好设置
┌────────────────────────────────────────┐
│ 签到通知 · 孩子上课签到后推送           │
│        [switch ⬤]           [订阅 📩]  │
├────────────────────────────────────────┤
│ 作业通知 · 新作业发布时推送             │
│        [switch ○]           [订阅 📩]  │
└────────────────────────────────────────┘
```

### 6.3 subscribe.js 工具函数

```js
export function requestSubscribe(tmplIds) {
  return new Promise((resolve) => {
    wx.requestSubscribeMessage({ tmplIds, success: resolve, fail: resolve });
  });
}
```

### 6.4 模板 ID 在小程序中的维护

在 `utils/subscribe.js` 中维护常量映射：

```js
export const TMPL_IDS = {
  SIGN_IN:     'xxxxxxxx',   // 注册时替换为真实模板ID
  HOMEWORK:    'xxxxxxxx',
  LEAVE:       'xxxxxxxx',
  EVALUATION:  'xxxxxxxx',
  REDEMPTION:  'xxxxxxxx',
};
```

---

## 7. 业务模块改动点

各模块的 Service 在关键动作完成后发布事件：

### 7.1 签到通知 (edu-module-attendance)
**触发**: 教师提交签到记录
**位置**: `CheckinServiceImpl.submitCheckin()` 方法末尾
**事件**: `SignInEvent`

### 7.2 作业通知 (edu-module-teaching)
**触发**: 教师发布/布置作业
**位置**: `HomeworkController.create()` 或对应 Service
**事件**: `HomeworkPublishedEvent`

### 7.3 请假通知 (edu-module-teaching)
**触发**: 管理员审批请假（家长请假 + 教师请假）
**位置**: `LeaveController.approve()` 或对应 Service
**事件**: `LeaveApprovedEvent`

### 7.4 评价通知 (edu-module-teaching)
**触发**: 教师提交课堂评价
**位置**: `EvaluationController.submit()` 或对应 Service
**事件**: `EvaluationSubmittedEvent`

### 7.5 兑换通知 (edu-module-points)
**触发**: 管理员处理积分兑换
**位置**: `PointsController.processRedemption()` 或对应 Service
**事件**: `PointsRedemptionEvent`

---

## 8. 前置依赖

| 依赖 | 状态 | 说明 |
|------|:--:|------|
| binarywang SDK | ✅ 已有 | `WxMaService` Bean 已配置 |
| UserNotificationSetting 表 | ✅ 已有 | 通知偏好 CRUD 完备 |
| sys_user.wechat_openid | ⚠️ 待确认 | 需要确认微信登录时已写入 openid |
| 微信模板 ID | ⚠️ 待申请 | 需在 mp.weixin.qq.com 申请消息模板 |
| Guava | ✅ Spring Boot 自带 | — |
| Spring @Async | ⚠️ 待配置 | 需在主类加 `@EnableAsync` |

---

## 9. 提交批次

全部改动量预估：~15 文件

| 批次 | 内容 | 文件数 |
|------|------|:--:|
| 1 | 后端事件类（5 个）+ 监听器 + 配置 | 8 |
| 2 | 业务模块埋点（5 个 Service 各加 1 行 publishEvent） | 5 |
| 3 | 小程序前端（subscribe.js + 3 页面改造） | 4 |

---

## 10. 风险与边界

- **模板 ID 未申请**: 用占位符 `"xxxxxxxx"` 替代，等审核通过后替换
- **openid 缺失**: 微信登录流程中确认已保存 openid，若缺失则跳过发送（log warn）
- **用户未订阅**: `sendSubscribeMsg` 返回错误码 43101 时静默忽略
- **教师端 5 种通知类型（第二批）**: 本期不实现，架构已预留扩展空间
