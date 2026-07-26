-- =====================================================
-- 小程序家长端测试数据脚本
-- 用于验证家长端全量业务闭环
-- =====================================================

USE edu_system;

-- =====================================================
-- 1. 创建家长用户（密码: 123456，BCrypt加密）
-- =====================================================
-- parent01: 张爸爸（关联学生1和学生2）
-- parent02: 李妈妈（关联学生3）
INSERT INTO sys_user (username, password, real_name, phone, email, role, status, create_time, update_time)
VALUES 
('parent01', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张爸爸', '13800138001', 'parent01@test.com', 'PARENT', 0, NOW(), NOW()),
('parent02', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李妈妈', '13800138002', 'parent02@test.com', 'PARENT', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 获取家长用户ID（用于后续关联）
SET @parent01_id = (SELECT id FROM sys_user WHERE username = 'parent01');
SET @parent02_id = (SELECT id FROM sys_user WHERE username = 'parent02');

-- =====================================================
-- 2. 建立亲子关系
-- =====================================================
-- 获取现有学生ID（假设已有测试学生，使用前3个学生）
SET @student1_id = (SELECT id FROM edu_student WHERE name LIKE '%测%' OR name LIKE '%测试%' LIMIT 1);
SET @student2_id = (SELECT id FROM edu_student WHERE id > @student1_id LIMIT 1);
SET @student3_id = (SELECT id FROM edu_student WHERE id > @student2_id LIMIT 1);

-- 如果没有测试学生，创建几个
INSERT IGNORE INTO edu_student (name, gender, grade, school, mobile, total_hours, remaining_hours, status, create_time, update_time)
SELECT '张小明', 1, '三年级', '实验小学', '13900139001', 40, 32, 'STUDYING', NOW(), NOW()
WHERE @student1_id IS NULL;

INSERT IGNORE INTO edu_student (name, gender, grade, school, mobile, total_hours, remaining_hours, status, create_time, update_time)
SELECT '张小红', 0, '一年级', '实验小学', '13900139002', 30, 28, 'STUDYING', NOW(), NOW()
WHERE @student2_id IS NULL OR @student2_id = @student1_id;

INSERT IGNORE INTO edu_student (name, gender, grade, school, mobile, total_hours, remaining_hours, status, create_time, update_time)
SELECT '李小强', 1, '二年级', '阳光小学', '13900139003', 36, 30, 'STUDYING', NOW(), NOW()
WHERE @student3_id IS NULL OR @student3_id <= @student2_id;

-- 重新获取学生ID
SET @student1_id = (SELECT id FROM edu_student WHERE name = '张小明' LIMIT 1);
SET @student2_id = (SELECT id FROM edu_student WHERE name = '张小红' LIMIT 1);
SET @student3_id = (SELECT id FROM edu_student WHERE name = '李小强' LIMIT 1);

-- 建立亲子关系
INSERT INTO edu_student_parent (user_id, student_id, relation, create_time)
VALUES 
(@parent01_id, @student1_id, '父亲', NOW()),
(@parent01_id, @student2_id, '父亲', NOW()),
(@parent02_id, @student3_id, '母亲', NOW())
ON DUPLICATE KEY UPDATE relation = VALUES(relation);

-- =====================================================
-- 3. 确保学生有班级关联
-- =====================================================
-- 获取现有班级ID
SET @class1_id = (SELECT id FROM edu_class LIMIT 1);

-- 如果没有班级，创建一个
INSERT IGNORE INTO edu_class (name, course_id, teacher_id, classroom_id, status, create_time, update_time)
SELECT '测试三年级数学班', 1, 1, 1, 'NORMAL', NOW(), NOW()
WHERE @class1_id IS NULL;

SET @class1_id = (SELECT id FROM edu_class LIMIT 1);

-- 为学生分配班级
INSERT IGNORE INTO edu_class_student (class_id, student_id, status, enroll_date, create_time)
VALUES 
(@class1_id, @student1_id, 'ENROLLED', CURDATE(), NOW()),
(@class1_id, @student2_id, 'ENROLLED', CURDATE(), NOW()),
(@class1_id, @student3_id, 'ENROLLED', CURDATE(), NOW());

-- =====================================================
-- 4. 添加今日排课数据
-- =====================================================
-- 获取教师和课程ID
SET @teacher1_id = (SELECT id FROM edu_teacher LIMIT 1);
SET @course1_id = (SELECT id FROM edu_course LIMIT 1);
SET @room1_id = (SELECT id FROM edu_classroom LIMIT 1);

INSERT INTO edu_schedule (class_id, course_id, teacher_id, room_id, schedule_date, start_time, end_time, status, create_time, update_time)
VALUES 
(@class1_id, @course1_id, @teacher1_id, @room1_id, CURDATE(), '09:00', '10:30', 'NORMAL', NOW(), NOW()),
(@class1_id, @course1_id, @teacher1_id, @room1_id, CURDATE(), '14:00', '15:30', 'NORMAL', NOW(), NOW()),
(@class1_id, @course1_id, @teacher1_id, @room1_id, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00', '10:30', 'NORMAL', NOW(), NOW()),
(@class1_id, @course1_id, @teacher1_id, @room1_id, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:00', '11:30', 'NORMAL', NOW(), NOW()),
(@class1_id, @course1_id, @teacher1_id, @room1_id, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '14:00', '15:30', 'NORMAL', NOW(), NOW());

-- 获取今日排课ID
SET @schedule_today1 = (SELECT id FROM edu_schedule WHERE schedule_date = CURDATE() ORDER BY id LIMIT 1);
SET @schedule_today2 = (SELECT id FROM edu_schedule WHERE schedule_date = CURDATE() ORDER BY id DESC LIMIT 1);

-- =====================================================
-- 5. 添加考勤数据（今日）
-- =====================================================
INSERT INTO edu_attendance (schedule_id, class_id, student_id, status, signin_time, remark, create_time, update_time)
VALUES 
(@schedule_today1, @class1_id, @student1_id, 1, CONCAT(CURDATE(), ' 08:55:00'), '准时到校', NOW(), NOW()),
(@schedule_today1, @class1_id, @student2_id, 2, CONCAT(CURDATE(), ' 09:10:00'), '迟到10分钟', NOW(), NOW()),
(@schedule_today1, @class1_id, @student3_id, 1, CONCAT(CURDATE(), ' 08:50:00'), '准时到校', NOW(), NOW());

-- 添加历史考勤数据
INSERT INTO edu_attendance (schedule_id, class_id, student_id, status, signin_time, remark, create_time, update_time)
SELECT @schedule_today1, @class1_id, @student1_id, 1, CONCAT(DATE_SUB(CURDATE(), INTERVAL n DAY), ' 08:55:00'), '', DATE_SUB(CURDATE(), INTERVAL n DAY), DATE_SUB(CURDATE(), INTERVAL n DAY)
FROM (
    SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) days
WHERE NOT EXISTS (
    SELECT 1 FROM edu_attendance 
    WHERE student_id = @student1_id 
    AND DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL n DAY)
);

-- =====================================================
-- 6. 添加作业数据
-- =====================================================
INSERT INTO edu_homework (class_id, course_id, teacher_id, title, content, status, create_time, update_time)
VALUES 
(@class1_id, @course1_id, @teacher1_id, '数学练习册第15页', '完成练习册第15页的所有题目，家长签字明天交', 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@class1_id, @course1_id, @teacher1_id, '口算练习', '完成20道口算题，时间5分钟', 'PUBLISHED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(@class1_id, @course1_id, @teacher1_id, '预习新课', '预习明天要讲的第3章内容', 'PUBLISHED', NOW(), NOW());

-- =====================================================
-- 7. 添加考试成绩数据
-- =====================================================
-- 创建考试记录
INSERT INTO edu_exam_score (exam_name, exam_type, class_id, subject_id, total_score, exam_date, create_time, update_time)
VALUES 
('第一单元测试', 'UNIT_TEST', @class1_id, 1, 100, DATE_SUB(CURDATE(), INTERVAL 7 DAY), NOW(), NOW()),
('期中考试', 'MIDTERM', @class1_id, 1, 100, DATE_SUB(CURDATE(), INTERVAL 30 DAY), NOW(), NOW());

SET @exam1_id = (SELECT id FROM edu_exam_score WHERE exam_name = '第一单元测试' LIMIT 1);
SET @exam2_id = (SELECT id FROM edu_exam_score WHERE exam_name = '期中考试' LIMIT 1);

-- 考试成绩关联学生
INSERT INTO edu_exam_score_student (exam_score_id, student_id, score, create_time)
VALUES 
(@exam1_id, @student1_id, 92, NOW()),
(@exam1_id, @student2_id, 88, NOW()),
(@exam1_id, @student3_id, 95, NOW()),
(@exam2_id, @student1_id, 89, NOW()),
(@exam2_id, @student2_id, 91, NOW()),
(@exam2_id, @student3_id, 87, NOW());

-- =====================================================
-- 8. 添加课堂评价数据
-- =====================================================
-- 创建评价主表
INSERT INTO edu_evaluation (schedule_id, class_id, teacher_id, summary, status, create_time, update_time)
VALUES 
(@schedule_today1, @class1_id, @teacher1_id, '今天同学们表现都很好，积极回答问题，课堂气氛活跃。', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@schedule_today1, @class1_id, @teacher1_id, '张小明同学今天表现特别突出，专注度很高。', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

SET @eval1_id = (SELECT id FROM edu_evaluation WHERE summary LIKE '%同学们表现都很好%' LIMIT 1);
SET @eval2_id = (SELECT id FROM edu_evaluation WHERE summary LIKE '%张小明同学今天表现特别突出%' LIMIT 1);

-- 评价学生明细
INSERT INTO edu_evaluation_student (evaluation_id, student_id, focus, participation, completion, overall, star_rating, comment, create_time)
VALUES 
(@eval1_id, @student1_id, 5, 5, 4, 5, 5, '上课认真，积极回答问题', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@eval1_id, @student2_id, 4, 4, 5, 4, 4, '作业完成很好，继续加油', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@eval1_id, @student3_id, 5, 5, 5, 5, 5, '表现优秀，是同学们的榜样', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@eval2_id, @student1_id, 5, 5, 5, 5, 5, '今天表现特别棒！', DATE_SUB(NOW(), INTERVAL 3 DAY));

-- =====================================================
-- 9. 添加请假记录
-- =====================================================
INSERT INTO edu_leave_student (student_id, leave_date, leave_type, reason, status, applicant_type, applicant_id, create_time, update_time)
VALUES 
(@student2_id, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 1, '生病请假去医院', 0, 'PARENT', @parent01_id, NOW(), NOW()),
(@student3_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 2, '家里有事请假半天', 1, 'PARENT', @parent02_id, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =====================================================
-- 10. 添加报名记录
-- =====================================================
INSERT INTO edu_enrollment (student_id, course_id, class_id, amount, enrollment_date, payment_status, remark, create_time, update_time)
VALUES 
(@student1_id, @course1_id, @class1_id, 3600, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'PAID', '春季班报名', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
(@student2_id, @course1_id, @class1_id, 3600, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 'PAID', '春季班报名', DATE_SUB(CURDATE(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
(@student3_id, @course1_id, @class1_id, 3600, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'PAID', '春季班报名', DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY));

-- =====================================================
-- 11. 添加积分账户和积分记录
-- =====================================================
INSERT INTO edu_points_account (student_id, balance, total_earned, total_spent, create_time, update_time)
VALUES 
(@student1_id, 250, 350, 100, NOW(), NOW()),
(@student2_id, 180, 280, 100, NOW(), NOW()),
(@student3_id, 320, 420, 100, NOW(), NOW())
ON DUPLICATE KEY UPDATE update_time = NOW();

-- 积分日志
INSERT INTO edu_points_log (student_id, change_type, amount, balance_after, source_type, source_id, remark, create_time)
VALUES 
(@student1_id, 'EARN', 20, 350, 'ATTENDANCE', @schedule_today1, '准时到校奖励', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@student1_id, 'EARN', 30, 330, 'EVALUATION', @eval1_id, '课堂评价优秀奖励', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@student1_id, 'SPEND', -100, 300, 'EXCHANGE', 1, '兑换文具', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(@student2_id, 'EARN', 10, 280, 'ATTENDANCE', @schedule_today1, '迟到扣分后奖励', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@student2_id, 'EARN', 20, 270, 'HOMEWORK', 1, '作业完成优秀', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(@student3_id, 'EARN', 20, 420, 'ATTENDANCE', @schedule_today1, '准时到校奖励', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@student3_id, 'EARN', 50, 400, 'EXAM', @exam1_id, '考试成绩优秀奖励', DATE_SUB(CURDATE(), INTERVAL 6 DAY));

-- =====================================================
-- 12. 添加通知消息数据
-- =====================================================
CREATE TABLE IF NOT EXISTS edu_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    type VARCHAR(50) COMMENT '消息类型：SIGN_IN/EVALUATION/HOMEWORK/EXAM/LEAVE/EXCHANGE/SYSTEM',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0未读 1已读',
    related_id BIGINT COMMENT '关联业务ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

INSERT INTO edu_notification (user_id, title, content, type, is_read, create_time)
VALUES 
(@parent01_id, '考勤通知', '张小明今日已准时到校', 'SIGN_IN', 0, NOW()),
(@parent01_id, '课堂评价', '张小明收到了新的课堂评价', 'EVALUATION', 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@parent01_id, '作业通知', '老师布置了新的作业：数学练习册第15页', 'HOMEWORK', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@parent01_id, '考试成绩', '第一单元测试成绩已发布，张小明得分92分', 'EXAM', 1, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
(@parent01_id, '系统通知', '欢迎使用教育管理平台小程序！', 'SYSTEM', 0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(@parent02_id, '考勤通知', '李小强今日已准时到校', 'SIGN_IN', 0, NOW()),
(@parent02_id, '请假审批', '您的请假申请已通过', 'LEAVE', 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(@parent02_id, '积分变动', '李小强获得50积分（考试成绩优秀奖励）', 'EXCHANGE', 0, DATE_SUB(CURDATE(), INTERVAL 6 DAY));

-- =====================================================
-- 13. 添加时间线数据
-- =====================================================
CREATE TABLE IF NOT EXISTS edu_timeline (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    type VARCHAR(50) NOT NULL COMMENT '事件类型',
    title VARCHAR(200) NOT NULL COMMENT '事件标题',
    content TEXT COMMENT '事件内容',
    event_time DATETIME NOT NULL COMMENT '事件时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_id (student_id),
    INDEX idx_event_time (event_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成长时间线表';

INSERT INTO edu_timeline (student_id, type, title, content, event_time)
VALUES 
(@student1_id, 'ATTENDANCE', '准时到校', '今天准时到校，获得20积分奖励', NOW()),
(@student1_id, 'EVALUATION', '收到课堂评价', '今天课堂表现优秀，获得5星评价', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@student1_id, 'HOMEWORK', '作业已发布', '数学练习册第15页', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(@student1_id, 'EXAM', '第一单元测试', '得分92分，继续加油！', DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
(@student2_id, 'ATTENDANCE', '迟到', '今天迟到10分钟，下次要注意哦', NOW()),
(@student2_id, 'LEAVE', '请假申请中', '申请2天后请假，等待审批', NOW()),
(@student3_id, 'ATTENDANCE', '准时到校', '今天准时到校，获得20积分奖励', NOW()),
(@student3_id, 'EXAM', '考试奖励', '考试成绩优秀，获得50积分奖励', DATE_SUB(CURDATE(), INTERVAL 6 DAY));

-- =====================================================
-- 完成
-- =====================================================
SELECT '家长端测试数据添加完成！' AS message;
SELECT '家长账号：parent01/123456, parent02/123456' AS accounts;
SELECT '学生：张小明、张小红（家长parent01），李小强（家长parent02）' AS students;
