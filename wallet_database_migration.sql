-- 钱包功能数据库迁移SQL
-- 执行前请先备份数据库

-- =============================================
-- 1. 扩展用户表 (users)
-- =============================================
ALTER TABLE `users` 
ADD COLUMN `withdrawable_balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '可提现金额',
ADD COLUMN `total_income` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总收入',
ADD COLUMN `total_withdraw` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总提现',
ADD COLUMN `service_fee_total` DECIMAL(10,2) DEFAULT 0.00 COMMENT '技术服务费总额';

-- =============================================
-- 2. 扩展钱包交易记录表 (wallet_transactions)
-- =============================================
ALTER TABLE `wallet_transactions` 
ADD COLUMN `withdrawable_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '可提现金额变动',
ADD COLUMN `service_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '技术服务费',
ADD COLUMN `available_at` TIMESTAMP NULL COMMENT '可提现时间（延迟到账）',
ADD COLUMN `transaction_no` VARCHAR(50) UNIQUE COMMENT '交易流水号',
ADD COLUMN `status` ENUM('pending', 'completed', 'failed', 'cancelled') DEFAULT 'completed' COMMENT '交易状态';

-- 添加索引
ALTER TABLE `wallet_transactions` 
ADD INDEX `idx_transaction_no` (`transaction_no`),
ADD INDEX `idx_available_at` (`available_at`),
ADD INDEX `idx_status` (`status`);

-- =============================================
-- 3. 创建延迟到账记录表 (wallet_pending_withdrawals)
-- =============================================
CREATE TABLE `wallet_pending_withdrawals` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `service_fee` DECIMAL(10,2) NOT NULL COMMENT '技术服务费',
    `available_at` TIMESTAMP NOT NULL COMMENT '可提现时间',
    `status` ENUM('pending', 'available', 'withdrawn', 'cancelled') DEFAULT 'pending' COMMENT '状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_available_at` (`available_at`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='延迟到账记录表';

-- =============================================
-- 4. 创建提现申请表 (withdrawal_requests)
-- =============================================
CREATE TABLE `withdrawal_requests` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提现申请ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `withdrawal_no` VARCHAR(50) UNIQUE NOT NULL COMMENT '提现单号',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
    `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '手续费',
    `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际到账金额',
    `payment_method` ENUM('wechat', 'alipay', 'bank') NOT NULL COMMENT '提现方式',
    `payment_account` VARCHAR(100) COMMENT '收款账户',
    `status` ENUM('pending', 'processing', 'success', 'failed', 'cancelled') DEFAULT 'pending' COMMENT '提现状态',
    `remark` VARCHAR(500) COMMENT '备注',
    `processed_at` TIMESTAMP NULL COMMENT '处理时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_withdrawal_no` (`withdrawal_no`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现申请表';

-- =============================================
-- 5. 创建钱包操作日志表 (wallet_operation_logs)
-- =============================================
CREATE TABLE `wallet_operation_logs` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `amount` DECIMAL(10,2) COMMENT '操作金额',
    `balance_before` DECIMAL(10,2) COMMENT '操作前余额',
    `balance_after` DECIMAL(10,2) COMMENT '操作后余额',
    `withdrawable_before` DECIMAL(10,2) COMMENT '操作前可提现金额',
    `withdrawable_after` DECIMAL(10,2) COMMENT '操作后可提现金额',
    `related_id` BIGINT COMMENT '关联ID',
    `related_type` VARCHAR(50) COMMENT '关联类型',
    `description` VARCHAR(500) COMMENT '操作描述',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_operation_type` (`operation_type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包操作日志表';

-- =============================================
-- 6. 创建系统配置表 (system_configs) - 如果不存在
-- =============================================
CREATE TABLE IF NOT EXISTS `system_configs` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) UNIQUE NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(500) COMMENT '配置描述',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =============================================
-- 7. 插入钱包相关系统配置
-- =============================================
INSERT INTO `system_configs` (`config_key`, `config_value`, `description`) VALUES
('wallet.service_fee_rate', '0.2', '技术服务费率（20%）'),
('wallet.withdrawal_delay_days', '7', '提现延迟天数'),
('wallet.min_withdrawal_amount', '10.00', '最小提现金额'),
('wallet.max_withdrawal_amount', '50000.00', '最大提现金额'),
('wallet.daily_withdrawal_limit', '100000.00', '每日提现限额'),
('wallet.withdrawal_fee_rate', '0.02', '提现手续费率（2%）'),
('wallet.min_withdrawal_fee', '1.00', '最小提现手续费'),
('wallet.max_withdrawal_fee', '50.00', '最大提现手续费')
ON DUPLICATE KEY UPDATE 
`config_value` = VALUES(`config_value`),
`description` = VALUES(`description`),
`updated_at` = CURRENT_TIMESTAMP;

