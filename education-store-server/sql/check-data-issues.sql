-- ============================================
-- 业务闭环数据问题检查脚本
-- ============================================

-- 1. 查询无效的 parent-student 关联
SELECT '=== 1. 无效 parent-student 关联 ===' as info;
SELECT sp.id, sp.student_id, sp.user_id as parent_user_id, s.name as student_name, u.real_name as parent_name
FROM edu_student_parent sp
LEFT JOIN edu_student s ON sp.student_id = s.id AND s.deleted = 0
LEFT JOIN sys_user u ON sp.user_id = u.id AND u.deleted = 0
WHERE s.id IS NULL OR u.id IS NULL;

-- 2. 查询无效的 class_student 关联
SELECT '=== 2. 无效 class_student 关联 ===' as info;
SELECT cs.id, cs.class_id, cs.student_id, s.name as student_name
FROM edu_class_student cs
LEFT JOIN edu_student s ON cs.student_id = s.id AND s.deleted = 0
WHERE s.id IS NULL;

-- 3. 在学学员未关联家长
SELECT '=== 3. 在学学员未关联家长 ===' as info;
SELECT s.id, s.name, s.status, s.parent_id, s.parent_name
FROM edu_student s
WHERE s.status = 'ENROLLED' AND s.deleted = 0
  AND (s.parent_id IS NULL OR s.parent_id = 0)
  AND NOT EXISTS (SELECT 1 FROM edu_student_parent sp WHERE sp.student_id = s.id AND sp.deleted = 0 AND sp.user_id IS NOT NULL);

-- 4. 在学学员课时为0
SELECT '=== 4. 在学学员课时为0 ===' as info;
SELECT s.id, s.name, s.total_hours, s.remaining_hours
FROM edu_student s
WHERE s.status = 'ENROLLED' AND s.deleted = 0
  AND (s.total_hours IS NULL OR s.total_hours = 0 OR s.remaining_hours IS NULL OR s.remaining_hours = 0);

-- 5. 在学学员无积分账户
SELECT '=== 5. 在学学员无积分账户 ===' as info;
SELECT s.id, s.name
FROM edu_student s
WHERE s.status = 'ENROLLED' AND s.deleted = 0
  AND NOT EXISTS (SELECT 1 FROM edu_points_account pa WHERE pa.student_id = s.id);

-- 6. 班级有排课但无学员
SELECT '=== 6. 班级有排课但无学员 ===' as info;
SELECT DISTINCT c.id, c.name, c.code,
  (SELECT COUNT(*) FROM edu_schedule sch WHERE sch.class_id = c.id AND sch.deleted = 0) as schedule_count,
  (SELECT COUNT(*) FROM edu_class_student cs WHERE cs.class_id = c.id AND cs.deleted = 0 AND cs.status = 'ACTIVE') as student_count
FROM edu_class c
WHERE c.deleted = 0
  AND EXISTS (SELECT 1 FROM edu_schedule sch WHERE sch.class_id = c.id AND sch.deleted = 0)
  AND NOT EXISTS (SELECT 1 FROM edu_class_student cs WHERE cs.class_id = c.id AND cs.deleted = 0 AND cs.status = 'ACTIVE');

-- 7. 历史排课无考勤记录
SELECT '=== 7. 历史排课无考勤记录 ===' as info;
SELECT sch.id, sch.class_id, sch.schedule_date, sch.start_time, sch.end_time, c.name as class_name
FROM edu_schedule sch
LEFT JOIN edu_class c ON sch.class_id = c.id
WHERE sch.deleted = 0
  AND sch.schedule_date < CURDATE()
  AND sch.status != 'CANCELLED'
  AND NOT EXISTS (SELECT 1 FROM edu_attendance a WHERE a.schedule_id = sch.id AND a.deleted = 0)
ORDER BY sch.schedule_date DESC
LIMIT 20;

-- 8. 作业无提交记录
SELECT '=== 8. 作业无提交记录 ===' as info;
SELECT h.id, h.title, h.class_id, c.name as class_name, h.assign_date
FROM edu_homework h
LEFT JOIN edu_class c ON h.class_id = c.id
WHERE h.deleted = 0
  AND NOT EXISTS (SELECT 1 FROM edu_homework_submission hs WHERE hs.homework_id = h.id AND hs.deleted = 0);
