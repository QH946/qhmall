-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 192.168.174.130    Database: qhmall_wms
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `undo_log`
--

DROP TABLE IF EXISTS `undo_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `undo_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `undo_log`
--

LOCK TABLES `undo_log` WRITE;
/*!40000 ALTER TABLE `undo_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `undo_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_purchase`
--

DROP TABLE IF EXISTS `wms_purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_purchase` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignee_id` bigint DEFAULT NULL,
  `assignee_name` varchar(255) DEFAULT NULL,
  `phone` char(13) DEFAULT NULL,
  `priority` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `ware_id` bigint DEFAULT NULL,
  `amount` decimal(18,4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采购信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_purchase`
--

LOCK TABLES `wms_purchase` WRITE;
/*!40000 ALTER TABLE `wms_purchase` DISABLE KEYS */;
INSERT INTO `wms_purchase` VALUES (1,2,'leifengyang','15795163648',0,1,NULL,NULL,'2022-10-06 02:47:53','2022-10-06 03:06:56'),(2,1,'admin','13612345678',NULL,4,NULL,NULL,'2022-10-06 06:52:17','2022-10-06 12:36:25'),(3,2,'leifengyang','15795163648',0,1,NULL,NULL,'2022-10-06 08:44:58','2022-10-06 09:15:38'),(4,1,'admin','13612345678',NULL,3,NULL,NULL,'2022-10-06 12:39:17','2022-10-06 12:41:21'),(5,NULL,'','',1,0,NULL,NULL,'2022-12-23 14:12:09','2022-12-23 14:12:09');
/*!40000 ALTER TABLE `wms_purchase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_purchase_detail`
--

DROP TABLE IF EXISTS `wms_purchase_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_purchase_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint DEFAULT NULL COMMENT '采购单id',
  `sku_id` bigint DEFAULT NULL COMMENT '采购商品id',
  `sku_num` int DEFAULT NULL COMMENT '采购数量',
  `sku_price` decimal(18,4) DEFAULT NULL COMMENT '采购金额',
  `ware_id` bigint DEFAULT NULL COMMENT '仓库id',
  `status` int DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_purchase_detail`
--

LOCK TABLES `wms_purchase_detail` WRITE;
/*!40000 ALTER TABLE `wms_purchase_detail` DISABLE KEYS */;
INSERT INTO `wms_purchase_detail` VALUES (1,2,1,10,NULL,1,3),(2,2,3,2,NULL,1,4),(4,4,4,44,NULL,1,3),(5,4,5,55,NULL,1,3),(6,4,6,66,NULL,1,3);
/*!40000 ALTER TABLE `wms_purchase_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_ware_info`
--

DROP TABLE IF EXISTS `wms_ware_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_ware_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '仓库名',
  `address` varchar(255) DEFAULT NULL COMMENT '仓库地址',
  `areacode` varchar(20) DEFAULT NULL COMMENT '区域编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='仓库信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_ware_info`
--

LOCK TABLES `wms_ware_info` WRITE;
/*!40000 ALTER TABLE `wms_ware_info` DISABLE KEYS */;
INSERT INTO `wms_ware_info` VALUES (1,'1号仓库','北京xx','124'),(2,'2号仓库','杭州','133'),(3,'3号仓库','备用','111');
/*!40000 ALTER TABLE `wms_ware_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_ware_order_task`
--

DROP TABLE IF EXISTS `wms_ware_order_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_ware_order_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint DEFAULT NULL COMMENT 'order_id',
  `order_sn` varchar(255) DEFAULT NULL COMMENT 'order_sn',
  `consignee` varchar(100) DEFAULT NULL COMMENT '收货人',
  `consignee_tel` char(15) DEFAULT NULL COMMENT '收货人电话',
  `delivery_address` varchar(500) DEFAULT NULL COMMENT '配送地址',
  `order_comment` varchar(200) DEFAULT NULL COMMENT '订单备注',
  `payment_way` tinyint(1) DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
  `task_status` tinyint DEFAULT NULL COMMENT '任务状态',
  `order_body` varchar(255) DEFAULT NULL COMMENT '订单描述',
  `tracking_no` char(30) DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime DEFAULT NULL COMMENT 'create_time',
  `ware_id` bigint DEFAULT NULL COMMENT '仓库id',
  `task_comment` varchar(500) DEFAULT NULL COMMENT '工作单备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存工作单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_ware_order_task`
--

LOCK TABLES `wms_ware_order_task` WRITE;
/*!40000 ALTER TABLE `wms_ware_order_task` DISABLE KEYS */;
INSERT INTO `wms_ware_order_task` VALUES (3,NULL,'202212231757052311606227383827562497',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,NULL,'202212231811401201606231053382189058',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `wms_ware_order_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_ware_order_task_detail`
--

DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_ware_order_task_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint DEFAULT NULL COMMENT 'sku_id',
  `sku_name` varchar(255) DEFAULT NULL COMMENT 'sku_name',
  `sku_num` int DEFAULT NULL COMMENT '购买个数',
  `task_id` bigint DEFAULT NULL COMMENT '工作单id',
  `ware_id` bigint DEFAULT NULL COMMENT '仓库id',
  `lock_status` int DEFAULT NULL COMMENT '1-已锁定  2-已解锁  3-扣减',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存工作单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_ware_order_task_detail`
--

LOCK TABLES `wms_ware_order_task_detail` WRITE;
/*!40000 ALTER TABLE `wms_ware_order_task_detail` DISABLE KEYS */;
INSERT INTO `wms_ware_order_task_detail` VALUES (1,44,'',1,3,1,2),(2,44,'',1,5,1,2);
/*!40000 ALTER TABLE `wms_ware_order_task_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_ware_sku`
--

DROP TABLE IF EXISTS `wms_ware_sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_ware_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint DEFAULT NULL COMMENT 'sku_id',
  `ware_id` bigint DEFAULT NULL COMMENT '仓库id',
  `stock` int DEFAULT NULL COMMENT '库存数',
  `sku_name` varchar(200) DEFAULT NULL COMMENT 'sku_name',
  `stock_locked` int DEFAULT '0' COMMENT '锁定库存',
  PRIMARY KEY (`id`),
  KEY `sku_id` (`sku_id`) USING BTREE,
  KEY `ware_id` (`ware_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品库存';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_ware_sku`
--

LOCK TABLES `wms_ware_sku` WRITE;
/*!40000 ALTER TABLE `wms_ware_sku` DISABLE KEYS */;
INSERT INTO `wms_ware_sku` VALUES (1,29,1,100,'华为 HUAWEI Mate 50 Pro 冰霜银 16GB+256GB',0),(2,30,1,100,'华为 HUAWEI Mate 50 Pro 冰霜银 16GB+512GB',0),(3,31,2,100,'华为 HUAWEI Mate 50 Pro 流光紫 16GB+256GB',0),(4,32,2,200,'华为 HUAWEI Mate 50 Pro 流光紫 16GB+512GB',0),(5,33,1,500,'华为 HUAWEI Mate 50 Pro 曜金黑16GB+256GB',0),(6,34,2,120,'华为 HUAWEI Mate 50 Pro 曜金黑16GB+512GB',0),(7,35,1,60,'华为 HUAWEI Mate 50 Pro 破晓霞光16GB+256GB',0),(8,36,2,80,'华为 HUAWEI Mate 50 Pro 破晓霞光16GB+512GB',0),(9,38,1,500,'Apple iPhone 14 pro max  暗夜紫 256GB ',0),(10,42,1,100,'Apple iPhone 14 pro max 深空黑 256GB',0),(11,39,2,100,'Apple iPhone 14 pro max 金色 128GB',0),(12,44,1,200,'Apple iPhone 14 pro max 银色 256GB',0),(13,13,1,100,'小米13远山蓝 256GB',0),(14,14,1,100,'oppofind2金色 256GB',0);
/*!40000 ALTER TABLE `wms_ware_sku` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-23 19:25:49