-- =============================================
-- 8. 更新现有钱包交易记录的状态
-- =============================================
UPDATE `wallet_transactions` 
SET `status` = 'completed' 
WHERE `status` IS NULL;

-- =============================================
-- 9. 为现有交易记录生成交易流水号
-- =============================================
UPDATE `wallet_transactions` 
SET `transaction_no` = CONCAT('TXN', DATE_FORMAT(created_at, '%Y%m%d'), LPAD(id, 6, '0'))
WHERE `transaction_no` IS NULL;

-- =============================================
-- 10. 验证表结构
-- =============================================
-- 查看用户表结构
DESCRIBE users;

-- 查看钱包交易记录表结构
DESCRIBE wallet_transactions;

-- 查看延迟到账记录表结构
DESCRIBE wallet_pending_withdrawals;

-- 查看提现申请表结构
DESCRIBE withdrawal_requests;

-- 查看钱包操作日志表结构
DESCRIBE wallet_operation_logs;

-- 查看系统配置
SELECT * FROM system_configs WHERE config_key LIKE 'wallet.%';

-- =============================================
-- 11. 创建视图 - 钱包统计视图
-- =============================================
CREATE OR REPLACE VIEW `wallet_statistics` AS
SELECT 
    u.id as user_id,
    u.nickname,
    u.balance,
    u.withdrawable_balance,
    u.total_income,
    u.total_withdraw,
    u.service_fee_total,
    COUNT(wt.id) as transaction_count,
    SUM(CASE WHEN wt.type = 'income' THEN wt.amount ELSE 0 END) as total_income_amount,
    SUM(CASE WHEN wt.type = 'expense' THEN wt.amount ELSE 0 END) as total_expense_amount,
    SUM(wt.service_fee) as total_service_fee,
    COUNT(wr.id) as withdrawal_count,
    SUM(wr.amount) as total_withdrawal_amount,
    COUNT(wpw.id) as pending_withdrawal_count,
    SUM(wpw.amount) as pending_withdrawal_amount
FROM users u
LEFT JOIN wallet_transactions wt ON u.id = wt.user_id
LEFT JOIN withdrawal_requests wr ON u.id = wr.user_id
LEFT JOIN wallet_pending_withdrawals wpw ON u.id = wpw.user_id AND wpw.status = 'pending'
GROUP BY u.id, u.nickname, u.balance, u.withdrawable_balance, u.total_income, u.total_withdraw, u.service_fee_total;

-- =============================================
-- 12. 创建存储过程 - 处理延迟到账
-- =============================================
DELIMITER //
CREATE PROCEDURE `ProcessPendingWithdrawals`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_user_id BIGINT;
    DECLARE v_order_id BIGINT;
    DECLARE v_amount DECIMAL(10,2);
    DECLARE v_service_fee DECIMAL(10,2);
    DECLARE v_id BIGINT;
    
    -- 声明游标
    DECLARE cur CURSOR FOR 
        SELECT id, user_id, order_id, amount, service_fee 
        FROM wallet_pending_withdrawals 
        WHERE status = 'pending' AND available_at <= NOW();
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始事务
    START TRANSACTION;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO v_id, v_user_id, v_order_id, v_amount, v_service_fee;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 更新延迟到账记录状态
        UPDATE wallet_pending_withdrawals 
        SET status = 'available', updated_at = NOW() 
        WHERE id = v_id;
        
        -- 更新用户可提现金额
        UPDATE users 
        SET withdrawable_balance = withdrawable_balance + v_amount 
        WHERE id = v_user_id;
        
        -- 插入交易记录
        INSERT INTO wallet_transactions (
            user_id, type, amount, withdrawable_amount, service_fee,
            description, related_type, related_id, transaction_no, status, created_at
        ) VALUES (
            v_user_id, 'withdrawable_available', v_amount, v_amount, v_service_fee,
            CONCAT('延迟到账 - 订单', v_order_id), 'pending_withdrawal', v_order_id,
            CONCAT('TXN', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(LAST_INSERT_ID(), 6, '0')),
            'completed', NOW()
        );
        
    END LOOP;
    
    CLOSE cur;
    
    -- 提交事务
    COMMIT;
    
