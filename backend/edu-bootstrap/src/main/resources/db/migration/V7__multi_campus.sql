-- =====================================================
-- V7: 多校区支持
-- =====================================================

-- 校区表
CREATE TABLE IF NOT EXISTS `campuses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `campus_name` VARCHAR(100) NOT NULL COMMENT '校区名称',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '校区电话',
    `principal_id` BIGINT DEFAULT NULL COMMENT '校长用户ID',
    `status` TINYINT DEFAULT 1 COMMENT '0-停用 1-正常',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_principal` (`principal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校区表';

-- 已有表增加 campus_id
ALTER TABLE `teachers` ADD COLUMN `campus_id` BIGINT DEFAULT NULL COMMENT '归属校区' AFTER `status`;
ALTER TABLE `classrooms` ADD COLUMN `campus_id` BIGINT DEFAULT NULL COMMENT '归属校区' AFTER `status`;
ALTER TABLE `students` ADD COLUMN `campus_id` BIGINT DEFAULT NULL COMMENT '归属校区' AFTER `status`;
ALTER TABLE `classes` ADD COLUMN `campus_id` BIGINT DEFAULT NULL COMMENT '归属校区' AFTER `status`;
ALTER TABLE `courses` ADD COLUMN `campus_id` BIGINT DEFAULT NULL COMMENT '归属校区(NULL=共享)' AFTER `status`;
ALTER TABLE `schedules` ADD COLUMN `campus_id` BIGINT DEFAULT NULL COMMENT '归属校区' AFTER `status`;

-- 种子校区数据
INSERT IGNORE INTO `campuses` (`id`, `campus_name`, `address`, `phone`, `principal_id`) VALUES
(1, '海淀总校', '北京市海淀区中关村大街1号', '010-66668888', 2),
(2, '朝阳分校', '北京市朝阳区望京SOHO A座', '010-88886666', NULL),
(3, '西城分校', '北京市西城区金融街10号', '010-99998888', NULL);

-- 更新已有数据归属校区
UPDATE `teachers` SET `campus_id` = 1;
UPDATE `classrooms` SET `campus_id` = 1;
UPDATE `students` SET `campus_id` = 1;
UPDATE `classes` SET `campus_id` = 1;
UPDATE `courses` SET `campus_id` = 1;
UPDATE `schedules` SET `campus_id` = 1;

-- 给用户2(校长)分配校区1
UPDATE `campuses` SET `principal_id` = 2 WHERE `id` = 1;
