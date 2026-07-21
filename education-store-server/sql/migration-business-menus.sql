-- =====================================================
-- 新艺智源管理平台 — 业务模块菜单迁移脚本
-- 创建时间: 2026-07-20
-- 说明: 为所有业务模块（N01-N09）配置菜单、权限码和教师角色
--
-- 运行前提：数据库已有系统管理模块菜单（id=1,100-106），本脚本在其上增量添加。
-- 与 edu-init-data.sql 互斥：不要在已执行 init-data 的数据库上再执行本脚本。
--
-- 包含:
--   1. 8 个目录 (M) + 1 个顶级菜单 (C) = 9 条
--   2. 29 个业务菜单 (C)
--   3. 100+ 个按钮权限 (F)
--   4. 教师角色 (sys_role) + 教师菜单分配
--   5. 管理员角色自动获得全部菜单
--
-- ⚠️ 执行前请确认数据库中 sys_menu 最大 ID 不冲突（本脚本从 ID=2 开始分配）
-- =====================================================

SET NAMES utf8mb4;

-- ============================================================
-- 第一部分：业务模块目录 (M, parent_id=0)
-- ============================================================

INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(2, 0, '学员管理', 'M', '/student', NULL, 'Student', 'peoples', NULL, 2, 1, 1),
(3, 0, '教务管理', 'M', '/course', NULL, 'Course', 'education', NULL, 3, 1, 1),
(4, 0, '教务教学', 'M', '/teaching', NULL, 'Teaching', 'guide', NULL, 4, 1, 1),
(5, 0, '财务管理', 'M', '/finance', NULL, 'Finance', 'money', NULL, 5, 1, 1),
(6, 0, '考勤管理', 'M', '/attendance', NULL, 'Attendance', 'time', NULL, 6, 1, 1),
(7, 0, '积分商城', 'M', '/points', NULL, 'Points', 'star', NULL, 7, 1, 1),
(8, 0, '教师端',   'M', '/teacher', NULL, 'Teacher', 'user', NULL, 8, 1, 1);

-- ============================================================
-- 第二部分：顶级菜单 (C, parent_id=0)
-- ============================================================

INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(9, 0, '工作台', 'C', '/dashboard', 'dashboard/index', 'Dashboard', 'dashboard', 'dashboard:dashboard:view', 1, 1, 1);

-- ============================================================
-- 第三部分：业务模块菜单 (C, parent_id=各目录ID)
-- ============================================================

-- ---------- 学员管理 (parent_id=2) ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(200, 2, '意向学员', 'C', 'prospect',  'student/prospect/index',  'StudentProspect',  '#', 'student:prospect:list',  1, 1, 1),
(201, 2, '在读学员', 'C', 'enrolled',  'student/enrolled/index',  'StudentEnrolled',  '#', 'student:enrolled:list',  2, 1, 1),
(202, 2, '结业学员', 'C', 'completed', 'student/completed/index', 'StudentCompleted', '#', 'student:completed:list', 3, 1, 1),
(203, 2, '学员学时', 'C', 'hours',     'student/hours/index',     'StudentHours',     '#', 'student:hours:list',     4, 1, 1),
(204, 2, '学员家长', 'C', 'parents',   'student/parents/index',   'StudentParents',   '#', 'student:parent:list',    5, 1, 1);

-- ---------- 教务管理 (parent_id=3) ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(210, 3, '科目管理', 'C', 'subject',      'course/subject/index',      'CourseSubject',      '#', 'course:subject:list',      1, 1, 1),
(211, 3, '教室管理', 'C', 'classroom',    'course/classroom/index',    'CourseClassroom',    '#', 'course:classroom:list',    2, 1, 1),
(212, 3, '课程管理', 'C', 'course',       'course/course/index',       'CourseManage',       '#', 'course:course:list',       3, 1, 1),
(213, 3, '班级管理', 'C', 'class',        'course/class/index',        'ClassManage',        '#', 'class:class:list',         4, 1, 1),
(214, 3, '排课方案', 'C', 'schedulePlan', 'course/schedulePlan/index', 'SchedulePlan',       '#', 'schedule:plan:list',       5, 1, 1),
(215, 3, '排课日历', 'C', 'schedule',     'course/schedule/index',     'ScheduleCalendar',   '#', 'schedule:calendar:list',   6, 1, 1),
(216, 3, '课件资源', 'C', 'resource',     'course/resource/index',     'CourseResource',     '#', 'course:resource:list',     7, 1, 1);

