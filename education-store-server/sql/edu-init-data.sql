-- =====================================================
-- 新艺智源管理平台 — 系统管理模块 初始化数据
-- 已去除 多公司 / 部门 / 岗位 / 数据权限 相关种子
-- 默认管理员: admin / admin123
-- =====================================================

SET NAMES utf8mb4;

-- ---------- 用户 ----------
-- 密码 admin123 的 BCrypt 哈希
INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark)
VALUES (1, 'admin', '$2b$10$5sQPytQwvHx9yqB1Ye/gwOV33rrcfLpo5HrXPGN4rPDTNZYydhPsG', '超级管理员', '管理员', 'admin@edu.com', '13800000000', 1, 1, '系统内置超级管理员');

-- ---------- 角色 ----------
INSERT INTO sys_role (id, name, code, sort, menu_check_strictly, status, remark) VALUES
(1, '超级管理员', 'admin', 1, 1, 1, '拥有全部权限'),
(2, '普通用户',   'user',  2, 1, 1, '基础权限');

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
(105, '公告删除', 'F', 'system:notice:delete', 4, 1, 1);

-- ---------- 角色-菜单（超级管理员拥有全部菜单） ----------
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

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
