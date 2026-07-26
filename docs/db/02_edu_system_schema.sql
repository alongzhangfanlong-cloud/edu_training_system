-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: edu_system
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `edu_system`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `edu_system` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `edu_system`;

--
-- Table structure for table `edu_attendance`
--

DROP TABLE IF EXISTS `edu_attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_id` bigint NOT NULL COMMENT '排课记录ID → edu_schedule.id',
  `class_id` bigint NOT NULL COMMENT '班级ID → edu_class.id',
  `student_id` bigint NOT NULL COMMENT '学员ID → edu_student.id',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '签到状态(0=到校 1=迟到 2=缺勤 3=请假 4=请假未审批)',
  `signin_time` datetime DEFAULT NULL COMMENT '签到操作时间',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID → sys_user.id',
  `operator_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作端(PC/MINI_PROGRAM)',
  `remark` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_schedule_student` (`schedule_id`,`student_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='签到记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_class`
--

DROP TABLE IF EXISTS `edu_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_class` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '班级名称',
  `code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '班级编号(CLS+yyyyMMdd+4位序号，唯一)',
  `course_id` bigint NOT NULL COMMENT '关联课程ID',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID(关联sys_user)',
  `classroom_id` bigint DEFAULT NULL COMMENT '默认教室ID(可空)',
  `max_students` int NOT NULL DEFAULT '20' COMMENT '最大学员数',
  `current_count` int NOT NULL DEFAULT '0' COMMENT '当前在读人数(冗余缓存)',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'RECRUITING' COMMENT '状态(RECRUITING/FULL/SUSPENDED/FINISHED/CANCELLED)',
  `start_date` date DEFAULT NULL COMMENT '开课日期',
  `end_date` date DEFAULT NULL COMMENT '结课日期',
  `description` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '班级说明',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_classroom_id` (`classroom_id`),
  KEY `idx_status` (`status`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_class_schedule_rule`
--

DROP TABLE IF EXISTS `edu_class_schedule_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_class_schedule_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint NOT NULL COMMENT '班级ID(UNIQUE, 1:1)',
  `rule_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'WEEKLY' COMMENT '规则类型(WEEKLY=每周, SPECIFIC=指定日期)',
  `week_day` int DEFAULT NULL COMMENT '上课日(ruleType=WEEKLY时使用, 1=周一, 7=周日)',
  `schedule_date` date DEFAULT NULL COMMENT '上课日期(ruleType=SPECIFIC时使用)',
  `start_time` time NOT NULL COMMENT '上课开始时间',
  `end_time` time NOT NULL COMMENT '上课结束时间',
  `duration` int NOT NULL DEFAULT '45' COMMENT '每节课时长(分钟)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级排课规则表(1:1)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_class_student`
--

DROP TABLE IF EXISTS `edu_class_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_class_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ENROLLED' COMMENT '在班状态(ENROLLED/SUSPENDED/EXITED)',
  `remark` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_student` (`class_id`,`student_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级学员关联表(跨模块桥接)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_classroom`
--

DROP TABLE IF EXISTS `edu_classroom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_classroom` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '教室ID',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '教室名称',
  `capacity` int NOT NULL DEFAULT '30' COMMENT '容纳人数(1-999, 0=不限)',
  `location` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '位置描述',
  `facilities` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设施配置(JSON数组字符串)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(1启用 0停用)',
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教室表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_course`
--

DROP TABLE IF EXISTS `edu_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名称',
  `code` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程编号（全局唯一）',
  `subject_id` bigint NOT NULL COMMENT '所属科目ID',
  `course_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'BATCH' COMMENT '课程类型(BATCH班课/ONE_ON_ONE一对一/SMALL_GROUP小班)',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单价(元)',
  `applicable_grade` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '适用年级编码(NULL=不限)',
  `cover_image` varchar(256) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '封面图URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(1上架 0下架)',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `description` text COLLATE utf8mb4_general_ci COMMENT '课程介绍',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  UNIQUE KEY `uk_name_subject` (`subject_id`,`name`),
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_status_sort` (`status`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_enrollment`
--

DROP TABLE IF EXISTS `edu_enrollment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_enrollment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID → edu_student.id',
  `course_id` bigint NOT NULL COMMENT '课程ID → edu_course.id',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID → edu_class.id(可为空)',
  `amount` decimal(10,2) NOT NULL COMMENT '报名应缴金额',
  `handler_id` bigint NOT NULL COMMENT '经手人ID → sys_user.id',
  `enrollment_date` date NOT NULL COMMENT '报名日期',
  `payment_status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '收费状态(DRAFT/PENDING/PARTIAL/PAID/REFUNDED/CANCELLED)',
  `tuition_record_id` bigint DEFAULT NULL COMMENT '关联首条收费记录ID → edu_tuition_fee.id',
  `remark` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_enrollment_date` (`enrollment_date`),
  KEY `idx_handler_id` (`handler_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报名记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_evaluation`
--

DROP TABLE IF EXISTS `edu_evaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_id` bigint NOT NULL COMMENT '关联排课记录ID → edu_schedule.id',
  `class_id` bigint NOT NULL COMMENT '关联班级ID（冗余方便查询）',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID → sys_user.id',
  `summary` text COLLATE utf8mb4_general_ci COMMENT '课堂总结（30-2000字）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0=草稿, 1=已提交',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_schedule_id` (`schedule_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课堂评价';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_evaluation_student`
--

DROP TABLE IF EXISTS `edu_evaluation_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_evaluation_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `evaluation_id` bigint NOT NULL COMMENT '关联评价ID',
  `student_id` bigint NOT NULL COMMENT '学生ID → edu_student.id',
  `star_rating` decimal(2,1) NOT NULL COMMENT '星级评分 1.0-5.0，0.5步进',
  `comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文字评语',
  `reward_points` int DEFAULT NULL COMMENT '奖励积分数值',
  `reward_reason` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '积分奖励原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_evaluation_student` (`evaluation_id`,`student_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生评价明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_exam_score`
--

DROP TABLE IF EXISTS `edu_exam_score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_exam_score` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint NOT NULL COMMENT '班级ID → edu_class.id',
  `subject_id` bigint NOT NULL COMMENT '科目ID → edu_subject.id',
  `exam_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '考试类型: MONTHLY/MIDTERM/FINAL/QUIZ/CUSTOM',
  `exam_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '自定义考试名称（仅CUSTOM时填写）',
  `exam_date` date NOT NULL COMMENT '考试日期',
  `total_score` decimal(5,1) NOT NULL COMMENT '满分分值',
  `teacher_id` bigint NOT NULL COMMENT '录入教师ID → sys_user.id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam` (`class_id`,`subject_id`,`exam_type`,`exam_date`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_exam_date` (`exam_date`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='考试成绩';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_exam_score_student`
--

DROP TABLE IF EXISTS `edu_exam_score_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_exam_score_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_score_id` bigint NOT NULL COMMENT '关联成绩ID',
  `student_id` bigint NOT NULL COMMENT '学生ID → edu_student.id',
  `score` decimal(5,1) DEFAULT NULL COMMENT '分数，缺考时null',
  `is_absent` tinyint NOT NULL DEFAULT '0' COMMENT '是否缺考: 0=否, 1=是',
  `remark` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student` (`exam_score_id`,`student_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生成绩明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_homework`
--

DROP TABLE IF EXISTS `edu_homework`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_homework` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint NOT NULL COMMENT '关联班级ID → edu_class.id',
  `course_id` bigint NOT NULL COMMENT '关联课程ID → edu_course.id',
  `schedule_id` bigint DEFAULT NULL COMMENT '关联排课记录ID → edu_schedule.id',
  `title` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '作业标题',
  `content` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '作业内容（最长5000字）',
  `teacher_id` bigint NOT NULL COMMENT '布置教师ID → sys_user.id',
  `is_updated` tinyint NOT NULL DEFAULT '0' COMMENT '是否被编辑更新过: 0=否, 1=是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课后作业';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_homework_attachment`
--

DROP TABLE IF EXISTS `edu_homework_attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_homework_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `homework_id` bigint NOT NULL COMMENT '关联作业ID',
  `file_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '原始文件名',
  `file_url` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件存储URL',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型: IMAGE/DOCUMENT',
  `mime_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'MIME类型',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_homework_id` (`homework_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='作业附件';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_homework_submission`
--

DROP TABLE IF EXISTS `edu_homework_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_homework_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `homework_id` bigint NOT NULL COMMENT '关联作业ID → edu_homework.id',
  `student_id` bigint NOT NULL COMMENT '学生ID → edu_student.id',
  `content` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文字内容（学生提交的文本备注）',
  `image_urls` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片URL列表（JSON数组）',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'SUBMITTED' COMMENT '状态: SUBMITTED=已提交, GRADED=已批改, LATE=迟交',
  `score` int DEFAULT NULL COMMENT '评分（1-5）',
  `comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '教师评语',
  `submitted_at` datetime DEFAULT NULL COMMENT '提交时间',
  `graded_at` datetime DEFAULT NULL COMMENT '批改时间',
  `graded_by` bigint DEFAULT NULL COMMENT '批改教师ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_homework_id` (`homework_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生作业提交';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_leave_conflict`
--

DROP TABLE IF EXISTS `edu_leave_conflict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_leave_conflict` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `leave_id` bigint NOT NULL COMMENT '关联教师请假ID → edu_leave_teacher.id',
  `schedule_id` bigint NOT NULL COMMENT '冲突排课记录ID → edu_schedule.id',
  `resolve_type` tinyint NOT NULL COMMENT '处理方式(0=代课 1=调课 2=停课)',
  `substitute_teacher_id` bigint DEFAULT NULL COMMENT '代课教师ID → sys_user.id(resolve_type=0时必填)',
  `new_schedule_date` date DEFAULT NULL COMMENT '调课后新日期(resolve_type=1时必填)',
  `new_time_slot` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '调课后新时段(resolve_type=1时必填)',
  `new_classroom_id` bigint DEFAULT NULL COMMENT '调课后新教室ID(resolve_type=1时可空)',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '处理状态(0=待处理 1=已处理)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_leave_id` (`leave_id`),
  KEY `idx_schedule_id` (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师请假冲突排课处理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_leave_student`
--

DROP TABLE IF EXISTS `edu_leave_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_leave_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID → edu_student.id',
  `leave_date` date NOT NULL COMMENT '请假日期',
  `leave_type` tinyint NOT NULL COMMENT '请假类型(0=事假 1=病假 2=其他)',
  `reason` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '请假原因',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0=待确认 1=已通过 2=已驳回 3=已撤销)',
  `applicant_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人类型(PARENT/TEACHER)',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID → sys_user.id',
  `confirm_teacher_id` bigint DEFAULT NULL COMMENT '确认教师ID → sys_user.id',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `reject_reason` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '驳回原因',
  `auto_approved` tinyint NOT NULL DEFAULT '0' COMMENT '是否自动通过(0否 1是)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_student_date` (`student_id`,`leave_date`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生请假表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_leave_teacher`
--

DROP TABLE IF EXISTS `edu_leave_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_leave_teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID → sys_user.id',
  `leave_start_date` date NOT NULL COMMENT '请假开始日期',
  `leave_end_date` date NOT NULL COMMENT '请假结束日期(单天时同start)',
  `leave_type` tinyint NOT NULL COMMENT '请假类型(0=事假 1=病假 2=其他)',
  `reason` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '请假原因',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0=待审批 1=已通过 2=已驳回 3=已处理 4=已撤销)',
  `applicant_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人类型(TEACHER/ADMIN)',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID → sys_user.id',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人ID → sys_user.id',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `reject_reason` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '驳回原因',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_teacher_date` (`teacher_id`,`leave_start_date`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师请假表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_parent_view_log`
--

DROP TABLE IF EXISTS `edu_parent_view_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_parent_view_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_user_id` bigint NOT NULL COMMENT '家长用户ID → sys_user.id',
  `student_id` bigint NOT NULL COMMENT '学生ID → edu_student.id',
  `biz_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型: EVALUATION/EXAM/HOMEWORK',
  `biz_id` bigint NOT NULL COMMENT '业务记录ID',
  `first_view_time` datetime NOT NULL COMMENT '首次查看时间',
  `last_view_time` datetime NOT NULL COMMENT '最近查看时间',
  `view_count` int NOT NULL DEFAULT '1' COMMENT '查看次数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_parent_view` (`parent_user_id`,`student_id`,`biz_type`,`biz_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_biz` (`biz_type`,`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家长查阅记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_points_account`
--

DROP TABLE IF EXISTS `edu_points_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_points_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID，关联 edu_student.id',
  `total_earned` int NOT NULL DEFAULT '0' COMMENT '累计获取积分',
  `total_spent` int NOT NULL DEFAULT '0' COMMENT '累计消费积分',
  `balance` int NOT NULL DEFAULT '0' COMMENT '当前积分余额 (total_earned - total_spent)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_points_gift`
--

DROP TABLE IF EXISTS `edu_points_gift`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_points_gift` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '礼品名称',
  `image_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '礼品图片URL',
  `points_required` int NOT NULL COMMENT '兑换所需积分',
  `stock_total` int NOT NULL COMMENT '总库存数量',
  `stock_remaining` int NOT NULL COMMENT '剩余库存数量',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1=启用, 0=禁用',
  `description` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '礼品描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0=未删除, 1=已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分礼品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_points_log`
--

DROP TABLE IF EXISTS `edu_points_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_points_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `change_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '变动类型: EARN/REDEEM/EXPIRE/ADJUST',
  `amount` int NOT NULL COMMENT '变动积分数，正数=获取，负数=消耗',
  `balance_after` int NOT NULL COMMENT '变动后账户余额',
  `source_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源类型: TEACHER/ADMIN/REDEEM/CANCEL_REDEEM/ADJUST',
  `source_id` bigint DEFAULT NULL COMMENT '来源业务记录ID（评价ID/兑换ID等）',
  `remark` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_source` (`source_type`,`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分日志（只追加）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_points_redemption`
--

DROP TABLE IF EXISTS `edu_points_redemption`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_points_redemption` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `gift_id` bigint NOT NULL COMMENT '礼品ID',
  `points_spent` int NOT NULL COMMENT '消耗积分',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING_CLAIM' COMMENT '状态: PENDING_CLAIM/CLAIMED/CANCELLED',
  `redeem_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '兑换时间',
  `claim_time` datetime DEFAULT NULL COMMENT '领取时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID（核销/取消时的操作人员，关联 sys_user）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0=未删除, 1=已删除',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_gift_id` (`gift_id`),
  KEY `idx_status` (`status`),
  KEY `idx_redeem_time` (`redeem_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分兑换记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_prospect`
--

DROP TABLE IF EXISTS `edu_prospect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_prospect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '意向学员ID',
  `name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `gender` char(1) COLLATE utf8mb4_general_ci DEFAULT 'U' COMMENT '性别(M男 F女 U未知)',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `grade` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '年级编码',
  `school` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '就读学校',
  `mobile` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `parent_name` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `parent_relation` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `parent_mobile` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `wechat_nickname` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信昵称',
  `intended_course_id` bigint DEFAULT NULL COMMENT '意向课程ID',
  `intended_course_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '意向课程名称',
  `interest_level` varchar(10) COLLATE utf8mb4_general_ci DEFAULT 'MEDIUM' COMMENT '意向等级(HIGH/MEDIUM/LOW)',
  `source` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'OTHER' COMMENT '来源渠道(DIRECT_MARKETING/REFERRAL/ONLINE/OTHER)',
  `counselor_id` bigint DEFAULT NULL COMMENT '顾问ID(关联sys_user)',
  `follow_status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NEW' COMMENT '跟进状态(NEW/CONTACTED/TRIAL/CONVERTED/LOST)',
  `trial_lesson_count` int NOT NULL DEFAULT '0' COMMENT '体验课次数(由排课/考勤自动累加)',
  `last_followup_date` datetime DEFAULT NULL COMMENT '最近跟进日期(新增跟进时自动更新)',
  `converted_student_id` bigint DEFAULT NULL COMMENT '转化后在学学员ID(转化后回填)',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_name_mobile` (`name`,`mobile`),
  KEY `idx_follow_status` (`follow_status`),
  KEY `idx_counselor_id` (`counselor_id`),
  KEY `idx_intended_course` (`intended_course_id`),
  KEY `idx_source` (`source`),
  KEY `idx_last_followup` (`last_followup_date`),
  KEY `idx_converted_student` (`converted_student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='意向学员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_prospect_followup`
--

DROP TABLE IF EXISTS `edu_prospect_followup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_prospect_followup` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '跟进记录ID',
  `prospect_id` bigint NOT NULL COMMENT '意向学员ID',
  `method` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '跟进方式(PHONE/WECHAT/FACE_TO_FACE)',
  `content` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL COMMENT '跟进内容',
  `followup_date` datetime NOT NULL COMMENT '跟进时间(支持补录)',
  `next_plan` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '下次跟进计划',
  `operator_id` bigint NOT NULL COMMENT '跟进人ID(关联sys_user)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_prospect_time` (`prospect_id`,`followup_date`),
  KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='意向学员跟进记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_resource`
--

DROP TABLE IF EXISTS `edu_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课件ID',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课件名称',
  `subject_id` bigint DEFAULT NULL COMMENT '关联科目ID',
  `grade` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '适用年级',
  `resource_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源类型(DOCUMENT/PPT/EXERCISE/OTHER)',
  `file_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '存储路径',
  `file_url` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '访问URL',
  `file_size` bigint NOT NULL DEFAULT '0' COMMENT '文件大小(字节)',
  `file_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'MIME类型',
  `is_public` tinyint NOT NULL DEFAULT '1' COMMENT '共享范围(1公开 0私有)',
  `upload_by` bigint NOT NULL COMMENT '上传人ID(关联sys_user)',
  `download_count` int NOT NULL DEFAULT '0' COMMENT '下载次数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(1正常 0禁用)',
  `description` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_upload_by` (`upload_by`),
  KEY `idx_resource_type` (`resource_type`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课件资料表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_schedule`
--

DROP TABLE IF EXISTS `edu_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` bigint DEFAULT NULL COMMENT '关联排课计划ID(单次排课可为空)',
  `class_id` bigint NOT NULL COMMENT '班级ID → edu_class.id',
  `course_id` bigint NOT NULL COMMENT '课程ID → edu_course.id(冗余,方便查询)',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID → sys_user.id',
  `room_id` bigint DEFAULT NULL COMMENT '教室ID → edu_classroom.id',
  `schedule_date` date NOT NULL COMMENT '上课日期',
  `start_time` time NOT NULL COMMENT '上课开始时间',
  `end_time` time NOT NULL COMMENT '上课结束时间',
  `schedule_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NORMAL' COMMENT '排课类型(NORMAL/TRIAL/MAKEUP)',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NORMAL' COMMENT '状态(NORMAL/CANCELLED/RESCHEDULED/MAKEUP/SUSPENDED/SUBSTITUTE)',
  `force_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否强制排课(0否 1是)',
  `force_reason` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '强制排课冲突摘要',
  `cancel_reason` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '取消原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '取消人',
  `rescheduled_from_id` bigint DEFAULT NULL COMMENT '调课来源排课ID',
  `rescheduled_to_id` bigint DEFAULT NULL COMMENT '调课目标排课ID',
  `consumed` tinyint NOT NULL DEFAULT '0' COMMENT '是否已消课(0未消 1已消)',
  `consume_time` datetime DEFAULT NULL COMMENT '消课时间',
  `consume_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '消课操作人',
  `remark` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_class_date` (`class_id`,`schedule_date`),
  KEY `idx_teacher_date` (`teacher_id`,`schedule_date`),
  KEY `idx_room_date` (`room_id`,`schedule_date`),
  KEY `idx_date` (`schedule_date`),
  KEY `idx_status` (`status`),
  KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='排课记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_schedule_plan`
--

DROP TABLE IF EXISTS `edu_schedule_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_schedule_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint DEFAULT NULL COMMENT '关联班级ID（单班级兼容）',
  `class_ids` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联班级ID列表(JSON数组)',
  `start_date` date NOT NULL COMMENT '排课起始日期',
  `end_date` date NOT NULL COMMENT '排课结束日期',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'UNGENERATED' COMMENT '状态(UNGENERATED/GENERATED)',
  `conflict_detail` text COLLATE utf8mb4_general_ci COMMENT '冲突详情JSON',
  `remark` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `room_id` bigint DEFAULT NULL COMMENT '教室ID',
  `plan_config` text COLLATE utf8mb4_general_ci COMMENT '排课配置JSON(weekDays/startTime/endTime/teacherId)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='排课计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_student`
--

DROP TABLE IF EXISTS `edu_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学员ID',
  `name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '学员姓名',
  `gender` char(1) COLLATE utf8mb4_general_ci DEFAULT 'M' COMMENT '性别(M男 F女)',
  `birth_date` date DEFAULT NULL COMMENT '出生日期（年龄由此派生）',
  `grade` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '年级编码',
  `school` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '就读学校',
  `mobile` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
  `counselor_id` bigint DEFAULT NULL COMMENT '顾问ID(关联sys_user)',
  `parent_name` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '家长姓名',
  `parent_relation` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '亲属关系(FATHER/MOTHER/GRANDFATHER/GRANDMOTHER/GRANDFATHER_M/GRANDMOTHER_M/OTHER)',
  `parent_mobile` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '家长手机号',
  `parent_id` bigint DEFAULT NULL COMMENT '家长用户ID(关联sys_user，用于微信通知)',
  `wechat_nickname` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信昵称',
  `intended_course_id` bigint DEFAULT NULL COMMENT '报名课程ID → edu_course.id',
  `intended_course_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报名课程名称',
  `source` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源渠道(DIRECT_MARKETING/REFERRAL/ONLINE/OTHER)',
  `tags` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '标签(逗号分隔)',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ENROLLED' COMMENT '状态(ENROLLED/SUSPENDED/COMPLETED/LOST)',
  `total_hours` decimal(10,1) NOT NULL DEFAULT '0.0' COMMENT '累计获得总课时(冗余缓存)',
  `remaining_hours` decimal(10,1) NOT NULL DEFAULT '0.0' COMMENT '剩余课时汇总(冗余缓存)',
  `last_visit_time` datetime DEFAULT NULL COMMENT '最近到校时间(冗余缓存)',
  `completed_at` datetime DEFAULT NULL COMMENT '结业时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mobile_deleted` (`mobile`,`deleted`),
  KEY `idx_name` (`name`),
  KEY `idx_grade` (`grade`),
  KEY `idx_status` (`status`),
  KEY `idx_counselor_id` (`counselor_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_intended_course` (`intended_course_id`),
  KEY `idx_source` (`source`),
  KEY `idx_last_visit` (`last_visit_time`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='在学学员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_student_hours_log`
--

DROP TABLE IF EXISTS `edu_student_hours_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_student_hours_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint DEFAULT NULL COMMENT '关联课程ID(赠送/手动调整时可为空)',
  `enrollment_id` bigint DEFAULT NULL COMMENT '关联报名记录ID(Batch 2预留)',
  `class_id` bigint DEFAULT NULL COMMENT '关联班级ID',
  `change_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '变动类型(ENROLL_GAIN/CONSUME/LEAVE_RETURN/MAKEUP_CONSUME/REFUND_DEDUCT/GIFT_GAIN/MANUAL_ADJUST)',
  `change_amount` decimal(10,1) NOT NULL COMMENT '变动数量(正=增加, 负=减少)',
  `balance_after` decimal(10,1) NOT NULL COMMENT '变动后剩余课时',
  `source_type` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源业务类型(ENROLLMENT/TUITION/ATTENDANCE/LEAVE/GIFT/MANUAL)',
  `source_id` bigint DEFAULT NULL COMMENT '来源业务记录ID',
  `operator_id` bigint DEFAULT NULL COMMENT '经手人ID(关联sys_user)',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(变动发生时间)',
  PRIMARY KEY (`id`),
  KEY `idx_student_time` (`student_id`,`create_time`),
  KEY `idx_student_course` (`student_id`,`course_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_enrollment_id` (`enrollment_id`),
  KEY `idx_source` (`source_type`,`source_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课时变动明细日志(只追加,不可修改/删除)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_student_parent`
--

DROP TABLE IF EXISTS `edu_student_parent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_student_parent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联系统用户ID(家长登录账号，可为空)',
  `name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '家长姓名',
  `relation` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '亲属关系(FATHER/MOTHER/GRANDFATHER/GRANDMOTHER/GRANDFATHER_M/GRANDMOTHER_M/OTHER)',
  `mobile` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '家长手机号',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序(同学员多家长时决定展示顺序)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_mobile` (`student_id`,`mobile`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='家长账号关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_subject`
--

DROP TABLE IF EXISTS `edu_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_subject` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '科目ID',
  `name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '科目名称',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(1启用 0停用)',
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status_sort` (`status`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='科目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_teacher`
--

DROP TABLE IF EXISTS `edu_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联系统用户ID → sys_user.id',
  `real_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师姓名',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '在职状态: ACTIVE=在职, INACTIVE=离职',
  `subjects` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '可授科目',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `default_pay_per_lesson` decimal(10,2) DEFAULT NULL COMMENT '默认课酬单价（元/节）',
  `phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_teacher_evaluation`
--

DROP TABLE IF EXISTS `edu_teacher_evaluation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_teacher_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint NOT NULL COMMENT '被评价教师ID → sys_user.id',
  `student_id` bigint NOT NULL COMMENT '提交评价的学生ID → edu_student.id',
  `schedule_id` bigint DEFAULT NULL COMMENT '关联排课记录ID → edu_schedule.id',
  `rating_score` tinyint NOT NULL COMMENT '综合评分 1-5（整数）',
  `content` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评价文字内容',
  `tags` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评价标签JSON数组',
  `is_anonymous` tinyint NOT NULL DEFAULT '0' COMMENT '是否匿名: 0=实名, 1=匿名',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/HIDDEN',
  `submit_time` datetime NOT NULL COMMENT '提交时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_eval` (`teacher_id`,`student_id`,`schedule_id`),
  KEY `idx_teacher_submit` (`teacher_id`,`submit_time`),
  KEY `idx_submit_time` (`submit_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学评教';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_teacher_pay_rate`
--

DROP TABLE IF EXISTS `edu_teacher_pay_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_teacher_pay_rate` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID → sys_user.id',
  `course_id` bigint NOT NULL COMMENT '课程ID → edu_course.id',
  `pay_per_lesson` decimal(10,2) NOT NULL COMMENT '单节课酬(元)',
  `effective_from` date NOT NULL COMMENT '生效起始日期',
  `effective_to` date DEFAULT NULL COMMENT '生效截止日期(可为空=长期有效)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_course_period` (`teacher_id`,`course_id`,`effective_from`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师课酬单价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_teacher_payroll`
--

DROP TABLE IF EXISTS `edu_teacher_payroll`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_teacher_payroll` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID → sys_user.id',
  `month` varchar(7) COLLATE utf8mb4_general_ci NOT NULL COMMENT '月份(YYYY-MM)',
  `course_id` bigint NOT NULL COMMENT '课程ID → edu_course.id',
  `lesson_count` int NOT NULL DEFAULT '0' COMMENT '消课数(人次)',
  `pay_per_lesson` decimal(10,2) NOT NULL COMMENT '计算时锁定的单节课酬',
  `total_pay` decimal(10,2) NOT NULL COMMENT '小计(lesson_count × pay_per_lesson)',
  `status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态(DRAFT/CONFIRMED/PAID)',
  `confirmed_at` datetime DEFAULT NULL COMMENT '确认时间',
  `confirmed_by` bigint DEFAULT NULL COMMENT '确认人ID → sys_user.id',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_month_course` (`teacher_id`,`month`,`course_id`),
  KEY `idx_month` (`month`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师课酬记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_tuition_fee`
--

DROP TABLE IF EXISTS `edu_tuition_fee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_tuition_fee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fee_no` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '收费编号(T+yyyyMMdd+4位流水)，UNIQUE',
  `enrollment_id` bigint DEFAULT NULL COMMENT '关联报名记录ID',
  `student_id` bigint NOT NULL COMMENT '学员ID → edu_student.id',
  `course_id` bigint NOT NULL COMMENT '课程ID → edu_course.id',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID → edu_class.id(可为空)',
  `fee_type` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '费用类型(REGISTRATION/TUITION/MATERIAL/OTHER)',
  `amount_receivable` decimal(10,2) NOT NULL COMMENT '应缴金额(继承自报名记录)',
  `amount_paid` decimal(10,2) NOT NULL COMMENT '本次实缴金额',
  `payment_method` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付方式(CASH/WECHAT/ALIPAY/BANK_TRANSFER)',
  `status` varchar(32) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PAID' COMMENT '状态(PAID/REFUNDED/PARTIAL_REFUNDED)',
  `pay_time` datetime DEFAULT NULL COMMENT '缴费时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `operator_id` bigint NOT NULL COMMENT '经手人ID → sys_user.id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收费时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fee_no` (`fee_no`),
  KEY `idx_enrollment_id` (`enrollment_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收费记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_tuition_gift_hours`
--

DROP TABLE IF EXISTS `edu_tuition_gift_hours`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_tuition_gift_hours` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gift_no` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '赠送编号(G+yyyyMMdd+4位流水)，UNIQUE',
  `student_id` bigint NOT NULL COMMENT '学员ID → edu_student.id',
  `course_id` bigint NOT NULL COMMENT '关联课程ID → edu_course.id',
  `hours` int NOT NULL COMMENT '赠送课时数',
  `reason` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '赠送原因(PROMOTION/COMPENSATION/OTHER)',
  `reason_custom` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '自定义原因',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `operator_id` bigint NOT NULL COMMENT '经手人ID → sys_user.id',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '赠送时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gift_no` (`gift_no`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='赠送课时记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edu_tuition_refund`
--

DROP TABLE IF EXISTS `edu_tuition_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edu_tuition_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `refund_no` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '退费编号(R+yyyyMMdd+4位流水)，UNIQUE',
  `fee_id` bigint NOT NULL COMMENT '关联收费记录ID → edu_tuition_fee.id',
  `student_id` bigint NOT NULL COMMENT '学员ID → edu_student.id',
  `course_id` bigint NOT NULL COMMENT '课程ID → edu_course.id',
  `amount` decimal(10,2) NOT NULL COMMENT '退费金额',
  `reason` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '退费原因编码',
  `reason_custom` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '自定义退费原因',
  `refund_method` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '退费方式(ORIGINAL/CASH/WECHAT/ALIPAY/BANK_TRANSFER)',
  `status` varchar(32) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'COMPLETED' COMMENT '状态(PENDING_APPROVAL/APPROVED/REJECTED/COMPLETED)',
  `hours_deducted` int NOT NULL DEFAULT '0' COMMENT '扣回课时数',
  `applicant_id` bigint NOT NULL COMMENT '申请人ID → sys_user.id',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人ID → sys_user.id(可为空)',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `reject_reason` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '驳回原因',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_fee_id` (`fee_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退费记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数ID',
  `name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数名称',
  `key` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键名',
  `value` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键值',
  `type` char(1) COLLATE utf8mb4_general_ci DEFAULT 'N' COMMENT '系统内置(Y是 N否)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
  `type_code` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典编码',
  `label` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典标签',
  `value` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典键值',
  `sort` int DEFAULT '0' COMMENT '显示顺序',
  `css_class` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性',
  `list_class` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认(1是 0否)',
  `status` tinyint DEFAULT '1' COMMENT '状态(1正常 0停用)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_type_code` (`type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典名称',
  `code` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典编码',
  `status` tinyint DEFAULT '1' COMMENT '状态(1正常 0停用)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_login_log`
--

DROP TABLE IF EXISTS `sys_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `username` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录账号',
  `ipaddr` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录IP',
  `login_location` varchar(256) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录地点',
  `browser` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作系统',
  `status` tinyint DEFAULT '1' COMMENT '状态(1成功 0失败)',
  `msg` varchar(256) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '提示信息',
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `type` char(1) COLLATE utf8mb4_general_ci DEFAULT 'C' COMMENT '类型(M目录 C菜单 F按钮)',
  `path` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由路径',
  `component` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
  `route_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由名称',
  `query` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由参数',
  `is_frame` tinyint DEFAULT '1' COMMENT '是否外链(1是 0否)',
  `is_cache` tinyint DEFAULT '0' COMMENT '是否缓存(1是 0否)',
  `icon` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '图标',
  `permission` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
  `sort` int DEFAULT '0' COMMENT '排序',
  `visible` tinyint DEFAULT '1' COMMENT '是否可见(1可见 0隐藏)',
  `status` tinyint DEFAULT '1' COMMENT '状态(1正常 0停用)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=377 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(256) COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `content` longtext COLLATE utf8mb4_general_ci COMMENT '公告内容',
  `type` varchar(32) COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '公告类型(1通知 2公告)',
  `level` varchar(32) COLLATE utf8mb4_general_ci DEFAULT 'normal' COMMENT '紧急程度(normal/important/urgent)',
  `publish_by` bigint DEFAULT NULL COMMENT '发布人ID',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `status` tinyint DEFAULT '0' COMMENT '状态(1已发布 0草稿)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `title` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作模块',
  `business_type` varchar(16) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '业务类型(INSERT/UPDATE/DELETE/EXPORT等)',
  `method` varchar(256) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '方法名',
  `request_method` varchar(16) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求方式',
  `oper_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员',
  `oper_url` varchar(256) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作IP',
  `oper_location` varchar(256) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作地点',
  `oper_param` text COLLATE utf8mb4_general_ci COMMENT '请求参数(JSON)',
  `json_result` text COLLATE utf8mb4_general_ci COMMENT '返回结果(JSON)',
  `status` tinyint DEFAULT '1' COMMENT '状态(1成功 0失败)',
  `error_msg` text COLLATE utf8mb4_general_ci COMMENT '错误信息',
  `cost_time` bigint DEFAULT '0' COMMENT '耗时(ms)',
  `oper_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_oper_time` (`oper_time`),
  KEY `idx_oper_name` (`oper_name`)
) ENGINE=InnoDB AUTO_INCREMENT=341 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `sort` int DEFAULT '0' COMMENT '显示顺序',
  `menu_check_strictly` tinyint DEFAULT '1' COMMENT '菜单树父子联动',
  `status` tinyint DEFAULT '1' COMMENT '状态(1正常 0停用)',
  `remark` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `password` varchar(256) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码(BCrypt)',
  `real_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '真实姓名',
  `nick_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '昵称',
  `email` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '邮箱',
  `mobile` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '手机号',
  `wx_openid` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信OpenID(唯一标识)',
  `gender` tinyint DEFAULT '0' COMMENT '性别(0未知 1男 2女)',
  `avatar` varchar(256) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '头像URL',
  `status` tinyint DEFAULT '1' COMMENT '状态(1正常 0停用)',
  `login_ip` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `wx_openid` (`wx_openid`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_notification_setting`
--

DROP TABLE IF EXISTS `user_notification_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_notification_setting` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `notify_type` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知类型: SIGN_IN/EVALUATION/HOMEWORK/EXAM/LEAVE/REDEMPTION',
  `enabled` tinyint DEFAULT '1' COMMENT '是否开启(1开启 0关闭)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type` (`user_id`,`notify_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户通知偏好设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'edu_system'
--

--
-- Dumping routines for database 'edu_system'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-26 15:17:24