-- ---------- 教务教学 (parent_id=4) ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(220, 4, '考试管理', 'C', 'exam',              'teaching/exam/index',              'TeachingExam',              '#', 'teaching:exam:list',              1, 1, 1),
(221, 4, '作业管理', 'C', 'homework',          'teaching/homework/index',          'TeachingHomework',          '#', 'teaching:homework:list',          2, 1, 1),
(222, 4, '教评学',   'C', 'evaluation',        'teaching/evaluation/index',        'TeachingEvaluation',        '#', 'teaching:evaluation:list',        3, 1, 1),
(223, 4, '学评教',   'C', 'teacherEvaluation', 'teaching/teacherEvaluation/index', 'TeachingTeacherEvaluation', '#', 'teaching:teacher-evaluation:list', 4, 1, 1),
(224, 4, '教师管理', 'C', 'teacher',           'teaching/teacher/index',           'TeachingTeacher',           '#', 'teaching:teacher:list',           5, 1, 1);

-- ---------- 财务管理 (parent_id=5) ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(230, 5, '学费管理', 'C', 'tuition',       'finance/tuition/index',       'FinanceTuition',       '#', 'tuition:fee:list',       1, 1, 1),
(231, 5, '薪资管理', 'C', 'payroll',       'finance/payroll/index',       'FinancePayroll',       '#', 'payroll:payroll:list',   2, 1, 1),
(232, 5, '教学统计', 'C', 'teachingStats', 'finance/teachingStats/index', 'FinanceTeachingStats', '#', 'payroll:pay-rate:list',  3, 1, 1);

-- ---------- 考勤管理 (parent_id=6) ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(240, 6, '签到记录', 'C', 'signin',       'attendance/signin/index',          'AttendanceSignin',       '#', 'attendance:signin:list',          1, 1, 1),
(241, 6, '学生请假', 'C', 'leave/student','attendance/leave/student/index',   'AttendanceLeaveStudent', '#', 'attendance:leave:student:list',  2, 1, 1),
(242, 6, '教师请假', 'C', 'leave/teacher','attendance/leave/teacher/index',   'AttendanceLeaveTeacher', '#', 'attendance:leave:teacher:list',  3, 1, 1);

-- ---------- 积分商城 (parent_id=7) ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(250, 7, '礼品管理', 'C', 'gift',       'points/gift/index',       'PointsGift',       '#', 'points:gift:list',       1, 1, 1),
(251, 7, '兑换记录', 'C', 'redemption', 'points/redemption/index', 'PointsRedemption', '#', 'points:redemption:list', 2, 1, 1);

-- ---------- 教师端 (parent_id=8) ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(260, 8, '工作台',   'C', 'dashboard',   'teacher/dashboard/index',   'TeacherDashboard',  '#', 'teacher:dashboard:view',   1, 1, 1),
(261, 8, '课后作业', 'C', 'homework',    'teacher/homework/index',    'TeacherHomework',   '#', 'teaching:homework:list',   2, 1, 1),
(262, 8, '课件共享', 'C', 'resource',    'teacher/resource/index',    'TeacherResource',   '#', 'course:resource:list',     3, 1, 1),
(263, 8, '我的课表', 'C', 'my-schedule', 'teacher/my-schedule/index', 'TeacherMySchedule', '#', 'schedule:calendar:list',   4, 1, 1);

-- ============================================================
-- 第四部分：按钮权限 (F, 挂在各 C 菜单下)
-- ============================================================

-- --- 意向学员 (200) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(200, '意向学员查询', 'F', 'student:prospect:query',    1, 1, 1),
(200, '意向学员新增', 'F', 'student:prospect:add',      2, 1, 1),
(200, '意向学员修改', 'F', 'student:prospect:edit',     3, 1, 1),
(200, '意向学员删除', 'F', 'student:prospect:delete',   4, 1, 1),
(200, '意向学员转化', 'F', 'student:prospect:convert',  5, 1, 1),
(200, '跟进记录管理', 'F', 'student:prospect:followup', 6, 1, 1);

