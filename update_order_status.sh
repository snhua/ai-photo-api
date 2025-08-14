#!/bin/bash

# 更新订单表状态枚举，添加confirmed状态
# 请根据您的数据库配置修改以下参数

DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="aiphone"
DB_USER="root"
DB_PASS=""

echo "开始更新订单表状态..."

# 执行SQL更新
mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASS $DB_NAME << EOF
-- 更新订单表状态枚举，添加paid和confirmed状态
ALTER TABLE \`orders\` 
MODIFY COLUMN \`status\` ENUM('pending', 'paid', 'accepted', 'in_progress', 'completed', 'confirmed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态';

-- 验证更新结果
SELECT DISTINCT status FROM orders;
EOF

echo "订单表状态更新完成！"
