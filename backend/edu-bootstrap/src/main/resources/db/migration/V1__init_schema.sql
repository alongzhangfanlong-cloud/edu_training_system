-- =====================================================
-- 教育培训管理系统 - 数据库初始化脚本
-- Database: edu_training_db
-- Charset: utf8mb4
-- =====================================================

-- 1. 学员表
CREATE TABLE IF NOT EXISTS `students` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_no` VARCHAR(32) NOT NULL COMMENT '学员编号 STU20240001',
    `name` VARCHAR(50) NOT NULL COMMENT '学员姓名',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级 如:初二',
    `school` VARCHAR(100) DEFAULT NULL COMMENT '就读学校',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '学员手机号',
    `tags` JSON DEFAULT NULL COMMENT '标签 ["VIP","数学薄弱"]',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-意向 1-在读 2-停课 3-结课 4-流失',
    `source` VARCHAR(50) DEFAULT NULL COMMENT '来源渠道',
    `consultant_id` BIGINT DEFAULT NULL COMMENT '顾问ID',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0-正常 1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员表';

-- 2. 学员家长关系表
CREATE TABLE IF NOT EXISTS `student_parents` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `parent_name` VARCHAR(50) NOT NULL COMMENT '家长姓名',
    `relation` VARCHAR(20) DEFAULT NULL COMMENT '关系 父亲/母亲/其他',
    `phone` VARCHAR(20) NOT NULL COMMENT '家长手机号',
    `is_primary` TINYINT DEFAULT 0 COMMENT '是否主联系人 0-否 1-是',
    `wx_openid` VARCHAR(100) DEFAULT NULL COMMENT '微信openid',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员家长关系表';

-- 3. 教师表
CREATE TABLE IF NOT EXISTS `teachers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `teacher_no` VARCHAR(32) NOT NULL COMMENT '教师编号',
    `name` VARCHAR(50) NOT NULL COMMENT '教师姓名',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    `subjects` JSON DEFAULT NULL COMMENT '可授科目 ["数学","物理"]',
    `introduction` TEXT DEFAULT NULL COMMENT '教师简介',
    `hire_date` DATE DEFAULT NULL COMMENT '入职日期',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-离职 1-在职 2-休假',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_no` (`teacher_no`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

-- 4. 教室表
CREATE TABLE IF NOT EXISTS `classrooms` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '教室名称',
    `capacity` INT DEFAULT 30 COMMENT '容纳人数',
    `location` VARCHAR(255) DEFAULT NULL COMMENT '位置描述',
    `latitude` DECIMAL(10,8) DEFAULT NULL COMMENT '纬度',
    `longitude` DECIMAL(11,8) DEFAULT NULL COMMENT '经度',
    `facilities` JSON DEFAULT NULL COMMENT '设施 ["投影仪","钢琴"]',
    `status` TINYINT DEFAULT 1 COMMENT '0-停用 1-正常',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教室表';

-- 5. 课程表
CREATE TABLE IF NOT EXISTS `courses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `code` VARCHAR(50) NOT NULL COMMENT '课程编码',
    `type` TINYINT NOT NULL COMMENT '类型 1-班课 2-一对一 3-小班',
    `subject` VARCHAR(50) DEFAULT NULL COMMENT '科目',
    `grade_range` VARCHAR(50) DEFAULT NULL COMMENT '适用年级范围',
    `duration` INT DEFAULT 120 COMMENT '单次课时分钟数',
    `unit_price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价(元/课时)',
    `description` TEXT DEFAULT NULL COMMENT '课程介绍',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    `status` TINYINT DEFAULT 1 COMMENT '0-下架 1-上架',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_type` (`type`),
    KEY `idx_subject` (`subject`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 6. 班级表
CREATE TABLE IF NOT EXISTS `classes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `teacher_id` BIGINT NOT NULL COMMENT '主教老师ID',
    `assistant_id` BIGINT DEFAULT NULL COMMENT '助教老师ID',
    `classroom_id` BIGINT DEFAULT NULL COMMENT '默认教室ID',
    `name` VARCHAR(100) NOT NULL COMMENT '班级名称',
    `max_students` INT DEFAULT 20 COMMENT '最大人数',
    `current_students` INT DEFAULT 0 COMMENT '当前人数',
    `schedule_rule` JSON DEFAULT NULL COMMENT '排课规则',
    `start_date` DATE DEFAULT NULL COMMENT '开班日期',
    `end_date` DATE DEFAULT NULL COMMENT '结课日期',
    `status` TINYINT DEFAULT 1 COMMENT '0-未开班 1-进行中 2-已结课',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 7. 课表（排课结果）
