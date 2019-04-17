CREATE DATABASE  IF NOT EXISTS `bc_server_db` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `bc_server_db`;
-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: 127.0.0.1    Database: bc_server_db
-- ------------------------------------------------------
-- Server version	8.0.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `custom_alarm_name_lang_entity_info`
--

DROP TABLE IF EXISTS `custom_alarm_name_lang_entity_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_alarm_name_lang_entity_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chinese` varchar(255) DEFAULT NULL,
  `english` varchar(255) DEFAULT NULL,
  `french` varchar(255) DEFAULT NULL,
  `russian` varchar(255) DEFAULT NULL,
  `arabic` varchar(255) DEFAULT NULL,
  `spanish` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_alarm_name_lang_entity_info`
--

LOCK TABLES `custom_alarm_name_lang_entity_info` WRITE;
/*!40000 ALTER TABLE `custom_alarm_name_lang_entity_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_alarm_name_lang_entity_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_group_lang_entity_info`
--

DROP TABLE IF EXISTS `custom_group_lang_entity_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_group_lang_entity_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `chinese` varchar(128) NOT NULL DEFAULT '',
  `english` varchar(128) NOT NULL DEFAULT '',
  `french` varchar(128) NOT NULL DEFAULT '',
  `russian` varchar(128) NOT NULL DEFAULT '',
  `arabic` varchar(128) NOT NULL DEFAULT '',
  `spanish` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_group_lang_entity_info`
--

LOCK TABLES `custom_group_lang_entity_info` WRITE;
/*!40000 ALTER TABLE `custom_group_lang_entity_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_group_lang_entity_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_group_lang_info`
--

DROP TABLE IF EXISTS `custom_group_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_group_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `group_lang` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_group_lang` (`group_lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_group_lang_info`
--

LOCK TABLES `custom_group_lang_info` WRITE;
/*!40000 ALTER TABLE `custom_group_lang_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_group_lang_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_alm_info`
--

DROP TABLE IF EXISTS `custom_signal_alm_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_alm_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL,
  `alm_class` tinyint(4) NOT NULL DEFAULT '4',
  `dly_before_alm` tinyint(3) unsigned NOT NULL DEFAULT '10',
  `dly_after_alm` tinyint(3) unsigned NOT NULL DEFAULT '10',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_alm_info`
--

LOCK TABLES `custom_signal_alm_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_alm_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_alm_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_boolean_info`
--

DROP TABLE IF EXISTS `custom_signal_boolean_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_boolean_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `def_val` tinyint(1) NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_boolean_info`
--

LOCK TABLES `custom_signal_boolean_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_boolean_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_boolean_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_enum_info`
--

DROP TABLE IF EXISTS `custom_signal_enum_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_enum_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `def_val` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_enum_info`
--

LOCK TABLES `custom_signal_enum_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_enum_info` DISABLE KEYS */;
INSERT INTO `custom_signal_enum_info` VALUES (1,'2018-08-15 15:04:55','2018-08-15 15:04:55',0,0,1,1),(2,'2018-08-15 15:04:55','2018-08-15 15:04:55',1,0,1,2);
/*!40000 ALTER TABLE `custom_signal_enum_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_enum_lang_entity_info`
--

DROP TABLE IF EXISTS `custom_signal_enum_lang_entity_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_enum_lang_entity_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `chinese` varchar(128) NOT NULL DEFAULT '',
  `english` varchar(128) NOT NULL DEFAULT '',
  `french` varchar(128) NOT NULL DEFAULT '',
  `russian` varchar(128) NOT NULL DEFAULT '',
  `arabic` varchar(128) NOT NULL DEFAULT '',
  `spanish` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_enum_lang_entity_info`
--

LOCK TABLES `custom_signal_enum_lang_entity_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_enum_lang_entity_info` DISABLE KEYS */;
INSERT INTO `custom_signal_enum_lang_entity_info` VALUES (1,'2018-08-14 07:36:40','2018-08-14 07:36:40','不明觉厉','Feel nice','','','',''),(2,'2018-08-14 07:36:40','2018-08-14 07:36:40','膜拜大神','Admire','','','',''),(3,'2018-08-14 07:36:40','2018-08-14 07:36:40','可以吃吗','Can be eaten?','','','',''),(4,'2018-08-14 07:36:40','2018-08-14 07:36:40','去吧，皮卡丘！','Go','','','',''),(5,'2018-08-14 07:36:40','2018-08-14 07:36:40','好讨厌的感觉','Feel bad','','','','');
/*!40000 ALTER TABLE `custom_signal_enum_lang_entity_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_enum_lang_info`
--

DROP TABLE IF EXISTS `custom_signal_enum_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_enum_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `enum_key` smallint(5) unsigned NOT NULL,
  `enum_val_lang_id` int(10) unsigned NOT NULL,
  `cus_sig_enm_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `i_enum_val_lang_id` (`enum_val_lang_id`),
  KEY `i_cus_sig_enm_id` (`cus_sig_enm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_enum_lang_info`
--

LOCK TABLES `custom_signal_enum_lang_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_enum_lang_info` DISABLE KEYS */;
INSERT INTO `custom_signal_enum_lang_info` VALUES (1,'2018-08-14 07:40:32','2018-08-14 07:40:32',0,1,1),(2,'2018-08-14 07:40:32','2018-08-14 07:40:32',1,2,1),(3,'2018-08-14 07:40:32','2018-08-14 07:40:32',2,3,1),(4,'2018-08-14 07:40:32','2018-08-14 07:40:32',0,4,2),(5,'2018-08-14 07:40:32','2018-08-14 07:40:32',1,5,2);
/*!40000 ALTER TABLE `custom_signal_enum_lang_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_float_info`
--

DROP TABLE IF EXISTS `custom_signal_float_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_float_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `accuracy` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` float NOT NULL DEFAULT '2147480000',
  `max_val` float NOT NULL DEFAULT '2147480000',
  `def_val` float NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_float_info`
