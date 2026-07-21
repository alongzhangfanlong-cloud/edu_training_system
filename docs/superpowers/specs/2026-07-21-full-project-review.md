# 全项目审查报告

> **审查日期**：2026-07-21 · **审查维度**：5 个（PRD对照/数据模型/后端质量/前端完整性/端到端闭环）
> **方法**：5 个 AI agent 并行审查，交叉验证关键发现
> **项目状态**：v2 分支，工作区干净，编译零警告

---

## 一、综合评分

| 维度 | 评分 | 状态 |
|:---|---:|:---:|
| PRD ↔ 实现覆盖 | 70/100 | ⚠️ 3 个模块有大量 P0 端点缺失 |
| 数据模型对齐 | 88/100 | ⚠️ 3 个 P0 幽灵字段/缺失字段 |
| 后端代码质量 | 82/100 | ⚠️ 2 个 P0 并发/事务问题 |
| 前端功能完整性 | 78/100 | ⚠️ 1 个 P0 空白首页 + 11 页缺状态 |
| 端到端业务闭环 | 90/100 | ⚠️ 1 个 P1 事件断链 |
| **综合** | **81/100** | |

---

## 二、P0 严重问题（共 9 项）

### 🔴 A. 端点缺口 — N06 财务数据（3 子模块，13 端点）

PRD 定位为 P0 模块，以下端点**零实现**：

| 子模块 | 缺失端点 | 数量 |
|--------|---------|:---:|
| **对账 Tab** (N06.1) | `GET reconciliation/summary`, `/by-type`, `/by-operator`, `/gift-stats`, `/export` | 5 |
| **授课统计** (N06.3) | `GET teaching-stats/hour-chart`, `/teacher-monthly`, `/evaluation-ranking`, `/evaluation-detail/{id}` | 4 |
| **消课课酬** (N06.2) | `PUT payroll/batch-confirm`, `/batch-cancel-confirm`, `POST /recalculate`, `GET /export` | 4 |

**影响**：PC 端 `finance/tuition/index.vue` 的「对账」Tab 和 `finance/teachingStats/index.vue` 无后端支撑。

### 🔴 B. 端点缺口 — N07 考勤管理（3 子模块，13 端点）

| 子模块 | 缺失端点 | 数量 |
|--------|---------|:---:|
| **签到管理** | `GET signin/today`, `/today/{scheduleId}/students`, `/student/{id}`, `PUT /{id}`, `GET /export` | 5 |
| **请假管理** | `GET leave/student/pending`, `PUT /student/{id}/cancel`, `PUT /teacher/{id}/cancel`, `GET /teacher/substitute-candidates`, `POST /notification/send` | 5 |
| **统计模块** | `GET statistics/today`, `/weekly`, `/student/{studentId}` | 3 |

### 🔴 C. 端点缺口 — N02 学员管理（多子模块，16 端点）

| 子模块 | 缺失端点 |
|--------|---------|
| **在学学员** | `GET import/template`, `POST import`, `GET export`, `PUT /{id}/complete`, `PUT /{id}/reactivate`, `GET /{id}/hour-logs`, `GET /{id}/enrollments` |
| **结业学员** | `GET completed/list` |
| **家长账号** | `GET parent/list`, `GET parent/{id}`, `PUT parent/{id}/status`, `DELETE parent/{id}/unbind/{studentId}` |
| **课时汇总** | `GET hours-summary/list` |
| **意向学员** | `PUT prospect/{id}/status`, `PUT prospect/batch/status`, `PUT prospect/batch/consultant`, `GET prospect/check-mobile` |

### 🔴 D. 数据模型 — 3 个幽灵字段/缺失字段

| 问题 | 位置 | 详情 |
|------|------|------|
| **幽灵字段 `parentId`** | `EduStudent.java:41` | Entity 有 `parentId` 字段但 DDL 的 `edu_student` 表无 `parent_id` 列。运行时会静默丢失数据或报 Unknown column |
| **缺失字段 `createBy`** | `EduTuitionFee.java` | DDL 有 `create_by` 列但 Entity 只声明了 `updateBy` 未声明 `createBy`，新增记录时创建者丢失 |
| **幽灵字段 `payTime`** | `EduTuitionFee.java:24` | Entity 有 `payTime` 但 DDL 无 `pay_time` 列（DDL 用 `create_time` 代收费时间），不一致 |

