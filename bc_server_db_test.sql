-- MySQL dump 10.13  Distrib 5.7.20, for Linux (x86_64)
--
-- Host: localhost    Database: bc_server_db
-- ------------------------------------------------------
-- Server version	5.7.20-0ubuntu0.16.04.1

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
-- Table structure for table `dev_auth`
--

DROP TABLE IF EXISTS `dev_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dev_auth` (
  `dev_uniq_id` bigint(20) unsigned NOT NULL,
  `admin_user` int(10) unsigned NOT NULL,
  `admin_auth` tinyint(3) unsigned NOT NULL DEFAULT '7',
  `user_id1` int(10) unsigned DEFAULT '0',
  `user_id1_auth` tinyint(3) unsigned DEFAULT '0',
  `user_id2` int(10) unsigned DEFAULT '0',
  `user_id2_auth` tinyint(3) unsigned DEFAULT '0',
  `user_id3` int(10) unsigned DEFAULT '0',
  `user_id3_auth` tinyint(3) unsigned DEFAULT '0',
  `user_id4` int(10) unsigned DEFAULT '0',
  `user_id4_auth` tinyint(3) unsigned DEFAULT '0',
  PRIMARY KEY (`dev_uniq_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dev_auth`
--

LOCK TABLES `dev_auth` WRITE;
/*!40000 ALTER TABLE `dev_auth` DISABLE KEYS */;
INSERT INTO `dev_auth` VALUES (1,5,7,0,0,0,0,0,0,0,0),(2,5,7,0,0,0,0,0,0,0,0),(3,5,7,0,0,0,0,0,0,0,0);
/*!40000 ALTER TABLE `dev_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dev_info`
--

DROP TABLE IF EXISTS `dev_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dev_info` (
  `dev_uniq_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `dev_password` char(32) NOT NULL,
  `dev_id` smallint(5) unsigned NOT NULL,
  `dev_name` char(128) NOT NULL DEFAULT '',
  `sys_sig_tab_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`dev_uniq_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dev_info`
--

LOCK TABLES `dev_info` WRITE;
/*!40000 ALTER TABLE `dev_info` DISABLE KEYS */;
INSERT INTO `dev_info` VALUES (1,5,'abcdefghijklmnopqrstuvwxyz123456',0,'',0),(2,5,'ABCDEFGHIJKLMNOPQRSTUVWXYZ123456',0,'',0),(3,5,'123456abcdefghijklmnopqrstuvwxyz',0,'AnsersionDev',3);
/*!40000 ALTER TABLE `dev_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_sig_tab`
--

DROP TABLE IF EXISTS `sys_sig_tab`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_sig_tab` (
  `sys_sig_tab_id` int(10) unsigned NOT NULL,
  `ssd00` varbinary(64) DEFAULT NULL,
  `ssd01` varbinary(64) DEFAULT NULL,
  `ssd02` varbinary(64) DEFAULT NULL,
  `ssd03` varbinary(64) DEFAULT NULL,
  `ssd04` varbinary(64) DEFAULT NULL,
  `ssd05` varbinary(64) DEFAULT NULL,
  `ssd06` varbinary(64) DEFAULT NULL,
  `ssd07` varbinary(64) DEFAULT NULL,
  `ssd08` varbinary(64) DEFAULT NULL,
  `ssd09` varbinary(64) DEFAULT NULL,
  `ssd10` varbinary(64) DEFAULT NULL,
  `ssd11` varbinary(64) DEFAULT NULL,
  `ssd12` varbinary(64) DEFAULT NULL,
  `ssd13` varbinary(64) DEFAULT NULL,
  `ssd14` varbinary(64) DEFAULT NULL,
  `ssd15` varbinary(64) DEFAULT NULL,
  PRIMARY KEY (`sys_sig_tab_id`)
) ENGINE=InnoDB DEFAULT CHARSET=binary;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_sig_tab`
--

LOCK TABLES `sys_sig_tab` WRITE;
/*!40000 ALTER TABLE `sys_sig_tab` DISABLE KEYS */;
INSERT INTO `sys_sig_tab` VALUES (3,'�',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `sys_sig_tab` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT '',
  `e_mail` varchar(256) DEFAULT '',
  `phone` varchar(128) DEFAULT '',
<<<<<<< HEAD
  `password` char(64) NOT NULL,
=======
  `password` char(32) NOT NULL,
>>>>>>> e688b7baff8f14fcd36b1b992788c86962e1a015
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
<<<<<<< HEAD
INSERT INTO `user_info` VALUES (1,'Ansersion','','','a123456'),(2,'张三','','','ansersion1'),(3,'李四','','','ansersion2'),(4,'王五','','','ansersion3'),(5,'Ansersion4','','','899b26c71f284019d2954b8e823e235b4c7ad2715a9831c47367231ef473e588'),(6,'Ansersion5','','','ansersion5'),(7,'Ansersion6','','','ansersion6'),(8,'Ansersion7','','','ansersion7'),(9,'Ansersion8','','','ansersion8'),(10,'Ansersion9','','','ansersion9');
=======
INSERT INTO `user_info` VALUES (1,'Ansersion','','','a123456'),(2,'张三','','','ansersion1'),(3,'李四','','','ansersion2'),(4,'王五','','','ansersion3'),(5,'Ansersion4','','','ansersion4'),(6,'Ansersion5','','','ansersion5'),(7,'Ansersion6','','','ansersion6'),(8,'Ansersion7','','','ansersion7'),(9,'Ansersion8','','','ansersion8'),(10,'Ansersion9','','','ansersion9');
>>>>>>> e688b7baff8f14fcd36b1b992788c86962e1a015
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

-- Dump completed on 2018-01-13 20:19:21
