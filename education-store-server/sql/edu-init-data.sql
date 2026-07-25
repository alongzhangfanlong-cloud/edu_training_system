-- =====================================================
-- 新艺智源管理平台 — 系统管理模块 + 业务模块 初始化数据
-- 已去除 多公司 / 部门 / 岗位 / 数据权限 相关种子
-- 默认管理员: admin / admin123
--
-- 运行前提：仅用于首次全新安装。执行前 sys_menu/sys_role 等表应为空。
-- 与 migration-business-menus.sql 互斥：不要在同一个数据库上先后执行。
-- =====================================================

SET NAMES utf8mb4;

-- ---------- 用户 ----------
-- 密码 admin123 的 BCrypt 哈希
INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark)
VALUES (1, 'admin', '$2b$10$5sQPytQwvHx9yqB1Ye/gwOV33rrcfLpo5HrXPGN4rPDTNZYydhPsG', '超级管理员', '管理员', 'admin@edu.com', '13800000000', 1, 1, '系统内置超级管理员');

-- ---------- 角色 ----------
INSERT INTO sys_role (id, name, code, sort, menu_check_strictly, status, remark) VALUES
(1, '超级管理员', 'admin',   1, 1, 1, '拥有全部权限'),
(2, '普通用户',   'user',    2, 1, 1, '基础权限'),
(3, '教师',      'teacher', 3, 1, 1, '教师角色，拥有教师端权限'),
(4, '家长',      'parent',  4, 1, 1, '家长角色，仅查看关联学员信息');

-- ---------- 用户-角色 ----------
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- ---------- 菜单 ----------
-- 目录
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(1, 0, '系统管理', 'M', '/system', NULL, 'System', 'system', NULL, 1, 1, 1);

-- 菜单（C）
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(100, 1, '用户管理', 'C', 'user',   'system/user/index',   'SysUser',   'user',   'system:user:list',   1, 1, 1),
(101, 1, '角色管理', 'C', 'role',   'system/role/index',   'SysRole',   'peoples', 'system:role:list',   2, 1, 1),
(102, 1, '菜单管理', 'C', 'menu',   'system/menu/index',   'SysMenu',   'tree-table', 'system:menu:list', 3, 1, 1),
(103, 1, '字典管理', 'C', 'dict',   'system/dict/index',   'SysDict',   'dict',   'system:dict:list',   4, 1, 1),
(104, 1, '参数设置', 'C', 'config', 'system/config/index', 'SysConfig', 'edit',   'system:config:list', 5, 1, 1),
(105, 1, '通知公告', 'C', 'notice', 'system/notice/index', 'SysNotice', 'message', 'system:notice:list', 6, 1, 1),
(106, 1, '日志管理', 'C', 'log',    'system/log/index',    'SysLog',    'log',    'system:log:list',    7, 1, 1);

