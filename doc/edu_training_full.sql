
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
DROP TABLE IF EXISTS `attendances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendances` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_id` bigint NOT NULL COMMENT '课表ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `status` tinyint NOT NULL COMMENT '1-出勤 2-请假 3-旷课 4-补课',
  `check_in_time` timestamp NULL DEFAULT NULL COMMENT '签到时间',
  `check_in_method` tinyint DEFAULT NULL COMMENT '1-扫码 2-教师点名 3-人脸 4-蓝牙 5-补签',
  `check_in_lat` decimal(10,8) DEFAULT NULL COMMENT '签到纬度',
  `check_in_lng` decimal(11,8) DEFAULT NULL COMMENT '签到经度',
  `leave_type` tinyint DEFAULT NULL COMMENT '请假类型 1-病假 2-事假 3-其他',
  `leave_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请假原因',
  `leave_apply_time` timestamp NULL DEFAULT NULL COMMENT '请假申请时间',
  `leave_approve_status` tinyint DEFAULT NULL COMMENT '0-待审批 1-通过 2-拒绝',
  `leave_approve_time` timestamp NULL DEFAULT NULL COMMENT '审批时间',
  `consume_hours` decimal(3,1) DEFAULT NULL COMMENT '本次消课课时',
  `is_makeup` tinyint DEFAULT '0' COMMENT '是否补课 0-否 1-是',
  `original_attendance_id` bigint DEFAULT NULL COMMENT '原考勤ID（补课时关联）',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_schedule_student` (`schedule_id`,`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `attendances` WRITE;
