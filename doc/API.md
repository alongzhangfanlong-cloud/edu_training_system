# 教务管理系统 API 文档 v1.0

> 基础URL: `http://localhost:8080/api/v1`
> Swagger UI: `http://localhost:8080/swagger-ui.html`
> 认证: `Authorization: Bearer {token}`

---

## 🔐 1. 认证模块 `/auth`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/auth/send-sms` | 发送验证码 | 否 |
| POST | `/auth/login` | 登录 | 否 |
| POST | `/auth/refresh` | 刷新Token | 否 |
| POST | `/auth/logout` | 登出 | 是 |

---

## 👤 2. 用户管理 `/users`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/users` | 用户列表 | 是 |
| POST | `/users` | 创建用户 | 是 |
| PUT | `/users/{id}` | 更新用户信息 | 是 |
| PUT | `/users/{id}/status` | 启/禁用用户 | 是 |
| GET | `/users/menus` | 用户菜单 | 是 |

---

## 🏫 3. 校区管理 `/campuses`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/campuses` | 校区列表 | 是 |
| GET | `/campuses/{id}` | 校区详情 | 是 |
| POST | `/campuses` | 创建校区 | 是 |
| PUT | `/campuses/{id}` | 更新校区 | 是 |
| DELETE | `/campuses/{id}` | 删除校区 | 是 |

---

## 📋 4. 菜单管理 `/menus`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/menus/tree` | 菜单树 | 是 |
| GET | `/menus` | 菜单列表 | 是 |
| POST | `/menus` | 创建菜单 | 是 |
| PUT | `/menus/{id}` | 更新菜单 | 是 |
| DELETE | `/menus/{id}` | 删除菜单 | 是 |

---

## 🔑 5. 权限管理 `/permissions`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/permissions` | 权限列表 | 是 |
| GET | `/permissions/role-permissions` | 角色权限矩阵 | 是 |
| POST | `/permissions/assign` | 分配权限 | 是 |

---

## 👨‍🎓 6. 学员管理 `/students`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/students` | 学员列表(分页) | 是 |
| POST | `/students` | 新增学员 | 是 |
| GET | `/students/{id}` | 学员详情 | 是 |
| PUT | `/students/{id}` | 更新学员 | 是 |
| DELETE | `/students/{id}` | 删除学员 | 是 |
| GET | `/students/{id}/hours` | 学员课时明细 | 是 |

---

## 📚 7. 课程管理 `/courses`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/courses` | 课程列表 | 是 |
| GET | `/courses/{id}` | 课程详情 | 是 |
| POST | `/courses` | 创建课程 | 是 |
| PUT | `/courses/{id}` | 更新课程 | 是 |
| DELETE | `/courses/{id}` | 删除课程 | 是 |

---

## 👨‍🏫 8. 教师管理 `/teachers`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/teachers` | 教师列表 | 是 |
| GET | `/teachers/{id}` | 教师详情 | 是 |
| POST | `/teachers` | 创建教师 | 是 |
| PUT | `/teachers/{id}` | 更新教师 | 是 |
| DELETE | `/teachers/{id}` | 删除教师 | 是 |

---

## 🏠 9. 教室管理 `/classrooms`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/classrooms` | 教室列表 | 是 |
| GET | `/classrooms/{id}` | 教室详情 | 是 |
| POST | `/classrooms` | 创建教室 | 是 |
| PUT | `/classrooms/{id}` | 更新教室 | 是 |
| DELETE | `/classrooms/{id}` | 删除教室 | 是 |

---

## 🏫 10. 班级管理 `/classes`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/classes` | 班级列表 | 是 |
| GET | `/classes/{id}` | 班级详情 | 是 |
| POST | `/classes` | 创建班级 | 是 |
| PUT | `/classes/{id}` | 更新班级 | 是 |
| DELETE | `/classes/{id}` | 删除班级 | 是 |

---

## 📅 11. 排课管理 `/schedules`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/schedules` | 课表查询(日期/教师/教室/学员) | 是 |
| POST | `/schedules` | 单条排课 | 是 |
| POST | `/schedules/batch` | 批量排课 | 是 |
| POST | `/schedules/conflict-check` | 冲突检测 | 是 |
| PUT | `/schedules/{id}` | 修改排课 | 是 |
| DELETE | `/schedules/{id}` | 删除排课 | 是 |