-- 按钮（F）
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
-- 用户
(100, '用户查询', 'F', 'system:user:query',    1, 1, 1),
(100, '用户新增', 'F', 'system:user:add',      2, 1, 1),
(100, '用户修改', 'F', 'system:user:edit',     3, 1, 1),
(100, '用户删除', 'F', 'system:user:delete',   4, 1, 1),
(100, '用户导出', 'F', 'system:user:export',   5, 1, 1),
(100, '用户导入', 'F', 'system:user:import',   6, 1, 1),
(100, '重置密码', 'F', 'system:user:resetPwd', 7, 1, 1),
-- 角色
(101, '角色查询', 'F', 'system:role:query',  1, 1, 1),
(101, '角色新增', 'F', 'system:role:add',    2, 1, 1),
(101, '角色修改', 'F', 'system:role:edit',   3, 1, 1),
(101, '角色删除', 'F', 'system:role:delete', 4, 1, 1),
-- 菜单
(102, '菜单查询', 'F', 'system:menu:query',  1, 1, 1),
(102, '菜单新增', 'F', 'system:menu:add',    2, 1, 1),
(102, '菜单修改', 'F', 'system:menu:edit',   3, 1, 1),
(102, '菜单删除', 'F', 'system:menu:delete', 4, 1, 1),
-- 字典
(103, '字典查询', 'F', 'system:dict:query',  1, 1, 1),
(103, '字典新增', 'F', 'system:dict:add',    2, 1, 1),
(103, '字典修改', 'F', 'system:dict:edit',   3, 1, 1),
(103, '字典删除', 'F', 'system:dict:delete', 4, 1, 1),
-- 参数
(104, '参数查询', 'F', 'system:config:query',  1, 1, 1),
(104, '参数新增', 'F', 'system:config:add',    2, 1, 1),
(104, '参数修改', 'F', 'system:config:edit',   3, 1, 1),
(104, '参数删除', 'F', 'system:config:delete', 4, 1, 1),
-- 通知
(105, '公告查询', 'F', 'system:notice:query',  1, 1, 1),
(105, '公告新增', 'F', 'system:notice:add',    2, 1, 1),
(105, '公告修改', 'F', 'system:notice:edit',   3, 1, 1),
(105, '公告删除', 'F', 'system:notice:delete', 4, 1, 1),
-- 日志
(106, '日志查询', 'F', 'system:log:query',  1, 1, 1),
(106, '日志删除', 'F', 'system:log:delete', 2, 1, 1);

-- ---------- 业务模块目录 ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(2, 0, '学员管理', 'M', '/student', NULL, 'Student', 'peoples', NULL, 2, 1, 1),
(3, 0, '教务管理', 'M', '/course', NULL, 'Course', 'education', NULL, 3, 1, 1),
(4, 0, '教务教学', 'M', '/teaching', NULL, 'Teaching', 'guide', NULL, 4, 1, 1),
(5, 0, '财务管理', 'M', '/finance', NULL, 'Finance', 'money', NULL, 5, 1, 1),
(6, 0, '考勤管理', 'M', '/attendance', NULL, 'Attendance', 'time', NULL, 6, 1, 1),
(7, 0, '积分商城', 'M', '/points', NULL, 'Points', 'star', NULL, 7, 1, 1),
(8, 0, '教师端',   'M', '/teacher', NULL, 'Teacher', 'user', NULL, 8, 1, 1),
(9, 0, '工作台',   'C', '/dashboard', 'dashboard/index', 'Dashboard', 'dashboard', 'dashboard:dashboard:view', 1, 1, 1);

-- 工作台禁用 isFrame，避免生成无意义的 Layout 父路由
UPDATE sys_menu SET is_frame = 0 WHERE id = 9;

