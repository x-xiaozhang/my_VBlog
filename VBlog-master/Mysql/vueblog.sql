/*
CREATE DATABASE `vueblog` DEFAULT CHARACTER SET utf8;
 */
USE `vueblog`;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `mdContent` text COMMENT 'md文件源码',
  `htmlContent` text COMMENT 'html源码',
  `summary` text,
  `cateName` varchar(255) DEFAULT NULL COMMENT '所属栏目',
  `nickname` varchar(255) DEFAULT NULL COMMENT '作者',
  `publishDate` datetime DEFAULT NULL,
  `editTime` datetime DEFAULT NULL,
  `state` int(11) DEFAULT NULL COMMENT '0表示草稿箱，1表示已发表，2表示已删除',
  `pageView` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES ('1','示例文章标题','# Markdown 标题\n这是文章的Markdown内容。','<h1>Markdown 标题</h1><p>这是文章的HTML内容。</p>','这是一篇示例文章的摘要。','技术分享','超级管理员','2025-12-01 00:00:00','2025-12-01 00:00:00', '1', '0');


-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cateName` varchar(64) DEFAULT NULL,
  `date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES ('1', '技术分享', '2025-12-01');


-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aid` int(11) DEFAULT NULL,
  `content` text,
  `publishDate` datetime DEFAULT NULL,
  `parentId` int(11) DEFAULT NULL COMMENT '-1表示正常回复，其他值表示是评论的回复',
  `uid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `aid` (`aid`),
  KEY `uid` (`uid`),
  KEY `parentId` (`parentId`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`aid`) REFERENCES `article` (`id`),
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`id`),
  CONSTRAINT `comments_ibfk_3` FOREIGN KEY (`parentId`) REFERENCES `comments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of comments
-- ----------------------------

-- ----------------------------
-- Table structure for pv
-- ----------------------------
DROP TABLE IF EXISTS `pv`;
CREATE TABLE `pv` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `countDate` date DEFAULT NULL,
  `pv` int(11) DEFAULT NULL,
  `uid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pv_ibfk_1` (`uid`),
  CONSTRAINT `pv_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of pv
-- ----------------------------
INSERT INTO `pv` VALUES ('1', '2017-12-24', '20', '6');
INSERT INTO `pv` VALUES ('2', '2017-12-24', '14', '7');
INSERT INTO `pv` VALUES ('4', '2017-12-25', '40', '6');
INSERT INTO `pv` VALUES ('5', '2017-12-25', '23', '7');
INSERT INTO `pv` VALUES ('6', '2017-12-26', '11', '6');
INSERT INTO `pv` VALUES ('7', '2017-12-26', '32', '7');
INSERT INTO `pv` VALUES ('26', '2017-12-23', '2', '6');
INSERT INTO `pv` VALUES ('27', '2017-12-23', '77', '7');

-- 注意：roles和roles_user表已被删除，用户角色直接存储在user表的role字段中



-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `nickname` varchar(64) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  `email` varchar(64) DEFAULT NULL,
  `userface` varchar(255) DEFAULT NULL,
  `regTime` datetime DEFAULT NULL,
  `role` int(11) DEFAULT '2' COMMENT '1=超级管理员,2=普通用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES('1', 'admin', '超级管理员', '202cb962ac59075b964b07152d234b70', '1', '3059697831@qq.com', 'https://api.dicebear.com/7.x/adventurer/png?seed=admin', '2025-12-01 00:00:00',1);
INSERT INTO `user` VALUES('2', 'user', '普通用户', '202cb962ac59075b964b07152d234b70', '1', '3059697831@qq.com', 'https://api.dicebear.com/7.x/adventurer/png?seed=user', '2025-12-01 00:00:00',2);
SET FOREIGN_KEY_CHECKS=1;
