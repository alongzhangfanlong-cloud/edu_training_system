-- =====================================================
-- 初始化10个教师用户（默认密码: 123456）
-- 生成时间: 2026-07-25
-- =====================================================

-- ---------- 教师用户表 sys_user ----------
INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(11, 'wangfang', '$2b$10$EoPTbo5WHzlHQxU8jLMz7uMiP0beEW7/m7B.89wHhGK2a8zDym8yG', '王芳', '王芳老师', 'wangfang@edu.com', '13800001001', 2, 1, '语文学科带头人');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(12, 'liqiang', '$2b$10$3rOQwYEDRC2xPVqGnk1thOUBSixrvlOzUPZ.y0u1x7TLz4oBJyFLK', '李强', '李强老师', 'liqiang@edu.com', '13800001002', 1, 1, '数学教研组长');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(13, 'zhangli', '$2b$10$LIejg..k1W6WB6AAyT85cOH4MS.VcmOToaGRfeCrCta039O2kR.oa', '张丽', '张丽老师', 'zhangli@edu.com', '13800001003', 2, 1, '英语专业八级');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(14, 'chenwei', '$2b$10$dViaHqafJPE/csWYp4RtOOrIHamiZg.u56e/M3XQ5q2fVwMfaIH0O', '陈伟', '陈伟老师', 'chenwei@edu.com', '13800001004', 1, 1, '');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(15, 'liuna', '$2b$10$.CTLD/owbudkKvMl9GwZE.bXoF.d/p56kUUG7DcVsu8V8l6YCrA8G', '刘娜', '刘娜老师', 'liuna@edu.com', '13800001005', 2, 1, '');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(16, 'zhaoming', '$2b$10$oJwtWGP7p0pGAIjgyXXEFexOb9oYmk/VJLnM9Sc5p3NHuwCIO8tQu', '赵明', '赵明老师', 'zhaoming@edu.com', '13800001006', 1, 1, '');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(17, 'sunyue', '$2b$10$HPH.zbHJ01AHXhzMBzuooe6/.9sjjfxIf.sZJaM1cVSXjNvdMUhzi', '孙悦', '孙悦老师', 'sunyue@edu.com', '13800001007', 2, 1, '');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(18, 'zhoujie', '$2b$10$fx71AE33P7ZrzBkynjxE9OjHsA4CBni/WiPxELh.J/L/86RgdKf8C', '周杰', '周杰老师', 'zhoujie@edu.com', '13800001008', 1, 1, '');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(19, 'wulei', '$2b$10$incd2a4qaq1NtWC.PlX0bu94gaQZGP4.KAsccXK8Yz55rjcmWE0pu', '吴磊', '吴磊老师', 'wulei@edu.com', '13800001009', 1, 1, '');

INSERT INTO sys_user (id, username, password, real_name, nick_name, email, mobile, gender, status, remark) VALUES
(20, 'huangjing', '$2b$10$qneptlTfszSB7gBnICrBnegzSouyoOyE.FZOinI41IktvqzyACeru', '黄静', '黄静老师', 'huangjing@edu.com', '13800001010', 2, 1, '美术特级教师');

-- ---------- 教师角色分配 sys_user_role (role_id=3 → 教师) ----------
INSERT INTO sys_user_role (user_id, role_id) VALUES (11, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (12, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (13, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (14, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (15, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (16, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (17, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (18, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (19, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (20, 3);

-- ---------- 教师档案表 edu_teacher ----------
INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(1, 11, '王芳', 'ACTIVE', '语文', '2023-02-15', 120, '13800001001', '语文学科带头人');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(2, 12, '李强', 'ACTIVE', '数学', '2022-09-01', 150, '13800001002', '数学教研组长');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(3, 13, '张丽', 'ACTIVE', '英语', '2023-07-10', 130, '13800001003', '英语专业八级');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(4, 14, '陈伟', 'ACTIVE', '物理', '2024-01-05', 140, '13800001004', '');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(5, 15, '刘娜', 'ACTIVE', '化学', '2023-03-20', 135, '13800001005', '');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(6, 16, '赵明', 'ACTIVE', '生物', '2022-11-01', 125, '13800001006', '');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(7, 17, '孙悦', 'ACTIVE', '历史', '2024-03-15', 110, '13800001007', '');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(8, 18, '周杰', 'ACTIVE', '地理', '2023-08-01', 115, '13800001008', '');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(9, 19, '吴磊', 'ACTIVE', '政治', '2024-06-01', 110, '13800001009', '');

INSERT INTO edu_teacher (id, user_id, real_name, status, subjects, hire_date, default_pay_per_lesson, phone, remark) VALUES
(10, 20, '黄静', 'ACTIVE', '美术', '2023-05-10', 100, '13800001010', '美术特级教师');

