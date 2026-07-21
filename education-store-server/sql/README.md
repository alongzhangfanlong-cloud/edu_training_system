# 数据库文件说明

## 快速开始（新开发者）

```bash
# 1. 创建数据库
mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS edu_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 2. 一键安装（推荐）
mysql -uroot -proot edu_system < edu-system-setup.sql
```

## 文件说明

| 文件 | 用途 | 说明 |
|------|------|------|
| `edu-system-setup.sql` | **一键安装** | 合并所有 schema + 初始数据，首次安装执行此文件即可 |
| `edu-schema.sql` | 系统表结构 | 用户/角色/菜单/字典/日志等 sys_ 表（12 张） |
| `edu-batch1-schema.sql` | 课程/班级/学生 | Phase 1 业务表（12 张） |
| `edu-batch2-schema.sql` | 教务/考勤/薪酬 | Phase 2 业务表（12 张） |
| `edu-batch3a-schema.sql` | 积分商城 | Phase 3a 业务表（4 张） |
| `edu-batch3b-schema.sql` | 家校沟通 | Phase 3b 业务表（10 张） |
| `edu-init-data.sql` | 初始数据 | 管理员账号、角色、菜单、字典等种子数据 |
| `migration-business-menus.sql` | 菜单迁移 | 业务模块菜单迁移脚本（与 edu-init-data.sql 互斥） |
| `exports/edu-system-full.sql` | 完整导出 | 当前数据库的完整备份（结构+数据），可直接恢复 |
| `exports/edu-system-schema.sql` | 结构导出 | 当前数据库的纯结构备份 |

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | admin123 |
| 普通用户 | user | 123456 |

## 注意事项

- `edu-system-setup.sql` 和 `migration-business-menus.sql` **互斥**，不要在同一个数据库上先后执行
- `exports/` 目录下的文件是 `mysqldump` 导出，包含完整数据库状态，可直接导入恢复
- 重新安装时所有表会被 `DROP TABLE IF EXISTS` 清除后重建
