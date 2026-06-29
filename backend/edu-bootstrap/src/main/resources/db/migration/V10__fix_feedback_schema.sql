-- =====================================================
-- V10: 修复class_feedbacks表约束 + 补充缺失的学员数据
-- =====================================================

-- class_id和teacher_id允许为空（反馈提交时可不填）
ALTER TABLE `class_feedbacks` MODIFY `class_id` BIGINT NULL;
ALTER TABLE `class_feedbacks` MODIFY `teacher_id` BIGINT NULL;

-- 修复学员1和2的student_no冲突(如果已修复则跳过)
UPDATE `students` SET `student_no` = 'STU20260001A' WHERE `id` = 1 AND `student_no` = 'STU202600001';
UPDATE `students` SET `student_no` = 'STU20260002B' WHERE `id` = 2 AND `student_no` = 'STU202600002';

-- 补充V4中因duplicate key跳过的学员3和4
INSERT IGNORE INTO `students` (`id`, `student_no`, `name`, `gender`, `grade`, `school`, `phone`, `tags`, `status`, `source`, `remark`) VALUES
(3, 'STU202600001', '李明', 1, '初三', '北京市第一中学', '13800010001', '["VIP","数学薄弱"]', 1, '官网', '需要重点辅导数学'),
(4, 'STU202600002', '王小红', 2, '初二', '北京市第二中学', '13800010002', '["英语尖子"]', 1, '转介绍', '英语成绩优秀');
INSERT IGNORE INTO `student_parents` (`student_id`, `parent_name`, `relation`, `phone`, `is_primary`) VALUES
(3, '李建国', '父亲', '13900020001', 1), (3, '张丽', '母亲', '13900020002', 0), (4, '王大明', '父亲', '13900020003', 1);
INSERT IGNORE INTO `student_classes` (`student_id`, `class_id`, `total_hours`, `used_hours`, `remain_hours`, `gift_hours`, `status`) VALUES
(3, 3, 40.0, 12.0, 28.0, 2.0, 1), (4, 5, 36.0, 8.0, 28.0, 0, 1), (3, 6, 20.0, 8.0, 12.0, 0, 1);
