/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Host           : localhost:3306
 Source Schema         : sakura_purchase

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 11/10/2022 20:32:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for record
-- ----------------------------
CREATE TABLE IF NOT EXISTS `record`
(
    `order_id`     BIGINT                                                        NOT NULL,
    `platform`     int                                                           NOT NULL,
    `status`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `order_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `order_amount` double UNSIGNED                                               NOT NULL,
    `out_trade_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    `attach`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL,
    `create_time`  datetime                                                      NOT NULL,
    PRIMARY KEY (`order_id`) USING BTREE,
    UNIQUE INDEX `id` (`order_id`) USING HASH
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `bukkit_record`
(
    `id`       int                                                       NOT NULL AUTO_INCREMENT,
    `uuid`     char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `order_id` bigint                                                    NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uid_order` (`uuid` ASC, `order_id` ASC) USING BTREE,
    UNIQUE INDEX `order_id` (`order_id` ASC) USING BTREE,
    CONSTRAINT `order_id` FOREIGN KEY (`order_id`) REFERENCES `record` (`order_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;