-- =====================================================
-- 新艺智源管理平台 — 完整安装脚本
-- 生成时间: 2026-07-21 18:34:43
-- 用途: 首次全新安装，一键创建所有表结构和初始数据
-- 用法: mysql -uroot -proot < edu-system-setup.sql
-- =====================================================

-- =====================================================
-- 新艺智源管理平台 — 系统管理模块 Schema
-- 数据库: edu_system
-- 说明: 从 OA-Space 抽取，已去除 多公司 / 部门 / 岗位 / 数据权限
-- 表前缀: sys_
-- =====================================================

-- CREATE DATABASE IF NOT EXISTS edu_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- USE edu_system;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '用户ID',
    username        VARCHAR(64)  NOT NULL UNIQUE             COMMENT '登录账号',
    password        VARCHAR(256) NOT NULL                    COMMENT '密码(BCrypt)',
    real_name       VARCHAR(64)  DEFAULT ''                  COMMENT '真实姓名',
    nick_name       VARCHAR(64)  DEFAULT ''                  COMMENT '昵称',
    email           VARCHAR(128) DEFAULT ''                  COMMENT '邮箱',
    mobile          VARCHAR(20)  DEFAULT ''                  COMMENT '手机号',
    wx_openid       VARCHAR(64)  DEFAULT NULL  UNIQUE           COMMENT '微信OpenID(唯一标识)',
    gender          TINYINT      DEFAULT 0                   COMMENT '性别(0未知 1男 2女)',
    avatar          VARCHAR(256) DEFAULT ''                  COMMENT '头像URL',
    status          TINYINT      DEFAULT 1                   COMMENT '状态(1正常 0停用)',
    login_ip        VARCHAR(128) DEFAULT ''                  COMMENT '最后登录IP',
    login_date      DATETIME                                 COMMENT '最后登录时间',
    pwd_update_date DATETIME                                 COMMENT '密码最后更新时间',
    create_by       VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)                             COMMENT '备注',
    deleted         TINYINT       NOT NULL DEFAULT 0                   COMMENT '逻辑删除',
    INDEX idx_mobile (mobile),
    INDEX idx_email (email)
) COMMENT '用户信息表';

-- 菜单表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '菜单ID',
    parent_id   BIGINT       DEFAULT 0                   COMMENT '父菜单ID',
    name        VARCHAR(64)  NOT NULL                    COMMENT '菜单名称',
    type        CHAR(1)      DEFAULT 'C'                 COMMENT '类型(M目录 C菜单 F按钮)',
    path        VARCHAR(200) DEFAULT ''                  COMMENT '路由路径',
    component   VARCHAR(255)                             COMMENT '组件路径',
    route_name  VARCHAR(64)  DEFAULT ''                  COMMENT '路由名称',
    query       VARCHAR(255)                             COMMENT '路由参数',
    is_frame    TINYINT      DEFAULT 1                   COMMENT '是否外链(1是 0否)',
    is_cache    TINYINT      DEFAULT 0                   COMMENT '是否缓存(1是 0否)',
    icon        VARCHAR(64)  DEFAULT ''                  COMMENT '图标',
    permission  VARCHAR(128)                             COMMENT '权限标识',
    sort        INT          DEFAULT 0                   COMMENT '排序',
    visible     TINYINT      DEFAULT 1                   COMMENT '是否可见(1可见 0隐藏)',
    status      TINYINT      DEFAULT 1                   COMMENT '状态(1正常 0停用)',
    create_by   VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by   VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT       NOT NULL DEFAULT 0                   COMMENT '逻辑删除'
) COMMENT '菜单权限表';

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id                  BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '角色ID',
    name                VARCHAR(64)  NOT NULL                    COMMENT '角色名称',
    code                VARCHAR(64)  NOT NULL UNIQUE             COMMENT '角色编码',
    sort                INT          DEFAULT 0                   COMMENT '显示顺序',
    menu_check_strictly TINYINT      DEFAULT 1                   COMMENT '菜单树父子联动',
    status              TINYINT      DEFAULT 1                   COMMENT '状态(1正常 0停用)',
    remark              VARCHAR(256)                             COMMENT '备注',
    create_by           VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
    create_time         DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by           VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
    update_time         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT       NOT NULL DEFAULT 0                   COMMENT '逻辑删除'
) COMMENT '角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) COMMENT '用户角色关联表';

-- 角色菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) COMMENT '角色菜单关联表';

-- 系统参数配置表
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '参数ID',
    name        VARCHAR(128) DEFAULT ''                  COMMENT '参数名称',
    `key`       VARCHAR(128) DEFAULT '' UNIQUE           COMMENT '参数键名',
    `value`     VARCHAR(500) DEFAULT ''                  COMMENT '参数键值',
    type        CHAR(1)      DEFAULT 'N'                 COMMENT '系统内置(Y是 N否)',
    create_by   VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by   VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(256)                             COMMENT '备注',
    deleted     TINYINT       NOT NULL DEFAULT 0                   COMMENT '逻辑删除'
) COMMENT '系统参数配置表';

-- 字典类型表
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '字典ID',
    name        VARCHAR(128) DEFAULT ''                  COMMENT '字典名称',
    code        VARCHAR(64)  DEFAULT '' UNIQUE           COMMENT '字典编码',
    status      TINYINT      DEFAULT 1                   COMMENT '状态(1正常 0停用)',
    create_by   VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by   VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(256)                             COMMENT '备注',
    deleted     TINYINT       NOT NULL DEFAULT 0                   COMMENT '逻辑删除'
) COMMENT '字典类型表';

