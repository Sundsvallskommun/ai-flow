CREATE TABLE IF NOT EXISTS `flow`(
    `id` VARCHAR(255) NOT NULL,
    `version` INTEGER NOT NULL,
    `content` TEXT,
    PRIMARY KEY (`id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `flow` ADD INDEX `idx_flow_id`(`id`);
ALTER TABLE `flow` ADD INDEX `idx_flow_id_and_version`(`id`, `version`);
