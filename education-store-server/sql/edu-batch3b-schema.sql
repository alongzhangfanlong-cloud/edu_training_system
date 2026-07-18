-- ============================================
-- Batch 3b: N05 家校沟通 — 数据表定义
-- 数据库：MySQL 8.0 + InnoDB
-- 字符集：utf8mb4
-- 日期：2026-07-18
-- ============================================

-- 建表顺序：homework → attachment → view_log → evaluation → evaluation_student → teacher_evaluation → exam_score → exam_score_student

-- ============================================
-- N05.1 课后作业
-- ============================================

-- 1. 作业表
CREATE TABLE edu_homework (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    class_id        BIGINT          NOT NULL                 COMMENT '关联班级ID → edu_class.id',
    course_id       BIGINT          NOT NULL                 COMMENT '关联课程ID → edu_course.id',
    schedule_id     BIGINT          NULL                     COMMENT '关联排课记录ID → edu_schedule.id',
    title           VARCHAR(200)    NOT NULL                 COMMENT '作业标题',
    content         TEXT            NOT NULL                 COMMENT '作业内容（最长5000字）',
    teacher_id      BIGINT          NOT NULL                 COMMENT '布置教师ID → sys_user.id',
    is_updated      TINYINT         NOT NULL DEFAULT 0       COMMENT '是否被编辑更新过: 0=否, 1=是',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_class_id (class_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_schedule_id (schedule_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课后作业';

-- 2. 作业附件表
CREATE TABLE edu_homework_attachment (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    homework_id     BIGINT          NOT NULL                 COMMENT '关联作业ID',
    file_name       VARCHAR(200)    NOT NULL                 COMMENT '原始文件名',
    file_url        VARCHAR(500)    NOT NULL                 COMMENT '文件存储URL',
    file_size       BIGINT          NOT NULL                 COMMENT '文件大小（字节）',
    file_type       VARCHAR(20)     NOT NULL                 COMMENT '文件类型: IMAGE/DOCUMENT',
    mime_type       VARCHAR(100)    NULL                     COMMENT 'MIME类型',
    sort_order      INT             NOT NULL DEFAULT 0       COMMENT '排序号',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_homework_id (homework_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='作业附件';

-- 3. 家长查阅记录表
CREATE TABLE edu_parent_view_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    parent_user_id  BIGINT          NOT NULL                 COMMENT '家长用户ID → sys_user.id',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID → edu_student.id',
    biz_type        VARCHAR(20)     NOT NULL                 COMMENT '业务类型: EVALUATION/EXAM/HOMEWORK',
    biz_id          BIGINT          NOT NULL                 COMMENT '业务记录ID',
    first_view_time DATETIME        NOT NULL                 COMMENT '首次查看时间',
    last_view_time  DATETIME        NOT NULL                 COMMENT '最近查看时间',
    view_count      INT             NOT NULL DEFAULT 1       COMMENT '查看次数',
    PRIMARY KEY (id),
    UNIQUE KEY uk_parent_view (parent_user_id, student_id, biz_type, biz_id),
    KEY idx_student_id (student_id),
    KEY idx_biz (biz_type, biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家长查阅记录';

-- ============================================
-- N05.2 教评学
-- ============================================

-- 4. 课堂评价主表
CREATE TABLE edu_evaluation (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    schedule_id     BIGINT          NOT NULL                 COMMENT '关联排课记录ID → edu_schedule.id',
    class_id        BIGINT          NOT NULL                 COMMENT '关联班级ID（冗余方便查询）',
    teacher_id      BIGINT          NOT NULL                 COMMENT '授课教师ID → sys_user.id',
    summary         TEXT            NULL                     COMMENT '课堂总结（30-2000字）',
    status          TINYINT         NOT NULL DEFAULT 0       COMMENT '状态: 0=草稿, 1=已提交',
    submit_time     DATETIME        NULL                     COMMENT '提交时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_schedule_id (schedule_id),
    KEY idx_class_id (class_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课堂评价';

-- 5. 学生评价明细表
CREATE TABLE edu_evaluation_student (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    evaluation_id   BIGINT          NOT NULL                 COMMENT '关联评价ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID → edu_student.id',
    star_rating     DECIMAL(2,1)    NOT NULL                 COMMENT '星级评分 1.0-5.0，0.5步进',
    comment         VARCHAR(500)    NULL                     COMMENT '文字评语',
    reward_points   INT             NULL                     COMMENT '奖励积分数值',
    reward_reason   VARCHAR(100)    NULL                     COMMENT '积分奖励原因',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_evaluation_student (evaluation_id, student_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生评价明细';

-- ============================================
-- N05.3 学评教
-- ============================================

-- 6. 学评教表
CREATE TABLE edu_teacher_evaluation (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    teacher_id      BIGINT          NOT NULL                 COMMENT '被评价教师ID → sys_user.id',
    student_id      BIGINT          NOT NULL                 COMMENT '提交评价的学生ID → edu_student.id',
    schedule_id     BIGINT          NULL                     COMMENT '关联排课记录ID → edu_schedule.id',
    rating_score    TINYINT         NOT NULL                 COMMENT '综合评分 1-5（整数）',
    content         VARCHAR(500)    NULL                     COMMENT '评价文字内容',
    tags            VARCHAR(500)    NULL                     COMMENT '评价标签JSON数组',
    is_anonymous    TINYINT         NOT NULL DEFAULT 0       COMMENT '是否匿名: 0=实名, 1=匿名',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/HIDDEN',
    submit_time     DATETIME        NOT NULL                 COMMENT '提交时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_teacher_eval (teacher_id, student_id, schedule_id),
    KEY idx_teacher_submit (teacher_id, submit_time),
    KEY idx_submit_time (submit_time),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学评教';

-- ============================================
-- 成绩管理
-- ============================================

-- 7. 考试成绩主表
CREATE TABLE edu_exam_score (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    class_id        BIGINT          NOT NULL                 COMMENT '班级ID → edu_class.id',
    subject_id      BIGINT          NOT NULL                 COMMENT '科目ID → edu_subject.id',
    exam_type       VARCHAR(20)     NOT NULL                 COMMENT '考试类型: MONTHLY/MIDTERM/FINAL/QUIZ/CUSTOM',
    exam_name       VARCHAR(50)     NULL                     COMMENT '自定义考试名称（仅CUSTOM时填写）',
    exam_date       DATE            NOT NULL                 COMMENT '考试日期',
    total_score     DECIMAL(5,1)    NOT NULL                 COMMENT '满分分值',
    teacher_id      BIGINT          NOT NULL                 COMMENT '录入教师ID → sys_user.id',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_exam (class_id, subject_id, exam_type, exam_date),
    KEY idx_class_id (class_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_exam_date (exam_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='考试成绩';

-- 8. 学生成绩明细表
CREATE TABLE edu_exam_score_student (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    exam_score_id   BIGINT          NOT NULL                 COMMENT '关联成绩ID',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID → edu_student.id',
    score           DECIMAL(5,1)    NULL                     COMMENT '分数，缺考时null',
    is_absent       TINYINT         NOT NULL DEFAULT 0       COMMENT '是否缺考: 0=否, 1=是',
    remark          VARCHAR(100)    NULL                     COMMENT '备注',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_exam_student (exam_score_id, student_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生成绩明细';

-- ============================================
-- N03 教师档案（PRD N01 §3.2.1 教资人数统计用）
-- ============================================

CREATE TABLE edu_teacher (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id             BIGINT          NULL                     COMMENT '关联系统用户ID → sys_user.id',
    real_name           VARCHAR(50)     NOT NULL                 COMMENT '教师姓名',
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '在职状态: ACTIVE=在职, INACTIVE=离职',
    subjects            VARCHAR(500)    NULL                     COMMENT '可授科目',
    hire_date           DATE            NULL                     COMMENT '入职日期',
    default_pay_per_lesson DECIMAL(10,2) NULL                   COMMENT '默认课酬单价（元/节）',
    phone               VARCHAR(20)     NULL                     COMMENT '联系电话',
    remark              VARCHAR(500)    NULL                     COMMENT '备注',
    create_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted          TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师档案';

-- ============================================
-- N05.1 学生作业提交（PRD N05 §2.5 §2.6）
-- ============================================

CREATE TABLE edu_homework_submission (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    homework_id     BIGINT          NOT NULL                 COMMENT '关联作业ID → edu_homework.id',
    student_id      BIGINT          NOT NULL                 COMMENT '学生ID → edu_student.id',
    content         VARCHAR(1000)   NULL                     COMMENT '文字内容（学生提交的文本备注）',
    image_urls      VARCHAR(2000)   NULL                     COMMENT '图片URL列表（JSON数组）',
    status          VARCHAR(20)     NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态: SUBMITTED=已提交, GRADED=已批改, LATE=迟交',
    score           INT             NULL                     COMMENT '评分（1-5）',
    comment         VARCHAR(500)    NULL                     COMMENT '教师评语',
    submitted_at    DATETIME        NULL                     COMMENT '提交时间',
    graded_at       DATETIME        NULL                     COMMENT '批改时间',
    graded_by       BIGINT          NULL                     COMMENT '批改教师ID',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_homework_id (homework_id),
    KEY idx_student_id (student_id),
    KEY idx_status (status),
    KEY idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生作业提交';
