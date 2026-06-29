-- =====================================================
-- V4: 角色/权限/菜单初始化 + 丰富演示数据
-- =====================================================

-- ===== 1. 角色 =====
INSERT IGNORE INTO `roles` (`id`, `role_code`, `role_name`, `description`) VALUES
(1, 'admin', '超级管理员', '系统最高权限，管理所有功能'),
(2, 'teacher', '教师', '授课、点名、反馈'),
(3, 'student', '学员', '查看课表、签到、查课时'),
(4, 'parent', '家长', '查看孩子信息、请假、缴费');

-- ===== 2. 权限 =====
INSERT IGNORE INTO `permissions` (`id`, `perm_code`, `perm_name`, `perm_type`, `parent_id`, `sort_order`) VALUES
(1, 'dashboard', '工作台', 'menu', 0, 1),
(2, 'student:read', '学员查看', 'menu', 0, 2),
(3, 'student:write', '学员编辑', 'button', 2, 1),
(4, 'student:delete', '学员删除', 'button', 2, 2),
(5, 'course:read', '课程查看', 'menu', 0, 3),
(6, 'course:write', '课程编辑', 'button', 5, 1),
(7, 'teacher:read', '教师查看', 'menu', 0, 4),
(8, 'teacher:write', '教师编辑', 'button', 7, 1),
(9, 'schedule:read', '排课查看', 'menu', 0, 5),
(10, 'schedule:write', '排课编辑', 'button', 9, 1),
(11, 'attendance:read', '考勤查看', 'menu', 0, 6),
(12, 'attendance:write', '考勤操作', 'button', 11, 1),
(13, 'order:read', '订单查看', 'menu', 0, 7),
(14, 'order:write', '订单操作', 'button', 13, 1),
(15, 'finance:read', '财务查看', 'menu', 0, 8),
(16, 'statistics:read', '统计查看', 'menu', 0, 9),
(17, 'feedback:read', '反馈查看', 'menu', 0, 10),
(18, 'feedback:write', '反馈操作', 'button', 17, 1),
(19, 'system:config', '系统配置', 'menu', 0, 11),
(20, 'user:manage', '用户管理', 'menu', 0, 12);

-- ===== 3. 角色-权限关联 =====
-- Admin: 所有权限
INSERT IGNORE INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),
(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20);
-- Teacher: 课表查看、考勤操作、反馈操作、学员查看
INSERT IGNORE INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(2,1),(2,2),(2,9),(2,10),(2,11),(2,12),(2,17),(2,18);
-- Student: 课表查看、考勤查看、订单查看、反馈查看
INSERT IGNORE INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(3,1),(3,9),(3,11),(3,13),(3,17);
-- Parent: 学员查看、课表查看、订单查看
INSERT IGNORE INTO `role_permissions` (`role_id`, `permission_id`) VALUES
(4,2),(4,9),(4,11),(4,13);

-- ===== 4. 菜单 =====
INSERT IGNORE INTO `menus` (`id`, `parent_id`, `menu_name`, `menu_path`, `menu_icon`, `menu_type`, `perm_code`, `sort_order`) VALUES
(1, 0, '工作台', '/dashboard', 'DataAnalysis', 2, 'dashboard', 1),
(2, 0, '教务管理', '', 'Calendar', 1, '', 2),
(3, 2, '排课管理', '/schedule-manage', '', 2, 'schedule:read', 1),
(4, 2, '课程管理', '/course-manage', '', 2, 'course:read', 2),
(5, 2, '教室管理', '/classroom-manage', '', 2, 'course:read', 3),
(6, 2, '教师管理', '/teacher-manage', '', 2, 'teacher:read', 4),
(7, 0, '学员管理', '/student-manage', 'User', 2, 'student:read', 3),
(8, 0, '报名缴费', '/enrollment', 'ShoppingCart', 2, 'order:write', 4),
(9, 0, '考勤查询', '/attendance-query', 'Check', 2, 'attendance:read', 5),
(10, 0, '财务管理', '/finance', 'Money', 2, 'finance:read', 6),
(11, 0, '系统设置', '', 'Setting', 1, '', 7),
(12, 11, '用户管理', '/user-manage', '', 2, 'user:manage', 1),
(13, 11, '系统配置', '/system-config', '', 2, 'system:config', 2);

