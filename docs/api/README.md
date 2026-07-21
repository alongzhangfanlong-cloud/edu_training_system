# API 接口文档 · 总览

> 新艺智源管理平台 · 后端 REST API · v2
>
> 对应 PRD 文档位于 `docs/prd/`，数据模型位于 `docs/model/`

---

## 模块路由索引

| 编号 | 模块 | PRD | API 文档 | Controller 数 | 端点数 |
|:---:|------|:---:|:---:|:---:|:---:|
| N01 | 工作台 | [01-工作台](../prd/01-工作台.md) | [01-工作台](./01-工作台.md) | 1 | 8 |
| N02 | 学员管理 | [02-学员管理](../prd/02-学员管理.md) | [02-学员管理](./02-学员管理.md) | 5 | 24 |
| N03 | 教务系统 | [03-教务系统](../prd/03-教务系统.md) | [03-教务系统](./03-教务系统.md) | 9 | 44 |
| N04 | 积分商城 | [04-积分商城](../prd/04-积分商城.md) | [04-积分商城](./04-积分商城.md) | 3 | 14 |
| N05 | 家校沟通 | [05-家校沟通](../prd/05-家校沟通.md) | [05-家校沟通](./05-家校沟通.md) | 6 | 30 |
| N06 | 财务数据 | [06-财务数据](../prd/06-财务数据.md) | [06-财务数据](./06-财务数据.md) | 6 | 32 |
| N07 | 考勤管理 | [07-考勤管理](../prd/07-考勤管理.md) | [07-考勤管理](./07-考勤管理.md) | 4 | 15 |
| N08 | 系统管理 | [08-系统管理](../prd/08-系统管理.md) | [08-系统管理](./08-系统管理.md) | 11 | 64 |
| N09 | 教师端 | [09-教师端](../prd/09-教师端.md) | [09-教师端](./09-教师端.md) | 1 | 5 |
| N10 | 小程序家长端 | [10-小程序功能设计](../prd/10-小程序功能设计.md) | [10-小程序家长端](./10-小程序家长端.md) | 3 | 16 |
| — | 文件上传 | — | [通用-文件上传](./通用-文件上传.md) | 1 | 2 |
| **合计** | | | | **49** | **254** |

---

## 统一约定

所有接口遵循以下约定：

### 请求格式

- **Base URL**：`/api/v1`
- **Content-Type**：`application/json`（文件上传使用 `multipart/form-data`）
- **认证**：Bearer JWT（`Authorization: Bearer <token>`）

### 统一响应格式 `R<T>`

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | int | 200=成功，其他=错误码 |
| `msg` | string | 提示信息 |
| `data` | object/array/null | 响应数据 |

