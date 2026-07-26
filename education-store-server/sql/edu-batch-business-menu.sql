-- =====================================================
-- 业务模块菜单数据 — 权限码对齐后端 @PreAuthorize 注解
-- =====================================================
SET NAMES utf8mb4;

-- 目录 (M) — 5个一级模块
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, sort, visible, status) VALUES
(2,  0, '学员管理',   'M', '/student',  NULL, 'Student',  'people',    2, 1, 1),
(3,  0, '教务管理',   'M', '/course',   NULL, 'Course',   'education', 3, 1, 1),
(4,  0, '班级管理',   'M', '/class',    NULL, 'Class',    'component', 4, 1, 1),
(5,  0, '教学管理',   'M', '/teaching', NULL, 'Teaching', 'guide',     5, 1, 1);

-- ==================== 学员管理 (parent=2) ====================
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(200, 2, '潜在学员',   'M', 'prospect',  NULL,                       'Prospect',  'upload',   NULL,                        1, 1, 1),
(201, 2, '在学学员',   'C', 'enrolled',  'student/enrolled/index',  'Enrolled',  'user',     'student:enrolled:list',     2, 1, 1),
(202, 2, '已结课学员', 'C', 'completed', 'student/completed/index', 'Completed',  'list',     'student:enrolled:list',     3, 1, 1),
(203, 2, '课时记录',   'C', 'hours',     'student/hours/index',     'Hours',      'time',     'student:hours:list',        4, 1, 1),
(204, 2, '家长管理',   'C', 'parents',   'student/parents/index',   'Parents',    'tree',     'student:parent:list',       5, 1, 1);

-- 潜在学员子菜单 (parent=200)
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(2001, 200, '潜在学员列表', 'C', 'list',   'student/prospect/index', 'ProspectList', 'peoples', 'student:prospect:list', 1, 1, 1),
(2002, 200, '跟进记录', 'C', 'followup',   'student/prospect/index', 'ProspectFollowup', 'documentation', 'student:prospect:followup', 2, 1, 1);

-- 学员管理按钮 (按后端实际权限码)
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
-- 潜在学员
(2001, '潜在查询', 'F', 'student:prospect:query',    1, 1, 1),
(2001, '潜在新增', 'F', 'student:prospect:add',      2, 1, 1),
(2001, '潜在修改', 'F', 'student:prospect:edit',     3, 1, 1),
(2001, '潜在删除', 'F', 'student:prospect:delete',   4, 1, 1),
(2001, '潜在转化', 'F', 'student:prospect:convert',  5, 1, 1),
(2002, '跟进新增', 'F', 'student:prospect:followup', 1, 1, 1),
-- 在学学员
(201, '学员查询', 'F', 'student:enrolled:query',  1, 1, 1),
(201, '学员新增', 'F', 'student:enrolled:add',     2, 1, 1),
(201, '学员修改', 'F', 'student:enrolled:edit',    3, 1, 1),
(201, '学员删除', 'F', 'student:enrolled:delete',  4, 1, 1),
-- 课时记录
(203, '课时查询', 'F', 'student:hours:query',     1, 1, 1),
(203, '课时新增', 'F', 'student:hours:add',       2, 1, 1),
-- 家长管理
(204, '家长查询', 'F', 'student:parent:query',    1, 1, 1),
(204, '家长新增', 'F', 'student:parent:add',      2, 1, 1),
(204, '家长修改', 'F', 'student:parent:edit',     3, 1, 1),
(204, '家长删除', 'F', 'student:parent:delete',   4, 1, 1);

-- ==================== 教务管理 (parent=3) ====================
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(300, 3, '科目管理',   'C', 'subject',      'course/subject/index',      'Subject',      'list',          'course:subject:list',      1, 1, 1),
(301, 3, '课程管理',   'C', 'course',       'course/course/index',       'CourseMgmt',   'education',     'course:course:list',       2, 1, 1),
(302, 3, '教室管理',   'C', 'classroom',    'course/classroom/index',    'Classroom',    'component',     'course:classroom:list',    3, 1, 1),
(303, 3, '排课计划',   'C', 'schedulePlan', 'course/schedulePlan/index', 'SchedulePlan', 'date',          'schedule:plan:list',       4, 1, 1),
(304, 3, '课表管理',   'C', 'schedule',     'course/schedule/index',     'Schedule',     'date-range',    'schedule:calendar:list',   5, 1, 1),
(305, 3, '资源管理',   'C', 'resource',     'course/resource/index',     'Resource',     'documentation', 'course:resource:list',     6, 1, 1);