-- ---------- 业务模块菜单 ----------
-- 学员管理
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(200, 2, '意向学员', 'C', 'prospect',  'student/prospect/index',  'StudentProspect',  '#', 'student:prospect:list',  1, 1, 1),
(201, 2, '在读学员', 'C', 'enrolled',  'student/enrolled/index',  'StudentEnrolled',  '#', 'student:enrolled:list',  2, 1, 1),
(202, 2, '结业学员', 'C', 'completed', 'student/completed/index', 'StudentCompleted', '#', 'student:completed:list', 3, 1, 1),
(203, 2, '学员学时', 'C', 'hours',     'student/hours/index',     'StudentHours',     '#', 'student:hours:list',     4, 1, 1),
-- 教务管理
(210, 3, '科目管理', 'C', 'subject',      'course/subject/index',      'CourseSubject',      '#', 'course:subject:list',      1, 1, 1),
(211, 3, '教室管理', 'C', 'classroom',    'course/classroom/index',    'CourseClassroom',    '#', 'course:classroom:list',    2, 1, 1),
(212, 3, '课程管理', 'C', 'course',       'course/course/index',       'CourseManage',       '#', 'course:course:list',       3, 1, 1),
(213, 3, '班级管理', 'C', 'class',        'course/class/index',        'ClassManage',        '#', 'class:class:list',         4, 1, 1),
(214, 3, '排课方案', 'C', 'schedulePlan', 'course/schedulePlan/index', 'SchedulePlan',       '#', 'schedule:plan:list',       5, 1, 1),
(215, 3, '排课日历', 'C', 'schedule',     'course/schedule/index',     'ScheduleCalendar',   '#', 'schedule:calendar:list',   6, 1, 1),
(216, 3, '课件资源', 'C', 'resource',     'course/resource/index',     'CourseResource',     '#', 'course:resource:list',     7, 1, 1),
-- 教务教学
(220, 4, '考试管理', 'C', 'exam',              'teaching/exam/index',              'TeachingExam',              '#', 'teaching:exam:list',              1, 1, 1),
(221, 4, '作业管理', 'C', 'homework',          'teaching/homework/index',          'TeachingHomework',          '#', 'teaching:homework:list',          2, 1, 1),
(222, 4, '教评学',   'C', 'evaluation',        'teaching/evaluation/index',        'TeachingEvaluation',        '#', 'teaching:evaluation:list',        3, 1, 1),
(223, 4, '学评教',   'C', 'teacherEvaluation', 'teaching/teacherEvaluation/index', 'TeachingTeacherEvaluation', '#', 'teaching:teacher-evaluation:list', 4, 1, 1),
(224, 4, '教师管理', 'C', 'teacher',           'teaching/teacher/index',           'TeachingTeacher',           '#', 'teaching:teacher:list',           5, 1, 1),
-- 财务管理
(230, 5, '学费管理', 'C', 'tuition',       'finance/tuition/index',       'FinanceTuition',       '#', 'tuition:fee:list',       1, 1, 1),
(231, 5, '薪资管理', 'C', 'payroll',       'finance/payroll/index',       'FinancePayroll',       '#', 'payroll:payroll:list',   2, 1, 1),
(232, 5, '教学统计', 'C', 'teachingStats', 'finance/teachingStats/index', 'FinanceTeachingStats', '#', 'payroll:pay-rate:list',  3, 1, 1),
-- 考勤管理
(240, 6, '签到记录', 'C', 'signin',       'attendance/signin/index',          'AttendanceSignin',       '#', 'attendance:signin:list',          1, 1, 1),
(241, 6, '学生请假', 'C', 'leave/student','attendance/leave/student/index',   'AttendanceLeaveStudent', '#', 'attendance:leave:student:list',  2, 1, 1),
(242, 6, '教师请假', 'C', 'leave/teacher','attendance/leave/teacher/index',   'AttendanceLeaveTeacher', '#', 'attendance:leave:teacher:list',  3, 1, 1),
-- 积分商城
(250, 7, '礼品管理', 'C', 'gift',       'points/gift/index',       'PointsGift',       '#', 'points:gift:list',       1, 1, 1),
(251, 7, '兑换记录', 'C', 'redemption', 'points/redemption/index', 'PointsRedemption', '#', 'points:redemption:list', 2, 1, 1),
-- 教师端
(260, 8, '工作台',   'C', 'dashboard',   'teacher/dashboard/index',   'TeacherDashboard',  '#', 'teacher:dashboard:view',   1, 1, 1),
(261, 8, '课后作业', 'C', 'homework',    'teacher/homework/index',    'TeacherHomework',   '#', 'teaching:homework:list',   2, 1, 1),
(262, 8, '课件共享', 'C', 'resource',    'teacher/resource/index',    'TeacherResource',   '#', 'course:resource:list',     3, 1, 1),
(263, 8, '我的课表', 'C', 'my-schedule', 'teacher/my-schedule/index', 'TeacherMySchedule', '#', 'schedule:calendar:list',   4, 1, 1);

