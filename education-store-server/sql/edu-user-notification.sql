-- 用户消息通知表
DROP TABLE IF EXISTS user_notification;
CREATE TABLE user_notification (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT       NOT NULL                    COMMENT '接收通知的用户ID',
    title           VARCHAR(200) NOT NULL                    COMMENT '通知标题',
    content         TEXT                                    COMMENT '通知内容',
    type            VARCHAR(32)  NOT NULL                    COMMENT '通知类型: SIGN_IN/EVALUATION/HOMEWORK/EXAM/LEAVE/REDEMPTION/SYSTEM',
    event_type      VARCHAR(32)                             COMMENT '事件类型(冗余字段)',
    related_id      BIGINT                                   COMMENT '关联业务ID(如排课ID/作业ID等)',
    related_type    VARCHAR(50)                             COMMENT '关联业务类型',
    is_read         TINYINT      DEFAULT 0                   COMMENT '是否已读(0未读 1已读)',
    read_time       DATETIME                                 COMMENT '阅读时间',
    sender_id       BIGINT                                   COMMENT '发送人ID(系统通知可为空)',
    status          TINYINT      DEFAULT 1                   COMMENT '状态(1正常 0禁用)',
    create_by       VARCHAR(64)  DEFAULT ''                  COMMENT '创建者',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)  DEFAULT ''                  COMMENT '更新者',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      DEFAULT 0                   COMMENT '逻辑删除(0未删除 1已删除)',
    INDEX idx_user_id (user_id),
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户消息通知表';

-- 插入一些示例数据
INSERT INTO user_notification (user_id, title, content, type, event_type, is_read, create_time) VALUES
(1, '签到提醒', '今天有3节课需要签到，请及时前往教室。', 'SIGN_IN', 'SIGN_IN', 0, NOW() - INTERVAL 1 HOUR),
(1, '作业批改', '您有2份作业待批改，请尽快完成。', 'HOMEWORK', 'HOMEWORK', 0, NOW() - INTERVAL 30 MINUTE),
(1, '学生评价', '有5位学生完成了课程评价，平均得分4.8分。', 'EVALUATION', 'EVALUATION', 1, NOW() - INTERVAL 2 HOUR),
(1, '请假审批', '有1条请假申请等待您的审批。', 'LEAVE', 'LEAVE', 0, NOW() - INTERVAL 10 MINUTE),
(2, '系统通知', '欢迎使用教育培训机构管理系统。', 'SYSTEM', 'SYSTEM', 0, NOW() - INTERVAL 5 HOUR),
(2, '成绩发布', '期中考试成绩已发布，请查看详情。', 'EXAM', 'EXAM', 0, NOW() - INTERVAL 1 HOUR);
