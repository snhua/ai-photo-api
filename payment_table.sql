-- 支付记录表
CREATE TABLE `payments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `payment_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付单号',
  `amount_yuan` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `payment_method` enum('wechat','balance') COLLATE utf8mb4_unicode_ci DEFAULT 'wechat' COMMENT '支付方式',
  `status` enum('pending','success','failed','refunded') COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '支付状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `amount` int(11) NOT NULL,
  `description` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_ip` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remark` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_no` (`payment_no`),
  KEY `idx_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`create_time`),
  KEY `idx_payments_order_status` (`order_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表'; 