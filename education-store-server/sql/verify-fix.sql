-- ============================================
-- 验证数据修复结果
-- ============================================

SELECT '=== 1. 无效 parent-student 关联 (应为0)' as check_item, COUNT(*) as count FROM edu_student_parent sp LEFT JOIN edu_student s ON sp.student_id = s.id AND s.deleted = 0 LEFT JOIN sys_user u ON sp.user_id = u.id AND u.deleted = 0 WHERE s.id IS NULL OR u.id IS NULL;

SELECT '=== 2. 无效 class_student 关联 (应为0)' as check_item, COUNT(*) as count FROM edu_class_student cs LEFT JOIN edu_student s ON cs.student_id = s.id AND s.deleted = 0 WHERE s.id IS NULL;

SELECT '=== 3. 在学学员未关联家长 (应为0)' as check_item, COUNT(*) as count FROM edu_student s WHERE s.status = 'ENROLLED' AND s.deleted = 0 AND (s.parent_id IS NULL OR s.parent_id = 0) AND NOT EXISTS (SELECT 1 FROM edu_student_parent sp WHERE sp.student_id = s.id AND sp.deleted = 0 AND sp.user_id IS NOT NULL);

SELECT '=== 4. 在学学员课时为0 (应为0)' as check_item, COUNT(*) as count FROM edu_student s WHERE s.status = 'ENROLLED' AND s.deleted = 0 AND (s.total_hours IS NULL OR s.total_hours = 0 OR s.remaining_hours IS NULL OR s.remaining_hours = 0);

SELECT '=== 5. 在学学员无积分账户 (应为0)' as check_item, COUNT(*) as count FROM edu_student s WHERE s.status = 'ENROLLED' AND s.deleted = 0 AND NOT EXISTS (SELECT 1 FROM edu_points_account pa WHERE pa.student_id = s.id);

SELECT '=== 6. 班级有排课但无学员 (应为0)' as check_item, COUNT(*) as count FROM edu_class c WHERE c.deleted = 0 AND EXISTS (SELECT 1 FROM edu_schedule sch WHERE sch.class_id = c.id AND sch.deleted = 0) AND NOT EXISTS (SELECT 1 FROM edu_class_student cs WHERE cs.class_id = c.id AND cs.deleted = 0 AND cs.status = 'ENROLLED');

SELECT '=== 7. 家长用户已创建' as check_item, COUNT(*) as count FROM sys_user WHERE username IN ('parent_xiaohong', 'parent_xiaohong2', 'parent_xiaohua');

SELECT '=== 8. 考勤记录总数' as check_item, COUNT(*) as count FROM edu_attendance WHERE deleted = 0;

SELECT '=== 9. 作业提交记录总数' as check_item, COUNT(*) as count FROM edu_homework_submission WHERE deleted = 0;