-- 字典数据表
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '字典数据ID',
    type_code   VARCHAR(64)  DEFAULT ''                  COMMENT '字典编码',
    label       VARCHAR(128) DEFAULT ''                  COMMENT '字典标签',
    value       VARCHAR(128) DEFAULT ''                  COMMENT '字典键值',
    sort        INT          DEFAULT 0                   COMMENT '显示顺序',
    css_class   VARCHAR(128)                             COMMENT '样式属性',
    list_class  VARCHAR(128)                             COMMENT '表格回显样式',
    is_default  TINYINT      DEFAULT 0                   COMMENT '是否默认(1是 0否)',
    status      TINYINT      DEFAULT 1                   COMMENT '状态(1正常 0停用)',
    create_by   VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by   VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(256)                             COMMENT '备注',
    deleted     TINYINT       NOT NULL DEFAULT 0                   COMMENT '逻辑删除',
    INDEX idx_type_code (type_code)
) COMMENT '字典数据表';

-- 通知公告表
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '公告ID',
    title        VARCHAR(256) NOT NULL                    COMMENT '公告标题',
    content      LONGTEXT                                 COMMENT '公告内容',
    type         VARCHAR(32)  DEFAULT '1'                 COMMENT '公告类型(1通知 2公告)',
    level        VARCHAR(32)  DEFAULT 'normal'            COMMENT '紧急程度(normal/important/urgent)',
    publish_by   BIGINT                                   COMMENT '发布人ID',
    publish_time DATETIME                                 COMMENT '发布时间',
    status       TINYINT      DEFAULT 0                   COMMENT '状态(1已发布 0草稿)',
    create_by    VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by    VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT       NOT NULL DEFAULT 0                   COMMENT '逻辑删除'
) COMMENT '通知公告表';

-- 操作日志表（无 deleted 列：实体不继承 BaseEntity）
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '日志ID',
    title           VARCHAR(64)  DEFAULT ''                  COMMENT '操作模块',
    business_type   VARCHAR(16)  DEFAULT ''                  COMMENT '业务类型(INSERT/UPDATE/DELETE/EXPORT等)',
    method          VARCHAR(256) DEFAULT ''                  COMMENT '方法名',
    request_method  VARCHAR(16)  DEFAULT ''                  COMMENT '请求方式',
    oper_name       VARCHAR(64)  DEFAULT ''                  COMMENT '操作人员',
    oper_url        VARCHAR(256) DEFAULT ''                  COMMENT '请求URL',
    oper_ip         VARCHAR(128) DEFAULT ''                  COMMENT '操作IP',
    oper_location   VARCHAR(256) DEFAULT ''                  COMMENT '操作地点',
    oper_param      TEXT                                     COMMENT '请求参数(JSON)',
    json_result     TEXT                                     COMMENT '返回结果(JSON)',
    status          TINYINT      DEFAULT 1                   COMMENT '状态(1成功 0失败)',
    error_msg       TEXT                                     COMMENT '错误信息',
    cost_time       BIGINT       DEFAULT 0                   COMMENT '耗时(ms)',
    oper_time       DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '操作时间',
    INDEX idx_oper_time (oper_time),
    INDEX idx_oper_name (oper_name)
) COMMENT '操作日志表';

-- 登录日志表（无 deleted 列：实体不继承 BaseEntity）
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '访问ID',
    username        VARCHAR(64)  DEFAULT ''                  COMMENT '登录账号',
    ipaddr          VARCHAR(128) DEFAULT ''                  COMMENT '登录IP',
    login_location  VARCHAR(256) DEFAULT ''                  COMMENT '登录地点',
    browser         VARCHAR(64)  DEFAULT ''                  COMMENT '浏览器类型',
    os              VARCHAR(64)  DEFAULT ''                  COMMENT '操作系统',
    status          TINYINT      DEFAULT 1                   COMMENT '状态(1成功 0失败)',
    msg             VARCHAR(256) DEFAULT ''                  COMMENT '提示信息',
    login_time      DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '登录时间',
    INDEX idx_login_time (login_time),
    INDEX idx_username (username)
) COMMENT '登录日志表';

-- 用户通知偏好设置表（微信订阅消息开关）
DROP TABLE IF EXISTS user_notification_setting;
CREATE TABLE user_notification_setting (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT       NOT NULL                    COMMENT '用户ID',
    notify_type     VARCHAR(32)  NOT NULL                    COMMENT '通知类型: SIGN_IN/EVALUATION/HOMEWORK/EXAM/LEAVE/REDEMPTION',
    enabled         TINYINT      DEFAULT 1                   COMMENT '是否开启(1开启 0关闭)',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_type (user_id, notify_type)
) COMMENT '用户通知偏好设置表';

SET FOREIGN_KEY_CHECKS = 1;

-- ========== edu-batch1-schema.sql ==========

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

-- ========== edu-batch2-schema.sql ==========

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
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted             TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted             TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted         TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted             TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted             TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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
    deleted                 TINYINT          NOT NULL DEFAULT 0                COMMENT '逻辑删除',
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

-- ========== edu-batch3a-schema.sql ==========

-- ============================================
-- Batch 3a: N04 积分商城 — 数据表定义
-- 数据库：MySQL 8.0 + InnoDB
-- 字符集：utf8mb4
-- 日期：2026-07-18
-- ============================================

-- 建表顺序：account → log → gift → redemption
-- gift 表在 redemption 之前（redemption 引用了 gift_id）

