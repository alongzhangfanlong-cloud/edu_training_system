-- =====================================================
-- 教育培训管理系统 - 初始数据
-- =====================================================

-- 系统配置
INSERT INTO `system_configs` (`config_key`, `config_value`, `description`) VALUES
('leave_free_hours', '24', '免费请假需提前小时数'),
('leave_charge_hours', '2', '收费请假需提前小时数'),
('checkin_qr_expire_minutes', '5', '签到二维码有效期(分钟)'),
('checkin_distance_meters', '500', '签到允许最大距离(米)'),
('hour_warning_threshold', '5', '课时预警阈值'),
('makeup_valid_days', '30', '补课有效期(天)');

-- 默认管理员账号 (phone: 13800000000, code: 123456)
-- 实际环境中密码/验证码通过短信发送
