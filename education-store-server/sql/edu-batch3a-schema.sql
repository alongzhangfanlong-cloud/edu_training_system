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
