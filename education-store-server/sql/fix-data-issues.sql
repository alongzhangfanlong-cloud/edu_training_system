-- ============================================
-- 业务闭环数据修复脚本 (V2 - 安全版本)
-- 使用 INSERT IGNORE / ON DUPLICATE KEY UPDATE 避免重复
-- ============================================

-- ===== 1. 清理无效的 parent-student 关联 =====
DELETE FROM edu_student_parent WHERE id IN (1, 2, 3, 4, 5, 6, 7);

-- ===== 2. 清理无效的 class_student 关联 =====
DELETE FROM edu_class_student WHERE id IN (1, 2, 3, 4, 5);

-- ===== 3. 为在学学员补齐家长关联 =====
-- 先创建家长用户 (gender: 0=未知 1=男 2=女)
INSERT IGNORE INTO sys_user (username, password, real_name, gender, mobile, status, create_by, create_time) VALUES
('parent_xiaohong', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '孙女士', 2, '13800000020', 1, 'system', NOW()),
('parent_xiaohong2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王女士', 2, '13800000021', 1, 'system', NOW()),
('parent_xiaohua', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李先生', 1, '13800000022', 1, 'system', NOW());

-- 获取家长用户ID
SELECT id INTO @parent_id1 FROM sys_user WHERE username = 'parent_xiaohong' LIMIT 1;
SELECT id INTO @parent_id2 FROM sys_user WHERE username = 'parent_xiaohong2' LIMIT 1;
SELECT id INTO @parent_id3 FROM sys_user WHERE username = 'parent_xiaohua' LIMIT 1;

-- 更新学生的parent_id
UPDATE edu_student SET parent_id = @parent_id1, parent_name = '孙女士', parent_mobile = '13800000020', parent_relation = 'MOTHER' WHERE id = 20;
UPDATE edu_student SET parent_id = @parent_id2, parent_name = '王女士', parent_mobile = '13800000021', parent_relation = 'MOTHER' WHERE id = 21;
UPDATE edu_student SET parent_id = @parent_id3, parent_name = '李先生', parent_mobile = '13800000022', parent_relation = 'FATHER' WHERE id = 22;

-- 插入家长-学生关联 (避免重复)
INSERT IGNORE INTO edu_student_parent (student_id, user_id, name, relation, mobile, sort, create_by, create_time)
SELECT 20, @parent_id1, '孙女士', 'MOTHER', '13800000020', 0, 'system', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_student_parent WHERE student_id = 20 AND user_id = @parent_id1);

INSERT IGNORE INTO edu_student_parent (student_id, user_id, name, relation, mobile, sort, create_by, create_time)
SELECT 21, @parent_id2, '王女士', 'MOTHER', '13800000021', 0, 'system', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_student_parent WHERE student_id = 21 AND user_id = @parent_id2);

INSERT IGNORE INTO edu_student_parent (student_id, user_id, name, relation, mobile, sort, create_by, create_time)
SELECT 22, @parent_id3, '李先生', 'FATHER', '13800000022', 0, 'system', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_student_parent WHERE student_id = 22 AND user_id = @parent_id3);

-- 为家长用户分配家长角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u, sys_role r
WHERE u.username IN ('parent_xiaohong', 'parent_xiaohong2', 'parent_xiaohua') AND r.code = 'parent';

-- ===== 4. 为在学学员补齐课时初始化 =====
UPDATE edu_student SET total_hours = 30, remaining_hours = 30 WHERE id = 9 AND (total_hours = 0 OR total_hours IS NULL);
UPDATE edu_student SET total_hours = 40, remaining_hours = 40 WHERE id = 19 AND (total_hours = 0 OR total_hours IS NULL);
UPDATE edu_student SET total_hours = 24, remaining_hours = 24 WHERE id = 20 AND (total_hours = 0 OR total_hours IS NULL);

-- 插入课时日志记录 (避免重复)
INSERT IGNORE INTO edu_student_hours_log (student_id, change_type, change_amount, balance_after, source_type, source_id, operator_id, remark, create_time)
SELECT 9, 'INCREASE', 30, 30, 'ENROLLMENT', NULL, 1, '报名赠送课时', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_student_hours_log WHERE student_id = 9 AND source_type = 'ENROLLMENT');

INSERT IGNORE INTO edu_student_hours_log (student_id, change_type, change_amount, balance_after, source_type, source_id, operator_id, remark, create_time)
SELECT 19, 'INCREASE', 40, 40, 'ENROLLMENT', NULL, 1, '报名赠送课时', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_student_hours_log WHERE student_id = 19 AND source_type = 'ENROLLMENT');

INSERT IGNORE INTO edu_student_hours_log (student_id, change_type, change_amount, balance_after, source_type, source_id, operator_id, remark, create_time)
SELECT 20, 'INCREASE', 24, 24, 'ENROLLMENT', NULL, 1, '报名赠送课时', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_student_hours_log WHERE student_id = 20 AND source_type = 'ENROLLMENT');

