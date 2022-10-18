SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `record`
(
    `order_id`     BIGINT          NOT NULL,
    `platform`     int             NOT NULL,
    `status`       varchar(255)    NOT NULL,
    `order_name`   varchar(255)    NOT NULL,
    `order_amount` double UNSIGNED NOT NULL,
    `out_trade_no` varchar(255)    NULL DEFAULT NULL,
    `attach`       text            NULL,
    `create_time`  datetime        NOT NULL,
    PRIMARY KEY (`order_id`) USING BTREE,
    INDEX `status` (`status`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `bukkit_record`
(
    `id`       int      NOT NULL AUTO_INCREMENT,
    `uuid`     char(36) NOT NULL,
    `order_id` bigint   NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uid_order` (`uuid` ASC, `order_id` ASC) USING BTREE,
    UNIQUE INDEX `order_id` (`order_id` ASC) USING BTREE,
    CONSTRAINT `order_id` FOREIGN KEY (`order_id`) REFERENCES `record` (`order_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;