---

## ✅ 12. 考勤签到 `/attendances`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/attendances/batch-check` | 批量点名 | 是 |
| GET | `/attendances/schedule/{id}` | 排课考勤查询 | 是 |
| GET | `/attendances/student/{id}` | 学员考勤记录 | 是 |
| GET | `/attendances/student/{id}/stats` | 学员考勤统计 | 是 |

---

## ⏱️ 13. 消课管理 `/consume`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/consume` | 消课记录列表 | 是 |
| POST | `/consume/calculate` | 计算消课规则 | 是 |
| POST | `/consume/execute` | 执行消课 | 是 |
| POST | `/consume/rollback` | 消课回滚 | 是 |

---

## 💬 14. 课堂反馈 `/feedbacks`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/feedbacks` | 反馈列表 | 是 |
| POST | `/feedbacks` | 提交反馈 | 是 |
| GET | `/feedbacks/schedule/{id}` | 排课反馈详情 | 是 |
| GET | `/feedbacks/schedule/{id}/student/{sid}` | 学员个人反馈 | 是 |
| DELETE | `/feedbacks/{id}` | 删除反馈 | 是 |

---

## 📝 15. 作业管理 `/homework`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/homework` | 作业列表 | 是 |
| POST | `/homework` | 布置作业 | 是 |
| GET | `/homework/{id}` | 作业详情 | 是 |
| PUT | `/homework/{id}` | 修改作业 | 是 |
| DELETE | `/homework/{id}` | 删除作业 | 是 |

---

## 🌱 16. 成长档案 `/growth-records`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/growth-records` | 成长记录列表 | 是 |
| POST | `/growth-records` | 新增记录 | 是 |
| GET | `/growth-records/{id}` | 记录详情 | 是 |
| PUT | `/growth-records/{id}` | 修改记录 | 是 |
| DELETE | `/growth-records/{id}` | 删除记录 | 是 |

---

## ⭐ 17. 积分管理 `/points`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/points` | 积分列表 | 是 |
| GET | `/points/student/{id}` | 学员积分 | 是 |
| POST | `/points/change` | 积分变动 | 是 |
| GET | `/points/student/{id}/transactions` | 积分流水 | 是 |

---

## 📋 18. 请假管理 `/leaves`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/leaves` | 请假列表 | 是 |
| POST | `/leaves` | 提交申请 | 是 |
| POST | `/leaves/{id}/approve` | 审批 | 是 |
| DELETE | `/leaves/{id}` | 删除记录 | 是 |

---

## 🎂 19. 生日祝福 `/birthday-greetings`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/birthday-greetings` | 祝福记录 | 是 |
| POST | `/birthday-greetings` | 发送祝福 | 是 |
| DELETE | `/birthday-greetings/{id}` | 删除记录 | 是 |

---

## 📊 20. 统计分析 `/statistics`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/statistics/dashboard` | 运营仪表盘 | 是 |
| GET | `/statistics/teacher/{id}` | 教师统计 | 是 |

---

## 🛒 21. 报名缴费 `/orders`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/orders` | 订单列表 | 是 |
| POST | `/orders` | 创建订单 | 是 |
| GET | `/orders/{id}` | 订单详情 | 是 |
| POST | `/orders/{id}/pay` | 支付 | 是 |

---

## 🎓 22. 学员门户 `/student-portal`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/student-portal/{id}/dashboard` | 学员首页聚合 | 是 |

---

## ⚙️ 23. 系统配置 `/system-configs`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/system-configs` | 配置列表 | 是 |
| GET | `/system-configs/{key}` | 配置详情 | 是 |
| PUT | `/system-configs/{key}` | 更新配置 | 是 |

---

## 统计

| 类别 | 数量 |
|------|------|
| 模块 | 23 |
| GET 端点 | 39 |
| POST 端点 | 28 |
| PUT 端点 | 11 |
| DELETE 端点 | 12 |
| **总计** | **90** |
