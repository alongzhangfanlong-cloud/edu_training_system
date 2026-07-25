-- =====================================================
-- 新艺智源管理平台 — edu_student 新增微信昵称 + 报名课程（对齐意向学员）
-- 创建时间: 2026-07-24
-- 说明: 新增学员表单与意向学员表单字段对齐
-- =====================================================

ALTER TABLE edu_student
    ADD COLUMN wechat_nickname        VARCHAR(50)  DEFAULT NULL COMMENT '微信昵称'       AFTER parent_id,
    ADD COLUMN intended_course_id     BIGINT       DEFAULT NULL COMMENT '报名课程ID → edu_course.id' AFTER wechat_nickname,
    ADD COLUMN intended_course_name   VARCHAR(100) DEFAULT NULL COMMENT '报名课程名称'   AFTER intended_course_id;

CREATE INDEX idx_intended_course ON edu_student(intended_course_id);
