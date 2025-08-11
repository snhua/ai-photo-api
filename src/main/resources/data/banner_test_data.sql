-- 横幅测试数据插入脚本
-- 执行前请确保 banners 表已创建

-- 清空现有横幅数据（可选）
-- DELETE FROM banners;

-- 插入横幅测试数据
INSERT INTO `banners` (`title`, `description`, `image_url`, `link_url`, `type`, `sort_weight`, `status`, `start_time`, `end_time`) VALUES
-- 首页横幅1：AI绘画师招募活动
('AI绘画师招募', '成为AI绘画师，开启你的艺术创作之旅', 'https://example.com/banners/ai-artist-recruit.jpg', '/pages/artist/recruit', 'home', 100, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),

-- 首页横幅2：新用户优惠活动
('新用户专享优惠', '首次下单享受8折优惠，限时抢购中', 'https://example.com/banners/new-user-discount.jpg', '/pages/promotion/new-user', 'home', 90, 1, '2024-01-01 00:00:00', '2024-06-30 23:59:59'),

-- 首页横幅3：热门作品展示
('精选作品展示', '查看最新热门AI绘画作品，发现艺术之美', 'https://example.com/banners/featured-works.jpg', '/pages/artworks/featured', 'home', 80, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),

-- 分类页横幅1：风景画分类推广
('风景画专区', '专业AI绘画师为您创作精美风景画', 'https://example.com/banners/landscape-category.jpg', '/pages/category/landscape', 'category', 95, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),

-- 分类页横幅2：人物画分类推广
('人物画专区', '专业AI绘画师为您创作精美人物画', 'https://example.com/banners/portrait-category.jpg', '/pages/category/portrait', 'category', 85, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),

-- 促销页横幅1：限时优惠活动
('限时优惠活动', '全场作品8折优惠，仅限今日', 'https://example.com/banners/flash-sale.jpg', '/pages/promotion/flash-sale', 'promotion', 100, 1, '2024-01-01 00:00:00', '2024-03-31 23:59:59'),

-- 促销页横幅2：会员专享活动
('会员专享福利', 'VIP会员享受专属优惠和服务', 'https://example.com/banners/vip-benefits.jpg', '/pages/promotion/vip', 'promotion', 90, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),

-- 促销页横幅3：节日特惠活动
('春节特惠活动', '新春佳节，AI绘画作品特价优惠', 'https://example.com/banners/spring-festival.jpg', '/pages/promotion/spring-festival', 'promotion', 85, 1, '2024-02-01 00:00:00', '2024-02-29 23:59:59');

-- 查询插入结果
SELECT '横幅数据插入完成，共插入 ' || COUNT(*) || ' 条记录' as result FROM banners; 