CREATE TABLE IF NOT EXISTS `schedules` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `classroom_id` BIGINT DEFAULT NULL COMMENT '教室ID',
    `date` DATE NOT NULL COMMENT '上课日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `lesson_no` INT DEFAULT NULL COMMENT '第几课次',
    `status` TINYINT DEFAULT 1 COMMENT '0-取消 1-正常 2-调课 3-补课 4-停课',
    `original_schedule_id` BIGINT DEFAULT NULL COMMENT '原课表ID（调课/补课时）',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_time` (`teacher_id`, `date`, `start_time`),
    UNIQUE KEY `uk_classroom_time` (`classroom_id`, `date`, `start_time`),
    KEY `idx_class_id_date` (`class_id`, `date`),
    KEY `idx_teacher_id_date` (`teacher_id`, `date`),
    KEY `idx_classroom_id_date` (`classroom_id`, `date`),
    KEY `idx_date` (`date`),
    KEY `idx_status` (`status`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课表（排课结果）';

-- 8. 学员班级关系（报名记录）
CREATE TABLE IF NOT EXISTS `student_classes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    `total_hours` DECIMAL(6,1) NOT NULL COMMENT '总课时',
    `used_hours` DECIMAL(6,1) DEFAULT 0 COMMENT '已用课时',
    `remain_hours` DECIMAL(6,1) DEFAULT NULL COMMENT '剩余课时',
    `gift_hours` DECIMAL(6,1) DEFAULT 0 COMMENT '赠送课时',
    `expire_date` DATE DEFAULT NULL COMMENT '有效期',
    `status` TINYINT DEFAULT 1 COMMENT '1-正常 2-停课 3-结课 4-转班',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_status` (`status`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员班级关系（报名记录）';

-- 9. 考勤记录表
CREATE TABLE IF NOT EXISTS `attendances` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `schedule_id` BIGINT NOT NULL COMMENT '课表ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `status` TINYINT NOT NULL COMMENT '1-出勤 2-请假 3-旷课 4-补课',
    `check_in_time` TIMESTAMP NULL DEFAULT NULL COMMENT '签到时间',
    `check_in_method` TINYINT DEFAULT NULL COMMENT '1-扫码 2-教师点名 3-人脸 4-蓝牙 5-补签',
    `check_in_lat` DECIMAL(10,8) DEFAULT NULL COMMENT '签到纬度',
    `check_in_lng` DECIMAL(11,8) DEFAULT NULL COMMENT '签到经度',
    `leave_type` TINYINT DEFAULT NULL COMMENT '请假类型 1-病假 2-事假 3-其他',
    `leave_reason` VARCHAR(255) DEFAULT NULL COMMENT '请假原因',
    `leave_apply_time` TIMESTAMP NULL DEFAULT NULL COMMENT '请假申请时间',
    `leave_approve_status` TINYINT DEFAULT NULL COMMENT '0-待审批 1-通过 2-拒绝',
    `leave_approve_time` TIMESTAMP NULL DEFAULT NULL COMMENT '审批时间',
    `consume_hours` DECIMAL(3,1) DEFAULT NULL COMMENT '本次消课课时',
    `is_makeup` TINYINT DEFAULT 0 COMMENT '是否补课 0-否 1-是',
    `original_attendance_id` BIGINT DEFAULT NULL COMMENT '原考勤ID（补课时关联）',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_schedule_id` (`schedule_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_schedule_student` (`schedule_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤记录表';

-- 10. 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `parent_id` BIGINT DEFAULT NULL COMMENT '下单家长ID',
    `type` TINYINT DEFAULT NULL COMMENT '1-新报 2-续费 3-扩科 4-补费',
    `total_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '订单总金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    `pay_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '实付金额',
    `pay_status` TINYINT DEFAULT 0 COMMENT '0-未支付 1-已支付 2-部分退款 3-全额退款',
    `pay_method` TINYINT DEFAULT NULL COMMENT '1-微信支付 2-支付宝 3-线下 4-余额',
    `pay_time` TIMESTAMP NULL DEFAULT NULL COMMENT '支付时间',
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '第三方支付流水号',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_pay_status` (`pay_status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 11. 订单明细表
CREATE TABLE IF NOT EXISTS `order_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `class_id` BIGINT DEFAULT NULL COMMENT '班级ID（班课）',
    `item_type` TINYINT DEFAULT NULL COMMENT '1-课时包 2-按期 3-按年 4-次卡',
    `hours` DECIMAL(6,1) DEFAULT NULL COMMENT '课时数',
    `unit_price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
    `total_price` DECIMAL(10,2) DEFAULT NULL COMMENT '小计',
    `gift_hours` DECIMAL(6,1) DEFAULT 0 COMMENT '赠送课时',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- 12. 课时流水表
CREATE TABLE IF NOT EXISTS `hour_transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `student_class_id` BIGINT NOT NULL COMMENT '学员班级关系ID',
    `type` TINYINT NOT NULL COMMENT '1-报名增加 2-上课消耗 3-请假返还 4-补课消耗 5-赠课 6-退课',
    `change_hours` DECIMAL(6,1) NOT NULL COMMENT '变动课时(正数增加/负数减少)',
    `before_hours` DECIMAL(6,1) DEFAULT NULL COMMENT '变动前',
    `after_hours` DECIMAL(6,1) DEFAULT NULL COMMENT '变动后',
    `schedule_id` BIGINT DEFAULT NULL COMMENT '关联课表ID',
    `attendance_id` BIGINT DEFAULT NULL COMMENT '关联考勤ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_student_class_id` (`student_class_id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课时流水表';

-- 13. 课堂反馈表
CREATE TABLE IF NOT EXISTS `class_feedbacks` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `schedule_id` BIGINT NOT NULL COMMENT '课表ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `content` TEXT DEFAULT NULL COMMENT '教学内容',
    `homework` TEXT DEFAULT NULL COMMENT '作业布置',
    `photos` JSON DEFAULT NULL COMMENT '课堂照片URL列表',
    `overall_comment` TEXT DEFAULT NULL COMMENT '整体评语',
    `next_preview` TEXT DEFAULT NULL COMMENT '下节课预告',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_schedule_id` (`schedule_id`),
    KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课堂反馈表';

-- 14. 学员个人课堂反馈
CREATE TABLE IF NOT EXISTS `student_feedbacks` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `schedule_id` BIGINT NOT NULL COMMENT '课表ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `performance_score` TINYINT DEFAULT NULL COMMENT '课堂表现评分 1-5',
    `performance_comment` TEXT DEFAULT NULL COMMENT '表现评语',
    `homework_status` TINYINT DEFAULT NULL COMMENT '作业状态 1-已完成 2-部分完成 3-未完成',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_schedule_student` (`schedule_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员个人课堂反馈';

-- 15. 系统配置表
CREATE TABLE IF NOT EXISTS `system_configs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT DEFAULT NULL COMMENT '配置值',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
