# 小程序教师端设计文档

> **版本**: v1.0 · **日期**: 2026-07-21 · **状态**: 开发中
> **技术栈**: uni-app (Vue 3 Composition API) + uView Plus + Pinia + SCSS

---

## 1. 概述

在已有家长端小程序项目中新增教师端功能。复用全部现有后端 API，JWT 角色判断后路由到教师端 4 Tab 界面。

### 1.1 后端就绪情况

全部复用现有端点，无需新后端开发：

| 功能 | 复用端点 | 来源 |
|------|---------|------|
| 课表查询 | `/api/v1/schedules/list` | N03 |
| 今日课程+签到状态 | `/api/v1/teacher/dashboard/schedule` | N09 |
| 批量签到 | `/api/v1/attendance/signin/batch` | N07 |
| 签到统计 | `/api/v1/attendance/signin/stats/{scheduleId}` | N07 |
| 学生名单 | `/api/v1/teacher/dashboard/schedule/{id}/students` | N09 |
| 教师信息 | `/api/v1/teacher/dashboard/user-info` | N09 |
| 统计卡片 | `/api/v1/teacher/dashboard/statistics` | N09 |
| 待办数据 | `/api/v1/teacher/dashboard/todos` | N09 |
| 作业管理 | `/api/v1/teaching/homework/*` | N05 |
| 课堂评价 | `/api/v1/teaching/evaluations/*` | N05 |
| 教师请假 | `/api/v1/attendance/leaves/teacher/*` | N07 |
| 通知设置 | `/api/v1/user/notification-settings` | 通用 |

---

## 2. 角色路由

小程序启动 → JWT 角色判断：
- `role = PARENT` → 家长端 3 Tab
- `role = TEACHER` → 教师端 4 Tab

通过 pages.json 的 condition 和 App.vue 的 onLaunch 完成角色分发。两种角色共用同一个小程序，通过 `uni.setTabBarItem` / 条件编译实现不同 Tab。

**实现方案**：使用自定义 TabBar（uView Plus 的 `u-tabbar`），根据 JWT role 动态渲染不同 Tab 配置。

---

## 3. 页面结构

```
pages/teacher/
├── schedule/index              ← Tab1 课表（周/月视图 + 日期翻页）
├── checkin/index               ← Tab2 签到（今日课程列表）
├── checkin/batch               ← 批量签到（学生名单+状态切换+提交）
├── teaching/index              ← Tab3 教学（分段控制器容器）
├── teaching/eval-list          ← 评价列表（待评/已评切换）
├── teaching/eval-form          ← 评价表单（课堂总结+学生星级）
├── teaching/homework-list      ← 作业列表（分页）
├── teaching/homework-form      ← 布置作业（班级选择+内容+附件）
├── profile/index               ← Tab4 我的（个人信息+菜单+退出）
├── leave/list                  ← 请假记录
├── leave/submit                ← 发起请假
└── settings/index              ← 通知设置
```

### 3.1 Tab 配置

| Tab | 路径 | 文本 | 说明 |
|-----|------|------|------|
| 课表 | `pages/teacher/schedule/index` | 课表 | 周/月视图 |
| 签到 | `pages/teacher/checkin/index` | 签到 | 今日课程签到 |
| 教学 | `pages/teacher/teaching/index` | 教学 | 评价+作业 |
| 我的 | `pages/teacher/profile/index` | 我的 | 个人中心 |

---

## 4. 页面详细设计

### 4.1 Tab1 课表 — schedule/index

- **默认周视图**，顶部分段控制器切换「周视图」「月视图」
- **周视图**：7列（一~日），日期行高亮今日，左右箭头翻周
  - 每格显示排课卡片：课程名 + 班级 + 时间 + 教室 + 签到状态标签
  - 点击卡片 → 弹窗显示详情（学生名单+签到状态）
- **月视图**：日历网格，有课日期圆点标记
  - 点击日期 → 展开当日课表卡片列表
- **"今天"按钮**：一键回到本周/本月并高亮今天
- **今日课程状态标记**：进行中（绿色左边框）、已结束（半透明）

### 4.2 Tab2 签到 — checkin/index + checkin/batch

- **签到首页**：今日排课列表
  - 每项：时间段 + 课程 + 班级 + 已签/应到人数
  - 状态：待签到 / 签到中 / 已完成
  - 点击条目 → 进入批量签到页
- **批量签到页**：学生名单
  - 默认全部选中为"到校"
  - 点击可切换：到校 → 迟到 → 缺勤
  - 底部"确认签到"按钮 + 二次确认弹窗
  - 提交后显示结果汇总

### 4.3 Tab3 教学 — teaching/index（容器）

顶部两个分段按钮：「评价」「作业」，切换下方内容区。

#### 评价列表 — teaching/eval-list
- 顶部 Tab：「待评价」「已评价」
- 列表项：日期 + 课程 + 班级 + 应到人数
- 点击进入评价表单

#### 评价表单 — teaching/eval-form
- 课堂总评（文本框，必填）
- 学生个人评价列表（仅已签到学生）：
  - 星级评分（1-5星，4维度：专注度/参与度/完成度/综合表现）
  - 文字评语（选填，200字）
- 底部"提交评价"

#### 作业列表 — teaching/homework-list
- 按日期倒序，分页加载
- 每项：标题 + 班级 + 日期 + 发布时间
- 悬浮按钮「+ 布置作业」

#### 布置作业 — teaching/homework-form
- 选择班级（下拉，仅所教班级）
- 文字内容（必填，5000字）
- 图片附件（最多9张）+ 文件附件（最多5个）
- 底部"发布"

### 4.4 Tab4 我的 — profile/index

- 个人头部：头像 + 姓名 + 科目 + 手机号
- 菜单列表：
  - 📅 我的课表（跳转 Tab1）
  - 📝 请假申请（跳转 leave/submit）
  - 📋 请假记录（跳转 leave/list）
  - ⚙️ 通知设置（跳转 settings/index）
  - 退出登录

### 4.5 请假 — leave/list + leave/submit

- **记录列表**：日期 + 类型 + 原因 + 审批状态（待审批/已通过/已驳回）
- **发起请假**：日期选择 + 类型（事假/病假/调休/其他）+ 原因（必填）+ 提交

### 4.6 通知设置 — settings/index

- 6 项开关（与家长端复用同一 API，用户维度存储）

---

## 5. 文件清单

| 类别 | 新增文件数 | 说明 |
|------|:---:|------|
| 页面 | 12 | 4 Tab + 8 子页面 |
| API 层 | 4 | teacher.js, checkin.js, teacher-leave.js, teacher-evaluation.js |
| Store | 1 | teacher.js |
| 组件 | 1 | schedule-card.vue |
| 配置 | 2 | 更新 pages.json + App.vue |
| **合计** | **20** | |

---

## 6. TabBar 实现方案

使用自定义 TabBar（`custom-tab-bar` 组件），在 App.vue onLaunch 中根据 JWT role 设置当前角色，custom-tab-bar 响应式切换家长/教师两种 Tab 配置。

```js
// pages.json 中所有页面注册为普通页面（不在 tabBar.list 中）
// custom-tab-bar 组件读取 authStore.role 决定显示哪些 Tab
```

家长端 3 Tab：首页 / 消息 / 我的
教师端 4 Tab：课表 / 签到 / 教学 / 我的

---

## 7. 自审清单

- [x] 无 TBD / TODO 占位
- [x] 所有页面有对应的后端 API
- [x] 路由与 Tab 配置一致
- [x] 复用家长端公共组件
- [x] 四态覆盖（loading / error / empty / data）
