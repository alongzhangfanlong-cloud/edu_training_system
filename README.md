# 教育培训资源管理平台

> 一站式教育培训机构数字化管理系统，涵盖学员管理、教务排课、考勤签到、家校沟通、财务收费、积分商城等核心功能。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-42b883.svg)](https://vuejs.org/)
[![uni-app](https://img.shields.io/badge/uni--app-3.x-007AFF.svg)](https://uniapp.dcloud.io/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 📋 目录

- [系统架构](#-系统架构)
- [功能模块](#-功能模块)
- [技术栈](#-技术栈)
- [项目结构](#-项目结构)
- [快速开始](#-快速开始)
- [文档导航](#-文档导航)
- [部署说明](#-部署说明)
- [安全加固](#-安全加固)

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                         用户终端                             │
├──────────────────┬──────────────────┬───────────────────────┤
│  微信小程序(家长/教师)  │  管理后台(PC)  │                        │
│  uni-app + Vue3  │  Vue3 + Vite    │                        │
└────────┬─────────┴────────┬─────────┘                       │
         │                   │                                 │
         └───────────────────┼───────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │    Nginx反向代理  │  HTTPS/SSL/静态资源/负载均衡
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
┌────────▼────────┐ ┌────────▼────────┐ ┌────────▼────────┐
│  Spring Boot    │ │   静态资源       │ │  WebSocket      │
│  Backend API    │ │  (上传文件等)    │ │  (消息通知)     │
└────────┬────────┘ └─────────────────┘ └─────────────────┘
         │
    ┌────┴────┬──────────┐
    │         │          │
┌───▼───┐ ┌───▼───┐ ┌───▼────┐
│ MySQL │ │ Redis │ │ 微信API │
│ 主数据库│ │ 缓存  │ │ 小程序  │
└───────┘ └───────┘ └────────┘
```

---

## 🎯 功能模块

| 模块 | 功能说明 | 端 |
|------|---------|-----|
| 🔐 **认证系统** | 用户名密码/微信登录、JWT双Token、角色权限 | 全端 |
| 👨‍🎓 **学员管理** | 学员档案、家长绑定、课时管理、跟进记录 | PC/小程序 |
| 👨‍🏫 **教师管理** | 教师档案、薪资核算、授课统计 | PC |
| 📚 **课程管理** | 课程、科目、教室、课件资源管理 | PC |
| 🏫 **班级管理** | 班级创建、学员分班、排课规则 | PC |
| 📅 **排课管理** | 周历排课、课程表、冲突检测 | PC/小程序 |
| ✅ **考勤签到** | 教师签到、学员签到、请假审批 | PC/小程序 |
| 📝 **教学管理** | 作业发布、课堂评价、考试成绩 | PC/小程序 |
| 💰 **收费管理** | 报名缴费、退费、课时赠送、财务报表 | PC |
| 👨‍👩‍👧 **家校沟通** | 站内消息、微信订阅通知、成长档案 | 小程序 |
| 🎁 **积分商城** | 积分获取、积分兑换、礼品管理 | PC/小程序 |
| 📊 **数据看板** | 经营数据、教学数据、招生数据统计 | PC |
| ⚙️ **系统管理** | 用户、角色、菜单、参数配置 | PC |

---

## 🛠️ 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | JDK版本 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存/Token存储 |
| JWT | 0.12.x | 身份认证 |
| Knife4j/Swagger | 4.x | API文档 |
| Hutool | 5.x | 工具库 |
| BCrypt | - | 密码加密 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.x | 前端框架 |
| Vite | 5.x | 构建工具 |
| Element Plus | 2.x | UI组件库 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由 |
| Axios | 1.x | HTTP客户端 |

### 小程序技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| uni-app | 3.x | 跨端框架 |
| Vue.js | 3.x | 前端框架 |

---

## 📁 项目结构

```
edu_training_system/
├── education-store-server/       # 后端服务 (Spring Boot)
│   ├── edu-common/               # 公共模块（工具类、异常、配置）
│   ├── edu-security/             # 安全认证模块（JWT、过滤器）
│   ├── edu-modules/              # 业务模块
│   │   ├── edu-module-system/    # 系统管理（用户、角色、菜单）
│   │   ├── edu-module-student/   # 学员管理
│   │   ├── edu-module-teacher/   # 教师管理（集成在其他模块）
│   │   ├── edu-module-course/    # 课程、教室、资源管理
│   │   ├── edu-module-class/     # 班级管理
│   │   ├── edu-module-schedule/  # 排课管理
│   │   ├── edu-module-attendance/# 考勤签到、请假
│   │   ├── edu-module-teaching/  # 教学（作业、评价、成绩）
│   │   ├── edu-module-tuition/   # 收费、退费、薪资
│   │   ├── edu-module-enrollment/# 报名管理
│   │   ├── edu-module-prospect/  # 潜在学员跟进
│   │   ├── edu-module-points/    # 积分商城
│   │   ├── edu-module-dashboard/ # 家长端API/首页聚合
│   │   └── edu-module-teacher-dashboard/ # 教师端API
│   ├── edu-server-app/           # 启动模块（Main Class）
│   └── sql/                      # 数据库脚本
│
├── education-store-web/          # 管理后台前端 (Vue3)
│   ├── src/
│   │   ├── api/                  # API接口定义
│   │   ├── views/                # 页面组件
│   │   ├── components/           # 公共组件
│   │   ├── router/               # 路由
│   │   └── stores/               # Pinia状态
│   └── package.json
│
├── education-store-mini/         # 微信小程序 (uni-app)
│   ├── src/
│   │   ├── pages/                # 页面
│   │   ├── components/           # 组件
│   │   ├── api/                  # API封装
│   │   └── stores/               # 状态管理
│   └── pages.json
│
├── docs/                         # 项目文档
│   ├── api/                      # API接口文档
│   ├── prd/                      # 产品需求文档
│   ├── db/                       # 数据库文档
│   └── model/                    # 数据模型设计
│
├── DEPLOYMENT.md                 # 生产部署文档
└── README.md                     # 本文件
```

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 本地开发

#### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE edu_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入初始脚本
mysql -u root -p edu_system < education-store-server/sql/exports/edu-system-full.sql
```

#### 2. 启动后端

```bash
cd education-store-server

# 修改application-dev.yml中的数据库和Redis配置
# 默认配置：
#   MySQL: localhost:3306, root/root
#   Redis: localhost:6379, 无密码

# 编译启动
mvn spring-boot:run -pl edu-server-app
```

后端启动后访问：
- API服务：http://localhost:8080
- API文档：http://localhost:8080/doc.html
- 健康检查：http://localhost:8080/actuator/health

#### 3. 启动管理后台前端

```bash
cd education-store-web

# 安装依赖
npm install
# 或 pnpm install

# 启动开发服务
npm run dev
# 或 pnpm dev
```

访问 http://localhost:5173

**默认管理员账号：**
- 用户名：`admin`
- 密码：`admin123` (生产环境务必修改)

#### 4. 启动小程序开发

使用HBuilderX或VS Code打开 `education-store-mini/` 目录，运行到微信开发者工具。

---

## 📚 文档导航

| 文档 | 说明 |
|------|------|
| [DEPLOYMENT.md](DEPLOYMENT.md) | **生产部署文档** - 服务器配置、Nginx、HTTPS、备份等 |
| [docs/api/](docs/api/) | API接口文档（各模块） |
| [docs/prd/](docs/prd/) | 产品需求文档 |
| [docs/db/](docs/db/) | 数据库设计和初始化脚本 |
| [docs/model/](docs/model/) | 数据模型文档 |

---

## 🔒 安全加固（生产环境已完成）

本次上线前已完成以下安全加固：

### ✅ 已修复安全问题

- [x] **SQL注入防护**：所有动态SQL使用#{}预编译，`.last()`拼接参数校验
- [x] **XSS防护**：全局异常处理不暴露敏感信息
- [x] **文件上传安全**：扩展名白名单、双后缀检测、UUID重命名、路径遍历防护
- [x] **越权访问防护**：数据所有权校验、家长只能访问自己孩子数据
- [x] **JWT安全**：双Token机制、Redis黑名单、密钥强度要求
- [x] **CORS安全**：生产环境域名白名单，不允许*通配符
- [x] **NPE防护**：所有Controller参数null检查、全局异常兜底
- [x] **分页限制**：单页最大200条限制，防止OOM
- [x] **密码加密**：BCrypt强哈希
- [x] **恶意文件删除**：清理upload目录下.jsp/.exe等危险文件
- [x] **上传文件访问控制**：Security层正则白名单，高危后缀直接deny
- [x] **防全表更新/删除**：MyBatis-Plus BlockAttackInnerInterceptor
- [x] **请求体校验**：JSON格式错误、空请求体友好提示
- [x] **参数类型校验**：PathVariable类型不匹配返回400

### ✅ 全局异常处理

已覆盖异常类型：
- BusinessException（业务异常）
- MethodArgumentNotValidException（@Valid校验失败）
- HttpMessageNotReadableException（JSON解析错误）
- HttpRequestMethodNotSupportedException（HTTP方法错误）
- MethodArgumentTypeMismatchException（参数类型错误）
- NullPointerException（空指针兜底）
- NumberFormatException（数字格式错误）
- DataAccessException/SQLException（数据库异常）
- Exception（通用兜底）

---

## 🧪 测试验证

### 启动验证清单

- [x] 全量编译通过：`mvn clean compile -DskipTests`
- [x] 应用启动正常（7.5秒内启动完成）
- [x] 健康检查端点正常返回 `{"status":"UP"}`
- [x] 未认证请求返回401
- [x] 参数校验失败返回400
- [x] HTTP方法错误返回405
- [x] 恶意文件（.jsp/.php）访问被拒绝
- [x] 空请求体返回友好错误
- [x] 登录参数校验正常

---

## 🤝 开发规范

### 代码风格

- Controller层统一返回 `Result<T>`
- 分页接口返回 `PageResult<T>`
- 业务异常抛出 `BusinessException`
- 所有写操作使用 `@Log` 注解记录操作日志
- 参数校验使用 `@Valid` + JSR-303注解

### API规范

- 接口前缀：`/api/v1/`
- RESTful风格：GET查询、POST创建、PUT更新、DELETE删除
- 统一响应体：`{code, message, data, timestamp}`
- 分页参数：`page`(页码)、`pageSize`(每页条数，默认10，最大100)

---

## 📞 技术支持

如有问题，请参考以下文档：
1. 首先查看 [DEPLOYMENT.md](DEPLOYMENT.md) 部署文档
2. 查看API文档（启动后访问 /doc.html）
3. 查看docs/目录下的产品和技术文档

### 联系方式

- 邮箱：1289965516@qq.com

---

## 📄 许可证

MIT License

---

**最后更新：2026-07-27** | **版本：v1.0.0生产就绪版**
