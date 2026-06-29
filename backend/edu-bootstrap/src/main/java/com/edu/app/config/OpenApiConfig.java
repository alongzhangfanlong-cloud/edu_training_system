package com.edu.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("教育培训管理系统 API")
                        .version("1.0.0")
                        .description("""
                        教育培训管理系统 API 文档

                        ## 模块概览
                        - 🔐 认证模块: 登录/登出/刷新Token
                        - 👤 用户管理: 用户CRUD
                        - 🏫 校区管理: 多校区CRUD
                        - 📋 菜单管理: 动态菜单树
                        - 🔑 权限管理: 角色-权限矩阵
                        - 👨‍🎓 学员管理: 学员CRUD + 课时明细
                        - 📚 课程管理: 课程CRUD
                        - 👨‍🏫 教师管理: 教师CRUD
                        - 🏠 教室管理: 教室CRUD
                        - 🏫 班级管理: 班级CRUD
                        - 📅 排课管理: 排课+批量排课+冲突检测
                        - ✅ 考勤签到: 批量点名+学生考勤统计
                        - ⏱️ 消课管理: 消课计算+执行+回滚
                        - 💬 课堂反馈: 反馈提交+查询
                        - 📝 作业管理: 作业CRUD
                        - 🌱 成长档案: 成长记录CRUD
                        - ⭐ 积分管理: 积分变动+流水
                        - 📋 请假管理: 申请+审批
                        - 🎂 生日祝福: 发送+记录
                        - 📊 统计分析: 运营数据仪表盘
                        - 🛒 报名缴费: 订单+支付
                        - ⚙️ 系统配置: 系统参数管理

                        ## 认证方式
                        所有API(除登录外)需在Header中携带: `Authorization: Bearer {token}`
                        """)
                        .contact(new Contact().name("Edu Team")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .schemaRequirement("Bearer", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Token: Bearer {token}"));
    }
}
