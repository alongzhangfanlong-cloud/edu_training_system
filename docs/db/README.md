# 数据库初始化与验证说明

> 导出日期：2026-07-26
> 数据库：MySQL 8.0+
> 字符集：utf8mb4

## 文件说明

| 文件名 | 说明 | 大小 |
|--------|------|------|
| `01_edu_system_full.sql` | 完整数据库备份（含表结构+全部测试数据），推荐直接使用 | ~350KB |
| `02_edu_system_schema.sql` | 仅表结构（不含数据），用于从零开始 | ~81KB |
| `03_verify_data.sql` | 数据完整性快速验证SQL脚本 | - |

## 快速开始（推荐）

直接导入完整备份即可获得包含测试数据的数据库：

```bash
# 1. 创建数据库（如不存在）
mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS edu_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 2. 导入完整数据
mysql -uroot -proot edu_system < 01_edu_system_full.sql
```

## 从零开始（仅表结构）

如果只想建表，自己造数据：

```bash
# 1. 创建数据库
mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS edu_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 2. 导入表结构
mysql -uroot -proot edu_system < 02_edu_system_schema.sql
```

> 注意：仅导入schema后，需手动添加系统管理员账号、角色、菜单等基础数据才能正常登录。

## 验证数据完整性

导入完成后，执行验证脚本确认数据完整：

```bash
mysql -uroot -proot edu_system < 03_verify_data.sql
```

预期输出应显示各模块数据条数，关键验证点：
- 教室设施为空应为 0
- 收费记录 pay_time 为空应为 0（已缴费记录）
- 已确认薪资 confirmed_at 为空应为 0
- 可登录账号应为 3 个（admin/teacher01/teacher02）

## 登录账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 管理端全部功能 |
| 教师 | teacher01 | 123456 | 张老师（美术），教师工作台 |
| 教师 | teacher02 | 123456 | 李老师（数学/英语），教师工作台 |

## 数据库配置

后端默认配置（application-dev.yml）：
- Host: `localhost`
- Port: `3306`
- Database: `edu_system`
- Username: `root`
- Password: `root`

可通过环境变量覆盖：`DB_HOST`、`DB_PORT`、`DB_USERNAME`、`DB_PASSWORD`

## 各模块数据量参考（导入full.sql后）

| 模块 | 表名 | 预计条数 |
|------|------|----------|
| **系统管理** | | |
| 系统用户 | sys_user | 3 |
| 系统角色 | sys_role | 4 |
| 系统菜单 | sys_menu | 186 |
| 系统字典类型 | sys_dict_type | 11 |
| **课程体系** | | |
| 科目 | edu_subject | 9 |
| 课程 | edu_course | 14 |
| 教室 | edu_classroom | 4 |
| 课件资源 | edu_resource | 10 |
| **人事管理** | | |
| 教师档案 | edu_teacher | 2 |
| 课酬标准 | edu_teacher_pay_rate | 4 |
| **学员管理** | | |
| 学员 | edu_student | 9 |
| 学员家长 | edu_student_parent | 7 |
| **班级管理** | | |
| 班级 | edu_class | 3 |
| 班级学员 | edu_class_student | 13 |
| **排课管理** | | |
| 排课 | edu_schedule | 22 |
| 排课计划 | edu_schedule_plan | 3 |
| **考试管理** | | |
| 考试 | edu_exam_score | 3 |
| **评价管理** | | |
| 教学评价 | edu_evaluation | 4 |
| 学评教 | edu_teacher_evaluation | 6 |
| **作业管理** | | |
| 作业 | edu_homework | 4 |
| 作业提交 | edu_homework_submission | 2 |
| **考勤管理** | | |
| 签到记录 | edu_attendance | 2 |
| 学生请假 | edu_leave_student | 3 |
| 教师请假 | edu_leave_teacher | 2 |
| **招生收费** | | |
| 报名记录 | edu_enrollment | 5 |
| 收费记录 | edu_tuition_fee | 5 |
| 退费记录 | edu_tuition_refund | 1 |
| 赠送课时 | edu_tuition_gift_hours | 1 |
| 潜客 | edu_prospect | 7 |
| **财务积分** | | |
| 薪资记录 | edu_teacher_payroll | 2 |
| 积分礼品 | edu_points_gift | 4 |

## 数据库表清单（共42张表）

**系统模块 (sys_)**：sys_config, sys_dict_data, sys_dict_type, sys_login_log, sys_menu, sys_notice, sys_oper_log, sys_role, sys_role_menu, sys_user, sys_user_role, user_notification_setting

**业务模块 (edu_)**：edu_attendance, edu_class, edu_class_schedule_rule, edu_class_student, edu_classroom, edu_course, edu_enrollment, edu_evaluation, edu_evaluation_student, edu_exam_score, edu_exam_score_student, edu_homework, edu_homework_attachment, edu_homework_submission, edu_leave_conflict, edu_leave_student, edu_leave_teacher, edu_parent_view_log, edu_points_account, edu_points_gift, edu_points_log, edu_points_redemption, edu_prospect, edu_prospect_followup, edu_resource, edu_schedule, edu_schedule_plan, edu_student, edu_student_hours_log, edu_student_parent, edu_subject, edu_teacher, edu_teacher_evaluation, edu_teacher_pay_rate, edu_teacher_payroll, edu_tuition_fee, edu_tuition_gift_hours, edu_tuition_refund