-- ---------- 业务模块按钮 ----------
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
-- 意向学员
(200, '意向学员查询', 'F', 'student:prospect:query',    1, 1, 1),
(200, '意向学员新增', 'F', 'student:prospect:add',      2, 1, 1),
(200, '意向学员修改', 'F', 'student:prospect:edit',     3, 1, 1),
(200, '意向学员删除', 'F', 'student:prospect:delete',   4, 1, 1),
(200, '意向学员转化', 'F', 'student:prospect:convert',  5, 1, 1),
(200, '跟进记录管理', 'F', 'student:prospect:followup', 6, 1, 1),
-- 在读学员
(201, '在读学员查询', 'F', 'student:enrolled:query',  1, 1, 1),
(201, '在读学员新增', 'F', 'student:enrolled:add',    2, 1, 1),
(201, '在读学员修改', 'F', 'student:enrolled:edit',   3, 1, 1),
(201, '在读学员删除', 'F', 'student:enrolled:delete', 4, 1, 1),
-- 结业学员
(202, '结业学员查询', 'F', 'student:completed:query', 1, 1, 1),
-- 学员学时
(203, '学员学时列表', 'F', 'student:hours:list',  1, 1, 1),
(203, '学员学时查询', 'F', 'student:hours:query', 2, 1, 1),
(203, '学员学时新增', 'F', 'student:hours:add',   3, 1, 1),
-- 学员家长
(204, '学员家长查询', 'F', 'student:parent:query',  1, 1, 1),
(204, '学员家长新增', 'F', 'student:parent:add',    2, 1, 1),
(204, '学员家长修改', 'F', 'student:parent:edit',   3, 1, 1),
(204, '学员家长删除', 'F', 'student:parent:delete', 4, 1, 1),
-- 科目管理
(210, '科目查询', 'F', 'course:subject:query',  1, 1, 1),
(210, '科目新增', 'F', 'course:subject:add',    2, 1, 1),
(210, '科目修改', 'F', 'course:subject:edit',   3, 1, 1),
(210, '科目删除', 'F', 'course:subject:delete', 4, 1, 1),
-- 教室管理
(211, '教室查询', 'F', 'course:classroom:query',  1, 1, 1),
(211, '教室新增', 'F', 'course:classroom:add',    2, 1, 1),
(211, '教室修改', 'F', 'course:classroom:edit',   3, 1, 1),
(211, '教室删除', 'F', 'course:classroom:delete', 4, 1, 1),
-- 课程管理
(212, '课程查询', 'F', 'course:course:query',  1, 1, 1),
(212, '课程新增', 'F', 'course:course:add',    2, 1, 1),
(212, '课程修改', 'F', 'course:course:edit',   3, 1, 1),
(212, '课程删除', 'F', 'course:course:delete', 4, 1, 1),
-- 班级管理
(213, '班级查询',   'F', 'class:class:query',  1, 1, 1),
(213, '班级新增',   'F', 'class:class:add',    2, 1, 1),
(213, '班级修改',   'F', 'class:class:edit',   3, 1, 1),
(213, '班级删除',   'F', 'class:class:delete', 4, 1, 1),
(213, '班级学员管理', 'F', 'class:student:add',    5, 1, 1),
(213, '班级学员列表', 'F', 'class:student:list',   6, 1, 1),
(213, '班级学员修改', 'F', 'class:student:edit',   7, 1, 1),
(213, '班级学员删除', 'F', 'class:student:delete', 8, 1, 1),
-- 排课方案
(214, '排课方案查询', 'F', 'schedule:plan:query',  1, 1, 1),
(214, '排课方案新增', 'F', 'schedule:plan:add',    2, 1, 1),
(214, '排课方案修改', 'F', 'schedule:plan:edit',   3, 1, 1),
(214, '排课方案删除', 'F', 'schedule:plan:delete', 4, 1, 1),
-- 排课日历
(215, '排课日历查询', 'F', 'schedule:calendar:query', 1, 1, 1),
(215, '排课取消',     'F', 'schedule:cancel',        2, 1, 1),
(215, '排课调课',     'F', 'schedule:reschedule',    3, 1, 1),
(215, '排课消课',     'F', 'schedule:consume',       4, 1, 1),
-- 课件资源
(216, '课件资源查询', 'F', 'course:resource:query',  1, 1, 1),
(216, '课件资源新增', 'F', 'course:resource:add',    2, 1, 1),
(216, '课件资源修改', 'F', 'course:resource:edit',   3, 1, 1),
(216, '课件资源删除', 'F', 'course:resource:delete', 4, 1, 1),
-- 考试管理
(220, '考试查询', 'F', 'teaching:exam:query',  1, 1, 1),
(220, '考试新增', 'F', 'teaching:exam:add',    2, 1, 1),
(220, '考试修改', 'F', 'teaching:exam:edit',   3, 1, 1),
(220, '考试删除', 'F', 'teaching:exam:remove', 4, 1, 1),
-- 作业管理
(221, '作业查询', 'F', 'teaching:homework:query',  1, 1, 1),
(221, '作业新增', 'F', 'teaching:homework:add',    2, 1, 1),
(221, '作业修改', 'F', 'teaching:homework:edit',   3, 1, 1),
(221, '作业删除', 'F', 'teaching:homework:remove', 4, 1, 1),
-- 教评学
(222, '教评学查询', 'F', 'teaching:evaluation:query',  1, 1, 1),
(222, '教评学新增', 'F', 'teaching:evaluation:add',    2, 1, 1),
(222, '教评学删除', 'F', 'teaching:evaluation:remove', 3, 1, 1),
-- 学评教
(223, '学评教查询', 'F', 'teaching:teacher-evaluation:query',  1, 1, 1),
(223, '学评教导出', 'F', 'teaching:teacher-evaluation:export', 2, 1, 1),
-- 教师管理
(224, '教师查询', 'F', 'teaching:teacher:query',  1, 1, 1),
(224, '教师新增', 'F', 'teaching:teacher:add',    2, 1, 1),
(224, '教师修改', 'F', 'teaching:teacher:edit',   3, 1, 1),
(224, '教师删除', 'F', 'teaching:teacher:delete', 4, 1, 1),
-- 学费管理（含报名/缴费/退费/赠课）
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
(230, '报名查询',   'F', 'enrollment:enrollment:query',  16, 1, 1),
-- 薪资管理
(231, '薪资查询', 'F', 'payroll:payroll:query',   1, 1, 1),
(231, '薪资新增', 'F', 'payroll:payroll:add',     2, 1, 1),
(231, '薪资确认', 'F', 'payroll:payroll:confirm', 3, 1, 1),
(231, '薪资发放', 'F', 'payroll:payroll:pay',     4, 1, 1),
(231, '薪资删除', 'F', 'payroll:payroll:delete',  5, 1, 1),
-- 教学统计/课时费率
(232, '课时费率查询', 'F', 'payroll:pay-rate:query',  1, 1, 1),
(232, '课时费率新增', 'F', 'payroll:pay-rate:add',    2, 1, 1),
(232, '课时费率修改', 'F', 'payroll:pay-rate:edit',   3, 1, 1),
(232, '课时费率删除', 'F', 'payroll:pay-rate:delete', 4, 1, 1),
-- 签到记录
(240, '签到记录查询', 'F', 'attendance:signin:query', 1, 1, 1),
(240, '签到记录新增', 'F', 'attendance:signin:add',   2, 1, 1),
-- 学生请假
(241, '学生请假查询', 'F', 'attendance:leave:student:query',   1, 1, 1),
(241, '学生请假新增', 'F', 'attendance:leave:student:add',     2, 1, 1),
(241, '学生请假审批', 'F', 'attendance:leave:student:approve', 3, 1, 1),
-- 教师请假
(242, '教师请假查询',   'F', 'attendance:leave:teacher:query',   1, 1, 1),
(242, '教师请假新增',   'F', 'attendance:leave:teacher:add',     2, 1, 1),
(242, '教师请假审批',   'F', 'attendance:leave:teacher:approve', 3, 1, 1),
(242, '教师请假冲突处理', 'F', 'attendance:leave:teacher:resolve',4, 1, 1),
-- 礼品管理
(250, '礼品查询', 'F', 'points:gift:query',  1, 1, 1),
(250, '礼品新增', 'F', 'points:gift:add',    2, 1, 1),
(250, '礼品修改', 'F', 'points:gift:edit',   3, 1, 1),
(250, '礼品删除', 'F', 'points:gift:delete', 4, 1, 1),
-- 兑换记录
(251, '兑换记录查询', 'F', 'points:redemption:query',  1, 1, 1),
(251, '兑换记录导出', 'F', 'points:redemption:export', 2, 1, 1),
(251, '兑换发放',     'F', 'points:redemption:claim',  3, 1, 1),
(251, '兑换取消',     'F', 'points:redemption:cancel', 4, 1, 1),
-- 工作台
(9, '工作台查看', 'F', 'dashboard:dashboard:view', 1, 1, 1),
-- 积分账户（挂积分商城目录）
(7, '积分账户查询', 'F', 'points:account:query',  1, 1, 1),
(7, '积分账户调整', 'F', 'points:account:adjust', 2, 1, 1),
-- 教师端工作台
(260, '教师工作台查看', 'F', 'teacher:dashboard:view', 1, 1, 1);

