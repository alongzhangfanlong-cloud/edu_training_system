-- =====================================================
-- V11: 操作日志表
-- =====================================================

CREATE TABLE IF NOT EXISTS `operation_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT COMMENT '操作用户ID',
    `user_name` VARCHAR(50) COMMENT '操作用户名',
    `role` VARCHAR(30) COMMENT '用户角色',
    `module` VARCHAR(50) COMMENT '操作模块',
    `action` VARCHAR(50) COMMENT '操作类型 CREATE/UPDATE/DELETE/LOGIN/LOGOUT',
    `target` VARCHAR(200) COMMENT '操作对象',
    `target_id` BIGINT COMMENT '操作对象ID',
    `detail` VARCHAR(500) COMMENT '操作详情',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `status` TINYINT DEFAULT 1 COMMENT '1-成功 0-失败',
    `error_msg` VARCHAR(500) COMMENT '错误信息',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_module` (`module`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 种子数据
INSERT IGNORE INTO `operation_logs` (`id`, `user_id`, `user_name`, `role`, `module`, `action`, `target`, `target_id`, `detail`, `status`, `created_at`) VALUES
(1, 1, '刘老板', 'boss', '系统', 'LOGIN', '用户登录', 1, '老板登录系统', 1, NOW()),
(2, 1, '刘老板', 'boss', '学员管理', 'CREATE', '新增学员', 3, '新增学员: 李明', 1, NOW()),
(3, 2, '李校长', 'principal', '排课管理', 'CREATE', '批量排课', NULL, '批量排课 数学冲刺A班 5节', 1, NOW()),
(4, 3, '张老师', 'teacher', '考勤签到', 'UPDATE', '点名签到', 1, '学员李明出勤, 陈小美出勤', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 1, '刘老板', 'boss', '校区管理', 'CREATE', '创建校区', 1, '创建校区: 海淀总校', 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 1, '刘老板', 'boss', '用户管理', 'UPDATE', '更新用户', 2, '更新用户: 李校长 状态', 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 3, '张老师', 'teacher', '成长档案', 'CREATE', '新增评价', 3, '新增学员李明成长记录', 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 2, '李校长', 'principal', '课程管理', 'DELETE', '删除课程', 10, '删除测试课程', 1, DATE_SUB(NOW(), INTERVAL 5 DAY));