### 分页响应 `PageResult<T>`

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1
  }
}
```

### 权限码命名规范

```
{模块}:{实体}:{操作}
```

示例：`system:user:list`、`teaching:teacher:add`

### HTTP 方法语义

| 方法 | 语义 |
|:---:|------|
| `GET` | 查询（列表/详情/选项） |
| `POST` | 新增 |
| `PUT` | 编辑 |
| `DELETE` | 删除（逻辑删除 `deleted=1`） |

---

## 已实现 Controller 完整清单

### N01 · 工作台（edu-module-dashboard）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| DashboardController | `/api/v1/dashboard` | `dashboard:*` |

### N02 · 学员管理（edu-module-student + edu-module-prospect）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| EduStudentController | `/api/v1/student` | `student:*` |
| EduStudentParentController | `/api/v1/student/parent` | `student:parent:*` |
| EduStudentHoursLogController | `/api/v1/student/hours-log` | — |
| EduProspectController | `/api/v1/prospect` | `prospect:*` |
| EduProspectFollowupController | `/api/v1/prospect/followup` | — |

### N03 · 教务系统（edu-module-course + edu-module-class + edu-module-schedule + edu-module-course）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| EduSubjectController | `/api/v1/subject` | `subject:*` |
| EduCourseController | `/api/v1/course` | `course:*` |
| EduClassroomController | `/api/v1/classroom` | `classroom:*` |
| EduResourceController | `/api/v1/resource` | `resource:*` |
| EduClassController | `/api/v1/class` | `class:*` |
| EduClassStudentController | `/api/v1/class/student` | `class:student:*` |
| EduClassScheduleRuleController | `/api/v1/class/schedule-rule` | — |
| EduScheduleController | `/api/v1/schedule` | `schedule:*` |
| EduSchedulePlanController | `/api/v1/schedule/plan` | — |

### N04 · 积分商城（edu-module-points）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| PointsGiftController | `/api/v1/points/gift` | `points:gift:*` |
| PointsRedemptionController | `/api/v1/points/redemption` | `points:redemption:*` |
| PointsAccountController | `/api/v1/points/account` | `points:account:*` |

### N05 · 家校沟通（edu-module-teaching）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| HomeworkController | `/api/v1/teaching/homework` | `teaching:homework:*` |
| EduHomeworkSubmissionController | `/api/v1/teaching/homework-submission` | `teaching:homework:*` |
| EvaluationController | `/api/v1/teaching/evaluation` | `teaching:evaluation:*` |
| TeacherEvaluationController | `/api/v1/teaching/teacher-evaluation` | — |
| ExamScoreController | `/api/v1/teaching/exam-score` | `teaching:exam:*` |
| EduTeacherController | `/api/v1/teaching/teacher` | `teaching:teacher:*` |

### N06 · 财务数据（edu-module-enrollment + edu-module-tuition + edu-module-payroll）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| EduEnrollmentController | `/api/v1/enrollment` | `enrollment:*` |
| EduTuitionFeeController | `/api/v1/tuition/fee` | `tuition:fee:*` |
| EduTuitionRefundController | `/api/v1/tuition/refund` | `tuition:refund:*` |
| EduTuitionGiftHoursController | `/api/v1/tuition/gift-hours` | — |
| EduTeacherPayrollController | `/api/v1/payroll` | `payroll:*` |
| EduTeacherPayRateController | `/api/v1/payroll/rate` | `payroll:rate:*` |

### N07 · 考勤管理（edu-module-attendance）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| EduAttendanceController | `/api/v1/attendance` | `attendance:*` |
| EduLeaveStudentController | `/api/v1/leave/student` | `leave:student:*` |
| EduLeaveTeacherController | `/api/v1/leave/teacher` | `leave:teacher:*` |
| EduLeaveConflictController | `/api/v1/leave/conflict` | — |

### N08 · 系统管理（edu-module-system）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| AuthController | `/api/v1/auth` | — |
| SysUserController | `/api/v1/system/user` | `system:user:*` |
| SysRoleController | `/api/v1/system/role` | `system:role:*` |
| SysMenuController | `/api/v1/system/menu` | `system:menu:*` |
| SysConfigController | `/api/v1/system/config` | `system:config:*` |
| SysDictTypeController | `/api/v1/system/dict/type` | `system:dict:*` |
| SysDictDataController | `/api/v1/system/dict/data` | `system:dict:*` |
| SysOperLogController | `/api/v1/system/log/operlog` | `system:log:*` |
| SysLogininforController | `/api/v1/system/log/logininfor` | `system:log:*` |
| SysNoticeController | `/api/v1/system/notice` | `system:notice:*` |
| FileController | `/api/v1/files` | — |

### N09 · 教师端（edu-module-teacher-dashboard）

| Controller | Base Path | 权限前缀 |
|------------|-----------|----------|
| TeacherDashboardController | `/api/v1/teacher/dashboard` | — |

---

## 文档维护

- API 有变更时，同步更新对应的 `docs/api/*.md` 或 `docs/prd/*.md`
- 权限码变更需同时更新 PRD 中对应的权限矩阵
- 新增 Controller 时在本文件的模块清单中追加
