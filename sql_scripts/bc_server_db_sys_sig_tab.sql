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
/*!40000 ALTER TABLE `sys_sig_tab` ENABLE KEYS */;
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