-- --- 在读学员 (201) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(201, '在读学员查询', 'F', 'student:enrolled:query',  1, 1, 1),
(201, '在读学员新增', 'F', 'student:enrolled:add',    2, 1, 1),
(201, '在读学员修改', 'F', 'student:enrolled:edit',   3, 1, 1),
(201, '在读学员删除', 'F', 'student:enrolled:delete', 4, 1, 1);

-- --- 结业学员 (202) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(202, '结业学员查询', 'F', 'student:completed:query', 1, 1, 1);

-- --- 学员学时 (203) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(203, '学员学时列表', 'F', 'student:hours:list',  1, 1, 1),
(203, '学员学时查询', 'F', 'student:hours:query', 2, 1, 1),
(203, '学员学时新增', 'F', 'student:hours:add',   3, 1, 1);

-- --- 学员家长 (204) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(204, '学员家长查询', 'F', 'student:parent:query',  1, 1, 1),
(204, '学员家长新增', 'F', 'student:parent:add',    2, 1, 1),
(204, '学员家长修改', 'F', 'student:parent:edit',   3, 1, 1),
(204, '学员家长删除', 'F', 'student:parent:delete', 4, 1, 1);

-- --- 科目管理 (210) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(210, '科目查询', 'F', 'course:subject:query',  1, 1, 1),
(210, '科目新增', 'F', 'course:subject:add',    2, 1, 1),
(210, '科目修改', 'F', 'course:subject:edit',   3, 1, 1),
(210, '科目删除', 'F', 'course:subject:delete', 4, 1, 1);

-- --- 教室管理 (211) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(211, '教室查询', 'F', 'course:classroom:query',  1, 1, 1),
(211, '教室新增', 'F', 'course:classroom:add',    2, 1, 1),
(211, '教室修改', 'F', 'course:classroom:edit',   3, 1, 1),
(211, '教室删除', 'F', 'course:classroom:delete', 4, 1, 1);

-- --- 课程管理 (212) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(212, '课程查询', 'F', 'course:course:query',  1, 1, 1),
(212, '课程新增', 'F', 'course:course:add',    2, 1, 1),
(212, '课程修改', 'F', 'course:course:edit',   3, 1, 1),
(212, '课程删除', 'F', 'course:course:delete', 4, 1, 1);

-- --- 班级管理 (213) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(213, '班级查询',   'F', 'class:class:query',  1, 1, 1),
(213, '班级新增',   'F', 'class:class:add',    2, 1, 1),
(213, '班级修改',   'F', 'class:class:edit',   3, 1, 1),
(213, '班级删除',   'F', 'class:class:delete', 4, 1, 1),
(213, '班级学员管理', 'F', 'class:student:add',    5, 1, 1),
(213, '班级学员列表', 'F', 'class:student:list',   6, 1, 1),
(213, '班级学员修改', 'F', 'class:student:edit',   7, 1, 1),
(213, '班级学员删除', 'F', 'class:student:delete', 8, 1, 1);

-- --- 排课方案 (214) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(214, '排课方案查询', 'F', 'schedule:plan:query',  1, 1, 1),
(214, '排课方案新增', 'F', 'schedule:plan:add',    2, 1, 1),
(214, '排课方案修改', 'F', 'schedule:plan:edit',   3, 1, 1),
(214, '排课方案删除', 'F', 'schedule:plan:delete', 4, 1, 1);

-- --- 排课日历 (215) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(215, '排课日历查询', 'F', 'schedule:calendar:query', 1, 1, 1),
(215, '排课取消',     'F', 'schedule:cancel',        2, 1, 1),
(215, '排课调课',     'F', 'schedule:reschedule',    3, 1, 1),
(215, '排课消课',     'F', 'schedule:consume',       4, 1, 1);

-- --- 课件资源 (216) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(216, '课件资源查询', 'F', 'course:resource:query',  1, 1, 1),
(216, '课件资源新增', 'F', 'course:resource:add',    2, 1, 1),
(216, '课件资源修改', 'F', 'course:resource:edit',   3, 1, 1),
(216, '课件资源删除', 'F', 'course:resource:delete', 4, 1, 1);

-- --- 考试管理 (220) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(220, '考试查询', 'F', 'teaching:exam:query',  1, 1, 1),
(220, '考试新增', 'F', 'teaching:exam:add',    2, 1, 1),
(220, '考试修改', 'F', 'teaching:exam:edit',   3, 1, 1),
(220, '考试删除', 'F', 'teaching:exam:remove', 4, 1, 1);

