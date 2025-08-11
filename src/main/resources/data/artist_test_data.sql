-- AI绘画师测试数据插入脚本

-- 插入测试用户数据（AI绘画师）
INSERT INTO `users` (`openid`, `nickname`, `avatar`, `user_type`, `status`) VALUES
('test_artist_001', 'AI绘画师小明', 'https://example.com/avatar1.jpg', 'artist', 1),
('test_artist_002', 'AI绘画师小红', 'https://example.com/avatar2.jpg', 'artist', 1),
('test_artist_003', 'AI绘画师小华', 'https://example.com/avatar3.jpg', 'artist', 1),
('test_artist_004', 'AI绘画师小李', 'https://example.com/avatar4.jpg', 'artist', 1),
('test_artist_005', 'AI绘画师小王', 'https://example.com/avatar5.jpg', 'artist', 1);

-- 插入测试AI绘画师数据
INSERT INTO `artists` (`user_id`, `artist_name`, `description`, `specialties`, `price_per_hour`, `rating`, `total_orders`) VALUES
(2, 'AI绘画师小明', '专业AI绘画师，擅长风景和人物绘画，作品风格独特，深受用户喜爱', '["风景画", "人物画", "写实风格"]', 100.00, 4.8, 156),
(3, 'AI绘画师小红', '现代艺术风格，作品充满创意，擅长抽象画和现代艺术', '["抽象画", "现代艺术", "创意设计"]', 120.00, 4.9, 203),
(4, 'AI绘画师小华', '卡通插画专家，作品生动有趣，适合儿童和年轻人', '["卡通画", "插画", "儿童画"]', 80.00, 4.7, 98),
(5, 'AI绘画师小李', '科幻风格大师，作品充满未来感，适合科幻题材', '["科幻画", "未来风格", "机械画"]', 150.00, 4.6, 134),
(6, 'AI绘画师小王', '写实风格专家，作品逼真细腻，适合商业用途', '["写实画", "肖像画", "商业画"]', 200.00, 4.5, 87);

-- 插入测试作品数据
INSERT INTO `artworks` (`artist_id`, `title`, `description`, `image_url`, `category`, `tags`, `price`) VALUES
(1, '梦幻森林', '美丽的森林风景画，充满梦幻色彩', 'https://example.com/artwork1.jpg', '风景画', '森林,梦幻,自然', 299.00),
(1, '人物肖像', '精美的人物肖像画，写实风格', 'https://example.com/artwork2.jpg', '人物画', '人物,肖像,写实', 399.00),
(2, '抽象艺术', '现代抽象艺术作品，充满创意', 'https://example.com/artwork3.jpg', '抽象画', '抽象,现代,艺术', 250.00),
(2, '创意海报', '商业创意海报设计', 'https://example.com/artwork4.jpg', '海报', '商业,创意,海报', 180.00),
(3, '卡通角色', '可爱的卡通角色设计', 'https://example.com/artwork5.jpg', '卡通画', '卡通,可爱,角色', 150.00),
(3, '儿童插画', '适合儿童的插画作品', 'https://example.com/artwork6.jpg', '插画', '儿童,插画,教育', 120.00),
(4, '科幻城市', '未来科幻城市景观', 'https://example.com/artwork7.jpg', '科幻画', '科幻,未来,城市', 450.00),
(4, '机械战士', '科幻机械战士设计', 'https://example.com/artwork8.jpg', '机械画', '机械,战士,科幻', 380.00),
(5, '商业肖像', '专业商业肖像画', 'https://example.com/artwork9.jpg', '肖像画', '商业,肖像,专业', 500.00),
(5, '产品展示', '产品展示图设计', 'https://example.com/artwork10.jpg', '商业画', '产品,展示,商业', 320.00);

-- 插入测试评价数据
INSERT INTO `reviews` (`order_id`, `user_id`, `artist_id`, `rating`, `content`, `tags`) VALUES
(1, 1, 1, 5, '作品质量很高，很满意！绘画师技术精湛，沟通也很顺畅', '["专业", "速度快", "质量高"]'),
(2, 1, 2, 5, '创意十足，完全符合我的要求，推荐！', '["创意", "专业", "服务好"]'),
(3, 1, 3, 4, '作品很可爱，孩子很喜欢，下次还会选择', '["可爱", "适合儿童", "服务好"]'),
(4, 1, 4, 5, '科幻风格很棒，细节处理得很到位', '["科幻", "细节", "专业"]'),
(5, 1, 5, 4, '写实风格很专业，适合商业用途', '["写实", "专业", "商业"]');

-- 更新用户类型为artist
UPDATE `users` SET `user_type` = 'artist' WHERE `id` IN (2, 3, 4, 5, 6); 