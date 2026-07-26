-- 插入一条今天的排课记录，用于测试签到流程
-- 班级ID=1, 课程ID=1, 教师ID=2(teacher01), 教室ID=1
-- 日期=今天，时间=09:00-10:30，状态=NORMAL，未消课

INSERT INTO edu_schedule (plan_id, class_id, course_id, teacher_id, room_id, schedule_date, start_time, end_time, status, schedule_type, consumed, create_time, update_time, deleted)
SELECT NULL, 1, 1, 2, 1, CURDATE(), '09:00:00', '10:30:00', 'NORMAL', 'NORMAL', 0, NOW(), NOW(), 0
FROM DUAL WHERE NOT EXISTS (
  SELECT 1 FROM edu_schedule WHERE class_id = 1 AND schedule_date = CURDATE() AND deleted = 0
);

-- 验证插入
SELECT id, class_id, schedule_date, start_time, end_time, status, consumed FROM edu_schedule;