-- 1. 积分账户
CREATE TABLE edu_points_account (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID，关联 edu_student.id',
    total_earned    INT             NOT NULL DEFAULT 0       COMMENT '累计获取积分',
    total_spent     INT             NOT NULL DEFAULT 0       COMMENT '累计消费积分',
    balance         INT             NOT NULL DEFAULT 0       COMMENT '当前积分余额 (total_earned - total_spent)',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分账户';

-- 2. 积分日志（只追加，不修改不删除）
CREATE TABLE edu_points_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID',
    change_type     VARCHAR(20)     NOT NULL                 COMMENT '变动类型: EARN/REDEEM/EXPIRE/ADJUST',
    amount          INT             NOT NULL                 COMMENT '变动积分数，正数=获取，负数=消耗',
    balance_after   INT             NOT NULL                 COMMENT '变动后账户余额',
    source_type     VARCHAR(50)     NOT NULL                 COMMENT '来源类型: TEACHER/ADMIN/REDEEM/CANCEL_REDEEM/ADJUST',
    source_id       BIGINT          NULL                     COMMENT '来源业务记录ID（评价ID/兑换ID等）',
    remark          VARCHAR(200)    NULL                     COMMENT '备注说明',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_create_time (create_time),
    KEY idx_source (source_type, source_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分日志（只追加）';

-- 3. 积分礼品
CREATE TABLE edu_points_gift (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL                 COMMENT '礼品名称',
    image_url       VARCHAR(500)    NULL                     COMMENT '礼品图片URL',
    points_required INT             NOT NULL                 COMMENT '兑换所需积分',
    stock_total     INT             NOT NULL                 COMMENT '总库存数量',
    stock_remaining INT             NOT NULL                 COMMENT '剩余库存数量',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 1=启用, 0=禁用',
    description     VARCHAR(500)    NULL DEFAULT ''          COMMENT '礼品描述',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分礼品';

-- 4. 积分兑换记录
CREATE TABLE edu_points_redemption (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID',
    gift_id         BIGINT          NOT NULL                 COMMENT '礼品ID',
    points_spent    INT             NOT NULL                 COMMENT '消耗积分',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING_CLAIM' COMMENT '状态: PENDING_CLAIM/CLAIMED/CANCELLED',
    redeem_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '兑换时间',
    claim_time      DATETIME        NULL                     COMMENT '领取时间',
    cancel_time     DATETIME        NULL                     COMMENT '取消时间',
    operator_id     BIGINT          NULL                     COMMENT '操作人ID（核销/取消时的操作人员，关联 sys_user）',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_gift_id (gift_id),
    KEY idx_status (status),
    KEY idx_redeem_time (redeem_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分兑换记录';

-- ========== edu-batch3b-schema.sql ==========

-- ============================================
-- Batch 3b: N05 家校沟通 — 数据表定义
-- 数据库：MySQL 8.0 + InnoDB
-- 字符集：utf8mb4
-- 日期：2026-07-18
-- ============================================

-- 建表顺序：homework → attachment → view_log → evaluation → evaluation_student → teacher_evaluation → exam_score → exam_score_student

-- ============================================
-- N05.1 课后作业
-- ============================================

-- 1. 作业表
CREATE TABLE edu_homework (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    class_id        BIGINT          NOT NULL                 COMMENT '关联班级ID → edu_class.id',
    course_id       BIGINT          NOT NULL                 COMMENT '关联课程ID → edu_course.id',
    schedule_id     BIGINT          NULL                     COMMENT '关联排课记录ID → edu_schedule.id',
    title           VARCHAR(200)    NOT NULL                 COMMENT '作业标题',
    content         TEXT            NOT NULL                 COMMENT '作业内容（最长5000字）',
    teacher_id      BIGINT          NOT NULL                 COMMENT '布置教师ID → sys_user.id',
    is_updated      TINYINT         NOT NULL DEFAULT 0       COMMENT '是否被编辑更新过: 0=否, 1=是',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_class_id (class_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_schedule_id (schedule_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课后作业';

-- 2. 作业附件表
CREATE TABLE edu_homework_attachment (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    homework_id     BIGINT          NOT NULL                 COMMENT '关联作业ID',
    file_name       VARCHAR(200)    NOT NULL                 COMMENT '原始文件名',
    file_url        VARCHAR(500)    NOT NULL                 COMMENT '文件存储URL',
    file_size       BIGINT          NOT NULL                 COMMENT '文件大小（字节）',
    file_type       VARCHAR(20)     NOT NULL                 COMMENT '文件类型: IMAGE/DOCUMENT',
    mime_type       VARCHAR(100)    NULL                     COMMENT 'MIME类型',
    sort_order      INT             NOT NULL DEFAULT 0       COMMENT '排序号',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_homework_id (homework_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='作业附件';

-- 3. 家长查阅记录表
CREATE TABLE edu_parent_view_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    parent_user_id  BIGINT          NOT NULL                 COMMENT '家长用户ID → sys_user.id',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID → edu_student.id',
    biz_type        VARCHAR(20)     NOT NULL                 COMMENT '业务类型: EVALUATION/EXAM/HOMEWORK',
    biz_id          BIGINT          NOT NULL                 COMMENT '业务记录ID',
    first_view_time DATETIME        NOT NULL                 COMMENT '首次查看时间',
    last_view_time  DATETIME        NOT NULL                 COMMENT '最近查看时间',
    view_count      INT             NOT NULL DEFAULT 1       COMMENT '查看次数',
    PRIMARY KEY (id),
    UNIQUE KEY uk_parent_view (parent_user_id, student_id, biz_type, biz_id),
    KEY idx_student_id (student_id),
    KEY idx_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家长查阅记录';

-- ============================================
-- N05.2 教评学
-- ============================================

-- 4. 课堂评价主表
CREATE TABLE edu_evaluation (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    schedule_id     BIGINT          NOT NULL                 COMMENT '关联排课记录ID → edu_schedule.id',
    class_id        BIGINT          NOT NULL                 COMMENT '关联班级ID（冗余方便查询）',
    teacher_id      BIGINT          NOT NULL                 COMMENT '授课教师ID → sys_user.id',
    summary         TEXT            NULL                     COMMENT '课堂总结（30-2000字）',
    status          TINYINT         NOT NULL DEFAULT 0       COMMENT '状态: 0=草稿, 1=已提交',
    submit_time     DATETIME        NULL                     COMMENT '提交时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_schedule_id (schedule_id),
    KEY idx_class_id (class_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课堂评价';

-- 5. 学生评价明细表
CREATE TABLE edu_evaluation_student (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    evaluation_id   BIGINT          NOT NULL                 COMMENT '关联评价ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID → edu_student.id',
    star_rating     DECIMAL(2,1)    NOT NULL                 COMMENT '星级评分 1.0-5.0，0.5步进',
    comment         VARCHAR(500)    NULL                     COMMENT '文字评语',
    reward_points   INT             NULL                     COMMENT '奖励积分数值',
    reward_reason   VARCHAR(100)    NULL                     COMMENT '积分奖励原因',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_evaluation_student (evaluation_id, student_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生评价明细';

-- ============================================
-- N05.3 学评教
-- ============================================

-- 6. 学评教表
CREATE TABLE edu_teacher_evaluation (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    teacher_id      BIGINT          NOT NULL                 COMMENT '被评价教师ID → sys_user.id',
    student_id      BIGINT          NOT NULL                 COMMENT '提交评价的学生ID → edu_student.id',
    schedule_id     BIGINT          NULL                     COMMENT '关联排课记录ID → edu_schedule.id',
    rating_score    TINYINT         NOT NULL                 COMMENT '综合评分 1-5（整数）',
    content         VARCHAR(500)    NULL                     COMMENT '评价文字内容',
    tags            VARCHAR(500)    NULL                     COMMENT '评价标签JSON数组',
    is_anonymous    TINYINT         NOT NULL DEFAULT 0       COMMENT '是否匿名: 0=实名, 1=匿名',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/HIDDEN',
    submit_time     DATETIME        NOT NULL                 COMMENT '提交时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_teacher_eval (teacher_id, student_id, schedule_id),
    KEY idx_teacher_submit (teacher_id, submit_time),
    KEY idx_submit_time (submit_time),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学评教';

-- ============================================
-- 成绩管理
-- ============================================

-- 7. 考试成绩主表
CREATE TABLE edu_exam_score (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    class_id        BIGINT          NOT NULL                 COMMENT '班级ID → edu_class.id',
    subject_id      BIGINT          NOT NULL                 COMMENT '科目ID → edu_subject.id',
    exam_type       VARCHAR(20)     NOT NULL                 COMMENT '考试类型: MONTHLY/MIDTERM/FINAL/QUIZ/CUSTOM',
    exam_name       VARCHAR(50)     NULL                     COMMENT '自定义考试名称（仅CUSTOM时填写）',
    exam_date       DATE            NOT NULL                 COMMENT '考试日期',
    total_score     DECIMAL(5,1)    NOT NULL                 COMMENT '满分分值',
    teacher_id      BIGINT          NOT NULL                 COMMENT '录入教师ID → sys_user.id',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_exam (class_id, subject_id, exam_type, exam_date),
    KEY idx_class_id (class_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_exam_date (exam_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='考试成绩';

-- 8. 学生成绩明细表
CREATE TABLE edu_exam_score_student (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    exam_score_id   BIGINT          NOT NULL                 COMMENT '关联成绩ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID → edu_student.id',
    score           DECIMAL(5,1)    NULL                     COMMENT '分数，缺考时null',
    is_absent       TINYINT         NOT NULL DEFAULT 0       COMMENT '是否缺考: 0=否, 1=是',
    remark          VARCHAR(100)    NULL                     COMMENT '备注',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_exam_student (exam_score_id, student_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生成绩明细';

-- ============================================
-- N03 教师档案（PRD N01 §3.2.1 教资人数统计用）
-- ============================================

CREATE TABLE edu_teacher (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id             BIGINT          NULL                     COMMENT '关联系统用户ID → sys_user.id',
    real_name           VARCHAR(50)     NOT NULL                 COMMENT '教师姓名',
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '在职状态: ACTIVE=在职, INACTIVE=离职',
    subjects            VARCHAR(500)    NULL                     COMMENT '可授科目',
    hire_date           DATE            NULL                     COMMENT '入职日期',
    default_pay_per_lesson DECIMAL(10,2) NULL                   COMMENT '默认课酬单价（元/节）',
    phone               VARCHAR(20)     NULL                     COMMENT '联系电话',
    remark              VARCHAR(500)    NULL                     COMMENT '备注',
    create_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted          TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师档案';

-- ============================================
-- N05.1 学生作业提交（PRD N05 §2.5 §2.6）
-- ============================================

CREATE TABLE edu_homework_submission (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    homework_id     BIGINT          NOT NULL                 COMMENT '关联作业ID → edu_homework.id',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID → edu_student.id',
    content         VARCHAR(1000)   NULL                     COMMENT '文字内容（学生提交的文本备注）',
    image_urls      VARCHAR(2000)   NULL                     COMMENT '图片URL列表（JSON数组）',
    status          VARCHAR(20)     NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态: SUBMITTED=已提交, GRADED=已批改, LATE=迟交',
    score           INT             NULL                     COMMENT '评分（1-5）',
    comment         VARCHAR(500)    NULL                     COMMENT '教师评语',
    submitted_at    DATETIME        NULL                     COMMENT '提交时间',
    graded_at       DATETIME        NULL                     COMMENT '批改时间',
    graded_by       BIGINT          NULL                     COMMENT '批改教师ID',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_homework_id (homework_id),
    KEY idx_student_id (student_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生作业提交';

-- ========== 初始化数据 ==========

-- =====================================================
-- 新艺智源管理平台 — 系统管理模块 + 业务模块 初始化数据
-- 已去除 多公司 / 部门 / 岗位 / 数据权限 相关种子
-- 默认管理员: admin / admin123
--
-- 运行前提：仅用于首次全新安装。执行前 sys_menu/sys_role 等表应为空。
-- 与 migration-business-menus.sql 互斥：不要在同一个数据库上先后执行。
-- =====================================================

SET NAMES utf8mb4;

-- ---------- 用户 ----------
-- 密码 admin123 的 BCrypt 哈希
INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark)
VALUES (1, 'admin', '$2b$10$5sQPytQwvHx9yqB1Ye/gwOV33rrcfLpo5HrXPGN4rPDTNZYydhPsG', '超级管理员', '管理员', 'admin@edu.com', '13800000000', 1, 1, '系统内置超级管理员');

-- ---------- 角色 ----------
INSERT INTO sys_role (id, name, code, sort, menu_check_strictly, status, remark) VALUES
(1, '超级管理员', 'admin',   1, 1, 1, '拥有全部权限'),
(2, '普通用户',   'user',    2, 1, 1, '基础权限'),
(3, '教师',      'teacher', 3, 1, 1, '教师角色，拥有教师端权限'),
(4, '家长',      'parent',  4, 1, 1, '家长角色，仅查看关联学员信息');

-- ---------- 用户-角色 ----------
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- ---------- 菜单 ----------
-- 目录
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(1, 0, '系统管理', 'M', '/system', NULL, 'System', 'system', NULL, 1, 1, 1);

-- 菜单（C）
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(100, 1, '用户管理', 'C', 'user',   'system/user/index',   'SysUser',   'user',   'system:user:list',   1, 1, 1),
(101, 1, '角色管理', 'C', 'role',   'system/role/index',   'SysRole',   'peoples', 'system:role:list',   2, 1, 1),
(102, 1, '菜单管理', 'C', 'menu',   'system/menu/index',   'SysMenu',   'tree-table', 'system:menu:list', 3, 1, 1),
(103, 1, '字典管理', 'C', 'dict',   'system/dict/index',   'SysDict',   'dict',   'system:dict:list',   4, 1, 1),
(104, 1, '参数设置', 'C', 'config', 'system/config/index', 'SysConfig', 'edit',   'system:config:list', 5, 1, 1),
(105, 1, '通知公告', 'C', 'notice', 'system/notice/index', 'SysNotice', 'message', 'system:notice:list', 6, 1, 1),
(106, 1, '日志管理', 'C', 'log',    'system/log/index',    'SysLog',    'log',    'system:log:list',    7, 1, 1);

-- 按钮（F）
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
-- 用户
(100, '用户查询', 'F', 'system:user:query',    1, 1, 1),
(100, '用户新增', 'F', 'system:user:add',      2, 1, 1),
(100, '用户修改', 'F', 'system:user:edit',     3, 1, 1),
(100, '用户删除', 'F', 'system:user:delete',   4, 1, 1),
(100, '用户导出', 'F', 'system:user:export',   5, 1, 1),
(100, '用户导入', 'F', 'system:user:import',   6, 1, 1),
(100, '重置密码', 'F', 'system:user:resetPwd', 7, 1, 1),
-- 角色
(101, '角色查询', 'F', 'system:role:query',  1, 1, 1),
(101, '角色新增', 'F', 'system:role:add',    2, 1, 1),
(101, '角色修改', 'F', 'system:role:edit',   3, 1, 1),
(101, '角色删除', 'F', 'system:role:delete', 4, 1, 1),
-- 菜单
(102, '菜单查询', 'F', 'system:menu:query',  1, 1, 1),
(102, '菜单新增', 'F', 'system:menu:add',    2, 1, 1),
(102, '菜单修改', 'F', 'system:menu:edit',   3, 1, 1),
(102, '菜单删除', 'F', 'system:menu:delete', 4, 1, 1),
-- 字典
(103, '字典查询', 'F', 'system:dict:query',  1, 1, 1),
(103, '字典新增', 'F', 'system:dict:add',    2, 1, 1),
(103, '字典修改', 'F', 'system:dict:edit',   3, 1, 1),
(103, '字典删除', 'F', 'system:dict:delete', 4, 1, 1),
-- 参数
(104, '参数查询', 'F', 'system:config:query',  1, 1, 1),
(104, '参数新增', 'F', 'system:config:add',    2, 1, 1),
(104, '参数修改', 'F', 'system:config:edit',   3, 1, 1),
(104, '参数删除', 'F', 'system:config:delete', 4, 1, 1),
-- 通知
(105, '公告查询', 'F', 'system:notice:query',  1, 1, 1),
(105, '公告新增', 'F', 'system:notice:add',    2, 1, 1),
(105, '公告修改', 'F', 'system:notice:edit',   3, 1, 1),
(105, '公告删除', 'F', 'system:notice:delete', 4, 1, 1),
-- 日志
(106, '日志查询', 'F', 'system:log:query',  1, 1, 1),
(106, '日志删除', 'F', 'system:log:delete', 2, 1, 1);

-- ---------- 业务模块目录 ----------
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(2, 0, '学员管理', 'M', '/student', NULL, 'Student', 'peoples', NULL, 2, 1, 1),
(3, 0, '教务管理', 'M', '/course', NULL, 'Course', 'education', NULL, 3, 1, 1),
(4, 0, '教务教学', 'M', '/teaching', NULL, 'Teaching', 'guide', NULL, 4, 1, 1),
(5, 0, '财务管理', 'M', '/finance', NULL, 'Finance', 'money', NULL, 5, 1, 1),
(6, 0, '考勤管理', 'M', '/attendance', NULL, 'Attendance', 'time', NULL, 6, 1, 1),
(7, 0, '积分商城', 'M', '/points', NULL, 'Points', 'star', NULL, 7, 1, 1),
(8, 0, '教师端',   'M', '/teacher', NULL, 'Teacher', 'user', NULL, 8, 1, 1),
(9, 0, '工作台',   'C', '/dashboard', 'dashboard/index', 'Dashboard', 'dashboard', 'dashboard:dashboard:view', 1, 1, 1);

-- ---------- 业务模块菜单 ----------
-- 学员管理
INSERT INTO sys_menu (id, parent_id, name, type, path, component, route_name, icon, permission, sort, visible, status) VALUES
(200, 2, '意向学员', 'C', 'prospect',  'student/prospect/index',  'StudentProspect',  '#', 'student:prospect:list',  1, 1, 1),
(201, 2, '在读学员', 'C', 'enrolled',  'student/enrolled/index',  'StudentEnrolled',  '#', 'student:enrolled:list',  2, 1, 1),
(202, 2, '结业学员', 'C', 'completed', 'student/completed/index', 'StudentCompleted', '#', 'student:completed:list', 3, 1, 1),
(203, 2, '学员学时', 'C', 'hours',     'student/hours/index',     'StudentHours',     '#', 'student:hours:list',     4, 1, 1),
(204, 2, '学员家长', 'C', 'parents',   'student/parents/index',   'StudentParents',   '#', 'student:parent:list',    5, 1, 1),
-- 教务管理
(210, 3, '科目管理', 'C', 'subject',      'course/subject/index',      'CourseSubject',      '#', 'course:subject:list',      1, 1, 1),
(211, 3, '教室管理', 'C', 'classroom',    'course/classroom/index',    'CourseClassroom',    '#', 'course:classroom:list',    2, 1, 1),
(212, 3, '课程管理', 'C', 'course',       'course/course/index',       'CourseManage',       '#', 'course:course:list',       3, 1, 1),
(213, 3, '班级管理', 'C', 'class',        'course/class/index',        'ClassManage',        '#', 'class:class:list',         4, 1, 1),
(214, 3, '排课方案', 'C', 'schedulePlan', 'course/schedulePlan/index', 'SchedulePlan',       '#', 'schedule:plan:list',       5, 1, 1),
(215, 3, '排课日历', 'C', 'schedule',     'course/schedule/index',     'ScheduleCalendar',   '#', 'schedule:calendar:list',   6, 1, 1),
(216, 3, '课件资源', 'C', 'resource',     'course/resource/index',     'CourseResource',     '#', 'course:resource:list',     7, 1, 1),
-- 教务教学
(220, 4, '考试管理', 'C', 'exam',              'teaching/exam/index',              'TeachingExam',              '#', 'teaching:exam:list',              1, 1, 1),
(221, 4, '作业管理', 'C', 'homework',          'teaching/homework/index',          'TeachingHomework',          '#', 'teaching:homework:list',          2, 1, 1),
(222, 4, '教评学',   'C', 'evaluation',        'teaching/evaluation/index',        'TeachingEvaluation',        '#', 'teaching:evaluation:list',        3, 1, 1),
(223, 4, '学评教',   'C', 'teacherEvaluation', 'teaching/teacherEvaluation/index', 'TeachingTeacherEvaluation', '#', 'teaching:teacher-evaluation:list', 4, 1, 1),
(224, 4, '教师管理', 'C', 'teacher',           'teaching/teacher/index',           'TeachingTeacher',           '#', 'teaching:teacher:list',           5, 1, 1),
-- 财务管理
(230, 5, '学费管理', 'C', 'tuition',       'finance/tuition/index',       'FinanceTuition',       '#', 'tuition:fee:list',       1, 1, 1),
(231, 5, '薪资管理', 'C', 'payroll',       'finance/payroll/index',       'FinancePayroll',       '#', 'payroll:payroll:list',   2, 1, 1),
(232, 5, '教学统计', 'C', 'teachingStats', 'finance/teachingStats/index', 'FinanceTeachingStats', '#', 'payroll:pay-rate:list',  3, 1, 1),
-- 考勤管理
(240, 6, '签到记录', 'C', 'signin',       'attendance/signin/index',          'AttendanceSignin',       '#', 'attendance:signin:list',          1, 1, 1),
(241, 6, '学生请假', 'C', 'leave/student','attendance/leave/student/index',   'AttendanceLeaveStudent', '#', 'attendance:leave:student:list',  2, 1, 1),
(242, 6, '教师请假', 'C', 'leave/teacher','attendance/leave/teacher/index',   'AttendanceLeaveTeacher', '#', 'attendance:leave:teacher:list',  3, 1, 1),
-- 积分商城
(250, 7, '礼品管理', 'C', 'gift',       'points/gift/index',       'PointsGift',       '#', 'points:gift:list',       1, 1, 1),
(251, 7, '兑换记录', 'C', 'redemption', 'points/redemption/index', 'PointsRedemption', '#', 'points:redemption:list', 2, 1, 1),
-- 教师端
(260, 8, '工作台',   'C', 'dashboard',   'teacher/dashboard/index',   'TeacherDashboard',  '#', 'teacher:dashboard:view',   1, 1, 1),
(261, 8, '课后作业', 'C', 'homework',    'teacher/homework/index',    'TeacherHomework',   '#', 'teaching:homework:list',   2, 1, 1),
(262, 8, '课件共享', 'C', 'resource',    'teacher/resource/index',    'TeacherResource',   '#', 'course:resource:list',     3, 1, 1),
(263, 8, '我的课表', 'C', 'my-schedule', 'teacher/my-schedule/index', 'TeacherMySchedule', '#', 'schedule:calendar:list',   4, 1, 1);

-- ---------- 业务模块按钮 ----------
INSERT INTO sys_menu (parent_id, name, type, permission, sort, visible, status) VALUES
-- 意向学员
(200, '意向学员查询', 'F', 'student:prospect:query',    1, 1, 1),
(200, '意向学员新增', 'F', 'student:prospect:add',      2, 1, 1),
(200, '意向学员修改', 'F', 'student:prospect:edit',     3, 1, 1),
(200, '意向学员删除', 'F', 'student:prospect:delete',   4, 1, 1),
(200, '意向学员转化', 'F', 'student:prospect:convert',  5, 1, 1),
(200, '跟进记录管理', 'F', 'student:prospect:followup', 6, 1, 1),
-- 在读学员
(201, '在读学员查询', 'F', 'student:enrolled:query',  1, 1, 1),
(201, '在读学员新增', 'F', 'student:enrolled:add',    2, 1, 1),
(201, '在读学员修改', 'F', 'student:enrolled:edit',   3, 1, 1),
(201, '在读学员删除', 'F', 'student:enrolled:delete', 4, 1, 1),
-- 结业学员
(202, '结业学员查询', 'F', 'student:completed:query', 1, 1, 1),
-- 学员学时
(203, '学员学时列表', 'F', 'student:hours:list',  1, 1, 1),
(203, '学员学时查询', 'F', 'student:hours:query', 2, 1, 1),
(203, '学员学时新增', 'F', 'student:hours:add',   3, 1, 1),
-- 学员家长
(204, '学员家长查询', 'F', 'student:parent:query',  1, 1, 1),
(204, '学员家长新增', 'F', 'student:parent:add',    2, 1, 1),
(204, '学员家长修改', 'F', 'student:parent:edit',   3, 1, 1),
(204, '学员家长删除', 'F', 'student:parent:delete', 4, 1, 1),
-- 科目管理
(210, '科目查询', 'F', 'course:subject:query',  1, 1, 1),
(210, '科目新增', 'F', 'course:subject:add',    2, 1, 1),
(210, '科目修改', 'F', 'course:subject:edit',   3, 1, 1),
(210, '科目删除', 'F', 'course:subject:delete', 4, 1, 1),
-- 教室管理
(211, '教室查询', 'F', 'course:classroom:query',  1, 1, 1),
(211, '教室新增', 'F', 'course:classroom:add',    2, 1, 1),
(211, '教室修改', 'F', 'course:classroom:edit',   3, 1, 1),
(211, '教室删除', 'F', 'course:classroom:delete', 4, 1, 1),
-- 课程管理
(212, '课程查询', 'F', 'course:course:query',  1, 1, 1),
(212, '课程新增', 'F', 'course:course:add',    2, 1, 1),
(212, '课程修改', 'F', 'course:course:edit',   3, 1, 1),
(212, '课程删除', 'F', 'course:course:delete', 4, 1, 1),
-- 班级管理
(213, '班级查询',   'F', 'class:class:query',  1, 1, 1),
(213, '班级新增',   'F', 'class:class:add',    2, 1, 1),
(213, '班级修改',   'F', 'class:class:edit',   3, 1, 1),
(213, '班级删除',   'F', 'class:class:delete', 4, 1, 1),
(213, '班级学员管理', 'F', 'class:student:add',    5, 1, 1),
(213, '班级学员列表', 'F', 'class:student:list',   6, 1, 1),
(213, '班级学员修改', 'F', 'class:student:edit',   7, 1, 1),
(213, '班级学员删除', 'F', 'class:student:delete', 8, 1, 1),
-- 排课方案
(214, '排课方案查询', 'F', 'schedule:plan:query',  1, 1, 1),
(214, '排课方案新增', 'F', 'schedule:plan:add',    2, 1, 1),
(214, '排课方案修改', 'F', 'schedule:plan:edit',   3, 1, 1),
(214, '排课方案删除', 'F', 'schedule:plan:delete', 4, 1, 1),
-- 排课日历
(215, '排课日历查询', 'F', 'schedule:calendar:query', 1, 1, 1),
(215, '排课取消',     'F', 'schedule:cancel',        2, 1, 1),
(215, '排课调课',     'F', 'schedule:reschedule',    3, 1, 1),
(215, '排课消课',     'F', 'schedule:consume',       4, 1, 1),
-- 课件资源
(216, '课件资源查询', 'F', 'course:resource:query',  1, 1, 1),
(216, '课件资源新增', 'F', 'course:resource:add',    2, 1, 1),
(216, '课件资源修改', 'F', 'course:resource:edit',   3, 1, 1),
(216, '课件资源删除', 'F', 'course:resource:delete', 4, 1, 1),
-- 考试管理
(220, '考试查询', 'F', 'teaching:exam:query',  1, 1, 1),
(220, '考试新增', 'F', 'teaching:exam:add',    2, 1, 1),
(220, '考试修改', 'F', 'teaching:exam:edit',   3, 1, 1),
(220, '考试删除', 'F', 'teaching:exam:remove', 4, 1, 1),
-- 作业管理
(221, '作业查询', 'F', 'teaching:homework:query',  1, 1, 1),
(221, '作业新增', 'F', 'teaching:homework:add',    2, 1, 1),
(221, '作业修改', 'F', 'teaching:homework:edit',   3, 1, 1),
(221, '作业删除', 'F', 'teaching:homework:remove', 4, 1, 1),
-- 教评学
(222, '教评学查询', 'F', 'teaching:evaluation:query',  1, 1, 1),
(222, '教评学新增', 'F', 'teaching:evaluation:add',    2, 1, 1),
(222, '教评学删除', 'F', 'teaching:evaluation:remove', 3, 1, 1),
-- 学评教
(223, '学评教查询', 'F', 'teaching:teacher-evaluation:query',  1, 1, 1),
(223, '学评教导出', 'F', 'teaching:teacher-evaluation:export', 2, 1, 1),
-- 教师管理
(224, '教师查询', 'F', 'teaching:teacher:query',  1, 1, 1),
(224, '教师新增', 'F', 'teaching:teacher:add',    2, 1, 1),
(224, '教师修改', 'F', 'teaching:teacher:edit',   3, 1, 1),
(224, '教师删除', 'F', 'teaching:teacher:delete', 4, 1, 1),
-- 学费管理（含报名/缴费/退费/赠课）
(230, '学费查询',   'F', 'tuition:fee:query',     1, 1, 1),
(230, '学费新增',   'F', 'tuition:fee:add',       2, 1, 1),
(230, '学费修改',   'F', 'tuition:fee:edit',      3, 1, 1),
(230, '学费删除',   'F', 'tuition:fee:delete',    4, 1, 1),
(230, '退费列表',   'F', 'tuition:refund:list',   5, 1, 1),
(230, '退费申请',   'F', 'tuition:refund:apply',  6, 1, 1),
(230, '退费审批',   'F', 'tuition:refund:approve',7, 1, 1),
(230, '退费删除',   'F', 'tuition:refund:delete', 8, 1, 1),
(230, '赠课列表',   'F', 'tuition:gift:list',     9, 1, 1),
(230, '赠课新增',   'F', 'tuition:gift:add',      10, 1, 1),
(230, '赠课删除',   'F', 'tuition:gift:delete',   11, 1, 1),
(230, '报名新增',   'F', 'enrollment:enrollment:add',    12, 1, 1),
(230, '报名修改',   'F', 'enrollment:enrollment:edit',   13, 1, 1),
(230, '报名取消',   'F', 'enrollment:enrollment:cancel', 14, 1, 1),
(230, '报名列表',   'F', 'enrollment:enrollment:list',   15, 1, 1),
(230, '报名查询',   'F', 'enrollment:enrollment:query',  16, 1, 1),
-- 薪资管理
(231, '薪资查询', 'F', 'payroll:payroll:query',   1, 1, 1),
(231, '薪资新增', 'F', 'payroll:payroll:add',     2, 1, 1),
(231, '薪资确认', 'F', 'payroll:payroll:confirm', 3, 1, 1),
(231, '薪资发放', 'F', 'payroll:payroll:pay',     4, 1, 1),
(231, '薪资删除', 'F', 'payroll:payroll:delete',  5, 1, 1),
-- 教学统计/课时费率
(232, '课时费率查询', 'F', 'payroll:pay-rate:query',  1, 1, 1),
(232, '课时费率新增', 'F', 'payroll:pay-rate:add',    2, 1, 1),
(232, '课时费率修改', 'F', 'payroll:pay-rate:edit',   3, 1, 1),
(232, '课时费率删除', 'F', 'payroll:pay-rate:delete', 4, 1, 1),
-- 签到记录
(240, '签到记录查询', 'F', 'attendance:signin:query', 1, 1, 1),
(240, '签到记录新增', 'F', 'attendance:signin:add',   2, 1, 1),
-- 学生请假
(241, '学生请假查询', 'F', 'attendance:leave:student:query',   1, 1, 1),
(241, '学生请假新增', 'F', 'attendance:leave:student:add',     2, 1, 1),
(241, '学生请假审批', 'F', 'attendance:leave:student:approve', 3, 1, 1),
-- 教师请假
(242, '教师请假查询',   'F', 'attendance:leave:teacher:query',   1, 1, 1),
(242, '教师请假新增',   'F', 'attendance:leave:teacher:add',     2, 1, 1),
(242, '教师请假审批',   'F', 'attendance:leave:teacher:approve', 3, 1, 1),
(242, '教师请假冲突处理', 'F', 'attendance:leave:teacher:resolve',4, 1, 1),
-- 礼品管理
(250, '礼品查询', 'F', 'points:gift:query',  1, 1, 1),
(250, '礼品新增', 'F', 'points:gift:add',    2, 1, 1),
(250, '礼品修改', 'F', 'points:gift:edit',   3, 1, 1),
(250, '礼品删除', 'F', 'points:gift:delete', 4, 1, 1),
-- 兑换记录
(251, '兑换记录查询', 'F', 'points:redemption:query',  1, 1, 1),
(251, '兑换记录导出', 'F', 'points:redemption:export', 2, 1, 1),
(251, '兑换发放',     'F', 'points:redemption:claim',  3, 1, 1),
(251, '兑换取消',     'F', 'points:redemption:cancel', 4, 1, 1),
-- 工作台
(9, '工作台查看', 'F', 'dashboard:dashboard:view', 1, 1, 1),
-- 积分账户（挂积分商城目录）
(7, '积分账户查询', 'F', 'points:account:query',  1, 1, 1),
(7, '积分账户调整', 'F', 'points:account:adjust', 2, 1, 1),
-- 教师端工作台
(260, '教师工作台查看', 'F', 'teacher:dashboard:view', 1, 1, 1);

-- ---------- 角色-菜单（超级管理员拥有全部菜单） ----------
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 教师角色菜单分配（ID精确匹配：教师端目录 + 4个子菜单 + 工作台）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu
WHERE id IN (8, 9, 260, 261, 262, 263)
   OR parent_id IN (8, 9, 260, 261, 262, 263)
   OR (permission IN (
       'teaching:homework:query', 'teaching:homework:add', 'teaching:homework:edit', 'teaching:homework:remove',
       'course:resource:query', 'course:resource:add', 'course:resource:edit', 'course:resource:delete',
       'schedule:calendar:query', 'schedule:calendar:list',
       'dashboard:dashboard:view', 'teacher:dashboard:view'
   ) AND type = 'F');

-- ---------- 字典类型 ----------
INSERT INTO sys_dict_type (id, name, code, status, remark) VALUES
(1, '用户性别', 'sys_user_sex',     1, '用户性别列表'),
(2, '系统开关', 'sys_normal_disable', 1, '系统开关列表'),
(3, '系统是否', 'sys_yes_no',       1, '系统是否列表'),
(4, '通知类型', 'sys_notice_type',  1, '通知类型列表'),
(5, '菜单类型', 'sys_menu_type',    1, '菜单类型列表'),
(6, '显示状态', 'sys_show_hide',    1, '显示状态列表');

-- ---------- 字典数据 ----------
INSERT INTO sys_dict_data (type_code, label, value, sort, is_default, status) VALUES
('sys_user_sex', '男',   '1', 1, 0, 1),
('sys_user_sex', '女',   '2', 2, 0, 1),
('sys_user_sex', '未知', '0', 3, 1, 1),
('sys_normal_disable', '正常', '1', 1, 1, 1),
('sys_normal_disable', '停用', '0', 2, 0, 1),
('sys_yes_no', '是', 'Y', 1, 1, 1),
('sys_yes_no', '否', 'N', 2, 0, 1),
('sys_notice_type', '通知', '1', 1, 1, 1),
('sys_notice_type', '公告', '2', 2, 0, 1),
('sys_menu_type', '目录', 'M', 1, 0, 1),
('sys_menu_type', '菜单', 'C', 2, 0, 1),
('sys_menu_type', '按钮', 'F', 3, 0, 1),
('sys_show_hide', '显示', '1', 1, 1, 1),
('sys_show_hide', '隐藏', '0', 2, 0, 1);

-- ---------- 系统参数 ----------
INSERT INTO sys_config (name, `key`, `value`, type, remark) VALUES
('用户初始密码',   'sys.user.initPassword',   '123456', 'Y', '用户新增/重置的初始密码'),
('系统名称',       'sys.system.name',         '新艺智源管理平台', 'Y', '系统名称'),
('系统描述',       'sys.system.description',  '在线教育课程展示与交易平台', 'Y', '系统描述'),
('账号最大重试次数', 'sys.account.maxRetryCount', '5', 'Y', '登录失败锁定阈值'),
('账号锁定时间(分)', 'sys.account.lockTime',      '10', 'Y', '登录失败锁定时长');
