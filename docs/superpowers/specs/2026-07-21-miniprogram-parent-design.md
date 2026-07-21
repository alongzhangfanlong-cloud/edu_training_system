# 小程序家长端设计文档

> **版本**: v1.0 · **日期**: 2026-07-21 · **状态**: 设计中
> **技术栈**: uni-app (Vue 3 Composition API) + uView Plus + Pinia

---

## 1. 概述

一个微信小程序，家长微信登录后查看关联孩子的学习数据。一个小程序根据 JWT 角色展示不同功能（家长/教师），本阶段只实现家长端。

### 1.1 后端就绪情况

家长端 16 个 API 端点全部就绪（详见 `docs/api/10-小程序家长端.md`）。

### 1.2 已排除（后续迭代）

- 教师端入口（后端 API 未开发）
- 微信订阅消息推送
- 消息列表 API（目前消息 Tab 只放通知偏好设置）

---

## 2. 页面结构

```
miniprogram/
├── pages/
│   ├── index/                ← 首页 Tab — 时间线
│   ├── messages/             ← 消息 Tab
│   │   └── index             ← 通知偏好设置
│   ├── profile/              ← 我的 Tab
│   │   ├── index             ← 孩子列表 + 退出登录
│   │   └── child-detail      ← 孩子详情（基本信息 + 在读班级）
│   ├── attendance/           ← 考勤
│   │   └── index             ← 分页考勤记录 + 月统计
│   ├── homework/             ← 作业
│   │   └── index             ← 作业列表
│   ├── leave/                ← 请假
│   │   ├── index             ← 请假记录
│   │   └── submit            ← 发起请假表单
│   ├── enrollment/           ← 报名
│   │   └── index             ← 报名记录
│   ├── schedule/             ← 课表
│   │   └── index             ← 日期范围课表
│   ├── exam/                 ← 成绩
│   │   └── index             ← 考试成绩列表
│   ├── evaluation/           ← 评价
│   │   └── index             ← 课堂评价列表
│   ├── points/               ← 积分
│   │   └── index             ← 积分余额 + 日志
├── components/               ← 公共组件
│   ├── child-switcher/       ← 孩子切换下拉
│   ├── empty-state/          ← 空状态提示
│   ├── loading-skeleton/     ← 骨架屏加载
│   └── page-header/          ← 页面标题栏
├── api/                      ← API 层
│   ├── auth.js               ← 微信登录/刷新/退出
│   ├── parent.js             ← 孩子信息
│   ├── timeline.js           ← 首页时间线
│   ├── attendance.js         ← 考勤
│   ├── homework.js           ← 作业
│   ├── leave.js              ← 请假
│   ├── enrollment.js         ← 报名
│   ├── schedule.js           ← 课表
│   ├── exam.js               ← 成绩
│   ├── evaluation.js         ← 评价
│   ├── points.js             ← 积分
│   └── notification.js       ← 通知偏好
├── stores/                   ← Pinia 状态管理
│   ├── auth.js               ← Token/用户信息/登录状态
│   └── children.js           ← 孩子列表/当前选中孩子
├── utils/
│   ├── request.js            ← uni.request 封装（自动带 Token、refresh、错误处理）
│   └── index.js              ← 日期格式化等工具函数
├── App.vue
├── main.js
├── pages.json                ← 路由 + Tab 配置
├── manifest.json             ← uni-app 配置
├── uni.scss                  ← 全局 SCSS 变量
└── package.json
```

### 2.1 底部 Tab 配置

| Tab | 路径 | 图标 | 页面 |
|-----|------|------|------|
| 首页 | `pages/index/index` | home | 时间线动态聚合 |
| 消息 | `pages/messages/index` | bell | 通知偏好设置 |
| 我的 | `pages/profile/index` | user | 孩子列表、设置、退出 |

---

## 3. 数据流设计

### 3.1 认证流程

```
用户打开小程序
  → wx.login() 获取 code
  → POST /api/v1/auth/wechat-login { code }
  → 获取 JWT token → 存入 Pinia auth store + uni.storage
  → 后续所有请求自动带 Authorization: Bearer {token}
  → Token 过期 → 自动 refresh 或跳转登录
```

### 3.2 孩子选择

```
进入小程序
  → GET /api/v1/parent/children
  → 存入 Pinia children store
  → 首页：所有孩子的数据聚合显示
  → 子页面：顶部孩子切换器，默认显示第一个
  → 切换孩子 → 子页面重新加载数据
```

### 3.3 首页时间线

```
GET /api/v1/parent/timeline?days=30&limit=20
  → 按孩子分组渲染
  → 事件类型：签到/评价/作业/成绩
  → 下拉刷新 + 上拉加载更多
```

---

## 4. 页面详细设计

### 4.1 首页（index）

- **时间线列表**：混合事件流，按时间倒序，按孩子分组
- **孩子选择器**：顶部下拉（多孩子时显示）
- **事件卡片**：不同类型不同样式（签到=绿色、评价=橙色、作业=蓝色、成绩=紫色）
- **下拉刷新 + 上拉加载更多**

