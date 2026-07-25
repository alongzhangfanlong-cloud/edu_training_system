-- =====================================================
-- 新艺智源管理平台 — 补充 edu_student 家长信息字段（对齐意向学员 EduProspect）
-- 创建时间: 2026-07-24
-- 说明: 在读学员新增时也需要填写家长信息，字段对齐意向学员
--       执行后请移除 EduStudent 实体上 parentId 的 @TableField(exist = false)
-- =====================================================

ALTER TABLE edu_student
    ADD COLUMN parent_name     VARCHAR(20)  NULL COMMENT '家长姓名'       AFTER counselor_id,
    ADD COLUMN parent_relation VARCHAR(20)  NULL COMMENT '亲属关系(FATHER/MOTHER/...)' AFTER parent_name,
    ADD COLUMN parent_mobile   VARCHAR(20)  NULL COMMENT '家长手机号'     AFTER parent_relation,
    ADD COLUMN parent_id       BIGINT       NULL COMMENT '家长用户ID(关联sys_user, 用于微信通知)' AFTER parent_mobile;

CREATE INDEX idx_parent_id ON edu_student(parent_id);
