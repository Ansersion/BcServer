CREATE DATABASE  IF NOT EXISTS `bc_server_db` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `bc_server_db`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: bc_server_db
-- ------------------------------------------------------
-- Server version	5.7.21-log

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
-- Table structure for table `dev_info`
--

DROP TABLE IF EXISTS `dev_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dev_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `admin_id` bigint(20) unsigned NOT NULL,
  `admin_auth` tinyint(3) unsigned NOT NULL DEFAULT '7',
  `dev_password` char(64) NOT NULL,
  `dev_name` char(128) NOT NULL DEFAULT '',
  `sys_sig_tab_id` int(10) unsigned NOT NULL DEFAULT '0',
  `sys_sig_diff_tab_id` int(10) unsigned NOT NULL DEFAULT '0',
  `cus_sig_tab_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_cus_sig_tab_id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `user_id1` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id1_auth` tinyint(3) unsigned DEFAULT '0',
  `user_id2` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id2_auth` tinyint(3) unsigned DEFAULT '0',
  `user_id3` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id3_auth` tinyint(3) unsigned DEFAULT '0',
  `user_id4` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id4_auth` tinyint(3) unsigned DEFAULT '0',
  `user_ext_tab_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dev_info`
--

LOCK TABLES `dev_info` WRITE;
/*!40000 ALTER TABLE `dev_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `dev_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-04-21 23:29:52
