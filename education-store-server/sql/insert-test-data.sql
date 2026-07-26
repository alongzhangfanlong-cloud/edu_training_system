-- ============================================
-- 测试数据初始化脚本
-- ============================================

-- 1. 添加测试教师用户
INSERT INTO sys_user (username, password, real_name, nick_name, mobile, gender, status, create_time, update_time, deleted, create_by)
SELECT 'teacher01', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张老师', '张老师', 'NOkT4YYwjyD', 1, 1, NOW(), NOW(), 0, '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'teacher01');

SET @teacher_id = (SELECT id FROM sys_user WHERE username = 'teacher01');

-- 2. 添加科目
INSERT INTO edu_subject (name, sort_order, status, description, create_time, update_time, deleted, create_by)
SELECT '少儿美术', 1, 1, '少儿美术类课程', NOW(), NOW(), 0, '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_subject WHERE name = '少儿美术');

-- 3. 添加教室
INSERT INTO edu_classroom (name, capacity, location, status, create_time, update_time, deleted, create_by)
SELECT '1号教室', 20, '1楼东侧', 1, NOW(), NOW(), 0, '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_classroom WHERE name = '1号教室');

SET @room_id = (SELECT id FROM edu_classroom WHERE name = '1号教室');
SET @subject_id = (SELECT id FROM edu_subject WHERE name = '少儿美术');

-- 4. 添加课程
INSERT INTO edu_course (name, code, subject_id, course_type, price, status, sort_order, description, create_time, update_time, create_by, deleted)
SELECT '少儿美术基础班', 'ART-BASIC-001', @subject_id, 'BATCH', 4800.00, 1, 1, '适合6-12岁儿童的美术基础课程', NOW(), NOW(), '1', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_course WHERE code = 'ART-BASIC-001');

SET @course_id = (SELECT id FROM edu_course WHERE code = 'ART-BASIC-001');

-- 5. 添加教师档案
INSERT INTO edu_teacher (user_id, real_name, subjects, hire_date, default_pay_per_lesson, phone, status, create_time, update_time, deleted)
SELECT @teacher_id, '张老师', '少儿美术', '2026-01-01', 80.00, 'NOkT4YYwjyD', 'ACTIVE', NOW(), NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_teacher WHERE user_id = @teacher_id);

-- 6. 添加班级
INSERT INTO edu_class (name, code, course_id, teacher_id, classroom_id, max_students, current_count, status, start_date, end_date, description, create_time, update_time, create_by, deleted)
SELECT '美术基础班-2026暑期', 'ART-CLASS-2026', @course_id, @teacher_id, @room_id, 15, 1, 'RECRUITING', '2026-07-01', '2026-08-31', '2026年暑期美术基础班', NOW(), NOW(), '1', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_class WHERE code = 'ART-CLASS-2026');

SET @class_id = (SELECT id FROM edu_class WHERE code = 'ART-CLASS-2026');

-- 7. 添加测试学员（直接在学员表中，包含家长信息）
INSERT INTO edu_student (name, gender, birth_date, mobile, grade, school, parent_name, parent_relation, parent_mobile, counselor_id, intended_course_id, source, status, total_hours, remaining_hours, create_time, update_time, create_by, deleted)
SELECT '小明', 'M', '2018-05-10', '9AmNIsrOzFI', '二年级', '阳光小学', '小明妈妈', '妈妈', 'rLdwD4kpev9', 1, @course_id, 'FRIEND', 'ENROLLED', 48.0, 48.0, NOW(), NOW(), '1', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_student WHERE mobile = '9AmNIsrOzFI');

SET @student_id = (SELECT id FROM edu_student WHERE mobile = '9AmNIsrOzFI');

-- 8. 添加学员班级关联
INSERT INTO edu_class_student (class_id, student_id, join_time, status, create_time, update_time, deleted)
SELECT @class_id, @student_id, NOW(), 'ENROLLED', NOW(), NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_class_student WHERE class_id = @class_id AND student_id = @student_id);

-- 9. 添加教师课酬单价
INSERT INTO edu_teacher_pay_rate (teacher_id, course_id, pay_per_lesson, effective_from, create_time, update_time, create_by, deleted)
SELECT @teacher_id, @course_id, 80.00, '2026-07-01', NOW(), NOW(), '1', 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_teacher_pay_rate WHERE teacher_id = @teacher_id AND course_id = @course_id);

-- 10. 创建积分账户
INSERT INTO edu_points_account (student_id, total_earned, total_spent, balance, create_time, update_time)
SELECT @student_id, 0, 0, 0, NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_points_account WHERE student_id = @student_id);

-- 验证数据
SELECT '科目' AS type, id, name FROM edu_subject
UNION ALL
SELECT '教室' AS type, id, name FROM edu_classroom
UNION ALL
SELECT '课程' AS type, id, name FROM edu_course
UNION ALL
SELECT '班级' AS type, id, name FROM edu_class
UNION ALL
SELECT '学员' AS type, id, name FROM edu_student
UNION ALL
SELECT '教师用户' AS type, id, username FROM sys_user;
