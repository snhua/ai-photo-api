-- 订单表更新SQL脚本
-- 为画师工作台功能添加必要字段

-- 1. 添加作品文件URL列表字段
ALTER TABLE `orders` 
ADD COLUMN `artwork_urls` TEXT COMMENT '作品文件URL列表，JSON格式' AFTER `deadline`;

-- 2. 添加作品说明字段
ALTER TABLE `orders` 
ADD COLUMN `notes` TEXT COMMENT '作品说明' AFTER `artwork_urls`;

-- 3. 添加技术说明字段
ALTER TABLE `orders` 
ADD COLUMN `technical_notes` TEXT COMMENT '技术说明' AFTER `notes`;

-- 4. 添加制作时间字段
ALTER TABLE `orders` 
ADD COLUMN `work_hours` INT COMMENT '制作时间（小时）' AFTER `technical_notes`;

-- 5. 添加完成时间字段
ALTER TABLE `orders` 
ADD COLUMN `completed_at` TIMESTAMP NULL COMMENT '完成时间' AFTER `work_hours`;

-- 6. 添加索引优化查询性能
ALTER TABLE `orders` 
ADD INDEX `idx_orders_status` (`status`),
ADD INDEX `idx_orders_artist_id` (`artist_id`),
ADD INDEX `idx_orders_user_id` (`user_id`),
ADD INDEX `idx_orders_created_at` (`created_at`),
ADD INDEX `idx_orders_completed_at` (`completed_at`);

-- 7. 更新表注释
ALTER TABLE `orders` COMMENT = '订单表 - 支持画师工作台功能';

-- 8. 查看更新后的表结构
-- DESCRIBE orders;

-- 9. 示例数据插入（可选）
-- INSERT INTO orders (
--     order_no, user_id, artist_id, title, description, 
--     reference_images, requirements, price, status, deadline,
--     artwork_urls, notes, technical_notes, work_hours, completed_at,
--     created_at, updated_at
-- ) VALUES (
--     'ORDER202501011200001', 1, 2, '个人证件照', '要像，要帅，要酷',
--     '["https://example.com/ref1.jpg"]', '高清证件照', 200.00, 'completed', 
--     '2025-01-15 23:59:59',
--     '["https://example.com/artwork1.jpg", "https://example.com/artwork2.jpg"]',
--     '已完成的作品，包含多个版本', '使用Photoshop绘制，分辨率300DPI', 8,
--     '2025-01-10 15:30:00', NOW(), NOW()
-- ); 