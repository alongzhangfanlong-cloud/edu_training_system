# 教育培训管理系统 (Education Training Management System)

面向K12、艺术培训、语言培训、职业技能培训等机构的移动端管理工具。

## 技术栈

| 层级 | 技术 |
|---|---|
| 后端 | Spring Boot 3.2 + MyBatis-Plus 3.5 + JDK 17 |
| 数据库 | MySQL 8.0 + Redis 6 |
| 消息队列 | RabbitMQ |
| 前端 | UniApp (Vue 3) + Vue 3 + Element Plus |
| 部署 | Docker Compose |

## 快速启动

### 1. 启动基础设施

```bash
docker-compose up -d
```

### 2. 启动后端

```bash
cd backend
mvn clean install -DskipTests
cd edu-bootstrap
mvn spring-boot:run
```

应用启动后访问:
- API 文档: http://localhost:8080/swagger-ui.html
- 健康检查: http://localhost:8080/api/v1/health

### 3. 测试API

```bash
# 发送验证码
curl -X POST http://localhost:8080/api/v1/auth/send-sms \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800000000"}'

# 登录 (验证码固定123456)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800000000","code":"123456","role":"admin"}'
```

## 项目结构

```
├── backend/                    # 后端服务
│   ├── edu-common/             # 公共模块
│   ├── edu-modules/            # 业务模块
│   └── edu-bootstrap/          # 启动入口
├── frontend/                   # 前端应用
│   ├── student-miniapp/        # 学员/家长端小程序
│   ├── teacher-miniapp/        # 教师端小程序
│   └── admin-app/              # 管理后台
├── doc/                        # 设计文档
└── docker-compose.yml          # Docker基础设施
```

## 四端角色

- **学员端**: 查看课表、签到打卡、查看课时余额、提交作业
- **家长端**: 查看孩子课表、接收通知、请假申请、查看消费记录
- **教师端**: 查看授课安排、课堂点名、布置作业、学员评价
- **管理端**: 排课管理、学员管理、财务管理、数据统计

## 版本规划

- v1.0.0 (MVP): 基础CRUD、手动排课、点名签到、基础消课、报名支付
- v1.5.0: 智能排课、扫码签到、请假/补课、消息推送
- v2.0.0: AI排课推荐、人脸识别签到、数据可视化大屏、营销工具
