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
-- Dumping data for table `edu_attendance`
--

LOCK TABLES `edu_attendance` WRITE;
/*!40000 ALTER TABLE `edu_attendance` DISABLE KEYS */;
INSERT INTO `edu_attendance` VALUES (1,1,1,1,0,'2026-07-25 22:05:34',0,'PC',NULL,'2026-07-25 22:05:34','2026-07-25 22:05:34',0),(2,37,1,7,1,'2026-07-26 14:08:54',2,'TEACHER',NULL,'2026-07-26 14:08:54','2026-07-26 14:08:54',0);
/*!40000 ALTER TABLE `edu_attendance` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_class`
--

LOCK TABLES `edu_class` WRITE;
/*!40000 ALTER TABLE `edu_class` DISABLE KEYS */;
INSERT INTO `edu_class` VALUES (1,'美术基础班-2026暑期','ART-CLASS-2026',1,2,1,15,3,'RECRUITING','2026-07-01','2026-08-31','2026年暑期美术基础班','1','2026-07-25 21:32:08','','2026-07-26 14:08:54',0),(6,'数学提高班-2026暑期','MATH-CLASS-2026',16,3,7,20,3,'RECRUITING','2026-07-01','2026-08-31','数学提高班-2026暑期','1','2026-07-26 11:33:40','','2026-07-26 14:08:54',0),(7,'英语启蒙班-2026暑期','ENG-CLASS-2026',17,3,1,20,3,'RECRUITING','2026-07-05','2026-08-31','英语启蒙班-2026暑期','1','2026-07-26 11:33:40','','2026-07-26 14:08:54',0);
/*!40000 ALTER TABLE `edu_class` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_class_schedule_rule`
--

LOCK TABLES `edu_class_schedule_rule` WRITE;
/*!40000 ALTER TABLE `edu_class_schedule_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `edu_class_schedule_rule` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_class_student`
--

LOCK TABLES `edu_class_student` WRITE;
/*!40000 ALTER TABLE `edu_class_student` DISABLE KEYS */;
INSERT INTO `edu_class_student` VALUES (1,1,1,'2026-07-25 21:32:08','ENROLLED',NULL,'2026-07-25 21:32:08','2026-07-25 21:32:08',0),(2,2,10,'2026-07-26 01:36:21','ENROLLED',NULL,'2026-07-26 01:36:21','2026-07-26 01:36:21',0),(3,3,12,'2026-07-26 01:43:23','ENROLLED',NULL,'2026-07-26 01:43:23','2026-07-26 01:43:23',0),(4,4,14,'2026-07-26 01:47:26','ENROLLED',NULL,'2026-07-26 01:47:26','2026-07-26 01:47:26',0),(5,5,16,'2026-07-26 01:49:30','ENROLLED',NULL,'2026-07-26 01:49:30','2026-07-26 01:49:30',0),(6,1,7,'2026-07-26 11:33:40','ENROLLED',NULL,'2026-07-26 11:33:40','2026-07-26 11:33:40',0),(7,6,21,'2026-07-26 11:33:40','ENROLLED',NULL,'2026-07-26 11:33:40','2026-07-26 11:33:40',0),(8,7,22,'2026-07-26 11:33:40','ENROLLED',NULL,'2026-07-26 11:33:40','2026-07-26 11:33:40',0),(9,1,19,'2026-07-26 14:08:54','ENROLLED',NULL,'2026-07-26 14:08:54','2026-07-26 14:08:54',0),(10,6,9,'2026-07-26 14:08:54','ENROLLED',NULL,'2026-07-26 14:08:54','2026-07-26 14:08:54',0),(11,6,20,'2026-07-26 14:08:54','ENROLLED',NULL,'2026-07-26 14:08:54','2026-07-26 14:08:54',0),(12,7,8,'2026-07-26 14:08:54','ENROLLED',NULL,'2026-07-26 14:08:54','2026-07-26 14:08:54',0),(13,7,23,'2026-07-26 14:08:54','ENROLLED',NULL,'2026-07-26 14:08:54','2026-07-26 14:08:54',0);
/*!40000 ALTER TABLE `edu_class_student` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_classroom`
--

LOCK TABLES `edu_classroom` WRITE;
/*!40000 ALTER TABLE `edu_classroom` DISABLE KEYS */;
INSERT INTO `edu_classroom` VALUES (1,'1号教室',20,'1楼东侧','[\"投影仪\",\"白板\",\"空调\",\"音响\"]',1,NULL,'1','2026-07-25 21:32:08','','2026-07-26 12:33:09',0),(2,'美术教室A-2437',30,'1楼101室','[\"投影仪\",\"白板\",\"空调\",\"画架\",\"洗水池\"]',1,NULL,'','2026-07-26 01:23:52','','2026-07-26 12:33:36',0),(7,'2号教室',25,'2楼西侧','[\"投影仪\",\"白板\",\"空调\"]',1,NULL,'1','2026-07-26 11:33:40','','2026-07-26 12:33:09',0),(8,'美术教室A',30,'1楼101室','[\"投影仪\",\"白板\",\"空调\",\"画架\",\"洗水池\"]',1,'专业美术教室','1','2026-07-26 12:33:09','1','2026-07-26 12:33:09',0);
/*!40000 ALTER TABLE `edu_classroom` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_course`
--

LOCK TABLES `edu_course` WRITE;
/*!40000 ALTER TABLE `edu_course` DISABLE KEYS */;
INSERT INTO `edu_course` VALUES (1,'少儿美术基础班','ART-BASIC-001',18,'BATCH',4800.00,NULL,'',1,1,'适合6-12岁儿童的美术基础课程','1','2026-07-25 21:32:08','','2026-07-26 12:33:09',0),(2,'高一数学基础班','MATH-G1-001',13,'班课',120.00,'高一','',1,3,'高一数学基础班课','','2026-07-26 00:21:09','','2026-07-26 12:33:09',0),(3,'高一英语基础班','ENG-G1-BASIC',14,'班课',120.00,'高一','',1,2,'高一英语基础班课，15人小班','','2026-07-26 00:23:34','','2026-07-26 12:33:09',0),(4,'高二数学提高班','MATH-G2-IMPROVE',13,'班课',150.00,'高二','',1,3,'高二数学提高班课','','2026-07-26 00:23:34','','2026-07-26 12:33:09',0),(5,'高三数学冲刺班','MATH-G3-SPRINT',13,'一对一',300.00,'高三','',1,1,'高三数学一对一冲刺，目标130+','','2026-07-26 00:27:46','','2026-07-26 12:33:09',0),(9,'测试课程-80841','COURSE80841',13,'GROUP',2400.00,NULL,'',1,0,'验证用课程','','2026-07-26 01:36:21','','2026-07-26 12:33:09',0),(10,'测试课程-02281','COURSE02281',13,'GROUP',2400.00,NULL,'',1,0,'验证用课程','','2026-07-26 01:43:22','','2026-07-26 12:33:09',0),(11,'测试课程-46092','COURSE46092',13,'GROUP',2400.00,NULL,'',1,0,'验证用课程','','2026-07-26 01:47:26','','2026-07-26 12:33:09',0),(12,'测试课程-69817','COURSE69817',13,'GROUP',2400.00,NULL,'',1,0,'验证用课程','','2026-07-26 01:49:30','','2026-07-26 12:33:09',0),(13,'初中数学提高班','MATH-JR-001',13,'BATCH',2400.00,NULL,'',1,1,NULL,'','2026-07-26 02:33:20','','2026-07-26 12:33:09',0),(14,'高中英语冲刺班','ENG-SR-001',14,'BATCH',2800.00,NULL,'',1,2,NULL,'','2026-07-26 02:33:20','','2026-07-26 12:33:09',0),(15,'物理一对一','PHY-1V1-001',15,'ONE_ON_ONE',200.00,NULL,'',1,3,NULL,'','2026-07-26 02:33:20','','2026-07-26 12:33:09',0),(16,'小学数学提高班','MATH-IMPROVE-001',13,'BATCH',5200.00,NULL,'',1,0,'小学数学提高班','1','2026-07-26 11:33:40','','2026-07-26 12:33:09',0),(17,'少儿英语启蒙班','ENG-START-001',14,'BATCH',5600.00,NULL,'',1,0,'少儿英语启蒙班','1','2026-07-26 11:33:40','','2026-07-26 12:33:09',0);
/*!40000 ALTER TABLE `edu_course` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_enrollment`
--

LOCK TABLES `edu_enrollment` WRITE;
/*!40000 ALTER TABLE `edu_enrollment` DISABLE KEYS */;
INSERT INTO `edu_enrollment` VALUES (1,8,5,NULL,9000.00,1,'2026-07-20','PAID',NULL,'报名30课时一对一，全款赠送2课时','1','2026-07-26 00:27:46','','2026-07-26 00:41:51',0),(2,9,3,NULL,2400.00,1,'2026-07-22','PARTIAL',NULL,'报名20课时班课','1','2026-07-26 00:27:46','','2026-07-26 00:41:51',0),(7,7,1,1,4800.00,1,'2026-07-05','PAID',3,NULL,'1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0),(8,21,16,6,5200.00,1,'2026-07-05','PAID',4,NULL,'1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0),(9,22,17,7,5600.00,1,'2026-07-05','PAID',5,NULL,'1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0);
/*!40000 ALTER TABLE `edu_enrollment` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_evaluation`
--

LOCK TABLES `edu_evaluation` WRITE;
/*!40000 ALTER TABLE `edu_evaluation` DISABLE KEYS */;
INSERT INTO `edu_evaluation` VALUES (1,11,6,3,'本节课教学顺利完成，学员掌握情况良好，课堂互动积极，重点内容已讲解清晰。',1,'2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0),(2,20,7,3,'本节课教学顺利完成，学员掌握情况良好，课堂互动积极，重点内容已讲解清晰。',1,'2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0),(3,12,6,3,'本节课教学顺利完成，学员掌握情况良好，课堂互动积极，重点内容已讲解清晰。',1,'2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0),(4,13,6,3,'本节课教学顺利完成，学员掌握情况良好，课堂互动积极，重点内容已讲解清晰。',1,'2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0);
/*!40000 ALTER TABLE `edu_evaluation` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_evaluation_student`
--

LOCK TABLES `edu_evaluation_student` WRITE;
/*!40000 ALTER TABLE `edu_evaluation_student` DISABLE KEYS */;
INSERT INTO `edu_evaluation_student` VALUES (1,1,7,5.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(2,1,8,5.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(3,1,9,4.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(4,2,7,4.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(5,2,8,3.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(6,2,9,3.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(7,3,7,5.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(8,3,8,4.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(9,3,9,4.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(10,4,7,4.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(11,4,8,5.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04'),(12,4,9,4.0,'课堂表现良好',10,'积极发言','2026-07-26 12:35:04','2026-07-26 12:35:04');
/*!40000 ALTER TABLE `edu_evaluation_student` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_exam_score`
--

LOCK TABLES `edu_exam_score` WRITE;
/*!40000 ALTER TABLE `edu_exam_score` DISABLE KEYS */;
INSERT INTO `edu_exam_score` VALUES (1,1,18,'QUIZ','美术基础测验-色彩','2026-07-10',100.0,2,'2026-07-26 12:35:04','2026-07-26 12:35:04',0),(2,6,13,'MONTHLY','数学7月月考','2026-07-16',100.0,3,'2026-07-26 12:35:04','2026-07-26 12:35:04',0),(3,7,14,'QUIZ','英语单词测验-Unit1','2026-07-11',50.0,3,'2026-07-26 12:35:04','2026-07-26 12:35:04',0);
/*!40000 ALTER TABLE `edu_exam_score` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_exam_score_student`
--

LOCK TABLES `edu_exam_score_student` WRITE;
/*!40000 ALTER TABLE `edu_exam_score_student` DISABLE KEYS */;
INSERT INTO `edu_exam_score_student` VALUES (1,1,7,77.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(2,1,8,93.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(3,1,9,96.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(4,1,19,95.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(5,1,20,76.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(6,2,7,90.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(7,2,8,74.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(8,2,9,NULL,1,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(9,2,19,66.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(10,2,20,73.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(11,3,7,31.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(12,3,8,31.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(13,3,9,39.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(14,3,19,40.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04'),(15,3,20,34.0,0,'','2026-07-26 12:35:04','2026-07-26 12:35:04');
/*!40000 ALTER TABLE `edu_exam_score_student` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_homework`
--

LOCK TABLES `edu_homework` WRITE;
/*!40000 ALTER TABLE `edu_homework` DISABLE KEYS */;
INSERT INTO `edu_homework` VALUES (1,1,1,2,'美术课后作业 - 完成一幅静物素描','请同学们根据今天课堂所学，完成一幅简单的静物素描画，要求光影处理得当，下节课带来讲评。',2,0,'2026-07-26 11:34:38','2026-07-26 11:45:23',0),(2,6,16,11,'数学作业 - 分数加减法练习题','完成课本第45-46页的分数加减法练习题，共20道题目，要求写出计算过程，家长签字后明天交。',3,0,'2026-07-26 11:34:38','2026-07-26 11:45:23',0),(3,6,2,38,'数学课后练习-分数运算','完成课本第15页习题1-5',3,0,'2026-07-26 14:08:54','2026-07-26 14:08:54',0),(4,7,14,39,'英语单词默写','默写Unit3单词，每个5遍',3,0,'2026-07-26 14:08:54','2026-07-26 14:08:54',0);
/*!40000 ALTER TABLE `edu_homework` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_homework_attachment`
--

LOCK TABLES `edu_homework_attachment` WRITE;
/*!40000 ALTER TABLE `edu_homework_attachment` DISABLE KEYS */;
/*!40000 ALTER TABLE `edu_homework_attachment` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_homework_submission`
--

LOCK TABLES `edu_homework_submission` WRITE;
/*!40000 ALTER TABLE `edu_homework_submission` DISABLE KEYS */;
INSERT INTO `edu_homework_submission` VALUES (1,3,21,'已完成作业，详见附件',NULL,'SUBMITTED',NULL,NULL,'2026-07-26 14:08:54',NULL,NULL,'2026-07-26 14:08:54','2026-07-26 14:08:54',0),(2,3,9,'已完成作业，详见附件',NULL,'SUBMITTED',NULL,NULL,'2026-07-26 14:08:54',NULL,NULL,'2026-07-26 14:08:54','2026-07-26 14:08:54',0);
/*!40000 ALTER TABLE `edu_homework_submission` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_leave_conflict`
--

LOCK TABLES `edu_leave_conflict` WRITE;
/*!40000 ALTER TABLE `edu_leave_conflict` DISABLE KEYS */;
/*!40000 ALTER TABLE `edu_leave_conflict` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_leave_student`
--

LOCK TABLES `edu_leave_student` WRITE;
/*!40000 ALTER TABLE `edu_leave_student` DISABLE KEYS */;
INSERT INTO `edu_leave_student` VALUES (1,7,'2026-07-22',1,'感冒发烧，需在家休息',1,'PARENT',1,2,'2026-07-26 11:34:38',NULL,0,'1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0),(2,21,'2026-07-25',0,'家中有事，需请假一天',1,'TEACHER',3,3,'2026-07-26 11:34:38',NULL,0,'1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0),(3,8,'2026-07-26',1,'身体不适需要请假去医院',0,'STUDENT',8,3,NULL,NULL,0,'8','2026-07-26 14:08:54','8','2026-07-26 14:08:54',0);
/*!40000 ALTER TABLE `edu_leave_student` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_leave_teacher`
--

LOCK TABLES `edu_leave_teacher` WRITE;
/*!40000 ALTER TABLE `edu_leave_teacher` DISABLE KEYS */;
INSERT INTO `edu_leave_teacher` VALUES (1,3,'2026-07-28','2026-07-28',0,'个人事务需处理',0,'TEACHER',3,NULL,NULL,NULL,'1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0),(2,2,'2026-07-30','2026-07-30',1,'身体不适去医院检查',1,'TEACHER',2,1,'2026-07-26 11:34:38',NULL,'1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0);
/*!40000 ALTER TABLE `edu_leave_teacher` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_parent_view_log`
--

LOCK TABLES `edu_parent_view_log` WRITE;
/*!40000 ALTER TABLE `edu_parent_view_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `edu_parent_view_log` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_points_account`
--

LOCK TABLES `edu_points_account` WRITE;
/*!40000 ALTER TABLE `edu_points_account` DISABLE KEYS */;
INSERT INTO `edu_points_account` VALUES (1,1,5,0,5,'2026-07-25 21:32:08','2026-07-25 22:05:34'),(2,7,2000,600,1400,'2026-07-26 01:36:22','2026-07-26 01:49:30'),(3,21,100,0,100,'2026-07-26 11:33:40','2026-07-26 11:33:40'),(4,22,100,0,100,'2026-07-26 11:33:40','2026-07-26 11:33:40');
/*!40000 ALTER TABLE `edu_points_account` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_points_gift`
--

LOCK TABLES `edu_points_gift` WRITE;
/*!40000 ALTER TABLE `edu_points_gift` DISABLE KEYS */;
INSERT INTO `edu_points_gift` VALUES (1,'测试礼品-80841','https://via.placeholder.com/100',150,30,29,1,'验证用礼品','2026-07-26 01:36:22','2026-07-26 01:36:21',0),(2,'测试礼品-02281','https://via.placeholder.com/100',150,30,29,1,'验证用礼品','2026-07-26 01:43:23','2026-07-26 01:43:22',0),(3,'测试礼品-46092','https://via.placeholder.com/100',150,30,29,1,'验证用礼品','2026-07-26 01:47:27','2026-07-26 01:47:26',0),(4,'测试礼品-69817','https://via.placeholder.com/100',150,30,29,1,'验证用礼品','2026-07-26 01:49:30','2026-07-26 01:49:30',0);
/*!40000 ALTER TABLE `edu_points_gift` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_points_log`
--

LOCK TABLES `edu_points_log` WRITE;
/*!40000 ALTER TABLE `edu_points_log` DISABLE KEYS */;
INSERT INTO `edu_points_log` VALUES (1,1,'EARN',5,5,'SIGN_IN',NULL,'签到奖励：少儿美术基础班','2026-07-25 22:05:34'),(2,7,'ADJUST',500,500,'ADJUST',NULL,'全勤奖励','2026-07-26 01:36:22'),(3,7,'REDEEM',-150,350,'REDEEM',1,'兑换礼品：测试礼品-80841','2026-07-26 01:36:22'),(4,7,'ADJUST',500,850,'ADJUST',NULL,'全勤奖励','2026-07-26 01:43:23'),(5,7,'REDEEM',-150,700,'REDEEM',2,'兑换礼品：测试礼品-02281','2026-07-26 01:43:23'),(6,7,'ADJUST',500,1200,'ADJUST',NULL,'全勤奖励','2026-07-26 01:47:27'),(7,7,'REDEEM',-150,1050,'REDEEM',3,'兑换礼品：测试礼品-46092','2026-07-26 01:47:27'),(8,7,'ADJUST',500,1550,'ADJUST',NULL,'全勤奖励','2026-07-26 01:49:30'),(9,7,'REDEEM',-150,1400,'REDEEM',4,'兑换礼品：测试礼品-69817','2026-07-26 01:49:30');
/*!40000 ALTER TABLE `edu_points_log` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_points_redemption`
--

LOCK TABLES `edu_points_redemption` WRITE;
/*!40000 ALTER TABLE `edu_points_redemption` DISABLE KEYS */;
INSERT INTO `edu_points_redemption` VALUES (1,7,1,150,'CLAIMED','2026-07-26 01:36:22','2026-07-26 01:36:22',NULL,1,'2026-07-26 01:36:22','2026-07-26 01:36:22',0),(2,7,2,150,'CLAIMED','2026-07-26 01:43:23','2026-07-26 01:43:23',NULL,1,'2026-07-26 01:43:23','2026-07-26 01:43:23',0),(3,7,3,150,'CLAIMED','2026-07-26 01:47:27','2026-07-26 01:47:27',NULL,1,'2026-07-26 01:47:27','2026-07-26 01:47:27',0),(4,7,4,150,'CLAIMED','2026-07-26 01:49:30','2026-07-26 01:49:30',NULL,1,'2026-07-26 01:49:30','2026-07-26 01:49:30',0);
/*!40000 ALTER TABLE `edu_points_redemption` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_prospect`
--

LOCK TABLES `edu_prospect` WRITE;
/*!40000 ALTER TABLE `edu_prospect` DISABLE KEYS */;
INSERT INTO `edu_prospect` VALUES (4,'赵小明','M',NULL,'高三','市第一中学','13633330001','赵先生','FATHER','13533330001',NULL,5,NULL,'HIGH','REFERRAL',NULL,'CONVERTED',0,'2026-07-26 02:34:33',19,'数学105分，目标130+，朋友介绍','','2026-07-26 00:27:46','','2026-07-26 00:41:12',0),(5,'孙小红','F',NULL,'高一','市第二中学','13633330002','孙女士','MOTHER','13533330002',NULL,3,NULL,'MEDIUM','DIRECT_MARKETING',NULL,'CONVERTED',0,'2026-07-26 02:34:33',20,'英语基础薄弱','','2026-07-26 00:27:46','','2026-07-26 00:27:46',0),(6,'周强','M',NULL,'高二','实验中学','13633330003','周先生','FATHER','13533330003',NULL,4,NULL,'HIGH','ONLINE',NULL,'CONVERTED',0,'2026-07-26 00:27:46',7,'网上咨询，已约试听','','2026-07-26 00:27:46','','2026-07-26 00:27:46',0),(7,'测试','M',NULL,NULL,'','13594060674',NULL,NULL,NULL,NULL,NULL,NULL,'MEDIUM','OTHER',NULL,'CONVERTED',0,'2026-07-26 02:34:33',18,NULL,'','2026-07-26 01:43:07','','2026-07-26 01:43:07',0),(8,'测02281','M',NULL,NULL,'','13957255979','测爸',NULL,'13819233060',NULL,10,NULL,'HIGH','REFERRAL',NULL,'CONVERTED',0,'2026-07-26 02:34:33',17,'验证用意向学员','','2026-07-26 01:43:22','','2026-07-26 01:43:22',0),(9,'测46092','M',NULL,NULL,'','13933126439','测爸','FATHER','13882538600',NULL,11,NULL,'HIGH','REFERRAL',NULL,'CONVERTED',0,'2026-07-26 01:47:26',13,'验证用意向学员','','2026-07-26 01:47:26','','2026-07-26 01:47:26',0),(10,'测69817','M',NULL,NULL,'','13938158952','测爸','FATHER','13856974147',NULL,12,NULL,'HIGH','REFERRAL',NULL,'CONVERTED',0,'2026-07-26 01:49:30',15,'验证用意向学员','','2026-07-26 01:49:30','','2026-07-26 01:49:30',0);
/*!40000 ALTER TABLE `edu_prospect` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_prospect_followup`
--

LOCK TABLES `edu_prospect_followup` WRITE;
/*!40000 ALTER TABLE `edu_prospect_followup` DISABLE KEYS */;
INSERT INTO `edu_prospect_followup` VALUES (1,4,'PHONE','电话沟通：家长反映数学偏科，周末来试听','2026-07-26 00:41:12','2026-07-26前完成试听安排',1,'admin','2026-07-26 00:41:12','','2026-07-26 00:41:12',0),(2,6,'PHONE','电话沟通：预约本周六上午10点试听','2026-07-26 00:41:12','2026-07-27回访试听结果',1,'admin','2026-07-26 00:41:12','','2026-07-26 00:41:12',0),(3,9,'PHONE','电话沟通，家长很感兴趣，约了周末试听','2026-07-26 01:47:26','周末安排试听课程',1,'','2026-07-26 01:47:26','','2026-07-26 01:47:26',0),(4,10,'PHONE','电话沟通，家长很感兴趣，约了周末试听','2026-07-26 01:49:30','周末安排试听课程',1,'','2026-07-26 01:49:30','','2026-07-26 01:49:30',0),(5,8,'PHONE','电话沟通，家长表示感兴趣','2026-07-26 02:34:32','安排试听',1,'','2026-07-26 02:34:32','','2026-07-26 02:34:32',0),(6,7,'PHONE','电话沟通，家长表示感兴趣','2026-07-26 02:34:32','安排试听',1,'','2026-07-26 02:34:32','','2026-07-26 02:34:32',0),(7,4,'PHONE','电话沟通，家长表示感兴趣','2026-07-26 02:34:32','安排试听',1,'','2026-07-26 02:34:32','','2026-07-26 02:34:32',0),(8,5,'PHONE','电话沟通，家长表示感兴趣','2026-07-26 02:34:32','安排试听',1,'','2026-07-26 02:34:32','','2026-07-26 02:34:32',0);
/*!40000 ALTER TABLE `edu_prospect_followup` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_resource`
--

LOCK TABLES `edu_resource` WRITE;
/*!40000 ALTER TABLE `edu_resource` DISABLE KEYS */;
INSERT INTO `edu_resource` VALUES (1,'数学讲义 - 分数运算',13,'三年级','DOCUMENT','数学讲义 - 分数运算.docx','/uploads/resources/','/uploads/resources/',245760,'application/octet-stream',1,3,0,1,'数学讲义 - 分数运算 - 教学课件资料','1','2026-07-26 11:34:38','1','2026-07-26 12:33:09',0),(2,'英语习题集 - Unit 1-3',14,'三年级','EXERCISE','英语习题集 - Unit 1-3.pdf','/uploads/resources/','/uploads/resources/',512000,'application/octet-stream',1,3,0,1,'英语习题集 - Unit 1-3 - 教学课件资料','1','2026-07-26 11:34:38','1','2026-07-26 12:33:09',0),(3,'美术课件 - 色彩基础',18,'三年级','PPT','美术课件 - 色彩基础.pptx','/uploads/resources/','/uploads/resources/',1048576,'application/octet-stream',1,2,0,1,'美术课件 - 色彩基础 - 教学课件资料','1','2026-07-26 11:34:38','1','2026-07-26 12:33:09',0),(4,'小学数学四则运算讲义',13,'三年级','COURSEWARE','小学数学四则运算讲义.pdf','/fake/path.pdf','/files/fake.pdf',102400,'application/pdf',1,1,0,1,'公开数学课件','1','2026-07-26 13:10:31','1','2026-07-26 13:10:31',0),(5,'小学数学应用题解题技巧',13,'四年级','COURSEWARE','小学数学应用题解题技巧.pdf','/fake/path.pdf','/files/fake.pdf',102400,'application/pdf',1,1,0,1,'应用题专项','1','2026-07-26 13:10:31','1','2026-07-26 13:10:31',0),(6,'少儿英语单词卡片-基础100词',14,'启蒙','DOCUMENT','少儿英语单词卡片-基础100词.pdf','/fake/path.pdf','/files/fake.pdf',102400,'application/pdf',1,1,0,1,'英语基础词汇','1','2026-07-26 13:10:31','1','2026-07-26 13:10:31',0),(7,'少儿英语自然拼读练习',14,'启蒙','EXERCISE','少儿英语自然拼读练习.pdf','/fake/path.pdf','/files/fake.pdf',102400,'application/pdf',0,1,0,1,'自然拼读训练','1','2026-07-26 13:10:31','1','2026-07-26 13:10:31',0),(8,'儿童美术素描入门教程',18,'基础班','COURSEWARE','儿童美术素描入门教程.pdf','/fake/path.pdf','/files/fake.pdf',102400,'application/pdf',1,2,0,1,'素描基础教学','1','2026-07-26 13:10:31','1','2026-07-26 13:10:31',0),(9,'儿童水彩画技法指导',18,'基础班','DOCUMENT','儿童水彩画技法指导.pdf','/fake/path.pdf','/files/fake.pdf',102400,'application/pdf',1,2,0,1,'水彩画入门','1','2026-07-26 13:10:31','1','2026-07-26 13:10:31',0),(10,'语文阅读理解答题模板',12,'五年级','DOCUMENT','语文阅读理解答题模板.pdf','/fake/path.pdf','/files/fake.pdf',102400,'application/pdf',1,1,0,1,'阅读理解技巧','1','2026-07-26 13:10:31','1','2026-07-26 13:10:31',0);
/*!40000 ALTER TABLE `edu_resource` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_schedule`
--

LOCK TABLES `edu_schedule` WRITE;
/*!40000 ALTER TABLE `edu_schedule` DISABLE KEYS */;
INSERT INTO `edu_schedule` VALUES (1,3,1,1,2,1,'2026-07-25','09:00:00','10:30:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,1,'2026-07-25 22:05:34','1',NULL,'','2026-07-25 21:36:54','','2026-07-26 11:43:34',0),(2,3,1,1,2,1,'2026-07-10','09:00:00','10:30:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1','2026-07-26 01:02:06','','2026-07-26 11:43:34',0),(3,3,1,1,2,1,'2026-07-12','09:00:00','10:30:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1','2026-07-26 01:02:06','','2026-07-26 11:43:34',0),(4,3,1,1,2,1,'2026-07-15','09:00:00','10:30:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1','2026-07-26 01:02:06','','2026-07-26 11:43:34',0),(5,3,1,1,2,1,'2026-07-17','09:00:00','10:30:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1','2026-07-26 01:02:06','','2026-07-26 11:43:34',0),(6,3,1,1,2,1,'2026-07-19','09:00:00','10:30:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1','2026-07-26 01:02:06','','2026-07-26 11:43:34',0),(11,4,6,16,3,7,'2026-07-02','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(12,4,6,16,3,7,'2026-07-07','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(13,4,6,16,3,7,'2026-07-09','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(14,4,6,16,3,7,'2026-07-14','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(15,4,6,16,3,7,'2026-07-16','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(16,4,6,16,3,7,'2026-07-21','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(17,4,6,16,3,7,'2026-07-23','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(18,4,6,16,3,7,'2026-07-28','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(19,4,6,16,3,7,'2026-07-30','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(20,5,7,17,3,1,'2026-07-04','09:00:00','11:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(21,5,7,17,3,1,'2026-07-11','09:00:00','11:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(22,5,7,17,3,1,'2026-07-18','09:00:00','11:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(23,5,7,17,3,1,'2026-07-25','09:00:00','11:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 11:44:40','1','2026-07-26 11:44:40',0),(37,NULL,1,1,2,8,'2026-07-26','09:00:00','10:30:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 14:08:54','1','2026-07-26 14:13:58',0),(38,NULL,6,2,3,7,'2026-07-26','10:00:00','12:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 14:08:54','1','2026-07-26 14:13:58',0),(39,NULL,7,14,3,1,'2026-07-26','14:00:00','16:00:00','NORMAL','NORMAL',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'1','2026-07-26 14:08:54','1','2026-07-26 14:13:58',0);
/*!40000 ALTER TABLE `edu_schedule` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_schedule_plan`
--

LOCK TABLES `edu_schedule_plan` WRITE;
/*!40000 ALTER TABLE `edu_schedule_plan` DISABLE KEYS */;
INSERT INTO `edu_schedule_plan` VALUES (3,1,'[1]','2026-07-01','2026-07-31','GENERATED',NULL,'张老师少儿美术暑期班排课计划',1,'{\"weekDays\":[1,3,5],\"startTime\":\"14:00\",\"endTime\":\"16:00\",\"teacherId\":2}','1','2026-07-26 11:43:34','1','2026-07-26 11:43:34',0),(4,6,'[6]','2026-07-01','2026-07-31','GENERATED',NULL,'李老师高一数学暑期班排课计划',7,'{\"weekDays\":[2,4],\"startTime\":\"10:00\",\"endTime\":\"12:00\",\"teacherId\":3}','1','2026-07-26 11:43:34','1','2026-07-26 11:43:34',0),(5,7,'[7]','2026-07-01','2026-07-31','GENERATED',NULL,'李老师英语启蒙暑期班排课计划',1,'{\"weekDays\":[6],\"startTime\":\"09:00\",\"endTime\":\"11:00\",\"teacherId\":3}','1','2026-07-26 11:43:34','1','2026-07-26 11:43:34',0);
/*!40000 ALTER TABLE `edu_schedule_plan` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_student`
--

LOCK TABLES `edu_student` WRITE;
/*!40000 ALTER TABLE `edu_student` DISABLE KEYS */;
INSERT INTO `edu_student` VALUES (7,'周强','M',NULL,'高二','实验中学','13633330003',1,'周先生','FATHER','13533330003',NULL,NULL,4,NULL,'ONLINE','','ENROLLED',52.0,52.0,NULL,'2026-07-20 00:00:00','[意向转化] 网上咨询，已约试听','','2026-07-26 00:27:46','','2026-07-26 14:08:54',0),(8,'陈小美','F',NULL,'高三','市第一中学','13644440001',1,'陈女士','MOTHER','13544440001',NULL,NULL,NULL,NULL,'FRIEND','','COMPLETED',0.0,0.0,NULL,'2026-07-20 00:00:00','学霸型学员','','2026-07-26 00:27:46','','2026-07-26 13:20:48',0),(9,'林小军','M',NULL,'高一','市第三中学','13644440002',NULL,'林先生','FATHER','13544440002',NULL,NULL,NULL,NULL,'DIRECT_MARKETING','','ENROLLED',0.0,0.0,NULL,NULL,'英语入门','','2026-07-26 00:27:46','','2026-07-26 00:41:12',0),(19,'赵小明','M',NULL,'高三','市第一中学','13633330001',NULL,'赵先生','FATHER','13533330001',NULL,NULL,5,NULL,'REFERRAL','','ENROLLED',0.0,0.0,NULL,NULL,'[意向转化] 数学105分，目标130+，朋友介绍','','2026-07-26 02:34:33','','2026-07-26 02:34:33',0),(20,'孙小红','F',NULL,'高一','市第二中学','13633330002',NULL,'孙女士','MOTHER','13533330002',NULL,NULL,3,NULL,'DIRECT_MARKETING','','ENROLLED',0.0,0.0,NULL,NULL,'[意向转化] 英语基础薄弱','','2026-07-26 02:34:33','','2026-07-26 02:34:33',0),(21,'小红','M','2017-03-15','三年级','实验小学','4q3rPPMSrFg',1,'家长','MOTHER','48Jn08WgCn7',NULL,NULL,16,'','REFERRAL','','ENROLLED',48.0,47.0,NULL,NULL,NULL,'1','2026-07-26 11:33:40','','2026-07-26 11:34:38',0),(22,'小华','M','2017-03-15','三年级','实验小学','jLVv2jVr2tL',1,'家长','MOTHER','48Jn08WgCn7',NULL,NULL,17,'','REFERRAL','','ENROLLED',48.0,48.0,NULL,NULL,NULL,'1','2026-07-26 11:33:40','','2026-07-26 11:33:40',0),(23,'王磊','M',NULL,'高三毕业生','一中','13800138001',NULL,'王先生','FATHER','13900139001',NULL,NULL,NULL,NULL,'OFFLINE','','FINISHED',60.0,0.0,NULL,'2026-06-26 12:33:09','已结业学员','1','2026-07-26 12:33:09','1','2026-07-26 12:33:09',0),(24,'刘芳','F',NULL,'高三毕业生','二中','13800138002',NULL,'刘女士','FATHER','13900139002',NULL,NULL,NULL,NULL,'OFFLINE','','FINISHED',48.0,0.0,NULL,'2026-06-26 12:33:09','已结业学员','1','2026-07-26 12:33:09','1','2026-07-26 12:33:09',0);
/*!40000 ALTER TABLE `edu_student` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_student_hours_log`
--

LOCK TABLES `edu_student_hours_log` WRITE;
/*!40000 ALTER TABLE `edu_student_hours_log` DISABLE KEYS */;
INSERT INTO `edu_student_hours_log` VALUES (1,1,NULL,NULL,NULL,'PURCHASE',48.0,48.0,'TUITION',NULL,NULL,'purchase','2026-07-25 22:02:16'),(2,1,NULL,NULL,NULL,'CONSUME',-1.0,48.0,'ATTENDANCE',1,NULL,NULL,'2026-07-25 22:05:34'),(3,7,1,NULL,1,'GIFT_GAIN',4.0,52.0,'GIFT',NULL,1,'暑期报名赠送4课时','2026-07-26 11:34:38'),(4,21,16,NULL,6,'REFUND_DEDUCT',-1.0,47.0,'REFUND',NULL,1,'退费扣减1课时','2026-07-26 11:34:38');
/*!40000 ALTER TABLE `edu_student_hours_log` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_student_parent`
--

LOCK TABLES `edu_student_parent` WRITE;
/*!40000 ALTER TABLE `edu_student_parent` DISABLE KEYS */;
INSERT INTO `edu_student_parent` VALUES (1,4,NULL,'周先生','FATHER','13511110003',0,'','2026-07-26 00:23:34','','2026-07-26 00:23:34',0),(2,7,NULL,'周先生','FATHER','13533330003',0,'','2026-07-26 00:27:46','','2026-07-26 00:27:46',0),(3,13,NULL,'测爸','FATHER','13882538600',0,'','2026-07-26 01:47:26','','2026-07-26 01:47:26',0),(4,15,NULL,'测爸','FATHER','13856974147',0,'','2026-07-26 01:49:30','','2026-07-26 01:49:30',0),(5,17,NULL,'测爸','OTHER','13819233060',0,'','2026-07-26 02:34:33','','2026-07-26 02:34:33',0),(6,19,NULL,'赵先生','FATHER','13533330001',0,'','2026-07-26 02:34:33','','2026-07-26 02:34:33',0),(7,20,NULL,'孙女士','MOTHER','13533330002',0,'','2026-07-26 02:34:33','','2026-07-26 02:34:33',0);
/*!40000 ALTER TABLE `edu_student_parent` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_subject`
--

LOCK TABLES `edu_subject` WRITE;
/*!40000 ALTER TABLE `edu_subject` DISABLE KEYS */;
INSERT INTO `edu_subject` VALUES (12,'语文',1,1,'语文科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0),(13,'数学',2,1,'数学科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0),(14,'英语',3,1,'英语科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0),(15,'物理',4,1,'物理科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0),(16,'化学',5,1,'化学科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0),(17,'生物',6,1,'生物科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0),(18,'美术',7,1,'美术科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0),(19,'音乐',8,1,'音乐科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0),(20,'编程',9,1,'编程科目','1','2026-07-26 12:33:09','','2026-07-26 12:33:09',0);
/*!40000 ALTER TABLE `edu_subject` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_teacher`
--

LOCK TABLES `edu_teacher` WRITE;
/*!40000 ALTER TABLE `edu_teacher` DISABLE KEYS */;
INSERT INTO `edu_teacher` VALUES (10,2,'张老师','ACTIVE','数学','2024-09-01',120.00,'13811110001','高级教师10年教龄','2026-07-26 00:36:39','2026-07-26 01:30:07',0),(11,3,'李老师','ACTIVE','英语','2025-02-01',100.00,'13911110002','英语专八','2026-07-26 00:36:39','2026-07-26 01:30:07',0),(14,NULL,'王老师','ACTIVE','物理','2025-03-01',130.00,'13682338011','物理教师','2026-07-26 02:16:32','2026-07-26 02:16:32',1),(15,NULL,'孙老师','ACTIVE','化学-高中','2025-06-01',120.00,'13611239592','高中化学教师','2026-07-26 02:16:57','2026-07-26 02:16:57',1);
/*!40000 ALTER TABLE `edu_teacher` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_teacher_evaluation`
--

LOCK TABLES `edu_teacher_evaluation` WRITE;
/*!40000 ALTER TABLE `edu_teacher_evaluation` DISABLE KEYS */;
INSERT INTO `edu_teacher_evaluation` VALUES (1,3,7,11,5,'老师讲课很清楚，容易理解，非常耐心！','[\"讲解清晰\",\"认真负责\",\"耐心细致\"]',0,'ACTIVE','2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0),(2,3,8,11,4,'课堂气氛活跃，老师很负责，课后问题都能及时解答。','[\"课堂生动\",\"互动性强\",\"作业批改及时\"]',0,'ACTIVE','2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0),(3,3,7,20,5,'课堂气氛活跃，老师很负责，课后问题都能及时解答。','[\"课堂生动\",\"互动性强\",\"作业批改及时\"]',0,'ACTIVE','2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0),(4,3,8,20,5,'教学方法很好，孩子进步明显，感谢老师的辛勤付出。','[\"专业水平高\",\"课堂氛围好\",\"知识点透彻\"]',0,'ACTIVE','2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0),(5,3,7,12,5,'教学方法很好，孩子进步明显，感谢老师的辛勤付出。','[\"专业水平高\",\"课堂氛围好\",\"知识点透彻\"]',0,'ACTIVE','2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0),(6,3,8,12,4,'课程内容丰富，讲解生动有趣，收获很大。','[\"讲解清晰\",\"认真负责\",\"耐心细致\"]',0,'ACTIVE','2026-07-26 12:35:04','2026-07-26 12:35:04','2026-07-26 12:35:04',0);
/*!40000 ALTER TABLE `edu_teacher_evaluation` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_teacher_pay_rate`
--

LOCK TABLES `edu_teacher_pay_rate` WRITE;
/*!40000 ALTER TABLE `edu_teacher_pay_rate` DISABLE KEYS */;
INSERT INTO `edu_teacher_pay_rate` VALUES (1,2,1,80.00,'2026-07-01',NULL,'1','2026-07-25 21:32:08','','2026-07-26 01:42:48',0),(2,3,2,150.00,'2026-07-01',NULL,'1','2026-07-26 01:02:06','','2026-07-26 01:42:48',0),(3,2,9,100.00,'2026-07-27',NULL,'','2026-07-26 11:01:59','','2026-07-26 11:08:43',1),(4,3,16,100.00,'2026-07-01',NULL,'1','2026-07-26 11:33:40','','2026-07-26 11:33:40',0),(5,3,17,90.00,'2026-07-01',NULL,'1','2026-07-26 11:33:40','','2026-07-26 11:33:40',0);
/*!40000 ALTER TABLE `edu_teacher_pay_rate` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_teacher_payroll`
--

LOCK TABLES `edu_teacher_payroll` WRITE;
/*!40000 ALTER TABLE `edu_teacher_payroll` DISABLE KEYS */;
INSERT INTO `edu_teacher_payroll` VALUES (12,2,'2026-07',1,6,80.00,480.00,'CONFIRMED','2026-07-24 12:35:04',1,'1','2026-07-26 01:43:07','1','2026-07-26 12:35:04',0),(13,3,'2026-07',2,4,150.00,600.00,'CONFIRMED','2026-07-26 12:59:27',1,'1','2026-07-26 01:43:07','1','2026-07-26 12:59:27',0);
/*!40000 ALTER TABLE `edu_teacher_payroll` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_tuition_fee`
--

LOCK TABLES `edu_tuition_fee` WRITE;
/*!40000 ALTER TABLE `edu_tuition_fee` DISABLE KEYS */;
INSERT INTO `edu_tuition_fee` VALUES (1,'T178499771132401',1,8,5,NULL,'TUITION',9000.00,9000.00,'微信','PAID','2026-07-26 00:41:51','全款缴纳',1,'2026-07-26 00:41:51','','2026-07-26 12:35:04',0),(2,'T178499771132402',2,9,3,NULL,'TUITION',2400.00,1200.00,'支付宝','PARTIAL','2026-07-26 00:41:51','首付1200，剩余下月缴清',1,'2026-07-26 00:41:51','','2026-07-26 14:46:13',0),(3,'T202607263719',7,7,1,1,'TUITION',4800.00,4800.00,'WECHAT','PAID','2026-07-26 11:34:38',NULL,1,'2026-07-26 11:34:38','1','2026-07-26 12:35:04',0),(4,'T202607269800',8,21,16,6,'TUITION',5200.00,5200.00,'WECHAT','PAID','2026-07-26 11:34:38',NULL,1,'2026-07-26 11:34:38','1','2026-07-26 12:35:04',0),(5,'T202607269189',9,22,17,7,'TUITION',5600.00,5600.00,'WECHAT','PAID','2026-07-26 11:34:38',NULL,1,'2026-07-26 11:34:38','1','2026-07-26 12:35:04',0);
/*!40000 ALTER TABLE `edu_tuition_fee` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_tuition_gift_hours`
--

LOCK TABLES `edu_tuition_gift_hours` WRITE;
/*!40000 ALTER TABLE `edu_tuition_gift_hours` DISABLE KEYS */;
INSERT INTO `edu_tuition_gift_hours` VALUES (1,'G202607261462',7,1,4,'PROMOTION','暑期报名优惠','暑期报名赠送4课时',1,'1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0);
/*!40000 ALTER TABLE `edu_tuition_gift_hours` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `edu_tuition_refund`
--

LOCK TABLES `edu_tuition_refund` WRITE;
/*!40000 ALTER TABLE `edu_tuition_refund` DISABLE KEYS */;
INSERT INTO `edu_tuition_refund` VALUES (1,'R202607268760',4,21,16,200.00,'OTHER','课程调整退费','ORIGINAL','COMPLETED',1,1,1,'2026-07-26 11:34:38',NULL,'课程调整部分退费','1','2026-07-26 11:34:38','1','2026-07-26 11:34:38',0);
/*!40000 ALTER TABLE `edu_tuition_refund` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'用户初始密码','sys.user.initPassword','123456','Y','','2026-07-25 20:40:34','','2026-07-25 20:40:34','用户新增/重置的初始密码',0),(2,'系统名称','sys.system.name','新艺智源管理平台','Y','','2026-07-25 20:40:34','','2026-07-25 20:40:34','系统名称',0),(3,'系统描述','sys.system.description','在线教育课程展示与交易平台','Y','','2026-07-25 20:40:34','','2026-07-25 20:40:34','系统描述',0),(4,'账号最大重试次数','sys.account.maxRetryCount','5','Y','','2026-07-25 20:40:34','','2026-07-25 20:40:34','登录失败锁定阈值',0),(5,'账号锁定时间(分)','sys.account.lockTime','10','Y','','2026-07-25 20:40:34','','2026-07-25 20:40:34','登录失败锁定时长',0);
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,'sys_user_sex','男','1',1,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(2,'sys_user_sex','女','2',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(3,'sys_user_sex','未知','0',3,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(4,'sys_normal_disable','正常','1',1,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(5,'sys_normal_disable','停用','0',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(6,'sys_yes_no','是','Y',1,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(7,'sys_yes_no','否','N',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(8,'sys_notice_type','通知','1',1,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(9,'sys_notice_type','公告','2',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(10,'sys_menu_type','目录','M',1,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(11,'sys_menu_type','菜单','C',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(12,'sys_menu_type','按钮','F',3,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(13,'sys_show_hide','显示','1',1,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(14,'sys_show_hide','隐藏','0',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(15,'student_source','地推','DIRECT_MARKETING',1,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(16,'student_source','转介绍','REFERRAL',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(17,'student_source','线上','ONLINE',3,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(18,'student_source','其他','OTHER',4,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(19,'interest_level','高','HIGH',1,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(20,'interest_level','中','MEDIUM',2,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(21,'interest_level','低','LOW',3,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(22,'prospect_follow_status','新线索','NEW',1,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(23,'prospect_follow_status','已联系','CONTACTED',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(24,'prospect_follow_status','已试听','TRIAL',3,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(25,'prospect_follow_status','已转化','CONVERTED',4,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(26,'prospect_follow_status','已流失','LOST',5,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(27,'parent_relation','本人','SELF',0,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(28,'parent_relation','父亲','FATHER',1,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(29,'parent_relation','母亲','MOTHER',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(30,'parent_relation','爷爷','GRANDFATHER',3,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(31,'parent_relation','奶奶','GRANDMOTHER',4,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(32,'parent_relation','外公','GRANDFATHER_M',5,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(33,'parent_relation','外婆','GRANDMOTHER_M',6,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(34,'parent_relation','其他','OTHER',7,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(35,'student_status','在读','ENROLLED',1,NULL,NULL,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(36,'student_status','停课','SUSPENDED',2,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(37,'student_status','结业','COMPLETED',3,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0),(38,'student_status','流失','LOST',4,NULL,NULL,0,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',NULL,0);
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','用户性别列表',0),(2,'系统开关','sys_normal_disable',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','系统开关列表',0),(3,'系统是否','sys_yes_no',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','系统是否列表',0),(4,'通知类型','sys_notice_type',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','通知类型列表',0),(5,'菜单类型','sys_menu_type',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','菜单类型列表',0),(6,'显示状态','sys_show_hide',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','显示状态列表',0),(7,'来源渠道','student_source',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','意向学员来源渠道',0),(8,'意向等级','interest_level',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','意向学员意向等级',0),(9,'跟进状态','prospect_follow_status',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','意向学员跟进状态',0),(10,'亲属关系','parent_relation',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','家长亲属关系',0),(11,'学员状态','student_status',1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34','在学学员状态',0);
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_login_log`
--

LOCK TABLES `sys_login_log` WRITE;
/*!40000 ALTER TABLE `sys_login_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_login_log` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,0,'系统管理','M','/system',NULL,'System',NULL,1,0,'system',NULL,1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(2,0,'学员管理','M','/student',NULL,'Student',NULL,1,0,'peoples',NULL,2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(3,0,'教务管理','M','/course',NULL,'Course',NULL,1,0,'education',NULL,3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(4,0,'教务教学','M','/teaching',NULL,'Teaching',NULL,1,0,'guide',NULL,4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(5,0,'财务管理','M','/finance',NULL,'Finance',NULL,1,0,'money',NULL,5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(6,0,'考勤管理','M','/attendance',NULL,'Attendance',NULL,1,0,'time',NULL,6,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(7,0,'积分商城','M','/points',NULL,'Points',NULL,1,0,'star',NULL,7,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(8,0,'教师端','M','/teacher',NULL,'Teacher',NULL,1,0,'user',NULL,8,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(9,0,'工作台','C','/dashboard','dashboard/index','Dashboard',NULL,0,0,'dashboard','dashboard:dashboard:view',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(100,1,'用户管理','C','user','system/user/index','SysUser',NULL,1,0,'user','system:user:list',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(101,1,'角色管理','C','role','system/role/index','SysRole',NULL,1,0,'peoples','system:role:list',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(102,1,'菜单管理','C','menu','system/menu/index','SysMenu',NULL,1,0,'tree-table','system:menu:list',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(103,1,'字典管理','C','dict','system/dict/index','SysDict',NULL,1,0,'dict','system:dict:list',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(104,1,'参数设置','C','config','system/config/index','SysConfig',NULL,1,0,'edit','system:config:list',5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(105,1,'通知公告','C','notice','system/notice/index','SysNotice',NULL,1,0,'message','system:notice:list',6,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(106,1,'日志管理','C','log','system/log/index','SysLog',NULL,1,0,'log','system:log:list',7,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(107,100,'用户查询','F','',NULL,'',NULL,1,0,'','system:user:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(108,100,'用户新增','F','',NULL,'',NULL,1,0,'','system:user:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(109,100,'用户修改','F','',NULL,'',NULL,1,0,'','system:user:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(110,100,'用户删除','F','',NULL,'',NULL,1,0,'','system:user:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(111,100,'用户导出','F','',NULL,'',NULL,1,0,'','system:user:export',5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(112,100,'用户导入','F','',NULL,'',NULL,1,0,'','system:user:import',6,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(113,100,'重置密码','F','',NULL,'',NULL,1,0,'','system:user:resetPwd',7,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(114,101,'角色查询','F','',NULL,'',NULL,1,0,'','system:role:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(115,101,'角色新增','F','',NULL,'',NULL,1,0,'','system:role:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(116,101,'角色修改','F','',NULL,'',NULL,1,0,'','system:role:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(117,101,'角色删除','F','',NULL,'',NULL,1,0,'','system:role:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(118,102,'菜单查询','F','',NULL,'',NULL,1,0,'','system:menu:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(119,102,'菜单新增','F','',NULL,'',NULL,1,0,'','system:menu:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(120,102,'菜单修改','F','',NULL,'',NULL,1,0,'','system:menu:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(121,102,'菜单删除','F','',NULL,'',NULL,1,0,'','system:menu:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(122,103,'字典查询','F','',NULL,'',NULL,1,0,'','system:dict:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(123,103,'字典新增','F','',NULL,'',NULL,1,0,'','system:dict:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(124,103,'字典修改','F','',NULL,'',NULL,1,0,'','system:dict:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(125,103,'字典删除','F','',NULL,'',NULL,1,0,'','system:dict:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(126,104,'参数查询','F','',NULL,'',NULL,1,0,'','system:config:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(127,104,'参数新增','F','',NULL,'',NULL,1,0,'','system:config:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(128,104,'参数修改','F','',NULL,'',NULL,1,0,'','system:config:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(129,104,'参数删除','F','',NULL,'',NULL,1,0,'','system:config:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(130,105,'公告查询','F','',NULL,'',NULL,1,0,'','system:notice:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(131,105,'公告新增','F','',NULL,'',NULL,1,0,'','system:notice:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(132,105,'公告修改','F','',NULL,'',NULL,1,0,'','system:notice:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(133,105,'公告删除','F','',NULL,'',NULL,1,0,'','system:notice:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(134,106,'日志查询','F','',NULL,'',NULL,1,0,'','system:log:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(135,106,'日志删除','F','',NULL,'',NULL,1,0,'','system:log:delete',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(200,2,'意向学员','C','prospect','student/prospect/index','StudentProspect',NULL,1,0,'#','student:prospect:list',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(201,2,'在读学员','C','enrolled','student/enrolled/index','StudentEnrolled',NULL,1,0,'#','student:enrolled:list',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(202,2,'结业学员','C','completed','student/completed/index','StudentCompleted',NULL,1,0,'#','student:completed:list',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(203,2,'学员学时','C','hours','student/hours/index','StudentHours',NULL,1,0,'#','student:hours:list',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(210,3,'科目管理','C','subject','course/subject/index','CourseSubject',NULL,1,0,'#','course:subject:list',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(211,3,'教室管理','C','classroom','course/classroom/index','CourseClassroom',NULL,1,0,'#','course:classroom:list',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(212,3,'课程管理','C','course','course/course/index','CourseManage',NULL,1,0,'#','course:course:list',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(213,3,'班级管理','C','class','course/class/index','ClassManage',NULL,1,0,'#','class:class:list',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(214,3,'排课方案','C','schedulePlan','course/schedulePlan/index','SchedulePlan',NULL,1,0,'#','schedule:plan:list',5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(215,3,'排课日历','C','schedule','course/schedule/index','ScheduleCalendar',NULL,1,0,'#','schedule:calendar:list',6,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(216,3,'课件资源','C','resource','course/resource/index','CourseResource',NULL,1,0,'#','course:resource:list',7,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(220,4,'考试管理','C','exam','teaching/exam/index','TeachingExam',NULL,1,0,'#','teaching:exam:list',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(221,4,'作业管理','C','homework','teaching/homework/index','TeachingHomework',NULL,1,0,'#','teaching:homework:list',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(222,4,'教评学','C','evaluation','teaching/evaluation/index','TeachingEvaluation',NULL,1,0,'#','teaching:evaluation:list',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(223,4,'学评教','C','teacherEvaluation','teaching/teacherEvaluation/index','TeachingTeacherEvaluation',NULL,1,0,'#','teaching:teacher-evaluation:list',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(224,4,'教师管理','C','teacher','teaching/teacher/index','TeachingTeacher',NULL,1,0,'#','teaching:teacher:list',5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(230,5,'学费管理','C','tuition','finance/tuition/index','FinanceTuition',NULL,1,0,'#','tuition:fee:list',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(231,5,'薪资管理','C','payroll','finance/payroll/index','FinancePayroll',NULL,1,0,'#','payroll:payroll:list',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(232,5,'教学统计','C','teachingStats','finance/teachingStats/index','FinanceTeachingStats',NULL,1,0,'#','payroll:pay-rate:list',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(240,6,'签到记录','C','signin','attendance/signin/index','AttendanceSignin',NULL,1,0,'#','attendance:signin:list',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(241,6,'学生请假','C','leave/student','attendance/leave/student/index','AttendanceLeaveStudent',NULL,1,0,'#','attendance:leave:student:list',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(242,6,'教师请假','C','leave/teacher','attendance/leave/teacher/index','AttendanceLeaveTeacher',NULL,1,0,'#','attendance:leave:teacher:list',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(250,7,'礼品管理','C','gift','points/gift/index','PointsGift',NULL,1,0,'#','points:gift:list',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(251,7,'兑换记录','C','redemption','points/redemption/index','PointsRedemption',NULL,1,0,'#','points:redemption:list',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(260,8,'工作台','C','dashboard','teacher/dashboard/index','TeacherDashboard',NULL,1,0,'#','teacher:dashboard:view',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(261,8,'课后作业','C','homework','teacher/homework/index','TeacherHomework',NULL,1,0,'#','teaching:homework:list',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(262,8,'课件共享','C','resource','teacher/resource/index','TeacherResource',NULL,1,0,'#','course:resource:list',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(263,8,'我的课表','C','my-schedule','teacher/my-schedule/index','TeacherMySchedule',NULL,1,0,'#','schedule:calendar:list',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(264,200,'意向学员查询','F','',NULL,'',NULL,1,0,'','student:prospect:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(265,200,'意向学员新增','F','',NULL,'',NULL,1,0,'','student:prospect:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(266,200,'意向学员修改','F','',NULL,'',NULL,1,0,'','student:prospect:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(267,200,'意向学员删除','F','',NULL,'',NULL,1,0,'','student:prospect:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(268,200,'意向学员转化','F','',NULL,'',NULL,1,0,'','student:prospect:convert',5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(269,200,'跟进记录管理','F','',NULL,'',NULL,1,0,'','student:prospect:followup',6,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(270,201,'在读学员查询','F','',NULL,'',NULL,1,0,'','student:enrolled:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(271,201,'在读学员新增','F','',NULL,'',NULL,1,0,'','student:enrolled:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(272,201,'在读学员修改','F','',NULL,'',NULL,1,0,'','student:enrolled:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(273,201,'在读学员删除','F','',NULL,'',NULL,1,0,'','student:enrolled:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(274,202,'结业学员查询','F','',NULL,'',NULL,1,0,'','student:completed:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(275,203,'学员学时列表','F','',NULL,'',NULL,1,0,'','student:hours:list',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(276,203,'学员学时查询','F','',NULL,'',NULL,1,0,'','student:hours:query',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(277,203,'学员学时新增','F','',NULL,'',NULL,1,0,'','student:hours:add',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(278,204,'学员家长查询','F','',NULL,'',NULL,1,0,'','student:parent:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(279,204,'学员家长新增','F','',NULL,'',NULL,1,0,'','student:parent:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(280,204,'学员家长修改','F','',NULL,'',NULL,1,0,'','student:parent:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(281,204,'学员家长删除','F','',NULL,'',NULL,1,0,'','student:parent:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(282,210,'科目查询','F','',NULL,'',NULL,1,0,'','course:subject:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(283,210,'科目新增','F','',NULL,'',NULL,1,0,'','course:subject:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(284,210,'科目修改','F','',NULL,'',NULL,1,0,'','course:subject:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(285,210,'科目删除','F','',NULL,'',NULL,1,0,'','course:subject:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(286,211,'教室查询','F','',NULL,'',NULL,1,0,'','course:classroom:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(287,211,'教室新增','F','',NULL,'',NULL,1,0,'','course:classroom:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(288,211,'教室修改','F','',NULL,'',NULL,1,0,'','course:classroom:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(289,211,'教室删除','F','',NULL,'',NULL,1,0,'','course:classroom:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(290,212,'课程查询','F','',NULL,'',NULL,1,0,'','course:course:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(291,212,'课程新增','F','',NULL,'',NULL,1,0,'','course:course:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(292,212,'课程修改','F','',NULL,'',NULL,1,0,'','course:course:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(293,212,'课程删除','F','',NULL,'',NULL,1,0,'','course:course:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(294,213,'班级查询','F','',NULL,'',NULL,1,0,'','class:class:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(295,213,'班级新增','F','',NULL,'',NULL,1,0,'','class:class:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(296,213,'班级修改','F','',NULL,'',NULL,1,0,'','class:class:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(297,213,'班级删除','F','',NULL,'',NULL,1,0,'','class:class:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(298,213,'班级学员管理','F','',NULL,'',NULL,1,0,'','class:student:add',5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(299,213,'班级学员列表','F','',NULL,'',NULL,1,0,'','class:student:list',6,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(300,213,'班级学员修改','F','',NULL,'',NULL,1,0,'','class:student:edit',7,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(301,213,'班级学员删除','F','',NULL,'',NULL,1,0,'','class:student:delete',8,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(302,214,'排课方案查询','F','',NULL,'',NULL,1,0,'','schedule:plan:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(303,214,'排课方案新增','F','',NULL,'',NULL,1,0,'','schedule:plan:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(304,214,'排课方案修改','F','',NULL,'',NULL,1,0,'','schedule:plan:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(305,214,'排课方案删除','F','',NULL,'',NULL,1,0,'','schedule:plan:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(306,215,'排课日历查询','F','',NULL,'',NULL,1,0,'','schedule:calendar:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(307,215,'排课取消','F','',NULL,'',NULL,1,0,'','schedule:cancel',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(308,215,'排课调课','F','',NULL,'',NULL,1,0,'','schedule:reschedule',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(309,215,'排课消课','F','',NULL,'',NULL,1,0,'','schedule:consume',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(310,216,'课件资源查询','F','',NULL,'',NULL,1,0,'','course:resource:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(311,216,'课件资源新增','F','',NULL,'',NULL,1,0,'','course:resource:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(312,216,'课件资源修改','F','',NULL,'',NULL,1,0,'','course:resource:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(313,216,'课件资源删除','F','',NULL,'',NULL,1,0,'','course:resource:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(314,220,'考试查询','F','',NULL,'',NULL,1,0,'','teaching:exam:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(315,220,'考试新增','F','',NULL,'',NULL,1,0,'','teaching:exam:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(316,220,'考试修改','F','',NULL,'',NULL,1,0,'','teaching:exam:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(317,220,'考试删除','F','',NULL,'',NULL,1,0,'','teaching:exam:remove',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(318,221,'作业查询','F','',NULL,'',NULL,1,0,'','teaching:homework:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(319,221,'作业新增','F','',NULL,'',NULL,1,0,'','teaching:homework:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(320,221,'作业修改','F','',NULL,'',NULL,1,0,'','teaching:homework:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(321,221,'作业删除','F','',NULL,'',NULL,1,0,'','teaching:homework:remove',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(322,222,'教评学查询','F','',NULL,'',NULL,1,0,'','teaching:evaluation:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(323,222,'教评学新增','F','',NULL,'',NULL,1,0,'','teaching:evaluation:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(324,222,'教评学删除','F','',NULL,'',NULL,1,0,'','teaching:evaluation:remove',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(325,223,'学评教查询','F','',NULL,'',NULL,1,0,'','teaching:teacher-evaluation:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(326,223,'学评教导出','F','',NULL,'',NULL,1,0,'','teaching:teacher-evaluation:export',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(327,224,'教师查询','F','',NULL,'',NULL,1,0,'','teaching:teacher:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(328,224,'教师新增','F','',NULL,'',NULL,1,0,'','teaching:teacher:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(329,224,'教师修改','F','',NULL,'',NULL,1,0,'','teaching:teacher:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(330,224,'教师删除','F','',NULL,'',NULL,1,0,'','teaching:teacher:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(331,230,'学费查询','F','',NULL,'',NULL,1,0,'','tuition:fee:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(332,230,'学费新增','F','',NULL,'',NULL,1,0,'','tuition:fee:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(333,230,'学费修改','F','',NULL,'',NULL,1,0,'','tuition:fee:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(334,230,'学费删除','F','',NULL,'',NULL,1,0,'','tuition:fee:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(335,230,'退费列表','F','',NULL,'',NULL,1,0,'','tuition:refund:list',5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(336,230,'退费申请','F','',NULL,'',NULL,1,0,'','tuition:refund:apply',6,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(337,230,'退费审批','F','',NULL,'',NULL,1,0,'','tuition:refund:approve',7,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(338,230,'退费删除','F','',NULL,'',NULL,1,0,'','tuition:refund:delete',8,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(339,230,'赠课列表','F','',NULL,'',NULL,1,0,'','tuition:gift:list',9,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(340,230,'赠课新增','F','',NULL,'',NULL,1,0,'','tuition:gift:add',10,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(341,230,'赠课删除','F','',NULL,'',NULL,1,0,'','tuition:gift:delete',11,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(342,230,'报名新增','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:add',12,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(343,230,'报名修改','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:edit',13,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(344,230,'报名取消','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:cancel',14,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(345,230,'报名列表','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:list',15,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(346,230,'报名查询','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:query',16,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(347,231,'薪资查询','F','',NULL,'',NULL,1,0,'','payroll:payroll:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(348,231,'薪资新增','F','',NULL,'',NULL,1,0,'','payroll:payroll:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(349,231,'薪资确认','F','',NULL,'',NULL,1,0,'','payroll:payroll:confirm',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(350,231,'薪资发放','F','',NULL,'',NULL,1,0,'','payroll:payroll:pay',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(351,231,'薪资删除','F','',NULL,'',NULL,1,0,'','payroll:payroll:delete',5,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(352,232,'课时费率查询','F','',NULL,'',NULL,1,0,'','payroll:pay-rate:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(353,232,'课时费率新增','F','',NULL,'',NULL,1,0,'','payroll:pay-rate:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(354,232,'课时费率修改','F','',NULL,'',NULL,1,0,'','payroll:pay-rate:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(355,232,'课时费率删除','F','',NULL,'',NULL,1,0,'','payroll:pay-rate:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(356,240,'签到记录查询','F','',NULL,'',NULL,1,0,'','attendance:signin:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(357,240,'签到记录新增','F','',NULL,'',NULL,1,0,'','attendance:signin:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(358,241,'学生请假查询','F','',NULL,'',NULL,1,0,'','attendance:leave:student:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(359,241,'学生请假新增','F','',NULL,'',NULL,1,0,'','attendance:leave:student:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(360,241,'学生请假审批','F','',NULL,'',NULL,1,0,'','attendance:leave:student:approve',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(361,242,'教师请假查询','F','',NULL,'',NULL,1,0,'','attendance:leave:teacher:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(362,242,'教师请假新增','F','',NULL,'',NULL,1,0,'','attendance:leave:teacher:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(363,242,'教师请假审批','F','',NULL,'',NULL,1,0,'','attendance:leave:teacher:approve',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(364,242,'教师请假冲突处理','F','',NULL,'',NULL,1,0,'','attendance:leave:teacher:resolve',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(365,250,'礼品查询','F','',NULL,'',NULL,1,0,'','points:gift:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(366,250,'礼品新增','F','',NULL,'',NULL,1,0,'','points:gift:add',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(367,250,'礼品修改','F','',NULL,'',NULL,1,0,'','points:gift:edit',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(368,250,'礼品删除','F','',NULL,'',NULL,1,0,'','points:gift:delete',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(369,251,'兑换记录查询','F','',NULL,'',NULL,1,0,'','points:redemption:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(370,251,'兑换记录导出','F','',NULL,'',NULL,1,0,'','points:redemption:export',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(371,251,'兑换发放','F','',NULL,'',NULL,1,0,'','points:redemption:claim',3,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(372,251,'兑换取消','F','',NULL,'',NULL,1,0,'','points:redemption:cancel',4,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(373,9,'工作台查看','F','',NULL,'',NULL,1,0,'','dashboard:dashboard:view',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(374,7,'积分账户查询','F','',NULL,'',NULL,1,0,'','points:account:query',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(375,7,'积分账户调整','F','',NULL,'',NULL,1,0,'','points:account:adjust',2,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(376,260,'教师工作台查看','F','',NULL,'',NULL,1,0,'','teacher:dashboard:view',1,1,1,'','2026-07-25 20:40:34','','2026-07-25 20:40:34',0);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (1,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"MSEdge\",\"os\":\"Windows\"}',NULL,1,NULL,702,'2026-07-25 20:41:50'),(2,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,492,'2026-07-25 21:13:22'),(3,'科目管理','INSERT','com.education.store.module.course.controller.EduSubjectController.create','POST','超级管理员','/api/v1/subjects','127.0.0.1','','{\"name\":\"测试科目001\",\"sortOrder\":0,\"status\":1,\"description\":\"\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784985353564}',1,NULL,38,'2026-07-25 21:15:54'),(4,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,484,'2026-07-25 21:29:41'),(5,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,77,'2026-07-25 21:29:49'),(6,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,77,'2026-07-25 21:34:17'),(7,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,69,'2026-07-25 21:34:49'),(8,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,67,'2026-07-25 21:35:31'),(9,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,66,'2026-07-25 21:36:24'),(10,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,67,'2026-07-25 21:37:28'),(11,'签到管理','INSERT','com.education.store.module.attendance.controller.EduAttendanceController.batchSignin','POST','超级管理员','/api/v1/attendance/signin/batch','0:0:0:0:0:0:0:1','','{};{};[{\"studentId\":1,\"status\":0}];{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784986648388}',1,NULL,45,'2026-07-25 21:37:28'),(12,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjFkMzk5M2JjLTQ0YjAtNDVlYi04ZjFmLTVlMGIwYmI5YTdiNCIsImlhdCI6MTc4NDk4NzEzMywiZXhwIjoxNzg0OTk0MzMzfQ.l5wo-ZyuU0nhfWv-ATrym0b3NZ9wUHnsY7diT9a7PytA9gPb6or59Atu6bbjbLq0YCu2yTsp6XS6X_fKPyhogQ\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI0MjAyYTY5Mi0zNDgyLTRhMzEtOTg5NS1jM2RjYTExOTlmYjciLCJpYXQiOjE3ODQ5ODcxMzMsImV4cCI6MTc4NTU5MTkzM30.AYyavWqwwIH5E4W3Vhxs2e-z6tbZLLyo-GeGeSkIUrSjjk4NhQD72pNK-v_ISZO3SfaHlO7g_TVk80KR1TDrxg\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987133984}',1,NULL,226,'2026-07-25 21:45:34'),(13,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6ImY0YmI2NDZhLTgzOGItNGM0My1iNmYxLTBiNTE0ZjY5ZWRmZCIsImlhdCI6MTc4NDk4NzE2NiwiZXhwIjoxNzg0OTk0MzY2fQ.wqM4xeHsUIpZX29zghwhTpdSc3DRCaxwINmZNMMBKrQ2jU0P1v7Qn_2SqUs26JVExh_T6aHtVohjmhE4wm0BBQ\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiIwZDYzM2UyNS1mZDc1LTRiNGMtODQ4Yi1mY2MzNDRkMDZlMzMiLCJpYXQiOjE3ODQ5ODcxNjYsImV4cCI6MTc4NTU5MTk2Nn0.TYbfShnct2Hd68Du4tfA_pcTUmXoPZuvqiEt3vy1Li01GdoR-3ud4qKVoU2PNCDvSjZuGKQ-EN3q35ugs2YkJQ\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987166131}',1,NULL,108,'2026-07-25 21:46:06'),(14,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjcwMGEwYjc1LWJmMTAtNDYzNS04YTZkLWE0NjljZTgyMTUzNyIsImlhdCI6MTc4NDk4NzM5OCwiZXhwIjoxNzg0OTk0NTk4fQ.fCK1sWyr2b_oj8usuLpz03fTYoyWPWMrvlanbYmCD69ynybGXz4ah6y5tyIASZ4BvjZdYOlvYnz2Msmars8gNA\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiJjMDQzYzYxZC1hZmNkLTQ2MDEtYmYwMi05NzM2MjY0OTIzNDYiLCJpYXQiOjE3ODQ5ODczOTgsImV4cCI6MTc4NTU5MjE5OH0.iDDlJKvgQ36s3x6TprEdL4IqlkD7yy2rDu0sdW-aSiyVvfSH6FQBINwnWkjTmQTJJp0WIpma892L0PDDIMVajw\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987398147}',1,NULL,899,'2026-07-25 21:49:57'),(15,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6Ijg2MjgwY2ExLTU2YmMtNGNkZi1hZmQ1LTNkNjNmNDE5MTQzMCIsImlhdCI6MTc4NDk4NzQ1MywiZXhwIjoxNzg0OTk0NjUzfQ.f_Xim_xgCeT0zlfKb9mpAW-mwzwD1YqmBhEnCsU8QILWRFLJLhOoNtzqqBh6BZPEF3FmaDO_wcyJ4GD3YGo3fw\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI1NmQ3NWMwOC01NmUzLTQzMTctYjQyNC05MjdiODFkMTgyM2MiLCJpYXQiOjE3ODQ5ODc0NTMsImV4cCI6MTc4NTU5MjI1M30.VrNy30dm1_jcKZwm79a9ouk4gU8PxSkVF2ila-oMpfo34QDhE6lxMKUqhDwC4-8YikdVbEYQ7tIq5Jw795iwfw\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987453279}',1,NULL,116,'2026-07-25 21:50:53'),(16,'签到管理','INSERT','com.education.store.module.attendance.controller.EduAttendanceController.batchSignin','POST','超级管理员','/api/v1/attendance/signin/batch','0:0:0:0:0:0:0:1','','{};{};[{\"studentId\":1,\"status\":0}];{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784987453753}',1,NULL,104,'2026-07-25 21:50:54'),(17,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6ImZiYjQyNzQ5LWVjZjQtNDUzNC1hNDljLTFhNDY4MDRmOTYwNCIsImlhdCI6MTc4NDk4Nzc0MywiZXhwIjoxNzg0OTk0OTQzfQ._E5ZEEOl5x-UGkVOvtmukmUNghMeZdMhWe8SQjb1tp8zcQxVZGrmm0fIHeCyZJVnQagMpAI_dfbe71nlu8uRIQ\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiIwYmFmNzk5Mi00M2Q5LTRmOGEtODZhMC0yNzI3ZDI3OWMzZmYiLCJpYXQiOjE3ODQ5ODc3NDMsImV4cCI6MTc4NTU5MjU0M30.4fNlHriqBBLmMKF9COjRU_2vUgixFbxKPFC0MY_nftqU3jVqxvOYuHJMo1j-scQ2eAkCxCUHK2VLRJ6mFbH0cA\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987743708}',1,NULL,772,'2026-07-25 21:55:43'),(18,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjhmOTU0YzVhLTQ0MWItNDM3YS1hODRiLTJjYWZkNzNjOWI5MyIsImlhdCI6MTc4NDk4Nzc1NSwiZXhwIjoxNzg0OTk0OTU1fQ.v05httEU48FSSdnzIFhUFSHDI0BUrEecRThWTFirgv1vnS-nGiGmD9AcluqTTH1l5rcvHe1eOI7UCWRG0fjlsg\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI0ZTk0NzAzZC0yZjA4LTQzYWMtODIxMS1hZWI2NDFhMDY3OGEiLCJpYXQiOjE3ODQ5ODc3NTUsImV4cCI6MTc4NTU5MjU1NX0.mrPiJXi6JjCO8jml0wLWIE2xAKhhOoKhCrZnk6ZDBObGB8BBQ7CKM5auuw0xg_MMVYT6CRY0mtc3_CweuUj5qw\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987755062}',1,NULL,120,'2026-07-25 21:55:55'),(19,'签到管理','INSERT','com.education.store.module.attendance.controller.EduAttendanceController.batchSignin','POST','超级管理员','/api/v1/attendance/signin/batch','0:0:0:0:0:0:0:1','','{};{};[{\"studentId\":1,\"status\":0}];{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784987755438}',1,NULL,77,'2026-07-25 21:55:55'),(20,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6Ijc4NzA2MDM0LWZhYjItNDNkNS05YWEzLTVhNWE0YmQyNmNhMiIsImlhdCI6MTc4NDk4Nzc5NSwiZXhwIjoxNzg0OTk0OTk1fQ.vPgxOZP8QM_vBjkt-J5GNmYIATzH1vf9gpFC3v3CPQQ2uUZHY-YX_nlmiAKVA1EtD7E2nHh-rI1PdauTYsw46A\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI0OTQxN2YxYi1hNTVkLTQzZWUtYjQ0OC00ZGFiYjM4Y2VmNjQiLCJpYXQiOjE3ODQ5ODc3OTUsImV4cCI6MTc4NTU5MjU5NX0.RXt8L7DNNQJaAS4FWKlk1IjaGuWZYLS6ThRra3M3u3OBr97fkBPnjQXRt3QTCXdh5sdXv4P1npr8b4LNJo2gxw\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987795643}',1,NULL,106,'2026-07-25 21:56:36'),(21,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjcwZTNjYWYzLTE1ZGYtNDM5ZC1iMGM3LTBhYmQ2YTZhZDIwNCIsImlhdCI6MTc4NDk4NzkzOCwiZXhwIjoxNzg0OTk1MTM4fQ.BAbgbLMVXgvEMNiYj_xtIxz1NeRbCrACsjgtZoDsa5zKyipZwXUtR977LVsanpX3kVeOarwYOCUsO-pBKdqlrg\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiJkMjc4ZWMzZi00YTc1LTRjYzctOGExNi1iZjBjMzdlN2IwZTgiLCJpYXQiOjE3ODQ5ODc5MzgsImV4cCI6MTc4NTU5MjczOH0.dyX0Kjubgqb2xj53v9TXQjpyW-iwjzlJR0md1Vg6_HoZrLMs8UdOiLYe1UfXJHGW0RKivsoZy5gFGPj_lOUt2g\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987938424}',1,NULL,159,'2026-07-25 21:58:58'),(22,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjZjOGQwOTA0LTAzOTUtNDVjZS1hMDU1LTBkOTM1YjRiZTZhMSIsImlhdCI6MTc4NDk4Nzk2OSwiZXhwIjoxNzg0OTk1MTY5fQ.aJ9Fm16uOTYYMXLa6EHxk7715SsVuAE4n8dQeaufTueHOceNnkGuLfq8JAwGCKgG8VhR2c819cJ8wOtFGjp23Q\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI0ZWVkNTJhYS00YWQ2LTQ0YTYtOTYyMC1lNTA1MWUxYTc3MGMiLCJpYXQiOjE3ODQ5ODc5NjksImV4cCI6MTc4NTU5Mjc2OX0.vSdQOJ_6p6FFu7mbN7abCpa-e3aZOIsrKRYO9LNUqC_ZZjSyS2PWLVvfJUkvAyyw8FkG5ypeMW7Bn4fr3cs1_g\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784987969159}',1,NULL,143,'2026-07-25 21:59:29'),(23,'签到管理','INSERT','com.education.store.module.attendance.controller.EduAttendanceController.batchSignin','POST','超级管理员','/api/v1/attendance/signin/batch','0:0:0:0:0:0:0:1','','{};{};[{\"studentId\":1,\"status\":0}];{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784987969592}',1,NULL,74,'2026-07-25 21:59:30'),(24,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjQyYTRiMjcyLTdhZWEtNDMzNC04NDFiLTcwNDhkZmI1ZjQ0YiIsImlhdCI6MTc4NDk4ODAwOCwiZXhwIjoxNzg0OTk1MjA4fQ.QyvB1k0kR4DNiyApI6-A1GDjX0vJnjF2mDzCMKkgvmOGUpRlJxmof5RwHvL4oqb8PMNkyoF6xsEst2FJcBBUfQ\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI0ZTI1N2QxMy00ZDAyLTRjMGItOGQxZC00OWZkOTg1OTFlN2UiLCJpYXQiOjE3ODQ5ODgwMDgsImV4cCI6MTc4NTU5MjgwOH0.Ravboe8gmmwZxlZCVj6AibrlBBHuFuh1R515nou0Qe2qtRBKOY0zGcjSWMb6cYMPwSUh-jxdL9M37dnkTdR7VQ\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784988008400}',1,NULL,109,'2026-07-25 22:00:08'),(25,'教师课酬','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.create','POST','超级管理员','/api/v1/payrolls','0:0:0:0:0:0:0:1','','{\"teacherId\":2,\"month\":\"2026-07\",\"courseId\":1,\"lessonCount\":1,\"status\":\"DRAFT\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'pay_per_lesson\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count,   status,     create_time, update_time )  VALUES (  ?, ?, ?, ?,   ?,     ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'pay_per_lesson\' doesn\'t have a default value\n; Field \'pay_per_lesson\' doesn\'t have a default value',116,'2026-07-25 22:00:09'),(26,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjlkMjNiZGYzLTg2MTEtNDQ1OC1iNmQ4LTQ1MmFlODliMjQ1OSIsImlhdCI6MTc4NDk4ODAyNSwiZXhwIjoxNzg0OTk1MjI1fQ.GWePdjwG2sRHImxcgiNUOcWn69-Ou1dDIO-srezyaINdMorYuJ7KizwtmEmdvdYkaMIKvPWHuhOTG9s2p_LoHg\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI5OGY3MzQwMy1mYjljLTQ0N2QtODY5Mi1kNGJkZDZmNmFiODQiLCJpYXQiOjE3ODQ5ODgwMjUsImV4cCI6MTc4NTU5MjgyNX0.dlmHwE00p9Xpl7589SlNcrLhs15Q8igwWKI6B3fvQmLEG0WdzzhmGlcanZ88gVesGMlgWEI_EKxYqgJAtOXS4A\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"system:log:query\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"system:log:delete\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784988025115}',1,NULL,99,'2026-07-25 22:00:25'),(27,'教师课酬','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.create','POST','超级管理员','/api/v1/payrolls','0:0:0:0:0:0:0:1','','{\"teacherId\":2,\"month\":\"2026-07\",\"courseId\":1,\"lessonCount\":1,\"status\":\"DRAFT\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'pay_per_lesson\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count,   status,     create_time, update_time )  VALUES (  ?, ?, ?, ?,   ?,     ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'pay_per_lesson\' doesn\'t have a default value\n; Field \'pay_per_lesson\' doesn\'t have a default value',7,'2026-07-25 22:00:25'),(28,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,541,'2026-07-25 22:05:13'),(29,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,111,'2026-07-25 22:05:34'),(30,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":0,\"timestamp\":1784988334281}',1,NULL,39,'2026-07-25 22:05:34'),(31,'签到管理','INSERT','com.education.store.module.attendance.controller.EduAttendanceController.batchSignin','POST','超级管理员','/api/v1/attendance/signin/batch','0:0:0:0:0:0:0:1','','{};{};[{\"studentId\":1,\"status\":0}];{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784988334431}',1,NULL,90,'2026-07-25 22:05:34'),(32,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":1,\"timestamp\":1784988337758}',1,NULL,27,'2026-07-25 22:05:38'),(33,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,97,'2026-07-25 22:08:35'),(34,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,90,'2026-07-25 22:10:05'),(35,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,105,'2026-07-25 22:11:15'),(36,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,151,'2026-07-25 22:14:16'),(37,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,118,'2026-07-25 22:20:34'),(38,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,94,'2026-07-25 22:31:05'),(39,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,74,'2026-07-25 22:35:48'),(40,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','127.0.0.1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":0,\"timestamp\":1784991630372}',1,NULL,33,'2026-07-25 23:00:30'),(41,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','127.0.0.1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":0,\"timestamp\":1784991802372}',1,NULL,7,'2026-07-25 23:03:22'),(42,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,424,'2026-07-25 23:23:11'),(43,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,0,'用户名或密码错误',282,'2026-07-25 23:55:07'),(44,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,144,'2026-07-25 23:56:03'),(45,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":1,\"feeType\":\"TUITION\",\"amountReceivable\":2000,\"amountPaid\":-100,\"paymentMethod\":\"CASH\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'pay_time\' in \'field list\'\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no,  student_id,   fee_type, amount_receivable, amount_paid, payment_method, status, pay_time,    create_time, update_time )  VALUES (  ?,  ?,   ?, ?, ?, ?, ?, ?,    ?, ?  )\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'pay_time\' in \'field list\'\n; bad SQL grammar []',98,'2026-07-25 23:57:23'),(46,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":1,\"amountReceivable\":2000,\"amountPaid\":-100};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'pay_time\' in \'field list\'\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no,  student_id,    amount_receivable, amount_paid,  status, pay_time,    create_time, update_time )  VALUES (  ?,  ?,    ?, ?,  ?, ?,    ?, ?  )\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'pay_time\' in \'field list\'\n; bad SQL grammar []',6,'2026-07-25 23:59:18'),(47,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,363,'2026-07-26 00:02:53'),(48,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":1,\"feeType\":\"TUITION\",\"amountReceivable\":2000,\"amountPaid\":-100};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'pay_time\' in \'field list\'\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no,  student_id,   fee_type, amount_receivable, amount_paid,  status, pay_time,    create_time, update_time )  VALUES (  ?,  ?,   ?, ?, ?,  ?, ?,    ?, ?  )\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'pay_time\' in \'field list\'\n; bad SQL grammar []',118,'2026-07-26 00:02:53'),(49,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,366,'2026-07-26 00:08:15'),(50,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,80,'2026-07-26 00:08:44'),(51,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,78,'2026-07-26 00:09:58'),(52,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,76,'2026-07-26 00:10:25'),(53,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,79,'2026-07-26 00:11:32'),(54,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,76,'2026-07-26 00:12:04'),(55,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,361,'2026-07-26 00:16:09'),(56,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,79,'2026-07-26 00:21:09'),(57,'科目管理','INSERT','com.education.store.module.course.controller.EduSubjectController.create','POST','超级管理员','/api/v1/subjects','0:0:0:0:0:0:0:1','','{\"name\":\"高中语文\",\"sortOrder\":2,\"status\":1,\"description\":\"高中语文科目\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996469025}',1,NULL,46,'2026-07-26 00:21:09'),(58,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"高一数学基础班\",\"code\":\"MATH-G1-001\",\"subjectId\":1,\"courseType\":\"班课\",\"price\":120,\"applicableGrade\":\"高一\",\"status\":1,\"sortOrder\":3,\"description\":\"高一数学基础班课\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996469050}',1,NULL,12,'2026-07-26 00:21:09'),(59,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"张明华\",\"status\":\"ACTIVE\",\"subjects\":\"数学\",\"hireDate\":1736870400000,\"defaultPayPerLesson\":100,\"phone\":\"13800138001\",\"remark\":\"高级数学教师\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784996469068}',1,NULL,8,'2026-07-26 00:21:09'),(60,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"王建国\",\"status\":\"ACTIVE\",\"subjects\":\"数学\",\"hireDate\":1748707200000,\"defaultPayPerLesson\":90,\"phone\":\"13700137003\",\"remark\":\"数学骨干教师\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784996469084}',1,NULL,6,'2026-07-26 00:21:09'),(61,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"赵小明\",\"gender\":\"男\",\"grade\":\"高三\",\"school\":\"第一中学\",\"mobile\":\"13600136001\",\"parentMobile\":\"13500135001\",\"source\":\"地推\",\"remark\":\"数学成绩110分，想冲刺130\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'parent_name\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,  grade, school, mobile,   parent_mobile,    source,  follow_status, trial_lesson_count, last_followup_date,  remark,    create_time, update_time )  VALUES (  ?, ?,  ?, ?, ?,   ?,    ?,  ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'parent_name\' doesn\'t have a default value\n; Field \'parent_name\' doesn\'t have a default value',94,'2026-07-26 00:21:09'),(62,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"孙小红\",\"gender\":\"女\",\"grade\":\"高二\",\"school\":\"第二中学\",\"mobile\":\"13600136002\",\"parentMobile\":\"13500135002\",\"source\":\"转介绍\",\"remark\":\"语文基础薄弱，需要加强\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'parent_name\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,  grade, school, mobile,   parent_mobile,    source,  follow_status, trial_lesson_count, last_followup_date,  remark,    create_time, update_time )  VALUES (  ?, ?,  ?, ?, ?,   ?,    ?,  ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'parent_name\' doesn\'t have a default value\n; Field \'parent_name\' doesn\'t have a default value',7,'2026-07-26 00:21:09'),(63,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"周小华\",\"gender\":\"男\",\"grade\":\"高一\",\"school\":\"第三中学\",\"mobile\":\"13600136003\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996469248}',1,NULL,9,'2026-07-26 00:21:09'),(64,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"陈小美\",\"gender\":\"女\",\"grade\":\"高三\",\"school\":\"第一中学\",\"mobile\":\"13600136004\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996469270}',1,NULL,11,'2026-07-26 00:21:09'),(65,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,75,'2026-07-26 00:23:34'),(66,'科目管理','INSERT','com.education.store.module.course.controller.EduSubjectController.create','POST','超级管理员','/api/v1/subjects','0:0:0:0:0:0:0:1','','{\"name\":\"高中英语\",\"sortOrder\":3,\"status\":1,\"description\":\"高中英语科目\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996614040}',1,NULL,21,'2026-07-26 00:23:34'),(67,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"高一英语基础班\",\"code\":\"ENG-G1-BASIC\",\"subjectId\":4,\"courseType\":\"班课\",\"price\":120,\"applicableGrade\":\"高一\",\"status\":1,\"sortOrder\":2,\"description\":\"高一英语基础班课，15人小班\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996614064}',1,NULL,8,'2026-07-26 00:23:34'),(68,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"高二数学提高班\",\"code\":\"MATH-G2-IMPROVE\",\"subjectId\":1,\"courseType\":\"班课\",\"price\":150,\"applicableGrade\":\"高二\",\"status\":1,\"sortOrder\":3,\"description\":\"高二数学提高班课\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996614079}',1,NULL,9,'2026-07-26 00:23:34'),(69,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"李文雅\",\"status\":\"ACTIVE\",\"subjects\":\"英语\",\"hireDate\":1738339200000,\"defaultPayPerLesson\":100,\"phone\":\"13900139002\",\"remark\":\"资深英语教师，英语专八\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784996614100}',1,NULL,6,'2026-07-26 00:23:34'),(70,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"王建国\",\"status\":\"ACTIVE\",\"subjects\":\"数学\",\"hireDate\":1748707200000,\"defaultPayPerLesson\":90,\"phone\":\"13700137003\",\"remark\":\"数学骨干教师\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784996614111}',1,NULL,6,'2026-07-26 00:23:34'),(71,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"赵小明\",\"gender\":\"M\",\"grade\":\"高三\",\"school\":\"市第一中学\",\"mobile\":\"13611110001\",\"parentName\":\"赵先生\",\"parentRelation\":\"FATHER\",\"parentMobile\":\"13511110001\",\"intendedCourseId\":1,\"interestLevel\":\"HIGH\",\"source\":\"REFERRAL\",\"followStatus\":\"NEW\",\"remark\":\"目前数学105分，目标130+，朋友介绍来的\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996614132}',1,NULL,8,'2026-07-26 00:23:34'),(72,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"孙小红\",\"gender\":\"F\",\"grade\":\"高一\",\"school\":\"市第二中学\",\"mobile\":\"13611110002\",\"parentName\":\"孙女士\",\"parentRelation\":\"MOTHER\",\"parentMobile\":\"13511110002\",\"intendedCourseId\":3,\"interestLevel\":\"MEDIUM\",\"source\":\"DIRECT_MARKETING\",\"followStatus\":\"NEW\",\"remark\":\"英语基础薄弱，尤其语法和听力\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996614145}',1,NULL,8,'2026-07-26 00:23:34'),(73,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"周强\",\"gender\":\"M\",\"grade\":\"高二\",\"school\":\"实验中学\",\"mobile\":\"13611110003\",\"parentName\":\"周先生\",\"parentRelation\":\"FATHER\",\"parentMobile\":\"13511110003\",\"intendedCourseId\":4,\"interestLevel\":\"HIGH\",\"source\":\"ONLINE\",\"followStatus\":\"CONTACTED\",\"remark\":\"网上咨询，已电话沟通，本周六试听\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996614159}',1,NULL,8,'2026-07-26 00:23:34'),(74,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/1/followups','0:0:0:0:0:0:0:1','','{};{\"content\":\"电话沟通：家长反映孩子数学偏科，周末有空来试听\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'method\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectFollowupMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectFollowupMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect_followup  ( prospect_id,  content,      create_time, update_time )  VALUES (  ?,  ?,      ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'method\' doesn\'t have a default value\n; Field \'method\' doesn\'t have a default value',7,'2026-07-26 00:23:34'),(75,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/3/followups','0:0:0:0:0:0:0:1','','{};{\"content\":\"电话沟通：已预约本周六上午10点试听课\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'method\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectFollowupMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectFollowupMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect_followup  ( prospect_id,  content,      create_time, update_time )  VALUES (  ?,  ?,      ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'method\' doesn\'t have a default value\n; Field \'method\' doesn\'t have a default value',5,'2026-07-26 00:23:34'),(76,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/3/convert','0:0:0:0:0:0:0:1','','{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":4},\"timestamp\":1784996614249}',1,NULL,23,'2026-07-26 00:23:34'),(77,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"陈小美\",\"gender\":\"F\",\"grade\":\"高三\",\"school\":\"市第一中学\",\"mobile\":\"13622220001\",\"parentName\":\"陈女士\",\"parentRelation\":\"MOTHER\",\"source\":\"FRIEND\",\"remark\":\"学霸型学员\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996614262}',1,NULL,7,'2026-07-26 00:23:34'),(78,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"林小军\",\"gender\":\"M\",\"grade\":\"高一\",\"school\":\"市第三中学\",\"mobile\":\"13622220002\",\"parentName\":\"林先生\",\"parentRelation\":\"FATHER\",\"source\":\"DIRECT_MARKETING\",\"remark\":\"英语入门学员\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996614275}',1,NULL,8,'2026-07-26 00:23:34'),(79,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":5,\"courseId\":1,\"amount\":9000,\"enrollmentDate\":1784476800000,\"remark\":\"报名30课时一对一，300元/课时，全款报名赠送2课时\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'handler_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/enrollment/mapper/EduEnrollmentMapper.java (best guess)\r\n### The error may involve com.education.store.module.enrollment.mapper.EduEnrollmentMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_enrollment  ( student_id, course_id,  amount,  enrollment_date, payment_status,  remark, create_by,  create_time, update_time )  VALUES (  ?, ?,  ?,  ?, ?,  ?, ?,  ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'handler_id\' doesn\'t have a default value\n; Field \'handler_id\' doesn\'t have a default value',9,'2026-07-26 00:23:34'),(80,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":6,\"courseId\":3,\"amount\":2400,\"enrollmentDate\":1784649600000,\"remark\":\"报名20课时班课，120元/课时\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'handler_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/enrollment/mapper/EduEnrollmentMapper.java (best guess)\r\n### The error may involve com.education.store.module.enrollment.mapper.EduEnrollmentMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_enrollment  ( student_id, course_id,  amount,  enrollment_date, payment_status,  remark, create_by,  create_time, update_time )  VALUES (  ?, ?,  ?,  ?, ?,  ?, ?,  ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'handler_id\' doesn\'t have a default value\n; Field \'handler_id\' doesn\'t have a default value',8,'2026-07-26 00:23:34'),(81,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 00:24:38'),(82,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,76,'2026-07-26 00:24:56'),(83,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,75,'2026-07-26 00:25:26'),(84,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":4,\"courseId\":1,\"amount\":9000,\"enrollmentDate\":1784476800000};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'handler_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/enrollment/mapper/EduEnrollmentMapper.java (best guess)\r\n### The error may involve com.education.store.module.enrollment.mapper.EduEnrollmentMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_enrollment  ( student_id, course_id,  amount,  enrollment_date, payment_status,   create_by,  create_time, update_time )  VALUES (  ?, ?,  ?,  ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'handler_id\' doesn\'t have a default value\n; Field \'handler_id\' doesn\'t have a default value',6,'2026-07-26 00:25:26'),(85,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,73,'2026-07-26 00:27:46'),(86,'科目管理','INSERT','com.education.store.module.course.controller.EduSubjectController.create','POST','超级管理员','/api/v1/subjects','0:0:0:0:0:0:0:1','','{\"name\":\"高中数学\",\"sortOrder\":1,\"status\":1};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866138}',1,NULL,35,'2026-07-26 00:27:46'),(87,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"高三数学冲刺班\",\"code\":\"MATH-G3-SPRINT\",\"subjectId\":5,\"courseType\":\"一对一\",\"price\":300,\"applicableGrade\":\"高三\",\"status\":1,\"sortOrder\":1,\"description\":\"高三数学一对一冲刺，目标130+\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866169}',1,NULL,7,'2026-07-26 00:27:46'),(88,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"张明华\",\"status\":\"ACTIVE\",\"subjects\":\"数学\",\"hireDate\":1725120000000,\"defaultPayPerLesson\":120,\"phone\":\"13811110001\",\"remark\":\"高级教师10年教龄\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784996866194}',1,NULL,4,'2026-07-26 00:27:46'),(89,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"李文雅\",\"status\":\"ACTIVE\",\"subjects\":\"英语\",\"hireDate\":1738339200000,\"defaultPayPerLesson\":100,\"phone\":\"13911110002\",\"remark\":\"英语专八\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784996866203}',1,NULL,4,'2026-07-26 00:27:46'),(90,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"赵小明\",\"gender\":\"M\",\"grade\":\"高三\",\"school\":\"市第一中学\",\"mobile\":\"13633330001\",\"parentName\":\"赵先生\",\"parentRelation\":\"FATHER\",\"parentMobile\":\"13533330001\",\"intendedCourseId\":5,\"interestLevel\":\"HIGH\",\"source\":\"REFERRAL\",\"followStatus\":\"NEW\",\"remark\":\"数学105分，目标130+，朋友介绍\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866225}',1,NULL,5,'2026-07-26 00:27:46'),(91,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"孙小红\",\"gender\":\"F\",\"grade\":\"高一\",\"school\":\"市第二中学\",\"mobile\":\"13633330002\",\"parentName\":\"孙女士\",\"parentRelation\":\"MOTHER\",\"parentMobile\":\"13533330002\",\"intendedCourseId\":3,\"interestLevel\":\"MEDIUM\",\"source\":\"DIRECT_MARKETING\",\"followStatus\":\"NEW\",\"remark\":\"英语基础薄弱\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866236}',1,NULL,6,'2026-07-26 00:27:46'),(92,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"周强\",\"gender\":\"M\",\"grade\":\"高二\",\"school\":\"实验中学\",\"mobile\":\"13633330003\",\"parentName\":\"周先生\",\"parentRelation\":\"FATHER\",\"parentMobile\":\"13533330003\",\"intendedCourseId\":4,\"interestLevel\":\"HIGH\",\"source\":\"ONLINE\",\"followStatus\":\"CONTACTED\",\"remark\":\"网上咨询，已约试听\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866246}',1,NULL,5,'2026-07-26 00:27:46'),(93,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/6/convert','0:0:0:0:0:0:0:1','','{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":7},\"timestamp\":1784996866292}',1,NULL,13,'2026-07-26 00:27:46'),(94,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"陈小美\",\"gender\":\"F\",\"grade\":\"高三\",\"school\":\"市第一中学\",\"mobile\":\"13644440001\",\"parentName\":\"陈女士\",\"parentRelation\":\"MOTHER\",\"source\":\"FRIEND\",\"remark\":\"学霸型学员\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866316}',1,NULL,7,'2026-07-26 00:27:46'),(95,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"林小军\",\"gender\":\"M\",\"grade\":\"高一\",\"school\":\"市第三中学\",\"mobile\":\"13644440002\",\"parentName\":\"林先生\",\"parentRelation\":\"FATHER\",\"source\":\"DIRECT_MARKETING\",\"remark\":\"英语入门\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866335}',1,NULL,8,'2026-07-26 00:27:46'),(96,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":8,\"courseId\":5,\"amount\":9000,\"handlerId\":1,\"enrollmentDate\":1784476800000,\"remark\":\"报名30课时一对一，全款赠送2课时\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866363}',1,NULL,7,'2026-07-26 00:27:46'),(97,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":9,\"courseId\":3,\"amount\":2400,\"handlerId\":1,\"enrollmentDate\":1784649600000,\"remark\":\"报名20课时班课\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1784996866375}',1,NULL,7,'2026-07-26 00:27:46'),(98,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":8,\"enrollmentId\":1,\"courseId\":5,\"feeType\":\"学费\",\"amountReceivable\":9000,\"amountPaid\":9000,\"paymentMethod\":\"微信\",\"remark\":\"全款缴纳\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no, enrollment_id, student_id, course_id,  fee_type, amount_receivable, amount_paid, payment_method, status, remark,   create_time, update_time )  VALUES (  ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\n; Field \'operator_id\' doesn\'t have a default value',6,'2026-07-26 00:27:46'),(99,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":9,\"enrollmentId\":2,\"courseId\":3,\"feeType\":\"学费\",\"amountReceivable\":2400,\"amountPaid\":1200,\"paymentMethod\":\"支付宝\",\"remark\":\"首付1200，剩余下月缴清\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no, enrollment_id, student_id, course_id,  fee_type, amount_receivable, amount_paid, payment_method, status, remark,   create_time, update_time )  VALUES (  ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\n; Field \'operator_id\' doesn\'t have a default value',4,'2026-07-26 00:27:46'),(100,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,71,'2026-07-26 00:28:09'),(101,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/4/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"测试跟进内容\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'followup_date\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectFollowupMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectFollowupMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect_followup  ( prospect_id, method, content,      create_time, update_time )  VALUES (  ?, ?, ?,      ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'followup_date\' doesn\'t have a default value\n; Field \'followup_date\' doesn\'t have a default value',4,'2026-07-26 00:28:09'),(102,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":8,\"enrollmentId\":1,\"courseId\":5,\"feeType\":\"学费\",\"amountReceivable\":9000,\"amountPaid\":9000,\"paymentMethod\":\"微信\",\"remark\":\"测试收费\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no, enrollment_id, student_id, course_id,  fee_type, amount_receivable, amount_paid, payment_method, status, remark,   create_time, update_time )  VALUES (  ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\n; Field \'operator_id\' doesn\'t have a default value',3,'2026-07-26 00:28:09'),(103,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,72,'2026-07-26 00:32:10'),(104,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"张明华\",\"status\":\"ACTIVE\",\"subjects\":\"数学\",\"hireDate\":1725120000000,\"defaultPayPerLesson\":120,\"phone\":\"13811110001\",\"remark\":\"高级教师10年教龄\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784997129709}',1,NULL,5,'2026-07-26 00:32:10'),(105,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"李文雅\",\"status\":\"ACTIVE\",\"subjects\":\"英语\",\"hireDate\":1738339200000,\"defaultPayPerLesson\":100,\"phone\":\"13911110002\",\"remark\":\"英语专八\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784997129719}',1,NULL,4,'2026-07-26 00:32:10'),(106,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/6/convert','0:0:0:0:0:0:0:1','','{};',NULL,0,'已转化/已流失的意向学员不可再次转化',3,'2026-07-26 00:32:10'),(107,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":8,\"enrollmentId\":1,\"courseId\":5,\"feeType\":\"TUITION\",\"amountReceivable\":9000,\"amountPaid\":9000,\"paymentMethod\":\"微信\",\"remark\":\"全款缴纳\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no, enrollment_id, student_id, course_id,  fee_type, amount_receivable, amount_paid, payment_method, status, remark,   create_time, update_time )  VALUES (  ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\n; Field \'operator_id\' doesn\'t have a default value',4,'2026-07-26 00:32:10'),(108,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":9,\"enrollmentId\":2,\"courseId\":3,\"feeType\":\"TUITION\",\"amountReceivable\":2400,\"amountPaid\":1200,\"paymentMethod\":\"支付宝\",\"remark\":\"首付1200，剩余下月缴清\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no, enrollment_id, student_id, course_id,  fee_type, amount_receivable, amount_paid, payment_method, status, remark,   create_time, update_time )  VALUES (  ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\n; Field \'operator_id\' doesn\'t have a default value',3,'2026-07-26 00:32:10'),(109,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,384,'2026-07-26 00:36:38'),(110,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"张明华\",\"status\":\"ACTIVE\",\"subjects\":\"数学\",\"hireDate\":1725120000000,\"defaultPayPerLesson\":120,\"phone\":\"13811110001\",\"remark\":\"高级教师10年教龄\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784997398620}',1,NULL,42,'2026-07-26 00:36:39'),(111,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"李文雅\",\"status\":\"ACTIVE\",\"subjects\":\"英语\",\"hireDate\":1738339200000,\"defaultPayPerLesson\":100,\"phone\":\"13911110002\",\"remark\":\"英语专八\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1784997398635}',1,NULL,5,'2026-07-26 00:36:39'),(112,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/4/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"电话沟通：家长反映数学偏科，周末来试听\",\"nextPlan\":\"2026-07-26前完成试听安排\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'followup_date\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectFollowupMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectFollowupMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect_followup  ( prospect_id, method, content,  next_plan,    create_time, update_time )  VALUES (  ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'followup_date\' doesn\'t have a default value\n; Field \'followup_date\' doesn\'t have a default value',107,'2026-07-26 00:36:39'),(113,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/6/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"电话沟通：预约本周六上午10点试听\",\"nextPlan\":\"2026-07-27回访试听结果\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'followup_date\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectFollowupMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectFollowupMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect_followup  ( prospect_id, method, content,  next_plan,    create_time, update_time )  VALUES (  ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'followup_date\' doesn\'t have a default value\n; Field \'followup_date\' doesn\'t have a default value',7,'2026-07-26 00:36:39'),(114,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/6/convert','0:0:0:0:0:0:0:1','','{};',NULL,0,'已转化/已流失的意向学员不可再次转化',4,'2026-07-26 00:36:39'),(115,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":8,\"enrollmentId\":1,\"courseId\":5,\"feeType\":\"TUITION\",\"amountReceivable\":9000,\"amountPaid\":9000,\"paymentMethod\":\"微信\",\"remark\":\"全款缴纳\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no, enrollment_id, student_id, course_id,  fee_type, amount_receivable, amount_paid, payment_method, status, remark,   create_time, update_time )  VALUES (  ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\n; Field \'operator_id\' doesn\'t have a default value',7,'2026-07-26 00:36:39'),(116,'收费管理','INSERT','com.education.store.module.tuition.controller.EduTuitionFeeController.create','POST','超级管理员','/api/v1/tuition/fees','0:0:0:0:0:0:0:1','','{\"studentId\":9,\"enrollmentId\":2,\"courseId\":3,\"feeType\":\"TUITION\",\"amountReceivable\":2400,\"amountPaid\":1200,\"paymentMethod\":\"支付宝\",\"remark\":\"首付1200，剩余下月缴清\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/tuition/mapper/EduTuitionFeeMapper.java (best guess)\r\n### The error may involve com.education.store.module.tuition.mapper.EduTuitionFeeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_tuition_fee  ( fee_no, enrollment_id, student_id, course_id,  fee_type, amount_receivable, amount_paid, payment_method, status, remark,   create_time, update_time )  VALUES (  ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'operator_id\' doesn\'t have a default value\n; Field \'operator_id\' doesn\'t have a default value',4,'2026-07-26 00:36:39'),(117,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"MSEdge\",\"os\":\"Windows\"}',NULL,1,NULL,112,'2026-07-26 00:40:28'),(118,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 00:47:47'),(119,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,107,'2026-07-26 01:02:46'),(120,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,75,'2026-07-26 01:02:53'),(121,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,77,'2026-07-26 01:03:01'),(122,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,75,'2026-07-26 01:03:12'),(123,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":1,\"timestamp\":1784998992043}',1,NULL,18,'2026-07-26 01:03:12'),(124,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Unknown\",\"os\":\"Windows\"}',NULL,1,NULL,375,'2026-07-26 01:06:30'),(125,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1784999190542}',1,NULL,33,'2026-07-26 01:06:31'),(126,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":0,\"timestamp\":1784999190615}',1,NULL,7,'2026-07-26 01:06:31'),(127,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,83,'2026-07-26 01:06:59'),(128,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,389,'2026-07-26 01:09:31'),(129,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,95,'2026-07-26 01:10:11'),(130,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,382,'2026-07-26 01:13:42'),(131,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,377,'2026-07-26 01:17:38'),(132,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,83,'2026-07-26 01:18:26'),(133,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,82,'2026-07-26 01:19:06'),(134,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1784999945688}',1,NULL,38,'2026-07-26 01:19:06'),(135,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1784999945747}',1,NULL,16,'2026-07-26 01:19:06'),(136,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,78,'2026-07-26 01:23:52'),(137,'科目管理','INSERT','com.education.store.module.course.controller.EduSubjectController.create','POST','超级管理员','/api/v1/subjects','0:0:0:0:0:0:0:1','','{\"name\":\"美术-1785000232395\",\"sortOrder\":1,\"status\":1,\"description\":\"美术学科\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000232421}',1,NULL,12,'2026-07-26 01:23:52'),(138,'教室管理','INSERT','com.education.store.module.course.controller.EduClassroomController.create','POST','超级管理员','/api/v1/classrooms','0:0:0:0:0:0:0:1','','{\"name\":\"美术教室A-2437\",\"capacity\":30,\"location\":\"1楼101室\",\"status\":1};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000232454}',1,NULL,9,'2026-07-26 01:23:52'),(139,'课酬单价','DELETE','com.education.store.module.payroll.controller.EduTeacherPayRateController.delete','DELETE','超级管理员','/api/v1/pay-rates/1','0:0:0:0:0:0:0:1','','[1];','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000232548}',1,NULL,13,'2026-07-26 01:23:53'),(140,'课酬单价','DELETE','com.education.store.module.payroll.controller.EduTeacherPayRateController.delete','DELETE','超级管理员','/api/v1/pay-rates/2','0:0:0:0:0:0:0:1','','[2];','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000232561}',1,NULL,6,'2026-07-26 01:23:53'),(141,'教师课酬','DELETE','com.education.store.module.payroll.controller.EduTeacherPayrollController.delete','DELETE','超级管理员','/api/v1/payrolls/4','0:0:0:0:0:0:0:1','','[4];','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000232790}',1,NULL,8,'2026-07-26 01:23:53'),(142,'教师课酬','DELETE','com.education.store.module.payroll.controller.EduTeacherPayrollController.delete','DELETE','超级管理员','/api/v1/payrolls/3','0:0:0:0:0:0:0:1','','[3];','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000232804}',1,NULL,9,'2026-07-26 01:23:53'),(143,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',91,'2026-07-26 01:23:53'),(144,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',8,'2026-07-26 01:23:53'),(145,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',69,'2026-07-26 01:23:53'),(146,'用户登录','LOGIN','','POST','teacher02','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher02\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',79,'2026-07-26 01:23:53'),(147,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,84,'2026-07-26 01:36:20'),(148,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',67,'2026-07-26 01:36:21'),(149,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',70,'2026-07-26 01:36:21'),(150,'用户登录','LOGIN','','POST','teacher02','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher02\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',68,'2026-07-26 01:36:21'),(151,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"测试课程-80841\",\"code\":\"COURSE80841\",\"subjectId\":1,\"courseType\":\"GROUP\",\"price\":2400,\"status\":1,\"description\":\"验证用课程\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000980957}',1,NULL,89,'2026-07-26 01:36:21'),(152,'教室管理','INSERT','com.education.store.module.course.controller.EduClassroomController.create','POST','超级管理员','/api/v1/classrooms','0:0:0:0:0:0:0:1','','{\"name\":\"测试教室-80841\",\"capacity\":25,\"location\":\"2楼201\",\"status\":1};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000981032}',1,NULL,42,'2026-07-26 01:36:21'),(153,'班级管理','INSERT','com.education.store.module.classinfo.controller.EduClassController.create','POST','超级管理员','/api/v1/classes','0:0:0:0:0:0:0:1','','{\"name\":\"测试班级-80841\",\"courseId\":9,\"teacherId\":2,\"classroomId\":3,\"maxStudents\":10,\"status\":\"1\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785000981079}',1,NULL,9,'2026-07-26 01:36:21'),(154,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"测80841\",\"gender\":\"M\",\"mobile\":\"13926261505\",\"parentName\":\"测爸\",\"parentMobile\":\"13811960565\",\"intendedCourseId\":9,\"interestLevel\":\"HIGH\",\"source\":\"REFERRAL\",\"remark\":\"验证用意向学员\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'parent_relation\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,    mobile, parent_name,  parent_mobile,  intended_course_id, interest_level, source,  follow_status, trial_lesson_count, last_followup_date,  remark,    create_time, update_time )  VALUES (  ?, ?,    ?, ?,  ?,  ?, ?, ?,  ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'parent_relation\' doesn\'t have a default value\n; Field \'parent_relation\' doesn\'t have a default value',98,'2026-07-26 01:36:21'),(155,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"测生80841\",\"gender\":\"1\",\"grade\":\"四年级\",\"school\":\"测试小学\",\"mobile\":\"13716152324\",\"parentName\":\"测妈\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000981301}',1,NULL,10,'2026-07-26 01:36:21'),(156,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":10,\"courseId\":9,\"classId\":2,\"amount\":2400,\"handlerId\":1,\"enrollmentDate\":1784908800000,\"remark\":\"验证报名\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000981400}',1,NULL,75,'2026-07-26 01:36:21'),(157,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',11,'2026-07-26 01:36:21'),(158,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',10,'2026-07-26 01:36:21'),(159,'积分账户管理','UPDATE','com.education.store.module.points.controller.PointsAccountController.adjust','POST','超级管理员','/api/v1/points/account/admin/adjust','0:0:0:0:0:0:0:1','','{};{};全勤奖励;','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"id\":2,\"studentId\":7,\"totalEarned\":500,\"totalSpent\":0,\"balance\":500,\"createTime\":1785000982000,\"updateTime\":1785000982000},\"timestamp\":1785000981648}',1,NULL,70,'2026-07-26 01:36:22'),(160,'积分礼品管理','INSERT','com.education.store.module.points.controller.PointsGiftController.add','POST','超级管理员','/api/v1/points/gift','0:0:0:0:0:0:0:1','','{\"name\":\"测试礼品-80841\",\"imageUrl\":\"https://via.placeholder.com/100\",\"pointsRequired\":150,\"stockTotal\":30,\"status\":1,\"description\":\"验证用礼品\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"name\":\"测试礼品-80841\",\"imageUrl\":\"https://via.placeholder.com/100\",\"pointsRequired\":150,\"stockTotal\":30,\"stockRemaining\":30,\"status\":1,\"description\":\"验证用礼品\",\"id\":1,\"createTime\":1785000981676,\"updateTime\":1785000981676},\"timestamp\":1785000981680}',1,NULL,9,'2026-07-26 01:36:22'),(161,'兑换记录管理','INSERT','com.education.store.module.points.controller.PointsRedemptionController.redeem','POST','超级管理员','/api/v1/points/redemption','0:0:0:0:0:0:0:1','','{};{};{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":7,\"giftId\":1,\"pointsSpent\":150,\"status\":\"PENDING_CLAIM\",\"redeemTime\":1785000981711,\"operatorId\":1,\"id\":1,\"createTime\":1785000981711,\"updateTime\":1785000981711},\"timestamp\":1785000981717}',1,NULL,16,'2026-07-26 01:36:22'),(162,'兑换记录管理','UPDATE','com.education.store.module.points.controller.PointsRedemptionController.claim','POST','超级管理员','/api/v1/points/redemption/1/claim','0:0:0:0:0:0:0:1','','{};{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785000981748}',1,NULL,14,'2026-07-26 01:36:22'),(163,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',7,'2026-07-26 01:36:22'),(164,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 01:37:32'),(165,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"测试\",\"gender\":\"M\",\"mobile\":\"13585831485\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'parent_name\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,    mobile,         follow_status, trial_lesson_count, last_followup_date,      create_time, update_time )  VALUES (  ?, ?,    ?,         ?, ?, ?,      ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'parent_name\' doesn\'t have a default value\n; Field \'parent_name\' doesn\'t have a default value',7,'2026-07-26 01:37:32'),(166,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',7,'2026-07-26 01:37:32'),(167,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,373,'2026-07-26 01:40:06'),(168,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"测试\",\"gender\":\"M\",\"mobile\":\"13527447599\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'parent_name\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,    mobile,         follow_status, trial_lesson_count, last_followup_date,      create_time, update_time )  VALUES (  ?, ?,    ?,         ?, ?, ?,      ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'parent_name\' doesn\'t have a default value\n; Field \'parent_name\' doesn\'t have a default value',121,'2026-07-26 01:40:07'),(169,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',16,'2026-07-26 01:40:07'),(170,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,84,'2026-07-26 01:43:07'),(171,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"测试\",\"gender\":\"M\",\"mobile\":\"13594060674\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001386589}',1,NULL,13,'2026-07-26 01:43:07'),(172,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785001386625}',1,NULL,22,'2026-07-26 01:43:07'),(173,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,78,'2026-07-26 01:43:22'),(174,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',70,'2026-07-26 01:43:22'),(175,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',72,'2026-07-26 01:43:22'),(176,'用户登录','LOGIN','','POST','teacher02','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher02\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',67,'2026-07-26 01:43:22'),(177,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"测试课程-02281\",\"code\":\"COURSE02281\",\"subjectId\":1,\"courseType\":\"GROUP\",\"price\":2400,\"status\":1,\"description\":\"验证用课程\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001402306}',1,NULL,12,'2026-07-26 01:43:22'),(178,'教室管理','INSERT','com.education.store.module.course.controller.EduClassroomController.create','POST','超级管理员','/api/v1/classrooms','0:0:0:0:0:0:0:1','','{\"name\":\"测试教室-02281\",\"capacity\":25,\"location\":\"2楼201\",\"status\":1};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001402349}',1,NULL,10,'2026-07-26 01:43:22'),(179,'班级管理','INSERT','com.education.store.module.classinfo.controller.EduClassController.create','POST','超级管理员','/api/v1/classes','0:0:0:0:0:0:0:1','','{\"name\":\"测试班级-02281\",\"courseId\":10,\"teacherId\":2,\"classroomId\":4,\"maxStudents\":10,\"status\":\"1\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":3,\"timestamp\":1785001402395}',1,NULL,8,'2026-07-26 01:43:22'),(180,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"测02281\",\"gender\":\"M\",\"mobile\":\"13957255979\",\"parentName\":\"测爸\",\"parentMobile\":\"13819233060\",\"intendedCourseId\":10,\"interestLevel\":\"HIGH\",\"source\":\"REFERRAL\",\"remark\":\"验证用意向学员\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001402451}',1,NULL,9,'2026-07-26 01:43:22'),(181,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/8/followups','0:0:0:0:0:0:0:1','','{};{\"content\":\"电话沟通，家长很感兴趣，约了周末试听\"};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'method\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectFollowupMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectFollowupMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect_followup  ( prospect_id,  content, followup_date,  operator_id,   create_time, update_time )  VALUES (  ?,  ?, ?,  ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'method\' doesn\'t have a default value\n; Field \'method\' doesn\'t have a default value',7,'2026-07-26 01:43:22'),(182,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/8/convert','0:0:0:0:0:0:0:1','','{};',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'relation\' doesn\'t have a default value\r\n### The error may exist in com/education/store/module/student/mapper/EduStudentParentMapper.java (best guess)\r\n### The error may involve com.education.store.module.student.mapper.EduStudentParentMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_student_parent  ( student_id,  name,  mobile, sort,   create_time, update_time )  VALUES (  ?,  ?,  ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'relation\' doesn\'t have a default value\n; Field \'relation\' doesn\'t have a default value',12,'2026-07-26 01:43:23'),(183,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"测生02281\",\"gender\":\"1\",\"grade\":\"四年级\",\"school\":\"测试小学\",\"mobile\":\"13739787999\",\"parentName\":\"测妈\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001402559}',1,NULL,8,'2026-07-26 01:43:23'),(184,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":12,\"courseId\":10,\"classId\":3,\"amount\":2400,\"handlerId\":1,\"enrollmentDate\":1784908800000,\"remark\":\"验证报名\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001402602}',1,NULL,18,'2026-07-26 01:43:23'),(185,'教师课酬','DELETE','com.education.store.module.payroll.controller.EduTeacherPayrollController.delete','DELETE','超级管理员','/api/v1/payrolls/13','0:0:0:0:0:0:0:1','','[13];','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001402667}',1,NULL,26,'2026-07-26 01:43:23'),(186,'教师课酬','DELETE','com.education.store.module.payroll.controller.EduTeacherPayrollController.delete','DELETE','超级管理员','/api/v1/payrolls/12','0:0:0:0:0:0:0:1','','[12];','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001402680}',1,NULL,6,'2026-07-26 01:43:23'),(187,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',8,'2026-07-26 01:43:23'),(188,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',8,'2026-07-26 01:43:23'),(189,'积分账户管理','UPDATE','com.education.store.module.points.controller.PointsAccountController.adjust','POST','超级管理员','/api/v1/points/account/admin/adjust','0:0:0:0:0:0:0:1','','{};{};全勤奖励;','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"id\":2,\"studentId\":7,\"totalEarned\":1000,\"totalSpent\":150,\"balance\":850,\"createTime\":1785000982000,\"updateTime\":1785000981000},\"timestamp\":1785001402817}',1,NULL,12,'2026-07-26 01:43:23'),(190,'积分礼品管理','INSERT','com.education.store.module.points.controller.PointsGiftController.add','POST','超级管理员','/api/v1/points/gift','0:0:0:0:0:0:0:1','','{\"name\":\"测试礼品-02281\",\"imageUrl\":\"https://via.placeholder.com/100\",\"pointsRequired\":150,\"stockTotal\":30,\"status\":1,\"description\":\"验证用礼品\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"name\":\"测试礼品-02281\",\"imageUrl\":\"https://via.placeholder.com/100\",\"pointsRequired\":150,\"stockTotal\":30,\"stockRemaining\":30,\"status\":1,\"description\":\"验证用礼品\",\"id\":2,\"createTime\":1785001402833,\"updateTime\":1785001402833},\"timestamp\":1785001402836}',1,NULL,7,'2026-07-26 01:43:23'),(191,'兑换记录管理','INSERT','com.education.store.module.points.controller.PointsRedemptionController.redeem','POST','超级管理员','/api/v1/points/redemption','0:0:0:0:0:0:0:1','','{};{};{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":7,\"giftId\":2,\"pointsSpent\":150,\"status\":\"PENDING_CLAIM\",\"redeemTime\":1785001402864,\"operatorId\":1,\"id\":2,\"createTime\":1785001402864,\"updateTime\":1785001402864},\"timestamp\":1785001402868}',1,NULL,12,'2026-07-26 01:43:23'),(192,'兑换记录管理','UPDATE','com.education.store.module.points.controller.PointsRedemptionController.claim','POST','超级管理员','/api/v1/points/redemption/2/claim','0:0:0:0:0:0:0:1','','{};{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001402896}',1,NULL,12,'2026-07-26 01:43:23'),(193,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',7,'2026-07-26 01:43:23'),(194,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,360,'2026-07-26 01:47:26'),(195,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',72,'2026-07-26 01:47:26'),(196,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',72,'2026-07-26 01:47:26'),(197,'用户登录','LOGIN','','POST','teacher02','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher02\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',67,'2026-07-26 01:47:26'),(198,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"测试课程-46092\",\"code\":\"COURSE46092\",\"subjectId\":1,\"courseType\":\"GROUP\",\"price\":2400,\"status\":1,\"description\":\"验证用课程\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001646142}',1,NULL,24,'2026-07-26 01:47:26'),(199,'教室管理','INSERT','com.education.store.module.course.controller.EduClassroomController.create','POST','超级管理员','/api/v1/classrooms','0:0:0:0:0:0:0:1','','{\"name\":\"测试教室-46092\",\"capacity\":25,\"location\":\"2楼201\",\"status\":1};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001646186}',1,NULL,9,'2026-07-26 01:47:26'),(200,'班级管理','INSERT','com.education.store.module.classinfo.controller.EduClassController.create','POST','超级管理员','/api/v1/classes','0:0:0:0:0:0:0:1','','{\"name\":\"测试班级-46092\",\"courseId\":11,\"teacherId\":2,\"classroomId\":5,\"maxStudents\":10,\"status\":\"1\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":4,\"timestamp\":1785001646233}',1,NULL,8,'2026-07-26 01:47:26'),(201,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"测46092\",\"gender\":\"M\",\"mobile\":\"13933126439\",\"parentName\":\"测爸\",\"parentRelation\":\"FATHER\",\"parentMobile\":\"13882538600\",\"intendedCourseId\":11,\"interestLevel\":\"HIGH\",\"source\":\"REFERRAL\",\"remark\":\"验证用意向学员\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001646299}',1,NULL,9,'2026-07-26 01:47:26'),(202,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/9/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"电话沟通，家长很感兴趣，约了周末试听\",\"nextPlan\":\"周末安排试听课程\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001646347}',1,NULL,17,'2026-07-26 01:47:26'),(203,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/9/convert','0:0:0:0:0:0:0:1','','{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":13},\"timestamp\":1785001646373}',1,NULL,17,'2026-07-26 01:47:26'),(204,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"测生46092\",\"gender\":\"1\",\"grade\":\"四年级\",\"school\":\"测试小学\",\"mobile\":\"13742420126\",\"parentName\":\"测妈\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001646412}',1,NULL,8,'2026-07-26 01:47:26'),(205,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":14,\"courseId\":11,\"classId\":4,\"amount\":2400,\"handlerId\":1,\"enrollmentDate\":1784908800000,\"remark\":\"验证报名\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001646451}',1,NULL,14,'2026-07-26 01:47:26'),(206,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',90,'2026-07-26 01:47:27'),(207,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',8,'2026-07-26 01:47:27'),(208,'积分账户管理','UPDATE','com.education.store.module.points.controller.PointsAccountController.adjust','POST','超级管理员','/api/v1/points/account/admin/adjust','0:0:0:0:0:0:0:1','','{};{};全勤奖励;','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"id\":2,\"studentId\":7,\"totalEarned\":1500,\"totalSpent\":300,\"balance\":1200,\"createTime\":1785000982000,\"updateTime\":1785001402000},\"timestamp\":1785001646743}',1,NULL,48,'2026-07-26 01:47:27'),(209,'积分礼品管理','INSERT','com.education.store.module.points.controller.PointsGiftController.add','POST','超级管理员','/api/v1/points/gift','0:0:0:0:0:0:0:1','','{\"name\":\"测试礼品-46092\",\"imageUrl\":\"https://via.placeholder.com/100\",\"pointsRequired\":150,\"stockTotal\":30,\"status\":1,\"description\":\"验证用礼品\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"name\":\"测试礼品-46092\",\"imageUrl\":\"https://via.placeholder.com/100\",\"pointsRequired\":150,\"stockTotal\":30,\"stockRemaining\":30,\"status\":1,\"description\":\"验证用礼品\",\"id\":3,\"createTime\":1785001646762,\"updateTime\":1785001646762},\"timestamp\":1785001646765}',1,NULL,8,'2026-07-26 01:47:27'),(210,'兑换记录管理','INSERT','com.education.store.module.points.controller.PointsRedemptionController.redeem','POST','超级管理员','/api/v1/points/redemption','0:0:0:0:0:0:0:1','','{};{};{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":7,\"giftId\":3,\"pointsSpent\":150,\"status\":\"PENDING_CLAIM\",\"redeemTime\":1785001646793,\"operatorId\":1,\"id\":3,\"createTime\":1785001646794,\"updateTime\":1785001646794},\"timestamp\":1785001646799}',1,NULL,14,'2026-07-26 01:47:27'),(211,'兑换记录管理','UPDATE','com.education.store.module.points.controller.PointsRedemptionController.claim','POST','超级管理员','/api/v1/points/redemption/3/claim','0:0:0:0:0:0:0:1','','{};{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001646826}',1,NULL,11,'2026-07-26 01:47:27'),(212,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;',NULL,0,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\r\n### The error may exist in com/education/store/module/payroll/mapper/EduTeacherPayrollMapper.java (best guess)\r\n### The error may involve com.education.store.module.payroll.mapper.EduTeacherPayrollMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_teacher_payroll  ( teacher_id, month, course_id, lesson_count, pay_per_lesson, total_pay, status,   create_by,  create_time, update_time )  VALUES (  ?, ?, ?, ?, ?, ?, ?,   ?,  ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'\n; Duplicate entry \'2-2026-07-1\' for key \'edu_teacher_payroll.uk_teacher_month_course\'',8,'2026-07-26 01:47:27'),(213,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 01:48:45'),(214,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,126,'2026-07-26 01:49:12'),(215,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785001751561}',1,NULL,16,'2026-07-26 01:49:12'),(216,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,75,'2026-07-26 01:49:30'),(217,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',69,'2026-07-26 01:49:30'),(218,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',68,'2026-07-26 01:49:30'),(219,'用户登录','LOGIN','','POST','teacher02','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher02\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',69,'2026-07-26 01:49:30'),(220,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"测试课程-69817\",\"code\":\"COURSE69817\",\"subjectId\":1,\"courseType\":\"GROUP\",\"price\":2400,\"status\":1,\"description\":\"验证用课程\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001769830}',1,NULL,8,'2026-07-26 01:49:30'),(221,'教室管理','INSERT','com.education.store.module.course.controller.EduClassroomController.create','POST','超级管理员','/api/v1/classrooms','0:0:0:0:0:0:0:1','','{\"name\":\"测试教室-69817\",\"capacity\":25,\"location\":\"2楼201\",\"status\":1};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001769851}',1,NULL,6,'2026-07-26 01:49:30'),(222,'班级管理','INSERT','com.education.store.module.classinfo.controller.EduClassController.create','POST','超级管理员','/api/v1/classes','0:0:0:0:0:0:0:1','','{\"name\":\"测试班级-69817\",\"courseId\":12,\"teacherId\":2,\"classroomId\":6,\"maxStudents\":10,\"status\":\"1\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":5,\"timestamp\":1785001769881}',1,NULL,5,'2026-07-26 01:49:30'),(223,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"测69817\",\"gender\":\"M\",\"mobile\":\"13938158952\",\"parentName\":\"测爸\",\"parentRelation\":\"FATHER\",\"parentMobile\":\"13856974147\",\"intendedCourseId\":12,\"interestLevel\":\"HIGH\",\"source\":\"REFERRAL\",\"remark\":\"验证用意向学员\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001769917}',1,NULL,6,'2026-07-26 01:49:30'),(224,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/10/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"电话沟通，家长很感兴趣，约了周末试听\",\"nextPlan\":\"周末安排试听课程\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001769945}',1,NULL,8,'2026-07-26 01:49:30'),(225,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/10/convert','0:0:0:0:0:0:0:1','','{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":15},\"timestamp\":1785001769960}',1,NULL,9,'2026-07-26 01:49:30'),(226,'学员管理','INSERT','com.education.store.module.student.controller.EduStudentController.create','POST','超级管理员','/api/v1/students','0:0:0:0:0:0:0:1','','{\"name\":\"测生69817\",\"gender\":\"1\",\"grade\":\"四年级\",\"school\":\"测试小学\",\"mobile\":\"13754323285\",\"parentName\":\"测妈\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001769984}',1,NULL,6,'2026-07-26 01:49:30'),(227,'报名管理','INSERT','com.education.store.module.enrollment.controller.EduEnrollmentController.create','POST','超级管理员','/api/v1/enrollments','0:0:0:0:0:0:0:1','','{\"studentId\":16,\"courseId\":12,\"classId\":5,\"amount\":2400,\"handlerId\":1,\"enrollmentDate\":1784908800000,\"remark\":\"验证报名\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001770012}',1,NULL,10,'2026-07-26 01:49:30'),(228,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785001770058}',1,NULL,12,'2026-07-26 01:49:30'),(229,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785001770086}',1,NULL,13,'2026-07-26 01:49:30'),(230,'积分账户管理','UPDATE','com.education.store.module.points.controller.PointsAccountController.adjust','POST','超级管理员','/api/v1/points/account/admin/adjust','0:0:0:0:0:0:0:1','','{};{};全勤奖励;','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"id\":2,\"studentId\":7,\"totalEarned\":2000,\"totalSpent\":450,\"balance\":1550,\"createTime\":1785000982000,\"updateTime\":1785001646000},\"timestamp\":1785001770142}',1,NULL,8,'2026-07-26 01:49:30'),(231,'积分礼品管理','INSERT','com.education.store.module.points.controller.PointsGiftController.add','POST','超级管理员','/api/v1/points/gift','0:0:0:0:0:0:0:1','','{\"name\":\"测试礼品-69817\",\"imageUrl\":\"https://via.placeholder.com/100\",\"pointsRequired\":150,\"stockTotal\":30,\"status\":1,\"description\":\"验证用礼品\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"name\":\"测试礼品-69817\",\"imageUrl\":\"https://via.placeholder.com/100\",\"pointsRequired\":150,\"stockTotal\":30,\"stockRemaining\":30,\"status\":1,\"description\":\"验证用礼品\",\"id\":4,\"createTime\":1785001770149,\"updateTime\":1785001770149},\"timestamp\":1785001770152}',1,NULL,5,'2026-07-26 01:49:30'),(232,'兑换记录管理','INSERT','com.education.store.module.points.controller.PointsRedemptionController.redeem','POST','超级管理员','/api/v1/points/redemption','0:0:0:0:0:0:0:1','','{};{};{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":7,\"giftId\":4,\"pointsSpent\":150,\"status\":\"PENDING_CLAIM\",\"redeemTime\":1785001770173,\"operatorId\":1,\"id\":4,\"createTime\":1785001770173,\"updateTime\":1785001770173},\"timestamp\":1785001770178}',1,NULL,14,'2026-07-26 01:49:30'),(233,'兑换记录管理','UPDATE','com.education.store.module.points.controller.PointsRedemptionController.claim','POST','超级管理员','/api/v1/points/redemption/4/claim','0:0:0:0:0:0:0:1','','{};{};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785001770200}',1,NULL,8,'2026-07-26 01:49:30'),(234,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785001770224}',1,NULL,11,'2026-07-26 01:49:30'),(235,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,70,'2026-07-26 01:51:13'),(236,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785001873164}',1,NULL,10,'2026-07-26 01:51:13'),(237,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785001873186}',1,NULL,10,'2026-07-26 01:51:13'),(238,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','0:0:0:0:0:0:0:1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785001873209}',1,NULL,9,'2026-07-26 01:51:13'),(239,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 01:59:41'),(240,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"王老师1337\",\"status\":\"ACTIVE\",\"subjects\":\"物理\",\"hireDate\":1740758400000,\"defaultPayPerLesson\":100,\"phone\":\"13726130580\",\"remark\":\"测试新增教师\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1785002381349}',1,NULL,5,'2026-07-26 01:59:41'),(241,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,73,'2026-07-26 02:00:43'),(242,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,354,'2026-07-26 02:03:58'),(243,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"赵老师7776\",\"status\":\"ACTIVE\",\"subjects\":\"化学\",\"hireDate\":1748707200000,\"defaultPayPerLesson\":110,\"phone\":\"13646232416\",\"remark\":\"化学骨干教师\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1785002637833}',1,NULL,29,'2026-07-26 02:03:58'),(244,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,78,'2026-07-26 02:04:22'),(245,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,361,'2026-07-26 02:06:54'),(246,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,358,'2026-07-26 02:10:44'),(247,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,83,'2026-07-26 02:11:55'),(248,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,358,'2026-07-26 02:15:36'),(249,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,85,'2026-07-26 02:16:09'),(250,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,78,'2026-07-26 02:16:32'),(251,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"王老师\",\"status\":\"ACTIVE\",\"subjects\":\"物理\",\"hireDate\":1740758400000,\"defaultPayPerLesson\":130,\"phone\":\"13682338011\",\"remark\":\"物理教师\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1785003392135}',1,NULL,126,'2026-07-26 02:16:32'),(252,'教师管理','DELETE','com.education.store.module.teaching.controller.EduTeacherController.delete','DELETE','超级管理员','/api/v1/teaching/teacher/14','0:0:0:0:0:0:0:1','','[14];','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1785003392227}',1,NULL,22,'2026-07-26 02:16:32'),(253,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,92,'2026-07-26 02:16:57'),(254,'教师管理','INSERT','com.education.store.module.teaching.controller.EduTeacherController.add','POST','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"孙老师\",\"status\":\"ACTIVE\",\"subjects\":\"化学\",\"hireDate\":1748707200000,\"defaultPayPerLesson\":110,\"phone\":\"13611239592\",\"remark\":\"chem\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1785003417261}',1,NULL,8,'2026-07-26 02:16:57'),(255,'教师管理','UPDATE','com.education.store.module.teaching.controller.EduTeacherController.update','PUT','超级管理员','/api/v1/teaching/teacher','0:0:0:0:0:0:0:1','','{\"realName\":\"孙老师\",\"status\":\"ACTIVE\",\"subjects\":\"化学-高中\",\"hireDate\":1748707200000,\"defaultPayPerLesson\":120,\"phone\":\"13611239592\",\"remark\":\"高中化学教师\",\"id\":15};','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1785003417297}',1,NULL,10,'2026-07-26 02:16:57'),(256,'教师管理','DELETE','com.education.store.module.teaching.controller.EduTeacherController.delete','DELETE','超级管理员','/api/v1/teaching/teacher/15','0:0:0:0:0:0:0:1','','[15];','{\"code\":200,\"message\":\"操作成功\",\"data\":true,\"timestamp\":1785003417328}',1,NULL,9,'2026-07-26 02:16:57'),(257,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,77,'2026-07-26 02:17:49'),(258,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,84,'2026-07-26 02:30:29'),(259,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,77,'2026-07-26 02:33:19'),(260,'科目管理','INSERT','com.education.store.module.course.controller.EduSubjectController.create','POST','超级管理员','/api/v1/subjects','0:0:0:0:0:0:0:1','','{\"name\":\"数学\",\"sortOrder\":1,\"status\":1,\"description\":\"数学科目\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004399484}',1,NULL,36,'2026-07-26 02:33:19'),(261,'科目管理','INSERT','com.education.store.module.course.controller.EduSubjectController.create','POST','超级管理员','/api/v1/subjects','0:0:0:0:0:0:0:1','','{\"name\":\"英语\",\"sortOrder\":2,\"status\":1,\"description\":\"英语科目\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004399516}',1,NULL,9,'2026-07-26 02:33:20'),(262,'科目管理','INSERT','com.education.store.module.course.controller.EduSubjectController.create','POST','超级管理员','/api/v1/subjects','0:0:0:0:0:0:0:1','','{\"name\":\"物理\",\"sortOrder\":3,\"status\":1,\"description\":\"物理科目\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004399542}',1,NULL,8,'2026-07-26 02:33:20'),(263,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"初中数学提高班\",\"code\":\"MATH-JR-001\",\"subjectId\":9,\"courseType\":\"BATCH\",\"price\":2400,\"status\":1,\"sortOrder\":1};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004399583}',1,NULL,11,'2026-07-26 02:33:20'),(264,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"高中英语冲刺班\",\"code\":\"ENG-SR-001\",\"subjectId\":10,\"courseType\":\"BATCH\",\"price\":2800,\"status\":1,\"sortOrder\":2};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004399623}',1,NULL,11,'2026-07-26 02:33:20'),(265,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"物理一对一\",\"code\":\"PHY-1V1-001\",\"subjectId\":11,\"courseType\":\"ONE_ON_ONE\",\"price\":200,\"status\":1,\"sortOrder\":3};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004399655}',1,NULL,10,'2026-07-26 02:33:20'),(266,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"小明\",\"gender\":\"MALE\",\"grade\":\"GRADE_8\",\"mobile\":\"13800138001\",\"parentName\":\"明父\",\"parentRelation\":\"FATHER\",\"parentMobile\":\"13900139001\",\"source\":\"FRIEND\",\"remark\":\"数学成绩提升需求\"};',NULL,0,'\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,  grade,  mobile, parent_name, parent_relation, parent_mobile,    source,  follow_status, trial_lesson_count, last_followup_date,  remark,    create_time, update_time )  VALUES (  ?, ?,  ?,  ?, ?, ?, ?,    ?,  ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\n; Data truncation: Data too long for column \'gender\' at row 1',93,'2026-07-26 02:33:20'),(267,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"小红\",\"gender\":\"FEMALE\",\"grade\":\"GRADE_9\",\"mobile\":\"13800138002\",\"parentName\":\"红母\",\"parentRelation\":\"MOTHER\",\"parentMobile\":\"13900139002\",\"source\":\"ONLINE\",\"remark\":\"英语冲刺\"};',NULL,0,'\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,  grade,  mobile, parent_name, parent_relation, parent_mobile,    source,  follow_status, trial_lesson_count, last_followup_date,  remark,    create_time, update_time )  VALUES (  ?, ?,  ?,  ?, ?, ?, ?,    ?,  ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\n; Data truncation: Data too long for column \'gender\' at row 1',7,'2026-07-26 02:33:20'),(268,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 02:33:42'),(269,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"测试学生\",\"gender\":\"MALE\",\"grade\":\"GRADE_8\",\"mobile\":\"13800138099\",\"source\":\"FRIEND\"};',NULL,0,'\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,  grade,  mobile,       source,  follow_status, trial_lesson_count, last_followup_date,      create_time, update_time )  VALUES (  ?, ?,  ?,  ?,       ?,  ?, ?, ?,      ?, ?  )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\n; Data truncation: Data too long for column \'gender\' at row 1',6,'2026-07-26 02:33:43'),(270,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 02:34:32'),(271,'课程管理','INSERT','com.education.store.module.course.controller.EduCourseController.create','POST','超级管理员','/api/v1/courses','0:0:0:0:0:0:0:1','','{\"name\":\"物理一对一\",\"code\":\"PHY-1V1-001\",\"subjectId\":11,\"courseType\":\"ONE_ON_ONE\",\"price\":200,\"status\":1,\"sortOrder\":3};','{\"code\":500,\"message\":\"课程编号已存在\",\"timestamp\":1785004472325}',1,NULL,4,'2026-07-26 02:34:32'),(272,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"张小明\",\"gender\":\"MALE\",\"grade\":\"GRADE_8\",\"mobile\":\"13800138001\",\"parentName\":\"张明\",\"parentRelation\":\"FATHER\",\"parentMobile\":\"13900139001\",\"interestLevel\":\"HIGH\",\"source\":\"REFERRAL\",\"counselorId\":1,\"remark\":\"数学成绩提升需求\"};',NULL,0,'\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,  grade,  mobile, parent_name, parent_relation, parent_mobile,   interest_level, source, counselor_id, follow_status, trial_lesson_count, last_followup_date,  remark,    create_time, update_time )  VALUES (  ?, ?,  ?,  ?, ?, ?, ?,   ?, ?, ?, ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\n; Data truncation: Data too long for column \'gender\' at row 1',6,'2026-07-26 02:34:32'),(273,'意向学员管理','INSERT','com.education.store.module.prospect.controller.EduProspectController.create','POST','超级管理员','/api/v1/prospects','0:0:0:0:0:0:0:1','','{\"name\":\"李小红\",\"gender\":\"FEMALE\",\"grade\":\"GRADE_9\",\"mobile\":\"13800138002\",\"parentName\":\"李红\",\"parentRelation\":\"MOTHER\",\"parentMobile\":\"13900139002\",\"interestLevel\":\"MEDIUM\",\"source\":\"ONLINE\",\"counselorId\":1,\"remark\":\"英语冲刺需求\"};',NULL,0,'\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\r\n### The error may exist in com/education/store/module/prospect/mapper/EduProspectMapper.java (best guess)\r\n### The error may involve com.education.store.module.prospect.mapper.EduProspectMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO edu_prospect  ( name, gender,  grade,  mobile, parent_name, parent_relation, parent_mobile,   interest_level, source, counselor_id, follow_status, trial_lesson_count, last_followup_date,  remark,    create_time, update_time )  VALUES (  ?, ?,  ?,  ?, ?, ?, ?,   ?, ?, ?, ?, ?, ?,  ?,    ?, ?  )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'gender\' at row 1\n; Data truncation: Data too long for column \'gender\' at row 1',6,'2026-07-26 02:34:32'),(274,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/8/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"电话沟通，家长表示感兴趣\",\"nextPlan\":\"安排试听\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004472437}',1,NULL,19,'2026-07-26 02:34:32'),(275,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/7/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"电话沟通，家长表示感兴趣\",\"nextPlan\":\"安排试听\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004472454}',1,NULL,10,'2026-07-26 02:34:32'),(276,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/4/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"电话沟通，家长表示感兴趣\",\"nextPlan\":\"安排试听\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004472474}',1,NULL,12,'2026-07-26 02:34:32'),(277,'跟进记录','INSERT','com.education.store.module.prospect.controller.EduProspectFollowupController.create','POST','超级管理员','/api/v1/prospects/5/followups','0:0:0:0:0:0:0:1','','{};{\"method\":\"PHONE\",\"content\":\"电话沟通，家长表示感兴趣\",\"nextPlan\":\"安排试听\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785004472491}',1,NULL,11,'2026-07-26 02:34:32'),(278,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/8/convert','0:0:0:0:0:0:0:1','','{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":17},\"timestamp\":1785004472516}',1,NULL,20,'2026-07-26 02:34:32'),(279,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/7/convert','0:0:0:0:0:0:0:1','','{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":18},\"timestamp\":1785004472532}',1,NULL,10,'2026-07-26 02:34:33'),(280,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/4/convert','0:0:0:0:0:0:0:1','','{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":19},\"timestamp\":1785004472552}',1,NULL,14,'2026-07-26 02:34:33'),(281,'意向学员转化','UPDATE','com.education.store.module.prospect.controller.EduProspectController.convert','POST','超级管理员','/api/v1/prospects/5/convert','0:0:0:0:0:0:0:1','','{};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"studentId\":20},\"timestamp\":1785004472569}',1,NULL,11,'2026-07-26 02:34:33'),(282,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,84,'2026-07-26 02:35:22'),(283,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 02:35:51'),(284,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 02:36:22'),(285,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,72,'2026-07-26 02:38:59'),(286,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"MSEdge\",\"os\":\"Windows\"}',NULL,1,NULL,102,'2026-07-26 10:42:10'),(287,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,75,'2026-07-26 10:45:57'),(288,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,74,'2026-07-26 10:47:50'),(289,'课酬单价','INSERT','com.education.store.module.payroll.controller.EduTeacherPayRateController.create','POST','超级管理员','/api/v1/pay-rates','127.0.0.1','','{\"teacherId\":2,\"courseId\":9,\"payPerLesson\":100,\"effectiveFrom\":1785081600000};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785034919377}',1,NULL,14,'2026-07-26 11:01:59'),(290,'教师课酬自动统计','INSERT','com.education.store.module.payroll.controller.EduTeacherPayrollController.autoGenerate','POST','超级管理员','/api/v1/payrolls/auto-generate','127.0.0.1','','2026-07;','{\"code\":200,\"message\":\"操作成功\",\"data\":2,\"timestamp\":1785035013213}',1,NULL,18,'2026-07-26 11:03:33'),(291,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,73,'2026-07-26 11:08:05'),(292,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,75,'2026-07-26 11:08:16'),(293,'课酬单价','DELETE','com.education.store.module.payroll.controller.EduTeacherPayRateController.delete','DELETE','超级管理员','/api/v1/pay-rates/3','127.0.0.1','','[3];','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785035323140}',1,NULL,14,'2026-07-26 11:08:43'),(294,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,70,'2026-07-26 11:38:21'),(295,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,73,'2026-07-26 11:38:44'),(296,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,72,'2026-07-26 11:43:50'),(297,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,71,'2026-07-26 11:45:57'),(298,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,352,'2026-07-26 11:55:48'),(299,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,77,'2026-07-26 11:56:24'),(300,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,366,'2026-07-26 11:58:36'),(301,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,495,'2026-07-26 12:01:53'),(302,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 12:02:32'),(303,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,554,'2026-07-26 12:09:04'),(304,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,74,'2026-07-26 12:10:00'),(305,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,70,'2026-07-26 12:11:44'),(306,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,74,'2026-07-26 12:12:22'),(307,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,1,NULL,71,'2026-07-26 12:12:59'),(308,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"MSEdge\",\"os\":\"Windows\"}',NULL,1,NULL,90,'2026-07-26 12:19:29'),(309,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,0,'用户名或密码错误',349,'2026-07-26 12:49:48'),(310,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"****\",\"browser\":\"Chrome\",\"os\":\"Windows\"}',NULL,0,'用户名或密码错误',64,'2026-07-26 12:49:58'),(311,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',64,'2026-07-26 12:52:50'),(312,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,138,'2026-07-26 12:52:50'),(313,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,199,'2026-07-26 12:53:33'),(314,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,94,'2026-07-26 12:54:06'),(315,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,91,'2026-07-26 12:55:41'),(316,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,125,'2026-07-26 12:56:43'),(317,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,99,'2026-07-26 12:57:23'),(318,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',407,'2026-07-26 13:13:32'),(319,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,174,'2026-07-26 13:15:14'),(320,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,101,'2026-07-26 13:15:33'),(321,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,96,'2026-07-26 13:17:06'),(322,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,603,'2026-07-26 13:27:15'),(323,'班级管理','UPDATE','com.education.store.module.classinfo.controller.EduClassController.update','PUT','超级管理员','/api/v1/classes','127.0.0.1','','{\"id\":6,\"name\":\"数学提高班-2026暑期\",\"courseId\":16,\"teacherId\":3,\"classroomId\":7,\"maxStudents\":20,\"status\":\"RECRUITING\",\"startDate\":1782835200000,\"endDate\":1788105600000,\"description\":\"数学提高班-2026暑期\"};','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785045577248}',1,NULL,35,'2026-07-26 13:59:37'),(324,'排课规则','UPDATE','com.education.store.module.classinfo.controller.EduClassScheduleRuleController.batchSave','PUT','超级管理员','/api/v1/classes/6/schedule-rule','127.0.0.1','','{};[];','{\"code\":200,\"message\":\"操作成功\",\"timestamp\":1785045577529}',1,NULL,15,'2026-07-26 13:59:38'),(325,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 14:12:20'),(326,'用户登录','LOGIN','','POST','teacher02','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher02\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 14:12:21'),(327,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,1,NULL,73,'2026-07-26 14:14:11'),(328,'用户登录','LOGIN','','POST','teacher02','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher02\",\"password\":\"****\"}',NULL,1,NULL,71,'2026-07-26 14:14:11'),(329,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,1,NULL,391,'2026-07-26 14:19:19'),(330,'用户登录','LOGIN','','POST','teacher02','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher02\",\"password\":\"****\"}',NULL,1,NULL,76,'2026-07-26 14:19:19'),(331,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',68,'2026-07-26 14:37:46'),(332,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,0,'用户名或密码错误',67,'2026-07-26 14:37:55'),(333,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,74,'2026-07-26 14:45:21'),(334,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,1,NULL,73,'2026-07-26 14:45:21'),(335,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,73,'2026-07-26 14:46:52'),(336,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,1,NULL,71,'2026-07-26 14:46:52'),(337,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,71,'2026-07-26 14:47:19'),(338,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,119,'2026-07-26 14:54:49'),(339,'用户登录','LOGIN','','POST','admin','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"****\"}',NULL,1,NULL,82,'2026-07-26 14:55:08'),(340,'用户登录','LOGIN','','POST','teacher01','/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher01\",\"password\":\"****\"}',NULL,1,NULL,77,'2026-07-26 14:55:08');
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,1,1,'拥有全部权限','','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(2,'普通用户','user',2,1,1,'基础权限','','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(3,'教师','teacher',3,1,1,'教师角色，拥有教师端权限','','2026-07-25 20:40:34','','2026-07-25 20:40:34',0),(4,'家长','parent',4,1,1,'家长角色，仅查看关联学员信息','','2026-07-25 20:40:34','','2026-07-25 20:40:34',0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,100),(1,101),(1,102),(1,103),(1,104),(1,105),(1,106),(1,107),(1,108),(1,109),(1,110),(1,111),(1,112),(1,113),(1,114),(1,115),(1,116),(1,117),(1,118),(1,119),(1,120),(1,121),(1,122),(1,123),(1,124),(1,125),(1,126),(1,127),(1,128),(1,129),(1,130),(1,131),(1,132),(1,133),(1,134),(1,135),(1,200),(1,201),(1,202),(1,203),(1,210),(1,211),(1,212),(1,213),(1,214),(1,215),(1,216),(1,220),(1,221),(1,222),(1,223),(1,224),(1,230),(1,231),(1,232),(1,240),(1,241),(1,242),(1,250),(1,251),(1,260),(1,261),(1,262),(1,263),(1,264),(1,265),(1,266),(1,267),(1,268),(1,269),(1,270),(1,271),(1,272),(1,273),(1,274),(1,275),(1,276),(1,277),(1,278),(1,279),(1,280),(1,281),(1,282),(1,283),(1,284),(1,285),(1,286),(1,287),(1,288),(1,289),(1,290),(1,291),(1,292),(1,293),(1,294),(1,295),(1,296),(1,297),(1,298),(1,299),(1,300),(1,301),(1,302),(1,303),(1,304),(1,305),(1,306),(1,307),(1,308),(1,309),(1,310),(1,311),(1,312),(1,313),(1,314),(1,315),(1,316),(1,317),(1,318),(1,319),(1,320),(1,321),(1,322),(1,323),(1,324),(1,325),(1,326),(1,327),(1,328),(1,329),(1,330),(1,331),(1,332),(1,333),(1,334),(1,335),(1,336),(1,337),(1,338),(1,339),(1,340),(1,341),(1,342),(1,343),(1,344),(1,345),(1,346),(1,347),(1,348),(1,349),(1,350),(1,351),(1,352),(1,353),(1,354),(1,355),(1,356),(1,357),(1,358),(1,359),(1,360),(1,361),(1,362),(1,363),(1,364),(1,365),(1,366),(1,367),(1,368),(1,369),(1,370),(1,371),(1,372),(1,373),(1,374),(1,375),(1,376),(3,8),(3,9),(3,260),(3,261),(3,262),(3,263),(3,306),(3,310),(3,311),(3,312),(3,313),(3,318),(3,319),(3,320),(3,321),(3,373),(3,376);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2b$10$Wsr/WS5qw9wOb2t8gMWLJeKiMs6I2MEU9mB/6J567V0YPXafrLFMy','超级管理员','管理员','admin@edu.com','13800000000',NULL,1,'',1,'',NULL,NULL,'','2026-07-25 20:40:34','','2026-07-26 14:44:23','系统内置超级管理员',0),(2,'teacher01','$2b$10$Wsr/WS5qw9wOb2t8gMWLJeKiMs6I2MEU9mB/6J567V0YPXafrLFMy','张老师','张老师','','15212376567',NULL,1,'',1,'',NULL,NULL,'1','2026-07-25 21:31:08','','2026-07-26 14:09:42','',0),(3,'teacher02','$2b$10$Wsr/WS5qw9wOb2t8gMWLJeKiMs6I2MEU9mB/6J567V0YPXafrLFMy','李老师','','','',NULL,0,'',1,'',NULL,NULL,'','2026-07-26 01:02:06','','2026-07-26 14:09:42',NULL,0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,3),(3,3);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_notification_setting`
--

LOCK TABLES `user_notification_setting` WRITE;
/*!40000 ALTER TABLE `user_notification_setting` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_notification_setting` ENABLE KEYS */;
UNLOCK TABLES;

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

-- Dump completed on 2026-07-26 15:17:03
