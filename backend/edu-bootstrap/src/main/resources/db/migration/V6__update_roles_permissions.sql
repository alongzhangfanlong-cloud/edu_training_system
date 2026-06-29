-- =====================================================
-- V6: 更新角色体系 + 新增权限
-- =====================================================

-- 清除旧角色重新定义
DELETE FROM role_permissions;
DELETE FROM user_roles;
DELETE FROM permissions;
DELETE FROM roles;
DELETE FROM menus;
DELETE FROM users;

-- ===== 1. 角色 =====
INSERT INTO `roles` (`id`, `role_code`, `role_name`, `description`) VALUES
(1, 'boss', '老板端', '看见所有数据，设置所有功能，财务管理'),
(2, 'principal', '校长端', '校区管理、教师/学生课时消耗查看'),
(3, 'teacher', '教师端', '学员管理、课程管理、点名签到消课、成长档案、家校互动、请假管理、排课、作业'),
(4, 'parent', '家长端', '孩子课时查看、上课提醒、预警、成长档案、积分、课表、请假、到校离校通知'),
(5, 'student', '学员端', '课表查看、签到、查课时');

-- ===== 2. 权限 =====
INSERT INTO `permissions` (`id`, `perm_code`, `perm_name`, `perm_type`) VALUES
-- 基础模块
(1, 'dashboard', '工作台', 'menu'),
(2, 'student:read', '学员查看', 'menu'),
(3, 'student:write', '学员编辑', 'button'),
(4, 'student:delete', '学员删除', 'button'),
(5, 'course:read', '课程查看', 'menu'),
(6, 'course:write', '课程编辑', 'button'),
(7, 'teacher:read', '教师查看', 'menu'),
(8, 'teacher:write', '教师编辑', 'button'),
(9, 'schedule:read', '排课查看', 'menu'),
(10, 'schedule:write', '排课编辑', 'button'),
(11, 'attendance:read', '考勤查看', 'menu'),
(12, 'attendance:write', '考勤操作', 'button'),
(13, 'order:read', '订单查看', 'menu'),
(14, 'order:write', '订单操作', 'button'),
(15, 'finance:read', '财务查看', 'menu'),
(16, 'statistics:read', '统计查看', 'menu'),
(17, 'feedback:read', '反馈查看', 'menu'),
(18, 'feedback:write', '反馈操作', 'button'),
(19, 'system:config', '系统配置', 'menu'),
(20, 'user:manage', '用户管理', 'menu'),
-- 教师端新增
(21, 'homework:write', '布置作业', 'button'),
(22, 'growth:read', '成长档案查看', 'menu'),
(23, 'growth:write', '成长档案编辑', 'button'),
(24, 'leave:approve', '请假审批', 'button'),
(25, 'smart:schedule', '智能排课', 'menu'),
(26, 'birthday:notify', '生日祝福', 'menu'),
(27, 'wechat:push', '公众号推送', 'menu'),
(28, 'face:checkin', '人脸签到', 'button'),
(29, 'consume:fee', '课时费扣减', 'button'),
(30, 'teacher:perm', '老师权限设置', 'menu'),
-- 家长端新增
(31, 'hours:balance', '课时余额查看', 'menu'),
(32, 'hours:warning', '课时不足预警', 'menu'),
(33, 'points:view', '积分查看', 'menu'),
(34, 'points:manage', '积分管理', 'button'),
(35, 'recommend', '学员推荐奖励', 'menu'),
(36, 'arrive:notify', '到校通知', 'menu'),
(37, 'leave:notify', '离校通知', 'menu'),
(38, 'owe:warning', '欠课提醒', 'menu'),
(39, 'leave:apply', '请假申请', 'menu'),
-- 校长/老板端
(40, 'campus:manage', '校区管理', 'menu'),
(41, 'teacher:hours', '教师课时消耗查看', 'menu'),
(42, 'all:student:hours', '全部学员课时消耗查看', 'menu'),
(43, 'all:view', '查看所有数据', 'menu'),
(44, 'all:config', '设置所有功能', 'menu');

-- ===== 3. 角色-权限关联 =====
-- Boss: 所有权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),
(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),
(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),
(1,31),(1,32),(1,33),(1,34),(1,35),(1,36),(1,37),(1,38),(1,39),
(1,40),(1,41),(1,42),(1,43),(1,44);

