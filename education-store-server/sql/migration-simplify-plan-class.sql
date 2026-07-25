-- =====================================================
-- 排课计划简化：class_ids(JSON数组) → class_id(单值)，新增 room_id
-- 日期：2026-07-25
-- 1. 一个排课计划只对应一个班级，多选没有实际业务场景
-- 2. 排课计划需要指定教室，用于冲突检测
-- =====================================================

ALTER TABLE edu_schedule_plan
    CHANGE COLUMN class_ids class_id BIGINT NULL COMMENT '关联班级ID';

ALTER TABLE edu_schedule_plan
    ADD COLUMN room_id BIGINT NULL COMMENT '教室ID → edu_classroom.id' AFTER end_date;
