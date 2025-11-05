-- ============================================
-- 图书管理系统数据库初始化脚本
-- 数据库名称: library1.4
-- 创建日期: 2024
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `library1.4` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library1.4`;

-- ============================================
-- 1. 图书分类表（category）
-- ============================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `category_id` INT NOT NULL AUTO_INCREMENT COMMENT '分类ID（主键）',
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` INT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    `level` INT NOT NULL DEFAULT 1 COMMENT '分类级别：1/2/3',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`category_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

-- ============================================
-- 2. 用户表（user）
-- ============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `user_id` VARCHAR(20) NOT NULL COMMENT '用户ID（主键），格式：U+年月日+4位流水号',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` VARCHAR(10) DEFAULT '保密' COMMENT '性别：男/女/保密',
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `role` VARCHAR(20) NOT NULL DEFAULT '普通用户' COMMENT '角色：系统管理员/图书管理员/普通用户',
    `status` VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态：正常/锁定/已注销',
    `register_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册日期',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    INDEX `idx_role` (`role`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 3. 图书表（book）
-- ============================================
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
    `book_id` VARCHAR(20) NOT NULL COMMENT '图书编号（主键），格式：BK+年月日+4位流水号',
    `title` VARCHAR(200) NOT NULL COMMENT '书名',
    `author` VARCHAR(100) NOT NULL COMMENT '作者，多个作者用逗号分隔',
    `publisher` VARCHAR(100) NOT NULL COMMENT '出版社',
    `publish_date` DATE NOT NULL COMMENT '出版日期',
    `isbn` VARCHAR(20) NOT NULL COMMENT 'ISBN号',
    `category_id` INT NOT NULL COMMENT '分类ID（外键）',
    `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `available_stock` INT NOT NULL DEFAULT 0 COMMENT '可用库存（库存-借出数量）',
    `description` TEXT COMMENT '图书描述',
    `cover_image` VARCHAR(200) DEFAULT NULL COMMENT '封面图片路径',
    `status` VARCHAR(20) NOT NULL DEFAULT '在库' COMMENT '状态：在库/已借出/已下架/损坏',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`book_id`),
    UNIQUE KEY `uk_isbn` (`isbn`),
    INDEX `idx_title` (`title`),
    INDEX `idx_author` (`author`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_status` (`status`),
    CONSTRAINT `fk_book_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- ============================================
-- 4. 借阅记录表（borrow_record）
-- ============================================
DROP TABLE IF EXISTS `borrow_record`;
CREATE TABLE `borrow_record` (
    `record_id` INT NOT NULL AUTO_INCREMENT COMMENT '记录ID（主键）',
    `user_id` VARCHAR(20) NOT NULL COMMENT '用户ID（外键）',
    `book_id` VARCHAR(20) NOT NULL COMMENT '图书编号（外键）',
    `borrow_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅日期',
    `due_date` DATETIME NOT NULL COMMENT '应还日期',
    `return_date` DATETIME DEFAULT NULL COMMENT '归还日期',
    `renew_count` INT NOT NULL DEFAULT 0 COMMENT '续借次数',
    `status` VARCHAR(20) NOT NULL DEFAULT '借阅中' COMMENT '状态：借阅中/已归还/已逾期',
    `overdue_days` INT NOT NULL DEFAULT 0 COMMENT '逾期天数',
    `overdue_fee` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '逾期费用',
    `book_status` VARCHAR(20) DEFAULT '正常' COMMENT '归还时图书状态：正常/损坏/丢失',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`record_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_book_id` (`book_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_borrow_date` (`borrow_date`),
    INDEX `idx_due_date` (`due_date`),
    CONSTRAINT `fk_borrow_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `fk_borrow_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- ============================================
-- 5. 操作日志表（operation_log）
-- ============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `log_id` INT NOT NULL AUTO_INCREMENT COMMENT '日志ID（主键）',
    `user_id` VARCHAR(20) NOT NULL COMMENT '操作用户ID',
    `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型：登录/登出/借阅/归还/添加图书等',
    `operation_content` TEXT COMMENT '操作内容',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(200) DEFAULT NULL COMMENT '用户代理',
    `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `result` VARCHAR(20) NOT NULL DEFAULT '成功' COMMENT '操作结果：成功/失败',
    PRIMARY KEY (`log_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_operation_type` (`operation_type`),
    INDEX `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 6. 系统设置表（system_config）
-- ============================================
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
    `config_id` INT NOT NULL AUTO_INCREMENT COMMENT '配置ID（主键）',
    `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
    `config_value` TEXT NOT NULL COMMENT '配置值',
    `config_desc` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`config_id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认分类数据
INSERT INTO `category` (`category_name`, `parent_id`, `level`, `description`, `sort_order`) VALUES
('文学', 0, 1, '文学类图书', 1),
('小说', 1, 2, '小说类', 1),
('现代小说', 2, 3, '现代小说', 1),
('古典小说', 2, 3, '古典小说', 2),
('散文', 1, 2, '散文类', 2),
('诗歌', 1, 2, '诗歌类', 3),
('科技', 0, 1, '科技类图书', 2),
('计算机', 7, 2, '计算机类', 1),
('编程语言', 8, 3, '编程语言', 1),
('数据库', 8, 3, '数据库', 2),
('人工智能', 7, 2, '人工智能类', 2),
('历史', 0, 1, '历史类图书', 3),
('中国历史', 12, 2, '中国历史', 1),
('世界历史', 12, 2, '世界历史', 2),
('经济', 0, 1, '经济类图书', 4),
('经济学', 15, 2, '经济学', 1),
('金融', 15, 2, '金融类', 2),
('教育', 0, 1, '教育类图书', 5),
('教材', 18, 2, '教材', 1),
('参考书', 18, 2, '参考书', 2),
('艺术', 0, 1, '艺术类图书', 6),
('绘画', 21, 2, '绘画', 1),
('音乐', 21, 2, '音乐', 2);

-- 插入系统管理员账户（密码：admin123，实际使用时应使用加密后的密码）
-- 注意：这里使用MD5加密，实际项目中应使用更安全的加密方式（如BCrypt）
INSERT INTO `user` (`user_id`, `username`, `password`, `name`, `gender`, `phone`, `email`, `role`, `status`) VALUES
('U20241201001', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '男', '13800138000', 'admin@library.com', '系统管理员', '正常');

-- 插入图书管理员账户（密码：librarian123）
INSERT INTO `user` (`user_id`, `username`, `password`, `name`, `gender`, `phone`, `email`, `role`, `status`) VALUES
('U20241201002', 'librarian', 'e10adc3949ba59abbe56e057f20f883e', '图书管理员', '女', '13800138001', 'librarian@library.com', '图书管理员', '正常');

-- 插入系统默认配置
INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`) VALUES
('borrow_days', '30', '借阅期限（天）'),
('max_borrow_count', '5', '普通用户最大借阅数量'),
('renew_count', '1', '续借次数'),
('renew_days', '30', '续借期限（天）'),
('overdue_fee_per_day', '1.00', '逾期费用（元/天）'),
('stock_warning_threshold', '5', '库存预警阈值'),
('system_name', '图书管理系统', '系统名称'),
('system_version', '1.0.0', '系统版本');

-- ============================================
-- 完成
-- ============================================
SELECT '数据库初始化完成！' AS message;

