-- ============================================================
-- 教育培训系统 - 数据完整性快速验证脚本
-- 使用方法: mysql -uroot -proot edu_system < 03_verify_data.sql
-- 或在MySQL客户端中直接执行
-- ============================================================

USE edu_system;

SET NAMES utf8mb4;

SELECT '========================================' AS ``;
SELECT '  教育培训系统 - 数据完整性验证' AS ``;
SELECT '========================================' AS ``;
SELECT '' AS ``;

-- ========================================
-- 1. 系统管理
-- ========================================
SELECT '--- 1. 系统管理 ---' AS ``;
SELECT CONCAT('系统用户: ', COUNT(*), ' 条') AS result FROM sys_user WHERE deleted=0;
SELECT CONCAT('系统角色: ', COUNT(*), ' 条') AS result FROM sys_role WHERE deleted=0;
SELECT CONCAT('系统菜单: ', COUNT(*), ' 条') AS result FROM sys_menu WHERE deleted=0;
SELECT CONCAT('数据字典类型: ', COUNT(*), ' 条') AS result FROM sys_dict_type WHERE deleted=0;

-- ========================================
-- 2. 课程与科目
-- ========================================
SELECT '' AS ``;
SELECT '--- 2. 课程与科目 ---' AS ``;
SELECT CONCAT('科目: ', COUNT(*), ' 条') AS result FROM edu_subject WHERE deleted=0;
SELECT CONCAT('课程: ', COUNT(*), ' 条') AS result FROM edu_course WHERE deleted=0;
SELECT CONCAT('教室: ', COUNT(*), ' 条') AS result FROM edu_classroom WHERE deleted=0;
SELECT CONCAT('教室设施为空: ', COUNT(*), ' 个 (应为0)') AS result
FROM edu_classroom WHERE deleted=0 AND (facilities IS NULL OR facilities = '' OR facilities = '[]');

-- ========================================
-- 3. 教师管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 3. 教师管理 ---' AS ``;
SELECT CONCAT('教师档案: ', COUNT(*), ' 条') AS result FROM edu_teacher WHERE deleted=0;
SELECT CONCAT('课酬标准: ', COUNT(*), ' 条') AS result FROM edu_teacher_pay_rate WHERE deleted=0;

-- ========================================
-- 4. 学员管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 4. 学员管理 ---' AS ``;
SELECT CONCAT('学员: ', COUNT(*), ' 条') AS result FROM edu_student WHERE deleted=0;
SELECT CONCAT('学员家长: ', COUNT(*), ' 条') AS result FROM edu_student_parent WHERE deleted=0;
SELECT CONCAT('课时日志: ', COUNT(*), ' 条') AS result FROM edu_student_hours_log WHERE deleted=0;

-- ========================================
-- 5. 班级管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 5. 班级管理 ---' AS ``;
SELECT CONCAT('班级: ', COUNT(*), ' 条') AS result FROM edu_class WHERE deleted=0;
SELECT CONCAT('班级学员: ', COUNT(*), ' 条') AS result FROM edu_class_student WHERE deleted=0;

-- ========================================
-- 6. 排课管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 6. 排课管理 ---' AS ``;
SELECT CONCAT('排课: ', COUNT(*), ' 条') AS result FROM edu_schedule WHERE deleted=0;
SELECT CONCAT('排课计划: ', COUNT(*), ' 条') AS result FROM edu_schedule_plan WHERE deleted=0;
SELECT CONCAT('今日排课(2026-07-26): ', COUNT(*), ' 条') AS result
FROM edu_schedule WHERE schedule_date='2026-07-26' AND deleted=0;

-- ========================================
-- 7. 课件资源
-- ========================================
SELECT '' AS ``;
SELECT '--- 7. 课件资源 ---' AS ``;
SELECT CONCAT('课件资源: ', COUNT(*), ' 条') AS result FROM edu_resource WHERE deleted=0;

-- ========================================
-- 8. 考试管理 (edu_exam_score)
-- ========================================
SELECT '' AS ``;
SELECT '--- 8. 考试管理 ---' AS ``;
SELECT CONCAT('考试: ', COUNT(*), ' 条') AS result FROM edu_exam_score WHERE deleted=0;
SELECT CONCAT('考试成绩明细: ', COUNT(*), ' 条') AS result FROM edu_exam_score_student;