### 🔴 E. 后端 — 2 个并发/事务问题

| 问题 | 位置 | 风险 |
|------|------|------|
| **Controller 层 `@Transactional`** | `ParentApiController.java:227` (submitLeave) | 事务应放在 Service 层，Controller 层管理事务导致 AOP 代理失效、嵌套混乱 |
| **签到消课并发竞态** | `EduAttendanceServiceImpl.consumeHours()` | 查询余额→计算→更新之间无锁，两个并发签到可能超额消课 |

### 🔴 F. 前端 — 小程序首页空白

`education-store-mini/src/pages/index/index.vue` — 仅显示 Hello World 模板，家长端首页（时间线聚合/孩子动态）未实现。

---

## 三、P1 高优先级问题（共 10 项）

### 后端

| # | 问题 | 位置 |
|:---:|------|------|
| 1 | **排课生成 TOCTOU 竞态**：`generateSchedules` 检查状态→生成之间有窗口 | `EduSchedulePlanServiceImpl` |
| 2 | **退费审批 TOCTOU 竞态**：检查 PENDING→更新之间可能被双人同时审批 | `EduTuitionRefundServiceImpl` |
| 3 | **课时更新无行锁**：`selectById` → `updateById` 非原子，余额写覆盖风险 | `EduAttendanceServiceImpl` |
| 4 | **regeneratePlan 丢失参数**：重新生成排课时未恢复 scheduleType/teacherId/roomId/startTime/endTime | `EduSchedulePlanServiceImpl` |
| 5 | **5 个模块缺 DTO**：attendance/payroll/points/prospect/teaching 直接用 Entity 接收请求 | 对应 Controller |
| 6 | **教师管理无分页无 DTO**：`EduTeacherController` 全量返回列表，无参数校验 | `edu-module-teaching` |
| 7 | **教师请假审批未发布事件**：`EduLeaveTeacherServiceImpl.approveLeave/rejectLeave` 无 `eventPublisher.publishEvent` | `EduLeaveTeacherServiceImpl:48-88` |

### 前端

| # | 问题 | 位置 |
|:---:|------|------|
| 8 | **11 个 PC 页面缺失空状态/错误状态 UI**（el-empty / el-result + retry） | student/enrolled, student/prospect, system/user, system/role, system/menu, system/dict, system/config, system/notice, teacher/homework 等 |
| 9 | **`window.location.hash` 绕过 Vue Router**：应改为 `router.push()` | `course/course/index.vue:158` |
| 10 | **角色管理页缺竞态保护**：`getList()` 无 querySeq | `system/role/index.vue` |

---

## 四、P2 中优先级问题（共 8 项）

### 后端

| # | 问题 | 位置 |
|:---:|------|------|
| 1 | 全项目 0 处 `@Transactional(readOnly = true)` — 读操作未标记只读 | 全部模块 |
| 2 | `generateSchedules` 循环单条 `insert()` — 大量排课时性能差，应用 `saveBatch()` | `EduSchedulePlanServiceImpl` |
| 3 | 退费编号 `generateRefundNo()` 使用时间戳+2位随机数 — 并发可能重复 | `EduTuitionRefundServiceImpl` |
| 4 | PRD 文档表名/列名与 DDL 不一致（`class_info` vs `edu_class` 等 8 处） | `docs/prd/` |
| 5 | PRD 字段类型与 DDL 不一致：`total_hours` 文档写 INT 实际 DECIMAL(10,1) | `docs/prd/02-学员管理.md` |

### 前端

