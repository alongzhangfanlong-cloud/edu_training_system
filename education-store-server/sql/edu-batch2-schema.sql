-- =====================================================
-- 新艺智源管理平台 — Batch 2 数据模型
-- 数据库: edu_system
-- 模块: N03 排课 + N06 财务 + N07 考勤
-- 表前缀: edu_
-- 引擎: InnoDB | 字符集: utf8mb4
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- Part 1: N03 排课系统（依赖 Batch 1 edu_class/edu_course/edu_classroom）
-- =====================================================

-- 1. 排课计划表
DROP TABLE IF EXISTS edu_schedule_plan;
CREATE TABLE edu_schedule_plan (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    class_ids       VARCHAR(500)    NOT NULL                 COMMENT '关联班级ID列表(JSON数组)',
    start_date      DATE            NOT NULL                 COMMENT '排课起始日期',
    end_date        DATE            NOT NULL                 COMMENT '排课结束日期',
    status          VARCHAR(20)     NOT NULL DEFAULT 'UNGENERATED' COMMENT '状态(UNGENERATED/GENERATED)',
    conflict_detail TEXT                                     COMMENT '冲突详情JSON',
    remark          VARCHAR(200)                             COMMENT '备注',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='排课计划表';


-- 2. 排课记录表
DROP TABLE IF EXISTS edu_schedule;
CREATE TABLE edu_schedule (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    plan_id             BIGINT                                   COMMENT '关联排课计划ID(单次排课可为空)',
    class_id            BIGINT          NOT NULL                 COMMENT '班级ID → edu_class.id',
    course_id           BIGINT          NOT NULL                 COMMENT '课程ID → edu_course.id(冗余,方便查询)',
    teacher_id          BIGINT          NOT NULL                 COMMENT '授课教师ID → sys_user.id',
    room_id             BIGINT                                   COMMENT '教室ID → edu_classroom.id',
    schedule_date       DATE            NOT NULL                 COMMENT '上课日期',
    start_time          TIME            NOT NULL                 COMMENT '上课开始时间',
    end_time            TIME            NOT NULL                 COMMENT '上课结束时间',
    schedule_type       VARCHAR(20)     NOT NULL DEFAULT 'NORMAL' COMMENT '排课类型(NORMAL/TRIAL/MAKEUP)',
    status              VARCHAR(20)     NOT NULL DEFAULT 'NORMAL' COMMENT '状态(NORMAL/CANCELLED/RESCHEDULED/MAKEUP/SUSPENDED/SUBSTITUTE)',
    force_flag          TINYINT         NOT NULL DEFAULT 0       COMMENT '是否强制排课(0否 1是)',
    force_reason        VARCHAR(500)                             COMMENT '强制排课冲突摘要',
    cancel_reason       VARCHAR(200)                             COMMENT '取消原因',
    cancel_time         DATETIME                                 COMMENT '取消时间',
    cancel_by           VARCHAR(64)                              COMMENT '取消人',
    rescheduled_from_id BIGINT                                   COMMENT '调课来源排课ID',
    rescheduled_to_id   BIGINT                                   COMMENT '调课目标排课ID',
    consumed            TINYINT         NOT NULL DEFAULT 0       COMMENT '是否已消课(0未消 1已消)',
    consume_time        DATETIME                                 COMMENT '消课时间',
    consume_by          VARCHAR(64)                              COMMENT '消课操作人',
    remark              VARCHAR(200)                             COMMENT '备注',
    create_by           VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time         DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_class_date (class_id, schedule_date),
    KEY idx_teacher_date (teacher_id, schedule_date),
    KEY idx_room_date (room_id, schedule_date),
    KEY idx_date (schedule_date),
    KEY idx_status (status),
    KEY idx_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='排课记录表';


-- =====================================================
-- Part 2: N06 财务管理（依赖 Batch 1 edu_student/edu_course/edu_class）
-- =====================================================

-- 3. 报名记录表
DROP TABLE IF EXISTS edu_enrollment;
CREATE TABLE edu_enrollment (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学员ID → edu_student.id',
    course_id       BIGINT          NOT NULL                 COMMENT '课程ID → edu_course.id',
    class_id        BIGINT                                   COMMENT '班级ID → edu_class.id(可为空)',
    amount          DECIMAL(10,2)   NOT NULL                 COMMENT '报名应缴金额',
    handler_id      BIGINT          NOT NULL                 COMMENT '经手人ID → sys_user.id',
    enrollment_date DATE            NOT NULL                 COMMENT '报名日期',
    payment_status      VARCHAR(20)     NOT NULL DEFAULT 'PENDING' COMMENT '收费状态(DRAFT/PENDING/PARTIAL/PAID/REFUNDED/CANCELLED)',
    tuition_record_id   BIGINT                                   COMMENT '关联首条收费记录ID → edu_tuition_fee.id',
    remark              VARCHAR(200)                             COMMENT '备注',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_course_id (course_id),
    KEY idx_payment_status (payment_status),
    KEY idx_enrollment_date (enrollment_date),
    KEY idx_handler_id (handler_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报名记录表';


-- 4. 收费记录表
DROP TABLE IF EXISTS edu_tuition_fee;
CREATE TABLE edu_tuition_fee (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    fee_no              VARCHAR(32)     NOT NULL                 COMMENT '收费编号(T+yyyyMMdd+4位流水)，UNIQUE',
    enrollment_id       BIGINT                                   COMMENT '关联报名记录ID',
    student_id          BIGINT          NOT NULL                 COMMENT '学员ID → edu_student.id',
    course_id           BIGINT          NOT NULL                 COMMENT '课程ID → edu_course.id',
    class_id            BIGINT                                   COMMENT '班级ID → edu_class.id(可为空)',
    fee_type            VARCHAR(32)     NOT NULL                 COMMENT '费用类型(REGISTRATION/TUITION/MATERIAL/OTHER)',
    amount_receivable   DECIMAL(10,2)   NOT NULL                 COMMENT '应缴金额(继承自报名记录)',
    amount_paid         DECIMAL(10,2)   NOT NULL                 COMMENT '本次实缴金额',
    payment_method      VARCHAR(32)     NOT NULL                 COMMENT '支付方式(CASH/WECHAT/ALIPAY/BANK_TRANSFER)',
    status              VARCHAR(32)     NOT NULL DEFAULT 'PAID'  COMMENT '状态(PAID/REFUNDED/PARTIAL_REFUNDED)',
    remark              VARCHAR(500)                             COMMENT '备注',
    operator_id         BIGINT          NOT NULL                 COMMENT '经手人ID → sys_user.id',
    create_time         DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '收费时间',
    update_by           VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_fee_no (fee_no),
    KEY idx_enrollment_id (enrollment_id),
    KEY idx_student_id (student_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收费记录表';


-- 5. 退费记录表
DROP TABLE IF EXISTS edu_tuition_refund;
CREATE TABLE edu_tuition_refund (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    refund_no       VARCHAR(32)     NOT NULL                 COMMENT '退费编号(R+yyyyMMdd+4位流水)，UNIQUE',
    fee_id          BIGINT          NOT NULL                 COMMENT '关联收费记录ID → edu_tuition_fee.id',
    student_id      BIGINT          NOT NULL                 COMMENT '学员ID → edu_student.id',
    course_id       BIGINT          NOT NULL                 COMMENT '课程ID → edu_course.id',
    amount          DECIMAL(10,2)   NOT NULL                 COMMENT '退费金额',
    reason          VARCHAR(32)     NOT NULL                 COMMENT '退费原因编码',
    reason_custom   VARCHAR(200)                             COMMENT '自定义退费原因',
    refund_method   VARCHAR(32)     NOT NULL                 COMMENT '退费方式(ORIGINAL/CASH/WECHAT/ALIPAY/BANK_TRANSFER)',
    status          VARCHAR(32)     NOT NULL DEFAULT 'COMPLETED' COMMENT '状态(PENDING_APPROVAL/APPROVED/REJECTED/COMPLETED)',
    hours_deducted  INT             NOT NULL DEFAULT 0       COMMENT '扣回课时数',
    applicant_id    BIGINT          NOT NULL                 COMMENT '申请人ID → sys_user.id',
    approver_id     BIGINT                                   COMMENT '审批人ID → sys_user.id(可为空)',
    approval_time   DATETIME                                 COMMENT '审批时间',
    reject_reason   VARCHAR(500)                             COMMENT '驳回原因',
    remark          VARCHAR(500)                             COMMENT '备注',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_fee_id (fee_id),
    KEY idx_student_id (student_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退费记录表';


-- 6. 赠送课时记录表
DROP TABLE IF EXISTS edu_tuition_gift_hours;
CREATE TABLE edu_tuition_gift_hours (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    gift_no         VARCHAR(32)     NOT NULL                 COMMENT '赠送编号(G+yyyyMMdd+4位流水)，UNIQUE',
    student_id      BIGINT          NOT NULL                 COMMENT '学员ID → edu_student.id',
    course_id       BIGINT          NOT NULL                 COMMENT '关联课程ID → edu_course.id',
    hours           INT             NOT NULL                 COMMENT '赠送课时数',
    reason          VARCHAR(32)     NOT NULL                 COMMENT '赠送原因(PROMOTION/COMPENSATION/OTHER)',
    reason_custom   VARCHAR(200)                             COMMENT '自定义原因',
    remark          VARCHAR(500)                             COMMENT '备注',
    operator_id     BIGINT          NOT NULL                 COMMENT '经手人ID → sys_user.id',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '赠送时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_gift_no (gift_no),
    KEY idx_student_id (student_id),
    KEY idx_course_id (course_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='赠送课时记录表';


-- 7. 教师课酬单价表
DROP TABLE IF EXISTS edu_teacher_pay_rate;
CREATE TABLE edu_teacher_pay_rate (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    teacher_id      BIGINT          NOT NULL                 COMMENT '教师ID → sys_user.id',
    course_id       BIGINT          NOT NULL                 COMMENT '课程ID → edu_course.id',
    pay_per_lesson  DECIMAL(10,2)   NOT NULL                 COMMENT '单节课酬(元)',
    effective_from  DATE            NOT NULL                 COMMENT '生效起始日期',
    effective_to    DATE                                     COMMENT '生效截止日期(可为空=长期有效)',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_teacher_course_period (teacher_id, course_id, effective_from),
    KEY idx_teacher_id (teacher_id),
    KEY idx_course_id (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师课酬单价表';


-- 8. 教师课酬记录表
DROP TABLE IF EXISTS edu_teacher_payroll;
CREATE TABLE edu_teacher_payroll (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    teacher_id      BIGINT          NOT NULL                 COMMENT '教师ID → sys_user.id',
    month           VARCHAR(7)      NOT NULL                 COMMENT '月份(YYYY-MM)',
    course_id       BIGINT          NOT NULL                 COMMENT '课程ID → edu_course.id',
    lesson_count    INT             NOT NULL DEFAULT 0       COMMENT '消课数(人次)',
    pay_per_lesson  DECIMAL(10,2)   NOT NULL                 COMMENT '计算时锁定的单节课酬',
    total_pay       DECIMAL(10,2)   NOT NULL                 COMMENT '小计(lesson_count × pay_per_lesson)',
    status          VARCHAR(20)     NOT NULL DEFAULT 'DRAFT' COMMENT '状态(DRAFT/CONFIRMED/PAID)',
    confirmed_at    DATETIME                                 COMMENT '确认时间',
    confirmed_by    BIGINT                                   COMMENT '确认人ID → sys_user.id',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_teacher_month_course (teacher_id, month, course_id),
    KEY idx_month (month),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师课酬记录表';


-- =====================================================
-- Part 3: N07 考勤管理（依赖 edu_schedule + Batch1 edu_student/edu_class）
-- =====================================================

-- 9. 签到记录表
DROP TABLE IF EXISTS edu_attendance;
CREATE TABLE edu_attendance (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    schedule_id     BIGINT          NOT NULL                 COMMENT '排课记录ID → edu_schedule.id',
    class_id        BIGINT          NOT NULL                 COMMENT '班级ID → edu_class.id',
    student_id      BIGINT          NOT NULL                 COMMENT '学员ID → edu_student.id',
    status          TINYINT         NOT NULL DEFAULT 0       COMMENT '签到状态(0=到校 1=迟到 2=缺勤 3=请假 4=请假未审批)',
    signin_time     DATETIME                                 COMMENT '签到操作时间',
    operator_id     BIGINT                                   COMMENT '操作人ID → sys_user.id',
    operator_type   VARCHAR(20)                              COMMENT '操作端(PC/MINI_PROGRAM)',
    remark          VARCHAR(200)                             COMMENT '备注',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_schedule_student (schedule_id, student_id),
    KEY idx_class_id (class_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='签到记录表';


-- 10. 学生请假表
DROP TABLE IF EXISTS edu_leave_student;
CREATE TABLE edu_leave_student (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    student_id          BIGINT          NOT NULL                 COMMENT '学员ID → edu_student.id',
    leave_date          DATE            NOT NULL                 COMMENT '请假日期',
    leave_type          TINYINT         NOT NULL                 COMMENT '请假类型(0=事假 1=病假 2=其他)',
    reason              VARCHAR(500)    NOT NULL                 COMMENT '请假原因',
    status              TINYINT         NOT NULL DEFAULT 0       COMMENT '状态(0=待确认 1=已通过 2=已驳回 3=已撤销)',
    applicant_type      VARCHAR(20)     NOT NULL                 COMMENT '申请人类型(PARENT/TEACHER)',
    applicant_id        BIGINT          NOT NULL                 COMMENT '申请人ID → sys_user.id',
    confirm_teacher_id  BIGINT                                   COMMENT '确认教师ID → sys_user.id',
    confirm_time        DATETIME                                 COMMENT '确认时间',
    reject_reason       VARCHAR(200)                             COMMENT '驳回原因',
    auto_approved       TINYINT         NOT NULL DEFAULT 0       COMMENT '是否自动通过(0否 1是)',
    create_by           VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time         DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_student_date (student_id, leave_date),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生请假表';


-- 11. 教师请假表
DROP TABLE IF EXISTS edu_leave_teacher;
CREATE TABLE edu_leave_teacher (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    teacher_id          BIGINT          NOT NULL                 COMMENT '教师ID → sys_user.id',
    leave_start_date    DATE            NOT NULL                 COMMENT '请假开始日期',
    leave_end_date      DATE            NOT NULL                 COMMENT '请假结束日期(单天时同start)',
    leave_type          TINYINT         NOT NULL                 COMMENT '请假类型(0=事假 1=病假 2=其他)',
    reason              VARCHAR(500)    NOT NULL                 COMMENT '请假原因',
    status              TINYINT         NOT NULL DEFAULT 0       COMMENT '状态(0=待审批 1=已通过 2=已驳回 3=已处理 4=已撤销)',
    applicant_type      VARCHAR(20)     NOT NULL                 COMMENT '申请人类型(TEACHER/ADMIN)',
    applicant_id        BIGINT          NOT NULL                 COMMENT '申请人ID → sys_user.id',
    approver_id         BIGINT                                   COMMENT '审批人ID → sys_user.id',
    approve_time        DATETIME                                 COMMENT '审批时间',
    reject_reason       VARCHAR(200)                             COMMENT '驳回原因',
    create_by           VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time         DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_teacher_date (teacher_id, leave_start_date),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师请假表';


-- 12. 教师请假冲突排课处理表
DROP TABLE IF EXISTS edu_leave_conflict;
CREATE TABLE edu_leave_conflict (
    id                      BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    leave_id                BIGINT          NOT NULL                 COMMENT '关联教师请假ID → edu_leave_teacher.id',
    schedule_id             BIGINT          NOT NULL                 COMMENT '冲突排课记录ID → edu_schedule.id',
    resolve_type            TINYINT         NOT NULL                 COMMENT '处理方式(0=代课 1=调课 2=停课)',
    substitute_teacher_id   BIGINT                                   COMMENT '代课教师ID → sys_user.id(resolve_type=0时必填)',
    new_schedule_date       DATE                                     COMMENT '调课后新日期(resolve_type=1时必填)',
    new_time_slot           VARCHAR(50)                              COMMENT '调课后新时段(resolve_type=1时必填)',
    new_classroom_id        BIGINT                                   COMMENT '调课后新教室ID(resolve_type=1时可空)',
    status                  TINYINT         NOT NULL DEFAULT 0       COMMENT '处理状态(0=待处理 1=已处理)',
    create_by               VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time             DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by               VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time             DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted                 TINYINT         DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_leave_id (leave_id),
    KEY idx_schedule_id (schedule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师请假冲突排课处理表';


SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Batch 2 建表完成，共 12 张表
-- =====================================================
-- 建表顺序依赖:
--  Batch 1: edu_class, edu_course, edu_classroom, edu_student, sys_user 必须先存在
--   1. edu_schedule_plan     (→ edu_class via class_ids JSON)
--   2. edu_schedule          (→ edu_class, edu_course, edu_classroom, sys_user)
--   3. edu_enrollment        (→ edu_student, edu_course, edu_class, sys_user)
--   4. edu_tuition_fee       (→ edu_enrollment, edu_student, edu_course, sys_user)
--   5. edu_tuition_refund    (→ edu_tuition_fee, edu_student, edu_course, sys_user)
--   6. edu_tuition_gift_hours(→ edu_student, edu_course, sys_user)
--   7. edu_teacher_pay_rate  (→ sys_user, edu_course)
--   8. edu_teacher_payroll   (→ sys_user, edu_course)
--   9. edu_attendance        (→ edu_schedule, edu_class, edu_student, sys_user)
--  10. edu_leave_student     (→ edu_student, sys_user)
--  11. edu_leave_teacher     (→ sys_user)
--  12. edu_leave_conflict    (→ edu_leave_teacher, edu_schedule)
--
-- 跨模块关系:
--   N03(排课) ←── edu_schedule ──→ N07(考勤) via edu_attendance
--   N06(财务) ←── edu_tuition_fee/refund/gift ──→ N02(学员) via edu_student_hours_log
--   N06(财务) ←── edu_teacher_payroll ──→ N07(考勤) via 消课数据
--   N07(考勤) ←── edu_leave_student/teacher ──→ N02(学员)/N03(教师)
-- =====================================================
