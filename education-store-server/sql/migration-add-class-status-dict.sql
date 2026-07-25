-- =====================================================
-- 迁移：补充班级状态字典（class_status）
-- 修复前：列表页状态列显示英文 RECRUITING/FULL/SUSPENDED 等
-- 修复后：显示中文 招生中/已满员/已停招/已结课/已取消
-- 运行前提：sys_dict_type 中不存在 code='class_status' 的记录
-- =====================================================

-- 字典类型（如果 id=13 已被占用，请手动调整）
INSERT INTO sys_dict_type (id, name, code, status, remark)
SELECT 13, '班级状态', 'class_status', 1, '班级状态列表'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE code = 'class_status');

-- 字典数据（幂等：使用 WHERE NOT EXISTS 避免重复插入）
INSERT INTO sys_dict_data (type_code, label, value, sort, is_default, status)
SELECT 'class_status', '招生中', 'RECRUITING', 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE type_code = 'class_status' AND value = 'RECRUITING');

INSERT INTO sys_dict_data (type_code, label, value, sort, is_default, status)
SELECT 'class_status', '已满员', 'FULL', 2, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE type_code = 'class_status' AND value = 'FULL');

INSERT INTO sys_dict_data (type_code, label, value, sort, is_default, status)
SELECT 'class_status', '已停招', 'SUSPENDED', 3, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE type_code = 'class_status' AND value = 'SUSPENDED');

INSERT INTO sys_dict_data (type_code, label, value, sort, is_default, status)
SELECT 'class_status', '已结课', 'FINISHED', 4, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE type_code = 'class_status' AND value = 'FINISHED');

INSERT INTO sys_dict_data (type_code, label, value, sort, is_default, status)
SELECT 'class_status', '已取消', 'CANCELLED', 5, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE type_code = 'class_status' AND value = 'CANCELLED');