-- 教务管理按钮
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(300, '科目查询', 'F', 'course:subject:query',    1, 1, 1),
(300, '科目新增', 'F', 'course:subject:add',      2, 1, 1),
(300, '科目修改', 'F', 'course:subject:edit',     3, 1, 1),
(300, '科目删除', 'F', 'course:subject:delete',   4, 1, 1),
(301, '课程查询', 'F', 'course:course:query',     1, 1, 1),
(301, '课程新增', 'F', 'course:course:add',       2, 1, 1),
(301, '课程修改', 'F', 'course:course:edit',      3, 1, 1),
(301, '课程删除', 'F', 'course:course:delete',    4, 1, 1),
(302, '教室查询', 'F', 'course:classroom:query',  1, 1, 1),
(302, '教室新增', 'F', 'course:classroom:add',    2, 1, 1),
(302, '教室修改', 'F', 'course:classroom:edit',   3, 1, 1),
(302, '教室删除', 'F', 'course:classroom:delete', 4, 1, 1),
(303, '排课查询', 'F', 'schedule:plan:query',     1, 1, 1),
(303, '排课新增', 'F', 'schedule:plan:add',       2, 1, 1),
(303, '排课修改', 'F', 'schedule:plan:edit',      3, 1, 1),
(303, '排课删除', 'F', 'schedule:plan:delete',    4, 1, 1),
(304, '课表查询', 'F', 'schedule:calendar:list',  1, 1, 1),
(304, '取消排课', 'F', 'schedule:cancel',          2, 1, 1),
(304, '重新排课', 'F', 'schedule:reschedule',      3, 1, 1),
(304, '课时消耗', 'F', 'schedule:consume',         4, 1, 1),
(305, '资源查询', 'F', 'course:resource:query',   1, 1, 1),
(305, '资源新增', 'F', 'course:resource:add',     2, 1, 1),
(305, '资源修改', 'F', 'course:resource:edit',    3, 1, 1),
(305, '资源删除', 'F', 'course:resource:delete',  4, 1, 1);

-- ==================== 班级管理 (parent=4) ====================
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(400, 4, '班级管理', 'C', 'classManage', 'course/class/index', 'ClassMgmt', 'class', 'class:class:list', 1, 1, 1);

INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(400, '班级查询', 'F', 'class:class:query',   1, 1, 1),
(400, '班级新增', 'F', 'class:class:add',     2, 1, 1),
(400, '班级修改', 'F', 'class:class:edit',    3, 1, 1),
(400, '班级删除', 'F', 'class:class:delete',  4, 1, 1),
(400, '学员管理', 'F', 'class:student:list',   5, 1, 1),
(400, '学员添加', 'F', 'class:student:add',    6, 1, 1),
(400, '学员移除', 'F', 'class:student:delete', 7, 1, 1);

-- ==================== 教学管理 (parent=5) ====================
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(500, 5, '教师管理',    'C', 'teacher',           'teaching/teacher/index',            'Teacher',    'peoples',  'teaching:teacher:list',             1, 1, 1),
(501, 5, '作业管理',    'C', 'homework',          'teaching/homework/index',           'Homework',   'edit',     'teaching:homework:list',            2, 1, 1),
(502, 5, '考试管理',    'C', 'exam',              'teaching/exam/index',               'Exam',       'form',     'teaching:exam:list',                3, 1, 1),
(503, 5, '教学评价',    'C', 'evaluation',        'teaching/evaluation/index',         'Evaluation', 'star',     'teaching:evaluation:list',          4, 1, 1),
(504, 5, '教师考核',    'C', 'teacherEvaluation',  'teaching/teacherEvaluation/index',  'TeacherEval','star-off', 'teaching:teacher-evaluation:list',  5, 1, 1);

-- 教学管理按钮
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
(500, '教师查询', 'F', 'teaching:teacher:query',   1, 1, 1),
(500, '教师新增', 'F', 'teaching:teacher:add',     2, 1, 1),
(500, '教师修改', 'F', 'teaching:teacher:edit',    3, 1, 1),
(500, '教师删除', 'F', 'teaching:teacher:delete',  4, 1, 1),
(501, '作业查询', 'F', 'teaching:homework:list',   1, 1, 1),
(501, '作业新增', 'F', 'teaching:homework:add',    2, 1, 1),
(501, '作业修改', 'F', 'teaching:homework:edit',   3, 1, 1),
(501, '作业删除', 'F', 'teaching:homework:remove', 4, 1, 1),
(502, '考试查询', 'F', 'teaching:exam:list',       1, 1, 1),
(502, '考试新增', 'F', 'teaching:exam:add',        2, 1, 1),
(502, '考试修改', 'F', 'teaching:exam:edit',       3, 1, 1),
(502, '考试删除', 'F', 'teaching:exam:remove',     4, 1, 1),
(503, '评价查询', 'F', 'teaching:evaluation:list',  1, 1, 1),
(503, '评价新增', 'F', 'teaching:evaluation:add',   2, 1, 1),
(503, '评价删除', 'F', 'teaching:evaluation:remove',3, 1, 1),
(504, '考核查询', 'F', 'teaching:teacher-evaluation:list', 1, 1, 1),
(504, '考核导出', 'F', 'teaching:teacher-evaluation:export',2, 1, 1);

-- ============ 超级管理员拥有全部新菜单 ============
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id NOT IN (SELECT menu_id FROM sys_role_menu WHERE role_id = 1);