/*!40000 ALTER TABLE `attendances` DISABLE KEYS */;
INSERT INTO `attendances` VALUES (1,1,3,3,1,'2026-06-29 03:00:22',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `attendances` VALUES (2,1,6,3,1,'2026-06-26 10:41:32',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `attendances` VALUES (3,2,9,4,2,NULL,NULL,NULL,NULL,1,'身体不适，请假一次',NULL,1,NULL,NULL,0,NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `attendances` VALUES (4,3,4,5,1,'2026-06-26 10:56:32',2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `attendances` VALUES (5,4,5,6,3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
/*!40000 ALTER TABLE `attendances` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `birthday_greetings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `birthday_greetings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `greeting_date` date DEFAULT NULL COMMENT '祝福日期',
  `message` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `campus_id` bigint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='生日祝福表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `birthday_greetings` WRITE;
/*!40000 ALTER TABLE `birthday_greetings` DISABLE KEYS */;
INSERT INTO `birthday_greetings` VALUES (1,3,'2026-06-15','李明同学，祝你生日快乐！愿你学业进步，心想事成！','2026-06-28 10:27:56',1);
INSERT INTO `birthday_greetings` VALUES (2,4,'2026-05-20','王小红同学，生日快乐！新的一岁更加优秀！','2026-06-28 10:27:56',1);
INSERT INTO `birthday_greetings` VALUES (3,5,'2026-06-29','HappyBirthday','2026-06-29 02:22:27',1);
/*!40000 ALTER TABLE `birthday_greetings` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `campuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campuses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `campus_name` varchar(100) NOT NULL COMMENT '校区名称',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '校区电话',
  `principal_id` bigint DEFAULT NULL COMMENT '校长用户ID',
  `status` tinyint DEFAULT '1' COMMENT '0-停用 1-正常',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_principal` (`principal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校区表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `campuses` WRITE;
/*!40000 ALTER TABLE `campuses` DISABLE KEYS */;
INSERT INTO `campuses` VALUES (1,'海淀总校','北京市海淀区中关村大街1号','010-66668888',2,1,'2026-06-28 09:57:08','2026-06-28 09:57:08');
INSERT INTO `campuses` VALUES (2,'朝阳分校','北京市朝阳区望京SOHO A座','010-88886666',NULL,1,'2026-06-28 09:57:08','2026-06-28 09:57:08');
INSERT INTO `campuses` VALUES (3,'西城分校','北京市西城区金融街10号','010-99998888',NULL,1,'2026-06-28 09:57:08','2026-06-28 09:57:08');
/*!40000 ALTER TABLE `campuses` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `class_feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_feedbacks` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_id` bigint NOT NULL COMMENT '课表ID',
  `class_id` bigint DEFAULT NULL,
  `teacher_id` bigint DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '教学内容',
  `homework` text COLLATE utf8mb4_unicode_ci COMMENT '作业布置',
  `photos` json DEFAULT NULL COMMENT '课堂照片URL列表',
  `overall_comment` text COLLATE utf8mb4_unicode_ci COMMENT '整体评语',
  `next_preview` text COLLATE utf8mb4_unicode_ci COMMENT '下节课预告',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课堂反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `class_feedbacks` WRITE;
/*!40000 ALTER TABLE `class_feedbacks` DISABLE KEYS */;
INSERT INTO `class_feedbacks` VALUES (1,1,3,3,'今日讲解了二次函数的图像与性质，重点复习了顶点式和一般式的转换','完成课本P56-P58习题1-10',NULL,'同学们学习积极性较高，李明的计算能力有明显进步','下次课将讲解二次函数与一元二次方程的关系','2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `class_feedbacks` VALUES (2,1,NULL,NULL,'Test feedback after fix',NULL,NULL,'Good',NULL,'2026-06-29 03:03:16','2026-06-29 03:03:16');
/*!40000 ALTER TABLE `class_feedbacks` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '主教老师ID',
  `assistant_id` bigint DEFAULT NULL COMMENT '助教老师ID',
  `classroom_id` bigint DEFAULT NULL COMMENT '默认教室ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级名称',
  `max_students` int DEFAULT '20' COMMENT '最大人数',
  `current_students` int DEFAULT '0' COMMENT '当前人数',
  `schedule_rule` json DEFAULT NULL COMMENT '排课规则',
  `start_date` date DEFAULT NULL COMMENT '开班日期',
  `end_date` date DEFAULT NULL COMMENT '结课日期',
  `status` tinyint DEFAULT '1' COMMENT '0-未开班 1-进行中 2-已结课',
  `campus_id` bigint DEFAULT NULL COMMENT '归属校区',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
INSERT INTO `classes` VALUES (1,1,1,NULL,1,'MathClassA',20,2,NULL,'2026-07-01',NULL,1,1,'2026-06-26 11:01:18','2026-06-28 09:57:08');
INSERT INTO `classes` VALUES (3,3,3,NULL,3,'数学冲刺A班',20,2,NULL,'2026-03-01',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classes` VALUES (4,3,3,NULL,4,'数学冲刺B班',20,1,NULL,'2026-03-01',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classes` VALUES (5,4,4,NULL,5,'英语培优班',10,2,NULL,'2026-02-15',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classes` VALUES (6,5,5,NULL,7,'物理竞赛班',8,2,NULL,'2026-01-10',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classes` VALUES (7,6,3,NULL,3,'数学1对1-张老师',1,0,NULL,'2026-04-01',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classes` VALUES (8,8,5,NULL,3,'化学特训班',20,2,NULL,'2026-03-15',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `classrooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classrooms` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教室名称',
  `capacity` int DEFAULT '30' COMMENT '容纳人数',
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '位置描述',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '经度',
  `facilities` json DEFAULT NULL COMMENT '设施 ["投影仪","钢琴"]',
  `status` tinyint DEFAULT '1' COMMENT '0-停用 1-正常',
  `campus_id` bigint DEFAULT NULL COMMENT '归属校区',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教室表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `classrooms` WRITE;
/*!40000 ALTER TABLE `classrooms` DISABLE KEYS */;
INSERT INTO `classrooms` VALUES (1,'101-数学冲刺班',30,'1楼电梯左边走到头',NULL,NULL,'[\"投影仪\", \"电脑\", \"空调\", \"白板\"]',1,1,'2026-06-26 10:48:55','2026-06-28 09:57:08');
INSERT INTO `classrooms` VALUES (2,'Room301',30,'Floor3',NULL,NULL,NULL,1,1,'2026-06-26 11:01:18','2026-06-28 09:57:08');
INSERT INTO `classrooms` VALUES (3,'101-多媒体教室',30,'1楼东侧',NULL,NULL,'[\"投影仪\", \"白板\", \"电脑\", \"空调\"]',1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classrooms` VALUES (4,'102-标准教室',25,'1楼西侧',NULL,NULL,'[\"白板\", \"空调\"]',1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classrooms` VALUES (5,'201-小班教室',10,'2楼东侧',NULL,NULL,'[\"白板\", \"空调\"]',1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classrooms` VALUES (6,'301-音乐教室',15,'3楼',NULL,NULL,'[\"钢琴\", \"音响系统\", \"舞蹈镜\"]',1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `classrooms` VALUES (7,'202-实验室',20,'2楼西侧',NULL,NULL,'[\"实验台\", \"投影仪\", \"储物柜\"]',1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
/*!40000 ALTER TABLE `classrooms` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程编码',
  `type` tinyint NOT NULL COMMENT '类型 1-班课 2-一对一 3-小班',
  `subject` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '科目',
  `grade_range` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '适用年级范围',
  `duration` int DEFAULT '120' COMMENT '单次课时分钟数',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价(元/课时)',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '课程介绍',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `status` tinyint DEFAULT '1' COMMENT '0-下架 1-上架',
  `campus_id` bigint DEFAULT NULL COMMENT '归属校区(NULL=共享)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_type` (`type`),
  KEY `idx_subject` (`subject`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'初三冲刺班','',1,'数学','初三',120,100.00,'专业老师团队授课',NULL,1,1,'2026-06-26 10:47:49','2026-06-28 09:57:08');
INSERT INTO `courses` VALUES (2,'Math Sprint','CRS000001',1,'Math','Grade9',120,150.00,NULL,NULL,1,1,'2026-06-26 11:01:18','2026-06-28 09:57:08');
INSERT INTO `courses` VALUES (3,'初三数学冲刺班','CRS001',1,'数学','初三',120,150.00,'针对中考数学重点难点进行强化训练',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `courses` VALUES (4,'初中英语培优班','CRS002',1,'英语','初一-初三',90,120.00,'提升英语综合能力，听说读写全面发展',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `courses` VALUES (5,'高中物理竞赛班','CRS003',3,'物理','高一-高三',180,200.00,'物理竞赛专项训练，目标省一以上',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `courses` VALUES (6,'一对一数学辅导','CRS004',2,'数学','初中-高中',60,300.00,'一对一精准辅导，量身定制学习方案',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `courses` VALUES (7,'少儿钢琴启蒙','CRS005',2,'音乐','5-12岁',45,200.00,'适合零基础儿童，培养音乐兴趣',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `courses` VALUES (8,'高中化学特训','CRS006',1,'化学','高一-高三',120,160.00,'高考化学重难点突破',NULL,1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `courses` VALUES (9,'TestCourse','CRS000002',1,'Math','test',60,50.00,NULL,NULL,1,NULL,'2026-06-29 03:00:09','2026-06-29 03:00:09');
INSERT INTO `courses` VALUES (10,'TestCourse2','CRS000003',1,'Math','test',60,50.00,NULL,NULL,0,NULL,'2026-06-29 03:00:22','2026-06-29 03:00:22');
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','init schema','SQL','V1__init_schema.sql',1980953621,'root','2026-06-26 10:21:50',297,1);
INSERT INTO `flyway_schema_history` VALUES (2,'2','seed configs','SQL','V2__seed_configs.sql',228260719,'root','2026-06-26 10:21:50',4,1);
INSERT INTO `flyway_schema_history` VALUES (3,'3','add user rbac','SQL','V3__add_user_rbac.sql',1362630997,'root','2026-06-26 11:11:32',98,1);
INSERT INTO `flyway_schema_history` VALUES (4,'4','seed demo data','SQL','V4__seed_demo_data.sql',1875025693,'root','2026-06-26 11:11:32',28,1);
INSERT INTO `flyway_schema_history` VALUES (5,'5','seed history data','SQL','V5__seed_history_data.sql',737028254,'root','2026-06-26 11:58:41',11,1);
INSERT INTO `flyway_schema_history` VALUES (6,'6','update roles permissions','SQL','V6__update_roles_permissions.sql',-868502576,'root','2026-06-28 09:39:30',31,1);
INSERT INTO `flyway_schema_history` VALUES (7,'7','multi campus','SQL','V7__multi_campus.sql',-1160153505,'root','2026-06-28 09:57:08',180,1);
INSERT INTO `flyway_schema_history` VALUES (8,'8','add demo tables','SQL','V8__add_demo_tables.sql',199428410,'root','2026-06-28 10:27:56',161,1);
INSERT INTO `flyway_schema_history` VALUES (9,'9','add leaves table','SQL','V9__add_leaves_table.sql',-1888246065,'root','2026-06-29 02:15:51',1,1);
INSERT INTO `flyway_schema_history` VALUES (10,'10','fix feedback schema','SQL','V10__fix_feedback_schema.sql',-1321569714,'root','2026-06-29 03:11:27',89,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `growth_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `growth_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `teacher_id` bigint DEFAULT NULL,
  `record_date` date DEFAULT NULL COMMENT '记录日期',
  `content` text COMMENT '评语',
  `score` tinyint DEFAULT '3' COMMENT '评分1-5',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `campus_id` bigint DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='成长档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `growth_records` WRITE;
/*!40000 ALTER TABLE `growth_records` DISABLE KEYS */;
INSERT INTO `growth_records` VALUES (1,3,3,'2026-06-10','课堂表现积极，正确回答了3道难题，数学思维能力有显著提升',4,'2026-06-28 10:27:56',1);
INSERT INTO `growth_records` VALUES (2,3,3,'2026-06-17','作业完成质量高，二次函数部分已完全掌握',5,'2026-06-28 10:27:56',1);
INSERT INTO `growth_records` VALUES (3,4,4,'2026-06-12','英语口语表达流畅，敢于在课堂上用英语交流',4,'2026-06-28 10:27:56',1);
INSERT INTO `growth_records` VALUES (4,5,5,'2026-06-15','物理竞赛题解题速度提升，但需要加强实验题训练',3,'2026-06-28 10:27:56',1);
INSERT INTO `growth_records` VALUES (5,3,NULL,'2026-06-29','Great progress this week',5,'2026-06-29 02:22:12',1);
/*!40000 ALTER TABLE `growth_records` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `homework`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homework` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `schedule_id` bigint DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `teacher_id` bigint DEFAULT NULL,
  `content` text COMMENT '作业内容',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `campus_id` bigint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `homework` WRITE;
/*!40000 ALTER TABLE `homework` DISABLE KEYS */;
INSERT INTO `homework` VALUES (1,NULL,3,3,'完成课本P56-P58习题1-10，重点复习二次函数顶点式转换','2026-06-28 10:27:56',1);
INSERT INTO `homework` VALUES (2,NULL,5,4,'背诵Unit 5单词，完成练习册P30-32阅读理解','2026-06-28 10:27:56',1);
INSERT INTO `homework` VALUES (3,NULL,3,NULL,'Math homework P60-62','2026-06-29 02:22:12',1);
/*!40000 ALTER TABLE `homework` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `hour_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hour_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `student_class_id` bigint NOT NULL COMMENT '学员班级关系ID',
  `type` tinyint NOT NULL COMMENT '1-报名增加 2-上课消耗 3-请假返还 4-补课消耗 5-赠课 6-退课',
  `change_hours` decimal(6,1) NOT NULL COMMENT '变动课时(正数增加/负数减少)',
  `before_hours` decimal(6,1) DEFAULT NULL COMMENT '变动前',
  `after_hours` decimal(6,1) DEFAULT NULL COMMENT '变动后',
  `schedule_id` bigint DEFAULT NULL COMMENT '关联课表ID',
  `attendance_id` bigint DEFAULT NULL COMMENT '关联考勤ID',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_student_class_id` (`student_class_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课时流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `hour_transactions` WRITE;
/*!40000 ALTER TABLE `hour_transactions` DISABLE KEYS */;
INSERT INTO `hour_transactions` VALUES (1,1,1,1,22.0,0.0,22.0,NULL,NULL,1,'报名增加20.0课时，赠送2.0课时','2026-06-26 11:01:52');
INSERT INTO `hour_transactions` VALUES (2,1,1,5,2.0,20.0,22.0,NULL,NULL,1,'赠送课时','2026-06-26 11:01:52');
INSERT INTO `hour_transactions` VALUES (3,2,2,1,10.0,0.0,10.0,NULL,NULL,2,'报名增加10.0课时','2026-06-26 11:02:38');
INSERT INTO `hour_transactions` VALUES (4,3,16,1,20.0,0.0,20.0,NULL,NULL,39,'报名增加20.0课时','2026-06-29 03:41:05');
INSERT INTO `hour_transactions` VALUES (5,5,17,1,20.0,0.0,20.0,NULL,NULL,42,'报名增加20.0课时','2026-06-29 05:25:50');
/*!40000 ALTER TABLE `hour_transactions` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `leaves`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leaves` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `schedule_id` bigint DEFAULT NULL,
  `leave_type` tinyint DEFAULT '1' COMMENT '1-病假 2-事假 3-其他',
  `reason` varchar(255) DEFAULT NULL COMMENT '请假原因',
  `status` tinyint DEFAULT '0' COMMENT '0-待审批 1-已通过 2-已拒绝',
  `approve_remark` varchar(255) DEFAULT NULL COMMENT '审批备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `campus_id` bigint DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='请假表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `leaves` WRITE;
/*!40000 ALTER TABLE `leaves` DISABLE KEYS */;
INSERT INTO `leaves` VALUES (1,3,1,1,'感冒发烧需要休息',1,'同意，注意休息','2026-06-29 02:10:33','2026-06-29 02:10:33',1);
INSERT INTO `leaves` VALUES (2,4,2,2,'家中有事需请假一天',1,'同意','2026-06-29 02:10:33','2026-06-29 02:10:33',1);
INSERT INTO `leaves` VALUES (3,5,3,1,'肠胃不适需就医',1,'OK','2026-06-29 02:10:33','2026-06-29 02:10:33',1);
INSERT INTO `leaves` VALUES (4,6,1,3,'其他原因',2,'建议调整时间而非请假','2026-06-29 02:10:33','2026-06-29 02:10:33',1);
INSERT INTO `leaves` VALUES (5,7,1,2,'Family matter',0,NULL,'2026-06-29 02:22:12','2026-06-29 02:22:12',1);
/*!40000 ALTER TABLE `leaves` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `menus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `menu_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由路径',
  `menu_icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `menu_type` tinyint DEFAULT '1' COMMENT '1-目录 2-菜单 3-按钮',
  `perm_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联权限编码',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `visible` tinyint DEFAULT '1' COMMENT '0-隐藏 1-显示',
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `menus` WRITE;
/*!40000 ALTER TABLE `menus` DISABLE KEYS */;
INSERT INTO `menus` VALUES (1,0,'📊 工作台','/dashboard','',2,'dashboard',1,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (2,0,'📅 教务管理','','',1,'',2,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (3,2,'排课管理','/schedule-manage','',2,'schedule:read',1,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (4,2,'课程管理','/course-manage','',2,'course:read',2,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (5,2,'教室管理','/classroom-manage','',2,'course:read',3,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (6,2,'教师管理','/teacher-manage','',2,'teacher:read',4,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (7,0,'👤 学员管理','/student-manage','',2,'student:read',3,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (8,0,'🛒 报名缴费','/enrollment','',2,'order:write',4,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (9,0,'✅ 考勤签到','/attendance-query','',2,'attendance:read',5,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (10,0,'💬 家校互动','','',1,'',6,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (11,10,'课堂反馈','/feedback-manage','',2,'feedback:read',1,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (12,10,'成长档案','/growth-manage','',2,'growth:read',2,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (13,10,'请假管理','/leave-manage','',2,'leave:approve',3,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (14,10,'布置作业','/homework-manage','',2,'homework:write',4,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (15,10,'积分管理','/points-manage','',2,'points:view',5,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (16,10,'生日祝福','/birthday-manage','',2,'birthday:notify',6,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (17,0,'🏫 校区管理','/campus-manage','',2,'campus:manage',7,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (18,0,'💰 财务管理','/finance','',2,'finance:read',8,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (19,0,'📈 数据分析','/statistics','',2,'statistics:read',9,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (20,0,'⚙️ 系统设置','','',1,'',10,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (21,20,'用户管理','/user-manage','',2,'user:manage',1,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (22,20,'菜单管理','/menu-manage','',2,'user:manage',2,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (23,20,'权限管理','/permission-manage','',2,'teacher:perm',3,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (24,20,'系统配置','/system-config','',2,'system:config',4,1,1,'2026-06-28 10:27:55');
INSERT INTO `menus` VALUES (25,20,'公众号设置','/wechat-config','',2,'wechat:push',5,1,1,'2026-06-28 10:27:55');
/*!40000 ALTER TABLE `menus` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID（班课）',
  `item_type` tinyint DEFAULT NULL COMMENT '1-课时包 2-按期 3-按年 4-次卡',
  `hours` decimal(6,1) DEFAULT NULL COMMENT '课时数',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '小计',
  `gift_hours` decimal(6,1) DEFAULT '0.0' COMMENT '赠送课时',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,1,1,20.0,150.00,3000.00,2.0,'2026-06-26 11:01:18');
INSERT INTO `order_items` VALUES (2,2,1,1,1,10.0,150.00,1500.00,0.0,'2026-06-26 11:02:24');
INSERT INTO `order_items` VALUES (3,3,3,3,1,40.0,150.00,6000.00,2.0,'2026-06-26 11:11:32');
INSERT INTO `order_items` VALUES (4,4,4,5,1,36.0,120.00,4320.00,0.0,'2026-06-26 11:11:32');
INSERT INTO `order_items` VALUES (5,5,5,6,1,48.0,200.00,9600.00,4.0,'2026-06-26 11:11:32');
INSERT INTO `order_items` VALUES (6,6,8,8,1,40.0,150.00,6000.00,0.0,'2026-06-26 11:11:32');
INSERT INTO `order_items` VALUES (7,38,1,NULL,1,NULL,100.00,0.00,0.0,'2026-06-29 03:03:17');
INSERT INTO `order_items` VALUES (8,39,4,5,1,20.0,120.00,2400.00,0.0,'2026-06-29 03:41:05');
INSERT INTO `order_items` VALUES (9,40,1,NULL,1,20.0,100.00,2000.00,0.0,'2026-06-29 05:23:29');
INSERT INTO `order_items` VALUES (10,41,4,NULL,1,30.0,80.00,2400.00,0.0,'2026-06-29 05:23:29');
INSERT INTO `order_items` VALUES (11,42,8,8,1,20.0,160.00,3200.00,0.0,'2026-06-29 05:25:50');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `parent_id` bigint DEFAULT NULL COMMENT '下单家长ID',
  `type` tinyint DEFAULT NULL COMMENT '1-新报 2-续费 3-扩科 4-补费',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单总金额',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '实付金额',
  `pay_status` tinyint DEFAULT '0' COMMENT '0-未支付 1-已支付 2-部分退款 3-全额退款',
  `pay_method` tinyint DEFAULT NULL COMMENT '1-微信支付 2-支付宝 3-线下 4-余额',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '第三方支付流水号',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'ORD202600000001',1,NULL,1,3000.00,0.00,3000.00,1,3,'2026-06-26 11:01:52',NULL,'cash','2026-06-26 11:01:18','2026-06-26 11:01:18');
INSERT INTO `orders` VALUES (2,'ORD202600000002',2,NULL,1,1500.00,0.00,1500.00,1,3,'2026-06-26 11:02:38',NULL,NULL,'2026-06-26 11:02:24','2026-06-26 11:02:24');
INSERT INTO `orders` VALUES (3,'ORD20260300001',3,NULL,1,6000.00,200.00,5800.00,1,3,'2026-03-01 02:00:00',NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `orders` VALUES (4,'ORD20260300002',4,NULL,1,4320.00,0.00,4320.00,1,3,'2026-02-15 06:00:00',NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `orders` VALUES (5,'ORD20260400001',5,NULL,1,9600.00,500.00,9100.00,1,1,'2026-04-01 01:00:00',NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `orders` VALUES (6,'ORD20260600001',6,NULL,1,6000.00,0.00,6000.00,0,NULL,NULL,NULL,NULL,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `orders` VALUES (7,'ORD20260528001',3,NULL,1,3000.00,0.00,3000.00,1,3,'2026-05-28 02:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (8,'ORD20260529001',4,NULL,1,2400.00,0.00,2400.00,1,3,'2026-05-29 06:30:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (9,'ORD20260530001',5,NULL,2,4800.00,200.00,4600.00,1,1,'2026-05-30 01:15:00',NULL,'续费优惠200','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (10,'ORD20260601001',6,NULL,1,3200.00,0.00,3200.00,1,3,'2026-06-01 03:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (11,'ORD20260601002',7,NULL,1,5600.00,300.00,5300.00,1,1,'2026-06-01 08:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (12,'ORD20260602001',3,NULL,3,1800.00,0.00,1800.00,1,3,'2026-06-02 02:30:00',NULL,'扩科物理','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (13,'ORD20260603001',8,NULL,1,4200.00,0.00,4200.00,1,3,'2026-06-03 07:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (14,'ORD20260604001',9,NULL,1,6000.00,500.00,5500.00,1,1,'2026-06-04 01:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (15,'ORD20260605001',4,NULL,3,2200.00,0.00,2200.00,1,3,'2026-06-05 06:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (16,'ORD20260606001',5,NULL,1,3600.00,100.00,3500.00,1,1,'2026-06-06 02:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (17,'ORD20260608001',10,NULL,1,4800.00,0.00,4800.00,1,3,'2026-06-08 03:30:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (18,'ORD20260608002',7,NULL,1,2800.00,0.00,2800.00,1,3,'2026-06-08 08:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (19,'ORD20260610001',3,NULL,2,4000.00,200.00,3800.00,1,1,'2026-06-10 01:00:00',NULL,'续费','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (20,'ORD20260611001',6,NULL,1,3200.00,0.00,3200.00,1,3,'2026-06-11 06:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (21,'ORD20260612001',8,NULL,1,5400.00,300.00,5100.00,1,1,'2026-06-12 02:30:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (22,'ORD20260613001',9,NULL,2,3500.00,0.00,3500.00,1,3,'2026-06-13 07:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (23,'ORD20260615001',4,NULL,1,2600.00,0.00,2600.00,1,3,'2026-06-15 01:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (24,'ORD20260615002',5,NULL,3,1800.00,0.00,1800.00,1,1,'2026-06-15 03:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (25,'ORD20260616001',10,NULL,1,5000.00,400.00,4600.00,1,3,'2026-06-16 08:30:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (26,'ORD20260618001',7,NULL,1,4200.00,0.00,4200.00,1,3,'2026-06-18 02:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (27,'ORD20260618002',3,NULL,1,3000.00,0.00,3000.00,1,1,'2026-06-18 06:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (28,'ORD20260619001',8,NULL,2,3800.00,200.00,3600.00,1,3,'2026-06-19 01:30:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (29,'ORD20260620001',6,NULL,1,4500.00,0.00,4500.00,1,3,'2026-06-20 03:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (30,'ORD20260622001',9,NULL,1,2800.00,0.00,2800.00,1,1,'2026-06-22 07:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (31,'ORD20260622002',4,NULL,1,5200.00,300.00,4900.00,1,3,'2026-06-22 08:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (32,'ORD20260623001',5,NULL,1,3400.00,0.00,3400.00,1,3,'2026-06-23 02:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (33,'ORD20260624001',10,NULL,3,2000.00,0.00,2000.00,1,1,'2026-06-24 06:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (34,'ORD20260625001',7,NULL,1,4800.00,200.00,4600.00,1,3,'2026-06-25 01:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (35,'ORD20260625002',3,NULL,1,3600.00,0.00,3600.00,1,3,'2026-06-25 03:30:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (36,'ORD20260626001',8,NULL,1,6000.00,500.00,5500.00,1,1,'2026-06-26 02:00:00',NULL,'','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (37,'ORD20260626002',6,NULL,1,4200.00,0.00,4200.00,0,NULL,NULL,NULL,'待支付','2026-06-26 11:58:41','2026-06-26 11:58:41');
INSERT INTO `orders` VALUES (38,'ORD202600000003',3,NULL,1,0.00,0.00,0.00,0,NULL,NULL,NULL,NULL,'2026-06-29 03:03:17','2026-06-29 03:03:17');
INSERT INTO `orders` VALUES (39,'ORD202600000004',3,NULL,1,2400.00,0.00,2400.00,1,3,'2026-06-29 03:41:05',NULL,'','2026-06-29 03:41:05','2026-06-29 03:41:05');
INSERT INTO `orders` VALUES (40,'ORD202600000005',3,NULL,1,2000.00,0.00,2000.00,1,1,'2026-06-29 05:23:29',NULL,NULL,'2026-06-29 05:23:29','2026-06-29 05:23:29');
INSERT INTO `orders` VALUES (41,'ORD202600000006',4,NULL,1,2400.00,0.00,2400.00,1,2,'2026-06-29 05:23:29',NULL,NULL,'2026-06-29 05:23:29','2026-06-29 05:23:29');
INSERT INTO `orders` VALUES (42,'ORD202600000007',5,NULL,1,3200.00,0.00,3200.00,1,1,'2026-06-29 05:25:50',NULL,NULL,'2026-06-29 05:25:50','2026-06-29 05:25:50');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `perm_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码 student:read',
  `perm_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `perm_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'button' COMMENT 'menu/button/api',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID',
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,'dashboard','工作台','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (2,'student:read','学员查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (3,'student:write','学员编辑','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (4,'student:delete','学员删除','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (5,'course:read','课程查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (6,'course:write','课程编辑','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (7,'teacher:read','教师查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (8,'teacher:write','教师编辑','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (9,'schedule:read','排课查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (10,'schedule:write','排课编辑','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (11,'attendance:read','考勤查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (12,'attendance:write','考勤操作','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (13,'order:read','订单查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (14,'order:write','订单操作','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (15,'finance:read','财务查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (16,'statistics:read','统计查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (17,'feedback:read','反馈查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (18,'feedback:write','反馈操作','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (19,'system:config','系统配置','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (20,'user:manage','用户管理','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (21,'homework:write','布置作业','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (22,'growth:read','成长档案查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (23,'growth:write','成长档案编辑','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (24,'leave:approve','请假审批','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (25,'smart:schedule','智能排课','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (26,'birthday:notify','生日祝福','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (27,'wechat:push','公众号推送','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (28,'face:checkin','人脸签到','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (29,'consume:fee','课时费扣减','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (30,'teacher:perm','老师权限设置','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (31,'hours:balance','课时余额查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (32,'hours:warning','课时不足预警','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (33,'points:view','积分查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (34,'points:manage','积分管理','button',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (35,'recommend','学员推荐奖励','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (36,'arrive:notify','到校通知','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (37,'leave:notify','离校通知','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (38,'owe:warning','欠课提醒','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (39,'leave:apply','请假申请','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (40,'campus:manage','校区管理','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (41,'teacher:hours','教师课时消耗查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (42,'all:student:hours','全部学员课时消耗查看','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (43,'all:view','查看所有数据','menu',0,0,'2026-06-28 09:39:30');
INSERT INTO `permissions` VALUES (44,'all:config','设置所有功能','menu',0,0,'2026-06-28 09:39:30');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `point_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `point_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `change_amount` int DEFAULT NULL COMMENT '变动(+/-)',
  `reason` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `campus_id` bigint DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='积分流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `point_transactions` WRITE;
/*!40000 ALTER TABLE `point_transactions` DISABLE KEYS */;
INSERT INTO `point_transactions` VALUES (1,3,500,'报名赠送积分','2026-06-28 10:27:56',1);
INSERT INTO `point_transactions` VALUES (2,3,200,'课堂表现优秀','2026-06-28 10:27:56',1);
INSERT INTO `point_transactions` VALUES (3,3,100,'作业全优','2026-06-28 10:27:56',1);
INSERT INTO `point_transactions` VALUES (4,4,500,'报名赠送积分','2026-06-28 10:27:56',1);
INSERT INTO `point_transactions` VALUES (5,4,300,'进步奖','2026-06-28 10:27:56',1);
INSERT INTO `point_transactions` VALUES (6,5,1000,'报名赠送积分','2026-06-28 10:27:56',1);
INSERT INTO `point_transactions` VALUES (7,5,500,'物理竞赛入围奖','2026-06-28 10:27:56',1);
INSERT INTO `point_transactions` VALUES (8,5,200,'课堂积极','2026-06-28 10:27:56',1);
INSERT INTO `point_transactions` VALUES (9,3,100,'Test reward','2026-06-29 02:22:12',1);
INSERT INTO `point_transactions` VALUES (10,3,50,'Test add points','2026-06-29 03:00:07',1);
INSERT INTO `point_transactions` VALUES (11,3,-30,'Test deduct points','2026-06-29 03:00:07',1);
INSERT INTO `point_transactions` VALUES (12,3,-200,'兑换: 文具礼包','2026-06-29 03:34:24',1);
INSERT INTO `point_transactions` VALUES (13,3,-500,'兑换: 课外读物','2026-06-29 03:34:28',1);
/*!40000 ALTER TABLE `point_transactions` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `balance` int DEFAULT '0' COMMENT '积分余额',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `campus_id` bigint DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='积分表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `points` WRITE;
/*!40000 ALTER TABLE `points` DISABLE KEYS */;
INSERT INTO `points` VALUES (1,3,670,'2026-06-28 10:27:56',1);
INSERT INTO `points` VALUES (2,4,800,'2026-06-28 10:27:56',1);
INSERT INTO `points` VALUES (3,5,2100,'2026-06-28 10:27:56',1);
INSERT INTO `points` VALUES (4,6,350,'2026-06-28 10:27:56',1);
INSERT INTO `points` VALUES (5,7,150,'2026-06-28 10:27:56',1);
/*!40000 ALTER TABLE `points` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
INSERT INTO `role_permissions` VALUES (38,1,1,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (39,1,2,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (40,1,3,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (41,1,4,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (42,1,5,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (43,1,6,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (44,1,7,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (45,1,8,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (46,1,9,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (47,1,10,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (48,1,11,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (49,1,12,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (50,1,13,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (51,1,14,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (52,1,15,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (53,1,16,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (54,1,17,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (55,1,18,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (56,1,19,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (57,1,20,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (58,1,21,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (59,1,22,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (60,1,23,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (61,1,24,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (62,1,25,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (63,1,26,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (64,1,27,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (65,1,28,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (66,1,29,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (67,1,30,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (68,1,31,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (69,1,32,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (70,1,33,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (71,1,34,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (72,1,35,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (73,1,36,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (74,1,37,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (75,1,38,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (76,1,39,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (77,1,40,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (78,1,41,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (79,1,42,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (80,1,43,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (81,1,44,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (82,2,1,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (83,2,2,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (84,2,5,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (85,2,7,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (86,2,9,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (87,2,10,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (88,2,11,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (89,2,13,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (90,2,16,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (91,2,17,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (92,2,22,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (93,2,24,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (94,2,40,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (95,2,41,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (96,2,42,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (97,3,1,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (98,3,2,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (99,3,3,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (100,3,5,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (101,3,6,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (102,3,9,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (103,3,10,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (104,3,11,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (105,3,12,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (106,3,16,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (107,3,17,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (108,3,18,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (109,3,21,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (110,3,22,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (111,3,23,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (112,3,24,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (113,3,25,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (114,3,26,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (115,3,28,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (116,3,29,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (117,4,1,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (118,4,2,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (119,4,9,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (120,4,11,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (121,4,13,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (122,4,17,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (123,4,31,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (124,4,32,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (125,4,33,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (126,4,35,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (127,4,36,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (128,4,37,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (129,4,38,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (130,4,39,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (131,5,1,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (132,5,9,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (133,5,11,'2026-06-28 09:39:30');
INSERT INTO `role_permissions` VALUES (134,5,13,'2026-06-28 09:39:30');
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码 admin/teacher/student/parent',
  `role_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'boss','老板端','看见所有数据，设置所有功能，财务管理',1,'2026-06-28 09:39:30');
INSERT INTO `roles` VALUES (2,'principal','校长端','校区管理、教师/学生课时消耗查看',1,'2026-06-28 09:39:30');
INSERT INTO `roles` VALUES (3,'teacher','教师端','学员管理、课程管理、点名签到消课、成长档案、家校互动、请假管理、排课、作业',1,'2026-06-28 09:39:30');
INSERT INTO `roles` VALUES (4,'parent','家长端','孩子课时查看、上课提醒、预警、成长档案、积分、课表、请假、到校离校通知',1,'2026-06-28 09:39:30');
INSERT INTO `roles` VALUES (5,'student','学员端','课表查看、签到、查课时',1,'2026-06-28 09:39:30');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `classroom_id` bigint DEFAULT NULL COMMENT '教室ID',
  `date` date NOT NULL COMMENT '上课日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `lesson_no` int DEFAULT NULL COMMENT '第几课次',
  `status` tinyint DEFAULT '1' COMMENT '0-取消 1-正常 2-调课 3-补课 4-停课',
  `campus_id` bigint DEFAULT NULL COMMENT '归属校区',
  `original_schedule_id` bigint DEFAULT NULL COMMENT '原课表ID（调课/补课时）',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_time` (`teacher_id`,`date`,`start_time`),
  UNIQUE KEY `uk_classroom_time` (`classroom_id`,`date`,`start_time`),
  KEY `idx_class_id_date` (`class_id`,`date`),
  KEY `idx_teacher_id_date` (`teacher_id`,`date`),
  KEY `idx_classroom_id_date` (`classroom_id`,`date`),
  KEY `idx_date` (`date`),
  KEY `idx_status` (`status`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课表（排课结果）';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `schedules` WRITE;
/*!40000 ALTER TABLE `schedules` DISABLE KEYS */;
INSERT INTO `schedules` VALUES (1,3,3,3,3,'2026-06-26','09:00:00','11:00:00',15,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (2,4,3,3,4,'2026-06-26','14:00:00','16:00:00',15,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (3,5,4,4,5,'2026-06-26','10:00:00','11:30:00',12,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (4,6,5,5,7,'2026-06-26','09:00:00','12:00:00',20,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (5,3,3,3,3,'2026-06-27','09:00:00','11:00:00',16,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (6,5,4,4,5,'2026-06-27','10:00:00','11:30:00',13,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (7,8,8,5,3,'2026-06-27','14:00:00','16:00:00',18,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (8,3,3,3,3,'2026-06-28','09:00:00','11:00:00',17,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (9,6,5,5,7,'2026-06-28','09:00:00','12:00:00',21,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (10,4,3,3,4,'2026-06-29','09:00:00','11:00:00',16,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (11,5,4,4,5,'2026-06-29','10:00:00','11:30:00',14,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (12,3,3,3,3,'2026-06-30','09:00:00','11:00:00',18,1,1,NULL,NULL,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `schedules` VALUES (14,3,3,3,3,'2026-06-29','14:00:00','16:00:00',NULL,1,NULL,NULL,NULL,'2026-06-29 03:38:46','2026-06-29 03:38:46');
INSERT INTO `schedules` VALUES (15,6,5,5,7,'2026-06-29','16:00:00','18:00:00',NULL,1,NULL,NULL,NULL,'2026-06-29 03:38:46','2026-06-29 03:38:46');
INSERT INTO `schedules` VALUES (17,3,3,3,3,'2026-07-01','09:00:00','11:00:00',NULL,1,NULL,NULL,NULL,'2026-06-29 03:38:55','2026-06-29 03:38:55');
INSERT INTO `schedules` VALUES (18,3,3,3,3,'2026-07-03','09:00:00','11:00:00',NULL,1,NULL,NULL,NULL,'2026-06-29 03:38:55','2026-06-29 03:38:55');
INSERT INTO `schedules` VALUES (19,6,5,5,7,'2026-07-02','14:00:00','16:00:00',NULL,1,NULL,NULL,NULL,'2026-06-29 03:38:55','2026-06-29 03:38:55');
/*!40000 ALTER TABLE `schedules` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `student_classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_classes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `total_hours` decimal(6,1) NOT NULL COMMENT '总课时',
  `used_hours` decimal(6,1) DEFAULT '0.0' COMMENT '已用课时',
  `remain_hours` decimal(6,1) DEFAULT NULL COMMENT '剩余课时',
  `gift_hours` decimal(6,1) DEFAULT '0.0' COMMENT '赠送课时',
  `expire_date` date DEFAULT NULL COMMENT '有效期',
  `status` tinyint DEFAULT '1' COMMENT '1-正常 2-停课 3-结课 4-转班',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_status` (`status`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员班级关系（报名记录）';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `student_classes` WRITE;
/*!40000 ALTER TABLE `student_classes` DISABLE KEYS */;
INSERT INTO `student_classes` VALUES (1,1,1,1,22.0,0.0,22.0,2.0,NULL,1,'2026-06-26 11:01:52','2026-06-26 11:01:52');
INSERT INTO `student_classes` VALUES (2,2,1,2,10.0,0.0,10.0,0.0,NULL,1,'2026-06-26 11:02:38','2026-06-26 11:02:38');
INSERT INTO `student_classes` VALUES (3,3,3,NULL,40.0,12.0,28.0,2.0,NULL,1,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `student_classes` VALUES (4,4,5,NULL,36.0,8.0,28.0,0.0,NULL,1,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `student_classes` VALUES (5,5,6,NULL,48.0,16.0,32.0,4.0,NULL,1,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `student_classes` VALUES (6,6,3,NULL,40.0,4.0,36.0,0.0,NULL,1,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `student_classes` VALUES (7,7,8,NULL,40.0,20.0,20.0,2.0,NULL,1,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `student_classes` VALUES (8,3,6,NULL,20.0,8.0,12.0,0.0,NULL,1,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `student_classes` VALUES (9,9,4,NULL,40.0,0.0,40.0,0.0,NULL,1,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `student_classes` VALUES (10,3,3,NULL,40.0,12.0,28.0,2.0,NULL,1,'2026-06-29 02:57:48','2026-06-29 02:57:48');
INSERT INTO `student_classes` VALUES (11,4,5,NULL,36.0,8.0,28.0,0.0,NULL,1,'2026-06-29 02:57:48','2026-06-29 02:57:48');
INSERT INTO `student_classes` VALUES (12,3,6,NULL,20.0,8.0,12.0,0.0,NULL,1,'2026-06-29 02:57:48','2026-06-29 02:57:48');
INSERT INTO `student_classes` VALUES (13,3,3,NULL,40.0,12.0,28.0,2.0,NULL,1,'2026-06-29 03:11:27','2026-06-29 03:11:27');
INSERT INTO `student_classes` VALUES (14,4,5,NULL,36.0,8.0,28.0,0.0,NULL,1,'2026-06-29 03:11:27','2026-06-29 03:11:27');
INSERT INTO `student_classes` VALUES (15,3,6,NULL,20.0,8.0,12.0,0.0,NULL,1,'2026-06-29 03:11:27','2026-06-29 03:11:27');
INSERT INTO `student_classes` VALUES (16,3,5,39,20.0,0.0,20.0,0.0,NULL,1,'2026-06-29 03:41:05','2026-06-29 03:41:05');
INSERT INTO `student_classes` VALUES (17,5,8,42,20.0,0.0,20.0,0.0,NULL,1,'2026-06-29 05:25:50','2026-06-29 05:25:50');
/*!40000 ALTER TABLE `student_classes` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `student_feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_feedbacks` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_id` bigint NOT NULL COMMENT '课表ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `performance_score` tinyint DEFAULT NULL COMMENT '课堂表现评分 1-5',
  `performance_comment` text COLLATE utf8mb4_unicode_ci COMMENT '表现评语',
  `homework_status` tinyint DEFAULT NULL COMMENT '作业状态 1-已完成 2-部分完成 3-未完成',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_student` (`schedule_id`,`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员个人课堂反馈';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `student_feedbacks` WRITE;
/*!40000 ALTER TABLE `student_feedbacks` DISABLE KEYS */;
INSERT INTO `student_feedbacks` VALUES (1,1,3,3,4,'课堂表现积极，正确回答了3道难题',1,'2026-06-26 11:11:32','2026-06-26 11:11:32');
INSERT INTO `student_feedbacks` VALUES (2,1,6,3,3,'注意力有时不集中，需要多提醒',2,'2026-06-26 11:11:32','2026-06-26 11:11:32');
/*!40000 ALTER TABLE `student_feedbacks` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `student_parents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_parents` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `parent_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '家长姓名',
  `relation` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关系 父亲/母亲/其他',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '家长手机号',
  `is_primary` tinyint DEFAULT '0' COMMENT '是否主联系人 0-否 1-是',
  `wx_openid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信openid',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员家长关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `student_parents` WRITE;
/*!40000 ALTER TABLE `student_parents` DISABLE KEYS */;
INSERT INTO `student_parents` VALUES (1,3,'李建国','父亲','13900020001',1,NULL,'2026-06-26 11:11:32');
INSERT INTO `student_parents` VALUES (2,3,'张丽','母亲','13900020002',0,NULL,'2026-06-26 11:11:32');
INSERT INTO `student_parents` VALUES (3,4,'王大明','父亲','13900020003',1,NULL,'2026-06-26 11:11:32');
INSERT INTO `student_parents` VALUES (4,3,'李建国','父亲','13900020001',1,NULL,'2026-06-29 02:57:48');
INSERT INTO `student_parents` VALUES (5,3,'张丽','母亲','13900020002',0,NULL,'2026-06-29 02:57:48');
INSERT INTO `student_parents` VALUES (6,4,'王大明','父亲','13900020003',1,NULL,'2026-06-29 02:57:48');
INSERT INTO `student_parents` VALUES (7,3,'李建国','父亲','13900020001',1,NULL,'2026-06-29 03:11:27');
INSERT INTO `student_parents` VALUES (8,3,'张丽','母亲','13900020002',0,NULL,'2026-06-29 03:11:27');
INSERT INTO `student_parents` VALUES (9,4,'王大明','父亲','13900020003',1,NULL,'2026-06-29 03:11:27');
/*!40000 ALTER TABLE `student_parents` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学员编号 STU20240001',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学员姓名',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `grade` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '年级 如:初二',
  `school` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '就读学校',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学员手机号',
  `tags` json DEFAULT NULL COMMENT '标签 ["VIP","数学薄弱"]',
  `status` tinyint DEFAULT '0' COMMENT '状态 0-意向 1-在读 2-停课 3-结课 4-流失',
  `campus_id` bigint DEFAULT NULL COMMENT '归属校区',
  `source` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源渠道',
  `consultant_id` bigint DEFAULT NULL COMMENT '顾问ID',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除 0-正常 1-删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'STU20260000A','李四',NULL,1,NULL,'小学一年级','蚌埠第一实验小学','12365498527',NULL,1,1,NULL,NULL,'新增学员数学基础薄弱需要加强','2026-06-26 10:46:37','2026-06-29 02:57:48',0);
INSERT INTO `students` VALUES (2,'STU20260000B','TestStudent',NULL,0,NULL,'Grade9',NULL,'13900001111',NULL,1,1,NULL,NULL,NULL,'2026-06-26 11:01:18','2026-06-29 02:57:48',0);
INSERT INTO `students` VALUES (3,'STU202600001','李明',NULL,1,NULL,'初三','北京市第一中学','13800010001','[\"VIP\", \"数学薄弱\"]',1,NULL,'官网',NULL,'需要重点辅导数学','2026-06-29 02:57:48','2026-06-29 02:57:48',0);
INSERT INTO `students` VALUES (4,'STU202600002','王小红',NULL,2,NULL,'初二','北京市第二中学','13800010002','[\"英语尖子\"]',1,NULL,'转介绍',NULL,'英语成绩优秀','2026-06-29 02:57:48','2026-06-29 02:57:48',0);
INSERT INTO `students` VALUES (5,'STU202600003','赵强',NULL,1,NULL,'高一','北京市第三中学','13800010003','[\"物理竞赛\"]',1,1,'地推',NULL,'目标物理竞赛省一','2026-06-26 11:11:32','2026-06-28 09:57:08',0);
INSERT INTO `students` VALUES (6,'STU202600004','陈小美',NULL,2,NULL,'初三','北京市第四中学','13800010004','[\"VIP\", \"全科\"]',1,1,'官网',NULL,'全科辅导需求','2026-06-26 11:11:32','2026-06-28 09:57:08',0);
INSERT INTO `students` VALUES (7,'STU202600005','刘洋',NULL,1,NULL,'高二','北京市第五中学','13800010005','[\"化学薄弱\"]',1,1,'合作渠道',NULL,'化学需要加强','2026-06-26 11:11:32','2026-06-28 09:57:08',0);
INSERT INTO `students` VALUES (8,'STU202600006','周杰',NULL,1,NULL,'初三','北京师范大学附中','13800010006','[]',0,1,'地推',NULL,'意向学员，待跟进','2026-06-26 11:11:32','2026-06-28 09:57:08',0);
INSERT INTO `students` VALUES (9,'STU202600007','吴芳',NULL,2,NULL,'初一','清华附中','13800010007','[\"VIP\"]',1,1,'官网',NULL,'','2026-06-26 11:11:32','2026-06-28 09:57:08',0);
INSERT INTO `students` VALUES (10,'STU202600008','郑浩',NULL,1,NULL,'高三','人大附中','13800010008','[\"冲刺班\"]',2,1,'转介绍',NULL,'因家庭原因停课','2026-06-26 11:11:32','2026-06-28 09:57:08',0);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `system_configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text COLLATE utf8mb4_unicode_ci COMMENT '配置值',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `system_configs` WRITE;
/*!40000 ALTER TABLE `system_configs` DISABLE KEYS */;
INSERT INTO `system_configs` VALUES (1,'leave_free_hours','24','免费请假需提前小时数','2026-06-26 10:21:50','2026-06-26 10:21:50');
INSERT INTO `system_configs` VALUES (2,'leave_charge_hours','2','收费请假需提前小时数','2026-06-26 10:21:50','2026-06-26 10:21:50');
INSERT INTO `system_configs` VALUES (3,'checkin_qr_expire_minutes','5','签到二维码有效期(分钟)','2026-06-26 10:21:50','2026-06-26 10:21:50');
INSERT INTO `system_configs` VALUES (4,'checkin_distance_meters','500','签到允许最大距离(米)','2026-06-26 10:21:50','2026-06-26 10:21:50');
INSERT INTO `system_configs` VALUES (5,'hour_warning_threshold','5','课时预警阈值','2026-06-26 10:21:50','2026-06-26 10:21:50');
INSERT INTO `system_configs` VALUES (6,'makeup_valid_days','30','补课有效期(天)','2026-06-26 10:21:50','2026-06-26 10:21:50');
/*!40000 ALTER TABLE `system_configs` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师编号',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `gender` tinyint DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
  `subjects` json DEFAULT NULL COMMENT '可授科目 ["数学","物理"]',
  `introduction` text COLLATE utf8mb4_unicode_ci COMMENT '教师简介',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `status` tinyint DEFAULT '1' COMMENT '状态 0-离职 1-在职 2-休假',
  `campus_id` bigint DEFAULT NULL COMMENT '归属校区',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_no` (`teacher_no`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES (1,'TCH20260000A','张三',NULL,'15212372825',1,'[\"语文\", \"数学\", \"英语\"]','大牛','2026-06-10',1,1,'2026-06-26 10:45:22','2026-06-29 04:13:17');
INSERT INTO `teachers` VALUES (2,'TCH20260000B','TeacherZhang',NULL,'13900002222',0,'[\"Math\"]',NULL,NULL,1,1,'2026-06-26 11:01:18','2026-06-29 04:13:17');
INSERT INTO `teachers` VALUES (3,'TCH202600001','张老师',NULL,'13900000001',1,'[\"数学\", \"物理\"]','10年教龄，曾任重点中学数学教研组长','2020-03-01',1,NULL,'2026-06-29 04:13:17','2026-06-29 04:13:17');
INSERT INTO `teachers` VALUES (4,'TCH202600002','李老师',NULL,'13900000002',2,'[\"英语\", \"语文\"]','海外留学背景，擅长口语教学','2021-09-01',1,NULL,'2026-06-29 04:13:17','2026-06-29 04:13:17');
INSERT INTO `teachers` VALUES (5,'TCH202600003','王老师',NULL,'13900000003',1,'[\"化学\", \"生物\"]','985博士，5年教学经验','2022-06-01',1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
INSERT INTO `teachers` VALUES (6,'TCH202600004','赵老师',NULL,'13900000004',2,'[\"钢琴\", \"音乐\"]','音乐学院硕士，擅长少儿钢琴启蒙','2023-01-01',1,1,'2026-06-26 11:11:32','2026-06-28 09:57:08');
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (11,1,1,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (12,2,2,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (13,3,3,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (14,4,3,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (15,5,3,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (16,6,4,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (17,7,4,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (18,8,5,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (19,9,5,'2026-06-28 09:39:30');
INSERT INTO `user_roles` VALUES (20,10,5,'2026-06-28 09:39:30');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(BCrypt)',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT '1' COMMENT '0-禁用 1-正常',
  `last_login_time` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'boss','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','王老板','13800000000',NULL,NULL,1,'2026-06-29 05:14:46','2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (2,'principal','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','Updated Name','13800000001',NULL,NULL,1,'2026-06-29 05:33:00','2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (3,'teacher1','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','张老师','13900000001',NULL,NULL,1,'2026-06-29 03:32:59','2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (4,'teacher2','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','李老师','13900000002',NULL,NULL,1,NULL,'2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (5,'teacher3','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','王老师','13900000003',NULL,NULL,1,NULL,'2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (6,'parent1','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','李爸爸','13900020001',NULL,NULL,1,'2026-06-29 03:33:48','2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (7,'parent2','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','王妈妈','13900020002',NULL,NULL,1,NULL,'2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (8,'student1','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','李明','13800010001',NULL,NULL,1,'2026-06-29 03:35:05','2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (9,'student2','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','王小红','13800010002',NULL,NULL,1,NULL,'2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (10,'student3','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','赵强','13800010003',NULL,NULL,1,NULL,'2026-06-28 09:39:30','2026-06-28 09:39:30');
INSERT INTO `users` VALUES (11,'testuser','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','Test User','13900009999',NULL,NULL,1,NULL,'2026-06-29 04:19:31','2026-06-29 04:19:31');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

