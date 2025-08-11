-- 横幅示例数据插入脚本
-- 包含3个核心横幅示例

-- 确保 banners 表存在
-- CREATE TABLE IF NOT EXISTS `banners` (
--     `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '横幅ID',
--     `title` VARCHAR(200) COMMENT '横幅标题',
--     `description` TEXT COMMENT '横幅描述',
--     `image_url` VARCHAR(255) COMMENT '横幅图片URL',
--     `link_url` VARCHAR(255) COMMENT '跳转链接',
--     `type` VARCHAR(50) COMMENT '横幅类型：home-首页，category-分类页，promotion-促销页',
--     `sort_weight` INT DEFAULT 0 COMMENT '排序权重，数字越大越靠前',
--     `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
--     `start_time` TIMESTAMP NULL COMMENT '开始时间',
--     `end_time` TIMESTAMP NULL COMMENT '结束时间',
--     `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--     `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
-- );

-- 插入3个横幅示例
INSERT INTO `banners` (`title`, `description`, `image_url`, `link_url`, `type`, `sort_weight`, `status`, `start_time`, `end_time`) VALUES

-- 示例1：首页横幅 - AI绘画师招募
('AI绘画师招募', '成为AI绘画师，开启你的艺术创作之旅', 'https://example.com/banners/ai-artist-recruit.jpg', '/pages/artist/recruit', 'home', 100, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),

-- 示例2：分类页横幅 - 风景画专区
('风景画专区', '专业AI绘画师为您创作精美风景画', 'https://example.com/banners/landscape-category.jpg', '/pages/category/landscape', 'category', 95, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),

-- 示例3：促销页横幅 - 限时优惠活动
('限时优惠活动', '全场作品8折优惠，仅限今日', 'https://example.com/banners/flash-sale.jpg', '/pages/promotion/flash-sale', 'promotion', 100, 1, '2024-01-01 00:00:00', '2024-03-31 23:59:59');

-- 验证插入结果
SELECT 
    id,
    title,
    type,
    sort_weight,
    status,
    start_time,
    end_time
FROM banners 
ORDER BY sort_weight DESC, created_at DESC; 