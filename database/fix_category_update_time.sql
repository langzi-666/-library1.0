-- ============================================
-- 修复 category 表缺少 update_time 字段的问题
-- 执行日期: 2024-12-01
-- ============================================

USE `library1.4`;

-- 安全地添加 update_time 字段（如果不存在）
-- 使用存储过程来检查字段是否存在，避免重复添加报错
DELIMITER $$

DROP PROCEDURE IF EXISTS AddColumnIfNotExists$$

CREATE PROCEDURE AddColumnIfNotExists()
BEGIN
    DECLARE column_count INT;
    
    -- 检查 update_time 字段是否存在
    SELECT COUNT(*) INTO column_count
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'library1.4'
      AND TABLE_NAME = 'category'
      AND COLUMN_NAME = 'update_time';
    
    -- 如果字段不存在，则添加
    IF column_count = 0 THEN
        ALTER TABLE `category` 
        ADD COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' 
        AFTER `create_time`;
        
        SELECT 'update_time 字段已成功添加' AS message;
    ELSE
        SELECT 'update_time 字段已存在，无需添加' AS message;
    END IF;
END$$

DELIMITER ;

-- 执行存储过程
CALL AddColumnIfNotExists();

-- 清理存储过程
DROP PROCEDURE IF EXISTS AddColumnIfNotExists;