-- ===== 5. 用户 (密码均为 123456 的BCrypt哈希) =====
-- BCrypt($2a$10$...) for "123456"
INSERT IGNORE INTO `users` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `email`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800000000', NULL, 'admin@edu.com', 1),
(2, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', '13900000001', NULL, 'zhang@edu.com', 1),
(3, 'teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李老师', '13900000002', NULL, 'li@edu.com', 1),
(4, 'teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王老师', '13900000003', NULL, 'wang@edu.com', 1),
(5, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李明', '13800010001', NULL, NULL, 1),
(6, 'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王小红', '13800010002', NULL, NULL, 1),
(7, 'student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵强', '13800010003', NULL, NULL, 1),
(8, 'student4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈小美', '13800010004', NULL, NULL, 1),
(9, 'student5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘洋', '13800010005', NULL, NULL, 1),
(10, 'parent1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李爸爸', '13900020001', NULL, NULL, 1);

-- 用户-角色关联
INSERT IGNORE INTO `user_roles` (`user_id`, `role_id`) VALUES
(1,1), (2,2), (3,2), (4,2),
(5,3), (6,3), (7,3), (8,3), (9,3), (10,4);

-- ===== 6. 学员演示数据 =====
INSERT IGNORE INTO `students` (`id`, `student_no`, `name`, `gender`, `grade`, `school`, `phone`, `tags`, `status`, `source`, `remark`) VALUES
(3, 'STU202600001', '李明', 1, '初三', '北京市第一中学', '13800010001', '["VIP","数学薄弱"]', 1, '官网', '需要重点辅导数学'),
(4, 'STU202600002', '王小红', 2, '初二', '北京市第二中学', '13800010002', '["英语尖子"]', 1, '转介绍', '英语成绩优秀'),
(5, 'STU202600003', '赵强', 1, '高一', '北京市第三中学', '13800010003', '["物理竞赛"]', 1, '地推', '目标物理竞赛省一'),
(6, 'STU202600004', '陈小美', 2, '初三', '北京市第四中学', '13800010004', '["VIP","全科"]', 1, '官网', '全科辅导需求'),
(7, 'STU202600005', '刘洋', 1, '高二', '北京市第五中学', '13800010005', '["化学薄弱"]', 1, '合作渠道', '化学需要加强'),
(8, 'STU202600006', '周杰', 1, '初三', '北京师范大学附中', '13800010006', '[]', 0, '地推', '意向学员，待跟进'),
(9, 'STU202600007', '吴芳', 2, '初一', '清华附中', '13800010007', '["VIP"]', 1, '官网', ''),
(10, 'STU202600008', '郑浩', 1, '高三', '人大附中', '13800010008', '["冲刺班"]', 2, '转介绍', '因家庭原因停课');

-- ===== 7. 学员家长关联 =====
INSERT IGNORE INTO `student_parents` (`student_id`, `parent_name`, `relation`, `phone`, `is_primary`) VALUES
(3, '李建国', '父亲', '13900020001', 1),
(3, '张丽', '母亲', '13900020002', 0),
(4, '王大明', '父亲', '13900020003', 1);

-- ===== 8. 教师演示数据 =====
INSERT IGNORE INTO `teachers` (`id`, `teacher_no`, `name`, `phone`, `gender`, `subjects`, `introduction`, `hire_date`, `status`) VALUES
(3, 'TCH202600001', '张老师', '13900000001', 1, '["数学","物理"]', '10年教龄，曾任重点中学数学教研组长', '2020-03-01', 1),
(4, 'TCH202600002', '李老师', '13900000002', 2, '["英语","语文"]', '海外留学背景，擅长口语教学', '2021-09-01', 1),
(5, 'TCH202600003', '王老师', '13900000003', 1, '["化学","生物"]', '985博士，5年教学经验', '2022-06-01', 1),
(6, 'TCH202600004', '赵老师', '13900000004', 2, '["钢琴","音乐"]', '音乐学院硕士，擅长少儿钢琴启蒙', '2023-01-01', 1);

-- ===== 9. 教室演示数据 =====
INSERT IGNORE INTO `classrooms` (`id`, `name`, `capacity`, `location`, `facilities`, `status`) VALUES
(3, '101-多媒体教室', 30, '1楼东侧', '["投影仪","白板","电脑","空调"]', 1),
(4, '102-标准教室', 25, '1楼西侧', '["白板","空调"]', 1),
(5, '201-小班教室', 10, '2楼东侧', '["白板","空调"]', 1),
(6, '301-音乐教室', 15, '3楼', '["钢琴","音响系统","舞蹈镜"]', 1),
(7, '202-实验室', 20, '2楼西侧', '["实验台","投影仪","储物柜"]', 1);

-- ===== 10. 课程演示数据 =====
INSERT IGNORE INTO `courses` (`id`, `name`, `code`, `type`, `subject`, `grade_range`, `duration`, `unit_price`, `description`, `status`) VALUES
(3, '初三数学冲刺班', 'CRS001', 1, '数学', '初三', 120, 150.00, '针对中考数学重点难点进行强化训练', 1),
(4, '初中英语培优班', 'CRS002', 1, '英语', '初一-初三', 90, 120.00, '提升英语综合能力，听说读写全面发展', 1),
(5, '高中物理竞赛班', 'CRS003', 3, '物理', '高一-高三', 180, 200.00, '物理竞赛专项训练，目标省一以上', 1),
(6, '一对一数学辅导', 'CRS004', 2, '数学', '初中-高中', 60, 300.00, '一对一精准辅导，量身定制学习方案', 1),
(7, '少儿钢琴启蒙', 'CRS005', 2, '音乐', '5-12岁', 45, 200.00, '适合零基础儿童，培养音乐兴趣', 1),
(8, '高中化学特训', 'CRS006', 1, '化学', '高一-高三', 120, 160.00, '高考化学重难点突破', 1);

-- ===== 11. 班级演示数据 =====
INSERT IGNORE INTO `classes` (`id`, `course_id`, `teacher_id`, `classroom_id`, `name`, `max_students`, `current_students`, `start_date`, `status`) VALUES
(3, 3, 3, 3, '数学冲刺A班', 20, 8, '2026-03-01', 1),
(4, 3, 3, 4, '数学冲刺B班', 20, 12, '2026-03-01', 1),
(5, 4, 4, 5, '英语培优班', 10, 6, '2026-02-15', 1),
(6, 5, 5, 7, '物理竞赛班', 8, 5, '2026-01-10', 1),
(7, 6, 3, 3, '数学1对1-张老师', 1, 1, '2026-04-01', 1),
(8, 8, 5, 3, '化学特训班', 20, 10, '2026-03-15', 1);

-- ===== 12. 学员-班级关系(报名记录) =====
INSERT IGNORE INTO `student_classes` (`student_id`, `class_id`, `total_hours`, `used_hours`, `remain_hours`, `gift_hours`, `status`) VALUES
(3, 3, 40.0, 12.0, 28.0, 2.0, 1),
(4, 5, 36.0, 8.0, 28.0, 0, 1),
(5, 6, 48.0, 16.0, 32.0, 4.0, 1),
(6, 3, 40.0, 4.0, 36.0, 0, 1),
(7, 8, 40.0, 20.0, 20.0, 2.0, 1),
(3, 6, 20.0, 8.0, 12.0, 0, 1),
(9, 4, 40.0, 0, 40.0, 0, 1);

-- ===== 13. 排课数据(近两周) =====
INSERT IGNORE INTO `schedules` (`class_id`, `course_id`, `teacher_id`, `classroom_id`, `date`, `start_time`, `end_time`, `lesson_no`, `status`) VALUES
(3, 3, 3, 3, CURDATE(), '09:00', '11:00', 15, 1),
(4, 3, 3, 4, CURDATE(), '14:00', '16:00', 15, 1),
(5, 4, 4, 5, CURDATE(), '10:00', '11:30', 12, 1),
(6, 5, 5, 7, CURDATE(), '09:00', '12:00', 20, 1),
(3, 3, 3, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00', '11:00', 16, 1),
(5, 4, 4, 5, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00', '11:30', 13, 1),
(8, 8, 5, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00', '16:00', 18, 1),
(3, 3, 3, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00', '11:00', 17, 1),
(6, 5, 5, 7, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00', '12:00', 21, 1),
(4, 3, 3, 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:00', '11:00', 16, 1),
(5, 4, 4, 5, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00', '11:30', 14, 1),
(3, 3, 3, 3, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '09:00', '11:00', 18, 1);

-- ===== 14. 考勤记录(最近几天) =====
INSERT IGNORE INTO `attendances` (`schedule_id`, `student_id`, `class_id`, `status`, `check_in_time`, `check_in_method`, `leave_type`, `leave_reason`, `leave_approve_status`) VALUES
(1, 3, 3, 1, TIMESTAMPADD(HOUR, -1, NOW()), 2, NULL, NULL, NULL),
(1, 6, 3, 1, TIMESTAMPADD(MINUTE, -30, NOW()), 2, NULL, NULL, NULL),
(2, 9, 4, 2, NULL, NULL, 1, '身体不适，请假一次', 1),
(3, 4, 5, 1, TIMESTAMPADD(MINUTE, -15, NOW()), 2, NULL, NULL, NULL),
(4, 5, 6, 3, NULL, NULL, NULL, NULL, NULL);

-- ===== 15. 订单数据 =====
INSERT IGNORE INTO `orders` (`id`, `order_no`, `student_id`, `type`, `total_amount`, `discount_amount`, `pay_amount`, `pay_status`, `pay_method`, `pay_time`) VALUES
(3, 'ORD20260300001', 3, 1, 6000.00, 200.00, 5800.00, 1, 3, '2026-03-01 10:00:00'),
(4, 'ORD20260300002', 4, 1, 4320.00, 0, 4320.00, 1, 3, '2026-02-15 14:00:00'),
(5, 'ORD20260400001', 5, 1, 9600.00, 500.00, 9100.00, 1, 1, '2026-04-01 09:00:00'),
(6, 'ORD20260600001', 6, 1, 6000.00, 0, 6000.00, 0, NULL, NULL);

INSERT IGNORE INTO `order_items` (`order_id`, `course_id`, `class_id`, `item_type`, `hours`, `unit_price`, `total_price`, `gift_hours`) VALUES
(3, 3, 3, 1, 40.0, 150.00, 6000.00, 2.0),
(4, 4, 5, 1, 36.0, 120.00, 4320.00, 0),
(5, 5, 6, 1, 48.0, 200.00, 9600.00, 4.0),
(6, 8, 8, 1, 40.0, 150.00, 6000.00, 0);

-- ===== 16. 课堂反馈 =====
INSERT IGNORE INTO `class_feedbacks` (`schedule_id`, `class_id`, `teacher_id`, `content`, `homework`, `overall_comment`, `next_preview`) VALUES
(1, 3, 3, '今日讲解了二次函数的图像与性质，重点复习了顶点式和一般式的转换', '完成课本P56-P58习题1-10', '同学们学习积极性较高，李明的计算能力有明显进步', '下次课将讲解二次函数与一元二次方程的关系');

INSERT IGNORE INTO `student_feedbacks` (`schedule_id`, `student_id`, `teacher_id`, `performance_score`, `performance_comment`, `homework_status`) VALUES
(1, 3, 3, 4, '课堂表现积极，正确回答了3道难题', 1),
(1, 6, 3, 3, '注意力有时不集中，需要多提醒', 2);
