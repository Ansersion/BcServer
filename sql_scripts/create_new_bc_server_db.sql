-- MySQL dump 10.13  Distrib 5.7.20, for Win64 (x86_64)
--
-- Host: localhost    Database: bc_server_db
-- ------------------------------------------------------
-- Server version	5.7.20

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
-- Table structure for table `custom_group_lang_info`
--

DROP TABLE IF EXISTS `custom_group_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_group_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `group_lang` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_unit_lang` (`group_lang`)
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
-- Table structure for table `custom_signal_enum_info`
--

DROP TABLE IF EXISTS `custom_signal_enum_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_enum_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `def_val` smallint(5) unsigned NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `cus_group_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_enum_info`
--

LOCK TABLES `custom_signal_enum_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_enum_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_enum_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_enum_lang_info`
--

DROP TABLE IF EXISTS `custom_signal_enum_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_enum_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `enum_key` smallint(5) unsigned NOT NULL,
  `enum_val` varchar(128) NOT NULL,
  `cus_sig_enm_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_cus_sig_enm_id` (`cus_sig_enm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_enum_lang_info`
--

LOCK TABLES `custom_signal_enum_lang_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_enum_lang_info` DISABLE KEYS */;
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
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL,
  `cus_sig_unit_lang_id` int(10) unsigned NOT NULL,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `accuracy` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` float NOT NULL DEFAULT '2147480000',
  `max_val` float NOT NULL DEFAULT '2147480000',
  `def_val` float NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `cus_group_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
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
-- Table structure for table `custom_signal_i16_info`
--

DROP TABLE IF EXISTS `custom_signal_i16_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_i16_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL,
  `cus_sig_unit_lang_id` int(10) unsigned NOT NULL,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` smallint(6) NOT NULL DEFAULT '32767',
  `max_val` smallint(6) NOT NULL DEFAULT '32767',
  `def_val` smallint(6) NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `cus_group_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
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
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL,
  `cus_sig_unit_lang_id` int(10) unsigned NOT NULL,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` int(11) NOT NULL DEFAULT '2147483647',
  `max_val` int(11) NOT NULL DEFAULT '2147483647',
  `def_val` int(11) NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `cus_group_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
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
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_alarm` tinyint(1) NOT NULL DEFAULT '0',
  `val_type` tinyint(3) unsigned NOT NULL,
  `signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_signal_id` (`signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_info`
--

LOCK TABLES `custom_signal_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `custom_signal_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `custom_signal_name_lang_info`
--

DROP TABLE IF EXISTS `custom_signal_name_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_signal_name_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `custom_signal_name` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_name` (`custom_signal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_name_lang_info`
--

LOCK TABLES `custom_signal_name_lang_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_name_lang_info` DISABLE KEYS */;
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
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `def_val` varchar(512) NOT NULL DEFAULT '',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `cus_group_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `custom_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_custom_signal_id` (`custom_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `custom_signal_string_info`
--

LOCK TABLES `custom_signal_string_info` WRITE;
/*!40000 ALTER TABLE `custom_signal_string_info` DISABLE KEYS */;
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
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL,
  `cus_sig_unit_lang_id` int(10) unsigned NOT NULL,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` smallint(5) unsigned NOT NULL DEFAULT '65535',
  `max_val` smallint(5) unsigned NOT NULL DEFAULT '65535',
  `def_val` smallint(5) unsigned NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `cus_group_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
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
  `cus_sig_name_lang_id` int(10) unsigned NOT NULL,
  `cus_sig_unit_lang_id` int(10) unsigned NOT NULL,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `min_val` int(10) unsigned NOT NULL DEFAULT '4294967295',
  `max_val` int(10) unsigned NOT NULL DEFAULT '4294967295',
  `def_val` int(10) unsigned NOT NULL DEFAULT '0',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `cus_group_lang_id` int(10) unsigned NOT NULL DEFAULT '0',
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
-- Table structure for table `custom_unit_lang_info`
--

DROP TABLE IF EXISTS `custom_unit_lang_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_unit_lang_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `unit_lang` char(32) NOT NULL,
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
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sn_id` int(10) unsigned NOT NULL,
  `admin_id` int(10) unsigned NOT NULL,
  `password` char(64) NOT NULL,
  `daily_sig_tab_change_times` tinyint(3) unsigned NOT NULL DEFAULT '3',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_sn_id` (`sn_id`),
  KEY `i_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dev_info`
--

LOCK TABLES `dev_info` WRITE;
/*!40000 ALTER TABLE `dev_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `dev_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `signal_info`
--

DROP TABLE IF EXISTS `signal_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `signal_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `signal_id` smallint(5) unsigned NOT NULL,
  `dev_id` int(10) unsigned NOT NULL,
  `notifying` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `i_dev_id` (`dev_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `signal_info`
--

LOCK TABLES `signal_info` WRITE;
/*!40000 ALTER TABLE `signal_info` DISABLE KEYS */;
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
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sn` char(64) NOT NULL,
  `develop_user_id` int(10) unsigned NOT NULL,
  `activite_date` date NOT NULL DEFAULT '1000-01-01',
  `expired_date` date NOT NULL DEFAULT '1000-01-01',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_sn` (`sn`),
  KEY `i_develop_user_id` (`develop_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sn_info`
--

LOCK TABLES `sn_info` WRITE;
/*!40000 ALTER TABLE `sn_info` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_enum_info`
--

LOCK TABLES `system_signal_enum_info` WRITE;
/*!40000 ALTER TABLE `system_signal_enum_info` DISABLE KEYS */;
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
  `enum_val` varchar(128) NOT NULL DEFAULT '',
  `sys_sig_enm_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_sys_sig_enm_id` (`sys_sig_enm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_enum_lang_info`
--

LOCK TABLES `system_signal_enum_lang_info` WRITE;
/*!40000 ALTER TABLE `system_signal_enum_lang_info` DISABLE KEYS */;
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
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `config_def` tinyint(1) NOT NULL DEFAULT '1',
  `signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_signal_id` (`signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_info`
--

LOCK TABLES `system_signal_info` WRITE;
/*!40000 ALTER TABLE `system_signal_info` DISABLE KEYS */;
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
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `permission` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `def_val` varchar(512) NOT NULL DEFAULT '',
  `group_lang_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `en_statistics` tinyint(1) NOT NULL DEFAULT '1',
  `system_signal_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_system_signal_id` (`system_signal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_signal_string_info`
--

LOCK TABLES `system_signal_string_info` WRITE;
/*!40000 ALTER TABLE `system_signal_string_info` DISABLE KEYS */;
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
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` int(10) unsigned NOT NULL,
  `dev_id` int(10) unsigned NOT NULL,
  `auth` tinyint(3) unsigned NOT NULL DEFAULT '6',
  PRIMARY KEY (`id`),
  KEY `i_user_id` (`user_id`),
  KEY `i_dev_id` (`dev_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_dev_rel_info`
--

LOCK TABLES `user_dev_rel_info` WRITE;
/*!40000 ALTER TABLE `user_dev_rel_info` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
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

-- Dump completed on 2018-04-24 11:20:55
