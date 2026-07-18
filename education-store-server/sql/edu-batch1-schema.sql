-- =====================================================
-- 新艺智源管理平台 — Batch 1 数据模型
-- 数据库: edu_system
-- 模块: N02 学员管理 + N03 教务基础资源
-- 表前缀: edu_
-- 引擎: InnoDB | 字符集: utf8mb4
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- Part 1: N03 教务基础资源（无业务依赖，先建）
-- =====================================================

-- 1. 科目表
DROP TABLE IF EXISTS edu_subject;
CREATE TABLE edu_subject (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '科目ID',
    name            VARCHAR(30)     NOT NULL                 COMMENT '科目名称',
    sort_order      INT             NOT NULL DEFAULT 0       COMMENT '排序号',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态(1启用 0停用)',
    description     VARCHAR(255)                             COMMENT '描述',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name),
    KEY idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='科目表';


-- 2. 课程表
DROP TABLE IF EXISTS edu_course;
CREATE TABLE edu_course (
    id                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '课程ID',
    name              VARCHAR(50)     NOT NULL                 COMMENT '课程名称',
    code              VARCHAR(30)     NOT NULL                 COMMENT '课程编号（全局唯一）',
    subject_id        BIGINT          NOT NULL                 COMMENT '所属科目ID',
    course_type       VARCHAR(20)     NOT NULL DEFAULT 'BATCH' COMMENT '课程类型(BATCH班课/ONE_ON_ONE一对一/SMALL_GROUP小班)',
    price             DECIMAL(10,2)   NOT NULL DEFAULT 0.00    COMMENT '单价(元)',
    applicable_grade  VARCHAR(32)                              COMMENT '适用年级编码(NULL=不限)',
    cover_image       VARCHAR(256)    DEFAULT ''               COMMENT '封面图URL',
    status            TINYINT         NOT NULL DEFAULT 1       COMMENT '状态(1上架 0下架)',
    sort_order        INT             NOT NULL DEFAULT 0       COMMENT '排序号',
    description       TEXT                                     COMMENT '课程介绍',
    create_by         VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by         VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted           TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    UNIQUE KEY uk_name_subject (subject_id, name),
    KEY idx_subject_id (subject_id),
    KEY idx_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程表';


-- 3. 教室表
DROP TABLE IF EXISTS edu_classroom;
CREATE TABLE edu_classroom (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '教室ID',
    name            VARCHAR(50)     NOT NULL                 COMMENT '教室名称',
    capacity        INT             NOT NULL DEFAULT 30      COMMENT '容纳人数(1-999, 0=不限)',
    location        VARCHAR(200)                             COMMENT '位置描述',
    facilities      VARCHAR(500)                             COMMENT '设施配置(JSON数组字符串)',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态(1启用 0停用)',
    description     VARCHAR(255)                             COMMENT '描述',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教室表';


-- =====================================================
-- Part 2: N02 学员管理核心（依赖 sys_user）
-- =====================================================

-- 4. 在学学员表
DROP TABLE IF EXISTS edu_student;
CREATE TABLE edu_student (
    id                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '学员ID',
    name              VARCHAR(20)     NOT NULL                 COMMENT '学员姓名',
    gender            CHAR(1)         DEFAULT 'M'              COMMENT '性别(M男 F女)',
    birth_date        DATE                                     COMMENT '出生日期（年龄由此派生）',
    grade             VARCHAR(20)                              COMMENT '年级编码',
    school            VARCHAR(50)     DEFAULT ''               COMMENT '就读学校',
    mobile            VARCHAR(20)     NOT NULL                 COMMENT '手机号',
    counselor_id      BIGINT                                   COMMENT '顾问ID(关联sys_user)',
    source            VARCHAR(20)                              COMMENT '来源渠道(DIRECT_MARKETING/REFERRAL/ONLINE/OTHER)',
    tags              VARCHAR(255)    DEFAULT ''               COMMENT '标签(逗号分隔)',
    status            VARCHAR(20)     NOT NULL DEFAULT 'ENROLLED' COMMENT '状态(ENROLLED/SUSPENDED/COMPLETED/LOST)',
    total_hours       DECIMAL(10,1)   NOT NULL DEFAULT 0.0     COMMENT '累计获得总课时(冗余缓存)',
    remaining_hours   DECIMAL(10,1)   NOT NULL DEFAULT 0.0     COMMENT '剩余课时汇总(冗余缓存)',
    last_visit_time   DATETIME                                 COMMENT '最近到校时间(冗余缓存)',
    completed_at      DATETIME                                 COMMENT '结业时间',
    remark            VARCHAR(500)                             COMMENT '备注',
    create_by         VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by         VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted           TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_mobile_deleted (mobile, deleted),
    KEY idx_name (name),
    KEY idx_grade (grade),
    KEY idx_status (status),
    KEY idx_counselor_id (counselor_id),
    KEY idx_source (source),
    KEY idx_last_visit (last_visit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='在学学员表';


-- 5. 家长账号关联表
DROP TABLE IF EXISTS edu_student_parent;
CREATE TABLE edu_student_parent (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学员ID',
    user_id         BIGINT                                   COMMENT '关联系统用户ID(家长登录账号，可为空)',
    name            VARCHAR(20)     NOT NULL                 COMMENT '家长姓名',
    relation        VARCHAR(20)     NOT NULL                 COMMENT '亲属关系(FATHER/MOTHER/GRANDFATHER/GRANDMOTHER/GRANDFATHER_M/GRANDMOTHER_M/OTHER)',
    mobile          VARCHAR(20)     NOT NULL                 COMMENT '家长手机号',
    sort            INT             NOT NULL DEFAULT 0       COMMENT '排序(同学员多家长时决定展示顺序)',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_mobile (student_id, mobile),
    KEY idx_user_id (user_id),
    KEY idx_mobile (mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家长账号关联表';


-- =====================================================
-- Part 3: N03 班级相关（依赖 edu_course + edu_classroom + sys_user + edu_student）
-- =====================================================

-- 6. 班级表
DROP TABLE IF EXISTS edu_class;
CREATE TABLE edu_class (
    id                BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '班级ID',
    name              VARCHAR(50)     NOT NULL                 COMMENT '班级名称',
    code              VARCHAR(32)                              COMMENT '班级编号(CLS+yyyyMMdd+4位序号，唯一)',
    course_id         BIGINT          NOT NULL                 COMMENT '关联课程ID',
    teacher_id        BIGINT          NOT NULL                 COMMENT '授课教师ID(关联sys_user)',
    classroom_id      BIGINT                                   COMMENT '默认教室ID(可空)',
    max_students      INT             NOT NULL DEFAULT 20      COMMENT '最大学员数',
    current_count     INT             NOT NULL DEFAULT 0       COMMENT '当前在读人数(冗余缓存)',
    status            VARCHAR(20)     NOT NULL DEFAULT 'RECRUITING' COMMENT '状态(RECRUITING/FULL/SUSPENDED/FINISHED/CANCELLED)',
    start_date        DATE                                     COMMENT '开课日期',
    end_date          DATE                                     COMMENT '结课日期',
    description       VARCHAR(500)                             COMMENT '班级说明',
    create_by         VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by         VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted           TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_course_id (course_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_classroom_id (classroom_id),
    KEY idx_status (status),
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级表';


-- 7. 班级排课规则表（与班级1:1关联）
DROP TABLE IF EXISTS edu_class_schedule_rule;
CREATE TABLE edu_class_schedule_rule (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    class_id        BIGINT          NOT NULL                 COMMENT '班级ID(UNIQUE, 1:1)',
    week_days       VARCHAR(20)     NOT NULL                 COMMENT '每周上课日(逗号分隔, 1=周一, 7=周日)',
    start_time      TIME            NOT NULL                 COMMENT '上课开始时间',
    end_time        TIME            NOT NULL                 COMMENT '上课结束时间',
    duration        INT             NOT NULL DEFAULT 45      COMMENT '每节课时长(分钟)',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级排课规则表(1:1)';


-- 8. 班级学员关联表（N02↔N03 跨模块桥接）
DROP TABLE IF EXISTS edu_class_student;
CREATE TABLE edu_class_student (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    class_id        BIGINT          NOT NULL                 COMMENT '班级ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学员ID',
    join_time       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ENROLLED' COMMENT '在班状态(ENROLLED/SUSPENDED/EXITED)',
    remark          VARCHAR(200)                             COMMENT '备注',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_class_student (class_id, student_id),
    KEY idx_student_id (student_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级学员关联表(跨模块桥接)';


-- =====================================================
-- Part 4: N02 课时日志 + 意向学员
-- =====================================================

-- 9. 课时变动明细日志（只追加，不修改不删除）
DROP TABLE IF EXISTS edu_student_hours_log;
CREATE TABLE edu_student_hours_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学员ID',
    course_id       BIGINT                                   COMMENT '关联课程ID(赠送/手动调整时可为空)',
    enrollment_id   BIGINT                                   COMMENT '关联报名记录ID(Batch 2预留)',
    class_id        BIGINT                                   COMMENT '关联班级ID',
    change_type     VARCHAR(20)     NOT NULL                 COMMENT '变动类型(ENROLL_GAIN/CONSUME/LEAVE_RETURN/MAKEUP_CONSUME/REFUND_DEDUCT/GIFT_GAIN/MANUAL_ADJUST)',
    change_amount   DECIMAL(10,1)   NOT NULL                 COMMENT '变动数量(正=增加, 负=减少)',
    balance_after   DECIMAL(10,1)   NOT NULL                 COMMENT '变动后剩余课时',
    source_type     VARCHAR(30)                              COMMENT '来源业务类型(ENROLLMENT/TUITION/ATTENDANCE/LEAVE/GIFT/MANUAL)',
    source_id       BIGINT                                   COMMENT '来源业务记录ID',
    operator_id     BIGINT                                   COMMENT '经手人ID(关联sys_user)',
    remark          VARCHAR(500)                             COMMENT '备注',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(变动发生时间)',
    PRIMARY KEY (id),
    KEY idx_student_time (student_id, create_time),
    KEY idx_student_course (student_id, course_id),
    KEY idx_course_id (course_id),
    KEY idx_enrollment_id (enrollment_id),
    KEY idx_source (source_type, source_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课时变动明细日志(只追加,不可修改/删除)';


-- 10. 意向学员表
DROP TABLE IF EXISTS edu_prospect;
CREATE TABLE edu_prospect (
    id                    BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '意向学员ID',
    name                  VARCHAR(20)     NOT NULL                 COMMENT '姓名',
    gender                CHAR(1)         DEFAULT 'U'              COMMENT '性别(M男 F女 U未知)',
    birth_date            DATE                                     COMMENT '出生日期',
    grade                 VARCHAR(20)                              COMMENT '年级编码',
    school                VARCHAR(50)     DEFAULT ''               COMMENT '就读学校',
    mobile                VARCHAR(20)     NOT NULL                 COMMENT '联系电话',
    parent_name           VARCHAR(20)     NOT NULL                 COMMENT '家长姓名',
    parent_relation       VARCHAR(20)     NOT NULL                 COMMENT '亲属关系',
    parent_mobile         VARCHAR(20)     NOT NULL                 COMMENT '家长手机号',
    wechat_nickname       VARCHAR(50)     DEFAULT ''               COMMENT '微信昵称',
    intended_course_id    BIGINT                                   COMMENT '意向课程ID',
    interest_level        VARCHAR(10)     DEFAULT 'MEDIUM'         COMMENT '意向等级(HIGH/MEDIUM/LOW)',
    source                VARCHAR(20)     NOT NULL DEFAULT 'OTHER' COMMENT '来源渠道(DIRECT_MARKETING/REFERRAL/ONLINE/OTHER)',
    counselor_id          BIGINT                                   COMMENT '顾问ID(关联sys_user)',
    follow_status         VARCHAR(20)     NOT NULL DEFAULT 'NEW'   COMMENT '跟进状态(NEW/CONTACTED/TRIAL/CONVERTED/LOST)',
    trial_lesson_count    INT             NOT NULL DEFAULT 0       COMMENT '体验课次数(由排课/考勤自动累加)',
    last_followup_date    DATETIME                                 COMMENT '最近跟进日期(新增跟进时自动更新)',
    converted_student_id  BIGINT                                   COMMENT '转化后在学学员ID(转化后回填)',
    remark                VARCHAR(500)                             COMMENT '备注',
    create_by             VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time           DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by             VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted               TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_mobile_deleted (mobile, deleted),
    KEY idx_name_mobile (name, mobile),
    KEY idx_follow_status (follow_status),
    KEY idx_counselor_id (counselor_id),
    KEY idx_intended_course (intended_course_id),
    KEY idx_source (source),
    KEY idx_last_followup (last_followup_date),
    KEY idx_converted_student (converted_student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='意向学员表';


-- 11. 意向学员跟进记录表
DROP TABLE IF EXISTS edu_prospect_followup;
CREATE TABLE edu_prospect_followup (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '跟进记录ID',
    prospect_id     BIGINT          NOT NULL                 COMMENT '意向学员ID',
    method          VARCHAR(20)     NOT NULL                 COMMENT '跟进方式(PHONE/WECHAT/FACE_TO_FACE)',
    content         VARCHAR(1000)   NOT NULL                 COMMENT '跟进内容',
    followup_date   DATETIME        NOT NULL                 COMMENT '跟进时间(支持补录)',
    next_plan       VARCHAR(500)                             COMMENT '下次跟进计划',
    operator_id     BIGINT          NOT NULL                 COMMENT '跟进人ID(关联sys_user)',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_prospect_time (prospect_id, followup_date),
    KEY idx_operator_id (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='意向学员跟进记录表';


-- 12. 课件资料表
DROP TABLE IF EXISTS edu_resource;
CREATE TABLE edu_resource (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '课件ID',
    name            VARCHAR(100)    NOT NULL                 COMMENT '课件名称',
    subject_id      BIGINT                                   COMMENT '关联科目ID',
    grade           VARCHAR(32)                              COMMENT '适用年级',
    resource_type   VARCHAR(20)     NOT NULL                 COMMENT '资源类型(DOCUMENT/PPT/EXERCISE/OTHER)',
    file_name       VARCHAR(255)    NOT NULL                 COMMENT '原始文件名',
    file_path       VARCHAR(500)    NOT NULL                 COMMENT '存储路径',
    file_url        VARCHAR(500)    NOT NULL                 COMMENT '访问URL',
    file_size       BIGINT          NOT NULL DEFAULT 0       COMMENT '文件大小(字节)',
    file_type       VARCHAR(50)     DEFAULT ''               COMMENT 'MIME类型',
    is_public       TINYINT         NOT NULL DEFAULT 1       COMMENT '共享范围(1公开 0私有)',
    upload_by       BIGINT          NOT NULL                 COMMENT '上传人ID(关联sys_user)',
    download_count  INT             NOT NULL DEFAULT 0       COMMENT '下载次数',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态(1正常 0禁用)',
    description     VARCHAR(500)                             COMMENT '描述',
    create_by       VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_subject_id (subject_id),
    KEY idx_upload_by (upload_by),
    KEY idx_resource_type (resource_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课件资料表';


SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Batch 1 建表完成，共 12 张表
-- =====================================================
-- 建表顺序依赖:
--   1. edu_subject           (无依赖)
--   2. edu_course            (→ edu_subject)
--   3. edu_classroom         (无依赖)
--   4. edu_student           (→ sys_user)
--   5. edu_student_parent    (→ edu_student, sys_user)
--   6. edu_class             (→ edu_course, sys_user, edu_classroom)
--   7. edu_class_schedule_rule (→ edu_class, 1:1)
--   8. edu_class_student     (→ edu_class, edu_student) ★跨模块桥接
--   9. edu_student_hours_log (→ edu_student, edu_course) ★日志型
--  10. edu_prospect         (→ edu_course, sys_user)
--  11. edu_prospect_followup (→ edu_prospect, sys_user)
--  12. edu_resource         (→ edu_subject, sys_user)
--
-- 跨模块关系:
--   N02(学员) ←── edu_class_student ──→ N03(班级)
--   N02(学员) ←── edu_student_hours_log ──→ N03(课程)
--   N02(意向) ←── edu_prospect.intended_course_id ──→ N03(课程)
--   N02(学员) ←── edu_student_hours_log.enrollment_id ──→ N06(报名, Batch 2)
-- =====================================================