-- ========================================
-- 9. 评价管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 9. 评价管理 ---' AS ``;
SELECT CONCAT('教学评价: ', COUNT(*), ' 条') AS result FROM edu_evaluation WHERE deleted=0;
SELECT CONCAT('评价学生明细: ', COUNT(*), ' 条') AS result FROM edu_evaluation_student WHERE deleted=0;
SELECT CONCAT('学评教: ', COUNT(*), ' 条') AS result FROM edu_teacher_evaluation WHERE deleted=0;

-- ========================================
-- 10. 作业管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 10. 作业管理 ---' AS ``;
SELECT CONCAT('作业: ', COUNT(*), ' 条') AS result FROM edu_homework WHERE deleted=0;
SELECT CONCAT('作业提交: ', COUNT(*), ' 条') AS result FROM edu_homework_submission WHERE deleted=0;
SELECT CONCAT('作业附件: ', COUNT(*), ' 条') AS result FROM edu_homework_attachment WHERE deleted=0;

-- ========================================
-- 11. 考勤管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 11. 考勤管理 ---' AS ``;
SELECT CONCAT('签到记录: ', COUNT(*), ' 条') AS result FROM edu_attendance WHERE deleted=0;
SELECT CONCAT('学生请假: ', COUNT(*), ' 条') AS result FROM edu_leave_student WHERE deleted=0;
SELECT CONCAT('教师请假: ', COUNT(*), ' 条') AS result FROM edu_leave_teacher WHERE deleted=0;

-- ========================================
-- 12. 报名与学费
-- ========================================
SELECT '' AS ``;
SELECT '--- 12. 报名与学费 ---' AS ``;
SELECT CONCAT('报名记录: ', COUNT(*), ' 条') AS result FROM edu_enrollment WHERE deleted=0;
SELECT CONCAT('收费记录: ', COUNT(*), ' 条') AS result FROM edu_tuition_fee WHERE deleted=0;
SELECT CONCAT('退费记录: ', COUNT(*), ' 条') AS result FROM edu_tuition_refund WHERE deleted=0;
SELECT CONCAT('赠送课时: ', COUNT(*), ' 条') AS result FROM edu_tuition_gift_hours WHERE deleted=0;
SELECT CONCAT('收费记录pay_time为空(已缴费): ', COUNT(*), ' 条 (应为0)') AS result
FROM edu_tuition_fee WHERE deleted=0 AND pay_time IS NULL AND (status='PAID' OR status='PARTIAL' OR status='CONFIRMED');

-- ========================================
-- 13. 薪资管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 13. 薪资管理 ---' AS ``;
SELECT CONCAT('薪资记录: ', COUNT(*), ' 条') AS result FROM edu_teacher_payroll WHERE deleted=0;
SELECT CONCAT('已确认薪资confirmed_at为空: ', COUNT(*), ' 条 (应为0)') AS result
FROM edu_teacher_payroll WHERE deleted=0 AND confirmed_at IS NULL AND status='CONFIRMED';

-- ========================================
-- 14. 积分管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 14. 积分管理 ---' AS ``;
SELECT CONCAT('积分账户: ', COUNT(*), ' 条') AS result FROM edu_points_account WHERE deleted=0;
SELECT CONCAT('积分礼品: ', COUNT(*), ' 条') AS result FROM edu_points_gift WHERE deleted=0;
SELECT CONCAT('积分流水: ', COUNT(*), ' 条') AS result FROM edu_points_log WHERE deleted=0;

-- ========================================
-- 15. 潜客管理
-- ========================================
SELECT '' AS ``;
SELECT '--- 15. 潜客管理 ---' AS ``;
SELECT CONCAT('潜客: ', COUNT(*), ' 条') AS result FROM edu_prospect WHERE deleted=0;
SELECT CONCAT('潜客跟进: ', COUNT(*), ' 条') AS result FROM edu_prospect_followup WHERE deleted=0;

-- ========================================
-- 16. 登录账号
-- ========================================
SELECT '' AS ``;
SELECT '--- 16. 登录账号 ---' AS ``;
SELECT CONCAT('可登录账号: ', COUNT(*), ' 个') AS result
FROM sys_user WHERE deleted=0 AND status=1 AND username IN ('admin','teacher01','teacher02');

-- ========================================
-- 汇总
-- ========================================
SELECT '' AS ``;
SELECT '========================================' AS ``;
SELECT '  验证完成！' AS ``;
SELECT '  账号: admin / 123456 (管理员)' AS ``;
SELECT '  账号: teacher01 / 123456 (张老师-美术)' AS ``;
SELECT '  账号: teacher02 / 123456 (李老师-数学/英语)' AS ``;
SELECT '========================================' AS ``;