| # | 问题 | 位置 |
|:---:|------|------|
| 6 | 小程序 4 个页面缺失 error+retry 状态 | messages, settings, eval-form, teacher-profile |
| 7 | 小程序 GET 请求 query 参数放入 `data` 而非 `params` | `api/attendance.js:4`, `api/teacher.js:30` |
| 8 | 小程序 `page-header.vue` 组件存在但未在任何页面使用 | `components/page-header.vue` |

---

## 五、通过项（亮点）

### 架构
- ✅ 16 个 Maven 模块分层清晰，四层结构完整
- ✅ Spring Event 事件驱动解耦：5 个事件 + 统一监听器 + @Async 异步
- ✅ MyBatis-Plus 全 Lambda 查询，SQL 注入零风险
- ✅ 150 处事务统一用 `rollbackFor = Exception.class`

### 后端
- ✅ 所有 Controller 端点均标注 `@PreAuthorize` 权限控制
- ✅ 所有 Controller 标注 `@Tag` / `@Operation` Swagger 文档
- ✅ 排课三维冲突检测（教师/教室/班级）含 excludeId 变体
- ✅ 积分兑换三重并发安全：SELECT FOR UPDATE + 条件扣库存 + 原子扣积分
- ✅ 积分发放 sourceType+sourceId 防重
- ✅ 评价提交积分发放失败不中断主流程（catch 仅 log.error）
- ✅ Dashboard 待办正确合并教师请假+学生请假，批量查询防 N+1

### 前端
- ✅ PC 端 10 个高质量页面完整三态（loading/empty/error）+ 竞态保护
- ✅ Dashboard 使用 `Promise.allSettled` 并发请求
- ✅ 小程序组件化良好（child-switcher/empty-state/loading-skeleton 复用率高）
- ✅ 小程序 16 个页面完整三态
- ✅ 小程序自定义 TabBar 按角色动态切换

### 端到端闭环
- ✅ 流程 1（报名→缴费→签到→通知家长）：**完全闭环**
- ✅ 流程 3（评价→积分→兑换→核销→通知家长）：**完全闭环**
- ✅ 流程 2 学生请假子链路（提交→审批→通知家长）：**完全闭环**
- ✅ parentId 链路修复有效（commit 6ef3295）
- ✅ getRating→getStarRating 全部替换完毕

---

## 六、统计汇总

| 严重度 | 数量 | 类型分布 |
|:---:|:---:|------|
| 🔴 P0 | 9 | 端点缺口(3) + 幽灵字段(3) + 并发/事务(2) + 空白首页(1) |
| 🟠 P1 | 10 | 并发风险(5) + 事件断链(1) + 前端状态缺失(3) + 路由(1) |
| 🟡 P2 | 8 | 性能优化(2) + 文档偏差(2) + 前端完善(3) + 死组件(1) |

---

## 七、修复建议优先级

### 第一批：数据完整性（先修，否则数据会丢）

1. `EduStudent.java` — 确认 parentId 是要加 DDL 列还是删 Entity 字段
2. `EduTuitionFee.java` — 补 createBy 字段，确认 payTime vs create_time 语义
3. `ParentApiController.java:227` — 移除 @Transactional，下沉到 Service

### 第二批：业务闭环

4. `EduLeaveTeacherServiceImpl` — 审批后补充 `eventPublisher.publishEvent`
5. `教育-store-mini/src/pages/index/index.vue` — 实现首页
6. 补 N06/N07/N02 端点（按需，首批补 N07 签到请假小程序强依赖的端点）

### 第三批：健壮性

7. `EduAttendanceServiceImpl.consumeHours()` — 原子更新防超额消课
8. 11 个 PC 页面补 empty/error 状态
9. 各模块补 Create/Update DTO

### 第四批：优化

10. readOnly 事务标注 / saveBatch() / 排课生成乐观锁 / PRD 文档同步

---

> **审查结论**：项目整体架构扎实，核心业务闭环已打通。9 项 P0 问题中，数据模型 3 项属于字段遗漏需紧急修复（否则数据静默丢失）；端点缺口 3 项属于 PRD 中定义但未实现的功能模块；后端并发问题影响生产稳定性。所有问题均有明确修复路径，不存在架构级返工需求。
