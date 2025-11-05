-- ============================================
-- 图书管理系统数据库性能优化脚本
-- 创建日期: 2024年12月
-- ============================================

USE `library1.4`;

-- ============================================
-- 1. 索引优化
-- ============================================

-- 1.1 图书表索引优化
-- 添加联合索引用于常用查询组合
ALTER TABLE `book` 
ADD INDEX `idx_status_category` (`status`, `category_id`),
ADD INDEX `idx_available_stock` (`available_stock`),
ADD INDEX `idx_publisher` (`publisher`),
ADD INDEX `idx_publish_date` (`publish_date`);

-- 1.2 借阅记录表索引优化
-- 添加联合索引用于常用查询组合
ALTER TABLE `borrow_record` 
ADD INDEX `idx_user_status` (`user_id`, `status`),
ADD INDEX `idx_book_status` (`book_id`, `status`),
ADD INDEX `idx_due_date_status` (`due_date`, `status`),
ADD INDEX `idx_return_date` (`return_date`);

-- 1.3 用户表索引优化
-- 添加联合索引用于常用查询组合
ALTER TABLE `user` 
ADD INDEX `idx_role_status` (`role`, `status`),
ADD INDEX `idx_last_login_time` (`last_login_time`);

-- 1.4 操作日志表索引优化
-- 添加联合索引用于常用查询组合
ALTER TABLE `operation_log` 
ADD INDEX `idx_user_operation_time` (`user_id`, `operation_time`),
ADD INDEX `idx_operation_type_time` (`operation_type`, `operation_time`),
ADD INDEX `idx_result` (`result`);

-- ============================================
-- 2. 表结构优化
-- ============================================

-- 2.1 优化字符集和排序规则（如果还未优化）
-- ALTER TABLE `book` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE `user` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE `borrow_record` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE `operation_log` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE `category` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE `system_config` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- 3. 查询优化建议
-- ============================================

-- 3.1 分析表统计信息（优化查询计划）
ANALYZE TABLE `book`;
ANALYZE TABLE `user`;
ANALYZE TABLE `borrow_record`;
ANALYZE TABLE `operation_log`;
ANALYZE TABLE `category`;
ANALYZE TABLE `system_config`;

-- 3.2 优化表（修复碎片，优化索引）
OPTIMIZE TABLE `book`;
OPTIMIZE TABLE `user`;
OPTIMIZE TABLE `borrow_record`;
OPTIMIZE TABLE `operation_log`;
OPTIMIZE TABLE `category`;
OPTIMIZE TABLE `system_config`;

-- ============================================
-- 4. 性能监控查询
-- ============================================

-- 4.1 查看表大小
SELECT 
    TABLE_NAME AS '表名',
    ROUND(DATA_LENGTH / 1024 / 1024, 2) AS '数据大小(MB)',
    ROUND(INDEX_LENGTH / 1024 / 1024, 2) AS '索引大小(MB)',
    ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024, 2) AS '总大小(MB)',
    TABLE_ROWS AS '记录数'
FROM 
    information_schema.TABLES
WHERE 
    TABLE_SCHEMA = 'library1.4'
ORDER BY 
    (DATA_LENGTH + INDEX_LENGTH) DESC;

-- 4.2 查看索引使用情况
SELECT 
    TABLE_NAME AS '表名',
    INDEX_NAME AS '索引名',
    COLUMN_NAME AS '列名',
    SEQ_IN_INDEX AS '索引顺序',
    CARDINALITY AS '基数',
    INDEX_TYPE AS '索引类型'
FROM 
    information_schema.STATISTICS
WHERE 
    TABLE_SCHEMA = 'library1.4'
ORDER BY 
    TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 4.3 查看慢查询（需要开启慢查询日志）
-- SHOW VARIABLES LIKE 'slow_query%';
-- SHOW VARIABLES LIKE 'long_query_time';

-- ============================================
-- 5. 索引使用建议
-- ============================================

-- 5.1 常用查询场景索引说明
-- 
-- 图书查询场景：
-- - 按分类和状态查询：idx_status_category
-- - 按可用库存查询：idx_available_stock
-- - 按出版社查询：idx_publisher
-- - 按出版日期排序：idx_publish_date
--
-- 借阅记录查询场景：
-- - 查询用户的借阅记录：idx_user_status
-- - 查询图书的借阅记录：idx_book_status
-- - 查询逾期记录：idx_due_date_status
-- - 按归还日期查询：idx_return_date
--
-- 用户查询场景：
-- - 按角色和状态查询：idx_role_status
-- - 按最后登录时间查询：idx_last_login_time
--
-- 日志查询场景：
-- - 查询用户的操作日志：idx_user_operation_time
-- - 按操作类型和时间查询：idx_operation_type_time
-- - 按结果查询：idx_result

-- ============================================
-- 完成
-- ============================================
SELECT '数据库性能优化完成！' AS message;