-- --- 作业管理 (221) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(221, '作业查询', 'F', 'teaching:homework:query',  1, 1, 1),
(221, '作业新增', 'F', 'teaching:homework:add',    2, 1, 1),
(221, '作业修改', 'F', 'teaching:homework:edit',   3, 1, 1),
(221, '作业删除', 'F', 'teaching:homework:remove', 4, 1, 1);

-- --- 教评学 (222) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(222, '教评学查询', 'F', 'teaching:evaluation:query',  1, 1, 1),
(222, '教评学新增', 'F', 'teaching:evaluation:add',    2, 1, 1),
(222, '教评学删除', 'F', 'teaching:evaluation:remove', 3, 1, 1);

-- --- 学评教 (223) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(223, '学评教查询', 'F', 'teaching:teacher-evaluation:query',  1, 1, 1),
(223, '学评教导出', 'F', 'teaching:teacher-evaluation:export', 2, 1, 1);

-- --- 教师管理 (224) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(224, '教师查询', 'F', 'teaching:teacher:query',  1, 1, 1),
(224, '教师新增', 'F', 'teaching:teacher:add',    2, 1, 1),
(224, '教师修改', 'F', 'teaching:teacher:edit',   3, 1, 1),
(224, '教师删除', 'F', 'teaching:teacher:delete', 4, 1, 1);

-- --- 学费管理 (230) ---
-- 学费管理含4个子Tab：报名、缴费、退费、赠课
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(230, '学费查询',   'F', 'tuition:fee:query',     1, 1, 1),
(230, '学费新增',   'F', 'tuition:fee:add',       2, 1, 1),
(230, '学费修改',   'F', 'tuition:fee:edit',      3, 1, 1),
(230, '学费删除',   'F', 'tuition:fee:delete',    4, 1, 1),
(230, '退费列表',   'F', 'tuition:refund:list',   5, 1, 1),
(230, '退费申请',   'F', 'tuition:refund:apply',  6, 1, 1),
(230, '退费审批',   'F', 'tuition:refund:approve',7, 1, 1),
(230, '退费删除',   'F', 'tuition:refund:delete', 8, 1, 1),
(230, '赠课列表',   'F', 'tuition:gift:list',     9, 1, 1),
(230, '赠课新增',   'F', 'tuition:gift:add',      10, 1, 1),
(230, '赠课删除',   'F', 'tuition:gift:delete',   11, 1, 1),
(230, '报名新增',   'F', 'enrollment:enrollment:add',    12, 1, 1),
(230, '报名修改',   'F', 'enrollment:enrollment:edit',   13, 1, 1),
(230, '报名取消',   'F', 'enrollment:enrollment:cancel', 14, 1, 1),
(230, '报名列表',   'F', 'enrollment:enrollment:list',   15, 1, 1),
(230, '报名查询',   'F', 'enrollment:enrollment:query',  16, 1, 1);

-- --- 薪资管理 (231) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(231, '薪资查询',   'F', 'payroll:payroll:query',   1, 1, 1),
(231, '薪资新增',   'F', 'payroll:payroll:add',     2, 1, 1),
(231, '薪资确认',   'F', 'payroll:payroll:confirm', 3, 1, 1),
(231, '薪资发放',   'F', 'payroll:payroll:pay',     4, 1, 1),
(231, '薪资删除',   'F', 'payroll:payroll:delete',  5, 1, 1);

-- --- 积分账户 (挂积分商城目录 7) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(7, '积分账户查询', 'F', 'points:account:query',  1, 1, 1),
(7, '积分账户调整', 'F', 'points:account:adjust', 2, 1, 1);

-- --- 教学统计 (232) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(232, '课时费率查询', 'F', 'payroll:pay-rate:query',  1, 1, 1),
(232, '课时费率新增', 'F', 'payroll:pay-rate:add',    2, 1, 1),
(232, '课时费率修改', 'F', 'payroll:pay-rate:edit',   3, 1, 1),
(232, '课时费率删除', 'F', 'payroll:pay-rate:delete', 4, 1, 1);

