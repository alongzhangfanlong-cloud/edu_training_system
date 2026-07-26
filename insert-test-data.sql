-- ========================================
-- 教育管理系统 - 业务流测试数据SQL脚本
-- ========================================

USE edu_system;

-- 【1】科目
INSERT IGNORE INTO edu_subject (name, sort_order, status, create_time, create_by) VALUES
('高中数学', 1, 1, NOW(), 'admin'),
('高中英语', 2, 1, NOW(), 'admin');

SET @math_subject_id = (SELECT id FROM edu_subject WHERE name = '高中数学' LIMIT 1);
SET @eng_subject_id = (SELECT id FROM edu_subject WHERE name = '高中英语' LIMIT 1);

-- 【2】课程
INSERT IGNORE INTO edu_course (name, code, subject_id, course_type, price, applicable_grade, status, sort_order, description, create_time, create_by) VALUES
('高三数学冲刺班', 'MATH-G3-SPRINT', @math_subject_id, '一对一', 300.00, '高三', 1, 1, '高三数学一对一冲刺，目标130+', NOW(), 'admin'),
('高一英语基础班', 'ENG-G1-BASIC', @eng_subject_id, '班课', 120.00, '高一', 1, 2, '高一英语基础班课', NOW(), 'admin'),
('高二数学提高班', 'MATH-G2-IMPROVE', @math_subject_id, '班课', 150.00, '高二', 1, 3, '高二数学提高班课', NOW(), 'admin');

SET @sprint_course_id = (SELECT id FROM edu_course WHERE code = 'MATH-G3-SPRINT' LIMIT 1);
SET @eng_course_id = (SELECT id FROM edu_course WHERE code = 'ENG-G1-BASIC' LIMIT 1);
SET @math_course2_id = (SELECT id FROM edu_course WHERE code = 'MATH-G2-IMPROVE' LIMIT 1);

-- 【3】教师
INSERT IGNORE INTO edu_teacher (real_name, phone, default_pay_per_lesson, status, subjects, hire_date, remark, create_time, create_by) VALUES
('张明华', '13811110001', 120.00, 'ACTIVE', '数学', '2024-09-01', '高级教师10年教龄', NOW(), 'admin'),
('李文雅', '13911110002', 100.00, 'ACTIVE', '英语', '2025-02-01', '英语专八', NOW(), 'admin');

-- 【4】意向学员
INSERT IGNORE INTO edu_prospect (name, mobile, parent_mobile, parent_name, parent_relation, gender, grade, school, intended_course_id, interest_level, source, follow_status, remark, create_time, create_by) VALUES
('赵小明', '13633330001', '13533330001', '赵先生', 'FATHER', 'M', '高三', '市第一中学', @sprint_course_id, 'HIGH', 'REFERRAL', 'CONTACTED', '数学105分，目标130+，朋友介绍', NOW(), 'admin'),
('孙小红', '13633330002', '13533330002', '孙女士', 'MOTHER', 'F', '高一', '市第二中学', @eng_course_id, 'MEDIUM', 'DIRECT_MARKETING', 'NEW', '英语基础薄弱', NOW(), 'admin'),
('周强', '13633330003', '13533330003', '周先生', 'FATHER', 'M', '高二', '实验中学', @math_course2_id, 'HIGH', 'ONLINE', 'CONTACTED', '网上咨询，已约试听', NOW(), 'admin');

SET @p_zhao_id = (SELECT id FROM edu_prospect WHERE mobile = '13633330001' LIMIT 1);
SET @p_sun_id = (SELECT id FROM edu_prospect WHERE mobile = '13633330002' LIMIT 1);
SET @p_zhou_id = (SELECT id FROM edu_prospect WHERE mobile = '13633330003' LIMIT 1);

-- 【5】跟进记录（只插入不存在的）
INSERT IGNORE INTO edu_prospect_followup (prospect_id, method, content, followup_date, next_plan, operator_id, create_time, create_by) VALUES
(@p_zhao_id, 'PHONE', '电话沟通：家长反映数学偏科，周末来试听', NOW(), '2026-07-26前完成试听安排', 1, NOW(), 'admin'),
(@p_zhou_id, 'PHONE', '电话沟通：预约本周六上午10点试听', NOW(), '2026-07-27回访试听结果', 1, NOW(), 'admin');

-- 更新意向学员的last_followup_date
UPDATE edu_prospect SET last_followup_date = NOW() WHERE id IN (@p_zhao_id, @p_zhou_id);

-- 【6】在读学员
INSERT IGNORE INTO edu_student (name, mobile, parent_mobile, parent_name, parent_relation, gender, grade, school, birthday, address, source, status, total_hours, remaining_hours, remark, create_time, create_by) VALUES
('陈小美', '13644440001', '13544440001', '陈女士', 'MOTHER', 'F', '高三', '市第一中学', '2008-03-20', '北京市海淀区中关村', 'FRIEND', 'ACTIVE', 0, 0, '学霸型学员', NOW(), 'admin'),
('林小军', '13644440002', '13544440002', '林先生', 'FATHER', 'M', '高一', '市第三中学', '2010-07-15', '北京市朝阳区建国路', 'DIRECT_MARKETING', 'ACTIVE', 0, 0, '英语入门', NOW(), 'admin');

SET @s_chen_id = (SELECT id FROM edu_student WHERE mobile = '13644440001' LIMIT 1);
SET @s_lin_id = (SELECT id FROM edu_student WHERE mobile = '13644440002' LIMIT 1);

-- 【7】报名记录
INSERT IGNORE INTO edu_enrollment (student_id, course_id, amount, payment_status, handler_id, enrollment_date, remark, create_time, create_by) VALUES
(@s_chen_id, @sprint_course_id, 9000.00, 'UNPAID', 1, '2026-07-20', '报名30课时一对一，全款赠送2课时', NOW(), 'admin'),
(@s_lin_id, @eng_course_id, 2400.00, 'UNPAID', 1, '2026-07-22', '报名20课时班课', NOW(), 'admin');

SET @e_chen_id = (SELECT id FROM edu_enrollment WHERE student_id = @s_chen_id AND course_id = @sprint_course_id LIMIT 1);
SET @e_lin_id = (SELECT id FROM edu_enrollment WHERE student_id = @s_lin_id AND course_id = @eng_course_id LIMIT 1);

-- 【8】收费记录（使用时间戳生成单号）
SET @fee_no1 = CONCAT('T', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), '01');
SET @fee_no2 = CONCAT('T', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), '02');

INSERT IGNORE INTO edu_tuition_fee (fee_no, enrollment_id, student_id, course_id, fee_type, amount_receivable, amount_paid, payment_method, status, operator_id, remark, create_time, create_by) VALUES
(@fee_no1, @e_chen_id, @s_chen_id, @sprint_course_id, 'TUITION', 9000.00, 9000.00, '微信', 'CONFIRMED', 1, '全款缴纳', NOW(), 'admin'),
(@fee_no2, @e_lin_id, @s_lin_id, @eng_course_id, 'TUITION', 2400.00, 1200.00, '支付宝', 'CONFIRMED', 1, '首付1200，剩余下月缴清', NOW(), 'admin');

-- 更新报名收费状态
UPDATE edu_enrollment SET payment_status = 'PAID', tuition_record_id = @e_chen_id WHERE id = @e_chen_id;
UPDATE edu_enrollment SET payment_status = 'PARTIAL', tuition_record_id = @e_lin_id WHERE id = @e_lin_id;
