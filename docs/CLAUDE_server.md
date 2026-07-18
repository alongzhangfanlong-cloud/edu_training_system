# CLAUDE_server.md

> 后端规范 · Spring Boot 3 + JDK 17 + MySQL 8.0 + Redis 7

---

## 🧩 技术约定

### Java 规范

- JDK 17，使用现代 Java 语法（record、sealed class、pattern matching）
- 包结构按模块分层：`com.education.store.module.{模块名}.{controller|service|mapper|entity|dto}`
- 类名使用 PascalCase，方法名使用 camelCase
- 使用 Lombok 减少样板代码（@Data, @Slf4j, @RequiredArgsConstructor）

### Spring Boot 规范

- Spring Boot 3.x，使用 Jakarta EE 规范
- Controller 层：只负责参数校验和路由，不写业务逻辑
- Service 层：接口 + 实现分离（IService → ServiceImpl）
- Mapper 层：使用 MyBatis-Plus BaseMapper
- 统一返回格式：`R<T>` 包装（code, msg, data）
- 统一异常处理：`GlobalExceptionHandler` 全局拦截

### 数据库规范

- MySQL 8.0 + InnoDB
- 表名小写 + 下划线分隔（`edu_course`）
- 字段名小写 + 下划线分隔（`create_time`）
- 每表必有的字段：`id`, `create_time`, `update_time`, `is_deleted`
- 逻辑删除使用 `is_deleted` 字段

### 安全规范

- Spring Security + JWT 认证
- 密码使用 BCrypt 加密
- 接口权限使用注解控制
- SQL 注入防护使用 MyBatis 参数绑定
- 多角色权限：学生、家长、教师、管理员

---

## 📦 模块结构

```
education-store-server/
├── edu-common/              # 公共模块（注解、异常、工具类、枚举）
├── edu-security/            # 安全模块（Spring Security + JWT）
├── edu-modules/             # 业务模块
│   ├── edu-module-system/   # 系统模块（用户、角色、权限、配置）
│   ├── edu-module-course/   # 课程模块（课程管理、分类、标签）
│   ├── edu-module-order/    # 订单模块（购物车、订单、支付）
│   └── edu-module-content/  # 内容模块（评价、问答、通知）
├── edu-server-app/          # 应用入口（启动类 + 配置）
└── sql/                     # 数据库脚本
```

---

## 🗄️ Mapper 层规范

- **方法名必须精确反映 SQL 的过滤条件**，避免近似命名陷阱：
  - `selectPermsByUserId` → 查询 `m.permission` 字段，**无 type 过滤**，返回全部权限字符串
  - `selectVisibleMenusByUserId` → 查询 `m.*` 字段，**含 `type IN ('M','C')`**，只返回菜单/目录实体
  - ⛔ **严禁**两个 SQL 逻辑不同但方法名只差一两个字母（如 `selectMenuPermsByUserId` vs `selectMenusByUserId`）
- Service 层委托 Mapper 方法时，必须在注释中写明调用了哪个 Mapper 方法及原因
- Mapper 接口每个方法标注 `@Select` SQL，代码审查时重点对比同名/近名方法的 SQL 差异

---

## ✅ 编码规范

- 缩进 4 空格
- Controller 方法使用 `@Operation` 注解（Swagger 文档）
- 关键操作使用 `@Log` 注解记录操作日志
- 禁止在循环中执行 SQL 查询
- 敏感信息不打印日志