-- --- 签到记录 (240) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(240, '签到记录查询', 'F', 'attendance:signin:query', 1, 1, 1),
(240, '签到记录新增', 'F', 'attendance:signin:add',   2, 1, 1);

-- --- 学生请假 (241) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(241, '学生请假查询', 'F', 'attendance:leave:student:query',   1, 1, 1),
(241, '学生请假新增', 'F', 'attendance:leave:student:add',     2, 1, 1),
(241, '学生请假审批', 'F', 'attendance:leave:student:approve', 3, 1, 1);

-- --- 教师请假 (242) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(242, '教师请假查询',   'F', 'attendance:leave:teacher:query',   1, 1, 1),
(242, '教师请假新增',   'F', 'attendance:leave:teacher:add',     2, 1, 1),
(242, '教师请假审批',   'F', 'attendance:leave:teacher:approve', 3, 1, 1),
(242, '教师请假冲突处理', 'F', 'attendance:leave:teacher:resolve', 4, 1, 1);

-- --- 礼品管理 (250) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(250, '礼品查询', 'F', 'points:gift:query',  1, 1, 1),
(250, '礼品新增', 'F', 'points:gift:add',    2, 1, 1),
(250, '礼品修改', 'F', 'points:gift:edit',   3, 1, 1),
(250, '礼品删除', 'F', 'points:gift:delete', 4, 1, 1);

-- --- 兑换记录 (251) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(251, '兑换记录查询', 'F', 'points:redemption:query',  1, 1, 1),
(251, '兑换记录导出', 'F', 'points:redemption:export', 2, 1, 1),
(251, '兑换发放',     'F', 'points:redemption:claim',  3, 1, 1),
(251, '兑换取消',     'F', 'points:redemption:cancel', 4, 1, 1);

-- --- 工作台 (9) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(9, '工作台查看', 'F', 'dashboard:dashboard:view', 1, 1, 1);

-- --- 教师端-工作台 (260) ---
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(260, '教师工作台查看', 'F', 'teacher:dashboard:view', 1, 1, 1);

-- ============================================================
-- 补充：系统模块缺失的权限按钮 (日志管理 106)
-- ============================================================
-- 第五部分：教师角色
-- ============================================================

INSERT IGNORE INTO sys_role (id, name, code, sort, menu_check_strictly, status, remark) VALUES
(3, '教师', 'teacher', 3, 1, 1, '教师角色，拥有教师端所有权限');

-- 教师角色菜单分配（ID精确匹配：教师端目录 + 4个子菜单 + 工作台）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu
WHERE id IN (8, 9, 260, 261, 262, 263)
UNION
SELECT 3, id FROM sys_menu WHERE parent_id IN (8, 9, 260, 261, 262, 263);

-- 还要给教师通用按钮权限（教学模块中教师会用到的）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu
WHERE type = 'F'
  AND permission IN (
    'teaching:homework:query', 'teaching:homework:add', 'teaching:homework:edit', 'teaching:homework:remove',
    'course:resource:query', 'course:resource:add', 'course:resource:edit', 'course:resource:delete',
    'schedule:calendar:query', 'schedule:calendar:list',
    'dashboard:dashboard:view'
)
AND id NOT IN (SELECT menu_id FROM sys_role_menu WHERE role_id = 3);

-- ============================================================
-- 第六部分：管理员角色获得全部新菜单
-- ============================================================

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.id FROM sys_menu m
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.id
);

-- ============================================================
-- 第七部分：家长角色（小程序家长端）
-- ============================================================

INSERT IGNORE INTO sys_role (id, name, code, sort, menu_check_strictly, status, remark) VALUES
(4, '家长', 'parent', 4, 1, 1, '家长角色，拥有小程序家长端权限（仅查看关联学员）');

-- 家长角色菜单分配：仅分配查询类按钮权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 4, id FROM sys_menu
WHERE type = 'F'
  AND permission IN (
    'attendance:signin:list',
    'attendance:leave:student:list', 'attendance:leave:student:add', 'attendance:leave:student:query',
    'teaching:homework:list', 'teaching:homework:query',
    'teaching:evaluation:list',
    'teaching:exam:list',
    'points:account:query', 'points:redemption:list',
    'dashboard:dashboard:view'
  )
  AND id NOT IN (SELECT menu_id FROM sys_role_menu WHERE role_id = 4);

-- ============================================================
-- END
-- ============================================================
