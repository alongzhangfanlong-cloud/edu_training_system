-- =====================================================
-- V8: 补充菜单项 + 作业/成长档案/积分/生日表 + 种子数据
-- =====================================================

-- 更新菜单(补全所有后台菜单项)
DELETE FROM menus;
INSERT INTO `menus` (`id`, `parent_id`, `menu_name`, `menu_path`, `menu_icon`, `menu_type`, `perm_code`, `sort_order`, `visible`) VALUES
(1, 0, '📊 工作台', '/dashboard', '', 2, 'dashboard', 1, 1),
(2, 0, '📅 教务管理', '', '', 1, '', 2, 1),
(3, 2, '排课管理', '/schedule-manage', '', 2, 'schedule:read', 1, 1),
(4, 2, '课程管理', '/course-manage', '', 2, 'course:read', 2, 1),
(5, 2, '教室管理', '/classroom-manage', '', 2, 'course:read', 3, 1),
(6, 2, '教师管理', '/teacher-manage', '', 2, 'teacher:read', 4, 1),
(7, 0, '👤 学员管理', '/student-manage', '', 2, 'student:read', 3, 1),
(8, 0, '🛒 报名缴费', '/enrollment', '', 2, 'order:write', 4, 1),
(9, 0, '✅ 考勤签到', '/attendance-query', '', 2, 'attendance:read', 5, 1),
(10, 0, '💬 家校互动', '', '', 1, '', 6, 1),
(11, 10, '课堂反馈', '/feedback-manage', '', 2, 'feedback:read', 1, 1),
(12, 10, '成长档案', '/growth-manage', '', 2, 'growth:read', 2, 1),
(13, 10, '请假管理', '/leave-manage', '', 2, 'leave:approve', 3, 1),
(14, 10, '布置作业', '/homework-manage', '', 2, 'homework:write', 4, 1),
(15, 10, '积分管理', '/points-manage', '', 2, 'points:view', 5, 1),
(16, 10, '生日祝福', '/birthday-manage', '', 2, 'birthday:notify', 6, 1),
(17, 0, '🏫 校区管理', '/campus-manage', '', 2, 'campus:manage', 7, 1),
(18, 0, '💰 财务管理', '/finance', '', 2, 'finance:read', 8, 1),
(19, 0, '📈 数据分析', '/statistics', '', 2, 'statistics:read', 9, 1),
(20, 0, '⚙️ 系统设置', '', '', 1, '', 10, 1),
(21, 20, '用户管理', '/user-manage', '', 2, 'user:manage', 1, 1),
(22, 20, '菜单管理', '/menu-manage', '', 2, 'user:manage', 2, 1),
(23, 20, '权限管理', '/permission-manage', '', 2, 'teacher:perm', 3, 1),
(24, 20, '系统配置', '/system-config', '', 2, 'system:config', 4, 1),
(25, 20, '公众号设置', '/wechat-config', '', 2, 'wechat:push', 5, 1);

-- 作业表
CREATE TABLE IF NOT EXISTS `homework` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `schedule_id` BIGINT,
    `class_id` BIGINT,
    `teacher_id` BIGINT,
    `content` TEXT COMMENT '作业内容',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 成长档案表
CREATE TABLE IF NOT EXISTS `growth_records` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL,
    `teacher_id` BIGINT,
    `record_date` DATE COMMENT '记录日期',
    `content` TEXT COMMENT '评语',
    `score` TINYINT DEFAULT 3 COMMENT '评分1-5',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成长档案表';

-- 积分表
CREATE TABLE IF NOT EXISTS `points` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL,
    `balance` INT DEFAULT 0 COMMENT '积分余额',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分表';

-- 积分流水
CREATE TABLE IF NOT EXISTS `point_transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL,
    `change_amount` INT COMMENT '变动(+/-)',
    `reason` VARCHAR(100),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流水表';

-- 生日祝福记录
CREATE TABLE IF NOT EXISTS `birthday_greetings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL,
    `greeting_date` DATE COMMENT '祝福日期',
    `message` VARCHAR(255),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生日祝福表';

-- ===== 种子数据 =====
INSERT IGNORE INTO `homework` (`id`, `class_id`, `teacher_id`, `content`) VALUES
(1, 3, 3, '完成课本P56-P58习题1-10，重点复习二次函数顶点式转换'),
(2, 5, 4, '背诵Unit 5单词，完成练习册P30-32阅读理解');

INSERT IGNORE INTO `growth_records` (`student_id`, `teacher_id`, `record_date`, `content`, `score`) VALUES
(3, 3, '2026-06-10', '课堂表现积极，正确回答了3道难题，数学思维能力有显著提升', 4),
(3, 3, '2026-06-17', '作业完成质量高，二次函数部分已完全掌握', 5),
(4, 4, '2026-06-12', '英语口语表达流畅，敢于在课堂上用英语交流', 4),
(5, 5, '2026-06-15', '物理竞赛题解题速度提升，但需要加强实验题训练', 3);

INSERT IGNORE INTO `points` (`student_id`, `balance`) VALUES
(3, 1250), (4, 800), (5, 2100), (6, 350), (7, 150);

INSERT IGNORE INTO `point_transactions` (`student_id`, `change_amount`, `reason`) VALUES
(3, 500, '报名赠送积分'), (3, 200, '课堂表现优秀'), (3, 100, '作业全优'),
(4, 500, '报名赠送积分'), (4, 300, '进步奖'),
(5, 1000, '报名赠送积分'), (5, 500, '物理竞赛入围奖'), (5, 200, '课堂积极');

INSERT IGNORE INTO `birthday_greetings` (`student_id`, `greeting_date`, `message`) VALUES
(3, '2026-06-15', '李明同学，祝你生日快乐！愿你学业进步，心想事成！'),
(4, '2026-05-20', '王小红同学，生日快乐！新的一岁更加优秀！');