-- ---------- 角色-菜单（超级管理员拥有全部菜单） ----------
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 教师角色菜单分配（ID精确匹配：教师端目录 + 4个子菜单 + 工作台）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu
WHERE id IN (8, 9, 260, 261, 262, 263)
   OR parent_id IN (8, 9, 260, 261, 262, 263)
   OR (permission IN (
       'teaching:homework:query', 'teaching:homework:add', 'teaching:homework:edit', 'teaching:homework:remove',
       'course:resource:query', 'course:resource:add', 'course:resource:edit', 'course:resource:delete',
       'schedule:calendar:query', 'schedule:calendar:list',
       'dashboard:dashboard:view', 'teacher:dashboard:view'
   ) AND type = 'F');

-- ---------- 字典类型 ----------
INSERT INTO sys_dict_type (id, name, code, status, remark) VALUES
(1, '用户性别', 'sys_user_sex',     1, '用户性别列表'),
(2, '系统开关', 'sys_normal_disable', 1, '系统开关列表'),
(3, '系统是否', 'sys_yes_no',       1, '系统是否列表'),
(4, '通知类型', 'sys_notice_type',  1, '通知类型列表'),
(5, '菜单类型', 'sys_menu_type',    1, '菜单类型列表'),
(6, '显示状态', 'sys_show_hide',    1, '显示状态列表'),
(7, '来源渠道', 'student_source',   1, '意向学员来源渠道'),
(8, '意向等级', 'interest_level',   1, '意向学员意向等级'),
(9, '跟进状态', 'prospect_follow_status', 1, '意向学员跟进状态'),
(10, '亲属关系', 'parent_relation', 1, '家长亲属关系'),
(11, '学员状态', 'student_status', 1, '在学学员状态'),
(12, '课程类型', 'course_type', 1, '课程类型列表'),
(13, '班级状态', 'class_status', 1, '班级状态列表');