END //
DELIMITER ;

-- =============================================
-- 13. 创建事件 - 自动处理延迟到账
-- =============================================
-- 启用事件调度器
SET GLOBAL event_scheduler = ON;

-- 创建每天凌晨2点执行的事件
CREATE EVENT IF NOT EXISTS `ProcessPendingWithdrawalsEvent`
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 2 HOUR
DO CALL ProcessPendingWithdrawals();

-- =============================================
-- 14. 创建触发器 - 钱包操作日志
-- =============================================
DELIMITER //

-- 用户余额更新触发器
CREATE TRIGGER `wallet_balance_log_trigger` 
AFTER UPDATE ON `users`
FOR EACH ROW
BEGIN
    IF OLD.balance != NEW.balance OR OLD.withdrawable_balance != NEW.withdrawable_balance THEN
        INSERT INTO wallet_operation_logs (
            user_id, operation_type, amount,
            balance_before, balance_after,
            withdrawable_before, withdrawable_after,
            description, created_at
        ) VALUES (
            NEW.id, 'balance_update', 
            CASE 
                WHEN OLD.balance != NEW.balance THEN NEW.balance - OLD.balance
                ELSE 0 
            END,
            OLD.balance, NEW.balance,
            OLD.withdrawable_balance, NEW.withdrawable_balance,
            '余额更新', NOW()
        );
    END IF;
END //

DELIMITER ;

-- =============================================
-- 15. 创建索引优化查询性能
-- =============================================
-- 钱包交易记录表索引
CREATE INDEX `idx_wallet_transactions_user_type` ON `wallet_transactions` (`user_id`, `type`);
CREATE INDEX `idx_wallet_transactions_created_at` ON `wallet_transactions` (`created_at` DESC);

-- 提现申请表索引
CREATE INDEX `idx_withdrawal_requests_user_status` ON `withdrawal_requests` (`user_id`, `status`);
CREATE INDEX `idx_withdrawal_requests_created_at` ON `withdrawal_requests` (`created_at` DESC);

-- 延迟到账记录表索引
CREATE INDEX `idx_wallet_pending_withdrawals_user_status` ON `wallet_pending_withdrawals` (`user_id`, `status`);

-- =============================================
-- 16. 数据验证和清理
-- =============================================
-- 确保所有用户都有默认的钱包字段值
UPDATE users 
SET 
    withdrawable_balance = COALESCE(withdrawable_balance, 0.00),
    total_income = COALESCE(total_income, 0.00),
    total_withdraw = COALESCE(total_withdraw, 0.00),
    service_fee_total = COALESCE(service_fee_total, 0.00)
WHERE 
    withdrawable_balance IS NULL 
    OR total_income IS NULL 
    OR total_withdraw IS NULL 
    OR service_fee_total IS NULL;

-- 确保所有钱包交易记录都有状态
UPDATE wallet_transactions 
SET status = 'completed' 
WHERE status IS NULL;

-- =============================================
-- 17. 完成提示
-- =============================================
SELECT '钱包功能数据库迁移完成！' as message;

-- 显示迁移后的表结构统计
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME IN ('users', 'wallet_transactions', 'wallet_pending_withdrawals', 'withdrawal_requests', 'wallet_operation_logs', 'system_configs');