-- ===== 5. 为在学学员补齐积分账户 =====
INSERT IGNORE INTO edu_points_account (student_id, total_earned, total_spent, balance, create_time, update_time)
SELECT 9, 100, 0, 100, NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_points_account WHERE student_id = 9);

INSERT IGNORE INTO edu_points_account (student_id, total_earned, total_spent, balance, create_time, update_time)
SELECT 19, 150, 0, 150, NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_points_account WHERE student_id = 19);

INSERT IGNORE INTO edu_points_account (student_id, total_earned, total_spent, balance, create_time, update_time)
SELECT 20, 80, 0, 80, NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_points_account WHERE student_id = 20);

-- 插入积分日志
INSERT IGNORE INTO edu_points_log (student_id, change_type, amount, balance_after, source_type, source_id, remark, create_time)
SELECT 9, 'EARN', 100, 100, 'SIGN_UP', NULL, '注册赠送积分', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_points_log WHERE student_id = 9 AND source_type = 'SIGN_UP');

INSERT IGNORE INTO edu_points_log (student_id, change_type, amount, balance_after, source_type, source_id, remark, create_time)
SELECT 19, 'EARN', 150, 150, 'SIGN_UP', NULL, '注册赠送积分', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_points_log WHERE student_id = 19 AND source_type = 'SIGN_UP');

INSERT IGNORE INTO edu_points_log (student_id, change_type, amount, balance_after, source_type, source_id, remark, create_time)
SELECT 20, 'EARN', 80, 80, 'SIGN_UP', NULL, '注册赠送积分', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_points_log WHERE student_id = 20 AND source_type = 'SIGN_UP');

-- ===== 6. 为无学员的班级补充学员 =====
INSERT IGNORE INTO edu_class_student (class_id, student_id, join_time, status, remark, create_time, update_time)
SELECT 1, 20, '2026-07-01 09:00:00', 'ENROLLED', '系统补录', NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_class_student WHERE class_id = 1 AND student_id = 20);

INSERT IGNORE INTO edu_class_student (class_id, student_id, join_time, status, remark, create_time, update_time)
SELECT 6, 9, '2026-07-01 09:00:00', 'ENROLLED', '系统补录', NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_class_student WHERE class_id = 6 AND student_id = 9);

INSERT IGNORE INTO edu_class_student (class_id, student_id, join_time, status, remark, create_time, update_time)
SELECT 7, 19, '2026-07-01 09:00:00', 'ENROLLED', '系统补录', NOW(), NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM edu_class_student WHERE class_id = 7 AND student_id = 19);

-- ===== 7. 为历史排课补充考勤记录 =====
-- status: 0=出勤(PRESENT), 1=迟到, 2=缺勤, 3=请假
INSERT IGNORE INTO edu_attendance (schedule_id, class_id, student_id, status, signin_time, operator_id, operator_type, remark, create_time, update_time)
SELECT 
    sch.id as schedule_id,
    sch.class_id,
    cs.student_id,
    0 as status,
    CONCAT(sch.schedule_date, ' ', sch.start_time) as signin_time,
    1 as operator_id,
    'ADMIN' as operator_type,
    '系统补录' as remark,
    NOW() as create_time,
    NOW() as update_time
FROM edu_schedule sch
JOIN edu_class_student cs ON sch.class_id = cs.class_id AND cs.status = 'ENROLLED' AND cs.deleted = 0
WHERE sch.deleted = 0
  AND sch.schedule_date < CURDATE()
  AND sch.status != 'CANCELLED'
  AND NOT EXISTS (
      SELECT 1 FROM edu_attendance a 
      WHERE a.schedule_id = sch.id AND a.student_id = cs.student_id AND a.deleted = 0
  );

-- ===== 8. 为作业补充提交记录 =====
INSERT IGNORE INTO edu_homework_submission (homework_id, student_id, content, status, score, comment, submitted_at, graded_at, graded_by, create_time, update_time)
SELECT 
    h.id as homework_id,
    cs.student_id,
    '作业已完成，详见附件' as content,
    'GRADED' as status,
    FLOOR(80 + RAND() * 20) as score,
    '完成良好' as comment,
    DATE_ADD(h.create_time, INTERVAL 1 DAY) as submitted_at,
    DATE_ADD(h.create_time, INTERVAL 2 DAY) as graded_at,
    h.teacher_id as graded_by,
    NOW() as create_time,
    NOW() as update_time
FROM edu_homework h
JOIN edu_class_student cs ON h.class_id = cs.class_id AND cs.status = 'ENROLLED' AND cs.deleted = 0
WHERE h.deleted = 0
  AND NOT EXISTS (
      SELECT 1 FROM edu_homework_submission hs 
      WHERE hs.homework_id = h.id AND hs.student_id = cs.student_id AND hs.deleted = 0
  );
