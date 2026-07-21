-- MySQL dump 10.13  Distrib 9.7.0, for Win64 (x86_64)
--
-- Host: localhost    Database: edu_system
-- ------------------------------------------------------
-- Server version	9.7.0

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

/*!40000 DROP DATABASE IF EXISTS `edu_system`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `edu_system` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `edu_system`;

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
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'用户初始密码','sys.user.initPassword','123456','Y','','2026-07-10 21:45:03','','2026-07-10 21:45:03','用户新增/重置的初始密码',0),(2,'系统名称','sys.system.name','教育店铺管理系统','Y','','2026-07-10 21:45:03','','2026-07-10 21:45:03','系统名称',0),(3,'系统描述','sys.system.description','在线教育课程展示与交易平台','Y','','2026-07-10 21:45:03','','2026-07-10 21:45:03','系统描述',0),(4,'账号最大重试次数','sys.account.maxRetryCount','5','Y','','2026-07-10 21:45:03','','2026-07-10 21:45:03','登录失败锁定阈值',0),(5,'账号锁定时间(分)','sys.account.lockTime','10','Y','','2026-07-10 21:45:03','','2026-07-10 21:45:03','登录失败锁定时长',0);
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
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_type_code` (`type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,'sys_user_sex','男','1',1,NULL,NULL,0,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(2,'sys_user_sex','女','2',2,NULL,NULL,0,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(3,'sys_user_sex','未知','0',3,NULL,NULL,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(4,'sys_normal_disable','正常','1',1,NULL,NULL,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(5,'sys_normal_disable','停用','0',2,NULL,NULL,0,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(6,'sys_yes_no','是','Y',1,NULL,NULL,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(7,'sys_yes_no','否','N',2,NULL,NULL,0,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(8,'sys_notice_type','通知','1',1,NULL,NULL,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(9,'sys_notice_type','公告','2',2,NULL,NULL,0,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(10,'sys_menu_type','目录','M',1,NULL,NULL,0,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(11,'sys_menu_type','菜单','C',2,NULL,NULL,0,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0),(12,'sys_menu_type','按钮','F',3,NULL,NULL,0,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',NULL,0);
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
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex',1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03','用户性别列表',0),(2,'系统开关','sys_normal_disable',1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03','系统开关列表',0),(3,'系统是否','sys_yes_no',1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03','系统是否列表',0),(4,'通知类型','sys_notice_type',1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03','通知类型列表',0),(5,'菜单类型','sys_menu_type',1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03','菜单类型列表',0);
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
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=377 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,0,'系统管理','M','/system',NULL,'System',NULL,1,0,'system',NULL,1,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(2,0,'学员管理','M','/student',NULL,'Student',NULL,1,0,'peoples',NULL,2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(3,0,'教务管理','M','/course',NULL,'Course',NULL,1,0,'education',NULL,3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(4,0,'教务教学','M','/teaching',NULL,'Teaching',NULL,1,0,'guide',NULL,4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(5,0,'财务管理','M','/finance',NULL,'Finance',NULL,1,0,'money',NULL,5,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(6,0,'考勤管理','M','/attendance',NULL,'Attendance',NULL,1,0,'time',NULL,6,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(7,0,'积分商城','M','/points',NULL,'Points',NULL,1,0,'star',NULL,7,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(8,0,'教师端','M','/teacher',NULL,'Teacher',NULL,1,0,'user',NULL,8,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(9,0,'工作台','C','/dashboard','dashboard/index','Dashboard',NULL,1,0,'dashboard','dashboard:dashboard:view',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(100,1,'用户管理','C','user','system/user/index','SysUser',NULL,1,0,'user','system:user:list',1,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(101,1,'角色管理','C','role','system/role/index','SysRole',NULL,1,0,'peoples','system:role:list',2,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(102,1,'菜单管理','C','menu','system/menu/index','SysMenu',NULL,1,0,'tree-table','system:menu:list',3,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(103,1,'字典管理','C','dict','system/dict/index','SysDict',NULL,1,0,'dict','system:dict:list',4,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(104,1,'参数设置','C','config','system/config/index','SysConfig',NULL,1,0,'edit','system:config:list',5,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(105,1,'通知公告','C','notice','system/notice/index','SysNotice',NULL,1,0,'message','system:notice:list',6,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(106,1,'日志管理','C','log','system/log/index','SysLog',NULL,1,0,'log','system:log:list',7,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(107,100,'用户查询','F','',NULL,'',NULL,1,0,'','system:user:query',1,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(108,100,'用户新增','F','',NULL,'',NULL,1,0,'','system:user:add',2,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(109,100,'用户修改','F','',NULL,'',NULL,1,0,'','system:user:edit',3,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(110,100,'用户删除','F','',NULL,'',NULL,1,0,'','system:user:delete',4,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(111,100,'用户导出','F','',NULL,'',NULL,1,0,'','system:user:export',5,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(112,100,'用户导入','F','',NULL,'',NULL,1,0,'','system:user:import',6,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(113,100,'重置密码','F','',NULL,'',NULL,1,0,'','system:user:resetPwd',7,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(114,101,'角色查询','F','',NULL,'',NULL,1,0,'','system:role:query',1,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(115,101,'角色新增','F','',NULL,'',NULL,1,0,'','system:role:add',2,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(116,101,'角色修改','F','',NULL,'',NULL,1,0,'','system:role:edit',3,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(117,101,'角色删除','F','',NULL,'',NULL,1,0,'','system:role:delete',4,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(118,102,'菜单查询','F','',NULL,'',NULL,1,0,'','system:menu:query',1,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(119,102,'菜单新增','F','',NULL,'',NULL,1,0,'','system:menu:add',2,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(120,102,'菜单修改','F','',NULL,'',NULL,1,0,'','system:menu:edit',3,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(121,102,'菜单删除','F','',NULL,'',NULL,1,0,'','system:menu:delete',4,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(122,103,'字典查询','F','',NULL,'',NULL,1,0,'','system:dict:query',1,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(123,103,'字典新增','F','',NULL,'',NULL,1,0,'','system:dict:add',2,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(124,103,'字典修改','F','',NULL,'',NULL,1,0,'','system:dict:edit',3,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(125,103,'字典删除','F','',NULL,'',NULL,1,0,'','system:dict:delete',4,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(126,104,'参数查询','F','',NULL,'',NULL,1,0,'','system:config:query',1,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(127,104,'参数新增','F','',NULL,'',NULL,1,0,'','system:config:add',2,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(128,104,'参数修改','F','',NULL,'',NULL,1,0,'','system:config:edit',3,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(129,104,'参数删除','F','',NULL,'',NULL,1,0,'','system:config:delete',4,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(130,105,'公告查询','F','',NULL,'',NULL,1,0,'','system:notice:query',1,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(131,105,'公告新增','F','',NULL,'',NULL,1,0,'','system:notice:add',2,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(132,105,'公告修改','F','',NULL,'',NULL,1,0,'','system:notice:edit',3,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(133,105,'公告删除','F','',NULL,'',NULL,1,0,'','system:notice:delete',4,1,1,'','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(200,2,'意向学员','C','prospect','student/prospect/index','StudentProspect',NULL,1,0,'#','student:prospect:list',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(201,2,'在读学员','C','enrolled','student/enrolled/index','StudentEnrolled',NULL,1,0,'#','student:enrolled:list',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(202,2,'结业学员','C','completed','student/completed/index','StudentCompleted',NULL,1,0,'#','student:completed:list',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(203,2,'学员学时','C','hours','student/hours/index','StudentHours',NULL,1,0,'#','student:hours:list',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(204,2,'学员家长','C','parents','student/parents/index','StudentParents',NULL,1,0,'#','student:parent:list',5,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(210,3,'科目管理','C','subject','course/subject/index','CourseSubject',NULL,1,0,'#','course:subject:list',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(211,3,'教室管理','C','classroom','course/classroom/index','CourseClassroom',NULL,1,0,'#','course:classroom:list',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(212,3,'课程管理','C','course','course/course/index','CourseManage',NULL,1,0,'#','course:course:list',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(213,3,'班级管理','C','class','course/class/index','ClassManage',NULL,1,0,'#','class:class:list',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(214,3,'排课方案','C','schedulePlan','course/schedulePlan/index','SchedulePlan',NULL,1,0,'#','schedule:plan:list',5,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(215,3,'排课日历','C','schedule','course/schedule/index','ScheduleCalendar',NULL,1,0,'#','schedule:calendar:list',6,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(216,3,'课件资源','C','resource','course/resource/index','CourseResource',NULL,1,0,'#','course:resource:list',7,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(220,4,'考试管理','C','exam','teaching/exam/index','TeachingExam',NULL,1,0,'#','teaching:exam:list',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(221,4,'作业管理','C','homework','teaching/homework/index','TeachingHomework',NULL,1,0,'#','teaching:homework:list',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(222,4,'教评学','C','evaluation','teaching/evaluation/index','TeachingEvaluation',NULL,1,0,'#','teaching:evaluation:list',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(223,4,'学评教','C','teacherEvaluation','teaching/teacherEvaluation/index','TeachingTeacherEvaluation',NULL,1,0,'#','teaching:teacher-evaluation:list',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(224,4,'教师管理','C','teacher','teaching/teacher/index','TeachingTeacher',NULL,1,0,'#','teaching:teacher:list',5,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(230,5,'学费管理','C','tuition','finance/tuition/index','FinanceTuition',NULL,1,0,'#','tuition:fee:list',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(231,5,'薪资管理','C','payroll','finance/payroll/index','FinancePayroll',NULL,1,0,'#','payroll:payroll:list',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(232,5,'教学统计','C','teachingStats','finance/teachingStats/index','FinanceTeachingStats',NULL,1,0,'#','payroll:pay-rate:list',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(240,6,'签到记录','C','signin','attendance/signin/index','AttendanceSignin',NULL,1,0,'#','attendance:signin:list',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(241,6,'学生请假','C','leave/student','attendance/leave/student/index','AttendanceLeaveStudent',NULL,1,0,'#','attendance:leave:student:list',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(242,6,'教师请假','C','leave/teacher','attendance/leave/teacher/index','AttendanceLeaveTeacher',NULL,1,0,'#','attendance:leave:teacher:list',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(250,7,'礼品管理','C','gift','points/gift/index','PointsGift',NULL,1,0,'#','points:gift:list',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(251,7,'兑换记录','C','redemption','points/redemption/index','PointsRedemption',NULL,1,0,'#','points:redemption:list',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(260,8,'工作台','C','dashboard','teacher/dashboard/index','TeacherDashboard',NULL,1,0,'#','teacher:dashboard:view',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(261,8,'课后作业','C','homework','teacher/homework/index','TeacherHomework',NULL,1,0,'#','teaching:homework:list',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(262,8,'课件共享','C','resource','teacher/resource/index','TeacherResource',NULL,1,0,'#','course:resource:list',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(263,8,'我的课表','C','my-schedule','teacher/my-schedule/index','TeacherMySchedule',NULL,1,0,'#','schedule:calendar:list',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(264,200,'意向学员查询','F','',NULL,'',NULL,1,0,'','student:prospect:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(265,200,'意向学员新增','F','',NULL,'',NULL,1,0,'','student:prospect:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(266,200,'意向学员修改','F','',NULL,'',NULL,1,0,'','student:prospect:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(267,200,'意向学员删除','F','',NULL,'',NULL,1,0,'','student:prospect:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(268,200,'意向学员转化','F','',NULL,'',NULL,1,0,'','student:prospect:convert',5,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(269,200,'跟进记录管理','F','',NULL,'',NULL,1,0,'','student:prospect:followup',6,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(270,201,'在读学员查询','F','',NULL,'',NULL,1,0,'','student:enrolled:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(271,201,'在读学员新增','F','',NULL,'',NULL,1,0,'','student:enrolled:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(272,201,'在读学员修改','F','',NULL,'',NULL,1,0,'','student:enrolled:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(273,201,'在读学员删除','F','',NULL,'',NULL,1,0,'','student:enrolled:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(274,202,'结业学员查询','F','',NULL,'',NULL,1,0,'','student:completed:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(275,203,'学员学时列表','F','',NULL,'',NULL,1,0,'','student:hours:list',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(276,203,'学员学时查询','F','',NULL,'',NULL,1,0,'','student:hours:query',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(277,203,'学员学时新增','F','',NULL,'',NULL,1,0,'','student:hours:add',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(278,204,'学员家长查询','F','',NULL,'',NULL,1,0,'','student:parent:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(279,204,'学员家长新增','F','',NULL,'',NULL,1,0,'','student:parent:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(280,204,'学员家长修改','F','',NULL,'',NULL,1,0,'','student:parent:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(281,204,'学员家长删除','F','',NULL,'',NULL,1,0,'','student:parent:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(282,210,'科目查询','F','',NULL,'',NULL,1,0,'','course:subject:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(283,210,'科目新增','F','',NULL,'',NULL,1,0,'','course:subject:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(284,210,'科目修改','F','',NULL,'',NULL,1,0,'','course:subject:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(285,210,'科目删除','F','',NULL,'',NULL,1,0,'','course:subject:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(286,211,'教室查询','F','',NULL,'',NULL,1,0,'','course:classroom:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(287,211,'教室新增','F','',NULL,'',NULL,1,0,'','course:classroom:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(288,211,'教室修改','F','',NULL,'',NULL,1,0,'','course:classroom:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(289,211,'教室删除','F','',NULL,'',NULL,1,0,'','course:classroom:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(290,212,'课程查询','F','',NULL,'',NULL,1,0,'','course:course:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(291,212,'课程新增','F','',NULL,'',NULL,1,0,'','course:course:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(292,212,'课程修改','F','',NULL,'',NULL,1,0,'','course:course:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(293,212,'课程删除','F','',NULL,'',NULL,1,0,'','course:course:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(294,213,'班级查询','F','',NULL,'',NULL,1,0,'','class:class:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(295,213,'班级新增','F','',NULL,'',NULL,1,0,'','class:class:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(296,213,'班级修改','F','',NULL,'',NULL,1,0,'','class:class:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(297,213,'班级删除','F','',NULL,'',NULL,1,0,'','class:class:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(298,213,'班级学员管理','F','',NULL,'',NULL,1,0,'','class:student:add',5,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(299,213,'班级学员列表','F','',NULL,'',NULL,1,0,'','class:student:list',6,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(300,213,'班级学员修改','F','',NULL,'',NULL,1,0,'','class:student:edit',7,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(301,213,'班级学员删除','F','',NULL,'',NULL,1,0,'','class:student:delete',8,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(302,214,'排课方案查询','F','',NULL,'',NULL,1,0,'','schedule:plan:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(303,214,'排课方案新增','F','',NULL,'',NULL,1,0,'','schedule:plan:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(304,214,'排课方案修改','F','',NULL,'',NULL,1,0,'','schedule:plan:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(305,214,'排课方案删除','F','',NULL,'',NULL,1,0,'','schedule:plan:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(306,215,'排课日历查询','F','',NULL,'',NULL,1,0,'','schedule:calendar:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(307,215,'排课取消','F','',NULL,'',NULL,1,0,'','schedule:cancel',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(308,215,'排课调课','F','',NULL,'',NULL,1,0,'','schedule:reschedule',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(309,215,'排课消课','F','',NULL,'',NULL,1,0,'','schedule:consume',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(310,216,'课件资源查询','F','',NULL,'',NULL,1,0,'','course:resource:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(311,216,'课件资源新增','F','',NULL,'',NULL,1,0,'','course:resource:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(312,216,'课件资源修改','F','',NULL,'',NULL,1,0,'','course:resource:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(313,216,'课件资源删除','F','',NULL,'',NULL,1,0,'','course:resource:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(314,220,'考试查询','F','',NULL,'',NULL,1,0,'','teaching:exam:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(315,220,'考试新增','F','',NULL,'',NULL,1,0,'','teaching:exam:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(316,220,'考试修改','F','',NULL,'',NULL,1,0,'','teaching:exam:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(317,220,'考试删除','F','',NULL,'',NULL,1,0,'','teaching:exam:remove',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(318,221,'作业查询','F','',NULL,'',NULL,1,0,'','teaching:homework:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(319,221,'作业新增','F','',NULL,'',NULL,1,0,'','teaching:homework:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(320,221,'作业修改','F','',NULL,'',NULL,1,0,'','teaching:homework:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(321,221,'作业删除','F','',NULL,'',NULL,1,0,'','teaching:homework:remove',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(322,222,'教评学查询','F','',NULL,'',NULL,1,0,'','teaching:evaluation:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(323,222,'教评学新增','F','',NULL,'',NULL,1,0,'','teaching:evaluation:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(324,222,'教评学删除','F','',NULL,'',NULL,1,0,'','teaching:evaluation:remove',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(325,223,'学评教查询','F','',NULL,'',NULL,1,0,'','teaching:teacher-evaluation:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(326,223,'学评教导出','F','',NULL,'',NULL,1,0,'','teaching:teacher-evaluation:export',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(327,224,'教师查询','F','',NULL,'',NULL,1,0,'','teaching:teacher:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(328,224,'教师新增','F','',NULL,'',NULL,1,0,'','teaching:teacher:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(329,224,'教师修改','F','',NULL,'',NULL,1,0,'','teaching:teacher:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(330,224,'教师删除','F','',NULL,'',NULL,1,0,'','teaching:teacher:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(331,230,'学费查询','F','',NULL,'',NULL,1,0,'','tuition:fee:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(332,230,'学费新增','F','',NULL,'',NULL,1,0,'','tuition:fee:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(333,230,'学费修改','F','',NULL,'',NULL,1,0,'','tuition:fee:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(334,230,'学费删除','F','',NULL,'',NULL,1,0,'','tuition:fee:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(335,230,'退费列表','F','',NULL,'',NULL,1,0,'','tuition:refund:list',5,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(336,230,'退费申请','F','',NULL,'',NULL,1,0,'','tuition:refund:apply',6,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(337,230,'退费审批','F','',NULL,'',NULL,1,0,'','tuition:refund:approve',7,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(338,230,'退费删除','F','',NULL,'',NULL,1,0,'','tuition:refund:delete',8,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(339,230,'赠课列表','F','',NULL,'',NULL,1,0,'','tuition:gift:list',9,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(340,230,'赠课新增','F','',NULL,'',NULL,1,0,'','tuition:gift:add',10,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(341,230,'赠课删除','F','',NULL,'',NULL,1,0,'','tuition:gift:delete',11,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(342,230,'报名新增','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:add',12,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(343,230,'报名修改','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:edit',13,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(344,230,'报名取消','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:cancel',14,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(345,230,'报名列表','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:list',15,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(346,230,'报名查询','F','',NULL,'',NULL,1,0,'','enrollment:enrollment:query',16,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(347,231,'薪资查询','F','',NULL,'',NULL,1,0,'','payroll:payroll:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(348,231,'薪资新增','F','',NULL,'',NULL,1,0,'','payroll:payroll:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(349,231,'薪资确认','F','',NULL,'',NULL,1,0,'','payroll:payroll:confirm',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(350,231,'薪资发放','F','',NULL,'',NULL,1,0,'','payroll:payroll:pay',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(351,231,'薪资删除','F','',NULL,'',NULL,1,0,'','payroll:payroll:delete',5,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(352,7,'积分账户查询','F','',NULL,'',NULL,1,0,'','points:account:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(353,7,'积分账户调整','F','',NULL,'',NULL,1,0,'','points:account:adjust',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(354,232,'课时费率查询','F','',NULL,'',NULL,1,0,'','payroll:pay-rate:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(355,232,'课时费率新增','F','',NULL,'',NULL,1,0,'','payroll:pay-rate:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(356,232,'课时费率修改','F','',NULL,'',NULL,1,0,'','payroll:pay-rate:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(357,232,'课时费率删除','F','',NULL,'',NULL,1,0,'','payroll:pay-rate:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(358,240,'签到记录查询','F','',NULL,'',NULL,1,0,'','attendance:signin:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(359,240,'签到记录新增','F','',NULL,'',NULL,1,0,'','attendance:signin:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(360,241,'学生请假查询','F','',NULL,'',NULL,1,0,'','attendance:leave:student:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(361,241,'学生请假新增','F','',NULL,'',NULL,1,0,'','attendance:leave:student:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(362,241,'学生请假审批','F','',NULL,'',NULL,1,0,'','attendance:leave:student:approve',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(363,242,'教师请假查询','F','',NULL,'',NULL,1,0,'','attendance:leave:teacher:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(364,242,'教师请假新增','F','',NULL,'',NULL,1,0,'','attendance:leave:teacher:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(365,242,'教师请假审批','F','',NULL,'',NULL,1,0,'','attendance:leave:teacher:approve',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(366,242,'教师请假冲突处理','F','',NULL,'',NULL,1,0,'','attendance:leave:teacher:resolve',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(367,250,'礼品查询','F','',NULL,'',NULL,1,0,'','points:gift:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(368,250,'礼品新增','F','',NULL,'',NULL,1,0,'','points:gift:add',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(369,250,'礼品修改','F','',NULL,'',NULL,1,0,'','points:gift:edit',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(370,250,'礼品删除','F','',NULL,'',NULL,1,0,'','points:gift:delete',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(371,251,'兑换记录查询','F','',NULL,'',NULL,1,0,'','points:redemption:query',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(372,251,'兑换记录导出','F','',NULL,'',NULL,1,0,'','points:redemption:export',2,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(373,251,'兑换发放','F','',NULL,'',NULL,1,0,'','points:redemption:claim',3,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(374,251,'兑换取消','F','',NULL,'',NULL,1,0,'','points:redemption:cancel',4,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(375,9,'工作台查看','F','',NULL,'',NULL,1,0,'','dashboard:dashboard:view',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0),(376,260,'教师工作台查看','F','',NULL,'',NULL,1,0,'','teacher:dashboard:view',1,1,1,'','2026-07-21 14:41:15','','2026-07-21 14:41:15',0);
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
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
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
  `dept_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称(冗余文本，保留兼容)',
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (1,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous',NULL,'/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjZiZTY3NDRjLTkxMmMtNGYwZS04NzcyLTcwYzM4OTg4YWQyMSIsImlhdCI6MTc4MzY5MTIxMiwiZXhwIjoxNzgzNjk4NDEyfQ.9NUy1aV6neO9MK1rqrfhOI3WTyZqCtFLvwBqzxn2nPF-QyvCEDJoDcgrbYzS_YC8AwOMiqscyWw2UuLQZZtBKw\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI3ZjBiYWRjZi02NzlkLTQzNDAtYWM0My1kMjY2Njk1MDlkNTQiLCJpYXQiOjE3ODM2OTEyMTIsImV4cCI6MTc4NDI5NjAxMn0.QZSlE-P1_K_cQ4BzYhq0FFs3RCSWdZQ8BiVFqdy1Bw_xsBK30YoVj_nCtklw1E6k7nWakJlj9jK04Mx8xpCUdg\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"system:log:list\",\"system:menu:query\",\"system:menu:list\",\"system:config:list\",\"system:menu:add\",\"system:role:list\",\"system:user:delete\",\"system:user:import\",\"system:dict:edit\",\"system:user:edit\",\"system:user:query\",\"system:role:edit\",\"system:user:add\",\"system:user:export\",\"system:menu:delete\",\"system:config:query\",\"system:role:add\",\"system:dict:add\",\"system:dict:list\",\"system:role:delete\",\"system:user:list\",\"system:notice:delete\",\"system:notice:list\",\"system:dict:query\",\"system:notice:add\",\"system:notice:query\",\"system:notice:edit\",\"system:config:add\",\"system:config:delete\",\"system:dict:delete\",\"system:role:query\",\"system:config:edit\",\"system:menu:edit\"],\"roles\":[\"admin\"]},\"timestamp\":1783691212648}',1,NULL,863,'2026-07-10 21:46:52'),(2,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous',NULL,'/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjM2YzdlZmRjLWZlOGUtNGIwYS1iNWM0LWMwMDM3MjU4YTkwNyIsImlhdCI6MTc4MzY5MTI1MSwiZXhwIjoxNzgzNjk4NDUxfQ.zBCDgJkYeJuAmqAwrUNF_gatl5CEQnEVisR1FI-FB2DQoMfC3wysB_Z095bJ7X5fiPFwRNbxbM1I9jTRiJPa3Q\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI2OWVmOWZjYy0yNTFmLTQ1MmYtOWM3ZC1lZDczMWUwZWM5MDUiLCJpYXQiOjE3ODM2OTEyNTEsImV4cCI6MTc4NDI5NjA1MX0.yeZDum3TnyU95rLKDOXPG3qSCO4OBR9vekv3fDcE6ejRYoTg1QobfBMDQSlUeVtChnNqfjQwc2kk82FhC6UfVg\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"system:log:list\",\"system:menu:query\",\"system:menu:list\",\"system:config:list\",\"system:menu:add\",\"system:role:list\",\"system:user:delete\",\"system:user:import\",\"system:dict:edit\",\"system:user:edit\",\"system:user:query\",\"system:role:edit\",\"system:user:add\",\"system:user:export\",\"system:menu:delete\",\"system:config:query\",\"system:role:add\",\"system:dict:add\",\"system:dict:list\",\"system:role:delete\",\"system:user:list\",\"system:notice:delete\",\"system:notice:list\",\"system:dict:query\",\"system:notice:add\",\"system:notice:query\",\"system:notice:edit\",\"system:config:add\",\"system:config:delete\",\"system:dict:delete\",\"system:role:query\",\"system:config:edit\",\"system:menu:edit\"],\"roles\":[\"admin\"]},\"timestamp\":1783691251941}',1,NULL,76,'2026-07-10 21:47:32'),(3,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous',NULL,'/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6Ijc0NDQ3ZmNiLTFmNjktNGFmYi1hYTA1LTQ5NmMyYWE3MzE4OSIsImlhdCI6MTc4NDYxNjMyNywiZXhwIjoxNzg0NjIzNTI3fQ.FoQw0kb7jFTQSkmVYYv_DoshvJlyJEjYYTgrWDvMVz9D4XSY_4vzBPSauX0E4JzxHoe6MpY25I6br2P_rmVZDA\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI5YzNjMmQzYS1kZjRkLTRjOTctOTA1Ny02M2JjMTljNzljYzciLCJpYXQiOjE3ODQ2MTYzMjcsImV4cCI6MTc4NTIyMTEyN30.P89LA-vpO0OuZ3HtW6mj0agivWCR6SUunl7W35cGbwg12nSKDMihAnEhZZLV7Y8EmJoKsXh_6tGngXNi5HoYHQ\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"student:parent:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784616327598}',1,NULL,294,'2026-07-21 14:45:27'),(4,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous',NULL,'/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjFiNDg3ZWY4LTQwZTYtNDk2MS1iOWZjLTFjYWY0ZTRlM2EyZiIsImlhdCI6MTc4NDYxNjMzNiwiZXhwIjoxNzg0NjIzNTM2fQ.Jj4e4UQm_S2mtXfl6bIWmiDaD4ibcMLQOMByiee7j_dfIQzBzsOpz_Aa1OA3KfzYfP3CMJ47xC4uGzJUlEz-_g\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI5YWExNzhiOS02NWU2LTQ5NGQtODNhMy1mZTc4OTQyODlmODgiLCJpYXQiOjE3ODQ2MTYzMzYsImV4cCI6MTc4NTIyMTEzNn0.9U9ua_NlSeWb3BTFThYj-7nCCyYawYn-4m-3oT-ylhdAX0LH2DczD7MMR6C_I6-t2TuXRSl2-mZpuV6aMnSk3A\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"student:parent:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784616336957}',1,NULL,57,'2026-07-21 14:45:37'),(5,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous',NULL,'/api/v1/auth/login','0:0:0:0:0:0:0:1','','{\"username\":\"teacher1\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwidXNlcklkIjoyLCJ1c2VybmFtZSI6InRlYWNoZXIxIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6ImRiZTQ5NWVlLWY1ODgtNDAxZC04ZDgyLWI1MzVlYjUyNTY0NCIsImlhdCI6MTc4NDYxNjM1NSwiZXhwIjoxNzg0NjIzNTU1fQ.OLhac3ZVx0Ar2vNCepkTcA68l5sWkvn65fo75hCdAIxHaQvHsDZEJrraI9vQ2nvtVkjkuJswlz0kjfgOx8ULEw\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwidXNlcklkIjoyLCJ1c2VybmFtZSI6InRlYWNoZXIxIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI3OGNmODg1Mi04MmIxLTRjMDgtYjAyNS1hNWQ5OTNiZDUwODAiLCJpYXQiOjE3ODQ2MTYzNTUsImV4cCI6MTc4NTIyMTE1NX0.DVhDgUFqR2nsPun9h5iEzkbnJglYYcT6_0nqCH-COTdbYLWTYfIuXqcp_3QVeUHOaTHQza9D7YFESs3AoxH6cg\",\"userId\":2,\"username\":\"teacher1\",\"realName\":\"寮犺?甯\",\"avatar\":\"\",\"permissions\":[\"teaching:homework:list\",\"teacher:dashboard:view\",\"course:resource:edit\",\"teaching:homework:add\",\"dashboard:dashboard:view\",\"schedule:calendar:query\",\"teaching:homework:remove\",\"course:resource:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"schedule:calendar:list\",\"course:resource:query\",\"course:resource:list\"],\"roles\":[\"teacher\"]},\"timestamp\":1784616355988}',1,NULL,54,'2026-07-21 14:45:56'),(6,'用户登录','OTHER','com.education.store.module.system.controller.AuthController.login','POST','anonymous',NULL,'/api/v1/auth/login','127.0.0.1','','{\"username\":\"admin\",\"password\":\"admin123\"};','{\"code\":200,\"message\":\"操作成功\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6ImFjY2VzcyIsImp0aSI6ImViZTFmY2UzLTgxMTUtNDFmNi1hYWJiLWRmMDc4ZWE0NDE1YSIsImlhdCI6MTc4NDYyOTk0NSwiZXhwIjoxNzg0NjM3MTQ1fQ.sWJeuRB0MqjekzXBzT6lM4qzXHLPfYqg7WDgaDu3iskPKAHmZm3rqikea0I-d1zAujv4jsaRL0ddhYC030toOQ\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidHlwZSI6InJlZnJlc2giLCJqdGkiOiI3MzZjZjkwNS03NTE2LTQ2ZjMtOTY3OS00ZjQwZjdmNTZhNDMiLCJpYXQiOjE3ODQ2Mjk5NDUsImV4cCI6MTc4NTIzNDc0NX0.PCYivYetZo1NFW8KX_FpfGyRNlO-9TB3IRVjqm0G8DJpk_01dnR4Y5u-zu0dnqkHRLQa0SmsO9cem3Ws9KBOEA\",\"userId\":1,\"username\":\"admin\",\"realName\":\"超级管理员\",\"avatar\":\"\",\"permissions\":[\"system:user:resetPwd\",\"payroll:payroll:list\",\"payroll:payroll:add\",\"student:hours:query\",\"course:subject:delete\",\"course:classroom:delete\",\"course:classroom:query\",\"system:menu:query\",\"system:menu:list\",\"payroll:payroll:pay\",\"tuition:fee:edit\",\"student:hours:add\",\"tuition:gift:list\",\"attendance:signin:add\",\"system:dict:edit\",\"teaching:teacher-evaluation:export\",\"system:user:query\",\"course:classroom:edit\",\"class:class:edit\",\"system:user:export\",\"student:enrolled:edit\",\"teaching:exam:list\",\"attendance:leave:teacher:approve\",\"points:redemption:export\",\"course:resource:query\",\"teaching:teacher-evaluation:query\",\"points:gift:query\",\"class:class:delete\",\"course:resource:list\",\"attendance:leave:teacher:resolve\",\"system:role:delete\",\"teaching:homework:list\",\"points:account:adjust\",\"system:dict:query\",\"course:course:edit\",\"points:account:query\",\"student:parent:edit\",\"student:parent:add\",\"system:notice:edit\",\"enrollment:enrollment:edit\",\"attendance:leave:student:query\",\"points:gift:delete\",\"teaching:teacher:list\",\"student:prospect:query\",\"class:student:list\",\"course:subject:query\",\"enrollment:enrollment:add\",\"class:class:query\",\"points:gift:edit\",\"attendance:leave:student:list\",\"schedule:plan:edit\",\"teaching:evaluation:list\",\"system:config:edit\",\"teaching:teacher-evaluation:list\",\"teaching:evaluation:add\",\"payroll:pay-rate:list\",\"system:log:list\",\"system:config:list\",\"system:menu:add\",\"system:user:edit\",\"student:prospect:add\",\"tuition:refund:list\",\"system:role:edit\",\"schedule:reschedule\",\"system:config:query\",\"course:subject:list\",\"schedule:plan:delete\",\"payroll:payroll:delete\",\"student:parent:query\",\"schedule:plan:add\",\"payroll:pay-rate:query\",\"system:role:add\",\"student:hours:list\",\"system:dict:add\",\"schedule:cancel\",\"enrollment:enrollment:cancel\",\"attendance:leave:teacher:add\",\"attendance:leave:teacher:list\",\"course:course:query\",\"teaching:exam:remove\",\"system:notice:delete\",\"course:classroom:add\",\"teaching:exam:add\",\"teaching:homework:add\",\"system:config:add\",\"student:prospect:convert\",\"course:subject:add\",\"student:prospect:delete\",\"points:redemption:query\",\"class:class:list\",\"class:class:add\",\"tuition:refund:apply\",\"student:enrolled:list\",\"student:completed:query\",\"payroll:pay-rate:add\",\"class:student:add\",\"teaching:evaluation:query\",\"course:classroom:list\",\"student:enrolled:add\",\"schedule:plan:list\",\"schedule:calendar:query\",\"tuition:gift:delete\",\"system:user:add\",\"teaching:homework:remove\",\"schedule:plan:query\",\"course:resource:delete\",\"tuition:gift:add\",\"schedule:calendar:list\",\"tuition:fee:add\",\"system:dict:list\",\"student:parent:list\",\"teaching:teacher:query\",\"teacher:dashboard:view\",\"teaching:exam:edit\",\"system:notice:list\",\"student:prospect:followup\",\"course:resource:edit\",\"points:redemption:claim\",\"tuition:fee:list\",\"class:student:edit\",\"course:course:add\",\"payroll:pay-rate:delete\",\"tuition:refund:approve\",\"system:notice:query\",\"student:enrolled:query\",\"tuition:fee:delete\",\"system:config:delete\",\"teaching:homework:edit\",\"course:resource:add\",\"teaching:homework:query\",\"enrollment:enrollment:list\",\"teaching:teacher:add\",\"attendance:leave:teacher:query\",\"points:redemption:cancel\",\"course:course:list\",\"payroll:payroll:confirm\",\"student:prospect:edit\",\"class:student:delete\",\"teaching:teacher:delete\",\"system:role:list\",\"system:user:delete\",\"teaching:teacher:edit\",\"enrollment:enrollment:query\",\"attendance:leave:student:approve\",\"dashboard:dashboard:view\",\"system:user:import\",\"student:parent:delete\",\"student:prospect:list\",\"tuition:refund:delete\",\"tuition:fee:query\",\"payroll:pay-rate:edit\",\"points:redemption:list\",\"system:menu:delete\",\"schedule:consume\",\"points:gift:list\",\"teaching:evaluation:remove\",\"attendance:leave:student:add\",\"points:gift:add\",\"course:subject:edit\",\"system:user:list\",\"course:course:delete\",\"student:completed:list\",\"attendance:signin:list\",\"student:enrolled:delete\",\"system:notice:add\",\"attendance:signin:query\",\"teaching:exam:query\",\"system:dict:delete\",\"system:role:query\",\"system:menu:edit\",\"payroll:payroll:query\"],\"roles\":[\"admin\"]},\"timestamp\":1784629946125}',1,NULL,1274,'2026-07-21 18:32:25');
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
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,1,1,'拥有全部权限','','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(2,'普通用户','user',2,1,1,'基础权限','','2026-07-10 21:45:03','','2026-07-10 21:45:03',0),(3,'教师','teacher',3,1,1,'教师角色，拥有教师端所有权限','','2026-07-21 14:41:16','','2026-07-21 14:41:16',0);
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
INSERT INTO `sys_role_menu` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,100),(1,101),(1,102),(1,103),(1,104),(1,105),(1,106),(1,107),(1,108),(1,109),(1,110),(1,111),(1,112),(1,113),(1,114),(1,115),(1,116),(1,117),(1,118),(1,119),(1,120),(1,121),(1,122),(1,123),(1,124),(1,125),(1,126),(1,127),(1,128),(1,129),(1,130),(1,131),(1,132),(1,133),(1,200),(1,201),(1,202),(1,203),(1,204),(1,210),(1,211),(1,212),(1,213),(1,214),(1,215),(1,216),(1,220),(1,221),(1,222),(1,223),(1,224),(1,230),(1,231),(1,232),(1,240),(1,241),(1,242),(1,250),(1,251),(1,260),(1,261),(1,262),(1,263),(1,264),(1,265),(1,266),(1,267),(1,268),(1,269),(1,270),(1,271),(1,272),(1,273),(1,274),(1,275),(1,276),(1,277),(1,278),(1,279),(1,280),(1,281),(1,282),(1,283),(1,284),(1,285),(1,286),(1,287),(1,288),(1,289),(1,290),(1,291),(1,292),(1,293),(1,294),(1,295),(1,296),(1,297),(1,298),(1,299),(1,300),(1,301),(1,302),(1,303),(1,304),(1,305),(1,306),(1,307),(1,308),(1,309),(1,310),(1,311),(1,312),(1,313),(1,314),(1,315),(1,316),(1,317),(1,318),(1,319),(1,320),(1,321),(1,322),(1,323),(1,324),(1,325),(1,326),(1,327),(1,328),(1,329),(1,330),(1,331),(1,332),(1,333),(1,334),(1,335),(1,336),(1,337),(1,338),(1,339),(1,340),(1,341),(1,342),(1,343),(1,344),(1,345),(1,346),(1,347),(1,348),(1,349),(1,350),(1,351),(1,352),(1,353),(1,354),(1,355),(1,356),(1,357),(1,358),(1,359),(1,360),(1,361),(1,362),(1,363),(1,364),(1,365),(1,366),(1,367),(1,368),(1,369),(1,370),(1,371),(1,372),(1,373),(1,374),(1,375),(1,376),(3,8),(3,9),(3,260),(3,261),(3,262),(3,263),(3,306),(3,310),(3,311),(3,312),(3,313),(3,318),(3,319),(3,320),(3,321),(3,375),(3,376);
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
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2b$10$5sQPytQwvHx9yqB1Ye/gwOV33rrcfLpo5HrXPGN4rPDTNZYydhPsG','超级管理员','管理员','admin@edu.com','13800000000',1,'',1,'',NULL,NULL,'','2026-07-10 21:45:03','','2026-07-10 21:45:03','系统内置超级管理员',0),(2,'teacher1','$2b$10$5sQPytQwvHx9yqB1Ye/gwOV33rrcfLpo5HrXPGN4rPDTNZYydhPsG','寮犺?甯','寮犺?甯','','',0,'',1,'',NULL,NULL,'','2026-07-21 14:45:46','','2026-07-21 14:45:46',NULL,0);
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
INSERT INTO `sys_user_role` VALUES (1,1),(2,3);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

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

-- Dump completed on 2026-07-21 18:33:57
