-- 更新订单表状态枚举，添加paid和confirmed状态
-- 执行前请备份数据库

-- 方法1：修改ENUM类型（推荐）
ALTER TABLE `orders` 
MODIFY COLUMN `status` ENUM('pending', 'paid', 'accepted', 'in_progress', 'completed', 'confirmed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态';

-- 方法2：如果方法1失败，可以删除并重新创建ENUM
-- 先删除ENUM约束
-- ALTER TABLE `orders` MODIFY COLUMN `status` VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态';
-- 然后重新添加ENUM约束
-- ALTER TABLE `orders` MODIFY COLUMN `status` ENUM('pending', 'paid', 'accepted', 'in_progress', 'completed', 'confirmed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态';

-- 验证更新结果
SELECT DISTINCT status FROM orders;