### 4.2 消息（messages/index）

- **通知偏好开关**：6 项开关（签到/评价/作业/成绩/请假/兑换）
- **获取/更新**：GET + PUT `/api/v1/user/notification-settings`
- **占位提示**："消息推送功能即将上线"

### 4.3 我的（profile/index）

- **孩子列表**：头像、姓名、年级、剩余课时
- **点击进入**：孩子详情页
- **退出登录**：确认弹窗 → 清除 Token → 跳转登录

### 4.4 孩子详情（profile/child-detail）

- **基本信息卡片**：姓名、年级、性别、学校
- **在读班级列表**

### 4.5 考勤（attendance/index）

- **顶部统计卡片**：出勤/迟到/缺勤/请假 四格
- **月份切换**：左右箭头，默认当月
- **考勤列表**：日期、课程、状态标签
- **上拉加载更多**

### 4.6 作业（homework/index）

- **作业列表**：标题、内容（截断）、布置时间
- **上拉加载更多**

### 4.7 请假（leave/index + leave/submit）

- **请假记录列表**：日期、类型、原因、审批状态
- **发起请假按钮** → 跳转 submit 页面
- **请假表单**：日期选择、类型选择（事假/病假/其他）、原因输入
- **提交后自动返回**列表并刷新

### 4.8 报名（enrollment/index）

- **报名列表**：课程名、班级、金额、日期、缴费状态标签

### 4.9 课表（schedule/index）

- **日期范围选择**：开始/结束日期
- **课表列表**：日期分组、课程、时间、教师、教室
- **默认显示本周**

### 4.10 成绩（exam/index）

- **成绩列表**：考试名称、类型、科目、总分、日期

### 4.11 评价（evaluation/index）

- **评价卡片**：星级、评语、时间

### 4.12 积分（points/index）

- **积分余额卡片**：当前余额、累计获得
- **积分日志列表**：变更类型、金额、来源、时间

---

## 5. 组件规范

### 5.1 公共组件

| 组件 | Props | Events | 说明 |
|------|-------|--------|------|
| `child-switcher` | `children: Array`, `currentId: Number` | `change(childId)` | 顶部孩子切换下拉 |
| `empty-state` | `icon: String`, `text: String`, `showRetry: Boolean` | `retry` | 空状态/错误状态 |
| `loading-skeleton` | `rows: Number` | — | 列表骨架屏 |
| `page-header` | `title: String`, `showBack: Boolean` | — | 页面标题栏 |

### 5.2 页面级模式

- **下拉刷新**：`pages.json` 中配置 `enablePullDownRefresh: true`，页面中 `onPullDownRefresh` + `uni.stopPullDownRefresh()`
- **加载更多**：`onReachBottom` 触发分页加载
- **数据加载模式**：`loading` → `error` → `empty` → `data` 四态

---

## 6. API 层规范

每个 API 模块导出具名函数，通过 `utils/request.js` 封装的 `uni.request` 调用：

```js
// utils/request.js
// 自动拼接 BASE_URL、注入 Authorization header
// 响应拦截：统一处理 Token 过期、网络错误
// 返回格式：{ success, data, msg }

// api/attendance.js 示例
import request from '@/utils/request';

export function getAttendance(studentId, params) {
  return request({
    url: `/api/v1/parent/children/${studentId}/attendance`,
    method: 'GET',
    data: params,
  });
}
```

---

## 7. 状态管理

### auth store
```js
{
  token: '',          // JWT token
  userInfo: null,     // { userId, userName, roles }
  isLogin: false,     // 登录状态
  login(code): Promise  // 微信登录
  logout(): void       // 退出
  checkToken(): void   // Token 有效性检查
}
```

### children store
```js
{
  children: [],       // 孩子列表
  currentChild: null, // 当前选中的孩子
  loading: false,
  fetchChildren(): Promise
  setCurrentChild(child): void
}
```

---

## 8. 文件清单

| 类别 | 文件数 | 文件列表 |
|------|:---:|------|
| 项目配置 | 5 | `package.json`, `pages.json`, `manifest.json`, `uni.scss`, `App.vue` |
| 入口 | 1 | `main.js` |
| API 层 | 12 | `auth`, `parent`, `timeline`, `attendance`, `homework`, `leave`, `enrollment`, `schedule`, `exam`, `evaluation`, `points`, `notification` |
| 状态管理 | 2 | `auth.js`, `children.js` |
| 工具函数 | 2 | `request.js`, `index.js` |
| 公共组件 | 4 | `child-switcher`, `empty-state`, `loading-skeleton`, `page-header` |
| 页面 | 13 | 首页1、消息1、我的2、考勤1、作业1、请假2、报名1、课表1、成绩1、评价1、积分1 |
| **合计** | **39** | |

---

## 9. 自审清单

- [x] 无 TBD / TODO 占位
- [x] 页面路由与 Tab 配置一致
- [x] 所有页面有对应的后端 API（全部就绪）
- [x] 组件 Props/Events 明确
- [x] 数据流完整（登录 → 孩子选择 → 各页面）
- [x] 四态覆盖（loading / error / empty / data）
