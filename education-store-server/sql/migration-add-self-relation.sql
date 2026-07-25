-- =====================================================
-- 新艺智源管理平台 — 亲属关系字典新增"本人"选项
-- 创建时间: 2026-07-24
-- 说明: 意向学员和在读学员表单新增"本人"亲属关系
-- =====================================================

INSERT INTO sys_dict_data (type_code, label, value, sort, is_default, status)
SELECT 'parent_relation', '本人', 'SELF', 0, 0, 1
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_data WHERE type_code = 'parent_relation' AND value = 'SELF' AND deleted = 0
);