-- ---------- 字典数据 ----------
INSERT INTO sys_dict_data (type_code, label, value, sort, is_default, status) VALUES
('sys_user_sex', '男',   '1', 1, 0, 1),
('sys_user_sex', '女',   '2', 2, 0, 1),
('sys_user_sex', '未知', '0', 3, 1, 1),
('sys_normal_disable', '正常', '1', 1, 1, 1),
('sys_normal_disable', '停用', '0', 2, 0, 1),
('sys_yes_no', '是', 'Y', 1, 1, 1),
('sys_yes_no', '否', 'N', 2, 0, 1),
('sys_notice_type', '通知', '1', 1, 1, 1),
('sys_notice_type', '公告', '2', 2, 0, 1),
('sys_menu_type', '目录', 'M', 1, 0, 1),
('sys_menu_type', '菜单', 'C', 2, 0, 1),
('sys_menu_type', '按钮', 'F', 3, 0, 1),
('sys_show_hide', '显示', '1', 1, 1, 1),
('sys_show_hide', '隐藏', '0', 2, 0, 1),
-- 意向学员相关字典
('student_source', '地推', 'DIRECT_MARKETING', 1, 0, 1),
('student_source', '转介绍', 'REFERRAL', 2, 0, 1),
('student_source', '线上', 'ONLINE', 3, 1, 1),
('student_source', '其他', 'OTHER', 4, 0, 1),
('interest_level', '高', 'HIGH', 1, 0, 1),
('interest_level', '中', 'MEDIUM', 2, 1, 1),
('interest_level', '低', 'LOW', 3, 0, 1),
('prospect_follow_status', '新线索', 'NEW', 1, 1, 1),
('prospect_follow_status', '已联系', 'CONTACTED', 2, 0, 1),
('prospect_follow_status', '已试听', 'TRIAL', 3, 0, 1),
('prospect_follow_status', '已转化', 'CONVERTED', 4, 0, 1),
('prospect_follow_status', '已流失', 'LOST', 5, 0, 1),
('parent_relation', '本人', 'SELF', 0, 0, 1),
('parent_relation', '父亲', 'FATHER', 1, 0, 1),
('parent_relation', '母亲', 'MOTHER', 2, 0, 1),
('parent_relation', '爷爷', 'GRANDFATHER', 3, 0, 1),
('parent_relation', '奶奶', 'GRANDMOTHER', 4, 0, 1),
('parent_relation', '外公', 'GRANDFATHER_M', 5, 0, 1),
('parent_relation', '外婆', 'GRANDMOTHER_M', 6, 0, 1),
('parent_relation', '其他', 'OTHER', 7, 0, 1),
-- 学员状态
('student_status', '在读', 'ENROLLED', 1, 1, 1),
('student_status', '停课', 'SUSPENDED', 2, 0, 1),
('student_status', '结业', 'COMPLETED', 3, 0, 1),
('student_status', '流失', 'LOST', 4, 0, 1),
-- 课程类型
('course_type', '班课', 'BATCH', 1, 1, 1),
('course_type', '一对一', 'ONE_ON_ONE', 2, 0, 1),
('course_type', '小班(2-6人)', 'SMALL_GROUP', 3, 0, 1),
-- 班级状态
('class_status', '招生中', 'RECRUITING', 1, 1, 1),
('class_status', '已满员', 'FULL', 2, 0, 1),
('class_status', '已停招', 'SUSPENDED', 3, 0, 1),
('class_status', '已结课', 'FINISHED', 4, 0, 1),
('class_status', '已取消', 'CANCELLED', 5, 0, 1);

-- ---------- 系统参数 ----------
INSERT INTO sys_config (name, `key`, `value`, type, remark) VALUES
('用户初始密码',   'sys.user.initPassword',   '123456', 'Y', '用户新增/重置的初始密码'),
('系统名称',       'sys.system.name',         '新艺智源管理平台', 'Y', '系统名称'),
('系统描述',       'sys.system.description',  '在线教育课程展示与交易平台', 'Y', '系统描述'),
('账号最大重试次数', 'sys.account.maxRetryCount', '5', 'Y', '登录失败锁定阈值'),
('账号锁定时间(分)', 'sys.account.lockTime',      '10', 'Y', '登录失败锁定时长');
