-- =====================================================
-- V9: 请假表 + 给interaction模块表补campus_id
-- =====================================================

-- 请假表
CREATE TABLE IF NOT EXISTS `leaves` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `student_id` BIGINT NOT NULL,
    `schedule_id` BIGINT,
    `leave_type` TINYINT DEFAULT 1 COMMENT '1-病假 2-事假 3-其他',
    `reason` VARCHAR(255) COMMENT '请假原因',
    `status` TINYINT DEFAULT 0 COMMENT '0-待审批 1-已通过 2-已拒绝',
    `approve_remark` VARCHAR(255) COMMENT '审批备注',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_student` (`student_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假表';

-- 给现有表补campus_id字段（多校区数据隔离）
ALTER TABLE `homework` ADD COLUMN `campus_id` BIGINT DEFAULT 1;
ALTER TABLE `growth_records` ADD COLUMN `campus_id` BIGINT DEFAULT 1;
ALTER TABLE `points` ADD COLUMN `campus_id` BIGINT DEFAULT 1;
ALTER TABLE `point_transactions` ADD COLUMN `campus_id` BIGINT DEFAULT 1;
ALTER TABLE `birthday_greetings` ADD COLUMN `campus_id` BIGINT DEFAULT 1;
ALTER TABLE `leaves` ADD COLUMN `campus_id` BIGINT DEFAULT 1;

-- 请假种子数据
INSERT IGNORE INTO `leaves` (`id`, `student_id`, `schedule_id`, `leave_type`, `reason`, `status`, `approve_remark`) VALUES
(1, 3, 1, 1, '感冒发烧需要休息', 1, '同意，注意休息'),
(2, 4, 2, 2, '家中有事需请假一天', 1, '同意'),
(3, 5, 3, 1, '肠胃不适需就医', 0, NULL),
(4, 6, 1, 3, '其他原因', 2, '建议调整时间而非请假');
