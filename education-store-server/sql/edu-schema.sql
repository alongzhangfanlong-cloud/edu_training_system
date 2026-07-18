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

SET FOREIGN_KEY_CHECKS = 1;