--

LOCK TABLES `custom_signal_float_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_float_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_float_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_group_lang_entity_info`
--

DROP TABLE IF EXISTS `custom_signal_group_lang_entity_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_group_lang_entity_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chinese` varchar(255) DEFAULT NULL,
  `english` varchar(255) DEFAULT NULL,
  `french` varchar(255) DEFAULT NULL,
  `russian` varchar(255) DEFAULT NULL,
  `arabic` varchar(255) DEFAULT NULL,
  `spanish` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_group_lang_entity_info`
--

LOCK TABLES `custom_signal_group_lang_entity_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_group_lang_entity_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_group_lang_entity_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_i16_info`
--

DROP TABLE IF EXISTS `custom_signal_i16_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_i16_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` smallint(6) NOT NULL DEFAULT '32767',
  `max_val` smallint(6) NOT NULL DEFAULT '32767',
  `def_val` smallint(6) NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_i16_info`
--

LOCK TABLES `custom_signal_i16_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_i16_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_i16_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_i32_info`
--

DROP TABLE IF EXISTS `custom_signal_i32_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_i32_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` int(11) NOT NULL DEFAULT '2147483647',
  `max_val` int(11) NOT NULL DEFAULT '2147483647',
  `def_val` int(11) NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_i32_info`
--

LOCK TABLES `custom_signal_i32_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_i32_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_i32_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_info`
--

DROP TABLE IF EXISTS `custom_signal_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_alarm` tinyint(1) NOT NULL DEFAULT '0',
  `val_type` tinyint(3) unsigned NOT NULL,
  `signal_id` int(10) unsigned NOT NULL,
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
  `cus_sig_unit_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
  `cus_group_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_signal_id` (`signal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_info`
--

LOCK TABLES `custom_signal_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_info` DISABLE KEYS */;
INSERT INTO `custom_signal_info` VALUES (2,'2018-09-03 07:31:55','2018-09-03 07:31:55',0,6,4,2,0,0),(12,'2019-04-16 06:31:21','2019-04-16 06:31:21',0,6,50,12,0,0),(16,'2019-04-16 07:14:26','2019-04-16 07:14:26',0,6,70,16,0,0),(17,'2019-04-16 07:27:05','2019-04-16 07:27:05',0,6,75,17,0,0),(21,'2019-04-16 07:49:06','2019-04-16 07:49:06',0,6,95,21,0,0);
/*!40000 ALTER TABLE `custom_signal_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_name_lang_entity_info`
--

DROP TABLE IF EXISTS `custom_signal_name_lang_entity_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_name_lang_entity_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `chinese` varchar(128) NOT NULL DEFAULT '',
  `english` varchar(128) NOT NULL DEFAULT '',
  `french` varchar(128) NOT NULL DEFAULT '',
  `russian` varchar(128) NOT NULL DEFAULT '',
  `arabic` varchar(128) NOT NULL DEFAULT '',
  `spanish` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_name_lang_entity_info`
--

LOCK TABLES `custom_signal_name_lang_entity_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_name_lang_entity_info` DISABLE KEYS */;
INSERT INTO `custom_signal_name_lang_entity_info` VALUES (2,'2018-09-03 07:30:30','2018-09-03 07:30:30','BC温度计','BC Thermometer','','','',''),(12,'2019-04-16 06:31:21','2019-04-16 06:31:21','BC灯','BC Light','','','',''),(16,'2019-04-16 07:14:26','2019-04-16 07:14:26','BC灯','BC Light','','','',''),(17,'2019-04-16 07:27:05','2019-04-16 07:27:05','BC灯','BC Light','','','',''),(21,'2019-04-16 07:49:06','2019-04-16 07:49:06','BC灯','BC Light','','','','');
/*!40000 ALTER TABLE `custom_signal_name_lang_entity_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_name_lang_info`
--

DROP TABLE IF EXISTS `custom_signal_name_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_name_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `custom_signal_name` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_name` (`custom_signal_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_name_lang_info`
--

LOCK TABLES `custom_signal_name_lang_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_name_lang_info` DISABLE KEYS */;
INSERT INTO `custom_signal_name_lang_info` VALUES (1,'2018-09-03 07:30:10','2018-09-03 07:30:10',1),(2,'2018-09-03 07:30:10','2018-09-03 07:30:10',2),(9,'2018-09-12 03:44:34','2018-09-12 03:44:34',9);
/*!40000 ALTER TABLE `custom_signal_name_lang_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_string_info`
--

DROP TABLE IF EXISTS `custom_signal_string_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_string_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `def_val` varchar(512) NOT NULL DEFAULT '',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_string_info`
--

LOCK TABLES `custom_signal_string_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_string_info` DISABLE KEYS */;
INSERT INTO `custom_signal_string_info` VALUES (7,'2019-04-16 06:31:21','2019-04-16 06:31:21',0,'',0,12),(11,'2019-04-16 07:14:26','2019-04-16 07:14:26',0,'',0,16),(12,'2019-04-16 07:27:05','2019-04-16 07:27:05',0,'',0,17),(16,'2019-04-16 07:49:06','2019-04-16 07:49:06',0,'',0,21);
/*!40000 ALTER TABLE `custom_signal_string_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_u16_info`
--

DROP TABLE IF EXISTS `custom_signal_u16_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_u16_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` smallint(5) unsigned NOT NULL DEFAULT '65535',
  `max_val` smallint(5) unsigned NOT NULL DEFAULT '65535',
  `def_val` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_u16_info`
--

LOCK TABLES `custom_signal_u16_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_u16_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_u16_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_u32_info`
--

DROP TABLE IF EXISTS `custom_signal_u32_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_u32_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` int(10) unsigned NOT NULL DEFAULT '4294967295',
  `max_val` int(10) unsigned NOT NULL DEFAULT '4294967295',
  `def_val` int(10) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_u32_info`
--

LOCK TABLES `custom_signal_u32_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_u32_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_u32_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_unit_lang_entity_info`
--

DROP TABLE IF EXISTS `custom_unit_lang_entity_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_unit_lang_entity_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `chinese` varchar(128) NOT NULL DEFAULT '',
  `english` varchar(128) NOT NULL DEFAULT '',
  `french` varchar(128) NOT NULL DEFAULT '',
  `russian` varchar(128) NOT NULL DEFAULT '',
  `arabic` varchar(128) NOT NULL DEFAULT '',
  `spanish` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_unit_lang_entity_info`
--

LOCK TABLES `custom_unit_lang_entity_info` WRITE;
/*!40000 ALTER TABLE `custom_unit_lang_entity_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_unit_lang_entity_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_unit_lang_info`
--

DROP TABLE IF EXISTS `custom_unit_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_unit_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `unit_lang` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_unit_lang` (`unit_lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_unit_lang_info`
--

LOCK TABLES `custom_unit_lang_info` WRITE;
/*!40000 ALTER TABLE `custom_unit_lang_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_unit_lang_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dev_info`
--

DROP TABLE IF EXISTS `dev_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dev_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sn_id` int(10) unsigned NOT NULL,
  `admin_id` int(10) unsigned NOT NULL,
  `password` char(64) NOT NULL,
  `sig_map_chksum` bigint(20) NOT NULL DEFAULT '9223372036854775807',
  `daily_sig_tab_change_times` tinyint(3) unsigned NOT NULL DEFAULT '3',
  `lang_support_mask` tinyint(3) unsigned NOT NULL DEFAULT '192',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_sn_id` (`sn_id`),
  KEY `i_admin_id` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dev_info`
--

LOCK TABLES `dev_info` WRITE;
/*!40000 ALTER TABLE `dev_info` DISABLE KEYS */;
INSERT INTO `dev_info` VALUES (1,'2019-04-16 08:06:23','2019-04-16 08:06:23',1,1,'1111111111111111111111111111111111111111111111111111111111111111',9223372036854775807,3,192),(2,'2019-04-16 08:06:23','2019-04-16 08:06:23',2,2,'2222222222222222222222222222222222222222222222222222222222222222',9223372036854775807,3,192),(3,'2019-04-16 08:06:23','2019-04-16 08:06:23',3,3,'3333333333333333333333333333333333333333333333333333333333333333',9223372036854775807,3,192);
/*!40000 ALTER TABLE `dev_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `server_chain`
--

DROP TABLE IF EXISTS `server_chain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_chain` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `client_id` int(10) unsigned NOT NULL,
  `upper_server` varchar(128) DEFAULT '',
  `upper_server_type` tinyint(4) DEFAULT '0',
  `lower_server` varchar(128) DEFAULT '',
  `lower_server_type` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_dev_id` (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `server_chain`
--

LOCK TABLES `server_chain` WRITE;
/*!40000 ALTER TABLE `server_chain` DISABLE KEYS */;
INSERT INTO `server_chain` VALUES (1,'2019-04-16 08:06:38','2019-04-16 08:06:38',1,'',0,'',0),(2,'2019-04-16 08:06:38','2019-04-16 08:06:38',2,'',0,'',0),(3,'2019-04-16 08:06:38','2019-04-16 08:06:38',3,'',0,'',0);
/*!40000 ALTER TABLE `server_chain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `signal_info`
--

DROP TABLE IF EXISTS `signal_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `signal_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `signal_id` smallint(5) unsigned NOT NULL,
  `dev_id` int(10) unsigned NOT NULL,
  `notifying` tinyint(1) NOT NULL DEFAULT '0',
  `display` tinyint(1) NOT NULL DEFAULT '1',
  `alm_class` tinyint(3) unsigned NOT NULL DEFAULT '127',
  `alm_dly_bef` tinyint(3) unsigned NOT NULL DEFAULT '5',
  `alm_dly_aft` tinyint(3) unsigned NOT NULL DEFAULT '5',
  PRIMARY KEY (`id`),
  KEY `i_dev_id` (`dev_id`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `signal_info`
--

LOCK TABLES `signal_info` WRITE;
/*!40000 ALTER TABLE `signal_info` DISABLE KEYS */;
INSERT INTO `signal_info` VALUES (4,'2018-09-03 02:00:01','2018-09-03 02:00:01',0,2,0,1,127,5,5),(5,'2018-09-03 02:00:01','2018-09-03 02:00:01',57344,2,0,1,127,5,5),(6,'2018-09-03 02:00:01','2018-09-03 02:00:01',57345,2,0,1,127,5,5),(46,'2019-04-16 06:31:20','2019-04-16 06:31:20',57344,3,0,1,255,5,5),(47,'2019-04-16 06:31:20','2019-04-16 06:31:20',57345,3,0,1,255,5,5),(48,'2019-04-16 06:31:20','2019-04-16 06:31:20',57346,3,0,1,255,5,5),(49,'2019-04-16 06:31:21','2019-04-16 06:31:21',57349,3,0,1,255,5,5),(50,'2019-04-16 06:31:21','2019-04-16 06:31:21',0,3,0,0,0,0,0),(66,'2019-04-16 07:14:26','2019-04-16 07:14:26',57344,4,0,1,255,5,5),(67,'2019-04-16 07:14:26','2019-04-16 07:14:26',57345,4,0,1,255,5,5),(68,'2019-04-16 07:14:26','2019-04-16 07:14:26',57346,4,0,1,255,5,5),(69,'2019-04-16 07:14:26','2019-04-16 07:14:26',57349,4,0,1,255,5,5),(70,'2019-04-16 07:14:26','2019-04-16 07:14:26',0,4,0,0,0,0,0),(71,'2019-04-16 07:27:05','2019-04-16 07:27:05',57344,7,0,1,255,5,5),(72,'2019-04-16 07:27:05','2019-04-16 07:27:05',57345,7,0,1,255,5,5),(73,'2019-04-16 07:27:05','2019-04-16 07:27:05',57346,7,0,1,255,5,5),(74,'2019-04-16 07:27:05','2019-04-16 07:27:05',57349,7,0,1,255,5,5),(75,'2019-04-16 07:27:05','2019-04-16 07:27:05',0,7,0,0,0,0,0),(91,'2019-04-16 07:49:06','2019-04-16 07:49:06',57344,1,0,1,255,5,5),(92,'2019-04-16 07:49:06','2019-04-16 07:49:06',57345,1,0,1,255,5,5),(93,'2019-04-16 07:49:06','2019-04-16 07:49:06',57346,1,0,1,255,5,5),(94,'2019-04-16 07:49:06','2019-04-16 07:49:06',57349,1,0,1,255,5,5),(95,'2019-04-16 07:49:06','2019-04-16 07:49:06',0,1,0,0,0,0,0);
/*!40000 ALTER TABLE `signal_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sn_info`
--

DROP TABLE IF EXISTS `sn_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sn_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sn` char(64) NOT NULL,
  `develop_user_id` int(10) unsigned NOT NULL,
  `activite_date` date NOT NULL DEFAULT '1000-01-01',
  `expired_date` date NOT NULL DEFAULT '1000-01-01',
  `exist_time` int(11) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_sn` (`sn`),
  KEY `i_develop_user_id` (`develop_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sn_info`
--

LOCK TABLES `sn_info` WRITE;
/*!40000 ALTER TABLE `sn_info` DISABLE KEYS */;
INSERT INTO `sn_info` VALUES (1,'2019-04-16 08:09:44','2019-04-16 08:09:44','XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1',3,'2019-04-16','2022-04-16',2147483647),(2,'2019-04-16 08:09:45','2019-04-16 08:09:45','XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX2',3,'2019-04-16','2022-04-16',2147483647),(3,'2019-04-16 08:09:45','2019-04-16 08:09:45','XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX3',3,'2019-04-16','2022-04-16',2147483647);
/*!40000 ALTER TABLE `sn_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_alm_info`
--

DROP TABLE IF EXISTS `system_signal_alm_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_alm_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `alm_class` tinyint(4) NOT NULL DEFAULT '4',
  `dly_before_alm` tinyint(3) unsigned NOT NULL DEFAULT '10',
  `dly_after_alm` tinyint(3) unsigned NOT NULL DEFAULT '10',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_alm_info`
--

LOCK TABLES `system_signal_alm_info` WRITE;
/*!40000 ALTER TABLE `system_signal_alm_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_signal_alm_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_boolean_info`
--

DROP TABLE IF EXISTS `system_signal_boolean_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_boolean_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `def_val` tinyint(1) NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_boolean_info`
--

LOCK TABLES `system_signal_boolean_info` WRITE;
/*!40000 ALTER TABLE `system_signal_boolean_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_signal_boolean_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_enum_info`
--

DROP TABLE IF EXISTS `system_signal_enum_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_enum_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `def_val` smallint(5) unsigned NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_enum_info`
--

LOCK TABLES `system_signal_enum_info` WRITE;
/*!40000 ALTER TABLE `system_signal_enum_info` DISABLE KEYS */;
INSERT INTO `system_signal_enum_info` VALUES (1,'2019-01-29 09:37:03','2019-01-29 09:37:03',4,0,0,1,22),(2,'2019-01-29 09:42:23','2019-01-29 09:42:23',4,0,0,1,26),(5,'2019-04-16 06:31:21','2019-04-16 06:31:21',4,0,0,1,38),(9,'2019-04-16 07:14:26','2019-04-16 07:14:26',4,0,0,1,54),(10,'2019-04-16 07:27:05','2019-04-16 07:27:05',4,0,0,1,58),(14,'2019-04-16 07:49:06','2019-04-16 07:49:06',4,0,0,1,74);
/*!40000 ALTER TABLE `system_signal_enum_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_enum_lang_info`
--

DROP TABLE IF EXISTS `system_signal_enum_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_enum_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `enum_key` smallint(5) unsigned NOT NULL,
  `enum_val` int(11) NOT NULL DEFAULT '0',
  `sys_sig_enm_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_enum_lang_info`
--

LOCK TABLES `system_signal_enum_lang_info` WRITE;
/*!40000 ALTER TABLE `system_signal_enum_lang_info` DISABLE KEYS */;
INSERT INTO `system_signal_enum_lang_info` VALUES (5,'2019-04-16 06:31:21','2019-04-16 06:31:21',0,9,5),(6,'2019-04-16 06:31:21','2019-04-16 06:31:21',1,11,5),(13,'2019-04-16 07:14:26','2019-04-16 07:14:26',0,9,9),(14,'2019-04-16 07:14:26','2019-04-16 07:14:26',1,11,9),(15,'2019-04-16 07:27:05','2019-04-16 07:27:05',0,9,10),(16,'2019-04-16 07:27:05','2019-04-16 07:27:05',1,11,10),(23,'2019-04-16 07:49:06','2019-04-16 07:49:06',0,9,14),(24,'2019-04-16 07:49:06','2019-04-16 07:49:06',1,11,14);
/*!40000 ALTER TABLE `system_signal_enum_lang_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_float_info`
--

DROP TABLE IF EXISTS `system_signal_float_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_float_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `accuracy` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` float NOT NULL DEFAULT '2147480000',
  `max_val` float NOT NULL DEFAULT '2147480000',
  `def_val` float NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_float_info`
--

LOCK TABLES `system_signal_float_info` WRITE;
/*!40000 ALTER TABLE `system_signal_float_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_signal_float_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_i16_info`
--

DROP TABLE IF EXISTS `system_signal_i16_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_i16_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` smallint(6) NOT NULL DEFAULT '32767',
  `max_val` smallint(6) NOT NULL DEFAULT '32767',
  `def_val` smallint(6) NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_i16_info`
--

LOCK TABLES `system_signal_i16_info` WRITE;
/*!40000 ALTER TABLE `system_signal_i16_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_signal_i16_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_i32_info`
--

DROP TABLE IF EXISTS `system_signal_i32_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_i32_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` int(11) NOT NULL DEFAULT '2147483647',
  `max_val` int(11) NOT NULL DEFAULT '2147483647',
  `def_val` int(11) NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_i32_info`
--

LOCK TABLES `system_signal_i32_info` WRITE;
/*!40000 ALTER TABLE `system_signal_i32_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_signal_i32_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_info`
--

DROP TABLE IF EXISTS `system_signal_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `custom_flags` smallint(5) unsigned NOT NULL DEFAULT '0',
  `signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_signal_id` (`signal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_info`
--

LOCK TABLES `system_signal_info` WRITE;
/*!40000 ALTER TABLE `system_signal_info` DISABLE KEYS */;
INSERT INTO `system_signal_info` VALUES (3,'2018-09-12 02:47:55','2018-09-12 02:47:55',0,5),(4,'2018-09-12 02:47:55','2018-09-12 02:47:55',0,6),(35,'2019-04-16 06:31:20','2019-04-16 06:31:21',64,46),(36,'2019-04-16 06:31:20','2019-04-16 06:31:20',0,47),(37,'2019-04-16 06:31:20','2019-04-16 06:31:20',0,48),(38,'2019-04-16 06:31:21','2019-04-16 06:31:21',2,49),(51,'2019-04-16 07:14:26','2019-04-16 07:14:26',64,66),(52,'2019-04-16 07:14:26','2019-04-16 07:14:26',0,67),(53,'2019-04-16 07:14:26','2019-04-16 07:14:26',0,68),(54,'2019-04-16 07:14:26','2019-04-16 07:14:26',2,69),(55,'2019-04-16 07:27:05','2019-04-16 07:27:05',64,71),(56,'2019-04-16 07:27:05','2019-04-16 07:27:05',0,72),(57,'2019-04-16 07:27:05','2019-04-16 07:27:05',0,73),(58,'2019-04-16 07:27:05','2019-04-16 07:27:05',2,74),(71,'2019-04-16 07:49:06','2019-04-16 07:49:06',64,91),(72,'2019-04-16 07:49:06','2019-04-16 07:49:06',0,92),(73,'2019-04-16 07:49:06','2019-04-16 07:49:06',0,93),(74,'2019-04-16 07:49:06','2019-04-16 07:49:06',2,94);
/*!40000 ALTER TABLE `system_signal_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_string_info`
--

DROP TABLE IF EXISTS `system_signal_string_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_string_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '4',
  `def_val` varchar(512) NOT NULL DEFAULT '',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_string_info`
--

LOCK TABLES `system_signal_string_info` WRITE;
/*!40000 ALTER TABLE `system_signal_string_info` DISABLE KEYS */;
INSERT INTO `system_signal_string_info` VALUES (1,'2018-09-12 03:26:24','2018-09-12 03:26:24',4,'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1',0,1,7),(2,'2018-09-12 03:33:35','2018-09-12 03:33:35',4,'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1',0,1,10),(9,'2019-04-16 06:31:21','2019-04-16 06:31:21',4,'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1',0,1,35),(13,'2019-04-16 07:14:26','2019-04-16 07:14:26',4,'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1',0,1,51),(14,'2019-04-16 07:27:05','2019-04-16 07:27:05',4,'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1',0,1,55),(18,'2019-04-16 07:49:06','2019-04-16 07:49:06',4,'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX1',0,1,71);
/*!40000 ALTER TABLE `system_signal_string_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_u16_info`
--

DROP TABLE IF EXISTS `system_signal_u16_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_u16_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` smallint(5) unsigned NOT NULL DEFAULT '65535',
  `max_val` smallint(5) unsigned NOT NULL DEFAULT '65535',
  `def_val` smallint(5) unsigned NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_u16_info`
--

LOCK TABLES `system_signal_u16_info` WRITE;
/*!40000 ALTER TABLE `system_signal_u16_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_signal_u16_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_signal_u32_info`
--

DROP TABLE IF EXISTS `system_signal_u32_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_signal_u32_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` int(10) unsigned NOT NULL DEFAULT '4294967295',
  `max_val` int(10) unsigned NOT NULL DEFAULT '4294967295',
  `def_val` int(10) unsigned NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_u32_info`
--

LOCK TABLES `system_signal_u32_info` WRITE;
/*!40000 ALTER TABLE `system_signal_u32_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_signal_u32_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_dev_rel_info`
--

DROP TABLE IF EXISTS `user_dev_rel_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_dev_rel_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` int(10) unsigned NOT NULL,
  `sn_id` int(10) unsigned NOT NULL,
  `auth` tinyint(3) unsigned NOT NULL DEFAULT '6',
  `unconfig_flag` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `i_user_id` (`user_id`),
  KEY `i_sn_id` (`sn_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_dev_rel_info`
--

LOCK TABLES `user_dev_rel_info` WRITE;
/*!40000 ALTER TABLE `user_dev_rel_info` DISABLE KEYS */;
INSERT INTO `user_dev_rel_info` VALUES (1,'2018-09-03 03:15:34','2018-09-03 03:15:34',2,1,6,1),(2,'2018-09-03 03:15:34','2018-09-03 03:15:34',3,1,4,1);
/*!40000 ALTER TABLE `user_dev_rel_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `name` varchar(128) NOT NULL DEFAULT '',
  `e_mail` varchar(256) NOT NULL DEFAULT '',
  `phone` char(32) NOT NULL DEFAULT '',
  `is_develop` tinyint(1) NOT NULL DEFAULT '0',
  `password` char(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_name` (`name`),
  UNIQUE KEY `i_e_mail` (`e_mail`),
  UNIQUE KEY `i_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES (1,'2018-04-24 10:40:01','2018-04-24 10:40:01','Ansersion1','@@@@?1','@@@@?1',0,'1a7d8f0ae2600087a30fda710f1bfd655287f4ee609bc654c634bce355a6fd41'),(2,'2018-04-24 10:40:01','2018-04-24 10:40:01','Ansersion2','@@@@?2','@@@@?2',0,'e8a3486b600b41c8336ae755550df518670f2aa19d61e77b64c60891f08e89a8'),(3,'2018-04-24 10:40:01','2019-04-11 06:38:50','Ansersion3','@@@@?3','13812345678',1,'357f30611fe5930e1b7b5638e192aeb7c08930e20d8d96bcbd76c831715cd227'),(4,'2019-04-11 08:29:02','2019-04-15 10:08:20','18898795902','1515169025@qq.com','18898795902',1,'A3BmV1zKL57977n06115h901sqt56Ke7D01Ec57hy9b3lE41896lHu6k2w04m1i7');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-04-16 16:10:25
