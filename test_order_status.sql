-- 测试订单状态更新
-- 执行前请确保已更新数据库表结构

-- 1. 检查当前订单表的状态枚举
SHOW COLUMNS FROM orders LIKE 'status';

-- 2. 查看现有订单的状态分布
SELECT status, COUNT(*) as count 
FROM orders 
GROUP BY status 
ORDER BY status;

-- 3. 测试插入不同状态的订单（可选）
-- INSERT INTO orders (order_no, user_id, artist_id, title, description, price, status) 
-- VALUES 
-- ('TEST001', 1, 1, '测试订单1', '测试描述1', 100.00, 'pending'),
-- ('TEST002', 1, 1, '测试订单2', '测试描述2', 200.00, 'paid'),
-- ('TEST003', 1, 1, '测试订单3', '测试描述3', 300.00, 'confirmed');

-- 4. 验证新状态是否可以正常插入和查询
-- SELECT * FROM orders WHERE status IN ('paid', 'confirmed');

-- 5. 清理测试数据（如果需要）
-- DELETE FROM orders WHERE order_no LIKE 'TEST%';
