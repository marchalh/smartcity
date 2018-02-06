/*
Navicat MySQL Data Transfer

Source Server         : Potager
Source Server Version : 50547
Source Host           : 149.202.56.108:3306
Source Database       : potager

Target Server Type    : MYSQL
Target Server Version : 50547
File Encoding         : 65001

Date: 2016-04-29 11:43:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `meteo`
-- ----------------------------
DROP TABLE IF EXISTS `meteo`;
CREATE TABLE `meteo` (
  `Date` varchar(255) NOT NULL,
  `Location` varchar(255) NOT NULL,
  `IdMeteo` int(11) NOT NULL,
  `Icon` varchar(255) NOT NULL,
  PRIMARY KEY (`Date`,`Location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of meteo
-- ----------------------------

-- ----------------------------
-- Table structure for `meteodemain`
-- ----------------------------
DROP TABLE IF EXISTS `meteodemain`;
CREATE TABLE `meteodemain` (
  `Temperature` int(11) NOT NULL,
  `Id` varchar(255) NOT NULL,
  `Icon` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of meteodemain
-- ----------------------------
INSERT INTO `meteodemain` VALUES ('8', '2783770', '10d');
INSERT INTO `meteodemain` VALUES ('6', '2789733', '10d');
INSERT INTO `meteodemain` VALUES ('6', '2790469', '13d');
INSERT INTO `meteodemain` VALUES ('4', '2791993', '13d');
INSERT INTO `meteodemain` VALUES ('5', '2792347', '01d');
INSERT INTO `meteodemain` VALUES ('5', '2792411', '01d');
INSERT INTO `meteodemain` VALUES ('7', '2796741', '13d');
INSERT INTO `meteodemain` VALUES ('6', '2800867', '10d');
INSERT INTO `meteodemain` VALUES ('6', '2803136', '10d');
INSERT INTO `meteodemain` VALUES ('6', '3333250', '10d');
INSERT INTO `meteodemain` VALUES ('6', '3333251', '10d');

-- ----------------------------
-- Table structure for `plante`
-- ----------------------------
DROP TABLE IF EXISTS `plante`;
CREATE TABLE `plante` (
  `IDPlante` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NomPlante` varchar(255) NOT NULL,
  `DatePlantationDebut` enum('Décembre','Novembre','Octobre','Septembre','Aout','Juillet','Juin','Mai','Avril','Mars','Février','Janvier') DEFAULT NULL,
  `DatePlantationFin` enum('Décembre','Novembre','Octobre','Septembre','Aout','Juillet','Juin','Mai','Avril','Mars','Février','Janvier') DEFAULT NULL,
  `DateRecolteDebut` enum('Décembre','Novembre','Octobre','Septembre','Aout','Juillet','Juin','Mai','Avril','Mars','Février','Janvier') DEFAULT NULL,
  `DateRecolteFin` enum('Décembre','Novembre','Octobre','Septembre','Aout','Juillet','Juin','Mai','Avril','Mars','Février','Janvier') DEFAULT NULL,
  PRIMARY KEY (`IDPlante`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of plante
-- ----------------------------
INSERT INTO `plante` VALUES ('1', 'Artichaut', 'Mars', 'Avril', 'Aout', 'Septembre');
INSERT INTO `plante` VALUES ('2', 'Asperge', 'Mars', 'Avril', 'Mai', 'Juin');
INSERT INTO `plante` VALUES ('3', 'Aubergine', 'Janvier', 'Mars', 'Aout', 'Septembre');
INSERT INTO `plante` VALUES ('4', 'Brocoli', 'Avril', 'Juin', 'Juillet', 'Octobre');
INSERT INTO `plante` VALUES ('5', 'Carotte', 'Février', 'Juin', 'Juin', 'Novembre');
INSERT INTO `plante` VALUES ('6', 'Céleri à couper', 'Avril', 'Septembre', 'Mai', 'Décembre');
INSERT INTO `plante` VALUES ('7', 'Cerfeuil', 'Mars', 'Septembre', 'Mai', 'Octobre');
INSERT INTO `plante` VALUES ('8', 'Chou blanc', 'Avril', 'Avril', 'Septembre', 'Décembre');
INSERT INTO `plante` VALUES ('9', 'Chou de Bruxelles', 'Mars', 'Mai', 'Octobre', 'Janvier');
INSERT INTO `plante` VALUES ('10', 'Chou-fleur', 'Avril', 'Juin', 'Aout', 'Octobre');
INSERT INTO `plante` VALUES ('11', 'Chou rouge', 'Avril', 'Avril', 'Octobre', 'Novembre');
INSERT INTO `plante` VALUES ('12', 'Concombre', 'Mai', 'Juin', 'Aout', 'Octobre');
INSERT INTO `plante` VALUES ('13', 'Courgette', 'Mai', 'Mai', 'Juin', 'Octobre');
INSERT INTO `plante` VALUES ('14', 'Epinard', 'Février', 'Septembre', 'Février', 'Novembre');
INSERT INTO `plante` VALUES ('15', 'Laitue', 'Avril', 'Aout', 'Mai', 'Octobre');
INSERT INTO `plante` VALUES ('16', 'Persil', 'Mars', 'Septembre', 'Juin', 'Novembre');
INSERT INTO `plante` VALUES ('17', 'Poireau', 'Mars', 'Mai', 'Aout', 'Février');
INSERT INTO `plante` VALUES ('18', 'Pois', 'Février', 'Avril', 'Juin', 'Aout');
INSERT INTO `plante` VALUES ('19', 'Radis', 'Mars', 'Septembre', 'Mai', 'Octobre');
INSERT INTO `plante` VALUES ('20', 'Roquette', 'Mars', 'Aout', 'Mai', 'Octobre');

-- ----------------------------
-- Table structure for `sensorhum`
-- ----------------------------
DROP TABLE IF EXISTS `sensorhum`;
CREATE TABLE `sensorhum` (
  `Date` varchar(255) NOT NULL,
  `Id` varchar(255) NOT NULL,
  `Value` double NOT NULL,
  `Jour` int(11) NOT NULL,
  `Ordre` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Ordre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sensorhum
-- ----------------------------
INSERT INTO `sensorhum` VALUES ('2016-04-25T21:16:59.187+02:00', '1', '16.4', '16916', '1');
INSERT INTO `sensorhum` VALUES ('2016-04-26T18:20:39.159+02:00', '1', '16.4', '16917', '2');
INSERT INTO `sensorhum` VALUES ('2016-04-27T15:14:26.111+02:00', '1', '1234', '16918', '3');

-- ----------------------------
-- Table structure for `sensorlum`
-- ----------------------------
DROP TABLE IF EXISTS `sensorlum`;
CREATE TABLE `sensorlum` (
  `Date` varchar(255) NOT NULL DEFAULT 'CURRENT_TIMESTAMP',
  `Id` varchar(255) NOT NULL,
  `Value` double NOT NULL,
  `Jour` int(11) NOT NULL,
  `Ordre` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Ordre`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sensorlum
-- ----------------------------
INSERT INTO `sensorlum` VALUES ('2016-04-25T21:16:50.619+02:00', '1', '541', '16916', '1');
INSERT INTO `sensorlum` VALUES ('2016-04-25T21:26:00.619+02:00', '1', '421', '16916', '2');
INSERT INTO `sensorlum` VALUES ('2016-04-25T21:36:10.619+02:00', '1', '379', '16916', '3');
INSERT INTO `sensorlum` VALUES ('2016-04-25T21:46:20.619+02:00', '1', '412', '16916', '4');
INSERT INTO `sensorlum` VALUES ('2016-04-25T21:56:30.619+02:00', '1', '422', '16916', '5');
INSERT INTO `sensorlum` VALUES ('2016-04-27T07:19:02.697+02:00', '1', '452', '16918', '6');

-- ----------------------------
-- Table structure for `sensortemp`
-- ----------------------------
DROP TABLE IF EXISTS `sensortemp`;
CREATE TABLE `sensortemp` (
  `Date` varchar(255) NOT NULL,
  `Id` varchar(255) NOT NULL,
  `Value` double NOT NULL,
  `Jour` int(11) NOT NULL,
  `Ordre` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Ordre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sensortemp
-- ----------------------------
INSERT INTO `sensortemp` VALUES ('2016-04-25T12:34:53.535+02:00', '1', '16.3', '16916', '1');
INSERT INTO `sensortemp` VALUES ('2016-04-25T20:37:36.922+02:00', '1', '16.4', '16916', '2');
INSERT INTO `sensortemp` VALUES ('2016-04-25T20:43:43.493+02:00', '1', '16.4', '16916', '3');
INSERT INTO `sensortemp` VALUES ('2016-04-25T20:43:55.259+02:00', '1', '15.5', '16916', '4');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `Id` varchar(255) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Location` int(255) NOT NULL,
  `Mdp` varchar(255) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'Anne', '2790469', 'salut');