-- 校长: 校区管理、教师/学生课时消耗、学员查看、排课、统计、反馈
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(2,1),(2,2),(2,5),(2,7),(2,9),(2,10),(2,11),(2,13),
(2,16),(2,17),(2,22),(2,24),(2,40),(2,41),(2,42);

-- 教师: 学员管理、课程管理、点名签到消课、成长档案、家校互动、请假审批、排课、作业、数据统计、二维码签到
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(3,1),(3,2),(3,3),(3,5),(3,6),(3,9),(3,10),
(3,11),(3,12),(3,16),(3,17),(3,18),
(3,21),(3,22),(3,23),(3,24),(3,25),(3,26),(3,28),(3,29);

-- 家长: 课时查看/预警、课表、请假、到校离校通知、积分、成长档案、推荐、欠课提醒
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(4,1),(4,2),(4,9),(4,11),(4,13),(4,17),
(4,31),(4,32),(4,33),(4,35),(4,36),(4,37),(4,38),(4,39);

-- 学员: 课表、签到、查课时
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(5,1),(5,9),(5,11),(5,13);

-- ===== 4. 菜单 =====
INSERT INTO `menus` (`id`, `parent_id`, `menu_name`, `menu_path`, `menu_icon`, `menu_type`, `perm_code`, `sort_order`) VALUES
(1, 0, '工作台', '/dashboard', '', 2, 'dashboard', 1),
(2, 0, '教务管理', '', '', 1, '', 2),
(3, 2, '排课管理', '/schedule-manage', '', 2, 'schedule:read', 1),
(4, 2, '智能排课', '/smart-schedule', '', 2, 'smart:schedule', 2),
(5, 2, '课程管理', '/course-manage', '', 2, 'course:read', 3),
(6, 2, '教师管理', '/teacher-manage', '', 2, 'teacher:read', 4),
(7, 0, '学员管理', '/student-manage', '', 2, 'student:read', 3),
(8, 0, '报名缴费', '/enrollment', '', 2, 'order:write', 4),
(9, 0, '考勤签到', '/attendance-query', '', 2, 'attendance:read', 5),
(10, 0, '家校互动', '', '', 1, '', 6),
(11, 10, '课堂反馈', '/feedback', '', 2, 'feedback:read', 1),
(12, 10, '成长档案', '/growth', '', 2, 'growth:read', 2),
(13, 10, '请假管理', '/leave-manage', '', 2, 'leave:approve', 3),
(14, 10, '生日祝福', '/birthday', '', 2, 'birthday:notify', 4),
(15, 10, '布置作业', '/homework', '', 2, 'homework:write', 5),
(16, 10, '积分管理', '/points', '', 2, 'points:view', 6),
(17, 0, '校区管理', '/campus', '', 2, 'campus:manage', 7),
(18, 0, '财务管理', '/finance', '', 2, 'finance:read', 8),
(19, 0, '数据分析', '/statistics', '', 2, 'statistics:read', 9),
(20, 0, '系统设置', '', '', 1, '', 10),
(21, 20, '用户管理', '/user-manage', '', 2, 'user:manage', 1),
(22, 20, '权限管理', '/permission-manage', '', 2, 'teacher:perm', 2),
(23, 20, '系统配置', '/system-config', '', 2, 'system:config', 3),
(24, 20, '公众号设置', '/wechat-config', '', 2, 'wechat:push', 4);

-- ===== 5. 用户 =====
INSERT INTO `users` (`id`, `username`, `password`, `real_name`, `phone`, `status`) VALUES
(1, 'boss', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王老板', '13800000000', 1),
(2, 'principal', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李校长', '13800000001', 1),
(3, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', '13900000001', 1),
(4, 'teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李老师', '13900000002', 1),
(5, 'teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王老师', '13900000003', 1),
(6, 'parent1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李爸爸', '13900020001', 1),
(7, 'parent2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王妈妈', '13900020002', 1),
(8, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李明', '13800010001', 1),
(9, 'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王小红', '13800010002', 1),
(10, 'student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵强', '13800010003', 1);

-- 用户-角色关联
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1,1), (2,2), (3,3), (4,3), (5,3),
(6,4), (7,4), (8,5), (9,5), (10,5);
