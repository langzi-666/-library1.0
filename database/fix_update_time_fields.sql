-- ============================================
-- 修复所有继承 BaseEntity 的表缺少 update_time 字段的问题
-- 执行日期: 2024-12-01
-- ============================================

USE `library1.4`;

-- 修复 category 表
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'library1.4' 
    AND TABLE_NAME = 'category' 
    AND COLUMN_NAME = 'update_time'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `category` ADD COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER `create_time`',
    'SELECT ''category 表的 update_time 字段已存在'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 修复 book 表
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'library1.4' 
    AND TABLE_NAME = 'book' 
    AND COLUMN_NAME = 'update_time'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `book` ADD COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER `create_time`',
    'SELECT ''book 表的 update_time 字段已存在'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 修复 borrow_record 表
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'library1.4' 
    AND TABLE_NAME = 'borrow_record' 
    AND COLUMN_NAME = 'update_time'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `borrow_record` ADD COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER `create_time`',
    'SELECT ''borrow_record 表的 update_time 字段已存在'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 修复 user 表
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'library1.4' 
    AND TABLE_NAME = 'user' 
    AND COLUMN_NAME = 'update_time'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `user` ADD COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER `create_time`',
    'SELECT ''user 表的 update_time 字段已存在'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT '所有表的 update_time 字段修复完成！' AS result;

